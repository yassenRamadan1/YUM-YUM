package com.example.yum_yum.presentation.Favorite;

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

import java.util.ArrayList;
import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder> {
    private List<Meal> meals;
    private final OnFavoriteActionListener listener;

    public interface OnFavoriteActionListener {
        void onRemoveClick(Meal meal);
        void onMealClick(Meal meal);
    }

    public FavoriteAdapter(OnFavoriteActionListener listener) {
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
    public FavoriteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.favorit_item_card, parent, false);
        return new FavoriteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoriteViewHolder holder, int position) {
        holder.bind(meals.get(position));
    }

    @Override
    public int getItemCount() {
        return meals.size();
    }

    class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageMeal;
        private final TextView textRecipeName;
        private final TextView instructionsTextview;
        private final ImageView favouriteIcon;

        public FavoriteViewHolder(@NonNull View itemView) {
            super(itemView);
            imageMeal = itemView.findViewById(R.id.image_meal);
            textRecipeName = itemView.findViewById(R.id.text_recipeName);
            instructionsTextview = itemView.findViewById(R.id.instructions_textview);
            favouriteIcon = itemView.findViewById(R.id.favourite_icon);
        }

        public void bind(Meal meal) {
            textRecipeName.setText(meal.getName());

            String category = meal.getCategory() + " • " + meal.getArea();
            instructionsTextview.setText(category);

            Glide.with(itemView.getContext())
                    .load(meal.getImageUrl())
                    .placeholder(R.drawable.quick_recipes_image)
                    .into(imageMeal);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onMealClick(meal);
                }
            });

            favouriteIcon.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onRemoveClick(meal);
                }
            });
        }
    }
}