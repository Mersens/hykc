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

import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.GoodsListDetailActivity;
import com.tuoying.hykc.activity.MainActivity;
import com.tuoying.hykc.adapter.DPJAdapter;
import com.tuoying.hykc.adapter.WWCAdapter;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.EventEntity;
import com.tuoying.hykc.entity.GoodsEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.RxBus;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.EvaluateDialogFragment;
import com.tuoying.hykc.view.LoadingDialogFragment;
import com.tuoying.swipelayout.OnLoadMoreListener;
import com.tuoying.swipelayout.OnRefreshListener;
import com.tuoying.swipelayout.SwipeToLoadLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DPJFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener,DPJAdapter.OnItemButtonClickListener {
    WeakReference<MainActivity> mActivityReference;
    User user;
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
    private boolean isRefrush = false;
    private DPJAdapter adapter;
    private CompositeDisposable mCompositeDisposable;

    public static DPJFragment getInstance(){
        return new DPJFragment();
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
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.layout_dpj, null);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void init(View view) {
        dao=new DBDaoImpl(getActivity());
        userid= SharePreferenceUtil.getInstance(getActivity()).getUserId();
        if(!TextUtils.isEmpty(userid)){
            user=dao.findUserInfoById(userid);
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
        initDatas();
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
                            if (type.equals("dpj")) {
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

    @Override
    public void onDestroy() {
        mCompositeDisposable.clear();
        super.onDestroy();
    }

    private void initDatas() {
        if (!TextUtils.isEmpty(userid)) {
            User user = dao.findUserInfoById(userid);
            if (user != null) {
                getGoodsInfo(user);
            }

        }
    }

    private void getGoodsInfo(User user) {
        if (!isRefrush) {
            mLayoutLoading.setVisibility(View.VISIBLE);
        }
        Map<String,String> map=new HashMap<>();
        map.put("mobile",user.getUserId());
        map.put("token",user.getToken());
        map.put("app", Constants.AppId);
        map.put("type","dpj");
        RequestManager.getInstance()
                .mServiceStore
                .findOrderInfo(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        mLayoutLoading.setVisibility(View.GONE);
                        if(TextUtils.isEmpty(msg)){
                            mLayoutNoMsg.setVisibility(View.VISIBLE);
                            return;
                        }
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        Log.e(" dpj onSuccess", "====" + msg);
                        JSONArray array = null;
                        try {
                            array = new JSONArray(str);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        if(array.length()==0){
                            mLayoutNoMsg.setVisibility(View.VISIBLE);
                            return;
                        } else {
                            mLayoutNoMsg.setVisibility(View.GONE);
                        }
                        try {
                            JSONArray array1=new JSONArray(str);
                            analysisJson(array1);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                            mLayoutLoading.setVisibility(View.GONE);
                        }
                    }
                    @Override
                    public void onError(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        Log.e("onError", "====" + msg);
                        Toast.makeText(activity, "失败！", Toast.LENGTH_SHORT).show();
                        mLayoutLoading.setVisibility(View.GONE);
                    }
                }));


    }

    private void analysisJson(JSONArray array) {
        boolean isOfwlgsinfo=false;
        if(TextUtils.isEmpty(user.getOfwlgsinfo())){
            isOfwlgsinfo=true;
        }else {
            isOfwlgsinfo=false;
        }
        List<GoodsEntity> mList = new ArrayList<>();
        try {
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
                if(object.has("data:bz")){
                    String bz = object.getString("data:bz");
                    entity.setBz(bz);
                }
                if(object.has("data:task_id")){
                    String task_id=object.getString("data:task_id");
                    entity.setTask_id(task_id);

                }
                if(object.has("data:driverPrice")){
                    String driverPrice = object.getString("data:driverPrice");
                    entity.setDriverPrice(driverPrice);
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
                if(object.has("data:driver_appraise")){
                    entity.setDriver_appraise(object.getString("data:driver_appraise"));
                }
                if(object.has("data:driver_score")){
                    entity.setDriver_score(object.getString("data:driver_score"));

                }
                if(object.has("data:yd_1_time")){
                    entity.setJdTime(object.getString("data:yd_1_time"));

                }
                entity.setHuozhu(huozhu);
                entity.setOfwlgsinfo(isOfwlgsinfo);
                mList.add(entity);
            }
            final MainActivity activity = mActivityReference.get();
            if (activity == null) {
                return;
            }
            if (isRefrush) {
                list.clear();
            }
            list.addAll(mList);
            if (adapter == null) {
                adapter = new DPJAdapter(activity, list);
                mListView.setAdapter(adapter);
                adapter.setOnItemButtonClickListener(this);
                adapter.setOnItemClickListener(new DPJAdapter.OnItemClickListener() {
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
        mSwipeToLoadLayout.setLoadingMore(false);
        initDatas();
    }

    @Override
    public void onRefresh() {
        isRefrush = true;
        mSwipeToLoadLayout.setRefreshing(false);
        initDatas();
    }

    @Override
    public void onButtonClick(View view, int index, final GoodsEntity entity, int type) {
        final EvaluateDialogFragment evaluateDialogFragment = EvaluateDialogFragment.getInstance();
        evaluateDialogFragment.show(getChildFragmentManager(), "EvaluateDialogFragment");
        evaluateDialogFragment.setOnReasonDialogListener(new EvaluateDialogFragment.OnReasonDialogListener() {
            @Override
            public void onCloseListener() {
                evaluateDialogFragment.dismiss();
            }

            @Override
            public void onComplateListener(String t, String reasons) {
                upLoadEvaluateInfo(evaluateDialogFragment, entity,t,reasons);

            }
        });
    }

    private void upLoadEvaluateInfo(final EvaluateDialogFragment evaluateDialogFragment, GoodsEntity entity, String t, String reasons) {
        final LoadingDialogFragment evaluateLoading = LoadingDialogFragment.getInstance();
        evaluateLoading.show(getChildFragmentManager(), "evaluateLoading");
        User user = dao.findUserInfoById(userid);
        JSONObject object=new JSONObject();
        try {
            object.put("driver_score",t);
            object.put("driver_appraise",reasons);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Map<String, String> map = new HashMap<>();
        map.put("mobile", user.getUserId());
        map.put("app", Constants.AppId);
        map.put("token", user.getToken());
        map.put("rowid", entity.getRowid());
        map.put("content", object.toString());

        RequestManager.getInstance()
                .mServiceStore
                .save_appraise(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        if(evaluateLoading!=null){
                            evaluateLoading.dismiss();
                        }
                        if(evaluateDialogFragment!=null){
                            evaluateDialogFragment.dismiss();
                        }
                        Log.e("Evaluate onSuccess", "====" + msg);
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject object = new JSONObject(str);
                            if (object.getBoolean("success")) {
                                Toast.makeText(getActivity(), "评价提交成功！", Toast.LENGTH_SHORT).show();
                                mSwipeToLoadLayout.setRefreshing(true);
                            } else {
                                String errorMsg = object.getString("message");
                                Toast.makeText(getActivity(), errorMsg, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {

                        }
                    }

                    @Override
                    public void onError(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        if(evaluateLoading!=null){
                            evaluateLoading.dismiss();

                        }
                        if(evaluateDialogFragment!=null){
                            evaluateDialogFragment.dismiss();

                        }
                        Log.e("onError", "====" + msg);
                        Toast.makeText(activity, "提交失败！", Toast.LENGTH_SHORT).show();

                    }
                }));

    }
}
