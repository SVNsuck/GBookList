package com.example.android.gbooklist;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.gbooklist.databinding.GbookListItemBinding;

import java.util.List;

/**
 * Created by PWT on 2017/6/13.
 */

public class GBookAdapter extends ArrayAdapter<GBook> implements AbsListView.OnScrollListener{

    private static final String TAG = "GBookAdapter";

    private List<GBook> mList;
    private LayoutInflater mInflater;
    private ListView mListView;
    private View listItemView;
    private ImageLoader imageLoader;

    private int mStart;
    private int mEnd;
    private boolean isFirstIn;

    public GBookAdapter(@NonNull Context context, @NonNull List<GBook> data, ListView listView) {
        //调用 ArrayAdapter 的构造器，因为 GBookAdapter 继承 ArrayAdapter，使用 super 代表 ArrayAdapter
        super(context, 0, data);
        mList = data;
        mInflater = LayoutInflater.from(context);
        mListView = listView;
        isFirstIn = true;

        imageLoader = new ImageLoader(mListView);
        imageLoader.mUrls = new String[mList.size()];
        for(int i=0;i<mList.size();i++){
            imageLoader.mUrls[i] = mList.get(i).getBookImageUrl();
        }
        mListView.setOnScrollListener(this);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        GbookListItemBinding listItemBinding;
        listItemView = convertView;
        //ViewHolder viewHolder;
        if(listItemView == null){
            listItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),R.layout.gbook_list_item,parent,false);

        }else{
            listItemBinding = DataBindingUtil.getBinding(listItemView);
        }

        GBook gBook = getItem(position);

        listItemBinding.setGBook(gBook);


        //设置默认图片
        listItemBinding.bookImg.setImageResource(R.drawable.default_img_ic);

        //给当前的ImageView设置Tag
        listItemBinding.bookImg.setTag(gBook.getBookImageUrl());

        //启动线程
        imageLoader.showImage(listItemBinding.bookImg,gBook.getBookImageUrl());
        return listItemBinding.getRoot();
    }

    /**
     * 当列表滑动时不加载图片,停止滑动时加载图片
     * @param view
     * @param scrollState
     */
    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        if(scrollState==SCROLL_STATE_IDLE){//滑动停止
            imageLoader.loadImages(mStart,mEnd);
        }else{//正在滑动
            imageLoader.cancelAllAsyncTask();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        /**
         * 当前列表的开始项
         */
        mStart=firstVisibleItem;
        /**
         * 当前列表的结束项
         */
        mEnd=firstVisibleItem+visibleItemCount;

        /**
         * 由于listView初始化时会调用onScroll方法,故将首次图片异步加载放在这里
         */
        if(isFirstIn&&visibleItemCount>0){
            imageLoader.loadImages(mStart,mEnd);
            isFirstIn=false;
        }
    }

}
