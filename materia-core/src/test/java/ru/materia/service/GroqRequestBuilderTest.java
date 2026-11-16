package ru.materia.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import ru.materia.service.ai.groq.GroqRequestBuilder;

import static org.junit.jupiter.api.Assertions.*;

class GroqRequestBuilderTest {
    private static final String VALID_DESCRIPTION = "Деревянный стол шириной 1200 мм";
    private final GroqRequestBuilder requestBuilder = new GroqRequestBuilder();
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void shouldRejectNullDescription() {
        assertThrows(IllegalArgumentException.class,
                () -> requestBuilder.buildRequest(null)
        );
    }

    @Test
    void shouldRejectBlankDescription() {
        assertThrows(
                IllegalArgumentException.class,
                () -> requestBuilder.buildRequest("")
        );

        assertThrows(
                IllegalArgumentException.class,
                () -> requestBuilder.buildRequest("   ")
        );
    }

    @Test
    void shouldContainModel() throws Exception {
        String json = requestBuilder.buildRequest(VALID_DESCRIPTION);
        JsonNode root = mapper.readTree(json);

        assertEquals(
                "openai/gpt-oss-20b",
                root.path("model").asText()
        );
    }

    @Test
    void shouldContainUserDescription() throws Exception {
        String json = requestBuilder.buildRequest(VALID_DESCRIPTION);
        JsonNode root = mapper.readTree(json);
        JsonNode userMessage = root.path("messages").get(1);
        assertEquals("user", userMessage.path("role").asText());
        assertEquals(VALID_DESCRIPTION, userMessage.path("content").asText());
    }

    @Test
    void shouldContainSystemPrompt() throws Exception {
        String json = requestBuilder.buildRequest(VALID_DESCRIPTION);
        JsonNode root = mapper.readTree(json);
        JsonNode systemMessage = root.path("messages").get(0);
        assertEquals("system", systemMessage.path("role").asText());
        assertTrue(systemMessage.path("content").asText().contains("Ты система анализа промышленных изделий"));
    }

    @Test
    void shouldContainJsonSchema() throws Exception {
        String json = requestBuilder.buildRequest(VALID_DESCRIPTION);
        JsonNode root = mapper.readTree(json);
        JsonNode responseFormat = root.path("response_format");
        assertEquals("json_schema", responseFormat.path("type").asText());
        JsonNode jsonSchema = responseFormat.path("json_schema");
        assertEquals("product_specification", jsonSchema.path("name").asText());
        JsonNode schema = jsonSchema.path("schema");
        assertEquals("object", schema.path("type").asText());
        JsonNode properties = schema.path("properties");

        assertTrue(properties.has("productName"));
        assertTrue(properties.has("productType"));
        assertTrue(properties.has("dimensions"));
        assertTrue(properties.has("components"));

        JsonNode dimensions = properties.path("dimensions");
        assertTrue(dimensions.path("properties").has("width"));
        assertTrue(dimensions.path("properties").has("height"));
        assertTrue(dimensions.path("properties").has("depth"));

        JsonNode components = properties.path("components");
        assertEquals("array", components.path("type").asText());

        JsonNode component = components.path("items");
        assertTrue(component.path("properties").has("name"));
        assertTrue(component.path("properties").has("type"));
        assertTrue(component.path("properties").has("quantity"));
    }

    @Test
    void shouldUseStrictJsonSchema() throws Exception {
        String json = requestBuilder.buildRequest(VALID_DESCRIPTION);
        JsonNode root = mapper.readTree(json);

        JsonNode jsonSchema = root.path("response_format")
                .path("json_schema");

        assertTrue(jsonSchema.path("strict").asBoolean());

        JsonNode schema = jsonSchema.path("schema");

        assertFalse(schema.path("additionalProperties").asBoolean(true));

        JsonNode dimensions = schema.path("properties")
                .path("dimensions");

        assertFalse(dimensions.path("additionalProperties").asBoolean(true));

        JsonNode component = schema.path("properties")
                .path("components")
                .path("items");

        assertFalse(component.path("additionalProperties").asBoolean(true));

        assertEquals(4, schema.path("required").size());
        assertEquals(3, dimensions.path("required").size());
        assertEquals(3, component.path("required").size());
    }
}

