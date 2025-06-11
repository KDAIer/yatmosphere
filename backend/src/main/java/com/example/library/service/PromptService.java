package com.example.library.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class PromptService {
    @Value("${chat.system-prompt}")
    private String systemPrompt;

    @Value("${chat.welcome-message}")
    private String welcomeMessage;

    public String getSystemPrompt() {
        return systemPrompt;
    }

    public String getWelcomeMessage() {
        return welcomeMessage;
    }
}
