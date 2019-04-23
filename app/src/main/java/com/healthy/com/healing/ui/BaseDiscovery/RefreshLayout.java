package com.healthy.com.healing.ui.BaseDiscovery;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.MotionEvent;
/**
 * 解决SwipeRefreshLayout和banner轮播图事件冲突
 */
public class RefreshLayout extends SwipeRefreshLayout {
    float lastx = 0;
    float lasty = 0;
    boolean ismovepic = false;

    public RefreshLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        if (ev.getAction() ==MotionEvent.ACTION_DOWN){
            lastx = ev.getX();
            lasty = ev.getY();
            ismovepic = false;
            return super.onInterceptTouchEvent(ev);
        }
        final int action = MotionEventCompat.getActionMasked(ev);
        int x2 = (int) Math.abs(ev.getX() - lastx);
        int y2 = (int) Math.abs(ev.getY() - lasty);
        //滑动图片最小距离检查
        if (x2>y2){
            if (x2>=100)ismovepic = true;
            return false;
        }
        //是否移动图片(下拉刷新不处理)
        if (ismovepic){
            return false;
        }
        boolean isok = super.onInterceptTouchEvent(ev);
        return isok;//最后返回的mIsBeingDragged就是返回觉得是否拦截事件，我们就在这个方法入手，我们先判断，如果x坐标变化大于y坐标的变化，那么我们就认为这个是左右滑动的，下拉刷新不处理。
    }
}
