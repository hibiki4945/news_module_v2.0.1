package com.example.news_module.constants;

public enum RtnCode {

    SUCCESSFUL("200", "成功しました"),
    DATA_EMPTY_ERROR("400", "データがない"),
    CATEGORY_EMPTY_ERROR("400", "カテゴリーがない"),
    SUB_CATEGORY_EMPTY_ERROR("400", "サブカテゴリーがない"),
    NEWS_TITLE_EMPTY_ERROR("400", "ニュースタイトルがない"),
    NEWS_SUB_TITLE_EMPTY_ERROR("400", "ニュースのサブタイトルがない"),
    RELEASE_TIME_FORMAT_ERROR("400", "発表日の仕様が合わない"),
    CONTENT_EMPTY_ERROR("400", "コンテンツがない"),
    NEWS_EXISTS_ERROR("400", "ニュースが既に存在している"),
    DAO_ERROR("400", "不明な問題が発生"),
    DATA_ERROR("400", "データエラー"),
    CATEGORY_EXISTS_ERROR("400", "カテゴリーが既に存在している"),
    SUB_CATEGORY_EXISTS_ERROR("400", "サブカテゴリーが既に存在している"),
    CATEGORY_NOT_EXISTS_ERROR("400", "カテゴリーは存在しない"),
    NEWS_TITLE_OVER_LENGTH_ERROR("400", "ニュースタイトルが長さ制限を超えた"),
    NEWS_SUB_TITLE_OVER_LENGTH_ERROR("400", "ニュースのサブタイトルが長さ制限を超えた"),
    CONTENT_OVER_LENGTH_ERROR("400", "コンテンツが長さ制限を超えた"),
    CATEGORY_OVER_LENGTH_ERROR("400", "カテゴリーが長さ制限を超えた"),
    SUB_CATEGORY_OVER_LENGTH_ERROR("400", "サブカテゴリが長さ制限を超えた"),
    NO_CHANGE_ERROR("400", "改変はしなかった");
    
    private String code;
    
    private String message;

    private RtnCode(String code, String message) {
        this.code = code;
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
    
}
