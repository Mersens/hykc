package com.tuoying.hykc.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.LoginActivity;
import com.tuoying.hykc.activity.MainActivity;
import com.tuoying.hykc.utils.SharePreferenceUtil;


/**
 * Created by Mersens on 2017/5/12 15:11
 * Email:626168564@qq.com
 */

public class GuideFragment extends BaseFragment {
    private ImageView imageView;
    private int index;
    public static int[] imags = {R.mipmap.bg_guide1, R.mipmap.bg_guide2,
            R.mipmap.bg_guide3, R.mipmap.bg_guide4,};

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_guide, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        index = getArguments().getInt("params");
        init(view);


    }

    public static Fragment getInstance(int params) {
        GuideFragment fragment = new GuideFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("params", params);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void init(View v) {
        imageView = (ImageView) v.findViewById(R.id.imageView);
        imageView.setImageResource(imags[index]);
        if (index == 3) {
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharePreferenceUtil.getInstance(getActivity()).setIsFirst(false);
                    String userid=SharePreferenceUtil.getInstance(getActivity()).getUserId();
                    if(TextUtils.isEmpty(userid)){
                        goLogin();
                    }else {
                        getActivity().startActivity(new Intent(getActivity(), MainActivity.class));
                        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                        getActivity().finish();
                    }
                }
            });
        }
    }
    private void goLogin() {
        startActivity(new Intent(getActivity(), LoginActivity.class).putExtra("tel",""));
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        getActivity().finish();
    }

}
