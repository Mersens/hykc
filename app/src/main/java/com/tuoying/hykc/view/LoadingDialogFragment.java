package com.tuoying.hykc.view;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.TextView;

import com.tuoying.hykc.R;

/**
 * Created by Administrator on 2018/3/30.
 */

public class LoadingDialogFragment extends DialogFragment {
    private TextView mTextTips;
    private String tips;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        return inflater.inflate(R.layout.layout_loading,null);
    }

    public static LoadingDialogFragment getInstance(){

        return getInstance("");
    }

    public static LoadingDialogFragment getInstance(String msg){
        LoadingDialogFragment fragment=  new LoadingDialogFragment();
        Bundle bundle=new Bundle();
        bundle.putString("tips",msg);
        fragment.setArguments(bundle);
        return fragment;
    }

    public void showF(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        tips=getArguments().getString("tips");
        initView(view);
    }

    private void initView(View view) {
        mTextTips=view.findViewById(R.id.tv_tips);
        if(!TextUtils.isEmpty(tips)){
            mTextTips.setText(tips);
        }

    }
}
