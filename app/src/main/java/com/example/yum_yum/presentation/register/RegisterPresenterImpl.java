package com.example.yum_yum.presentation.register;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;

import com.example.yum_yum.data.auth.repository.AuthRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class RegisterPresenterImpl implements RegisterContract.Presenter {
    private final RegisterContract.View view;
    private final AuthRepository repository;
    private final CompositeDisposable disposable;

    public RegisterPresenterImpl(RegisterContract.View view, Context context) {
        this.view = view;
        this.repository = new AuthRepository(context);
        this.disposable = new CompositeDisposable();
    }

    @Override
    public void register(String username, String email, String password) {
        boolean isValid = true;

        if (TextUtils.isEmpty(username)) {
            view.setUsernameError("Username is required");
            isValid = false;
        } else {
            view.setUsernameError(null);
        }

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.setEmailError("Invalid email");
            isValid = false;
        } else {
            view.setEmailError(null);
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            view.setPasswordError("Password must be 6+ chars");
            isValid = false;
        } else {
            view.setPasswordError(null);
        }

        if (!isValid) return;

        view.showLoading();
        disposable.add(
                repository.registerUser(username, email, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    view.hideLoading();
                                    view.navigateToHome();
                                },
                                throwable -> {
                                    view.hideLoading();
                                    view.showError(throwable.getMessage());
                                }
                        )
        );
    }

    @Override
    public void onGoogleSignInClicked() {
        view.launchGoogleSignIn("WEB_CLIENT_ID_FROM_GOOGLE_SERVICES");
    }

    @Override
    public void onGoogleSignInSuccess(String idToken) {
        view.showLoading();
        disposable.add(
                repository.signInWithGoogle(idToken)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    view.hideLoading();
                                    view.navigateToHome();
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError(error.getMessage());
                                }
                        )
        );
    }

    @Override
    public void onGoogleSignInFailure(Exception e) {
        view.hideLoading();
        view.showError("Google Sign In Cancelled or Failed");
    }

    @Override
    public void onDestroy() {
        disposable.dispose();
    }
}