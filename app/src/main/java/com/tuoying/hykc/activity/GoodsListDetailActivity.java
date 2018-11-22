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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tuoying.hykc.R;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.EventEntity;
import com.tuoying.hykc.entity.GoodsEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.RxBus;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.ExitDialogFragment;
import com.tuoying.hykc.view.LoadingDialogFragment;
import com.tuoying.hykc.view.PayMoneyDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class GoodsListDetailActivity extends BaseActivity {
    private Toolbar mToolbar;
    private String userid;
    private DBDao dao;
    private User user;
    private RelativeLayout mLayoutNoMsg;
    private RelativeLayout mLayoutLoading;
    private int type;
    private GoodsEntity entity;
    private Intent mIntent;
    private TextView mTextFhrName;
    private TextView mTextPrice;
    private TextView mTextHwmc;
    private TextView mTextFhrTel;
    private TextView mTextZl;
    private TextView mTextTj;
    private TextView mTextStart;
    private TextView mTextEnd;
    private TextView mTextBz;
    private TextView mTextShrName;
    private TextView mTextShrTel;
    private TextView mTextTime;
    private TextView mTextStatue;
    private Button mBtnOrder;
    private RelativeLayout mLayoutWJSYF;
    private TextView mTextWJSYF;
    private RelativeLayout mLayoutHSHC;
    private TextView mTextHSHC;
    private TextView mTextJSYFBZ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_goods_detail);
        init();
    }

    @Override
    public void init() {
        userid = SharePreferenceUtil.getInstance(this).getUserId();
        dao = new DBDaoImpl(this);
        if(!TextUtils.isEmpty(userid)){
            user=dao.findUserInfoById(userid);
        }
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("详情");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mIntent = getIntent();
        type = mIntent.getIntExtra("type", 0);
        entity = (GoodsEntity) mIntent.getSerializableExtra("entity");
        initViews();
        initDatas();
    }

    private void initViews() {
        mLayoutNoMsg = findViewById(R.id.layout_nomsg);
        mLayoutLoading = findViewById(R.id.layout_loading);
        mTextFhrName = findViewById(R.id.tv_hz);
        mTextPrice = findViewById(R.id.tv_price);
        mTextHwmc = findViewById(R.id.tv_hwmc);
        mTextFhrTel = findViewById(R.id.tv_fhr_tel);
        mTextZl = findViewById(R.id.tv_zl);
        mTextTj = findViewById(R.id.tv_tj);
        mTextStart = findViewById(R.id.tv_start);
        mTextEnd = findViewById(R.id.tv_end);
        mTextBz = findViewById(R.id.tv_bz);
        mTextShrName = findViewById(R.id.tv_shr);
        mTextShrTel = findViewById(R.id.tv_shrdh);
        mTextTime = findViewById(R.id.tv_creatTime);
        mTextStatue = findViewById(R.id.tv_statue);
        mBtnOrder=findViewById(R.id.btn_order);
        mLayoutWJSYF=findViewById(R.id.layout_wjsyf);
        mLayoutWJSYF.setVisibility(View.GONE);
        mTextWJSYF=findViewById(R.id.tv_wjsyf);
        mLayoutHSHC=findViewById(R.id.layout_hshc);
        mLayoutHSHC.setVisibility(View.GONE);
        mTextHSHC=findViewById(R.id.tv_hshc);
        mTextJSYFBZ=findViewById(R.id.tv_jsyfbz);
        if(type==100){
            mBtnOrder.setText("接单");
            mBtnOrder.setVisibility(View.VISIBLE);
        }
        mBtnOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(user!=null){
                    String rzType = user.getRz();
                    if ("0".equals(rzType)) {
                        confirmRZTips("用户认证未通过！");
                    } else if ("1".equals(rzType)) {
                        doOrder(entity);
                    } else if (TextUtils.isEmpty(rzType)) {
                        confirmRZTips("用户未认证！");
                    }

                }
            }
        });
    }
    private void confirmRZTips(String msg) {
        //退出操作
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @Override
            public void onClickOk() {
                Intent intent = new Intent(GoodsListDetailActivity.this, RzTextActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });
        dialog.show(getSupportFragmentManager(), "confirmRZTips333");

    }

    private void initDatas() {
        if (!TextUtils.isEmpty(userid)) {
            User user = dao.findUserInfoById(userid);
            if (user != null && entity != null) {
                getGoodsInfo(user);
            }
        }
    }

    private void getGoodsInfo(User user) {
        mLayoutLoading.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("mobile", userid);
        map.put("app", Constants.AppId);
        map.put("token", user.getToken());
        map.put("rowid", entity.getRowid());
        Log.e("rowid", "rowid===" + entity.getRowid());
        RequestManager.getInstance()
                .mServiceStore
                .get_source_detail(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        mLayoutLoading.setVisibility(View.GONE);
                        Log.e("get_source onSuccess", "====" + msg);
                        if (TextUtils.isEmpty(msg) || "[]".equals(msg)) {
                            mLayoutNoMsg.setVisibility(View.VISIBLE);
                            return;
                        }
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject object = new JSONObject(str);
                            setInfos(object);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {

                        }
                    }

                    @Override
                    public void onError(String msg) {
                        mLayoutLoading.setVisibility(View.GONE);
                        Log.e("onError", "====" + msg);
                        Toast.makeText(GoodsListDetailActivity.this, "查询失败！", Toast.LENGTH_SHORT).show();
                    }
                }));

    }

    private void setInfos(JSONObject object) {

        try {
            String fhrName = object.getString("fhr");
            mTextFhrName.setText(fhrName);
            String price = object.getString("yf");
            if(type==200){
                double bl= Double.parseDouble(entity.getBl());
                double yf=Double.parseDouble(entity.getZyf());
                double p=yf*(1-bl);
                String strMoney = String.format("%.2f", p);
                mTextPrice.setText(strMoney+"元");
            }else {
                String pdwlgs="-1";
                if(object.has("pdwlgs")){
                    pdwlgs=object.getString("pdwlgs");
                }else {
                    pdwlgs="-1";
                }

                if(!TextUtils.isEmpty(pdwlgs)){
                    if("-1".equals(pdwlgs)){
                        double bl= Double.parseDouble(entity.getBl());
                        double yf=Double.parseDouble(entity.getZyf());
                        double p=yf*(1-bl);
                        String strMoney = String.format("%.2f", p);
                        mTextPrice.setText(strMoney+"元");
                    }else {
/*                double bl= Double.parseDouble(entity.getBl());
                double d = Double.valueOf(entity.getZyf());
                String str=String.format("%.2f", (1-bl)*d);*/
                        mTextPrice.setText("****元");
                    }

                }else {
                    double bl= Double.parseDouble(entity.getBl());
                    double d = Double.valueOf(entity.getZyf());
                    String str=String.format("%.2f", (1-bl)*d);
                    mTextPrice.setText(str+"元");
                }
            }

            String hwmc = object.getString("hwmc");
            mTextHwmc.setText(hwmc);
            String fhrTel = object.getString("fhrdh");
            mTextFhrTel.setText(fhrTel);
            String zl = object.getString("hwzl");
            mTextZl.setText(zl);
            String tj = object.getString("hwtj");
            mTextTj.setText(tj);
            String fromeCity = object.getString("from_city");
            String fromeCounty = object.getString("from_county");
            mTextStart.setText(fromeCity + " " + fromeCounty);
            String end = object.getString("to_addr");
            mTextEnd.setText(end);
            String bz = object.getString("bz");
            mTextBz.setText(bz);
            String shrxm = object.getString("shr");
            mTextShrName.setText(shrxm);
            String shrTel = object.getString("shrdh");
            mTextShrTel.setText(shrTel);
            String time = object.getString("create_time");
            mTextTime.setText(time);
            String statue = object.getString("yd_trans_status");
            mTextStatue.setText(statue);
            if("待结算尾款".equals(statue)){
                if (object.has("yf_detain")){
                    mLayoutWJSYF.setVisibility(View.VISIBLE);
                    mTextWJSYF.setText(object.getString("yf_detain"));
                }
            }
            if(object.has("yf_difference")){
                String yf_difference=object.getString("yf_difference");
                if(!TextUtils.isEmpty(yf_difference)){
                    mLayoutHSHC.setVisibility(View.VISIBLE);
                    mTextHSHC.setText(yf_difference);
                }
            }
            if(object.has("yf_remark")){
                String yf_remark=object.getString("yf_remark");
                if(!TextUtils.isEmpty(yf_remark)){
                    mTextJSYFBZ.setText(yf_remark);
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private void doOrder(final GoodsEntity entity) {
        final LoadingDialogFragment dialogFragment=LoadingDialogFragment.getInstance();
        dialogFragment.show(getSupportFragmentManager(),"orderLoading");
        Map<String, String> map = new HashMap<>();
        map.put("token", user.getToken());
        map.put("mobile", user.getUserId());
        map.put("app", Constants.AppId);
        map.put("rowid", entity.getRowid());
        RequestManager.getInstance()
                .mServiceStore
                .create_yd(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {

                        Log.e("doOrder","==="+msg);
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject object=new JSONObject(str);
                            if(object.getBoolean("success")){
                                String success=object.getString("message");
                                Toast.makeText(GoodsListDetailActivity.this, success, Toast.LENGTH_SHORT).show();
                                shoPayMoneyViews(entity.getZyf(),Double.parseDouble(entity.getBl()),entity);
                            }else {
                                String error=object.getString("message");
                                Toast.makeText(GoodsListDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            dialogFragment.dismiss();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        dialogFragment.dismiss();
                        Log.e("query_sources onError", msg);
                    }
                }));
    }

    private void shoPayMoneyViews(String price,double bl,final GoodsEntity entity) {
        final PayMoneyDialog dialog=PayMoneyDialog.getInstance(price,bl,entity.getSid(),user.getUserName(),entity);
        dialog.show(getSupportFragmentManager(),"paymoneydialog");
        dialog.setOnOrderListener(new PayMoneyDialog.OnOrderListener() {
            @Override
            public void onOrder(String psd,String money) {
                dialog.dismiss();
                if(user!=null){
                    doPayMoney(psd,money,entity);
                }
            }
        });
        dialog.setOnDismissListener(new PayMoneyDialog.onDismissListener() {
            @Override
            public void onDismiss() {
                dialog.dismiss();
                RxBus.getInstance().send(new EventEntity("刷新","刷新"));
            }
        });
    }

    private void doPayMoney(final String psd,final String money,final GoodsEntity entity){
        final   LoadingDialogFragment loadingDialogFragment=LoadingDialogFragment.getInstance();
        loadingDialogFragment.show(getSupportFragmentManager(),"paydialog");
        Map<String,String> map=new HashMap<>();
        map.put("mobile",user.getUserId());
        map.put("app",Constants.AppId);
        map.put("token",user.getToken());
        map.put("amount",money);
        map.put("pwd",psd);
        map.put("req_type","DEPS");
        map.put("type_name","运输保证金支付");
        JSONObject object=new JSONObject();
        try {
            object.put("rowid",entity.getRowid());
            object.put("flag","driver_deps");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        map.put("ext",object.toString());
        RequestManager.getInstance()
                .mServiceStore
                .create_request(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {

                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject jsonObject=new JSONObject(str);
                            if(jsonObject.getBoolean("success")){
                                String orderno=jsonObject.getString("order_no");
                                String tokenid=jsonObject.getString("tokenid");
                                submit_order(orderno,tokenid,entity.getRowid(),loadingDialogFragment);
                            }else {
                                String error=jsonObject.getString("message");

                                RxBus.getInstance().send(new EventEntity("刷新","刷新"));
                                loadingDialogFragment.dismiss();
                                Toast.makeText(GoodsListDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                                Log.e("create_request onError", msg);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(String msg) {
                        RxBus.getInstance().send(new EventEntity("刷新","刷新"));
                        loadingDialogFragment.dismiss();
                        Toast.makeText(GoodsListDetailActivity.this, "支付失败！", Toast.LENGTH_SHORT).show();
                        Log.e("create_request onError", msg);
                    }
                }));
    }


    private void submit_order(String orderno, String tokenid, final String rowid, final  LoadingDialogFragment loadingDialogFragment){
        Map<String,String> map=new HashMap<>();
        map.put("mobile",user.getUserId());
        map.put("app",Constants.AppId);
        map.put("token",user.getToken());
        map.put("tokenid",tokenid);
        map.put("order_no",orderno);
        map.put("req_type","DEPS");
        map.put("rowid",rowid);
        RequestManager.getInstance()
                .mServiceStore
                .submit_order(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        loadingDialogFragment.dismiss();
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject jsonObject=new JSONObject(str);
                            if(jsonObject.getBoolean("success")){
                                //doAnsycWayBill(rowid);
                                Toast.makeText(GoodsListDetailActivity.this, "支付成功！", Toast.LENGTH_SHORT).show();
                                RxBus.getInstance().send(new EventEntity("刷新","刷新"));
                                mBtnOrder.setEnabled(false);
                                mBtnOrder.setClickable(false);
                                mBtnOrder.setBackgroundResource(R.drawable.btn_clickable_false_bg);
                                finish();
                                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                            }else {
                                String error=jsonObject.getString("message");
                                RxBus.getInstance().send(new EventEntity("刷新","刷新"));
                                loadingDialogFragment.dismiss();
                                Toast.makeText(GoodsListDetailActivity.this, error, Toast.LENGTH_SHORT).show();
                                Log.e("create_request onError", msg);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String msg) {

                        RxBus.getInstance().send(new EventEntity("刷新","刷新"));
                        loadingDialogFragment.dismiss();
                        Toast.makeText(GoodsListDetailActivity.this, "支付失败！", Toast.LENGTH_SHORT).show();
                        Log.e("create_request onError", msg);
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
}
