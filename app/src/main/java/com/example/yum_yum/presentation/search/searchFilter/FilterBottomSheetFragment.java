package com.example.yum_yum.presentation.search.searchFilter;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.yum_yum.R;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.yum_yum.databinding.FragmentFilterBottomSheetBinding;
import com.example.yum_yum.presentation.model.Category;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.chip.Chip;

import java.util.ArrayList;
import java.util.List;

public class FilterBottomSheetFragment extends BottomSheetDialogFragment {

    private static final String ARG_CATEGORIES = "categories";
    private static final String ARG_AREAS = "areas";
    private static final String ARG_INGREDIENTS = "ingredients";

    private FragmentFilterBottomSheetBinding binding;
    private FilterListener listener;

    private List<Category> categories;
    private List<String> areas;
    private List<String> ingredients;

    private final List<String> selectedCategories = new ArrayList<>();
    private final List<String> selectedAreas = new ArrayList<>();
    private final List<String> selectedIngredients = new ArrayList<>();

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            categories = (List<Category>) getArguments().getSerializable(ARG_CATEGORIES);
            areas = getArguments().getStringArrayList(ARG_AREAS);
            ingredients = getArguments().getStringArrayList(ARG_INGREDIENTS);
        }
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

        setupCategories();
        setupAreas();
        setupIngredients();
        setupButtons();
    }

    private void setupCategories() {
        binding.chipGroupCategories.removeAllViews();

        if (categories != null) {
            for (Category category : categories) {
                Chip chip = createFilterChip(category.getName());
                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedCategories.add(category.getName());
                    } else {
                        selectedCategories.remove(category.getName());
                    }
                });
                binding.chipGroupCategories.addView(chip);
            }
        }
    }

    private void setupAreas() {
        binding.chipGroupAreas.removeAllViews();

        if (areas != null) {
            for (String area : areas) {
                Chip chip = createFilterChip(area);
                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedAreas.add(area);
                    } else {
                        selectedAreas.remove(area);
                    }
                });
                binding.chipGroupAreas.addView(chip);
            }
        }
    }

    private void setupIngredients() {
        binding.chipGroupIngredients.removeAllViews();
        if (ingredients != null) {
            int maxIngredients = Math.min(ingredients.size(), 30);
            for (int i = 0; i < maxIngredients; i++) {
                String ingredient = ingredients.get(i);
                Chip chip = createFilterChip(ingredient);
                chip.setOnCheckedChangeListener((buttonView, isChecked) -> {
                    if (isChecked) {
                        selectedIngredients.add(ingredient);
                    } else {
                        selectedIngredients.remove(ingredient);
                    }
                });
                binding.chipGroupIngredients.addView(chip);
            }
        }
    }

    private void setupButtons() {
        binding.buttonApplyFilters.setOnClickListener(v -> {
            if (listener != null) {
                listener.onFiltersApplied(
                        new ArrayList<>(selectedCategories),
                        new ArrayList<>(selectedAreas),
                        new ArrayList<>(selectedIngredients)
                );
            }
            dismiss();
        });

        binding.buttonResetFilters.setOnClickListener(v -> {
            clearAllSelections();
            if (listener != null) {
                listener.onFiltersReset();
            }
            dismiss();
        });
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

    private void clearAllSelections() {
        selectedCategories.clear();
        selectedAreas.clear();
        selectedIngredients.clear();

        clearChipGroup(binding.chipGroupCategories);
        clearChipGroup(binding.chipGroupAreas);
        clearChipGroup(binding.chipGroupIngredients);
    }

    private void clearChipGroup(com.google.android.material.chip.ChipGroup chipGroup) {
        for (int i = 0; i < chipGroup.getChildCount(); i++) {
            View view = chipGroup.getChildAt(i);
            if (view instanceof Chip) {
                ((Chip) view).setChecked(false);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}