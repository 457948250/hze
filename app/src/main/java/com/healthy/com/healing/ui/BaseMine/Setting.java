package com.healthy.com.healing.ui.BaseMine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.healthy.com.healing.BaseActivity;
import com.healthy.com.healing.R;
import com.healthy.com.healing.TrackApplication;
import com.healthy.com.healing.ui.BaseMine.Login;
import com.healthy.com.healing.ui.MainActivity;
import com.healthy.com.healing.util.StatusBarUtil;

public class Setting extends BaseActivity implements View.OnClickListener {

    private View Setting_Layout;

    private static boolean isLogout = false;

    private ImageView iv_back;

    private Button bt_loginout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ActionBar actionBar = getSupportActionBar();//隐藏标题栏
        if (actionBar != null){
            actionBar.hide();
        }
        initView();
        //计算状态栏高度，加到View上
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        Setting_Layout.setPadding(0,statusBarHeight,0,0);
    }

    private void initView() {
        Setting_Layout = findViewById(R.id.Setting_Layout);
        iv_back = findViewById(R.id.iv_back);
        bt_loginout = findViewById(R.id.bt_loginout);
        iv_back.setOnClickListener(this);
        bt_loginout.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.bt_loginout:
                Login.setIsLogin(false);
                isLogout = true;
                setIsLogout(isLogout);
                Login.getTencent().logout(this);
                TrackApplication.getInstance().exitAllActivity();
                startActivity(new Intent(this, MainActivity.class));
                break;
        }

    }


    public static boolean getisIsLogout() {
        return isLogout;
    }
    public static void setIsLogout(boolean isLogout1){
        isLogout = isLogout1;
    }
}
