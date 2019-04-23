package com.healthy.com.healing.ui.BaseMine;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.healthy.com.healing.BaseActivity;
import com.healthy.com.healing.R;
import com.healthy.com.healing.litepal.User;
import com.healthy.com.healing.util.HandleImageUtil;
import com.healthy.com.healing.util.StatusBarUtil;
import com.healthy.com.healing.util.UserUtil;
import com.steelkiwi.cropiwa.image.CropIwaResultReceiver;

import java.security.Permissions;

public class Personal_Info extends BaseActivity implements View.OnClickListener,CropIwaResultReceiver.Listener{

    private View PersonalInfo_Layout;
    private NumberPicker yearPicker;
    private NumberPicker monthPicker;
    private NumberPicker dayPicker;
    private int year = 1929;
    private int month = 1;
    private int day = 1;
    private View dialogView;
    private SharedPreferences pref;
    private ImageView IV_back;
    private ImageView IV_save;
    private ImageView IV_head;
    private RelativeLayout rl_changehead;
    private EditText ET_username;
    private RelativeLayout rl_gender;
    private RelativeLayout rl_born;
    private TextView TV_previewsex;
    private TextView TV_previewborn;
    User user = Login.getUser();
    private int i;
    private final String[] items = { "男","女","保密" };

    private static final int REQUEST_CHOOSE_PHOTO = 1101;
    private static final int REQUEST_STORAGE_PERMISSION = 9;
    private CropIwaResultReceiver cropResultReceiver;//头像广播监听器
    private HandleImageUtil handleImageUtil = new HandleImageUtil();

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_info);
        final ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        init();

        //计算状态栏高度，加到View上
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        PersonalInfo_Layout.setPadding(0,statusBarHeight,0,0);

        if(Login.getisLogin()){
            ET_username.setText(user.getUsername());
            TV_previewsex.setText(user.getSex());
            TV_previewborn.setText(user.getYear()+"."+user.getMonth()+"."+user.getDay());
        }
    }
    public void init(){
        if(!(user.getYear() == 0 && user.getMonth() == 0 && user.getDay() == 0)) {
            year = user.getYear();
            month = user.getMonth();
            day = user.getDay();
        }
        if(user.getSex().toString().equals("男")){
            i = 0;
        }else if(user.getSex().toString().equals("女")){
            i = 1;
        }else{
            i = 2;
        }
        LayoutInflater inflater = LayoutInflater.from(Personal_Info.this);
        //父View
        dialogView = inflater.inflate(R.layout.alertdialog_born,null);
        //三个子View
        yearPicker = (NumberPicker) dialogView.findViewById(R.id.NP_year);
        yearPicker.setMaxValue(2019);
        yearPicker.setMinValue(1929);
        yearPicker.setValue(year);
        yearPicker.setFocusable(true);
        yearPicker.setFocusableInTouchMode(true);
        yearPicker.setOnValueChangedListener(yearChangedListener);

        monthPicker = (NumberPicker) dialogView.findViewById(R.id.NP_month);
        monthPicker.setMaxValue(12);
        monthPicker.setMinValue(1);
        monthPicker.setValue(month);
        monthPicker.setFocusable(true);
        monthPicker.setFocusableInTouchMode(true);
        monthPicker.setOnValueChangedListener(monthChangedListener);
        /*
         * / setMaxValue根据每月的天数不一样，使用switch()进行分别判断
         */
        dayPicker = (NumberPicker) dialogView.findViewById(R.id.NP_day);
        dayPicker.setMinValue(1);
        dayPicker.setMaxValue(31);
        dayPicker.setValue(day);
        dayPicker.setFocusable(true);
        dayPicker.setFocusableInTouchMode(true);
        dayPicker.setOnValueChangedListener(dayChangedListener);


        pref = PreferenceManager.getDefaultSharedPreferences(this);
        PersonalInfo_Layout = findViewById(R.id.Personal_Info_Layout);
        IV_back = (ImageView) findViewById(R.id.IV_back);
        IV_save = (ImageView)findViewById(R.id.IV_save);
        IV_head = findViewById(R.id.IV_head);
        rl_changehead = (RelativeLayout) findViewById(R.id.rl_changehead);
        ET_username = (EditText)findViewById(R.id.ET_username);
        rl_gender = (RelativeLayout) findViewById(R.id.rl_gender);
        rl_born = (RelativeLayout) findViewById(R.id.rl_born);
        TV_previewsex = (TextView)findViewById(R.id.TV_previewsex);
        TV_previewborn = (TextView)findViewById(R.id.TV_previewborn);

        IV_back.setClickable(true);
        IV_save.setClickable(true);
        rl_changehead.setClickable(true);
        rl_gender.setClickable(true);
        rl_born.setClickable(true);

        IV_back.setOnClickListener(this);
        IV_save.setOnClickListener(this);
        rl_changehead.setOnClickListener(this);
        rl_gender.setOnClickListener(this);
        rl_born.setOnClickListener(this);

        cropResultReceiver = new CropIwaResultReceiver();
        cropResultReceiver.setListener(this);
        cropResultReceiver.register(this);

        if (user.getHeadshot() != null){
            Bitmap bitmap = BitmapFactory.decodeFile(user.getHeadshot());
            IV_head.setImageBitmap(bitmap);
        }
    }
    @Override
    public void onClick(View v) {
        int item = v.getId();
        switch (item){
            case R.id.IV_back:
                Personal_Info.this.finish();
                break;
            case R.id.IV_save:
                ProgressDialog saveDialog = new ProgressDialog(Personal_Info.this);
                saveDialog.setTitle("正在保存");
                saveDialog.setMessage("请等待...");
                saveDialog.setCancelable(true);
                saveDialog.show();
                user.setUsername(ET_username.getText().toString().trim());
                user.setSex(TV_previewsex.getText().toString().trim());
                user.setYear(year);
                user.setMonth(month);
                user.setDay(day);
                handleImageUtil.saveheadshot();
               // user.setBorn(TV_previewborn.getText().toString().trim());
                UserUtil.updateUser(user);
                saveDialog.dismiss();
                Toast.makeText(Personal_Info.this,"保存成功~",Toast.LENGTH_SHORT).show();
                break;
            case R.id.rl_changehead:
                startGalleryApp();
                break;
            case R.id.rl_gender:
                genderDialog();
                break;
            case R.id.rl_born:
                birthdDialog();
                break;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHOOSE_PHOTO && resultCode == Activity.RESULT_OK) {
            startCropActivity(data.getData());
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGalleryApp();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startGalleryApp() {
        if (ActivityCompat.checkSelfPermission(Personal_Info.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            intent = Intent.createChooser(intent, getString(R.string.title_choose_image));
            startActivityForResult(intent, REQUEST_CHOOSE_PHOTO);
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        }
    }

    private void startCropActivity(Uri uri) {
        startActivity(Crop.callingIntent(this, uri));
    }

    private void genderDialog() {
        AlertDialog.Builder sexDialog = new AlertDialog.Builder(Personal_Info.this);
        sexDialog.setTitle("性别");
        // 第二个参数是默认选项，此处设置为SharedPreferences读取保存的状态
        sexDialog.setSingleChoiceItems(items, i,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        TV_previewsex.setText(items[which]);
                        //SharedPreferences保存选择index
                        //pref.edit().putInt("sex",which).apply();
                        if(items[which].toString().equals("男")){
                            i = 0;
                        }else if(items[which].toString().equals("女")){
                            i = 1;
                        }else{
                            i = 2;
                        }
                        dialog.dismiss();
                    }
                });
        sexDialog.show();
    }

    private void birthdDialog() {

        AlertDialog.Builder bornDialog = new AlertDialog.Builder(Personal_Info.this);
        bornDialog.setTitle("出生日期");
        bornDialog.setView(dialogView);
        bornDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                TV_previewborn.setText(year + "."+ month + "." + day);
            }
        });
        //☆☆☆☆☆☆☆☆☆☆  子View已经存在于父View，你必须先调用该子View的父View的removeView()方法
        ViewGroup parent = (ViewGroup) dialogView.getParent();
        if(parent != null) {
            parent.removeAllViews();
        }
        //☆☆☆☆☆☆☆☆☆☆
        bornDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        bornDialog.show();
    }

    private NumberPicker.OnValueChangeListener yearChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub
            year = yearPicker.getValue();
        }
    };
    private NumberPicker.OnValueChangeListener monthChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub
            month = monthPicker.getValue();
            switch (month) {
                case 1:
                    case 3:
                        case 5:
                            case 7:
                                case 8:
                                    case 10:
                                        case 12:
                                            dayPicker.setMaxValue(31);
                                            break;
                                            case 2: dayPicker.setMaxValue(29);
                                            break;
                                            case 4:
                                                case 6:
                                                    case 9:
                                                        case 11: dayPicker.setMaxValue(30);
                                                        break; default: break;
            }
        }
    };
    private NumberPicker.OnValueChangeListener dayChangedListener = new NumberPicker.OnValueChangeListener() {
        @Override
        public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
            // TODO Auto-generated method stub
            day = dayPicker.getValue();
        }
    };


    @Override
    public void onCropSuccess(Uri croppedUri) {
        handleImageUtil.handleImage(croppedUri,IV_head,this);
    }

    @Override
    public void onCropFailed(Throwable e) {
        Snackbar.make(PersonalInfo_Layout,
                getString(R.string.msg_crop_failed, e.getMessage()),
                Snackbar.LENGTH_LONG)
                .show();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        cropResultReceiver.unregister(this);
    }
}
