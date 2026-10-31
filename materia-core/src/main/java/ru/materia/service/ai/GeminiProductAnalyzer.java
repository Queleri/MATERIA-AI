package ru.materia.service.ai;

import ru.materia.model.specification.ProductSpecification;
import ru.materia.service.ai.http.AiHttpClient;
import ru.materia.service.json.ProductJsonConverter;

public class GeminiProductAnalyzer implements AiProductAnalyzer {

    private final AiHttpClient httpClient;
    private final ProductJsonConverter jsonConverter;
    private final String apiKey;

    public GeminiProductAnalyzer(AiHttpClient httpClient, ProductJsonConverter jsonConverter, String apiKey) {
        if (httpClient == null)
            throw new IllegalArgumentException("HTTP клиент не может быть null");
        if (jsonConverter == null)
            throw new IllegalArgumentException("JSON конвертер не может быть null");
        if(apiKey == null || apiKey.isBlank())
            throw new IllegalArgumentException("API ключ не может быть null или состоять из пробелов");
        this.httpClient = httpClient;
        this.jsonConverter = jsonConverter;
        this.apiKey = apiKey;
    }

    @Override
    public ProductSpecification analyze(String description) {
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Описание не может быть null или состоять из пробелов");
        throw new UnsupportedOperationException("Интеграция Gemini пока не реализована");
    }

}
