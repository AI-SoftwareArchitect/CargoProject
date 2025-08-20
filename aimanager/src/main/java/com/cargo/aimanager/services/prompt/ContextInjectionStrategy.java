package com.cargo.aimanager.services.prompt;

import java.util.Map;

public class ContextInjectionStrategy implements PromptStrategy {

    private final Map<String, String> placeholders;

    public ContextInjectionStrategy(Map<String, String> placeholders) {
        this.placeholders = placeholders;
    }

    @Override
    public String apply(String input) {
        if (input == null) return "";
        String result = input;
        for (Map.Entry<String, String> entry : placeholders.entrySet()) {
            result = result.replace("{" + entry.getKey() + "}", entry.getValue());
        }
        return result;
    }
}
