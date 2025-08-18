package com.cargo.notifications.services;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
public class NotificationConsumerService {

    private final List<String> notifications = Collections.synchronizedList(new ArrayList<>());

    @KafkaListener(topics = "notifications", groupId = "notification-group")
    public void listen(String message) {
        notifications.add(message);
        System.out.println("Received notification: " + message);
    }

    public List<String> getNotifications() {
        return new ArrayList<>(notifications);
    }
}
