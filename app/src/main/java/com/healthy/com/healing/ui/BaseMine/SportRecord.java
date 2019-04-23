package com.healthy.com.healing.ui.BaseMine;

import android.annotation.SuppressLint;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import com.healthy.com.healing.BaseActivity;
import com.healthy.com.healing.R;
import com.healthy.com.healing.adapter.SportAdapter;
import com.healthy.com.healing.adapter.item_SportRecord;
import com.healthy.com.healing.litepal.SportRecordList;
import com.healthy.com.healing.util.StatusBarUtil;

import org.litepal.crud.DataSupport;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class SportRecord extends BaseActivity implements View.OnClickListener{

    private View Sport_Record_Layout;
    private List<item_SportRecord> sportRecord = new ArrayList<>();
    public List<item_SportRecord> sportRecord_run;
    public List<item_SportRecord> sportRecord_walk;
    public List<item_SportRecord> sportRecord_bike;
    private ImageView IV_record_back;
    private TextView TV_sport_style;
    private TextView tv_hint;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sport_record);
        //去掉标题栏
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        initView();

        //计算状态栏高度，加到View上
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        Sport_Record_Layout.setPadding(0,statusBarHeight,0,0);

        //初始化所有运动数据
        showRecyclerView(initSportRecord_all());
    }

    private void showRecyclerView(List<item_SportRecord> item_SportRecord) {
        if (item_SportRecord.isEmpty()) {
            tv_hint.setVisibility(View.VISIBLE);
        } else {
            tv_hint.setVisibility(View.GONE);
        }
            RecyclerView recyclerView = (RecyclerView) findViewById(R.id.RV_sport_record);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(linearLayoutManager);
            SportAdapter adapter = new SportAdapter(SportRecord.this, item_SportRecord);
            recyclerView.setAdapter(adapter);
    }

    public void initView() {
        Sport_Record_Layout = findViewById(R.id.Sport_Record_Layout);
        IV_record_back = (ImageView)findViewById(R.id.IV_record_back);
        TV_sport_style = (TextView)findViewById(R.id.TV_sport_style);
        tv_hint = (TextView)findViewById(R.id.tv_hint);
        IV_record_back.setClickable(true);
        IV_record_back.setOnClickListener(this);
        TV_sport_style.setClickable(true);
        TV_sport_style.setOnClickListener(this);
    }


    //初始化所有运动数据
    public List<item_SportRecord> initSportRecord_all() {
        sportRecord.clear();
        SimpleDateFormat time = new SimpleDateFormat("HH:mm:ss");
        SimpleDateFormat day = new SimpleDateFormat("MM/dd");
        List<SportRecordList> sportRecordLists = DataSupport.where("useraccount = ?", String.valueOf(Login.getUser().getUseraccount())).find(SportRecordList.class);
            for (int i = 0; i < sportRecordLists.size(); i++) {
                int imageId = 0;
                String sportstyle = sportRecordLists.get(i).getSportstyle();
                switch (sportstyle) {
                    case "run":
                        imageId = R.drawable.ic_directions_run_black_24dp;
                        break;
                    case "walk":
                        imageId = R.drawable.ic_directions_walk_black_24dp;
                        break;
                    case "bike":
                        imageId = R.drawable.ic_directions_bike_black_24dp;
                        break;
                }
                item_SportRecord item_sportRecord = new item_SportRecord(String.format("%.2f", sportRecordLists.get(i).getDistance() / 1000) + "  公里", time.format((sportRecordLists.get(i).getEndtime() - sportRecordLists.get(i).getStarttime()) * 1000 - 3600 * 8000), String.format("%.2f", sportRecordLists.get(i).getSpeed()) + " 公里/小时", day.format(sportRecordLists.get(i).getEndtime() * 1000), imageId, sportRecordLists.get(i).getStarttime(), sportRecordLists.get(i).getEndtime());
                sportRecord.add(item_sportRecord);
            }
//        item_SportRecord walk = new item_SportRecord("0.55"+"  公里","00.11.14","20'30"+" /公里","3/13",R.drawable.ic_directions_walk_black_24dp,1,2);
//        sportRecord.add(walk);
//        item_SportRecord run = new item_SportRecord("6.18"+"  公里","00.12.13","30'16"+" /公里","3/12",R.drawable.ic_directions_run_black_24dp,1,2);
//        sportRecord.add(run);
//        item_SportRecord bike = new item_SportRecord("16.32"+"  公里","00.34.18","45'30"+" /公里","3/10",R.drawable.ic_directions_bike_black_24dp,1,2);
//        sportRecord.add(bike);
            return sportRecord;
    }

    //初始化跑步数据
    public List<item_SportRecord> initSportRecord_run(){
        sportRecord_run = new ArrayList<>();
        sportRecord_run.clear();
        for (int i = 0;i < sportRecord.size();i++){
            if(sportRecord.get(i).getImageId() == R.drawable.ic_directions_run_black_24dp){
                sportRecord_run.add(sportRecord.get(i));
            }
        }
        return sportRecord_run;
    }
    //初始化步行数据
    public List<item_SportRecord> initSportRecord_walk(){
        sportRecord_walk = new ArrayList<>();
        sportRecord_walk.clear();
        for (int i = 0;i < sportRecord.size();i++){
            if(sportRecord.get(i).getImageId() == R.drawable.ic_directions_walk_black_24dp){
                sportRecord_walk.add(sportRecord.get(i));
            }
        }
        return sportRecord_walk;
    }
    //初始化骑行数据
    public List<item_SportRecord> initSportRecord_ride(){
        sportRecord_bike = new ArrayList<>();
        sportRecord_bike.clear();
        for (int i = 0;i < sportRecord.size();i++){
            if(sportRecord.get(i).getImageId() == R.drawable.ic_directions_bike_black_24dp){
                sportRecord_bike.add(sportRecord.get(i));
            }
        }
        return sportRecord_bike;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @SuppressLint("RestrictedApi")
    @Override
    public void onClick(View v) {
        int item = v.getId();
        switch (item) {
            case R.id.IV_record_back:
                SportRecord.this.finish();
                break;
            case R.id.TV_sport_style:
                PopupMenu popupMenu =new PopupMenu(SportRecord.this, TV_sport_style);
                popupMenu.getMenuInflater().inflate(R.menu.sport_style,popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()){
                            case R.id.all:
                                TV_sport_style.setText("所有运动 ☟");
                                showRecyclerView(initSportRecord_all());
                                break;
                            case R.id.run:
                                TV_sport_style.setText("跑步 ☟");
                                showRecyclerView(initSportRecord_run());
                                break;
                            case R.id.walk:
                                TV_sport_style.setText("步行 ☟");
                                showRecyclerView(initSportRecord_walk());
                                break;
                            case R.id.ride:
                                TV_sport_style.setText("骑行 ☟");
                                showRecyclerView(initSportRecord_ride());
                                break;
                        }
                        return true;
                    }
                });
                //使用反射，强制显示菜单图标
//                try{
//                    Field field = popupMenu.getClass().getDeclaredField("mPopup");
//                    field.setAccessible(true);
//                    MenuPopupHelper mHelper = (MenuPopupHelper)field.get(popupMenu);
//                    mHelper.setForceShowIcon(true);
//                }catch (IllegalAccessException | NoSuchFieldException e){
//                    e.printStackTrace();
//                }
                popupMenu.show();
                break;
        }
    }
}

