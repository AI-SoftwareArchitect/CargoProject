package com.cargo.aimanager.services;

import io.github.ollama4j.OllamaAPI;
import io.github.ollama4j.models.response.OllamaResult;
import io.github.ollama4j.utils.Options;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChatService {

    private final OllamaAPI ollamaApi;

    public ChatService() {
        this.ollamaApi = new OllamaAPI("http://localhost:11434");
        this.ollamaApi.setRequestTimeoutSeconds(60);
    }

    public String chat(String model, String prompt) {
        try {
            // Basit mesaj gönderme
            OllamaResult result = ollamaApi.generate(model, prompt, false, new Options(Map.of(
                    "temperature", 0.7f,
                    "num_predict", 1000,
                    "top_k", 40,
                    "top_p", 0.9f
            )));
            return "Yanıt: " + result.getResponse();
        } catch (Exception e) {
            return "Hata: " + e.getMessage();
        }
    }

    public String chatWithOptions(String model, String prompt) {
        try {
            // Daha detaylı seçeneklerle mesaj gönderme
            Options options = new Options(Map.of(
                    "temperature", 0.7f,
                    "num_predict", 1000,
                    "top_k", 40,
                    "top_p", 0.9f
            ));

            OllamaResult result = ollamaApi.generate(model, prompt, false, options);
            return "Yanıt: " + result.getResponse();
        } catch (Exception e) {
            return "Hata: " + e.getMessage();
        }
    }

    public String chatStream(String model, String prompt) {
        try {
            // Stream modunda mesaj gönderme (bloklanmaz)
            OllamaResult result = ollamaApi.generate(model, prompt, true, new Options(Map.of(
                    "temperature", 0.7f,
                    "num_predict", 1000,
                    "top_k", 40,
                    "top_p", 0.9f
            )));
            return "Stream Yanıt: " + result.getResponse();
        } catch (Exception e) {
            return "Hata: " + e.getMessage();
        }
    }

    // Model listesini almak için yardımcı metod
    public String getAvailableModels() {
        try {
            var models = ollamaApi.listModels();
            return "Mevcut modeller: " + models.toString();
        } catch (Exception e) {
            return "Model listesi alınamadı: " + e.getMessage();
        }
    }
}