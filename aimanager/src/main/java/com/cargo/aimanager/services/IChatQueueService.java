package com.cargo.aimanager.services;

public interface IChatQueueService {
    String chat(String model, String prompt);
    String chatStream(String model, String prompt);
    String getAvailableModels();
}
