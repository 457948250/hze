package com.healthy.com.healing.ui.BaseMine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;

import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.model.LatLng;
import com.baidu.trace.api.track.DistanceRequest;
import com.baidu.trace.api.track.DistanceResponse;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.SupplementMode;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.CoordType;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.SortType;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TransportMode;
import com.healthy.com.healing.BaseActivity;
import com.healthy.com.healing.R;
import com.healthy.com.healing.TrackApplication;
import com.healthy.com.healing.util.CommonUtil;
import com.healthy.com.healing.util.Constants;
import com.healthy.com.healing.util.MapUtil;
import com.healthy.com.healing.util.StatusBarUtil;
import com.healthy.com.healing.util.ViewUtil;

import java.util.ArrayList;
import java.util.List;

public class item_TrackQuery extends BaseActivity implements View.OnClickListener{

    private View Track_Query_Layout;
    private Intent intent;
    private ImageView IV_track_back;
    private TrackApplication trackApp = null;
    private ViewUtil viewUtil = null;

    /**
     * 地图工具
     */
    private MapUtil mapUtil = null;

    /**
     * 历史轨迹请求
     */
    private HistoryTrackRequest historyTrackRequest = new HistoryTrackRequest();

    /**
     * 轨迹监听器（用于接收历史轨迹回调）
     */
    private OnTrackListener mTrackListener = null;

    /**
     * 查询轨迹的开始时间
     */
    private long startTime = CommonUtil.getCurrentTime();

    /**
     * 查询轨迹的结束时间
     */
    private long endTime = CommonUtil.getCurrentTime();

    /**
     * 轨迹点集合
     */
    private List<LatLng> trackPoints = new ArrayList<>();

    /**
     * 轨迹排序规则
     */
    private SortType sortType = SortType.asc;

