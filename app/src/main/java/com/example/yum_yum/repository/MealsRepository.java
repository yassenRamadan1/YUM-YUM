package com.example.yum_yum.repository;

import com.example.yum_yum.data.meals.dto.MealDto;
import com.example.yum_yum.presentation.model.Meal;
import com.example.yum_yum.presentation.model.MealMapper;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Single;

public class MealsRepository {
    private final MealDatasource dataSource;

    public MealsRepository(MealDatasource dataSource) {
        this.dataSource = dataSource;
    }

    public Single<Meal> getDailyMeal() {
        return dataSource.getRandomMeal()
                .map(response -> response.getMeals().get(0)) // Get first item
                .map(MealMapper::mapToUiModel);
    }

    // This complex chain gets list of areas -> picks one -> gets meals for it
    public Single<List<Meal>> getMealsFromRandomCountry() {
        return dataSource.getAreaList()
                .flatMap(response -> {
                    List<MealDto> areas = response.getMeals();
                    if (areas == null || areas.isEmpty()) {
                        return Single.error(new Throwable("No areas found"));
                    }
                    int randomIndex = new Random().nextInt(areas.size());
                    String randomArea = areas.get(randomIndex).getArea();
                    return dataSource.getMealsByArea(randomArea);
                })
                .map(response -> {
                    List<MealDto> allMeals = response.getMeals();
                    if (allMeals.size() > 10) {
                        return allMeals.subList(0, 10);
                    }
                    return allMeals;
                })
                .map(dtos -> dtos.stream()
                        .map(MealMapper::mapToUiModel)
                        .collect(Collectors.toList()));
    }
}