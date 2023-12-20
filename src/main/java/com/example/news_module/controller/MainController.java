package com.example.news_module.controller;

import com.example.news_module.entity.Category;
import com.example.news_module.entity.News;
import com.example.news_module.entity.SubCategory;
import com.example.news_module.repository.CategoryDao;
import com.example.news_module.repository.NewsDao;
import com.example.news_module.repository.SubCategoryDao;
import com.example.news_module.service.ifs.MainService;
import com.example.news_module.vo.CategoryAddResponse;
import com.example.news_module.vo.CheckedRes;
import com.example.news_module.vo.MultipleSearch;
import com.example.news_module.vo.NewsAddResponse;
import com.example.news_module.vo.SubCategoryAddResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.ArrayList;
import java.util.List;

@Controller
public class MainController {

    //  newsAddCheck一時保管
    private NewsAddResponse newsAddCheckData;
    //  ニュース追加のカテゴリー
    private String newsAddCategorySelect;
    //  res一時保管
    private List<News> resTemp = new ArrayList<News>();
    ////////////////////////////////////////////////
    //  最後の検索
    private int lastSearch = 0;
    //  最後の検索キーワード
    private String lastKeyWordStr = "";
    //  最後の検索キーワード（複数条件）
    private MultipleSearch lastKeyWordMultipleStr = new MultipleSearch();
    //  昇順と降順
    private boolean sortDescFlag = false;
    //  最後のニュース検索結果
    private List<News> NewsListTemp = null;
    //  最後のチェック結果
    private CheckedRes checkedResTemp = null;
    //  最後のチェック数
    private int checkedResTempCount = 0;
    //  最後のチェックしたニュース
    private int idTemp = 0;
    ////////////////////////////////////////////////
    //  最後のカテゴリー検索結果
    private List<Category> CategoryListTemp = null;
    //  最後のカテゴリー検索結果のid
    private int categoryIdTemp = 0;
    ////////////////////////////////////////////////
    //  最後のサブカテゴリーの検索
    private int subCategoryLastSearch = 0;
    //  最後のサブカテゴリーの検索キーワード
    private String subCategorylastKeyWordStr = "";
    //  最後のサブカテゴリーの検索結果
    private List<SubCategory> SubCategoryListTemp = null;
    //  最後のサブカテゴリーの検索結果のid
    private int subCategoryIdTemp = 0;
    ////////////////////////////////////////////////
    //  ページ
    private int pageSize = 10;
    //  今のページ
    private int pageNumTemp = 0;
    ////////////////////////////////////////////////
    //  更新前のカテゴリー
    private String oldCategoryTemp = "";
    //  更新前のサブカテゴリー
    private String oldSubCategoryTemp = "";
    ////////////////////////////////////////////////

    //  Service
    @Autowired
    private MainService mainService;
    //  ニュースのDao
    @Autowired
    private NewsDao newsDao;
    //  カテゴリーのDao
    @Autowired
    private CategoryDao categoryDao;
    //  サブカテゴリーのDao
    @Autowired
    private SubCategoryDao subCateogryDao;

    //////////////////////////////////////////////////
    //  ニュースのホームページ
    @RequestMapping(value = "/home/{pageNum}")
    public String Home(@PathVariable(value = "pageNum", required = false) int pageNum,
                       Model model) {
        //        検索結果が無し場合、ページがマイナスになるのを防止する
        if (pageNum == -1)
            pageNum = 0;
        //        今のページをpageNumTempに保存する
        pageNumTemp = pageNum;

        //          カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //          サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //          ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //          ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //          発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //          発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //          複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        //          カテゴリーでニュース検索
        if (lastSearch == 1)
            news01.setCategory(lastKeyWordStr);
        //          サブカテゴリーでニュース検索
        if (lastSearch == 2)
            news02.setSubCategory(lastKeyWordStr);
        //          ニュースタイルでニュース検索
        if (lastSearch == 3)
            news03.setNewsTitle(lastKeyWordStr);
        //          ニュースのサブタイルでニュース検索
        if (lastSearch == 4)
            news04.setNewsSubTitle(lastKeyWordStr);
        //          発表日以降でニュース検索
        if (lastSearch == 5)
            news05.setReleaseTime(lastKeyWordStr);
        //          発表日以前でニュース検索
        if (lastSearch == 6)
            news06.setReleaseTime(lastKeyWordStr);
        //          複数条件でニュース検索
        if (lastSearch == 7)
            news07 = lastKeyWordMultipleStr;

        //          ニュース検索用の変数をThymeleafに設定する
        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        //　　　　　　ニュースのカテゴリーをThymeleafに設定する
        model.addAttribute("categoryList", categoryListInitializer());

        //          ニュースのサブカテゴリーをThymeleafに設定する
        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //          チェックボックスの入力用変数を初期化する
        CheckedRes checkedRes = new CheckedRes();
        //          チェックボックスの入力用変数をThymeleafに設定する
        model.addAttribute("checkedRes", checkedRes);

        //          今のページの変数をThymeleafに設定する
        model.addAttribute("pageNum", pageNum);
        //          各ページの本数の変数をThymeleafに設定する
        model.addAttribute("pageSize", pageSize);

        //          ニュースの検索結果をThymeleafに設定する
        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        //          ニュースの検索結果の各ページの本数をThymeleafに設定する
        model.addAttribute("newsPageSize",
                NewsSearch(pageNum, pageSize).getNumberOfElements());

        //      カテゴリーとサブカテゴリーのニュースの数を更新する
        List<Category> findCategoryByAllRes = null;
        List<News> findByCategoryRes = null;
        List<News> findByCategoryAndSubCategoryRes = null;
        List<SubCategory> findSubCategoryByCategoryRes = null;
        int newsCount = 0;
        findCategoryByAllRes = mainService.findCategoryByAll();
        for (Category item : findCategoryByAllRes) {
            findByCategoryRes = mainService.findByCategory(item.getCategory());
            newsCount = findByCategoryRes.size();
            item.setNewsCount(newsCount);
            mainService.categoryEditNewsCount(item);
            findSubCategoryByCategoryRes = mainService.findSubCategoryByCategory(item.getCategory());
            for (SubCategory item02 : findSubCategoryByCategoryRes) {
                findByCategoryAndSubCategoryRes = mainService.findByCategoryAndSubCategory(item02.getCategory(),
                        item02.getSubCategory());
                newsCount = findByCategoryAndSubCategoryRes.size();
                item02.setSubCategoryNewsCount(newsCount);
                mainService.subCategoryEditNewsCount(item02);
            }
        }

        //      ニュースのホームページに移動する
        return "home";
    }

    //  カテゴリーでニュースを検索
    @PostMapping("/home_search_category")
    public String HomeSearchCategory(@ModelAttribute("news01") News news, Model model) {
        newsAddCategorySelect = news.getCategory();
        //        最後の検査条件を更新
        lastSearch = 1;
        lastKeyWordStr = news.getCategory();

        //        カテゴリーでニュース検索用の変数を初期化する
        News news01 = news;
        //          サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //          ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //          ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //          発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //          発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //          複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());
        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //          チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //      今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        return "home";

    }

    //  サブカテゴリーでニュースを検索    
    @PostMapping("/home_search_sub_category")
    public String HomeSearchSubCategory(@ModelAttribute("news02") News news, Model model) {
        //      最後の検査条件を更新
        lastSearch = 2;
        lastKeyWordStr = news.getSubCategory();

        //            カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //            サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = news;
        //            ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //            ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //            発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //            発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //            複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //          チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //          今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        return "home";

    }

    //  ニュースのタイルでニュースを検索
    @PostMapping("/home_search_news_title")
    public String HomeSearchNewsTitle(@ModelAttribute("news03") News news, Model model) {
        //          最後の検査条件を更新
        lastSearch = 3;
        lastKeyWordStr = news.getNewsTitle();

        //            カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //            サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //            ニュースタイルでニュース検索用の変数を初期化する
        News news03 = news;
        //            ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //            発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //            発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //            複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //          チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //          今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        return "home";

    }

    //  ニュースのサブカテゴリーでニュースを検索
    @PostMapping("/home_search_news_sub_title")
    public String HomeSearchNewsSubTitle(@ModelAttribute("news04") News news, Model model) {
        //          最後の検査条件を更新
        lastSearch = 4;
        lastKeyWordStr = news.getNewsSubTitle();

        //            カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //            サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //            ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //            ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = news;
        //            発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //            発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //            複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //          チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //          今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        return "home";

    }

    //  発表日以降でニュースを検索
    @PostMapping("/home_search_start_time")
    public String HomeSearchStartTime(@ModelAttribute("news05") News news, Model model) {
        //          最後の検査条件を更新
        lastSearch = 5;
        lastKeyWordStr = news.getReleaseTime();

        //            カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //            サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //            ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //            ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //            発表日以降でニュース検索用の変数を初期化する
        News news05 = news;
        //            発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //            複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //          チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //            今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        return "home";

    }

    //  発表日以前でニュースを検索
    @PostMapping("/home_search_end_time")
    public String HomeSearchEndTime(@ModelAttribute("news06") News news, Model model) {
        //          最後の検査条件を更新
        lastSearch = 6;
        lastKeyWordStr = news.getReleaseTime();

        //            カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //            サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //            ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //            ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //            発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //            発表日以前でニュース検索用の変数を初期化する
        News news06 = news;
        //            複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //          チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //            今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        return "home";

    }

    //  複数条件でニュースを検索
    @PostMapping("/news_multiple_search")
    public String NewsMultipleSearch(@ModelAttribute("news07") MultipleSearch news, Model model) {
        //　最後の検査条件を更新
        lastSearch = 7;
        lastKeyWordMultipleStr = news;

        //            カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //            サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //            ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //            ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //            発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //            発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //            複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = news;

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //          チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //          今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        return "home";

    }

