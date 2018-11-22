package com.tuoying.hykc.fragment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.LXKFActivity;
import com.tuoying.hykc.activity.MainActivity;
import com.tuoying.hykc.view.ExitDialogFragment;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2018/3/26.
 */

public class ContactFragment extends BaseFragment {
    private static final int MY_PERMISSIONS_REQUEST_CALL_PHONE = 1;
    WeakReference<LXKFActivity> mActivityReference;
    private TextView mTextNum;

    public static ContactFragment getInstance(){
        return new ContactFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityReference=new WeakReference<>((LXKFActivity)context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_contact,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void init(View v) {
        mTextNum=v.findViewById(R.id.tv_service_num);
        mTextNum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    if (ContextCompat.checkSelfPermission(getActivity(),
                            Manifest.permission.CALL_PHONE)
                            != PackageManager.PERMISSION_GRANTED) {

                        ActivityCompat.requestPermissions(getActivity(),
                                new String[]{Manifest.permission.CALL_PHONE},
                                MY_PERMISSIONS_REQUEST_CALL_PHONE);
                    } else {
                        callPhone();
                    }
                }else {
                    callPhone();
                }
            }
        });
    }

    private void callPhone() {
        confirmUserPhone("确定拨打电话？");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
    {

        if (requestCode == MY_PERMISSIONS_REQUEST_CALL_PHONE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                callPhone();
            } else
            {
                // Permission Denied
                Toast.makeText(getActivity(), "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return;
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void confirmUserPhone(String msg) {

        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @Override
            public void onClickOk() {
                final String num=mTextNum.getText().toString();
                Intent intent = new Intent(Intent.ACTION_CALL);
                Uri data = Uri.parse("tel:" + num);
                intent.setData(data);
                startActivity(intent);
                dialog.dismiss();
            }
        });

        dialog.show(getChildFragmentManager(), "CallDialog");

    }
}
