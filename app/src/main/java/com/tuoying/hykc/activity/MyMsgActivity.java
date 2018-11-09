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
import com.tuoying.hykc.adapter.MsgAdapter;
import com.tuoying.hykc.adapter.MyCardAdapter;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.CardEntity;
import com.tuoying.hykc.entity.GoodsEntity;
import com.tuoying.hykc.entity.MsgEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.BankBandDialog;
import com.tuoying.hykc.view.LoadingDialogFragment;
import com.tuoying.hykc.view.ZFB_WXBandDialog;
import com.tuoying.swipelayout.OnLoadMoreListener;
import com.tuoying.swipelayout.OnRefreshListener;
import com.tuoying.swipelayout.SwipeToLoadLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class MyMsgActivity extends BaseActivity implements OnRefreshListener, OnLoadMoreListener, MsgAdapter.OnItemRemoveListener {
    private Toolbar mToolbar;
    private ListView mListView;
    private DBDao dao;
    private String userid;
    private RelativeLayout mLayoutLoading;
    private RelativeLayout mLayoutNoMsg;
    private View header;
    private View footer;
    private LayoutInflater mInflater;
    private SwipeToLoadLayout mSwipeToLoadLayout;
    private List<MsgEntity> mList = new ArrayList<>();
    private MsgAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_msg);
        init();
    }

    @Override
    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("消息");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        dao = new DBDaoImpl(this);
        userid = SharePreferenceUtil.getInstance(this).getUserId();
        mListView = (ListView) findViewById(R.id.swipe_target);
        mLayoutLoading = findViewById(R.id.layout_loading);
        mLayoutNoMsg = findViewById(R.id.layout_nomsg);
        mLayoutNoMsg.setVisibility(View.GONE);
        mInflater = LayoutInflater.from(this);
        mSwipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        footer = mInflater.inflate(R.layout.layout_classic_footer, mSwipeToLoadLayout, false);
        header = mInflater.inflate(R.layout.layout_twitter_header, mSwipeToLoadLayout, false);
        mSwipeToLoadLayout.setSwipeStyle(SwipeToLoadLayout.STYLE.CLASSIC);
        mSwipeToLoadLayout.setLoadMoreFooterView(footer);
        mSwipeToLoadLayout.setRefreshHeaderView(header);
        mSwipeToLoadLayout.setOnRefreshListener(this);
        mSwipeToLoadLayout.setOnLoadMoreListener(this);
        mLayoutLoading.setVisibility(View.GONE);
        initDatas();
    }

    private void initDatas() {
        mList=dao.findAllMsg();
        if (mList.size()==0) {
            mLayoutNoMsg.setVisibility(View.VISIBLE);
            return;
        }
        setLocDatas();
    }

    private void setLocDatas() {

        adapter = new MsgAdapter(this, R.layout.layout_msg_item, mList);
        mListView.setAdapter(adapter);
        adapter.setOnItemRemoveListener(this);
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
    public void onItemRemove(int position, MsgEntity entity) {
        mList.remove(entity);
        dao.delMsgById(entity.getId());
        if (mList.size()==0) {
            mLayoutNoMsg.setVisibility(View.VISIBLE);
            return;
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onLoadMore() {

    }

    @Override
    public void onRefresh() {

    }
}
