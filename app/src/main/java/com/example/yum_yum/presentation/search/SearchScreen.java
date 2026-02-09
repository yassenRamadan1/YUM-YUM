package com.example.yum_yum.presentation.search;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
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
import com.example.yum_yum.databinding.FragmentSearchScreenBinding;
import com.example.yum_yum.presentation.model.Category;
import com.example.yum_yum.presentation.model.Meal;
import com.example.yum_yum.presentation.search.searchFilter.FilterBottomSheetFragment;

import java.util.List;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.subjects.PublishSubject;
public class SearchScreen extends Fragment implements SearchContract.View, FilterBottomSheetFragment.FilterListener {

    private FragmentSearchScreenBinding binding;
    private SearchContract.Presenter presenter;
    private SearchResultsAdapter adapter;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final PublishSubject<String> searchSubject = PublishSubject.create();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = new SearchPresenter(requireContext(), this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentSearchScreenBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setupRecyclerView();
        setupSearchInput();
        setupFilterIcon();
        showInitialEmptyState();
        presenter.onViewCreated();
    }

    private void setupRecyclerView() {
        adapter = new SearchResultsAdapter(meal -> presenter.onMealClicked(meal));
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 2);
        binding.recyclerSearchResult.setLayoutManager(layoutManager);
        binding.recyclerSearchResult.setAdapter(adapter);
        binding.recyclerSearchResult.setHasFixedSize(true);
    }

    private void setupSearchInput() {
        Disposable searchDisposable = searchSubject
                .debounce(500, TimeUnit.MILLISECONDS)
                .distinctUntilChanged()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        query -> {
                            presenter.onSearchQueryChanged(query);
                        },
                        throwable -> {
                            showError("Search error: " + throwable.getMessage());
                        }
                );

        disposables.add(searchDisposable);

        binding.etSearchQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchSubject.onNext(s.toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
    }

    private void setupFilterIcon() {
        binding.inputLayoutSearch.setEndIconOnClickListener(v -> {
            presenter.onFilterIconClicked();
        });
    }


    private void showInitialEmptyState() {
        if (binding != null) {
            binding.recyclerSearchResult.setVisibility(View.INVISIBLE);
            binding.tvEmptyState.setVisibility(View.VISIBLE);
            binding.tvEmptyState.setText("Type any meal name to search");
        }
    }


    @Override
    public void showLoading() {
        if (binding != null) {
            binding.loadingIndicatorSearch.setVisibility(View.VISIBLE);
            binding.recyclerSearchResult.setVisibility(View.INVISIBLE);
            binding.tvEmptyState.setVisibility(View.GONE);
        }
    }

    @Override
    public void hideLoading() {
        if (binding != null) {
            binding.loadingIndicatorSearch.setVisibility(View.GONE);
        }
    }

    @Override
    public void showNoInternetError() {
        if (binding != null) {
            binding.loadingIndicatorSearch.setVisibility(View.GONE);
            binding.recyclerSearchResult.setVisibility(View.INVISIBLE);
            binding.tvEmptyState.setVisibility(View.VISIBLE);
            binding.tvEmptyState.setText("No internet connection");

        }
    }

    @Override
    public void showSearchResults(List<Meal> meals) {
        if (binding != null) {
            binding.recyclerSearchResult.setVisibility(View.VISIBLE);
            binding.tvEmptyState.setVisibility(View.GONE);
            adapter.setMeals(meals);
        }
    }

    @Override
    public void showEmptyResults() {
        if (binding != null) {
            binding.recyclerSearchResult.setVisibility(View.INVISIBLE);
            binding.tvEmptyState.setVisibility(View.VISIBLE);
            binding.tvEmptyState.setText("No meals found");
            adapter.clearMeals();
        }
    }

    @Override
    public void showFilterDialog(List<Category> categories, List<String> areas, List<String> ingredients) {
        FilterBottomSheetFragment filterSheet = FilterBottomSheetFragment.newInstance(
                categories,
                areas,
                ingredients
        );
        filterSheet.setFilterListener(this);
        filterSheet.show(getChildFragmentManager(), "FilterBottomSheet");
    }

    @Override
    public void navigateToMealDetails(Meal meal) {
        if (binding != null && meal != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable("meal", meal);

            Navigation.findNavController(binding.getRoot())
                    .navigate(R.id.action_searchScreen_to_mealDetailsScreen, bundle);
        }
    }

    @Override
    public void showError(String message) {
        if (getContext() != null) {
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void clearSearchQuery() {
        if (binding != null) {
            binding.etSearchQuery.setText("");
            showInitialEmptyState();
        }
    }


    @Override
    public void onFiltersApplied(List<String> categories, List<String> areas, List<String> ingredients) {
        presenter.onFiltersApplied(categories, areas, ingredients);
    }

    @Override
    public void onFiltersReset() {
        presenter.onFiltersReset();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        searchSubject.onComplete();
        disposables.clear();
        presenter.onDestroy();
        binding = null;
    }
}