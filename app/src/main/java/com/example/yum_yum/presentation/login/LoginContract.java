package com.example.yum_yum.presentation.login;

public interface LoginContract {
    interface View {
        void showLoading();
        void hideLoading();
        void showError(String message);
        void setEmailError(String error);
        void setPasswordError(String error);
        void showGoogleSignInError(String message);
        void launchGoogleSignIn(String webClientId);
        void navigateToHome();
    }

    interface Presenter {
        void login(String email, String password);
        void onGoogleSignInClicked();

        void onGoogleSignInSuccess(String idToken);

        void onGoogleSignInFailure(Exception e);
        void onDestroy();
    }
}