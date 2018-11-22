package com.tuoying.hykc.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import com.tuoying.hykc.R;
import com.tuoying.hykc.adapter.BalanceDetailAdapter;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.JLEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.swipelayout.OnLoadMoreListener;
import com.tuoying.swipelayout.OnRefreshListener;
import com.tuoying.swipelayout.SwipeToLoadLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class TXHistoryActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener {
    private Toolbar mToolbar;
    private String userid;
    private DBDao dao;
    private RelativeLayout mLayoutLoading;
    private RelativeLayout mLayoutNoMsg;
    private ListView mListView;
    private View header;
    private View footer;
    private LayoutInflater mInflater;
    private SwipeToLoadLayout mSwipeToLoadLayout;
    private List<JLEntity> mList=new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_tx_history);
        init();
    }

    @Override
    public void init() {
        userid = SharePreferenceUtil.getInstance(this).getUserId();
        dao = new DBDaoImpl(this);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("提现记录");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mListView = findViewById(R.id.swipe_target);
        mLayoutLoading = findViewById(R.id.layout_loading);
        mLayoutNoMsg = findViewById(R.id.layout_nomsg);
        mLayoutLoading.setVisibility(View.GONE);
        mInflater = LayoutInflater.from(this);
        mSwipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        footer = mInflater.inflate(R.layout.layout_classic_footer, mSwipeToLoadLayout, false);
        header = mInflater.inflate(R.layout.layout_twitter_header, mSwipeToLoadLayout, false);
        mSwipeToLoadLayout.setSwipeStyle(SwipeToLoadLayout.STYLE.CLASSIC);
        mSwipeToLoadLayout.setLoadMoreFooterView(footer);
        mSwipeToLoadLayout.setRefreshHeaderView(header);
        mSwipeToLoadLayout.setOnRefreshListener(this);
        mSwipeToLoadLayout.setOnLoadMoreListener(this);
        initDatas();
    }

    private void initDatas() {
        if (!TextUtils.isEmpty(userid)) {
            User user = dao.findUserInfoById(userid);
            if (user != null) {
                getTXHistory(user);

            }
        }
    }
    private void getTXHistory(User user) {
        mLayoutLoading.setVisibility(View.VISIBLE);
        Map<String, String> map = new HashMap<>();
        map.put("mobile", userid);
        map.put("token", user.getToken());
        map.put("app", Constants.AppId);
        map.put("pcount", "1000");
        //  map.put("last", "");
        RequestManager.getInstance()
                .mServiceStore
                .load_acct_detail(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e(" getTXHistory onSuccess", "====" + msg);
                        mLayoutLoading.setVisibility(View.GONE);
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject object = new JSONObject(str);
                            if (object.getBoolean("success")) {
                               String items= object.getString("items");
                               if(items.equals("[]")){
                                   mLayoutNoMsg.setVisibility(View.VISIBLE);
                               }else {
                                   //解析JSON
                                   setDatas(object);
                               }
                            } else {

                                String errorMsg = object.getString("msg");
                                Toast.makeText(TXHistoryActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {

                        }
                    }

                    @Override
                    public void onError(String msg) {
                        mLayoutLoading.setVisibility(View.VISIBLE);
                        Log.e("onError", "====" + msg);
                        Toast.makeText(TXHistoryActivity.this, "查询失败！", Toast.LENGTH_SHORT).show();
                    }
                }));
    }

private void setDatas(JSONObject object){
    try {
        JSONArray array=new JSONArray(object.getString("items"));
        for (int i = 0; i <array.length() ; i++) {
            JSONObject object1=array.getJSONObject(i);
            JLEntity entity=new JLEntity();
            entity.setName(object1.getString("data:order_name"));
            entity.setTime(object1.getString("data:submit_time"));
            entity.setBalance(object1.getString("data:balance_rest"));
            entity.setChangeMoney(object1.getString("data:change_amount"));
            mList.add(entity);
        }
        BalanceDetailAdapter adapter = new BalanceDetailAdapter(this, mList);
        mListView.setAdapter(adapter);

    } catch (JSONException e) {
        e.printStackTrace();
    }


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
    public void onLoadMore() {
        mSwipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeToLoadLayout.setLoadingMore(false);

            }
        }, 3000);
    }

    @Override
    public void onRefresh() {

        mSwipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeToLoadLayout.setRefreshing(false);

            }
        }, 3000);
    }
}
