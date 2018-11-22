package com.tuoying.hykc.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.CircleOptions;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.MyLocationConfiguration;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Stroke;
import com.baidu.mapapi.map.TextureMapView;
import com.baidu.mapapi.model.LatLng;
import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.GoodsListDetailActivity;
import com.tuoying.hykc.activity.MainActivity;
import com.tuoying.hykc.activity.RzTextActivity;
import com.tuoying.hykc.adapter.GoodsListAdapter;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.EventEntity;
import com.tuoying.hykc.entity.GoodsEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.utils.APKVersionCodeUtils;
import com.tuoying.hykc.utils.AnimationUtil;
import com.tuoying.hykc.utils.DateUtils;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.RxBus;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.AlwaysMarqueeTextView;
import com.tuoying.hykc.view.ExitDialogFragment;
import com.tuoying.hykc.view.LoadingDialogFragment;
import com.tuoying.hykc.view.PayMoneyDialog;
import com.tuoying.hykc.view.RadarView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Administrator on 2018/3/26.
 */

public class MapFragment extends BaseFragment implements SensorEventListener {
    private final int SDK_PERMISSION_REQUEST = 127;
    public MyLocationListenner myListener = new MyLocationListenner();
    WeakReference<MainActivity> mActivityReference;
    boolean isFirstLoc = true;
    BitmapDescriptor mCurrentMarker;
    private String permissionInfo;
    private TextureMapView mMapView;
    private BaiduMap mBaiduMap;
    private LocationClient mLocClient;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private float mCurrentAccracy;
    private MyLocationData locData;
    private int mCurrentDirection = 0;
    private RelativeLayout mLayoutLoc;
    // 自定义定位图标
    private BitmapDescriptor mIconLocation;
    private float mCurrentX;
    private MyLocationConfiguration.LocationMode mLocationMode;
    // 覆盖物相关
    private BitmapDescriptor mMarker;
    private String userid;
    private DBDao dao;
    private User user;
    private SensorManager mSensorManager;
    private Double lastX = 0.0;
    private RelativeLayout layout_bg;
    private RadarView radarView;
    private String sCity;
    private String eCity;
    private String sDistrict;
    private String eDistrict;
    private MarkerHolder holder = null;
    private LinearLayout mMarkerLy;
    private RelativeLayout mLayoutScroll;
    private AlwaysMarqueeTextView mTextScroll;
    private ImageView mImgScrollClose;

