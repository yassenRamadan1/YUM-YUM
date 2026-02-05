package com.example.yum_yum.data.meals.dto;

import com.google.gson.annotations.SerializedName;

public class CategoryDto {

    @SerializedName("idCategory")
    private String idCategory;

    @SerializedName("strCategory")
    private String strCategory;

    @SerializedName("strCategoryThumb")
    private String strCategoryThumb;

    @SerializedName("strCategoryDescription")
    private String strCategoryDescription;
    public String getIdCategory() { return idCategory; }
    public String getStrCategory() { return strCategory; }
    public String getStrCategoryThumb() { return strCategoryThumb; }
    public String getStrCategoryDescription() { return strCategoryDescription; }
    public void setIdCategory(String idCategory) { this.idCategory = idCategory; }
    public void setStrCategory(String strCategory) { this.strCategory = strCategory; }
    public void setStrCategoryThumb(String strCategoryThumb) { this.strCategoryThumb = strCategoryThumb; }
    public void setStrCategoryDescription(String strCategoryDescription) { this.strCategoryDescription = strCategoryDescription; }
}
