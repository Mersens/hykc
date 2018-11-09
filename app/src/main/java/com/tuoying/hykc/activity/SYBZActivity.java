package com.tuoying.hykc.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.tuoying.hykc.R;
import com.tuoying.hykc.fragment.CommonFragment;
import com.tuoying.hykc.fragment.SettingFragment;

public class SYBZActivity extends SingleFragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public Fragment getContent() {
        String urls[]=getResources().getStringArray(R.array.web_urls);
        return CommonFragment.getInstance(urls[0]);
    }

    @Override
    public String setTitleText() {
        return "使用帮助";
    }
}
