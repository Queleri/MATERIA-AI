package ru.materia.service.ai.gemini;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GeminiRequestBuilder {

    private final ObjectMapper mapper = new ObjectMapper();

    private static final String SYSTEM_PROMPT = """
        Ты система анализа промышленных изделий.

        Проанализируй описание изделия и определи:
        - название изделия;
        - тип изделия;
        - габариты в миллиметрах;
        - основные компоненты и их количество.

        Возвращай данные строго в соответствии
        с заданной JSON-схемой.

        Не добавляй пояснений и Markdown.
        """;

    private static final String RESPONSE_SCHEMA = """
            {
              "type": "object",
              "properties": {
                "productName": {
                  "type": "string"
                },
                "productType": {
                  "type": "string"
                },
                "dimensions": {
                  "type": "object",
                  "properties": {
                    "width": {"type": "integer"},
                    "height": {"type": "integer"},
                    "depth": {"type": "integer"}
                  },
                  "required": ["width", "height", "depth"]
                },
                "components": {
                  "type": "array",
                  "items": {
                    "type": "object",
                    "properties": {
                      "name": {"type": "string"},
                      "type": {"type": "string"},
                      "quantity": {"type": "integer"}
                    },
                    "required": ["name", "type", "quantity"]
                  }
                }
              },
              "required": [
                "productName",
                "productType",
                "dimensions",
                "components"
              ]
            }
            """;

    public String buildRequest(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Описание изделия не должно быть пустым"
            );
        }
        ObjectNode request = mapper.createObjectNode();
        request.putArray("contents")
                .addObject()
                .putArray("parts")
                .addObject()
                .put("text", SYSTEM_PROMPT + "\n" + description);
        ObjectNode format = request
                .putObject("generationConfig")
                .putObject("responseFormat")
                .putObject("text");
        format.put("mimeType", "APPLICATION_JSON");
        try {
            format.set("schema", mapper.readTree(RESPONSE_SCHEMA));
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Ошибка формирования запроса Gemini", e);
        }
    }
}

