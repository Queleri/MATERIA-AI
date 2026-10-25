package ru.materia.model;

public class FurniturePart {
    private final String name;
    private final int length;
    private final int width;
    private final int thickness;
    private final int quantity;

    public FurniturePart(String name, int length, int width, int thickness, int quantity) {
        if ((name == null || name.isBlank())) {
            throw new IllegalArgumentException("Название объекта указано некорректно");
        }
        if (length < 1 || length > 3000){
            throw new IllegalArgumentException("Длина должна быть от 1мм до 3000мм");
        }
        if (width < 1 || width > 3000){
            throw new IllegalArgumentException("Ширина должна быть от 1мм до 3000мм");
        }
        if (thickness < 1 || thickness > 100){
            throw new IllegalArgumentException("Толщина должна быть от 1мм до 100мм ");
        }
        if (quantity < 1 || quantity  > 100){
            throw new IllegalArgumentException("Количество должно быть от 1 до 100");
        }
        this.name = name;
        this.length = length;
        this.width = width;
        this.thickness = thickness;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public int getLength() {
        return length;
    }

    public int getWidth() {
        return width;
    }

    public int getThickness() {
        return thickness;
    }

    public int getQuantity() {
        return quantity;
    }

    public double calculateArea(){
        double area = (length * width)/1_000_000.0;
        return area;
    }
    public double calculateTotalArea(){
         double totalArea = calculateArea() * quantity;
         return totalArea;
    }
}
