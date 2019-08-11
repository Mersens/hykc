package com.tuoying.hykc.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.tuoying.hykc.R;
import com.tuoying.hykc.adapter.OilListAdapter;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.entity.OilEntity;
import com.tuoying.hykc.service.ServiceStore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class OilListActivity extends BaseActivity {
    private Toolbar mToolbar;
    private TextView mTextMap;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RelativeLayout mLayoutNoMsg;
    private OilListAdapter adapter;
    private List<OilEntity> list=new ArrayList<>();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_oil_list);
        init();
    }

    @Override
    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("油卡信息");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initView();
        initDatas();
    }


    private void initView() {
        mTextMap=findViewById(R.id.tv_right_title);
        mTextMap.setVisibility(View.GONE);
        swipeRefreshLayout=findViewById(R.id.swipeRefreshLayout);
        recyclerView=findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(null);
        adapter=new OilListAdapter(this,list);
        recyclerView.setAdapter(adapter);
        mLayoutNoMsg=findViewById(R.id.layout_nomsg);
        int color = getResources().getColor(R.color.actionbar_color);
        swipeRefreshLayout.setColorSchemeColors(color, color, color);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshDatas();
            }
        });
    }
    private void initDatas() {
        String string="{\n" +
                "\t\"code\": \"0\",\n" +
                "\t\"message\": \"ok\",\n" +
                "\t\"result\": [{\n" +
                "\t\t\"stationId\": \"1\",\n" +
                "\t\t\"stationName\": \"濮阳服务区中石化加油站（东）\",\n" +
                "\t\t\"provinceCode\": \"410000\",\n" +
                "\t\t\"cityCode\": \"410900\",\n" +
                "\t\t\"areaCode\": \"410922\",\n" +
                "\t\t\"lng\": \"115.07637045\",\n" +
                "\t\t\"lat\": \"36.00676692\",\n" +
                "\t\t\"address\": \"河南省濮阳市清丰县\",\n" +
                "\t\t\"isStop\": 0,\n" +
                "\t\t\"isHighspeed\": 1,\n" +
                "\t\t\"fuels\": [{\n" +
                "\t\t\t\"sf_id\": \"3\",\n" +
                "\t\t\t\"fuel_name\": \"0# 柴油\",\n" +
                "\t\t\t\"status\": \"1\",\n" +
                "\t\t\t\"price\": \"616\"\n" +
                "\n" +
                "\t\t}]\n" +
                "\n" +
                "\t}]\n" +
                "}";
        try {
            JSONObject object=new JSONObject(string);
            analysisJson(object.getString("result"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
      /*  Map<String,String> map=new HashMap<>();
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.OIL_URL_TEST).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call=serviceStore.getStationAndFuels(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if(swipeRefreshLayout!=null && swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
                ResponseBody body=response.body();
                String str= null;
                try {
                    if(null!=body){
                        str = body.string();
                        JSONObject object=new JSONObject(str);
                        int code=object.getInt("code");
                        if(code==0){
                            analysisJson(object.getString("result"));

                        }else {
                            String string=object.getString("message");
                            Toast.makeText(OilListActivity.this, string, Toast.LENGTH_SHORT).show();
                            mLayoutNoMsg.setVisibility(View.VISIBLE);
                        }
                    }else {
                        Toast.makeText(OilListActivity.this, "信息查询失败！", Toast.LENGTH_SHORT).show();
                        mLayoutNoMsg.setVisibility(View.VISIBLE);

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("getStationAndFuels","getStationAndFuels=="+str);

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                if(swipeRefreshLayout!=null && swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }
                Log.e("onFailure","onFailure=="+t.getMessage());
                Toast.makeText(OilListActivity.this, "信息查询失败！", Toast.LENGTH_SHORT).show();
                mLayoutNoMsg.setVisibility(View.VISIBLE);
            }
        });*/

    }

    //解析油站数据
    private void analysisJson(String result) throws JSONException {
        JSONArray array=new JSONArray(result);
        for (int i = 0; i < array.length(); i++) {
            Gson gson=new Gson();
            OilEntity entity =gson.fromJson(array.getString(i),OilEntity.class);
            list.add(entity);
        }
        if(adapter!=null){
            adapter.setDatas(list);
        }
    }

    //刷新数据
    private void refreshDatas() {

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
