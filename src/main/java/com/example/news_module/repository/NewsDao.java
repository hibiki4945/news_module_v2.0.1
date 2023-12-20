package com.example.news_module.repository;

import com.example.news_module.entity.News;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface NewsDao extends JpaRepository<News, Integer>{
//  コンテンツでニュースが既にあるかどうかを判断
    public boolean existsByContent(String content);
//  コンテンツでニュースを削除
    public int removeByContent(String content);
//  カテゴリーでニュースを検索
    public List<News> findByCategory(String category);
//  サブカテゴリーでニュースを検索
    public List<News> findBySubCategory(String subCategory);
//  カテゴリーとサブカテゴリーでニュースを検索    
    public List<News> findByCategoryAndSubCategory(String category, String subCategory);
//  カテゴリーでニュースを検索
    public Page<News> findByCategory(Pageable pageable, String category);
//  サブカテゴリーでニュースを検索
    public Page<News> findBySubCategory(Pageable pageable, String subCategory);
//  ニュースタイルでニュースを検索
    @Query(value = "select * from news where news_title like concat ('%', :keyword, '%')", nativeQuery = true)
    public List<News> findByNewsTitle(@Param("keyword")String str);
//  ニュースタイルでニュースを検索
    @Query(value = "select * from news where news_title like concat ('%', :keyword, '%')", nativeQuery = true)
    public Page<News> findByNewsTitle(Pageable pageable, @Param("keyword")String str);
//  ニュースタイルのサブタイルでニュースを検索
    @Query(value = "select * from news where news_sub_title like concat ('%', :keyword, '%')", nativeQuery = true)
    public List<News> findByNewsSubTitle(@Param("keyword")String str);
//  ニュースタイルのサブタイルでニュースを検索
    @Query(value = "select * from news where news_sub_title like concat ('%', :keyword, '%')", nativeQuery = true)
    public Page<News> findByNewsSubTitle(Pageable pageable, @Param("keyword")String str);
//  発表日以降でニュースを検索
    public List<News> findByReleaseTimeGreaterThanEqual(String date);
//  発表日以降でニュースを検索    
    public Page<News> findByReleaseTimeGreaterThanEqual(Pageable pageable, String date);
//  発表日以前でニュースを検索
    public List<News> findByReleaseTimeLessThanEqual(String date);
//  発表日以前でニュースを検索  
    public Page<News> findByReleaseTimeLessThanEqual(Pageable pageable, String date);
//  ニュースを更新する
    @Modifying
    @Transactional
    @Query(value = "update news n"
            + " set n.category = :inputCategory, "
            + " n.sub_category = :inputSubCategory, "
            + " n.news_title = :inputNewsTitle, "
            + " n.news_sub_title = :inputNewsSubTitle, "
            + " n.release_time = :inputReleaseTime, "
            + " n.content = :inputContent "
            + " where n.id = :inputId ", nativeQuery = true)
    public int updateNewsById(@Param("inputId") int id, 
                              @Param("inputCategory") String category,
                              @Param("inputSubCategory") String subCategory,
                              @Param("inputNewsTitle") String newsTitle,
                              @Param("inputNewsSubTitle") String newsSubTitle,
                              @Param("inputReleaseTime") String newsReleaseTime,
                              @Param("inputContent") String content
                              );
//  既存のニュースのカテゴリーを更新する
    @Modifying
    @Transactional
    @Query(value = "update news n"
            + " set n.category = :inputCategory "
            + " where n.category = :inputOldCategory ", nativeQuery = true)
    public int updateNewsCategoryByOldCategory(@Param("inputCategory") String category, @Param("inputOldCategory") String oldCategory);
//  既存のニュースのサブカテゴリーを更新する
    @Modifying
    @Transactional
    @Query(value = "update news n"
            + " set n.sub_category = :inputSubCategory "
            + " where n.sub_category = :inputOldSubCategory ", nativeQuery = true)
    public int updateNewsSubCategoryByOldSubCategory(@Param("inputSubCategory") String subCategory, @Param("inputOldSubCategory") String oldSubCategory);
//  複数条件でニュースを検索
    @Query(value = "SELECT * FROM `news` where "
            + " category = case when :categoryInput is null then category else :categoryInput end and "
            + " sub_category like case when :subCategoryInput is null then '%%' else concat('%',:subCategoryInput,'%') end and "
            + " news_title like case when :newsTitleInput is null then '%%' else concat('%',:newsTitleInput,'%') end and "
            + " news_sub_title like case when :newsSubTitleInput is null then '%%' else concat('%',:newsSubTitleInput,'%') end and "
            + " release_time >= case when :releaseTimeStartInput not REGEXP '^[0-9]{4}-[0-9]{2}-[0-9]{2}' then release_time else :releaseTimeStartInput end and "
            + " release_time <= case when :releaseTimeEndInput not REGEXP '^[0-9]{4}-[0-9]{2}-[0-9]{2}' then release_time else :releaseTimeEndInput end and "
            + " build_time >= case when :buildTimeStartInput not REGEXP '^[0-9]{4}-[0-9]{2}-[0-9]{2}' then build_time else :buildTimeStartInput end and "
            + " build_time <= case when :buildTimeEndInput not REGEXP '^[0-9]{4}-[0-9]{2}-[0-9]{2}' then build_time else :buildTimeEndInput end "
            , nativeQuery = true)
    public Page<News> searchNewsByInput(
            Pageable pageable,
            @Param("categoryInput") String categoryInput,
            @Param("subCategoryInput") String subCategoryInput,
            @Param("newsTitleInput") String newsTitleInput,
            @Param("newsSubTitleInput") String newsSubTitleInput,
            @Param("releaseTimeStartInput") String releaseTimeStartInput,
            @Param("releaseTimeEndInput") String releaseTimeEndInput,
            @Param("buildTimeStartInput") String buildTimeStartInput,
            @Param("buildTimeEndInput") String buildTimeEndInput
            );
//  全てのニュースを検索    
    public Page<News> findAll(Pageable pageable);

}
