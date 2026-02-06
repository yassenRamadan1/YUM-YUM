package com.example.yum_yum.presentation.home;

import static com.example.yum_yum.presentation.utils.ImageUtils.loadFlag;
import static com.example.yum_yum.presentation.utils.ImageUtils.loadImage;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.FragmentNavigator;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.yum_yum.R;
import com.example.yum_yum.databinding.FragmentHomeScreenBinding;
import com.example.yum_yum.presentation.model.Meal;
import com.example.yum_yum.presentation.utils.FlagManger;
import com.example.yum_yum.presentation.utils.ImageUtils;

import java.util.List;


public class HomeScreen extends Fragment implements HomeContract.View, OnMealClickListener {

    private HomeContract.Presenter presenter;
    private FragmentHomeScreenBinding _binding;
    private CountryMealsAdapter mealsAdapter;

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
        initRecyclerView();
        presenter.getHomeContent();
    }

    private void initRecyclerView() {
        mealsAdapter = new CountryMealsAdapter(requireContext(), this);

        getBinding().recipesOfCountryRecyclerview.setAdapter(mealsAdapter);
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
        _binding.countryNameTextview.setText(meal.getArea());

        FlagManger flagManger = FlagManger.getInstance();
        String flagUrl = flagManger.getFlagUrl(meal.getArea());
        loadFlag(_binding.flagItemIcon, flagUrl);

        _binding.imageRecipe.setTransitionName("daily_meal_image");
        _binding.mealOfTheDayCardview.setOnClickListener(v -> {
            navigateToDetails(meal, _binding.imageRecipe);
        });
    }
    private void navigateToDetails(Meal meal, ImageView sharedImage) {
        String destinationTransitionName = "shared_meal_image";
        FragmentNavigator.Extras extras = new FragmentNavigator.Extras.Builder()
                .addSharedElement(sharedImage, destinationTransitionName)
                .build();
        Bundle bundle = new Bundle();
        bundle.putSerializable("meal_data", meal);
        NavController navController = Navigation.findNavController(requireView());
        navController.navigate(
                R.id.action_homeScreen_to_mealDetailsScreen,
                bundle,
                null,
                extras
        );
    }
    @Override
    public void showDailyCountryMeals(List<Meal> meals, String countryName) {
        mealsAdapter.setList(meals);
        getBinding().countryOfTheDayNameTextview.setText(countryName);
        FlagManger flagManger = FlagManger.getInstance();
        String flagUrl = flagManger.getFlagUrl(countryName);
        ImageUtils.loadFlag(getBinding().countryOfTheDayImageview, flagUrl);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(this.getActivity(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onDestroy();
            presenter = null;
        }
    }


    @Override
    public void onMealClick(Meal meal, ImageView sharedImageView) {
        navigateToDetails(meal, sharedImageView);
    }
}