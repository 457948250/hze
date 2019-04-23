package com.healthy.com.healing.ui.BaseDiscovery;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.healthy.com.healing.BaseActivity;
import com.healthy.com.healing.R;
import com.healthy.com.healing.adapter.ActivityPushAdapter;
import com.healthy.com.healing.adapter.item_Push_Activity;
import com.healthy.com.healing.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class Keep_Activity extends BaseActivity implements View.OnClickListener{

    private View Keep_Activity_Layout;

    private RecyclerView rv_keep_activity;

    private LinearLayoutManager linearLayoutManager;

    private List<item_Push_Activity> keepActivity = new ArrayList<>();

    private ActivityPushAdapter ActivityAdapter;

    private SwipeRefreshLayout swipeRefreshLayout;

    private ImageView iv_back_keep_activity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep_activity);
        initView();
        initData();
        //计算状态栏高度，加到View上
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        Keep_Activity_Layout.setPadding(0,statusBarHeight,0,0);
    }

    private void initRefreshView() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                }catch (InterruptedException e){
                    e.printStackTrace();
                }
                runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        initData();
                        ActivityAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void initView() {
        Keep_Activity_Layout = findViewById(R.id.Keep_Activity_Layout);

        swipeRefreshLayout = findViewById(R.id.refresh_keep_activity);
        swipeRefreshLayout.setColorSchemeResources(R.color.colortheme);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initRefreshView();
            }
        });

        iv_back_keep_activity = findViewById(R.id.iv_back_keep_activity);
        iv_back_keep_activity.setOnClickListener(this);

        rv_keep_activity = findViewById(R.id.rv_keep_activity);
        linearLayoutManager = new LinearLayoutManager(this);
        rv_keep_activity.setLayoutManager(linearLayoutManager);
        ActivityAdapter = new ActivityPushAdapter(this, keepActivity);
        rv_keep_activity.setAdapter(ActivityAdapter);
    }

    private void initData() {
        //初始化“活动”
        keepActivity.clear();
        for (int i = 0; i < 3; i++) {
            item_Push_Activity one = new item_Push_Activity(R.mipmap.timg, "一个活动鸭", "98");
            keepActivity.add(one);
            item_Push_Activity two = new item_Push_Activity(R.mipmap.timg, "一个活动鸭", "256");
            keepActivity.add(two);
            item_Push_Activity three = new item_Push_Activity(R.mipmap.timg, "一个活动鸭", "965");
            keepActivity.add(three);
        }
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back_keep_activity:
                this.finish();
                break;
        }
    }
}
