package com.healthy.com.healing.ui.BaseMine;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.healthy.com.healing.BaseActivity;
import com.healthy.com.healing.R;
import com.healthy.com.healing.util.StatusBarUtil;

public class Personal_Data extends BaseActivity implements View.OnClickListener{

    private LinearLayout Personal_Data_Layout;
    private ImageView iv_back_personal_data;
    private ImageView iv_save_personal_data;
    private RelativeLayout rl_height;
    private RelativeLayout rl_weight;
    private RelativeLayout rl_blood_pressure;
    private RelativeLayout rl_blood_glucose;
    private RelativeLayout rl_step_number;
    private RelativeLayout rl_distance;
    private RelativeLayout rl_heat;

    private TextView tv_preview_height;
    private TextView tv_preview_weight;
    private TextView tv_preview_blood_pressure;
    private TextView tv_preview_blood_glucose;
    private TextView tv_preview_step_number;
    private TextView tv_preview_distance;
    private TextView tv_preview_heat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_data);
        initView();

        //计算状态栏高度，加到View上
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        Personal_Data_Layout.setPadding(0,statusBarHeight,0,0);
    }

    private void initView() {
        Personal_Data_Layout = findViewById(R.id.Personal_Data_Layout);
        iv_back_personal_data = findViewById(R.id.iv_back_personal_data);
        iv_back_personal_data.setOnClickListener(this);
        iv_save_personal_data = findViewById(R.id.iv_save_personal_data);
        iv_save_personal_data.setOnClickListener(this);
        rl_height = findViewById(R.id.rl_height);
        rl_height.setOnClickListener(this);
        rl_weight = findViewById(R.id.rl_weight);
        rl_weight.setOnClickListener(this);
        rl_blood_pressure = findViewById(R.id.rl_blood_pressure);
        rl_blood_pressure.setOnClickListener(this);
        rl_blood_glucose = findViewById(R.id.rl_blood_glucose);
        rl_blood_glucose.setOnClickListener(this);
        rl_step_number = findViewById(R.id.rl_step_number);
        rl_step_number.setOnClickListener(this);
        rl_distance = findViewById(R.id.rl_distance);
        rl_distance.setOnClickListener(this);
        rl_heat = findViewById(R.id.rl_heat);
        rl_heat.setOnClickListener(this);

        tv_preview_height = findViewById(R.id.tv_preview_height);
        tv_preview_weight = findViewById(R.id.tv_preview_weight);
        tv_preview_blood_pressure = findViewById(R.id.tv_preview_blood_pressure);
        tv_preview_blood_glucose = findViewById(R.id.tv_preview_blood_glucose);
        tv_preview_step_number = findViewById(R.id.tv_preview_step_number);
        tv_preview_distance = findViewById(R.id.tv_preview_distance);
        tv_preview_heat = findViewById(R.id.tv_preview_heat);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back_personal_data:
                this.finish();
                break;
            case R.id.iv_save_personal_data:

                break;
            case R.id.rl_height:

                break;
            case R.id.rl_weight:

                break;
            case R.id.rl_blood_pressure:

                break;
            case R.id.rl_blood_glucose:

                break;
            case R.id.rl_step_number:

                break;
            case R.id.rl_distance:

                break;
            case R.id.rl_heat:

                break;
        }
    }
}
