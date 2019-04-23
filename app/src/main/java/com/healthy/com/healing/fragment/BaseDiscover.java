package com.healthy.com.healing.fragment;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.facebook.drawee.view.SimpleDraweeView;
import com.healthy.com.healing.R;
import com.healthy.com.healing.adapter.ActivityPushAdapter;
import com.healthy.com.healing.adapter.FitnessPushAdapter;
import com.healthy.com.healing.adapter.HealthyPushAdapter;
import com.healthy.com.healing.adapter.item_Push;
import com.healthy.com.healing.adapter.item_Push_Activity;
import com.healthy.com.healing.litepal.User;
import com.healthy.com.healing.ui.BaseDiscovery.RefreshLayout;
import com.healthy.com.healing.ui.BaseMine.Login;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.zhouwei.mzbanner.MZBannerView;
import com.zhouwei.mzbanner.holder.MZHolderCreator;
import com.zhouwei.mzbanner.holder.MZViewHolder;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BaseDiscover extends Fragment implements View.OnClickListener {

    public static final int REFRESH_DELAY = 2000;

    private RefreshLayout swipeRefresh;

    private CircleImageView iv_head_discovery;

    private View rootView;

    private Toolbar toolbar;

    private MaterialSearchView searchView;

    public static int []localimage = new int[]{};//发现页面广告栏

    private MZBannerView mNormalBanner;//顶部广告栏控件

    private List<item_Push> healthyPush = new ArrayList<>();

    private List<item_Push> fitnessPush = new ArrayList<>();

    private List<item_Push_Activity> activityPush = new ArrayList<>();

    private RecyclerView rv_push_healthy;
    private RecyclerView rv_push_fitness;
    private RecyclerView rv_push_activity;

    private HealthyPushAdapter HealthyAdapter;
    private FitnessPushAdapter FitnessAdapter;
    private ActivityPushAdapter ActivityAdapter;

    private RelativeLayout rl_keep_healthy;
    private RelativeLayout rl_keep_fitness;
    private RelativeLayout rl_keep_activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_dicover,container,false);
        }
        initView();
        initData();
//        setHasOptionsMenu(true);
//        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        initRefreshView();
        return rootView;
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
                getActivity().runOnUiThread(new Runnable(){
                    @Override
                    public void run() {
                        initRecycler();
                        HealthyAdapter.notifyDataSetChanged();
                        FitnessAdapter.notifyDataSetChanged();
                        ActivityAdapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);
                    }
                });
            }
        }).start();
    }



    public static class BannerViewHolder implements MZViewHolder<Integer> {
        private SimpleDraweeView mImageView;
        @Override
        public View createView(Context context) {
            // 返回页面布局文件
            View view = LayoutInflater.from(context).inflate(R.layout.banner_item,null);
            mImageView = (SimpleDraweeView) view.findViewById(R.id.banner_image);
            return view;
        }

        @Override
        public void onBind(Context context, int position, Integer data) {
            // 数据绑定
            mImageView.setImageResource(data);
        }
    }

    private void initView() {
        rl_keep_healthy = rootView.findViewById(R.id.rl_keep_healthy);
        rl_keep_healthy.setOnClickListener(this);
        rl_keep_fitness = rootView.findViewById(R.id.rl_keep_fitness);
        rl_keep_fitness.setOnClickListener(this);
        rl_keep_activity = rootView.findViewById(R.id.rl_keep_activity);
        rl_keep_activity.setOnClickListener(this);

        swipeRefresh = rootView.findViewById(R.id.swipe_refresh);
        //toolbar = rootView.findViewById(R.id.toolbar);
        //searchView = (MaterialSearchView) rootView.findViewById(R.id.search_view);
        mNormalBanner = (MZBannerView) rootView.findViewById(R.id.banner);

        iv_head_discovery = rootView.findViewById(R.id.iv_head_discovery);

        rv_push_healthy = (RecyclerView) rootView.findViewById(R.id.rv_push_healthy);
        rv_push_fitness = (RecyclerView) rootView.findViewById(R.id.rv_push_fitness);
        rv_push_activity = (RecyclerView) rootView.findViewById(R.id.rv_push_activity);
        LinearLayoutManager healthy_linearLayoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager fitness_linearLayoutManager = new LinearLayoutManager(getContext());
        LinearLayoutManager activity_linearLayoutManager = new LinearLayoutManager(getContext());
        rv_push_healthy.setLayoutManager(healthy_linearLayoutManager);
        rv_push_fitness.setLayoutManager(fitness_linearLayoutManager);
        rv_push_activity.setLayoutManager(activity_linearLayoutManager);
        HealthyAdapter = new HealthyPushAdapter(getContext(), healthyPush);
        FitnessAdapter = new FitnessPushAdapter(getContext(), fitnessPush);
        ActivityAdapter = new ActivityPushAdapter(getContext(), activityPush);
        rv_push_healthy.setAdapter(HealthyAdapter);
        rv_push_fitness.setAdapter(FitnessAdapter);
        rv_push_activity.setAdapter(ActivityAdapter);
        //设置RecyclerView不可滑动
        rv_push_healthy.setHasFixedSize(true);
        rv_push_healthy.setNestedScrollingEnabled(false);
        rv_push_fitness.setHasFixedSize(true);
        rv_push_fitness.setNestedScrollingEnabled(false);
        rv_push_activity.setHasFixedSize(true);
        rv_push_activity.setNestedScrollingEnabled(false);
    }
    private void initData() {
        localimage = new int[]{R.mipmap.timg,R.mipmap.timg,R.mipmap.timg,R.mipmap.timg};
        List<Integer> list = new ArrayList<>();
        for(int i=0;i<localimage.length;i++){
            list.add(localimage[i]);
        }
        mNormalBanner.setBannerPageClickListener(new MZBannerView.BannerPageClickListener() {
            @Override
            public void onPageClick(View view, int position) {
                Toast.makeText(getContext(),"click page:"+position,Toast.LENGTH_SHORT).show();
            }
        });
        mNormalBanner.setPages(list, new MZHolderCreator<BannerViewHolder>() {
            @Override
            public BannerViewHolder createViewHolder() {
                return new BannerViewHolder();
            }
        });

        swipeRefresh.setColorSchemeResources(R.color.colortheme);
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                initRefreshView();
            }
        });

        //搜索框文字变化监听
