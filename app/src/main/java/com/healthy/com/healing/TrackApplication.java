package com.healthy.com.healing;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.LocRequest;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.LatestPointRequest;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.BaseRequest;
import com.baidu.trace.model.OnCustomAttributeListener;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.TransportMode;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.healthy.com.healing.util.CommonUtil;
import com.healthy.com.healing.util.NetUtil;


import org.litepal.LitePalApplication;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by zhh
 */

public class TrackApplication extends Application {

    private static TrackApplication mInstance;

    private static List<Activity> activityList = new LinkedList<Activity>();

    private AtomicInteger mSequenceGenerator = new AtomicInteger();

    private LocRequest locRequest = null;

    public Context mContext = null;

    public SharedPreferences trackConf = null;

    /**
     * 轨迹客户端
     */
    public LBSTraceClient mClient = null;

    /**
     * 轨迹服务
     */
    public Trace mTrace = null;

    /**
     * 轨迹服务ID
     */
    public long serviceId = 210001;//这里是申请的鹰眼服务id

    /**
     * Entity标识
     */
    public String entityName = "myTrace_1";

    public boolean isRegisterReceiver = false;

    /**
     * 服务是否开启标识
     */
    public boolean isTraceStarted = false;

    /**
     * 采集是否开启标识
     */
    public boolean isGatherStarted = false;

    public static int screenWidth = 0;

    public static int screenHeight = 0;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        mContext = getApplicationContext();
        LitePalApplication.initialize(mContext);
        entityName = CommonUtil.getImei(this);

        // 若为创建独立进程，则不初始化成员变量
        if ("com.baidu.track:remote".equals(CommonUtil.getCurProcessName(mContext))) {
            return;
        }
        Fresco.initialize(this);
        SDKInitializer.initialize(mContext);
        getScreenSize();
        mClient = new LBSTraceClient(mContext);
        mTrace = new Trace(serviceId, entityName);

        trackConf = getSharedPreferences("track_conf", MODE_PRIVATE);
        //定位请求参数类
        locRequest = new LocRequest(serviceId);

        mClient.setOnCustomAttributeListener(new OnCustomAttributeListener() {
            @Override
            public Map<String, String> onTrackAttributeCallback() {
                Map<String, String> map = new HashMap<>();
                map.put("key1", "value1");
                map.put("key2", "value2");
                return map;
            }

            @Override
            public Map<String, String> onTrackAttributeCallback(long l) {
                return null;
            }
        });

        clearTraceStatus();
    }

    /**
     * 获取当前位置
     */
    public void getCurrentLocation(OnEntityListener entityListener, OnTrackListener trackListener) {
        // 网络连接正常，开启服务及采集，则查询纠偏后实时位置；否则进行实时定位
        if (NetUtil.isNetworkAvailable(mContext)
                && trackConf.contains("is_trace_started")
                && trackConf.contains("is_gather_started")
                && trackConf.getBoolean("is_trace_started", false)
                && trackConf.getBoolean("is_gather_started", false)) {
            LatestPointRequest request = new LatestPointRequest(getTag(), serviceId, entityName);
            ProcessOption processOption = new ProcessOption();// 创建纠偏选项实例
            processOption.setRadiusThreshold(50);// 设置精度过滤值(定位精度大于50米的过滤掉)
            processOption.setTransportMode(TransportMode.walking);// 设置交通方式为步行
            processOption.setNeedDenoise(true);// 设置需要去噪
            //processOption.setNeedMapMatch(true);// 设置需要绑路
            request.setProcessOption(processOption);//设置参数
            mClient.queryLatestPoint(request, trackListener);//请求纠偏后的最新点
        } else {
            //实时定位（优先使用GPS定位服务） 使用要点: 用于定位设备当前位置，定位信息不会存储在轨迹服务端，即不会形成轨迹信息,只用于在MapView显示当前位置
            mClient.queryRealTimeLoc(locRequest, entityListener);//多次定位使Handler.postDelayed(Runnable, interval)实现;
        }
    }

    /**
     * 获取屏幕尺寸
     */
    private void getScreenSize() {
        DisplayMetrics dm = getResources().getDisplayMetrics();
        screenHeight = dm.heightPixels;
        screenWidth = dm.widthPixels;
    }

    /**
     * 清除Trace状态：初始化app时，判断上次是正常停止服务还是强制杀死进程，根据trackConf中是否有is_trace_started字段进行判断。
     *
     * 停止服务成功后，会将该字段清除；若未清除，表明为非正常停止服务。
     */
    private void clearTraceStatus() {
        if (trackConf.contains("is_trace_started") || trackConf.contains("is_gather_started")) {
            SharedPreferences.Editor editor = trackConf.edit();
            editor.remove("is_trace_started");
            editor.remove("is_gather_started");
            editor.apply();
        }
    }

    /**
     * 初始化请求公共参数
     *
     * @param request
     */
    public void initRequest(BaseRequest request) {
        request.setTag(getTag());//设置请求标识，用于唯一标记本次请求，在响应结果中会返回该标识
        request.setServiceId(serviceId);
    }

    /**
     * 获取请求标识
     *
     * @return
     */
    public int getTag() {
        return mSequenceGenerator.incrementAndGet();
    }

    public static TrackApplication getInstance() {
        return mInstance;
    }

    public void addActivity(Activity activity)  {
        activityList.add(activity);
    }
    public void removeActivity(Activity activity){
        activityList.remove(activity);
    }
    public void exitAllActivity(){
        for(Activity activity:activityList) {
            if(!activity.isFinishing()) {//这里判断act是否正在销毁，如果没有就调用finish进行销毁
                activity.finish();
            }
        }
    }

}
