package ru.materia.service.ai.groq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GroqRequestBuilder {
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
              "required": ["width", "height", "depth"],
              "additionalProperties": false
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
                "required": ["name", "type", "quantity"],
                "additionalProperties": false
              }
            }
          },
          "required": [
            "productName",
            "productType",
            "dimensions",
            "components"
          ],
          "additionalProperties": false
        }
        """;


    public String buildRequest(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException(
                    "Описание изделия не должно быть пустым"
            );
        }
        ObjectNode request = mapper.createObjectNode();
        request.put("model", "openai/gpt-oss-20b");
        var messages = request.putArray("messages");
        messages.addObject()
                .put("role", "system")
                .put("content", SYSTEM_PROMPT);
        messages.addObject()
                .put("role", "user")
                .put("content", description);
        ObjectNode responseFormat = request.putObject("response_format");
        responseFormat.put("type", "json_schema");
        ObjectNode jsonSchema = responseFormat.putObject("json_schema");
        jsonSchema.put("name", "product_specification");
        jsonSchema.put("strict", true);
        try {
            jsonSchema.set("schema", mapper.readTree(RESPONSE_SCHEMA));
            return mapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException(
                    "Ошибка формирования запроса Groq", e
            );
        }
    }

}
