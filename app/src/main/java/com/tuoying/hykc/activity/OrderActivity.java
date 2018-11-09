package com.tuoying.hykc.activity;

/*import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alct.mdp.MDPLocationCollectionManager;
import com.alct.mdp.callback.OnResultListener;
import com.alct.mdp.model.Goods;
import com.alct.mdp.model.Location;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.route.BikingRouteResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.IndoorRouteResult;
import com.baidu.mapapi.search.route.MassTransitRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;

import com.tuoying.hykc.R;
import com.tuoying.hykc.adapter.BottomPageAdapter;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.EventEntity;
import com.tuoying.hykc.entity.GoodsEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.fragment.BottomMsgFragment;
import com.tuoying.hykc.overlayutil.DrivingRouteOverlay;
import com.tuoying.hykc.utils.AnimationUtil;
import com.tuoying.hykc.utils.DateUtils;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.RxBus;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.ExitDialogFragment;
import com.tuoying.hykc.view.LoadingDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;*/

public class OrderActivity extends BaseActivity  {
    @Override
    public void init() {

    }
    //implements OnGetRoutePlanResultListener, BottomMsgFragment.OnBottomClickListener,SensorEventListener
    /*private Toolbar mToolbar;
    private MapView mMapView;
    private BaiduMap mBaiduMap;
    private LinearLayout mLayoutDetail;
    private RelativeLayout mLayoutBtn;
    private RelativeLayout mLayoutLoc;
    private ImageView mImg;
    private LocationClient mLocClient;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    private MyLocationData locData;
    private int mCurrentDirection = 0;
    boolean isFirstLoc = true;
    public MyLocationListenner myListener = new MyLocationListenner();
    RoutePlanSearch mSearch = null;
    DrivingRouteResult nowResultdrive = null;
    int nodeIndex = -1; // 节点索引,供浏览节点时使用
    private ViewPager mViewPager;
    private BottomPageAdapter mBottomPageAdapter;
    private int type;
    private String rowid;
    private String userid;
    private DBDao dao;
    private List<GoodsEntity> list = new ArrayList<>();
    private TextView mTextType;
    private TextView mTextIndex;
    private TextView mTextCount;
    private RelativeLayout mLayoutNoMsg;
    private int pos;
    int status;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private boolean isMoved=false;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_order);
        type = getIntent().getIntExtra("params", 1);
        rowid = getIntent().getStringExtra("rowid");
        init();
    }

    @Override
    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("导航");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        dao = new DBDaoImpl(OrderActivity.this);
        userid = SharePreferenceUtil.getInstance(OrderActivity.this).getUserId();
        mLayoutLoc = findViewById(R.id.layout_loc);
        mLayoutDetail = findViewById(R.id.layout_detial);
        mViewPager = findViewById(R.id.viewPager);
        mLayoutNoMsg = findViewById(R.id.layout_nomsg);
        mLayoutBtn = findViewById(R.id.layout_btn);
        mMapView = findViewById(R.id.mapView);
        mTextCount = findViewById(R.id.tv_count);
        mTextIndex = findViewById(R.id.tv_index);
        mTextType = findViewById(R.id.tv_type);
        mSensorManager = (SensorManager)this.getSystemService(Context.SENSOR_SERVICE);//获取传感器管理服务
        if (type == 1) {
            mTextType.setText("待配送");
            status=3;
        } else if (type == 2) {
            mTextType.setText("运输中");
            status=4;
        }
        mBaiduMap = mMapView.getMap();
        mMapView.removeViewAt(1);
        mMapView.showZoomControls(false);
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        MyLocationConfiguration.LocationMode mCurrentMode = MyLocationConfiguration.LocationMode.NORMAL;
        BitmapDescriptor mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_marker_car);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(mCurrentMode, true, mCurrentMarker));
        mImg = findViewById(R.id.img);
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(false); // 打开gps
        option.setLocationNotify(true);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        option.setScanSpan(10000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        initEvent();
        initDatas();
    }

    private void initDatas() {
        dao = new DBDaoImpl(this);
        userid = SharePreferenceUtil.getInstance(this).getUserId();
        if (userid != null) {
            User user = dao.findUserInfoById(userid);
            if (user != null) {
                getGoodsInfo(user);
            }
        }
    }

    private void getGoodsInfo(User user) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", user.getUserId());
        map.put("token", user.getToken());
        map.put("app", Constants.AppId);
        map.put("type", "wwc");
        RequestManager.getInstance()
                .mServiceStore
                .findOrderInfo(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        if (TextUtils.isEmpty(msg) || "[]".equals(msg)) {
                            mLayoutNoMsg.setVisibility(View.VISIBLE);
                            return;
                        }
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        Log.e("order activity" ,str);
                        analysisJson(str);
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("onError", "====" + msg);
                        Toast.makeText(OrderActivity.this, "查询失败！", Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    private void analysisJson(String msg) {
        List<GoodsEntity> mList = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(msg);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                String statusName = object.getString("data:yd_trans_status");
                Log.e("statusName",statusName);
                if(("待装货".equals(statusName)) || ("配送中".equals(statusName))) {
                    GoodsEntity entity = new GoodsEntity();
                    String formCity = object.getString("data:from_city");
                    String formCounty = object.getString("data:from_county");
                    entity.setStartAddress(formCity + " " + formCounty);
                    String toCity = object.getString("data:to_city");
                    String toCounty = object.getString("data:to_county");
                    entity.setEndAddress(toCity + " " + toCounty);
                    String fromLon = object.getString("data:from_lon");
                    entity.setLon_from(fromLon);
                    String fromLat = object.getString("data:from_lat");
                    entity.setLat_from(fromLat);
                    String toLon = object.getString("data:to_lon");
                    entity.setLon_to(toLon);
                    String toLat = object.getString("data:to_lat");
                    entity.setLat_to(toLat);
                    String sid = object.getString("data:sid");
                    entity.setSid(sid);
                    String name = object.getString("data:hwmc");
                    entity.setName(name);
                    String zl = object.getString("data:hwzl");
                    entity.setWeight(zl);
                    String tj = object.getString("data:hwtj");
                    entity.setVolume(tj);
                    String bz = object.getString("data:bz");
                    entity.setBz(bz);
                    String hzxm = object.getString("data:fhr");
                    entity.setHzxm(hzxm);
                    String zyf = object.getString("data:yf");
                    entity.setZyf(zyf);
                    if (object.has("data:sxfbl")) {
                        String bl = object.getString("data:sxfbl");
                        entity.setBl(bl);
                    } else {
                        entity.setBl("0.1");
                    }
                    if(object.has("data:moxid")){
                        entity.setMoxid(object.getString("data:moxid"));
                    }
                    String time = object.getString("data:create_time");
                    entity.setTime(time);
                    String shrName = object.getString("data:shr");
                    entity.setShrName(shrName);
                    String shrTel = object.getString("data:shrdh");
                    entity.setShrTel(shrTel);
                    entity.setStatusName(statusName);
                    String r = object.getString("rowid");
                    entity.setRowid(r);
                    String status=object.getString("data:yd_status");
                    entity.setStatus(status);
                    String driver=object.getString("data:yd_driver");
                    entity.setDriver(driver);
                    String huozhu=object.getString("data:fbr");
                    entity.setHuozhu(huozhu);
                    if(object.has("data:policyNo")){
                        String policyNo=object.getString("data:policyNo");
                        entity.setPolicyNo(policyNo);
                    }
                    if(object.has("data:yd_1_time")){
                        entity.setJdTime(object.getString("data:yd_1_time"));

                    }
                    if (rowid.equals(r)) {
                        entity.setGuiding(true);
                    } else {
                        entity.setGuiding(false);
                    }
                    mList.add(entity);
                }
            }
            pos=getIndexFromList(mList);
            mTextCount.setText(mList.size() + "");
            list.clear();
            list.addAll(mList);
            if (mBottomPageAdapter == null) {
                mBottomPageAdapter = new BottomPageAdapter(getSupportFragmentManager(), list,type);
                mViewPager.setAdapter(mBottomPageAdapter);
                mViewPager.setCurrentItem(pos);
            }
            Log.e("order pos" ,pos+"");
            GoodsEntity e=list.get(pos);
            LatLng ll = new LatLng(Double.valueOf(e.getLat_from()),
                    Double.valueOf(e.getLon_from()));
            LatLng end = new LatLng(Double.valueOf(e.getLat_to()), Double.valueOf(e.getLon_to()));
            drivingPlan(ll, end);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
        }

    }

    private int getIndexFromList(List<GoodsEntity> mList){
        int index=0;
        for (int i = 0; i < mList.size(); i++) {
            String str=mList.get(i).getRowid();
            if(rowid.equals(str)){
                index=i;
                return index;
            }

        }

        return index;
    }


    private void initEvent() {
        mLayoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLayoutDetail.getVisibility() == View.GONE) {
                    mImg.setImageResource(R.drawable.ic_action_up);
                    mLayoutDetail.setAnimation(AnimationUtil.moveToViewLocation());
                    mLayoutDetail.setVisibility(View.VISIBLE);
                } else {
                    mImg.setImageResource(R.drawable.ic_action_down);
                    mLayoutDetail.setAnimation(AnimationUtil.moveToViewBottom());
                    mLayoutDetail.setVisibility(View.GONE);
                }
            }
        });
        mLayoutDetail.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });
        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if (mLayoutDetail.getVisibility() == View.GONE) {
                    mImg.setImageResource(R.drawable.ic_action_down);
                    mLayoutDetail.setAnimation(AnimationUtil.moveToViewLocation());
                    mLayoutDetail.setVisibility(View.VISIBLE);
                } else {
                    mImg.setImageResource(R.drawable.ic_action_up);
                    mLayoutDetail.setAnimation(AnimationUtil.moveToViewBottom());
                    mLayoutDetail.setVisibility(View.GONE);
                }
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        mBaiduMap.setOnMapStatusChangeListener(new BaiduMap.OnMapStatusChangeListener() {
            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus) {
                Log.e("onMapStatusChangeStart1","=======>onMapStatusChangeStart1");
            }

            @Override
            public void onMapStatusChangeStart(MapStatus mapStatus, int i) {
                Log.e("onMapStatusChangeStart2","=======>onMapStatusChangeStart2");
            }
            @Override
            public void onMapStatusChange(MapStatus mapStatus) {
                Log.e("onMapStatusChange","=======>onMapStatusChange");
            }

            @Override
            public void onMapStatusChangeFinish(MapStatus mapStatus) {
                Log.e("onMapStatusChangeFinish","=======>onMapStatusChangeFinish");
                isMoved=true;

            }
        });
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                mTextIndex.setText((position + 1) + "");
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mLayoutLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isMoved=false;
                if (mCurrentLat != 0.0 && mCurrentLon != 0.0) {
                    LatLng ll = new LatLng(mCurrentLat,
                            mCurrentLon);
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(ll).zoom(15.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }

            }
        });

    }


    private void drivingPlan(LatLng startLatLng, LatLng endLatLng) {
        DrivingRoutePlanOption option = new DrivingRoutePlanOption();
        // 创建起点
        PlanNode startNode = PlanNode.withLocation(startLatLng);
        // 创建终点
        PlanNode endNode = PlanNode.withLocation(endLatLng);
        // 设置起点
        option.from(startNode);
        // 设置终点
        option.to(endNode);
        // 开始规划
        mSearch.drivingSearch(option);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double x = event.values[SensorManager.DATA_X];
        if (Math.abs(x - lastX) > 1.0) {
            mCurrentDirection = (int) x;
            locData = new MyLocationData.Builder()
                    .accuracy(mCurrentAccracy)
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(mCurrentLat)
                    .longitude(mCurrentLon).build();
            mBaiduMap.setMyLocationData(locData);
        }
        lastX = x;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            Log.e("isMoved","=====>"+isMoved);

            mCurrentLat = location.getLatitude();//维度
            mCurrentLon = location.getLongitude();//经度
            Log.e("location", mCurrentLat + " , " + mCurrentLon);
            mCurrentAccracy = location.getRadius();

                locData = new MyLocationData.Builder()
                        .accuracy(location.getRadius())
                        // 此处设置开发者获取到的方向信息，顺时针0-360
                        .direction(mCurrentDirection).latitude(location.getLatitude())
                        .longitude(location.getLongitude()).build();
                mBaiduMap.setMyLocationData(locData);

            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                //addOverlays(getLocationDatas());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            } else {
                if(!isMoved){
                    if (mCurrentLat != 0.0 && mCurrentLon != 0.0) {
                        LatLng ll = new LatLng(mCurrentLat,
                                mCurrentLon);
                        MapStatus.Builder builder = new MapStatus.Builder();
                        builder.target(ll);
                        mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                    }
                }
            }
        }
        public void onReceivePoi(BDLocation poiLocation) {

        }
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        super.onResume();
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
    }


    @Override
    protected void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        super.onPause();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocClient != null) {
            mLocClient.stop();
        }
        // 关闭定位图层
        if (mBaiduMap != null) {
            mBaiduMap.setMyLocationEnabled(false);
        }

        mMapView.onDestroy();
        mMapView = null;
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
    public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {

    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {

    }

    @Override
    public void onGetMassTransitRouteResult(MassTransitRouteResult massTransitRouteResult) {

    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        //驾车相关回调
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(OrderActivity.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            // 起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            // result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            // 获取到驾车路线的结果集合
            List<DrivingRouteLine> lines = result.getRouteLines();
            DrivingRouteLine routeLine = lines.get(0);
            // 创建路线的覆盖物
            DrivingRouteOverlay overlay = new MyDrivingRouteOverlay(mBaiduMap);
            // 把数据设置到覆盖物上
            overlay.setData(routeLine);
            // 把覆盖物添加到地图上
            overlay.addToMap();
            // 缩放到合适的距离
            overlay.zoomToSpan();

        }
    }

    @Override
    public void onGetIndoorRouteResult(IndoorRouteResult indoorRouteResult) {

    }

    @Override
    public void onGetBikingRouteResult(BikingRouteResult bikingRouteResult) {

    }

    // 定制RouteOverly
    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {

        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.icon_st);
        }
        @Override
        public BitmapDescriptor getTerminalMarker() {
            return BitmapDescriptorFactory.fromResource(R.mipmap.icon_en);
        }
    }

    @Override
    public void onCancelClick(final GoodsEntity entity) {
        if (userid != null) {
            User user = dao.findUserInfoById(userid);
            if (user != null) {
                doCancel(entity,user);
            }
        }
    }

    private void doCancel(final GoodsEntity entity, User user) {
        final LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.getInstance();
        loadingDialogFragment.show(getSupportFragmentManager(), "canceldialog");
        Map<String, String> map = new HashMap<>();
        map.put("mobile", user.getUserId());
        map.put("app", Constants.AppId);
        map.put("token", user.getToken());
        map.put("rowid", entity.getRowid());
        map.put("yd_status", entity.getStatus());
        map.put("driver", entity.getDriver());
        map.put("huozhu", entity.getHuozhu());
        RequestManager.getInstance()
                .mServiceStore
                .cancel_yd(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        loadingDialogFragment.dismiss();

                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.getBoolean("success")) {
                                Toast.makeText(OrderActivity.this, "取消成功！", Toast.LENGTH_SHORT).show();
                                if (entity.isGuiding()) {
                                    mBaiduMap.clear();
                                }
                                list.remove(entity);
                                if (list.size() == 0) {
                                    mLayoutNoMsg.setVisibility(View.VISIBLE);

                                }
                                mBottomPageAdapter.setList(list);
                                mBottomPageAdapter.notifyDataSetChanged();
                                RxBus.getInstance().send(new EventEntity("刷新","刷新"));
                            } else {
                                String error = jsonObject.getString("message");
                                loadingDialogFragment.dismiss();
                                Toast.makeText(OrderActivity.this, error, Toast.LENGTH_SHORT).show();
                                Log.e("create_request onError", msg);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        loadingDialogFragment.dismiss();
                        Toast.makeText(OrderActivity.this, "取消失败！", Toast.LENGTH_SHORT).show();
                        Log.e("create_request onError", msg);
                    }
                }));

    }
    private void confirmTips(String msg) {
        //退出操作
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {

                dialog.dismiss();
            }

            @Override
            public void onClickOk() {

                dialog.dismiss();

            }
        });
        dialog.show(getSupportFragmentManager(), "tipsDialog");

    }
    private void doWc(final GoodsEntity entity, User user) {
        if(!TextUtils.isEmpty(entity.getJdTime())){
            int i=(int)DateUtils.formatTime(entity.getJdTime());
            if(i<30){
                confirmTips("配送时间过短！");
                return;
            }
        }
        final LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.getInstance();
        loadingDialogFragment.show(getSupportFragmentManager(), "sddialog");
        Map<String, String> map = new HashMap<>();
        map.put("mobile", user.getUserId());
        map.put("app", Constants.AppId);
        map.put("token", user.getToken());
        map.put("status_code", "2");
        map.put("command", "送达客户");
        map.put("rowid", entity.getRowid());
        RequestManager.getInstance()
                .mServiceStore
                .change_yd_status(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        loadingDialogFragment.dismiss();

                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.getBoolean("success")) {
                                if (entity.isGuiding()) {
                                    mBaiduMap.clear();
                                }
                                list.remove(entity);
                                if (list.size() == 0) {
                                    mLayoutNoMsg.setVisibility(View.VISIBLE);

                                }
                                mBottomPageAdapter.setList(list);
                                mBottomPageAdapter.notifyDataSetChanged();
                                mTextCount.setText(mBottomPageAdapter.getCount() + "");
                                Toast.makeText(OrderActivity.this, "送达成功！", Toast.LENGTH_SHORT).show();
                                RxBus.getInstance().send(new EventEntity("刷新","刷新"));
                                String sdmsg = SharePreferenceUtil.getInstance(OrderActivity.this).getSDMsg();
                                if ("0".equals(sdmsg)) {
                                    doUnLoad(entity, entity.getName(), entity.getRowid(), entity.getAlctCode());

                                }
                            } else {
                                String error = jsonObject.getString("message");
                                loadingDialogFragment.dismiss();
                                Toast.makeText(OrderActivity.this, error, Toast.LENGTH_SHORT).show();
                                Log.e("create_request onError", msg);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        loadingDialogFragment.dismiss();
                        Toast.makeText(OrderActivity.this, "送达失败！", Toast.LENGTH_SHORT).show();
                        Log.e("create_request onError", msg);
                    }
                }));

    }
    private void doUnLoad(final GoodsEntity entity, final String hwmc, final String shipmentCode, final String enterpriseCode) {
        MDPLocationCollectionManager.unload(OrderActivity.this, shipmentCode, enterpriseCode, getLocation(entity,false), new OnResultListener() {
            public void onSuccess() {
                Log.e("doUnLoad onSuccess", "onSuccess----");
                // doPickUp(shipmentCode,enterpriseCode);
                doSign(entity, shipmentCode, enterpriseCode, hwmc);

            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("doUnLoad", "onFailure----" + s + s1);
                doUploadErrorMsg(shipmentCode, s, s1);

            }
        });

    }

    private void doSign(final GoodsEntity entity, final String shipmentCode, final String enterpriseCode, final String hwmc) {
        MDPLocationCollectionManager.sign(OrderActivity.this, shipmentCode, enterpriseCode, getLocation(entity,false), getGoodsList(hwmc)
                , new OnResultListener() {
                    @Override
                    public void onSuccess() {
                        Log.e("doSign onSuccess", "onSuccess----");
                        pod(entity, shipmentCode, enterpriseCode);
                        getLocation(entity.getRowid(),entity, shipmentCode, enterpriseCode, hwmc);
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        Log.e("doSign", "onFailure----" + s + s1);
                        doUploadErrorMsg(shipmentCode, s, s1);
                        getLocation(entity.getRowid(),entity, shipmentCode, enterpriseCode, hwmc);
                    }
                });

    }


    private void pod(final GoodsEntity entity, final String shipmentCode, final String enterpriseCode) {

        MDPLocationCollectionManager.pod(OrderActivity.this, shipmentCode, enterpriseCode, getLocation(entity,false), new OnResultListener() {
            @Override
            public void onSuccess() {
                Log.e("pod onSuccess", "onSuccess----");
                Intent intent = new Intent(OrderActivity.this, UpLoadImgActivity.class);
                intent.putExtra("entity", entity);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("doSign", "onFailure----" + s + s1);
                doUploadErrorMsg(shipmentCode, s, s1);
            }
        });

    }


    private List<Goods> getGoodsList(String hwmc) {
        List<Goods> list = new ArrayList<Goods>();
        Goods good = new Goods();
        good.setItemNo(1);
        good.setGoodsName(hwmc);
        good.setQuantity(1);
        good.setReceivedQuantity(1);
        good.setDamageQuantity(1);
        good.setLostQuantity(1);
        good.setUnit("车");
        list.add(good);
        return list;

    }

    @Override
    public void onComplateClick(GoodsEntity entity) {
        dao = new DBDaoImpl(this);
        userid = SharePreferenceUtil.getInstance(this).getUserId();
        if (userid != null) {
            User user = dao.findUserInfoById(userid);
            if (user != null) {
                if(type==1){
                    doPs(entity, user);
                }else if(type==2){
                    doWc(entity, user);
                }

            }

        }
    }
    private void doPs(final GoodsEntity entity, final User user) {
        final LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.getInstance();
        loadingDialogFragment.show(getSupportFragmentManager(), "psdialog");
        Map<String, String> map = new HashMap<>();
        map.put("mobile", user.getUserId());
        map.put("app", Constants.AppId);
        map.put("token", user.getToken());
        map.put("status_code", "1");
        map.put("command", "开始配送");
        map.put("rowid", entity.getRowid());
        RequestManager.getInstance()
                .mServiceStore
                .change_yd_status(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        loadingDialogFragment.dismiss();
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.getBoolean("success")) {
                                Toast.makeText(OrderActivity.this, "开始配送成功！", Toast.LENGTH_SHORT).show();
                                if (entity.isGuiding()) {
                                    mBaiduMap.clear();
                                }
                                list.remove(entity);
                                if (list.size() == 0) {
                                    mLayoutNoMsg.setVisibility(View.VISIBLE);

                                }
                                mBottomPageAdapter.setList(list);
                                mBottomPageAdapter.notifyDataSetChanged();
                                mTextCount.setText(mBottomPageAdapter.getCount() + "");
                                String sdmsg = SharePreferenceUtil.getInstance(OrderActivity.this).getSDMsg();
                                if ("0".equals(sdmsg)) {
                                    doCheckNfc(entity.getRowid(), entity.getAlctCode(),entity);
                                }
                                RxBus.getInstance().send(new EventEntity("刷新","刷新"));
                            } else {
                                String error = jsonObject.getString("message");
                                loadingDialogFragment.dismiss();
                                Toast.makeText(OrderActivity.this, error, Toast.LENGTH_SHORT).show();
                                Log.e("create_request onError", msg);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        loadingDialogFragment.dismiss();
                        Toast.makeText(OrderActivity.this, "开始配送失败！", Toast.LENGTH_SHORT).show();
                        Log.e("create_request onError", msg);
                    }
                }));

    }

    private void doCheckNfc(final String shipmentCode, final String enterpriseCode,final GoodsEntity entity) {
        MDPLocationCollectionManager.checkNfc(OrderActivity.this, shipmentCode, enterpriseCode, "", new OnResultListener() {
            @Override
            public void onSuccess() {
                Log.e("doCheckNfc onSuccess", "onSuccess----");
                doPickUp(shipmentCode, enterpriseCode,entity);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("doCheckNfc", "onFailure----" + s + s1);
                doUploadErrorMsg(shipmentCode, s, s1);
            }
        });

    }


    private void doPickUp(final String shipmentCode, final String enterpriseCode,final GoodsEntity entity) {

        //需要获取坐标

        MDPLocationCollectionManager.pickup(OrderActivity.this, shipmentCode, enterpriseCode, getLocation(entity,true), new OnResultListener() {
            @Override
            public void onSuccess() {
                dopickup(entity.getRowid(), true+"");
                Log.e("doPickUp", "onSuccess----");
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e(entity.getMoxid(), "onFailure----" + s + s1);
                doUploadErrorMsg(shipmentCode, s, s1);
                dopickup(entity.getRowid(), false+"");

            }
        });

    }
    private Location getLocation(GoodsEntity entity,boolean isPickUp) {
        double lat;
        double lon;
        if(isPickUp){
            lat=Double.parseDouble(entity.getLat_from());
            lon=Double.parseDouble(entity.getLon_from());
        }else {
            lat=Double.parseDouble(entity.getLat_to());
            lon=Double.parseDouble(entity.getLon_to());
        }
        Location location = new Location();
        location.setBaiduLatitude(lat);
        location.setBaiduLongitude(lon);
        location.setLocation("");
        location.setTime(getNowtime());
        return location;

    }
    private String getNowtime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String time = sdf.format(new Date());
        return time;
    }

    private void doUploadErrorMsg(String enterpriseCode, String s, String s1) {
        Map<String, String> map = new HashMap<>();
        map.put("alctErrorCode", s);
        map.put("rowid", enterpriseCode);
        map.put("erromessage", s1);
        RequestManager.getInstance()
                .mServiceStore
                .uplaodErrorInfo(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {

                    }

                    @Override
                    public void onError(String msg) {


                        Log.e("alct onError", msg);
                    }
                }));


    }


    private void dopickup(String id,String orderType) {
        Map<String, String> map = new HashMap<>();
        map.put("rowid",id);
        map.put("orderType",orderType);
        RequestManager.getInstance()
                .mServiceStore
                .pickup(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {

                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        Log.e("dopickup", "str===" + str);
                    }

                    @Override
                    public void onError(String msg) {

                        Log.e("dopickup onError", "str===" + msg);
                    }
                }));

    }

    private void getLocation(String id,final GoodsEntity entity, final String shipmentCode, final String enterpriseCode, final String hwmc) {
        Map<String, String> map = new HashMap<>();
        map.put("rowid",id);
        map.put("type","hykc");
        RequestManager.getInstance()
                .mServiceStore
                .getLocation(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {

                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        Log.e("getLocation", "str===" + str);
                        try {
                            JSONObject object=new JSONObject(str);
                            boolean isSuccess=object.getBoolean("success");
                            if(isSuccess){
                                String successMsg=object.getString("msg");
                                Toast.makeText(OrderActivity.this, successMsg, Toast.LENGTH_SHORT).show();
                            }else {
                                String errorMsg=object.getString("msg");
                                Toast.makeText(OrderActivity.this, errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(String msg) {
                        Log.e("getLocation onError", "str===" + msg);
                    }
                }));
    }

    @Override
    public void onButtonClick(GoodsEntity entity) {
        mBaiduMap.clear();
        setStatue(entity);
        if (entity.isGuiding()) {
                LatLng ll = new LatLng(Double.valueOf(entity.getLat_from()),
                        Double.valueOf(entity.getLon_from()));
                LatLng end = new LatLng(Double.valueOf(entity.getLat_to()), Double.valueOf(entity.getLon_to()));
                drivingPlan(ll, end);
        } else {
            mBaiduMap.clear();
        }
    }

    private void setStatue(GoodsEntity entity) {
        String rowid = entity.getRowid();
        for (int i = 0; i < list.size(); i++) {
            GoodsEntity e = list.get(i);
            if (!rowid.equals(e.getRowid())) {
                e.setGuiding(false);
            }
        }
        mBottomPageAdapter.notifyDataSetChanged();
    }*/
}
