package com.example.yum_yum.data.user.repository;

import android.content.Context;

import com.example.yum_yum.data.user.datasource.UserLocalDataSource;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class UserRepository {

    private static UserRepository instance;
    private final UserLocalDataSource localDataSource;

    private UserRepository(Context context) {
        this.localDataSource = new UserLocalDataSource(context);
    }

    public static synchronized UserRepository getInstance(Context context) {
        if (instance == null) {
            instance = new UserRepository(context);
        }
        return instance;
    }

    public Completable saveUser(String name, String email) {
        return Completable.fromAction(() -> {
            localDataSource.saveUserSession(name, email);
        });
    }

    public Completable logout() {
        return Completable.fromAction(localDataSource::clearUserSession);
    }

    public Single<Boolean> isUserLoggedIn() {
        return Single.fromCallable(localDataSource::isUserLoggedIn);
    }

    public Single<Boolean> isFirstTimeLaunch() {
        return Single.fromCallable(localDataSource::isFirstTimeAppOpen);
    }

    public Completable setFirstTimeLaunchComplete() {
        return Completable.fromAction(() -> localDataSource.setFirstTimeAppOpen(false));
    }

    public Single<String> getUserName() {
        return Single.fromCallable(localDataSource::getUserName);
    }
}
