package com.example.yum_yum.presentation.login;

import android.text.TextUtils;
import android.util.Patterns;

import com.example.yum_yum.data.auth.repository.AuthRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class LoginPresenterImpl implements LoginContract.Presenter{
    private LoginContract.View view;
    private AuthRepository repository;
    private CompositeDisposable disposable;

    public LoginPresenterImpl(LoginContract.View view) {
        this.view = view;
        this.repository = new AuthRepository();
        this.disposable = new CompositeDisposable();
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
    public void onDestroy() {
        if (disposable != null && !disposable.isDisposed()) {
            disposable.dispose();
        }
    }
}
