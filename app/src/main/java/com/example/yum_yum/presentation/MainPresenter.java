package com.example.yum_yum.presentation;

import android.content.Context;

import com.example.yum_yum.R;
import com.example.yum_yum.data.auth.repository.AuthRepository;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class MainPresenter implements MainContract.Presenter {

    private final MainContract.View view;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final AuthRepository authRepository;
    private final Context context;

    private final List<Integer> offlineFragments = Arrays.asList(
            R.id.favoriteScreen,
            R.id.weaklyMealsScreen
    );

    public MainPresenter(MainContract.View view, Context context) {
        this.view = view;
        this.context = context;
        this.authRepository = new AuthRepository(context);
    }

    @Override
    public void checkAppStartDestination() {
        view.showLoading();
        disposable.add(
                authRepository.isFirstTimeLaunch()
                        .delay(1000, TimeUnit.MILLISECONDS)
                        .subscribeOn(Schedulers.io())
                        .flatMap(isFirstTime -> {
                            if (isFirstTime) {
                                return authRepository.setFirstTimeLaunchComplete()
                                        .andThen(Single.just("WELCOME"));
                            } else {
                                return authRepository.isUserLoggedIn()
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