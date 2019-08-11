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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.tuoying.hykc.R;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.service.ServiceStore;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.QRCDialogView;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OilActivity extends BaseActivity implements View.OnClickListener {
    private Toolbar mToolbar;
    private TextView mTextMoney;
    private RelativeLayout mLayoutOil;
    private RelativeLayout mLayoutQRC;
    private String userId;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_oil);
        init();
    }

    @Override
    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("油卡信息");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initViews();
        getOilMoneyInfo();

    }

    private void getOilMoneyInfo() {
        userId=SharePreferenceUtil.getInstance(this).getUserId();
        if(TextUtils.isEmpty(userId)){
            Toast.makeText(this, "用户信息为空，请重新登录！",
                    Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String,String> map=new HashMap<>();
        map.put("username",userId);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.OIL_URL_TEST).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call=serviceStore.queryDriverMoney(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody body=response.body();
                String str= null;
                try {
                    if(null!=body){
                        str = body.string();
                        JSONObject object=new JSONObject(str);
                        int code=object.getInt("code");
                        if(code==0){
                            double balance=object.getDouble("outstanding_balance");
                            double d=balance/1000;
                            String strMoney = String.format("%.2f", d);
                            mTextMoney.setText(strMoney);
                        }else {
                            String string=object.getString("message");
                            Toast.makeText(OilActivity.this, string, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(OilActivity.this, "信息查询失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("queryDriverMoney","queryDriverMoney=="+str);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure","onFailure=="+t.getMessage());
            }
        });


    }

    private void initViews() {
        mTextMoney=findViewById(R.id.tv_yue);
        mLayoutOil=findViewById(R.id.layout_oil);
        mLayoutQRC=findViewById(R.id.layout_qrc);
        mLayoutOil.setOnClickListener(this);
        mLayoutQRC.setOnClickListener(this);

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
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_oil:
                //加油站列表
                Intent mIntent=new Intent(OilActivity.this,OilListActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                break;
            case R.id.layout_qrc:
                //加油二维码
                QRCDialogView qrcDialogView=QRCDialogView.newInstance(userId);
                qrcDialogView.show(getSupportFragmentManager(),"QRCView");
                break;
        }

    }
}
