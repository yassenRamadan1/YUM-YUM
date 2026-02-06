package com.example.yum_yum.presentation.home;

import android.widget.ImageView;

import com.example.yum_yum.presentation.model.Meal;

public interface OnMealClickListener {
    // Pass the ImageView that will transition
    void onMealClick(Meal meal, ImageView sharedImageView);
}