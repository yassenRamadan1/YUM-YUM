package com.example.yum_yum.data.auth.repository;

import android.content.Context;

import com.example.yum_yum.data.auth.datasource.AuthRemoteDataSource;
import com.example.yum_yum.data.auth.datasource.UserLocalDataSource;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class AuthRepository {
    private final AuthRemoteDataSource remoteDataSource;
    private final UserLocalDataSource localDataSource;

    public AuthRepository(Context context) {
        this.remoteDataSource = new AuthRemoteDataSource();
        this.localDataSource = new UserLocalDataSource(context);
    }

    public Completable loginUser(String email, String password) {
        return remoteDataSource.login(email, password)
                .andThen(remoteDataSource.getCurrentUserUuid())
                .flatMapCompletable(uuid -> {
                    String name = remoteDataSource.getCurrentUserName().blockingGet();
                    if (name.equals("User")) name = email.split("@")[0];

                    return saveUserSession(name, email, uuid);
                });
    }

    public Completable registerUser(String username, String email, String password) {
        return remoteDataSource.register(email, password)
                .andThen(remoteDataSource.getCurrentUserUuid())
                .flatMapCompletable(uuid -> saveUserSession(username, email, uuid));
    }

    public Completable signInWithGoogle(String idToken) {
        return remoteDataSource.firebaseAuthWithGoogle(idToken)
                .andThen(Single.zip(
                        remoteDataSource.getCurrentUserUuid(),
                        remoteDataSource.getCurrentUserName(),
                        remoteDataSource.getCurrentUserEmail(),
                        (uuid, name, email) -> new String[]{uuid, name, email}
                ))
                .flatMapCompletable(data -> saveUserSession(data[1], data[2], data[0]));
    }

    private Completable saveUserSession(String name, String email, String uuid) {
        return Completable.fromAction(() -> {
            localDataSource.saveUserSession(name, email, uuid);
        });
    }

    public Single<String> getCurrentUserUUID() {
        return remoteDataSource.getCurrentUserUuid();
    }

    public Single<String> getCurrentUserUUIDFromLocal() {
        return Single.fromCallable(localDataSource::getUserUuid);
    }

    public Completable logout() {
        return Completable.fromAction(() -> {
            localDataSource.clearUserSession();
            remoteDataSource.logout().subscribe();
        });
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
    public Single<String> getCurrentUserName() {
        return Single.fromCallable(localDataSource::getUserName);
    }
}