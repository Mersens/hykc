package com.tuoying.hykc.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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
import com.alct.mdp.callback.OnResultListener;
import com.alct.mdp.model.Goods;
import com.alct.mdp.model.Location;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.model.inner.GeoPoint;
import com.baidu.mapapi.utils.DistanceUtil;
import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.BaseActivity;
import com.tuoying.hykc.activity.GoodsListDetailActivity;
import com.tuoying.hykc.activity.LoginActivity;
import com.tuoying.hykc.activity.MainActivity;
import com.tuoying.hykc.activity.OrderActivity;
import com.tuoying.hykc.activity.UpLoadImgActivity;
import com.tuoying.hykc.adapter.GoodsListAdapter;
import com.tuoying.hykc.adapter.WWCAdapter;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.EventEntity;
import com.tuoying.hykc.entity.GoodsEntity;
import com.tuoying.hykc.entity.User;
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

import java.lang.ref.WeakReference;
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

public class WWCFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener, WWCAdapter.OnItemButtonClickListener {
    final LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.getInstance();
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
    private boolean onPouse=false;

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
        Disposable d = RxBus.getInstance().toObservable().toObservable().subscribeOn(Schedulers.io())
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
                                    initDatas();
                                }
                            }
                        }
                    }
                });
        //subscription交给compositeSubscription进行管理，防止内存溢出
        mCompositeDisposable.add(d);

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
        onPouse=false;
        if(adapter!=null){
            list.clear();
            adapter.setList(list);
        }
        initDatas();
        super.onResume();

    }

    @Override
    public void onPause() {
        onPouse=true;
        isRefrush=false;
        super.onPause();

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

                        if(mSwipeToLoadLayout.isRefreshing()){

                            mSwipeToLoadLayout.setRefreshing(false);

                        }
                        if(mSwipeToLoadLayout.isLoadingMore()){
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

    private void analysisJson(String msgStr) {
        boolean isOfwlgsinfo = false;
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
                if (object.has("data:bz")) {
                    String bz = object.getString("data:bz");
                    entity.setBz(bz);
                }
                String hzxm = object.getString("data:fhr");
                entity.setHzxm(hzxm);
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
                if(object.has("data:pdwlgs")){
                    entity.setPdwlgs(object.getString("data:pdwlgs"));
                }else {
                    entity.setPdwlgs("-1");
                }
                mList.add(entity);
            }
            final MainActivity activity = mActivityReference.get();
            if (activity == null) {
                return;
            }
            if(onPouse){
                return;
            }
            if (isRefrush) {
                list.clear();
                if(adapter!=null){
                    adapter.setList(list);
                    adapter.notifyDataSetChanged();

                }
            }
            list.addAll(mList);
            if(adapter!=null){
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
                doPs(entity);
                break;
            case 3:
                calculateTime(entity);

                break;
            case 12:
                final ExitDialogFragment dialog = ExitDialogFragment.getInstance("确定取消运单？");
                dialog.show(getChildFragmentManager(), "confirmCancelTips");
                dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
                    @Override
                    public void onClickCancel() {
                        dialog.dismiss();
                    }

                    @Override
                    public void onClickOk() {
                        dialog.dismissAllowingStateLoss();
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

    private void calculateTime(final GoodsEntity entity){
        double d= getPointsDistances(entity.getLat_from(),entity.getLon_from(),entity.getLat_to(),entity.getLon_to());
        Log.e("getPointsDistances","getPointsDistances==="+d);
        int needTime=(int)((d/10)*60);//实际运输需要的大致时间(分钟)
        Log.e("getJdTime1==",entity.getJdTime()+"");
        if (!TextUtils.isEmpty(entity.getJdTime())) {
            int i = (int) DateUtils.formatTime(entity.getJdTime());
            Log.e("getJdTime2==",i+"");
            if (i < needTime) {
                //confirmTips("配送时间过短！");
                final ValidationCodeFragment validationCodeFragment=ValidationCodeFragment.getInstance("配送时间过短,确定送达？");
                validationCodeFragment.showF(getChildFragmentManager(),"timelow");
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
            }else {
                final ValidationCodeFragment validationCodeFragment=ValidationCodeFragment.getInstance("确定进行送达操作？");
                validationCodeFragment.showF(getChildFragmentManager(),"timehight");
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
            }
        }else {
            confirmTips("配送时间为空！");
        }

    }


    private void doPz(GoodsEntity entity) {
        final ValidationCodeFragment validationCodeFragment=ValidationCodeFragment.getInstance("");
        validationCodeFragment.showF(getChildFragmentManager(),"hha");
        validationCodeFragment.setOnValidationComplListener(new ValidationCodeFragment.OnValidationComplListener() {
            @Override
            public void onClickCancel() {
                validationCodeFragment.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                validationCodeFragment.dismissAllowingStateLoss();
            }
        });

/*        Intent intent = new Intent(getActivity(), UpLoadImgActivity.class);
        intent.putExtra("entity", entity);
        startActivity(intent);
        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);*/
    }

    private void doTB(GoodsEntity entity) {
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
        dialog.show(getChildFragmentManager(), "tipsDialog");

    }

    private void doSd(final GoodsEntity entity) {

        final MainActivity activity = mActivityReference.get();
        if (activity == null) {
            return;
        }

        loadingDialogFragment.showF(getChildFragmentManager(),"sdDialogFragment");
        String sdmsg = SharePreferenceUtil.getInstance(activity).getSDMsg();
        if ("0".equals(sdmsg)) {
            doUnLoad(entity, entity.getName(), entity.getRowid(), entity.getAlctCode());
        }
        final long sdl1=DateUtils.getTimeMillis();
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
                        if(loadingDialogFragment!=null && loadingDialogFragment.isAdded()){
                           loadingDialogFragment.dismissAllowingStateLoss();
                        }

                        long sdl2=DateUtils.getTimeMillis();
                        long l=DateUtils.getBetwnTime(sdl2,sdl1);
                        Log.e("sd_time","sd_time=="+l);

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
                                if(loadingDialogFragment!=null && loadingDialogFragment.isAdded()){
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
                        if(loadingDialogFragment!=null && loadingDialogFragment.isAdded()){
                            loadingDialogFragment.dismissAllowingStateLoss();
                        }
                        Toast.makeText(activity, "送达失败！", Toast.LENGTH_SHORT).show();
                        Log.e("create_request onError", msg);
                    }
                }));


    }

    private void doUnLoad(final GoodsEntity entity, final String hwmc, final String shipmentCode, final String enterpriseCode) {
       allon1=DateUtils.getTimeMillis();
        MDPLocationCollectionManager.unload(getActivity(), shipmentCode, enterpriseCode, getLocation(entity, false), new OnResultListener() {
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

        MDPLocationCollectionManager.sign(getActivity(), shipmentCode, enterpriseCode, getLocation(entity, false), getGoodsList(hwmc)
                , new OnResultListener() {
                    @Override
                    public void onSuccess() {
                        Log.e("doSign onSuccess", "onSuccess----");
                        pod(entity, shipmentCode, enterpriseCode);
                        getLocation(entity.getRowid(), entity, shipmentCode, enterpriseCode);

                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        Log.e("doSign", "onFailure----" + s + s1);
                        doUploadErrorMsg(shipmentCode, s, s1);
                        getLocation(entity.getRowid(), entity, shipmentCode, enterpriseCode);

                    }
                });

    }

    private void pod(final GoodsEntity entity, final String shipmentCode, final String enterpriseCode) {
        MDPLocationCollectionManager.pod(getActivity(), shipmentCode, enterpriseCode, getLocation(entity, false), new OnResultListener() {
            @Override
            public void onSuccess() {
                allon2=DateUtils.getTimeMillis();
                long l=DateUtils.getBetwnTime(allon2,allon1);
                Log.e("sdal_time","sdal_time=="+l);
                Log.e("pod onSuccess", "onSuccess----");
                Intent intent = new Intent(getActivity(), UpLoadImgActivity.class);
                intent.putExtra("entity", entity);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
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


    private void doPs(final GoodsEntity entity) {
        String sdmsg = SharePreferenceUtil.getInstance(getActivity()).getSDMsg();
        // dopickup(entity.getMoxid(), true+"");
        if ("0".equals(sdmsg)) {
            doCheckNfc(entity.getRowid(), entity.getAlctCode(), entity);
        }
        final long startMillis=DateUtils.getTimeMillis();
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
                        long endMillis=DateUtils.getTimeMillis();
                        long l=DateUtils.getBetwnTime(endMillis,startMillis);
                        Log.e("PS_TimeMillis==","psTimeMillis=="+l);
                        Log.e("mox===", entity.toString());
                        if(loadingDialogFragment!=null){
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
                                if(loadingDialogFragment!=null){
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

    private void doCheckNfc(final String shipmentCode, final String enterpriseCode, final GoodsEntity entity) {
        //doPickUp(shipmentCode, enterpriseCode,entity);
        allon1=DateUtils.getTimeMillis();
        Log.e("entity111==", "entity1111==" + entity.toString());
        MDPLocationCollectionManager.checkNfc(getActivity(), shipmentCode, enterpriseCode, "", new OnResultListener() {
            @Override
            public void onSuccess() {
                Log.e("doCheckNfc onSuccess", "onSuccess----");
                doPickUp(shipmentCode, enterpriseCode, entity);
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("doCheckNfc", "onFailure----" + s + s1);
                doUploadErrorMsg(shipmentCode, s, s1);
            }
        });

    }

    private void doPickUp(final String shipmentCode, final String enterpriseCode, final GoodsEntity entity) {
        final MainActivity activity = mActivityReference.get();
        if (activity == null) {
            return;
        }
        //需要获取坐标

        MDPLocationCollectionManager.pickup(getActivity(), shipmentCode, enterpriseCode, getLocation(entity, true), new OnResultListener() {
            @Override
            public void onSuccess() {
                allon2=DateUtils.getTimeMillis();
                long l=DateUtils.getBetwnTime(allon2,allon1);
                Log.e("PSal_TimeMillis==","psalTimeMillis=="+l);
                Log.e("doPickUp", "onSuccess----");
                isPickUp = true;
                dopickup(entity.getRowid(), true + "");
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("doPickUp", "onFailure----" + s + s1);
                doUploadErrorMsg(shipmentCode, s, s1);
                isPickUp = true;
                dopickup(entity.getRowid(), false + "");
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

        Log.e("doPayMoney==",entity.toString());
        System.out.println(entity.toString());
        final PayMoneyDialog dialog = PayMoneyDialog.getInstance(price, bl, entity.getSid(), user.getUserName(),entity);
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

    private void uploadPayMoney(String psd, String money, final GoodsEntity entity) {
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

    private void dopickup(String id, String orderType) {
        Log.e("moxid==", "moxid==" + id);
        Map<String, String> map = new HashMap<>();
        map.put("rowid", id);
        map.put("orderType", orderType);
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

    private void getLocation(String id, final GoodsEntity entity, final String shipmentCode, final String enterpriseCode) {
        Map<String, String> map = new HashMap<>();
        map.put("rowid", id);
        map.put("type", "hykc");
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
                                String successMsg=object.getString("msg");
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


    private double  getPointsDistances(String lat1,String lon1,String lat2,String lon2){
        double distance=0;
        double dLat1=Double.parseDouble(lat1);
        double dLon1=Double.parseDouble(lon1);
        double dLat2=Double.parseDouble(lat2);
        double dLon2=Double.parseDouble(lon2);
        LatLng p1LL = new LatLng(dLat1, dLon1);
        LatLng p2LL = new LatLng(dLat2, dLon2);
        distance=DistanceUtil.getDistance(p1LL, p2LL)/1000;
        String str=lat1+","+lon1+","+lat2+","+lon2;
        Log.e("distance",str);
        return (double) Math.round(distance * 100) / 100;
    }


}
