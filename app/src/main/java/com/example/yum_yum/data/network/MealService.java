package com.example.yum_yum.data.network;

import com.example.yum_yum.data.meals.dto.CategoryResponse;
import com.example.yum_yum.data.meals.dto.MealResponse;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealService {
    @GET("random")
    Single<MealResponse> getRandomMeal();

    @GET("search")
    Single<MealResponse> searchMealByName(@Query("s") String mealName);

    @GET("lookup")
    Single<MealResponse> getMealById(@Query("i") String mealId);

    @GET("filter")
    Single<MealResponse> filterByCategory(@Query("c") String category);

    @GET("filter")
    Single<MealResponse> filterByIngredient(@Query("i") String ingredient);

    @GET("filter")
    Single<MealResponse> filterByArea(@Query("a") String area);

    @GET("categories")
    Single<CategoryResponse> getCategories();
}
