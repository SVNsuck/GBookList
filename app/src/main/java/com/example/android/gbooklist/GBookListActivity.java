package com.example.android.gbooklist;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class GBookListActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<GBook>> {

    private static final String TAG = "GBookListActivity";

    private GBookAdapter mGBookAdapter;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gbook_list_activity);

        ListView gBookListView = (ListView) findViewById(R.id.list);

        //初始化Adapter
        mGBookAdapter = new GBookAdapter(GBookListActivity.this, new ArrayList<GBook>());

        //设置adapter
        gBookListView.setAdapter(mGBookAdapter);

        //设置ListView内容为空时显示的layout
        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);
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
        Button searchButton = (Button) findViewById(R.id.search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //如果无网络连接,则显示空页面
                ConnectivityManager cm =
                        (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                //得到加载圈圈实体
                View bar = findViewById(R.id.pro_bar);

                NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
                boolean isConnected = activeNetwork != null &&
                        activeNetwork.isConnectedOrConnecting();
                if (isConnected) {
                    EditText editText = (EditText) findViewById(R.id.input_text);
                    String text = editText.getText().toString();

                    mGBookAdapter.clear();

                    //隐藏listview
                    ListView listView =(ListView)findViewById(R.id.list);
                    listView.setVisibility(View.GONE);
                    //显示加载圈圈
                    bar.setVisibility(View.VISIBLE);
                    if (TextUtils.isEmpty(text)) {
                        gBookRequestUrl.append(BASE_REQ_URL).append("&maxResults=10");
                    } else {
                        gBookRequestUrl.append(BASE_REQ_URL).append("?q=" + text).append("&maxResults=10");
                    }

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
        View bar = findViewById(R.id.pro_bar);
        bar.setVisibility(View.GONE);

        //显示listview
        ListView listView =(ListView)findViewById(R.id.list);
        listView.setVisibility(View.VISIBLE);

        // 清除之前地震数据的适配器
        mGBookAdapter.clear();

        // 如果存在 {@link Earthquake} 的有效列表，则将其添加到适配器的
        // 数据集。这将触发 ListView 执行更新。
        if (data != null && !data.isEmpty()) {
            mGBookAdapter.addAll(data);
        }

        gBookRequestUrl.setLength(0);
    }

    @Override
    public void onLoaderReset(Loader<List<GBook>> loader) {
        mGBookAdapter.clear();
    }
}
