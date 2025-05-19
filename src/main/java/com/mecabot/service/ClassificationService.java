package com.mecabot.service;

import com.mecabot.model.ClassificationResponse;
import com.mecabot.entity.RepairCategory;
import com.mecabot.repository.RepairCategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClassificationService {

    private final OpenAiClient openAiClient;
    private final RepairCategoryRepository repairCategoryRepository;
    private final ObjectMapper objectMapper;

    // Constante para el prompt del sistema de OpenAI
    private static final String SYSTEM_PROMPT_TEMPLATE = """
            Devuelve SOLO JSON con la forma {"category":"<snake_case>", "minutes":<int>}
            Categorías y minutos válidos: cambio_aceite=30, pastillas_freno=90, 
            revision_general=120, cambio_neumaticos=60, alineacion_direccion=45, 
            cambio_bateria=20, reparacion_escape=75, cambio_amortiguadores=150, 
            reparacion_motor=300, diagnostico_electronico=60.
            Si no reconoces la avería usa null en ambos campos.
            Descripción: \"%s\"
            """;

    public ClassificationService(OpenAiClient openAiClient, RepairCategoryRepository repairCategoryRepository, ObjectMapper objectMapper) {
        this.openAiClient = openAiClient;
        this.repairCategoryRepository = repairCategoryRepository;
        this.objectMapper = objectMapper;
    }

    public ClassificationResponse classify(String message) {
        // El prompt ahora se pasa directamente como mensaje de usuario al OpenAiClient
        // OpenAiClient internamente usará el modelo de chat que espera mensajes de usuario/sistema
        String userMessageForOpenAI = String.format(SYSTEM_PROMPT_TEMPLATE, message);

        String openAiResult = openAiClient.getResponse(userMessageForOpenAI);

        try {
            ClassificationResponse response = objectMapper.readValue(openAiResult, ClassificationResponse.class);
            if (response.getCategory() != null && response.getMinutes() != null) {
                return response;
            }
        } catch (Exception e) {
            System.err.println("Error parsing OpenAI response: " + e.getMessage());
        }

        // Si LLM devuelve null o hay error, buscar en BD
        Optional<RepairCategory> categoryOpt = repairCategoryRepository.findByNameIgnoreCase(message.trim());

        if (categoryOpt.isPresent()) {
            RepairCategory category = categoryOpt.get();
            return new ClassificationResponse(category.getName().toLowerCase().replace(" ", "_"), category.getAvgMinutes());
        }

        return new ClassificationResponse(null, null);
    }
}
