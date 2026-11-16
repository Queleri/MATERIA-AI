package ru.materia.service.ai.groq;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.materia.model.specification.ProductSpecification;
import ru.materia.service.ai.AiProductAnalyzer;
import ru.materia.service.ai.http.AiHttpClient;
import ru.materia.service.json.ProductJsonConverter;

import java.io.IOException;
import java.util.Map;

public class GroqProductAnalyzer implements AiProductAnalyzer {

    private static final String API_URL =
            "https://api.groq.com/openai/v1/chat/completions";

    private final AiHttpClient httpClient;
    private final ProductJsonConverter jsonConverter;
    private final GroqRequestBuilder requestBuilder;
    private final String apiKey;
    private final ObjectMapper mapper = new ObjectMapper();

    public GroqProductAnalyzer(
            AiHttpClient httpClient,
            ProductJsonConverter jsonConverter,
            String apiKey,
            GroqRequestBuilder requestBuilder
    ) {
        if (httpClient == null)
            throw new IllegalArgumentException("HTTP-клиент не может быть null");

        if (jsonConverter == null)
            throw new IllegalArgumentException("JSON-конвертер не может быть null");

        if (apiKey == null || apiKey.isBlank())
            throw new IllegalArgumentException("API-ключ Groq не может быть пустым");

        if (requestBuilder == null)
            throw new IllegalArgumentException("GroqRequestBuilder не может быть null");

        this.httpClient = httpClient;
        this.jsonConverter = jsonConverter;
        this.apiKey = apiKey;
        this.requestBuilder = requestBuilder;
    }

    @Override
    public ProductSpecification analyze(String description) {

        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Описание изделия не может быть пустым");
        }

        String requestJson = requestBuilder.buildRequest(description);

        Map<String, String> headers = Map.of("Authorization", "Bearer " + apiKey);

        try {
            String response = httpClient.sendPost(API_URL, requestJson, headers);

            JsonNode root = mapper.readTree(response);
            JsonNode choices = root.path("choices");

            if (!choices.isArray() || choices.isEmpty()) {
                throw new IllegalStateException("Groq не вернул результатов генерации");
            }

            JsonNode content = choices.get(0)
                    .path("message")
                    .path("content");

            if (!content.isTextual() || content.asText().isBlank()) {
                throw new IllegalStateException("Groq вернул пустой или некорректный ответ");
            }

            return jsonConverter.fromJson(content.asText());

        } catch (IOException e) {
            throw new IllegalStateException("Ошибка обмена данными с Groq или обработки JSON", e);

        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();

            throw new IllegalStateException("Запрос к Groq был прерван", e);
        }
    }
}
