package com.example.news_module;

import com.example.news_module.constants.RtnCode;
import com.example.news_module.entity.News;
import com.example.news_module.repository.NewsDao;
import com.example.news_module.service.ifs.MainService;
import com.example.news_module.vo.NewsAddResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@SpringBootTest
public class NewsTests {

    @Autowired
    private MainService mainService;
    
    @Autowired
    private NewsDao newsDao;

    @Transactional
    @Test
    public void newsAddTest() {
//      單元測試 of mainService的newsAdd方法
        
//      測試0 : 判斷'資料'是否為空(null)
//      新增1個News型別的變數(news)
        News news = null;
//      呼叫mainService的newsAdd方法
        NewsAddResponse res = mainService.newsAddCheck(news);
        Assert.isTrue(res.getMessage().equals(RtnCode.DATA_EMPTY_ERROR.getMessage()), "Failed!(測試0)");

//      測試1 : 判斷'分類'是否為空(null)
//      新增1個News型別的變數(news)
        news = new News(null, "demo_subCategory", "demo_newsTitle", "demo_newsSubTitle", "1999-01-01", "demo_content");
//      呼叫mainService的newsAdd方法
        res = mainService.newsAddCheck(news);
        Assert.isTrue(res.getMessage().equals(RtnCode.CATEGORY_EMPTY_ERROR.getMessage()), "Failed!(測試1)");

//      測試1-1 : 判斷'分類'是否為空(isEmpty)
//      新增1個News型別的變數(news)
        news = new News(" ", "demo_subCategory", "demo_newsTitle", "demo_newsSubTitle", "1999-01-01", "demo_content");
//      呼叫mainService的newsAdd方法
        res = mainService.newsAddCheck(news);
        Assert.isTrue(res.getMessage().equals(RtnCode.CATEGORY_EMPTY_ERROR.getMessage()), "Failed!(測試1-1)");

//      測試2 : 判斷'子分類'是否為空(null)
//      新增1個News型別的變數(news)
        news = new News("demo_category", null, "demo_newsTitle", "demo_newsSubTitle", "1999-01-01", "demo_content");
//      呼叫mainService的newsAdd方法
        res = mainService.newsAddCheck(news);
        Assert.isTrue(res.getMessage().equals(RtnCode.SUB_CATEGORY_EMPTY_ERROR.getMessage()), "Failed!(測試2)");

//      測試2-1 : 判斷'子分類'是否為空(isEmpty)
//      新增1個News型別的變數(news)
        news = new News("demo_category", " ", "demo_newsTitle", "demo_newsSubTitle", "1999-01-01", "demo_content");
//      呼叫mainService的newsAdd方法
        res = mainService.newsAddCheck(news);
        Assert.isTrue(res.getMessage().equals(RtnCode.SUB_CATEGORY_EMPTY_ERROR.getMessage()), "Failed!(測試2-1)");

//      測試3 : 判斷'新聞標題'是否為空(null)
//      新增1個News型別的變數(news)
        news = new News("demo_category", "demo_subCategory", null, "demo_newsSubTitle", "1999-01-01", "demo_content");
//      呼叫mainService的newsAdd方法
        res = mainService.newsAddCheck(news);
        Assert.isTrue(res.getMessage().equals(RtnCode.NEWS_TITLE_EMPTY_ERROR.getMessage()), "Failed!(測試3)");

//      測試3-1 : 判斷'新聞標題'是否為空(isEmpty)
//      新增1個News型別的變數(news)
        news = new News("demo_category", "demo_subCategory", " ", "demo_newsSubTitle", "1999-01-01", "demo_content");
//      呼叫mainService的newsAdd方法
        res = mainService.newsAddCheck(news);
        Assert.isTrue(res.getMessage().equals(RtnCode.NEWS_TITLE_EMPTY_ERROR.getMessage()), "Failed!(測試3-1)");

//      測試3.5 : 判斷'新聞標題'是否超過長度上限(100)
        String testStr = "";
        for(int i = 0; i <= 100; i++) {
            testStr += "!";
        }
        news = new News("demo_category", "demo_subCategory", testStr, "demo_newsSubTitle", "1999-01-01", "demo_content");
//      呼叫mainService的newsAdd方法
        res = mainService.newsAddCheck(news);
        Assert.isTrue(res.getMessage().equals(RtnCode.NEWS_TITLE_OVER_LENGTH_ERROR.getMessage()), "Failed!(測試3.5)"+res.getMessage());
 
//      測試4 : 判斷'新聞副標題'是否為空(null)
//      新增1個News型別的變數(news)
        news = new News("demo_category", "demo_subCategory", "demo_newsTitle", null, "1999-01-01", "demo_content");
//      呼叫mainService的newsAdd方法
        res = mainService.newsAddCheck(news);
        Assert.isTrue(res.getMessage().equals(RtnCode.NEWS_SUB_TITLE_EMPTY_ERROR.getMessage()), "Failed!(測試4)");

//      測試4-1 : 判斷'新聞副標題'是否為空(isEmpty)
//      新增1個News型別的變數(news)
        news = new News("demo_category", "demo_subCategory", "demo_newsTitle", " ", "1999-01-01", "demo_content");
//      呼叫mainService的newsAdd方法
        res = mainService.newsAddCheck(news);
        Assert.isTrue(res.getMessage().equals(RtnCode.NEWS_SUB_TITLE_EMPTY_ERROR.getMessage()), "Failed!(測試4-1)");

//      測試4.5 : 判斷'新聞副標題'是否超過長度上限(20)
        testStr = "";
        for(int i = 0; i <= 20; i++) {
            testStr += "!";
        }
        news = new News("demo_category", "demo_subCategory", "demo_newsTitle", testStr, "1999-01-01", "demo_content");
//      呼叫mainService的newsAdd方法
        res = mainService.newsAddCheck(news);
        Assert.isTrue(res.getMessage().equals(RtnCode.NEWS_SUB_TITLE_OVER_LENGTH_ERROR.getMessage()), "Failed!(測試4.5)");
        
//      測試5 : 判斷'發布時間'是否格式錯誤
//      新增1個News型別的變數(news)
        news = new News("demo_category", "demo_subCategory", "demo_newsTitle", "demo_newsSubTitle", "1999-01-010", "demo_content");
//      呼叫mainService的newsAdd方法
        res = mainService.newsAddCheck(news);
        Assert.isTrue(res.getMessage().equals(RtnCode.RELEASE_TIME_FORMAT_ERROR.getMessage()), "Failed!(測試5)");

//      測試6 : 判斷'內文'是否為空(null)
//      新增1個News型別的變數(news)
        news = new News("demo_category", "demo_subCategory", "demo_newsTitle", "demo_newsSubTitle", "1999-01-01", null);
//      呼叫mainService的newsAdd方法
        res = mainService.newsAddCheck(news);
        Assert.isTrue(res.getMessage().equals(RtnCode.CONTENT_EMPTY_ERROR.getMessage()), "Failed!(測試6)");

//      測試6-1 : 判斷'內文'是否為空(isEmpty)
//      新增1個News型別的變數(news)
        news = new News("demo_category", "demo_subCategory", "demo_newsTitle", "demo_newsSubTitle", "1999-01-01", " ");
//      呼叫mainService的newsAdd方法
        res = mainService.newsAddCheck(news);
        Assert.isTrue(res.getMessage().equals(RtnCode.CONTENT_EMPTY_ERROR.getMessage()), "Failed!(測試6-1)");

//      測試6.5 : 判斷'內文'是否超過長度上限(1000)
        testStr = "";
        for(int i = 0; i <= 1000; i++) {
            testStr += "!";
        }
        news = new News("demo_category", "demo_subCategory", "demo_newsTitle", "demo_newsSubTitle", "1999-01-01", testStr);
//      呼叫mainService的newsAdd方法
        res = mainService.newsAddCheck(news);
        Assert.isTrue(res.getMessage().equals(RtnCode.CONTENT_OVER_LENGTH_ERROR.getMessage()), "Failed!(測試6.5)");
        
//      測試7 : 將 新聞資料(news) 存到資料庫
//      新增1個News型別的變數(news)
        news = new News("demo_category", "demo_subCategory", "demo_newsTitle", "demo_newsSubTitle", "1999-01-01", "demo_content");
//      呼叫mainService的newsAdd方法
        res = mainService.newsAddCheck(news);
        Assert.isTrue(res.getMessage().equals(RtnCode.SUCCESSFUL.getMessage()), "Failed!(測試7)");
        res = mainService.newsAdd(res);
        Assert.isTrue(res.getMessage().equals(RtnCode.SUCCESSFUL.getMessage()), "Failed!(測試7)");
        
//      測試8 : 判斷'內文'是否已存在
//      新增1個News型別的變數(news)
        news = new News("demo_category", "demo_subCategory", "demo_newsTitle", "demo_newsSubTitle", "1999-01-01", "demo_content");
//      呼叫mainService的newsAdd方法
        res = mainService.newsAddCheck(news);
        Assert.isTrue(res.getMessage().equals(RtnCode.NEWS_EXISTS_ERROR.getMessage()), "Failed!(測試8)");

//      刪除 測試用資料
        newsDao.removeByContent(news.getContent());
        
    }
    
}
