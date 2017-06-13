package com.example.android.gbooklist;

/**
 * Created by PWT on 2017/6/13.
 */

public class GBook {

    //书名
    private String title;

    //作者
    private String authors;

    //出版日期

    private String publishDate;

    //书本图片网址
    private String bookImageUrl;

    //书本对应的GooglePlay的展示页面网址
    private String bookItemShowUrl;

    public GBook(String title, String authors, String publishDate, String bookImageUrl, String bookItemShowUrl) {
        this.title = title;
        this.authors = authors;
        this.publishDate = publishDate;
        this.bookImageUrl = bookImageUrl;
        this.bookItemShowUrl = bookItemShowUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthors() {
        return authors;
    }

    public String getPublishDate() {
        return publishDate;
    }

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public String getBookItemShowUrl() {
        return bookItemShowUrl;
    }
}
