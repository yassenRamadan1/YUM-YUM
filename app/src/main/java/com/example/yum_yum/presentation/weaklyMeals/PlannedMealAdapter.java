package com.example.yum_yum.presentation.weaklyMeals;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.yum_yum.R;
import com.example.yum_yum.presentation.model.Meal;
import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;
import java.util.List;

public class PlannedMealAdapter extends RecyclerView.Adapter<PlannedMealAdapter.MealViewHolder> {
    private List<Meal> meals;
    private final OnMealActionListener listener;

    public interface OnMealActionListener {
        void onRemoveClick(Meal meal);
        void onMealClick(Meal meal);
    }

    public PlannedMealAdapter(OnMealActionListener listener) {
        this.meals = new ArrayList<>();
        this.listener = listener;
    }

    public void setMeals(List<Meal> meals) {
        this.meals = meals != null ? meals : new ArrayList<>();
        notifyDataSetChanged();
    }

    public void removeMeal(String mealId) {
        for (int i = 0; i < meals.size(); i++) {
            if (meals.get(i).getId().equals(mealId)) {
                meals.remove(i);
                notifyItemRemoved(i);
                break;
            }
        }
    }

    @NonNull
    @Override
    public MealViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_planned_meal, parent, false);
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
        private final ImageView imageMeal;
        private final TextView textMealName;
        private final TextView textMealCategory;
        private final MaterialButton buttonRemove;

        public MealViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMeal = itemView.findViewById(R.id.image_meal);
            textMealName = itemView.findViewById(R.id.text_meal_name);
            textMealCategory = itemView.findViewById(R.id.text_meal_category);
            buttonRemove = itemView.findViewById(R.id.button_remove);
        }

        public void bind(Meal meal) {
            textMealName.setText(meal.getName());
            textMealCategory.setText(meal.getCategory() + " • " + meal.getArea());

            Glide.with(itemView.getContext())
                    .load(meal.getImageUrl())
                    .placeholder(R.drawable.quick_recipes_image)
                    .into(imageMeal);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMealClick(meal);
                }
            });

            buttonRemove.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveClick(meal);
                }
            });
        }
    }
}
