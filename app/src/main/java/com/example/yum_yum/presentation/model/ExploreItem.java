package com.example.yum_yum.presentation.model;

public class ExploreItem {
    private final String name;
    private final String imageUrl;

    public ExploreItem(String name, String imageUrl) {
        this.name = name;
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
