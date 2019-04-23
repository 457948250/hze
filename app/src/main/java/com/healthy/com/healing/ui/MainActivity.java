package com.healthy.com.healing.ui;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.baidu.mapapi.SDKInitializer;
import com.healthy.com.healing.BaseActivity;
import com.healthy.com.healing.R;
import com.healthy.com.healing.TrackApplication;
import com.healthy.com.healing.adapter.myFragmentPagerAdapter;
import com.healthy.com.healing.fragment.BaseDiscover;
import com.healthy.com.healing.fragment.BaseExercise;
import com.healthy.com.healing.fragment.BaseHome;
import com.healthy.com.healing.fragment.BaseMine;
import com.healthy.com.healing.ui.BaseMine.Login;
import com.healthy.com.healing.util.BitmapUtil;
import com.healthy.com.healing.util.StatusBarUtil;


import org.litepal.tablemanager.Connector;

import java.util.ArrayList;
import java.util.List;

import pl.com.salsoft.sqlitestudioremote.SQLiteStudioService;


public class MainActivity extends BaseActivity {

    private long exitTime=0;
    private View container;
    private BaseMine baseMine;
    private BaseHome baseHome;
    private BaseDiscover baseDiscover;
    private BaseExercise baseExercise;
    private ViewPager viewPager;
    private List<Fragment> fragmentList;
    private FragmentManager fragmentManager;
    private com.healthy.com.healing.adapter.myFragmentPagerAdapter myFragmentPagerAdapter;
    private MenuItem menuItem;

    private TrackApplication trackApp;

    private SDKReceiver mReceiver;

    /**
     * 构造广播监听类，监听 SDK key 验证以及网络异常广播
     */
    public class SDKReceiver extends BroadcastReceiver {

        public void onReceive(Context context, Intent intent) {
            String s = intent.getAction();

            if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR)) {
                Toast.makeText(MainActivity.this,"apikey验证失败，地图功能无法正常使用",Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK)) {
                Toast.makeText(MainActivity.this,"apikey验证成功",Toast.LENGTH_SHORT).show();
            } else if (s.equals(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR)) {
                Toast.makeText(MainActivity.this,"网络错误",Toast.LENGTH_SHORT).show();
            }
        }
    }


    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    viewPager.setCurrentItem(0);
                    return true;
                case R.id.navigation_exercise:
                    viewPager.setCurrentItem(1);
                    return true;
                case R.id.navigation_discover:
                    viewPager.setCurrentItem(2);
                    return true;
                case R.id.navigation_my:
                    viewPager.setCurrentItem(3);
                    return true;
            }
            return false;
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ActionBar actionBar = getSupportActionBar();//隐藏标题栏
        if (actionBar != null){
            actionBar.hide();
        }


        final BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        container = findViewById(R.id.container);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        baseHome = new BaseHome();
        baseExercise = new BaseExercise();
        baseDiscover = new BaseDiscover();
        baseMine = new BaseMine();
        fragmentList = new ArrayList<Fragment>();
        fragmentManager = getSupportFragmentManager();
        fragmentList.add(baseHome);
        fragmentList.add(baseExercise);
        fragmentList.add(baseDiscover);
        fragmentList.add(baseMine);

        myFragmentPagerAdapter = new myFragmentPagerAdapter(fragmentManager,fragmentList);
        viewPager.setAdapter(myFragmentPagerAdapter);
        //计算状态栏高度，加到View上
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        container.setPadding(0,statusBarHeight,0,0);

        /** 此方法为添加 ViewPaper 和 ButtonNavigationView 的联动
        *
        * 获取MenuItem
        * MenuItem 的 setChecked
        */
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (menuItem != null) {
                    menuItem.setChecked(false);
                } else {
                    navigation.getMenu().getItem(0).setChecked(false);
                }
                menuItem = navigation.getMenu().getItem(position);
                menuItem.setChecked(true);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });//页面变化监听
//        //禁止ViewPager滑动
//        viewPager.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return true;
//            }
//        });
        viewPager.setOffscreenPageLimit(3);//预加载
        Connector.getDatabase();
        SQLiteStudioService.instance().start(this);


        // apikey的授权需要一定的时间，在授权成功之前地图相关操作会出现异常；apikey授权成功后会发送广播通知，我们这里注册 SDK 广播监听者
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_OK);
        iFilter.addAction(SDKInitializer.SDK_BROADTCAST_ACTION_STRING_PERMISSION_CHECK_ERROR);
        iFilter.addAction(SDKInitializer.SDK_BROADCAST_ACTION_STRING_NETWORK_ERROR);
        mReceiver = new SDKReceiver();
        //动态注册广播接收者
        registerReceiver(mReceiver, iFilter);
        trackApp = (TrackApplication) getApplicationContext();
        BitmapUtil.init();
        Log.i("isLogin",String.valueOf(Login.getisLogin()));
        if(!Login.getisLogin()){
            Intent intent = new Intent("com.healthy.com.healing.LOGIN");
            startActivity(intent);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 适配android M，检查权限
        List<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && isNeedRequestPermissions(permissions)) {
            requestPermissions(permissions.toArray(new String[permissions.size()]), 0);
        }
    }
    private boolean isNeedRequestPermissions(List<String> permissions) {
        // 定位精确位置
        addPermission(permissions, Manifest.permission.ACCESS_FINE_LOCATION);
        // 存储权限
        addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        // 读取手机状态
        addPermission(permissions, Manifest.permission.READ_PHONE_STATE);
        return permissions.size() > 0;
    }

    private void addPermission(List<String> permissionsList, String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //取消注册广播接收者
        unregisterReceiver(mReceiver);

        if (trackApp.trackConf.contains("is_trace_started")
                && trackApp.trackConf.getBoolean("is_trace_started", true)) {
            // 退出app停止轨迹服务时，不再接收回调，将OnTraceListener置空
            trackApp.mClient.setOnTraceListener(null);
            trackApp.mClient.stopTrace(trackApp.mTrace, null);
            trackApp.mClient.clear();
        } else {
            trackApp.mClient.clear();
        }
        trackApp.isTraceStarted = false;
        trackApp.isGatherStarted = false;
        SharedPreferences.Editor editor = trackApp.trackConf.edit();
        editor.remove("is_trace_started");
        editor.remove("is_gather_started");
        editor.apply();

        BitmapUtil.clear();
    }
    //捕获back键，并执行exit()记录按键时间计算时间差
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event){
        if(keyCode==KeyEvent.KEYCODE_BACK){
            exit();
            return false;
        }
        return super.onKeyDown(keyCode,event);
    }

    private void exit() {
        if ((System.currentTimeMillis() - exitTime) > 2000) {
            Toast.makeText(getApplicationContext(),
                    "再按一次退出程序", Toast.LENGTH_SHORT).show();
            exitTime = System.currentTimeMillis();
        } else{
                finish();
                System.exit(0);
        }
    }

}
