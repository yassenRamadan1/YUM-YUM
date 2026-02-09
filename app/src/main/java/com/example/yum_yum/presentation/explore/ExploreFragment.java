package com.example.yum_yum.presentation.explore;

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
import com.example.yum_yum.databinding.FragmentExploreBinding;
import com.example.yum_yum.presentation.model.ExploreItem;
import com.google.android.material.tabs.TabLayout;

import java.util.List;

public class ExploreFragment extends Fragment implements ExploreContract.View {

    private FragmentExploreBinding binding;
    private ExploreContract.Presenter presenter;
    private ExploreAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new ExplorePresenter(requireContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = FragmentExploreBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        presenter.attachView(this);
        setupToolbar();
        setupRecyclerView();
        setupTabs();
        presenter.loadData();
    }

    private void setupToolbar() {
        binding.exploreToolbar.setNavigationOnClickListener(v ->
                Navigation.findNavController(v).navigateUp());
    }

    private void setupRecyclerView() {
        adapter = new ExploreAdapter(item -> presenter.onItemClicked(item));
        binding.exploreRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.exploreRecyclerView.setAdapter(adapter);
        binding.exploreRecyclerView.setHasFixedSize(true);
    }

    private void setupTabs() {
        binding.exploreTabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                presenter.onTabSelected(position);
                GridLayoutManager layoutManager;
                if (position == 0) {
                    layoutManager = new GridLayoutManager(requireContext(), 2);
                } else {
                    layoutManager = new GridLayoutManager(requireContext(), 3);
                }
                binding.exploreRecyclerView.setLayoutManager(layoutManager);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    @Override
    public void showItems(List<ExploreItem> items) {
        if (binding != null) {
            adapter.setItems(items);
        }
    }

    @Override
    public void showLoading() {
        if (binding != null) {
            binding.exploreLoadingIndicator.setVisibility(View.VISIBLE);
            binding.exploreRecyclerView.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideLoading() {
        if (binding != null) {
            binding.exploreLoadingIndicator.setVisibility(View.GONE);
            binding.exploreRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void navigateToFilteredMeals(String filterType, String filterValue) {
        if (binding != null) {
            Bundle bundle = new Bundle();
            bundle.putString("filter_type", filterType);
            bundle.putString("filter_value", filterValue);
            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_exploreFragment_to_filteredMealsFragment, bundle);
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
