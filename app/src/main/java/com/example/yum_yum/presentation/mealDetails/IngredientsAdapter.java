package com.example.yum_yum.presentation.mealDetails;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.yum_yum.databinding.ItemIngredientBinding;
import com.example.yum_yum.presentation.model.IngredientItem;
import com.example.yum_yum.presentation.utils.ImageUtils;

import java.util.List;

public class IngredientsAdapter extends RecyclerView.Adapter<IngredientsAdapter.ViewHolder> {

    private List<IngredientItem> ingredients;

    public IngredientsAdapter(List<IngredientItem> ingredients) {
        this.ingredients = ingredients;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemIngredientBinding binding = ItemIngredientBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        IngredientItem item = ingredients.get(position);

        holder.binding.tvIngredientName.setText(item.getName());
        holder.binding.tvIngredientMeasure.setText(item.getMeasure());

        String imgUrl = "https://www.themealdb.com/images/ingredients/" + item.getName() + ".png";
        ImageUtils.loadImage(holder.binding.imgIngredient, imgUrl);
    }

    @Override
    public int getItemCount() {
        return ingredients == null ? 0 : ingredients.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        final ItemIngredientBinding binding;
        public ViewHolder(ItemIngredientBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}