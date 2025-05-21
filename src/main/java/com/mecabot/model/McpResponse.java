package com.mecabot.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class McpResponse {
    private String status;
    private Object result;
    private Map<String, Object> diagnosticDetails;
}
