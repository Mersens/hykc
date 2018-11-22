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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tuoying.hykc.R;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.CardEntity;
import com.tuoying.hykc.entity.EventEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.RxBus;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.ExitDialogFragment;
import com.tuoying.hykc.view.InputPsdDialog;
import com.tuoying.hykc.view.LoadingDialogFragment;
import com.tuoying.hykc.view.TXMoneyDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class TXInputMoneyActivity extends BaseActivity {
    protected CardEntity entity;
    private Toolbar mToolbar;
    private EditText mEditMoney;
    private TextView mTextYue;
    private TextView mTextAllTx;
    private Button mBtnOk;
    private ImageView mImageType;
    private TextView mTextAccount;
    private TextView mTextType;
    private RelativeLayout mLayoutType;
    private String userid;
    private DBDao dao;
    private TextView mTextHistory;
    private List<CardEntity> mCardList = new ArrayList<>();
    private boolean isAllow = false;
    private TextView mTextNoCard;
    private double mYe;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tx_inputmoney);
        init();
    }

    @Override
    public void init() {
        initViews();
        initEvent();
        initDatas();
    }

    private void initDatas() {
        if (!TextUtils.isEmpty(userid)) {
            User user = dao.findUserInfoById(userid);
            getMoneyInfo(user);
        }
    }

    private void initViews() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("提现");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mTextHistory = mToolbar.findViewById(R.id.tv_right_title);
        mEditMoney = findViewById(R.id.editMoney);
        mTextYue = findViewById(R.id.tv_yue);
        mTextAllTx = findViewById(R.id.tv_alltx);
        mBtnOk = findViewById(R.id.btn_ok);
        mImageType = findViewById(R.id.img_type);
        mTextAccount = findViewById(R.id.tv_zh);
        mTextType = findViewById(R.id.tv_type);
        mLayoutType = findViewById(R.id.layout_type);
        mTextNoCard = findViewById(R.id.tv_nocard);
        dao = new DBDaoImpl(this);
        userid = SharePreferenceUtil.getInstance(this).getUserId();

    }

    private void initEvent() {
        mTextHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TXInputMoneyActivity.this, TXHistoryActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        mTextAllTx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ye = mTextYue.getText().toString();
                String mYe = ye.substring(0, ye.length() - 1);
                if (!"0.00".equals(mYe) && !"0".equals(mYe)) {
                    mEditMoney.setText(mYe);
                }
            }
        });
        mLayoutType.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (entity != null) {
                    showViews();

                }

            }
        });
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dotx();
            }
        });
    }

    private void dotx() {

        String money = mEditMoney.getText().toString();
        if (TextUtils.isEmpty(money)) {
            Toast.makeText(this, "提现金额不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        double strToDoubleMoney = 0.00;
        try {
            strToDoubleMoney = Double.valueOf(money);
        } catch (NumberFormatException e) {
            return;
        }

        if (strToDoubleMoney == 0.00) {
            Toast.makeText(this, "提现金额不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(strToDoubleMoney>mYe){
            Toast.makeText(this, "  请输入正确金额！", Toast.LENGTH_SHORT).show();
            return;
        }
        final String strMoney = String.format("%.2f", strToDoubleMoney);
        final InputPsdDialog inputPsdDialog = InputPsdDialog.getInstance();
        inputPsdDialog.show(getSupportFragmentManager(), "inputPsdDialog");
        inputPsdDialog.setOnOrderListener(new InputPsdDialog.OnOrderListener() {
            @Override
            public void onOrder(String psd) {
                inputPsdDialog.dismiss();
                uploadInfo(strMoney, psd);
            }
        });


    }

    private void uploadInfo(String strMoney, String psd) {
        if (entity == null) {
            return;
        }
        User user = dao.findUserInfoById(userid);
        if (user == null) {
            return;
        }
        final LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.getInstance();
        loadingDialogFragment.show(getSupportFragmentManager(), "txloading");
        String t = null;
        String ty = entity.getType();
        Log.e("type", ty + "======");
        if ("支付宝".equals(ty)) {
            t = "1";
        } else if ("微信".equals(ty)) {
            t = "2";
        } else if ("银行卡".equals(ty)) {
            t = "3";
        }
        Map<String, String> map = new HashMap<>();
        map.put("mobile", userid);
        map.put("token", user.getToken());
        map.put("app", Constants.AppId);
        map.put("pwd", psd);
        map.put("amount", strMoney);
        map.put("req_type", "CASH");
        map.put("type_name", "提现");

        JSONObject object = new JSONObject();
        try {
            object.put("name", entity.getName());
            object.put("type", entity.getType());
            object.put("account", entity.getAccount());
            if (!TextUtils.isEmpty(entity.getAddress()) && !TextUtils.isEmpty(entity.getBank())) {
                map.put("bank", entity.getBank());
                map.put("address", entity.getAddress());
                object.put("bank", entity.getBank());
                object.put("address", entity.getAddress());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String msg = object.toString();
        Log.e("msg", msg + "======");
        map.put("ext", msg);
        RequestManager.getInstance()
                .mServiceStore
                .create_request(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        loadingDialogFragment.dismiss();
                        Log.e(" uploadTXInfo onSuccess", "====" + msg);
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject object = new JSONObject(str);
                            if (object.getBoolean("success")) {
                                Toast.makeText(TXInputMoneyActivity.this, "提现申请提交成功！", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(TXInputMoneyActivity.this, TXHistoryActivity.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                            } else {
                                String errorMsg = object.getString("message");
                                Toast.makeText(TXInputMoneyActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {

                        }
                    }

                    @Override
                    public void onError(String msg) {
                        loadingDialogFragment.dismiss();
                        Log.e("onError", "====" + msg);
                        Toast.makeText(TXInputMoneyActivity.this, "提现申请提交失败！", Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void showViews() {
        TXMoneyDialog txDialog = TXMoneyDialog.getInstance(mCardList);
        txDialog.show(getSupportFragmentManager(), "txDialog");
        txDialog.setOnSelectListener(new TXMoneyDialog.OnSelectListener() {
            @Override
            public void onSelect(int pos, CardEntity entity) {
                TXInputMoneyActivity.this.entity = entity;
                setCardInfo(entity);
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

    private void getMoneyInfo(final User user) {

        RequestManager.getInstance()
                .mServiceStore
                .findMyRz(user.getToken(), user.getUserId(), Constants.AppId)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("getRzInfo onSuccess", msg);
                        boolean isSuccess = false;
                        try {
                            JSONObject jsonObject = new JSONObject(msg.replaceAll("\r", "").replaceAll("\n", ""));

                            if (jsonObject.has("balance")) {
                                String money = jsonObject.getString("balance");
                                mTextYue.setText(money + "元");
                                mYe=Double.valueOf(money);
                            }
                            if (jsonObject.has("acct_info")) {
                                analysisJson(jsonObject.toString());
                            } else {
                                mTextNoCard.setVisibility(View.VISIBLE);
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


    private void analysisJson(String str) {
        mCardList.clear();
        try {
            JSONObject object1 = new JSONObject(str);
            JSONArray array = new JSONArray(object1.getString("acct_info"));
            for (int i = 0; i < array.length(); i++) {
                CardEntity entity = new CardEntity();
                JSONObject object = array.getJSONObject(i);
                if (object.has("bank") && object.has("address")) {
                    entity.setAddress(object.getString("address"));
                    entity.setBank(object.getString("bank"));
                }
                entity.setName(object.getString("name"));
                entity.setType(object.getString("type"));
                entity.setAccount(object.getString("account"));
                mCardList.add(entity);
            }
            setDefaultCardInfo();

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void setDefaultCardInfo() {
        if (mCardList.size() > 0) {
            entity = mCardList.get(0);
        }
        setCardInfo(entity);
    }

    private void setCardInfo(CardEntity entity) {
        if (entity != null) {
            if (!TextUtils.isEmpty(entity.getAddress()) && !TextUtils.isEmpty(entity.getBank())) {
                mImageType.setImageResource(R.mipmap.ic_yjk);
                String num = entity.getAccount();
                mTextAccount.setText(num.substring(num.length() - 4, num.length()));
                mTextType.setText(entity.getType());

            } else {
                if ("支付宝".equals(entity.getType())) {
                    mImageType.setImageResource(R.mipmap.icon_zfb);
                    String num = entity.getAccount();
                    mTextAccount.setText(num);
                    mTextType.setText(entity.getType());
                } else if ("微信".equals(entity.getType())) {
                    mImageType.setImageResource(R.mipmap.weixin);
                    String num = entity.getAccount();
                    mTextAccount.setText(num);
                    mTextType.setText(entity.getType());
                }
            }
        }
    }



}
