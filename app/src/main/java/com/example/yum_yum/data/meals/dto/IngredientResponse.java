package com.example.yum_yum.data.meals.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class IngredientResponse {

    @SerializedName("meals")
    private List<Ingredient> meals;

    public List<Ingredient> getMeals() {
        return meals;
    }

    public void setMeals(List<Ingredient> meals) {
        this.meals = meals;
    }

    @Override
    public String toString() {
        return "IngredientResponse{meals=" + meals + '}';
    }

    public static class Ingredient {

        @SerializedName("idIngredient")
        private String id;

        @SerializedName("strIngredient")
        private String name;

        @SerializedName("strDescription")
        private String description;

        @SerializedName("strThumb")
        private String thumbnail;

        @SerializedName("strType")
        private String type;

        public Ingredient() {
        }

        public Ingredient(String id, String name, String description, String thumbnail, String type) {
            this.id = id;
            this.name = name;
            this.description = description;
            this.thumbnail = thumbnail;
            this.type = type;
        }

        // Getters and Setters
        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getThumbnail() {
            return thumbnail;
        }

        public void setThumbnail(String thumbnail) {
            this.thumbnail = thumbnail;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "Ingredient{" +
                    "id='" + id + '\'' +
                    ", name='" + name + '\'' +
                    ", description='" + description + '\'' +
                    ", thumbnail='" + thumbnail + '\'' +
                    ", type='" + type + '\'' +
                    '}';
        }
    }
}