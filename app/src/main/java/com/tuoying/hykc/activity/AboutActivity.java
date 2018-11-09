package com.tuoying.hykc.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.tuoying.hykc.fragment.AboutFragment;
import com.tuoying.hykc.fragment.SettingFragment;

public class AboutActivity extends SingleFragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public Fragment getContent() {
        return AboutFragment.getInstance();
    }

    @Override
    public String setTitleText() {
        return "关于我们";
    }
}
