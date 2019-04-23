package com.healthy.com.healing.fragment;

import android.support.v4.app.Fragment;

public class LazyFragmnet extends Fragment {
    private static final String TAG = "LazyFragment";

    //Fragment的View加载完毕的标记
    private boolean isViewCreated;

    //Fragment对用户可见的标记
    private boolean isUIVisible;
}
