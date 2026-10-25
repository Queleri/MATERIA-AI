package ru.materia;

import ru.materia.model.Wardrobe;
import ru.materia.model.FurniturePart;
import ru.materia.service.WardrobeCalculator;

import java.util.List;

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
    }
}