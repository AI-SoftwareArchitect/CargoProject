package com.cargo.aimanager.services.prompt;

public class SanitizationStrategy implements PromptStrategy {

    @Override
    public String apply(String input) {
        if (input == null) return "";
        return input.trim()
                .replaceAll("\\s+", " ")
                .replaceAll("[\\r\\n\\t]+", " ");
    }
}