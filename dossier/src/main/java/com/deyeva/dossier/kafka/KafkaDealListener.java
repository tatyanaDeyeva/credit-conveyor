package com.deyeva.dossier.kafka;

import com.deyeva.dossier.email.EmailServiceImpl;
import com.deyeva.dossier.feign.DealClient;
import com.deyeva.dossier.model.EmailMessage;
import com.deyeva.dossier.model.entity.Application;
import com.deyeva.dossier.model.entity.Client;
import com.deyeva.dossier.model.entity.Credit;
import com.deyeva.dossier.tempFile.TempFileWrite;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class KafkaDealListener {

    private final EmailServiceImpl emailService;
    private final TempFileWrite tempFileWrite;
    private final DealClient dealClient;

    public KafkaDealListener(EmailServiceImpl emailService, TempFileWrite tempFileWrite, DealClient dealClient) {
        this.emailService = emailService;
        this.tempFileWrite = tempFileWrite;
        this.dealClient = dealClient;
    }

    @KafkaListener(topics = "finish-registration", groupId = "dossier")
    public void consumeFinishRegistrationTopic(String message) {
        EmailMessage emailMessage = getMessage(message);

        emailService.sendSimpleMessage(emailMessage.getAddress(), "Finish registration",
                "Registration of the application is over. Complete the loan processing by the link: " +
                        "http://localhost:8081/deal/calculate/" + emailMessage.getApplicationId());
        System.out.println("Received Message in group dossier for finish-registration topic: : " + message);
    }

    @KafkaListener(topics = "create-documents", groupId = "dossier")
    public void consumeCreateDocumentsTopic(String message) {
        EmailMessage emailMessage = getMessage(message);

        emailService.sendSimpleMessage(emailMessage.getAddress(), "Documents created",
                "The loan is calculated. Please proceed to the documents processing" +
                        " by the link: http://localhost:8081/deal/document/"+emailMessage.getApplicationId()+"/send.");
        System.out.println("Received Message in group dossier for create-documents topic: : " + message);
    }

    @KafkaListener(topics = "send-documents", groupId = "dossier")
    public void consumeSendDocumentsTopic(String message) {
        EmailMessage emailMessage = getMessage(message);

        Application application = dealClient.getApplicationById(String.valueOf(emailMessage.getApplicationId()));

        Credit credit = application.getCredit();
        Client client = application.getClient();
        String pathToAttachment = tempFileWrite.createTempFile(credit, client);

        emailService.sendMessageWithAttachment(emailMessage.getAddress(), "Documents", "The documents are formed. " +
                "Please follow the link to sign: http://localhost:8081/deal/document/"+emailMessage.getApplicationId()+"/sign", pathToAttachment);
        System.out.println("Received Message in group dossier for send-documents topic: " + message);
    }

    @KafkaListener(topics = "send-ses", groupId = "dossier")
    public void consumeSendSesTopic(String message) {
        EmailMessage emailMessage = getMessage(message);

        Application application = dealClient.getApplicationById(String.valueOf(emailMessage.getApplicationId()));

        emailService.sendSimpleMessage(emailMessage.getAddress(), "Ses", "If you are satisfied with the conditions, " +
                "please follow the link to confirm: http://localhost:8081/deal/document/"+emailMessage.getApplicationId()+"/code. " +
                "Your personal code: " + application.getSesCode());
        System.out.println("Received Message in group dossier for send-ses topic: : " + message);
    }

    @KafkaListener(topics = "credit-issued", groupId = "dossier")
    public void consumeCreditIssuedTopic(String message) {
        EmailMessage emailMessage = getMessage(message);
        emailService.sendSimpleMessage(emailMessage.getAddress(), "Credit issued", "Congratulations, the loan has been issued. " +
                "You can check the balance in your personal account.");
        System.out.println("Received Message in group dossier for credit-issued topic: : " + message);
    }

    @KafkaListener(topics = "application-denied", groupId = "dossier")
    public void consumeApplicationDeniedTopic(String message) {
        EmailMessage emailMessage = getMessage(message);
        emailService.sendSimpleMessage(emailMessage.getAddress(), "Application denied",
                "Unfortunately, your application has been refused.");
        System.out.println("Received Message in group dossier for application-denied topic: : " + message);
    }

    public EmailMessage getMessage(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        EmailMessage emailMessage = new EmailMessage();

        try {
            emailMessage = objectMapper.readValue(message, EmailMessage.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return emailMessage;
    }
}