package com.healthy.com.healing.ui.BaseDiscovery;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.healthy.com.healing.BaseActivity;
import com.healthy.com.healing.R;
import com.healthy.com.healing.adapter.HealthyPushAdapter;
import com.healthy.com.healing.adapter.item_Push;
import com.healthy.com.healing.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class Keep_Healthy extends BaseActivity implements View.OnClickListener{

    private View Keep_Healthy_Layout;

    private RecyclerView rv_keep_healthy;

    private LinearLayoutManager linearLayoutManager;

    private HealthyPushAdapter HealthyAdapter;

    private List<item_Push> keepHaelthy = new ArrayList<>();

    private ImageView iv_back_keep_healthy;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep_healthy);
        initView();
        initData();
        //计算状态栏高度，加到View上
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        Keep_Healthy_Layout.setPadding(0,statusBarHeight,0,0);
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
                        HealthyAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }

    private void initView() {
        swipeRefreshLayout = findViewById(R.id.refresh_keep_healthy);
        swipeRefreshLayout.setColorSchemeResources(R.color.colortheme);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initRefreshView();
            }
        });

        iv_back_keep_healthy = findViewById(R.id.iv_back_keep_healthy);
        iv_back_keep_healthy.setOnClickListener(this);

        Keep_Healthy_Layout = findViewById(R.id.Keep_Healthy_Layout);

        rv_keep_healthy= findViewById(R.id.rv_keep_healthy);
        linearLayoutManager = new LinearLayoutManager(this);
        rv_keep_healthy.setLayoutManager(linearLayoutManager);
        HealthyAdapter = new HealthyPushAdapter(this, keepHaelthy);
        rv_keep_healthy.setAdapter(HealthyAdapter);
    }

    private void initData() {
        //初始化“养生保健”
        keepHaelthy.clear();
        for (int i = 0; i < 3; i++) {
            item_Push h_one = new item_Push(R.mipmap.tian, "开春早餐这样吃，营养简单又快手，再也不用早起，肠胃特别舒服", "666");
            keepHaelthy.add(h_one);
            item_Push h_two = new item_Push(R.mipmap.tian, "开春早餐这样吃，营养简单又快手，再也不用早起，肠胃特别舒服", "2678");
            keepHaelthy.add(h_two);
            item_Push h_three = new item_Push(R.mipmap.tian, "开春早餐这样吃，营养简单又快手，再也不用早起，肠胃特别舒服", "555");
            keepHaelthy.add(h_three);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back_keep_healthy:
                this.finish();
                break;
        }
    }
}
