package com.example.yum_yum.presentation.mealDetails;

import com.example.yum_yum.presentation.model.IngredientItem;
import com.example.yum_yum.presentation.model.Meal;

import java.util.List;

public interface MealDetailsContract {    interface View {
    void showMealInfo(Meal meal);
    void showIngredientsList(List<IngredientItem> ingredients);
    void setupVideo(String videoUrl);
    void showLoginRequired(String feature);
    void updateFavoriteIcon(boolean isFavorite);
    void showCalendarPicker(Meal meal);
    void showMessage(String message);
    void showError(String error);
}

    interface Presenter {
        void loadMealDetails(Meal meal);
        void onFavoriteClicked(Meal meal);
        void onCalendarClicked(Meal meal);
        void onDateSelected(Meal meal, String date);
        void checkFavoriteStatus(String mealId);
        void onDestroy();
    }
}
