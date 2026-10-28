package ru.materia.service.json;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import ru.materia.model.specification.ProductSpecification;

public class ProductJsonConverter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public String toJson(ProductSpecification specification){
        if (specification == null)
            throw new IllegalArgumentException("specification не может быть null");
        String jsonResult;
        try {
            jsonResult = objectMapper.writeValueAsString(specification);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка преобразования JSON",e);
        }
        return jsonResult;
    }
    public ProductSpecification fromJson(String json){
        if (json == null || json.isBlank())
            throw new IllegalArgumentException("Входная строка json не может быть null или пустой");
        ProductSpecification productSpecification;
        try {
            productSpecification  = objectMapper.readValue(json, ProductSpecification.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Ошибка преобразования JSON",e);
        }
        if (productSpecification == null)
            throw new IllegalArgumentException("Ошибка преобразования JSON");
        return productSpecification;
    }
}
