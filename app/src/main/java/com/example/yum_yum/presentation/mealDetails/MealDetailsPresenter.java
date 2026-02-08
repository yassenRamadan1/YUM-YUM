package com.example.yum_yum.presentation.mealDetails;

import android.content.Context;
import android.util.Log;

import com.example.yum_yum.data.auth.repository.AuthRepository;
import com.example.yum_yum.data.meals.repository.MealsRepository;
import com.example.yum_yum.presentation.model.IngredientItem;
import com.example.yum_yum.presentation.model.Meal;

import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealDetailsPresenter implements MealDetailsContract.Presenter {

    private static final String TAG = "MealDetailsPresenter";
    private final MealDetailsContract.View view;
    private final MealsRepository mealsRepository;
    private final AuthRepository authRepository;
    private final CompositeDisposable disposables;
    private String currentUserId;

    public MealDetailsPresenter(MealDetailsContract.View view, Context context) {
        this.view = view;
        this.mealsRepository = new MealsRepository(context);
        this.authRepository = new AuthRepository();
        this.disposables = new CompositeDisposable();
        getCurrentUser();
    }

    private void getCurrentUser() {
        disposables.add(
                authRepository.getCurrentUser()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                userId -> {
                                    this.currentUserId = userId;
                                    Log.d(TAG, "User ID: " + userId);
                                },
                                error -> {
                                    this.currentUserId = null;
                                    Log.d(TAG, "No user logged in");
                                }
                        )
        );
    }

    @Override
    public void loadMealDetails(Meal meal) {
        if (meal == null) return;

        view.showMealInfo(meal);
        if (meal.getIngredients() != null && !meal.getIngredients().isEmpty()) {
            view.showIngredientsList(meal.getIngredients());
        }
        if (meal.getYoutubeUrl() != null && !meal.getYoutubeUrl().isEmpty()) {
            view.setupVideo(meal.getYoutubeUrl());
        }
        if (currentUserId != null) {
            checkFavoriteStatus(meal.getId());
        }
    }

    @Override
    public void onFavoriteClicked(Meal meal) {
        if (currentUserId == null) {
            view.showLoginRequired("favorite");
            return;
        }
        disposables.add(
                mealsRepository.isFavorite(currentUserId, meal.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                isFavorite -> {
                                    if (isFavorite) {
                                        removeFromFavorites(meal.getId());
                                    } else {
                                        addToFavorites(meal);
                                    }
                                },
                                error -> {
                                    Log.e(TAG, "Error checking favorite status", error);
                                    addToFavorites(meal);
                                }
                        )
        );
    }

    private void addToFavorites(Meal meal) {
        disposables.add(
                mealsRepository.addToFavorites(meal, currentUserId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    view.updateFavoriteIcon(true);
                                    view.showMessage("Added to favorites");
                                },
                                error -> {
                                    Log.e(TAG, "Error adding to favorites", error);
                                    view.showError("Failed to add to favorites");
                                }
                        )
        );
    }

    private void removeFromFavorites(String mealId) {
        disposables.add(
                mealsRepository.removeFromFavorites(currentUserId, mealId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    view.updateFavoriteIcon(false);
                                    view.showMessage("Removed from favorites");
                                },
                                error -> {
                                    Log.e(TAG, "Error removing from favorites", error);
                                    view.showError("Failed to remove from favorites");
                                }
                        )
        );
    }

    @Override
    public void onCalendarClicked(Meal meal) {
        if (currentUserId == null) {
            view.showLoginRequired("weekly plan");
            return;
        }
        view.showCalendarPicker(meal);
    }

    @Override
    public void onDateSelected(Meal meal, String date) {
        if (currentUserId == null) {
            view.showLoginRequired("weekly plan");
            return;
        }
        disposables.add(
                mealsRepository.hasPlannedMealForDate(currentUserId, date)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                hasPlanned -> {
                                    if (hasPlanned) {
                                        view.showError("You already have a meal planned for this date");
                                    } else {
                                        addToWeeklyPlan(meal, date);
                                    }
                                },
                                error -> {
                                    Log.e(TAG, "Error checking planned meal", error);
                                    addToWeeklyPlan(meal, date);
                                }
                        )
        );
    }

    private void addToWeeklyPlan(Meal meal, String date) {
        disposables.add(
                mealsRepository.addToWeeklyPlan(meal, currentUserId, date)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> view.showMessage("Added to weekly plan for " + date),
                                error -> {
                                    Log.e(TAG, "Error adding to weekly plan", error);
                                    view.showError("Failed to add to weekly plan");
                                }
                        )
        );
    }

    @Override
    public void checkFavoriteStatus(String mealId) {
        if (currentUserId == null) return;

        disposables.add(
                mealsRepository.isFavorite(currentUserId, mealId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                view::updateFavoriteIcon,
                                error -> Log.e(TAG, "Error checking favorite status", error)
                        )
        );
    }

    @Override
    public void onDestroy() {
        disposables.clear();
    }
}