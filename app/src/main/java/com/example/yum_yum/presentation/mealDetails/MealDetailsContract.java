package com.example.yum_yum.presentation.mealDetails;

import com.example.yum_yum.presentation.model.IngredientItem;
import com.example.yum_yum.presentation.model.Meal;

import java.util.List;

public interface MealDetailsContract {
    interface View {
        void showMealInfo(Meal meal);
        void showIngredientsList(List<IngredientItem> ingredients);
        void setupVideo(String videoUrl);
    }

    interface Presenter {
        void loadMealDetails(Meal meal);
    }
}
