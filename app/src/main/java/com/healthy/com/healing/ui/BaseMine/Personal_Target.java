package com.healthy.com.healing.ui.BaseMine;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.healthy.com.healing.BaseActivity;
import com.healthy.com.healing.R;
import com.healthy.com.healing.litepal.User;
import com.healthy.com.healing.util.StatusBarUtil;


public class Personal_Target extends BaseActivity implements View.OnClickListener {

    private LinearLayout Personal_Target_Layout;
    private SeekBar seekBar_step;
    private SeekBar seekBar_weight;
    private TextView tv_target_step_number;
    private TextView tv_target_weight;
    private TextView tv_small;
    private TextView tv_normal;
    private TextView tv_big;
    private ImageView iv_back_target;
    private ImageView IV_save;
    private int target_step = 2000;
    private int target_weight = 20;
    private User user = Login.getUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_target);
        initView();
        initData();
        //计算状态栏高度，加到View上
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        Personal_Target_Layout.setPadding(0,statusBarHeight,0,0);
    }

    private void initView() {
        Personal_Target_Layout = findViewById(R.id.Personal_Target_Layout);
        seekBar_step = findViewById(R.id.sb_target_step_number);
        seekBar_weight = findViewById(R.id.sb_target_weight);
        tv_target_step_number = findViewById(R.id.tv_target_step_number);
        tv_target_weight = findViewById(R.id.tv_target_weight);
        tv_small = findViewById(R.id.tv_small);
        tv_normal = findViewById(R.id.tv_normal);
        tv_big = findViewById(R.id.tv_big);
        iv_back_target = findViewById(R.id.iv_back_target);
        iv_back_target.setOnClickListener(this);
        IV_save = findViewById(R.id.IV_save);
        IV_save.setOnClickListener(this);
    }

    private void initData() {
        seekBar_step.setMax(18);
        if(user.getStep() == 0){
            tv_target_step_number.setText(2+"");
            seekBar_step.setProgress(0);
            Changecolor(0);
        }else{
            tv_target_step_number.setText(user.getStep()/1000+"");
            seekBar_step.setProgress(user.getStep()/1000-2);
            Changecolor(user.getStep()/1000-2);
        }
        seekBar_step.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_target_step_number.setText(progress+2+"");
                target_step = (progress+2)*1000;
                Changecolor(progress);
//                Log.v("拖动过程中的值：", String.valueOf(progress) + ", " + String.valueOf(fromUser));
            }
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seekBar_weight.setMax(130);
        if (user.getWeight() == 0){
            tv_target_weight.setText(20+"");
            seekBar_weight.setProgress(0);
        }else {
            tv_target_weight.setText(user.getWeight()+"");
            seekBar_weight.setProgress(user.getWeight()-20);
        }
        seekBar_weight.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                tv_target_weight.setText(progress+20+"");
                target_weight = progress+20;
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void Changecolor(int progress) {
        if (progress < 6){
            tv_small.setTextColor(Color.parseColor("#000000"));
            tv_normal.setTextColor(Color.parseColor("#d9d9d9"));
            tv_big.setTextColor(Color.parseColor("#d9d9d9"));
        }else if (progress > 12){
            tv_small.setTextColor(Color.parseColor("#d9d9d9"));
            tv_normal.setTextColor(Color.parseColor("#d9d9d9"));
            tv_big.setTextColor(Color.parseColor("#000000"));
        }else {
            tv_small.setTextColor(Color.parseColor("#d9d9d9"));
            tv_normal.setTextColor(Color.parseColor("#000000"));
            tv_big.setTextColor(Color.parseColor("#d9d9d9"));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back_target:
            this.finish();
            break;
            case R.id.IV_save:
                user.setStep(target_step);
                user.setWeight(target_weight);
                user.save();
                Toast.makeText(this,"保存成功~",Toast.LENGTH_SHORT).show();
        }
    }
}
