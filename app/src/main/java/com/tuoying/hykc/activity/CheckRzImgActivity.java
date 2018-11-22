package com.tuoying.hykc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tuoying.hykc.R;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.utils.SharePreferenceUtil;

public class CheckRzImgActivity extends BaseActivity {
    private Toolbar mToolbar;
    private TextView mTextResetRz;
    private ImageView mImgCard_Z;
    private ImageView mImgCard_F;
    private ImageView mImgJsz;
    private ImageView mImgXsz;
    private ImageView mImgDlysz;
    private ImageView mImgCyzgz;
    private ImageView mImgXSZ_Z;
    private ImageView mImgXSZ_F;
    private ImageView mImgXSZFB;
    private ImageView mImgGCZL;
    private ImageView mImgGCZLFYF;
    private String userid;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_check_rz_img);
        init();
    }

    @Override
    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("认证信息");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initViews();
        initEvent();
        initDatas();
    }

    private void initDatas() {
        userid=SharePreferenceUtil.getInstance(this).getUserId();
        if(TextUtils.isEmpty(userid)){
            Toast.makeText(this,
                    "请重新登录！", Toast.LENGTH_SHORT).show();
            return;
        }
        downLoadImg(userid);

    }

    private void downLoadImg(String tel){
        String urls[]=getResources().getStringArray(R.array.rzimgs);
        Glide.with(this).load(getUrl(tel,urls[0]))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgCard_Z);
        Glide.with(this)
                .load(getUrl(tel,urls[1]))
                .skipMemoryCache(true)

                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgCard_F);
        Glide.with(this)
                .load(getUrl(tel,urls[2]))
                .skipMemoryCache(true)

                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgJsz);
        Glide.with(this)
                .load(getUrl(tel,urls[3]))
                .skipMemoryCache(true)

                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgXsz);
        Glide.with(this)
                .load(getUrl(tel,urls[4]))
                .skipMemoryCache(true)

                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgDlysz);
        Glide.with(this)
                .load(getUrl(tel,urls[5]))
                .skipMemoryCache(true)

                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgCyzgz);
        Glide.with(this)
                .load(getUrl(tel,urls[6]))
                .skipMemoryCache(true)

                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgXSZ_Z);
        Glide.with(this)
                .load(getUrl(tel,urls[7]))
                .skipMemoryCache(true)

                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgXSZ_F);
        Glide.with(this)
                .load(getUrl(tel,urls[8]))
                .skipMemoryCache(true)

                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgXSZFB);
        Glide.with(this)
                .load(getUrl(tel,urls[9]))
                .skipMemoryCache(true)

                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgGCZL);
        Glide.with(this)
                .load(getUrl(tel,urls[10]))
                .skipMemoryCache(true)

                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgGCZLFYF);

    }

    private String getUrl(String tel,String imhName){

        return Constants.WEBSERVICE_URL+"files/temp/"+tel+"/"+imhName;
    }


    private void initEvent() {
        mTextResetRz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(CheckRzImgActivity.this,RzTextActivity.class);
                intent.putExtra("rzType",1);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }

    private void initViews() {
        mTextResetRz=mToolbar.findViewById(R.id.tv_right_title);
        mImgXSZ_Z = findViewById(R.id.img_xsz_z);
        mImgXSZ_F = findViewById(R.id.img_xsz_f);
        mImgXSZFB = findViewById(R.id.img_xszfb);
        mImgGCZL = findViewById(R.id.img_gczl);
        mImgCard_Z = findViewById(R.id.img_card_z);
        mImgCard_F = findViewById(R.id.img_card_f);
        mImgJsz = findViewById(R.id.img_jsz);
        mImgXsz = findViewById(R.id.img_xsz);
        mImgDlysz = findViewById(R.id.img_dlysz);
        mImgCyzgz = findViewById(R.id.img_cyzgz);
        mImgGCZLFYF=findViewById(R.id.img_gczfyf);
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
