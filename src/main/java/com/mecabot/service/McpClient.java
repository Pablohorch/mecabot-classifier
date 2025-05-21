package com.mecabot.service;

import com.mecabot.model.McpRequest;
import com.mecabot.model.McpResponse;
import java.util.Optional;

public interface McpClient {
    Optional<McpResponse> processRequest(McpRequest request);
}
