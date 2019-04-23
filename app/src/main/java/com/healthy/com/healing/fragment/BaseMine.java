package com.healthy.com.healing.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthy.com.healing.R;
import com.healthy.com.healing.litepal.User;
import com.healthy.com.healing.ui.BaseMine.Login;
import com.steelkiwi.cropiwa.image.CropIwaResultReceiver;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;


public class BaseMine extends Fragment implements View.OnClickListener{

    private View rootView;
    private de.hdodenhof.circleimageview.CircleImageView IV_head;
    private ImageView IV_record;
    private ImageView IV_weight;
    private ImageView IV_daily;
    private ImageView IV_myinfo;
    private ImageView IV_mydata;
    private ImageView iv_setting;

    private TextView TV_username;
    private TextView TV_record;
    private TextView TV_weight;
    private TextView TV_daily;

    private RelativeLayout rl_myinfo;
    private RelativeLayout rl_mydata;
    private RelativeLayout rl_mytarget;

    User user;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (rootView == null) {
            rootView = inflater.inflate(R.layout.activity_mine, container, false);
        }
        initView();
        return rootView;
    }

    private void initView() {

        rl_myinfo = rootView.findViewById(R.id.rl_myinfo);
        rl_mydata = rootView.findViewById(R.id.rl_mydata);
        rl_mytarget = rootView.findViewById(R.id.rl_mytarget);
        IV_head = rootView.findViewById(R.id.IV_head);
        IV_record = rootView.findViewById(R.id.IV_record);
        IV_weight = rootView.findViewById(R.id.IV_weight);
        IV_daily = rootView.findViewById(R.id.IV_daily);
        iv_setting = rootView.findViewById(R.id.iv_setting);

        TV_username = rootView.findViewById(R.id.TV_username);
        TV_record = rootView.findViewById(R.id.TV_record);
        TV_weight = rootView.findViewById(R.id.TV_weight);
        TV_daily = rootView.findViewById(R.id.TV_daily);

        rl_myinfo.setOnClickListener(this);
        rl_mydata.setOnClickListener(this);
        rl_mytarget.setOnClickListener(this);
        IV_head.setOnClickListener(this);
        IV_record.setOnClickListener(this);
        IV_weight.setOnClickListener(this);
        IV_daily.setOnClickListener(this);
        TV_username.setOnClickListener(this);
        TV_record.setOnClickListener(this);
        TV_weight.setOnClickListener(this);
        TV_daily.setOnClickListener(this);
        iv_setting.setOnClickListener(this);

        if (Login.getisLogin()){
            user = Login.getUser();
            Bitmap bitmap = BitmapFactory.decodeFile(user.getHeadshot());
            IV_head.setImageBitmap(bitmap);
        }

    }

    @Override
    public void onResume() {
        super.onResume();
        if(Login.getisLogin()){
            EventBus.getDefault().post(new BaseMine.updataName());
        }else{
            TV_username.setText("请登录");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onClick(View v) {
        int item = v.getId();
        switch (item){
            case R.id.IV_head:
                if(!Login.getisLogin()){
                    Intent intent = new Intent("com.healthy.com.healing.LOGIN");
                    startActivity(intent);
                }else{
                    Intent intent_1 = new Intent("com.healthy.com.healing.PERSONALINFO");
                    startActivity(intent_1);
                }
                break;
            case R.id.TV_username:
                if(!Login.getisLogin()){
                    Intent intent = new Intent("com.healthy.com.healing.LOGIN");
                    startActivity(intent);
                }else{
                    Intent intent_1 = new Intent("com.healthy.com.healing.PERSONALINFO");
                    startActivity(intent_1);
                }
                break;
            case R.id.IV_record:
                if(!Login.getisLogin()){
                    Intent intent = new Intent("com.healthy.com.healing.LOGIN");
                    startActivity(intent);
                }else{
                    Intent intent_1 = new Intent("com.healthy.com.healing.SPORTRECORD");
                    startActivity(intent_1);
                }
                break;
            case R.id.TV_record:
                if(!Login.getisLogin()){
                    Intent intent = new Intent("com.healthy.com.healing.LOGIN");
                    startActivity(intent);
                }else{
                    Intent intent_1 = new Intent("com.healthy.com.healing.SPORTRECORD");
                    startActivity(intent_1);
                }
                break;
            case R.id.rl_myinfo:
                if(!Login.getisLogin()){
                    Intent intent = new Intent("com.healthy.com.healing.LOGIN");
                    startActivity(intent);
                }else {
                    Intent intent_1 = new Intent("com.healthy.com.healing.PERSONALINFO");
                    startActivity(intent_1);
                }
                break;
            case R.id.rl_mydata:
                if(!Login.getisLogin()){
                    Intent intent = new Intent("com.healthy.com.healing.LOGIN");
                    startActivity(intent);
                }else {
                    Intent intent_1 = new Intent("com.healthy.com.healing.PERSONALDATA");
                    startActivity(intent_1);
                }
                break;
            case R.id.rl_mytarget:
                if(!Login.getisLogin()){
                    Intent intent = new Intent("com.healthy.com.healing.LOGIN");
                    startActivity(intent);
                }else {
                    Intent intent_1 = new Intent("com.healthy.com.healing.PERSONALTARGET");
                    startActivity(intent_1);
                }
                break;
            case R.id.iv_setting:
                Intent intent = new Intent("com.healthy.com.healing.SETTING");
                startActivity(intent);
                break;

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Subscribe
    public void onEventMainThread(updataName event){
        if(Login.getUser().getUsername() != null){
            TV_username.setText(String.valueOf(Login.getUser().getUsername()));
        }else{
            TV_username.setText("请修改昵称~");
        }
        if (Login.getisLogin()){
            user = Login.getUser();
            Bitmap bitmap = BitmapFactory.decodeFile(user.getHeadshot());
            IV_head.setImageBitmap(bitmap);
        }
    }
    public static class updataName{

    }
}
