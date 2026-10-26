package ru.materia.model;

public class Wardrobe {
    private final int height;
    private final int width;
    private final int depth;
    private final int shelfCount;
    private final int doorCount;

    public Wardrobe(int height, int width, int depth, int shelfCount, int doorCount) {
        if (height < 500 || height>3000) {
            throw new IllegalArgumentException("Введены некоректные размеры высоты");
        }
        if (width < 400 || width > 2400){
            throw new IllegalArgumentException("Введены некоректные размеры ширины");
        }
        if (depth < 200 || depth > 1000){
            throw new IllegalArgumentException("Введены некоректные размеры глубины");
        }
        if (shelfCount < 0 || shelfCount > 20){
            throw new IllegalArgumentException("Введено некоректное колличество полок");
        }
        if (doorCount < 1 || doorCount > 6){
            throw new IllegalArgumentException("Введено неккореткное количество дверей");
        }
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.shelfCount = shelfCount;
        this.doorCount = doorCount;
    }

    public int getHeight() {
        return height;
    }

    public int getDepth() {
        return depth;
    }

    public int getWidth() {
        return width;
    }

    public int getDoorCount() {
        return doorCount;
    }

    public int getShelfCount() {
        return shelfCount;
    }

    @Override
    public String toString() {
        return "Шкаф{" +
                "Высота=" + height +
                ", Ширина=" + width +
                ", Глубина=" + depth +
                ", Количество полок=" + shelfCount +
                ", Количество дверей=" + doorCount +
                '}';
    }
}
