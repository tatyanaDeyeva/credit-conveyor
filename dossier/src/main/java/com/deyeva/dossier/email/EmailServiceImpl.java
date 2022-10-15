package com.deyeva.dossier.email;

import lombok.AllArgsConstructor;
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

@Component
@AllArgsConstructor
@Slf4j
public class EmailServiceImpl{

    private final JavaMailSender javaMailSender;

    public void sendSimpleMessage(String to, String subject, String text) {
        System.out.println("Send simple Message");

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("deyevat@yandex.ru");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);

        try {
            javaMailSender.send(message);
            System.out.println("Message sent");
        } catch (ListenerExecutionFailedException e) {
            System.out.println(e.getMessage());
        }
    }

    public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) {
        System.out.println("Send Message with Attachment");

        MimeMessage message = javaMailSender.createMimeMessage();

        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom("deyevat@yandex.ru");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text);

            FileSystemResource file = new FileSystemResource(new File(pathToAttachment));
            helper.addAttachment("Document", file);
        } catch (MessagingException e) {
            System.out.println(e.getMessage());
        }

        javaMailSender.send(message);
    }
}
