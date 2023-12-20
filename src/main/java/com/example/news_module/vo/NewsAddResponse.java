package com.example.news_module.vo;

import com.example.news_module.entity.News;

public class NewsAddResponse {

    private String Code;
    
    private String Message;
    
    private News News;

    public String getCode() {
        return Code;
    }

    public void setCode(String code) {
        Code = code;
    }

    public String getMessage() {
        return Message;
    }

    public void setMessage(String message) {
        Message = message;
    }

    public News getNews() {
        return News;
    }

    public void setNews(News news) {
        News = news;
    }

    public NewsAddResponse(String code, String message, com.example.news_module.entity.News news) {
        super();
        Code = code;
        Message = message;
        News = news;
    }

    public NewsAddResponse() {
        super();
        // TODO Auto-generated constructor stub
    }
    
}
