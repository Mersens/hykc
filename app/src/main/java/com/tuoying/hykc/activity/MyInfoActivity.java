package com.tuoying.hykc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.tuoying.hykc.R;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.EventEntity;
import com.tuoying.hykc.entity.RZEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.RxBus;
import com.tuoying.hykc.utils.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/3/28.
 */

public class MyInfoActivity extends BaseActivity {
    private CompositeDisposable mCompositeDisposable;
    private Toolbar mToolbar;
    private RelativeLayout mLayoutRz;
    private TextView mTextRzMsg;
    private LinearLayout mLayoutInfo;
    private DBDao dao;
    private String userid;
    private TextView mTextName;
    private TextView mTextNum;
    private TextView mTextTel;
    private TextView tv_name;
    private RelativeLayout mLayoutCarInfo;
    private RZEntity entity = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_myinfo);
        init();
    }

    @Override
    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("我的信息");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mLayoutRz = (RelativeLayout) findViewById(R.id.layout_rz);
        mTextRzMsg = (TextView) findViewById(R.id.rz_msg);
        mLayoutInfo = findViewById(R.id.layout_info);
        tv_name = findViewById(R.id.tv_name);
        mTextName = findViewById(R.id.tv_rname);
        mTextNum = findViewById(R.id.tv_idcard);
        mTextTel = findViewById(R.id.tv_tel);
        mLayoutCarInfo = findViewById(R.id.layout_carinfo);
        dao = new DBDaoImpl(this);
        initDatas();
        initEvent();
    }

    private void initDatas() {
        userid = SharePreferenceUtil.getInstance(this).getUserId();
        if (!TextUtils.isEmpty(userid)) {
            tv_name.setText(userid + "");
            User user = dao.findUserInfoById(userid);
            if (user != null) {
                getInfo(user, userid);
            }
        }
        mCompositeDisposable = new CompositeDisposable();
        //监听订阅事件
        Disposable d = RxBus.getInstance().toObservable().toObservable().subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof EventEntity) {
                            EventEntity e = (EventEntity) o;
                            String type = e.type;
                            if (type.equals("rz")) {
                                if (!TextUtils.isEmpty(userid)) {
                                    User user = dao.findUserInfoById(userid);
                                    if (user != null) {
                                        if ("0".equals(user.getRz())) {
                                            mTextRzMsg.setTextColor(getResources().getColor(R.color.colorAccent));
                                            mTextRzMsg.setText("未通过");
                                        } else if ("1".equals(user.getRz())) {
                                            mTextRzMsg.setTextColor(getResources().getColor(R.color.actionbar_color));
                                            mTextRzMsg.setText("已认证");
                                        } else if (TextUtils.isEmpty(user.getRz())) {
                                            mTextRzMsg.setTextColor(getResources().getColor(R.color.actionbar_color));
                                            mTextRzMsg.setText("未认证");
                                        }
                                    }
                                }
                            }
                        }
                    }
                });
        //subscription交给compositeSubscription进行管理，防止内存溢出
        mCompositeDisposable.add(d);
    }
    private void getInfo(final User user, final String id) {
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
                            analysisJson(jsonObject);
                            User locUser = user;
                            if (jsonObject.has("rz#zt")) {
                                String info = jsonObject.getString("rz#zt");
                                if (info.equals("1")) {
                                    mTextRzMsg.setText("已认证");
                                    locUser.setRz(info);
                                    if(jsonObject.has("rz#xm")) {
                                        locUser.setUserName(jsonObject.getString("rz#xm"));
                                    }
                                    mLayoutInfo.setVisibility(View.VISIBLE);
                                } else if (info.equals("0")) {
                                    mTextRzMsg.setText("未通过");
                                    locUser.setRz(info);
                                }
                            } else {
                                mTextRzMsg.setText("未认证");
                                locUser.setRz("");
                            }
                            dao.updateUserInfo(locUser, id);
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


    private void analysisJson(JSONObject json) {
        entity = new RZEntity();
        try {
            if (json.has("rz#zt")) {
                entity.setZt(json.getString("rz#zt"));
            }
            if(json.has("rz#sfzh")) {
                mTextNum.setText(json.getString("rz#sfzh"));
                entity.setSfzh(json.getString("rz#sfzh"));
            }
            if(json.has("rz#mobile")) {
                mTextTel.setText(json.getString("rz#mobile"));
                entity.setMobile(json.getString("rz#mobile"));
            }
            if(json.has("rz#xm")) {
                mTextName.setText(json.getString("rz#xm"));
                entity.setXm(json.getString("rz#xm"));
            }
            if(json.has("rz#cph")) {
                entity.setCph(json.getString("rz#cph"));
            }
            if(json.has("rz#pp")) {
                entity.setPp(json.getString("rz#pp"));
            }
            if(json.has("rz#cc")) {
                entity.setCc(json.getString("rz#cc"));
            }
            if(json.has("rz#cx")) {
                entity.setCx(json.getString("rz#cx"));
            }
            if(json.has("rz#zz")) {
                entity.setZz(json.getString("rz#zz"));
            }
            if(json.has("rz#nf")) {
                entity.setNf(json.getString("rz#nf"));
            }
            if(json.has("rz#dlysz")) {
                entity.setDlysz(json.getString("rz#dlysz"));
            }
            if(json.has("rz#cplx")) {
                entity.setCplx(json.getString("rz#cplx"));
            }
            if(json.has("rz#clfl")) {
                entity.setClfl(json.getString("rz#clfl"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
}

    private void initEvent() {
        mLayoutRz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String msg = mTextRzMsg.getText().toString();
                if ("未认证".equals(msg) || "未通过".equals(msg)) {
                    Intent intent = new Intent(MyInfoActivity.this, RzTextActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }else if("已认证".equals(msg)){
                    //跳转到查看页面
                    Intent intent=new Intent(MyInfoActivity.this,CheckRzTextActivity.class);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                }
            }
        });
        mLayoutCarInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyInfoActivity.this, CarInfoActivity.class);
                intent.putExtra("carinfo",entity);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }
}
