package com.tuoying.hykc.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.tuoying.hykc.fragment.ContactFragment;
import com.tuoying.hykc.fragment.SettingFragment;

public class LXKFActivity extends SingleFragmentActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public Fragment getContent() {
        return ContactFragment.getInstance();
    }

    @Override
    public String setTitleText() {
        return "联系客服";
    }
}
