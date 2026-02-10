package com.example.yum_yum.presentation.search.searchFilter;

import com.example.yum_yum.presentation.model.Category;

import java.util.List;

public interface FilterContract {

    interface View {
        void renderCategories(List<Category> categories);
        void renderAreas(List<String> areas);
        void renderIngredients(List<String> ingredients);

        void applyFiltersAndDismiss(List<String> selectedCategories, List<String> selectedAreas, List<String> selectedIngredients);
        void resetFiltersAndDismiss();

        void clearAllChipSelections();
    }

    interface Presenter {
        void onViewCreated(List<Category> categories, List<String> areas, List<String> ingredients);

        void onCategorySelected(String category, boolean isSelected);
        void onAreaSelected(String area, boolean isSelected);
        void onIngredientSelected(String ingredient, boolean isSelected);

        void onApplyClicked();
        void onResetClicked();

        void onDestroy();
    }
}
