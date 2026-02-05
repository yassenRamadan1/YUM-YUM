package com.example.yum_yum.data.meals.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CategoryResponse {
        @SerializedName("categories")
        private List<CategoryDto> categories;
        public List<CategoryDto> getCategories() {
            return categories;
        }
        public void setCategories(List<CategoryDto> categories) {
            this.categories = categories;
        }
}
