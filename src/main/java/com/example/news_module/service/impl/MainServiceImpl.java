package com.example.news_module.service.impl;

import com.example.news_module.constants.RtnCode;
import com.example.news_module.entity.Category;
import com.example.news_module.entity.News;
import com.example.news_module.entity.SubCategory;
import com.example.news_module.repository.CategoryDao;
import com.example.news_module.repository.NewsDao;
import com.example.news_module.repository.SubCategoryDao;
import com.example.news_module.service.ifs.MainService;
import com.example.news_module.vo.CategoryAddResponse;
import com.example.news_module.vo.NewsAddResponse;
import com.example.news_module.vo.SubCategoryAddResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class MainServiceImpl implements MainService {

    @Autowired
    private NewsDao newsDao;
    @Autowired
    private CategoryDao categoryDao;
    @Autowired
    private SubCategoryDao subCategoryDao;

    //  ニュース追加チェック
    @Override
    public NewsAddResponse newsAddCheck(News news) {

//        //      引数がヌルのチェック
//        if (news == null) {
//            return new NewsAddResponse(RtnCode.DATA_EMPTY_ERROR.getCode(), RtnCode.DATA_EMPTY_ERROR.getMessage(), null);
//        }
//        //      カテゴリーがヌルのチェック
//        if (news.getCategory() == null || news.getCategory().isBlank()) {
//            return new NewsAddResponse(RtnCode.CATEGORY_EMPTY_ERROR.getCode(), RtnCode.CATEGORY_EMPTY_ERROR.getMessage(), null);
//        }
        //      サブカテゴリーがヌルのチェック
        if (news.getSubCategory() == null || news.getSubCategory().isBlank()) {
            return new NewsAddResponse(RtnCode.SUB_CATEGORY_EMPTY_ERROR.getCode(), RtnCode.SUB_CATEGORY_EMPTY_ERROR.getMessage(), null);
        }
        //      ニュースタイルがヌルのチェック
        if (news.getNewsTitle() == null || news.getNewsTitle().isBlank()) {
            return new NewsAddResponse(RtnCode.NEWS_TITLE_EMPTY_ERROR.getCode(), RtnCode.NEWS_TITLE_EMPTY_ERROR.getMessage(), null);
        }
        //      ニュースタイルの長さチェック
        if (news.getNewsTitle().length() > 100) {
            return new NewsAddResponse(RtnCode.NEWS_TITLE_OVER_LENGTH_ERROR.getCode(), RtnCode.NEWS_TITLE_OVER_LENGTH_ERROR.getMessage(), null);
        }
        ////      ニュースのサブタイルがヌルのチェック
        //        if(news.getNewsSubTitle().isBlank()) {
        //            return new NewsAddResponse(RtnCode.NEWS_SUB_TITLE_EMPTY_ERROR.getCode(), RtnCode.NEWS_SUB_TITLE_EMPTY_ERROR.getMessage(), null);
        //        }
        //      ニュースのサブタイルの長さチェック
        if (news.getNewsSubTitle().length() > 20) {
            return new NewsAddResponse(RtnCode.NEWS_SUB_TITLE_OVER_LENGTH_ERROR.getCode(), RtnCode.NEWS_SUB_TITLE_OVER_LENGTH_ERROR.getMessage(), null);
        }
        //      発表日のフォーマットチェック
        if (!news.getReleaseTime().matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
            return new NewsAddResponse(RtnCode.RELEASE_TIME_FORMAT_ERROR.getCode(), RtnCode.RELEASE_TIME_FORMAT_ERROR.getMessage(), null);
        }
        //      コンテンツがヌルのチェック
        if (news.getContent() == null || news.getContent().isBlank()) {
            return new NewsAddResponse(RtnCode.CONTENT_EMPTY_ERROR.getCode(), RtnCode.CONTENT_EMPTY_ERROR.getMessage(), null);
        }
        //      コンテンツの長さチェック
        if (news.getContent().length() > 1000) {
            return new NewsAddResponse(RtnCode.CONTENT_OVER_LENGTH_ERROR.getCode(), RtnCode.CONTENT_OVER_LENGTH_ERROR.getMessage(), null);
        }

        //      コンテンツの既存判断
        if (newsDao.existsByContent(news.getContent())) {
            return new NewsAddResponse(RtnCode.NEWS_EXISTS_ERROR.getCode(), RtnCode.NEWS_EXISTS_ERROR.getMessage(), null);
        }

        //      今の日付を獲得
        Date date = new Date();
        //      BuildTimeに設定
        news.setBuildTime(date);

        return new NewsAddResponse(RtnCode.SUCCESSFUL.getCode(), RtnCode.SUCCESSFUL.getMessage(), news);

    }

    //  ニュース追加
    @Override
    public NewsAddResponse newsAdd(NewsAddResponse newsAddResponse) {

//        try {
            //          ニュースを更新
            News res = newsDao.save(newsAddResponse.getNews());
            return new NewsAddResponse(RtnCode.SUCCESSFUL.getCode(), RtnCode.SUCCESSFUL.getMessage(), newsAddResponse.getNews());
//        } catch (Exception e) {
//            return new NewsAddResponse(RtnCode.DAO_ERROR.getCode(), RtnCode.DAO_ERROR.getMessage(), null);
//        }
    }

    //  カテゴリー追加
    @Override
    public CategoryAddResponse categoryAdd(Category category) {

        //      引数がヌルのチェック
        if (category == null) {
            return new CategoryAddResponse(RtnCode.DATA_EMPTY_ERROR.getCode(), RtnCode.DATA_EMPTY_ERROR.getMessage(), null);
        }
        //      カテゴリーがヌルのチェック
        if (category.getCategory().isBlank()) {
            return new CategoryAddResponse(RtnCode.CATEGORY_EMPTY_ERROR.getCode(), RtnCode.CATEGORY_EMPTY_ERROR.getMessage(), null);
        }
        //      カテゴリーの長さチェック
        if (category.getCategory().length() > 20) {
            return new CategoryAddResponse(RtnCode.CATEGORY_OVER_LENGTH_ERROR.getCode(), RtnCode.CATEGORY_OVER_LENGTH_ERROR.getMessage(), null);
        }
        //      カテゴリーの既存判断
        if (categoryDao.existsByCategory(category.getCategory())) {
            return new CategoryAddResponse(RtnCode.CATEGORY_EXISTS_ERROR.getCode(), RtnCode.CATEGORY_EXISTS_ERROR.getMessage(), null);
        }

        //      今の日付を獲得
        Date date = new Date();
        //      BuildTimeに設定
        category.setBuildTime(date);
        //      ニュースの数をゼロにする
        category.setNewsCount(0);
        //      カテゴリー追加
        Category res = categoryDao.save(category);
        if (res == null) {
            return new CategoryAddResponse(RtnCode.DAO_ERROR.getCode(), RtnCode.DAO_ERROR.getMessage(), null);
        }

        return new CategoryAddResponse(RtnCode.SUCCESSFUL.getCode(), RtnCode.SUCCESSFUL.getMessage(), res);

    }

    //  サブカテゴリー追加
    @Override
    public SubCategoryAddResponse subCategoryAdd(SubCategory subCategory) {

        //      引数がヌルのチェック
        if (subCategory == null) {
            return new SubCategoryAddResponse(RtnCode.DATA_EMPTY_ERROR.getCode(), RtnCode.DATA_EMPTY_ERROR.getMessage(), null);
        }
        //      カテゴリーがヌルのチェック
        if (subCategory.getCategory().isBlank()) {
            return new SubCategoryAddResponse(RtnCode.CATEGORY_EMPTY_ERROR.getCode(), RtnCode.CATEGORY_EMPTY_ERROR.getMessage(), null);
        }
        //      サブカテゴリーがヌルのチェック
        if (subCategory.getSubCategory().isBlank()) {
            return new SubCategoryAddResponse(RtnCode.SUB_CATEGORY_EMPTY_ERROR.getCode(), RtnCode.SUB_CATEGORY_EMPTY_ERROR.getMessage(), null);
        }
        //      サブカテゴリーの長さチェック
        if (subCategory.getSubCategory().length() > 20) {
            return new SubCategoryAddResponse(RtnCode.SUB_CATEGORY_OVER_LENGTH_ERROR.getCode(), RtnCode.SUB_CATEGORY_OVER_LENGTH_ERROR.getMessage(), null);
        }
        //      カテゴリーの既存判断
        if (!categoryDao.existsByCategory(subCategory.getCategory())) {
            return new SubCategoryAddResponse(RtnCode.CATEGORY_NOT_EXISTS_ERROR.getCode(), RtnCode.CATEGORY_NOT_EXISTS_ERROR.getMessage(), null);
        }
        //      サブカテゴリーの既存判断
        if (subCategoryDao.existsByCategoryAndSubCategory(subCategory.getCategory(), subCategory.getSubCategory())) {

            return new SubCategoryAddResponse(RtnCode.SUB_CATEGORY_EXISTS_ERROR.getCode(), RtnCode.SUB_CATEGORY_EXISTS_ERROR.getMessage(), null);
        }

        //      今の日付を獲得
        Date date = new Date();
        //      BuildTimeに設定
        subCategory.setBuildTime(date);
        //      ニュースの数をゼロに設定
        subCategory.setSubCategoryNewsCount(0);
        //      サブカテゴリー追加
        SubCategory res = subCategoryDao.save(subCategory);

        return new SubCategoryAddResponse(RtnCode.SUCCESSFUL.getCode(), RtnCode.SUCCESSFUL.getMessage(), res);

    }

    //  カテゴリーでニュースを検索
    @Override
    public Page<News> findPageByCategory(boolean sortDescFlag, int pageNum, int pageSize, String category) {

        Sort sort = null;
        if (sortDescFlag)
            sort = Sort.by(Sort.Direction.DESC, "id");
        else
            sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        //             サブカテゴリーでニュースを検索
        Page<News> newsList = newsDao.findByCategory(pageable, category);

        return newsList;
    }

    //  サブカテゴリーでニュースを検索
    @Override
    public Page<News> findPageBySubCategory(boolean sortDescFlag, int pageNum, int pageSize, String subCategory) {

        Sort sort = null;
        if (sortDescFlag)
            sort = Sort.by(Sort.Direction.DESC, "id");
        else
            sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        //      サブカテゴリーでニュースを検索
        Page<News> newsList = newsDao.findBySubCategory(pageable, subCategory);

        return newsList;
    }

    //  ニュースのタイルでニュースを検索
    @Override
    public Page<News> findPageByNewsTitle(boolean sortDescFlag, int pageNum, int pageSize, String newsTitle) {
        Sort sort = null;
        if (sortDescFlag)
            sort = Sort.by(Sort.Direction.DESC, "id");
        else
            sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        //      ニュースのタイルでニュースを検索
        Page<News> newsList = newsDao.findByNewsTitle(pageable, newsTitle);

        return newsList;
    }

    //  ニュースのサブタイルでニュースを検索
    @Override
    public Page<News> findPageByNewsSubTitle(boolean sortDescFlag, int pageNum, int pageSize, String newsSubTitle) {

        Sort sort = null;
        if (sortDescFlag)
            sort = Sort.by(Sort.Direction.DESC, "id");
        else
            sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        //      ニュースのサブタイルでニュースを検索
        Page<News> newsList = newsDao.findByNewsSubTitle(pageable, newsSubTitle);

        return newsList;
    }

    //  発表日以降でニュースを検索
    @Override
    public Page<News> findPageByReleaseTimeGreater(boolean sortDescFlag, int pageNum, int pageSize, String date) {

        Sort sort = null;
        if (sortDescFlag)
            sort = Sort.by(Sort.Direction.DESC, "id");
        else
            sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        //      発表日以降でニュースを検索
        Page<News> newsList = newsDao.findByReleaseTimeGreaterThanEqual(pageable, date);

        return newsList;
    }

    //  発表日以前でニュースを検索
    @Override
    public Page<News> findPageByReleaseTimeLess(boolean sortDescFlag, int pageNum, int pageSize, String date) {

        Sort sort = null;
        if (sortDescFlag)
            sort = Sort.by(Sort.Direction.DESC, "id");
        else
            sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        //      発表日以前でニュースを検索
        Page<News> newsList = newsDao.findByReleaseTimeLessThanEqual(pageable, date);

        return newsList;
    }

    //  複数条件でニュースを検索
    @Override
    public Page<News> findPageByNewsByInput(boolean sortDescFlag, int pageNum, int pageSize, String Category, String SubCategory, String NewsTitle, String NewsSubTitle,
                                            String ReleaseTimeStart, String ReleaseTimeEnd, String BuildTimeStart, String BuildTimeEnd) {

        Sort sort = null;
        if (sortDescFlag)
            sort = Sort.by(Sort.Direction.DESC, "id");
        else
            sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        //      複数条件でニュースを検索
        Page<News> newsList = newsDao.searchNewsByInput(pageable, Category, SubCategory, NewsTitle, NewsSubTitle, ReleaseTimeStart, ReleaseTimeEnd, BuildTimeStart, BuildTimeEnd);

        return newsList;
    }

    //  全てのニュースを検索
    @Override
    public Page<News> findPageAll(boolean sortDescFlag, int pageNum, int pageSize) {

        Sort sort = null;
        sort = Sort.by(Sort.Direction.DESC, "releaseTime");
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        //　　　全てのニュースを検索
        Page<News> newsList = newsDao.findAll(pageable);

        return newsList;
    }

    //  ニュース更新チェック
    @Override
    public NewsAddResponse newsEditCheck(News news) {
        //      引数がヌルのチェック
        if (news == null) {
            return new NewsAddResponse(RtnCode.DATA_EMPTY_ERROR.getCode(), RtnCode.DATA_EMPTY_ERROR.getMessage(), null);
        }
        //      カテゴリーがヌルのチェック
        if (news.getCategory() == null || news.getCategory().isBlank()) {
            return new NewsAddResponse(RtnCode.CATEGORY_EMPTY_ERROR.getCode(), RtnCode.CATEGORY_EMPTY_ERROR.getMessage(), null);
        }
        //      サブカテゴリーがヌルのチェック
        if (news.getSubCategory() == null || news.getSubCategory().isBlank()) {
            return new NewsAddResponse(RtnCode.SUB_CATEGORY_EMPTY_ERROR.getCode(), RtnCode.SUB_CATEGORY_EMPTY_ERROR.getMessage(), null);
        }
        //      ニュースのタイルがヌルのチェック
        if (news.getNewsTitle() == null || news.getNewsTitle().isBlank()) {
            return new NewsAddResponse(RtnCode.NEWS_TITLE_EMPTY_ERROR.getCode(), RtnCode.NEWS_TITLE_EMPTY_ERROR.getMessage(), null);
        }
        //      ニュースのタイルの長さチェック
        if (news.getNewsTitle().length() > 100) {
            return new NewsAddResponse(RtnCode.NEWS_TITLE_OVER_LENGTH_ERROR.getCode(), RtnCode.NEWS_TITLE_OVER_LENGTH_ERROR.getMessage(), null);
        }
        //      ニュースのサブタイルがヌルのチェック
        if (news.getNewsSubTitle() == null || news.getNewsSubTitle().isBlank()) {
            return new NewsAddResponse(RtnCode.NEWS_SUB_TITLE_EMPTY_ERROR.getCode(), RtnCode.NEWS_SUB_TITLE_EMPTY_ERROR.getMessage(), null);
        }
        //      ニュースのサブタイルがヌルのチェック
        if (news.getNewsSubTitle().length() > 20) {
            return new NewsAddResponse(RtnCode.NEWS_SUB_TITLE_OVER_LENGTH_ERROR.getCode(), RtnCode.NEWS_SUB_TITLE_OVER_LENGTH_ERROR.getMessage(), null);
        }
        //      発表日のフォーマットチェック
        if (!news.getReleaseTime().matches("[\\d]{4}-[\\d]{2}-[\\d]{2}")) {
            return new NewsAddResponse(RtnCode.RELEASE_TIME_FORMAT_ERROR.getCode(), RtnCode.RELEASE_TIME_FORMAT_ERROR.getMessage(), null);
        }
        //      コンテンツがヌルのチェック
        if (news.getContent() == null || news.getContent().isBlank()) {
            return new NewsAddResponse(RtnCode.CONTENT_EMPTY_ERROR.getCode(), RtnCode.CONTENT_EMPTY_ERROR.getMessage(), null);
        }
        //      コンテンツの長さチェック
        if (news.getContent().length() > 1000) {
            return new NewsAddResponse(RtnCode.CONTENT_OVER_LENGTH_ERROR.getCode(), RtnCode.CONTENT_OVER_LENGTH_ERROR.getMessage(), null);
        }

        // 今の日付を獲得
        Date date = new Date();
        //      BuildTimeに設定
        news.setBuildTime(date);

        return new NewsAddResponse(RtnCode.SUCCESSFUL.getCode(), RtnCode.SUCCESSFUL.getMessage(), news);

    }

    //  ニュース更新
    @Override
    public NewsAddResponse newsEdit(NewsAddResponse newsAddResponse) {

        try {
            //          ニュース更新
            int res = newsDao.updateNewsById(newsAddResponse.getNews().getId(), newsAddResponse.getNews().getCategory(), newsAddResponse.getNews().getSubCategory(),
                    newsAddResponse.getNews().getNewsTitle(), newsAddResponse.getNews().getNewsSubTitle(), newsAddResponse.getNews().getReleaseTime(),
                    newsAddResponse.getNews().getContent());
            //         ニュース更新結果のチェック
            if (!(res > 0)) {
                return new NewsAddResponse(RtnCode.DATA_ERROR.getCode(), RtnCode.DATA_ERROR.getMessage(), null);
            }

            return new NewsAddResponse(RtnCode.SUCCESSFUL.getCode(), RtnCode.SUCCESSFUL.getMessage(), newsAddResponse.getNews());
        } catch (Exception e) {

            return new NewsAddResponse(RtnCode.DAO_ERROR.getCode(), RtnCode.DAO_ERROR.getMessage(), null);
        }

    }

    //  ニュースを削除
    @Override
    public void newsDelete(int id) {
        try {
            //      ニュースを削除
            newsDao.deleteById(id);
        } catch (Exception e) {
        }
    }

    //  全てのカテゴリーを検索
    @Override
    public Page<Category> findCategoryPageByAll(boolean sortDescFlag, int pageNum, int pageSize) {

        Sort sort = null;
        if (sortDescFlag)
            sort = Sort.by(Sort.Direction.DESC, "id");
        else
            sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        //      全てのカテゴリーを検索
        Page<Category> categoryList = categoryDao.findAll(pageable);

        return categoryList;
    }

    //  カテゴリー更新
    @Override
    public CategoryAddResponse categoryEdit(Category category) {
        //      引数がヌルのチェック
        if (category == null) {
            return new CategoryAddResponse(RtnCode.DATA_EMPTY_ERROR.getCode(), RtnCode.DATA_EMPTY_ERROR.getMessage(), null);
        }
        //      カテゴリーがヌルのチェック
        if (category.getCategory() == null || category.getCategory().isBlank()) {
            return new CategoryAddResponse(RtnCode.CATEGORY_EMPTY_ERROR.getCode(), RtnCode.CATEGORY_EMPTY_ERROR.getMessage(), null);
        }
        //      カテゴリーの長さチェック
        if (category.getCategory().length() > 20) {
            return new CategoryAddResponse(RtnCode.CATEGORY_OVER_LENGTH_ERROR.getCode(), RtnCode.CATEGORY_OVER_LENGTH_ERROR.getMessage(), null);
        }
        //      カテゴリーの既存判断
        if (categoryDao.existsByCategory(category.getCategory())) {
            return new CategoryAddResponse(RtnCode.CATEGORY_EXISTS_ERROR.getCode(), RtnCode.CATEGORY_EXISTS_ERROR.getMessage(), null);
        }
        //      カテゴリー更新
        int res = categoryDao.updateCategoryById(category.getId(), category.getCategory(), category.getNewsCount());
        //      カテゴリー更新結果のチェック
        if (!(res > 0)) {
            return new CategoryAddResponse(RtnCode.DATA_ERROR.getCode(), RtnCode.DATA_ERROR.getMessage(), null);
        }

        return new CategoryAddResponse(RtnCode.SUCCESSFUL.getCode(), RtnCode.SUCCESSFUL.getMessage(), null);

    }

    //  カテゴリーのニュース数の更新
    @Override
    public CategoryAddResponse categoryEditNewsCount(Category category) {

        //      引数がヌルのチェック
        if (category == null) {
            return new CategoryAddResponse(RtnCode.DATA_EMPTY_ERROR.getCode(), RtnCode.DATA_EMPTY_ERROR.getMessage(), null);
        }
        //      カテゴリーがヌルのチェック
        if (category.getCategory().isBlank()) {
            return new CategoryAddResponse(RtnCode.CATEGORY_EMPTY_ERROR.getCode(), RtnCode.CATEGORY_EMPTY_ERROR.getMessage(), null);
        }
        //      カテゴリーの長さチェック
        if (category.getCategory().length() > 20) {
            return new CategoryAddResponse(RtnCode.CATEGORY_OVER_LENGTH_ERROR.getCode(), RtnCode.CATEGORY_OVER_LENGTH_ERROR.getMessage(), null);
        }

        //      カテゴリーのニュース数更新
        int res = categoryDao.updateCategoryById(category.getId(), category.getCategory(), category.getNewsCount());
        if (!(res > 0)) {
            return new CategoryAddResponse(RtnCode.DATA_ERROR.getCode(), RtnCode.DATA_ERROR.getMessage(), null);
        }

        return new CategoryAddResponse(RtnCode.SUCCESSFUL.getCode(), RtnCode.SUCCESSFUL.getMessage(), null);

    }

    //  カテゴリー削除
    @Override
    public void categoryDelete(int id) {
        try {
            //          カテゴリー削除
            categoryDao.deleteById(id);
        } catch (Exception e) {
        }

    }

    //  全てのサブカテゴリーを検索
    @Override
    public Page<SubCategory> findSubCategoryPageByAll(boolean sortDescFlag, int pageNum, int pageSize) {
        
        Sort sort = null;
        if (sortDescFlag)
            sort = Sort.by(Sort.Direction.DESC, "id");
        else
            sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        //      全てのサブカテゴリーを検索
        Page<SubCategory> subCategoryList = subCategoryDao.findAll(pageable);

        return subCategoryList;
    }

    // 　カテゴリーでサブカテゴリーを検索
    @Override
    public Page<SubCategory> findSubCategoryPageByCategory(boolean sortDescFlag, int pageNum, int pageSize, String category) {
        
        Sort sort = null;
        if (sortDescFlag)
            sort = Sort.by(Sort.Direction.DESC, "id");
        else
            sort = Sort.by(Sort.Direction.ASC, "id");
        Pageable pageable = PageRequest.of(pageNum, pageSize, sort);
        //      カテゴリーでサブカテゴリーを検索
        Page<SubCategory> subCategoryList = subCategoryDao.findByCategory(pageable, category);

        return subCategoryList;
    }

    //  サブカテゴリー更新
    @Override
    public SubCategoryAddResponse subCategoryEdit(SubCategory subCategory) {

        //      引数がヌルのチェック
        if (subCategory == null) {
            return new SubCategoryAddResponse(RtnCode.DATA_EMPTY_ERROR.getCode(), RtnCode.DATA_EMPTY_ERROR.getMessage(), null);
        }
        //      カテゴリーがヌルのチェック
        if (subCategory.getCategory().isBlank()) {
            return new SubCategoryAddResponse(RtnCode.CATEGORY_EMPTY_ERROR.getCode(), RtnCode.CATEGORY_EMPTY_ERROR.getMessage(), null);
        }
        //      サブカテゴリーがヌルのチェック
        if (subCategory.getSubCategory().isBlank()) {
            return new SubCategoryAddResponse(RtnCode.SUB_CATEGORY_EMPTY_ERROR.getCode(), RtnCode.SUB_CATEGORY_EMPTY_ERROR.getMessage(), null);
        }
        //      サブカテゴリーの長さチェック
        if (subCategory.getSubCategory().length() > 20) {
            return new SubCategoryAddResponse(RtnCode.SUB_CATEGORY_OVER_LENGTH_ERROR.getCode(), RtnCode.SUB_CATEGORY_OVER_LENGTH_ERROR.getMessage(), null);
        }
        //      カテゴリーの既存判断
        if (!categoryDao.existsByCategory(subCategory.getCategory())) {
            return new SubCategoryAddResponse(RtnCode.CATEGORY_NOT_EXISTS_ERROR.getCode(), RtnCode.CATEGORY_NOT_EXISTS_ERROR.getMessage(), null);
        }
        //      カテゴリーとサブカテゴリーの既存判断
        if (subCategoryDao.existsByCategoryAndSubCategory(subCategory.getCategory(), subCategory.getSubCategory())) {
            return new SubCategoryAddResponse(RtnCode.NO_CHANGE_ERROR.getCode(), RtnCode.NO_CHANGE_ERROR.getMessage(), null);
        }

        //      サブカテゴリー更新
        int res = subCategoryDao.updateSubCategoryById(subCategory.getId(), subCategory.getCategory(), subCategory.getSubCategory());
        //      サブカテゴリー更新結果のチェック
        if (!(res > 0)) {
            return new SubCategoryAddResponse(RtnCode.DATA_ERROR.getCode(), RtnCode.DATA_ERROR.getMessage(), null);
        }

        return new SubCategoryAddResponse(RtnCode.SUCCESSFUL.getCode(), RtnCode.SUCCESSFUL.getMessage(), null);

    }

    //  サブカテゴリー削除
    @Override
    public void subCategoryDelete(int id) {
        try {
            subCategoryDao.deleteById(id);
         } catch (Exception e) {
        }

    }

    //  カテゴリーでニュースを検索
    @Override
    public List<News> findByCategory(String category) {
        List<News> newsList = newsDao.findByCategory(category);
        return newsList;
    }

    //  サブカテゴリーでニュースを検索
    @Override
    public List<News> findBySubCategory(String subCategory) {
        List<News> newsList = newsDao.findBySubCategory(subCategory);
        return newsList;
    }

    //  全てのカテゴリーを検索
    @Override
    public List<Category> findCategoryByAll() {
        List<Category> categoryList = categoryDao.findAll();
        return categoryList;
    }

    //  カテゴリーでサブカテゴリーを検索
    @Override
    public List<SubCategory> findSubCategoryByCategory(String category) {
        List<SubCategory> subCategoryList = subCategoryDao.findByCategory(category);
        return subCategoryList;
    }

    //  カテゴリーとサブカテゴリーでニュースを検索
    @Override
    public List<News> findByCategoryAndSubCategory(String category, String subCategory) {
        List<News> newsList = newsDao.findByCategoryAndSubCategory(category, subCategory);
        return newsList;
    }

    //  サブカテゴリーのニュース数を更新
    @Override
    public SubCategoryAddResponse subCategoryEditNewsCount(SubCategory subCategory) {

        //      引数がヌルのチェック
        if (subCategory == null) {
            return new SubCategoryAddResponse(RtnCode.DATA_EMPTY_ERROR.getCode(), RtnCode.DATA_EMPTY_ERROR.getMessage(), null);
        }
        //      カテゴリーがヌルのチェック
        if (subCategory.getCategory().isBlank()) {
            return new SubCategoryAddResponse(RtnCode.CATEGORY_EMPTY_ERROR.getCode(), RtnCode.CATEGORY_EMPTY_ERROR.getMessage(), null);
        }
        //      サブカテゴリーがヌルのチェック
        if (subCategory.getSubCategory().isBlank()) {
            return new SubCategoryAddResponse(RtnCode.SUB_CATEGORY_EMPTY_ERROR.getCode(), RtnCode.SUB_CATEGORY_EMPTY_ERROR.getMessage(), null);
        }
        //      サブカテゴリーの長さチェック
        if (subCategory.getSubCategory().length() > 20) {
            return new SubCategoryAddResponse(RtnCode.SUB_CATEGORY_OVER_LENGTH_ERROR.getCode(), RtnCode.SUB_CATEGORY_OVER_LENGTH_ERROR.getMessage(), null);
        }
        //      カテゴリーの既存判断
        if (!categoryDao.existsByCategory(subCategory.getCategory())) {
            return new SubCategoryAddResponse(RtnCode.CATEGORY_NOT_EXISTS_ERROR.getCode(), RtnCode.CATEGORY_NOT_EXISTS_ERROR.getMessage(), null);
        }

        //      サブカテゴリーのニュース数を更新
        int res = subCategoryDao.updateSubCategoryNewsCountById(subCategory.getId(), subCategory.getCategory(), subCategory.getSubCategory(),
                subCategory.getSubCategoryNewsCount());
        //      サブカテゴリーのニュース数を更新結果のチェック
        if (!(res > 0)) {
            return new SubCategoryAddResponse(RtnCode.DATA_ERROR.getCode(), RtnCode.DATA_ERROR.getMessage(), null);
        }

        return new SubCategoryAddResponse(RtnCode.SUCCESSFUL.getCode(), RtnCode.SUCCESSFUL.getMessage(), null);

    }

}
