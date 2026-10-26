package ru.materia.service;

import ru.materia.model.FurniturePart;
import ru.materia.model.Material;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class MateriaSelector {
    private final MaterialCostCalculator costCalculator =
            new MaterialCostCalculator();



    public List<Material> findSuitableMaterials(
            List<Material> materials,
            List<FurniturePart> parts,
            double maxBudget,
            double minStrength
    ){

        if (materials == null){
            throw new IllegalArgumentException("Материлы не могут быть null");
        }
        if (parts == null){
            throw new IllegalArgumentException("Детали изделия не могут быть null");
        }
        if (maxBudget < 0 || Double.isNaN(maxBudget) || Double.isInfinite(maxBudget)) {
            throw new IllegalArgumentException("Максимальный бюджет не может быть меньше 0 или равен 0"
                    + " число не может быть NaN и не может уходить в бесеонечность");
        }
        if (minStrength < 0 || Double.isNaN(minStrength) || Double.isInfinite(minStrength)){
            throw new IllegalArgumentException("Прочность изделия не может быть меньше или равна 0"
            + " число не может быть NaN и не может уходить в бесеонечность");
        }

            List<Material> suitableMaterials = new ArrayList<>();
            for (Material material : materials) {
                double totalCost = costCalculator.calculateTotalCost(parts, material);
                if (material == null) {
                    throw new IllegalArgumentException("Список материалов не может содержвть null");
                }
                    if (maxBudget >= totalCost && minStrength <= material.getStrength() ){
                        suitableMaterials.add(material);
                }
            }
            return suitableMaterials;
    }

    public Material findCheapestMaterial(
            List<Material> materials,
            List<FurniturePart> parts,
            double maxBudget,
            double minStrength
    ) {
        // Получаем список материалов, подходящих под требования
        List<Material> suitableMaterials =
                findSuitableMaterials(materials, parts, maxBudget, minStrength);

        // Если подходящих материалов нет
        if (suitableMaterials.isEmpty()) {
            return null;
        }

        // Изначально считаем первый материал самым дешёвым
        Material cheapestMaterial = suitableMaterials.get(0);

        // Рассчитываем его стоимость
        double cheapestCost =
                costCalculator.calculateTotalCost(parts, cheapestMaterial);

        // Перебираем подходящие материалы
        for (Material material : suitableMaterials) {

            double currentCost =
                    costCalculator.calculateTotalCost(parts, material);

            // Если нашли материал дешевле, обновляем результат
            if (currentCost < cheapestCost) {
                cheapestMaterial = material;
                cheapestCost = currentCost;
            }
        }

        return cheapestMaterial;
    }

}
