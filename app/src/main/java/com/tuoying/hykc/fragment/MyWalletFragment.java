package com.tuoying.hykc.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.BalanceDetailActivity;
import com.tuoying.hykc.activity.CZInputMoneyActivity;
import com.tuoying.hykc.activity.MainActivity;
import com.tuoying.hykc.activity.MyCardActivity;
import com.tuoying.hykc.activity.MyWalletActivity;
import com.tuoying.hykc.activity.RzTextActivity;
import com.tuoying.hykc.activity.TXHistoryActivity;
import com.tuoying.hykc.activity.TXInputMoneyActivity;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.EventEntity;
import com.tuoying.hykc.entity.User;
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

/**
 * Created by Administrator on 2018/3/26.
 */

public class MyWalletFragment extends BaseFragment {
    WeakReference<MyWalletActivity> mActivityReference;
    private CompositeDisposable mCompositeDisposable;
    private TextView mYue;
    private TextView mMx;
    private Button mBtncz;
    private Button mBtntx;
    private DBDao dao;
    private String userid;
    private User user = null;
    private TextView mMyCard;
    private boolean isHasCardInfo=false;

    public static MyWalletFragment getInstance() {

        return new MyWalletFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityReference = new WeakReference<>((MyWalletActivity) context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_mywallet, null);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);

    }

    @Override
    public void init(View view) {
        dao = new DBDaoImpl(getActivity());
        userid = SharePreferenceUtil.getInstance(getActivity()).getUserId();
        if (!TextUtils.isEmpty(userid)) {
            user = dao.findUserInfoById(userid);
            getMoneyInfo(user);
        }
        mYue = (TextView) view.findViewById(R.id.tv_yue);
        mMx = (TextView) view.findViewById(R.id.tv_mx);
        mBtncz = (Button) view.findViewById(R.id.btn_cz);
        mBtntx = (Button) view.findViewById(R.id.btn_tx);
        mMyCard = view.findViewById(R.id.myCard);
        mCompositeDisposable = new CompositeDisposable();
        //监听订阅事件
        Disposable d = RxBus.getInstance().toObservable().toObservable().subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof EventEntity) {
                            EventEntity e = (EventEntity) o;
                            String type = e.type;
                            if (type.equals("money")) {
                                if (user != null) {
                                    getMoneyInfo(user);
                                }

                            }
                        }
                    }
                });
        //subscription交给compositeSubscription进行管理，防止内存溢出
        mCompositeDisposable.add(d);
        initEvents();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }

    private void initEvents() {
        mMx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userid=SharePreferenceUtil.getInstance(getActivity()).getUserId();
                if(!TextUtils.isEmpty(userid)){
                    User user = dao.findUserInfoById(userid);
                    if (user == null) {
                        return;
                    }
                    if (!TextUtils.isEmpty(user.getOfwlgsinfo())) {
                        StringBuffer sbf = new StringBuffer();
                        String ofwlgsinfo = user.getOfwlgsinfo();
                        try {
                            JSONObject object = new JSONObject(ofwlgsinfo);
                            Iterator<String> sIterator = object.keys();
                            while (sIterator.hasNext()) {
                                String key = sIterator.next();
                                sbf.append(key);
                                sbf.append(",");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String str = sbf.toString();
                        if (!TextUtils.isEmpty(str)) {
                            if (str.length() > 1) {
                                str = str.substring(0, str.length() - 1);
                            }
                        }
                        confirmTips("你已经被分配在" + str + "物流公司下！");
                        return;
                    }
                }
                if (user != null) {
                    String rzType = user.getRz();
                    if (rzType.equals("0")) {
                        showDialog("认证未通过，请重新认证！", 3);
                    } else if (rzType.equals("1")) {
                        Intent intent = new Intent(getActivity(), TXHistoryActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    } else if (TextUtils.isEmpty(rzType)) {
                        showDialog("用户未认证，请先认证！", 0);
                    }
                }

            }
        });
        mBtncz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user != null) {
                    String rzType = user.getRz();
                    if (rzType.equals("0")) {
                        showDialog("认证未通过，请重新认证！", 3);
                    } else if (rzType.equals("1")) {
                        Intent intent = new Intent(getActivity(), CZInputMoneyActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    } else if (TextUtils.isEmpty(rzType)) {
                        showDialog("用户未认证，请先认证！", 0);
                    }
                }
            }
        });

        mBtntx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userid=SharePreferenceUtil.getInstance(getActivity()).getUserId();
                if(!TextUtils.isEmpty(userid)){
                    User user = dao.findUserInfoById(userid);
                    if (user == null) {
                        return;
                    }
                    if (!TextUtils.isEmpty(user.getOfwlgsinfo())) {
                        StringBuffer sbf = new StringBuffer();
                        String ofwlgsinfo = user.getOfwlgsinfo();
                        try {
                            JSONObject object = new JSONObject(ofwlgsinfo);
                            Iterator<String> sIterator = object.keys();
                            while (sIterator.hasNext()) {
                                String key = sIterator.next();
                                sbf.append(key);
                                sbf.append(",");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String str = sbf.toString();
                        if (!TextUtils.isEmpty(str)) {
                            if (str.length() > 1) {
                                str = str.substring(0, str.length() - 1);
                            }
                        }
                        confirmTips("你已经被分配在" + str + "物流公司下！");
                        return;
                    }
                }
                if (user != null) {
                    String rzType = user.getRz();
                    if (rzType.equals("0")) {
                        showDialog("认证未通过，请重新认证！", 3);
                    } else if (rzType.equals("1")) {
                        Intent intent = new Intent(getActivity(), TXInputMoneyActivity.class);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    } else if (TextUtils.isEmpty(rzType)) {
                        showDialog("用户未认证，请先认证！", 0);
                    }
                }
            }
        });

        mMyCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userid=SharePreferenceUtil.getInstance(getActivity()).getUserId();
                if(!TextUtils.isEmpty(userid)){
                    User user = dao.findUserInfoById(userid);
                    if (user == null) {
                        return;
                    }
                    if (!TextUtils.isEmpty(user.getOfwlgsinfo())) {
                        StringBuffer sbf = new StringBuffer();
                        String ofwlgsinfo = user.getOfwlgsinfo();
                        try {
                            JSONObject object = new JSONObject(ofwlgsinfo);
                            Iterator<String> sIterator = object.keys();
                            while (sIterator.hasNext()) {
                                String key = sIterator.next();
                                sbf.append(key);
                                sbf.append(",");
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        String str = sbf.toString();
                        if (!TextUtils.isEmpty(str)) {
                            if (str.length() > 1) {
                                str = str.substring(0, str.length() - 1);
                            }
                        }
                        confirmTips("你已经被分配在" + str + "物流公司下！");
                        return;
                    }
                }
                if (user != null) {
                    String rzType = user.getRz();
                    if (rzType.equals("0")) {
                        showDialog("认证未通过，请重新认证！", 3);
                    } else if (rzType.equals("1")) {
                      Intent intent = new Intent(getActivity(), MyCardActivity.class);
                      startActivity(intent);
                      getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

                    } else if (TextUtils.isEmpty(rzType)) {
                        showDialog("用户未认证，请先认证！", 0);
                    }
                }
            }
        });
    }

    public void showDialog(String msg, final int type) {
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getChildFragmentManager(), "RZDialog");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @Override
            public void onClickOk() {
                if (type == 0 || type == 3) {
                    Intent intent = new Intent(getActivity(), RzTextActivity.class);
                    startActivity(intent);
                    getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    dialog.dismiss();
                } else {
                    dialog.dismiss();
                }

            }
        });

    }

    private void confirmTips(String msg) {
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getChildFragmentManager(), "confirmTips332");
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
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        Log.e("onHiddenChanged===", hidden + "");
        if (!hidden) {
            if (!TextUtils.isEmpty(userid)) {
                user = dao.findUserInfoById(userid);
                getMoneyInfo(user);
            }
        }
    }

    private void getMoneyInfo(final User user) {
        RequestManager.getInstance()
                .mServiceStore
                .findMyRz(user.getToken(), user.getUserId(), Constants.AppId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("getRzInfo onSuccess", msg);
                        boolean isSuccess = false;
                        try {
                            JSONObject jsonObject = new JSONObject(msg.replaceAll("\r", "").replaceAll("\n", ""));
                            User locUser = user;
                            if(jsonObject.has("ofwlgsinfo")){
                                locUser.setOfwlgsinfo(jsonObject.getString("ofwlgsinfo"));
                            }
                            if (jsonObject.has("rz#zt")) {
                                String info = jsonObject.getString("rz#zt");
                                if (info.equals("1")) {
                                    locUser.setRz(info);
                                } else if (info.equals("0")) {
                                    locUser.setRz(info);
                                }
                            } else {
                                locUser.setRz("");
                            }
                            if(jsonObject.has("balance")){
                                String money=jsonObject.getString("balance");
                                if(jsonObject.has("ofwlgsinfo")){
                                    mYue.setText("****");
                                }else{
                                    mYue.setText(money);
                                }

                            }
                            if(jsonObject.has("acct_info")){
                                isHasCardInfo=true;

                            }
                            dao.updateUserInfo(locUser, user.getUserId());
                            RxBus.getInstance().send(new EventEntity("rz", "rz"));
                            RxBus.getInstance().send(new EventEntity("heardview_rz", "heardview_rz"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(String msg) {
                    }
                }));

    }
}
