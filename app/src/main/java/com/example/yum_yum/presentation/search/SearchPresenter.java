package com.example.yum_yum.presentation.search;

import android.content.Context;
import android.util.Log;

import com.example.yum_yum.data.meals.repository.MealsRepository;
import com.example.yum_yum.presentation.model.Category;
import com.example.yum_yum.presentation.model.IngredientItem;
import com.example.yum_yum.presentation.model.Meal;
import com.example.yum_yum.presentation.utils.NetworkUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SearchPresenter implements SearchContract.Presenter {
    private static final String TAG = "SearchPresenter";

    private final SearchContract.View view;
    private final MealsRepository repository;
    private final CompositeDisposable disposables;

    private List<Category> allCategories = new ArrayList<>();
    private List<String> allAreas = new ArrayList<>();
    private List<String> allIngredients = new ArrayList<>();
    private List<Meal> currentSearchResults = new ArrayList<>();
    private List<String> selectedCategories = new ArrayList<>();
    private List<String> selectedAreas = new ArrayList<>();
    private List<String> selectedIngredients = new ArrayList<>();

    private Disposable networkDisposable;
    private boolean wasDisconnected = false;
    private boolean isConnected = true;
    private String lastSearchQuery = "";

    public SearchPresenter(Context context, SearchContract.View view) {
        this.view = view;
        this.repository = new MealsRepository(context);
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void onViewCreated() {
        loadFilterOptions();
    }

    @Override
    public void startNetworkMonitoring(Context context) {
        stopNetworkMonitoring();
        wasDisconnected = false;
        networkDisposable = NetworkUtil.getNetworkStatusObservable(context)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        connected -> {
                            isConnected = connected;
                            if (connected) {
                                view.hideNoInternetError();
                                if (wasDisconnected) {
                                    wasDisconnected = false;
                                    loadFilterOptions();
                                    if (lastSearchQuery.isEmpty()) {
                                        view.showInitialState();
                                    } else {
                                        searchMealsByName(lastSearchQuery);
                                    }
                                }
                            } else {
                                wasDisconnected = true;
                                view.showNoInternetError();
                            }
                        },
                        throwable -> { }
                );
    }

    @Override
    public void stopNetworkMonitoring() {
        if (networkDisposable != null && !networkDisposable.isDisposed()) {
            networkDisposable.dispose();
            networkDisposable = null;
        }
    }

    private void loadFilterOptions() {
        disposables.add(
                Single.zip(
                                repository.getAllCategories(),
                                repository.getAllAreas(),
                                repository.getAllIngredients(),
                                (categories, areas, ingredients) -> {
                                    FilterOptionsData data = new FilterOptionsData();
                                    data.categories = categories;
                                    data.areas = areas;
                                    data.ingredients = ingredients;
                                    return data;
                                }
                        )
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                data -> {
                                    this.allCategories = data.categories;
                                    this.allAreas = data.areas;
                                    this.allIngredients = data.ingredients;
                                    Log.d(TAG, "Filter options loaded: " + allCategories.size()
                                            + " categories, " + allAreas.size() + " areas, "
                                            + allIngredients.size() + " ingredients");
                                },
                                error -> {
                                    Log.e(TAG, "Error loading filter options", error);
                                }
                        )
        );
    }

    @Override
    public void onSearchQueryChanged(String query) {
        if (query == null || query.trim().isEmpty()) {
            lastSearchQuery = "";
            currentSearchResults.clear();
            view.showInitialState();
            return;
        }

        lastSearchQuery = query.trim();

        if (!isConnected) {
            return;
        }

        searchMealsByName(lastSearchQuery);
    }

    private void searchMealsByName(String query) {
        view.showLoading();

        disposables.add(
                repository.searchMealsByName(query)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meals -> {
                                    view.hideLoading();
                                    currentSearchResults = meals;
                                    applyFiltersToResults();
                                },
                                error -> {
                                    view.hideLoading();
                                    Log.e(TAG, "Search error", error);
                                    view.showError("Search failed: " + error.getMessage());
                                    view.showEmptyResults();
                                }
                        )
        );
    }

    @Override
    public void onFilterIconClicked() {
        if (allCategories.isEmpty() || allAreas.isEmpty() || allIngredients.isEmpty()) {
            view.showError("Filter options are loading, please try again");
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

        applyFiltersToResults();
    }

    @Override
    public void onFiltersReset() {
        selectedCategories.clear();
        selectedAreas.clear();
        selectedIngredients.clear();
        applyFiltersToResults();

        Log.d(TAG, "Filters reset");
    }

    private void applyFiltersToResults() {
        if (currentSearchResults.isEmpty()) {
            view.showEmptyResults();
            return;
        }

        disposables.add(
                Observable.fromIterable(currentSearchResults)
                        .filter(meal -> {
                            if (!selectedCategories.isEmpty()) {
                                if (!selectedCategories.contains(meal.getCategory())) {
                                    return false;
                                }
                            }
                            if (!selectedAreas.isEmpty()) {
                                if (!selectedAreas.contains(meal.getArea())) {
                                    return false;
                                }
                            }
                            if (!selectedIngredients.isEmpty()) {
                                List<String> mealIngredientNames = meal.getIngredients().stream()
                                        .map(IngredientItem::getName)
                                        .map(String::toLowerCase)
                                        .collect(Collectors.toList());

                                for (String selectedIngredient : selectedIngredients) {
                                    boolean found = false;
                                    for (String mealIngredient : mealIngredientNames) {
                                        if (mealIngredient.contains(selectedIngredient.toLowerCase())) {
                                            found = true;
                                            break;
                                        }
                                    }
                                    if (!found) {
                                        return false;
                                    }
                                }
                            }

                            return true;
                        })
                        .toList()
                        .subscribeOn(Schedulers.computation())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                filteredMeals -> {
                                    if (filteredMeals.isEmpty()) {
                                        view.showEmptyResults();
                                    } else {
                                        view.showSearchResults(filteredMeals);
                                    }
                                    Log.d(TAG, "Filtered results: " + filteredMeals.size() + " meals");
                                },
                                error -> {
                                    Log.e(TAG, "Filter error", error);
                                    view.showError("Filter failed: " + error.getMessage());
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
    public void onDestroy() {
        stopNetworkMonitoring();
        disposables.clear();
    }

    private static class FilterOptionsData {
        List<Category> categories;
        List<String> areas;
        List<String> ingredients;
    }
}
