package com.example.yum_yum.presentation.search.searchFilter;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.yum_yum.R;
import com.example.yum_yum.databinding.FragmentFilterBottomSheetBinding;
import com.example.yum_yum.presentation.model.Category;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.ArrayList;
import java.util.List;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment implements FilterContract.View {

    private static final String ARG_CATEGORIES = "categories";
    private static final String ARG_AREAS = "areas";
    private static final String ARG_INGREDIENTS = "ingredients";

    private FragmentFilterBottomSheetBinding binding;
    private FilterListener listener;
    private FilterContract.Presenter presenter;
    public interface FilterListener {
        void onFiltersApplied(List<String> categories, List<String> areas, List<String> ingredients);
        void onFiltersReset();
    }

    public static FilterBottomSheetFragment newInstance(
            List<Category> categories,
            List<String> areas,
            List<String> ingredients
    ) {
        FilterBottomSheetFragment fragment = new FilterBottomSheetFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_CATEGORIES, new ArrayList<>(categories));
        args.putStringArrayList(ARG_AREAS, new ArrayList<>(areas));
        args.putStringArrayList(ARG_INGREDIENTS, new ArrayList<>(ingredients));
        fragment.setArguments(args);
        return fragment;
    }

    public void setFilterListener(FilterListener listener) {
        this.listener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentFilterBottomSheetBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter = new FilterPresenter(this);

        List<Category> categories = null;
        List<String> areas = null;
        List<String> ingredients = null;

        if (getArguments() != null) {
            categories = (List<Category>) getArguments().getSerializable(ARG_CATEGORIES);
            areas = getArguments().getStringArrayList(ARG_AREAS);
            ingredients = getArguments().getStringArrayList(ARG_INGREDIENTS);
        }
        presenter.onViewCreated(categories, areas, ingredients);
        setupButtons();
    }


    @Override
    public void renderCategories(List<Category> categories) {
        binding.chipGroupCategories.removeAllViews();
        for (Category category : categories) {
            Chip chip = createFilterChip(category.getName());
            chip.setOnCheckedChangeListener((v, isChecked) ->
                    presenter.onCategorySelected(category.getName(), isChecked));
            binding.chipGroupCategories.addView(chip);
        }
    }

    @Override
    public void renderAreas(List<String> areas) {
        binding.chipGroupAreas.removeAllViews();
        for (String area : areas) {
            Chip chip = createFilterChip(area);
            chip.setOnCheckedChangeListener((v, isChecked) ->
                    presenter.onAreaSelected(area, isChecked));
            binding.chipGroupAreas.addView(chip);
        }
    }

    @Override
    public void renderIngredients(List<String> ingredients) {
        binding.chipGroupIngredients.removeAllViews();
        for (String ingredient : ingredients) {
            Chip chip = createFilterChip(ingredient);
            // Forward event to Presenter
            chip.setOnCheckedChangeListener((v, isChecked) ->
                    presenter.onIngredientSelected(ingredient, isChecked));
            binding.chipGroupIngredients.addView(chip);
        }
    }

    @Override
    public void applyFiltersAndDismiss(List<String> categories, List<String> areas, List<String> ingredients) {
        if (listener != null) {
            listener.onFiltersApplied(categories, areas, ingredients);
        }
        dismiss();
    }

    @Override
    public void resetFiltersAndDismiss() {
        if (listener != null) {
            listener.onFiltersReset();
        }
        dismiss();
    }

    @Override
    public void clearAllChipSelections() {
        uncheckAllChildren(binding.chipGroupCategories);
        uncheckAllChildren(binding.chipGroupAreas);
        uncheckAllChildren(binding.chipGroupIngredients);
    }


    private void setupButtons() {
        binding.buttonApplyFilters.setOnClickListener(v -> presenter.onApplyClicked());
        binding.buttonResetFilters.setOnClickListener(v -> presenter.onResetClicked());
    }

    private Chip createFilterChip(String text) {
        Chip chip = new Chip(requireContext());
        chip.setText(text);
        chip.setCheckable(true);
        chip.setChipBackgroundColorResource(R.color.chip_background);
        chip.setTextColor(getResources().getColorStateList(R.color.chip_text, null));
        chip.setEnsureMinTouchTargetSize(true);
        return chip;
    }

    private void uncheckAllChildren(ChipGroup chipGroup) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View view = chipGroup.getChildAt(i);
            if (view instanceof Chip) {
                ((Chip) view).setChecked(false);
            }
        }
    }

    @Override
    public void onDestroyView() {
        presenter.onDestroy();
        super.onDestroyView();
        binding = null;
    }
}