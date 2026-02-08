package com.example.yum_yum.presentation.Favorite;

import com.example.yum_yum.presentation.model.Meal;

import java.util.List;

public interface FavoriteContract {
    interface View {
        void showLoginRequired();
        void showFavoriteMeals(List<Meal> meals);
        void showEmptyState();
        void showLoading();
        void hideLoading();
        void showMessage(String message);
        void showError(String error);
        void removeMealFromList(String mealId);
    }

    interface Presenter {
        void loadFavoriteMeals();
        void onRemoveFavoriteClicked(Meal meal);
        void confirmRemoveFavorite(Meal meal);
        void onMealClicked(Meal meal);
        void onDestroy();
    }
}