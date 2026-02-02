package com.example.yum_yum.presentation.model;

import com.example.yum_yum.data.model.MealDto;

import java.util.ArrayList;
import java.util.List;

public class MealMapper {
    public static Meal mapToUiModel(MealDto dto) {
        List<IngredientItem> ingredientsList = new ArrayList<>();

        for (int i = 1; i <= 20; i++) {
            String ingredient = dto.getIngredient(i);
            String measure = dto.getMeasure(i);
            boolean hasIngredient = ingredient != null && !ingredient.trim().isEmpty();
            boolean hasMeasure = measure != null && !measure.trim().isEmpty();

            if (hasIngredient) {
                String finalMeasure = hasMeasure ? measure : "";
                ingredientsList.add(new IngredientItem(ingredient, finalMeasure));
            }
        }

        return new Meal(
                dto.getId(),
                dto.getName(),
                dto.getThumbnail(),
                dto.getInstructions(),
                dto.getCategory(),
                dto.getArea(),
                dto.getYoutubeUrl(),
                ingredientsList
        );
    }
}
