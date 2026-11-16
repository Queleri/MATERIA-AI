package ru.materia;

import ru.materia.model.specification.ProductSpecification;
import ru.materia.service.ai.GeminiProductAnalyzer;
import ru.materia.service.ai.gemini.GeminiRequestBuilder;
import ru.materia.service.ai.groq.GroqProductAnalyzer;
import ru.materia.service.ai.groq.GroqRequestBuilder;
import ru.materia.service.ai.http.AiHttpClient;
import ru.materia.service.json.ProductJsonConverter;


public class Main {

    public static void main(String[] args) {


        ProductJsonConverter converter = new ProductJsonConverter();


        String groqApiKey = System.getenv("GROQ_API_KEY");

        if (groqApiKey == null || groqApiKey.isBlank()) {
            throw new IllegalStateException("API-ключ Groq не найден");
        }


        GroqProductAnalyzer analyzer = new GroqProductAnalyzer(
                new AiHttpClient(),
                converter,
                groqApiKey,
                new GroqRequestBuilder()
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