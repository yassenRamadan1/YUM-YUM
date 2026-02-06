package com.example.yum_yum.presentation.home;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yum_yum.databinding.ItemRecipeCardBinding;
import com.example.yum_yum.presentation.model.Meal;
import com.example.yum_yum.presentation.utils.ImageUtils;

import java.util.ArrayList;
import java.util.List;

public class CountryMealsAdapter extends RecyclerView.Adapter<CountryMealsAdapter.MealViewHolder> {

    private List<Meal> meals = new ArrayList<>();
    private final OnMealClickListener listener;
    private Context context;

    public CountryMealsAdapter(Context context, OnMealClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setList(List<Meal> newMeals) {
        this.meals = newMeals;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ItemRecipeCardBinding binding = ItemRecipeCardBinding.inflate(inflater, parent, false);
        return new MealViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull MealViewHolder holder, int position) {
        holder.bind(meals.get(position));
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    class MealViewHolder extends RecyclerView.ViewHolder {
        private final ItemRecipeCardBinding binding;

        public MealViewHolder(@NonNull ItemRecipeCardBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Meal meal) {
            binding.itemNameText.setText(meal.getName());
            ImageUtils.loadImage(binding.itemImage, meal.getImageUrl());
            binding.itemImage.setTransitionName("meal_image_" + meal.getId());
            binding.getRoot().setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMealClick(meal, binding.itemImage);
                }
            });
        }
    }
}
