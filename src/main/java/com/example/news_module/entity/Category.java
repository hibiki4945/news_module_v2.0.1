package com.example.news_module.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "category")
public class Category {

    @Id
    @Column(name = "id")
    private int id;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "news_count")
    private int newsCount;
    
    @Column(name = "build_time")
    private Date buildTime;

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getNewsCount() {
        return newsCount;
    }

    public void setNewsCount(int newsCount) {
        this.newsCount = newsCount;
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

    public Category(int id, String category, int newsCount, Date buildTime) {
        super();
        this.id = id;
        this.category = category;
        this.newsCount = newsCount;
        this.buildTime = buildTime;
    }

    public Category(String category) {
        super();
        this.category = category;
    }

    public Category() {
        super();
        // TODO Auto-generated constructor stub
    }

    
}
