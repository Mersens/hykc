package com.tuoying.hykc.fragment;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.AboutActivity;
import com.tuoying.hykc.activity.BXActivity;
import com.tuoying.hykc.activity.CheckRzTextActivity;
import com.tuoying.hykc.activity.FWActivity;
import com.tuoying.hykc.activity.GoodsListDetailActivity;
import com.tuoying.hykc.activity.LXKFActivity;
import com.tuoying.hykc.activity.MainActivity;
import com.tuoying.hykc.activity.MyInfoActivity;
import com.tuoying.hykc.activity.MyMsgActivity;
import com.tuoying.hykc.activity.MyWalletActivity;
import com.tuoying.hykc.activity.OilActivity;
import com.tuoying.hykc.activity.RzTextActivity;
import com.tuoying.hykc.activity.SCActivity;
import com.tuoying.hykc.activity.SYBZActivity;
import com.tuoying.hykc.activity.SettingActivity;
import com.tuoying.hykc.activity.TrailerInfoActivity;
import com.tuoying.hykc.app.App;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.EventEntity;
import com.tuoying.hykc.entity.GoodsEntity;
import com.tuoying.hykc.entity.MsgEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.utils.DateUtils;
import com.tuoying.hykc.utils.NotificationUtils;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.RxBus;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.ExitDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.Iterator;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class OthersFragment extends Fragment implements View.OnClickListener{
    WeakReference<MainActivity> mActivityReference;
    private CompositeDisposable mCompositeDisposable;
    private ImageView imgExit;
    private TextView mTextName;
    private TextView mTextRZ;
    private ImageView avatarImg;
    private DBDao dao;
    private String userid;
    private MainActivity activity;
    private RelativeLayout mLayoutQB;
    private RelativeLayout mLayoutSetting;
    private RelativeLayout mLayoutKF;
    private RelativeLayout mLayoutWM;
    private RelativeLayout mLayoutBZ;
    private RelativeLayout mLayoutSC;
    private RelativeLayout mLayoutFW;
    private RelativeLayout mLayoutBX;
    private RelativeLayout mLayoutCar;
    private RelativeLayout mLayoutOil;
    public static OthersFragment getInstance() {
        return new OthersFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        activity = (MainActivity) context;
        mActivityReference = new WeakReference<>((MainActivity) context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_others, null);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    private void init(View view) {
        dao = new DBDaoImpl(getActivity());
        userid = SharePreferenceUtil.getInstance(getActivity()).getUserId();
        initViews(view);
        initEvent();
        initDatas();
        mCompositeDisposable = new CompositeDisposable();
        //监听订阅事件
        Disposable d = RxBus.getInstance().toObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof EventEntity) {
                            EventEntity e = (EventEntity) o;
                            String type = e.type;
                            if (type.equals("heardview_rz")) {
                                initDatas();
                            }
                        }
                    }
                });
        //subscription交给compositeSubscription进行管理，防止内存溢出
        mCompositeDisposable.add(d);
    }

    private void initDatas() {
        userid = SharePreferenceUtil.getInstance(getActivity()).getUserId();
        if (!TextUtils.isEmpty(userid)) {
            User user = dao.findUserInfoById(userid);
            if (user != null) {
                getRzInfo(user, userid);
            }
        }
    }

    private void initEvent() {
        mLayoutQB.setOnClickListener(this);
        mLayoutSetting.setOnClickListener(this);
        mLayoutKF.setOnClickListener(this);
        mLayoutWM.setOnClickListener(this);
        mLayoutBZ.setOnClickListener(this);
        mLayoutSC.setOnClickListener(this);
        mLayoutFW.setOnClickListener(this);
        mLayoutBX.setOnClickListener(this);
        mLayoutCar.setOnClickListener(this);
        mLayoutOil.setOnClickListener(this);
        mTextRZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msg = mTextRZ.getText().toString();
                if ("未认证".equals(msg) || "未通过".equals(msg)) {
                    Intent intent = new Intent(activity, RzTextActivity.class);
                    startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }else if("已认证".equals(msg)){
                    //跳转到查看页面
                    Intent intent=new Intent(getActivity(),CheckRzTextActivity.class);
                    startActivity(intent);
                    activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            }
        });
        avatarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent myInfoIntent = new Intent(activity, MyInfoActivity.class);
                startActivity(myInfoIntent);
                activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        imgExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmUserExit("是否退出登录？");
            }
        });

    }

    private void confirmUserExit(String msg) {
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getChildFragmentManager(), "otherDialogUser");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @Override
            public void onClickOk() {
                String userid = SharePreferenceUtil.getInstance(activity).getUserId();
                if (!TextUtils.isEmpty(userid)) {
                    dao.delUserInfoById(userid);
                }
                SharePreferenceUtil.getInstance(activity).setUserId(null);
                dialog.dismiss();
                App.getInstance().exit();
            }
        });


    }

    private void initViews(View view) {
        imgExit = (ImageView) view.findViewById(R.id.img_exit);
        mTextName = (TextView) view.findViewById(R.id.tv_name);
        mTextRZ = view.findViewById(R.id.tv_rz);
        avatarImg = (ImageView) view.findViewById(R.id.imageView);
        mLayoutQB=view.findViewById(R.id.layout_qb);
        mLayoutSetting=view.findViewById(R.id.layout_setting);
        mLayoutKF=view.findViewById(R.id.layout_kf);
        mLayoutWM=view.findViewById(R.id.layout_wm);
        mLayoutBZ=view.findViewById(R.id.layout_bz);
        mLayoutSC=view.findViewById(R.id.layout_sc);
        mLayoutFW=view.findViewById(R.id.layout_fw);
        mLayoutBX=view.findViewById(R.id.layout_bx);
        mLayoutCar=view.findViewById(R.id.layout_car);
        mLayoutOil=view.findViewById(R.id.layout_oil);
    }

    private void getRzInfo(final User user, final String id) {
        RequestManager.getInstance()
                .mServiceStore
                .findMyRz(user.getToken(), user.getUserId(), Constants.AppId)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("getRzInfo onSuccess", msg);
                        activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        if (TextUtils.isEmpty(msg)) {
                            return;
                        }
                        boolean isSuccess = false;
                        try {
                            JSONObject jsonObject = new JSONObject(msg.replaceAll("\r", "").replaceAll("\n", ""));
                            User locUser = user;
                            if (jsonObject.has("ofwlgsinfo")) {
                                String string = jsonObject.getString("ofwlgsinfo");
                                if ("{}".equals(string)) {
                                    locUser.setOfwlgsinfo(null);
                                } else {
                                    locUser.setOfwlgsinfo(jsonObject.getString("ofwlgsinfo"));
                                }

                            }
                            if (jsonObject.has("rz#zt")) {
                                String info = jsonObject.getString("rz#zt");
                                if (info.equals("1")) {
                                    mTextRZ.setText("已认证");
                                    locUser.setRz(info);
                                    if (jsonObject.has("rz#xm")) {
                                        locUser.setUserName(jsonObject.getString("rz#xm"));
                                        mTextName.setText(jsonObject.getString("rz#xm"));
                                    }
                                } else if (info.equals("0")) {
                                    mTextRZ.setText("未通过");
                                    locUser.setRz(info);
                                    confirmRZTips("用户未认证未通过！");
                                }
                            } else {
                                mTextRZ.setText("未认证");
                                locUser.setRz("");
                                confirmRZTips("用户未认证,请认证！");
                            }
                            dao.updateUserInfo(locUser, id);
                            if (!TextUtils.isEmpty(userid)) {
                                User user = dao.findUserInfoById(userid);
                                if (user != null) {
                                    mTextName.setText(user.getUserName());
                                    String rzType = user.getRz();
                                    if ("0".equals(rzType)) {
                                        mTextRZ.setText("未通过");
                                    } else if ("1".equals(rzType)) {
                                        mTextRZ.setText("已认证");
                                    } else if (TextUtils.isEmpty(rzType)) {
                                        mTextRZ.setText("未认证");
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(String msg) {
                    }
                }));
    }

    private void confirmRZTips(String msg) {
        //退出操作
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getChildFragmentManager(), "RZExitDialog");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @Override
            public void onClickOk() {
                dialog.dismiss();
                Intent intent = new Intent(activity, RzTextActivity.class);
                startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);


            }
        });


    }

    @Override
    public void onClick(View v) {
        Intent mIntent=null;
        switch (v.getId()){
            case R.id.layout_qb:

                mIntent=new Intent(activity,MyWalletActivity.class);
                break;
            case R.id.layout_setting:
                mIntent=new Intent(activity, SettingActivity.class);
                break;
            case R.id.layout_kf:
                mIntent=new Intent(activity, LXKFActivity.class);

                break;
            case R.id.layout_wm:
                mIntent=new Intent(activity,AboutActivity.class);

                break;
            case R.id.layout_bz:
                mIntent=new Intent(activity,SYBZActivity.class);

                break;
            case R.id.layout_sc:
                mIntent=new Intent(activity,SCActivity.class);

                break;
            case R.id.layout_fw:
                mIntent=new Intent(activity,FWActivity.class);

                break;
            case R.id.layout_bx:
                mIntent=new Intent(activity,BXActivity.class);
                break;
            case R.id.layout_car:
                mIntent=new Intent(activity,TrailerInfoActivity.class);
                break;
            case R.id.layout_oil:
                mIntent=new Intent(activity,OilActivity.class);
                break;
        }
        if(mIntent!=null){
            startActivity(mIntent);
            activity.overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
        }
    }

    private void confirmTips(String msg) {
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getChildFragmentManager(), "confirmTips1112");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @Override
            public void onClickOk() {

                dialog.dismiss();
            }
        });


    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
