package ru.materia.model.specification;

public class ProductComponent {
    private final String name;
    private final String type;
    private final int quantity;

    public ProductComponent(String name, String type, int quantity) {
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Название не может быть null или пустым");
        if (type == null || type.isBlank())
            throw new IllegalArgumentException("Тип не может быть null или пустым");
        if (quantity <= 0)
            throw new IllegalArgumentException("Количество не может быть меньше 0");
        this.name = name;
        this.type = type;
        this.quantity = quantity;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public int getQuantity() {
        return quantity;
    }

    @Override
    public String toString() {
        return "Компонент изделия{" +
                "Название ='" + name + '\'' +
                ", Тип ='" + type + '\'' +
                ", Количество =" + quantity +
                '}';
    }
}
