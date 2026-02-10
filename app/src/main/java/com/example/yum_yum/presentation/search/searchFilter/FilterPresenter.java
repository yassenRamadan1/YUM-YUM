package com.example.yum_yum.presentation.search.searchFilter;

import com.example.yum_yum.presentation.model.Category;
import java.util.ArrayList;
import java.util.List;

public class FilterPresenter implements FilterContract.Presenter {

    private FilterContract.View view;

    private final List<String> selectedCategories = new ArrayList<>();
    private final List<String> selectedAreas = new ArrayList<>();
    private final List<String> selectedIngredients = new ArrayList<>();

    public FilterPresenter(FilterContract.View view) {
        this.view = view;
    }

    @Override
    public void onViewCreated(List<Category> categories, List<String> areas, List<String> ingredients) {
        if (view == null) return;

        if (categories != null) {
            view.renderCategories(categories);
        }
        if (areas != null) {
            view.renderAreas(areas);
        }
        if (ingredients != null) {
            int maxIngredients = Math.min(ingredients.size(), 30);
            view.renderIngredients(ingredients.subList(0, maxIngredients));
        }
    }

    @Override
    public void onCategorySelected(String category, boolean isSelected) {
        if (isSelected) {
            selectedCategories.add(category);
        } else {
            selectedCategories.remove(category);
        }
    }

    @Override
    public void onAreaSelected(String area, boolean isSelected) {
        if (isSelected) {
            selectedAreas.add(area);
        } else {
            selectedAreas.remove(area);
        }
    }

    @Override
    public void onIngredientSelected(String ingredient, boolean isSelected) {
        if (isSelected) {
            selectedIngredients.add(ingredient);
        } else {
            selectedIngredients.remove(ingredient);
        }
    }

    @Override
    public void onApplyClicked() {
        if (view != null) {
            view.applyFiltersAndDismiss(
                    new ArrayList<>(selectedCategories),
                    new ArrayList<>(selectedAreas),
                    new ArrayList<>(selectedIngredients)
            );
        }
    }

    @Override
    public void onResetClicked() {
        selectedCategories.clear();
        selectedAreas.clear();
        selectedIngredients.clear();

        if (view != null) {
            view.clearAllChipSelections();
            view.resetFiltersAndDismiss();
        }
    }

    @Override
    public void onDestroy() {
        view = null;
    }
}