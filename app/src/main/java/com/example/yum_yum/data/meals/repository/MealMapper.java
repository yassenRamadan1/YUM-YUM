package com.example.yum_yum.data.meals.repository;

import com.example.yum_yum.data.meals.datasource.local.entity.MealEntity;
import com.example.yum_yum.data.meals.dto.CategoryDto;
import com.example.yum_yum.data.meals.dto.CategoryResponse;
import com.example.yum_yum.data.meals.dto.MealDto;
import com.example.yum_yum.presentation.model.Category;
import com.example.yum_yum.presentation.model.IngredientItem;
import com.example.yum_yum.presentation.model.Meal;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MealMapper {
    private static final Gson gson = new Gson();

    // ===== DTO TO UI MODEL =====
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

    // ===== ENTITY TO UI MODEL =====
    public static Meal convertEntityToMeal(MealEntity entity) {
        if (entity == null) return null;

        List<IngredientItem> ingredients = parseIngredients(
                entity.getIngredientsJson(),
                entity.getMeasuresJson()
        );

        return new Meal(
                entity.getId(),
                entity.getName(),
                entity.getImageUrl(),
                entity.getInstructions(),
                entity.getCategory(),
                entity.getArea(),
                entity.getYoutubeUrl(),
                ingredients,
                entity.getPlannedDate()
        );
    }

    // ===== UI MODEL TO ENTITY =====
    public static MealEntity convertToEntity(Meal meal) {
        MealEntity entity = new MealEntity();
        entity.setId(meal.getId());
        entity.setName(meal.getName());
        entity.setCategory(meal.getCategory());
        entity.setArea(meal.getArea());
        entity.setInstructions(meal.getInstructions());
        entity.setImageUrl(meal.getImageUrl());
        entity.setYoutubeUrl(meal.getYoutubeUrl());
        entity.setCachedAt(System.currentTimeMillis());

        // Convert ingredients to JSON
        List<String> ingredients = new ArrayList<>();
        List<String> measures = new ArrayList<>();
        if (meal.getIngredients() != null) {
            for (IngredientItem item : meal.getIngredients()) {
                ingredients.add(item.getName());
                measures.add(item.getMeasure());
            }
        }

        entity.setIngredientsJson(gson.toJson(ingredients));
        entity.setMeasuresJson(gson.toJson(measures));

        return entity;
    }

    // ===== CATEGORY MAPPING =====
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

    private static List<IngredientItem> parseIngredients(String ingredientsJson, String measuresJson) {
        List<IngredientItem> ingredientItems = new ArrayList<>();

        if (ingredientsJson == null || measuresJson == null) {
            return ingredientItems;
        }

        try {
            Type listType = new TypeToken<List<String>>(){}.getType();
            List<String> ingredients = gson.fromJson(ingredientsJson, listType);
            List<String> measures = gson.fromJson(measuresJson, listType);

            if (ingredients != null && measures != null) {
                int size = Math.min(ingredients.size(), measures.size());
                for (int i = 0; i < size; i++) {
                    String ingredient = ingredients.get(i);
                    String measure = measures.get(i);
                    if (ingredient != null && !ingredient.trim().isEmpty()) {
                        ingredientItems.add(new IngredientItem(
                                ingredient,
                                measure != null ? measure : ""
                        ));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ingredientItems;
    }
}
