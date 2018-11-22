package com.tuoying.hykc.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.tuoying.hykc.R;
import com.tuoying.hykc.fragment.GuideFragment;

/**
 * Created by Administrator on 2018/3/22.
 */

public class GuideActivity extends FragmentActivity {
    private RadioGroup dotLayout;
    private ViewPager viewPager;
    private PageFragmentAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_guide);
        init();
    }

    private void init() {
        dotLayout = (RadioGroup) findViewById(R.id.advertise_point_group);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        adapter = new PageFragmentAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new MyPagerChangeListener());
    }
    public class MyPagerChangeListener implements ViewPager.OnPageChangeListener {

        public void onPageSelected(int position) {


        }

        public void onPageScrollStateChanged(int arg0) {

        }

        public void onPageScrolled(int position, float positionOffset,
                                   int positionOffsetPixels) {
            ((RadioButton) dotLayout.getChildAt(position)).setChecked(true);
            if(position==3){
                dotLayout.setVisibility(View.GONE);
            }else{
                dotLayout.setVisibility(View.VISIBLE);
            }
        }
    }
    class PageFragmentAdapter extends FragmentPagerAdapter {
        public PageFragmentAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int idx) {
            return GuideFragment.getInstance(idx);
        }

        @Override
        public int getCount() {
            return GuideFragment.imags.length;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }
}
