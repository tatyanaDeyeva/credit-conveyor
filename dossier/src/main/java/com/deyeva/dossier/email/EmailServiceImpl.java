package com.deyeva.dossier.email;

import com.deyeva.dossier.model.Application;
import com.deyeva.dossier.tempFile.TempFileCreator;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class EmailServiceImpl{

    private final JavaMailSender javaMailSender;
    private final TempFileCreator tempFileCreator;

    public void sendSimpleMessage(String to, String subject, String text) {
        log.info("Send simple Message");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("deyevat@yandex.ru");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            javaMailSender.send(message);
            log.info("Message sent");
        } catch (ListenerExecutionFailedException e) {
            log.debug(e.getMessage());
        }
    }

    public void sendMessageWithAttachment(String to, String subject, String text, Application application) {
        log.info("Send Message with Attachment");

        List<String> result = tempFileCreator.createTempFile(application.getCredit(), application.getClient());

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("deyevat@yandex.ru");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            FileSystemResource creditContract = new FileSystemResource(new File(result.get(0)));
            FileSystemResource creditApplication = new FileSystemResource(new File(result.get(1)));
            FileSystemResource paymentSchedule = new FileSystemResource(new File(result.get(2)));
            helper.addAttachment("CreditContract.txt", creditContract);
            helper.addAttachment("CreditApplication.txt", creditApplication);
            helper.addAttachment("PaymentSchedule.txt", paymentSchedule);

        } catch (MessagingException e) {
            log.debug(e.getMessage());
        }

        javaMailSender.send(message);
    }
}
