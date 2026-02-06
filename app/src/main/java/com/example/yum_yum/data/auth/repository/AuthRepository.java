package com.example.yum_yum.data.auth.repository;

import com.example.yum_yum.data.auth.datasource.AuthRemoteDataSource;

import io.reactivex.rxjava3.core.Completable;

public class AuthRepository {
    private final AuthRemoteDataSource remoteDataSource;

    public AuthRepository() {
        this.remoteDataSource = new AuthRemoteDataSource();
    }

    public Completable loginUser(String email, String password) {
        return remoteDataSource.login(email, password);
    }

    public Completable registerUser(String email, String password) {
        return remoteDataSource.register(email, password);
    }
}