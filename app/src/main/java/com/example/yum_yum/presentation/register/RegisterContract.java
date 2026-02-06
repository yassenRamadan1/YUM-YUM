package com.example.yum_yum.presentation.register;

public interface RegisterContract {
    interface View {
        void showLoading();
        void hideLoading();
        void showError(String message);
        void setEmailError(String error);
        void setPasswordError(String error);
        void launchGoogleSignIn(String webClientId);
        void setUsernameError(String error);
        void navigateToHome();
    }

    interface Presenter {
        void register(String username, String email, String password);
        void onGoogleSignInClicked();

        void onGoogleSignInSuccess(String idToken);

        void onGoogleSignInFailure(Exception e);
        void onDestroy();
    }
}
