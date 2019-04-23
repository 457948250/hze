package com.healthy.com.healing.ui.BaseExercise;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.LatestPoint;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TraceLocation;
import com.healthy.com.healing.BaseActivity;
import com.healthy.com.healing.R;
import com.healthy.com.healing.TrackApplication;
import com.healthy.com.healing.litepal.SportRecordList;
import com.healthy.com.healing.model.CurrentLocation;
import com.healthy.com.healing.ui.BaseMine.Login;
import com.healthy.com.healing.util.CommonUtil;
import com.healthy.com.healing.util.Constants;
import com.healthy.com.healing.util.MapUtil;
import com.healthy.com.healing.util.SportRecordUtil;
import com.healthy.com.healing.util.StatusBarUtil;
import com.healthy.com.healing.util.TrackReceiver;
import com.healthy.com.healing.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class Exercise extends BaseActivity implements View.OnClickListener, SensorEventListener {

    private View Exercise_Layout;

    private Intent intent;

    public ProgressDialog progressDialog;

    public static Activity mActivity;

    private TrackApplication trackApp = null;

    private ViewUtil viewUtil = null;

    private Button BT_trace = null;

    private Button BT_gather = null;

    private PowerManager powerManager = null;

    private PowerManager.WakeLock wakeLock = null;

    private TrackReceiver trackReceiver = null;

    private SensorManager mSensorManager;

    private Double lastX = 0.0;
    private int mCurrentDirection = 0;

    /**
     * 地图工具
     */
    private MapUtil mapUtil = null;

    /**
     * 轨迹服务监听器
     */
    private OnTraceListener traceListener = null;

    /**
     * 轨迹监听器(用于接收纠偏后实时位置回调)
     */
    private OnTrackListener trackListener = null;

    /**
     * Entity监听器(用于接收实时定位回调)
     */
    private OnEntityListener entityListener = null;

    /**
     * 实时定位任务
     */
    private RealTimeHandler realTimeHandler = new RealTimeHandler();

    private RealTimeLocRunnable realTimeLocRunnable = null;

    /**
     * 打包周期
     */
    public int packInterval = Constants.DEFAULT_PACK_INTERVAL;

    /**
     * 轨迹点集合
     */
    private List<LatLng> trackPoints;

    private boolean firstLocate = true;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.exercise);
        mActivity = this;
        ActionBar actionBar = getSupportActionBar();//隐藏标题栏
        if (actionBar != null){
            actionBar.hide();
        }
        init();
        //计算状态栏高度，加到View上
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        Exercise_Layout.setPadding(0,statusBarHeight,0,0);
    }

    private void init() {
        Exercise_Layout = findViewById(R.id.Exercise_Layout);
        initListener();
        progressDialog = new ProgressDialog(Exercise.this);
        progressDialog.setTitle("获取起点");
        progressDialog.setMessage("起点获取中，请稍后...");
        progressDialog.setCancelable(true);

        intent = getIntent();
        trackApp = (TrackApplication) getApplicationContext();
        viewUtil = new ViewUtil();
        mapUtil = MapUtil.getInstance();
        mapUtil.init((MapView) findViewById(R.id.bmapView));
        mapUtil.setCenter(mCurrentDirection);//设置地图中心点
        powerManager = (PowerManager) trackApp.getSystemService(Context.POWER_SERVICE);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);// 获取传感器管理服务

        BT_trace = (Button) findViewById(R.id.BT_trace);
        BT_gather = (Button) findViewById(R.id.BT_gather);

        BT_trace.setOnClickListener(this);
        BT_gather.setOnClickListener(this);
        setBT_traceStyle();
        setBT_gatherStyle();

        trackPoints = new ArrayList<>();
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        //每次方向改变，重新给地图设置定位数据，用上一次的经纬度
        double x = sensorEvent.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {// 方向改变大于1度才设置，以免地图上的箭头转动过于频繁
            mCurrentDirection = (int) x;
            if (!CommonUtil.isZeroPoint(CurrentLocation.latitude, CurrentLocation.longitude)) {
                mapUtil.updateMapLocation(new LatLng(CurrentLocation.latitude, CurrentLocation.longitude), (float) mCurrentDirection);
            }
        }
        lastX = x;

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    /**
     * 设置服务按钮样式
     */
    private void setBT_traceStyle() {
        boolean isTraceStarted = trackApp.trackConf.getBoolean("is_trace_started", false);
        if (isTraceStarted) {
            BT_trace.setText("停止服务");
        } else {
            BT_trace.setText("开启服务");
        }
    }

    /**
     * 设置采集按钮样式
     */
    private void setBT_gatherStyle() {
        boolean isGatherStarted = trackApp.trackConf.getBoolean("is_gather_started", false);
        if (isGatherStarted) {
            BT_gather.setText("停止采集");
        } else {
            BT_gather.setText("开始采集");
        }
    }

    /**
     * 实时定位任务
     */
    class RealTimeLocRunnable implements Runnable {

        private int interval = 0;

        public RealTimeLocRunnable(int interval) {
            this.interval = interval;
        }

        @Override
        public void run() {
            trackApp.getCurrentLocation(entityListener, trackListener);
            realTimeHandler.postDelayed(this, interval * 1000);
        }
    }

    static class RealTimeHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    }

    public void startRealTimeLoc(int interval) {
        realTimeLocRunnable = new RealTimeLocRunnable(interval);
        realTimeHandler.post(realTimeLocRunnable);
    }

    public void stopRealTimeLoc() {
        if (null != realTimeHandler && null != realTimeLocRunnable) {
            realTimeHandler.removeCallbacks(realTimeLocRunnable);
        }
        trackApp.mClient.stopRealTimeLoc();
    }

    private void initListener() {

        //轨迹监听器(用于接收纠偏后实时位置回调)
        trackListener = new OnTrackListener() {

            @Override
            public void onLatestPointCallback(LatestPointResponse response) {
                //经过服务端纠偏后的最新的一个位置点，回调
                //将纠偏后实时位置显示在地图MapView上
                //这里位置点的返回间隔时间为数据打包上传的频率；数据发送到服务端，才会更新最新的纠偏位置
                try {
                    if (StatusCodes.SUCCESS != response.getStatus()) {
                        return;
                    }

                    LatestPoint point = response.getLatestPoint();
                    if (null == point || CommonUtil.isZeroPoint(point.getLocation().getLatitude(), point.getLocation()
                            .getLongitude())) {
                        return;
                    }

                    LatLng currentLatLng = mapUtil.convertTrace2Map(point.getLocation());
                    if (null == currentLatLng) {
                        return;
                    }

                    if (firstLocate) {//返回的第一个点是上一次采集的最后一个点，可能和当前位置距离很大，应该弃用
                        firstLocate = false;
                        Toast.makeText(Exercise.this, "起点获取中，请稍后...", Toast.LENGTH_SHORT).show();
                        progressDialog.show();

                        SportRecordList sportRecordList = SportRecordUtil.addSportRecordList(Login.getUser().getUseraccount());
                        Login.setSportRecordList(sportRecordList);
                        Login.getSportRecordList().setSportstyle(intent.getStringExtra("sportstyle"));
                        Login.getSportRecordList().setStarttime(CommonUtil.getCurrentTime());
                        Login.getSportRecordList().save();
                        return;
                    }

                    //当前经纬度
                    CurrentLocation.locTime = point.getLocTime();
                    CurrentLocation.latitude = currentLatLng.latitude;
                    CurrentLocation.longitude = currentLatLng.longitude;

                    if (trackPoints == null) {
                        return;
                    }
                    trackPoints.add(currentLatLng);
                    progressDialog.dismiss();
                    mapUtil.drawHistoryTrack(trackPoints, false, mCurrentDirection);//时时动态的画出运动轨迹

                } catch (Exception x) {

                }
            }
        };

        //Entity监听器(用于接收实时定位回调)
        entityListener = new OnEntityListener() {

            @Override
            public void onReceiveLocation(TraceLocation location) {
                //本地LBSTraceClient客户端获取的位置
                //这里位置点的返回间隔时间为Handler.postDelayed的延时时间
                try {
                    if (StatusCodes.SUCCESS != location.getStatus() || CommonUtil.isZeroPoint(location.getLatitude(),
                            location.getLongitude())) {
                        return;
                    }
                    LatLng currentLatLng = mapUtil.convertTraceLocation2Map(location);
                    if (null == currentLatLng) {
                        return;
                    }
                    CurrentLocation.locTime = CommonUtil.toTimeStamp(location.getTime());
                    CurrentLocation.latitude = currentLatLng.latitude;
                    CurrentLocation.longitude = currentLatLng.longitude;

                    if (null != mapUtil) {
                        mapUtil.updateMapLocation(currentLatLng, mCurrentDirection);//显示当前位置
                        mapUtil.animateMapStatus(currentLatLng);//缩放
                    }

                } catch (Exception x) {

                }
            }
        };

        //轨迹服务监听器
        traceListener = new OnTraceListener() {
            /**
             * 绑定com.baidu.trace.LBSTraceService服务回调接口
             * @param errorNo  状态码,0：成功,1：失败
             * @param message 消息
             */
            @Override
            public void onBindServiceCallback(int errorNo, String message) {
                viewUtil.showToast(Exercise.this,
                        String.format("onBindServiceCallback, errorNo:%d, message:%s ", errorNo, message));
            }
            /**
             * 开启服务回调接口
             * @param errorNo 状态码
             * 0：成功,10000：请求发送失败,10001：服务开启失败,10002：参数错误,10003：网络连接失败
            10004：网络未开启,10005：服务正在开启,10006：服务已开启
             * @param message 消息
             */
            @Override
            public void onStartTraceCallback(int errorNo, String message) {
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.START_TRACE_NETWORK_CONNECT_FAILED <= errorNo) {
                    trackApp.isTraceStarted = true;
                    SharedPreferences.Editor editor = trackApp.trackConf.edit();
                    editor.putBoolean("is_trace_started", true);
                    editor.apply();
                    setBT_traceStyle();
                    registerReceiver();
                }
                viewUtil.showToast(Exercise.this,
                        String.format("onStartTraceCallback, errorNo:%d, message:%s ", errorNo, message));
            }
            /**
             * 停止服务回调接口
             * @param errorNo 状态码
             * 0：成功,11000：请求发送失败,11001：服务停止失败,11002：服务未开启,11003：服务正在停止
             * @param message 消息
             */
            @Override
            public void onStopTraceCallback(int errorNo, String message) {
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.CACHE_TRACK_NOT_UPLOAD == errorNo) {
                    trackApp.isTraceStarted = false;
                    trackApp.isGatherStarted = false;
                    // 停止成功后，直接移除is_trace_started记录（便于区分用户没有停止服务，直接杀死进程的情况）
                    SharedPreferences.Editor editor = trackApp.trackConf.edit();
                    editor.remove("is_trace_started");
                    editor.remove("is_gather_started");
                    editor.apply();
                    setBT_traceStyle();
                    setBT_gatherStyle();
                    unregisterPowerReceiver();
                    firstLocate = true;
                }
                viewUtil.showToast(Exercise.this,
                        String.format("onStopTraceCallback, errorNo:%d, message:%s ", errorNo, message));
            }
            /**
             * 开启采集回调接口
             * @param errorNo 状态码
             * 0：成功,12000：请求发送失败,12001：采集开启失败,12002：服务未开启
             * @param message 消息
             */
            @Override
            public void onStartGatherCallback(int errorNo, String message) {
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.GATHER_STARTED == errorNo) {
                    trackApp.isGatherStarted = true;
                    SharedPreferences.Editor editor = trackApp.trackConf.edit();
                    editor.putBoolean("is_gather_started", true);
                    editor.apply();
                    setBT_gatherStyle();

                    stopRealTimeLoc();
                    startRealTimeLoc(packInterval);
                }
                viewUtil.showToast(Exercise.this,
                        String.format("onStartGatherCallback, errorNo:%d, message:%s ", errorNo, message));
            }
            /**
             * 停止采集回调接口
             * @param errorNo 状态码
             * 0：成功,13000：请求发送失败,13001：采集停止失败,13002：服务未开启
             * @param message 消息
             */
            @Override
            public void onStopGatherCallback(int errorNo, String message) {
                if (StatusCodes.SUCCESS == errorNo || StatusCodes.GATHER_STOPPED == errorNo) {
                    trackApp.isGatherStarted = false;
                    SharedPreferences.Editor editor = trackApp.trackConf.edit();
                    editor.remove("is_gather_started");
                    editor.apply();
                    setBT_gatherStyle();

                    firstLocate = true;
                    stopRealTimeLoc();
                    startRealTimeLoc(Constants.LOC_INTERVAL);

                    if (trackPoints.size() >= 1) {
                        try {
                            mapUtil.drawEndPoint(trackPoints.get(trackPoints.size() - 1));
                        } catch (Exception e) {

                        }

                    }

                }
                viewUtil.showToast(Exercise.this,
                        String.format("onStopGatherCallback, errorNo:%d, message:%s ", errorNo, message));
            }

            @Override
            public void onPushCallback(byte messageType, PushMessage pushMessage) {

            }

            @Override
            public void onInitBOSCallback(int i, String s) {

            }
        };

    }



    /**
     * 注册广播（电源锁、GPS状态）
     */
    @SuppressLint("InvalidWakeLockTag")
    private void registerReceiver() {
        if (trackApp.isRegisterReceiver) {
            return;
        }

        if (null == wakeLock) {
            wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "track upload");
        }
        if (null == trackReceiver) {
            trackReceiver = new TrackReceiver(wakeLock);
        }

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_USER_PRESENT);
        filter.addAction(StatusCodes.GPS_STATUS_ACTION);
        trackApp.registerReceiver(trackReceiver, filter);
        trackApp.isRegisterReceiver = true;

    }

    private void unregisterPowerReceiver() {
        if (!trackApp.isRegisterReceiver) {
            return;
        }
        if (null != trackReceiver) {
            trackApp.unregisterReceiver(trackReceiver);
        }
        trackApp.isRegisterReceiver = false;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (trackApp.trackConf.contains("is_trace_started")
                && trackApp.trackConf.contains("is_gather_started")
                && trackApp.trackConf.getBoolean("is_trace_started", false)
                && trackApp.trackConf.getBoolean("is_gather_started", false)) {
            startRealTimeLoc(packInterval);
        } else {
            startRealTimeLoc(Constants.LOC_INTERVAL);
        }

    }


    @Override
    public void onResume() {
        super.onResume();
        mapUtil.onResume();

        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION), SensorManager.SENSOR_DELAY_UI);

        // 在Android 6.0及以上系统，若定制手机使用到doze模式，请求将应用添加到白名单。
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String packageName = trackApp.getPackageName();
            boolean isIgnoring = powerManager.isIgnoringBatteryOptimizations(packageName);
            if (!isIgnoring) {
                Intent intent = new Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS);
                intent.setData(Uri.parse("package:" + packageName));
                try {
                    startActivity(intent);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        mapUtil.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopRealTimeLoc();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopRealTimeLoc();
        trackPoints.clear();
        trackPoints = null;
        mapUtil.clear();
    }

    @Override
    public void onClick(View v) {
        int item = v.getId();
        switch (item) {
            case R.id.BT_trace:
                if (trackApp.isTraceStarted) {
                    trackApp.mClient.stopTrace(trackApp.mTrace, traceListener);//停止服务
                } else {
                    trackApp.mClient.startTrace(trackApp.mTrace, traceListener);//开始服务
                }
                break;

            case R.id.BT_gather:
                if (trackApp.isGatherStarted) {
                    trackApp.mClient.stopGather(traceListener);
                    Login.getSportRecordList().setEndtime(CommonUtil.getCurrentTime());  //记录停止采集时间
                    Login.getSportRecordList().save();
                    Intent intent = new Intent("com.healthy.com.healing.TRACKQUERY");
                    startActivity(intent);

                } else {
                    /**
                     * 设置采集频率：这里的采集频率指的是轨迹数据的采集频率，和上面显示当前位置的定位频率要区分开
                     最小为2秒，最大为5分钟，否则设置不成功，默认值为5s
                     * 打包上传频率：mClient每隔packInterval时间会自动打包上传
                     * 打包时间间隔必须为采集时间间隔的整数倍，且最大不能超过5分钟，否则设置不成功，默认为30s
                     */
                    trackApp.mClient.setInterval(Constants.DEFAULT_GATHER_INTERVAL, packInterval);
                    trackApp.mClient.startGather(traceListener);//开启采集
                }
                break;


            default:
                break;
        }
    }
}
