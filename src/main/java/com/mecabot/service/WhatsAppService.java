package com.mecabot.service;

import java.util.Map;

public interface WhatsAppService {
    boolean sendMessage(String recipientPhoneNumber, String messageTemplateId, Map<String, String> templateParameters);
    // Consider a more generic version or overloaded methods for different message types (text, template, media) later
    // boolean sendTextMessage(String recipientPhoneNumber, String message);
}