//        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
//
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                //Do some magic
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                //Do some magic
//                return false;
//            }
//        });
//        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
//            @Override
//            public void onSearchViewShown() {
//                //Do some magic
//            }
//
//            @Override
//            public void onSearchViewClosed() {
//                //Do some magic
//            }
//        });
//        //点击事件
//        searchView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Toast.makeText(getContext(),"第"+i+"行",Toast.LENGTH_LONG).show();
//            }
//        });
        initRecycler();
    }

    private void initRecycler() {

        healthyPush.clear();
        fitnessPush.clear();
        activityPush.clear();
        //初始化“养生保健”
        item_Push h_one = new item_Push(R.mipmap.tian,"开春早餐这样吃，营养简单又快手，再也不用早起，肠胃特别舒服","666");
        healthyPush.add(h_one);
        item_Push h_two = new item_Push(R.mipmap.tian,"开春早餐这样吃，营养简单又快手，再也不用早起，肠胃特别舒服","2678");
        healthyPush.add(h_two);
        item_Push h_three = new item_Push(R.mipmap.tian,"开春早餐这样吃，营养简单又快手，再也不用早起，肠胃特别舒服","555");
        healthyPush.add(h_three);
        //初始化“健体瘦身”
        item_Push f_one = new item_Push(R.mipmap.zhitian,"怎样养成“易瘦体质”？保持这6个习惯，想胖起来都难","98");
        fitnessPush.add(f_one);
        item_Push f_two = new item_Push(R.mipmap.zhitian,"怎样养成“易瘦体质”？保持这6个习惯，想胖起来都难","256");
        fitnessPush.add(f_two);
        item_Push f_three = new item_Push(R.mipmap.zhitian,"怎样养成“易瘦体质”？保持这6个习惯，想胖起来都难","965");
        fitnessPush.add(f_three);
        //初始化“活动”
        item_Push_Activity one = new item_Push_Activity(R.mipmap.timg,"一个活动鸭","98");
        activityPush.add(one);
        item_Push_Activity two = new item_Push_Activity(R.mipmap.timg,"一个活动鸭","256");
        activityPush.add(two);
        item_Push_Activity three = new item_Push_Activity(R.mipmap.timg,"一个活动鸭","965");
        activityPush.add(three);
    }


//    @Override
//    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//
//        inflater.inflate(R.menu.toolbar_discovery, menu);
//        MenuItem item = menu.findItem(R.id.action_search);
//        searchView.setMenuItem(item);super.onCreateOptionsMenu(menu, inflater);
//    }

    @Override
    public void onPause() {
        super.onPause();
        mNormalBanner.pause();
    }

    @Override
    public void onResume() {
        super.onResume();
        EventBus.getDefault().post(new BaseMine.updataName());
        mNormalBanner.start();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEventMainThread(BaseMine.updataName event){
        if (Login.getisLogin()){
            User user = Login.getUser();
            Bitmap bitmap = BitmapFactory.decodeFile(user.getHeadshot());
            iv_head_discovery.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_keep_healthy:
                Intent intent = new Intent("com.healthy.com.healing.KEEPHEALTHY");
                startActivity(intent);
                break;
            case R.id.rl_keep_fitness:
                Intent intent1 = new Intent("com.healthy.com.healing.KEEPFITNESS");
                startActivity(intent1);
                break;
            case R.id.rl_keep_activity:
                Intent intent2 = new Intent("com.healthy.com.healing.KEEPACTIVITY");
                startActivity(intent2);
                break;
        }
    }
}
