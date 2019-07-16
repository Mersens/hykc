package com.tuoying.hykc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.tuoying.hykc.R;
import com.tuoying.hykc.entity.EventEntity;
import com.tuoying.hykc.service.RegisterCodeTimerService;
import com.tuoying.hykc.utils.IOUtils;
import com.tuoying.hykc.utils.RegisterCodeTimer;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.RxBus;
import com.tuoying.hykc.utils.ViewClickHelper;
import com.tuoying.hykc.view.ExitDialogFragment;
import com.tuoying.hykc.view.LoadingDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Administrator on 2018/3/22.
 */

public class RegisterActivity extends BaseActivity {
    private static final String TYPE = "driver";
    LoadingDialogFragment loadingDialogFragment=LoadingDialogFragment.getInstance();
    private Toolbar mToolbar;
    private EditText mEditPhone;
    private EditText mEdCode;
    private EditText mEditPass;
    private TextView mGetCode;
    private RelativeLayout mLayoutXY;
    private CheckBox checkBox;
    Handler mCodeHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == RegisterCodeTimer.IN_RUNNING) {// 正在倒计时
                mGetCode.setText(msg.obj.toString());
                mGetCode.setEnabled(false);
            } else if (msg.what == RegisterCodeTimer.END_RUNNING) {// 完成倒计时
                mGetCode.setEnabled(true);
                mGetCode.setText("获取验证码");
            }
        }

        ;
    };
    private Button mBtnRegister;
    private String mobile = null;
    private String chkCode = null;
    private Intent mIntent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_register);
        init();
    }

    @Override
    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("注册");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initViews();
        initEvent();
    }

    private void initViews() {
        RegisterCodeTimerService.setHandler(mCodeHandler);
        mIntent = new Intent(RegisterActivity.this,
                RegisterCodeTimerService.class);
        mEditPhone = (EditText) findViewById(R.id.editPhone);
        mEdCode = (EditText) findViewById(R.id.edCode);
        mEditPass = (EditText) findViewById(R.id.editPass);
        mGetCode = (TextView) findViewById(R.id.tv_getCode);
        mBtnRegister = (Button) findViewById(R.id.btn_register);
        mLayoutXY=findViewById(R.id.layout_xy);
        checkBox=findViewById(R.id.checkBox);
        mLayoutXY.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doCheckXY();
            }
        });
    }

    private void doCheckXY() {
        Intent intentFind = new Intent(RegisterActivity.this, AgreementActivity.class);
        startActivity(intentFind);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void initEvent() {

        ViewClickHelper.clicks(mGetCode, new ViewClickHelper.onViewClickListener() {
            @Override
            public void onAccept() {
                //获取验证码
                doGetCode();

            }
        });
        ViewClickHelper.clicks(mBtnRegister, new ViewClickHelper.onViewClickListener() {
            @Override
            public void onAccept() {
                //注册
                doRegister();

            }
        });
    }

    private void doRegister() {
        if(!checkBox.isChecked()){
            Toast.makeText(this, "请阅读和接受货运快车服务协议！", Toast.LENGTH_SHORT).show();
            return;
        }
        String telnum = mEditPhone.getText().toString().trim();
        String psd = mEditPass.getText().toString().trim();
        String code = mEdCode.getText().toString().trim();
        if (TextUtils.isEmpty(telnum)) {
            Toast.makeText(this, "手机号不能为空！", Toast.LENGTH_SHORT).show();
            return;

        }
        if (telnum.length()!=11) {
            Toast.makeText(this, "请输入正确手机号！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(code)) {
            Toast.makeText(this, "验证码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(psd)) {
            Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (psd.length() < 6) {
            Toast.makeText(this, "密码至少六位！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!telnum.equals(mobile)) {
            Toast.makeText(this, "手机号码不一致！", Toast.LENGTH_SHORT).show();
            return;
        }
        loadingDialogFragment.show(getSupportFragmentManager(),"reg");
        RequestManager.getInstance()
                .mServiceStore
                .reg(telnum, psd, code, "driver")
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("onSuccess", msg);
                        analysisJson(msg);
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("onError", msg);
                        loadingDialogFragment.dismiss();
                    }
                }));
    }

    private void analysisJson(String msg) {
        boolean isSuccess = false;
        try {
            JSONObject mySO = new JSONObject(msg);
            isSuccess = mySO.getBoolean("success");
            if (isSuccess) {
                confirmReg("注册成功！");
            } else {
                String str = mySO.getString("message");
                Toast.makeText(this, str, Toast.LENGTH_SHORT).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            loadingDialogFragment.dismiss();
        }

    }

    private void confirmReg(String msg) {

        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
                RxBus.getInstance().send(new EventEntity("注册成功",mobile));
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }

            @Override
            public void onClickOk() {
                dialog.dismiss();
                RxBus.getInstance().send(new EventEntity("注册成功",mobile));
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

            }
        });
        dialog.show(getSupportFragmentManager(), "ExitDialogREG");

    }

    private void doGetCode() {
        final String tel = mEditPhone.getText().toString().trim();
        if (TextUtils.isEmpty(tel)) {
            Toast.makeText(this, "手机号不能为空！", Toast.LENGTH_SHORT).show();
            return;

        }
        if (tel.length()!=11) {
            Toast.makeText(this, "请输入正确手机号！", Toast.LENGTH_SHORT).show();
            return;
        }
        mGetCode.setEnabled(false);
        startService(mIntent);
        RequestManager.getInstance()
                .mServiceStore
                .getCode(tel, "reg")
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("onSuccess", msg);
                        boolean isSuccess = false;
                        try {
                            JSONObject mySO = new JSONObject(msg);
                            isSuccess = mySO.getBoolean("success");
                            if (isSuccess) {
                                if(mySO.has("sms")){
                                    chkCode = mySO.getString("sms");
                                }
                                String m = mySO.getString("mobile");
                                mobile = m;
                            } else {
                                String message = mySO.getString("message");
                                Toast.makeText(RegisterActivity.this, message, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("onError", msg);
                        Toast.makeText(RegisterActivity.this, "获取验证码失败！", Toast.LENGTH_SHORT).show();
                    }
                }));
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
        stopService(mIntent);
        mCodeHandler.removeCallbacksAndMessages(null);
    }
}
