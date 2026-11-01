package ru.materia.service.ai.gemini;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GeminiRequestBuilder {
    private static final String SYSTEM_PROMPT = """
        Ты система анализа промышленных изделий.

        Проанализируй описание пользователя и определи:
        - название изделия;
        - тип изделия;
        - ширину, высоту и глубину в миллиметрах;
        - основные конструктивные компоненты;
        - количество каждого компонента.

        Верни результат строго в формате JSON.

        Не добавляй пояснения или Markdown.
        """;
    private final ObjectMapper objectMapper = new ObjectMapper();       //для java- json и обратно
    public String buildRequest(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Описание не может быть пустым");
        }
        String prompt = SYSTEM_PROMPT + "\nОписание изделия:\n" + description;
        ObjectNode root = objectMapper.createObjectNode();              //[]
        ArrayNode contents = objectMapper.createArrayNode();            //{}
        ObjectNode contentObject = objectMapper.createObjectNode();
        ArrayNode parts = objectMapper.createArrayNode();
        ObjectNode textObject = objectMapper.createObjectNode();
        textObject.put("text", prompt);
        parts.add(textObject);
        contentObject.set("parts", parts);
        contents.add(contentObject);
        root.set("contents", contents);
        return root.toString();
    }
}
