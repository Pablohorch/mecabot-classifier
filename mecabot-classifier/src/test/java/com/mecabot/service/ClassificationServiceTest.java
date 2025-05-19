package com.mecabot.service;

import com.mecabot.model.ClassificationResponse;
import com.mecabot.entity.RepairCategory;
import com.mecabot.repository.RepairCategoryRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.startsWith;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class ClassificationServiceTest {

    @Mock
    private OpenAiClient openAiClient;

    @Mock
    private RepairCategoryRepository repairCategoryRepository;

    // Usamos @Spy para ObjectMapper para poder usar su funcionalidad real
    // y mockearla solo si es estrictamente necesario para un caso de prueba específico.
    // Para la mayoría de los casos, la instancia real es suficiente.
    @Spy
    private ObjectMapper objectMapper = new ObjectMapper();

    @InjectMocks
    private ClassificationService classificationService;

    private final String llmJsonResponseCategoryFound = "{\"category\":\"cambio_aceite\", \"minutes\":30}";
    private final String llmJsonResponseCategoryNotFound = "{\"category\":null, \"minutes\":null}";

    // No es necesario setUp si usamos @Spy y @InjectMocks correctamente.
    // @BeforeEach
    // void setUp() {
    // }

    @Test
    void classify_whenOpenAiReturnsCategory_shouldReturnResponseFromOpenAi() throws Exception {
        ClassificationResponse expectedOpenAiResponse = new ClassificationResponse("cambio_aceite", 30);
        // El prompt que se envía a OpenAI ahora incluye el mensaje del usuario formateado.
        when(openAiClient.getResponse(startsWith("Devuelve SOLO JSON"))).thenReturn(llmJsonResponseCategoryFound);
        // No necesitamos mockear objectMapper.readValue si el JSON es válido y el objeto es simple.
        // Si el JSON fuera inválido o el mapeo complejo, podríamos mockearlo.

        ClassificationResponse actualResponse = classificationService.classify("Necesito un cambio de aceite");

        assertEquals(expectedOpenAiResponse.getCategory(), actualResponse.getCategory());
        assertEquals(expectedOpenAiResponse.getMinutes(), actualResponse.getMinutes());
        verify(repairCategoryRepository, never()).findByNameIgnoreCase(anyString());
    }

    @Test
    void classify_whenOpenAiReturnsNullAndDbFindsCategory_shouldReturnResponseFromDb() throws Exception {
        String message = "Cambio de batería";
        RepairCategory dbCategory = new RepairCategory(1L, "Cambio de batería", 45);
        // Esperamos que el nombre de la categoría de la BD se convierta a snake_case
        ClassificationResponse expectedDbResponse = new ClassificationResponse("cambio_de_batería", 45);

        when(openAiClient.getResponse(startsWith("Devuelve SOLO JSON"))).thenReturn(llmJsonResponseCategoryNotFound);
        when(repairCategoryRepository.findByNameIgnoreCase(message.trim())).thenReturn(Optional.of(dbCategory));

        ClassificationResponse actualResponse = classificationService.classify(message);

        assertEquals(expectedDbResponse.getCategory(), actualResponse.getCategory());
        assertEquals(expectedDbResponse.getMinutes(), actualResponse.getMinutes());
    }

    @Test
    void classify_whenOpenAiReturnsNullAndDbNotFindsCategory_shouldReturnNullResponse() throws Exception {
        String message = "Reparación muy específica";
        when(openAiClient.getResponse(startsWith("Devuelve SOLO JSON"))).thenReturn(llmJsonResponseCategoryNotFound);
        when(repairCategoryRepository.findByNameIgnoreCase(message.trim())).thenReturn(Optional.empty());

        ClassificationResponse actualResponse = classificationService.classify(message);

        assertNull(actualResponse.getCategory());
        assertNull(actualResponse.getMinutes());
    }

    @Test
    void classify_whenOpenAiResponseIsInvalidJson_shouldTryDbAndReturnNullIfNotFound() throws Exception {
        String message = "Problema con el motor";
        String invalidJsonFromOpenAi = "Esto no es un JSON";
        when(openAiClient.getResponse(startsWith("Devuelve SOLO JSON"))).thenReturn(invalidJsonFromOpenAi);
        // No es necesario mockear objectMapper.readValue para que lance la excepción,
        // la instancia real de ObjectMapper lo hará si el JSON es inválido.
        when(repairCategoryRepository.findByNameIgnoreCase(message.trim())).thenReturn(Optional.empty());

        ClassificationResponse actualResponse = classificationService.classify(message);

        assertNull(actualResponse.getCategory());
        assertNull(actualResponse.getMinutes());
        // Verificamos que se intentó buscar en la BD después del fallo de OpenAI
        verify(repairCategoryRepository).findByNameIgnoreCase(message.trim());
    }

    @Test
    void classify_whenOpenAiResponseIsInvalidJson_shouldTryDbAndReturnCategoryIfFound() throws Exception {
        String message = "Cambio de aceite"; // Mensaje que coincide con una categoría de BD
        String invalidJsonFromOpenAi = "Esto no es un JSON";
        RepairCategory dbCategory = new RepairCategory(2L, "Cambio de aceite", 30);
        ClassificationResponse expectedDbResponse = new ClassificationResponse("cambio_de_aceite", 30);

        when(openAiClient.getResponse(startsWith("Devuelve SOLO JSON"))).thenReturn(invalidJsonFromOpenAi);
        when(repairCategoryRepository.findByNameIgnoreCase(message.trim())).thenReturn(Optional.of(dbCategory));

        ClassificationResponse actualResponse = classificationService.classify(message);

        assertEquals(expectedDbResponse.getCategory(), actualResponse.getCategory());
        assertEquals(expectedDbResponse.getMinutes(), actualResponse.getMinutes());
        verify(repairCategoryRepository).findByNameIgnoreCase(message.trim());
    }
}
