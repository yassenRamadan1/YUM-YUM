package com.example.yum_yum.presentation.mealDetails;

import com.example.yum_yum.presentation.model.IngredientItem;
import com.example.yum_yum.presentation.model.Meal;

import java.util.List;

public class MealDetailsPresenter implements MealDetailsContract.Presenter {

    private final MealDetailsContract.View view;

    public MealDetailsPresenter(MealDetailsContract.View view) {
        this.view = view;
    }

    @Override
    public void loadMealDetails(Meal meal) {
        if (meal == null) return;
        view.showMealInfo(meal);

        List<IngredientItem> items = meal.getIngredients();
        if (items != null && !items.isEmpty()) {
            view.showIngredientsList(items);
        }

        if (meal.getYoutubeUrl() != null && !meal.getYoutubeUrl().isEmpty()) {
            view.setupVideo(meal.getYoutubeUrl());
        }
    }
}