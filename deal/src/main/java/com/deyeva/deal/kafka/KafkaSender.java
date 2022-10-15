package com.deyeva.deal.kafka;

import com.deyeva.deal.model.EmailMessage;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vladmihalcea.hibernate.type.json.internal.JacksonUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class KafkaSender {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(EmailMessage.ThemeEnum topic, EmailMessage msg){
        System.out.println("Message: "+msg+" for topic = "+topic+" is sending.");

        kafkaTemplate.send(topic.getValue().toLowerCase(), JacksonUtil.toString(msg));
    }
}
