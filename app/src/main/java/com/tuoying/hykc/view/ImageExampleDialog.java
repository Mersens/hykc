package com.tuoying.hykc.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuoying.hykc.R;


/**
 * Created by Administrator on 2018/3/30.
 */

public class ImageExampleDialog extends DialogFragment {
    private ImageView mImageClose;
    private ImageView mImg;
    private TextView mTextTitle;
    private TextView mTextMsg;
    private int type=-1;

    public static ImageExampleDialog getInstance(int type){
        ImageExampleDialog fragment=new ImageExampleDialog();
        Bundle bundle=new Bundle();
        bundle.putInt("type",type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        return inflater.inflate(R.layout.layout_img_example,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        type=getArguments().getInt("type");

        mImageClose=view.findViewById(R.id.img_close);
        mImageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mImg=view.findViewById(R.id.img_example);
        mTextTitle=view.findViewById(R.id.tv_title);
        mTextMsg=view.findViewById(R.id.tv_tips);

        if(type==1){
            mTextTitle.setText("卸货照");
            mTextMsg.setText(R.string.xhz_tips);
            mImg.setImageResource(R.drawable.img_unload);

        }else if(type==2){
            mTextTitle.setText("回单照");
            mTextMsg.setText(R.string.hdz_tips);
            mImg.setImageResource(R.drawable.img_hdz);
        }

    }

    public void showF(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }
}
