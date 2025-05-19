package com.mecabot.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * Cliente para interactuar con la API de OpenAI.
 * Utiliza RestTemplate de Spring para mayor integración con el framework.
 */
@Service
public class OpenAiClient {
    
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    /**
     * Constructor que inyecta la clave API desde la configuración.
     */
    public OpenAiClient(@Value("${openai.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
        this.restTemplate = new RestTemplate();
    }

    /**
     * Envía un prompt del usuario a la API de OpenAI y devuelve la respuesta como texto.
     */
    public String getResponse(String userPrompt) {
        try {
            // Preparamos los headers HTTP
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);
            
            // Construimos el cuerpo de la solicitud JSON
            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", "gpt-3.5-turbo");
            requestBody.put("temperature", 0.0);
            requestBody.put("max_tokens", 150);
            
            // Creamos el array de mensajes
            ArrayNode messages = requestBody.putArray("messages");
            ObjectNode message = messages.addObject();
            message.put("role", "user");
            message.put("content", userPrompt);
            
            // Creamos la entidad HTTP con headers y body
            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            
            // Hacemos la llamada a la API
            ResponseEntity<String> response = restTemplate.postForEntity(OPENAI_API_URL, request, String.class);
            
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Parseamos la respuesta JSON
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                
                // Extraemos el contenido del mensaje
                if (responseJson.has("choices") && 
                    responseJson.get("choices").size() > 0 && 
                    responseJson.get("choices").get(0).has("message") &&
                    responseJson.get("choices").get(0).get("message").has("content")) {
                    
                    return responseJson.get("choices").get(0).get("message").get("content").asText().trim();
                }
            }
            
            System.err.println("OpenAI API error or unexpected response format");
            return "{\"category\":null, \"minutes\":null}";
        } catch (Exception e) {
            System.err.println("Error calling OpenAI API: " + e.getMessage());
            e.printStackTrace();
            return "{\"category\":null, \"minutes\":null}";
        }
    }
}
