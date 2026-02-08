package com.example.yum_yum.presentation.Favorite;

import android.content.Context;
import android.util.Log;

import com.example.yum_yum.data.auth.repository.AuthRepository;
import com.example.yum_yum.data.meals.repository.MealsRepository;
import com.example.yum_yum.presentation.model.Meal;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FavoritePresenter implements FavoriteContract.Presenter {
    private static final String TAG = "FavoritePresenter";
    private final FavoriteContract.View view;
    private final MealsRepository mealsRepository;
    private final AuthRepository authRepository;
    private final CompositeDisposable disposables;
    private String currentUserId;

    public FavoritePresenter(FavoriteContract.View view, Context context) {
        this.view = view;
        this.mealsRepository = new MealsRepository(context);
        this.authRepository = new AuthRepository();
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void loadFavoriteMeals() {
        view.showLoading();
        disposables.add(
                authRepository.getCurrentUser()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                userId -> {
                                    this.currentUserId = userId;
                                    fetchFavoriteMeals(userId);
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showLoginRequired();
                                }
                        )
        );
    }

    private void fetchFavoriteMeals(String userId) {
        disposables.add(
                mealsRepository.getFavoriteMeals(userId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meals -> {
                                    view.hideLoading();
                                    if (meals.isEmpty()) {
                                        view.showEmptyState();
                                    } else {
                                        view.showFavoriteMeals(meals);
                                    }
                                },
                                error -> {
                                    view.hideLoading();
                                    Log.e(TAG, "Error loading favorite meals", error);
                                    view.showError("Failed to load favorite meals");
                                }
                        )
        );
    }

    @Override
    public void onRemoveFavoriteClicked(Meal meal) {
        view.showMessage("Remove " + meal.getName() + " from favorites?");
    }

    @Override
    public void confirmRemoveFavorite(Meal meal) {
        if (currentUserId == null) {
            view.showLoginRequired();
            return;
        }

        disposables.add(
                mealsRepository.removeFromFavorites(currentUserId, meal.getId())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    view.removeMealFromList(meal.getId());
                                    view.showMessage("Removed from favorites");
                                },
                                error -> {
                                    Log.e(TAG, "Error removing favorite", error);
                                    view.showError("Failed to remove favorite");
                                }
                        )
        );
    }

    @Override
    public void onMealClicked(Meal meal) {
        Log.d(TAG, "Meal clicked: " + meal.getName());
    }

    @Override
    public void onDestroy() {
        disposables.clear();
    }
}
