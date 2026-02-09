package com.example.yum_yum.presentation.filteredMeals;

import android.content.Context;

import com.example.yum_yum.data.meals.repository.MealsRepository;
import com.example.yum_yum.presentation.model.Meal;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FilteredMealsPresenter implements FilteredMealsContract.Presenter {

    private FilteredMealsContract.View view;
    private final MealsRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private List<Meal> cachedMeals = null;

    public FilteredMealsPresenter(Context context) {
        this.repository = new MealsRepository(context);
    }

    @Override
    public void attachView(FilteredMealsContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void loadMeals(String type, String value) {
        if (view == null) return;

        if (cachedMeals != null) {
            if (cachedMeals.isEmpty()) {
                view.showEmpty();
            } else {
                view.showMeals(cachedMeals);
            }
            return;
        }

        view.showLoading();

        Single<List<Meal>> source;
        switch (type) {
            case "area":
                source = repository.getMealsByArea(value);
                break;
            case "ingredient":
                source = repository.getMealsByIngredient(value);
                break;
            default:
                source = repository.getMealsByCategory(value);
                break;
        }

        disposables.add(
                source.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meals -> {
                                    cachedMeals = meals;
                                    if (view != null) {
                                        view.hideLoading();
                                        if (meals.isEmpty()) {
                                            view.showEmpty();
                                        } else {
                                            view.showMeals(meals);
                                        }
                                    }
                                },
                                error -> {
                                    if (view != null) {
                                        view.hideLoading();
                                        view.showError(error.getMessage());
                                    }
                                }
                        )
        );
    }

    @Override
    public void onMealClicked(Meal meal) {
        if (view == null) return;
        view.showLoading();

        disposables.add(
                repository.getMealById(meal.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                fullMeal -> {
                                    if (view != null) {
                                        view.hideLoading();
                                        view.navigateToMealDetails(fullMeal);
                                    }
                                },
                                error -> {
                                    if (view != null) {
                                        view.hideLoading();
                                        view.showError(error.getMessage());
                                    }
                                }
                        )
        );
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        view = null;
    }
}
