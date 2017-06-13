package com.example.android.gbooklist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.content.AsyncTaskLoader;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by PWT on 2017/6/13.
 */

public class ImageLoader extends AsyncTaskLoader {

    private static final String TAG = "GBookLoader";

    /** 查询 URL */
    private String imageUrl;

    public ImageLoader(Context context, String url){
        super(context);
        this.imageUrl = url;

    }

    @Override
    public Bitmap loadInBackground() {
        Bitmap bitmap = null;
        if(TextUtils.isEmpty(imageUrl)){
            return null;
        }
        try {
            bitmap =getImageBitmap(imageUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    private Bitmap getImageBitmap(String url) throws IOException {
        Bitmap bm = null;
        BufferedInputStream bis = null;
        InputStream is = null;
        try {
            URL aURL = new URL(url);
            is = aURL.openStream();
            bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
        } catch (IOException e) {
            Log.e(TAG, "Error getting bitmap", e);
        }finally {
            if(bis !=null){
                bis.close();
            }
            if(is != null){
                is.close();
            }
        }
        return bm;
    }

}
