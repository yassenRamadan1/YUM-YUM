package com.example.yum_yum.presentation.home;

import static io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread;

import android.content.Context;

import com.example.yum_yum.data.auth.repository.AuthRepository;
import com.example.yum_yum.data.meals.dto.HomeContentData;
import com.example.yum_yum.data.meals.repository.MealsRepository;
import com.example.yum_yum.presentation.utils.NetworkUtil;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomePresenterImpl implements HomeContract.Presenter {
    private static HomePresenterImpl INSTANCE = null;
    private HomeContract.View view;
    private final MealsRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private HomeContentData cachedHomeContent;
    private String currentUserName;
    private final AuthRepository authRepository;
    private boolean isLoading = false;
    private Disposable networkDisposable;
    private boolean wasDisconnected = false;

    private HomePresenterImpl(Context context) {
        this.repository = new MealsRepository(context.getApplicationContext());
        this.authRepository = new AuthRepository(context.getApplicationContext());
    }

    public static HomePresenterImpl getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (HomePresenterImpl.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HomePresenterImpl(context.getApplicationContext());
                }
            }
        }
        return INSTANCE;
    }

    public static void resetInstance() {
        if (INSTANCE != null) {
            INSTANCE.onDestroy();
            INSTANCE = null;
        }
    }

    @Override
    public void attachView(HomeContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void getHomeContent() {
        if (cachedHomeContent != null && !isLoading) {
            if (view != null) {
                view.showDailyMeal(cachedHomeContent.getDailyMeal());
                view.showDailyCountryMeals(
                        cachedHomeContent.getCountryMeals(),
                        cachedHomeContent.getCountryName()
                );
                view.showUserName(currentUserName);
                view.hideLoading();
            }
            return;
        }
        if (isLoading) {
            return;
        }
        isLoading = true;
        if (view != null) {
            view.showLoading();
        }
        disposables.add(
                repository.getHomeContent()
                        .subscribeOn(Schedulers.io())
                        .observeOn(mainThread())
                        .subscribe(
                                homeContent -> {
                                    this.cachedHomeContent = homeContent;
                                    this.isLoading = false;
                                    if (view != null) {
                                        view.showDailyMeal(homeContent.getDailyMeal());
                                        view.showDailyCountryMeals(
                                                homeContent.getCountryMeals(),
                                                homeContent.getCountryName()
                                        );
                                        view.hideLoading();
                                    }
                                },
                                error -> {
                                    this.isLoading = false;
                                    if (view != null) {
                                        view.showError(error.getMessage());
                                        view.hideLoading();
                                    }
                                }
                        )
        );
        disposables.add(
                authRepository.getCurrentUserName()
                        .subscribeOn(Schedulers.io())
                        .observeOn(mainThread())
                        .subscribe(
                                name -> {
                                    if (view != null) {
                                        currentUserName = name;
                                        view.showUserName(name);
                                    };
                                },
                                error -> {
                                    view.showError("cant return user name");
                                }
                        )
        );
    }

    @Override
    public void refreshHomeContent() {
        cachedHomeContent = null;
        if (view != null) {
            view.showLoading();
        }
        isLoading = true;
        disposables.add(
                repository.refreshHomeContent()
                        .subscribeOn(Schedulers.io())
                        .observeOn(mainThread())
                        .subscribe(
                                homeContent -> {
                                    this.cachedHomeContent = homeContent;
                                    this.isLoading = false;

                                    if (view != null) {
                                        view.showDailyMeal(homeContent.getDailyMeal());
                                        view.showDailyCountryMeals(
                                                homeContent.getCountryMeals(),
                                                homeContent.getCountryName()
                                        );
                                        view.hideLoading();
                                    }
                                },
                                error -> {
                                    this.isLoading = false;

                                    if (view != null) {
                                        view.showError(error.getMessage());
                                        view.hideLoading();
                                    }
                                }
                        )
        );
    }

    @Override
    public void startNetworkMonitoring(Context context) {
        stopNetworkMonitoring();
        wasDisconnected = false;
        networkDisposable = NetworkUtil.getNetworkStatusObservable(context)
                .distinctUntilChanged()
                .observeOn(mainThread())
                .subscribe(
                        isConnected -> {
                            if (isConnected) {
                                if (view != null) {
                                    view.hideNoInternetError();
                                }
                                if (wasDisconnected) {
                                    wasDisconnected = false;
                                    refreshHomeContent();
                                }
                            } else {
                                wasDisconnected = true;
                                if (view != null) {
                                    view.showNoInternetError();
                                }
                            }
                        },
                        throwable -> { }
                );
    }

    @Override
    public void stopNetworkMonitoring() {
        if (networkDisposable != null && !networkDisposable.isDisposed()) {
            networkDisposable.dispose();
            networkDisposable = null;
        }
    }

    public void clearMemoryCache() {
        cachedHomeContent = null;
    }

    public boolean hasDataCached() {
        return cachedHomeContent != null;
    }

    @Override
    public void onDestroy() {
        stopNetworkMonitoring();
        disposables.clear();
        view = null;
        cachedHomeContent = null;
        isLoading = false;
    }
}
