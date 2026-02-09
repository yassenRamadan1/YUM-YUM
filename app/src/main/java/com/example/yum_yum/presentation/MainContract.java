package com.example.yum_yum.presentation;

public interface MainContract {
    interface View {
        void navigateToHome();
        void navigateToWelcome();
        void showLoading();
        void hideLoading();
    }

    interface Presenter {
        void checkAppStartDestination();
        void onDestroy();
    }
}