package com.example.yum_yum.data.meals.datasource.network;

import com.example.yum_yum.data.meals.datasource.local.entity.MealEntity;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class MealFirestoreDataSource {
    private final FirebaseFirestore firestore;
    private static final String COLLECTION_MEALS = "meals";
    private static final String COLLECTION_USER_FAVORITES = "user_favorites";
    private static final String COLLECTION_USER_PLANS = "user_plans";

    public MealFirestoreDataSource() {
        this.firestore = FirebaseFirestore.getInstance();
    }

    public Completable addFavorite(MealEntity meal) {
        return Completable.create(emitter -> {
            if (meal.getUserId() == null || !meal.isFavorite()) {
                emitter.onError(new IllegalArgumentException("Invalid favorite meal data"));
                return;
            }
            String mealDocId = meal.getId();
            Map<String, Object> mealData = convertMealToMap(meal);

            firestore.collection(COLLECTION_MEALS)
                    .document(mealDocId)
                    .set(mealData)
                    .addOnSuccessListener(aVoid -> {
                        String favoriteDocId = meal.getUserId() + "_" + meal.getId();
                        Map<String, Object> favoriteData = new HashMap<>();
                        favoriteData.put("userId", meal.getUserId());
                        favoriteData.put("mealId", meal.getId());
                        favoriteData.put("timestamp", meal.getTimestamp());

                        firestore.collection(COLLECTION_USER_FAVORITES)
                                .document(favoriteDocId)
                                .set(favoriteData)
                                .addOnSuccessListener(aVoid2 -> emitter.onComplete())
                                .addOnFailureListener(emitter::onError);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable removeFavorite(String userId, String mealId) {
        return Completable.create(emitter -> {
            String favoriteDocId = userId + "_" + mealId;

            firestore.collection(COLLECTION_USER_FAVORITES)
                    .document(favoriteDocId)
                    .delete()
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Single<List<MealEntity>> getFavoritesForUser(String userId) {
        return Single.create(emitter -> {
            firestore.collection(COLLECTION_USER_FAVORITES)
                    .whereEqualTo("userId", userId)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        List<String> mealIds = new ArrayList<>();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            String mealId = doc.getString("mealId");
                            if (mealId != null) {
                                mealIds.add(mealId);
                            }
                        }
                        if (mealIds.isEmpty()) {
                            emitter.onSuccess(new ArrayList<>());
                            return;
                        }
                        fetchMealsByIds(mealIds, userId, true, null)
                                .subscribe(
                                        emitter::onSuccess,
                                        emitter::onError
                                );
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable addPlan(MealEntity meal) {
        return Completable.create(emitter -> {
            if (meal.getUserId() == null || meal.getPlannedDate() == null) {
                emitter.onError(new IllegalArgumentException("Invalid plan meal data"));
                return;
            }
            String mealDocId = meal.getId();
            Map<String, Object> mealData = convertMealToMap(meal);

            firestore.collection(COLLECTION_MEALS)
                    .document(mealDocId)
                    .set(mealData)
                    .addOnSuccessListener(aVoid -> {
                        String planDocId = meal.getUserId() + "_" + meal.getId() + "_" + meal.getPlannedDate();
                        Map<String, Object> planData = new HashMap<>();
                        planData.put("userId", meal.getUserId());
                        planData.put("mealId", meal.getId());
                        planData.put("plannedDate", meal.getPlannedDate());
                        planData.put("timestamp", meal.getTimestamp());

                        firestore.collection(COLLECTION_USER_PLANS)
                                .document(planDocId)
                                .set(planData)
                                .addOnSuccessListener(aVoid2 -> emitter.onComplete())
                                .addOnFailureListener(emitter::onError);
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Completable removePlan(String userId, String mealId, String date) {
        return Completable.create(emitter -> {
            String planDocId = userId + "_" + mealId + "_" + date;

            firestore.collection(COLLECTION_USER_PLANS)
                    .document(planDocId)
                    .delete()
                    .addOnSuccessListener(aVoid -> emitter.onComplete())
                    .addOnFailureListener(emitter::onError);
        });
    }

    public Single<List<MealEntity>> getPlansForUser(String userId, String startDate, String endDate) {
        return Single.create(emitter -> {
            firestore.collection(COLLECTION_USER_PLANS)
                    .whereEqualTo("userId", userId)
                    .whereGreaterThanOrEqualTo("plannedDate", startDate)
                    .whereLessThanOrEqualTo("plannedDate", endDate)
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        Map<String, String> mealIdToDateMap = new HashMap<>();
                        for (QueryDocumentSnapshot doc : querySnapshot) {
                            String mealId = doc.getString("mealId");
                            String date = doc.getString("plannedDate");
                            if (mealId != null && date != null) {
                                mealIdToDateMap.put(mealId, date);
                            }
                        }

                        if (mealIdToDateMap.isEmpty()) {
                            emitter.onSuccess(new ArrayList<>());
                            return;
                        }
                        fetchMealsByIds(new ArrayList<>(mealIdToDateMap.keySet()), userId, false, mealIdToDateMap)
                                .subscribe(
                                        emitter::onSuccess,
                                        emitter::onError
                                );
                    })
                    .addOnFailureListener(emitter::onError);
        });
    }

    private Single<List<MealEntity>> fetchMealsByIds(List<String> mealIds, String userId,
                                                     boolean isFavorite, Map<String, String> mealIdToDateMap) {
        return Single.create(emitter -> {
            List<MealEntity> meals = new ArrayList<>();
            int[] completedCount = {0};

            for (String mealId : mealIds) {
                firestore.collection(COLLECTION_MEALS)
                        .document(mealId)
                        .get()
                        .addOnSuccessListener(documentSnapshot -> {
                            if (documentSnapshot.exists()) {
                                MealEntity meal = convertMapToMeal(documentSnapshot.getData());
                                if (meal != null) {
                                    meal.setUserId(userId);
                                    meal.setFavorite(isFavorite);
                                    if (mealIdToDateMap != null && mealIdToDateMap.containsKey(mealId)) {
                                        meal.setPlannedDate(mealIdToDateMap.get(mealId));
                                    }
                                    meal.setSynced(true);
                                    meals.add(meal);
                                }
                            }

                            completedCount[0]++;
                            if (completedCount[0] == mealIds.size()) {
                                emitter.onSuccess(meals);
                            }
                        })
                        .addOnFailureListener(e -> {
                            completedCount[0]++;
                            if (completedCount[0] == mealIds.size()) {
                                emitter.onSuccess(meals);
                            }
                        });
            }
        });
    }

    private Map<String, Object> convertMealToMap(MealEntity meal) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", meal.getId());
        map.put("name", meal.getName());
        map.put("category", meal.getCategory());
        map.put("area", meal.getArea());
        map.put("instructions", meal.getInstructions());
        map.put("imageUrl", meal.getImageUrl());
        map.put("youtubeUrl", meal.getYoutubeUrl());
        map.put("ingredientsJson", meal.getIngredientsJson());
        map.put("measuresJson", meal.getMeasuresJson());
        map.put("cachedAt", meal.getCachedAt());
        return map;
    }

    private MealEntity convertMapToMeal(Map<String, Object> map) {
        if (map == null) return null;

        MealEntity meal = new MealEntity();
        meal.setId((String) map.get("id"));
        meal.setName((String) map.get("name"));
        meal.setCategory((String) map.get("category"));
        meal.setArea((String) map.get("area"));
        meal.setInstructions((String) map.get("instructions"));
        meal.setImageUrl((String) map.get("imageUrl"));
        meal.setYoutubeUrl((String) map.get("youtubeUrl"));
        meal.setIngredientsJson((String) map.get("ingredientsJson"));
        meal.setMeasuresJson((String) map.get("measuresJson"));

        Object cachedAt = map.get("cachedAt");
        if (cachedAt instanceof Long) {
            meal.setCachedAt((Long) cachedAt);
        } else if (cachedAt != null) {
            meal.setCachedAt(System.currentTimeMillis());
        }

        return meal;
    }
}

