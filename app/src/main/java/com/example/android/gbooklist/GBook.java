package com.example.android.gbooklist;

import android.databinding.ObservableField;

/**
 * Created by PWT on 2017/6/13.
 */

public class GBook {

    //书名
    public final ObservableField<String> title = new ObservableField<>();

    //作者
    public final ObservableField<String> authors = new ObservableField<>();

    //出版日期

    public final ObservableField<String> publishDate = new ObservableField<>();

    //书本图片网址
    private String bookImageUrl;

    //书本对应的GooglePlay的展示页面网址
    private String bookItemShowUrl;

    public String getBookImageUrl() {
        return bookImageUrl;
    }

    public void setBookImageUrl(String bookImageUrl) {
        this.bookImageUrl = bookImageUrl;
    }

    public String getBookItemShowUrl() {
        return bookItemShowUrl;
    }

    public void setBookItemShowUrl(String bookItemShowUrl) {
        this.bookItemShowUrl = bookItemShowUrl;
    }
}
