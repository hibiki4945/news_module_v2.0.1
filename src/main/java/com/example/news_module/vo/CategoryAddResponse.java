package com.example.news_module.vo;

import com.example.news_module.entity.Category;

public class CategoryAddResponse {
    
    private String code;
    
    private String message;
    
    private Category category;

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

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public CategoryAddResponse(String code, String message, Category category) {
        super();
        this.code = code;
        this.message = message;
        this.category = category;
    }

    public CategoryAddResponse() {
        super();
        // TODO Auto-generated constructor stub
    }

}
