package ru.materia.service.ai;

import ru.materia.model.specification.ProductSpecification;
import ru.materia.service.ai.exception.AiApiException;
import java.io.IOException;

public class FallbackProductAnalyzer implements AiProductAnalyzer {

    private final AiProductAnalyzer primaryAnalyzer;
    private final AiProductAnalyzer fallbackAnalyzer;

    public FallbackProductAnalyzer(AiProductAnalyzer primaryAnalyzer, AiProductAnalyzer fallbackAnalyzer) {
        if (primaryAnalyzer == null || fallbackAnalyzer == null)
            throw new IllegalArgumentException("AI-анализаторы не могут быть null");

        this.primaryAnalyzer = primaryAnalyzer;
        this.fallbackAnalyzer = fallbackAnalyzer;
    }

    @Override
    public ProductSpecification analyze(String description) throws IOException, InterruptedException {
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Описание изделия не может быть пустым");

        try {
            return primaryAnalyzer.analyze(description);
        } catch (AiApiException e) {
            int statusCode = e.getStatusCode();

            if (statusCode != 429 && (statusCode < 500 || statusCode > 504))
                throw e;

            try {
                return fallbackAnalyzer.analyze(description);
            } catch (AiApiException fallbackError) {
                throw new IllegalStateException("Обе AI-модели недоступны. Основная: HTTP "
                        + statusCode + ", резервная: HTTP " + fallbackError.getStatusCode(), fallbackError);
            }
        }
    }
}