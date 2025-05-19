package com.mecabot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * Cliente para interactuar con la API de OpenAI.
 * Implementa llamadas directas HTTP usando OkHttp para mayor control y simplicidad.
 */
@Service
public class OpenAiClient {

    private static final MediaType JSON = MediaType.get("application/json; charset=utf-8");
    private static final String OPENAI_API_URL = "https://api.openai.com/v1/chat/completions";
    
    private final OkHttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String apiKey;

    /**
     * Constructor que inyecta la clave API desde la configuración.
     */
    public OpenAiClient(@Value("${openai.api.key}") String apiKey) {
        this.apiKey = apiKey;
        this.objectMapper = new ObjectMapper();
        
        // Configuramos el cliente HTTP con timeouts adecuados
        this.httpClient = new OkHttpClient.Builder()
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
    }

    /**
     * Envía un prompt del usuario a la API de OpenAI y devuelve la respuesta como texto.
     */
    public String getResponse(String userPrompt) {
        try {
            // Construimos el cuerpo de la solicitud JSON para la API de chat completions
            ObjectNode requestJson = objectMapper.createObjectNode();
            requestJson.put("model", "gpt-3.5-turbo");
            requestJson.put("temperature", 0.0);  // Determinista
            requestJson.put("max_tokens", 150);   // Suficiente para la respuesta JSON
            
            // Creamos el array de mensajes
            ArrayNode messagesArray = requestJson.putArray("messages");
            
            // Añadimos el mensaje del usuario
            ObjectNode userMessage = messagesArray.addObject();
            userMessage.put("role", "user");
            userMessage.put("content", userPrompt);
            
            // Convertimos el JSON a string para el cuerpo de la solicitud
            String requestBody = objectMapper.writeValueAsString(requestJson);
            
            // Construimos la solicitud HTTP
            Request request = new Request.Builder()
                    .url(OPENAI_API_URL)
                    .addHeader("Authorization", "Bearer " + apiKey)
                    .addHeader("Content-Type", "application/json")
                    .post(RequestBody.create(requestBody, JSON))
                    .build();
            
            // Enviamos la solicitud y procesamos la respuesta
            try (Response response = httpClient.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    System.err.println("OpenAI API error: " + response.code() + " - " + response.message());
                    if (response.body() != null) {
                        System.err.println(response.body().string());
                    }
                    return "{\"category\":null, \"minutes\":null}";
                }
                
                if (response.body() == null) {
                    System.err.println("OpenAI API returned empty body");
                    return "{\"category\":null, \"minutes\":null}";
                }
                
                // Parseamos la respuesta JSON
                JsonNode responseJson = objectMapper.readTree(response.body().string());
                
                // Extraemos el contenido del mensaje de respuesta
                if (responseJson.has("choices") && 
                    responseJson.get("choices").size() > 0 && 
                    responseJson.get("choices").get(0).has("message") &&
                    responseJson.get("choices").get(0).get("message").has("content")) {
                    
                    return responseJson.get("choices").get(0).get("message").get("content").asText().trim();
                } else {
                    System.err.println("OpenAI API response missing expected fields");
                    System.err.println("Response: " + responseJson.toString());
                    return "{\"category\":null, \"minutes\":null}";
                }
            }
        } catch (IOException e) {
            System.err.println("Error calling OpenAI API: " + e.getMessage());
            e.printStackTrace();
            return "{\"category\":null, \"minutes\":null}";
        }
    }
}
