package com.mecabot.controller;

import com.mecabot.model.ClassificationRequest;
import com.mecabot.model.ClassificationResponse;
import com.mecabot.service.ClassificationService;
import jakarta.validation.Valid; // For Spring Boot 3
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/classify") // Path updated as per the API_REFERENCE.md
public class ClassificationController {

    private final ClassificationService classificationService;

    public ClassificationController(ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    @PostMapping
    public ResponseEntity<ClassificationResponse> classify(@Valid @RequestBody ClassificationRequest request) {
        // The field name in ClassificationRequest was changed from "message" to "problemDescription"
        ClassificationResponse response = classificationService.classify(request.getProblemDescription());
        return ResponseEntity.ok(response);
    }
}
