package com.example.yum_yum.data.meals.repository;

import static com.example.yum_yum.data.meals.repository.MealMapper.mapToUiModel;

import android.content.Context;

import com.example.yum_yum.data.meals.datasource.local.DailyCachePreferences;
import com.example.yum_yum.data.meals.datasource.network.MealsNetworkDataSource;
import com.example.yum_yum.data.meals.dto.AreaResponse;
import com.example.yum_yum.data.meals.dto.CountryMealsData;
import com.example.yum_yum.data.meals.dto.HomeContentData;
import com.example.yum_yum.data.meals.dto.MealDto;
import com.example.yum_yum.presentation.model.Meal;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import io.reactivex.rxjava3.core.Single;

public class MealsRepository {
    private final MealsNetworkDataSource networkDataSource;
    private final DailyCachePreferences cachePreferences;

    public MealsRepository(Context context) {
        this.networkDataSource = new MealsNetworkDataSource();
        this.cachePreferences = new DailyCachePreferences(context);
    }
    public Single<HomeContentData> getHomeContent() {
        String cachedMealId = cachePreferences.getCachedDailyMealId();
        String cachedCountry = cachePreferences.getCachedCountryName();
        if (cachedMealId != null && cachedCountry != null) {
            return fetchHomeContentByIds(cachedMealId, cachedCountry);
        } else {
            return fetchAndCacheFreshHomeContent();
        }
    }

    private Single<HomeContentData> fetchHomeContentByIds(String dailyMealId, String countryName) {
        return Single.zip(
                networkDataSource.getMealById(dailyMealId)
                        .map(response -> mapToUiModel(response.getMeals().get(0))),
                fetchCountryMeals(countryName),
                (dailyMeal, countryMeals) -> new HomeContentData(
                        dailyMeal,
                        countryMeals,
                        countryName
                )
        );
    }

    private Single<HomeContentData> fetchAndCacheFreshHomeContent() {
        return Single.zip(
                getRandomMeal(),
                getRandomCountryWith10Meals(),
                (dailyMeal, countryData) -> {
                    cachePreferences.saveDailyCache(
                            dailyMeal.getId(),
                            countryData.getCountryName()
                    );
                    return new HomeContentData(
                            dailyMeal,
                            countryData.getMeals(),
                            countryData.getCountryName()
                    );
                }
        );
    }


    public Single<Meal> getRandomMeal() {
        return networkDataSource.getRandomMeal()
                .map(response -> mapToUiModel(response.getMeals().get(0)));
    }
    private Single<List<Meal>> fetchCountryMeals(String countryName) {
        return networkDataSource.getMealsByArea(countryName)
                .flatMap(mealResponse -> {
                    List<MealDto> meals = mealResponse.getMeals();

                    if (meals == null || meals.isEmpty()) {
                        return Single.error(new Exception("No meals found for: " + countryName));
                    }
                    List<MealDto> selectedMeals = meals.size() > 10
                            ? meals.subList(0, 10)
                            : meals;
                    List<Single<Meal>> detailRequests = new ArrayList<>();
                    for (MealDto meal : selectedMeals) {
                        detailRequests.add(
                                networkDataSource.getMealById(meal.getId())
                                        .map(response -> mapToUiModel(response.getMeals().get(0)))
                        );
                    }
                    return Single.zip(detailRequests, objects -> {
                        List<Meal> fullMeals = new ArrayList<>();
                        for (Object obj : objects) {
                            fullMeals.add((Meal) obj);
                        }
                        return fullMeals;
                    });
                });
    }

    private Single<CountryMealsData> getRandomCountryWith10Meals() {
        return networkDataSource.getAllAreas()
                .flatMap(areaResponse -> {
                    List<AreaResponse.AreaDto> areas = areaResponse.getAreas();
                    if (areas == null || areas.isEmpty()) {
                        return Single.error(new Exception("No areas available"));
                    }
                    Random random = new Random();
                    String randomArea = areas.get(random.nextInt(areas.size())).getArea();
                    return fetchCountryMeals(randomArea)
                            .map(meals -> new CountryMealsData(randomArea, meals));
                });
    }

    public Single<HomeContentData> refreshHomeContent() {
        return fetchAndCacheFreshHomeContent();
    }
    public void clearCache() {
        cachePreferences.clearCache();
    }

}