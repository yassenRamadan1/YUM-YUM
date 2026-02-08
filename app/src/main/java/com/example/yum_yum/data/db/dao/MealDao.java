package com.example.yum_yum.data.db.dao;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.yum_yum.data.meals.datasource.local.entity.MealEntity;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface MealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMeal(MealEntity meal);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable insertMeals(List<MealEntity> meals);

    @Query("SELECT * FROM meals WHERE id = :mealId LIMIT 1")
    Single<MealEntity> getMealById(String mealId);

    @Query("SELECT * FROM meals WHERE user_id = :userId AND is_favorite = 1 ORDER BY timestamp DESC")
    Flowable<List<MealEntity>> getFavoriteMeals(String userId);

    @Query("SELECT COUNT(*) > 0 FROM meals WHERE user_id = :userId AND id = :mealId AND is_favorite = 1")
    Single<Boolean> isFavorite(String userId, String mealId);

    @Query("UPDATE meals SET is_favorite = 1, user_id = :userId, is_synced = 0, timestamp = :timestamp WHERE id = :mealId")
    Completable markAsFavorite(String userId, String mealId, long timestamp);

    @Query("UPDATE meals SET is_favorite = 0, is_synced = 0 WHERE user_id = :userId AND id = :mealId")
    Completable removeFromFavorites(String userId, String mealId);

    @Query("SELECT * FROM meals WHERE user_id = :userId AND is_favorite = 1 AND is_synced = 0")
    Single<List<MealEntity>> getUnsyncedFavorites(String userId);

    @Query("UPDATE meals SET is_synced = 1 WHERE id IN (:mealIds) AND user_id = :userId AND is_favorite = 1")
    Completable markFavoritesAsSynced(String userId, List<String> mealIds);

    // Weekly plan queries
    @Query("SELECT * FROM meals WHERE user_id = :userId AND planned_date BETWEEN :startDate AND :endDate ORDER BY planned_date ASC")
    Flowable<List<MealEntity>> getPlannedMealsForWeek(String userId, String startDate, String endDate);

    @Query("SELECT COUNT(*) > 0 FROM meals WHERE user_id = :userId AND id = :mealId AND planned_date = :date")
    Single<Boolean> isPlanned(String userId, String mealId, String date);

    @Query("SELECT COUNT(*) > 0 FROM meals WHERE user_id = :userId AND planned_date = :date")
    Single<Boolean> hasPlannedMealForDate(String userId, String date);

    @Query("UPDATE meals SET planned_date = :date, user_id = :userId, is_synced = 0, timestamp = :timestamp WHERE id = :mealId")
    Completable addToWeeklyPlan(String userId, String mealId, String date, long timestamp);

    @Query("UPDATE meals SET planned_date = NULL, is_synced = 0 WHERE user_id = :userId AND id = :mealId AND planned_date = :date")
    Completable removeFromWeeklyPlan(String userId, String mealId, String date);

    @Query("SELECT * FROM meals WHERE user_id = :userId AND planned_date IS NOT NULL AND is_synced = 0")
    Single<List<MealEntity>> getUnsyncedPlans(String userId);

    @Query("UPDATE meals SET is_synced = 1 WHERE id IN (:mealIds) AND user_id = :userId AND planned_date IS NOT NULL")
    Completable markPlansAsSynced(String userId, List<String> mealIds);

    @Query("DELETE FROM meals WHERE user_id = :userId")
    Completable clearUserData(String userId);

    @Query("DELETE FROM meals WHERE id = :mealId")
    Completable deleteMeal(String mealId);
}

