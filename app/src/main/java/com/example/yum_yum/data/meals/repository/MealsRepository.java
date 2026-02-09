package com.example.yum_yum.data.meals.repository;

import static android.content.ContentValues.TAG;
import static com.example.yum_yum.data.meals.repository.MealMapper.convertToEntity;
import static com.example.yum_yum.data.meals.repository.MealMapper.mapToUiModel;

import android.content.Context;
import android.util.Log;

import com.example.yum_yum.data.meals.datasource.local.DailyCachePreferences;
import com.example.yum_yum.data.meals.datasource.local.MealLocalDataSource;
import com.example.yum_yum.data.meals.datasource.local.entity.MealEntity;
import com.example.yum_yum.data.meals.datasource.network.MealFirestoreDataSource;
import com.example.yum_yum.data.meals.datasource.network.MealsNetworkDataSource;
import com.example.yum_yum.data.meals.dto.AreaResponse;
import com.example.yum_yum.data.meals.dto.CategoryDto;
import com.example.yum_yum.data.meals.dto.CountryMealsData;
import com.example.yum_yum.data.meals.dto.HomeContentData;
import com.example.yum_yum.data.meals.dto.IngredientResponse;
import com.example.yum_yum.data.meals.dto.MealDto;
import com.example.yum_yum.data.meals.dto.MealResponse;
import com.example.yum_yum.presentation.model.Category;
import com.example.yum_yum.presentation.model.IngredientItem;
import com.example.yum_yum.presentation.model.Meal;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MealsRepository {
    private final MealsNetworkDataSource networkDataSource;
    private final DailyCachePreferences cachePreferences;
    private final MealLocalDataSource localDataSource;
    private final MealFirestoreDataSource firestoreDataSource;
    private List<Meal> allMealsCache = null;
    private List<Category> allCategoriesCache = null;
    private List<String> allAreasCache = null;
    private List<String> allIngredientsCache = null;

    public MealsRepository(Context context) {
        this.networkDataSource = new MealsNetworkDataSource();
        this.cachePreferences = new DailyCachePreferences(context);
        this.localDataSource = new MealLocalDataSource(context);
        this.firestoreDataSource = new MealFirestoreDataSource();
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
    public Single<List<Meal>> getAllMeals() {
        if (allMealsCache != null) {
            Log.d(TAG, "Returning cached meals: " + allMealsCache.size());
            return Single.just(allMealsCache);
        }

        Log.d(TAG, "Fetching all meals from network...");

        return networkDataSource.getAllCategories()
                .flatMap(categoryResponse -> {
                    List<CategoryDto> categories = categoryResponse.getCategories();
                    if (categories == null || categories.isEmpty()) {
                        return Single.error(new Exception("No categories available"));
                    }
                    List<Single<MealResponse>> categoryRequests = new ArrayList<>();
                    for (CategoryDto category : categories) {
                        categoryRequests.add(
                                networkDataSource.getMealsByCategory(category.getStrCategory())
                                        .onErrorReturnItem(new MealResponse())
                        );
                    }
                    return Single.zip(categoryRequests, objects -> {
                        Set<String> uniqueMealIds = new HashSet<>();
                        for (Object obj : objects) {
                            MealResponse response = (MealResponse) obj;
                            if (response.getMeals() != null) {
                                for (MealDto dto : response.getMeals()) {
                                    uniqueMealIds.add(dto.getId());
                                }
                            }
                        }
                        Log.d(TAG, "Found " + uniqueMealIds.size() + " unique meal IDs");
                        return new ArrayList<>(uniqueMealIds);
                    });
                })
                .flatMap(uniqueMealIds -> {
                    List<Single<Meal>> mealDetailRequests = new ArrayList<>();
                    for (String mealId : uniqueMealIds) {
                        mealDetailRequests.add(
                                networkDataSource.getMealById(mealId)
                                        .map(response -> {
                                            Log.d("MealsRepository", "Fetched details for meal ID: " + mealId);
                                            return mapToUiModel(response.getMeals().get(0));})
                                        .onErrorReturnItem(  new Meal(  mealId,
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                "",
                                                new ArrayList<>()
                                        ))
                        );
                    }
                    return Single.zip(mealDetailRequests, objects -> {
                        List<Meal> allMeals = new ArrayList<>();
                        for (Object obj : objects) {
                            if (obj != null) {
                                allMeals.add((Meal) obj);
                            }
                        }
                        Log.d(TAG, "Fetched total meals: " + allMeals.size());
                        return allMeals;
                    });
                })
                .doOnSuccess(meals -> {
                    allMealsCache = meals;
                    Log.d(TAG, "Cached " + meals.size() + " meals");
                });
    }
    public Single<List<Category>> getAllCategories() {
        if ( allCategoriesCache != null) {
            return Single.just(allCategoriesCache);
        }

        return networkDataSource.getAllCategories()
                .map(MealMapper::mapToModelList)
                .doOnSuccess(categories -> {
                    allCategoriesCache = categories;
                    Log.d(TAG, "Cached " + categories.size() + " categories");
                });
    }
    public Single<List<String>> getAllAreas() {
        if (allAreasCache != null) {
            return Single.just(allAreasCache);
        }

        return networkDataSource.getAllAreas()
                .map(areaResponse -> {
                    List<String> areas = new ArrayList<>();
                    if (areaResponse.getAreas() != null) {
                        for (AreaResponse.AreaDto areaDto : areaResponse.getAreas()) {
                            areas.add(areaDto.getArea());
                        }
                    }
                    return areas;
                })
                .doOnSuccess(areas -> {
                    allAreasCache = areas;
                    Log.d(TAG, "Cached " + areas.size() + " areas");
                });
    }
    public Single<List<String>> getAllIngredients() {
        if ( allIngredientsCache != null) {
            return Single.just(allIngredientsCache);
        }

        return networkDataSource.getAllIngredients()
                .map(ingredientResponse -> {
                    List<String> ingredients = new ArrayList<>();
                    if (ingredientResponse.getMeals() != null) {
                        for (IngredientResponse.Ingredient ingredient : ingredientResponse.getMeals()) {
                            if (ingredient.getName() != null && !ingredient.getName().isEmpty()) {
                                ingredients.add(ingredient.getName());
                            }
                        }
                    }
                    return ingredients;
                })
                .doOnSuccess(ingredients -> {
                    allIngredientsCache = ingredients;
                    Log.d(TAG, "Cached " + ingredients.size() + " ingredients");
                });
    }
    public Single<List<Meal>> getMealsByCategory(String category) {
        return networkDataSource.getMealsByCategory(category)
                .map(response -> {
                    List<Meal> meals = new ArrayList<>();
                    if (response.getMeals() != null) {
                        for (MealDto dto : response.getMeals()) {
                            meals.add(new Meal(dto.getId(), dto.getName(), dto.getThumbnail(),
                                    "", "", "", "", new ArrayList<>()));
                        }
                    }
                    return meals;
                });
    }

    public Single<List<Meal>> getMealsByArea(String area) {
        return networkDataSource.getMealsByArea(area)
                .map(response -> {
                    List<Meal> meals = new ArrayList<>();
                    if (response.getMeals() != null) {
                        for (MealDto dto : response.getMeals()) {
                            meals.add(new Meal(dto.getId(), dto.getName(), dto.getThumbnail(),
                                    "", "", "", "", new ArrayList<>()));
                        }
                    }
                    return meals;
                });
    }

    public Single<List<Meal>> getMealsByIngredient(String ingredient) {
        return networkDataSource.getMealsByIngredient(ingredient)
                .map(response -> {
                    List<Meal> meals = new ArrayList<>();
                    if (response.getMeals() != null) {
                        for (MealDto dto : response.getMeals()) {
                            meals.add(new Meal(dto.getId(), dto.getName(), dto.getThumbnail(),
                                    "", "", "", "", new ArrayList<>()));
                        }
                    }
                    return meals;
                });
    }

    public Single<Meal> getMealById(String id) {
        return networkDataSource.getMealById(id)
                .map(response -> mapToUiModel(response.getMeals().get(0)));
    }

    public Single<List<Meal>> searchMeals(
            String query,
            List<String> selectedCategories,
            List<String> selectedAreas,
            List<String> selectedIngredients
    ) {
        return getAllMeals()
                .map(allMeals -> {
                    List<Meal> filteredMeals = new ArrayList<>(allMeals);
                    if (query != null && !query.trim().isEmpty()) {
                        String lowerQuery = query.toLowerCase().trim();
                        filteredMeals = filteredMeals.stream()
                                .filter(meal -> meal.getName().toLowerCase().contains(lowerQuery))
                                .collect(Collectors.toList());
                    }
                    if (selectedCategories != null && !selectedCategories.isEmpty()) {
                        filteredMeals = filteredMeals.stream()
                                .filter(meal -> selectedCategories.contains(meal.getCategory()))
                                .collect(Collectors.toList());
                    }
                    if (selectedAreas != null && !selectedAreas.isEmpty()) {
                        filteredMeals = filteredMeals.stream()
                                .filter(meal -> selectedAreas.contains(meal.getArea()))
                                .collect(Collectors.toList());
                    }
                    if (selectedIngredients != null && !selectedIngredients.isEmpty()) {
                        filteredMeals = filteredMeals.stream()
                                .filter(meal -> {
                                    List<String> mealIngredientNames = meal.getIngredients().stream()
                                            .map(IngredientItem::getName)
                                            .map(String::toLowerCase)
                                            .collect(Collectors.toList());
                                    for (String selectedIngredient : selectedIngredients) {
                                        boolean found = false;
                                        for (String mealIngredient : mealIngredientNames) {
                                            if (mealIngredient.contains(selectedIngredient.toLowerCase())) {
                                                found = true;
                                                break;
                                            }
                                        }
                                        if (!found) {
                                            return false;
                                        }
                                    }
                                    return true;
                                })
                                .collect(Collectors.toList());
                    }
                    Log.d(TAG, "Search results: " + filteredMeals.size() + " meals");
                    return filteredMeals;
                });
    }
    public Single<List<Meal>> searchMealsByName(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Single.just(new ArrayList<>());
        }
        return networkDataSource.searchMealByName(query)
                .map(mealResponse -> {
                    if (mealResponse.getMeals() == null) {
                        return new ArrayList<Meal>();
                    }
                    return mealResponse.getMeals().stream()
                            .map(MealMapper::mapToUiModel)
                            .collect(Collectors.toList());
                })
                .onErrorResumeNext(error -> {
                    Log.w(TAG, "API search failed, using local filtering", error);
                    return searchMeals(query, null, null, null);
                });
    }
    public void clearCache() {
        cachePreferences.clearCache();
    }

    public Completable addToFavorites(Meal meal, String userId) {
        MealEntity mealEntity = convertToEntity(meal);

        return localDataSource.addToFavorites(mealEntity, userId)
                .doOnComplete(() ->
                        firestoreDataSource.addFavorite(mealEntity)
                                .andThen(localDataSource.markFavoritesAsSynced(
                                        userId,
                                        java.util.Collections.singletonList(meal.getId())
                                ))
                                .subscribeOn(Schedulers.io())
                                .subscribe(
                                        () -> Log.d(TAG, "Firestore sync: favorite added"),
                                        error -> Log.w(TAG, "Firestore sync failed for addFavorite", error)
                                )
                );
    }

    public Completable removeFromFavorites(String userId, String mealId) {
        return localDataSource.removeFromFavorites(userId, mealId)
                .doOnComplete(() ->
                        firestoreDataSource.removeFavorite(userId, mealId)
                                .subscribeOn(Schedulers.io())
                                .subscribe(
                                        () -> Log.d(TAG, "Firestore sync: favorite removed"),
                                        error -> Log.w(TAG, "Firestore sync failed for removeFavorite", error)
                                )
                );
    }

    public Flowable<List<Meal>> getFavoriteMeals(String userId) {
        return localDataSource.getFavoriteMeals(userId)
                .flatMap(localFavorites -> {
                    if (!localFavorites.isEmpty()) {
                        return Flowable.just(convertEntitiesToMeals(localFavorites));
                    } else {
                        return syncFavoritesFromFirestore(userId)
                                .andThen(localDataSource.getFavoriteMeals(userId)
                                        .map(this::convertEntitiesToMeals))
                                .onErrorReturn(error -> new ArrayList<>());
                    }
                });
    }

    public Single<Boolean> isFavorite(String userId, String mealId) {
        return localDataSource.isFavorite(userId, mealId);
    }

    private Completable syncFavoritesFromFirestore(String userId) {
        return firestoreDataSource.getFavoritesForUser(userId)
                .timeout(5, java.util.concurrent.TimeUnit.SECONDS)
                .flatMapCompletable(meals -> {
                    if (meals.isEmpty()) {
                        return Completable.complete();
                    }
                    return localDataSource.insertMeals(meals);
                });
    }

    public Completable addToWeeklyPlan(Meal meal, String userId, String date) {
        MealEntity mealEntity = convertToEntity(meal);

        return localDataSource.addToWeeklyPlan(mealEntity, userId, date)
                .doOnComplete(() ->
                        firestoreDataSource.addPlan(mealEntity)
                                .andThen(localDataSource.markPlansAsSynced(
                                        userId,
                                        java.util.Collections.singletonList(meal.getId())
                                ))
                                .subscribeOn(Schedulers.io())
                                .subscribe(
                                        () -> Log.d(TAG, "Firestore sync: plan added"),
                                        error -> Log.w(TAG, "Firestore sync failed for addPlan", error)
                                )
                );
    }

    public Completable removeFromWeeklyPlan(String userId, String mealId, String date) {
        return localDataSource.removeFromWeeklyPlan(userId, mealId, date)
                .doOnComplete(() ->
                        firestoreDataSource.removePlan(userId, mealId, date)
                                .subscribeOn(Schedulers.io())
                                .subscribe(
                                        () -> Log.d(TAG, "Firestore sync: plan removed"),
                                        error -> Log.w(TAG, "Firestore sync failed for removePlan", error)
                                )
                );
    }

    public Flowable<List<Meal>> getPlannedMealsForWeek(String userId, String startDate, String endDate) {
        return localDataSource.getPlannedMealsForWeek(userId, startDate, endDate)
                .flatMap(localPlans -> {
                    if (!localPlans.isEmpty()) {
                        return Flowable.just(convertEntitiesToMeals(localPlans));
                    } else {
                        return syncPlansFromFirestore(userId, startDate, endDate)
                                .andThen(localDataSource.getPlannedMealsForWeek(userId, startDate, endDate)
                                        .map(this::convertEntitiesToMeals))
                                .onErrorReturn(error -> new ArrayList<>());
                    }
                });
    }

    public Single<Boolean> hasPlannedMealForDate(String userId, String date) {
        return localDataSource.hasPlannedMealForDate(userId, date);
    }

    private Completable syncPlansFromFirestore(String userId, String startDate, String endDate) {
        return firestoreDataSource.getPlansForUser(userId, startDate, endDate)
                .timeout(5, java.util.concurrent.TimeUnit.SECONDS)
                .flatMapCompletable(meals -> {
                    if (meals.isEmpty()) {
                        return Completable.complete();
                    }
                    return localDataSource.insertMeals(meals);
                });
    }
    public Completable clearAllData(String userId) {
        return localDataSource.clearUserData(userId);
    }

    private List<Meal> convertEntitiesToMeals(List<MealEntity> entities) {
        return entities.stream()
                .map(MealMapper::convertEntityToMeal)
                .collect(Collectors.toList());
    }
}
