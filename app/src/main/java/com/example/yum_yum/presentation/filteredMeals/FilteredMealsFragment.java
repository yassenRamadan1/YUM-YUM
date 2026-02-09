package com.example.yum_yum.presentation.filteredMeals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;

import com.example.yum_yum.R;
import com.example.yum_yum.databinding.FragmentFilteredMealsBinding;
import com.example.yum_yum.presentation.model.Meal;

import java.util.List;

public class FilteredMealsFragment extends Fragment implements FilteredMealsContract.View {

    private FragmentFilteredMealsBinding binding;
    private FilteredMealsContract.Presenter presenter;
    private FilteredMealsAdapter adapter;
    private String filterType;
    private String filterValue;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new FilteredMealsPresenter(requireContext());
        if (getArguments() != null) {
            filterType = getArguments().getString("filter_type", "category");
            filterValue = getArguments().getString("filter_value", "");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentFilteredMealsBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
        setupToolbar();
        setupRecyclerView();
        presenter.loadMeals(filterType, filterValue);
    }

    private void setupToolbar() {
        binding.filteredMealsToolbar.setTitle(filterValue);
        binding.filteredMealsToolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());
    }

    private void setupRecyclerView() {
        adapter = new FilteredMealsAdapter(meal -> presenter.onMealClicked(meal));
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.filteredMealsRecyclerView.setLayoutManager(layoutManager);
        binding.filteredMealsRecyclerView.setAdapter(adapter);
        binding.filteredMealsRecyclerView.setHasFixedSize(true);
    }

    @Override
    public void showMeals(List<Meal> meals) {
        if (binding != null) {
            binding.filteredMealsRecyclerView.setVisibility(View.VISIBLE);
            binding.emptyTextView.setVisibility(View.GONE);
            adapter.setMeals(meals);
        }
    }

    @Override
    public void showLoading() {
        if (binding != null) {
            binding.filteredMealsLoadingIndicator.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideLoading() {
        if (binding != null) {
            binding.filteredMealsLoadingIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void showEmpty() {
        if (binding != null) {
            binding.filteredMealsRecyclerView.setVisibility(View.GONE);
            binding.emptyTextView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void navigateToMealDetails(Meal meal) {
        if (binding != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("meal_data", meal);
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_filteredMealsFragment_to_mealDetailsScreen, bundle);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        presenter.detachView();
        binding = null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.onDestroy();
    }
}
