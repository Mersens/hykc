package com.tuoying.hykc.view;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.GoodsListDetailActivity;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class MoreMsgDialog extends DialogFragment {
    String dws;
    String name;
    private String taskid;
    private String yd_driver;
    private ImageView img_close;
    private TextView khm;
    private TextView hth;
    private TextView cfd;
    private TextView mdd;
    private TextView bz;
    private TextView yj;
    private TextView xm;
    private TextView sjh;
    private TextView cph;
    private TextView sfz;
    private TextView hwmc;
    private TextView dw;

    public static MoreMsgDialog getInstance(String taskid, String yd_driver,String dw,String name) {
        MoreMsgDialog dialog = new MoreMsgDialog();
        Bundle bundle = new Bundle();
        bundle.putString("taskid", taskid);
        bundle.putString("yd_driver", yd_driver);
        bundle.putString("dw", dw);
        bundle.putString("name", name);
        dialog.setArguments(bundle);
        return dialog;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        return inflater.inflate(R.layout.layout_order_more,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        taskid = getArguments().getString("taskid");
        yd_driver = getArguments().getString("yd_driver");
        dws=getArguments().getString("dw");
        name=getArguments().getString("name");
        img_close = view.findViewById(R.id.img_close);
        khm = view.findViewById(R.id.tv_khm);
        hth = view.findViewById(R.id.tv_hth);
        cfd = view.findViewById(R.id.tv_cfd);
        mdd = view.findViewById(R.id.tv_mdd);
        bz = view.findViewById(R.id.tv_bz);
        yj = view.findViewById(R.id.tv_yj);
        xm = view.findViewById(R.id.tv_xm);
        sjh = view.findViewById(R.id.tv_sjh);
        cph = view.findViewById(R.id.tv_cph);
        sfz = view.findViewById(R.id.tv_sfz);
        hwmc = view.findViewById(R.id.tv_hwmc);
        dw = view.findViewById(R.id.tv_dw);

        img_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissAllowingStateLoss();
            }
        });

        initDatas();
    }

    private void initDatas() {
        Map<String, String> map = new HashMap<>();
        map.put("app", Constants.AppId);
        map.put("task_id", taskid);
        map.put("yd_driver", yd_driver);
        RequestManager.getInstance()
                .mServiceStore
                .acceptBillOfTask(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("acceptBillOfTask", "====" + msg);
                        if(!TextUtils.isEmpty(msg)){
                            try {
                                JSONObject object=new JSONObject(msg.trim());
                                khm.setText(object.getString("customer_name"));
                                hth.setText(object.getString("task_cNumber"));
                                bz.setText(object.getString("ext"));
                                yj.setText(object.getString("price"));
                                xm.setText(object.getString("xm"));
                                sjh.setText(object.getString("sjh"));
                                cph.setText(object.getString("cph"));
                                sfz.setText(object.getString("sfz"));
                                hwmc.setText(name);
                                dw.setText(dws+"吨");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onError(String msg) {
                        Log.e("acceptBillOfTask", "====" + msg);
                    }
                }));
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }
}
