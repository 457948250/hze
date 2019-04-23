package com.healthy.com.healing.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.healthy.com.healing.R;
import com.healthy.com.healing.adapter.myFragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;


public class BaseExercise extends Fragment {

    private View view;
    private TabLayout tabLayout;
    private ViewPager mViewPager;
    private List<Fragment> mFragmentList = new ArrayList<>();
    private String[] titles = new String[]{"跑步","步行","骑行"};
    private myFragmentPagerAdapter adapter;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (view == null) {
            view = inflater.inflate(R.layout.activity_exercise, container, false);
        }
        tabLayout = view.findViewById(R.id.tab_layout);
        mViewPager = view.findViewById(R.id.base_exercise_pager);
        initData();

        setListener();
        return view;
    }

    private void initData() {
        // 将我们自定义 Fragment 的对象添加到 List<BaseFragment> 中。
        mFragmentList.add(new fragment_run());
        mFragmentList.add(new fragment_walk());
        mFragmentList.add(new fragment_bike());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.addTab(tabLayout.newTab());
        tabLayout.setupWithViewPager(mViewPager,false);
        // 新建适配器
        adapter = new myFragmentPagerAdapter(getChildFragmentManager(), mFragmentList);
        // 为 ViewPager 设置适配器
        mViewPager.setAdapter(adapter);
        tabLayout.getTabAt(0).setText(titles[0]);
        tabLayout.getTabAt(1).setText(titles[1]);
        tabLayout.getTabAt(2).setText(titles[2]);
        // 打开应用时 ViewPager 显示第一个 Fragment
        mViewPager.setCurrentItem(0);
    }
    private void setListener() {

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
    }
}