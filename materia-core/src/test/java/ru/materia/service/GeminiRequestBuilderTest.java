package ru.materia.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;
import ru.materia.service.ai.gemini.GeminiRequestBuilder;

import static org.junit.jupiter.api.Assertions.*;

public class GeminiRequestBuilderTest {
    private final GeminiRequestBuilder builder = new GeminiRequestBuilder();
    @Test
    void shouldBuildValidJson() throws Exception {
        GeminiRequestBuilder builder = new GeminiRequestBuilder();
        String json = builder.buildRequest("Шкаф шириной 1200 мм");
        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);
        assertNotNull(root);
        assertTrue(root.has("contents"));
        assertTrue(root.get("contents").isArray());
    }
    @Test
    void shouldThrowExceptionWhenDescriptionIsNull() {
        assertThrows(
                IllegalArgumentException.class,
                () -> builder.buildRequest(null)
        );
    }
    @Test
    void shouldThrowExceptionWhenDescriptionIsEmpty() {
        assertThrows(
                IllegalArgumentException.class,
                () -> builder.buildRequest("")
        );
    }
    @Test
    void shouldThrowExceptionWhenDescriptionIsBlank() {
        assertThrows(
                IllegalArgumentException.class,
                () -> builder.buildRequest("   ")
        );
    }
    @Test
    void shouldContainUserDescription() throws Exception {
        String description = "Шкаф шириной 1200 мм";

        String json = builder.buildRequest(description);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        String prompt = root
                .get("contents")
                .get(0)
                .get("parts")
                .get(0)
                .get("text")
                .asText();

        assertTrue(prompt.contains(description));
    }
    @Test
    void shouldContainSystemPrompt() throws Exception {
        String json = builder.buildRequest("Создай компьютерный стол");

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(json);

        String prompt = root
                .get("contents")
                .get(0)
                .get("parts")
                .get(0)
                .get("text")
                .asText();
        assertTrue(prompt.contains("Ты система анализа промышленных изделий"));
    }

    @Test
    void shouldIncludeStructuredOutput() throws Exception {
        String json = builder.buildRequest("Стул высотой 950 мм");
        JsonNode root = new ObjectMapper().readTree(json);
        JsonNode format = root.path("generationConfig")
                .path("responseFormat")
                .path("text");

        assertEquals("APPLICATION_JSON",
                format.path("mimeType").asText());
        JsonNode schema = format.path("schema");
        assertEquals("object", schema.path("type").asText());

        assertTrue(schema.path("properties").has("productName"));
        assertTrue(schema.path("properties").has("productType"));
        assertTrue(schema.path("properties").has("dimensions"));
        assertTrue(schema.path("properties").has("components"));
    }

}
