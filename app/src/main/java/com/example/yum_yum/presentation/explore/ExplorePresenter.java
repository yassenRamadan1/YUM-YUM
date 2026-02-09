package com.example.yum_yum.presentation.explore;

import android.content.Context;

import com.example.yum_yum.data.meals.repository.MealsRepository;
import com.example.yum_yum.presentation.model.Category;
import com.example.yum_yum.presentation.model.ExploreItem;
import com.example.yum_yum.presentation.utils.FlagManger;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class ExplorePresenter implements ExploreContract.Presenter {

    private ExploreContract.View view;
    private final MealsRepository repository;
    private final CompositeDisposable disposables = new CompositeDisposable();

    private List<ExploreItem> categoryItems = new ArrayList<>();
    private List<ExploreItem> areaItems = new ArrayList<>();
    private List<ExploreItem> ingredientItems = new ArrayList<>();
    private int currentTab = 0;

    private static final String TYPE_CATEGORY = "category";
    private static final String TYPE_AREA = "area";
    private static final String TYPE_INGREDIENT = "ingredient";

    public ExplorePresenter(Context context) {
        this.repository = new MealsRepository(context);
    }

    @Override
    public void attachView(ExploreContract.View view) {
        this.view = view;
    }

    @Override
    public void detachView() {
        this.view = null;
    }

    @Override
    public void loadData() {
        if (view == null) return;

        if (!categoryItems.isEmpty()) {
            showCurrentTab();
            return;
        }

        view.showLoading();

        disposables.add(
                Single.zip(
                        repository.getAllCategories(),
                        repository.getAllAreas(),
                        repository.getAllIngredients(),
                        (categories, areas, ingredients) -> {
                            categoryItems = mapCategories(categories);
                            areaItems = mapAreas(areas);
                            ingredientItems = mapIngredients(ingredients);
                            return true;
                        }
                )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        success -> {
                            if (view != null) {
                                view.hideLoading();
                                showCurrentTab();
                            }
                        },
                        error -> {
                            if (view != null) {
                                view.hideLoading();
                                view.showError(error.getMessage());
                            }
                        }
                )
        );
    }

    private List<ExploreItem> mapCategories(List<Category> categories) {
        List<ExploreItem> items = new ArrayList<>();
        for (Category category : categories) {
            items.add(new ExploreItem(category.getName(), category.getImageUrl()));
        }
        return items;
    }

    private List<ExploreItem> mapAreas(List<String> areas) {
        List<ExploreItem> items = new ArrayList<>();
        FlagManger flagManger = FlagManger.getInstance();
        for (String area : areas) {
            items.add(new ExploreItem(area, flagManger.getFlagUrl(area)));
        }
        return items;
    }

    private List<ExploreItem> mapIngredients(List<String> ingredients) {
        List<ExploreItem> items = new ArrayList<>();
        for (String ingredient : ingredients) {
            String imageUrl = "https://www.themealdb.com/images/ingredients/" + ingredient + ".png";
            items.add(new ExploreItem(ingredient, imageUrl));
        }
        return items;
    }

    @Override
    public void onTabSelected(int tabIndex) {
        currentTab = tabIndex;
        showCurrentTab();
    }

    private void showCurrentTab() {
        if (view == null) return;
        switch (currentTab) {
            case 0:
                view.showItems(categoryItems);
                break;
            case 1:
                view.showItems(areaItems);
                break;
            case 2:
                view.showItems(ingredientItems);
                break;
        }
    }

    @Override
    public void onItemClicked(ExploreItem item) {
        if (view == null) return;
        String filterType;
        switch (currentTab) {
            case 1:
                filterType = TYPE_AREA;
                break;
            case 2:
                filterType = TYPE_INGREDIENT;
                break;
            default:
                filterType = TYPE_CATEGORY;
                break;
        }
        view.navigateToFilteredMeals(filterType, item.getName());
    }

    @Override
    public void onDestroy() {
        disposables.clear();
        view = null;
    }
}
