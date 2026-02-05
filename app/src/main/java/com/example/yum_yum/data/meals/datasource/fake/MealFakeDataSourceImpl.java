package com.example.yum_yum.data.meals.datasource.fake;

import com.example.yum_yum.data.meals.dto.MealDto;
import com.example.yum_yum.data.meals.dto.MealResponse;
import com.example.yum_yum.data.meals.repository.MealDatasource;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.core.Single;

public class MealFakeDataSourceImpl implements MealDatasource {
    @Override
    public Single<MealResponse> getRandomMeal() {
        MealDto meal = new MealDto();
        String json = "{ 'meals': [{ 'idMeal': '1', 'strMeal': 'Fake Adana Kebab', 'strCategory': 'Lamb', 'strArea': 'Turkish', 'strMealThumb': 'https://www.themealdb.com/images/media/meals/04axct1763793018.jpg', 'strInstructions': 'Tasty food...', 'strYoutube': 'https://www.youtube.com/watch?v=9WR40GHpsyo' }] }";
        MealResponse response = new Gson().fromJson(json, MealResponse.class);

        return Single.just(response).delay(1, TimeUnit.SECONDS);
    }

    @Override
    public Single<MealResponse> getAreaList() {
        // Mimic list.php?a=list
        String json = "{ 'meals': [{ 'strArea': 'American' }, { 'strArea': 'Turkish' }, { 'strArea': 'Canadian' }] }";
        MealResponse response = new Gson().fromJson(json, MealResponse.class);
        return Single.just(response).delay(500, TimeUnit.MILLISECONDS);
    }

    @Override
    public Single<MealResponse> getMealsByArea(String area) {
        List<MealDto> meals = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            MealDto m = new MealDto();
            String mealJson = String.format("{ 'idMeal': '%d', 'strMeal': '%s Meal %d', 'strMealThumb': 'https://www.themealdb.com/images/media/meals/grhn401765687086.jpg' }", i, area, i);
            meals.add(new Gson().fromJson(mealJson, MealDto.class));
        }
        MealResponse response = new MealResponse();
        String finalJson = "{ 'meals': " + new Gson().toJson(meals) + "}";
        MealResponse finalResponse = new Gson().fromJson(finalJson, MealResponse.class);

        return Single.just(finalResponse).delay(1, TimeUnit.SECONDS);
    }
}
