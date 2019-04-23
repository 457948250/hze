package com.healthy.com.healing.ui.BaseDiscovery;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.healthy.com.healing.BaseActivity;
import com.healthy.com.healing.R;
import com.healthy.com.healing.adapter.FitnessPushAdapter;
import com.healthy.com.healing.adapter.HealthyPushAdapter;
import com.healthy.com.healing.adapter.item_Push;
import com.healthy.com.healing.util.StatusBarUtil;

import java.util.ArrayList;
import java.util.List;

public class Keep_Fitness extends BaseActivity implements View.OnClickListener {

    private View Keep_Fitness_Layout;

    private RecyclerView rv_keep_fitness;

    private LinearLayoutManager linearLayoutManager;

    private FitnessPushAdapter FitnessAdapter;

    private List<item_Push> keepFitness = new ArrayList<>();

    private ImageView iv_back_keep_fitness;

    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_keep_fitness);
        initView();
        initData();
        //计算状态栏高度，加到View上
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        Keep_Fitness_Layout.setPadding(0,statusBarHeight,0,0);
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
                        FitnessAdapter.notifyDataSetChanged();
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        }).start();
    }
    private void initView() {
        Keep_Fitness_Layout = findViewById(R.id.Keep_Fitness_Layout);

        swipeRefreshLayout = findViewById(R.id.refresh_keep_fitness);
        swipeRefreshLayout.setColorSchemeResources(R.color.colortheme);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initRefreshView();
            }
        });

        iv_back_keep_fitness = findViewById(R.id.iv_back_keep_fitness);
        iv_back_keep_fitness.setOnClickListener(this);

        rv_keep_fitness= findViewById(R.id.rv_keep_fitness);
        linearLayoutManager = new LinearLayoutManager(this);
        rv_keep_fitness.setLayoutManager(linearLayoutManager);
        FitnessAdapter = new FitnessPushAdapter(this, keepFitness);
        rv_keep_fitness.setAdapter(FitnessAdapter);
    }
    private void initData() {
        //初始化“健体瘦身”
        keepFitness.clear();
        for (int i = 0; i < 3; i++) {
            item_Push f_one = new item_Push(R.mipmap.zhitian, "怎样养成“易瘦体质”？保持这6个习惯，想胖起来都难", "98");
            keepFitness.add(f_one);
            item_Push f_two = new item_Push(R.mipmap.zhitian, "怎样养成“易瘦体质”？保持这6个习惯，想胖起来都难", "256");
            keepFitness.add(f_two);
            item_Push f_three = new item_Push(R.mipmap.zhitian, "怎样养成“易瘦体质”？保持这6个习惯，想胖起来都难", "965");
            keepFitness.add(f_three);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back_keep_fitness:
                this.finish();
                break;
        }
    }
}
