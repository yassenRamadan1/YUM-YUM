package com.example.yum_yum.data.meals.datasource.local;

import android.content.Context;
import android.content.SharedPreferences;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DailyCachePreferences {

    private static final String PREF_NAME = "daily_cache";
    private static final String KEY_DAILY_MEAL_ID = "daily_meal_id";
    private static final String KEY_COUNTRY_NAME = "country_name";
    private static final String KEY_CACHE_DATE = "cache_date";

    private final SharedPreferences preferences;

    public DailyCachePreferences(Context context) {
        this.preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveDailyCache(String dailyMealId, String countryName) {
        preferences.edit()
                .putString(KEY_DAILY_MEAL_ID, dailyMealId)
                .putString(KEY_COUNTRY_NAME, countryName)
                .putString(KEY_CACHE_DATE, getTodayDate())
                .apply();
    }

    public String getCachedDailyMealId() {
        if (!isCacheValid()) return null;
        return preferences.getString(KEY_DAILY_MEAL_ID, null);
    }

    public String getCachedCountryName() {
        if (!isCacheValid()) return null;
        return preferences.getString(KEY_COUNTRY_NAME, null);
    }

    public boolean isCacheValid() {
        String cachedDate = preferences.getString(KEY_CACHE_DATE, null);
        return cachedDate != null && cachedDate.equals(getTodayDate());
    }

    public String getTodayDate() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        return sdf.format(new Date());
    }

    public void clearCache() {
        preferences.edit().clear().apply();
    }
}
