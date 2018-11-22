package com.tuoying.hykc.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.tuoying.hykc.R;
import com.tuoying.hykc.entity.RZEntity;
import com.tuoying.hykc.utils.CarInfoUtils;

public class CarInfoActivity extends BaseActivity {
    private Toolbar mToolbar;
    private TextView mTextCph;
    private TextView mTextPp;
    private TextView mTextCx;
    private TextView mTextCc;
    private TextView mTextZz;
    private TextView mTextNf;
    private TextView mTextDlysz;
    private TextView mTextCplx;
    private TextView mTextClfl;
    private RZEntity entity = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_carinfo);
        init();
    }

    @Override
    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("车辆信息");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        entity = (RZEntity) getIntent().getSerializableExtra("carinfo");
        initViews();
        initDatas();
    }

    private void initDatas() {
        if (entity == null) {
            return;
        }
        mTextCph.setText(entity.getCph());
        mTextPp.setText(entity.getPp());
        mTextCx.setText(entity.getCx());
        mTextCc.setText(entity.getCc());
        mTextZz.setText(entity.getZz()+"吨");
        mTextNf.setText(entity.getNf());
        if(!TextUtils.isEmpty(entity.getCplx())){
            mTextCplx.setText(CarInfoUtils.getInstance().getLxById(entity.getCplx()));
        }
        if(!TextUtils.isEmpty(entity.getClfl())){
            mTextClfl.setText(CarInfoUtils.getInstance().getFlById(entity.getClfl()));
        }
        mTextDlysz.setText(entity.getDlysz());
    }

    private void initViews() {
        mTextCph = findViewById(R.id.tv_cph);
        mTextPp = findViewById(R.id.tv_pp);
        mTextCx = findViewById(R.id.tv_cx);
        mTextCc = findViewById(R.id.tv_cc);
        mTextZz = findViewById(R.id.tv_zz);
        mTextNf = findViewById(R.id.tv_nf);
        mTextDlysz = findViewById(R.id.tv_dlysz);
        mTextCplx = findViewById(R.id.tv_cplx);
        mTextClfl = findViewById(R.id.tv_clfl);
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
