package ru.materia.model;

public class Material {
    private final long id;
    private final String name;
    private final double density;               //double	Плотность, кг/м³
    private final double pricePerSquareMeter;   //Цена за 1 м² в рублях
    private final double strength;              //Условный показатель прочности

    public Material(long id, String name, double density, double pricePerSquareMeter, double strength) {
        if (id < 0)
            throw new IllegalArgumentException("ID не может быть меньше 0");
        if (name == null || name.isBlank())
            throw new IllegalArgumentException("Имя не может быть null или пустым");
        if(density<=0 || Double.isNaN(density) || Double.isInfinite(density))
            throw new IllegalArgumentException("Плотность не может быть меньше 0, также " +
                    "число не может быть NaN и не может уходить в бесеонечность");
        if(pricePerSquareMeter<0 || Double.isNaN(pricePerSquareMeter) || Double.isInfinite(pricePerSquareMeter))
            throw new IllegalArgumentException("Цена должна быть не меньше 0, " +
                    "число не может быть NaN и не может уходить в бесеонечность");
        if(strength<=0 || Double.isNaN(strength) || Double.isInfinite(strength))
            throw new IllegalArgumentException("Прочность должна быть больше 0, "+
                    "число не может быть NaN и не может уходить в бесеонечность");



        this.id = id;
        this.name = name;
        this.density = density;
        this.pricePerSquareMeter = pricePerSquareMeter;
        this.strength = strength;
    }

    public long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public double getDensity() {
        return density;
    }

    public double getPricePerSquareMeter() {
        return pricePerSquareMeter;
    }

    public double getStrength() {
        return strength;
    }

    @Override
    public String toString() {
        return "Материал{" +
                "ID=" + id +
                ", Название = " + name + '\'' +
                ", Плотность  = " + density +
                ", Цена за 1 м² = " + pricePerSquareMeter +
                ", Прочность = " + strength +
                '}';
    }
}