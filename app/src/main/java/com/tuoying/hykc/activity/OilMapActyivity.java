package com.tuoying.hykc.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.tuoying.hykc.R;
import com.tuoying.hykc.entity.OilEntity;

public class OilMapActyivity extends BaseActivity {
    private Toolbar mToolbar;
    private OilEntity entity;
    private MapView mapView;
    private TextView mTextAddress;
    private BaiduMap mBaiduMap;
    private double lat;
    private double lon;
    private BitmapDescriptor mMarker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_oil_map);
        init();
    }

    @Override
    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("油站信息");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        entity= (OilEntity) getIntent().getSerializableExtra("info");
        initViews();
        initDatas();
    }

    private void initViews() {
        mapView=findViewById(R.id.mapView);
        mBaiduMap = mapView.getMap();
        mapView.removeViewAt(1);
        mapView.showZoomControls(false);
        mTextAddress=findViewById(R.id.tv_address);
        mMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_location);

    }

    private void initDatas() {
        if(entity==null){
            return;
        }
        String address=entity.getAddress();
        mTextAddress.setText(address);
        String strlat=entity.getLat();
        String strlon=entity.getLng();
        if(!TextUtils.isEmpty(strlat)){
            lat=Double.valueOf(strlat);
        }
        if(!TextUtils.isEmpty(strlon)){
            lon=Double.valueOf(strlon);
        }
        LatLng ll = new LatLng(lat, lon);
        OverlayOptions options = new MarkerOptions().position(ll).icon(mMarker)
                .zIndex(8);
        Marker marker = (Marker) mBaiduMap.addOverlay(options);
        Bundle arg0 = new Bundle();
        arg0.putSerializable("info", entity);
        marker.setExtraInfo(arg0);
        MapStatus.Builder builder = new MapStatus.Builder();
        builder.target(ll).zoom(16.0f);
        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));

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
    protected void onResume() {
        mapView.onResume();
        super.onResume();
    }



    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        mapView = null;
        super.onDestroy();

    }
}
