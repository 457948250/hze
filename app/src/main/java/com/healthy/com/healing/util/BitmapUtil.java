package com.healthy.com.healing.util;

import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.healthy.com.healing.R;

public class BitmapUtil {

    public static BitmapDescriptor bmArrowPoint = null;

    public static BitmapDescriptor bmStart = null;

    public static BitmapDescriptor bmEnd = null;

    /**
     * 创建bitmap，在MainActivity onCreate()中调用
     */
    public static void init() {
        bmStart = BitmapDescriptorFactory.fromResource(R.mipmap.icon_start);
        bmEnd = BitmapDescriptorFactory.fromResource(R.mipmap.icon_end);
    }

    /**
     * 回收bitmap，在MainActivity onDestroy()中调用
     */
    public static void clear() {
        bmStart.recycle();
        bmEnd.recycle();
    }
}
