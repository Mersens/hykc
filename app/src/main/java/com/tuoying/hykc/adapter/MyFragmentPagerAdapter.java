package com.tuoying.hykc.adapter;


import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

/**
 * Created by zzu on 2016/4/11.
 */
public class MyFragmentPagerAdapter extends PagerAdapter {
    private List<Fragment> fragmentList;
    private List<String> tabTitles;
    private FragmentManager fm;
    public MyFragmentPagerAdapter(FragmentManager fm, List<Fragment> fragmentList, List<String> tabTitles){
        this.fm=fm;
        this.fragmentList=fragmentList;
        this.tabTitles=tabTitles;
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        Fragment fragment = fragmentList.get(position);
        if (!fragment.isAdded()) {
            FragmentTransaction transaction = fm.beginTransaction();
            transaction.add(fragment, fragment.getClass().getSimpleName());
            transaction.commitAllowingStateLoss();
            fm.executePendingTransactions();
        }
        if (fragment.getView().getParent() == null) {
            container.addView(fragment.getView());
        }
        return fragment.getView();

    }



    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(fragmentList.get(position).getView());
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return view==object;
    }

    @Override
    public int getCount() {
        return tabTitles.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {

        return tabTitles.get(position % tabTitles.size());
    }
}
