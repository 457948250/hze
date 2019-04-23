package com.healthy.com.healing.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.healthy.com.healing.R;
import com.healthy.com.healing.ui.BaseMine.Login;
import com.healthy.com.healing.ui.BaseMine.SportRecord;
import com.healthy.com.healing.adapter.item_SportRecord;

import java.util.List;

public class fragment_run extends Fragment implements View.OnClickListener{

    private Button bt_run_start;

    private TextView tv_run_distance;

    private View view;

    private SportRecord sportRecord;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_run, container, false);
        }
        initView();
        initData();
        return view;
    }

    private void initView() {
        bt_run_start = view.findViewById(R.id.bt_run_start);
        tv_run_distance = view.findViewById(R.id.tv_run_distance);
        bt_run_start.setOnClickListener(this);
    }

    private void initData() {
        sportRecord = new SportRecord();
        if(Login.getisLogin()) {
            double distance = 0;
            sportRecord.initSportRecord_all();
            sportRecord.initSportRecord_run();
            List<item_SportRecord> sportRecordLists = sportRecord.sportRecord_run;
            for (int i = 0; i < sportRecordLists.size(); i++) {
                String s = sportRecordLists.get(i).getDistance();
                s = s.replace("公里","");

                distance = distance + Double.valueOf(s);
            }
            Log.i("---------daistance",String.format("%.2f", distance));
            tv_run_distance.setText(String.format("%.2f", distance));
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bt_run_start:
                Intent intent = new Intent("com.healthy.com.healing.EXERCISE");
                intent.putExtra("sportstyle","run");
                startActivity(intent);
                break;
        }
    }
}
