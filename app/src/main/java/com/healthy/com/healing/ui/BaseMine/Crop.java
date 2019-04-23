package com.healthy.com.healing.ui.BaseMine;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.healthy.com.healing.BaseActivity;
import com.healthy.com.healing.R;
import com.healthy.com.healing.TrackApplication;
import com.healthy.com.healing.util.StatusBarUtil;
import com.steelkiwi.cropiwa.CropIwaView;
import com.steelkiwi.cropiwa.config.CropIwaSaveConfig;

import java.io.File;

public class Crop extends BaseActivity {

    private View Crop_Layout;
    private CropIwaSaveConfig.Builder saveConfig;
    private static final String EXTRA_URI = "https://pp.vk.me/c637119/v637119751/248d1/6dd4IPXWwzI.jpg";
    private CropIwaView cropView;
    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        initView();
        //计算状态栏高度，加到View上
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        Crop_Layout.setPadding(0,statusBarHeight,0,0);
    }

    private void initView() {
        Crop_Layout = findViewById(R.id.Crop_Layout);
//        saveConfig = new CropIwaSaveConfig.Builder(Uri.fromFile(new File(
//                TrackApplication.getInstance().getFilesDir(),
//                System.currentTimeMillis() + ".png")));
        Uri imageUri = getIntent().getParcelableExtra(EXTRA_URI);
        cropView = (CropIwaView) findViewById(R.id.crop_view);
        cropView.setImageUri(imageUri);
        saveConfig = new CropIwaSaveConfig.Builder(Uri.fromFile(new File(
                TrackApplication.getInstance().getFilesDir(),
                System.currentTimeMillis() + ".png")));
        toolbar = findViewById(R.id.tb_crop);
        toolbar.setTitle("头像");
        setSupportActionBar(toolbar);
    }

    public static Intent callingIntent(Context context, Uri imageUri) {
        Intent intent = new Intent(context, Crop.class);
        intent.putExtra(EXTRA_URI, imageUri);
        return intent;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_crop,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.done:
                cropView.crop(saveConfig.build());
                this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
