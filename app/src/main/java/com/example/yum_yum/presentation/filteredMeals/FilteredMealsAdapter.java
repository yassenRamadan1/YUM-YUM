package com.example.yum_yum.presentation.filteredMeals;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.example.yum_yum.R;
import com.example.yum_yum.presentation.model.Meal;

import java.util.ArrayList;
import java.util.List;

public class FilteredMealsAdapter extends RecyclerView.Adapter<FilteredMealsAdapter.MealViewHolder> {

    private List<Meal> meals = new ArrayList<>();
    private final OnMealClickListener listener;

    public interface OnMealClickListener {
        void onMealClick(Meal meal);
    }

    public FilteredMealsAdapter(OnMealClickListener listener) {
        this.listener = listener;
    }

    public void setMeals(List<Meal> newMeals) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new MealDiffCallback(this.meals, newMeals));
        this.meals = new ArrayList<>(newMeals);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_search_result, parent, false);
        return new MealViewHolder(view);
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
        private final ImageView imageRecipe;
        private final TextView textRecipeName;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            imageRecipe = itemView.findViewById(R.id.image_recipe);
            textRecipeName = itemView.findViewById(R.id.text_recipe_name);
        }

        public void bind(Meal meal) {
            textRecipeName.setText(meal.getName());
            Glide.with(itemView.getContext())
                    .load(meal.getImageUrl())
                    .placeholder(R.drawable.quick_recipes_image)
                    .error(R.drawable.quick_recipes_image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(imageRecipe);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMealClick(meal);
                }
            });
        }
    }

    private static class MealDiffCallback extends DiffUtil.Callback {
        private final List<Meal> oldList;
        private final List<Meal> newList;

        public MealDiffCallback(List<Meal> oldList, List<Meal> newList) {
            this.oldList = oldList;
            this.newList = newList;
        }

        @Override
        public int getOldListSize() {
            return oldList.size();
        }

        @Override
        public int getNewListSize() {
            return newList.size();
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).getId()
                    .equals(newList.get(newItemPosition).getId());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            Meal oldMeal = oldList.get(oldItemPosition);
            Meal newMeal = newList.get(newItemPosition);
            return oldMeal.getId().equals(newMeal.getId()) &&
                    oldMeal.getName().equals(newMeal.getName()) &&
                    oldMeal.getImageUrl().equals(newMeal.getImageUrl());
        }
    }
}
