package com.cargo.aimanager.services.prompt;

public class FormattingStrategy implements PromptStrategy {

    @Override
    public String apply(String input) {
        if (input == null || input.isEmpty()) return "";
        String[] sentences = input.split("(?<=[.!?])\\s*");
        StringBuilder sb = new StringBuilder();
        for (String sentence : sentences) {
            if (!sentence.isEmpty()) {
                sb.append(Character.toUpperCase(sentence.charAt(0)))
                        .append(sentence.substring(1))
                        .append(" ");
            }
        }
        return sb.toString().trim();
    }
}
