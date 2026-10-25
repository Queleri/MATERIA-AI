package ru.materia.service;

import ru.materia.model.FurniturePart;
import ru.materia.model.Wardrobe;

import java.util.ArrayList;
import java.util.List;

public class WardrobeCalculator {
    private static final int BODY_THICKNESS = 16;
    private static final int DOOR_THICKNESS = 16;
    private static final int DOOR_SIDE_GAP = 2;
    private static final int DOOR_UPPER_GAP = 2;
    private static final int DOOR_BOTTOM_GAP = 2;
    private static final int DOOR_TO_DOOR_GAP = 2;


    public List<FurniturePart> calculate(Wardrobe wardrobe) {
        if (wardrobe == null)
            throw new IllegalArgumentException("Шкаф не может быть null");
        List<FurniturePart> parts = new ArrayList<>();
        parts.add(calculateDoor(wardrobe));
        parts.add(calculateHorizontalPanel(wardrobe, "Верхняя панель"));
        parts.add(calculateHorizontalPanel(wardrobe, "Нижняя панель"));
        parts.add(calculateSidePanel(wardrobe));
        if (wardrobe.getShelfCount()>0)
            parts.add(calculateShelf(wardrobe));
        return parts;
    }

    private FurniturePart calculateSidePanel(Wardrobe wardrobe){

            int length = wardrobe.getHeight();
            int width = wardrobe.getDepth();

            FurniturePart sidePanel = new FurniturePart(
                    "Боковая стенка",
                    length,
                    width,
                    BODY_THICKNESS,
                    2
            );

            return sidePanel;

    }

    private FurniturePart calculateHorizontalPanel(Wardrobe wardrobe, String name){
       int length = wardrobe.getWidth() - 2 * BODY_THICKNESS;
        int  width = wardrobe.getDepth();
        FurniturePart horizontalPanel = new FurniturePart(name,
                length, width, BODY_THICKNESS, 1);
        return horizontalPanel;
    }

    private FurniturePart calculateShelf(Wardrobe wardrobe){
       int length = wardrobe.getWidth() - 2 * BODY_THICKNESS;
        int width = wardrobe.getDepth();
        FurniturePart shelf = new FurniturePart("Полка",
                length, width, BODY_THICKNESS, wardrobe.getShelfCount());
        return shelf;
    }

    private FurniturePart calculateDoor(Wardrobe wardrobe){
      int  length = wardrobe.getHeight() - DOOR_UPPER_GAP - DOOR_BOTTOM_GAP;
      int width = (
              wardrobe.getWidth()
                      - 2 * DOOR_SIDE_GAP
                      - (wardrobe.getDoorCount() - 1) * DOOR_TO_DOOR_GAP
      ) / wardrobe.getDoorCount();
      FurniturePart door = new FurniturePart("Дверь",
              length, width,DOOR_THICKNESS,wardrobe.getDoorCount());
      return door;
    }

    public double calculateTotalArea(Wardrobe wardrobe){
        List<FurniturePart> parts = calculate(wardrobe);
        double totalArea = 0;
        for (FurniturePart part : parts) {
           totalArea += part.calculateTotalArea();
        }
        return totalArea;
    }

}
