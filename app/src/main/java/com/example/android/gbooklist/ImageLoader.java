package com.example.android.gbooklist;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v4.util.LruCache;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by PWT on 2017/6/14.
 */

/**
 * 图片加载的工具类
 */
public class ImageLoader {

    /**
     * LRU缓存
     */
    private LruCache<String,Bitmap> mMemoryCaches;

    /**
     *
     */
    private ListView mListView;

    /**
     * 用来存放异步加载任务的集合
     */
    private Set<ImageAsyncTask> mTasks;

    /**
     * 当前listView中需要加载图片的url字符串集合
     */
    public String[] mUrls;


    public ImageLoader(ListView listView){

        this.mListView = listView;

        mTasks = new HashSet<>();
        //最大内存
        int maxMemory = (int)Runtime.getRuntime().maxMemory();
        //缓存大小
        int cacheSizes = maxMemory/5;
        //初始化缓存
        mMemoryCaches = new LruCache<String,Bitmap>(cacheSizes){
            @SuppressLint("NewApi")
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }
    /**
     * 返回图片异步加载类的实例
     * @param imageView
     * @param url
     */
    public void showImage(ImageView imageView, String url){
        //首先去LruCache中去找图片
        Bitmap bitmap = getBitmapFromLrucache(url);
        //如果不为空，说明LruCache中已经缓存了该图片，则读取缓存直接显示，
        if (bitmap != null) {
            imageView.setImageBitmap(bitmap);
        } else {
            imageView.setImageResource(R.drawable.default_img_ic);
        }
    }

    /**
     * 从LRU缓存中查找图片
     * @param url
     * @return
     */
    public Bitmap getBitmapFromLrucache(String url){
        return mMemoryCaches.get(url);
    }

    /**
     * 将从web端获取的图片放入LRU缓存中
     * @param url
     * @param bitmap
     */
    public void addBitmapToLrucaches(String url,Bitmap bitmap){
        if(getBitmapFromLrucache(url)==null){
            mMemoryCaches.put(url,bitmap);
        }
    }

    /**
     * 根据起始位置加载图片
     * @param start
     * @param end
     */
    public void loadImages(int start, int end) {

        for (int i = start; i < end; i++) {
            String loadUrl = mUrls[i];
            if (getBitmapFromLrucache(loadUrl) != null) {
                ImageView imageView = (ImageView) mListView
                        .findViewWithTag(loadUrl);

                imageView.setImageBitmap(getBitmapFromLrucache(loadUrl));
            } else {
                ImageAsyncTask imageAsyncTask = new ImageAsyncTask(loadUrl);
                mTasks.add(imageAsyncTask);
                imageAsyncTask.execute(loadUrl);
            }
        }
    }

    /**
     * 取消所有的异步加载任务
     */
    public void cancelAllAsyncTask() {
        if (mTasks != null) {
            for (ImageAsyncTask imageAsyncTask : mTasks) {
                imageAsyncTask.cancel(false);
            }
        }
    }

    /**
     * 图片异步加载类
     */
    private class ImageAsyncTask extends AsyncTask<String,Void,Bitmap>{

        private static final String TAG = "ImageAsyncTask";


        private String mUrl;

        public ImageAsyncTask(String url){
            this.mUrl = url;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            //设置图片路径
//            if(mImageView.getTag().equals(mUrl)){
//                mImageView.setImageBitmap(bitmap);
//            }
            ImageView imageView = (ImageView) mListView.findViewWithTag(mUrl);

            if (bitmap != null && imageView != null) {
                imageView.setImageBitmap(bitmap);
            }

            mTasks.remove(this);
        }

        @Override
        protected Bitmap doInBackground(String... imageUrls) {
            Bitmap bitmap = null;
            String url = imageUrls[0];
            if(TextUtils.isEmpty(url)){
                return null;
            }
            try {
                bitmap =getImageBitmap(url);

                //下载完成之后将其加入到LruCache中这样下次加载的时候，就可以直接从LruCache中直接读取
                if (bitmap != null) {
                    addBitmapToLrucaches(url, bitmap);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return bitmap;
        }

        /**
         * 将图片url解析为Bitmap
         * @param url
         * @return
         * @throws IOException
         */
        private Bitmap getImageBitmap(String url) throws IOException {
            Bitmap bm = null;
            BufferedInputStream bis = null;
            InputStream is = null;
            try {
                URL aURL = new URL(url);
                HttpURLConnection conn = (HttpURLConnection)aURL.openConnection();
                is = conn.getInputStream();
                bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                conn.disconnect();
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
}
