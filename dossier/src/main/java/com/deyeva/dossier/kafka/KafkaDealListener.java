package com.deyeva.dossier.kafka;

import com.deyeva.dossier.email.EmailServiceImpl;
import com.deyeva.dossier.feign.DealClient;
import com.deyeva.dossier.model.Application;
import com.deyeva.dossier.model.EmailMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaDealListener {

    private final EmailServiceImpl emailService;
    private final DealClient dealClient;

    @Value("${msa.message.log}")
    private String LOG_DEBUG;

    @Value("${msa.message.finish-registration.subject}")
    private String FINISH_REGISTRATION_SUBJECT;
    @Value("${msa.message.create-documents.subject}")
    private String CREATE_DOCUMENT_SUBJECT;
    @Value("${msa.message.send-documents.subject}")
    private String SEND_DOCUMENT_SUBJECT;
    @Value("${msa.message.send-ses.subject}")
    private String SEND_SES_SUBJECT;
    @Value("${msa.message.credit-issued.subject}")
    private String CREDIT_ISSUED_SUBJECT;
    @Value("${msa.message.application-denied.subject}")
    private String APPLICATION_DENIED_SUBJECT;

    @Value("${msa.message.finish-registration.text}")
    private String FINISH_REGISTRATION_TEXT;
    @Value("${msa.message.create-documents.text}")
    private String CREATE_DOCUMENT_TEXT;
    @Value("${msa.message.send-documents.text}")
    private String SEND_DOCUMENT_TEXT;
    @Value("${msa.message.send-ses.text}")
    private String SEND_SES_TEXT;
    @Value("${msa.message.credit-issued.text}")
    private String CREDIT_ISSUED_TEXT;
    @Value("${msa.message.application-denied.text}")
    private String APPLICATION_DENIED_TEXT;

    @KafkaListener(topics = "finish-registration", groupId = "dossier")
    public void consumeFinishRegistrationTopic(String message) {
        log.debug(LOG_DEBUG + message);

        EmailMessage emailMessage = getMessage(message);

        emailService.sendSimpleMessage(emailMessage.getAddress(), FINISH_REGISTRATION_SUBJECT,
                FINISH_REGISTRATION_TEXT + emailMessage.getApplicationId());
    }

    @KafkaListener(topics = "create-documents", groupId = "dossier")
    public void consumeCreateDocumentsTopic(String message) {
        log.debug(LOG_DEBUG + message);

        EmailMessage emailMessage = getMessage(message);

        emailService.sendSimpleMessage(emailMessage.getAddress(), CREATE_DOCUMENT_SUBJECT,
                CREATE_DOCUMENT_TEXT);
    }

    @KafkaListener(topics = "send-documents", groupId = "dossier")
    public void consumeSendDocumentsTopic(String message) {
        log.debug(LOG_DEBUG + message);

        EmailMessage emailMessage = getMessage(message);

        Application application = dealClient.getApplicationById(String.valueOf(emailMessage.getApplicationId()));

        emailService.sendMessageWithAttachment(emailMessage.getAddress(), SEND_DOCUMENT_SUBJECT,
                SEND_DOCUMENT_TEXT, application);
    }

    @KafkaListener(topics = "send-ses", groupId = "dossier")
    public void consumeSendSesTopic(String message) {
        log.debug(LOG_DEBUG + message);
        EmailMessage emailMessage = getMessage(message);
        Application application = dealClient.getApplicationById(String.valueOf(emailMessage.getApplicationId()));
        emailService.sendSimpleMessage(emailMessage.getAddress(), SEND_SES_SUBJECT,
                SEND_SES_TEXT + application.getSesCode());
    }

    @KafkaListener(topics = "credit-issued", groupId = "dossier")
    public void consumeCreditIssuedTopic(String message) {
        log.debug(LOG_DEBUG + message);
        EmailMessage emailMessage = getMessage(message);
        emailService.sendSimpleMessage(emailMessage.getAddress(), CREDIT_ISSUED_SUBJECT,
                CREDIT_ISSUED_TEXT);
    }

    @KafkaListener(topics = "application-denied", groupId = "dossier")
    public void consumeApplicationDeniedTopic(String message) {
        log.debug(LOG_DEBUG + message);
        EmailMessage emailMessage = getMessage(message);
        emailService.sendSimpleMessage(emailMessage.getAddress(), APPLICATION_DENIED_SUBJECT,
                APPLICATION_DENIED_TEXT);
    }

    public EmailMessage getMessage(String message) {
        ObjectMapper objectMapper = new ObjectMapper();
        EmailMessage emailMessage = new EmailMessage();

        try {
            emailMessage = objectMapper.readValue(message, EmailMessage.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            log.info("Can not processing JSON: " + message);
        }

        return emailMessage;
    }
}