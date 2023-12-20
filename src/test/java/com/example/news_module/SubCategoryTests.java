package com.example.news_module;

import com.example.news_module.constants.RtnCode;
import com.example.news_module.entity.Category;
import com.example.news_module.entity.SubCategory;
import com.example.news_module.repository.CategoryDao;
import com.example.news_module.repository.SubCategoryDao;
import com.example.news_module.service.ifs.MainService;
import com.example.news_module.vo.SubCategoryAddResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.Assert;

@SpringBootTest
public class SubCategoryTests {

    @Autowired
    private MainService mainService;
    
    @Autowired
    private SubCategoryDao subCategoryDao;
    
    @Autowired
    private CategoryDao categoryDao;
    
    @Test
    public void subCategoryAddTest() {
//      單元測試 of mainService的subCategoryAdd方法
        
//      測試1 : 判斷'資料'是否為空(null)
//      新增1個SubCategory型別的變數(subCategory)
        SubCategory subCategory = null;
//      呼叫mainService的subCategoryAdd方法
        SubCategoryAddResponse res = mainService.subCategoryAdd(subCategory);
        Assert.isTrue(res.getMessage().equals(RtnCode.DATA_EMPTY_ERROR.getMessage()), "Failed!(測試1)");
        
//      測試2 : 判斷'分類'是否為空(null)
//      新增1個SubCategory型別的變數(subCategory)
        subCategory = new SubCategory("demo_sub_category", null);
//      呼叫mainService的subCategoryAdd方法
        res = mainService.subCategoryAdd(subCategory);
        Assert.isTrue(res.getMessage().equals(RtnCode.CATEGORY_EMPTY_ERROR.getMessage()), "Failed!(測試2)");

//      測試2-1 : 判斷'分類'是否為空(isEmpty)
//      新增1個SubCategory型別的變數(subCategory)
        subCategory = new SubCategory("demo_sub_category", " ");
//      呼叫mainService的subCategoryAdd方法
        res = mainService.subCategoryAdd(subCategory);
        Assert.isTrue(res.getMessage().equals(RtnCode.CATEGORY_EMPTY_ERROR.getMessage()), "Failed!(測試2-1)");

//      測試3 : 判斷'子分類'是否為空(null)
//      新增1個SubCategory型別的變數(subCategory)
        subCategory = new SubCategory(null, "demo_category");
//      呼叫mainService的subCategoryAdd方法
        res = mainService.subCategoryAdd(subCategory);
        Assert.isTrue(res.getMessage().equals(RtnCode.SUB_CATEGORY_EMPTY_ERROR.getMessage()), "Failed!(測試3)");

//      測試3-1 : 判斷'子分類'是否為空(isEmpty)
//      新增1個SubCategory型別的變數(subCategory)
        subCategory = new SubCategory(" ", "demo_category");
//      呼叫mainService的subCategoryAdd方法
        res = mainService.subCategoryAdd(subCategory);
        Assert.isTrue(res.getMessage().equals(RtnCode.SUB_CATEGORY_EMPTY_ERROR.getMessage()), "Failed!(測試3-1)");

//      測試3.5 : 判斷'子分類'是否超過長度上限(20)
        String testStr = "";
        for(int i = 0; i <= 20; i++) {
            testStr += "!";
        }
        subCategory = new SubCategory(testStr, "demo_category");
//      呼叫mainService的categoryAdd方法
        res = mainService.subCategoryAdd(subCategory);
        Assert.isTrue(res.getMessage().equals(RtnCode.SUB_CATEGORY_OVER_LENGTH_ERROR.getMessage()), "Failed!(測試3.5)");
        
//      測試4 : 判斷'分類'是否已存在
//      新增1個SubCategory型別的變數(subCategory)
        subCategory = new SubCategory("demo_sub_category", "demo_category");
//      呼叫mainService的subCategoryAdd方法
        res = mainService.subCategoryAdd(subCategory);
        Assert.isTrue(res.getMessage().equals(RtnCode.CATEGORY_NOT_EXISTS_ERROR.getMessage()), "Failed!(測試4)");

//      建立 測試用資料(分類)
        categoryDao.save(new Category("demo_category"));
        
//      測試5 : 將 分類資料(category) 存到資料庫
//      新增1個SubCategory型別的變數(subCategory)
        subCategory = new SubCategory("demo_sub_category", "demo_category");
//      呼叫mainService的subCategoryAdd方法
        res = mainService.subCategoryAdd(subCategory);
        Assert.isTrue(res.getMessage().equals(RtnCode.SUCCESSFUL.getMessage()), "Failed!(測試5)"+res.getMessage());

//      測試6 : 判斷'子分類'是否已存在
//      新增1個SubCategory型別的變數(subCategory)
        subCategory = new SubCategory("demo_sub_category", "demo_category");
//      呼叫mainService的subCategoryAdd方法
        res = mainService.subCategoryAdd(subCategory);
        Assert.isTrue(res.getMessage().equals(RtnCode.SUB_CATEGORY_EXISTS_ERROR.getMessage()), "Failed!(測試6)");
        
//      刪除 測試用資料(記得改回(subCategoryDao.deleteById))
//        subCategoryDao.deleteById(subCategory.getSubCategory());
        categoryDao.deleteByCategory(subCategory.getCategory());
        
    }
    
}
