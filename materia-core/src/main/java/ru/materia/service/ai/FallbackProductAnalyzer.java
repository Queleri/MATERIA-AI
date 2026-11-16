package ru.materia.service.ai;

import ru.materia.model.specification.ProductSpecification;

import java.io.IOException;

public class FallbackProductAnalyzer implements  AiProductAnalyzer{
    @Override
    public ProductSpecification analyze(String description) throws IOException, InterruptedException {
        return null;
    }
}
