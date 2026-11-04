package ru.materia;

import ru.materia.model.Material;
import ru.materia.model.Wardrobe;
import ru.materia.model.FurniturePart;
import ru.materia.model.specification.ProductComponent;
import ru.materia.model.specification.ProductDimensions;
import ru.materia.model.specification.ProductSpecification;
import ru.materia.service.MateriaSelector;
import ru.materia.service.MaterialCostCalculator;
import ru.materia.service.WardrobeCalculator;
import ru.materia.service.ai.AiProductAnalyzer;
import ru.materia.service.ai.GeminiProductAnalyzer;
import ru.materia.service.ai.MockProductAnalyzer;
import ru.materia.service.ai.gemini.GeminiRequestBuilder;
import ru.materia.service.ai.http.AiHttpClient;
import ru.materia.service.json.ProductJsonConverter;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {


        ProductJsonConverter converter = new ProductJsonConverter();



        GeminiProductAnalyzer analyzer = new GeminiProductAnalyzer(
                new AiHttpClient(),
                converter,
                System.getenv("GEMINI_API_KEY"),
                new GeminiRequestBuilder()
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