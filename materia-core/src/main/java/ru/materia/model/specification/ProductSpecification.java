package ru.materia.model.specification;

import java.util.List;

public class ProductSpecification {
    private final String productName;
    private final String productType;
    private final ProductDimensions dimensions;
    private final List<ProductComponent> components;

    public ProductSpecification(String productName, String productType, ProductDimensions dimensions, List<ProductComponent> components) {
        if (productName == null || productName.isBlank())
            throw new IllegalArgumentException("Название продукта не может быть null или пустым");
        if (productType == null || productType.isBlank())
            throw new IllegalArgumentException("Тип продукта не может быть null или пустым");
        if (dimensions == null )
            throw new IllegalArgumentException("Размеры не могут быть null");
        if (components == null || components.isEmpty() )
            throw new IllegalArgumentException("Список компонентов не может быть null или пустым");
        this.productName = productName;
        this.productType = productType;
        this.dimensions = dimensions;
        this.components = List.copyOf(components);
    }

    public String getProductName() {
        return productName;
    }

    public String getProductType() {
        return productType;
    }

    public ProductDimensions getDimensions() {
        return dimensions;
    }

    public List<ProductComponent> getComponents() {
        return components;
    }

    @Override
    public String toString() {
        return "Спецификация продукта{" +
                "Название продукта ='" + productName + '\'' +
                ", Тип продукта ='" + productType + '\'' +
                ", Размеры =" + dimensions +
                ", Компонеты =" + components +
                '}';
    }
}
