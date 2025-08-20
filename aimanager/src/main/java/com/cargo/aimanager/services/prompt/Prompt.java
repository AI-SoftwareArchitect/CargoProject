package com.cargo.aimanager.services.prompt;

import java.util.HashMap;
import java.util.Map;

public class Prompt {

    private String systemMessage = "";
    private String message;

    private final PromptEngine engine;

    public Prompt(String message) {
        this.message = message;
        this.engine = new PromptEngine();

        // Strategy zinciri ekle
        engine.addStrategy(new SanitizationStrategy());
        engine.addStrategy(new NSFWFilterStrategy());
        engine.addStrategy(new FormattingStrategy());

        Map<String, String> placeholders = new HashMap<>();
        placeholders.put("date", java.time.LocalDate.now().toString());
        engine.addStrategy(new ContextInjectionStrategy(placeholders));
    }

    public String getMessage() {
        return engine.process(systemMessage + " " + message);
    }

    public void setMessage(String message) {
        PromptValidator validator = new PromptValidator();
        if (!validator.isValidPromptMessage(message)) return;
        this.message = message;
    }

    public void setSystemMessage(String systemMessage) {
        this.systemMessage = systemMessage;
    }
}
