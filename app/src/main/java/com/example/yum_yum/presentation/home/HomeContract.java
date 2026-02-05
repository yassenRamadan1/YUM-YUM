package com.example.yum_yum.presentation.home;

import com.example.yum_yum.presentation.model.Meal;

import java.util.List;

public interface HomeContract {
    interface View {
        void showLoading();
        void hideLoading();
        void showDailyMeal(Meal meal);
        void showDailyCountryMeals(List<Meal> meals, String countryName);
        void showError(String message);
    }

    interface Presenter {
        void getHomeContent();
        void onDestroy();
    }
}