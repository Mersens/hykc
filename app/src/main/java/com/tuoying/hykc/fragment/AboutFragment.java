package com.tuoying.hykc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.AboutActivity;
import com.tuoying.hykc.activity.MainActivity;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.utils.APKVersionCodeUtils;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2018/3/26.
 */

public class AboutFragment extends BaseFragment {
    WeakReference<AboutActivity> mActivityReference;
    private TextView mTextCode;

    public static AboutFragment getInstance(){
        return new AboutFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityReference=new WeakReference<>((AboutActivity)context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_about,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void init(View v) {
        mTextCode=v.findViewById(R.id.tv_build_num);
        float apkCode= APKVersionCodeUtils.getVerName(getActivity());
        mTextCode.setText("V"+apkCode+" "+Constants.VERSIONTIME);
    }
}
