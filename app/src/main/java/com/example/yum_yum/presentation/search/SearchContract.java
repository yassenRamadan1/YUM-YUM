package com.example.yum_yum.presentation.search;

import android.content.Context;

import com.example.yum_yum.presentation.model.Category;
import com.example.yum_yum.presentation.model.Meal;

import java.util.List;

public interface SearchContract {

    interface View {
        void showLoading();
        void hideLoading();
        void showNoInternetError();
        void hideNoInternetError();
        void showInitialState();
        void showSearchResults(List<Meal> meals);
        void showEmptyResults();
        void showFilterDialog(List<Category> categories, List<String> areas, List<String> ingredients);
        void navigateToMealDetails(Meal meal);
        void showError(String message);
        void clearSearchQuery();
    }

    interface Presenter {
        void onViewCreated();
        void onDestroy();
        void startNetworkMonitoring(Context context);
        void stopNetworkMonitoring();
        void onSearchQueryChanged(String query);
        void onFilterIconClicked();
        void onFiltersApplied(List<String> selectedCategories, List<String> selectedAreas, List<String> selectedIngredients);
        void onFiltersReset();
        void onMealClicked(Meal meal);
    }
}