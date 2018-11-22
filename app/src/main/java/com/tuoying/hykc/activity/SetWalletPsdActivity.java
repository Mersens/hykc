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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tuoying.hykc.R;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.EventEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.service.RegisterCodeTimerService;
import com.tuoying.hykc.utils.IOUtils;
import com.tuoying.hykc.utils.RegisterCodeTimer;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.RxBus;
import com.tuoying.hykc.utils.ServiceUtils;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.utils.ViewClickHelper;
import com.tuoying.hykc.view.ExitDialogFragment;
import com.tuoying.hykc.view.LoadingDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/3/23.
 */

public class SetWalletPsdActivity extends BaseActivity {
    LoadingDialogFragment findPsdDialogFragment= LoadingDialogFragment.getInstance();
    private Toolbar mToolbar;
    private EditText mEditPhone;
    private EditText mEditPass;
    private TextView mTextForgetPsd;
    private EditText mEditPsdNew;
    private Button mBtnSave;
    private DBDao dao;
    private User user;
    private CompositeDisposable compositeDisposable;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_setwalletpsd);
        init();
    }

    @Override
    public void init() {
        compositeDisposable = new CompositeDisposable();
        dao=new DBDaoImpl(this);
        String id=SharePreferenceUtil.getInstance(this).getUserId();
        user=dao.findUserInfoById(id);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("设置钱包密码");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initView();
        initEvent();
        //监听订阅事件
        Disposable d = RxBus.getInstance().toObservable().toObservable().subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof EventEntity) {
                            EventEntity e = (EventEntity) o;
                            String type = e.type;
                            if ("finish".equals(type)) {
                                finish();
                            }
                        }
                    }
                });
        //subscription交给compositeSubscription进行管理，防止内存溢出
        compositeDisposable.add(d);
    }

    private void initEvent() {

        ViewClickHelper.clicks(mBtnSave, new ViewClickHelper.onViewClickListener() {
            @Override
            public void onAccept() {
                doSave();

            }
        });
        mTextForgetPsd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findIntent=new Intent(SetWalletPsdActivity.this, ResetWalletPsdActivity.class);
                startActivity(findIntent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });
    }

    private void doSave() {
        String telnum = mEditPhone.getText().toString().trim();
        String psd = mEditPass.getText().toString().trim();
        String newPsd=mEditPsdNew.getText().toString().trim();
        if (TextUtils.isEmpty(telnum)) {
            Toast.makeText(this, "手机号不能为空！", Toast.LENGTH_SHORT).show();
            return;

        }
        if (telnum.length()!=11) {
            Toast.makeText(this, "请输入正确手机号！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(psd)){
            Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(psd.length()<6){
            Toast.makeText(this, "密码至少六位！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(newPsd.length()<6){
            Toast.makeText(this, "新密码至少六位！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(user==null){
            Toast.makeText(this, "用户信息为空，请重新登录！", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String,String> map= new HashMap<>();
        map.put("token",user.getToken());
        map.put("mobile",user.getUserId());
        map.put("app", Constants.AppId);
        map.put("pwd",newPsd);
        map.put("pwd_old",psd);
        findPsdDialogFragment.show(getSupportFragmentManager(),"setwalletpsd");
        RequestManager.getInstance()
                .mServiceStore
                .set_pwd(map)
                .subscribeOn(Schedulers.io())
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
                        findPsdDialogFragment.dismiss();
                    }
                }));
    }

    private void analysisJson(String str) {
        String msg = str.replaceAll("\r", "").replaceAll("\n", "");
        boolean isSuccess = false;
        try {
            JSONObject mySO = new JSONObject(msg);
            isSuccess = mySO.getBoolean("success");
            if (isSuccess) {
                confirmFind("钱包密码修改成功！");
            } else {
                String strs = mySO.getString("message");
                Toast.makeText(this, strs, Toast.LENGTH_SHORT).show();

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }finally {

            findPsdDialogFragment.dismiss();
        }
    }

    private void confirmFind(String s) {
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(s);
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            }

            @Override
            public void onClickOk() {
                dialog.dismiss();
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);


            }
        });
        dialog.show(getSupportFragmentManager(), "ExitDialogFind");
    }



    private void initView() {
        mEditPhone=(EditText)findViewById(R.id.editPhone);
        if(user!=null){
            mEditPhone.setText(user.getUserId());
        }
        mEditPass=(EditText)findViewById(R.id.editPass_again);
        mBtnSave=(Button)findViewById(R.id.btn_save);
        mEditPsdNew=findViewById(R.id.editPass);
        mTextForgetPsd=findViewById(R.id.tv_forgetpsd);
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
        compositeDisposable.clear();
    }
}
