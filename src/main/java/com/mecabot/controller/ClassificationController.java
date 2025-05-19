package com.mecabot.controller;

import com.mecabot.model.ClassificationRequest;
import com.mecabot.model.ClassificationResponse;
import com.mecabot.service.ClassificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/classify")
public class ClassificationController {

    private final ClassificationService classificationService;

    public ClassificationController(ClassificationService classificationService) {
        this.classificationService = classificationService;
    }

    @PostMapping
    public ResponseEntity<ClassificationResponse> classify(@RequestBody ClassificationRequest request) {
        ClassificationResponse response = classificationService.classify(request.getMessage());
        return ResponseEntity.ok(response);
    }
}
