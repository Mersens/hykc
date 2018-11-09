package com.tuoying.hykc.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.tuoying.hykc.R;
import com.tuoying.hykc.adapter.MyCardAdapter;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.CardEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.BankBandDialog;
import com.tuoying.hykc.view.LoadingDialogFragment;
import com.tuoying.hykc.view.ZFB_WXBandDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class MyCardActivity extends BaseActivity {
    private Toolbar mToolbar;
    private ListView mListView;
    private RelativeLayout mLayoutNo;
    private MyCardAdapter adapter;
    private DBDao dao;
    private String userid;
    private TextView mTextEdit;
    private List<CardEntity> mCardList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_my_card);
        init();
    }

    @Override
    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("我的卡包");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mTextEdit=mToolbar.findViewById(R.id.tv_edit);
        dao = new DBDaoImpl(this);
        userid = SharePreferenceUtil.getInstance(this).getUserId();
        mLayoutNo = findViewById(R.id.layout_card_nomsg);
        mListView = findViewById(R.id.cardListView);
        mTextEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //编辑
                if(adapter!=null){
                    String string=mTextEdit.getText().toString().trim();
                    if("编辑".equals(string)){
                        adapter.showDel();
                        mTextEdit.setText("完成");
                    }else {
                        adapter.hideDel();
                        mTextEdit.setText("编辑");
                    }

                }
            }
        });
        initDatas();
    }

    private void initDatas() {
        if (!TextUtils.isEmpty(userid)) {
            User user = dao.findUserInfoById(userid);
            if (user != null) {
                getMyCardInfo(user);
            }
        }

    }

    private void getMyCardInfo(final User user) {
         final LoadingDialogFragment selectDialogFragment = LoadingDialogFragment.getInstance();
        selectDialogFragment.showF(getSupportFragmentManager(), "selectDialogFragment");
       RequestManager.getInstance()
                .mServiceStore
               .findMyRz(user.getToken(), user.getUserId(), Constants.AppId)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("onSuccess getMyCardInfo", "====" + msg);
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject object=new JSONObject(str);
                            if (!object.has("acct_info")) {
                                mLayoutNo.setVisibility(View.VISIBLE);
                                mTextEdit.setVisibility(View.GONE);
                                if(selectDialogFragment!=null){
                                    selectDialogFragment.dismissAllowingStateLoss();

                                }
                                return;
                            } else {
                                mLayoutNo.setVisibility(View.GONE);
                                mTextEdit.setVisibility(View.VISIBLE);
                                if(selectDialogFragment!=null){
                                    selectDialogFragment.dismissAllowingStateLoss();

                                }
                                analysisJson(str);

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            if(selectDialogFragment!=null){
                                selectDialogFragment.dismissAllowingStateLoss();

                            }
                        }

                    }

                    @Override
                    public void onError(String msg) {
                        Toast.makeText(MyCardActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.e("onError", "====" + msg);
                        if(selectDialogFragment!=null){
                            selectDialogFragment.dismissAllowingStateLoss();

                        }

                    }
                }));
    }

    private void analysisJson(String str) {
        mCardList.clear();
        try {
            JSONObject object1=new JSONObject(str);
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

            if (adapter == null) {
                adapter = new MyCardAdapter(MyCardActivity.this, mCardList);
                mListView.setAdapter(adapter);
                adapter.setOnCardItemClickListener(new MyCardAdapter.OnCardItemClickListener() {
                    @Override
                    public void onCardItemClick(int pos, CardEntity entity) {

                    }

                    @Override
                    public void onCardDelClick(int pos, CardEntity entity) {
                        cardDel(pos,entity);
                    }

                });
            } else {
                adapter.setList(mCardList);
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void cardDel(int pos, CardEntity entity){
        mCardList.remove(entity);
        adapter.setList(mCardList);
        if (!TextUtils.isEmpty(userid)) {
            User user = dao.findUserInfoById(userid);
            if (user != null) {
                delCardInfo(entity,user);
            }
        }

    }

    private void delCardInfo(CardEntity entity,User user) {
        Map<String,String> map=new HashMap<>();
        map.put("rowid","USER-"+user.getUserId()+"-"+Constants.AppId);
        map.put("token",user.getToken());
        map.put("mobile",user.getUserId());
        map.put("app",Constants.AppId);
        map.put("type",entity.getType());
        map.put("account",entity.getAccount());
        RequestManager.getInstance()
                .mServiceStore
                .deleteAccount(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("delCardInfo", "====" + msg);
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        if (str.equals("[]")) {
                        } else {
                            try {
                                JSONObject object1 = new JSONObject(str);
                                if (object1.has("success")) {
                                    boolean isSuccess = object1.getBoolean("success");
                                    if (isSuccess) {
                                        Toast.makeText(MyCardActivity.this, "删除成功！", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String msg1=object1.getString("message");
                                        Toast.makeText(MyCardActivity.this, msg1, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        Toast.makeText(MyCardActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.e("onError", "====" + msg);

                    }
                }));


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.card_type, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(adapter!=null){
            adapter.hideDel();
            mTextEdit.setText("编辑");
        }

        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            return true;
        } else if (id == R.id.type_zfb) {
            bandType(1);
        } else if (id == R.id.type_wx) {
            bandType(2);

        } else if (id == R.id.type_yhk) {
            bandType(3);
        }
        return super.onOptionsItemSelected(item);
    }

    private void bandType(int i) {
        switch (i) {
            case 1:
                BandZFB(1);
                break;
            case 2:
                BandWX(2);
                break;
            case 3:
                BandYHK();
                break;
        }
    }

    private void BandYHK() {
        final BankBandDialog yhkDialog = BankBandDialog.getInstance(3);
        yhkDialog.show(getSupportFragmentManager(), "yhkDialog");
        yhkDialog.setOnOrderListener(new BankBandDialog.OnOrderListener() {
            @Override
            public void onOrder(String psd, String accountl, String bank, String address) {
                yhkDialog.dismiss();
                uploadBankBandInfo(true,psd, accountl, bank, address);
            }
        });
    }

    private void BandZFB(int i) {
        final ZFB_WXBandDialog zfbDialog = ZFB_WXBandDialog.getInstance(i);
        zfbDialog.show(getSupportFragmentManager(), "zfbDialog");
        zfbDialog.setOnOrderListener(new ZFB_WXBandDialog.OnOrderListener() {
            @Override
            public void onOrder(String name, String account) {
                zfbDialog.dismiss();
                uploadZFBOrWXBandInfo(true,1, name, account);
            }
        });

    }

    private void BandWX(int i) {
        final ZFB_WXBandDialog wxDialog = ZFB_WXBandDialog.getInstance(i);
        wxDialog.show(getSupportFragmentManager(), "wxDialog");
        wxDialog.setOnOrderListener(new ZFB_WXBandDialog.OnOrderListener() {
            @Override
            public void onOrder(String name, String account) {
                wxDialog.dismiss();
               uploadZFBOrWXBandInfo(true,2, name, account);
            }
        });
    }

    private void uploadBankBandInfo(boolean isAdd,String name, String account, String bank, String address) {
        String type = "银行卡";
        final User user = dao.findUserInfoById(userid);
        if (user == null) {
            return;
        }
        final LoadingDialogFragment loadingDialog = LoadingDialogFragment.getInstance();
        loadingDialog.show(getSupportFragmentManager(), "ZFBOrWXBandInfo");
        JSONObject object = new JSONObject();
        if(isAdd){

            try {
                object.put("name", name);
                object.put("type", type);
                object.put("account", account);
                object.put("bank", bank);
                object.put("address", address);
            } catch (JSONException e) {
                e.printStackTrace();
                loadingDialog.dismiss();
            }
        }
        RequestManager.getInstance()
                .mServiceStore
                .uploadCardInfo("USER-"+user.getUserId()+"-"+Constants.AppId,user.getToken(),user.getUserId(),Constants.AppId,object.toString())
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        loadingDialog.dismiss();
                        Log.e("uploadCardInfo", "====" + msg);
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        if (str.equals("[]")) {
                        } else {
                            try {
                                JSONObject object1 = new JSONObject(str);
                                if (object1.has("success")) {
                                    boolean isSuccess = object1.getBoolean("success");
                                    if (isSuccess) {
                                        initDatas();
                                        Toast.makeText(MyCardActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String msg1=object1.getString("message");
                                        Toast.makeText(MyCardActivity.this, msg1, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        Toast.makeText(MyCardActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.e("onError", "====" + msg);
                        loadingDialog.dismiss();
                    }
                }));
    }


    private void uploadZFBOrWXBandInfo(final boolean isAdd, int type, String name, String account) {
        final LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.getInstance();
        if (TextUtils.isEmpty(userid)) {
            return;

        }
        String t = null;
        if (type == 1) {
            t = "支付宝";

        } else if (type == 2) {
            t = "微信";
        }
        if (TextUtils.isEmpty(t)) {
            return;
        }

        final User user = dao.findUserInfoById(userid);
        if (user == null) {
            return;

        }
        loadingDialogFragment.show(getSupportFragmentManager(), "ZFBOrWXBandInfo");
        JSONObject object = new JSONObject();

        if(isAdd) {

            try {
                object.put("name", name);
                object.put("type", t);
                object.put("account", account);

            } catch (JSONException e) {
                e.printStackTrace();
                loadingDialogFragment.dismiss();
            }
        }
        RequestManager.getInstance()
                .mServiceStore
                .uploadCardInfo("USER-"+user.getUserId()+"-"+Constants.AppId,user.getToken(),user.getUserId(),Constants.AppId,object.toString())
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        loadingDialogFragment.dismiss();
                        Log.e("uploadCardInfo", "====" + msg);
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        if (str.equals("[]")) {
                        } else {
                            try {
                                JSONObject object1 = new JSONObject(str);
                                if (object1.has("success")) {
                                    boolean isSuccess = object1.getBoolean("success");
                                    if (isSuccess) {
                                        initDatas();
                                        Toast.makeText(MyCardActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                                    } else {
                                        String msg1=object1.getString("message");
                                        Toast.makeText(MyCardActivity.this, msg1, Toast.LENGTH_SHORT).show();
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        Toast.makeText(MyCardActivity.this, msg, Toast.LENGTH_SHORT).show();
                        Log.e("onError", "====" + msg);
                        loadingDialogFragment.dismiss();
                    }
                }));
    }



}
