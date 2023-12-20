package com.example.news_module.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import java.util.Date;

@Entity
@Table(name = "news")
public class News {

    @Id
    @Column(name = "id")
    private int id;
    
    @Column(name = "category")
    private String category;
    
    @Column(name = "sub_category")
    private String subCategory;
    
    @Column(name = "news_title")
    private String newsTitle;
    
    @Column(name = "news_sub_title")
    private String newsSubTitle;
    
    @Column(name = "release_time")
    private String releaseTime;
    
    @Column(name = "content")
    private String content;
    
    @Column(name = "build_time")
    private Date buildTime;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getSubCategory() {
        return subCategory;
    }

    public void setSubCategory(String subCategory) {
        this.subCategory = subCategory;
    }

    public String getNewsTitle() {
        return newsTitle;
    }

    public void setNewsTitle(String newsTitle) {
        this.newsTitle = newsTitle;
    }

    public String getNewsSubTitle() {
        return newsSubTitle;
    }

    public void setNewsSubTitle(String newsSubTitle) {
        this.newsSubTitle = newsSubTitle;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Date getBuildTime() {
        return buildTime;
    }

    public void setBuildTime(Date buildTime) {
        this.buildTime = buildTime;
    }

    public News(int id, String category, String subCategory, String newsTitle, String newsSubTitle, String releaseTime, String content, Date buildTime) {
        super();
        this.id = id;
        this.category = category;
        this.subCategory = subCategory;
        this.newsTitle = newsTitle;
        this.newsSubTitle = newsSubTitle;
        this.releaseTime = releaseTime;
        this.content = content;
        this.buildTime = buildTime;
    }

    public News(String category, String subCategory, String newsTitle, String newsSubTitle, String releaseTime, String content) {
        super();
        this.category = category;
        this.subCategory = subCategory;
        this.newsTitle = newsTitle;
        this.newsSubTitle = newsSubTitle;
        this.releaseTime = releaseTime;
        this.content = content;
    }

    public News() {
        super();
        // TODO Auto-generated constructor stub
    }

    @Override
    public String toString() {
        return "News [id=" + id + ", category=" + category + ", subCategory=" + subCategory + ", newsTitle=" + newsTitle + ", newsSubTitle=" + newsSubTitle + ", releaseTime="
               + releaseTime + ", content=" + content + ", buildTime=" + buildTime + "]";
    }
    
}
