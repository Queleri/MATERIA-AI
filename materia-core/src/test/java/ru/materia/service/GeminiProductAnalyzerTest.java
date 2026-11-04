package ru.materia.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.materia.model.specification.ProductSpecification;
import ru.materia.service.ai.GeminiProductAnalyzer;
import ru.materia.service.ai.gemini.GeminiRequestBuilder;
import ru.materia.service.ai.http.AiHttpClient;
import ru.materia.service.json.ProductJsonConverter;
import static org.junit.jupiter.api.Assertions.*;


public class GeminiProductAnalyzerTest {
    private GeminiProductAnalyzer analyzer;
    private AiHttpClient httpClient;
    private static final String VALID_DESCRIPTION =
            "Стул высотой 950 мм, шириной 450 мм и глубиной 500 мм";
    private     String specificationJson = """
            {
              "productName": "Стул эргономичный WoodComfort",
              "productType": "Мебель",
              "dimensions": {
                "width": 450,
                "height": 950,
                "depth": 500
              },
              "components": [
                {
                  "name": "Спинка деревянная",
                  "type": "Деревянная деталь",
                  "quantity": 1
                },
                {
                  "name": "Сиденье",
                  "type": "Мягкая деталь",
                  "quantity": 1
                },
                {
                  "name": "Ножки металлические",
                  "type": "Металлическая деталь",
                  "quantity": 4
                }
              ]
            }
            """;
    private ProductJsonConverter converter ;
    private ObjectMapper mapper;
    @BeforeEach
    void setUp() {
        converter = new ProductJsonConverter();
        mapper = new ObjectMapper();
        httpClient = Mockito.mock(AiHttpClient.class);   //fake
        ProductJsonConverter productJsonConverter = new ProductJsonConverter();
        String apiKey = "test-api-key";
        GeminiRequestBuilder geminiRequestBuilder = new GeminiRequestBuilder();
        this.analyzer = new GeminiProductAnalyzer(httpClient,
                productJsonConverter, apiKey,geminiRequestBuilder );
    }

    private void mockGeminiResponse(String response) throws Exception {
        Mockito.when(httpClient.sendPost(
                Mockito.anyString(),
                Mockito.anyString(),
                Mockito.anyString()
        )).thenReturn(response);
    }

        @Test
        void shouldReturnValidProductSpecification() throws Exception {
            ObjectNode response = mapper.createObjectNode();

            ObjectNode candidate = response
                    .putArray("candidates")
                    .addObject();

            ObjectNode content = candidate.putObject("content");

            content.putArray("parts")
                    .addObject()
                    .put("text", specificationJson);

            String geminiResponse = response.toString();
            Mockito.when(httpClient.sendPost(
                    Mockito.anyString(),
                    Mockito.anyString(),
                    Mockito.anyString()
            )).thenReturn(geminiResponse);
            ProductSpecification result = analyzer.analyze(
                    "Стул высотой 950 мм, шириной 450 мм и глубиной 500 мм"
            );
            assertNotNull(result);
            JsonNode resultJson = mapper.readTree(converter.toJson(result));
            assertEquals(
                    "Стул эргономичный WoodComfort",
                    resultJson.path("productName").asText()
            );
            assertEquals(
                    450,
                    resultJson.path("dimensions").path("width").asInt()
            );
            assertEquals(
                    950,
                    resultJson.path("dimensions").path("height").asInt()
            );
            assertEquals(
                    500,
                    resultJson.path("dimensions").path("depth").asInt()
            );
            assertEquals(
                    3,
                    resultJson.path("components").size()
            );
        }


    @Test
    void shouldThrowExceptionWhenDescriptionIsNull() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.analyze(null));

    }
    @Test
    void shouldThrowExceptionWhenDescriptionIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.analyze(""));
    }
    @Test
    void shouldThrowExceptionWhenDescriptionIsBlank() {
        assertThrows(IllegalArgumentException.class, () -> analyzer.analyze(" "));
    }


    @Test
    void shouldThrowExceptionWhenCandidatesAreMissing() throws Exception {
        String response = "{}";

        mockGeminiResponse(response);

        assertThrows(
                IllegalStateException.class,
                () -> analyzer.analyze(VALID_DESCRIPTION)
        );
    }

    @Test
    void shouldThrowExceptionWhenCandidatesAreEmpty() throws Exception {
        String response = """
            {
              "candidates": []
            }
            """;

        mockGeminiResponse(response);

        assertThrows(
                IllegalStateException.class,
                () -> analyzer.analyze(VALID_DESCRIPTION)
        );
    }

    @Test
    void shouldThrowExceptionWhenPartsAreMissing() throws Exception {
        String response = """
            {
              "candidates": [
                {
                  "content": {}
                }
              ]
            }
            """;

        mockGeminiResponse(response);

        assertThrows(
                IllegalStateException.class,
                () -> analyzer.analyze(VALID_DESCRIPTION)
        );
    }

    @Test
    void shouldThrowExceptionWhenResponseTextIsEmpty() throws Exception {
        String response = """
            {
              "candidates": [
                {
                  "content": {
                    "parts": [
                      {
                        "text": ""
                      }
                    ]
                  }
                }
              ]
            }
            """;

        mockGeminiResponse(response);

        assertThrows(
                IllegalStateException.class,
                () -> analyzer.analyze(VALID_DESCRIPTION)
        );
    }

    @Test
    void shouldThrowExceptionWhenSpecificationJsonIsInvalid() throws Exception {
        String response = """
            {
              "candidates": [
                {
                  "content": {
                    "parts": [
                      {
                        "text": "This is not valid JSON"
                      }
                    ]
                  }
                }
              ]
            }
            """;

        mockGeminiResponse(response);

        assertThrows(
                RuntimeException.class,
                () -> analyzer.analyze(VALID_DESCRIPTION)
        );
    }

}
