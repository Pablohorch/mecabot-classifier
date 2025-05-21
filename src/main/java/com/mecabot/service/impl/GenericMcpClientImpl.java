package com.mecabot.service.impl;

import com.mecabot.model.McpRequest;
import com.mecabot.model.McpResponse;
import com.mecabot.service.McpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Optional;

@Service
public class GenericMcpClientImpl implements McpClient {

    private static final Logger log = LoggerFactory.getLogger(GenericMcpClientImpl.class);

    private final RestTemplate restTemplate;
    private final String mcpServiceUrl;
    private final String mcpServiceApiKey; // Optional, may not be used by all MCP services

    public GenericMcpClientImpl(
            RestTemplate restTemplate, // Consider configuring RestTemplate via a @Bean
            @Value("${mcp.service.url}") String mcpServiceUrl,
            @Value("${mcp.service.apikey:#{null}}") String mcpServiceApiKey) { // Default to null if not provided
        this.restTemplate = restTemplate;
        this.mcpServiceUrl = mcpServiceUrl;
        this.mcpServiceApiKey = mcpServiceApiKey;
    }

    @Override
    public Optional<McpResponse> processRequest(McpRequest request) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        if (mcpServiceApiKey != null && !mcpServiceApiKey.isEmpty()) {
            headers.set("Authorization", "Bearer " + mcpServiceApiKey); // Or other auth scheme
        }

        HttpEntity<McpRequest> entity = new HttpEntity<>(request, headers);

        try {
            log.info("Sending MCP request to URL: {}", mcpServiceUrl);
            // Ensure mcpServiceUrl is the full URL including any specific path
            ResponseEntity<McpResponse> response = restTemplate.exchange(
                    mcpServiceUrl,
                    HttpMethod.POST,
                    entity,
                    McpResponse.class);

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("MCP request successful. Status: {}", response.getStatusCode());
                return Optional.ofNullable(response.getBody());
            } else {
                log.error("MCP request failed. Status: {}, Body: {}", response.getStatusCode(), response.getBody());
                return Optional.empty();
            }
        } catch (Exception e) {
            log.error("Error during MCP request to {}: {}", mcpServiceUrl, e.getMessage(), e);
            return Optional.empty();
        }
    }
}