    public static MapFragment getInstance() {
        return new MapFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityReference = new WeakReference<>((MainActivity) context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_map, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void init(View view) {
        dao = new DBDaoImpl(getActivity());
        userid = SharePreferenceUtil.getInstance(getActivity()).getUserId();
        if (!TextUtils.isEmpty(userid)) {
            user = dao.findUserInfoById(userid);
        }
        mLayoutScroll = view.findViewById(R.id.layout_scroll);
        mTextScroll = view.findViewById(R.id.tv_scroll);
        mImgScrollClose = view.findViewById(R.id.img_scrill_close);
        layout_bg = view.findViewById(R.id.layout_bg);
        radarView = view.findViewById(R.id.radarView);
        mSensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);//获取传感器管理服务
        mLayoutLoc = view.findViewById(R.id.layout_loc);
        mMapView = view.findViewById(R.id.mapView);
        mMarkerLy = (LinearLayout) view.findViewById(R.id.layout_msg);
        mBaiduMap = mMapView.getMap();
        mMapView.removeViewAt(1);
        mMapView.showZoomControls(false);
        // 开启定位图层
        mBaiduMap.setMyLocationEnabled(true);
        mCurrentMarker = BitmapDescriptorFactory
                .fromResource(R.mipmap.icon_marker_car);
        mBaiduMap.setMyLocationConfiguration(new MyLocationConfiguration(
                MyLocationConfiguration.LocationMode.NORMAL, true, mCurrentMarker));

        mLocClient = new LocationClient(getActivity().getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(false); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        mLocClient.setLocOption(option);
        mLocClient.start();
        showScan();
        initEvent();
        initMarker();

    }

    private void initMarker() {
        mMarker = BitmapDescriptorFactory.fromResource(R.drawable.ic_action_huowu);
        initHolder();
    }

    private void initHolder() {
        holder = new MarkerHolder();
        holder.mTextFhr = mMarkerLy.findViewById(R.id.tv_fhr);
        holder.mTextStart = mMarkerLy.findViewById(R.id.tv_start);
        holder.mTextZl = mMarkerLy.findViewById(R.id.tv_zl);
        holder.mTextFbTime = mMarkerLy.findViewById(R.id.tv_fb_time);
        holder.mTextEnd = mMarkerLy.findViewById(R.id.tv_end);
        holder.mTextJg = mMarkerLy.findViewById(R.id.tv_jg);
        holder.mTextTj = mMarkerLy.findViewById(R.id.tv_tj);
        holder.mTextBz = mMarkerLy.findViewById(R.id.tv_bz);
        holder.mBtnDetial = mMarkerLy.findViewById(R.id.btn_xq);
        holder.mBtnJd = mMarkerLy.findViewById(R.id.btn_jd);

    }

    private void initEvent() {
        mImgScrollClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutScroll.setVisibility(View.GONE);
            }
        });
        mBaiduMap.setOnMarkerClickListener(new BaiduMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                drawMarker(marker);
                return true;
            }
        });

        mBaiduMap.setOnMapClickListener(new BaiduMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMarkerLy.setAnimation(AnimationUtil.moveToViewBottom());
                mMarkerLy.setVisibility(View.GONE);
                mBaiduMap.hideInfoWindow();
            }

            @Override
            public boolean onMapPoiClick(MapPoi mapPoi) {
                return false;
            }
        });
        mLayoutLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mCurrentLat != 0.0 && mCurrentLon != 0.0) {
                    LatLng ll = new LatLng(mCurrentLat,
                            mCurrentLon);
                    MapStatus.Builder builder = new MapStatus.Builder();
                    builder.target(ll).zoom(14.0f);
                    mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                }
            }
        });
    }

    private void drawMarker(Marker marker) {
        Bundle extraInfo = marker.getExtraInfo();
        final GoodsEntity info = (GoodsEntity) extraInfo.getSerializable("info");
        if (holder == null) {
            initHolder();
        }

        holder.mTextFhr.setText(info.getHzxm());
        holder.mTextStart.setText(info.getStartAddress());
        holder.mTextZl.setText(info.getWeight() + "吨");
        holder.mTextFbTime.setText(DateUtils.strTimeFormat(info.getTime(), "yyyy-MM-dd"));
        if (info.isOfwlgsinfo()) {
            holder.mTextJg.setText(info.getZyf());
        } else {
            holder.mTextJg.setText("****");
        }

        holder.mTextEnd.setText(info.getEndAddress());
        holder.mTextTj.setText(info.getVolume() + "立方");
        holder.mTextBz.setText(info.getBz());
        TextView mTextTop = new TextView(getActivity());
        mTextTop.setBackgroundResource(R.drawable.location_tips);
        mTextTop.setPadding(30, 20, 30, 50);
        mTextTop.setTextColor(Color.parseColor("#ffffff"));
        mTextTop.setText(info.getName());
        holder.mBtnDetial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GoodsListDetailActivity.class);
                intent.putExtra("type", 0);
                intent.putExtra("entity", info);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        holder.mBtnJd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (user != null) {
                    String rzType = user.getRz();
                    if ("0".equals(rzType)) {
                        confirmRZTips("用户认证未通过！");
                    } else if ("1".equals(rzType)) {
                        doOrder(info);
                    } else if (TextUtils.isEmpty(rzType)) {
                        confirmRZTips("用户未认证！");
                    }
                }
            }

        });
        final LatLng latLng = marker.getPosition();
        Point p = mBaiduMap.getProjection().toScreenLocation(latLng);
        LatLng ll = mBaiduMap.getProjection().fromScreenLocation(p);

        InfoWindow infoWindow = new InfoWindow(BitmapDescriptorFactory.fromView(mTextTop), ll, -47,
                new InfoWindow.OnInfoWindowClickListener() {
                    @Override
                    public void onInfoWindowClick() {
                        mMarkerLy.setAnimation(AnimationUtil.moveToViewBottom());
                        mMarkerLy.setVisibility(View.GONE);
                        mBaiduMap.hideInfoWindow();
                    }
                });
        mBaiduMap.showInfoWindow(infoWindow);
        mMarkerLy.setAnimation(AnimationUtil.moveToViewLocation());
        mMarkerLy.setVisibility(View.VISIBLE);


    }

    private void confirmRZTips(String msg) {
        //退出操作
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @Override
            public void onClickOk() {
                Intent intent = new Intent(getActivity(), RzTextActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

            }
        });
        dialog.show(getChildFragmentManager(), "confirmRZTips111");

    }

    private void showScan() {
        radarView.start();
        layout_bg.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hideScan();
            }
        }, 3000);

    }

    private void hideScan() {
        radarView.stop();
        layout_bg.setVisibility(View.GONE);
    }

    private void query_sources() {
        if (user == null) {
            return;
        }
        StringBuffer sbf = new StringBuffer();
        if (!TextUtils.isEmpty(sCity) && !TextUtils.isEmpty(sDistrict)) {
            sbf.append(".*?\\|");
            sbf.append(sCity);
            sbf.append(".*?");
            sbf.append(sDistrict);
            sbf.append(".*?\\-");
        }
        if (!TextUtils.isEmpty(eCity) && !TextUtils.isEmpty(eDistrict)) {
            sbf.append(".*?");
            sbf.append(eCity);
            sbf.append(".*?");
            sbf.append(eDistrict);
            sbf.append(".*?");
        }
        Map<String, String> map = new HashMap<>();
        map.put("token", user.getToken());
        map.put("mobile", user.getUserId());
        map.put("app", Constants.AppId);
        map.put("line", sbf.toString());
        map.put("excludes", "");
        Log.e("prams", sbf.toString());
        RequestManager.getInstance()
                .mServiceStore
                .query_sources(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("query_sources onSuccess", msg);
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        if ("[]".equals(str)) {
                        } else {
                            analysisJson(str);
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("query_sources onError", msg);
                    }
                }));
    }

    private void analysisJson(String str) {
        boolean isOfwlgsinfo = false;
        if (TextUtils.isEmpty(user.getOfwlgsinfo())) {
            isOfwlgsinfo = true;
        } else {
            isOfwlgsinfo = false;
        }
        List<GoodsEntity> mList = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(str);
            for (int i = 0; i < array.length(); i++) {
                String val = array.getString(i);
                Log.e("val", val);
                GoodsEntity entity = new GoodsEntity();
                String vals[] = val.split("\\$");
                String rowid = vals[0];
                String strDetial = vals[1];
                entity.setRowid(rowid);
                String rowids[] = rowid.split("\\|");
                String time = rowids[0];
                entity.setTime(time);
                String address = rowids[2];
                String startAddress = address.split("\\-")[0];
                String endAddress = address.split("\\-")[1];
                entity.setEndAddress(endAddress);
                entity.setStartAddress(startAddress);
                String strs[] = strDetial.split("\\|");
                String sid = strs[2];
                entity.setSid(sid);
                String name = strs[3];
                entity.setName(name);
                String w = strs[4];
                entity.setWeight(w);
                String v = strs[5];
                entity.setVolume(v);
                String bz = strs[6];
                entity.setBz(bz);
                String xm = strs[7];
                entity.setHzxm(xm);
                String yf = strs[9];
                entity.setZyf(yf);
                String bl = strs[10];
                entity.setBl(bl);
                String form_lat = strs[11];
                entity.setLat_from(form_lat);
                String form_lon = strs[12];
                entity.setLon_from(form_lon);
                String to_lat = strs[13];
                entity.setLat_to(to_lat);
                String to_lon = strs[14];
                entity.setLon_to(to_lon);
                entity.setOfwlgsinfo(isOfwlgsinfo);
                mList.add(entity);
            }
            addOverlays(mList);
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
        }
    }

    /**
     * 添加覆盖物
     *
     * @param infos
     */
    private void addOverlays(List<GoodsEntity> infos) {
        if (mMapView == null) {
            return;
        }
        if (mBaiduMap == null) {
            return;

        }
        mBaiduMap.clear();
        LatLng latLng = null;
        Marker marker = null;
        OverlayOptions options;
        for (GoodsEntity info : infos) {
            // 经纬度
            latLng = new LatLng(Double.valueOf(info.getLat_from()), Double.valueOf(info.getLon_from()));
            // 图标
            options = new MarkerOptions().position(latLng).icon(mMarker)
                    .zIndex(5);
            marker = (Marker) mBaiduMap.addOverlay(options);
            Bundle arg0 = new Bundle();
            arg0.putSerializable("info", info);
            marker.setExtraInfo(arg0);
        }
        if (mCurrentLat != 0.0 && mCurrentLon != 0.0) {
            LatLng ll = new LatLng(mCurrentLat,
                    mCurrentLon);
            MapStatus.Builder builder = new MapStatus.Builder();
            builder.target(ll).zoom(14.0f);
            mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
            OverlayOptions ooCircle = new CircleOptions().fillColor(0x384d73b3)
                    .center(ll).stroke(new Stroke(3, 0x784d73b3))

                    .radius(Constants.LOC_RADIUS);
            mBaiduMap.addOverlay(ooCircle);

        }

    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onResume() {
        mMapView.onResume();
        if (mLocClient != null) {
            mLocClient.start();
        }
        mBaiduMap.setMyLocationEnabled(true);
        mSensorManager.registerListener(this, mSensorManager.getDefaultSensor(Sensor.TYPE_ORIENTATION),
                SensorManager.SENSOR_DELAY_UI);
        Log.e("maponResume", "onResume");
        super.onResume();

    }

    @Override
    public void onStop() {
        mSensorManager.unregisterListener(this);
        super.onStop();
    }

    @Override
    public void onPause() {
        mMapView.onPause();
        if (mLocClient != null) {
            mLocClient.stop();
        }
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

    private void doOrder(final GoodsEntity entity) {
        final LoadingDialogFragment dialogFragment = LoadingDialogFragment.getInstance();
        dialogFragment.show(getChildFragmentManager(), "orderLoading");
        Map<String, String> map = new HashMap<>();
        map.put("token", user.getToken());
        map.put("mobile", user.getUserId());
        map.put("app", Constants.AppId);
        map.put("rowid", entity.getRowid());
        RequestManager.getInstance()
                .mServiceStore
                .create_yd(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        Log.e("doOrder", "===" + msg);
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject object = new JSONObject(str);
                            if (object.getBoolean("success")) {
                                String success = object.getString("message");
                                Toast.makeText(activity, success, Toast.LENGTH_SHORT).show();
                                shoPayMoneyViews(entity.getZyf(), Double.parseDouble(entity.getBl()), entity);
                            } else {
                                String error = object.getString("message");
                                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            dialogFragment.dismiss();
                            mMarkerLy.setAnimation(AnimationUtil.moveToViewBottom());
                            mMarkerLy.setVisibility(View.GONE);
                            mBaiduMap.hideInfoWindow();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        dialogFragment.dismiss();
                        Log.e("query_sources onError", msg);
                    }
                }));
    }

    private void shoPayMoneyViews(String price, double bl, final GoodsEntity entity) {
        final PayMoneyDialog dialog = PayMoneyDialog.getInstance(price, bl, entity.getSid(), user.getUserName(),entity);
        dialog.show(getChildFragmentManager(), "paymoneydialog");
        dialog.setOnOrderListener(new PayMoneyDialog.OnOrderListener() {
            @Override
            public void onOrder(String psd, String money) {
                dialog.dismiss();
                if (user != null) {
                    doPayMoney(psd, money, entity);
                }
            }
        });
        dialog.setOnDismissListener(new PayMoneyDialog.onDismissListener() {
            @Override
            public void onDismiss() {
                dialog.dismiss();
                RxBus.getInstance().send(new EventEntity("刷新", "刷新"));
            }
        });
    }

    private void doPayMoney(final String psd, final String money, final GoodsEntity entity) {
        final LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.getInstance();
        loadingDialogFragment.show(getChildFragmentManager(), "paydialog");
        Map<String, String> map = new HashMap<>();
        map.put("mobile", user.getUserId());
        map.put("app", Constants.AppId);
        map.put("token", user.getToken());
        map.put("amount", money);
        map.put("pwd", psd);
        map.put("req_type", "DEPS");
        map.put("type_name", "运输保证金支付");
        JSONObject object = new JSONObject();
        try {
            object.put("rowid", entity.getRowid());
            object.put("flag", "driver_deps");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        map.put("ext", object.toString());
        RequestManager.getInstance()
                .mServiceStore
                .create_request(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.getBoolean("success")) {
                                String orderno = jsonObject.getString("order_no");
                                String tokenid = jsonObject.getString("tokenid");
                                submit_order(orderno, tokenid, entity.getRowid(), loadingDialogFragment);
                            } else {
                                String error = jsonObject.getString("message");
                                RxBus.getInstance().send(new EventEntity("刷新", "刷新"));
                                loadingDialogFragment.dismiss();
                                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                                Log.e("create_request onError", msg);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        RxBus.getInstance().send(new EventEntity("刷新", "刷新"));
                        loadingDialogFragment.dismiss();
                        Toast.makeText(activity, "支付失败！", Toast.LENGTH_SHORT).show();
                        Log.e("create_request onError", msg);
                    }
                }));
    }

    private void submit_order(String orderno, String tokenid, final String rowid, final LoadingDialogFragment loadingDialogFragment) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", user.getUserId());
        map.put("app", Constants.AppId);
        map.put("token", user.getToken());
        map.put("tokenid", tokenid);
        map.put("order_no", orderno);
        map.put("req_type", "DEPS");
        map.put("rowid", rowid);
        RequestManager.getInstance()
                .mServiceStore
                .submit_order(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        loadingDialogFragment.dismiss();
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.getBoolean("success")) {
                                doAnsycWayBill(rowid);
                                Toast.makeText(activity, "支付成功！", Toast.LENGTH_SHORT).show();
                                RxBus.getInstance().send(new EventEntity("刷新", "刷新"));
                                if (mMarkerLy.getVisibility() == View.VISIBLE) {
                                    mMarkerLy.setAnimation(AnimationUtil.moveToViewBottom());
                                    mMarkerLy.setVisibility(View.GONE);
                                    mBaiduMap.hideInfoWindow();
                                }
                            } else {
                                String error = jsonObject.getString("message");
                                RxBus.getInstance().send(new EventEntity("刷新", "刷新"));
                                loadingDialogFragment.dismiss();
                                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                                Log.e("create_request onError", msg);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }

                        RxBus.getInstance().send(new EventEntity("刷新", "刷新"));
                        loadingDialogFragment.dismiss();
                        Toast.makeText(activity, "支付失败！", Toast.LENGTH_SHORT).show();
                        Log.e("create_request onError", msg);
                    }
                }));
    }

    private void doAnsycWayBill(String rowid) {
        Map<String, String> map = new HashMap<>();
        map.put("rowid", rowid);
        RequestManager.getInstance()
                .mServiceStore
                .alctyydtb(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        Log.e("doAnsycWayBill", "str===" + str);
                    }

                    @Override
                    public void onError(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        Log.e("doAnsycWayBill onError", "str===" + msg);
                    }
                }));
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        Log.e("map onSensorChanged", "=============onSensorChanged");
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

    static class MarkerHolder {
        public TextView mTextFhr;
        public TextView mTextStart;
        public TextView mTextZl;
        public TextView mTextFbTime;
        public TextView mTextJg;
        public TextView mTextEnd;
        public TextView mTextTj;
        public TextView mTextBz;
        public Button mBtnDetial;
        public Button mBtnJd;

    }

    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            // map view 销毁后不在处理新接收的位置
            if (location == null || mMapView == null) {
                return;
            }
            final MainActivity activity = mActivityReference.get();
            if (activity == null) {
                return;
            }
            sCity = location.getCity();
            eCity = location.getCity();
            sDistrict = location.getDistrict();
            eDistrict = location.getDistrict();
            Log.e("location", location.getCity() + "===" + location.getDistrict());
            mCurrentLat = location.getLatitude();
            mCurrentLon = location.getLongitude();
            Log.e("location", mCurrentLat + " , " + mCurrentLon);
            mCurrentAccracy = location.getRadius();
            locData = new MyLocationData.Builder()
                    .accuracy(location.getRadius())
                    // 此处设置开发者获取到的方向信息，顺时针0-360
                    .direction(mCurrentDirection).latitude(location.getLatitude())
                    .longitude(location.getLongitude()).build();
            mBaiduMap.setMyLocationData(locData);
            query_sources();
            if (isFirstLoc) {
                isFirstLoc = false;
                LatLng ll = new LatLng(location.getLatitude(),
                        location.getLongitude());
                MapStatus.Builder builder = new MapStatus.Builder();
                builder.target(ll).zoom(14.0f);
                mBaiduMap.animateMapStatus(MapStatusUpdateFactory.newMapStatus(builder.build()));
                OverlayOptions ooCircle = new CircleOptions().fillColor(0x384d73b3)
                        .center(ll).stroke(new Stroke(3, 0x784d73b3))
                        .radius(Constants.LOC_RADIUS);
                mBaiduMap.addOverlay(ooCircle);

            }
        }

        public void onReceivePoi(BDLocation poiLocation) {

        }
    }
}
