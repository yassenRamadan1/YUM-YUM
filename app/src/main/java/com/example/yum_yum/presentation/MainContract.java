package com.example.yum_yum.presentation;

public interface MainContract {
    interface View {
        void navigateToHome();
        void navigateToWelcome();
        void showLoading();
        void hideLoading();
        void showNetworkError();
        void hideNetworkError();
    }

    interface Presenter {
        void checkAppStartDestination();
        void startNetworkMonitoring();
        void onDestinationChanged(int destinationId);
        void onDestroy();
    }
}