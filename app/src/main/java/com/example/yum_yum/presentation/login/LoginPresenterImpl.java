package com.example.yum_yum.presentation.login;

import android.content.Context;
import android.text.TextUtils;
import android.util.Patterns;

import com.example.yum_yum.data.auth.repository.AuthRepository;
import com.example.yum_yum.data.user.repository.UserRepository;
import com.google.firebase.auth.FirebaseAuth;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginPresenterImpl implements LoginContract.Presenter {
    private LoginContract.View view;
    private AuthRepository repository;
    private CompositeDisposable disposable;
    private UserRepository userRepository;

    public LoginPresenterImpl(LoginContract.View view, Context context) {
        this.view = view;
        this.repository = new AuthRepository();
        this.disposable = new CompositeDisposable();
        this.userRepository = UserRepository.getInstance(context);
    }

    @Override
    public void login(String email, String password) {
        boolean isValid = true;

        if (TextUtils.isEmpty(email) || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            view.setEmailError("Please enter a valid email address");
            isValid = false;
        } else {
            view.setEmailError(null);
        }

        if (TextUtils.isEmpty(password) || password.length() < 6) {
            view.setPasswordError("Password must be at least 6 characters");
            isValid = false;
        } else {
            view.setPasswordError(null);
        }

        if (!isValid) return;
        view.showLoading();
        disposable.add(
                repository.loginUser(email, password)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    userRepository.saveUser(email.split("@")[0], email)
                                            .subscribeOn(Schedulers.io())
                                            .observeOn(AndroidSchedulers.mainThread())
                                            .subscribe(() -> {
                                                view.hideLoading();
                                                view.navigateToHome();
                                            }, throwable -> {
                                                view.hideLoading();
                                                view.showError("Failed to save user data");
                                            });
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
                        .subscribe(() -> {
                            repository.getCurrentUser()
                                    .flatMapCompletable(name -> {
                                        String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                                        return userRepository.saveUser(name, email);
                                    })
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(AndroidSchedulers.mainThread())
                                    .subscribe(() -> {
                                        view.hideLoading();
                                        view.navigateToHome();
                                    }, err -> {
                                        view.hideLoading();
                                        view.showError("Failed to save user data");
                                    });
                        }, error -> {
                            view.hideLoading();
                            view.showError(error.getMessage());
                        })
        );
    }
    @Override
    public void onGoogleSignInFailure(Exception e) {
        view.hideLoading();
        view.showError("Google Sign In Cancelled or Failed");
    }
    @Override
    public void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
