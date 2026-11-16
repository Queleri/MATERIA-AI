package ru.materia.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.materia.model.specification.ProductSpecification;
import ru.materia.service.ai.exception.AiApiException;
import ru.materia.service.ai.groq.GroqProductAnalyzer;
import ru.materia.service.ai.groq.GroqRequestBuilder;
import ru.materia.service.ai.http.AiHttpClient;
import ru.materia.service.json.ProductJsonConverter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class GroqProductAnalyzerTest {

    private static final String VALID_DESCRIPTION =
            "Деревянный стол шириной 1200 мм, высотой 750 мм и глубиной 600 мм";

    private static final String SPECIFICATION_JSON = """
            {
              "productName": "Деревянный стол",
              "productType": "Мебель",
              "dimensions": {
                "width": 1200,
                "height": 750,
                "depth": 600
              },
              "components": [
                {
                  "name": "Столешница",
                  "type": "Панель",
                  "quantity": 1
                },
                {
                  "name": "Ножка",
                  "type": "Опора",
                  "quantity": 4
                }
              ]
            }
            """;

    private final ObjectMapper mapper = new ObjectMapper();
    private final ProductJsonConverter converter = new ProductJsonConverter();

    private GroqProductAnalyzer analyzer;
    private AiHttpClient httpClient;

    @BeforeEach
    void setUp() {
        httpClient = mock(AiHttpClient.class);

        analyzer = new GroqProductAnalyzer(
                httpClient,
                converter,
                "test-api-key",
                new GroqRequestBuilder()
        );
    }

    @Test
    void shouldReturnValidProductSpecification() throws Exception {
        mockResponse(createGroqResponse(SPECIFICATION_JSON));

        ProductSpecification result = analyzer.analyze(VALID_DESCRIPTION);

        assertNotNull(result);

        JsonNode json = mapper.readTree(converter.toJson(result));

        assertEquals("Деревянный стол", json.path("productName").asText());
        assertEquals("Мебель", json.path("productType").asText());

        assertEquals(1200, json.path("dimensions").path("width").asInt());
        assertEquals(750, json.path("dimensions").path("height").asInt());
        assertEquals(600, json.path("dimensions").path("depth").asInt());

        assertEquals(2, json.path("components").size());
        assertEquals(4, json.path("components").get(1).path("quantity").asInt());
    }

    @Test
    void shouldRejectNullDescription() {
        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze(null)
        );
    }

    @Test
    void shouldRejectBlankDescription() {
        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze("")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> analyzer.analyze("   ")
        );
    }

    @Test
    void shouldThrowWhenChoicesMissing() throws Exception {
        mockResponse("{}");

        assertThrows(
                IllegalStateException.class,
                () -> analyzer.analyze(VALID_DESCRIPTION)
        );
    }

    @Test
    void shouldThrowWhenChoicesEmpty() throws Exception {
        mockResponse("{\"choices\":[]}");

        assertThrows(
                IllegalStateException.class,
                () -> analyzer.analyze(VALID_DESCRIPTION)
        );
    }

    @Test
    void shouldThrowWhenContentEmpty() throws Exception {
        mockResponse(createGroqResponse(""));

        assertThrows(
                IllegalStateException.class,
                () -> analyzer.analyze(VALID_DESCRIPTION)
        );
    }

    @Test
    void shouldThrowWhenJsonInvalid() throws Exception {
        mockResponse(createGroqResponse("Это не JSON"));

        assertThrows(
                RuntimeException.class,
                () -> analyzer.analyze(VALID_DESCRIPTION)
        );
    }

    @Test
    void shouldPreserveAiApiException() throws Exception {
        when(httpClient.sendPost(
                anyString(),
                anyString(),
                anyMap()
        )).thenThrow(new AiApiException(429, "Превышен лимит"));

        AiApiException exception = assertThrows(
                AiApiException.class,
                () -> analyzer.analyze(VALID_DESCRIPTION)
        );

        assertEquals(429, exception.getStatusCode());
        assertEquals("Превышен лимит", exception.getMessage());
    }

    private void mockResponse(String response) throws Exception {
        when(httpClient.sendPost(
                anyString(),
                anyString(),
                anyMap()
        )).thenReturn(response);
    }

    private String createGroqResponse(String content) {
        ObjectNode response = mapper.createObjectNode();

        response.putArray("choices")
                .addObject()
                .putObject("message")
                .put("content", content);

        return response.toString();
    }
}

