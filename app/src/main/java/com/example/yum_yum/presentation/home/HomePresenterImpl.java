package com.example.yum_yum.presentation.home;

import static io.reactivex.rxjava3.android.schedulers.AndroidSchedulers.mainThread;

import com.example.yum_yum.data.meals.repository.MealsRepository;

import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class HomePresenterImpl implements HomeContract.Presenter {
    private HomeContract.View _homeView;
    private final MealsRepository mealsRepository;
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    public HomePresenterImpl(HomeContract.View homeView) {
        _homeView = homeView;
        mealsRepository = new MealsRepository();
    }

    @Override
    public void getHomeContent() {
        _homeView.showLoading();
        compositeDisposable.add(
                mealsRepository.getRandomMeal()
                        .subscribeOn(Schedulers.io())
                        .observeOn(mainThread())
                        .subscribe(
                                meal -> {
                                    _homeView.showDailyMeal(meal);
                                    _homeView.hideLoading();
                                },
                                error -> {
                                    _homeView.showError(error.getMessage());
                                    _homeView.hideLoading();
                                }
                        )
        );
    }

    @Override
    public void onDestroy() {
        compositeDisposable.clear();
        _homeView = null;
    }
}
