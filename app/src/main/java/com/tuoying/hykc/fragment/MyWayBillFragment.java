package com.tuoying.hykc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.MainActivity;
import com.tuoying.hykc.adapter.MyFragmentPagerAdapter;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/26.
 */

public class MyWayBillFragment extends BaseFragment {
    private TabLayout mTabLayout;
    private ViewPager mViewPager;
    private List<Fragment> fragmentList;
    private List<String> tabTitles;
    private MyFragmentPagerAdapter mAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_my_waybill,null);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

    }

    @Override
    public void init(View view) {
        mTabLayout = (TabLayout) view.findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) view.findViewById(R.id.viewPager);
        initDatas();
    }

    public void initDatas(){
        fragmentList = new ArrayList<Fragment>();
        fragmentList.add(WWCFragment.getInstance());
        fragmentList.add(YWCFragment.getInstance());
        fragmentList.add(DPJFragment.getInstance());
        fragmentList.add(XSZFragment.getInstance());
        tabTitles = new ArrayList<String>();
        tabTitles.add("未完成");
        tabTitles.add("已完成");
        tabTitles.add("待评价");
        tabTitles.add("协商中");
        mTabLayout.setTabMode(TabLayout.MODE_FIXED);
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitles.get(0)));
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitles.get(1)));
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitles.get(2)));
        mTabLayout.addTab(mTabLayout.newTab().setText(tabTitles.get(3)));
        mAdapter = new MyFragmentPagerAdapter(getChildFragmentManager(), fragmentList, tabTitles);
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
    }

    public static MyWayBillFragment getInstance(){
        return new MyWayBillFragment();
    }
}
