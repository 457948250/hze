package com.healthy.com.healing;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TrackApplication.getInstance().addActivity(this);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        TrackApplication.getInstance().removeActivity(this);
    }
}
