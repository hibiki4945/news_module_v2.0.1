package com.example.news_module.vo;

public class MultipleSearch {

    private String category;

    private String subCategory;

    private String newsTitle;

    private String newsSubTitle;
    
    private String content;

    private String releaseTimeStart;
    
    private String releaseTimeEnd;

    private String buildTimeStart;
    
    private String buildTimeEnd;

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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReleaseTimeStart() {
        return releaseTimeStart;
    }

    public void setReleaseTimeStart(String releaseTimeStart) {
        this.releaseTimeStart = releaseTimeStart;
    }

    public String getReleaseTimeEnd() {
        return releaseTimeEnd;
    }

    public void setReleaseTimeEnd(String releaseTimeEnd) {
        this.releaseTimeEnd = releaseTimeEnd;
    }

    public String getBuildTimeStart() {
        return buildTimeStart;
    }

    public void setBuildTimeStart(String buildTimeStart) {
        this.buildTimeStart = buildTimeStart;
    }

    public String getBuildTimeEnd() {
        return buildTimeEnd;
    }

    public void setBuildTimeEnd(String buildTimeEnd) {
        this.buildTimeEnd = buildTimeEnd;
    }

    public MultipleSearch() {
        super();
        // TODO Auto-generated constructor stub
    }

    public MultipleSearch(String category, String subCategory, String newsTitle, String newsSubTitle, String content, String releaseTimeStart, String releaseTimeEnd,
            String buildTimeStart, String buildTimeEnd) {
        super();
        this.category = category;
        this.subCategory = subCategory;
        this.newsTitle = newsTitle;
        this.newsSubTitle = newsSubTitle;
        this.content = content;
        this.releaseTimeStart = releaseTimeStart;
        this.releaseTimeEnd = releaseTimeEnd;
        this.buildTimeStart = buildTimeStart;
        this.buildTimeEnd = buildTimeEnd;
    }
    
}
