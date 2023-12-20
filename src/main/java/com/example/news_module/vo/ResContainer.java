package com.example.news_module.vo;

import java.util.List;

public class ResContainer {

    private List<NewsCheck> newsCheckList;

    public List<NewsCheck> getNewsCheckList() {
        return newsCheckList;
    }

    public void setNewsCheckList(List<NewsCheck> newsCheckList) {
        this.newsCheckList = newsCheckList;
    }

    public ResContainer(List<NewsCheck> newsCheckList) {
        super();
        this.newsCheckList = newsCheckList;
    }

    public ResContainer() {
        super();
        // TODO Auto-generated constructor stub
    }
    
}
