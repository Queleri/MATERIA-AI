package ru.materia;

import ru.materia.model.specification.ProductSpecification;
import ru.materia.service.ai.FallbackProductAnalyzer;
import ru.materia.service.ai.GeminiProductAnalyzer;
import ru.materia.service.ai.gemini.GeminiRequestBuilder;
import ru.materia.service.ai.groq.GroqProductAnalyzer;
import ru.materia.service.ai.groq.GroqRequestBuilder;
import ru.materia.service.ai.http.AiHttpClient;
import ru.materia.service.json.ProductJsonConverter;

import java.io.IOException;


public class Main {

    public static void main(String[] args) throws Exception {

        String geminiKey = System.getenv("GEMINI_API_KEY");
        String groqKey = System.getenv("GROQ_API_KEY");

        if (geminiKey == null || geminiKey.isBlank())
            throw new IllegalStateException("API-ключ Gemini не найден");

        if (groqKey == null || groqKey.isBlank())
            throw new IllegalStateException("API-ключ Groq не найден");

        ProductJsonConverter converter = new ProductJsonConverter();
        AiHttpClient httpClient = new AiHttpClient();

        GeminiProductAnalyzer geminiAnalyzer = new GeminiProductAnalyzer(
                httpClient, converter, geminiKey, new GeminiRequestBuilder()
        );

        GroqProductAnalyzer groqAnalyzer = new GroqProductAnalyzer(
                httpClient, converter, groqKey, new GroqRequestBuilder()
        );

        FallbackProductAnalyzer analyzer = new FallbackProductAnalyzer(
                geminiAnalyzer, groqAnalyzer
        );

        String description = """
                Создай спецификацию деревянного письменного стола.
                Ширина 1200 мм, высота 750 мм, глубина 600 мм.
                Стол состоит из столешницы и четырёх ножек.
                """;

        ProductSpecification specification = analyzer.analyze(description);

        System.out.println(converter.toJson(specification));
    }
}