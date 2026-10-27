package ru.materia.model.specification;

public class ProductDimensions {
    private final int width;
    private final int height;
    private final int depth;

    public ProductDimensions(int width, int height, int depth) {
        if (width <= 0)
            throw new IllegalArgumentException("Ширина не может быть меньше 0");
        if (height <= 0)
            throw new IllegalArgumentException("Высота не может быть меньше 0");
        if (depth <= 0)
            throw new IllegalArgumentException("Глубина не может быть меньше 0");
        this.width = width;
        this.height = height;
        this.depth = depth;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    @Override
    public String toString() {
        return "Размеры изделия{" +
                "Ширина =" + width +
                ", Высота =" + height +
                ", Глубина =" + depth +
                '}';
    }
}
