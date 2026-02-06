package com.example.yum_yum.presentation.home;

import static com.example.yum_yum.presentation.utils.ImageUtils.loadImage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.yum_yum.R;
import com.example.yum_yum.databinding.FragmentHomeScreenBinding;
import com.example.yum_yum.presentation.model.Meal;
import com.example.yum_yum.presentation.utils.FlagManger;

import java.util.List;


public class HomeScreen extends Fragment implements HomeContract.View {

    private HomeContract.Presenter presenter;
    private FragmentHomeScreenBinding _binding;

    private FragmentHomeScreenBinding getBinding() {
        return _binding;
    }

    public HomeScreen() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
            _binding.contentScrollView.setVisibility(View.GONE);
            _binding.loadingIndicatorHome.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideLoading() {
        _binding.contentScrollView.setVisibility(View.VISIBLE);
        _binding.loadingIndicatorHome.setVisibility(View.GONE);
    }

    @Override
    public void showDailyMeal(Meal meal) {
        loadImage(_binding.imageRecipe, meal.getImageUrl());
        _binding.textRecipeName.setText(meal.getName());
        FlagManger flagManger = FlagManger.getInstance();
        int flagRes = flagManger.getFlagDrawableId(getActivity().getApplicationContext(), meal.getArea());
        _binding.flagItemIcon.setImageResource(flagRes);
        _binding.countryNameTextview.setText(meal.getArea());
    }

    @Override
    public void showDailyCountryMeals(List<Meal> meals, String countryName) {

    }

    @Override
    public void showError(String message) {
        Toast.makeText(this.getActivity(),message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        _binding = null;
    }

}