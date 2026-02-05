package com.example.yum_yum.presentation.home;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yum_yum.R;
import com.example.yum_yum.databinding.FragmentHomeScreenBinding;
import com.example.yum_yum.presentation.model.Meal;

import java.util.List;


public class HomeScreen extends Fragment implements HomeContract.View {

    private HomeContract.Presenter presenter;

    // 1. The backing field (nullable)
    private FragmentHomeScreenBinding _binding;

    // 2. This property is only valid between onCreateView and onDestroyView.
    // It prevents us from having to type "_binding!!" or checks everywhere.
    private FragmentHomeScreenBinding getBinding() {
        return _binding;
    }
    public HomeScreen() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // 3. Inflate using the generated binding class
        _binding = FragmentHomeScreenBinding.inflate(inflater, container, false);
        return _binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new HomePresenterImpl(this);
        presenter.getHomeContent();
    }
    @Override
    public void showLoading() {
    if (_binding != null) {
        }
    }

    @Override
    public void hideLoading() {

    }

    @Override
    public void showDailyMeal(Meal meal) {

    }

    @Override
    public void showDailyCountryMeals(List<Meal> meals, String countryName) {

    }

    @Override
    public void showError(String message) {

    }
}