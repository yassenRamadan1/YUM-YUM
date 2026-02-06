package com.example.yum_yum.presentation.utils;

import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.yum_yum.R;

public class ImageUtils {
    public static void loadImage(ImageView view,String url){
        Glide.with(view)
                .load(url)
                .centerCrop()
                .placeholder(R.drawable.ic_baseline_cloud_download_24)
                .error(R.drawable.ic_baseline_error_outline_24)
                .into(view);
    }
    public static void loadFlag(ImageView view, String url){
        Glide.with(view)
                .load(url)
                .fitCenter()
                .placeholder(R.drawable.ic_flag)
                .error(R.drawable.ic_baseline_error_outline_24)
                .into(view);
    }
}
