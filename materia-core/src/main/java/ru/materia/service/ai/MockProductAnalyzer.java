package ru.materia.service.ai;

import ru.materia.model.specification.ProductComponent;
import ru.materia.model.specification.ProductDimensions;
import ru.materia.model.specification.ProductSpecification;

import java.util.List;
import java.util.PriorityQueue;

public class MockProductAnalyzer implements AiProductAnalyzer{
    @Override
    public ProductSpecification analyze(String description) {
        if (description == null || description.isBlank())
            throw new IllegalArgumentException("Описание не может быть null или пустым");
        ProductDimensions dimensions =
                new ProductDimensions(1400, 750, 700);
        List<ProductComponent> components = List.of(
                new ProductComponent("Столешница", "PANEL", 1),
                new ProductComponent("Ножка", "SUPPORT", 4)
        );
        ProductSpecification computerTable = new ProductSpecification("Компьютерный стол",
                "TABLE",dimensions,components);
            return computerTable;
    }
}
