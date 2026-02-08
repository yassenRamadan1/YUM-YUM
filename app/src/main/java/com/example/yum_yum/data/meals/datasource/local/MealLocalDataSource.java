package com.example.yum_yum.data.meals.datasource.local;

import android.content.Context;

import com.example.yum_yum.data.db.AppDatabase;
import com.example.yum_yum.data.db.dao.MealDao;
import com.example.yum_yum.data.meals.datasource.local.entity.MealEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

public class MealLocalDataSource {
    private final MealDao mealDao;

    public MealLocalDataSource(Context context) {
        AppDatabase database = AppDatabase.getInstance(context);
        this.mealDao = database.mealDao();
    }

    public Completable insertMeal(MealEntity meal) {
        return mealDao.insertMeal(meal);
    }

    public Completable insertMeals(List<MealEntity> meals) {
        return mealDao.insertMeals(meals);
    }

    public Completable addToFavorites(MealEntity meal, String userId) {
        meal.setUserId(userId);
        meal.setFavorite(true);
        meal.setSynced(false);
        meal.setTimestamp(System.currentTimeMillis());
        return insertMeal(meal)
                .onErrorResumeNext(error ->
                        mealDao.markAsFavorite(userId, meal.getId(), System.currentTimeMillis())
                );
    }

    public Completable removeFromFavorites(String userId, String mealId) {
        return mealDao.removeFromFavorites(userId, mealId);
    }

    public Flowable<List<MealEntity>> getFavoriteMeals(String userId) {
        return mealDao.getFavoriteMeals(userId);
    }

    public Single<Boolean> isFavorite(String userId, String mealId) {
        return mealDao.isFavorite(userId, mealId);
    }

    public Completable markFavoritesAsSynced(String userId, List<String> mealIds) {
        if (mealIds == null || mealIds.isEmpty()) {
            return Completable.complete();
        }
        return mealDao.markFavoritesAsSynced(userId, mealIds);
    }

    public Completable addToWeeklyPlan(MealEntity meal, String userId, String date) {
        meal.setUserId(userId);
        meal.setPlannedDate(date);
        meal.setSynced(false);
        meal.setTimestamp(System.currentTimeMillis());

        return insertMeal(meal)
                .onErrorResumeNext(error ->
                        mealDao.addToWeeklyPlan(userId, meal.getId(), date, System.currentTimeMillis())
                );
    }

    public Completable removeFromWeeklyPlan(String userId, String mealId, String date) {
        return mealDao.removeFromWeeklyPlan(userId, mealId, date);
    }

    public Flowable<List<MealEntity>> getPlannedMealsForWeek(String userId, String startDate, String endDate) {
        return mealDao.getPlannedMealsForWeek(userId, startDate, endDate);
    }


    public Single<Boolean> hasPlannedMealForDate(String userId, String date) {
        return mealDao.hasPlannedMealForDate(userId, date);
    }

    public Completable markPlansAsSynced(String userId, List<String> mealIds) {
        if (mealIds == null || mealIds.isEmpty()) {
            return Completable.complete();
        }
        return mealDao.markPlansAsSynced(userId, mealIds);
    }

    public Completable clearUserData(String userId) {
        return mealDao.clearUserData(userId);
    }
}
