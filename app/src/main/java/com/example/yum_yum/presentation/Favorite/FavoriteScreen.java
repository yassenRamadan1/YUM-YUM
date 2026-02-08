package com.example.yum_yum.presentation.Favorite;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.yum_yum.R;
import com.example.yum_yum.databinding.FragmentFavoriteScreenBinding;
import com.example.yum_yum.presentation.model.Meal;

import java.util.List;

public class FavoriteScreen extends Fragment implements FavoriteContract.View {
    private FragmentFavoriteScreenBinding binding;
    private FavoriteContract.Presenter presenter;
    private FavoriteAdapter adapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFavoriteScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new FavoritePresenter(this, requireContext());
        setupRecyclerView();
        presenter.loadFavoriteMeals();
    }

    private void setupRecyclerView() {
        adapter = new FavoriteAdapter(new FavoriteAdapter.OnFavoriteActionListener() {
            @Override
            public void onRemoveClick(Meal meal) {
                showRemoveConfirmation(meal);
            }

            @Override
            public void onMealClick(Meal meal) {
                presenter.onMealClicked(meal);
            }
        });

        binding.favRecycle.setLayoutManager(new LinearLayoutManager(requireContext()));
        binding.favRecycle.setAdapter(adapter);
    }

    private void showRemoveConfirmation(Meal meal) {
        new AlertDialog.Builder(requireContext())
                .setTitle("Remove from Favorites")
                .setMessage("Are you sure you want to remove " + meal.getName() + " from your favorites?")
                .setPositiveButton("Remove", (dialog, which) -> {
                    presenter.confirmRemoveFavorite(meal);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    @Override
    public void showLoginRequired() {
        binding.favRecycle.setVisibility(View.GONE);
        binding.loadingIndicatorHome.setVisibility(View.GONE);
        binding.textviewAddItemsHere.setVisibility(View.VISIBLE);
        binding.textviewAddItemsHere.setText("You must login to view your favorite meals");
    }

    @Override
    public void showFavoriteMeals(List<Meal> meals) {
        binding.favRecycle.setVisibility(View.VISIBLE);
        binding.loadingIndicatorHome.setVisibility(View.GONE);
        binding.textviewAddItemsHere.setVisibility(View.GONE);
        adapter.setMeals(meals);
    }

    @Override
    public void showEmptyState() {
        binding.favRecycle.setVisibility(View.GONE);
        binding.loadingIndicatorHome.setVisibility(View.GONE);
        binding.textviewAddItemsHere.setVisibility(View.VISIBLE);
        binding.textviewAddItemsHere.setText(R.string.here_you_can_add_your_favourite_recieps);
    }

    @Override
    public void showLoading() {
        binding.loadingIndicatorHome.setVisibility(View.VISIBLE);
        binding.textviewAddItemsHere.setVisibility(View.GONE);
    }

    @Override
    public void hideLoading() {
        binding.loadingIndicatorHome.setVisibility(View.GONE);
    }

    @Override
    public void showMessage(String message) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showError(String error) {
        Toast.makeText(requireContext(), error, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void removeMealFromList(String mealId) {
        adapter.removeMeal(mealId);
        if (adapter.getItemCount() == 0) {
            showEmptyState();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (presenter != null) {
            presenter.onDestroy();
        }
        binding = null;
    }
}