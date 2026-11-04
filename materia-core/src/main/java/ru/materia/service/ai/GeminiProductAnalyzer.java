package ru.materia.service.ai;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.materia.model.specification.ProductSpecification;
import ru.materia.service.ai.gemini.GeminiRequestBuilder;
import ru.materia.service.ai.http.AiHttpClient;
import ru.materia.service.json.ProductJsonConverter;

import java.io.IOException;

public class GeminiProductAnalyzer implements AiProductAnalyzer {

    private final AiHttpClient httpClient;
    private final ProductJsonConverter jsonConverter;
    private final String apiKey;
    private final GeminiRequestBuilder requestBuilder;
    String apiURL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash-lite:generateContent";

    public GeminiProductAnalyzer(AiHttpClient httpClient, ProductJsonConverter jsonConverter, String apiKey, GeminiRequestBuilder requestBuilder) {

        if (httpClient == null)
            throw new IllegalArgumentException("HTTP клиент не может быть null");
        if (jsonConverter == null)
            throw new IllegalArgumentException("JSON конвертер не может быть null");
        if(apiKey == null || apiKey.isBlank())
            throw new IllegalArgumentException("API ключ не может быть null или состоять из пробелов");
        if (requestBuilder == null)
            throw new IllegalArgumentException("requestBuilder не может быть null");
        this.httpClient = httpClient;
        this.jsonConverter = jsonConverter;
        this.apiKey = apiKey;
        this.requestBuilder = requestBuilder;
    }


    @Override
    public ProductSpecification analyze(String description) {
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Описание не может быть null или состоять из пробелов");
        var requestJson = requestBuilder.buildRequest(description);

        try {
            String answerGemini =  httpClient.sendPost(apiURL,requestJson,apiKey);
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode answerJSON = objectMapper.readTree(answerGemini);
            JsonNode candidatesArray = answerJSON.path("candidates");
            if (!candidatesArray.isArray() || candidatesArray.isEmpty()) {
                throw new IllegalStateException(
                        "Gemini не вернул результатов генерации"
                );
            }
            JsonNode partsArray = candidatesArray.get(0)
                    .path("content")
                    .path("parts");

            if (!partsArray.isArray() || partsArray.isEmpty()) {
                throw new IllegalStateException(
                        "Ответ Gemini не содержит parts"
                );
            }
            JsonNode textNode = partsArray.get(0).path("text");

            if (!textNode.isTextual() || textNode.asText().isBlank()) {
                throw new IllegalStateException(
                        "Gemini вернул пустой или некорректный текст"
                );
            }

            String resultText = textNode.asText();
            return jsonConverter.fromJson(resultText);

        } catch (IOException e) {
            throw new IllegalStateException("Ошибка обмена данными с Gemini или обработки JSON", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Запрос к Gemini был прерван", e);
        }
    }

}
