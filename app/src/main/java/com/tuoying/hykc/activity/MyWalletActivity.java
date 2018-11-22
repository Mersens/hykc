package com.tuoying.hykc.activity;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.tuoying.hykc.fragment.MyWalletFragment;

public class MyWalletActivity extends SingleFragmentActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public Fragment getContent() {
        return MyWalletFragment.getInstance();
    }

    @Override
    public String setTitleText() {
        return "我的钱包";
    }
}
