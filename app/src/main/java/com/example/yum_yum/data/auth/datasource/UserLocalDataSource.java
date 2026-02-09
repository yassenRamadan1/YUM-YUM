package com.example.yum_yum.data.auth.datasource;

import android.content.Context;
import android.content.SharedPreferences;

public class UserLocalDataSource {
    private static final String PREF_NAME = "YumYum_Prefs";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    private static final String KEY_IS_FIRST_TIME = "is_first_time";
    private static final String KEY_USER_NAME = "user_name";
    private static final String KEY_EMAIL = "user_email";
    private static final String USER_UUID = "user_uuid";

    private final SharedPreferences sharedPreferences;

    public UserLocalDataSource(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public void saveUserSession(String name, String email, String uuid) {
        sharedPreferences.edit()
                .putBoolean(KEY_IS_LOGGED_IN, true)
                .putString(KEY_USER_NAME, name)
                .putString(KEY_EMAIL, email)
                .putString(USER_UUID, uuid)
                .apply();
    }

    public void clearUserSession() {
        sharedPreferences.edit()
                .remove(KEY_IS_LOGGED_IN)
                .remove(KEY_USER_NAME)
                .remove(KEY_EMAIL)
                .remove(USER_UUID)
                .apply();
    }

    public boolean isUserLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public boolean isFirstTimeAppOpen() {
        return sharedPreferences.getBoolean(KEY_IS_FIRST_TIME, true);
    }

    public void setFirstTimeAppOpen(boolean isFirstTime) {
        sharedPreferences.edit().putBoolean(KEY_IS_FIRST_TIME, isFirstTime).apply();
    }

    public String getUserName() {
        return sharedPreferences.getString(KEY_USER_NAME, "Guest");
    }

    public String getUserUuid() {
        return sharedPreferences.getString(USER_UUID, "1");
    }

    public void saveUserUuid(String uuid) {
        sharedPreferences.edit().putString(USER_UUID, uuid).apply();
    }
}

