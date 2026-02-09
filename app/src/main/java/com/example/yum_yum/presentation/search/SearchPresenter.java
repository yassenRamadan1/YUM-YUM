package com.example.yum_yum.presentation.search;

import android.content.Context;
import android.util.Log;

import com.example.yum_yum.data.meals.repository.MealsRepository;
import com.example.yum_yum.presentation.model.Category;
import com.example.yum_yum.presentation.model.Meal;
import com.example.yum_yum.presentation.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;


public class SearchPresenter implements SearchContract.Presenter {
    private static final String TAG = "SearchPresenter";

    private final SearchContract.View view;
    private final MealsRepository repository;
    private final NetworkUtil networkUtil;
    private final CompositeDisposable disposables;

    private List<Meal> allMeals = new ArrayList<>();
    private List<Meal> currentDisplayedMeals = new ArrayList<>();
    private List<Category> allCategories = new ArrayList<>();
    private List<String> allAreas = new ArrayList<>();
    private List<String> allIngredients = new ArrayList<>();

    private String currentSearchQuery = "";
    private List<String> selectedCategories = new ArrayList<>();
    private List<String> selectedAreas = new ArrayList<>();
    private List<String> selectedIngredients = new ArrayList<>();

    public SearchPresenter(Context context, SearchContract.View view) {
        this.view = view;
        this.repository = new MealsRepository(context);
        this.networkUtil = new NetworkUtil(context);
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void onViewCreated() {
        checkInternetAndLoadData();
    }

    @Override
    public void checkInternetAndLoadData() {
        if (!networkUtil.isNetworkAvailable()) {
            view.showNoInternetError();
            return;
        }

        loadAllData();
    }


    private void loadAllData() {
        view.showLoading();

        disposables.add(
                Single.zip(
                                repository.getAllMeals(),
                                repository.getAllCategories(),
                                repository.getAllAreas(),
                                repository.getAllIngredients(),
                                (meals, categories, areas, ingredients) -> {
                                    SearchData data = new SearchData();
                                    data.meals = meals;
                                    data.categories = categories;
                                    data.areas = areas;
                                    data.ingredients = ingredients;
                                    return data;
                                }
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                this::onDataLoaded,
                                this::onDataLoadError
                        )
        );
    }

    private void onDataLoaded(SearchData data) {
        view.hideLoading();

        this.allMeals = data.meals;
        this.allCategories = data.categories;
        this.allAreas = data.areas;
        this.allIngredients = data.ingredients;
        this.currentDisplayedMeals = new ArrayList<>(allMeals);

        view.showAllMeals(allMeals);

        Log.d(TAG, "Data loaded successfully: " + allMeals.size() + " meals, "
                + allCategories.size() + " categories, "
                + allAreas.size() + " areas, "
                + allIngredients.size() + " ingredients");
    }

    private void onDataLoadError(Throwable error) {
        view.hideLoading();
        Log.e(TAG, "Error loading data", error);

        if (!networkUtil.isNetworkAvailable()) {
            view.showNoInternetError();
        } else {
            view.showError("Failed to load meals: " + error.getMessage());
        }
    }

    @Override
    public void onSearchQueryChanged(String query) {
        currentSearchQuery = query != null ? query.trim() : "";
        applySearchAndFilters();
    }

    @Override
    public void onFilterIconClicked() {
        if (allCategories.isEmpty() || allAreas.isEmpty() || allIngredients.isEmpty()) {
            view.showError("Filter options not loaded yet");
            return;
        }
        view.showFilterDialog(allCategories, allAreas, allIngredients);
    }

    @Override
    public void onFiltersApplied(
            List<String> selectedCategories,
            List<String> selectedAreas,
            List<String> selectedIngredients
    ) {
        this.selectedCategories = selectedCategories != null ? selectedCategories : new ArrayList<>();
        this.selectedAreas = selectedAreas != null ? selectedAreas : new ArrayList<>();
        this.selectedIngredients = selectedIngredients != null ? selectedIngredients : new ArrayList<>();

        Log.d(TAG, "Filters applied - Categories: " + this.selectedCategories.size()
                + ", Areas: " + this.selectedAreas.size()
                + ", Ingredients: " + this.selectedIngredients.size());

        applySearchAndFilters();
    }

    @Override
    public void onFiltersReset() {
        selectedCategories.clear();
        selectedAreas.clear();
        selectedIngredients.clear();
        currentSearchQuery = "";
        view.clearSearchQuery();
        currentDisplayedMeals = new ArrayList<>(allMeals);
        view.showAllMeals(allMeals);
        Log.d(TAG, "Filters reset");
    }
    private void applySearchAndFilters() {
        if (allMeals.isEmpty()) {
            return;
        }
        view.showLoading();
        disposables.add(
                repository.searchMeals(
                                currentSearchQuery.isEmpty() ? null : currentSearchQuery,
                                selectedCategories.isEmpty() ? null : selectedCategories,
                                selectedAreas.isEmpty() ? null : selectedAreas,
                                selectedIngredients.isEmpty() ? null : selectedIngredients
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                filteredMeals -> {
                                    view.hideLoading();
                                    currentDisplayedMeals = filteredMeals;

                                    if (filteredMeals.isEmpty()) {
                                        view.showEmptyResults();
                                    } else {
                                        view.showSearchResults(filteredMeals);
                                    }

                                    Log.d(TAG, "Search and filter applied. Results: " + filteredMeals.size());
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError("Search failed: " + error.getMessage());
                                    Log.e(TAG, "Search error", error);
                                }
                        )
        );
    }

    @Override
    public void onMealClicked(Meal meal) {
        if (meal != null) {
            view.navigateToMealDetails(meal);
        }
    }

    @Override
    public void onRetryClicked() {
        checkInternetAndLoadData();
    }

    @Override
    public void onDestroy() {
        disposables.clear();
    }

    private static class SearchData {
        List<Meal> meals;
        List<Category> categories;
        List<String> areas;
        List<String> ingredients;
    }
}