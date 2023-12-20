package com.example.news_module.vo;

import com.example.news_module.entity.SubCategory;

public class SubCategoryAddResponse {

    private String code;
    
    private String message;
    
    private SubCategory subCategory;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public SubCategory getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(SubCategory subCategory) {
        this.subCategory = subCategory;
    }

    public SubCategoryAddResponse(String code, String message, SubCategory subCategory) {
        super();
        this.code = code;
        this.message = message;
        this.subCategory = subCategory;
    }

    public SubCategoryAddResponse() {
        super();
        // TODO Auto-generated constructor stub
    }
    
}
