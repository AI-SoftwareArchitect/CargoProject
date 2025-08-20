package com.cargo.aimanager.services.prompt;

import java.util.ArrayList;
import java.util.List;

public class PromptEngine {

    private final List<PromptStrategy> strategies = new ArrayList<>();

    public void addStrategy(PromptStrategy strategy) {
        strategies.add(strategy);
    }

    public String process(String input) {
        String result = input;
        for (PromptStrategy strategy : strategies) {
            result = strategy.apply(result);
        }
        return result;
    }
}