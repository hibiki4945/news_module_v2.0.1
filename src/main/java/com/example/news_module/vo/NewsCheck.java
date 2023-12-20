package com.example.news_module.vo;

import java.util.Date;

public class NewsCheck {

    private int id;
    
    private String category;
    
    private String subCategory;
    
    private String newsTitle;
    
    private String newsSubTitle;
    
    private String releaseTime;
    
    private String content;
    
    private Date buildTime;
    
//    private NewsCheckInside check;
    
    private boolean checkThis;

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

    public boolean isCheckThis() {
        return checkThis;
    }

    public void setCheckThis(boolean checkThis) {
        this.checkThis = checkThis;
    }

    public NewsCheck() {
        super();
        // TODO Auto-generated constructor stub
    }

    public NewsCheck(int id, String category, String subCategory, String newsTitle, String newsSubTitle, String releaseTime, String content, Date buildTime, boolean checkThis) {
        super();
        this.id = id;
        this.category = category;
        this.subCategory = subCategory;
        this.newsTitle = newsTitle;
        this.newsSubTitle = newsSubTitle;
        this.releaseTime = releaseTime;
        this.content = content;
        this.buildTime = buildTime;
        this.checkThis = checkThis;
    }

    @Override
    public String toString() {
        return "NewsCheck [id=" + id + ", category=" + category + ", subCategory=" + subCategory + ", newsTitle=" + newsTitle + ", newsSubTitle=" + newsSubTitle + ", releaseTime="
               + releaseTime + ", content=" + content + ", buildTime=" + buildTime + ", checkThis=" + checkThis + "]";
    }
    
}
