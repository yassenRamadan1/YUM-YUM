package com.example.yum_yum.data.meals.dto;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AreaResponse {
    @SerializedName("meals")
    private List<AreaDto> areas;

    public List<AreaDto> getAreas() {
        return areas;
    }

    public static class AreaDto {
        @SerializedName("strArea")
        private String area;

        public String getArea() {
            return area;
        }
    }
}
