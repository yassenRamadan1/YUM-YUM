package com.example.yum_yum.presentation.weaklyMeals;

import com.example.yum_yum.presentation.model.Meal;

import java.util.List;
import java.util.Map;

public interface WeeklyMealsContract {
    interface View {
        void showLoginRequired();
        void showPlannedMeals(Map<String, List<Meal>> mealsByDate);
        void showEmptyState();
        void showLoading();
        void hideLoading();
        void showMessage(String message);
        void showError(String error);
        void removeMealFromList(String date, String mealId);
    }

    interface Presenter {
        void loadWeeklyMeals();
        void onRemoveMealClicked(Meal meal, String date);
        void confirmRemoveMeal(Meal meal, String date);
        void onDestroy();
    }
}
