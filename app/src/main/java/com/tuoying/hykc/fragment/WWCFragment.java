package com.tuoying.hykc.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.alct.mdp.MDPLocationCollectionManager;
import com.alct.mdp.callback.OnDownloadResultListener;
import com.alct.mdp.callback.OnResultListener;
import com.alct.mdp.model.Goods;
import com.alct.mdp.model.Location;
import com.alct.mdp.model.ShipmentStatusEnum;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.utils.DistanceUtil;
import com.google.gson.Gson;
import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.BaseActivity;
import com.tuoying.hykc.activity.GoodsListDetailActivity;
import com.tuoying.hykc.activity.LoginActivity;
import com.tuoying.hykc.activity.MainActivity;
import com.tuoying.hykc.activity.MyTrailerDetailsActivity;
import com.tuoying.hykc.activity.OrderActivity;
import com.tuoying.hykc.activity.UpLoadImgActivity;
import com.tuoying.hykc.adapter.GoodsListAdapter;
import com.tuoying.hykc.adapter.WWCAdapter;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.EventEntity;
import com.tuoying.hykc.entity.GoodsEntity;
import com.tuoying.hykc.entity.LocEntity;
import com.tuoying.hykc.entity.LocationEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.service.ServiceStore;
import com.tuoying.hykc.utils.DateUtils;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.RxBus;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.ExitDialogFragment;
import com.tuoying.hykc.view.LoadingDialogFragment;
import com.tuoying.hykc.view.PayMoneyDialog;
import com.tuoying.swipelayout.OnLoadMoreListener;
import com.tuoying.swipelayout.OnRefreshListener;
import com.tuoying.swipelayout.SwipeToLoadLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class WWCFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener, WWCAdapter.OnItemButtonClickListener {

    WeakReference<MainActivity> mActivityReference;
    User user;
    private CompositeDisposable mCompositeDisposable;
    private View rootView;
    private RelativeLayout mLayoutLoading;
    private RelativeLayout mLayoutNoMsg;
    private ListView mListView;
    private View header;
    private View footer;
    private LayoutInflater mInflater;
    private SwipeToLoadLayout mSwipeToLoadLayout;
    private DBDao dao;
    private String userid;
    private List<GoodsEntity> list = new ArrayList<>();
    private WWCAdapter adapter;
    private boolean isRefrush = false;
    private boolean isPickUp = false;
    private long allon1;
    private long allon2;
    private boolean onPouse = false;
    public MyLocationListenner myListener = new MyLocationListenner();
    private LocationClient mLocClient;
    private boolean isStopLocation = false;
    private static final int NFCTIPSDISMISS = 1;
    final LoadingDialogFragment nfcTipsFragment = LoadingDialogFragment.getInstance("正在检测NFCID");
    public Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == NFCTIPSDISMISS) {
                if (nfcTipsFragment != null) {
                    nfcTipsFragment.dismissAllowingStateLoss();
                }
            }
            return true;
        }
    });
    private String nfcid = "";
    private ExitDialogFragment nfcDialogFragment;
    private int pickUpCount = 0;
    private int unLoadCount = 0;
    private int signCount = 0;
    private int podCount = 0;


    public static WWCFragment getInstance() {
        return new WWCFragment();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityReference = new WeakReference<>((MainActivity) context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_wwc, null);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    public static String changeTtime(String time) {
        String str = null;
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            str = format.format(sdf.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return str;
    }


    private void showGetNfcMsg(String msg) {
        final ExitDialogFragment exitDialogFragment = ExitDialogFragment.getInstance(msg);
        exitDialogFragment.show(getChildFragmentManager(), "showGetNfcMsg");
        exitDialogFragment.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                exitDialogFragment.dismissAllowingStateLoss();

            }

            @Override
            public void onClickOk() {
                exitDialogFragment.dismissAllowingStateLoss();

            }
        });
    }


    private void initDatas() {
        if (!TextUtils.isEmpty(userid)) {
            User user = dao.findUserInfoById(userid);
            if (user != null) {
                getGoodsInfo(user);
            }
        }
    }

    @Override
    public void onResume() {
        onPouse = false;
        if (adapter != null) {
            list.clear();
            adapter.setList(list);
        }
        initDatas();
        super.onResume();

    }

    @Override
    public void init(View view) {
        //doUploadErrorMsg("111", "222", "333");
        dao = new DBDaoImpl(getActivity());
        userid = SharePreferenceUtil.getInstance(getActivity()).getUserId();
        if (!TextUtils.isEmpty(userid)) {
            user = dao.findUserInfoById(userid);
        }
        mListView = (ListView) view.findViewById(R.id.swipe_target);
        mLayoutLoading = view.findViewById(R.id.layout_loading);
        mLayoutNoMsg = view.findViewById(R.id.layout_nomsg);
        mLayoutLoading.setVisibility(View.GONE);
        mInflater = LayoutInflater.from(getContext());
        mSwipeToLoadLayout = (SwipeToLoadLayout) view.findViewById(R.id.swipeToLoadLayout);
        footer = mInflater.inflate(R.layout.layout_classic_footer, mSwipeToLoadLayout, false);
        header = mInflater.inflate(R.layout.layout_twitter_header, mSwipeToLoadLayout, false);
        mSwipeToLoadLayout.setSwipeStyle(SwipeToLoadLayout.STYLE.CLASSIC);
        mSwipeToLoadLayout.setLoadMoreFooterView(footer);
        mSwipeToLoadLayout.setRefreshHeaderView(header);
        mSwipeToLoadLayout.setOnRefreshListener(this);
        mSwipeToLoadLayout.setOnLoadMoreListener(this);
        //initDatas();
        mCompositeDisposable = new CompositeDisposable();
        //监听订阅事件
        Disposable d = RxBus.getInstance().toObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof EventEntity) {
                            EventEntity e = (EventEntity) o;
                            String type = e.type;
                            String v = e.value;
                            if (type.equals("刷新")) {
                                if (v.equals("刷新")) {
                                    isRefrush = true;
                                    list.clear();
                                    if (adapter != null) {
                                        adapter.setList(list);
                                    }
                                    initDatas();
                                }
                            } else if (type.equals("nfc")) {
                                nfcid = v;
                                if (TextUtils.isEmpty(nfcid)) {
                                    Toast.makeText(getActivity(), "NFCID获取失败，请重新获取！", Toast.LENGTH_SHORT).show();
                                } else {
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            showGetNfcMsg("NFCId获取成功,请重新操作!");
                                        }
                                    }, 1500);

                                }

                            }
                        }
                    }
                });
        //subscription交给compositeSubscription进行管理，防止内存溢出
        mCompositeDisposable.add(d);
        mLocClient = new LocationClient(getActivity().getApplicationContext());
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(false); // 打开gps
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        int t = (int) (2.5 * 60 * 1000);
        // int t=3000;
        option.setScanSpan(t);
        mLocClient.setLocOption(option);
    }

    private void getGoodsInfo(final User user) {
        if (!isRefrush) {
            mLayoutLoading.setVisibility(View.VISIBLE);
        }
        Map<String, String> map = new HashMap<>();
        map.put("mobile", user.getUserId());
        map.put("token", user.getToken());
        map.put("app", Constants.AppId);
        map.put("type", "wwc");
        RequestManager.getInstance()
                .mServiceStore
                .findOrderInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        mLayoutLoading.setVisibility(View.GONE);
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        if (TextUtils.isEmpty(msg)) {
                            mLayoutNoMsg.setVisibility(View.VISIBLE);
                            return;
                        }
                        if (mSwipeToLoadLayout.isRefreshing()) {
                            mSwipeToLoadLayout.setRefreshing(false);
                        }
                        if (mSwipeToLoadLayout.isLoadingMore()) {
                            mSwipeToLoadLayout.setLoadingMore(false);
                        }
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        Log.e(" wwc1 onSuccess", "====" + msg);
                        JSONArray array = null;
                        try {
                            array = new JSONArray(str);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            mLayoutLoading.setVisibility(View.GONE);
                        }
                        if (array.length() == 0) {
                            mLayoutNoMsg.setVisibility(View.VISIBLE);
                            mLayoutLoading.setVisibility(View.GONE);
                            return;
                        } else {
                            mLayoutNoMsg.setVisibility(View.GONE);
                            mLayoutLoading.setVisibility(View.GONE);
                        }
                        analysisJson(str);
                    }

                    @Override
                    public void onError(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        mLayoutLoading.setVisibility(View.GONE);
                        Log.e("onError", "====" + msg);
                        Toast.makeText(activity, "失败！", Toast.LENGTH_SHORT).show();
                    }
                }));
    }

    @Override
    public void onPause() {
        onPouse = true;
        isRefrush = false;
        super.onPause();
    }

    @Override
    public void onLoadMore() {
        isRefrush = true;
        initDatas();
    }

    @Override
    public void onRefresh() {
        isRefrush = true;
        initDatas();
    }

    @Override
    public void onButtonClick(View view, int index, final GoodsEntity entity, int type) {
        if (userid != null) {
            user = dao.findUserInfoById(userid);
            if (user == null) {
                return;
            }
        }
        switch (type) {
            case 1:
                doPayMoney(entity.getZyf(), Double.parseDouble(entity.getBl()), entity);
                break;
            case 2:
                //  doPs(entity);
                checkStatu(entity);
                break;
            case 3:
                calculateTime(entity);
                break;
            case 12:
                final ValidationCodeFragment validationCodeFragment = ValidationCodeFragment.getInstance("确定取消运单？");
                validationCodeFragment.showF(getChildFragmentManager(), "cancelOrderDialog");
                validationCodeFragment.setOnValidationComplListener(new ValidationCodeFragment.OnValidationComplListener() {
                    @Override
                    public void onClickCancel() {
                        validationCodeFragment.dismissAllowingStateLoss();
                    }

                    @Override
                    public void onClickOk() {
                        validationCodeFragment.dismissAllowingStateLoss();
                        doCancel(entity);
                    }
                });
                break;
            case 13:
                doGuide(index, entity, type);
                break;
            case 14:
                doTB(entity);
                break;
            case 15:
                doPz(entity);
                break;
        }
    }

    private void checkStatu(final GoodsEntity entity) {
        // doPs(entity);
        Map<String, String> map = new HashMap<>();
        map.put("rowid", entity.getRowid());
        System.out.println("mox+++" + entity.toString());
        RequestManager.getInstance()
                .mServiceStore
                .checkPickupOk(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        msg = msg.trim();
                        Log.e("checkStatu", "checkStatu===" + msg);
                        if ("0".equals(msg)) {
                            doPs(entity);
                        } else if ("1".equals(msg)) {
                            confirmTips("运单同步中,请等待！");

                        } else if ("2".equals(msg)) {
                            confirmTips("运单同步中,请等待！");
                        } else if ("3".equals(msg)) {
                            confirmTips("正在重新同步,请稍后!");
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        Log.e("create_request onError", msg);
                    }
                }));

    }

    private void calculateTime(final GoodsEntity entity) {
        if (!TextUtils.isEmpty(entity.getJdTime())) {
            int i = (int) DateUtils.formatTime(entity.getJdTime());
            Log.e("getJdTime2==", i + "");
            if (i < 30) {
                //step:16
                //tel:157****0385
                //msg:登录获取用户信息成功
                //time:2018-12-18:11:11:10
                //rowid:
                // confirmTips("配送时间过短!");
                Map<String, String> map = new HashMap<>();
                map.put("step", "16");
                map.put("tel", user.getUserId());
                map.put("msg", "送货时间小于30分钟，输入验证码即可送货");
                map.put("time", getNowtime());
                map.put("rowid", entity.getRowid());
                upLoadUserLog(map);
                final ValidationCodeFragment validationCodeFragment = ValidationCodeFragment.getInstance("配送时间过短,确定送达？");
                validationCodeFragment.showF(getChildFragmentManager(), "pstimelow");
                validationCodeFragment.setOnValidationComplListener(new ValidationCodeFragment.OnValidationComplListener() {
                    @Override
                    public void onClickCancel() {
                        validationCodeFragment.dismissAllowingStateLoss();
                    }

                    @Override
                    public void onClickOk() {
                        validationCodeFragment.dismissAllowingStateLoss();
                        doSd(entity);
                    }
                });

            } else {
                double d = getPointsDistances(entity.getLat_from(), entity.getLon_from(), entity.getLat_to(), entity.getLon_to());
                Log.e("getPointsDistances", "getPointsDistances===" + d);
                int needTime = (int) ((d / 100) * 60);//实际运输需要的大致时间(分钟)
                if (i < needTime) {
                    //step:17
                    //tel:157****0385
                    //msg:登录获取用户信息成功
                    //time:2018-12-18:11:11:10
                    //rowid:
                    Map<String, String> map = new HashMap<>();
                    map.put("step", "17");
                    map.put("tel", user.getUserId());
                    map.put("msg", "理论时间过短，输入验证码即可送货");
                    map.put("time", getNowtime());
                    map.put("rowid", entity.getRowid());
                    upLoadUserLog(map);
                    final ValidationCodeFragment validationCodeFragment = ValidationCodeFragment.getInstance("配送时间过短,确定送达？");
                    validationCodeFragment.showF(getChildFragmentManager(), "timelow");
                    validationCodeFragment.setOnValidationComplListener(new ValidationCodeFragment.OnValidationComplListener() {
                        @Override
                        public void onClickCancel() {
                            validationCodeFragment.dismissAllowingStateLoss();
                        }

                        @Override
                        public void onClickOk() {
                            validationCodeFragment.dismissAllowingStateLoss();
                            doSd(entity);
                        }
                    });
                } else {
                    doSd(entity);
                }
            }
        } else {
            //step:15
            //tel:157****0385
            //msg:登录获取用户信息成功
            //time:2018-12-18:11:11:10
            //rowid:
            Map<String, String> map = new HashMap<>();
            map.put("step", "15");
            map.put("tel", user.getUserId());
            map.put("msg", "JdTime为空，未执行卸货操作");
            map.put("time", getNowtime());
            map.put("rowid", entity.getRowid());
            upLoadUserLog(map);

        }
    }

    private void doPz(GoodsEntity entity) {
        Intent intent = new Intent(getActivity(), UpLoadImgActivity.class);
        intent.putExtra("entity", entity);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
    }

    private void doTB(GoodsEntity entity) {
        final LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.getInstance();
        loadingDialogFragment.showF(getChildFragmentManager(), "TBdialog");
        Map<String, String> map = new HashMap<>();
        map.put("mobile", user.getUserId());
        map.put("app", Constants.AppId);
        map.put("token", user.getToken());
        map.put("rowid", entity.getRowid());
        RequestManager.getInstance()
                .mServiceStore
                .create_bdxx(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        loadingDialogFragment.dismissAllowingStateLoss();
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.getBoolean("success")) {
                                Toast.makeText(activity, "投保成功！", Toast.LENGTH_SHORT).show();
                                mSwipeToLoadLayout.setRefreshing(true);
                            } else {
                                String error = jsonObject.getString("message");
                                mSwipeToLoadLayout.setRefreshing(true);
                                loadingDialogFragment.dismissAllowingStateLoss();
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
                        mSwipeToLoadLayout.setRefreshing(true);
                        loadingDialogFragment.dismissAllowingStateLoss();
                        Toast.makeText(activity, "投保失败！", Toast.LENGTH_SHORT).show();
                        Log.e("create_request onError", msg);
                    }
                }));


    }

    private void doGuide(int index, GoodsEntity entity, int type) {
        int params = 1;
        if (type == 2) {
            params = 1;
        } else if (type == 3) {
            params = 2;
        }
        Intent intent = new Intent(getActivity(), OrderActivity.class);
        intent.putExtra("params", params);
        intent.putExtra("rowid", entity.getRowid());
        intent.putExtra("pos", index);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);

    }

    private void doCancel(GoodsEntity entity) {
        final LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.getInstance();
        loadingDialogFragment.showF(getChildFragmentManager(), "canceldialog");
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        loadingDialogFragment.dismissAllowingStateLoss();
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.getBoolean("success")) {
                                Toast.makeText(activity, "取消成功！", Toast.LENGTH_SHORT).show();
                                mSwipeToLoadLayout.setRefreshing(true);
                            } else {
                                String error = jsonObject.getString("message");
                                mSwipeToLoadLayout.setRefreshing(true);
                                loadingDialogFragment.dismissAllowingStateLoss();
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
                        mSwipeToLoadLayout.setRefreshing(true);
                        loadingDialogFragment.dismissAllowingStateLoss();
                        Toast.makeText(activity, "取消失败！", Toast.LENGTH_SHORT).show();
                        Log.e("create_request onError", msg);
                    }
                }));

    }

    private void confirmTips(String msg) {
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getChildFragmentManager(), "tipswwcDialog");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                dialog.dismissAllowingStateLoss();
            }
        });


    }

    private void doUnlodCheckNfc(final String shipmentCode, final String enterpriseCode, final GoodsEntity entity) {
        //doPickUp(shipmentCode, enterpriseCode,entity);
        allon1 = DateUtils.getTimeMillis();
        Log.e("entity111==", "entity1111==" + entity.toString());
        MDPLocationCollectionManager.checkNfc(getActivity(), shipmentCode, enterpriseCode, nfcid, new OnResultListener() {
            @Override
            public void onSuccess() {
                //step:11
                //tel:157****0385
                //msg:登录获取用户信息成功
                //time:2018-12-18:11:11:10
                //rowid:
                Map<String, String> map = new HashMap<>();
                map.put("step", "11");
                map.put("tel", user.getUserId());
                map.put("msg", "安联卸货doCheckNfc执行成功");
                map.put("time", getNowtime());
                map.put("rowid", entity.getRowid());
                upLoadUserLog(map);
                Log.e("doCheckNfc onSuccess", "onSuccess----");
                doUnLoad(entity, entity.getName(), shipmentCode, enterpriseCode);
            }

            @Override
            public void onFailure(String s, String s1) {
                //step:12
                //tel:157****0385
                //msg:登录获取用户信息成功
                //time:2018-12-18:11:11:10
                //rowid:
                // doPickUp(shipmentCode, enterpriseCode, entity);
                Map<String, String> map = new HashMap<>();
                map.put("step", "12");
                map.put("tel", user.getUserId());
                map.put("msg", "安联卸货doCheckNfc执行失败，失败原因：" + "s=" + s + ":s1=" + s1);
                map.put("time", getNowtime());
                map.put("rowid", entity.getRowid());
                String param = "NFCID=" + nfcid + ";shipmentCode=" + shipmentCode + "; enterpriseCode="
                        + enterpriseCode + ";货源信息=" + entity.toString();
                map.put("param", param);
                upLoadUserLog(map);
                Log.e("doCheckNfc", "onFailure----" + s + s1);
                doUploadErrorMsg(shipmentCode, s, s1);
                unLoadcheckNFCAgainMsg("checkNfc失败！" + "\n" + "是否重新扫描？", entity, shipmentCode, enterpriseCode);
            }
        });

    }

    private void unLoadcheckNFCAgainMsg(String msg, final GoodsEntity entity, final String shipmentCode, final String enterpriseCode) {
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getChildFragmentManager(), "checkNFCAgainMsg1");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismissAllowingStateLoss();
                //执行平台操作
                //doUnLoad(entity,entity.getName(),shipmentCode,enterpriseCode);
                doUnLoadToService(entity);
            }

            @Override
            public void onClickOk() {
                dialog.dismissAllowingStateLoss();
                nfcTipsFragment.showF(getChildFragmentManager(), "nfcTipsFragment8");
                handler.sendEmptyMessageDelayed(NFCTIPSDISMISS, 5000);
            }
        });
    }

    private void doSd(final GoodsEntity entity) {
        submitOrderEtcInfo(entity,2);
        final MainActivity activity = mActivityReference.get();
        if (activity == null) {
            return;
        }
        String sdmsg = SharePreferenceUtil.getInstance(activity).getSDMsg();
        if ("0".equals(sdmsg)) {
            //step:18
            //tel:157****0385
            //msg:登录获取用户信息成功
            //time:2018-12-18:11:11:10
            //rowid:
            if (TextUtils.isEmpty(nfcid)) {
                doUnLoad(entity, entity.getName(), entity.getRowid(), entity.getAlctCode());
            } else {
                doUnlodCheckNfc(entity.getRowid(), entity.getAlctCode(), entity);
            }
        } else {
            //step:19
            //tel:157****0385
            //msg:登录获取用户信息成功
            //time:2018-12-18:11:11:10
            //rowid:
            Map<String, String> map = new HashMap<>();
            map.put("step", "19");
            map.put("tel", user.getUserId());
            map.put("msg", "用户未税登，不执行卸货操作");
            map.put("time", getNowtime());
            map.put("rowid", entity.getRowid());
            String param = "货源信息=" + entity.toString();
            map.put("param", param);
            upLoadUserLog(map);
            Toast.makeText(activity, "用户未税登！", Toast.LENGTH_SHORT).show();
            doUnLoadToService(entity);
        }
    }

    private void analysisJson(String msgStr) {
        boolean isOfwlgsinfo = false;
        if (user == null) {
            return;
        }
        if (TextUtils.isEmpty(user.getOfwlgsinfo())) {
            isOfwlgsinfo = true;
        } else {
            isOfwlgsinfo = false;
        }
        List<GoodsEntity> mList = new ArrayList<>();
        try {
            JSONArray array = new JSONArray(msgStr);
            for (int i = 0; i < array.length(); i++) {
                JSONObject object = array.getJSONObject(i);
                GoodsEntity entity = new GoodsEntity();
                String formCity = object.getString("data:from_city");
                String formCounty = object.getString("data:from_county");
                entity.setStartAddress(formCity + " " + formCounty);
                String toCity = object.getString("data:to_city");
                String toCounty = object.getString("data:to_county");
                String fromLon = object.getString("data:from_lon");
                entity.setLon_from(fromLon);
                String fromLat = object.getString("data:from_lat");
                entity.setLat_from(fromLat);
                String toLon = object.getString("data:to_lon");
                entity.setLon_to(toLon);
                String toLat = object.getString("data:to_lat");
                entity.setLat_to(toLat);
                entity.setEndAddress(toCity + " " + toCounty);
                String sid = object.getString("data:sid");
                entity.setSid(sid);
                String name = object.getString("data:hwmc");
                entity.setName(name);
                String zl = object.getString("data:hwzl");
                entity.setWeight(zl);
                String tj = object.getString("data:hwtj");
                entity.setVolume(tj);

                if (object.has("data:req_length")) {
                    String req_length = object.getString("data:req_length");
                    entity.setReq_length(req_length);
                }
                if (object.has("data:pd_ext")) {
                    String pd_ext = object.getString("data:pd_ext");
                    entity.setPd_ext(pd_ext);

                }

                if (object.has("data:task_id")) {
                    String task_id = object.getString("data:task_id");
                    entity.setTask_id(task_id);

                }
                if (object.has("data:driverPrice")) {
                    String driverPrice = object.getString("data:driverPrice");
                    entity.setDriverPrice(driverPrice);
                }

                if (object.has("data:bz")) {
                    String bz = object.getString("data:bz");
                    entity.setBz(bz);
                }
                String hzxm = object.getString("data:fhr");
                entity.setHzxm(hzxm);
                if(object.has("data:fhrdh")){
                    entity.setFhrdh(object.getString("data:fhrdh"));
                }
                String zyf = object.getString("data:yf");
                entity.setZyf(zyf);
                if (object.has("data:alctid")) {
                    String alctid = object.getString("data:alctid");
                    entity.setAlctId(alctid);
                } else {
                    entity.setAlctId(Constants.APPIDENTITY);
                }
                if (object.has("data:alctcode")) {
                    String alctcode = object.getString("data:alctcode");
                    entity.setAlctCode(alctcode);
                } else {
                    entity.setAlctCode(Constants.ENTERPRISECODE);
                }
                if (object.has("data:alctkey")) {
                    String alctKey = object.getString("data:alctkey");
                    entity.setAlctKey(alctKey);
                } else {
                    entity.setAlctKey(Constants.APPKEY);
                }
                if (object.has("data:sxfbl")) {
                    String bl = object.getString("data:sxfbl");
                    entity.setBl(bl);
                } else {
                    entity.setBl("0.1");
                }
                if (object.has("data:moxid")) {
                    String moxid = object.getString("data:moxid");
                    entity.setMoxid(moxid);
                    System.out.println("mox---" + entity.toString());
                }

                if(object.has("data:yd_cph")){
                    entity.setYd_cph(object.getString("data:yd_cph"));

                }
                if(object.has("data:to_addr")){
                    entity.setTo_addr(object.getString("data:to_addr"));

                }
                if(object.has("data:from_addr")){
                    entity.setFrom_addr(object.getString("data:from_addr"));

                }


                String time = object.getString("data:create_time");
                entity.setTime(time);
                String shrName = object.getString("data:shr");
                entity.setShrName(shrName);
                String shrTel = object.getString("data:shrdh");
                entity.setShrTel(shrTel);
                String statusName = object.getString("data:yd_trans_status");
                entity.setStatusName(statusName);
                String rowid = object.getString("rowid");
                entity.setRowid(rowid);
                String status = object.getString("data:yd_status");
                entity.setStatus(status);
                String driver = object.getString("data:yd_driver");
                entity.setDriver(driver);
                String huozhu = object.getString("data:fbr");
                entity.setHuozhu(huozhu);
                entity.setOfwlgsinfo(isOfwlgsinfo);
                if (object.has("data:policyNo")) {
                    String policyNo = object.getString("data:policyNo");
                    entity.setPolicyNo(policyNo);
                }
                if (object.has("data:yd_1_time")) {
                    entity.setJdTime(object.getString("data:yd_1_time"));
                }
                if (object.has("data:pdwlgs")) {
                    entity.setPdwlgs(object.getString("data:pdwlgs"));
                } else {
                    entity.setPdwlgs("-1");
                }
                mList.add(entity);
            }
            final MainActivity activity = mActivityReference.get();
            if (activity == null) {
                return;
            }
            if (onPouse) {
                return;
            }
            if (isRefrush) {
                list.clear();
                if (adapter != null) {
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();
                }
            }
            list.addAll(mList);
            if (adapter != null) {
                adapter.setList(list);
                adapter.notifyDataSetChanged();

            }
            if (adapter == null) {
                adapter = new WWCAdapter(activity, list);
                mListView.setAdapter(adapter);
                adapter.setOnItemButtonClickListener(this);
                adapter.setOnItemClickListener(new WWCAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int pos, GoodsEntity entity) {
                        GoodsEntity e = (GoodsEntity) adapter.getItem(pos);
                        Intent intent = new Intent(getActivity(), GoodsListDetailActivity.class);
                        intent.putExtra("type", 1);
                        intent.putExtra("entity", e);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }
                });
            } else {
                adapter.setList(list);
                adapter.notifyDataSetChanged();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            isRefrush = false;
        }
    }


    private void doAlctUnLoad(final GoodsEntity entity, final String hwmc, final String shipmentCode, final String enterpriseCode) {
        MDPLocationCollectionManager.unload(getActivity(), shipmentCode, enterpriseCode, getLocation(entity, false), new OnResultListener() {
            public void onSuccess() {
                //step:20
                //tel:157****0385
                //msg:登录获取用户信息成功
                //time:2018-12-18:11:11:10
                //rowid:
                unLoadCount = 0;
                Map<String, String> map = new HashMap<>();
                map.put("step", "20");
                map.put("tel", user.getUserId());
                map.put("msg", "安联unload执行成功，开始执行签收sign");
                map.put("time", getNowtime());
                map.put("rowid", entity.getRowid());
                upLoadUserLog(map);
                Log.e("doUnLoad onSuccess", "onSuccess----");
                // doPickUp(shipmentCode,enterpriseCode);
                doSign(entity, shipmentCode, enterpriseCode, hwmc);
                doUnLoadToService(entity);
            }

            @Override
            public void onFailure(String s, String s1) {
                if (s1.contains("NFC")) {
                    showUnLoadNfcView("请将手机靠近NFC设备获取ID", entity);
                    Map<String, String> map = new HashMap<>();
                    map.put("step", "14");
                    map.put("tel", user.getUserId());
                    map.put("msg", "安联unload执行失败，失败信息包含NFC,提示用户开启NFC检测,失败原因：" + "s=" + s + ":s1=" + s1);
                    map.put("time", getNowtime());
                    map.put("rowid", entity.getRowid());
                    String param = "shipmentCode=" + shipmentCode + "; enterpriseCode=" + enterpriseCode + ";货源信息=" + entity.toString();
                    map.put("param", param);
                    upLoadUserLog(map);
                } else {
                    //step:14
                    //tel:157****0385
                    //msg:登录获取用户信息成功
                    //time:2018-12-18:11:11:10
                    //rowid:
                    if (s1.contains("身份验证已过期")) {
                        if (unLoadCount < 3) {
                            doAlctUnLoad(entity, hwmc, shipmentCode, enterpriseCode);
                            unLoadCount = unLoadCount + 1;
                        } else {
                            unLoadCount = 0;
                            Map<String, String> map = new HashMap<>();
                            map.put("step", "14");
                            map.put("tel", user.getUserId());
                            map.put("msg", "安联unload执行失败，失败信息未包含NFC,不开启NFC检测,失败原因：" + "s=" + s + ":s1=" + s1);
                            map.put("time", getNowtime());
                            map.put("rowid", entity.getRowid());
                            String param = "shipmentCode=" + shipmentCode + "; enterpriseCode=" + enterpriseCode + ";货源信息=" + entity.toString();
                            map.put("param", param);
                            upLoadUserLog(map);
                            Log.e("doPickUp", "onFailure----" + s + s1);
                            doUploadErrorMsg(shipmentCode, s, s1);
                            doUnLoadToService(entity);
                        }
                    } else {
                        Map<String, String> map = new HashMap<>();
                        map.put("step", "14");
                        map.put("tel", user.getUserId());
                        map.put("msg", "安联unload执行失败，失败信息未包含NFC,不开启NFC检测,失败原因：" + "s=" + s + ":s1=" + s1);
                        map.put("time", getNowtime());
                        map.put("rowid", entity.getRowid());
                        String param = "shipmentCode=" + shipmentCode + "; enterpriseCode=" + enterpriseCode + ";货源信息=" + entity.toString();
                        map.put("param", param);
                        upLoadUserLog(map);
                        Log.e("doPickUp", "onFailure----" + s + s1);
                        doUploadErrorMsg(shipmentCode, s, s1);
                        doUnLoadToService(entity);
                    }
                }
            }
        });
    }


    private void doUnLoad(final GoodsEntity entity, final String hwmc, final String shipmentCode, final String enterpriseCode) {
        getShipmentStatus(shipmentCode, enterpriseCode, new OnShipmentStatusListener() {
            @Override
            public void onSuccess(ShipmentStatusEnum status) {
                Log.e("doUnLoad ShipmentStatus", "ShipmentStatus==" + status.getValue());
                switch (status) {
                    case PICKUPED://运单提货状态
                        doAlctUnLoad(entity, hwmc, shipmentCode, enterpriseCode);
                        break;
                    case UNLOADED://运单到货状态
                        doAlctSign(entity, shipmentCode, enterpriseCode, hwmc);
                        break;
                    case SIGNED://运单签收状态
                        doAlctPod(entity, shipmentCode, enterpriseCode);
                        break;
                    default:
                        doAlctUnLoad(entity, hwmc, shipmentCode, enterpriseCode);
                        break;
                }
            }

            @Override
            public void onError() {
                Log.e("doUnLoad ShipmentStatus", "onError==");
                doAlctUnLoad(entity, hwmc, shipmentCode, enterpriseCode);
            }
        });
    }

    private void doAlctSign(final GoodsEntity entity, final String shipmentCode, final String enterpriseCode, final String hwmc) {
        MDPLocationCollectionManager.sign(getActivity(), shipmentCode, enterpriseCode, getLocation(entity, false), getGoodsList(hwmc)
                , new OnResultListener() {
                    @Override
                    public void onSuccess() {
                        //step:22
                        //tel:157****0385
                        //msg:登录获取用户信息成功
                        //time:2018-12-18:11:11:10
                        //rowid:
                        signCount = 0;
                        Map<String, String> map = new HashMap<>();
                        map.put("step", "22");
                        map.put("tel", user.getUserId());
                        map.put("msg", "安联sign执行成功，开始执行回单pod");
                        map.put("time", getNowtime());
                        map.put("rowid", entity.getRowid());
                        upLoadUserLog(map);
                        Log.e("doSign onSuccess", "onSuccess----");
                        pod(entity, shipmentCode, enterpriseCode);
                        getLocation(entity.getRowid(), entity, shipmentCode, enterpriseCode);

                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        //step:23

                        //tel:157****0385
                        //msg:登录获取用户信息成功
                        //time:2018-12-18:11:11:10
                        //rowid:
                        if (s1.contains("身份验证已过期")) {
                            if (signCount < 3) {
                                doAlctSign(entity, shipmentCode, enterpriseCode, hwmc);
                                signCount = signCount + 1;
                            } else {
                                signCount = 0;
                                Map<String, String> map = new HashMap<>();
                                map.put("step", "23");
                                map.put("tel", user.getUserId());
                                map.put("msg", "安联sign执行失败，失败原因：" + "s=" + s + ":s1=" + s1 + "");
                                map.put("time", getNowtime());
                                map.put("rowid", entity.getRowid());
                                String param = "shipmentCode=" + shipmentCode + "; enterpriseCode=" + enterpriseCode + ";货源信息=" + entity.toString();
                                map.put("param", param);
                                upLoadUserLog(map);
                                Log.e("doSign", "onFailure----" + s + s1);
                                doUploadErrorMsg(shipmentCode, s, s1);
                                getLocation(entity.getRowid(), entity, shipmentCode, enterpriseCode);

                            }
                        } else {
                            Map<String, String> map = new HashMap<>();
                            map.put("step", "23");
                            map.put("tel", user.getUserId());
                            map.put("msg", "安联sign执行失败，失败原因：" + "s=" + s + ":s1=" + s1 + "");
                            map.put("time", getNowtime());
                            map.put("rowid", entity.getRowid());
                            String param = "shipmentCode=" + shipmentCode + "; enterpriseCode=" + enterpriseCode + ";货源信息=" + entity.toString();
                            map.put("param", param);
                            upLoadUserLog(map);
                            Log.e("doSign", "onFailure----" + s + s1);
                            doUploadErrorMsg(shipmentCode, s, s1);
                            getLocation(entity.getRowid(), entity, shipmentCode, enterpriseCode);
                        }

                    }
                });
    }

    private void doSign(final GoodsEntity entity, final String shipmentCode, final String enterpriseCode, final String hwmc) {
        getShipmentStatus(shipmentCode, enterpriseCode, new OnShipmentStatusListener() {
            @Override
            public void onSuccess(ShipmentStatusEnum status) {
                Log.e("doSign ShipmentStatus", "ShipmentStatus==" + status.getValue());
                switch (status) {
                    case UNLOADED://运单到货状态
                        doAlctSign(entity, shipmentCode, enterpriseCode, hwmc);
                        break;
                    case SIGNED://运单签收状态
                        doAlctPod(entity, shipmentCode, enterpriseCode);
                        break;
                    default:
                        doAlctSign(entity, shipmentCode, enterpriseCode, hwmc);
                        break;
                }
            }

            @Override
            public void onError() {
                Log.e("doSign ShipmentStatus", "onError==");
                doAlctSign(entity, shipmentCode, enterpriseCode, hwmc);
            }
        });
    }

    private void doAlctPod(final GoodsEntity entity, final String shipmentCode, final String enterpriseCode) {
        MDPLocationCollectionManager.pod(getActivity(), shipmentCode, enterpriseCode, getLocation(entity, false), new OnResultListener() {
            @Override
            public void onSuccess() {
                //step:24
                //tel:157****0385
                //msg:登录获取用户信息成功
                //time:2018-12-18:11:11:10
                //rowid:
                podCount = 0;
                Map<String, String> map = new HashMap<>();
                map.put("step", "24");
                map.put("tel", user.getUserId());
                map.put("msg", "安联pod执行成功，开始跳转到上传卸货照、回单照页面");
                map.put("time", getNowtime());
                map.put("rowid", entity.getRowid());
                upLoadUserLog(map);
                allon2 = DateUtils.getTimeMillis();
                long l = DateUtils.getBetwnTime(allon2, allon1);
                Log.e("sdal_time", "sdal_time==" + l);
                Log.e("pod onSuccess", "onSuccess----");
                Intent intent = new Intent(getActivity(), UpLoadImgActivity.class);
                intent.putExtra("entity", entity);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }

            @Override
            public void onFailure(String s, String s1) {
                //step:25
                //tel:157****0385
                //msg:登录获取用户信息成功
                //time:2018-12-18:11:11:10
                //rowid:
                if (s1.contains("身份验证已过期")) {
                    if (podCount < 3) {
                        doAlctPod(entity, shipmentCode, enterpriseCode);
                        podCount = podCount + 1;
                    } else {
                        podCount = 0;
                        Map<String, String> map = new HashMap<>();
                        map.put("step", "25");
                        map.put("tel", user.getUserId());
                        map.put("msg", "安联pod执行失败，失败原因：" + "s=" + s + ":s1=" + s1 + " 未跳转到上传卸货照、回单照页面");
                        map.put("time", getNowtime());
                        map.put("rowid", entity.getRowid());
                        String param = "shipmentCode=" + shipmentCode + "; enterpriseCode=" + enterpriseCode + ";货源信息=" + entity.toString();
                        map.put("param", param);
                        upLoadUserLog(map);
                        Log.e("doSign", "onFailure----" + s + s1);
                        doUploadErrorMsg(shipmentCode, s, s1);
                    }

                } else {
                    Map<String, String> map = new HashMap<>();
                    map.put("step", "25");
                    map.put("tel", user.getUserId());
                    map.put("msg", "安联pod执行失败，失败原因：" + "s=" + s + ":s1=" + s1 + " 未跳转到上传卸货照、回单照页面");
                    map.put("time", getNowtime());
                    map.put("rowid", entity.getRowid());
                    String param = "shipmentCode=" + shipmentCode + "; enterpriseCode=" + enterpriseCode + ";货源信息=" + entity.toString();
                    map.put("param", param);
                    upLoadUserLog(map);
                    Log.e("doSign", "onFailure----" + s + s1);
                    doUploadErrorMsg(shipmentCode, s, s1);
                }

            }
        });

    }

    private void pod(final GoodsEntity entity, final String shipmentCode, final String enterpriseCode) {
        getShipmentStatus(shipmentCode, enterpriseCode, new OnShipmentStatusListener() {
            @Override
            public void onSuccess(ShipmentStatusEnum status) {
                Log.e("pod ShipmentStatus", "ShipmentStatus==" + status.getValue());
                switch (status) {
                    case SIGNED://运单签收状态
                        doAlctPod(entity, shipmentCode, enterpriseCode);
                        break;
                    default:
                        doAlctPod(entity, shipmentCode, enterpriseCode);
                        break;
                }
            }

            @Override
            public void onError() {
                Log.e("pod ShipmentStatus", "onError==");
                doAlctPod(entity, shipmentCode, enterpriseCode);
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

    private Location getLocation(GoodsEntity entity, boolean isPickUp) {
        double lat;
        double lon;
        if (isPickUp) {
            lat = Double.parseDouble(entity.getLat_from());
            lon = Double.parseDouble(entity.getLon_from());
        } else {
            lat = Double.parseDouble(entity.getLat_to());
            lon = Double.parseDouble(entity.getLon_to());
        }
        Location location = new Location();
        location.setBaiduLatitude(lat);
        location.setBaiduLongitude(lon);
        location.setLocation("");
        location.setTime(getNowtime());
        return location;

    }
    public void submitOrderEtcInfo(GoodsEntity entity,int t){
        Map<String,String> map=new HashMap<>();
        map.put("alctCode",entity.getAlctCode());
        map.put("num",entity.getSid());
        map.put("plateNum",entity.getYd_cph());
        map.put("plateColor","1");
        map.put("rowid",entity.getRowid());
        map.put("startTime",DateUtils.getTimeWishT(new Date()));
        map.put("sourceAddr",entity.getFrom_addr());
        map.put("destAddr",entity.getTo_addr());
        Date date=DateUtils.getNextDay(new Date(),3);
        map.put("predictEndTime",DateUtils.getTimeWishT(date));
        double d=Double.valueOf(entity.getZyf());
        long b=(long)(d*100);
        map.put("fee",b+"");
        map.put("titleType","2");
        map.put("type","0");
        map.put("statuType",t+"");
        Gson gson=new Gson();
        String params=gson.toJson(map);
        Log.e("params","params=="+params);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.ETC_URL).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call=null;
        if(t==1){
            call = serviceStore.submitOrderEctInfoStart(map);
        }else {
            call = serviceStore.submitOrderEctInfoEnd(map);
        }
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody body=response.body();
                String str= null;
                try {
                    if(null!=body){
                        str = body.string();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("onResponse","onResponse=="+str);

            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure","onFailure=="+t.getMessage());
            }
        });
    }


    private void doPs(final GoodsEntity entity) {
        submitOrderEtcInfo(entity,1);
        pickUpCount = 0;
        String sdmsg = SharePreferenceUtil.getInstance(getActivity()).getSDMsg();
        // dopickup(entity.getMoxid(), true+"");
        if ("0".equals(sdmsg)) {
            //step:9
            //tel:157****0385
            //msg:登录获取用户信息成功
            //time:2018-12-18:11:11:10
            //rowid:

            if (TextUtils.isEmpty(nfcid)) {
                Map<String, String> map = new HashMap<>();
                map.put("step", "9");
                map.put("tel", user.getUserId());
                map.put("msg", "用户已税登,开始执行提货pickup");
                map.put("time", getNowtime());
                map.put("rowid", entity.getRowid());
                upLoadUserLog(map);
                doPickUp(entity.getRowid(), entity.getAlctCode(), entity);

            } else {
                Map<String, String> map = new HashMap<>();
                map.put("step", "9");
                map.put("tel", user.getUserId());
                map.put("msg", "用户已税登,开始执行提货checkNfc");
                map.put("time", getNowtime());
                map.put("rowid", entity.getRowid());
                upLoadUserLog(map);
                doCheckNfc(entity.getRowid(), entity.getAlctCode(), entity);
            }
            //doCheckNfc(entity.getRowid(), entity.getAlctCode(), entity);
            // doCheckNfc(entity.getRowid(), entity.getAlctCode(), entity);
        } else {
            //step:10
            //tel:157****0385
            //msg:登录获取用户信息成功
            //time:2018-12-18:11:11:10
            //rowid:
            Map<String, String> map = new HashMap<>();
            map.put("step", "10");
            map.put("tel", user.getUserId());
            map.put("msg", "用户未税登");
            map.put("time", getNowtime());
            map.put("rowid", entity.getRowid());
            map.put("param", entity.toString());
            upLoadUserLog(map);
            changeOrderStatu(entity);
        }

    }

    private void doUnLoadToService(final GoodsEntity entity) {
        String req = entity.getReq_length();
        if (!TextUtils.isEmpty(req)) {
            if ("4.2米".equals(req)) {
                if (mLocClient != null) {
                    myListener.setRid(null);
                    mLocClient.stop();
                }
            }
        }
        final LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.getInstance();
        loadingDialogFragment.showF(getChildFragmentManager(), "sdDialogFragment");
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        if (dao != null) {
                            dao.delLocInfo(entity.getRowid());
                        }

                        if (loadingDialogFragment != null && loadingDialogFragment.isAdded()) {
                            loadingDialogFragment.dismissAllowingStateLoss();
                        }

                        long sdl2 = DateUtils.getTimeMillis();
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.getBoolean("success")) {
                                Toast.makeText(activity, "送达成功！", Toast.LENGTH_SHORT).show();
                                RxBus.getInstance().send(new EventEntity("刷新", "刷新"));
                                RxBus.getInstance().send(new EventEntity("dpj", "刷新"));
/*                                String sdmsg = SharePreferenceUtil.getInstance(activity).getSDMsg();
                                if ("0".equals(sdmsg)) {
                                    doUnLoad(entity, entity.getName(), entity.getRowid(), entity.getAlctCode());

                                }*/
                            } else {
                                if (loadingDialogFragment != null && loadingDialogFragment.isAdded()) {
                                    loadingDialogFragment.dismissAllowingStateLoss();
                                }
                                String error = jsonObject.getString("message");
                                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                                Log.e("create_request onError", msg);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            isPickUp = false;
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        if (loadingDialogFragment != null && loadingDialogFragment.isAdded()) {
                            loadingDialogFragment.dismissAllowingStateLoss();
                        }
                        Toast.makeText(activity, "送达失败！", Toast.LENGTH_SHORT).show();
                        Log.e("create_request onError", msg);
                    }
                }));
    }


    private void doCheckNfc(final String shipmentCode, final String enterpriseCode, final GoodsEntity entity) {
        //doPickUp(shipmentCode, enterpriseCode,entity);
        allon1 = DateUtils.getTimeMillis();
        Log.e("entity111==", "entity1111==" + entity.toString());
        MDPLocationCollectionManager.checkNfc(getActivity(), shipmentCode, enterpriseCode, nfcid, new OnResultListener() {
            @Override
            public void onSuccess() {
                //step:11
                //tel:157****0385
                //msg:登录获取用户信息成功
                //time:2018-12-18:11:11:10
                //rowid:
                Map<String, String> map = new HashMap<>();
                map.put("step", "11");
                map.put("tel", user.getUserId());
                map.put("msg", "安联doCheckNfc执行成功");
                map.put("time", getNowtime());
                map.put("rowid", entity.getRowid());
                upLoadUserLog(map);
                Log.e("doCheckNfc onSuccess", "onSuccess----");
                doPickUp(shipmentCode, enterpriseCode, entity);
            }

            @Override
            public void onFailure(String s, String s1) {
                //step:12
                //tel:157****0385
                //msg:登录获取用户信息成功
                //time:2018-12-18:11:11:10
                //rowid:
                // doPickUp(shipmentCode, enterpriseCode, entity);
                Map<String, String> map = new HashMap<>();
                map.put("step", "12");
                map.put("tel", user.getUserId());
                map.put("msg", "安联doCheckNfc执行失败，失败原因：" + "s=" + s + ":s1=" + s1);
                map.put("time", getNowtime());
                map.put("rowid", entity.getRowid());
                String param = "NFCID=" + nfcid + ";shipmentCode=" + shipmentCode + "; enterpriseCode="
                        + enterpriseCode + ";货源信息=" + entity.toString();
                map.put("param", param);
                upLoadUserLog(map);
                Log.e("doCheckNfc", "onFailure----" + s + s1);
                doUploadErrorMsg(shipmentCode, s, s1);
                checkNFCAgainMsg("checkNfc失败！" + "\n" + "是否重新扫描？", entity);
            }
        });
    }

    private void checkNFCAgainMsg(String msg, final GoodsEntity entity) {
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getChildFragmentManager(), "checkNFCAgainMsg");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismissAllowingStateLoss();
                //执行平台操作
                changeOrderStatu(entity);

            }

            @Override
            public void onClickOk() {
                dialog.dismissAllowingStateLoss();
                nfcTipsFragment.showF(getChildFragmentManager(), "nfcTipsFragment0");
                handler.sendEmptyMessageDelayed(NFCTIPSDISMISS, 5000);
            }
        });
    }


    private void doAlctPickUp(final String shipmentCode, final String enterpriseCode, final GoodsEntity entity) {
        final MainActivity activity = mActivityReference.get();
        if (activity == null) {
            return;
        }

        //需要获取坐标
        MDPLocationCollectionManager.pickup(getActivity(), shipmentCode, enterpriseCode, getLocation(entity, true), new OnResultListener() {
            @Override
            public void onSuccess() {//身份验证已过期
                //step:13
                //tel:157****0385
                //msg:登录获取用户信息成功
                //time:2018-12-18:11:11:10
                //rowid:
                pickUpCount = 0;
                changeOrderStatu(entity);
                Map<String, String> map = new HashMap<>();
                map.put("step", "13");
                map.put("tel", user.getUserId());
                map.put("msg", "安联pickup执行成功，提货成功");
                map.put("time", getNowtime());
                map.put("rowid", entity.getRowid());
                upLoadUserLog(map);
                allon2 = DateUtils.getTimeMillis();
                long l = DateUtils.getBetwnTime(allon2, allon1);
                Log.e("PSal_TimeMillis==", "psalTimeMillis==" + l);
                Log.e("doPickUp", "onSuccess----");
                isPickUp = true;
                StringBuffer sbf = new StringBuffer();
                sbf.append("安联pickup执行成功，提货成功");
                dopickup(entity.getRowid(), true + "", sbf.toString());
            }

            @Override
            public void onFailure(String s, String s1) {
                if (s1.contains("NFC")) {
                    showPickUpNfcView("请将手机靠近NFC设备获取ID", entity);
                    Map<String, String> map = new HashMap<>();
                    map.put("step", "14");
                    map.put("tel", user.getUserId());
                    map.put("msg", "安联pickup执行失败，失败信息包含NFC,提示用户开启NFC检测,失败原因：" + "s=" + s + ":s1=" + s1);
                    map.put("time", getNowtime());
                    map.put("rowid", entity.getRowid());
                    String param = "shipmentCode=" + shipmentCode + "; enterpriseCode=" + enterpriseCode + ";货源信息=" + entity.toString();
                    map.put("param", param);
                    upLoadUserLog(map);
                    StringBuffer sbf = new StringBuffer();
                    sbf.append("安联pickup执行失败，失败信息包含NFC,提示用户开启NFC检测,失败原因：" + "s=" + s + ":s1=" + s1);
                    dopickup(entity.getRowid(), false + "", sbf.toString());
                } else {
                    //step:14
                    //tel:157****0385
                    //msg:登录获取用户信息成功
                    //time:2018-12-18:11:11:10
                    //rowid:
                    if (s1.contains("身份验证已过期")) {
                        if (pickUpCount < 3) {
                            doAlctPickUp(shipmentCode, enterpriseCode, entity);
                            pickUpCount = pickUpCount + 1;
                        } else {
                            pickUpCount = 0;
                            Map<String, String> map = new HashMap<>();
                            map.put("step", "14");
                            map.put("tel", user.getUserId());
                            map.put("msg", "安联pickup执行失败，失败信息未包含NFC,不开启NFC检测,失败原因：" + "s=" + s + ":s1=" + s1);
                            map.put("time", getNowtime());
                            map.put("rowid", entity.getRowid());
                            String param = "shipmentCode=" + shipmentCode + "; enterpriseCode=" + enterpriseCode + ";货源信息=" + entity.toString();
                            map.put("param", param);
                            upLoadUserLog(map);
                            Log.e("doPickUp", "onFailure----" + s + s1);
                            doUploadErrorMsg(shipmentCode, s, s1);
                            isPickUp = true;
                            changeOrderStatu(entity);
                            StringBuffer sbf = new StringBuffer();
                            sbf.append("安联pickup执行失败，失败信息未包含NFC,不开启NFC检测,失败原因：" + "s=" + s + ":s1=" + s1);
                            dopickup(entity.getRowid(), false + "", sbf.toString());
                        }
                    } else {
                        Map<String, String> map = new HashMap<>();
                        map.put("step", "14");
                        map.put("tel", user.getUserId());
                        map.put("msg", "安联pickup执行失败，失败信息未包含NFC,不开启NFC检测,失败原因：" + "s=" + s + ":s1=" + s1);
                        map.put("time", getNowtime());
                        map.put("rowid", entity.getRowid());
                        String param = "shipmentCode=" + shipmentCode + "; enterpriseCode=" + enterpriseCode + ";货源信息=" + entity.toString();
                        map.put("param", param);
                        upLoadUserLog(map);
                        Log.e("doPickUp", "onFailure----" + s + s1);
                        doUploadErrorMsg(shipmentCode, s, s1);
                        isPickUp = true;
                        changeOrderStatu(entity);
                        StringBuffer sbf = new StringBuffer();
                        sbf.append("安联pickup执行失败，失败信息未包含NFC,不开启NFC检测,失败原因：" + "s=" + s + ":s1=" + s1);
                        dopickup(entity.getRowid(), false + "", sbf.toString());
                    }

                }
            }
        });
    }


    private void doPickUp(final String shipmentCode, final String enterpriseCode, final GoodsEntity entity) {
        getShipmentStatus(shipmentCode, enterpriseCode, new OnShipmentStatusListener() {
            @Override
            public void onSuccess(ShipmentStatusEnum status) {
                Log.e("pickup ShipmentStatus", "ShipmentStatus==" + status.getValue());
                switch (status) {
                    case CONFIRMED://运单已确认状态
                        doAlctPickUp(shipmentCode, enterpriseCode, entity);
                        break;
                }
            }

            @Override
            public void onError() {
                Log.e("pickup ShipmentStatus", "onError");
                doAlctPickUp(shipmentCode, enterpriseCode, entity);
            }
        });

    }

    private void showPickUpNfcView(String msg, final GoodsEntity entity) {
        nfcDialogFragment = ExitDialogFragment.getInstance(msg);
        nfcDialogFragment.show(getChildFragmentManager(), "showNfcView");
        nfcDialogFragment.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                nfcDialogFragment.dismissAllowingStateLoss();
                changeOrderStatu(entity);

            }

            @Override
            public void onClickOk() {
                nfcDialogFragment.dismissAllowingStateLoss();
                nfcTipsFragment.showF(getChildFragmentManager(), "nfcTipsFragment");
                handler.sendEmptyMessageDelayed(NFCTIPSDISMISS, 5000);

            }
        });
    }

    private void showUnLoadNfcView(String msg, final GoodsEntity entity) {
        nfcDialogFragment = ExitDialogFragment.getInstance(msg);
        nfcDialogFragment.show(getChildFragmentManager(), "showNfcView");
        nfcDialogFragment.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                nfcDialogFragment.dismissAllowingStateLoss();
                doUnLoadToService(entity);

            }

            @Override
            public void onClickOk() {
                nfcDialogFragment.dismissAllowingStateLoss();
                nfcTipsFragment.showF(getChildFragmentManager(), "nfcTipsFragment");
                handler.sendEmptyMessageDelayed(NFCTIPSDISMISS, 5000);

            }
        });
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {

                    }

                    @Override
                    public void onError(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }

                    }
                }));
    }

    private void doPayMoney(String price, double bl, final GoodsEntity entity) {

        Log.e("doPayMoney==", entity.toString());
        System.out.println(entity.toString());
        final PayMoneyDialog dialog = PayMoneyDialog.getInstance(price, bl, entity.getSid(), user.getUserName(), entity);
        dialog.show(getChildFragmentManager(), "paymoneydialog");
        dialog.setOnOrderListener(new PayMoneyDialog.OnOrderListener() {
            @Override
            public void onOrder(String psd, String money) {
                dialog.dismiss();
                uploadPayMoney(psd, money, entity);

            }
        });
        dialog.setOnDismissListener(new PayMoneyDialog.onDismissListener() {
            @Override
            public void onDismiss() {
                dialog.dismiss();

            }
        });
    }

    private void uploadPayMoney(final String psd, String money, final GoodsEntity entity) {
        final LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.getInstance();
        loadingDialogFragment.showF(getChildFragmentManager(), "paydialog");
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
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("uploadPayMoney", "==" + msg);
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
                                SharePreferenceUtil.getInstance(getActivity()).setPayPwd(psd);
                                submit_order(orderno, tokenid, entity, loadingDialogFragment);
                            } else {
                                String error = jsonObject.getString("message");
                                loadingDialogFragment.dismissAllowingStateLoss();
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
                        mSwipeToLoadLayout.setRefreshing(true);
                        loadingDialogFragment.dismissAllowingStateLoss();
                        Toast.makeText(activity, "支付失败！", Toast.LENGTH_SHORT).show();
                        Log.e("create_request onError", msg);
                    }
                }));

    }

    private void submit_order(String orderno, String tokenid, final GoodsEntity entity, final LoadingDialogFragment loadingDialogFragment) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", user.getUserId());
        map.put("app", Constants.AppId);
        map.put("token", user.getToken());
        map.put("order_no", orderno);
        map.put("req_type", "DEPS");
        map.put("tokenid", tokenid);
        map.put("rowid", entity.getRowid());
        RequestManager.getInstance()
                .mServiceStore
                .submit_order(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("submit_order", "==" + msg);
                        loadingDialogFragment.dismissAllowingStateLoss();
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.getBoolean("success")) {
                                //doAnsycWayBill(entity.getRowid());
                                Toast.makeText(activity, "支付成功！", Toast.LENGTH_SHORT).show();
                                mSwipeToLoadLayout.setRefreshing(true);
                            } else {
                                mSwipeToLoadLayout.setRefreshing(true);
                                loadingDialogFragment.dismissAllowingStateLoss();
                                String error = jsonObject.getString("message");
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
                        Log.e("submit_order onError", "==" + msg);
                        mSwipeToLoadLayout.setRefreshing(true);
                        loadingDialogFragment.dismissAllowingStateLoss();
                        Toast.makeText(activity, "支付失败！", Toast.LENGTH_SHORT).show();
                        Log.e("create_request onError", msg);
                    }
                }));
    }

    private void dopickup(String id, String orderType, String msg) {
        Log.e("moxid==", "moxid==" + id);
        Map<String, String> map = new HashMap<>();
        map.put("rowid", id);
        map.put("orderType", orderType);
        map.put("msg", msg);
        RequestManager.getInstance()
                .mServiceStore
                .pickup(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        Log.e("dopickup", "str===" + str);
                    }

                    @Override
                    public void onError(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        Log.e("dopickup onError", "str===" + msg);
                    }
                }));

    }

    private void changeOrderStatu(final GoodsEntity entity) {
        String req = entity.getReq_length();
        if (!TextUtils.isEmpty(req)) {
            if ("4.2米".equals(req)) {
                if (mLocClient != null) {
                    myListener.setRid(entity.getRowid());
                    mLocClient.start();
                }
            }
        }

        final long startMillis = DateUtils.getTimeMillis();
        final LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.getInstance();
        loadingDialogFragment.showF(getChildFragmentManager(), "psdialog");
        Map<String, String> map = new HashMap<>();
        map.put("mobile", user.getUserId());
        map.put("app", Constants.AppId);
        map.put("token", user.getToken());
        map.put("status_code", "1");
        map.put("command", "开始配送");
        map.put("rowid", entity.getRowid());
        System.out.println("mox+++" + entity.toString());
        RequestManager.getInstance()
                .mServiceStore
                .change_yd_status(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        long endMillis = DateUtils.getTimeMillis();
                        long l = DateUtils.getBetwnTime(endMillis, startMillis);
                        Log.e("PS_TimeMillis==", "psTimeMillis==" + l);
                        Log.e("mox===", entity.toString());
                        if (loadingDialogFragment != null) {
                            loadingDialogFragment.dismissAllowingStateLoss();

                        }

                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject jsonObject = new JSONObject(str);
                            if (jsonObject.getBoolean("success")) {
                                Toast.makeText(activity, "开始配送成功！", Toast.LENGTH_SHORT).show();
                                /*String sdmsg = SharePreferenceUtil.getInstance(getActivity()).getSDMsg();
                                // dopickup(entity.getMoxid(), true+"");
                                if ("0".equals(sdmsg)) {
                                    doCheckNfc(entity.getRowid(), entity.getAlctCode(), entity);
                                }*/
                                mSwipeToLoadLayout.setRefreshing(true);
                            } else {
                                String error = jsonObject.getString("message");
                                mSwipeToLoadLayout.setRefreshing(true);
                                if (loadingDialogFragment != null) {
                                    loadingDialogFragment.dismissAllowingStateLoss();

                                }
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
                        mSwipeToLoadLayout.setRefreshing(true);
                        loadingDialogFragment.dismissAllowingStateLoss();
                        Toast.makeText(activity, "开始配送失败！", Toast.LENGTH_SHORT).show();
                        Log.e("create_request onError", msg);
                    }
                }));
    }


    private double getPointsDistances(String lat1, String lon1, String lat2, String lon2) {
        double distance = 0;
        double dLat1 = Double.parseDouble(lat1);
        double dLon1 = Double.parseDouble(lon1);
        double dLat2 = Double.parseDouble(lat2);
        double dLon2 = Double.parseDouble(lon2);
        LatLng p1LL = new LatLng(dLat1, dLon1);
        LatLng p2LL = new LatLng(dLat2, dLon2);
        distance = DistanceUtil.getDistance(p1LL, p2LL) / 1000;
        String str = lat1 + "," + lon1 + "," + lat2 + "," + lon2;
        Log.e("distance", str);
        return (double) Math.round(distance * 100) / 100;
    }

    private void upLoadUserLog(Map<String, String> params) {
        RequestManager.getInstance()
                .mServiceStore
                .uplog(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {

                    }

                    @Override
                    public void onError(String msg) {

                    }
                }));

    }

    private void getLocation(final String id, final GoodsEntity entity, final String shipmentCode, final String enterpriseCode) {
        String reg = entity.getReq_length();
        Map<String, String> map = new HashMap<>();
        map.put("rowid", id);
        if (TextUtils.isEmpty(reg)) {
            map.put("type", "0");
        } else {
            if ("4.2米".equals(reg)) {
                LocationEntity locationEntity = dao.findLocInfoById(id);
                Log.e("location", "location===" + locationEntity.getLocation().toString());
                map.put("type", "1");
                if (locationEntity != null) {
                    map.put("location", locationEntity.getLocation().toString());
                } else {
                    map.put("location", "");
                }
            } else {
                map.put("type", "0");
            }
        }
        RequestManager.getInstance()
                .mServiceStore
                .getLocation(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        Log.e("getLocation", "str===" + str);
                        try {
                            JSONObject object = new JSONObject(str);
                            boolean isSuccess = object.getBoolean("success");
                            if (isSuccess) {
                                if (dao != null) {
                                    dao.delLocInfo(id);
                                }
                                String successMsg = object.getString("msg");
                                Toast.makeText(activity, successMsg, Toast.LENGTH_SHORT).show();
                            } else {
                                /*String errorMsg = object.getString("msg");
                                Toast.makeText(activity, errorMsg, Toast.LENGTH_SHORT).show();*/
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
                        Log.e("getLocation onError", "str===" + msg);
                    }
                }));
    }

    @Override
    public void onDestroy() {
        if (mLocClient != null) {
            mLocClient.stop();
        }
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }

    public class MyLocationListenner implements BDLocationListener {
        private String rid;

        public void setRid(String rid) {
            this.rid = rid;
        }

        @Override
        public void onReceiveLocation(BDLocation bdLocation) {
            if (bdLocation == null) {
                return;
            }
            Log.e("bdLocation", "bdLocation recived");
            final MainActivity activity = mActivityReference.get();
            if (activity == null) {
                return;
            }

            if (dao != null && !TextUtils.isEmpty(rid)) {
                String loc = bdLocation.getAddrStr();
                String lat = bdLocation.getLatitude() + "";
                String lon = bdLocation.getLongitude() + "";
                String time = changeTtime(bdLocation.getTime());
                if (dao.findLocInfoIsExist(rid)) {
                    //存在更新
                    LocationEntity entity = dao.findLocInfoById(rid);
                    String str = entity.getLocation();
                    try {
                        if (!TextUtils.isEmpty(str)) {
                            JSONArray array = new JSONArray(str);
                            Gson gson = new Gson();
                            LocEntity locEntity = new LocEntity();
                            locEntity.setTime(time);
                            locEntity.setLongitude(lon);
                            locEntity.setLatitude(lat);
                            locEntity.setLocation(loc);
                            entity.setRowid(rid);
                            JSONObject object = new JSONObject(gson.toJson(locEntity));
                            array.put(object);
                            entity.setLocation(array.toString());
                            dao.updateLocInfo(entity, rid);
                            Log.e("updateLocInfo", "updateLocInfo===" + entity.getLocation());
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                } else {
                    //不存在添加
                    Gson gson = new Gson();
                    LocEntity locEntity = new LocEntity();
                    locEntity.setTime(time);
                    locEntity.setLongitude(lon);
                    locEntity.setLatitude(lat);
                    locEntity.setLocation(loc);
                    LocationEntity entity = new LocationEntity();
                    JSONArray array = new JSONArray();
                    entity.setRowid(rid);
                    try {
                        JSONObject object = new JSONObject(gson.toJson(locEntity));
                        array.put(object);
                        entity.setLocation(array.toString());
                        dao.addLocInfo(entity);
                        Log.e("addLocInfo", "addLocInfo===" + entity.getLocation());
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void getShipmentStatus(String shipmentCode, String enterpriseCode, final OnShipmentStatusListener shipmentStatusListener) {
        final MainActivity activity = mActivityReference.get();
        if (activity == null) {
            return;
        }
        MDPLocationCollectionManager.getShipmentStatus(activity, shipmentCode, enterpriseCode, new OnDownloadResultListener() {
            @Override
            public void onSuccess(Object o) {
                if (o instanceof ShipmentStatusEnum) {
                    ShipmentStatusEnum shipmentStatusEnum = (ShipmentStatusEnum) o;
                    if (shipmentStatusListener != null) {
                        shipmentStatusListener.onSuccess(shipmentStatusEnum);
                    }
                }
            }

            @Override
            public void onFailure(String s, String s1) {
                if (shipmentStatusListener != null) {
                    shipmentStatusListener.onError();
                }
            }
        });
    }

    private interface OnShipmentStatusListener {
        void onSuccess(ShipmentStatusEnum status);

        void onError();
    }

}
