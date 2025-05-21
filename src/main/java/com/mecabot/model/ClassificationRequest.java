package com.mecabot.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotBlank; // For Spring Boot 3
import jakarta.validation.constraints.Size;   // For Spring Boot 3

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ClassificationRequest {

    @NotBlank(message = "Problem description cannot be blank.")
    @Size(min = 10, max = 2000, message = "Problem description must be between 10 and 2000 characters.")
    private String problemDescription;
}
