package com.example.news_module.service.ifs;

import com.example.news_module.entity.Category;
import com.example.news_module.entity.News;
import com.example.news_module.entity.SubCategory;
import com.example.news_module.vo.CategoryAddResponse;
import com.example.news_module.vo.NewsAddResponse;
import com.example.news_module.vo.SubCategoryAddResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MainService {
//  ニュース追加チェック
    public NewsAddResponse newsAddCheck(News news);
//  ニュース追加
    public NewsAddResponse newsAdd(NewsAddResponse newsAddResponse);
//  カテゴリー追加
    public CategoryAddResponse categoryAdd(Category category);
//  サブカテゴリー追加
    public SubCategoryAddResponse subCategoryAdd(SubCategory subCategory);
//  カテゴリーでニュースを検索
    public Page<News> findPageByCategory(boolean sortDescFlag, int pageNum, int pageSize, String category);
//  カテゴリーでニュースを検索
    public List<News> findByCategory(String category);
//  サブカテゴリーでニュースを検索
    public Page<News> findPageBySubCategory(boolean sortDescFlag, int pageNum, int pageSize, String subCategory);
//  サブカテゴリーでニュースを検索
    public List<News> findBySubCategory(String subCategory);
//  カテゴリーとサブカテゴリーでニュースを検索
    public List<News> findByCategoryAndSubCategory(String category, String subCategory);
//  ニュースのタイルでニュースを検索
    public Page<News> findPageByNewsTitle(boolean sortDescFlag, int pageNum, int pageSize, String newsTitle);
//  ニュースのサブタイルでニュースを検索
    public Page<News> findPageByNewsSubTitle(boolean sortDescFlag, int pageNum, int pageSize, String newsSubTitle);
//  発表日以降でニュースを検索
    public Page<News> findPageByReleaseTimeGreater(boolean sortDescFlag, int pageNum, int pageSize, String date);
//  発表日以前でニュースを検索
    public Page<News> findPageByReleaseTimeLess(boolean sortDescFlag, int pageNum, int pageSize, String date);
//  複数条件でニュースを検索
    public Page<News> findPageByNewsByInput(boolean sortDescFlag, int pageNum, int pageSize, String Category, String SubCategory, String NewsTitle, String NewsSubTitle, String ReleaseTimeStart, String ReleaseTimeEnd, String BuildTimeStart, String BuildTimeEnd);
//  全てのニュースを検索
    public Page<News> findPageAll(boolean sortDescFlag, int pageNum, int pageSize);
//  ニュース更新チェック
    public NewsAddResponse newsEditCheck(News news);
//  ニュース更新
    public NewsAddResponse newsEdit(NewsAddResponse newsAddResponse);
//  ニュース削除
    public void newsDelete(int id);
    ///////////////////////////////////////////////
//  全てのカテゴリーを検索
    public Page<Category> findCategoryPageByAll(boolean sortDescFlag, int pageNum, int pageSize);
//  全てのカテゴリーを検索
    public List<Category> findCategoryByAll();
//  カテゴリー更新
    public CategoryAddResponse categoryEdit(Category category);
//  カテゴリーのニュースの数を更新
    public CategoryAddResponse categoryEditNewsCount(Category category);
//  カテゴリーを削除
    public void categoryDelete(int id);
    
    ///////////////////////////////////////////////
//  全てのサブカテゴリーを検索
    public Page<SubCategory> findSubCategoryPageByAll(boolean sortDescFlag, int pageNum, int pageSize);
//  カテゴリーでサブカテゴリーを検索
    public Page<SubCategory> findSubCategoryPageByCategory(boolean sortDescFlag, int pageNum, int pageSize, String category);
//  カテゴリーでサブカテゴリーを検索
    public List<SubCategory> findSubCategoryByCategory(String category);
//  サブカテゴリー更新
    public SubCategoryAddResponse subCategoryEdit(SubCategory subCategory);
//  サブカテゴリーのニュースの数を更新
    public SubCategoryAddResponse subCategoryEditNewsCount(SubCategory subCategory);
//  サブカテゴリーを削除
    public void subCategoryDelete(int id);
    
    
}
