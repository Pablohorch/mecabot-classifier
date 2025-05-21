package com.mecabot.service.impl;

import com.mecabot.service.WhatsAppService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
// Required for List and ArrayList if the commented-out component section is used
// import java.util.List; 
// import java.util.ArrayList;

@Service
public class WhatsAppServiceImpl implements WhatsAppService {

    private static final Logger log = LoggerFactory.getLogger(WhatsAppServiceImpl.class);

    private final RestTemplate restTemplate;
    private final String whatsappApiUrl;
    private final String whatsappApiToken;
    private final String whatsappPhoneNumberId;

    public WhatsAppServiceImpl(
            RestTemplate restTemplate, // Consider configuring RestTemplate via a @Bean
            @Value("${whatsapp.api.url}") String whatsappApiUrl,
            @Value("${whatsapp.api.token}") String whatsappApiToken,
            @Value("${whatsapp.phonenumber.id}") String whatsappPhoneNumberId) {
        this.restTemplate = restTemplate;
        this.whatsappApiUrl = whatsappApiUrl; // Should be like https://graph.facebook.com/vXX.X
        this.whatsappApiToken = whatsappApiToken;
        this.whatsappPhoneNumberId = whatsappPhoneNumberId; // Used to construct the full URL path
    }

    @Override
    public boolean sendMessage(String recipientPhoneNumber, String messageTemplateId, Map<String, String> templateParameters) {
        // Construct the full API URL, typically includes the phone number ID in the path
        String fullApiUrl = whatsappApiUrl.endsWith("/") ? whatsappApiUrl : whatsappApiUrl + "/";
        fullApiUrl = fullApiUrl + this.whatsappPhoneNumberId + "/messages";


        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + whatsappApiToken);
        headers.set("Content-Type", "application/json");

        Map<String, Object> payload = new HashMap<>();
        payload.put("messaging_product", "whatsapp");
        payload.put("to", recipientPhoneNumber);
        payload.put("type", "template");

        Map<String, Object> template = new HashMap<>();
        template.put("name", messageTemplateId);

        Map<String, Object> language = new HashMap<>();
        language.put("code", "en_US"); // Or make language code configurable / part of templateParameters
        template.put("language", language);
        
        // Handling template parameters (components)
        // This section needs to be adapted based on actual template structure and WhatsApp provider (e.g., Meta)
        // The example below is a common way to structure parameters for body variables.
        // if (templateParameters != null && !templateParameters.isEmpty()) {
        //     List<Map<String, Object>> components = new ArrayList<>();
        //     Map<String, Object> bodyComponent = new HashMap<>();
        //     bodyComponent.put("type", "body");
        //     List<Map<String, String>> parametersList = new ArrayList<>();
        //     // Assuming templateParameters keys like "1", "2" map to {{1}}, {{2}}
        //     // Or iterate based on a predefined order if keys are not positional
        //     templateParameters.forEach((key, value) -> {
        //         Map<String, String> param = new HashMap<>();
        //         param.put("type", "text");
        //         param.put("text", value);
        //         parametersList.add(param);
        //     });
        //     bodyComponent.put("parameters", parametersList);
        //     components.add(bodyComponent);
        //     template.put("components", components);
        // }

        payload.put("template", template);
        
        log.info("Attempting to send WhatsApp template message to {} with template ID {}. URL: {}", recipientPhoneNumber, messageTemplateId, fullApiUrl);
        // For debugging, you might log the payload, but be careful with sensitive data:
        // log.debug("WhatsApp Payload: {}", payload);


        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(payload, headers);

        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    fullApiUrl,
                    HttpMethod.POST,
                    entity,
                    String.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("WhatsApp message sent successfully to {}. Response: {}", recipientPhoneNumber, response.getBody());
                return true;
            } else {
                log.error("Failed to send WhatsApp message to {}. Status: {}, Response: {}",
                        recipientPhoneNumber, response.getStatusCode(), response.getBody());
                return false;
            }
        } catch (Exception e) {
            log.error("Error sending WhatsApp message to {}: {}", recipientPhoneNumber, e.getMessage(), e);
            return false;
        }
    }
}
