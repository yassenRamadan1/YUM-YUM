package com.example.yum_yum.presentation;

import com.example.yum_yum.data.user.repository.UserRepository;

import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainPresenter implements MainContract.Presenter {

    private final MainContract.View view;
    private final UserRepository userRepository;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public MainPresenter(MainContract.View view, UserRepository userRepository) {
        this.view = view;
        this.userRepository = userRepository;
    }

    @Override
    public void checkAppStartDestination() {
        view.showLoading();
        disposable.add(
                userRepository.isFirstTimeLaunch()
                        .delay(1000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .flatMap(isFirstTime -> {
                            if (isFirstTime) {
                                return userRepository.setFirstTimeLaunchComplete()
                                        .andThen(Single.just("WELCOME"));
                            } else {
                                return userRepository.isUserLoggedIn()
                                        .map(isLoggedIn -> isLoggedIn ? "HOME" : "WELCOME");
                            }
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(destination -> {
                            if (destination.equals("HOME")) {
                                view.navigateToHome();
                            } else {
                                view.navigateToWelcome();
                            }
                            view.hideLoading();

                        }, throwable -> {
                            view.navigateToWelcome();
                            view.hideLoading();
                        })
        );
    }

    @Override
    public void onDestroy() {
        disposable.clear();
    }
}