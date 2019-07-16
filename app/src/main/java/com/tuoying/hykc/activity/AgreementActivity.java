package com.tuoying.hykc.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.tuoying.hykc.R;
import com.tuoying.hykc.fragment.CommonFragment;

public class AgreementActivity extends SingleFragmentActivity {


    @Override
    public Fragment getContent() {
        return CommonFragment.getInstance("file:///android_asset/hykc_agreement.html");
    }

    @Override
    public String setTitleText() {
        return "用户协议";
    }
}
