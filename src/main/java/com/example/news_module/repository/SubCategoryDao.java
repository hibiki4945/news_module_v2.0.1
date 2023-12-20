package com.example.news_module.repository;

import com.example.news_module.entity.SubCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SubCategoryDao extends JpaRepository<SubCategory, Integer>{
//  カテゴリーがあるかどうかを判断する
    public boolean existsByCategory(String category);
//  カテゴリーでニュースを検索
    public List<SubCategory> findByCategory(String category);
//  カテゴリーでニュースを検索
    public Page<SubCategory> findByCategory(Pageable pageable, String category);
//  サブカテゴリーがあるかどうかを判断する
    public boolean existsBySubCategory(String subCategory);
//  カテゴリーとサブカテゴリーがあるかどうかを判断する 
    public boolean existsByCategoryAndSubCategory(String category, String subCategory);
//  サブカテゴリーを更新する
    @Modifying
    @Transactional
    @Query(value = "update sub_category s"
            + " set s.category = :inputCategory, "
            + " s.sub_category = :inputSubCategory "
            + " where s.id = :inputId ", nativeQuery = true)
    public int updateSubCategoryById(@Param("inputId") int id,
                              @Param("inputCategory") String category,
                              @Param("inputSubCategory") String subCategory
                              );
//  ニュースの数を更新する
    @Modifying
    @Transactional
    @Query(value = "update sub_category s"
            + " set s.category = :inputCategory, "
            + " s.sub_category = :inputSubCategory, "
            + " s.sub_category_news_count = case when :inputSubCategoryNewsCount is null then 0 else :inputSubCategoryNewsCount end "
            + " where s.id = :inputId ", nativeQuery = true)
    public int updateSubCategoryNewsCountById(@Param("inputId") int id,
                              @Param("inputCategory") String category,
                              @Param("inputSubCategory") String subCategory,
                              @Param("inputSubCategoryNewsCount") int NewsCount
                              );
//  既存のニュースのサブカテゴリーを更新する
    @Modifying
    @Transactional
    @Query(value = "update sub_category s"
            + " set s.category = :inputCategory "
            + " where s.category = :inputOldCategory ", nativeQuery = true)
    public int updateSubCategoryCategoryByOldCategory(@Param("inputCategory") String category, @Param("inputOldCategory") String oldCategory);
    
}
