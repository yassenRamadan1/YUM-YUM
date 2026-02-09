package com.example.yum_yum.presentation.filteredMeals;

import com.example.yum_yum.presentation.model.Meal;

import java.util.List;

public interface FilteredMealsContract {

    interface View {
        void showMeals(List<Meal> meals);
        void showLoading();
        void hideLoading();
        void showError(String message);
        void showEmpty();
        void navigateToMealDetails(Meal meal);
    }

    interface Presenter {
        void attachView(View view);
        void detachView();
        void loadMeals(String type, String value);
        void onMealClicked(Meal meal);
        void onDestroy();
    }
}
