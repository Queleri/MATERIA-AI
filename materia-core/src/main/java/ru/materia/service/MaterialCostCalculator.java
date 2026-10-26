package ru.materia.service;

import ru.materia.model.FurniturePart;
import ru.materia.model.Material;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public class MaterialCostCalculator {

    public double calculatePartCost(FurniturePart part, Material material){
        if (part == null)
            throw new IllegalArgumentException("Часть изделия не может быть равна null");
        if (material == null)
            throw new IllegalArgumentException("Материал не может быть null");
        double totalArea = part.calculateTotalArea();
        double partCost = BigDecimal.valueOf(totalArea * material.getPricePerSquareMeter()).
                setScale(2, RoundingMode.HALF_UP).doubleValue();
        return  partCost;
    }
    public double calculateTotalCost(List<FurniturePart> parts, Material material){
        if (parts == null)
            throw new IllegalArgumentException("Часть изделия не может быть равна null");
        if (material == null)
            throw new IllegalArgumentException("Материал не может быть null");
        double totalCost = 0;
        for (FurniturePart part : parts){
            totalCost += calculatePartCost(part,material);
        }
        return totalCost;
    }

}
