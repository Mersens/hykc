package com.tuoying.hykc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.alct.mdp.MDPLocationCollectionManager;
import com.alct.mdp.callback.OnDownloadResultListener;
import com.alct.mdp.callback.OnResultListener;
import com.alct.mdp.model.EnterpriseIdentity;
import com.alct.mdp.model.Invoice;
import com.alct.mdp.model.MultiIdentity;
import com.alct.mdp.response.GetInvoicesResponse;
import com.tuoying.hykc.R;
import com.tuoying.hykc.app.App;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.EventEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.utils.IOUtils;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.RxBus;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.utils.ViewClickHelper;
import com.tuoying.hykc.view.ExitDialogFragment;
import com.tuoying.hykc.view.LoadingDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.dom.DOMResult;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.internal.publicsuffix.PublicSuffixDatabase;

/**
 * Created by Administrator on 2018/3/22.
 */

public class LoginActivity extends BaseActivity {
    private static final String TYPE = "driver";
    final LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.getInstance();
    private EditText mEditPhone;
    private EditText mEditPsd;
    private TextView mTextFind;
    private Button mBtnLogin;
    private TextView mTextRegister;
    private DBDao dao;
    private CompositeDisposable compositeDisposable;
    private String tel = null;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
        }
    };
    private RelativeLayout mLayoutXY;
    private CheckBox checkBox;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_login);
        init();
    }

    @Override
    public void init() {
        compositeDisposable = new CompositeDisposable();
        if(getIntent().hasExtra("tel")){
            tel = getIntent().getStringExtra("tel");
        }
        mEditPhone = (EditText) findViewById(R.id.editPhone);
        if (!TextUtils.isEmpty(tel)) {
            mEditPhone.setText(tel);
        }
        mEditPsd = (EditText) findViewById(R.id.editPass);
        mTextFind = (TextView) findViewById(R.id.tvselect);
        mTextRegister = (TextView) findViewById(R.id.tvnewReg);
        mBtnLogin = (Button) findViewById(R.id.btnlogin);
        dao = new DBDaoImpl(this);
        //监听订阅事件
        Disposable d = RxBus.getInstance().toObservable().toObservable().subscribeOn(Schedulers.io())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof EventEntity) {
                            EventEntity e = (EventEntity) o;
                            String type = e.type;
                            if ("注册成功".equals(type)) {
                                mEditPhone.setText(e.value);
                                mEditPsd.setText("");
                            } else if ("找回成功".equals(type)) {
                                mEditPhone.setText(e.value);
                                mEditPsd.setText("");
                            }
                        }
                    }
                });
        //subscription交给compositeSubscription进行管理，防止内存溢出
        compositeDisposable.add(d);
        ViewClickHelper.clicks(mTextFind, new ViewClickHelper.onViewClickListener() {
            @Override
            public void onAccept() {
                mEditPsd.setText("");
                Intent intentFind = new Intent(LoginActivity.this, FindPsdActivity.class);
                startActivity(intentFind);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        ViewClickHelper.clicks(mTextRegister, new ViewClickHelper.onViewClickListener() {
            @Override
            public void onAccept() {
                Intent intentReg = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intentReg);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });

        ViewClickHelper.clicks(mBtnLogin, new ViewClickHelper.onViewClickListener() {
            @Override
            public void onAccept() {
                doLogin();
            }
        });
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
        Intent intentFind = new Intent(LoginActivity.this, AgreementActivity.class);
        startActivity(intentFind);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

    }

    private void doLogin() {
        if(!checkBox.isChecked()){
            Toast.makeText(this, "请阅读和接受货运快车服务协议！", Toast.LENGTH_SHORT).show();
            return;
        }
        String tel = mEditPhone.getText().toString().trim();
        String psd = mEditPsd.getText().toString().trim();
        if (TextUtils.isEmpty(tel)) {
            Toast.makeText(this, "手机号不能为空！", Toast.LENGTH_SHORT).show();
            return;

        }
        if (TextUtils.isEmpty(psd)) {
            Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (tel.length()!=11) {
            Toast.makeText(this, "请输入正确手机号！", Toast.LENGTH_SHORT).show();
            return;
        }
        loadingDialogFragment.show(getSupportFragmentManager(), "loadingDialogFragment");
        login(tel, psd);
    }

    private void login(final String tel, final String psd) {
        RequestManager.getInstance()
                .mServiceStore
                .login(tel, psd, TYPE)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("login onSuccess", msg);
                        analysisJson(msg,psd);
                    }
                    @Override
                    public void onError(String msg) {
                        loadingDialogFragment.dismiss();
                        Log.e("login onError",msg);
                        Toast.makeText(LoginActivity.this, "登录失败！", Toast.LENGTH_SHORT).show();
                    }
                }));
    }
    private void analysisJson(String json,final String pwd) {
        boolean isSuccess = false;
        try {
            JSONObject mySO = new JSONObject(json);
            isSuccess = mySO.getBoolean("success");
            if (isSuccess) {
                User user = new User();
                String tel = mySO.getString("mobile");
                String psd = mySO.getString("pwd");
                user.setUserId(tel);
                user.setPsd(psd);
                user.setUserName(tel);
                user.setToken(mySO.getString("token"));
                if (dao.findUserIsExist(tel)) {
                    dao.updateUserInfo(user, tel);
                } else {
                    dao.addUserInfo(user);
                }
                String port=mySO.getString("port");
                String server=mySO.getString("server");
                String mqtturl="tcp://"+server+":"+port;
                SharePreferenceUtil.getInstance(this).setMqttUrl(mqtturl);
                SharePreferenceUtil.getInstance(this).setUserId(tel);
                SharePreferenceUtil.getInstance(this).setLoginPwd(pwd);
                Toast.makeText(this, "登录成功！", Toast.LENGTH_SHORT).show();
                String alct=mySO.getString("alct");
               // SharePreferenceUtil.getInstance(this).setALCTMsg(alct);
                getRzInfo(alct,user.getToken(), user.getUserId());
            } else {
                loadingDialogFragment.dismiss();
                String msg = mySO.getString("message");
                Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void doRegisterAlct(String alct, String json,final String id) {
        User user=null;
        String userid=SharePreferenceUtil.getInstance(this).getUserId();
        if(!TextUtils.isEmpty(userid)){
            user=dao.findUserInfoById(userid);
        }
        final MultiIdentity mMultiIdentity =new MultiIdentity();
        try {
            JSONObject mySO = new JSONObject(json);
            if(mySO.has("ofwlgsinfo")){
                if(user!=null){
                    user.setOfwlgsinfo(mySO.getString("ofwlgsinfo"));
                    if (dao.findUserIsExist(userid)) {
                        dao.updateUserInfo(user, userid);
                    } else {
                        dao.addUserInfo(user);
                    }
                }
            }
            if(!mySO.has("rz#sfzh")){
                if(loadingDialogFragment!=null){
                    loadingDialogFragment.dismiss();
                }
                Intent intent=new Intent(LoginActivity.this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                finish();
                return;
            }

            JSONArray array=new JSONArray(alct);
            List<EnterpriseIdentity> mList=new ArrayList<>();
            for (int i=0;i<array.length();i++){
                JSONObject object=array.getJSONObject(i);
                EnterpriseIdentity enterpriseIdentity=new EnterpriseIdentity();
                String alctid= object.getString("alctid");
                if(TextUtils.isEmpty(alctid)){
                    continue;
                }
                enterpriseIdentity.setAppIdentity(object.getString("alctid"));
                enterpriseIdentity.setAppKey(object.getString("alctkey"));
                enterpriseIdentity.setEnterpriseCode(object.getString("alctcode"));
                mList.add(enterpriseIdentity);
            }
            //总公司
            EnterpriseIdentity enterpriseIdentity=new EnterpriseIdentity();
            enterpriseIdentity.setAppIdentity(Constants.APPIDENTITY);
            enterpriseIdentity.setAppKey(Constants.APPKEY);
            enterpriseIdentity.setEnterpriseCode(Constants.ENTERPRISECODE);
            mList.add(enterpriseIdentity);
            mMultiIdentity.setEnterpriseIdentities(mList);
            mMultiIdentity.setDriverIdentity(mySO.getString("rz#sfzh"));
            upLoadLog(mList,id);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        MDPLocationCollectionManager.register(LoginActivity.this, mMultiIdentity, new OnResultListener() {
            @Override
            public void onSuccess() {
                //step:3
                //tel:157****0385
                //msg:登录获取用户信息成功
                //time:2018-12-18:11:11:10
                //rowid:
                Log.e("login register","register success");
                Map<String,String> map=new HashMap<>();
                map.put("step","3");
                map.put("tel",id);
                map.put("msg","登录页面安联register成功");
                map.put("time",getNowtime());
                map.put("rowid","");
                upLoadUserLog(map);
                if(loadingDialogFragment!=null){
                    loadingDialogFragment.dismiss();
                }
                getInvoices(mMultiIdentity);
                SharePreferenceUtil.getInstance(LoginActivity.this).setSDMsg("0");
                startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("login register","register onFailure");
                //step:4
                //tel:157****0385
                //msg:登录获取用户信息成功
                //time:2018-12-18:11:11:10
                //rowid:
                Map<String,String> map=new HashMap<>();
                map.put("step","4");
                map.put("tel",id);
                map.put("msg","登录页面安联register失败，失败原因："+"s="+s+":s1="+s1);
                map.put("time",getNowtime());
                map.put("rowid","");
                upLoadUserLog(map);
                Log.e("ssss===","s="+s+":s1="+s1);
                if(loadingDialogFragment!=null){
                    loadingDialogFragment.dismiss();
                }
                SharePreferenceUtil.getInstance(LoginActivity.this).setSDMsg("1");
                startActivity(new Intent(LoginActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                finish();
               // confirmTips("联系客服,是否税登");
            }
        });

    }
    private void upLoadLog(List<EnterpriseIdentity> list,String id){
        StringBuffer sbf=new StringBuffer();
        for(EnterpriseIdentity enterpriseIdentity:list){
            sbf.append("alctid:"+enterpriseIdentity.getAppIdentity()+" ; ");
            sbf.append("alctkey:"+enterpriseIdentity.getAppKey()+" ; ");
            sbf.append("alctcode:"+enterpriseIdentity.getEnterpriseCode()+" ; ");
            sbf.append("\n");
        }
        Map<String,String> map=new HashMap<>();
        map.put("tel",id);
        map.put("msg","LoginActivity页面安联登录企业参数");
        map.put("time",getNowtime());
        map.put("rowid","");
        map.put("param",sbf.toString());
        upLoadUserLog(map);
    }
    private void getInvoices(final MultiIdentity mMultiIdentity){
        List<EnterpriseIdentity> mList=mMultiIdentity.getEnterpriseIdentities();
        for(EnterpriseIdentity identity:mList){
            MDPLocationCollectionManager.getInvoices(getApplicationContext(), identity.getEnterpriseCode(), 10, 1, new OnDownloadResultListener() {
                @Override
                public void onSuccess(Object o) {
                    if(o instanceof GetInvoicesResponse){
                        GetInvoicesResponse getInvoicesResponse=(GetInvoicesResponse) o;
                        List<Invoice> list=getInvoicesResponse.getDriverInvoices();
                        for (Invoice invoice:list){
                            String s=invoice.getInvoiceReceiverName()+";"+invoice.getTaxAmount();
                            Log.e("Invoice==",s);
                            confirmInvoice(invoice);
                        }
                    }
                }

                @Override
                public void onFailure(String s, String s1) {
                    Log.e("Invoice onFailure",s+s1);
                }
            });
        }
    }

    private void confirmInvoice(Invoice invoice){
        MDPLocationCollectionManager.confirmInvoice(getApplicationContext(), invoice.getEnterpriseCode(), invoice.getDriverInvoiceCode(), new OnResultListener() {
            @Override
            public void onSuccess() {
                Log.e("confirmInvoice success","onSuccess");

            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("confirmInvoiceFailure","onFailure");
            }
        });
    }

    private void getRzInfo(final String alct,final String token, final String id) {
        RequestManager.getInstance()
                .mServiceStore
                .findMyRz(token, id, Constants.AppId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        //step:1
                        //tel:157****0385
                        //msg:登录获取用户信息成功
                        //time:2018-12-18:11:11:10
                        //rowid:
                        Map<String,String> map=new HashMap<>();
                        map.put("step","1");
                        map.put("tel",id);
                        map.put("msg","登录页面获取用户信息成功");
                        map.put("time",getNowtime());
                        map.put("rowid","");
                        upLoadUserLog(map);
                        Log.e("loaduserinfo onSuccess", msg);
                        String str=msg.replaceAll("\r", "").replaceAll("\n", "");
                        doRegisterAlct(alct,str,id);
                    }

                    @Override
                    public void onError(String msg) {
                        if(loadingDialogFragment!=null){
                            loadingDialogFragment.dismiss();
                        }
                        //step:2
                        //tel:157****0385
                        //msg:登录获取用户信息成功
                        //time:2018-12-18:11:11:10
                        //rowid:
                        Map<String,String> map=new HashMap<>();
                        map.put("step","2");
                        map.put("tel",id);
                        map.put("msg","登录页面获取用户信息失败，失败原因："+msg);
                        map.put("time",getNowtime());
                        map.put("rowid","");
                        upLoadUserLog(map);
                        Log.e("loaduserinfo onSuccess", msg);
                        String str=msg.replaceAll("\r", "").replaceAll("\n", "");
                        doRegisterAlct(alct,str,id);
                    }
                }));
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            confirmExit();
            return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    private void confirmExit() {
        //退出操作
        final ExitDialogFragment dialog = new ExitDialogFragment();
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @Override
            public void onClickOk() {
                SharePreferenceUtil.getInstance(LoginActivity.this).setUserId(null);
                App.getInstance().exit();

            }
        });
        dialog.show(getSupportFragmentManager(), "ExitDialog");

    }

    private void confirmTips(String msg) {
        //退出操作
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                dialog.dismiss();
            }

            @Override
            public void onClickOk() {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
                dialog.dismiss();

            }
        });
        dialog.show(getSupportFragmentManager(), "ExitDialog");

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        compositeDisposable.clear();
        handler.removeCallbacksAndMessages(null);
    }


    private void upLoadUserLog(Map<String, String> params){
        RequestManager.getInstance()
                .mServiceStore
                .uplog(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {

                    }

                    @Override
                    public void onError(String msg) {

                    }
                }));

    }

    private String getNowtime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String time = sdf.format(new Date());
        return time;
    }


}
