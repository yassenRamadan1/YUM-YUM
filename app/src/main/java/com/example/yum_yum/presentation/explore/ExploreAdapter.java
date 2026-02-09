package com.example.yum_yum.presentation.explore;

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
import com.example.yum_yum.presentation.model.ExploreItem;

import java.util.ArrayList;
import java.util.List;

public class ExploreAdapter extends RecyclerView.Adapter<ExploreAdapter.ExploreViewHolder> {

    private List<ExploreItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(ExploreItem item);
    }

    public ExploreAdapter(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setItems(List<ExploreItem> newItems) {
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(new ExploreDiffCallback(this.items, newItems));
        this.items = new ArrayList<>(newItems);
        diffResult.dispatchUpdatesTo(this);
    }

    @NonNull
    @Override
    public ExploreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_explore, parent, false);
        return new ExploreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ExploreViewHolder holder, int position) {
        holder.bind(items.get(position));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ExploreViewHolder extends RecyclerView.ViewHolder {
        private final ImageView imageView;
        private final TextView nameText;

        public ExploreViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.explore_item_image);
            nameText = itemView.findViewById(R.id.explore_item_name);
        }

        public void bind(ExploreItem item) {
            nameText.setText(item.getName());
            Glide.with(itemView.getContext())
                    .load(item.getImageUrl())
                    .placeholder(R.drawable.quick_recipes_image)
                    .error(R.drawable.quick_recipes_image)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .centerCrop()
                    .into(imageView);

            itemView.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onItemClick(item);
                }
            });
        }
    }

    private static class ExploreDiffCallback extends DiffUtil.Callback {
        private final List<ExploreItem> oldList;
        private final List<ExploreItem> newList;

        public ExploreDiffCallback(List<ExploreItem> oldList, List<ExploreItem> newList) {
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
            return oldList.get(oldItemPosition).getName()
                    .equals(newList.get(newItemPosition).getName());
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            ExploreItem oldItem = oldList.get(oldItemPosition);
            ExploreItem newItem = newList.get(newItemPosition);
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getImageUrl().equals(newItem.getImageUrl());
        }
    }
}
