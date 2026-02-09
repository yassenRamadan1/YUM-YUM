package com.example.yum_yum.presentation.home;

import android.content.Context;

import com.example.yum_yum.presentation.model.Meal;

import java.util.List;

public interface HomeContract {
    interface View {
        void showLoading();
        void hideLoading();
        void showDailyMeal(Meal meal);
        void showDailyCountryMeals(List<Meal> meals, String countryName);
        void showError(String message);
        void showUserName(String name);
        void showNoInternetError();
        void hideNoInternetError();
    }

    interface Presenter {
        void attachView(View view);
        void detachView();
        void getHomeContent();
        void refreshHomeContent();
        void startNetworkMonitoring(Context context);
        void stopNetworkMonitoring();
        void onDestroy();
    }
}