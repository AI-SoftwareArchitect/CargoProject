package com.cargo.notifications.services;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationProducerService {

    private final KafkaTemplate<String, String> kafkaTemplate;

    private String topic = "notifications";

    public void sendNotification(String message) {
        kafkaTemplate.send(topic, message);
    }
}
