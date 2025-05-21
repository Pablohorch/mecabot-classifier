package com.mecabot.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class McpRequest {
    private String contextId;
    private String modelId;
    private Map<String, Object> payload;
}
