package com.example.yum_yum.presentation.profile;

public interface ProfileContract {
    interface View {
        void showLoading();
        void hideLoading();
        void showUserName(String name);
        void navigateToWelcome();
        void showError(String message);
    }

    interface Presenter {
        void loadUserProfile();
        void logout();
        void onDestroy();
    }
}
