package com.example.yum_yum.presentation.weaklyMeals;

import android.content.Context;
import android.util.Log;

import com.example.yum_yum.data.auth.repository.AuthRepository;
import com.example.yum_yum.data.meals.repository.MealsRepository;
import com.example.yum_yum.presentation.model.Meal;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class WeeklyMealsPresenter implements WeeklyMealsContract.Presenter {
    private static final String TAG = "WeeklyMealsPresenter";
    private final WeeklyMealsContract.View view;
    private final MealsRepository mealsRepository;
    private final AuthRepository authRepository;
    private final CompositeDisposable disposables;
    private String currentUserId;

    public WeeklyMealsPresenter(WeeklyMealsContract.View view, Context context) {
        this.view = view;
        this.mealsRepository = new MealsRepository(context);
        this.authRepository = new AuthRepository(context);
        this.disposables = new CompositeDisposable();
    }

    @Override
    public void loadWeeklyMeals() {
        view.showLoading();

        disposables.add(
                authRepository.isUserLoggedIn()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(isLoggedIn -> {
                            if (isLoggedIn) {
                                fetchUserAndMeals();
                            } else {
                                view.hideLoading();
                                view.showLoginRequired();
                            }
                        }, error -> {
                            view.hideLoading();
                            Log.e(TAG, "Error checking login status", error);
                            view.showLoginRequired();
                        })
        );
    }

    private void fetchUserAndMeals() {
        disposables.add(
                authRepository.getCurrentUserUUIDFromLocal()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                userId -> {
                                    this.currentUserId = userId;
                                    fetchWeeklyMeals(userId);
                                },
                                error -> {
                                    view.hideLoading();
                                    Log.e(TAG, "Error fetching User ID", error);
                                    view.showLoginRequired();
                                }
                        )
        );
    }

    private void fetchWeeklyMeals(String userId) {
        String[] dateRange = getCurrentWeekRange();
        String startDate = dateRange[0];
        String endDate = dateRange[1];

        disposables.add(
                mealsRepository.getPlannedMealsForWeek(userId, startDate, endDate)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                meals -> {
                                    view.hideLoading();
                                    if (meals.isEmpty()) {
                                        view.showEmptyState();
                                    } else {
                                        Map<String, List<Meal>> groupedMeals = groupMealsByDate(meals);
                                        view.showPlannedMeals(groupedMeals);
                                    }
                                },
                                error -> {
                                    view.hideLoading();
                                    Log.e(TAG, "Error loading weekly meals", error);
                                    view.showError("Failed to load weekly meals");
                                }
                        )
        );
    }

    @Override
    public void onRemoveMealClicked(Meal meal, String date) {
        view.showMessage("Remove " + meal.getName() + " from " + date + "?");
    }

    @Override
    public void confirmRemoveMeal(Meal meal, String date) {
        if (currentUserId == null) {
            view.showLoginRequired();
            return;
        }

        disposables.add(
                mealsRepository.removeFromWeeklyPlan(currentUserId, meal.getId(), date)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    view.removeMealFromList(date, meal.getId());
                                    view.showMessage("Meal removed from weekly plan");
                                },
                                error -> {
                                    Log.e(TAG, "Error removing meal from plan", error);
                                    view.showError("Failed to remove meal");
                                }
                        )
        );
    }

    @Override
    public void onDestroy() {
        disposables.clear();
    }

    // Helper methods
    private String[] getCurrentWeekRange() {
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());
        String startDate = sdf.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_WEEK, 6);
        String endDate = sdf.format(calendar.getTime());

        return new String[]{startDate, endDate};
    }

    private Map<String, List<Meal>> groupMealsByDate(List<Meal> meals) {
        Map<String, List<Meal>> grouped = new HashMap<>();
        String[] dateRange = getCurrentWeekRange();
        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        try {
            calendar.setTime(sdf.parse(dateRange[0]));
            for (int i = 0; i < 7; i++) {
                String date = sdf.format(calendar.getTime());
                grouped.put(date, new ArrayList<>());
                calendar.add(Calendar.DAY_OF_WEEK, 1);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error creating date range", e);
        }

        for (Meal meal : meals) {
            String plannedDate = meal.getPlannedDate();
            if (plannedDate != null && grouped.containsKey(plannedDate)) {
                grouped.get(plannedDate).add(meal);
            }
        }

        return grouped;
    }
}

