package com.example.android.gbooklist;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;

import java.util.List;

/**
 * Created by PWT on 2017/6/13.
 */

public class GBookLoader extends AsyncTaskLoader {

    private static final String TAG = "GBookLoader";

    /** 查询 URL */
    private String mUrl;

    public GBookLoader(Context context,String url){
        super(context);
        this.mUrl = url;

    }

    @Override
    public Object loadInBackground() {
        if(TextUtils.isEmpty(mUrl)){
            return null;
        }
        List<GBook> gBooks = QueryUtils.fetchGBookListData(mUrl);
        return gBooks;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }
}
