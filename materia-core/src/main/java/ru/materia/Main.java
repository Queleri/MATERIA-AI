package ru.materia;

import ru.materia.model.Material;
import ru.materia.model.Wardrobe;
import ru.materia.model.FurniturePart;
import ru.materia.service.MateriaSelector;
import ru.materia.service.MaterialCostCalculator;
import ru.materia.service.WardrobeCalculator;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        // 1. Создаём шкаф
        Wardrobe wardrobe = new Wardrobe(
                2400, // Высота
                1600, // Ширина
                600,  // Глубина
                4,    // Количество полок
                2     // Количество дверей
        );

        // 2. Создаём калькулятор
        WardrobeCalculator calculator = new WardrobeCalculator();

        // 3. Получаем список деталей шкафа
        List<FurniturePart> parts = calculator.calculate(wardrobe);

        // 4. Выводим информацию о каждой детали
        System.out.println("=== СПЕЦИФИКАЦИЯ ШКАФА ===");

        for (FurniturePart part : parts) {
            System.out.println("Название: " + part.getName());
            System.out.println("Размеры: "
                    + part.getLength() + " x "
                    + part.getWidth() + " x "
                    + part.getThickness() + " мм");
            System.out.println("Количество: " + part.getQuantity() + " шт.");
            System.out.println("----------------------------");
        }

        // 5. Количество позиций
        System.out.println("Количество позиций: " + parts.size());

        // 6. Общее количество физических деталей
        int totalQuantity = 0;

        for (FurniturePart part : parts) {
            totalQuantity += part.getQuantity();
        }

        System.out.println("Всего деталей: " + totalQuantity);

        // 7. Общая площадь всех деталей
        double totalArea = calculator.calculateTotalArea(wardrobe);

        System.out.printf("Общая площадь: %.6f м²%n", totalArea);

        System.out.println();

        Material ldsp = new Material(1,"ЛДСП",650,850,12);
        Material mdf = new Material(2,"МДФ",750,1200,15);
        Material fanera = new Material(3,"Фанера",680,1500,18);

        MaterialCostCalculator materialCostCalculator = new MaterialCostCalculator();
        System.out.println("=== СТОИМОСТЬ ИЗГОТОВЛЕНИЯ ИЗДЕЛИЙ ===");
        for (FurniturePart part : parts) {
            System.out.println(part.getName() + " : " +
                    materialCostCalculator.calculatePartCost(part, ldsp) + " руб." );
        }
        System.out.println("=== ИТОГОВАЯ СТОИМОСТЬ ИЗГОТОВЛЕНИЯ ШКАФА ===");
        System.out.println(materialCostCalculator.calculateTotalCost(parts,ldsp) + " руб.");

        List<Material> materials = new ArrayList<>();
        materials.add(ldsp);
        materials.add(mdf);
        materials.add(fanera);
        MateriaSelector materiaSelector = new MateriaSelector();
        Scanner scanner = new Scanner(System.in);
        System.out.println("Добро пожаловать в систему подбора материала по вашим параметрам! ");
        System.out.print("Укажити ваш максимальный бюджет: ");
        double maxBudget = scanner.nextDouble();
        System.out.print("Укажите желаемую прочность материала: ");
        double minStrength = scanner.nextDouble();
        Material cheapestMaterial = materiaSelector.findCheapestMaterial(materials,parts,maxBudget,minStrength);
        if (cheapestMaterial != null) {
            System.out.println("Самый выгодный вариант: "
                    + cheapestMaterial.getName()
                    + ", цена за квадратный метр: "
                    + cheapestMaterial.getPricePerSquareMeter()
                    + " руб., прочность: "
                    + cheapestMaterial.getStrength());
        } else {
            System.out.println("К сожалению, материалов по вашим требованиям не найдено.");
        }
    }
}