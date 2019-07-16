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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tuoying.hykc.R;
import com.tuoying.hykc.adapter.MyCarInfoAdapter;
import com.tuoying.hykc.adapter.MyCardAdapter;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.entity.UCarEntity;
import com.tuoying.hykc.service.ServiceStore;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.LoadingDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class TrailerInfoActivity extends BaseActivity {
    private Toolbar mToolbar;
    private TextView mTextAdd;
    private RelativeLayout mLayoutNoMsg;
    private ListView mListView;
    List<UCarEntity> list=new ArrayList<>();
    private boolean isLoadSuccess=false;
    MyCarInfoAdapter adapter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_trailerinfo);
        init();
    }

    @Override
    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("挂车信息");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initView();
        initEvent();
    }


    private void initEvent() {
        mTextAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isLoadSuccess ){
                    if(list.size()==0){
                        Intent intent = new Intent(TrailerInfoActivity.this, AddTrailerInfoActivity.class);
                        startActivity(intent);
                        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }else {
                        Toast.makeText(TrailerInfoActivity.this,"只能绑定一辆挂车！",Toast.LENGTH_SHORT).show();
                    }

                }else {
                    Toast.makeText(TrailerInfoActivity.this, "数据加载失败，请稍后再试！", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final UCarEntity entity=list.get(position);
                Intent intent = new Intent(TrailerInfoActivity.this, MyTrailerDetailsActivity.class);
                intent.putExtra("entity",entity);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });
    }

    @Override
    protected void onResume() {
        list.clear();
        initDatas();
        super.onResume();
    }

    private void initDatas() {
        final LoadingDialogFragment dialogFragment=LoadingDialogFragment.getInstance();
        dialogFragment.showF(getSupportFragmentManager(),"loading");
        String id=SharePreferenceUtil.getInstance(this).getUserId();
        if(TextUtils.isEmpty(id)){
            Toast.makeText(this, "用户信息为空,请重新登录!", Toast.LENGTH_SHORT).show();
            return;
        }
        Map<String,String> map=new HashMap<>();
        map.put("mobile",id);
        map.put("cartype","2");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.TRAILERINFO_URL).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call = serviceStore.selectTrailerInfo(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialogFragment.dismissAllowingStateLoss();
                ResponseBody body=response.body();
                try {
                   String str= body.string();
                    try {
                        JSONObject jsonObject=new JSONObject(str);
                        if(jsonObject.getBoolean("success")){
                            isLoadSuccess=true;
                            String string=jsonObject.getString("entity");
                            if(!"null".equals(string)){
                                mLayoutNoMsg.setVisibility(View.GONE);
                                Gson gson=new Gson();
                                UCarEntity uCarEntity=gson.fromJson(string,UCarEntity.class);
                                uCarEntity.setLicensePlateNoold(uCarEntity.getLicensePlateNo());
                                list.add(uCarEntity);
                                setDatas();
                            }else {
                                mLayoutNoMsg.setVisibility(View.VISIBLE);
                            }
                        }else {
                            Toast.makeText(TrailerInfoActivity.this, "查询失败！", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("onResponse","onResponse=="+str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogFragment.dismissAllowingStateLoss();
                Log.e("onFailure","onFailure=="+t.getMessage());
            }
        });
    }
    private void setDatas() {
        adapter.setList(list);

    }
    private void initView() {
        mTextAdd=findViewById(R.id.tv_right_title);
        mLayoutNoMsg=findViewById(R.id.layout_nomsg);
        mListView=findViewById(R.id.listView);
        adapter=new MyCarInfoAdapter(TrailerInfoActivity.this,list);
        mListView.setAdapter(adapter);
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
