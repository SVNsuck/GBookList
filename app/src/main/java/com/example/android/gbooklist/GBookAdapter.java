package com.example.android.gbooklist;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;

/**
 * Created by PWT on 2017/6/13.
 */

public class GBookAdapter extends ArrayAdapter<GBook> {

    private static final String TAG = "GBookAdapter";

    private View listItemView;

    private int count = 1;

    public GBookAdapter(@NonNull Context context, @NonNull List<GBook> gBooks) {
        super(context, 0, gBooks);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        listItemView = convertView;

        if(listItemView == null){
            listItemView = LayoutInflater.from(getContext()).inflate(R.layout.gbook_list_item,parent,false);
        }

        GBook gBook = getItem(position);

        //设置标题
        TextView titleTextView = (TextView)listItemView.findViewById(R.id.book_title);
        titleTextView.setText(gBook.getTitle());

        //设置作者
        TextView authorsTextView = (TextView)listItemView.findViewById(R.id.book_authors);
        authorsTextView.setText(gBook.getAuthors());

        //设置出版日期
        TextView publishDateTextView = (TextView)listItemView.findViewById(R.id.book_publishDate);
        publishDateTextView.setText(gBook.getPublishDate());

        //启动线程
        ImageAsyncTask imageAsyncTask = new ImageAsyncTask();
        imageAsyncTask.execute(gBook.getBookImageUrl());
        Log.i(TAG, "getView: count :" + count++);
        return listItemView;
    }

    private class ImageAsyncTask extends AsyncTask<String,Void,Bitmap>{
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //设置图片路径
            ImageView imageView = (ImageView)listItemView.findViewById(R.id.book_img);
            imageView.setImageBitmap(bitmap);
        }

        @Override
        protected Bitmap doInBackground(String... imageUrls) {
            Bitmap bitmap = null;
            if(TextUtils.isEmpty(imageUrls[0])){
                return null;
            }
            try {
                bitmap =getImageBitmap(imageUrls[0]);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }
    }

    private Bitmap getImageBitmap(String url) throws IOException {
        Bitmap bm = null;
        BufferedInputStream bis = null;
        InputStream is = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            is = conn.getInputStream();
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
