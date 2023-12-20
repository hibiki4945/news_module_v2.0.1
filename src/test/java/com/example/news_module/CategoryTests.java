package com.example.news_module;

import com.example.news_module.constants.RtnCode;
import com.example.news_module.entity.Category;
import com.example.news_module.repository.CategoryDao;
import com.example.news_module.service.ifs.MainService;
import com.example.news_module.vo.CategoryAddResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class CategoryTests {

    @Autowired
    private MainService mainService;
    
    @Autowired
    private CategoryDao categoryDao;
    
    @Test
    public void CategoryAddTest() {
//      單元測試 of mainService的categoryAdd方法

//      測試1 : 判斷'資料'是否為空(null)
//      新增1個Category型別的變數(category)
        Category category = null;
//      呼叫mainService的categoryAdd方法
        CategoryAddResponse res = mainService.categoryAdd(category);
        Assert.isTrue(res.getMessage().equals(RtnCode.DATA_EMPTY_ERROR.getMessage()), "Failed!(測試1)");

//      測試2 : 判斷'分類'是否為空(null)
//      新增1個Category型別的變數(category)
        category = new Category(null);
//      呼叫mainService的categoryAdd方法
        res = mainService.categoryAdd(category);
        Assert.isTrue(res.getMessage().equals(RtnCode.CATEGORY_EMPTY_ERROR.getMessage()), "Failed!(測試2)");

//      測試2-1 : 判斷'分類'是否為空(isEmpty)
//      新增1個Category型別的變數(category)
        category = new Category(" ");
//      呼叫mainService的categoryAdd方法
        res = mainService.categoryAdd(category);
        Assert.isTrue(res.getMessage().equals(RtnCode.CATEGORY_EMPTY_ERROR.getMessage()), "Failed!(測試2-1)");

//      測試2.5 : 判斷'分類'是否超過長度上限(20)
        String testStr = "";
        for(int i = 0; i <= 20; i++) {
            testStr += "!";
        }
        category = new Category(testStr);
//      呼叫mainService的categoryAdd方法
        res = mainService.categoryAdd(category);
        Assert.isTrue(res.getMessage().equals(RtnCode.CATEGORY_OVER_LENGTH_ERROR.getMessage()), "Failed!(測試2.5)");
    
//      測試3 : 將 分類資料(category) 存到資料庫
//      新增1個Category型別的變數(category)
        category = new Category("demo_category");
//      呼叫mainService的categoryAdd方法
        res = mainService.categoryAdd(category);
        Assert.isTrue(res.getMessage().equals(RtnCode.SUCCESSFUL.getMessage()), "Failed!(測試3)");
        
//      測試4 : 判斷'分類'是否已存在
//      新增1個Category型別的變數(category)
        category = new Category("demo_category");
//      呼叫mainService的categoryAdd方法
        res = mainService.categoryAdd(category);
        Assert.isTrue(res.getMessage().equals(RtnCode.CATEGORY_EXISTS_ERROR.getMessage()), "Failed!(測試4)");
        
//      刪除 測試用資料
        categoryDao.deleteByCategory(category.getCategory());
        
    }
    
}
