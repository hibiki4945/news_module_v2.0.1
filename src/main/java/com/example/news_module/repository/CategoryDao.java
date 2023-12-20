package com.example.news_module.repository;

import com.example.news_module.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface CategoryDao extends JpaRepository<Category, Integer>{
//  カテゴリーがあるのかを判断する
    public boolean existsByCategory(String category);
//  カテゴリーでニュースを削除
    public boolean deleteByCategory(String category);
//  ニュースの数を更新する
    @Modifying
    @Transactional
    @Query(value = " update category c "
            + " set c.category = :inputCategory, "
            + " c.news_count = case when :inputNewsCount is null then 0 else :inputNewsCount end "
            + " where c.id = :inputId ", nativeQuery = true)
    public int updateCategoryById(@Param("inputId") int id,
                              @Param("inputCategory") String category,
                              @Param("inputNewsCount") int newsCount
                              );
    
}
