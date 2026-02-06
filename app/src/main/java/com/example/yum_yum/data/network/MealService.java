package com.example.yum_yum.data.network;

import com.example.yum_yum.data.meals.dto.CategoryResponse;
import com.example.yum_yum.data.meals.dto.MealResponse;

import io.reactivex.rxjava3.core.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MealService {
    @GET("random.php")
    Single<MealResponse> getRandomMeal();

    @GET("search.php")
    Single<MealResponse> searchMealByName(@Query("s") String mealName);

    @GET("lookup.php")
    Single<MealResponse> getMealById(@Query("i") String mealId);

    @GET("filter.php")
    Single<MealResponse> filterByCategory(@Query("c") String category);

    @GET("filter.php")
    Single<MealResponse> filterByIngredient(@Query("i") String ingredient);

    @GET("filter.php")
    Single<MealResponse> filterByArea(@Query("a") String area);

    @GET("categories.php")
    Single<CategoryResponse> getCategories();
}
