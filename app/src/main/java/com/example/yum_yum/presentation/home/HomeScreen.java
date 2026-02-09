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

import java.util.List;


public class HomeScreen extends Fragment implements HomeContract.View, OnMealClickListener {
    private FragmentHomeScreenBinding binding;
    private HomeContract.Presenter presenter;
    private CountryMealsAdapter mealsAdapter;

    public HomeScreen() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = HomePresenterImpl.getInstance(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
        initRecyclerView();
        presenter.getHomeContent();

        binding.exploreMoreCard.setOnClickListener(v ->
                Navigation.findNavController(v).navigate(R.id.action_homeScreen_to_exploreFragment));

        binding.avatarImageView.setOnClickListener(v -> {
            if(binding.usernameTextview.getText().toString().isEmpty() || binding.usernameTextview.getText().toString().equals("Guest")) {
                Toast.makeText(getContext(), "Please log in to view your profile", Toast.LENGTH_SHORT).show();
                return;
            }
            Navigation.findNavController(v).navigate(R.id.action_homeScreen_to_profileFragment);
        });

        presenter.startNetworkMonitoring(requireContext());
    }

    private void initRecyclerView() {
        mealsAdapter = new CountryMealsAdapter(requireContext(), this);
        binding.recipesOfCountryRecyclerview.setAdapter(mealsAdapter);
    }

    @Override
    public void showLoading() {
        if (binding != null) {
            binding.contentScrollView.setVisibility(View.GONE);
            binding.loadingIndicatorHome.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideLoading() {
        if (binding != null) {
            binding.contentScrollView.setVisibility(View.VISIBLE);
            binding.loadingIndicatorHome.setVisibility(View.GONE);
        }
    }

    @Override
    public void showDailyMeal(Meal meal) {
        if (binding == null) return;

        loadImage(binding.imageRecipe, meal.getImageUrl());
        binding.textRecipeName.setText(meal.getName());
        binding.countryNameTextview.setText(meal.getArea());

        FlagManger flagManger = FlagManger.getInstance();
        String flagUrl = flagManger.getFlagUrl(meal.getArea());
        loadFlag(binding.flagItemIcon, flagUrl);

        binding.imageRecipe.setTransitionName("daily_meal_image");
        binding.mealOfTheDayCardview.setOnClickListener(v -> {
            navigateToDetails(meal, binding.imageRecipe);
        });
    }

    @Override
    public void showDailyCountryMeals(List<Meal> meals, String countryName) {
        if (binding == null) return;

        mealsAdapter.setList(meals);
        binding.countryOfTheDayNameTextview.setText(countryName);

        FlagManger flagManger = FlagManger.getInstance();
        String flagUrl = flagManger.getFlagUrl(countryName);
        loadImage(binding.countryOfTheDayImageview, flagUrl);
    }

    @Override
    public void showError(String message) {
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showUserName(String name) {
        if (binding != null) {
            binding.usernameTextview.setText(name);
        }
    }

    @Override
    public void showNoInternetError() {
        if (binding != null) {
            binding.noInternetView.getRoot().setVisibility(View.VISIBLE);
            binding.contentScrollView.setVisibility(View.GONE);
            binding.loadingIndicatorHome.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideNoInternetError() {
        if (binding != null) {
            binding.noInternetView.getRoot().setVisibility(View.GONE);
        }
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
    public void onMealClick(Meal meal, ImageView sharedImageView) {
        navigateToDetails(meal, sharedImageView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.stopNetworkMonitoring();
        presenter.detachView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
