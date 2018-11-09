package com.tuoying.hykc.activity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.alipay.sdk.app.PayTask;
import com.tencent.mm.opensdk.constants.Build;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tuoying.hykc.R;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.EventEntity;
import com.tuoying.hykc.entity.PayResult;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.utils.OrderInfoUtil2_0;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.RxBus;
import com.tuoying.hykc.utils.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class CZInputMoneyActivity extends BaseActivity {
    private Toolbar mToolbar;
    private Button mBtnNext;
    private EditText mEditMoney;
    private static final int SDK_PAY_FLAG = 1;
    private static final int SDK_AUTH_FLAG = 2;
    private String userid;
    private DBDao dao;
    private CheckBox mChCheckBoxZFB;
    private CheckBox mChCheckBoxWX;
    private RelativeLayout mLayoutZFB;
    private RelativeLayout mLayoutWX;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_cz_inputmoney);
        init();
    }

    @Override
    public void init() {
        dao = new DBDaoImpl(this);
        userid = SharePreferenceUtil.getInstance(this).getUserId();
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("充值");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mChCheckBoxZFB = findViewById(R.id.checkBox_zfb);
        mChCheckBoxWX = findViewById(R.id.checkBox_wx);
        mLayoutZFB = findViewById(R.id.layout_zfb);
        mLayoutWX = findViewById(R.id.layout_wx);
        mBtnNext = findViewById(R.id.btn_next);
        mEditMoney = findViewById(R.id.editMoney);
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mChCheckBoxWX.isChecked()){
                    doWx();
                }else if(mChCheckBoxZFB.isChecked()){
                    doCZ();
                }

            }
        });
        mLayoutWX.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChCheckBoxZFB.setChecked(false);
                mChCheckBoxWX.setChecked(true);
            }
        });
        mLayoutZFB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mChCheckBoxZFB.setChecked(true);
                mChCheckBoxWX.setChecked(false);
            }
        });
        mChCheckBoxZFB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mChCheckBoxWX.setChecked(false);
                }
            }
        });
        mChCheckBoxWX.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mChCheckBoxZFB.setChecked(false);
                }

            }
        });
    }

    private void doWx() {
        String money = mEditMoney.getText().toString();
        if (TextUtils.isEmpty(money)) {
            Toast.makeText(this, "充值金额不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        double strToDoubleMoney = 0.00;
        try {
            strToDoubleMoney = Double.valueOf(money);
        } catch (NumberFormatException e) {
            return;
        }
        if (strToDoubleMoney == 0.00) {
            Toast.makeText(this, "充值金额不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mChCheckBoxWX.isChecked()) {
            Toast.makeText(this, "请选择支付方式！", Toast.LENGTH_SHORT).show();
            return;
        }
        final String strMoney = String.format("%.2f", strToDoubleMoney);
        getWxOrderNo(strMoney);

    }

    private void getWxOrderNo(String strMoney) {
        if (!TextUtils.isEmpty(userid)) {
            User user = dao.findUserInfoById(userid);
            if (user != null) {
                Map<String, String> map = new HashMap<>();
                map.put("token", user.getToken());
                map.put("mobile", "cp-"+userid);
                map.put("app", Constants.AppId);
                map.put("paytype", Constants.PAYTYPE_WX);
                map.put("amount", strMoney);
                RequestManager.getInstance()
                        .mServiceStore
                        .createWXPayOrder(map)
                        .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                            @Override
                            public void onSuccess(String msg) {
                                Log.e("wxpay===", msg.trim());
                                String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                                try {
                                    JSONObject object0 = new JSONObject(str.trim());
                                    if (object0.getString("success").equals("true")) {
                                        JSONObject object = new JSONObject(object0.getString("sign"));
                                        String appId = object.getString("appid");
                                        String nonceStr = object.getString("noncestr");
                                        String prepay_id = object.getString("prepayid");
                                        String paySign = object.getString("paysign");
                                        String timeStamp = object.getString("timestamp");
//                                        String timeStamp = getTime();
                                        Log.e("解析数据==>", "\nAPPid:" + appId + "\n预支付ID:" + prepay_id + "\n随机字符串:" + nonceStr + "\n时间戳:" + timeStamp + "\n签名:" + paySign);

                                        final IWXAPI msgApi = WXAPIFactory.createWXAPI(CZInputMoneyActivity.this, null);
                                        // 将该app注册到微信
                                        msgApi.registerApp("wx9e92b64666bcd9cb");
                                        boolean isPaySupported = msgApi.getWXAppSupportAPI() >= Build.PAY_SUPPORTED_SDK_INT;
                                        if (isPaySupported) {
                                            PayReq request = new PayReq();
                                            request.appId = "wx9e92b64666bcd9cb";//应用ID
                                            request.partnerId = "1508252301";//商户号
                                            request.prepayId = prepay_id;//预支付交易会话ID
                                            request.packageValue = "Sign=WXPay";//扩展字段 固定值
                                            request.nonceStr = nonceStr;//随机字符串
                                            request.timeStamp = timeStamp;//时间戳
                                            request.sign = paySign;//签名
                                            Log.e("上传数据==>", "\nappId:" + request.appId + "\npartnerId商户号:" + request.partnerId + "\nprepayId预支付ID:" + request.prepayId + "\npackageValue扩展字段:" + request.packageValue + "\nnonceStr随机字符串:" + request.nonceStr + "\ntimeStamp时间戳:" + request.timeStamp + "\nsign签名:" + request.sign);
                                            msgApi.sendReq(request);
                                        } else {
                                            Toast.makeText(CZInputMoneyActivity.this, "本设备不支持微信支付", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } catch (Exception e) {
                                    System.out.println("wx====" + e);
                                } finally {

                                }
                            }

                            @Override
                            public void onError(String msg) {
                                Log.e("wxpay_onError===", msg);
                            }
                        }));
            }
        }


    }


    //执行充值操作
    private void doCZ() {
        String money = mEditMoney.getText().toString();
        if (TextUtils.isEmpty(money)) {
            Toast.makeText(this, "充值金额不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        double strToDoubleMoney = 0.00;
        try {
            strToDoubleMoney = Double.valueOf(money);
        } catch (NumberFormatException e) {
            return;
        }
        if (strToDoubleMoney == 0.00) {
            Toast.makeText(this, "充值金额不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!mChCheckBoxZFB.isChecked()) {
            Toast.makeText(this, "请选择支付方式！", Toast.LENGTH_SHORT).show();
            return;
        }
        final String strMoney = String.format("%.2f", strToDoubleMoney);
        getorderno(strMoney);
    }


    private void getorderno(final String strMoney) {
        if (!TextUtils.isEmpty(userid)) {
            com.tuoying.hykc.entity.User user = dao.findUserInfoById(userid);
            if (user != null) {
               Map<String, String> map = new HashMap<>();
               map.put("mobile",userid);
               map.put("app", Constants.AppId);
               map.put("token",user.getToken());
               map.put("pay_type","zfb");
               map.put("amount",strMoney);
                com.tuoying.hykc.utils.RequestManager.getInstance()
                        .mServiceStore
                        .create_order(map)
                        .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new com.tuoying.hykc.utils.ResultObserver(new com.tuoying.hykc.utils.RequestManager.onRequestCallBack() {
                            @Override
                            public void onSuccess(String msg) {
                                Log.e("CZInputMoneyActivity", msg);
                                String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                                try {
                                    JSONObject object = new JSONObject(str);
                                    if (object.getBoolean("success")) {
                                        String orderno = object.getString("order_no");
                                        doPayTask(strMoney, orderno);
                                    } else {
                                        Toast.makeText(CZInputMoneyActivity.this, "获取信息失败！", Toast.LENGTH_SHORT).show();
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                } finally {

                                }
                            }

                            @Override
                            public void onError(String msg) {

                            }
                        }));

            }

        }
    }

    private void doPayTask(final String money, final String orderno) {
        if (TextUtils.isEmpty(Constants.ZFB_APP_ID) || (TextUtils.isEmpty(Constants.PRIVATE_KEY))) {
            Toast.makeText(this, "APP_ID或PRIVATE_KEY不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        Log.e("money", money);
        boolean rsa2 = (Constants.PRIVATE_KEY.length() > 0);
        Map<String, String> params = OrderInfoUtil2_0.buildOrderParamMap(Constants.ZFB_APP_ID, rsa2, money, orderno);
        String orderParam = OrderInfoUtil2_0.buildOrderParam(params);
        String privateKey = rsa2 ? Constants.PRIVATE_KEY : Constants.PRIVATE_KEY;
        String sign = OrderInfoUtil2_0.getSign(params, privateKey, rsa2);
        Log.e("sign", sign);
        final String orderInfo = orderParam + "&" + sign;
        Log.e("orderInfo", orderInfo);
        Runnable payRunnable = new Runnable() {

            @Override
            public void run() {
                PayTask alipay = new PayTask(CZInputMoneyActivity.this);
                Map<String, String> result = alipay.payV2(orderInfo, true);
                Log.i("msp", result.toString());
                Message msg = new Message();
                msg.what = SDK_PAY_FLAG;
                msg.obj = result;
                mHandler.sendMessage(msg);
            }
        };
        Thread payThread = new Thread(payRunnable);
        payThread.start();

    }
    private Handler mHandler = new Handler() {

        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SDK_PAY_FLAG: {
                    @SuppressWarnings("unchecked")
                    PayResult payResult = new PayResult((Map<String, String>) msg.obj);
                    String resultInfo = payResult.getResult();// 同步返回需要验证的信息
                    String resultStatus = payResult.getResultStatus();
                    Log.e("resultInfo","========"+resultInfo);

                    // 判断resultStatus 为9000则代表支付成功
                    if (TextUtils.equals(resultStatus, "9000")) {
                        // 该笔订单是否真实支付成功，需要依赖服务端的异步通知。
                        Toast.makeText(CZInputMoneyActivity.this, "支付成功", Toast.LENGTH_SHORT).show();
                        RxBus.getInstance().send(new EventEntity("money","money"));
                        finish();
                        overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);


                    } else {
                        // 该笔订单真实的支付结果，需要依赖服务端的异步通知。
                        Toast.makeText(CZInputMoneyActivity.this, "支付失败", Toast.LENGTH_SHORT).show();
                    }
                    break;
                }
                case SDK_AUTH_FLAG: {

                    break;
                }
                default:
                    break;
            }
        }

        ;
    };


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
        mHandler.removeCallbacksAndMessages(null);
    }
}
