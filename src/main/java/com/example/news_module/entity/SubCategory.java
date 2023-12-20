package com.example.news_module.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "sub_category")
public class SubCategory {

    @Id
    @Column(name = "id")
    private int id;
    
    @Column(name = "sub_category")
    private String subCategory;
    
    @Column(name = "sub_category_news_count")
    private int subCategoryNewsCount;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "build_time")
    private Date buildTime;

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public int getSubCategoryNewsCount() {
        return subCategoryNewsCount;
    }

    public void setSubCategoryNewsCount(int subCategoryNewsCount) {
        this.subCategoryNewsCount = subCategoryNewsCount;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public Date getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(Date buildTime) {
        this.buildTime = buildTime;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SubCategory(int id, String subCategory, int subCategoryNewsCount, String category, Date buildTime) {
        super();
        this.id = id;
        this.subCategory = subCategory;
        this.subCategoryNewsCount = subCategoryNewsCount;
        this.category = category;
        this.buildTime = buildTime;
    }

    public SubCategory(String subCategory, String category) {
        super();
        this.subCategory = subCategory;
        this.category = category;
    }

    public SubCategory() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "SubCategory [subCategory=" + subCategory + ", subCategoryNewsCount=" + subCategoryNewsCount + ", category=" + category + ", buildTime=" + buildTime + "]";
    }
    
}
