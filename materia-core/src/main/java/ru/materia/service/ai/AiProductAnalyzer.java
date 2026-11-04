package ru.materia.service.ai;

import ru.materia.model.specification.ProductSpecification;

import java.io.IOException;

public interface AiProductAnalyzer {
    ProductSpecification analyze(String description) throws IOException, InterruptedException;

}
