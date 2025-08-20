package com.cargo.aimanager.services.prompt;

import java.util.regex.Pattern;

public class PromptValidator {

    private boolean isValidPrompt;

    public PromptValidator() {
        this.isValidPrompt = true;
    }

    public boolean isValidPromptMessage(String message) {
        if (message == null || message.trim().isEmpty()) {
            return false;
        }

        if (!containsSqlOrNoSql(message)) return false;
        if (containsHarmfulContent(message)) return false;
        if (containsNSFW(message)) return false;
        if (containsXSS(message)) return false;
        if (containsURL(message)) return false;

        return true;
    }

    // Basit SQL/NoSQL keyword kontrolü
    private boolean containsSqlOrNoSql(String message) {
        String[] forbidden = {"SELECT", "INSERT", "UPDATE", "DELETE", "DROP", "db.", "find(", "aggregate("};
        for (String word : forbidden) {
            if (message.toUpperCase().contains(word)) {
                return false;
            }
        }
        return true;
    }

    // Basit zararlı içerik kontrolü (küfür vs.)
    private boolean containsHarmfulContent(String message) {
        String[] harmful = {"malware", "virus", "exploit", "hack"};
        for (String word : harmful) {
            if (message.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    }

    // NSFW basit kontrol
    private boolean containsNSFW(String message) {
        String[] nsfw = {"porn", "sex", "xxx"};
        for (String word : nsfw) {
            if (message.toLowerCase().contains(word)) {
                return true;
            }
        }
        return false;
    }

    // XSS basit regex kontrolü
    private boolean containsXSS(String message) {
        Pattern pattern = Pattern.compile("<\\s*script.*?>.*?<\\s*/\\s*script\\s*>", Pattern.CASE_INSENSITIVE);
        return pattern.matcher(message).find();
    }

    // URL kontrolü
    private boolean containsURL(String message) {
        Pattern pattern = Pattern.compile(
                "(http|https)://[\\w\\-]+(\\.[\\w\\-]+)+[/#?]?.*",
                Pattern.CASE_INSENSITIVE
        );
        return pattern.matcher(message).find();
    }

}
