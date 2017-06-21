package com.example.android.gbooklist;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.gbooklist.databinding.GbookListActivityBinding;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

public class GBookListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<GBook>> {

    private static final String TAG = "GBookListActivity";

    private GBookAdapter mGBookAdapter;

    private ListView gBookListView;

    /**
     * 列表为空时显示的 TextView
     */
    private TextView mEmptyStateTextView;

    /**
     * 谷歌书籍列表 loader ID 的常量值。我们可选择任意整数。
     * 仅当使用多个 loader 时该设置才起作用。
     */
    private static final int GBOOKLIST_LOADER_ID = 1;

    private static final String BASE_REQ_URL = "https://www.googleapis.com/books/v1/volumes";

    private StringBuffer gBookRequestUrl = new StringBuffer();

    GbookListActivityBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gbook_list_activity);

        /**
         * 通过databinding得到自动生成的绑定类
         */
        binding = DataBindingUtil.setContentView(this,R.layout.gbook_list_activity);

        gBookListView =  binding.list;

        //设置ListView内容为空时显示的layout
        mEmptyStateTextView = binding.emptyView;
        gBookListView.setEmptyView(mEmptyStateTextView);


        gBookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // 查找单击的当前地震
                GBook currentGBook = mGBookAdapter.getItem(position);

                // 将字符串 URL 转换为 URI 对象（以传递至 Intent 中 constructor)
                Uri earthquakeUri = Uri.parse(currentGBook.getBookItemShowUrl());

                // 创建一个新的 Intent 以查看地震 URI
                Intent websiteIntent = new Intent(Intent.ACTION_VIEW, earthquakeUri);

                // 发送 Intent 以启动新活动
                startActivity(websiteIntent);
            }
        });

        /**
         * 搜索按钮的点击事件
         */
        Button searchButton = binding.searchButton;
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //将空页面的文字清空
                mEmptyStateTextView.setText("");
                //如果无网络连接,则显示空页面
                ConnectivityManager cm =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                //得到加载圈圈实体
                View bar = binding.proBar;

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    EditText editText = binding.inputText;
                    //取得用户输入的关键字
                    String text = editText.getText().toString();

                    //隐藏listview
                    gBookListView.setVisibility(View.GONE);
                    //显示加载圈圈
                    bar.setVisibility(View.VISIBLE);
                    //设置要进行搜索的url
                    try {
                        if (TextUtils.isEmpty(text)) {
                            gBookRequestUrl.append(BASE_REQ_URL).append("&maxResults=30");
                        } else {
                            gBookRequestUrl.append(BASE_REQ_URL).append("?q=" + URLEncoder.encode(text,"UTF-8")).append("&maxResults=30");
                        }
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }

                    //每次点击都先将Loader销毁,不然这该死onCreateLoader方法只会在Loader初始化的时候调用一次,
                    // 导致更新后的url传不到GBookLoader实例中,使得后面的搜索都不起作用了
                    getSupportLoaderManager().destroyLoader(GBOOKLIST_LOADER_ID);
                    //启动线程
                    getSupportLoaderManager().initLoader(GBOOKLIST_LOADER_ID, null, GBookListActivity.this);
                } else {
                    mEmptyStateTextView.setText(R.string.no_network);
                }
            }
        });
    }

    @Override
    public Loader<List<GBook>> onCreateLoader(int id, Bundle args) {
        return new GBookLoader(GBookListActivity.this, gBookRequestUrl.toString());
    }

    @Override
    public void onLoadFinished(Loader<List<GBook>> loader, List<GBook> data) {
        // Set empty state text to display "No Fucking earthquakes found."
        mEmptyStateTextView.setText(R.string.no_earthquakes);

        //隐藏加载圈圈
        View bar = binding.proBar;
        bar.setVisibility(View.GONE);

        //显示listview
        gBookListView.setVisibility(View.VISIBLE);

        //初始化Adapter
        mGBookAdapter = new GBookAdapter(GBookListActivity.this, data, gBookListView);

        //设置adapter
        binding.setAdapter(mGBookAdapter);
        //gBookListView.setAdapter(mGBookAdapter);

        gBookRequestUrl.setLength(0);
    }

    @Override
    public void onLoaderReset(Loader<List<GBook>> loader) {
    }
}
