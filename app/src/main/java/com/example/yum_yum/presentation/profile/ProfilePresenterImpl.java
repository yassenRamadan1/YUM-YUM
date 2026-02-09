package com.example.yum_yum.presentation.profile;

import android.content.Context;

import com.example.yum_yum.data.auth.repository.AuthRepository;
import com.example.yum_yum.data.meals.repository.MealsRepository;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ProfilePresenterImpl implements ProfileContract.Presenter {

    private ProfileContract.View view;
    private final AuthRepository authRepository;
    private final MealsRepository mealsRepository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public ProfilePresenterImpl(ProfileContract.View view, Context context) {
        this.view = view;
        this.authRepository = new AuthRepository(context);
        this.mealsRepository = new MealsRepository(context);
    }

    @Override
    public void loadUserProfile() {
        disposables.add(
                authRepository.getCurrentUserName()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                name -> view.showUserName(name),
                                error -> view.showUserName("Guest")
                        )
        );
    }

    @Override
    public void logout() {
        view.showLoading();
        disposables.add(
                authRepository.getCurrentUserUUIDFromLocal()
                        .flatMapCompletable(mealsRepository::clearAllData
                        )
                        .andThen(authRepository.logout())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                () -> {
                                    view.hideLoading();
                                    view.navigateToWelcome();
                                },
                                error -> {
                                    view.hideLoading();
                                    view.showError("Logout failed: " + error.getMessage());
                                }
                        )
        );
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        view = null;
    }
}