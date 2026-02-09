package com.example.yum_yum.presentation.explore;

import com.example.yum_yum.presentation.model.ExploreItem;

import java.util.List;

public interface ExploreContract {

    interface View {
        void showItems(List<ExploreItem> items);
        void showLoading();
        void hideLoading();
        void showError(String message);
        void navigateToFilteredMeals(String filterType, String filterValue);
    }

    interface Presenter {
        void attachView(View view);
        void detachView();
        void loadData();
        void onTabSelected(int tabIndex);
        void onItemClicked(ExploreItem item);
        void onDestroy();
    }
}