    //  チェック結果を確認
    @PostMapping("/home")
    public String HomeChecked(@ModelAttribute("checkedRes") CheckedRes checkedRes, Model model) {
        //    チェック結果を更新
        checkedResTemp = checkedRes;

        checkedResTempCount = 0;
        //    チェック結果を確認
        if (checkedRes.isChecked1())
            checkedResTempCount++;
        if (checkedRes.isChecked2())
            checkedResTempCount++;
        if (checkedRes.isChecked3())
            checkedResTempCount++;
        if (checkedRes.isChecked4())
            checkedResTempCount++;
        if (checkedRes.isChecked5())
            checkedResTempCount++;
        if (checkedRes.isChecked6())
            checkedResTempCount++;
        if (checkedRes.isChecked7())
            checkedResTempCount++;
        if (checkedRes.isChecked8())
            checkedResTempCount++;
        if (checkedRes.isChecked9())
            checkedResTempCount++;
        if (checkedRes.isChecked10())
            checkedResTempCount++;
        if (checkedRes.isChecked11())
            checkedResTempCount++;
        if (checkedRes.isChecked12())
            checkedResTempCount++;
        if (checkedRes.isChecked13())
            checkedResTempCount++;
        if (checkedRes.isChecked14())
            checkedResTempCount++;
        if (checkedRes.isChecked15())
            checkedResTempCount++;
        if (checkedRes.isChecked16())
            checkedResTempCount++;
        if (checkedRes.isChecked17())
            checkedResTempCount++;
        if (checkedRes.isChecked18())
            checkedResTempCount++;
        if (checkedRes.isChecked19())
            checkedResTempCount++;
        if (checkedRes.isChecked20())
            checkedResTempCount++;
        if (checkedRes.isChecked21())
            checkedResTempCount++;
        if (checkedRes.isChecked22())
            checkedResTempCount++;
        if (checkedRes.isChecked23())
            checkedResTempCount++;
        if (checkedRes.isChecked24())
            checkedResTempCount++;
        if (checkedRes.isChecked25())
            checkedResTempCount++;
        if (checkedRes.isChecked26())
            checkedResTempCount++;
        if (checkedRes.isChecked27())
            checkedResTempCount++;
        if (checkedRes.isChecked28())
            checkedResTempCount++;
        if (checkedRes.isChecked29())
            checkedResTempCount++;
        if (checkedRes.isChecked30())
            checkedResTempCount++;
        if (checkedRes.isChecked31())
            checkedResTempCount++;
        if (checkedRes.isChecked32())
            checkedResTempCount++;
        if (checkedRes.isChecked33())
            checkedResTempCount++;
        if (checkedRes.isChecked34())
            checkedResTempCount++;
        if (checkedRes.isChecked35())
            checkedResTempCount++;
        if (checkedRes.isChecked36())
            checkedResTempCount++;
        if (checkedRes.isChecked37())
            checkedResTempCount++;
        if (checkedRes.isChecked38())
            checkedResTempCount++;
        if (checkedRes.isChecked39())
            checkedResTempCount++;
        if (checkedRes.isChecked40())
            checkedResTempCount++;
        if (checkedRes.isChecked41())
            checkedResTempCount++;
        if (checkedRes.isChecked42())
            checkedResTempCount++;
        if (checkedRes.isChecked43())
            checkedResTempCount++;
        if (checkedRes.isChecked44())
            checkedResTempCount++;
        if (checkedRes.isChecked45())
            checkedResTempCount++;
        if (checkedRes.isChecked46())
            checkedResTempCount++;
        if (checkedRes.isChecked47())
            checkedResTempCount++;
        if (checkedRes.isChecked48())
            checkedResTempCount++;
        if (checkedRes.isChecked49())
            checkedResTempCount++;
        if (checkedRes.isChecked50())
            checkedResTempCount++;
        if (checkedRes.isChecked51())
            checkedResTempCount++;
        if (checkedRes.isChecked52())
            checkedResTempCount++;
        if (checkedRes.isChecked53())
            checkedResTempCount++;
        if (checkedRes.isChecked54())
            checkedResTempCount++;
        if (checkedRes.isChecked55())
            checkedResTempCount++;
        if (checkedRes.isChecked56())
            checkedResTempCount++;
        if (checkedRes.isChecked57())
            checkedResTempCount++;
        if (checkedRes.isChecked58())
            checkedResTempCount++;
        if (checkedRes.isChecked59())
            checkedResTempCount++;
        if (checkedRes.isChecked60())
            checkedResTempCount++;
        if (checkedRes.isChecked61())
            checkedResTempCount++;
        if (checkedRes.isChecked62())
            checkedResTempCount++;
        if (checkedRes.isChecked63())
            checkedResTempCount++;
        if (checkedRes.isChecked64())
            checkedResTempCount++;
        if (checkedRes.isChecked65())
            checkedResTempCount++;
        if (checkedRes.isChecked66())
            checkedResTempCount++;
        if (checkedRes.isChecked67())
            checkedResTempCount++;
        if (checkedRes.isChecked68())
            checkedResTempCount++;
        if (checkedRes.isChecked69())
            checkedResTempCount++;
        if (checkedRes.isChecked70())
            checkedResTempCount++;
        if (checkedRes.isChecked71())
            checkedResTempCount++;
        if (checkedRes.isChecked72())
            checkedResTempCount++;
        if (checkedRes.isChecked73())
            checkedResTempCount++;
        if (checkedRes.isChecked74())
            checkedResTempCount++;
        if (checkedRes.isChecked75())
            checkedResTempCount++;
        if (checkedRes.isChecked76())
            checkedResTempCount++;
        if (checkedRes.isChecked77())
            checkedResTempCount++;
        if (checkedRes.isChecked78())
            checkedResTempCount++;
        if (checkedRes.isChecked79())
            checkedResTempCount++;
        if (checkedRes.isChecked80())
            checkedResTempCount++;
        if (checkedRes.isChecked81())
            checkedResTempCount++;
        if (checkedRes.isChecked82())
            checkedResTempCount++;
        if (checkedRes.isChecked83())
            checkedResTempCount++;
        if (checkedRes.isChecked84())
            checkedResTempCount++;
        if (checkedRes.isChecked85())
            checkedResTempCount++;
        if (checkedRes.isChecked86())
            checkedResTempCount++;
        if (checkedRes.isChecked87())
            checkedResTempCount++;
        if (checkedRes.isChecked88())
            checkedResTempCount++;
        if (checkedRes.isChecked89())
            checkedResTempCount++;
        if (checkedRes.isChecked90())
            checkedResTempCount++;
        if (checkedRes.isChecked91())
            checkedResTempCount++;
        if (checkedRes.isChecked92())
            checkedResTempCount++;
        if (checkedRes.isChecked93())
            checkedResTempCount++;
        if (checkedRes.isChecked94())
            checkedResTempCount++;
        if (checkedRes.isChecked95())
            checkedResTempCount++;
        if (checkedRes.isChecked96())
            checkedResTempCount++;
        if (checkedRes.isChecked97())
            checkedResTempCount++;
        if (checkedRes.isChecked98())
            checkedResTempCount++;
        if (checkedRes.isChecked99())
            checkedResTempCount++;
        if (checkedRes.isChecked100())
            checkedResTempCount++;

        News news01 = new News();
        News news02 = new News();
        News news03 = new News();
        News news04 = new News();
        News news05 = new News();
        News news06 = new News();
        MultipleSearch news07 = new MultipleSearch();

        //  ニュースの検索キーワードを更新
        if (lastSearch == 1)
            news01.setCategory(lastKeyWordStr);
        if (lastSearch == 2)
            news02.setSubCategory(lastKeyWordStr);
        if (lastSearch == 3)
            news03.setNewsTitle(lastKeyWordStr);
        if (lastSearch == 4)
            news04.setNewsSubTitle(lastKeyWordStr);
        if (lastSearch == 5)
            news05.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 6)
            news06.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 7)
            news07 = lastKeyWordMultipleStr;

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //  チェック用
        checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //  今のページをゼロに設定
        int pageNum = pageNumTemp;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        return "home";
    }

    //  ニュースの検索結果を全てチェック
    @RequestMapping("/all_check")
    public String HomeAllChecked(Model model) {

        CheckedRes checkedRes = new CheckedRes();
        //    ニュースの検索結果を全てチェック
        checkedRes.setChecked1(true);
        checkedRes.setChecked2(true);
        checkedRes.setChecked3(true);
        checkedRes.setChecked4(true);
        checkedRes.setChecked5(true);
        checkedRes.setChecked6(true);
        checkedRes.setChecked7(true);
        checkedRes.setChecked8(true);
        checkedRes.setChecked9(true);
        checkedRes.setChecked10(true);
        checkedRes.setChecked11(true);
        checkedRes.setChecked12(true);
        checkedRes.setChecked13(true);
        checkedRes.setChecked14(true);
        checkedRes.setChecked15(true);
        checkedRes.setChecked16(true);
        checkedRes.setChecked17(true);
        checkedRes.setChecked18(true);
        checkedRes.setChecked19(true);
        checkedRes.setChecked20(true);
        checkedRes.setChecked21(true);
        checkedRes.setChecked22(true);
        checkedRes.setChecked23(true);
        checkedRes.setChecked24(true);
        checkedRes.setChecked25(true);
        checkedRes.setChecked26(true);
        checkedRes.setChecked27(true);
        checkedRes.setChecked28(true);
        checkedRes.setChecked29(true);
        checkedRes.setChecked30(true);
        checkedRes.setChecked31(true);
        checkedRes.setChecked32(true);
        checkedRes.setChecked33(true);
        checkedRes.setChecked34(true);
        checkedRes.setChecked35(true);
        checkedRes.setChecked36(true);
        checkedRes.setChecked37(true);
        checkedRes.setChecked38(true);
        checkedRes.setChecked39(true);
        checkedRes.setChecked40(true);
        checkedRes.setChecked41(true);
        checkedRes.setChecked42(true);
        checkedRes.setChecked43(true);
        checkedRes.setChecked44(true);
        checkedRes.setChecked45(true);
        checkedRes.setChecked46(true);
        checkedRes.setChecked47(true);
        checkedRes.setChecked48(true);
        checkedRes.setChecked49(true);
        checkedRes.setChecked50(true);
        checkedRes.setChecked51(true);
        checkedRes.setChecked52(true);
        checkedRes.setChecked53(true);
        checkedRes.setChecked54(true);
        checkedRes.setChecked55(true);
        checkedRes.setChecked56(true);
        checkedRes.setChecked57(true);
        checkedRes.setChecked58(true);
        checkedRes.setChecked59(true);
        checkedRes.setChecked60(true);
        checkedRes.setChecked61(true);
        checkedRes.setChecked62(true);
        checkedRes.setChecked63(true);
        checkedRes.setChecked64(true);
        checkedRes.setChecked65(true);
        checkedRes.setChecked66(true);
        checkedRes.setChecked67(true);
        checkedRes.setChecked68(true);
        checkedRes.setChecked69(true);
        checkedRes.setChecked70(true);
        checkedRes.setChecked71(true);
        checkedRes.setChecked72(true);
        checkedRes.setChecked73(true);
        checkedRes.setChecked74(true);
        checkedRes.setChecked75(true);
        checkedRes.setChecked76(true);
        checkedRes.setChecked77(true);
        checkedRes.setChecked78(true);
        checkedRes.setChecked79(true);
        checkedRes.setChecked80(true);
        checkedRes.setChecked81(true);
        checkedRes.setChecked82(true);
        checkedRes.setChecked83(true);
        checkedRes.setChecked84(true);
        checkedRes.setChecked85(true);
        checkedRes.setChecked86(true);
        checkedRes.setChecked87(true);
        checkedRes.setChecked88(true);
        checkedRes.setChecked89(true);
        checkedRes.setChecked90(true);
        checkedRes.setChecked91(true);
        checkedRes.setChecked92(true);
        checkedRes.setChecked93(true);
        checkedRes.setChecked94(true);
        checkedRes.setChecked95(true);
        checkedRes.setChecked96(true);
        checkedRes.setChecked97(true);
        checkedRes.setChecked98(true);
        checkedRes.setChecked99(true);
        checkedRes.setChecked100(true);

        //    カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //    サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //    ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //    ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //    発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //    発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //    複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        //    ニュースの検索キーワードを更新
        if (lastSearch == 1)
            news01.setCategory(lastKeyWordStr);
        if (lastSearch == 2)
            news02.setSubCategory(lastKeyWordStr);
        if (lastSearch == 3)
            news03.setNewsTitle(lastKeyWordStr);
        if (lastSearch == 4)
            news04.setNewsSubTitle(lastKeyWordStr);
        if (lastSearch == 5)
            news05.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 6)
            news06.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 7)
            news07 = lastKeyWordMultipleStr;

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        model.addAttribute("checkedRes", checkedRes);

        //  今のページをゼロに設定
        int pageNum = pageNumTemp;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        return "home";
    }

    //////////////////////////////////////////////////
    //  ニュース追加
    @RequestMapping(value = "/news_add")
    public String NewsAdd(Model model) {

        News news = new News();

        model.addAttribute("news", news);

        model.addAttribute("error", "");

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        return "news_add";
    }

    //  ニュース追加
    @PostMapping("/news_add")
    public String NewsAdd(@ModelAttribute("news") News news, Model model) {
        //      ニュース追加
        NewsAddResponse res = mainService.newsAddCheck(news);
        //      ニュース追加結果をチェック
        if (res.getCode() != "200") {
            newsAddCategorySelect = news.getCategory();

            model.addAttribute("categoryList", categoryListInitializer());

            model.addAttribute("subCategoryList", subCategoryListInitializer());

            model.addAttribute("error", res.getMessage());

            return "news_add";
        }
        //      追加ニュースの更新
        newsAddCheckData = res;

        return "news_add_preview";
    }

    //  ニュース更新　    
    @RequestMapping(value = "/news_edit")
    public String NewsEdit(Model model) {

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //          カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //          サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //          ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //          ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //          発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //          発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //          複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        //          ニュースの検査キーワードを更新
        if (lastSearch == 1)
            news01.setCategory(lastKeyWordStr);
        if (lastSearch == 2)
            news02.setSubCategory(lastKeyWordStr);
        if (lastSearch == 3)
            news03.setNewsTitle(lastKeyWordStr);
        if (lastSearch == 4)
            news04.setNewsSubTitle(lastKeyWordStr);
        if (lastSearch == 5)
            news05.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 6)
            news06.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 7)
            news07 = lastKeyWordMultipleStr;

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        //        チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //        今のページをゼロに設定
        int pageNum = 0;
        //          各ページの件数を10に設定
        int pageSize = 10;

        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        if (checkedResTemp == null) {

            return "home";
        }
        //      複合チェックの確認
        if (checkedResTempCount >= 2) {

            return "home";
        }
        //      選択したニュースを獲得
        News news = NewsListTempReader();
        idTemp = news.getId();

        model.addAttribute("news", news);
        model.addAttribute("error", "");
        checkedResTemp = null;

        return "news_edit";
    }

    //  ニュース更新
    @PostMapping("/news_edit")
    public String NewsEdit(@ModelAttribute("news") News news, Model model) {
        //      ニュース更新
        NewsAddResponse res = mainService.newsEditCheck(news);
        //      ニュース更新結果をチェック
        if (res.getCode() != "200") {
            //          選択したカテゴリーを更新
            newsAddCategorySelect = news.getCategory();

            model.addAttribute("categoryList", categoryListInitializer());

            model.addAttribute("subCategoryList", subCategoryListInitializer());

            model.addAttribute("error", res.getMessage());

            return "news_edit";
        }
        //      更新のニュースを更新
        newsAddCheckData = res;

        return "news_edit_preview";
    }

    //  ニュース追加のプレビュー
    @PostMapping("/news_add_preview")
    public String NewsAddPreview(Model model) {

        //      ニュース追加
        newsAddCheckData = mainService.newsAdd(newsAddCheckData);
        //      ニュース追加結果をチェック
        if (newsAddCheckData.getCode() != "200") {
            model.addAttribute("error", newsAddCheckData.getMessage());

            return "news_add_preview";
        }

        newsAddCheckData = null;

        //        カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //        サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //        ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //        ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //        発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //        発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //        複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        //        ニュースの検索キーワードを更新
        if (lastSearch == 1)
            news01.setCategory(lastKeyWordStr);
        if (lastSearch == 2)
            news02.setSubCategory(lastKeyWordStr);
        if (lastSearch == 3)
            news03.setNewsTitle(lastKeyWordStr);
        if (lastSearch == 4)
            news04.setNewsSubTitle(lastKeyWordStr);
        if (lastSearch == 5)
            news05.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 6)
            news06.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 7)
            news07 = lastKeyWordMultipleStr;

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //      チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //      今のページをゼロに設定
        int pageNum = 0;
        //        各ページの件数を10に設定
        int pageSize = 10;

        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        return "home";
    }

    //  ニュース更新のプレビュー
    @PostMapping("/news_edit_preview")
    public String NewsEditPreview(Model model) {

        //        カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //        サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //        ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //        ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //        発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //        発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //        複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        //        ニュースの検索キーワードを更新
        if (lastSearch == 1)
            news01.setCategory(lastKeyWordStr);
        if (lastSearch == 2)
            news02.setSubCategory(lastKeyWordStr);
        if (lastSearch == 3)
            news03.setNewsTitle(lastKeyWordStr);
        if (lastSearch == 4)
            news04.setNewsSubTitle(lastKeyWordStr);
        if (lastSearch == 5)
            news05.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 6)
            news06.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 7)
            news07 = lastKeyWordMultipleStr;

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //        チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //      今のページをゼロに設定
        int pageNum = 0;
        //        各ページの件数を10に設定
        int pageSize = 10;

        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        //    ニュースの検索結果のidを更新
        newsAddCheckData.getNews().setId(idTemp);
        //    ニュース更新
        newsAddCheckData = mainService.newsEdit(newsAddCheckData);
        //    ニュース更新結果をチェック
        if (newsAddCheckData.getCode() != "200") {

            model.addAttribute("error", newsAddCheckData.getMessage());

            return "news_edit_preview";
        }

        newsAddCheckData = null;

        return "home";
    }

    //  ニュース削除
    @RequestMapping(value = "/news_delete")
    public String NewsDelete(@RequestParam(value = "pageSize", defaultValue = "3") int pageSize, Model model) {

        //        カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //        サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //        ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //        ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //        発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //        発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //        複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        //        ニュースの検索キーワードを更新
        if (lastSearch == 1)
            news01.setCategory(lastKeyWordStr);
        if (lastSearch == 2)
            news02.setSubCategory(lastKeyWordStr);
        if (lastSearch == 3)
            news03.setNewsTitle(lastKeyWordStr);
        if (lastSearch == 4)
            news04.setNewsSubTitle(lastKeyWordStr);
        if (lastSearch == 5)
            news05.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 6)
            news06.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 7)
            news07 = lastKeyWordMultipleStr;

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //      チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //      今のページをゼロに設定
        int pageNum = 0;
        //        各ページの件数を10に設定
        pageSize = 10;

        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        //        チェックしてないを確認
        if (checkedResTemp == null) {

            return "home";
        }

        int[] checked = new int[100];
        for (int i = 0; i < 100; i++) {
            checked[i] = 0;
        }
        //        チェック結果を確認
        if ((checkedResTemp.isChecked1() && pageNumTemp == 0)
            || (checkedResTemp.isChecked1() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked1() && pageSize == 50 && pageNumTemp != 0))
            checked[0] = 1;
        if ((checkedResTemp.isChecked2() && pageNumTemp == 0)
            || (checkedResTemp.isChecked2() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked2() && pageSize == 50 && pageNumTemp != 0))
            checked[1] = 1;
        if ((checkedResTemp.isChecked3() && pageNumTemp == 0)
            || (checkedResTemp.isChecked3() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked3() && pageSize == 50 && pageNumTemp != 0))
            checked[2] = 1;
        if ((checkedResTemp.isChecked4() && pageNumTemp == 0)
            || (checkedResTemp.isChecked4() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked4() && pageSize == 50 && pageNumTemp != 0))
            checked[3] = 1;
        if ((checkedResTemp.isChecked5() && pageNumTemp == 0)
            || (checkedResTemp.isChecked5() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked5() && pageSize == 50 && pageNumTemp != 0))
            checked[4] = 1;
        if ((checkedResTemp.isChecked6() && pageNumTemp == 0)
            || (checkedResTemp.isChecked6() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked6() && pageSize == 50 && pageNumTemp != 0))
            checked[5] = 1;
        if ((checkedResTemp.isChecked7() && pageNumTemp == 0)
            || (checkedResTemp.isChecked7() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked7() && pageSize == 50 && pageNumTemp != 0))
            checked[6] = 1;
        if ((checkedResTemp.isChecked8() && pageNumTemp == 0)
            || (checkedResTemp.isChecked8() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked8() && pageSize == 50 && pageNumTemp != 0))
            checked[7] = 1;
        if ((checkedResTemp.isChecked9() && pageNumTemp == 0)
            || (checkedResTemp.isChecked9() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked9() && pageSize == 50 && pageNumTemp != 0))
            checked[8] = 1;
        if ((checkedResTemp.isChecked10() && pageNumTemp == 0)
            || (checkedResTemp.isChecked10() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked10() && pageSize == 50 && pageNumTemp != 0))
            checked[9] = 1;
        if ((checkedResTemp.isChecked11() && pageNumTemp == 0)
            || (checkedResTemp.isChecked11() && pageSize == 50 && pageNumTemp != 0))
            checked[10] = 1;
        if ((checkedResTemp.isChecked12() && pageNumTemp == 0)
            || (checkedResTemp.isChecked12() && pageSize == 50 && pageNumTemp != 0))
            checked[11] = 1;
        if ((checkedResTemp.isChecked13() && pageNumTemp == 0)
            || (checkedResTemp.isChecked13() && pageSize == 50 && pageNumTemp != 0))
            checked[12] = 1;
        if ((checkedResTemp.isChecked14() && pageNumTemp == 0)
            || (checkedResTemp.isChecked14() && pageSize == 50 && pageNumTemp != 0))
            checked[13] = 1;
        if ((checkedResTemp.isChecked15() && pageNumTemp == 0)
            || (checkedResTemp.isChecked15() && pageSize == 50 && pageNumTemp != 0))
            checked[14] = 1;
        if ((checkedResTemp.isChecked16() && pageNumTemp == 0)
            || (checkedResTemp.isChecked16() && pageSize == 50 && pageNumTemp != 0))
            checked[15] = 1;
        if ((checkedResTemp.isChecked17() && pageNumTemp == 0)
            || (checkedResTemp.isChecked17() && pageSize == 50 && pageNumTemp != 0))
            checked[16] = 1;
        if ((checkedResTemp.isChecked18() && pageNumTemp == 0)
            || (checkedResTemp.isChecked18() && pageSize == 50 && pageNumTemp != 0))
            checked[17] = 1;
        if ((checkedResTemp.isChecked19() && pageNumTemp == 0)
            || (checkedResTemp.isChecked19() && pageSize == 50 && pageNumTemp != 0))
            checked[18] = 1;
        if ((checkedResTemp.isChecked20() && pageNumTemp == 0)
            || (checkedResTemp.isChecked20() && pageSize == 50 && pageNumTemp != 0))
            checked[19] = 1;
        if ((checkedResTemp.isChecked21() && pageNumTemp == 0)
            || (checkedResTemp.isChecked21() && pageSize == 50 && pageNumTemp != 0))
            checked[20] = 1;
        if ((checkedResTemp.isChecked22() && pageNumTemp == 0)
            || (checkedResTemp.isChecked22() && pageSize == 50 && pageNumTemp != 0))
            checked[21] = 1;
        if ((checkedResTemp.isChecked23() && pageNumTemp == 0)
            || (checkedResTemp.isChecked23() && pageSize == 50 && pageNumTemp != 0))
            checked[22] = 1;
        if ((checkedResTemp.isChecked24() && pageNumTemp == 0)
            || (checkedResTemp.isChecked24() && pageSize == 50 && pageNumTemp != 0))
            checked[23] = 1;
        if ((checkedResTemp.isChecked25() && pageNumTemp == 0)
            || (checkedResTemp.isChecked25() && pageSize == 50 && pageNumTemp != 0))
            checked[24] = 1;
        if ((checkedResTemp.isChecked26() && pageNumTemp == 0)
            || (checkedResTemp.isChecked26() && pageSize == 50 && pageNumTemp != 0))
            checked[25] = 1;
        if ((checkedResTemp.isChecked27() && pageNumTemp == 0)
            || (checkedResTemp.isChecked27() && pageSize == 50 && pageNumTemp != 0))
            checked[26] = 1;
        if ((checkedResTemp.isChecked28() && pageNumTemp == 0)
            || (checkedResTemp.isChecked28() && pageSize == 50 && pageNumTemp != 0))
            checked[27] = 1;
        if ((checkedResTemp.isChecked29() && pageNumTemp == 0)
            || (checkedResTemp.isChecked29() && pageSize == 50 && pageNumTemp != 0))
            checked[28] = 1;
        if ((checkedResTemp.isChecked30() && pageNumTemp == 0)
            || (checkedResTemp.isChecked30() && pageSize == 50 && pageNumTemp != 0))
            checked[29] = 1;
        if ((checkedResTemp.isChecked31() && pageNumTemp == 0)
            || (checkedResTemp.isChecked31() && pageSize == 50 && pageNumTemp != 0))
            checked[30] = 1;
        if ((checkedResTemp.isChecked32() && pageNumTemp == 0)
            || (checkedResTemp.isChecked32() && pageSize == 50 && pageNumTemp != 0))
            checked[31] = 1;
        if ((checkedResTemp.isChecked33() && pageNumTemp == 0)
            || (checkedResTemp.isChecked33() && pageSize == 50 && pageNumTemp != 0))
            checked[32] = 1;
        if ((checkedResTemp.isChecked34() && pageNumTemp == 0)
            || (checkedResTemp.isChecked34() && pageSize == 50 && pageNumTemp != 0))
            checked[33] = 1;
        if ((checkedResTemp.isChecked35() && pageNumTemp == 0)
            || (checkedResTemp.isChecked35() && pageSize == 50 && pageNumTemp != 0))
            checked[34] = 1;
        if ((checkedResTemp.isChecked36() && pageNumTemp == 0)
            || (checkedResTemp.isChecked36() && pageSize == 50 && pageNumTemp != 0))
            checked[35] = 1;
        if ((checkedResTemp.isChecked37() && pageNumTemp == 0)
            || (checkedResTemp.isChecked37() && pageSize == 50 && pageNumTemp != 0))
            checked[36] = 1;
        if ((checkedResTemp.isChecked38() && pageNumTemp == 0)
            || (checkedResTemp.isChecked38() && pageSize == 50 && pageNumTemp != 0))
            checked[37] = 1;
        if ((checkedResTemp.isChecked39() && pageNumTemp == 0)
            || (checkedResTemp.isChecked39() && pageSize == 50 && pageNumTemp != 0))
            checked[38] = 1;
        if ((checkedResTemp.isChecked40() && pageNumTemp == 0)
            || (checkedResTemp.isChecked40() && pageSize == 50 && pageNumTemp != 0))
            checked[39] = 1;
        if ((checkedResTemp.isChecked41() && pageNumTemp == 0)
            || (checkedResTemp.isChecked41() && pageSize == 50 && pageNumTemp != 0))
            checked[40] = 1;
        if ((checkedResTemp.isChecked42() && pageNumTemp == 0)
            || (checkedResTemp.isChecked42() && pageSize == 50 && pageNumTemp != 0))
            checked[41] = 1;
        if ((checkedResTemp.isChecked43() && pageNumTemp == 0)
            || (checkedResTemp.isChecked43() && pageSize == 50 && pageNumTemp != 0))
            checked[42] = 1;
        if ((checkedResTemp.isChecked44() && pageNumTemp == 0)
            || (checkedResTemp.isChecked44() && pageSize == 50 && pageNumTemp != 0))
            checked[43] = 1;
        if ((checkedResTemp.isChecked45() && pageNumTemp == 0)
            || (checkedResTemp.isChecked45() && pageSize == 50 && pageNumTemp != 0))
            checked[44] = 1;
        if ((checkedResTemp.isChecked46() && pageNumTemp == 0)
            || (checkedResTemp.isChecked46() && pageSize == 50 && pageNumTemp != 0))
            checked[45] = 1;
        if ((checkedResTemp.isChecked47() && pageNumTemp == 0)
            || (checkedResTemp.isChecked47() && pageSize == 50 && pageNumTemp != 0))
            checked[46] = 1;
        if ((checkedResTemp.isChecked48() && pageNumTemp == 0)
            || (checkedResTemp.isChecked48() && pageSize == 50 && pageNumTemp != 0))
            checked[47] = 1;
        if ((checkedResTemp.isChecked49() && pageNumTemp == 0)
            || (checkedResTemp.isChecked49() && pageSize == 50 && pageNumTemp != 0))
            checked[48] = 1;
        if ((checkedResTemp.isChecked50() && pageNumTemp == 0)
            || (checkedResTemp.isChecked50() && pageSize == 50 && pageNumTemp != 0))
            checked[49] = 1;
        if (checkedResTemp.isChecked51() && pageNumTemp == 0)
            checked[50] = 1;
        if (checkedResTemp.isChecked52() && pageNumTemp == 0)
            checked[51] = 1;
        if (checkedResTemp.isChecked53() && pageNumTemp == 0)
            checked[52] = 1;
        if (checkedResTemp.isChecked54() && pageNumTemp == 0)
            checked[53] = 1;
        if (checkedResTemp.isChecked55() && pageNumTemp == 0)
            checked[54] = 1;
        if (checkedResTemp.isChecked56() && pageNumTemp == 0)
            checked[55] = 1;
        if (checkedResTemp.isChecked57() && pageNumTemp == 0)
            checked[56] = 1;
        if (checkedResTemp.isChecked58() && pageNumTemp == 0)
            checked[57] = 1;
        if (checkedResTemp.isChecked59() && pageNumTemp == 0)
            checked[58] = 1;
        if (checkedResTemp.isChecked60() && pageNumTemp == 0)
            checked[59] = 1;
        if (checkedResTemp.isChecked61() && pageNumTemp == 0)
            checked[60] = 1;
        if (checkedResTemp.isChecked62() && pageNumTemp == 0)
            checked[61] = 1;
        if (checkedResTemp.isChecked63() && pageNumTemp == 0)
            checked[62] = 1;
        if (checkedResTemp.isChecked64() && pageNumTemp == 0)
            checked[63] = 1;
        if (checkedResTemp.isChecked65() && pageNumTemp == 0)
            checked[64] = 1;
        if (checkedResTemp.isChecked66() && pageNumTemp == 0)
            checked[65] = 1;
        if (checkedResTemp.isChecked67() && pageNumTemp == 0)
            checked[66] = 1;
        if (checkedResTemp.isChecked68() && pageNumTemp == 0)
            checked[67] = 1;
        if (checkedResTemp.isChecked69() && pageNumTemp == 0)
            checked[68] = 1;
        if (checkedResTemp.isChecked70() && pageNumTemp == 0)
            checked[69] = 1;
        if (checkedResTemp.isChecked71() && pageNumTemp == 0)
            checked[70] = 1;
        if (checkedResTemp.isChecked72() && pageNumTemp == 0)
            checked[71] = 1;
        if (checkedResTemp.isChecked73() && pageNumTemp == 0)
            checked[72] = 1;
        if (checkedResTemp.isChecked74() && pageNumTemp == 0)
            checked[73] = 1;
        if (checkedResTemp.isChecked75() && pageNumTemp == 0)
            checked[74] = 1;
        if (checkedResTemp.isChecked76() && pageNumTemp == 0)
            checked[75] = 1;
        if (checkedResTemp.isChecked77() && pageNumTemp == 0)
            checked[76] = 1;
        if (checkedResTemp.isChecked78() && pageNumTemp == 0)
            checked[77] = 1;
        if (checkedResTemp.isChecked79() && pageNumTemp == 0)
            checked[78] = 1;
        if (checkedResTemp.isChecked80() && pageNumTemp == 0)
            checked[79] = 1;
        if (checkedResTemp.isChecked81() && pageNumTemp == 0)
            checked[80] = 1;
        if (checkedResTemp.isChecked82() && pageNumTemp == 0)
            checked[81] = 1;
        if (checkedResTemp.isChecked83() && pageNumTemp == 0)
            checked[82] = 1;
        if (checkedResTemp.isChecked84() && pageNumTemp == 0)
            checked[83] = 1;
        if (checkedResTemp.isChecked85() && pageNumTemp == 0)
            checked[84] = 1;
        if (checkedResTemp.isChecked86() && pageNumTemp == 0)
            checked[85] = 1;
        if (checkedResTemp.isChecked87() && pageNumTemp == 0)
            checked[86] = 1;
        if (checkedResTemp.isChecked88() && pageNumTemp == 0)
            checked[87] = 1;
        if (checkedResTemp.isChecked89() && pageNumTemp == 0)
            checked[88] = 1;
        if (checkedResTemp.isChecked90() && pageNumTemp == 0)
            checked[89] = 1;
        if (checkedResTemp.isChecked91() && pageNumTemp == 0)
            checked[90] = 1;
        if (checkedResTemp.isChecked92() && pageNumTemp == 0)
            checked[91] = 1;
        if (checkedResTemp.isChecked93() && pageNumTemp == 0)
            checked[92] = 1;
        if (checkedResTemp.isChecked94() && pageNumTemp == 0)
            checked[93] = 1;
        if (checkedResTemp.isChecked95() && pageNumTemp == 0)
            checked[94] = 1;
        if (checkedResTemp.isChecked96() && pageNumTemp == 0)
            checked[95] = 1;
        if (checkedResTemp.isChecked97() && pageNumTemp == 0)
            checked[96] = 1;
        if (checkedResTemp.isChecked98() && pageNumTemp == 0)
            checked[97] = 1;
        if (checkedResTemp.isChecked99() && pageNumTemp == 0)
            checked[98] = 1;
        if (checkedResTemp.isChecked100() && pageNumTemp == 0)
            checked[99] = 1;

        int counter = 0;
        for (News item : NewsListTemp) {
            if (checked[counter] == 1) {
                //                チェックしたニュースを削除
                mainService.newsDelete(item.getId());
            }
            counter++;
        }

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        newsAddCheckData = null;
        checkedResTemp = null;

        return "home";
    }

    //  ニュース詳細
    @RequestMapping("/news_zoom")
    public String NewsZoom(Model model) {

        //        カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //        サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //        ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //        ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //        発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //        発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //        複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        //        ニュースの検索キーワードを更新
        if (lastSearch == 1)
            news01.setCategory(lastKeyWordStr);
        if (lastSearch == 2)
            news02.setSubCategory(lastKeyWordStr);
        if (lastSearch == 3)
            news03.setNewsTitle(lastKeyWordStr);
        if (lastSearch == 4)
            news04.setNewsSubTitle(lastKeyWordStr);
        if (lastSearch == 5)
            news05.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 6)
            news06.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 7)
            news07 = lastKeyWordMultipleStr;

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //        チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //      今のページをゼロに設定
        int pageNum = 0;
        //        各ページの件数を10に設定
        int pageSize = 10;

        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        //      チェックしてないを判断
        if (checkedResTemp == null)
            return "home";
        //      二つ以上チェックしたを判断
        if (checkedResTempCount >= 2)
            return "home";

        int counter0 = 0;
        int counter = 0;
        //      チェック結果を確認
        if (checkedResTemp.isChecked1())
            counter0 = 1;
        if (checkedResTemp.isChecked2())
            counter0 = 2;
        if (checkedResTemp.isChecked3())
            counter0 = 3;
        if (checkedResTemp.isChecked4())
            counter0 = 4;
        if (checkedResTemp.isChecked5())
            counter0 = 5;
        if (checkedResTemp.isChecked6())
            counter0 = 6;
        if (checkedResTemp.isChecked7())
            counter0 = 7;
        if (checkedResTemp.isChecked8())
            counter0 = 8;
        if (checkedResTemp.isChecked9())
            counter0 = 9;
        if (checkedResTemp.isChecked10())
            counter0 = 10;
        if (checkedResTemp.isChecked11())
            counter0 = 11;
        if (checkedResTemp.isChecked12())
            counter0 = 12;
        if (checkedResTemp.isChecked13())
            counter0 = 13;
        if (checkedResTemp.isChecked14())
            counter0 = 14;
        if (checkedResTemp.isChecked15())
            counter0 = 15;
        if (checkedResTemp.isChecked16())
            counter0 = 16;
        if (checkedResTemp.isChecked17())
            counter0 = 17;
        if (checkedResTemp.isChecked18())
            counter0 = 18;
        if (checkedResTemp.isChecked19())
            counter0 = 19;
        if (checkedResTemp.isChecked20())
            counter0 = 20;
        if (checkedResTemp.isChecked21())
            counter0 = 21;
        if (checkedResTemp.isChecked22())
            counter0 = 22;
        if (checkedResTemp.isChecked23())
            counter0 = 23;
        if (checkedResTemp.isChecked24())
            counter0 = 24;
        if (checkedResTemp.isChecked25())
            counter0 = 25;
        if (checkedResTemp.isChecked26())
            counter0 = 26;
        if (checkedResTemp.isChecked27())
            counter0 = 27;
        if (checkedResTemp.isChecked28())
            counter0 = 28;
        if (checkedResTemp.isChecked29())
            counter0 = 29;
        if (checkedResTemp.isChecked30())
            counter0 = 30;
        if (checkedResTemp.isChecked31())
            counter0 = 31;
        if (checkedResTemp.isChecked32())
            counter0 = 32;
        if (checkedResTemp.isChecked33())
            counter0 = 33;
        if (checkedResTemp.isChecked34())
            counter0 = 34;
        if (checkedResTemp.isChecked35())
            counter0 = 35;
        if (checkedResTemp.isChecked36())
            counter0 = 36;
        if (checkedResTemp.isChecked37())
            counter0 = 37;
        if (checkedResTemp.isChecked38())
            counter0 = 38;
        if (checkedResTemp.isChecked39())
            counter0 = 39;
        if (checkedResTemp.isChecked40())
            counter0 = 40;
        if (checkedResTemp.isChecked41())
            counter0 = 41;
        if (checkedResTemp.isChecked42())
            counter0 = 42;
        if (checkedResTemp.isChecked43())
            counter0 = 43;
        if (checkedResTemp.isChecked44())
            counter0 = 44;
        if (checkedResTemp.isChecked45())
            counter0 = 45;
        if (checkedResTemp.isChecked46())
            counter0 = 46;
        if (checkedResTemp.isChecked47())
            counter0 = 47;
        if (checkedResTemp.isChecked48())
            counter0 = 48;
        if (checkedResTemp.isChecked49())
            counter0 = 49;
        if (checkedResTemp.isChecked50())
            counter0 = 50;
        if (checkedResTemp.isChecked51())
            counter0 = 51;
        if (checkedResTemp.isChecked52())
            counter0 = 52;
        if (checkedResTemp.isChecked53())
            counter0 = 53;
        if (checkedResTemp.isChecked54())
            counter0 = 54;
        if (checkedResTemp.isChecked55())
            counter0 = 55;
        if (checkedResTemp.isChecked56())
            counter0 = 56;
        if (checkedResTemp.isChecked57())
            counter0 = 57;
        if (checkedResTemp.isChecked58())
            counter0 = 58;
        if (checkedResTemp.isChecked59())
            counter0 = 59;
        if (checkedResTemp.isChecked60())
            counter0 = 60;
        if (checkedResTemp.isChecked61())
            counter0 = 61;
        if (checkedResTemp.isChecked62())
            counter0 = 62;
        if (checkedResTemp.isChecked63())
            counter0 = 63;
        if (checkedResTemp.isChecked64())
            counter0 = 64;
        if (checkedResTemp.isChecked65())
            counter0 = 65;
        if (checkedResTemp.isChecked66())
            counter0 = 66;
        if (checkedResTemp.isChecked67())
            counter0 = 67;
        if (checkedResTemp.isChecked68())
            counter0 = 68;
        if (checkedResTemp.isChecked69())
            counter0 = 69;
        if (checkedResTemp.isChecked70())
            counter0 = 70;
        if (checkedResTemp.isChecked71())
            counter0 = 71;
        if (checkedResTemp.isChecked72())
            counter0 = 72;
        if (checkedResTemp.isChecked73())
            counter0 = 73;
        if (checkedResTemp.isChecked74())
            counter0 = 74;
        if (checkedResTemp.isChecked75())
            counter0 = 75;
        if (checkedResTemp.isChecked76())
            counter0 = 76;
        if (checkedResTemp.isChecked77())
            counter0 = 77;
        if (checkedResTemp.isChecked78())
            counter0 = 78;
        if (checkedResTemp.isChecked79())
            counter0 = 79;
        if (checkedResTemp.isChecked80())
            counter0 = 80;
        if (checkedResTemp.isChecked81())
            counter0 = 81;
        if (checkedResTemp.isChecked82())
            counter0 = 82;
        if (checkedResTemp.isChecked83())
            counter0 = 83;
        if (checkedResTemp.isChecked84())
            counter0 = 84;
        if (checkedResTemp.isChecked85())
            counter0 = 85;
        if (checkedResTemp.isChecked86())
            counter0 = 86;
        if (checkedResTemp.isChecked87())
            counter0 = 87;
        if (checkedResTemp.isChecked88())
            counter0 = 88;
        if (checkedResTemp.isChecked89())
            counter0 = 89;
        if (checkedResTemp.isChecked90())
            counter0 = 90;
        if (checkedResTemp.isChecked91())
            counter0 = 91;
        if (checkedResTemp.isChecked92())
            counter0 = 92;
        if (checkedResTemp.isChecked93())
            counter0 = 93;
        if (checkedResTemp.isChecked94())
            counter0 = 94;
        if (checkedResTemp.isChecked95())
            counter0 = 95;
        if (checkedResTemp.isChecked96())
            counter0 = 96;
        if (checkedResTemp.isChecked97())
            counter0 = 97;
        if (checkedResTemp.isChecked98())
            counter0 = 98;
        if (checkedResTemp.isChecked99())
            counter0 = 99;
        if (checkedResTemp.isChecked100())
            counter0 = 100;
        News news = new News();
        for (News item : NewsListTemp) {
            counter++;
            if (counter == counter0) {
                //              チェックしたニュースを獲得
                news = item;
                break;
            }
        }

        model.addAttribute("news", news);

        checkedResTemp = null;

        return "news_zoom";
    }

    //////////////////////////////////////////////////
    //  カテゴリーのホームページ
    @RequestMapping(value = "/category_home/{pageNum}")
    public String categoryHome(@PathVariable(value = "pageNum", required = false) int pageNum, Model model) {
        //      検索結果がゼロの時、pageNumがマイナスにならない為に
        if (pageNum == -1)
            pageNum = 0;
        //      チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        Page<Category> categoryPage = null;
        //      全てのカテゴリーを検索
        categoryPage = mainService.findCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        CategoryListTemp = categoryPage.getContent();

        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("categoryPageSize", categoryPage.getNumberOfElements());

        List<Category> findCategoryByAllRes = null;
        List<News> findByCategoryRes = null;
        List<News> findByCategoryAndSubCategoryRes = null;
        List<SubCategory> findSubCategoryByCategoryRes = null;

        int newsCount = 0;
        //      カテゴリーとサブカテゴリーのニュース数を更新
        findCategoryByAllRes = mainService.findCategoryByAll();
        for (Category item : findCategoryByAllRes) {
            findByCategoryRes = mainService.findByCategory(item.getCategory());
            newsCount = findByCategoryRes.size();
            item.setNewsCount(newsCount);
            mainService.categoryEditNewsCount(item);
            findSubCategoryByCategoryRes = mainService.findSubCategoryByCategory(item.getCategory());
            for (SubCategory item02 : findSubCategoryByCategoryRes) {
                findByCategoryAndSubCategoryRes = mainService.findByCategoryAndSubCategory(item02.getCategory(), item02.getSubCategory());
                newsCount = findByCategoryAndSubCategoryRes.size();
                item02.setSubCategoryNewsCount(newsCount);
                mainService.subCategoryEditNewsCount(item02);
            }
        }

        return "category_home";
    }

    //  カテゴリーのチェック結果を確認    
    @PostMapping("/category_home")
    public String CategoryHomeChecked(@ModelAttribute("checkedRes") CheckedRes checkedRes, Model model) {

        checkedResTemp = checkedRes;
        checkedResTempCount = 0;
        if (checkedRes.isChecked1())
            checkedResTempCount++;
        if (checkedRes.isChecked2())
            checkedResTempCount++;
        if (checkedRes.isChecked3())
            checkedResTempCount++;
        if (checkedRes.isChecked4())
            checkedResTempCount++;
        if (checkedRes.isChecked5())
            checkedResTempCount++;
        if (checkedRes.isChecked6())
            checkedResTempCount++;
        if (checkedRes.isChecked7())
            checkedResTempCount++;
        if (checkedRes.isChecked8())
            checkedResTempCount++;
        if (checkedRes.isChecked9())
            checkedResTempCount++;
        if (checkedRes.isChecked10())
            checkedResTempCount++;
        if (checkedRes.isChecked11())
            checkedResTempCount++;
        if (checkedRes.isChecked12())
            checkedResTempCount++;
        if (checkedRes.isChecked13())
            checkedResTempCount++;
        if (checkedRes.isChecked14())
            checkedResTempCount++;
        if (checkedRes.isChecked15())
            checkedResTempCount++;
        if (checkedRes.isChecked16())
            checkedResTempCount++;
        if (checkedRes.isChecked17())
            checkedResTempCount++;
        if (checkedRes.isChecked18())
            checkedResTempCount++;
        if (checkedRes.isChecked19())
            checkedResTempCount++;
        if (checkedRes.isChecked20())
            checkedResTempCount++;
        if (checkedRes.isChecked21())
            checkedResTempCount++;
        if (checkedRes.isChecked22())
            checkedResTempCount++;
        if (checkedRes.isChecked23())
            checkedResTempCount++;
        if (checkedRes.isChecked24())
            checkedResTempCount++;
        if (checkedRes.isChecked25())
            checkedResTempCount++;
        if (checkedRes.isChecked26())
            checkedResTempCount++;
        if (checkedRes.isChecked27())
            checkedResTempCount++;
        if (checkedRes.isChecked28())
            checkedResTempCount++;
        if (checkedRes.isChecked29())
            checkedResTempCount++;
        if (checkedRes.isChecked30())
            checkedResTempCount++;
        if (checkedRes.isChecked31())
            checkedResTempCount++;
        if (checkedRes.isChecked32())
            checkedResTempCount++;
        if (checkedRes.isChecked33())
            checkedResTempCount++;
        if (checkedRes.isChecked34())
            checkedResTempCount++;
        if (checkedRes.isChecked35())
            checkedResTempCount++;
        if (checkedRes.isChecked36())
            checkedResTempCount++;
        if (checkedRes.isChecked37())
            checkedResTempCount++;
        if (checkedRes.isChecked38())
            checkedResTempCount++;
        if (checkedRes.isChecked39())
            checkedResTempCount++;
        if (checkedRes.isChecked40())
            checkedResTempCount++;
        if (checkedRes.isChecked41())
            checkedResTempCount++;
        if (checkedRes.isChecked42())
            checkedResTempCount++;
        if (checkedRes.isChecked43())
            checkedResTempCount++;
        if (checkedRes.isChecked44())
            checkedResTempCount++;
        if (checkedRes.isChecked45())
            checkedResTempCount++;
        if (checkedRes.isChecked46())
            checkedResTempCount++;
        if (checkedRes.isChecked47())
            checkedResTempCount++;
        if (checkedRes.isChecked48())
            checkedResTempCount++;
        if (checkedRes.isChecked49())
            checkedResTempCount++;
        if (checkedRes.isChecked50())
            checkedResTempCount++;
        if (checkedRes.isChecked51())
            checkedResTempCount++;
        if (checkedRes.isChecked52())
            checkedResTempCount++;
        if (checkedRes.isChecked53())
            checkedResTempCount++;
        if (checkedRes.isChecked54())
            checkedResTempCount++;
        if (checkedRes.isChecked55())
            checkedResTempCount++;
        if (checkedRes.isChecked56())
            checkedResTempCount++;
        if (checkedRes.isChecked57())
            checkedResTempCount++;
        if (checkedRes.isChecked58())
            checkedResTempCount++;
        if (checkedRes.isChecked59())
            checkedResTempCount++;
        if (checkedRes.isChecked60())
            checkedResTempCount++;
        if (checkedRes.isChecked61())
            checkedResTempCount++;
        if (checkedRes.isChecked62())
            checkedResTempCount++;
        if (checkedRes.isChecked63())
            checkedResTempCount++;
        if (checkedRes.isChecked64())
            checkedResTempCount++;
        if (checkedRes.isChecked65())
            checkedResTempCount++;
        if (checkedRes.isChecked66())
            checkedResTempCount++;
        if (checkedRes.isChecked67())
            checkedResTempCount++;
        if (checkedRes.isChecked68())
            checkedResTempCount++;
        if (checkedRes.isChecked69())
            checkedResTempCount++;
        if (checkedRes.isChecked70())
            checkedResTempCount++;
        if (checkedRes.isChecked71())
            checkedResTempCount++;
        if (checkedRes.isChecked72())
            checkedResTempCount++;
        if (checkedRes.isChecked73())
            checkedResTempCount++;
        if (checkedRes.isChecked74())
            checkedResTempCount++;
        if (checkedRes.isChecked75())
            checkedResTempCount++;
        if (checkedRes.isChecked76())
            checkedResTempCount++;
        if (checkedRes.isChecked77())
            checkedResTempCount++;
        if (checkedRes.isChecked78())
            checkedResTempCount++;
        if (checkedRes.isChecked79())
            checkedResTempCount++;
        if (checkedRes.isChecked80())
            checkedResTempCount++;
        if (checkedRes.isChecked81())
            checkedResTempCount++;
        if (checkedRes.isChecked82())
            checkedResTempCount++;
        if (checkedRes.isChecked83())
            checkedResTempCount++;
        if (checkedRes.isChecked84())
            checkedResTempCount++;
        if (checkedRes.isChecked85())
            checkedResTempCount++;
        if (checkedRes.isChecked86())
            checkedResTempCount++;
        if (checkedRes.isChecked87())
            checkedResTempCount++;
        if (checkedRes.isChecked88())
            checkedResTempCount++;
        if (checkedRes.isChecked89())
            checkedResTempCount++;
        if (checkedRes.isChecked90())
            checkedResTempCount++;
        if (checkedRes.isChecked91())
            checkedResTempCount++;
        if (checkedRes.isChecked92())
            checkedResTempCount++;
        if (checkedRes.isChecked93())
            checkedResTempCount++;
        if (checkedRes.isChecked94())
            checkedResTempCount++;
        if (checkedRes.isChecked95())
            checkedResTempCount++;
        if (checkedRes.isChecked96())
            checkedResTempCount++;
        if (checkedRes.isChecked97())
            checkedResTempCount++;
        if (checkedRes.isChecked98())
            checkedResTempCount++;
        if (checkedRes.isChecked99())
            checkedResTempCount++;
        if (checkedRes.isChecked100())
            checkedResTempCount++;

        //      今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        //      全てのカテゴリーを検索
        Page<Category> categoryPage = null;
        categoryPage = mainService.findCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("categoryPageSize", categoryPage.getNumberOfElements());

        return "category_home";

    }

    //  カテゴリーの全ての検索結果をチェック
    @RequestMapping(value = "/category_home_all_check")
    public String categoryHomeAllCheck(Model model) {

        CheckedRes checkedRes = new CheckedRes();
        checkedRes.setChecked1(true);
        checkedRes.setChecked2(true);
        checkedRes.setChecked3(true);
        checkedRes.setChecked4(true);
        checkedRes.setChecked5(true);
        checkedRes.setChecked6(true);
        checkedRes.setChecked7(true);
        checkedRes.setChecked8(true);
        checkedRes.setChecked9(true);
        checkedRes.setChecked10(true);
        checkedRes.setChecked11(true);
        checkedRes.setChecked12(true);
        checkedRes.setChecked13(true);
        checkedRes.setChecked14(true);
        checkedRes.setChecked15(true);
        checkedRes.setChecked16(true);
        checkedRes.setChecked17(true);
        checkedRes.setChecked18(true);
        checkedRes.setChecked19(true);
        checkedRes.setChecked20(true);
        checkedRes.setChecked21(true);
        checkedRes.setChecked22(true);
        checkedRes.setChecked23(true);
        checkedRes.setChecked24(true);
        checkedRes.setChecked25(true);
        checkedRes.setChecked26(true);
        checkedRes.setChecked27(true);
        checkedRes.setChecked28(true);
        checkedRes.setChecked29(true);
        checkedRes.setChecked30(true);
        checkedRes.setChecked31(true);
        checkedRes.setChecked32(true);
        checkedRes.setChecked33(true);
        checkedRes.setChecked34(true);
        checkedRes.setChecked35(true);
        checkedRes.setChecked36(true);
        checkedRes.setChecked37(true);
        checkedRes.setChecked38(true);
        checkedRes.setChecked39(true);
        checkedRes.setChecked40(true);
        checkedRes.setChecked41(true);
        checkedRes.setChecked42(true);
        checkedRes.setChecked43(true);
        checkedRes.setChecked44(true);
        checkedRes.setChecked45(true);
        checkedRes.setChecked46(true);
        checkedRes.setChecked47(true);
        checkedRes.setChecked48(true);
        checkedRes.setChecked49(true);
        checkedRes.setChecked50(true);
        checkedRes.setChecked51(true);
        checkedRes.setChecked52(true);
        checkedRes.setChecked53(true);
        checkedRes.setChecked54(true);
        checkedRes.setChecked55(true);
        checkedRes.setChecked56(true);
        checkedRes.setChecked57(true);
        checkedRes.setChecked58(true);
        checkedRes.setChecked59(true);
        checkedRes.setChecked60(true);
        checkedRes.setChecked61(true);
        checkedRes.setChecked62(true);
        checkedRes.setChecked63(true);
        checkedRes.setChecked64(true);
        checkedRes.setChecked65(true);
        checkedRes.setChecked66(true);
        checkedRes.setChecked67(true);
        checkedRes.setChecked68(true);
        checkedRes.setChecked69(true);
        checkedRes.setChecked70(true);
        checkedRes.setChecked71(true);
        checkedRes.setChecked72(true);
        checkedRes.setChecked73(true);
        checkedRes.setChecked74(true);
        checkedRes.setChecked75(true);
        checkedRes.setChecked76(true);
        checkedRes.setChecked77(true);
        checkedRes.setChecked78(true);
        checkedRes.setChecked79(true);
        checkedRes.setChecked80(true);
        checkedRes.setChecked81(true);
        checkedRes.setChecked82(true);
        checkedRes.setChecked83(true);
        checkedRes.setChecked84(true);
        checkedRes.setChecked85(true);
        checkedRes.setChecked86(true);
        checkedRes.setChecked87(true);
        checkedRes.setChecked88(true);
        checkedRes.setChecked89(true);
        checkedRes.setChecked90(true);
        checkedRes.setChecked91(true);
        checkedRes.setChecked92(true);
        checkedRes.setChecked93(true);
        checkedRes.setChecked94(true);
        checkedRes.setChecked95(true);
        checkedRes.setChecked96(true);
        checkedRes.setChecked97(true);
        checkedRes.setChecked98(true);
        checkedRes.setChecked99(true);
        checkedRes.setChecked100(true);
        model.addAttribute("checkedRes", checkedRes);
        //      今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        //      全てのカテゴリーを検索
        Page<Category> categoryPage = null;
        categoryPage = mainService.findCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        CategoryListTemp = categoryPage.getContent();
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("categoryPageSize", categoryPage.getNumberOfElements());

        return "category_home";
    }

    //  カテゴリー追加
    @RequestMapping(value = "/category_add")
    public String categoryAdd(Model model) {

        Category categoryInput = new Category();
        model.addAttribute("categoryInput", categoryInput);
        model.addAttribute("error", "");

        return "category_add";

    }

    //  カテゴリー追加    
    @PostMapping("/category_add")
    public String CategoryAdd(@ModelAttribute("categoryInput") Category category, Model model) {
        //      カテゴリー追加
        CategoryAddResponse res = mainService.categoryAdd(category);
        //      カテゴリー追加結果を確認
        if (res.getCode() != "200") {
            model.addAttribute("error", res.getMessage());
            return "category_add";
        }

        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);
        //      今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        //      全てのカテゴリーを検索
        Page<Category> categoryPage = null;
        categoryPage = mainService.findCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("categoryPageSize", categoryPage.getNumberOfElements());

        return "category_home";
    }

    //  カテゴリー更新
    @RequestMapping(value = "/category_edit")
    public String CategoryEdit(Model model) {

        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);
        //      今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        //      全てのカテゴリーを検索
        Page<Category> categoryPage = null;
        categoryPage = mainService.findCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("categoryPageSize", categoryPage.getNumberOfElements());
        //      チェックしてないを判断
        if (checkedResTemp == null)
            return "category_home";
        //      二つ以上チェックしたを判断
        if (checkedResTempCount >= 2)
            return "category_home";
        //      チェックを結果を確認
        Category category = new Category();
        int counter0 = 0;
        int counter = 0;
        if ((checkedResTemp.isChecked1() && pageNumTemp == 0)
            || (checkedResTemp.isChecked1() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked1() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 1;
        if ((checkedResTemp.isChecked2() && pageNumTemp == 0)
            || (checkedResTemp.isChecked2() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked2() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 2;
        if ((checkedResTemp.isChecked3() && pageNumTemp == 0)
            || (checkedResTemp.isChecked3() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked3() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 3;
        if ((checkedResTemp.isChecked4() && pageNumTemp == 0)
            || (checkedResTemp.isChecked4() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked4() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 4;
        if ((checkedResTemp.isChecked5() && pageNumTemp == 0)
            || (checkedResTemp.isChecked5() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked5() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 5;
        if ((checkedResTemp.isChecked6() && pageNumTemp == 0)
            || (checkedResTemp.isChecked6() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked6() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 6;
        if ((checkedResTemp.isChecked7() && pageNumTemp == 0)
            || (checkedResTemp.isChecked7() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked7() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 7;
        if ((checkedResTemp.isChecked8() && pageNumTemp == 0)
            || (checkedResTemp.isChecked8() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked8() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 8;
        if ((checkedResTemp.isChecked9() && pageNumTemp == 0)
            || (checkedResTemp.isChecked9() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked9() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 9;
        if ((checkedResTemp.isChecked10() && pageNumTemp == 0)
            || (checkedResTemp.isChecked10() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked10() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 10;
        if ((checkedResTemp.isChecked11() && pageNumTemp == 0)
            || (checkedResTemp.isChecked11() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 11;
        if ((checkedResTemp.isChecked12() && pageNumTemp == 0)
            || (checkedResTemp.isChecked12() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 12;
        if ((checkedResTemp.isChecked13() && pageNumTemp == 0)
            || (checkedResTemp.isChecked13() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 13;
        if ((checkedResTemp.isChecked14() && pageNumTemp == 0)
            || (checkedResTemp.isChecked14() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 14;
        if ((checkedResTemp.isChecked15() && pageNumTemp == 0)
            || (checkedResTemp.isChecked15() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 15;
        if ((checkedResTemp.isChecked16() && pageNumTemp == 0)
            || (checkedResTemp.isChecked16() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 16;
        if ((checkedResTemp.isChecked17() && pageNumTemp == 0)
            || (checkedResTemp.isChecked17() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 17;
        if ((checkedResTemp.isChecked18() && pageNumTemp == 0)
            || (checkedResTemp.isChecked18() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 18;
        if ((checkedResTemp.isChecked19() && pageNumTemp == 0)
            || (checkedResTemp.isChecked19() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 19;
        if ((checkedResTemp.isChecked20() && pageNumTemp == 0)
            || (checkedResTemp.isChecked20() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 20;
        if ((checkedResTemp.isChecked21() && pageNumTemp == 0)
            || (checkedResTemp.isChecked21() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 21;
        if ((checkedResTemp.isChecked22() && pageNumTemp == 0)
            || (checkedResTemp.isChecked22() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 22;
        if ((checkedResTemp.isChecked23() && pageNumTemp == 0)
            || (checkedResTemp.isChecked23() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 23;
        if ((checkedResTemp.isChecked24() && pageNumTemp == 0)
            || (checkedResTemp.isChecked24() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 24;
        if ((checkedResTemp.isChecked25() && pageNumTemp == 0)
            || (checkedResTemp.isChecked25() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 25;
        if ((checkedResTemp.isChecked26() && pageNumTemp == 0)
            || (checkedResTemp.isChecked26() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 26;
        if ((checkedResTemp.isChecked27() && pageNumTemp == 0)
            || (checkedResTemp.isChecked27() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 27;
        if ((checkedResTemp.isChecked28() && pageNumTemp == 0)
            || (checkedResTemp.isChecked28() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 28;
        if ((checkedResTemp.isChecked29() && pageNumTemp == 0)
            || (checkedResTemp.isChecked29() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 29;
        if ((checkedResTemp.isChecked30() && pageNumTemp == 0)
            || (checkedResTemp.isChecked30() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 30;
        if ((checkedResTemp.isChecked31() && pageNumTemp == 0)
            || (checkedResTemp.isChecked31() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 31;
        if ((checkedResTemp.isChecked32() && pageNumTemp == 0)
            || (checkedResTemp.isChecked32() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 32;
        if ((checkedResTemp.isChecked33() && pageNumTemp == 0)
            || (checkedResTemp.isChecked33() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 33;
        if ((checkedResTemp.isChecked34() && pageNumTemp == 0)
            || (checkedResTemp.isChecked34() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 34;
        if ((checkedResTemp.isChecked35() && pageNumTemp == 0)
            || (checkedResTemp.isChecked35() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 35;
        if ((checkedResTemp.isChecked36() && pageNumTemp == 0)
            || (checkedResTemp.isChecked36() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 36;
        if ((checkedResTemp.isChecked37() && pageNumTemp == 0)
            || (checkedResTemp.isChecked37() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 37;
        if ((checkedResTemp.isChecked38() && pageNumTemp == 0)
            || (checkedResTemp.isChecked38() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 38;
        if ((checkedResTemp.isChecked39() && pageNumTemp == 0)
            || (checkedResTemp.isChecked39() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 39;
        if ((checkedResTemp.isChecked40() && pageNumTemp == 0)
            || (checkedResTemp.isChecked40() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 40;
        if ((checkedResTemp.isChecked41() && pageNumTemp == 0)
            || (checkedResTemp.isChecked41() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 41;
        if ((checkedResTemp.isChecked42() && pageNumTemp == 0)
            || (checkedResTemp.isChecked42() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 42;
        if ((checkedResTemp.isChecked43() && pageNumTemp == 0)
            || (checkedResTemp.isChecked43() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 43;
        if ((checkedResTemp.isChecked44() && pageNumTemp == 0)
            || (checkedResTemp.isChecked44() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 44;
        if ((checkedResTemp.isChecked45() && pageNumTemp == 0)
            || (checkedResTemp.isChecked45() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 45;
        if ((checkedResTemp.isChecked46() && pageNumTemp == 0)
            || (checkedResTemp.isChecked46() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 46;
        if ((checkedResTemp.isChecked47() && pageNumTemp == 0)
            || (checkedResTemp.isChecked47() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 47;
        if ((checkedResTemp.isChecked48() && pageNumTemp == 0)
            || (checkedResTemp.isChecked48() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 48;
        if ((checkedResTemp.isChecked49() && pageNumTemp == 0)
            || (checkedResTemp.isChecked49() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 49;
        if ((checkedResTemp.isChecked50() && pageNumTemp == 0)
            || (checkedResTemp.isChecked50() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 50;
        if (checkedResTemp.isChecked51() && pageNumTemp == 0)
            counter0 = 51;
        if (checkedResTemp.isChecked52() && pageNumTemp == 0)
            counter0 = 52;
        if (checkedResTemp.isChecked53() && pageNumTemp == 0)
            counter0 = 53;
        if (checkedResTemp.isChecked54() && pageNumTemp == 0)
            counter0 = 54;
        if (checkedResTemp.isChecked55() && pageNumTemp == 0)
            counter0 = 55;
        if (checkedResTemp.isChecked56() && pageNumTemp == 0)
            counter0 = 56;
        if (checkedResTemp.isChecked57() && pageNumTemp == 0)
            counter0 = 57;
        if (checkedResTemp.isChecked58() && pageNumTemp == 0)
            counter0 = 58;
        if (checkedResTemp.isChecked59() && pageNumTemp == 0)
            counter0 = 59;
        if (checkedResTemp.isChecked60() && pageNumTemp == 0)
            counter0 = 60;
        if (checkedResTemp.isChecked61() && pageNumTemp == 0)
            counter0 = 61;
        if (checkedResTemp.isChecked62() && pageNumTemp == 0)
            counter0 = 62;
        if (checkedResTemp.isChecked63() && pageNumTemp == 0)
            counter0 = 63;
        if (checkedResTemp.isChecked64() && pageNumTemp == 0)
            counter0 = 64;
        if (checkedResTemp.isChecked65() && pageNumTemp == 0)
            counter0 = 65;
        if (checkedResTemp.isChecked66() && pageNumTemp == 0)
            counter0 = 66;
        if (checkedResTemp.isChecked67() && pageNumTemp == 0)
            counter0 = 67;
        if (checkedResTemp.isChecked68() && pageNumTemp == 0)
            counter0 = 68;
        if (checkedResTemp.isChecked69() && pageNumTemp == 0)
            counter0 = 69;
        if (checkedResTemp.isChecked70() && pageNumTemp == 0)
            counter0 = 70;
        if (checkedResTemp.isChecked71() && pageNumTemp == 0)
            counter0 = 71;
        if (checkedResTemp.isChecked72() && pageNumTemp == 0)
            counter0 = 72;
        if (checkedResTemp.isChecked73() && pageNumTemp == 0)
            counter0 = 73;
        if (checkedResTemp.isChecked74() && pageNumTemp == 0)
            counter0 = 74;
        if (checkedResTemp.isChecked75() && pageNumTemp == 0)
            counter0 = 75;
        if (checkedResTemp.isChecked76() && pageNumTemp == 0)
            counter0 = 76;
        if (checkedResTemp.isChecked77() && pageNumTemp == 0)
            counter0 = 77;
        if (checkedResTemp.isChecked78() && pageNumTemp == 0)
            counter0 = 78;
        if (checkedResTemp.isChecked79() && pageNumTemp == 0)
            counter0 = 79;
        if (checkedResTemp.isChecked80() && pageNumTemp == 0)
            counter0 = 80;
        if (checkedResTemp.isChecked81() && pageNumTemp == 0)
            counter0 = 81;
        if (checkedResTemp.isChecked82() && pageNumTemp == 0)
            counter0 = 82;
        if (checkedResTemp.isChecked83() && pageNumTemp == 0)
            counter0 = 83;
        if (checkedResTemp.isChecked84() && pageNumTemp == 0)
            counter0 = 84;
        if (checkedResTemp.isChecked85() && pageNumTemp == 0)
            counter0 = 85;
        if (checkedResTemp.isChecked86() && pageNumTemp == 0)
            counter0 = 86;
        if (checkedResTemp.isChecked87() && pageNumTemp == 0)
            counter0 = 87;
        if (checkedResTemp.isChecked88() && pageNumTemp == 0)
            counter0 = 88;
        if (checkedResTemp.isChecked89() && pageNumTemp == 0)
            counter0 = 89;
        if (checkedResTemp.isChecked90() && pageNumTemp == 0)
            counter0 = 90;
        if (checkedResTemp.isChecked91() && pageNumTemp == 0)
            counter0 = 91;
        if (checkedResTemp.isChecked92() && pageNumTemp == 0)
            counter0 = 92;
        if (checkedResTemp.isChecked93() && pageNumTemp == 0)
            counter0 = 93;
        if (checkedResTemp.isChecked94() && pageNumTemp == 0)
            counter0 = 94;
        if (checkedResTemp.isChecked95() && pageNumTemp == 0)
            counter0 = 95;
        if (checkedResTemp.isChecked96() && pageNumTemp == 0)
            counter0 = 96;
        if (checkedResTemp.isChecked97() && pageNumTemp == 0)
            counter0 = 97;
        if (checkedResTemp.isChecked98() && pageNumTemp == 0)
            counter0 = 98;
        if (checkedResTemp.isChecked99() && pageNumTemp == 0)
            counter0 = 99;
        if (checkedResTemp.isChecked100() && pageNumTemp == 0)
            counter0 = 100;
        for (Category item : CategoryListTemp) {
            counter++;
            if (counter == counter0) {
                category = item;
                categoryIdTemp = item.getId();
                oldCategoryTemp = item.getCategory();
                break;
            }
        }

        model.addAttribute("category", category);

        model.addAttribute("error", "");

        checkedResTemp = null;

        return "category_edit";
    }

    //  カテゴリー更新
    @PostMapping("/category_edit")
    public String CategoryEditPost(@ModelAttribute("category") String category01, Model model) {

        Category category = new Category();
        category.setId(categoryIdTemp);
        category.setCategory(category01);

        categoryIdTemp = 0;
        //      カテゴリー更新
        CategoryAddResponse res = mainService.categoryEdit(category);
        if (res.getCode() != "200") {
            model.addAttribute("category", category);
            model.addAttribute("error", res.getMessage());
            return "category_edit";
        }
        //      カテゴリーとサブカテゴリーのニュース数を更新
        newsDao.updateNewsCategoryByOldCategory(category01, oldCategoryTemp);
        subCateogryDao.updateSubCategoryCategoryByOldCategory(category01, oldCategoryTemp);

        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);
        //      今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        //      全てのカテゴリーを検索
        Page<Category> categoryPage = null;
        categoryPage = mainService.findCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("categoryPageSize", categoryPage.getNumberOfElements());

        return "category_home";
    }

    //  カテゴリー削除
    @RequestMapping(value = "/category_delete")
    public String CategoryDelete(Model model) {

        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);
        //        今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        //        全てのカテゴリーを検索
        Page<Category> categoryPage = null;
        categoryPage = mainService.findCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("categoryPageSize", categoryPage.getNumberOfElements());
        //        チェックしてないを判断
        if (checkedResTemp == null)
            return "category_home";
        //        チェック結果を確認
        int[] checked = new int[100];
        for (int i = 0; i < 100; i++) {
            checked[i] = 0;
        }
        if ((checkedResTemp.isChecked1() && pageNumTemp == 0)
            || (checkedResTemp.isChecked1() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked1() && pageSize == 50 && pageNumTemp != 0))
            checked[0] = 1;
        if ((checkedResTemp.isChecked2() && pageNumTemp == 0)
            || (checkedResTemp.isChecked2() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked2() && pageSize == 50 && pageNumTemp != 0))
            checked[1] = 1;
        if ((checkedResTemp.isChecked3() && pageNumTemp == 0)
            || (checkedResTemp.isChecked3() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked3() && pageSize == 50 && pageNumTemp != 0))
            checked[2] = 1;
        if ((checkedResTemp.isChecked4() && pageNumTemp == 0)
            || (checkedResTemp.isChecked4() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked4() && pageSize == 50 && pageNumTemp != 0))
            checked[3] = 1;
        if ((checkedResTemp.isChecked5() && pageNumTemp == 0)
            || (checkedResTemp.isChecked5() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked5() && pageSize == 50 && pageNumTemp != 0))
            checked[4] = 1;
        if ((checkedResTemp.isChecked6() && pageNumTemp == 0)
            || (checkedResTemp.isChecked6() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked6() && pageSize == 50 && pageNumTemp != 0))
            checked[5] = 1;
        if ((checkedResTemp.isChecked7() && pageNumTemp == 0)
            || (checkedResTemp.isChecked7() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked7() && pageSize == 50 && pageNumTemp != 0))
            checked[6] = 1;
        if ((checkedResTemp.isChecked8() && pageNumTemp == 0)
            || (checkedResTemp.isChecked8() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked8() && pageSize == 50 && pageNumTemp != 0))
            checked[7] = 1;
        if ((checkedResTemp.isChecked9() && pageNumTemp == 0)
            || (checkedResTemp.isChecked9() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked9() && pageSize == 50 && pageNumTemp != 0))
            checked[8] = 1;
        if ((checkedResTemp.isChecked10() && pageNumTemp == 0)
            || (checkedResTemp.isChecked10() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked10() && pageSize == 50 && pageNumTemp != 0))
            checked[9] = 1;
        if ((checkedResTemp.isChecked11() && pageNumTemp == 0)
            || (checkedResTemp.isChecked11() && pageSize == 50 && pageNumTemp != 0))
            checked[10] = 1;
        if ((checkedResTemp.isChecked12() && pageNumTemp == 0)
            || (checkedResTemp.isChecked12() && pageSize == 50 && pageNumTemp != 0))
            checked[11] = 1;
        if ((checkedResTemp.isChecked13() && pageNumTemp == 0)
            || (checkedResTemp.isChecked13() && pageSize == 50 && pageNumTemp != 0))
            checked[12] = 1;
        if ((checkedResTemp.isChecked14() && pageNumTemp == 0)
            || (checkedResTemp.isChecked14() && pageSize == 50 && pageNumTemp != 0))
            checked[13] = 1;
        if ((checkedResTemp.isChecked15() && pageNumTemp == 0)
            || (checkedResTemp.isChecked15() && pageSize == 50 && pageNumTemp != 0))
            checked[14] = 1;
        if ((checkedResTemp.isChecked16() && pageNumTemp == 0)
            || (checkedResTemp.isChecked16() && pageSize == 50 && pageNumTemp != 0))
            checked[15] = 1;
        if ((checkedResTemp.isChecked17() && pageNumTemp == 0)
            || (checkedResTemp.isChecked17() && pageSize == 50 && pageNumTemp != 0))
            checked[16] = 1;
        if ((checkedResTemp.isChecked18() && pageNumTemp == 0)
            || (checkedResTemp.isChecked18() && pageSize == 50 && pageNumTemp != 0))
            checked[17] = 1;
        if ((checkedResTemp.isChecked19() && pageNumTemp == 0)
            || (checkedResTemp.isChecked19() && pageSize == 50 && pageNumTemp != 0))
            checked[18] = 1;
        if ((checkedResTemp.isChecked20() && pageNumTemp == 0)
            || (checkedResTemp.isChecked20() && pageSize == 50 && pageNumTemp != 0))
            checked[19] = 1;
        if ((checkedResTemp.isChecked21() && pageNumTemp == 0)
            || (checkedResTemp.isChecked21() && pageSize == 50 && pageNumTemp != 0))
            checked[20] = 1;
        if ((checkedResTemp.isChecked22() && pageNumTemp == 0)
            || (checkedResTemp.isChecked22() && pageSize == 50 && pageNumTemp != 0))
            checked[21] = 1;
        if ((checkedResTemp.isChecked23() && pageNumTemp == 0)
            || (checkedResTemp.isChecked23() && pageSize == 50 && pageNumTemp != 0))
            checked[22] = 1;
        if ((checkedResTemp.isChecked24() && pageNumTemp == 0)
            || (checkedResTemp.isChecked24() && pageSize == 50 && pageNumTemp != 0))
            checked[23] = 1;
        if ((checkedResTemp.isChecked25() && pageNumTemp == 0)
            || (checkedResTemp.isChecked25() && pageSize == 50 && pageNumTemp != 0))
            checked[24] = 1;
        if ((checkedResTemp.isChecked26() && pageNumTemp == 0)
            || (checkedResTemp.isChecked26() && pageSize == 50 && pageNumTemp != 0))
            checked[25] = 1;
        if ((checkedResTemp.isChecked27() && pageNumTemp == 0)
            || (checkedResTemp.isChecked27() && pageSize == 50 && pageNumTemp != 0))
            checked[26] = 1;
        if ((checkedResTemp.isChecked28() && pageNumTemp == 0)
            || (checkedResTemp.isChecked28() && pageSize == 50 && pageNumTemp != 0))
            checked[27] = 1;
        if ((checkedResTemp.isChecked29() && pageNumTemp == 0)
            || (checkedResTemp.isChecked29() && pageSize == 50 && pageNumTemp != 0))
            checked[28] = 1;
        if ((checkedResTemp.isChecked30() && pageNumTemp == 0)
            || (checkedResTemp.isChecked30() && pageSize == 50 && pageNumTemp != 0))
            checked[29] = 1;
        if ((checkedResTemp.isChecked31() && pageNumTemp == 0)
            || (checkedResTemp.isChecked31() && pageSize == 50 && pageNumTemp != 0))
            checked[30] = 1;
        if ((checkedResTemp.isChecked32() && pageNumTemp == 0)
            || (checkedResTemp.isChecked32() && pageSize == 50 && pageNumTemp != 0))
            checked[31] = 1;
        if ((checkedResTemp.isChecked33() && pageNumTemp == 0)
            || (checkedResTemp.isChecked33() && pageSize == 50 && pageNumTemp != 0))
            checked[32] = 1;
        if ((checkedResTemp.isChecked34() && pageNumTemp == 0)
            || (checkedResTemp.isChecked34() && pageSize == 50 && pageNumTemp != 0))
            checked[33] = 1;
        if ((checkedResTemp.isChecked35() && pageNumTemp == 0)
            || (checkedResTemp.isChecked35() && pageSize == 50 && pageNumTemp != 0))
            checked[34] = 1;
        if ((checkedResTemp.isChecked36() && pageNumTemp == 0)
            || (checkedResTemp.isChecked36() && pageSize == 50 && pageNumTemp != 0))
            checked[35] = 1;
        if ((checkedResTemp.isChecked37() && pageNumTemp == 0)
            || (checkedResTemp.isChecked37() && pageSize == 50 && pageNumTemp != 0))
            checked[36] = 1;
        if ((checkedResTemp.isChecked38() && pageNumTemp == 0)
            || (checkedResTemp.isChecked38() && pageSize == 50 && pageNumTemp != 0))
            checked[37] = 1;
        if ((checkedResTemp.isChecked39() && pageNumTemp == 0)
            || (checkedResTemp.isChecked39() && pageSize == 50 && pageNumTemp != 0))
            checked[38] = 1;
        if ((checkedResTemp.isChecked40() && pageNumTemp == 0)
            || (checkedResTemp.isChecked40() && pageSize == 50 && pageNumTemp != 0))
            checked[39] = 1;
        if ((checkedResTemp.isChecked41() && pageNumTemp == 0)
            || (checkedResTemp.isChecked41() && pageSize == 50 && pageNumTemp != 0))
            checked[40] = 1;
        if ((checkedResTemp.isChecked42() && pageNumTemp == 0)
            || (checkedResTemp.isChecked42() && pageSize == 50 && pageNumTemp != 0))
            checked[41] = 1;
        if ((checkedResTemp.isChecked43() && pageNumTemp == 0)
            || (checkedResTemp.isChecked43() && pageSize == 50 && pageNumTemp != 0))
            checked[42] = 1;
        if ((checkedResTemp.isChecked44() && pageNumTemp == 0)
            || (checkedResTemp.isChecked44() && pageSize == 50 && pageNumTemp != 0))
            checked[43] = 1;
        if ((checkedResTemp.isChecked45() && pageNumTemp == 0)
            || (checkedResTemp.isChecked45() && pageSize == 50 && pageNumTemp != 0))
            checked[44] = 1;
        if ((checkedResTemp.isChecked46() && pageNumTemp == 0)
            || (checkedResTemp.isChecked46() && pageSize == 50 && pageNumTemp != 0))
            checked[45] = 1;
        if ((checkedResTemp.isChecked47() && pageNumTemp == 0)
            || (checkedResTemp.isChecked47() && pageSize == 50 && pageNumTemp != 0))
            checked[46] = 1;
        if ((checkedResTemp.isChecked48() && pageNumTemp == 0)
            || (checkedResTemp.isChecked48() && pageSize == 50 && pageNumTemp != 0))
            checked[47] = 1;
        if ((checkedResTemp.isChecked49() && pageNumTemp == 0)
            || (checkedResTemp.isChecked49() && pageSize == 50 && pageNumTemp != 0))
            checked[48] = 1;
        if ((checkedResTemp.isChecked50() && pageNumTemp == 0)
            || (checkedResTemp.isChecked50() && pageSize == 50 && pageNumTemp != 0))
            checked[49] = 1;
        if (checkedResTemp.isChecked51() && pageNumTemp == 0)
            checked[50] = 1;
        if (checkedResTemp.isChecked52() && pageNumTemp == 0)
            checked[51] = 1;
        if (checkedResTemp.isChecked53() && pageNumTemp == 0)
            checked[52] = 1;
        if (checkedResTemp.isChecked54() && pageNumTemp == 0)
            checked[53] = 1;
        if (checkedResTemp.isChecked55() && pageNumTemp == 0)
            checked[54] = 1;
        if (checkedResTemp.isChecked56() && pageNumTemp == 0)
            checked[55] = 1;
        if (checkedResTemp.isChecked57() && pageNumTemp == 0)
            checked[56] = 1;
        if (checkedResTemp.isChecked58() && pageNumTemp == 0)
            checked[57] = 1;
        if (checkedResTemp.isChecked59() && pageNumTemp == 0)
            checked[58] = 1;
        if (checkedResTemp.isChecked60() && pageNumTemp == 0)
            checked[59] = 1;
        if (checkedResTemp.isChecked61() && pageNumTemp == 0)
            checked[60] = 1;
        if (checkedResTemp.isChecked62() && pageNumTemp == 0)
            checked[61] = 1;
        if (checkedResTemp.isChecked63() && pageNumTemp == 0)
            checked[62] = 1;
        if (checkedResTemp.isChecked64() && pageNumTemp == 0)
            checked[63] = 1;
        if (checkedResTemp.isChecked65() && pageNumTemp == 0)
            checked[64] = 1;
        if (checkedResTemp.isChecked66() && pageNumTemp == 0)
            checked[65] = 1;
        if (checkedResTemp.isChecked67() && pageNumTemp == 0)
            checked[66] = 1;
        if (checkedResTemp.isChecked68() && pageNumTemp == 0)
            checked[67] = 1;
        if (checkedResTemp.isChecked69() && pageNumTemp == 0)
            checked[68] = 1;
        if (checkedResTemp.isChecked70() && pageNumTemp == 0)
            checked[69] = 1;
        if (checkedResTemp.isChecked71() && pageNumTemp == 0)
            checked[70] = 1;
        if (checkedResTemp.isChecked72() && pageNumTemp == 0)
            checked[71] = 1;
        if (checkedResTemp.isChecked73() && pageNumTemp == 0)
            checked[72] = 1;
        if (checkedResTemp.isChecked74() && pageNumTemp == 0)
            checked[73] = 1;
        if (checkedResTemp.isChecked75() && pageNumTemp == 0)
            checked[74] = 1;
        if (checkedResTemp.isChecked76() && pageNumTemp == 0)
            checked[75] = 1;
        if (checkedResTemp.isChecked77() && pageNumTemp == 0)
            checked[76] = 1;
        if (checkedResTemp.isChecked78() && pageNumTemp == 0)
            checked[77] = 1;
        if (checkedResTemp.isChecked79() && pageNumTemp == 0)
            checked[78] = 1;
        if (checkedResTemp.isChecked80() && pageNumTemp == 0)
            checked[79] = 1;
        if (checkedResTemp.isChecked81() && pageNumTemp == 0)
            checked[80] = 1;
        if (checkedResTemp.isChecked82() && pageNumTemp == 0)
            checked[81] = 1;
        if (checkedResTemp.isChecked83() && pageNumTemp == 0)
            checked[82] = 1;
        if (checkedResTemp.isChecked84() && pageNumTemp == 0)
            checked[83] = 1;
        if (checkedResTemp.isChecked85() && pageNumTemp == 0)
            checked[84] = 1;
        if (checkedResTemp.isChecked86() && pageNumTemp == 0)
            checked[85] = 1;
        if (checkedResTemp.isChecked87() && pageNumTemp == 0)
            checked[86] = 1;
        if (checkedResTemp.isChecked88() && pageNumTemp == 0)
            checked[87] = 1;
        if (checkedResTemp.isChecked89() && pageNumTemp == 0)
            checked[88] = 1;
        if (checkedResTemp.isChecked90() && pageNumTemp == 0)
            checked[89] = 1;
        if (checkedResTemp.isChecked91() && pageNumTemp == 0)
            checked[90] = 1;
        if (checkedResTemp.isChecked92() && pageNumTemp == 0)
            checked[91] = 1;
        if (checkedResTemp.isChecked93() && pageNumTemp == 0)
            checked[92] = 1;
        if (checkedResTemp.isChecked94() && pageNumTemp == 0)
            checked[93] = 1;
        if (checkedResTemp.isChecked95() && pageNumTemp == 0)
            checked[94] = 1;
        if (checkedResTemp.isChecked96() && pageNumTemp == 0)
            checked[95] = 1;
        if (checkedResTemp.isChecked97() && pageNumTemp == 0)
            checked[96] = 1;
        if (checkedResTemp.isChecked98() && pageNumTemp == 0)
            checked[97] = 1;
        if (checkedResTemp.isChecked99() && pageNumTemp == 0)
            checked[98] = 1;
        if (checkedResTemp.isChecked100() && pageNumTemp == 0)
            checked[99] = 1;
        //        チェックした検索結果を削除
        int counter = 0;
        for (Category item : CategoryListTemp) {
            if (checked[counter] == 1 && item.getNewsCount() == 0) {

                mainService.categoryDelete(item.getId());

            }
            counter++;
        }

        categoryPage = null;
        //        全てのカテゴリーを検索
        categoryPage = mainService.findCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("categoryPageSize", categoryPage.getNumberOfElements());

        checkedResTemp = null;

        return "category_home";
    }

    //////////////////////////////////////////////////
    //  サブカテゴリーのホームページ
    @RequestMapping(value = "/sub_category_home/{pageNum}")
    public String subCategoryHome(@PathVariable(value = "pageNum", required = false) int pageNum, Model model) {
        //      pageNumがマイナスにならないように
        if (pageNum == -1)
            pageNum = 0;

        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        //      全てのカテゴリーを検索
        List<Category> res02 = categoryDao.findAll();
        List<String> categoryList = new ArrayList<>();
        for (Category item : res02) {
            categoryList.add(item.getCategory());
        }
        model.addAttribute("categoryList", categoryList);
        SubCategory subCategory = new SubCategory();
        model.addAttribute("subCategory", subCategory);

        Page<SubCategory> subCategoryPage = null;
        //      サブカテゴリーを検索
        if (subCategoryLastSearch == 0) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        if (subCategoryLastSearch == 1) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        SubCategoryListTemp = subCategoryPage.getContent();
        model.addAttribute("subCategoryPage", subCategoryPage);
        model.addAttribute("subCategoryPageSize", subCategoryPage.getNumberOfElements());
        model.addAttribute("subCategoryPage", subCategoryPage);

        List<Category> findCategoryByAllRes = null;
        List<News> findByCategoryRes = null;
        List<News> findByCategoryAndSubCategoryRes = null;
        List<SubCategory> findSubCategoryByCategoryRes = null;
        int newsCount = 0;

        //      カテゴリーとサブカテゴリーのニュース数を更新
        findCategoryByAllRes = mainService.findCategoryByAll();
        for (Category item : findCategoryByAllRes) {
            findByCategoryRes = mainService.findByCategory(item.getCategory());
            newsCount = findByCategoryRes.size();
            item.setNewsCount(newsCount);
            mainService.categoryEditNewsCount(item);
            findSubCategoryByCategoryRes = mainService.findSubCategoryByCategory(item.getCategory());
            for (SubCategory item02 : findSubCategoryByCategoryRes) {
                findByCategoryAndSubCategoryRes = mainService.findByCategoryAndSubCategory(item02.getCategory(), item02.getSubCategory());
                newsCount = findByCategoryAndSubCategoryRes.size();
                item02.setSubCategoryNewsCount(newsCount);
                mainService.subCategoryEditNewsCount(item02);
            }
        }

        return "sub_category_home";
    }

    //  サブカテゴリーのチェック結果を確認 
    @PostMapping("/sub_category_home")
    public String SubCategoryHomeChecked(@ModelAttribute("checkedRes") CheckedRes checkedRes, Model model) {

        checkedResTemp = checkedRes;
        checkedResTempCount = 0;
        if (checkedRes.isChecked1())
            checkedResTempCount++;
        if (checkedRes.isChecked2())
            checkedResTempCount++;
        if (checkedRes.isChecked3())
            checkedResTempCount++;
        if (checkedRes.isChecked4())
            checkedResTempCount++;
        if (checkedRes.isChecked5())
            checkedResTempCount++;
        if (checkedRes.isChecked6())
            checkedResTempCount++;
        if (checkedRes.isChecked7())
            checkedResTempCount++;
        if (checkedRes.isChecked8())
            checkedResTempCount++;
        if (checkedRes.isChecked9())
            checkedResTempCount++;
        if (checkedRes.isChecked10())
            checkedResTempCount++;
        if (checkedRes.isChecked11())
            checkedResTempCount++;
        if (checkedRes.isChecked12())
            checkedResTempCount++;
        if (checkedRes.isChecked13())
            checkedResTempCount++;
        if (checkedRes.isChecked14())
            checkedResTempCount++;
        if (checkedRes.isChecked15())
            checkedResTempCount++;
        if (checkedRes.isChecked16())
            checkedResTempCount++;
        if (checkedRes.isChecked17())
            checkedResTempCount++;
        if (checkedRes.isChecked18())
            checkedResTempCount++;
        if (checkedRes.isChecked19())
            checkedResTempCount++;
        if (checkedRes.isChecked20())
            checkedResTempCount++;
        if (checkedRes.isChecked21())
            checkedResTempCount++;
        if (checkedRes.isChecked22())
            checkedResTempCount++;
        if (checkedRes.isChecked23())
            checkedResTempCount++;
        if (checkedRes.isChecked24())
            checkedResTempCount++;
        if (checkedRes.isChecked25())
            checkedResTempCount++;
        if (checkedRes.isChecked26())
            checkedResTempCount++;
        if (checkedRes.isChecked27())
            checkedResTempCount++;
        if (checkedRes.isChecked28())
            checkedResTempCount++;
        if (checkedRes.isChecked29())
            checkedResTempCount++;
        if (checkedRes.isChecked30())
            checkedResTempCount++;
        if (checkedRes.isChecked31())
            checkedResTempCount++;
        if (checkedRes.isChecked32())
            checkedResTempCount++;
        if (checkedRes.isChecked33())
            checkedResTempCount++;
        if (checkedRes.isChecked34())
            checkedResTempCount++;
        if (checkedRes.isChecked35())
            checkedResTempCount++;
        if (checkedRes.isChecked36())
            checkedResTempCount++;
        if (checkedRes.isChecked37())
            checkedResTempCount++;
        if (checkedRes.isChecked38())
            checkedResTempCount++;
        if (checkedRes.isChecked39())
            checkedResTempCount++;
        if (checkedRes.isChecked40())
            checkedResTempCount++;
        if (checkedRes.isChecked41())
            checkedResTempCount++;
        if (checkedRes.isChecked42())
            checkedResTempCount++;
        if (checkedRes.isChecked43())
            checkedResTempCount++;
        if (checkedRes.isChecked44())
            checkedResTempCount++;
        if (checkedRes.isChecked45())
            checkedResTempCount++;
        if (checkedRes.isChecked46())
            checkedResTempCount++;
        if (checkedRes.isChecked47())
            checkedResTempCount++;
        if (checkedRes.isChecked48())
            checkedResTempCount++;
        if (checkedRes.isChecked49())
            checkedResTempCount++;
        if (checkedRes.isChecked50())
            checkedResTempCount++;
        if (checkedRes.isChecked51())
            checkedResTempCount++;
        if (checkedRes.isChecked52())
            checkedResTempCount++;
        if (checkedRes.isChecked53())
            checkedResTempCount++;
        if (checkedRes.isChecked54())
            checkedResTempCount++;
        if (checkedRes.isChecked55())
            checkedResTempCount++;
        if (checkedRes.isChecked56())
            checkedResTempCount++;
        if (checkedRes.isChecked57())
            checkedResTempCount++;
        if (checkedRes.isChecked58())
            checkedResTempCount++;
        if (checkedRes.isChecked59())
            checkedResTempCount++;
        if (checkedRes.isChecked60())
            checkedResTempCount++;
        if (checkedRes.isChecked61())
            checkedResTempCount++;
        if (checkedRes.isChecked62())
            checkedResTempCount++;
        if (checkedRes.isChecked63())
            checkedResTempCount++;
        if (checkedRes.isChecked64())
            checkedResTempCount++;
        if (checkedRes.isChecked65())
            checkedResTempCount++;
        if (checkedRes.isChecked66())
            checkedResTempCount++;
        if (checkedRes.isChecked67())
            checkedResTempCount++;
        if (checkedRes.isChecked68())
            checkedResTempCount++;
        if (checkedRes.isChecked69())
            checkedResTempCount++;
        if (checkedRes.isChecked70())
            checkedResTempCount++;
        if (checkedRes.isChecked71())
            checkedResTempCount++;
        if (checkedRes.isChecked72())
            checkedResTempCount++;
        if (checkedRes.isChecked73())
            checkedResTempCount++;
        if (checkedRes.isChecked74())
            checkedResTempCount++;
        if (checkedRes.isChecked75())
            checkedResTempCount++;
        if (checkedRes.isChecked76())
            checkedResTempCount++;
        if (checkedRes.isChecked77())
            checkedResTempCount++;
        if (checkedRes.isChecked78())
            checkedResTempCount++;
        if (checkedRes.isChecked79())
            checkedResTempCount++;
        if (checkedRes.isChecked80())
            checkedResTempCount++;
        if (checkedRes.isChecked81())
            checkedResTempCount++;
        if (checkedRes.isChecked82())
            checkedResTempCount++;
        if (checkedRes.isChecked83())
            checkedResTempCount++;
        if (checkedRes.isChecked84())
            checkedResTempCount++;
        if (checkedRes.isChecked85())
            checkedResTempCount++;
        if (checkedRes.isChecked86())
            checkedResTempCount++;
        if (checkedRes.isChecked87())
            checkedResTempCount++;
        if (checkedRes.isChecked88())
            checkedResTempCount++;
        if (checkedRes.isChecked89())
            checkedResTempCount++;
        if (checkedRes.isChecked90())
            checkedResTempCount++;
        if (checkedRes.isChecked91())
            checkedResTempCount++;
        if (checkedRes.isChecked92())
            checkedResTempCount++;
        if (checkedRes.isChecked93())
            checkedResTempCount++;
        if (checkedRes.isChecked94())
            checkedResTempCount++;
        if (checkedRes.isChecked95())
            checkedResTempCount++;
        if (checkedRes.isChecked96())
            checkedResTempCount++;
        if (checkedRes.isChecked97())
            checkedResTempCount++;
        if (checkedRes.isChecked98())
            checkedResTempCount++;
        if (checkedRes.isChecked99())
            checkedResTempCount++;
        if (checkedRes.isChecked100())
            checkedResTempCount++;
        //      今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        //      全てのカテゴリーを検索
        List<Category> res02 = categoryDao.findAll();
        List<String> categoryList = new ArrayList<>();
        for (Category item : res02) {
            categoryList.add(item.getCategory());
        }
        model.addAttribute("categoryList", categoryList);
        SubCategory subCategory = new SubCategory();
        model.addAttribute("subCategory", subCategory);

        Page<SubCategory> subCategoryPage = null;
        //      サブカテゴリーを検索
        if (subCategoryLastSearch == 0) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        if (subCategoryLastSearch == 1) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        SubCategoryListTemp = subCategoryPage.getContent();
        model.addAttribute("subCategoryPage", subCategoryPage);
        model.addAttribute("subCategoryPageSize", subCategoryPage.getNumberOfElements());

        return "sub_category_home";
    }

    //  サブカテゴリーの全ての検索結果をチェック
    @RequestMapping(value = "/sub_category_home_all_check")
    public String subCategoryHomeAllCheck(Model model) {

        CheckedRes checkedRes = new CheckedRes();
        checkedRes.setChecked1(true);
        checkedRes.setChecked2(true);
        checkedRes.setChecked3(true);
        checkedRes.setChecked4(true);
        checkedRes.setChecked5(true);
        checkedRes.setChecked6(true);
        checkedRes.setChecked7(true);
        checkedRes.setChecked8(true);
        checkedRes.setChecked9(true);
        checkedRes.setChecked10(true);
        checkedRes.setChecked11(true);
        checkedRes.setChecked12(true);
        checkedRes.setChecked13(true);
        checkedRes.setChecked14(true);
        checkedRes.setChecked15(true);
        checkedRes.setChecked16(true);
        checkedRes.setChecked17(true);
        checkedRes.setChecked18(true);
        checkedRes.setChecked19(true);
        checkedRes.setChecked20(true);
        checkedRes.setChecked21(true);
        checkedRes.setChecked22(true);
        checkedRes.setChecked23(true);
        checkedRes.setChecked24(true);
        checkedRes.setChecked25(true);
        checkedRes.setChecked26(true);
        checkedRes.setChecked27(true);
        checkedRes.setChecked28(true);
        checkedRes.setChecked29(true);
        checkedRes.setChecked30(true);
        checkedRes.setChecked31(true);
        checkedRes.setChecked32(true);
        checkedRes.setChecked33(true);
        checkedRes.setChecked34(true);
        checkedRes.setChecked35(true);
        checkedRes.setChecked36(true);
        checkedRes.setChecked37(true);
        checkedRes.setChecked38(true);
        checkedRes.setChecked39(true);
        checkedRes.setChecked40(true);
        checkedRes.setChecked41(true);
        checkedRes.setChecked42(true);
        checkedRes.setChecked43(true);
        checkedRes.setChecked44(true);
        checkedRes.setChecked45(true);
        checkedRes.setChecked46(true);
        checkedRes.setChecked47(true);
        checkedRes.setChecked48(true);
        checkedRes.setChecked49(true);
        checkedRes.setChecked50(true);
        checkedRes.setChecked51(true);
        checkedRes.setChecked52(true);
        checkedRes.setChecked53(true);
        checkedRes.setChecked54(true);
        checkedRes.setChecked55(true);
        checkedRes.setChecked56(true);
        checkedRes.setChecked57(true);
        checkedRes.setChecked58(true);
        checkedRes.setChecked59(true);
        checkedRes.setChecked60(true);
        checkedRes.setChecked61(true);
        checkedRes.setChecked62(true);
        checkedRes.setChecked63(true);
        checkedRes.setChecked64(true);
        checkedRes.setChecked65(true);
        checkedRes.setChecked66(true);
        checkedRes.setChecked67(true);
        checkedRes.setChecked68(true);
        checkedRes.setChecked69(true);
        checkedRes.setChecked70(true);
        checkedRes.setChecked71(true);
        checkedRes.setChecked72(true);
        checkedRes.setChecked73(true);
        checkedRes.setChecked74(true);
        checkedRes.setChecked75(true);
        checkedRes.setChecked76(true);
        checkedRes.setChecked77(true);
        checkedRes.setChecked78(true);
        checkedRes.setChecked79(true);
        checkedRes.setChecked80(true);
        checkedRes.setChecked81(true);
        checkedRes.setChecked82(true);
        checkedRes.setChecked83(true);
        checkedRes.setChecked84(true);
        checkedRes.setChecked85(true);
        checkedRes.setChecked86(true);
        checkedRes.setChecked87(true);
        checkedRes.setChecked88(true);
        checkedRes.setChecked89(true);
        checkedRes.setChecked90(true);
        checkedRes.setChecked91(true);
        checkedRes.setChecked92(true);
        checkedRes.setChecked93(true);
        checkedRes.setChecked94(true);
        checkedRes.setChecked95(true);
        checkedRes.setChecked96(true);
        checkedRes.setChecked97(true);
        checkedRes.setChecked98(true);
        checkedRes.setChecked99(true);
        checkedRes.setChecked100(true);
        model.addAttribute("checkedRes", checkedRes);
        //      今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        List<Category> res02 = categoryDao.findAll();
        List<String> categoryList = new ArrayList<>();
        for (Category item : res02) {
            categoryList.add(item.getCategory());
        }
        model.addAttribute("categoryList", categoryList);
        SubCategory subCategory = new SubCategory();
        model.addAttribute("subCategory", subCategory);

        Page<SubCategory> subCategoryPage = null;
        //      サブカテゴリーを検索
        if (subCategoryLastSearch == 0) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        if (subCategoryLastSearch == 1) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        SubCategoryListTemp = subCategoryPage.getContent();
        model.addAttribute("subCategoryPage", subCategoryPage);
        model.addAttribute("subCategoryPageSize", subCategoryPage.getNumberOfElements());
        model.addAttribute("subCategoryPage", subCategoryPage);

        return "sub_category_home";
    }

    //  カテゴリーでサブカテゴリーを検索    
    @PostMapping("/sub_category_home_search_category")
    public String SubCategoryHomeSearchCategory(@ModelAttribute("subCategory") SubCategory subCategory, Model model) {

        List<Category> res02 = categoryDao.findAll();
        List<String> categoryList = new ArrayList<>();
        for (Category item : res02) {
            categoryList.add(item.getCategory());
        }
        model.addAttribute("categoryList", categoryList);
        model.addAttribute("subCategory", subCategory);
        //      チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);
        //      今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        //      カテゴリーでサブカテゴリーを検索
        Page<SubCategory> subCategoryPage = mainService.findSubCategoryPageByCategory(sortDescFlag, pageNum, pageSize, subCategory.getCategory());
        subCategoryLastSearch = 1;
        subCategorylastKeyWordStr = subCategory.getCategory();
        SubCategoryListTemp = subCategoryPage.getContent();
        model.addAttribute("subCategoryPage", subCategoryPage);
        model.addAttribute("subCategoryPageSize", subCategoryPage.getNumberOfElements());

        return "sub_category_home";

    }

    //  サブカテゴリー追加
    @RequestMapping(value = "/sub_category_add")
    public String subCategoryAdd(Model model) {

        SubCategory subCategoryInput = new SubCategory();
        model.addAttribute("subCategoryInput", subCategoryInput);
        model.addAttribute("error", "");

        List<Category> res = categoryDao.findAll();
        List<String> categoryList = new ArrayList<>();
        for (Category item : res) {
            categoryList.add(item.getCategory());
        }
        model.addAttribute("categoryList", categoryList);

        return "sub_category_add";
    }

    //  サブカテゴリー追加  
    @PostMapping("/sub_category_add")
    public String subCategoryAdd(@ModelAttribute("subCategoryInput") SubCategory subCategoryInput, Model model) {

        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);
        //      今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        List<Category> res02 = categoryDao.findAll();
        List<String> categoryList = new ArrayList<>();
        for (Category item : res02) {
            categoryList.add(item.getCategory());
        }
        model.addAttribute("categoryList", categoryList);
        SubCategory subCategory = new SubCategory();
        model.addAttribute("subCategory", subCategory);
        //      サブカテゴリー追加
        SubCategoryAddResponse res = mainService.subCategoryAdd(subCategoryInput);
        //      サブカテゴリー追加結果を確認
        if (res.getCode() != "200") {
            model.addAttribute("error", res.getMessage());
            return "sub_category_add";
        }

        Page<SubCategory> subCategoryPage = null;
        //      サブカテゴリーを検索
        if (subCategoryLastSearch == 0) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        if (subCategoryLastSearch == 1) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        SubCategoryListTemp = subCategoryPage.getContent();
        model.addAttribute("subCategoryPage", subCategoryPage);
        model.addAttribute("subCategoryPageSize", subCategoryPage.getNumberOfElements());

        return "sub_category_home";
    }

    //  サブカテゴリー更新
    @RequestMapping(value = "/sub_category_edit")
    public String subCategoryEdit(Model model) {

        List<Category> res02 = categoryDao.findAll();
        List<String> categoryList = new ArrayList<>();
        for (Category item : res02) {
            categoryList.add(item.getCategory());
        }
        model.addAttribute("categoryList", categoryList);

        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);
        //      今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        SubCategory subCategory = new SubCategory();
        model.addAttribute("subCategory", subCategory);

        Page<SubCategory> subCategoryPage = null;
        //      サブカテゴリーを検索
        if (subCategoryLastSearch == 0) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        if (subCategoryLastSearch == 1) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        model.addAttribute("subCategoryPage", subCategoryPage);
        model.addAttribute("subCategoryPageSize", subCategoryPage.getNumberOfElements());
        //      チェックしてないを判断
        if (checkedResTemp == null)
            return "sub_category_home";
        //      二つ以上チェックしたを判断
        if (checkedResTempCount >= 2)
            return "sub_category_home";
        //      チェックの結果を確認
        int counter0 = 0;
        int counter = 0;
        if ((checkedResTemp.isChecked1() && pageNumTemp == 0)
            || (checkedResTemp.isChecked1() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked1() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 1;
        if ((checkedResTemp.isChecked2() && pageNumTemp == 0)
            || (checkedResTemp.isChecked2() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked2() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 2;
        if ((checkedResTemp.isChecked3() && pageNumTemp == 0)
            || (checkedResTemp.isChecked3() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked3() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 3;
        if ((checkedResTemp.isChecked4() && pageNumTemp == 0)
            || (checkedResTemp.isChecked4() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked4() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 4;
        if ((checkedResTemp.isChecked5() && pageNumTemp == 0)
            || (checkedResTemp.isChecked5() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked5() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 5;
        if ((checkedResTemp.isChecked6() && pageNumTemp == 0)
            || (checkedResTemp.isChecked6() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked6() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 6;
        if ((checkedResTemp.isChecked7() && pageNumTemp == 0)
            || (checkedResTemp.isChecked7() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked7() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 7;
        if ((checkedResTemp.isChecked8() && pageNumTemp == 0)
            || (checkedResTemp.isChecked8() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked8() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 8;
        if ((checkedResTemp.isChecked9() && pageNumTemp == 0)
            || (checkedResTemp.isChecked9() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked9() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 9;
        if ((checkedResTemp.isChecked10() && pageNumTemp == 0)
            || (checkedResTemp.isChecked10() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked10() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 10;
        if ((checkedResTemp.isChecked11() && pageNumTemp == 0)
            || (checkedResTemp.isChecked11() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 11;
        if ((checkedResTemp.isChecked12() && pageNumTemp == 0)
            || (checkedResTemp.isChecked12() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 12;
        if ((checkedResTemp.isChecked13() && pageNumTemp == 0)
            || (checkedResTemp.isChecked13() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 13;
        if ((checkedResTemp.isChecked14() && pageNumTemp == 0)
            || (checkedResTemp.isChecked14() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 14;
        if ((checkedResTemp.isChecked15() && pageNumTemp == 0)
            || (checkedResTemp.isChecked15() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 15;
        if ((checkedResTemp.isChecked16() && pageNumTemp == 0)
            || (checkedResTemp.isChecked16() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 16;
        if ((checkedResTemp.isChecked17() && pageNumTemp == 0)
            || (checkedResTemp.isChecked17() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 17;
        if ((checkedResTemp.isChecked18() && pageNumTemp == 0)
            || (checkedResTemp.isChecked18() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 18;
        if ((checkedResTemp.isChecked19() && pageNumTemp == 0)
            || (checkedResTemp.isChecked19() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 19;
        if ((checkedResTemp.isChecked20() && pageNumTemp == 0)
            || (checkedResTemp.isChecked20() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 20;
        if ((checkedResTemp.isChecked21() && pageNumTemp == 0)
            || (checkedResTemp.isChecked21() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 21;
        if ((checkedResTemp.isChecked22() && pageNumTemp == 0)
            || (checkedResTemp.isChecked22() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 22;
        if ((checkedResTemp.isChecked23() && pageNumTemp == 0)
            || (checkedResTemp.isChecked23() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 23;
        if ((checkedResTemp.isChecked24() && pageNumTemp == 0)
            || (checkedResTemp.isChecked24() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 24;
        if ((checkedResTemp.isChecked25() && pageNumTemp == 0)
            || (checkedResTemp.isChecked25() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 25;
        if ((checkedResTemp.isChecked26() && pageNumTemp == 0)
            || (checkedResTemp.isChecked26() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 26;
        if ((checkedResTemp.isChecked27() && pageNumTemp == 0)
            || (checkedResTemp.isChecked27() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 27;
        if ((checkedResTemp.isChecked28() && pageNumTemp == 0)
            || (checkedResTemp.isChecked28() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 28;
        if ((checkedResTemp.isChecked29() && pageNumTemp == 0)
            || (checkedResTemp.isChecked29() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 29;
        if ((checkedResTemp.isChecked30() && pageNumTemp == 0)
            || (checkedResTemp.isChecked30() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 30;
        if ((checkedResTemp.isChecked31() && pageNumTemp == 0)
            || (checkedResTemp.isChecked31() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 31;
        if ((checkedResTemp.isChecked32() && pageNumTemp == 0)
            || (checkedResTemp.isChecked32() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 32;
        if ((checkedResTemp.isChecked33() && pageNumTemp == 0)
            || (checkedResTemp.isChecked33() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 33;
        if ((checkedResTemp.isChecked34() && pageNumTemp == 0)
            || (checkedResTemp.isChecked34() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 34;
        if ((checkedResTemp.isChecked35() && pageNumTemp == 0)
            || (checkedResTemp.isChecked35() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 35;
        if ((checkedResTemp.isChecked36() && pageNumTemp == 0)
            || (checkedResTemp.isChecked36() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 36;
        if ((checkedResTemp.isChecked37() && pageNumTemp == 0)
            || (checkedResTemp.isChecked37() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 37;
        if ((checkedResTemp.isChecked38() && pageNumTemp == 0)
            || (checkedResTemp.isChecked38() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 38;
        if ((checkedResTemp.isChecked39() && pageNumTemp == 0)
            || (checkedResTemp.isChecked39() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 39;
        if ((checkedResTemp.isChecked40() && pageNumTemp == 0)
            || (checkedResTemp.isChecked40() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 40;
        if ((checkedResTemp.isChecked41() && pageNumTemp == 0)
            || (checkedResTemp.isChecked41() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 41;
        if ((checkedResTemp.isChecked42() && pageNumTemp == 0)
            || (checkedResTemp.isChecked42() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 42;
        if ((checkedResTemp.isChecked43() && pageNumTemp == 0)
            || (checkedResTemp.isChecked43() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 43;
        if ((checkedResTemp.isChecked44() && pageNumTemp == 0)
            || (checkedResTemp.isChecked44() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 44;
        if ((checkedResTemp.isChecked45() && pageNumTemp == 0)
            || (checkedResTemp.isChecked45() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 45;
        if ((checkedResTemp.isChecked46() && pageNumTemp == 0)
            || (checkedResTemp.isChecked46() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 46;
        if ((checkedResTemp.isChecked47() && pageNumTemp == 0)
            || (checkedResTemp.isChecked47() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 47;
        if ((checkedResTemp.isChecked48() && pageNumTemp == 0)
            || (checkedResTemp.isChecked48() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 48;
        if ((checkedResTemp.isChecked49() && pageNumTemp == 0)
            || (checkedResTemp.isChecked49() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 49;
        if ((checkedResTemp.isChecked50() && pageNumTemp == 0)
            || (checkedResTemp.isChecked50() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 50;
        if (checkedResTemp.isChecked51() && pageNumTemp == 0)
            counter0 = 51;
        if (checkedResTemp.isChecked52() && pageNumTemp == 0)
            counter0 = 52;
        if (checkedResTemp.isChecked53() && pageNumTemp == 0)
            counter0 = 53;
        if (checkedResTemp.isChecked54() && pageNumTemp == 0)
            counter0 = 54;
        if (checkedResTemp.isChecked55() && pageNumTemp == 0)
            counter0 = 55;
        if (checkedResTemp.isChecked56() && pageNumTemp == 0)
            counter0 = 56;
        if (checkedResTemp.isChecked57() && pageNumTemp == 0)
            counter0 = 57;
        if (checkedResTemp.isChecked58() && pageNumTemp == 0)
            counter0 = 58;
        if (checkedResTemp.isChecked59() && pageNumTemp == 0)
            counter0 = 59;
        if (checkedResTemp.isChecked60() && pageNumTemp == 0)
            counter0 = 60;
        if (checkedResTemp.isChecked61() && pageNumTemp == 0)
            counter0 = 61;
        if (checkedResTemp.isChecked62() && pageNumTemp == 0)
            counter0 = 62;
        if (checkedResTemp.isChecked63() && pageNumTemp == 0)
            counter0 = 63;
        if (checkedResTemp.isChecked64() && pageNumTemp == 0)
            counter0 = 64;
        if (checkedResTemp.isChecked65() && pageNumTemp == 0)
            counter0 = 65;
        if (checkedResTemp.isChecked66() && pageNumTemp == 0)
            counter0 = 66;
        if (checkedResTemp.isChecked67() && pageNumTemp == 0)
            counter0 = 67;
        if (checkedResTemp.isChecked68() && pageNumTemp == 0)
            counter0 = 68;
        if (checkedResTemp.isChecked69() && pageNumTemp == 0)
            counter0 = 69;
        if (checkedResTemp.isChecked70() && pageNumTemp == 0)
            counter0 = 70;
        if (checkedResTemp.isChecked71() && pageNumTemp == 0)
            counter0 = 71;
        if (checkedResTemp.isChecked72() && pageNumTemp == 0)
            counter0 = 72;
        if (checkedResTemp.isChecked73() && pageNumTemp == 0)
            counter0 = 73;
        if (checkedResTemp.isChecked74() && pageNumTemp == 0)
            counter0 = 74;
        if (checkedResTemp.isChecked75() && pageNumTemp == 0)
            counter0 = 75;
        if (checkedResTemp.isChecked76() && pageNumTemp == 0)
            counter0 = 76;
        if (checkedResTemp.isChecked77() && pageNumTemp == 0)
            counter0 = 77;
        if (checkedResTemp.isChecked78() && pageNumTemp == 0)
            counter0 = 78;
        if (checkedResTemp.isChecked79() && pageNumTemp == 0)
            counter0 = 79;
        if (checkedResTemp.isChecked80() && pageNumTemp == 0)
            counter0 = 80;
        if (checkedResTemp.isChecked81() && pageNumTemp == 0)
            counter0 = 81;
        if (checkedResTemp.isChecked82() && pageNumTemp == 0)
            counter0 = 82;
        if (checkedResTemp.isChecked83() && pageNumTemp == 0)
            counter0 = 83;
        if (checkedResTemp.isChecked84() && pageNumTemp == 0)
            counter0 = 84;
        if (checkedResTemp.isChecked85() && pageNumTemp == 0)
            counter0 = 85;
        if (checkedResTemp.isChecked86() && pageNumTemp == 0)
            counter0 = 86;
        if (checkedResTemp.isChecked87() && pageNumTemp == 0)
            counter0 = 87;
        if (checkedResTemp.isChecked88() && pageNumTemp == 0)
            counter0 = 88;
        if (checkedResTemp.isChecked89() && pageNumTemp == 0)
            counter0 = 89;
        if (checkedResTemp.isChecked90() && pageNumTemp == 0)
            counter0 = 90;
        if (checkedResTemp.isChecked91() && pageNumTemp == 0)
            counter0 = 91;
        if (checkedResTemp.isChecked92() && pageNumTemp == 0)
            counter0 = 92;
        if (checkedResTemp.isChecked93() && pageNumTemp == 0)
            counter0 = 93;
        if (checkedResTemp.isChecked94() && pageNumTemp == 0)
            counter0 = 94;
        if (checkedResTemp.isChecked95() && pageNumTemp == 0)
            counter0 = 95;
        if (checkedResTemp.isChecked96() && pageNumTemp == 0)
            counter0 = 96;
        if (checkedResTemp.isChecked97() && pageNumTemp == 0)
            counter0 = 97;
        if (checkedResTemp.isChecked98() && pageNumTemp == 0)
            counter0 = 98;
        if (checkedResTemp.isChecked99() && pageNumTemp == 0)
            counter0 = 99;
        if (checkedResTemp.isChecked100() && pageNumTemp == 0)
            counter0 = 100;
        SubCategory subCategoryInput = new SubCategory();
        for (SubCategory item : SubCategoryListTemp) {
            counter++;
            if (counter == counter0) {
                subCategoryInput = item;
                subCategoryIdTemp = item.getId();
                oldSubCategoryTemp = item.getSubCategory();
                break;
            }
        }
        model.addAttribute("subCategoryInput", subCategoryInput);
        model.addAttribute("error", "");

        checkedResTemp = null;

        return "sub_category_edit";
    }

    //  サブカテゴリー更新    
    @PostMapping("/sub_category_edit")
    public String subCategoryEditPost(@ModelAttribute("subCategoryInput") SubCategory subCategoryInput, Model model) {

        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);
        //      今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        List<Category> res02 = categoryDao.findAll();
        List<String> categoryList = new ArrayList<>();
        for (Category item : res02) {
            categoryList.add(item.getCategory());
        }
        model.addAttribute("categoryList", categoryList);
        SubCategory subCategory = new SubCategory();
        model.addAttribute("subCategory", subCategory);
        subCategoryInput.setId(subCategoryIdTemp);

        //      サブカテゴリー更新
        SubCategoryAddResponse res = mainService.subCategoryEdit(subCategoryInput);
        //      サブカテゴリー更新結果を確認
        if (res.getCode() != "200") {
            model.addAttribute("subCategoryInput", subCategoryInput);
            model.addAttribute("error", res.getMessage());
            return "sub_category_edit";
        }
        //      サブカテゴリーのニュースを更新
        newsDao.updateNewsSubCategoryByOldSubCategory(subCategoryInput.getSubCategory(), oldSubCategoryTemp);
        
        Page<SubCategory> subCategoryPage = null;
        //      サブカテゴリーを検索
        if (subCategoryLastSearch == 0) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        if (subCategoryLastSearch == 1) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        model.addAttribute("subCategoryPage", subCategoryPage);
        model.addAttribute("subCategoryPageSize", subCategoryPage.getNumberOfElements());
        
        return "sub_category_home";
    }

    //  サブカテゴリーを削除
    @RequestMapping(value = "/sub_category_delete")
    public String SubCategoryDelete(Model model) {
        
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);
        //        今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        
        List<Category> res02 = categoryDao.findAll();
        List<String> categoryList = new ArrayList<>();
        for (Category item : res02) {
            categoryList.add(item.getCategory());
        }
        model.addAttribute("categoryList", categoryList);
        SubCategory subCategory = new SubCategory();
        model.addAttribute("subCategory", subCategory);

        Page<SubCategory> subCategoryPage = null;
        //        サブカテゴリーを検索
        if (subCategoryLastSearch == 0) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        if (subCategoryLastSearch == 1) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        SubCategoryListTemp = subCategoryPage.getContent();
        model.addAttribute("subCategoryPage", subCategoryPage);
        model.addAttribute("subCategoryPageSize", subCategoryPage.getNumberOfElements());
        //        チェックしてないを判断
        if (checkedResTemp == null)
            return "sub_category_home";
        //        チェックの結果を確認
        int[] checked = new int[100];
        for (int i = 0; i < 100; i++) {
            checked[i] = 0;
        }
        if ((checkedResTemp.isChecked1() && pageNumTemp == 0)
            || (checkedResTemp.isChecked1() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked1() && pageSize == 50 && pageNumTemp != 0))
            checked[0] = 1;
        if ((checkedResTemp.isChecked2() && pageNumTemp == 0)
            || (checkedResTemp.isChecked2() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked2() && pageSize == 50 && pageNumTemp != 0))
            checked[1] = 1;
        if ((checkedResTemp.isChecked3() && pageNumTemp == 0)
            || (checkedResTemp.isChecked3() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked3() && pageSize == 50 && pageNumTemp != 0))
            checked[2] = 1;
        if ((checkedResTemp.isChecked4() && pageNumTemp == 0)
            || (checkedResTemp.isChecked4() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked4() && pageSize == 50 && pageNumTemp != 0))
            checked[3] = 1;
        if ((checkedResTemp.isChecked5() && pageNumTemp == 0)
            || (checkedResTemp.isChecked5() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked5() && pageSize == 50 && pageNumTemp != 0))
            checked[4] = 1;
        if ((checkedResTemp.isChecked6() && pageNumTemp == 0)
            || (checkedResTemp.isChecked6() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked6() && pageSize == 50 && pageNumTemp != 0))
            checked[5] = 1;
        if ((checkedResTemp.isChecked7() && pageNumTemp == 0)
            || (checkedResTemp.isChecked7() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked7() && pageSize == 50 && pageNumTemp != 0))
            checked[6] = 1;
        if ((checkedResTemp.isChecked8() && pageNumTemp == 0)
            || (checkedResTemp.isChecked8() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked8() && pageSize == 50 && pageNumTemp != 0))
            checked[7] = 1;
        if ((checkedResTemp.isChecked9() && pageNumTemp == 0)
            || (checkedResTemp.isChecked9() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked9() && pageSize == 50 && pageNumTemp != 0))
            checked[8] = 1;
        if ((checkedResTemp.isChecked10() && pageNumTemp == 0)
            || (checkedResTemp.isChecked10() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked10() && pageSize == 50 && pageNumTemp != 0))
            checked[9] = 1;
        if ((checkedResTemp.isChecked11() && pageNumTemp == 0)
            || (checkedResTemp.isChecked11() && pageSize == 50 && pageNumTemp != 0))
            checked[10] = 1;
        if ((checkedResTemp.isChecked12() && pageNumTemp == 0)
            || (checkedResTemp.isChecked12() && pageSize == 50 && pageNumTemp != 0))
            checked[11] = 1;
        if ((checkedResTemp.isChecked13() && pageNumTemp == 0)
            || (checkedResTemp.isChecked13() && pageSize == 50 && pageNumTemp != 0))
            checked[12] = 1;
        if ((checkedResTemp.isChecked14() && pageNumTemp == 0)
            || (checkedResTemp.isChecked14() && pageSize == 50 && pageNumTemp != 0))
            checked[13] = 1;
        if ((checkedResTemp.isChecked15() && pageNumTemp == 0)
            || (checkedResTemp.isChecked15() && pageSize == 50 && pageNumTemp != 0))
            checked[14] = 1;
        if ((checkedResTemp.isChecked16() && pageNumTemp == 0)
            || (checkedResTemp.isChecked16() && pageSize == 50 && pageNumTemp != 0))
            checked[15] = 1;
        if ((checkedResTemp.isChecked17() && pageNumTemp == 0)
            || (checkedResTemp.isChecked17() && pageSize == 50 && pageNumTemp != 0))
            checked[16] = 1;
        if ((checkedResTemp.isChecked18() && pageNumTemp == 0)
            || (checkedResTemp.isChecked18() && pageSize == 50 && pageNumTemp != 0))
            checked[17] = 1;
        if ((checkedResTemp.isChecked19() && pageNumTemp == 0)
            || (checkedResTemp.isChecked19() && pageSize == 50 && pageNumTemp != 0))
            checked[18] = 1;
        if ((checkedResTemp.isChecked20() && pageNumTemp == 0)
            || (checkedResTemp.isChecked20() && pageSize == 50 && pageNumTemp != 0))
            checked[19] = 1;
        if ((checkedResTemp.isChecked21() && pageNumTemp == 0)
            || (checkedResTemp.isChecked21() && pageSize == 50 && pageNumTemp != 0))
            checked[20] = 1;
        if ((checkedResTemp.isChecked22() && pageNumTemp == 0)
            || (checkedResTemp.isChecked22() && pageSize == 50 && pageNumTemp != 0))
            checked[21] = 1;
        if ((checkedResTemp.isChecked23() && pageNumTemp == 0)
            || (checkedResTemp.isChecked23() && pageSize == 50 && pageNumTemp != 0))
            checked[22] = 1;
        if ((checkedResTemp.isChecked24() && pageNumTemp == 0)
            || (checkedResTemp.isChecked24() && pageSize == 50 && pageNumTemp != 0))
            checked[23] = 1;
        if ((checkedResTemp.isChecked25() && pageNumTemp == 0)
            || (checkedResTemp.isChecked25() && pageSize == 50 && pageNumTemp != 0))
            checked[24] = 1;
        if ((checkedResTemp.isChecked26() && pageNumTemp == 0)
            || (checkedResTemp.isChecked26() && pageSize == 50 && pageNumTemp != 0))
            checked[25] = 1;
        if ((checkedResTemp.isChecked27() && pageNumTemp == 0)
            || (checkedResTemp.isChecked27() && pageSize == 50 && pageNumTemp != 0))
            checked[26] = 1;
        if ((checkedResTemp.isChecked28() && pageNumTemp == 0)
            || (checkedResTemp.isChecked28() && pageSize == 50 && pageNumTemp != 0))
            checked[27] = 1;
        if ((checkedResTemp.isChecked29() && pageNumTemp == 0)
            || (checkedResTemp.isChecked29() && pageSize == 50 && pageNumTemp != 0))
            checked[28] = 1;
        if ((checkedResTemp.isChecked30() && pageNumTemp == 0)
            || (checkedResTemp.isChecked30() && pageSize == 50 && pageNumTemp != 0))
            checked[29] = 1;
        if ((checkedResTemp.isChecked31() && pageNumTemp == 0)
            || (checkedResTemp.isChecked31() && pageSize == 50 && pageNumTemp != 0))
            checked[30] = 1;
        if ((checkedResTemp.isChecked32() && pageNumTemp == 0)
            || (checkedResTemp.isChecked32() && pageSize == 50 && pageNumTemp != 0))
            checked[31] = 1;
        if ((checkedResTemp.isChecked33() && pageNumTemp == 0)
            || (checkedResTemp.isChecked33() && pageSize == 50 && pageNumTemp != 0))
            checked[32] = 1;
        if ((checkedResTemp.isChecked34() && pageNumTemp == 0)
            || (checkedResTemp.isChecked34() && pageSize == 50 && pageNumTemp != 0))
            checked[33] = 1;
        if ((checkedResTemp.isChecked35() && pageNumTemp == 0)
            || (checkedResTemp.isChecked35() && pageSize == 50 && pageNumTemp != 0))
            checked[34] = 1;
        if ((checkedResTemp.isChecked36() && pageNumTemp == 0)
            || (checkedResTemp.isChecked36() && pageSize == 50 && pageNumTemp != 0))
            checked[35] = 1;
        if ((checkedResTemp.isChecked37() && pageNumTemp == 0)
            || (checkedResTemp.isChecked37() && pageSize == 50 && pageNumTemp != 0))
            checked[36] = 1;
        if ((checkedResTemp.isChecked38() && pageNumTemp == 0)
            || (checkedResTemp.isChecked38() && pageSize == 50 && pageNumTemp != 0))
            checked[37] = 1;
        if ((checkedResTemp.isChecked39() && pageNumTemp == 0)
            || (checkedResTemp.isChecked39() && pageSize == 50 && pageNumTemp != 0))
            checked[38] = 1;
        if ((checkedResTemp.isChecked40() && pageNumTemp == 0)
            || (checkedResTemp.isChecked40() && pageSize == 50 && pageNumTemp != 0))
            checked[39] = 1;
        if ((checkedResTemp.isChecked41() && pageNumTemp == 0)
            || (checkedResTemp.isChecked41() && pageSize == 50 && pageNumTemp != 0))
            checked[40] = 1;
        if ((checkedResTemp.isChecked42() && pageNumTemp == 0)
            || (checkedResTemp.isChecked42() && pageSize == 50 && pageNumTemp != 0))
            checked[41] = 1;
        if ((checkedResTemp.isChecked43() && pageNumTemp == 0)
            || (checkedResTemp.isChecked43() && pageSize == 50 && pageNumTemp != 0))
            checked[42] = 1;
        if ((checkedResTemp.isChecked44() && pageNumTemp == 0)
            || (checkedResTemp.isChecked44() && pageSize == 50 && pageNumTemp != 0))
            checked[43] = 1;
        if ((checkedResTemp.isChecked45() && pageNumTemp == 0)
            || (checkedResTemp.isChecked45() && pageSize == 50 && pageNumTemp != 0))
            checked[44] = 1;
        if ((checkedResTemp.isChecked46() && pageNumTemp == 0)
            || (checkedResTemp.isChecked46() && pageSize == 50 && pageNumTemp != 0))
            checked[45] = 1;
        if ((checkedResTemp.isChecked47() && pageNumTemp == 0)
            || (checkedResTemp.isChecked47() && pageSize == 50 && pageNumTemp != 0))
            checked[46] = 1;
        if ((checkedResTemp.isChecked48() && pageNumTemp == 0)
            || (checkedResTemp.isChecked48() && pageSize == 50 && pageNumTemp != 0))
            checked[47] = 1;
        if ((checkedResTemp.isChecked49() && pageNumTemp == 0)
            || (checkedResTemp.isChecked49() && pageSize == 50 && pageNumTemp != 0))
            checked[48] = 1;
        if ((checkedResTemp.isChecked50() && pageNumTemp == 0)
            || (checkedResTemp.isChecked50() && pageSize == 50 && pageNumTemp != 0))
            checked[49] = 1;
        if (checkedResTemp.isChecked51() && pageNumTemp == 0)
            checked[50] = 1;
        if (checkedResTemp.isChecked52() && pageNumTemp == 0)
            checked[51] = 1;
        if (checkedResTemp.isChecked53() && pageNumTemp == 0)
            checked[52] = 1;
        if (checkedResTemp.isChecked54() && pageNumTemp == 0)
            checked[53] = 1;
        if (checkedResTemp.isChecked55() && pageNumTemp == 0)
            checked[54] = 1;
        if (checkedResTemp.isChecked56() && pageNumTemp == 0)
            checked[55] = 1;
        if (checkedResTemp.isChecked57() && pageNumTemp == 0)
            checked[56] = 1;
        if (checkedResTemp.isChecked58() && pageNumTemp == 0)
            checked[57] = 1;
        if (checkedResTemp.isChecked59() && pageNumTemp == 0)
            checked[58] = 1;
        if (checkedResTemp.isChecked60() && pageNumTemp == 0)
            checked[59] = 1;
        if (checkedResTemp.isChecked61() && pageNumTemp == 0)
            checked[60] = 1;
        if (checkedResTemp.isChecked62() && pageNumTemp == 0)
            checked[61] = 1;
        if (checkedResTemp.isChecked63() && pageNumTemp == 0)
            checked[62] = 1;
        if (checkedResTemp.isChecked64() && pageNumTemp == 0)
            checked[63] = 1;
        if (checkedResTemp.isChecked65() && pageNumTemp == 0)
            checked[64] = 1;
        if (checkedResTemp.isChecked66() && pageNumTemp == 0)
            checked[65] = 1;
        if (checkedResTemp.isChecked67() && pageNumTemp == 0)
            checked[66] = 1;
        if (checkedResTemp.isChecked68() && pageNumTemp == 0)
            checked[67] = 1;
        if (checkedResTemp.isChecked69() && pageNumTemp == 0)
            checked[68] = 1;
        if (checkedResTemp.isChecked70() && pageNumTemp == 0)
            checked[69] = 1;
        if (checkedResTemp.isChecked71() && pageNumTemp == 0)
            checked[70] = 1;
        if (checkedResTemp.isChecked72() && pageNumTemp == 0)
            checked[71] = 1;
        if (checkedResTemp.isChecked73() && pageNumTemp == 0)
            checked[72] = 1;
        if (checkedResTemp.isChecked74() && pageNumTemp == 0)
            checked[73] = 1;
        if (checkedResTemp.isChecked75() && pageNumTemp == 0)
            checked[74] = 1;
        if (checkedResTemp.isChecked76() && pageNumTemp == 0)
            checked[75] = 1;
        if (checkedResTemp.isChecked77() && pageNumTemp == 0)
            checked[76] = 1;
        if (checkedResTemp.isChecked78() && pageNumTemp == 0)
            checked[77] = 1;
        if (checkedResTemp.isChecked79() && pageNumTemp == 0)
            checked[78] = 1;
        if (checkedResTemp.isChecked80() && pageNumTemp == 0)
            checked[79] = 1;
        if (checkedResTemp.isChecked81() && pageNumTemp == 0)
            checked[80] = 1;
        if (checkedResTemp.isChecked82() && pageNumTemp == 0)
            checked[81] = 1;
        if (checkedResTemp.isChecked83() && pageNumTemp == 0)
            checked[82] = 1;
        if (checkedResTemp.isChecked84() && pageNumTemp == 0)
            checked[83] = 1;
        if (checkedResTemp.isChecked85() && pageNumTemp == 0)
            checked[84] = 1;
        if (checkedResTemp.isChecked86() && pageNumTemp == 0)
            checked[85] = 1;
        if (checkedResTemp.isChecked87() && pageNumTemp == 0)
            checked[86] = 1;
        if (checkedResTemp.isChecked88() && pageNumTemp == 0)
            checked[87] = 1;
        if (checkedResTemp.isChecked89() && pageNumTemp == 0)
            checked[88] = 1;
        if (checkedResTemp.isChecked90() && pageNumTemp == 0)
            checked[89] = 1;
        if (checkedResTemp.isChecked91() && pageNumTemp == 0)
            checked[90] = 1;
        if (checkedResTemp.isChecked92() && pageNumTemp == 0)
            checked[91] = 1;
        if (checkedResTemp.isChecked93() && pageNumTemp == 0)
            checked[92] = 1;
        if (checkedResTemp.isChecked94() && pageNumTemp == 0)
            checked[93] = 1;
        if (checkedResTemp.isChecked95() && pageNumTemp == 0)
            checked[94] = 1;
        if (checkedResTemp.isChecked96() && pageNumTemp == 0)
            checked[95] = 1;
        if (checkedResTemp.isChecked97() && pageNumTemp == 0)
            checked[96] = 1;
        if (checkedResTemp.isChecked98() && pageNumTemp == 0)
            checked[97] = 1;
        if (checkedResTemp.isChecked99() && pageNumTemp == 0)
            checked[98] = 1;
        if (checkedResTemp.isChecked100() && pageNumTemp == 0)
            checked[99] = 1;
        int counter = 0;
        for (SubCategory item : SubCategoryListTemp) {
            if (checked[counter] == 1 && item.getSubCategoryNewsCount() == 0) {

                mainService.subCategoryDelete(item.getId());

            }
            counter++;
        }

        subCategoryPage = null;
        //        サブカテゴリーを検索
        if (subCategoryLastSearch == 0) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        if (subCategoryLastSearch == 1) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        SubCategoryListTemp = subCategoryPage.getContent();
        model.addAttribute("subCategoryPage", subCategoryPage);
        model.addAttribute("subCategoryPageSize", subCategoryPage.getNumberOfElements());
        
        checkedResTemp = null;
        
        return "sub_category_home";
    }

    //////////////////////////////////////////////////
    //  各ページの件数を10に設定
    @RequestMapping(value = "/home_set_pageSize_10")
    public String HomeSetPageSize10(Model model) {
        
        pageSize = 10;

        //        カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //        サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //        ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //        ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //        発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //        発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //        複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        //        ニュースを検索キーワードを更新
        if (lastSearch == 1)
            news01.setCategory(lastKeyWordStr);
        if (lastSearch == 2)
            news02.setSubCategory(lastKeyWordStr);
        if (lastSearch == 3)
            news03.setNewsTitle(lastKeyWordStr);
        if (lastSearch == 4)
            news04.setNewsSubTitle(lastKeyWordStr);
        if (lastSearch == 5)
            news05.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 6)
            news06.setReleaseTime(lastKeyWordStr);

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //        チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //      今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);

        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        return "home";
    }

    //  各ページの件数を50に設定
    @RequestMapping(value = "/home_set_pageSize_50")
    public String HomeSetPageSize50(Model model) {

        pageSize = 50;

        //      カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //      サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //      ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //      ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //      発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //      発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //      複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        //      ニュースを検索キーワードを更新
        if (lastSearch == 1)
            news01.setCategory(lastKeyWordStr);
        if (lastSearch == 2)
            news02.setSubCategory(lastKeyWordStr);
        if (lastSearch == 3)
            news03.setNewsTitle(lastKeyWordStr);
        if (lastSearch == 4)
            news04.setNewsSubTitle(lastKeyWordStr);
        if (lastSearch == 5)
            news05.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 6)
            news06.setReleaseTime(lastKeyWordStr);

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //      チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //    　今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        return "home";

    }

    //  各ページの件数を100に設定
    @RequestMapping(value = "/home_set_pageSize_100")
    public String HomePageSize100(Model model) {
        
        pageSize = 100;

        //      カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //      サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //      ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //      ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //      発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //      発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //      複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        //      ニュースの検索キーワードを更新
        if (lastSearch == 1)
            news01.setCategory(lastKeyWordStr);
        if (lastSearch == 2)
            news02.setSubCategory(lastKeyWordStr);
        if (lastSearch == 3)
            news03.setNewsTitle(lastKeyWordStr);
        if (lastSearch == 4)
            news04.setNewsSubTitle(lastKeyWordStr);
        if (lastSearch == 5)
            news05.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 6)
            news06.setReleaseTime(lastKeyWordStr);

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //      チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //    今のページをゼロに設定
        int pageNum = 0;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());
        
        return "home";

    }

    //  各ページの件数を10に設定
    @RequestMapping(value = "/category_home_set_pageSize_10")
    public String CategoryHomeSetPageSize10(Model model) {
        
        pageSize = 10;

        int pageNum = 0;

        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        Page<Category> categoryPage = null;
        //      全てのカテゴリーを検索
        categoryPage = mainService.findCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        CategoryListTemp = categoryPage.getContent();
        
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("categoryPageSize", categoryPage.getNumberOfElements());

        List<Category> findCategoryByAllRes = null;
        List<News> findByCategoryRes = null;
        List<News> findByCategoryAndSubCategoryRes = null;
        List<SubCategory> findSubCategoryByCategoryRes = null;
        int newsCount = 0;

//      カテゴリーとサブカテゴリーのニュース数を更新
        findCategoryByAllRes = mainService.findCategoryByAll();
        for (Category item : findCategoryByAllRes) {
            findByCategoryRes = mainService.findByCategory(item.getCategory());
            newsCount = findByCategoryRes.size();
            item.setNewsCount(newsCount);
            mainService.categoryEditNewsCount(item);
            findSubCategoryByCategoryRes = mainService.findSubCategoryByCategory(item.getCategory());
            for (SubCategory item02 : findSubCategoryByCategoryRes) {
                findByCategoryAndSubCategoryRes = mainService.findByCategoryAndSubCategory(item02.getCategory(), item02.getSubCategory());
                newsCount = findByCategoryAndSubCategoryRes.size();
                item02.setSubCategoryNewsCount(newsCount);
                mainService.subCategoryEditNewsCount(item02);
            }
        }

        return "category_home";
    }

    //  各ページの件数を50に設定    
    @RequestMapping(value = "/category_home_set_pageSize_50")
    public String CategoryHomeSetPageSize50(Model model) {
        
        pageSize = 50;

        int pageNum = 0;
        
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        
        Page<Category> categoryPage = null;
        //      全てのカテゴリーを検索
        categoryPage = mainService.findCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        CategoryListTemp = categoryPage.getContent();

        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("categoryPageSize", categoryPage.getNumberOfElements());

        List<Category> findCategoryByAllRes = null;
        List<News> findByCategoryRes = null;
        List<News> findByCategoryAndSubCategoryRes = null;
        List<SubCategory> findSubCategoryByCategoryRes = null;
        int newsCount = 0;

        //      カテゴリーとサブカテゴリーのニュース数を更新
        findCategoryByAllRes = mainService.findCategoryByAll();
        for (Category item : findCategoryByAllRes) {
            findByCategoryRes = mainService.findByCategory(item.getCategory());
            newsCount = findByCategoryRes.size();
            item.setNewsCount(newsCount);
            mainService.categoryEditNewsCount(item);
            findSubCategoryByCategoryRes = mainService.findSubCategoryByCategory(item.getCategory());
            for (SubCategory item02 : findSubCategoryByCategoryRes) {
                findByCategoryAndSubCategoryRes = mainService.findByCategoryAndSubCategory(item02.getCategory(), item02.getSubCategory());
                newsCount = findByCategoryAndSubCategoryRes.size();
                item02.setSubCategoryNewsCount(newsCount);
                mainService.subCategoryEditNewsCount(item02);
            }
        }

        return "category_home";

    }

    //  各ページの件数を100に設定
    @RequestMapping(value = "/category_home_set_pageSize_100")
    public String CategoryHomePageSize100(Model model) {
        
        pageSize = 100;

        int pageNum = 0;
        
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);
        
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        
        Page<Category> categoryPage = null;
        //      全てのカテゴリーを検索
        categoryPage = mainService.findCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        CategoryListTemp = categoryPage.getContent();
        
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("categoryPageSize", categoryPage.getNumberOfElements());

        List<Category> findCategoryByAllRes = null;
        List<News> findByCategoryRes = null;
        List<News> findByCategoryAndSubCategoryRes = null;
        List<SubCategory> findSubCategoryByCategoryRes = null;
        int newsCount = 0;

        //      カテゴリーとサブカテゴリーのニュース数を更新
        findCategoryByAllRes = mainService.findCategoryByAll();
        for (Category item : findCategoryByAllRes) {
            findByCategoryRes = mainService.findByCategory(item.getCategory());
            newsCount = findByCategoryRes.size();
            item.setNewsCount(newsCount);
            mainService.categoryEditNewsCount(item);
            findSubCategoryByCategoryRes = mainService.findSubCategoryByCategory(item.getCategory());
            for (SubCategory item02 : findSubCategoryByCategoryRes) {
                findByCategoryAndSubCategoryRes = mainService.findByCategoryAndSubCategory(item02.getCategory(), item02.getSubCategory());
                newsCount = findByCategoryAndSubCategoryRes.size();
                item02.setSubCategoryNewsCount(newsCount);
                mainService.subCategoryEditNewsCount(item02);
            }
        }

        return "category_home";

    }

    //  各ページの件数を10に設定
    @RequestMapping(value = "/sub_category_home_set_pageSize_10")
    public String SubCategoryHomeSetPageSize10(Model model) {
    
        pageSize = 10;

        int pageNum = 0;

        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);
        
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        //      全てのカテゴリーを検索
        List<Category> res02 = categoryDao.findAll();
        List<String> categoryList = new ArrayList<>();
        for (Category item : res02) {
            categoryList.add(item.getCategory());
        }
        model.addAttribute("categoryList", categoryList);
        SubCategory subCategory = new SubCategory();
        model.addAttribute("subCategory", subCategory);
        Page<SubCategory> subCategoryPage = null;
        //      サブカテゴリーを検索
        if (subCategoryLastSearch == 0) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        if (subCategoryLastSearch == 1) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        SubCategoryListTemp = subCategoryPage.getContent();
        model.addAttribute("subCategoryPage", subCategoryPage);
        model.addAttribute("subCategoryPageSize", subCategoryPage.getNumberOfElements());
        model.addAttribute("subCategoryPage", subCategoryPage);

        List<Category> findCategoryByAllRes = null;
        List<News> findByCategoryRes = null;
        List<News> findByCategoryAndSubCategoryRes = null;
        List<SubCategory> findSubCategoryByCategoryRes = null;
        int newsCount = 0;

        //      カテゴリーとサブカテゴリーのニュース数を更新
        findCategoryByAllRes = mainService.findCategoryByAll();
        for (Category item : findCategoryByAllRes) {
            findByCategoryRes = mainService.findByCategory(item.getCategory());
            newsCount = findByCategoryRes.size();
            item.setNewsCount(newsCount);
            mainService.categoryEditNewsCount(item);
            findSubCategoryByCategoryRes = mainService.findSubCategoryByCategory(item.getCategory());
            for (SubCategory item02 : findSubCategoryByCategoryRes) {
                findByCategoryAndSubCategoryRes = mainService.findByCategoryAndSubCategory(item02.getCategory(), item02.getSubCategory());
                newsCount = findByCategoryAndSubCategoryRes.size();
                item02.setSubCategoryNewsCount(newsCount);
                mainService.subCategoryEditNewsCount(item02);
            }
        }
        
        return "sub_category_home";
    }

    //  各ページの件数を50に設定    
    @RequestMapping(value = "/sub_category_home_set_pageSize_50")
    public String SubCategoryHomeSetPageSize50(Model model) {
        
        pageSize = 50;

        int pageNum = 0;
        
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);
        
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        //      全てのカテゴリーを検索
        List<Category> res02 = categoryDao.findAll();
        List<String> categoryList = new ArrayList<>();
        for (Category item : res02) {
            categoryList.add(item.getCategory());
        }
        model.addAttribute("categoryList", categoryList);
        SubCategory subCategory = new SubCategory();
        model.addAttribute("subCategory", subCategory);

        Page<SubCategory> subCategoryPage = null;
        //      サブカテゴリーを検索
        if (subCategoryLastSearch == 0) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        if (subCategoryLastSearch == 1) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        SubCategoryListTemp = subCategoryPage.getContent();
        model.addAttribute("subCategoryPage", subCategoryPage);
        model.addAttribute("subCategoryPageSize", subCategoryPage.getNumberOfElements());
        model.addAttribute("subCategoryPage", subCategoryPage);

        List<Category> findCategoryByAllRes = null;
        List<News> findByCategoryRes = null;
        List<News> findByCategoryAndSubCategoryRes = null;
        List<SubCategory> findSubCategoryByCategoryRes = null;
        int newsCount = 0;
        //      カテゴリーとサブカテゴリーのニュース数を更新
        findCategoryByAllRes = mainService.findCategoryByAll();
        for (Category item : findCategoryByAllRes) {
            findByCategoryRes = mainService.findByCategory(item.getCategory());
            newsCount = findByCategoryRes.size();
            item.setNewsCount(newsCount);
            mainService.categoryEditNewsCount(item);
            findSubCategoryByCategoryRes = mainService.findSubCategoryByCategory(item.getCategory());
            for (SubCategory item02 : findSubCategoryByCategoryRes) {
                findByCategoryAndSubCategoryRes = mainService.findByCategoryAndSubCategory(item02.getCategory(), item02.getSubCategory());
                newsCount = findByCategoryAndSubCategoryRes.size();
                item02.setSubCategoryNewsCount(newsCount);
                mainService.subCategoryEditNewsCount(item02);
            }
        }
        
        return "sub_category_home";

    }

    //  各ページの件数を100に設定
    @RequestMapping(value = "/sub_category_home_set_pageSize_100")
    public String SubCategoryHomeSetPageSize100(Model model) {
        
        pageSize = 100;

        int pageNum = 0;
        
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        //      全てのカテゴリーを検索
        List<Category> res02 = categoryDao.findAll();
        List<String> categoryList = new ArrayList<>();
        for (Category item : res02) {
            categoryList.add(item.getCategory());
        }
        model.addAttribute("categoryList", categoryList);
        SubCategory subCategory = new SubCategory();
        model.addAttribute("subCategory", subCategory);

        Page<SubCategory> subCategoryPage = null;
        //      サブカテゴリーを検索
        if (subCategoryLastSearch == 0) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        if (subCategoryLastSearch == 1) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        SubCategoryListTemp = subCategoryPage.getContent();
        model.addAttribute("subCategoryPage", subCategoryPage);
        model.addAttribute("subCategoryPageSize", subCategoryPage.getNumberOfElements());
        model.addAttribute("subCategoryPage", subCategoryPage);

        List<Category> findCategoryByAllRes = null;
        List<News> findByCategoryRes = null;
        List<News> findByCategoryAndSubCategoryRes = null;
        List<SubCategory> findSubCategoryByCategoryRes = null;
        int newsCount = 0;
        //      カテゴリーとサブカテゴリーのニュース数を更新
        findCategoryByAllRes = mainService.findCategoryByAll();
        for (Category item : findCategoryByAllRes) {
            findByCategoryRes = mainService.findByCategory(item.getCategory());
            newsCount = findByCategoryRes.size();
            item.setNewsCount(newsCount);
            mainService.categoryEditNewsCount(item);
            findSubCategoryByCategoryRes = mainService.findSubCategoryByCategory(item.getCategory());
            for (SubCategory item02 : findSubCategoryByCategoryRes) {
                findByCategoryAndSubCategoryRes = mainService.findByCategoryAndSubCategory(item02.getCategory(), item02.getSubCategory());
                newsCount = findByCategoryAndSubCategoryRes.size();
                item02.setSubCategoryNewsCount(newsCount);
                mainService.subCategoryEditNewsCount(item02);
            }
        }

        return "sub_category_home";
    }

    //  検索結果を昇順に設定
    @RequestMapping(value = "/home_set_asc")
    public String HomeSetAsc(Model model) {
        
        sortDescFlag = false;

        //        カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //        サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //        ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //        ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //        発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //        発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //      　複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        //        ニュースの検索キーワードを更新
        if (lastSearch == 1)
            news01.setCategory(lastKeyWordStr);
        if (lastSearch == 2)
            news02.setSubCategory(lastKeyWordStr);
        if (lastSearch == 3)
            news03.setNewsTitle(lastKeyWordStr);
        if (lastSearch == 4)
            news04.setNewsSubTitle(lastKeyWordStr);
        if (lastSearch == 5)
            news05.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 6)
            news06.setReleaseTime(lastKeyWordStr);

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //        チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //      今のページをゼロに設定
        int pageNum = 0;
        
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        return "home";
    }

    //  検索結果を降順に設定    
    @RequestMapping(value = "/home_set_desc")
    public String HomeSetDesc(Model model) {
        
        sortDescFlag = true;

        //        カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //        サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //        ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //        ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //        発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //        発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //        複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        //        ニュースの検索キーワードを更新
        if (lastSearch == 1)
            news01.setCategory(lastKeyWordStr);
        if (lastSearch == 2)
            news02.setSubCategory(lastKeyWordStr);
        if (lastSearch == 3)
            news03.setNewsTitle(lastKeyWordStr);
        if (lastSearch == 4)
            news04.setNewsSubTitle(lastKeyWordStr);
        if (lastSearch == 5)
            news05.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 6)
            news06.setReleaseTime(lastKeyWordStr);

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //        チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //      今のページをゼロに設定
        int pageNum = 0;

        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        return "home";
    }

    //  検索結果を昇順に設定
    @RequestMapping(value = "/category_home_set_asc")
    public String CategoryHomeSetAsc(Model model) {
        
        sortDescFlag = false;

        int pageNum = 0;
        
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);
        
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        
        Page<Category> categoryPage = null;
        //      全てのカテゴリーを検索
        categoryPage = mainService.findCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        CategoryListTemp = categoryPage.getContent();

        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("categoryPageSize", categoryPage.getNumberOfElements());

        List<Category> findCategoryByAllRes = null;
        List<News> findByCategoryRes = null;
        List<News> findByCategoryAndSubCategoryRes = null;
        List<SubCategory> findSubCategoryByCategoryRes = null;
        int newsCount = 0;

        //      カテゴリーとサブカテゴリーのニュース数を更新
        findCategoryByAllRes = mainService.findCategoryByAll();
        for (Category item : findCategoryByAllRes) {
            findByCategoryRes = mainService.findByCategory(item.getCategory());
            newsCount = findByCategoryRes.size();
            item.setNewsCount(newsCount);
            mainService.categoryEditNewsCount(item);
            findSubCategoryByCategoryRes = mainService.findSubCategoryByCategory(item.getCategory());
            for (SubCategory item02 : findSubCategoryByCategoryRes) {
                findByCategoryAndSubCategoryRes = mainService.findByCategoryAndSubCategory(item02.getCategory(), item02.getSubCategory());
                newsCount = findByCategoryAndSubCategoryRes.size();
                item02.setSubCategoryNewsCount(newsCount);
                mainService.subCategoryEditNewsCount(item02);
            }
        }

        return "category_home";
    }

    //  検索結果を降順に設定    
    @RequestMapping(value = "/category_home_set_desc")
    public String CategoryHomeSetDesc(Model model) {
        
        sortDescFlag = true;

        int pageNum = 0;

        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);
        
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        
        Page<Category> categoryPage = null;
        //      全てのカテゴリーを検索
        categoryPage = mainService.findCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        CategoryListTemp = categoryPage.getContent();
        
        model.addAttribute("categoryPage", categoryPage);
        model.addAttribute("categoryPageSize", categoryPage.getNumberOfElements());

        List<Category> findCategoryByAllRes = null;
        List<News> findByCategoryRes = null;
        List<News> findByCategoryAndSubCategoryRes = null;
        List<SubCategory> findSubCategoryByCategoryRes = null;
        int newsCount = 0;

        //      カテゴリーとサブカテゴリーのニュース数を更新
        findCategoryByAllRes = mainService.findCategoryByAll();
        for (Category item : findCategoryByAllRes) {
            findByCategoryRes = mainService.findByCategory(item.getCategory());
            newsCount = findByCategoryRes.size();
            item.setNewsCount(newsCount);
            mainService.categoryEditNewsCount(item);
            findSubCategoryByCategoryRes = mainService.findSubCategoryByCategory(item.getCategory());
            for (SubCategory item02 : findSubCategoryByCategoryRes) {
                findByCategoryAndSubCategoryRes = mainService.findByCategoryAndSubCategory(item02.getCategory(), item02.getSubCategory());
                newsCount = findByCategoryAndSubCategoryRes.size();
                item02.setSubCategoryNewsCount(newsCount);
                mainService.subCategoryEditNewsCount(item02);
            }
        }
        
        return "category_home";
    }

    //  検索結果を昇順に設定
    @RequestMapping(value = "/sub_category_home_set_asc")
    public String SubCategoryHomeSetAsc(Model model) {
        
        sortDescFlag = false;

        int pageNum = 0;

        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);
        
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        //      全てのカテゴリーを検索
        List<Category> res02 = categoryDao.findAll();
        List<String> categoryList = new ArrayList<>();
        for (Category item : res02) {
            categoryList.add(item.getCategory());
        }
        model.addAttribute("categoryList", categoryList);
        SubCategory subCategory = new SubCategory();
        model.addAttribute("subCategory", subCategory);
        
        Page<SubCategory> subCategoryPage = null;
        //      サブカテゴリーを検索
        if (subCategoryLastSearch == 0) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        if (subCategoryLastSearch == 1) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        SubCategoryListTemp = subCategoryPage.getContent();
        model.addAttribute("subCategoryPage", subCategoryPage);
        model.addAttribute("subCategoryPageSize", subCategoryPage.getNumberOfElements());
        model.addAttribute("subCategoryPage", subCategoryPage);

        List<Category> findCategoryByAllRes = null;
        List<News> findByCategoryRes = null;
        List<News> findByCategoryAndSubCategoryRes = null;
        List<SubCategory> findSubCategoryByCategoryRes = null;
        int newsCount = 0;

        //      カテゴリーとサブカテゴリーのニュース数を更新
        findCategoryByAllRes = mainService.findCategoryByAll();
        for (Category item : findCategoryByAllRes) {
            findByCategoryRes = mainService.findByCategory(item.getCategory());
            newsCount = findByCategoryRes.size();
            item.setNewsCount(newsCount);
            mainService.categoryEditNewsCount(item);
            findSubCategoryByCategoryRes = mainService.findSubCategoryByCategory(item.getCategory());
            for (SubCategory item02 : findSubCategoryByCategoryRes) {
                findByCategoryAndSubCategoryRes = mainService.findByCategoryAndSubCategory(item02.getCategory(), item02.getSubCategory());
                newsCount = findByCategoryAndSubCategoryRes.size();
                item02.setSubCategoryNewsCount(newsCount);
                mainService.subCategoryEditNewsCount(item02);
            }
        }
        
        return "sub_category_home";
    }

    //  検索結果を降順に設定
    @RequestMapping(value = "/sub_category_home_set_desc")
    public String SubCategoryHomeSetDesc(Model model) {
        
        sortDescFlag = true;

        int pageNum = 0;
        
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);
        
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        //      全てのカテゴリーを検索
        List<Category> res02 = categoryDao.findAll();
        List<String> categoryList = new ArrayList<>();
        for (Category item : res02) {
            categoryList.add(item.getCategory());
        }
        model.addAttribute("categoryList", categoryList);
        SubCategory subCategory = new SubCategory();
        model.addAttribute("subCategory", subCategory);
        
        Page<SubCategory> subCategoryPage = null;
        //      サブカテゴリーを検索
        if (subCategoryLastSearch == 0) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        if (subCategoryLastSearch == 1) {
            subCategoryPage = mainService.findSubCategoryPageByAll(sortDescFlag, pageNum, pageSize);
        }
        SubCategoryListTemp = subCategoryPage.getContent();
        model.addAttribute("subCategoryPage", subCategoryPage);
        model.addAttribute("subCategoryPageSize", subCategoryPage.getNumberOfElements());
        model.addAttribute("subCategoryPage", subCategoryPage);

        List<Category> findCategoryByAllRes = null;
        List<News> findByCategoryRes = null;
        List<News> findByCategoryAndSubCategoryRes = null;
        List<SubCategory> findSubCategoryByCategoryRes = null;
        int newsCount = 0;

        //      カテゴリーとサブカテゴリーのニュース数を更新
        findCategoryByAllRes = mainService.findCategoryByAll();
        for (Category item : findCategoryByAllRes) {
            findByCategoryRes = mainService.findByCategory(item.getCategory());
            newsCount = findByCategoryRes.size();
            item.setNewsCount(newsCount);
            mainService.categoryEditNewsCount(item);
            findSubCategoryByCategoryRes = mainService.findSubCategoryByCategory(item.getCategory());
            for (SubCategory item02 : findSubCategoryByCategoryRes) {
                findByCategoryAndSubCategoryRes = mainService.findByCategoryAndSubCategory(item02.getCategory(), item02.getSubCategory());
                newsCount = findByCategoryAndSubCategoryRes.size();
                item02.setSubCategoryNewsCount(newsCount);
                mainService.subCategoryEditNewsCount(item02);
            }
        }
        
        return "sub_category_home";
    }

    //////////////////////////////////////////////////

    //  カテゴリーリストの初期化
    private List<String> categoryListInitializer() {

        //        全てのカテゴリーを検索
        List<Category> res02 = categoryDao.findAll();
        
        List<String> categoryList = new ArrayList<>();
        for (Category item : res02) {
            categoryList.add(item.getCategory());
        }
        return categoryList;
    }

    //  サブカテゴリーリストの初期化
    private List<String> subCategoryListInitializer() {
    
        //      全てのカテゴリーを検索
        List<SubCategory> res01 = subCateogryDao.findByCategory(newsAddCategorySelect);

        List<String> subCategoryList = new ArrayList<>();
        for (SubCategory item : res01) {
            subCategoryList.add(item.getSubCategory());
        }

        return subCategoryList;
    }

    //  ニュース検索
    private Page<News> NewsSearch(int pageNum, int pageSize) {

        Page<News> newsPage = null;
        //        ニュース検索
        if (lastSearch == 0)
            newsPage = mainService.findPageByCategory(sortDescFlag, pageNum, pageSize, "分類1");
        if (lastSearch == 1)
            newsPage = mainService.findPageByCategory(sortDescFlag, pageNum, pageSize, lastKeyWordStr);
        if (lastSearch == 2)
            newsPage = mainService.findPageBySubCategory(sortDescFlag, pageNum, pageSize, lastKeyWordStr);
        if (lastSearch == 3)
            newsPage = mainService.findPageByNewsTitle(sortDescFlag, pageNum, pageSize, lastKeyWordStr);
        if (lastSearch == 4)
            newsPage = mainService.findPageByNewsSubTitle(sortDescFlag, pageNum, pageSize, lastKeyWordStr);
        if (lastSearch == 5)
            newsPage = mainService.findPageByReleaseTimeGreater(sortDescFlag, pageNum, pageSize, lastKeyWordStr);
        if (lastSearch == 6)
            newsPage = mainService.findPageByReleaseTimeLess(sortDescFlag, pageNum, pageSize, lastKeyWordStr);
        if (lastSearch == 7)
            newsPage = mainService.findPageByNewsByInput(sortDescFlag, pageNum, pageSize, lastKeyWordMultipleStr.getCategory(), lastKeyWordMultipleStr.getSubCategory(),
                    lastKeyWordMultipleStr.getNewsTitle(), lastKeyWordMultipleStr.getNewsSubTitle(), lastKeyWordMultipleStr.getReleaseTimeStart(),
                    lastKeyWordMultipleStr.getReleaseTimeEnd(), lastKeyWordMultipleStr.getBuildTimeStart(), lastKeyWordMultipleStr.getBuildTimeEnd());
        if (lastSearch == 8)
            newsPage = mainService.findPageAll(sortDescFlag, pageNum, pageSize);
        
        NewsListTemp = newsPage.getContent();

        return newsPage;
    }

    //  チェックしたニュースを獲得
    private News NewsListTempReader() {

        int counter0 = 0;
        int counter = 0;
        if ((checkedResTemp.isChecked1() && pageNumTemp == 0)
            || (checkedResTemp.isChecked1() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked1() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 1;
        if ((checkedResTemp.isChecked2() && pageNumTemp == 0)
            || (checkedResTemp.isChecked2() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked2() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 2;
        if ((checkedResTemp.isChecked3() && pageNumTemp == 0)
            || (checkedResTemp.isChecked3() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked3() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 3;
        if ((checkedResTemp.isChecked4() && pageNumTemp == 0)
            || (checkedResTemp.isChecked4() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked4() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 4;
        if ((checkedResTemp.isChecked5() && pageNumTemp == 0)
            || (checkedResTemp.isChecked5() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked5() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 5;
        if ((checkedResTemp.isChecked6() && pageNumTemp == 0)
            || (checkedResTemp.isChecked6() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked6() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 6;
        if ((checkedResTemp.isChecked7() && pageNumTemp == 0)
            || (checkedResTemp.isChecked7() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked7() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 7;
        if ((checkedResTemp.isChecked8() && pageNumTemp == 0)
            || (checkedResTemp.isChecked8() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked8() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 8;
        if ((checkedResTemp.isChecked9() && pageNumTemp == 0)
            || (checkedResTemp.isChecked9() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked9() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 9;
        if ((checkedResTemp.isChecked10() && pageNumTemp == 0)
            || (checkedResTemp.isChecked10() && pageSize == 10 && pageNumTemp != 0)
            || (checkedResTemp.isChecked10() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 10;
        if ((checkedResTemp.isChecked11() && pageNumTemp == 0)
            || (checkedResTemp.isChecked11() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 11;
        if ((checkedResTemp.isChecked12() && pageNumTemp == 0)
            || (checkedResTemp.isChecked12() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 12;
        if ((checkedResTemp.isChecked13() && pageNumTemp == 0)
            || (checkedResTemp.isChecked13() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 13;
        if ((checkedResTemp.isChecked14() && pageNumTemp == 0)
            || (checkedResTemp.isChecked14() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 14;
        if ((checkedResTemp.isChecked15() && pageNumTemp == 0)
            || (checkedResTemp.isChecked15() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 15;
        if ((checkedResTemp.isChecked16() && pageNumTemp == 0)
            || (checkedResTemp.isChecked16() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 16;
        if ((checkedResTemp.isChecked17() && pageNumTemp == 0)
            || (checkedResTemp.isChecked17() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 17;
        if ((checkedResTemp.isChecked18() && pageNumTemp == 0)
            || (checkedResTemp.isChecked18() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 18;
        if ((checkedResTemp.isChecked19() && pageNumTemp == 0)
            || (checkedResTemp.isChecked19() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 19;
        if ((checkedResTemp.isChecked20() && pageNumTemp == 0)
            || (checkedResTemp.isChecked20() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 20;
        if ((checkedResTemp.isChecked21() && pageNumTemp == 0)
            || (checkedResTemp.isChecked21() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 21;
        if ((checkedResTemp.isChecked22() && pageNumTemp == 0)
            || (checkedResTemp.isChecked22() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 22;
        if ((checkedResTemp.isChecked23() && pageNumTemp == 0)
            || (checkedResTemp.isChecked23() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 23;
        if ((checkedResTemp.isChecked24() && pageNumTemp == 0)
            || (checkedResTemp.isChecked24() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 24;
        if ((checkedResTemp.isChecked25() && pageNumTemp == 0)
            || (checkedResTemp.isChecked25() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 25;
        if ((checkedResTemp.isChecked26() && pageNumTemp == 0)
            || (checkedResTemp.isChecked26() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 26;
        if ((checkedResTemp.isChecked27() && pageNumTemp == 0)
            || (checkedResTemp.isChecked27() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 27;
        if ((checkedResTemp.isChecked28() && pageNumTemp == 0)
            || (checkedResTemp.isChecked28() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 28;
        if ((checkedResTemp.isChecked29() && pageNumTemp == 0)
            || (checkedResTemp.isChecked29() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 29;
        if ((checkedResTemp.isChecked30() && pageNumTemp == 0)
            || (checkedResTemp.isChecked30() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 30;
        if ((checkedResTemp.isChecked31() && pageNumTemp == 0)
            || (checkedResTemp.isChecked31() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 31;
        if ((checkedResTemp.isChecked32() && pageNumTemp == 0)
            || (checkedResTemp.isChecked32() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 32;
        if ((checkedResTemp.isChecked33() && pageNumTemp == 0)
            || (checkedResTemp.isChecked33() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 33;
        if ((checkedResTemp.isChecked34() && pageNumTemp == 0)
            || (checkedResTemp.isChecked34() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 34;
        if ((checkedResTemp.isChecked35() && pageNumTemp == 0)
            || (checkedResTemp.isChecked35() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 35;
        if ((checkedResTemp.isChecked36() && pageNumTemp == 0)
            || (checkedResTemp.isChecked36() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 36;
        if ((checkedResTemp.isChecked37() && pageNumTemp == 0)
            || (checkedResTemp.isChecked37() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 37;
        if ((checkedResTemp.isChecked38() && pageNumTemp == 0)
            || (checkedResTemp.isChecked38() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 38;
        if ((checkedResTemp.isChecked39() && pageNumTemp == 0)
            || (checkedResTemp.isChecked39() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 39;
        if ((checkedResTemp.isChecked40() && pageNumTemp == 0)
            || (checkedResTemp.isChecked40() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 40;
        if ((checkedResTemp.isChecked41() && pageNumTemp == 0)
            || (checkedResTemp.isChecked41() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 41;
        if ((checkedResTemp.isChecked42() && pageNumTemp == 0)
            || (checkedResTemp.isChecked42() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 42;
        if ((checkedResTemp.isChecked43() && pageNumTemp == 0)
            || (checkedResTemp.isChecked43() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 43;
        if ((checkedResTemp.isChecked44() && pageNumTemp == 0)
            || (checkedResTemp.isChecked44() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 44;
        if ((checkedResTemp.isChecked45() && pageNumTemp == 0)
            || (checkedResTemp.isChecked45() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 45;
        if ((checkedResTemp.isChecked46() && pageNumTemp == 0)
            || (checkedResTemp.isChecked46() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 46;
        if ((checkedResTemp.isChecked47() && pageNumTemp == 0)
            || (checkedResTemp.isChecked47() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 47;
        if ((checkedResTemp.isChecked48() && pageNumTemp == 0)
            || (checkedResTemp.isChecked48() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 48;
        if ((checkedResTemp.isChecked49() && pageNumTemp == 0)
            || (checkedResTemp.isChecked49() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 49;
        if ((checkedResTemp.isChecked50() && pageNumTemp == 0)
            || (checkedResTemp.isChecked50() && pageSize == 50 && pageNumTemp != 0))
            counter0 = 50;
        if (checkedResTemp.isChecked51() && pageNumTemp == 0)
            counter0 = 51;
        if (checkedResTemp.isChecked52() && pageNumTemp == 0)
            counter0 = 52;
        if (checkedResTemp.isChecked53() && pageNumTemp == 0)
            counter0 = 53;
        if (checkedResTemp.isChecked54() && pageNumTemp == 0)
            counter0 = 54;
        if (checkedResTemp.isChecked55() && pageNumTemp == 0)
            counter0 = 55;
        if (checkedResTemp.isChecked56() && pageNumTemp == 0)
            counter0 = 56;
        if (checkedResTemp.isChecked57() && pageNumTemp == 0)
            counter0 = 57;
        if (checkedResTemp.isChecked58() && pageNumTemp == 0)
            counter0 = 58;
        if (checkedResTemp.isChecked59() && pageNumTemp == 0)
            counter0 = 59;
        if (checkedResTemp.isChecked60() && pageNumTemp == 0)
            counter0 = 60;
        if (checkedResTemp.isChecked61() && pageNumTemp == 0)
            counter0 = 61;
        if (checkedResTemp.isChecked62() && pageNumTemp == 0)
            counter0 = 62;
        if (checkedResTemp.isChecked63() && pageNumTemp == 0)
            counter0 = 63;
        if (checkedResTemp.isChecked64() && pageNumTemp == 0)
            counter0 = 64;
        if (checkedResTemp.isChecked65() && pageNumTemp == 0)
            counter0 = 65;
        if (checkedResTemp.isChecked66() && pageNumTemp == 0)
            counter0 = 66;
        if (checkedResTemp.isChecked67() && pageNumTemp == 0)
            counter0 = 67;
        if (checkedResTemp.isChecked68() && pageNumTemp == 0)
            counter0 = 68;
        if (checkedResTemp.isChecked69() && pageNumTemp == 0)
            counter0 = 69;
        if (checkedResTemp.isChecked70() && pageNumTemp == 0)
            counter0 = 70;
        if (checkedResTemp.isChecked71() && pageNumTemp == 0)
            counter0 = 71;
        if (checkedResTemp.isChecked72() && pageNumTemp == 0)
            counter0 = 72;
        if (checkedResTemp.isChecked73() && pageNumTemp == 0)
            counter0 = 73;
        if (checkedResTemp.isChecked74() && pageNumTemp == 0)
            counter0 = 74;
        if (checkedResTemp.isChecked75() && pageNumTemp == 0)
            counter0 = 75;
        if (checkedResTemp.isChecked76() && pageNumTemp == 0)
            counter0 = 76;
        if (checkedResTemp.isChecked77() && pageNumTemp == 0)
            counter0 = 77;
        if (checkedResTemp.isChecked78() && pageNumTemp == 0)
            counter0 = 78;
        if (checkedResTemp.isChecked79() && pageNumTemp == 0)
            counter0 = 79;
        if (checkedResTemp.isChecked80() && pageNumTemp == 0)
            counter0 = 80;
        if (checkedResTemp.isChecked81() && pageNumTemp == 0)
            counter0 = 81;
        if (checkedResTemp.isChecked82() && pageNumTemp == 0)
            counter0 = 82;
        if (checkedResTemp.isChecked83() && pageNumTemp == 0)
            counter0 = 83;
        if (checkedResTemp.isChecked84() && pageNumTemp == 0)
            counter0 = 84;
        if (checkedResTemp.isChecked85() && pageNumTemp == 0)
            counter0 = 85;
        if (checkedResTemp.isChecked86() && pageNumTemp == 0)
            counter0 = 86;
        if (checkedResTemp.isChecked87() && pageNumTemp == 0)
            counter0 = 87;
        if (checkedResTemp.isChecked88() && pageNumTemp == 0)
            counter0 = 88;
        if (checkedResTemp.isChecked89() && pageNumTemp == 0)
            counter0 = 89;
        if (checkedResTemp.isChecked90() && pageNumTemp == 0)
            counter0 = 90;
        if (checkedResTemp.isChecked91() && pageNumTemp == 0)
            counter0 = 91;
        if (checkedResTemp.isChecked92() && pageNumTemp == 0)
            counter0 = 92;
        if (checkedResTemp.isChecked93() && pageNumTemp == 0)
            counter0 = 93;
        if (checkedResTemp.isChecked94() && pageNumTemp == 0)
            counter0 = 94;
        if (checkedResTemp.isChecked95() && pageNumTemp == 0)
            counter0 = 95;
        if (checkedResTemp.isChecked96() && pageNumTemp == 0)
            counter0 = 96;
        if (checkedResTemp.isChecked97() && pageNumTemp == 0)
            counter0 = 97;
        if (checkedResTemp.isChecked98() && pageNumTemp == 0)
            counter0 = 98;
        if (checkedResTemp.isChecked99() && pageNumTemp == 0)
            counter0 = 99;
        if (checkedResTemp.isChecked100() && pageNumTemp == 0)
            counter0 = 100;

        for (News item : NewsListTemp) {
            counter++;
            if (counter == counter0) {
                return item;
            }
        }

        return null;
    }

    //////////////////////////////////////////////////
    //  クライアント側のホームページ
    @RequestMapping(value = "/client_home/{pageNum}")
    public String ClientHome(@PathVariable(value = "pageNum", required = false) int pageNum, Model model) {
        //      pageNumがマイナスにならないように
        if (pageNum == -1)
            pageNum = 0;

        pageNumTemp = pageNum;

        //          検索機能を無効化
        lastSearch = 8;
        lastKeyWordStr = "";

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //          チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        List<Category> findCategoryByAllRes = null;
        List<News> findByCategoryRes = null;
        List<News> findByCategoryAndSubCategoryRes = null;
        List<SubCategory> findSubCategoryByCategoryRes = null;
        int newsCount = 0;

        //      カテゴリーとサブカテゴリーのニュース数を更新
        findCategoryByAllRes = mainService.findCategoryByAll();
        for (Category item : findCategoryByAllRes) {
            findByCategoryRes = mainService.findByCategory(item.getCategory());
            newsCount = findByCategoryRes.size();
            item.setNewsCount(newsCount);
            mainService.categoryEditNewsCount(item);
            findSubCategoryByCategoryRes = mainService.findSubCategoryByCategory(item.getCategory());
            for (SubCategory item02 : findSubCategoryByCategoryRes) {
                findByCategoryAndSubCategoryRes = mainService.findByCategoryAndSubCategory(item02.getCategory(), item02.getSubCategory());
                newsCount = findByCategoryAndSubCategoryRes.size();
                item02.setSubCategoryNewsCount(newsCount);
                mainService.subCategoryEditNewsCount(item02);
            }
        }

        return "client_home";
    }

    //  クライアント側のチェック結果を確認
    @PostMapping("/client_home")
    public String ClientHomeChecked(@ModelAttribute("checkedRes") CheckedRes checkedRes, Model model) {
        
        checkedResTemp = checkedRes;
        checkedResTempCount = 0;
        if (checkedRes.isChecked1())
            checkedResTempCount++;
        if (checkedRes.isChecked2())
            checkedResTempCount++;
        if (checkedRes.isChecked3())
            checkedResTempCount++;
        if (checkedRes.isChecked4())
            checkedResTempCount++;
        if (checkedRes.isChecked5())
            checkedResTempCount++;
        if (checkedRes.isChecked6())
            checkedResTempCount++;
        if (checkedRes.isChecked7())
            checkedResTempCount++;
        if (checkedRes.isChecked8())
            checkedResTempCount++;
        if (checkedRes.isChecked9())
            checkedResTempCount++;
        if (checkedRes.isChecked10())
            checkedResTempCount++;
        if (checkedRes.isChecked11())
            checkedResTempCount++;
        if (checkedRes.isChecked12())
            checkedResTempCount++;
        if (checkedRes.isChecked13())
            checkedResTempCount++;
        if (checkedRes.isChecked14())
            checkedResTempCount++;
        if (checkedRes.isChecked15())
            checkedResTempCount++;
        if (checkedRes.isChecked16())
            checkedResTempCount++;
        if (checkedRes.isChecked17())
            checkedResTempCount++;
        if (checkedRes.isChecked18())
            checkedResTempCount++;
        if (checkedRes.isChecked19())
            checkedResTempCount++;
        if (checkedRes.isChecked20())
            checkedResTempCount++;
        if (checkedRes.isChecked21())
            checkedResTempCount++;
        if (checkedRes.isChecked22())
            checkedResTempCount++;
        if (checkedRes.isChecked23())
            checkedResTempCount++;
        if (checkedRes.isChecked24())
            checkedResTempCount++;
        if (checkedRes.isChecked25())
            checkedResTempCount++;
        if (checkedRes.isChecked26())
            checkedResTempCount++;
        if (checkedRes.isChecked27())
            checkedResTempCount++;
        if (checkedRes.isChecked28())
            checkedResTempCount++;
        if (checkedRes.isChecked29())
            checkedResTempCount++;
        if (checkedRes.isChecked30())
            checkedResTempCount++;
        if (checkedRes.isChecked31())
            checkedResTempCount++;
        if (checkedRes.isChecked32())
            checkedResTempCount++;
        if (checkedRes.isChecked33())
            checkedResTempCount++;
        if (checkedRes.isChecked34())
            checkedResTempCount++;
        if (checkedRes.isChecked35())
            checkedResTempCount++;
        if (checkedRes.isChecked36())
            checkedResTempCount++;
        if (checkedRes.isChecked37())
            checkedResTempCount++;
        if (checkedRes.isChecked38())
            checkedResTempCount++;
        if (checkedRes.isChecked39())
            checkedResTempCount++;
        if (checkedRes.isChecked40())
            checkedResTempCount++;
        if (checkedRes.isChecked41())
            checkedResTempCount++;
        if (checkedRes.isChecked42())
            checkedResTempCount++;
        if (checkedRes.isChecked43())
            checkedResTempCount++;
        if (checkedRes.isChecked44())
            checkedResTempCount++;
        if (checkedRes.isChecked45())
            checkedResTempCount++;
        if (checkedRes.isChecked46())
            checkedResTempCount++;
        if (checkedRes.isChecked47())
            checkedResTempCount++;
        if (checkedRes.isChecked48())
            checkedResTempCount++;
        if (checkedRes.isChecked49())
            checkedResTempCount++;
        if (checkedRes.isChecked50())
            checkedResTempCount++;
        if (checkedRes.isChecked51())
            checkedResTempCount++;
        if (checkedRes.isChecked52())
            checkedResTempCount++;
        if (checkedRes.isChecked53())
            checkedResTempCount++;
        if (checkedRes.isChecked54())
            checkedResTempCount++;
        if (checkedRes.isChecked55())
            checkedResTempCount++;
        if (checkedRes.isChecked56())
            checkedResTempCount++;
        if (checkedRes.isChecked57())
            checkedResTempCount++;
        if (checkedRes.isChecked58())
            checkedResTempCount++;
        if (checkedRes.isChecked59())
            checkedResTempCount++;
        if (checkedRes.isChecked60())
            checkedResTempCount++;
        if (checkedRes.isChecked61())
            checkedResTempCount++;
        if (checkedRes.isChecked62())
            checkedResTempCount++;
        if (checkedRes.isChecked63())
            checkedResTempCount++;
        if (checkedRes.isChecked64())
            checkedResTempCount++;
        if (checkedRes.isChecked65())
            checkedResTempCount++;
        if (checkedRes.isChecked66())
            checkedResTempCount++;
        if (checkedRes.isChecked67())
            checkedResTempCount++;
        if (checkedRes.isChecked68())
            checkedResTempCount++;
        if (checkedRes.isChecked69())
            checkedResTempCount++;
        if (checkedRes.isChecked70())
            checkedResTempCount++;
        if (checkedRes.isChecked71())
            checkedResTempCount++;
        if (checkedRes.isChecked72())
            checkedResTempCount++;
        if (checkedRes.isChecked73())
            checkedResTempCount++;
        if (checkedRes.isChecked74())
            checkedResTempCount++;
        if (checkedRes.isChecked75())
            checkedResTempCount++;
        if (checkedRes.isChecked76())
            checkedResTempCount++;
        if (checkedRes.isChecked77())
            checkedResTempCount++;
        if (checkedRes.isChecked78())
            checkedResTempCount++;
        if (checkedRes.isChecked79())
            checkedResTempCount++;
        if (checkedRes.isChecked80())
            checkedResTempCount++;
        if (checkedRes.isChecked81())
            checkedResTempCount++;
        if (checkedRes.isChecked82())
            checkedResTempCount++;
        if (checkedRes.isChecked83())
            checkedResTempCount++;
        if (checkedRes.isChecked84())
            checkedResTempCount++;
        if (checkedRes.isChecked85())
            checkedResTempCount++;
        if (checkedRes.isChecked86())
            checkedResTempCount++;
        if (checkedRes.isChecked87())
            checkedResTempCount++;
        if (checkedRes.isChecked88())
            checkedResTempCount++;
        if (checkedRes.isChecked89())
            checkedResTempCount++;
        if (checkedRes.isChecked90())
            checkedResTempCount++;
        if (checkedRes.isChecked91())
            checkedResTempCount++;
        if (checkedRes.isChecked92())
            checkedResTempCount++;
        if (checkedRes.isChecked93())
            checkedResTempCount++;
        if (checkedRes.isChecked94())
            checkedResTempCount++;
        if (checkedRes.isChecked95())
            checkedResTempCount++;
        if (checkedRes.isChecked96())
            checkedResTempCount++;
        if (checkedRes.isChecked97())
            checkedResTempCount++;
        if (checkedRes.isChecked98())
            checkedResTempCount++;
        if (checkedRes.isChecked99())
            checkedResTempCount++;
        if (checkedRes.isChecked100())
            checkedResTempCount++;

        //    カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //    サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //    ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //    ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //    発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //    発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //    複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        //    ニュースの検索キーワードを更新
        if (lastSearch == 1)
            news01.setCategory(lastKeyWordStr);
        if (lastSearch == 2)
            news02.setSubCategory(lastKeyWordStr);
        if (lastSearch == 3)
            news03.setNewsTitle(lastKeyWordStr);
        if (lastSearch == 4)
            news04.setNewsSubTitle(lastKeyWordStr);
        if (lastSearch == 5)
            news05.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 6)
            news06.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 7)
            news07 = lastKeyWordMultipleStr;

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //    チェック用
        checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        int pageNum = pageNumTemp;
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        return "client_home";
    }

    //  クライアント側の検索結果を全てチェック
    @RequestMapping("/client_all_check")
    public String ClientHomeAllChecked(Model model) {
        
        CheckedRes checkedRes = new CheckedRes();
        checkedRes.setChecked1(true);
        checkedRes.setChecked2(true);
        checkedRes.setChecked3(true);
        checkedRes.setChecked4(true);
        checkedRes.setChecked5(true);
        checkedRes.setChecked6(true);
        checkedRes.setChecked7(true);
        checkedRes.setChecked8(true);
        checkedRes.setChecked9(true);
        checkedRes.setChecked10(true);
        checkedRes.setChecked11(true);
        checkedRes.setChecked12(true);
        checkedRes.setChecked13(true);
        checkedRes.setChecked14(true);
        checkedRes.setChecked15(true);
        checkedRes.setChecked16(true);
        checkedRes.setChecked17(true);
        checkedRes.setChecked18(true);
        checkedRes.setChecked19(true);
        checkedRes.setChecked20(true);
        checkedRes.setChecked21(true);
        checkedRes.setChecked22(true);
        checkedRes.setChecked23(true);
        checkedRes.setChecked24(true);
        checkedRes.setChecked25(true);
        checkedRes.setChecked26(true);
        checkedRes.setChecked27(true);
        checkedRes.setChecked28(true);
        checkedRes.setChecked29(true);
        checkedRes.setChecked30(true);
        checkedRes.setChecked31(true);
        checkedRes.setChecked32(true);
        checkedRes.setChecked33(true);
        checkedRes.setChecked34(true);
        checkedRes.setChecked35(true);
        checkedRes.setChecked36(true);
        checkedRes.setChecked37(true);
        checkedRes.setChecked38(true);
        checkedRes.setChecked39(true);
        checkedRes.setChecked40(true);
        checkedRes.setChecked41(true);
        checkedRes.setChecked42(true);
        checkedRes.setChecked43(true);
        checkedRes.setChecked44(true);
        checkedRes.setChecked45(true);
        checkedRes.setChecked46(true);
        checkedRes.setChecked47(true);
        checkedRes.setChecked48(true);
        checkedRes.setChecked49(true);
        checkedRes.setChecked50(true);
        checkedRes.setChecked51(true);
        checkedRes.setChecked52(true);
        checkedRes.setChecked53(true);
        checkedRes.setChecked54(true);
        checkedRes.setChecked55(true);
        checkedRes.setChecked56(true);
        checkedRes.setChecked57(true);
        checkedRes.setChecked58(true);
        checkedRes.setChecked59(true);
        checkedRes.setChecked60(true);
        checkedRes.setChecked61(true);
        checkedRes.setChecked62(true);
        checkedRes.setChecked63(true);
        checkedRes.setChecked64(true);
        checkedRes.setChecked65(true);
        checkedRes.setChecked66(true);
        checkedRes.setChecked67(true);
        checkedRes.setChecked68(true);
        checkedRes.setChecked69(true);
        checkedRes.setChecked70(true);
        checkedRes.setChecked71(true);
        checkedRes.setChecked72(true);
        checkedRes.setChecked73(true);
        checkedRes.setChecked74(true);
        checkedRes.setChecked75(true);
        checkedRes.setChecked76(true);
        checkedRes.setChecked77(true);
        checkedRes.setChecked78(true);
        checkedRes.setChecked79(true);
        checkedRes.setChecked80(true);
        checkedRes.setChecked81(true);
        checkedRes.setChecked82(true);
        checkedRes.setChecked83(true);
        checkedRes.setChecked84(true);
        checkedRes.setChecked85(true);
        checkedRes.setChecked86(true);
        checkedRes.setChecked87(true);
        checkedRes.setChecked88(true);
        checkedRes.setChecked89(true);
        checkedRes.setChecked90(true);
        checkedRes.setChecked91(true);
        checkedRes.setChecked92(true);
        checkedRes.setChecked93(true);
        checkedRes.setChecked94(true);
        checkedRes.setChecked95(true);
        checkedRes.setChecked96(true);
        checkedRes.setChecked97(true);
        checkedRes.setChecked98(true);
        checkedRes.setChecked99(true);
        checkedRes.setChecked100(true);

        //    カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //    サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //    ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //    ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //    発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //    発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //    複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        //    ニュースの検索キーワードを更新
        if (lastSearch == 1)
            news01.setCategory(lastKeyWordStr);
        if (lastSearch == 2)
            news02.setSubCategory(lastKeyWordStr);
        if (lastSearch == 3)
            news03.setNewsTitle(lastKeyWordStr);
        if (lastSearch == 4)
            news04.setNewsSubTitle(lastKeyWordStr);
        if (lastSearch == 5)
            news05.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 6)
            news06.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 7)
            news07 = lastKeyWordMultipleStr;

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        model.addAttribute("checkedRes", checkedRes);

        int pageNum = pageNumTemp;

        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());

        return "client_home";
    }

    //  ニュース詳細
    @RequestMapping("/client_news_zoom")
    public String ClientNewsZoom(Model model) {

        //        カテゴリーでニュース検索用の変数を初期化する
        News news01 = new News();
        //        サブカテゴリーでニュース検索用の変数を初期化する
        News news02 = new News();
        //        ニュースタイルでニュース検索用の変数を初期化する
        News news03 = new News();
        //        ニュースのサブタイルでニュース検索用の変数を初期化する
        News news04 = new News();
        //        発表日以降でニュース検索用の変数を初期化する
        News news05 = new News();
        //        発表日以前でニュース検索用の変数を初期化する
        News news06 = new News();
        //        複数条件でニュース検索用の変数を初期化する
        MultipleSearch news07 = new MultipleSearch();

        //        ニュースの検索キーワードを更新
        if (lastSearch == 1)
            news01.setCategory(lastKeyWordStr);
        if (lastSearch == 2)
            news02.setSubCategory(lastKeyWordStr);
        if (lastSearch == 3)
            news03.setNewsTitle(lastKeyWordStr);
        if (lastSearch == 4)
            news04.setNewsSubTitle(lastKeyWordStr);
        if (lastSearch == 5)
            news05.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 6)
            news06.setReleaseTime(lastKeyWordStr);
        if (lastSearch == 7)
            news07 = lastKeyWordMultipleStr;

        model.addAttribute("news01", news01);
        model.addAttribute("news02", news02);
        model.addAttribute("news03", news03);
        model.addAttribute("news04", news04);
        model.addAttribute("news05", news05);
        model.addAttribute("news06", news06);
        model.addAttribute("news07", news07);

        model.addAttribute("categoryList", categoryListInitializer());

        model.addAttribute("subCategoryList", subCategoryListInitializer());

        //        チェック用
        CheckedRes checkedRes = new CheckedRes();
        model.addAttribute("checkedRes", checkedRes);

        //      今のページをゼロに設定
        int pageNum = 0;
        //        各ページの件数を10に設定
        int pageSize = 10;

        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);

        model.addAttribute("newsPage", NewsSearch(pageNum, pageSize));
        model.addAttribute("newsPageSize", NewsSearch(pageNum, pageSize).getNumberOfElements());
        
        //      チェックしてないを判断
        if (checkedResTemp == null)
            return "client_home";
        //      二つ以上チェックしたを判断
        if (checkedResTempCount >= 2)
            return "client_home";

        int counter0 = 0;
        int counter = 0;
        if (checkedResTemp.isChecked1())
            counter0 = 1;
        if (checkedResTemp.isChecked2())
            counter0 = 2;
        if (checkedResTemp.isChecked3())
            counter0 = 3;
        if (checkedResTemp.isChecked4())
            counter0 = 4;
        if (checkedResTemp.isChecked5())
            counter0 = 5;
        if (checkedResTemp.isChecked6())
            counter0 = 6;
        if (checkedResTemp.isChecked7())
            counter0 = 7;
        if (checkedResTemp.isChecked8())
            counter0 = 8;
        if (checkedResTemp.isChecked9())
            counter0 = 9;
        if (checkedResTemp.isChecked10())
            counter0 = 10;
        if (checkedResTemp.isChecked11())
            counter0 = 11;
        if (checkedResTemp.isChecked12())
            counter0 = 12;
        if (checkedResTemp.isChecked13())
            counter0 = 13;
        if (checkedResTemp.isChecked14())
            counter0 = 14;
        if (checkedResTemp.isChecked15())
            counter0 = 15;
        if (checkedResTemp.isChecked16())
            counter0 = 16;
        if (checkedResTemp.isChecked17())
            counter0 = 17;
        if (checkedResTemp.isChecked18())
            counter0 = 18;
        if (checkedResTemp.isChecked19())
            counter0 = 19;
        if (checkedResTemp.isChecked20())
            counter0 = 20;
        if (checkedResTemp.isChecked21())
            counter0 = 21;
        if (checkedResTemp.isChecked22())
            counter0 = 22;
        if (checkedResTemp.isChecked23())
            counter0 = 23;
        if (checkedResTemp.isChecked24())
            counter0 = 24;
        if (checkedResTemp.isChecked25())
            counter0 = 25;
        if (checkedResTemp.isChecked26())
            counter0 = 26;
        if (checkedResTemp.isChecked27())
            counter0 = 27;
        if (checkedResTemp.isChecked28())
            counter0 = 28;
        if (checkedResTemp.isChecked29())
            counter0 = 29;
        if (checkedResTemp.isChecked30())
            counter0 = 30;
        if (checkedResTemp.isChecked31())
            counter0 = 31;
        if (checkedResTemp.isChecked32())
            counter0 = 32;
        if (checkedResTemp.isChecked33())
            counter0 = 33;
        if (checkedResTemp.isChecked34())
            counter0 = 34;
        if (checkedResTemp.isChecked35())
            counter0 = 35;
        if (checkedResTemp.isChecked36())
            counter0 = 36;
        if (checkedResTemp.isChecked37())
            counter0 = 37;
        if (checkedResTemp.isChecked38())
            counter0 = 38;
        if (checkedResTemp.isChecked39())
            counter0 = 39;
        if (checkedResTemp.isChecked40())
            counter0 = 40;
        if (checkedResTemp.isChecked41())
            counter0 = 41;
        if (checkedResTemp.isChecked42())
            counter0 = 42;
        if (checkedResTemp.isChecked43())
            counter0 = 43;
        if (checkedResTemp.isChecked44())
            counter0 = 44;
        if (checkedResTemp.isChecked45())
            counter0 = 45;
        if (checkedResTemp.isChecked46())
            counter0 = 46;
        if (checkedResTemp.isChecked47())
            counter0 = 47;
        if (checkedResTemp.isChecked48())
            counter0 = 48;
        if (checkedResTemp.isChecked49())
            counter0 = 49;
        if (checkedResTemp.isChecked50())
            counter0 = 50;
        if (checkedResTemp.isChecked51())
            counter0 = 51;
        if (checkedResTemp.isChecked52())
            counter0 = 52;
        if (checkedResTemp.isChecked53())
            counter0 = 53;
        if (checkedResTemp.isChecked54())
            counter0 = 54;
        if (checkedResTemp.isChecked55())
            counter0 = 55;
        if (checkedResTemp.isChecked56())
            counter0 = 56;
        if (checkedResTemp.isChecked57())
            counter0 = 57;
        if (checkedResTemp.isChecked58())
            counter0 = 58;
        if (checkedResTemp.isChecked59())
            counter0 = 59;
        if (checkedResTemp.isChecked60())
            counter0 = 60;
        if (checkedResTemp.isChecked61())
            counter0 = 61;
        if (checkedResTemp.isChecked62())
            counter0 = 62;
        if (checkedResTemp.isChecked63())
            counter0 = 63;
        if (checkedResTemp.isChecked64())
            counter0 = 64;
        if (checkedResTemp.isChecked65())
            counter0 = 65;
        if (checkedResTemp.isChecked66())
            counter0 = 66;
        if (checkedResTemp.isChecked67())
            counter0 = 67;
        if (checkedResTemp.isChecked68())
            counter0 = 68;
        if (checkedResTemp.isChecked69())
            counter0 = 69;
        if (checkedResTemp.isChecked70())
            counter0 = 70;
        if (checkedResTemp.isChecked71())
            counter0 = 71;
        if (checkedResTemp.isChecked72())
            counter0 = 72;
        if (checkedResTemp.isChecked73())
            counter0 = 73;
        if (checkedResTemp.isChecked74())
            counter0 = 74;
        if (checkedResTemp.isChecked75())
            counter0 = 75;
        if (checkedResTemp.isChecked76())
            counter0 = 76;
        if (checkedResTemp.isChecked77())
            counter0 = 77;
        if (checkedResTemp.isChecked78())
            counter0 = 78;
        if (checkedResTemp.isChecked79())
            counter0 = 79;
        if (checkedResTemp.isChecked80())
            counter0 = 80;
        if (checkedResTemp.isChecked81())
            counter0 = 81;
        if (checkedResTemp.isChecked82())
            counter0 = 82;
        if (checkedResTemp.isChecked83())
            counter0 = 83;
        if (checkedResTemp.isChecked84())
            counter0 = 84;
        if (checkedResTemp.isChecked85())
            counter0 = 85;
        if (checkedResTemp.isChecked86())
            counter0 = 86;
        if (checkedResTemp.isChecked87())
            counter0 = 87;
        if (checkedResTemp.isChecked88())
            counter0 = 88;
        if (checkedResTemp.isChecked89())
            counter0 = 89;
        if (checkedResTemp.isChecked90())
            counter0 = 90;
        if (checkedResTemp.isChecked91())
            counter0 = 91;
        if (checkedResTemp.isChecked92())
            counter0 = 92;
        if (checkedResTemp.isChecked93())
            counter0 = 93;
        if (checkedResTemp.isChecked94())
            counter0 = 94;
        if (checkedResTemp.isChecked95())
            counter0 = 95;
        if (checkedResTemp.isChecked96())
            counter0 = 96;
        if (checkedResTemp.isChecked97())
            counter0 = 97;
        if (checkedResTemp.isChecked98())
            counter0 = 98;
        if (checkedResTemp.isChecked99())
            counter0 = 99;
        if (checkedResTemp.isChecked100())
            counter0 = 100;
        News news = new News();
        for (News item : NewsListTemp) {
            counter++;
            if (counter == counter0) {
                news = item;
                break;
            }
        }
        model.addAttribute("news", news);

        checkedResTemp = null;

        return "client_news_zoom";
    }

}
