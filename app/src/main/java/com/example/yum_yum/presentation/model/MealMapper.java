package com.example.yum_yum.presentation.model;

import com.example.yum_yum.data.meals.dto.CategoryDto;
import com.example.yum_yum.data.meals.dto.CategoryResponse;
import com.example.yum_yum.data.meals.dto.MealDto;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

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
    public Category mapToModel(CategoryDto dto) {
        if (dto == null) {
            return null;
        }

        return new Category(
                dto.getIdCategory(),
                dto.getStrCategory(),
                dto.getStrCategoryThumb(),
                dto.getStrCategoryDescription()
        );
    }

    public List<Category> mapToModelList(CategoryResponse responseDto) {
        if (responseDto == null || responseDto.getCategories() == null) {
            return Collections.emptyList();
        }
        return responseDto.getCategories().stream()
                .map(this::mapToModel)
                .collect(Collectors.toList());
    }
}
