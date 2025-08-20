package com.cargo.aimanager.services.prompt;

import java.util.regex.Pattern;

public class NSFWFilterStrategy implements PromptStrategy {

    private static final Pattern NSFW_PATTERN = Pattern.compile("porn|sex|xxx", Pattern.CASE_INSENSITIVE);

    @Override
    public String apply(String input) {
        if (input == null) return "";
        return NSFW_PATTERN.matcher(input).replaceAll("[REDACTED]");
    }
}