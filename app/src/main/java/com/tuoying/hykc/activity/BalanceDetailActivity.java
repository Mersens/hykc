package com.tuoying.hykc.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;


import com.tuoying.hykc.R;
import com.tuoying.hykc.adapter.BalanceDetailAdapter;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.BalanceEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.utils.SharePreferenceUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/27.
 */

public class BalanceDetailActivity extends BaseActivity {
    private Toolbar mToolbar;
    private ListView mListView;
    private User user;
    private String userid;
    private DBDao dao;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_balance_detail);
        init();
    }


    @Override
    public void init() {
        initViews();
    }

    private void initViews() {
        mListView = findViewById(R.id.listView);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("余额明细");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        dao=new DBDaoImpl(this);
        userid= SharePreferenceUtil.getInstance(this).getUserId();
        if(!TextUtils.isEmpty(userid)){
            user=dao.findUserInfoById(userid);
        }
        initDatas();

        List<BalanceEntity> list = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            BalanceEntity entity = new BalanceEntity();
            list.add(entity);
        }
    }

    private void initDatas() {

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