    private int pageIndex = 1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trackquery);
        ActionBar actionBar = getSupportActionBar();//隐藏标题栏
        if (actionBar != null){
            actionBar.hide();
        }
        trackApp = (TrackApplication) getApplicationContext();
        init();

        //计算状态栏高度，加到View上
        int statusBarHeight = StatusBarUtil.getStatusBarHeight(this);
        Track_Query_Layout.setPadding(0,statusBarHeight,0,0);
    }

    private void init() {
        Track_Query_Layout = findViewById(R.id.Track_Query_Layout);
        intent = getIntent();
        IV_track_back = findViewById(R.id.IV_track_back);
        IV_track_back.setOnClickListener(this);
        viewUtil = new ViewUtil();
        mapUtil = MapUtil.getInstance();
        mapUtil.init((MapView) findViewById(R.id.MV_track_query));
        startTime = intent.getLongExtra("starttime",0);
        endTime = intent.getLongExtra("endtime",0);
        System.out.println("startime:"+startTime+"endtime:"+endTime);
        initListener();
        queryHistoryTrack();
    }

    private void initListener() {
        mTrackListener = new OnTrackListener() {
            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse response) {
                try {

                    int total = response.getTotal();
                    if (StatusCodes.SUCCESS != response.getStatus()) {
                        viewUtil.showToast(item_TrackQuery.this, response.getMessage());
                    } else if (0 == total) {
                        viewUtil.showToast(item_TrackQuery.this, "未查询到轨迹");
                    } else {
                        List<TrackPoint> points = response.getTrackPoints();
                        if (null != points) {
                            for (TrackPoint trackPoint : points) {
                                if (!CommonUtil.isZeroPoint(trackPoint.getLocation().getLatitude(),
                                        trackPoint.getLocation().getLongitude())) {
                                    trackPoints.add(MapUtil.convertTrace2Map(trackPoint.getLocation()));

                                }
                            }
                        }
                    }

                    //查找下一页数据
                    if (total > Constants.PAGE_SIZE * pageIndex) {
                        historyTrackRequest.setPageIndex(++pageIndex);
                        queryHistoryTrack();
                    } else {
                        mapUtil.drawHistoryTrack(trackPoints, true, 0);//画轨迹

                    }

                    queryDistance();// 查询里程
                } catch (Exception e) {

                }
            }

            @Override
            public void onDistanceCallback(DistanceResponse response) {
                viewUtil.showToast(item_TrackQuery.this, "里程：" + response.getDistance());
                super.onDistanceCallback(response);
            }

        };
    }

    private void queryDistance() {
        DistanceRequest distanceRequest = new DistanceRequest(trackApp.getTag(), trackApp.serviceId, trackApp.entityName);
        distanceRequest.setStartTime(startTime);// 设置开始时间
        distanceRequest.setEndTime(endTime);// 设置结束时间
        distanceRequest.setProcessed(true);// 纠偏
        ProcessOption processOption = new ProcessOption();// 创建纠偏选项实例
        processOption.setNeedDenoise(true);// 去噪
        //processOption.setNeedMapMatch(true);// 绑路
        processOption.setTransportMode(TransportMode.walking);// 交通方式为步行
        distanceRequest.setProcessOption(processOption);// 设置纠偏选项
        distanceRequest.setSupplementMode(SupplementMode.no_supplement);// 里程填充方式为无
        trackApp.mClient.queryDistance(distanceRequest, mTrackListener);// 查询里程
    }

    /**
     * 轨迹查询设置回调
     *
     * @param historyTrackRequestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int historyTrackRequestCode, int resultCode, Intent data) {
        if (null == data) {
            return;
        }

        trackPoints.clear();
        pageIndex = 1;

        if (data.hasExtra("startTime")) {
            startTime = data.getLongExtra("startTime", CommonUtil.getCurrentTime());
        }
        if (data.hasExtra("endTime")) {
            endTime = data.getLongExtra("endTime", CommonUtil.getCurrentTime());
        }

        ProcessOption processOption = new ProcessOption();
        if (data.hasExtra("radius")) {
            processOption.setRadiusThreshold(data.getIntExtra("radius", Constants.DEFAULT_RADIUS_THRESHOLD));
        }
        processOption.setTransportMode(TransportMode.walking);

        if (data.hasExtra("denoise")) {//去噪
            processOption.setNeedDenoise(data.getBooleanExtra("denoise", true));
        }
        if (data.hasExtra("vacuate")) {//抽稀
            processOption.setNeedVacuate(data.getBooleanExtra("vacuate", true));
        }
        if (data.hasExtra("mapmatch")) {//绑路
            processOption.setNeedMapMatch(data.getBooleanExtra("mapmatch", true));
        }
        historyTrackRequest.setProcessOption(processOption);

        if (data.hasExtra("processed")) {//纠偏
            historyTrackRequest.setProcessed(data.getBooleanExtra("processed", true));
        }

        queryHistoryTrack();
    }

    /**
     * 查询历史轨迹
     */
    private void queryHistoryTrack() {
        ProcessOption processOption = new ProcessOption();
        processOption.setTransportMode(TransportMode.walking);
        trackApp.initRequest(historyTrackRequest);
        historyTrackRequest.setProcessOption(processOption);

        historyTrackRequest.setSupplementMode(SupplementMode.no_supplement);
        historyTrackRequest.setSortType(SortType.asc);
        historyTrackRequest.setCoordTypeOutput(CoordType.bd09ll);
        historyTrackRequest.setEntityName(trackApp.entityName);
        historyTrackRequest.setStartTime(startTime);
        historyTrackRequest.setEndTime(endTime);
        historyTrackRequest.setPageIndex(pageIndex);
        historyTrackRequest.setPageSize(Constants.PAGE_SIZE);
        trackApp.mClient.queryHistoryTrack(historyTrackRequest, mTrackListener);
    }

    @Override
    public void onClick(View v) {
        int item = v.getId();
        switch (item) {
            case R.id.IV_track_back:
                finish();

                break;

                default:
                    break;
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mapUtil.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapUtil.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != trackPoints) {
            trackPoints.clear();
        }

        trackPoints = null;
        mapUtil.clear();
    }
}
