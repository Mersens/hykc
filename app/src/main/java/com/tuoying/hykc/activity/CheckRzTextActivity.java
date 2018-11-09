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
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.tuoying.hykc.R;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class CheckRzTextActivity extends BaseActivity {
    private Toolbar mToolbar;
    private TextView mTextResetRz;
    private Button mBtnNextPage;

    private TextView mTextName;
    private TextView mTextCardNum;
    private TextView mTextCardStartTime;
    private TextView mTextCardStartEnd;

    private TextView mTextJSZ;
    private TextView mTextJSZStartTime;
    private TextView mTextJSZEndTime;
    private TextView mTextSCLZTime;
    private TextView mTextSYR;
    private TextView mTextCPH;
    private TextView mTextCLSBM;
    private TextView mTextFDJH;

    private TextView mTextTEL;
    private TextView mTextZZ;
    private TextView mTextDLYSZ;
    private TextView mTextJZLX;
    private TextView mTextCLLX;
    private TextView mTextCX;
    private TextView mTextCC;
    private TextView mTextCPLX;
    private TextView mTextCLFL;
    private DBDao dao;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_check_rz_text);
        init();
    }

    @Override
    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("认证信息");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        dao=new DBDaoImpl(this);
        initViews();
        initEvent();
        initDatas();

    }

    private void initDatas() {
        String userid=SharePreferenceUtil.getInstance(this).getUserId();
        if(TextUtils.isEmpty(userid)){
            Toast.makeText(this, "请重新登录！", Toast.LENGTH_SHORT).show();
            return;
        }
        User user=dao.findUserInfoById(userid);
        if(user==null){
            Toast.makeText(this, "请重新登录！", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String,String> map=new HashMap<>();
        map.put("mobile",user.getUserId());
        map.put("app",Constants.AppId);
        map.put("token",user.getToken());
        RequestManager.getInstance()
                .mServiceStore
                .loadRzInfo(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("loadRzInfo onSuccess", msg);
                        if(TextUtils.isEmpty(msg)){
                            return;
                        }
                        try {
                            JSONObject object=new JSONObject(msg);
                            analysisJson(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(String msg) {
                        Log.e("loadRzInfo onError", msg);
                    }
                }));

    }

    private void analysisJson(JSONObject json) {

        try {
            if(json.has("data:xm")){
                String userName=json.getString("data:xm");
                mTextName.setText(userName);
            }
            if(json.has("data:sfzh")){
                String num=json.getString("data:sfzh");
                mTextCardNum.setText(num);
            }
            if(json.has("data:sfzStartTime")){
                String num=json.getString("data:sfzStartTime");
                mTextCardStartTime.setText(num);
            }
            if(json.has("data:sfzEndTime")){
                String num=json.getString("data:sfzEndTime");
                mTextCardStartEnd.setText(num);
            }

            if(json.has("data:licenseNo")){
                String num=json.getString("data:licenseNo");
                mTextJSZ.setText(num);
            }
            if(json.has("data:licenseStartTime")){
                String num=json.getString("data:licenseStartTime");
                mTextJSZStartTime.setText(num);
            }
            if(json.has("data:licenseEndTime")){
                String num=json.getString("data:licenseEndTime");
                mTextJSZEndTime.setText(num);
            }
            if(json.has("data:licenseFirstGetDate")){
                String num=json.getString("data:licenseFirstGetDate");
                mTextSCLZTime.setText(num);
            }


            if(json.has("data:owner")){
                String num=json.getString("data:owner");
                mTextSYR.setText(num);
            }

            if(json.has("data:cph")){
                String num=json.getString("data:cph");
                mTextCPH.setText(num);
            }

            if(json.has("data:vehicleIdentityCode")){
                String num=json.getString("data:vehicleIdentityCode");
                mTextCLSBM.setText(num);
            }

            if(json.has("data:engineNumber")){
                String num=json.getString("data:engineNumber");
                mTextFDJH.setText(num);
            }
            if(json.has("data:mobile")){
                String num=json.getString("data:mobile");
                mTextTEL.setText(num);
            }
            if(json.has("data:zz")){
                String num=json.getString("data:zz");
                mTextZZ.setText(num);
            }
            if(json.has("data:dlysz")){
                String num=json.getString("data:dlysz");
                mTextDLYSZ.setText(num);
            }

            if(json.has("data:licenseType")){
                String num=json.getString("data:licenseType");
                mTextJZLX.setText(num);
            }
            if(json.has("data:pp")){
                String num=json.getString("data:pp");
                mTextCLLX.setText(num);
            }
            if(json.has("data:cx")){
                String num=json.getString("data:cx");
                mTextCX.setText(num);
            }
            if(json.has("data:cc")){
                String num=json.getString("data:cc");
                mTextCC.setText(num);
            }
            if(json.has("data:cplx")){
                String num=json.getString("data:cplx");
                mTextCPLX.setText(num);
            }
            if(json.has("data:clfl")){
                String num=json.getString("data:clfl");
                mTextCLFL.setText(num);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    private void initViews() {
        mTextResetRz=mToolbar.findViewById(R.id.tv_right_title);
        mBtnNextPage=findViewById(R.id.btn_next);
        mTextName=findViewById(R.id.tv_name);
        mTextCardNum=findViewById(R.id.tv_num);
        mTextCardStartTime=findViewById(R.id.num_start_Time);
        mTextCardStartEnd=findViewById(R.id.num_start_end);
        mTextJSZ=findViewById(R.id.tv_jsz);
        mTextJSZStartTime=findViewById(R.id.jsz_start_name);
        mTextJSZEndTime=findViewById(R.id.jsz_start_end);
        mTextSCLZTime=findViewById(R.id.tv_jsz_time);
        mTextSYR=findViewById(R.id.tv_syr);
        mTextCPH=findViewById(R.id.tv_cph);
        mTextCLSBM=findViewById(R.id.tv_clsbm);
        mTextFDJH=findViewById(R.id.tv_fdjh);
        mTextTEL=findViewById(R.id.tv_tel);
        mTextZZ=findViewById(R.id.tv_zz);
        mTextDLYSZ=findViewById(R.id.tv_dlysz);
        mTextJZLX=findViewById(R.id.tv_jszlx);
        mTextCLLX=findViewById(R.id.tv_cllx);
        mTextCX=findViewById(R.id.tv_cx);
        mTextCC=findViewById(R.id.tv_cc);
        mTextCPLX=findViewById(R.id.tv_cplx);
        mTextCLFL=findViewById(R.id.tv_clfl);
    }

    private void initEvent() {
        mTextResetRz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CheckRzTextActivity.this,RzTextActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        mBtnNextPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //跳转到图片查看页面
                Intent intent=new Intent(CheckRzTextActivity.this,CheckRzImgActivity.class);
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

}
