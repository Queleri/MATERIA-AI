package ru.materia.service.ai;

import ru.materia.model.specification.ProductSpecification;

public interface AiProductAnalyzer {
    ProductSpecification analyze(String description);

}
