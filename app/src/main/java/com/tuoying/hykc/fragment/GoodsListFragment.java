package com.tuoying.hykc.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citylist.Toast.ToastUtils;
import com.lljjcoder.style.citypickerview.CityPickerView;
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
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.RxBus;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.CarLengthPopWindow;
import com.tuoying.hykc.view.CarTypePopWindow;
import com.tuoying.hykc.view.ExitDialogFragment;
import com.tuoying.hykc.view.LoadingDialogFragment;
import com.tuoying.hykc.view.PayMoneyDialog;
import com.tuoying.swipelayout.OnLoadMoreListener;
import com.tuoying.swipelayout.OnRefreshListener;
import com.tuoying.swipelayout.SwipeToLoadLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

/**
 * Created by Administrator on 2018/3/26.
 */

public class GoodsListFragment extends BaseFragment implements View.OnClickListener, OnRefreshListener, OnLoadMoreListener,GoodsListAdapter.OnItemButtonClickListener {
    WeakReference<MainActivity> mActivityReference;
    CityPickerView mCityPickerView = new CityPickerView();
    private RelativeLayout mLayoutCity;
    private RelativeLayout mLayoutArea;
    private RelativeLayout mLayoutLength;
    private RelativeLayout mLayoutType;
    private TextView mTextCity;
    private TextView mTextArea;
    private TextView mTextLength;
    private TextView mTextType;
    private ImageView mImgCity;
    private ImageView mImgArea;
    private ImageView mImgLength;
    private ImageView mImgType;
    private LinearLayout layout_main;
    private String sCity;
    private String eCity;
    private String sProvince;
    private String eProvince;
    private String sDistrict;
    private String eDistrict;
    private boolean isStart = false;
    private boolean isEnd = false;
    private CarLengthPopWindow carLengthPopWindow;
    private CarTypePopWindow carTypePopWindow;
    private StringBuffer sbf = new StringBuffer();
    private String carLength;
    private String carType;
    private String id;
    private DBDao dao;
    private User user;
    private View rootView;
    private RelativeLayout mLayoutLoading;
    private RelativeLayout mLayoutNoMsg;
    private ListView mListView;
    private View header;
    private View footer;
    private LayoutInflater mInflater;
    private SwipeToLoadLayout mSwipeToLoadLayout;
    private List<GoodsEntity> list=new ArrayList<>();
    private GoodsListAdapter adapter;
    private boolean isRefrush=false;

    public static GoodsListFragment getInstance() {
        return new GoodsListFragment();
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
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.layout_goods_list, null);
        }
        return rootView;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }

    @Override
    public void init(View v) {
        dao = new DBDaoImpl(getActivity());
        id = SharePreferenceUtil.getInstance(getActivity()).getUserId();
        if (!TextUtils.isEmpty(id)) {
            user = dao.findUserInfoById(id);
        }
        initViews(v);
        initCityPicker();
        initEvent();

    }

    private void initCityPicker() {
        mCityPickerView.init(mActivityReference.get());
        CityConfig cityConfig = new CityConfig.Builder().title("选择城市")
                .title("选择城市")//标题
                .confirTextColor("#33b5e5")//确认按钮文字颜色
                .provinceCyclic(false)//省份滚轮是否可以循环滚动
                .cityCyclic(false)//城市滚轮是否可以循环滚动
                .districtCyclic(false)//区县滚轮是否循环滚动
                .province("河南省")//默认显示的省份
                .city("郑州市")//默认显示省份下面的城市
                .district("金水区")//默认显示省市下面的区县数据
                .build();

        mCityPickerView.setConfig(cityConfig);
        mCityPickerView.setOnCityItemClickListener(new OnCityItemClickListener() {
            @Override
            public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                if (province != null) {
                    if (isStart) {
                        sProvince = province.getName();
                    }
                    if (isEnd) {
                        eProvince = province.getName();
                    }
                }

                if (city != null) {
                    if (isStart) {
                        sCity = city.getName();

                    }
                    if (isEnd) {

                        eCity = city.getName();
                    }
                }

                if (district != null) {
                    if (isStart) {
                        sDistrict = district.getName();
                        mTextCity.setText(sDistrict);
                        setImgDown(mImgCity);
                    }
                    if (isEnd) {
                        eDistrict = district.getName();
                        mTextArea.setText(eDistrict);
                        setImgDown(mImgArea);
                    }
                }
                isEnd = false;
                isStart = false;
                query_sources();
            }

            @Override
            public void onCancel() {
                if (isStart) {
                    setImgDown(mImgCity);
                }
                if (isEnd) {
                    setImgDown(mImgArea);
                }
            }

        });


    }

    private void initViews(View v) {
        layout_main = v.findViewById(R.id.layout_main);
        mLayoutCity = v.findViewById(R.id.layout_city);
        mLayoutArea = v.findViewById(R.id.layout_area);
        mLayoutLength = v.findViewById(R.id.layout_length);
        mLayoutType = v.findViewById(R.id.layout_type);

        mTextCity = v.findViewById(R.id.tv_city);
        mTextArea = v.findViewById(R.id.tv_area);
        mTextLength = v.findViewById(R.id.tv_length);
        mTextType = v.findViewById(R.id.tv_type);

        mImgCity = v.findViewById(R.id.img_city);
        mImgArea = v.findViewById(R.id.img_area);
        mImgLength = v.findViewById(R.id.img_length);
        mImgType = v.findViewById(R.id.img_type);
        mListView = (ListView) v.findViewById(R.id.swipe_target);
        mLayoutLoading = v.findViewById(R.id.layout_loading);
        mLayoutNoMsg = v.findViewById(R.id.layout_nomsg);
        mLayoutLoading.setVisibility(View.GONE);
        mLayoutNoMsg.setVisibility(View.VISIBLE);
        mInflater = LayoutInflater.from(getContext());
        mSwipeToLoadLayout = (SwipeToLoadLayout) v.findViewById(R.id.swipeToLoadLayout);
        footer = mInflater.inflate(R.layout.layout_classic_footer, mSwipeToLoadLayout, false);
        header = mInflater.inflate(R.layout.layout_twitter_header, mSwipeToLoadLayout, false);
        mSwipeToLoadLayout.setSwipeStyle(SwipeToLoadLayout.STYLE.CLASSIC);
        mSwipeToLoadLayout.setLoadMoreFooterView(footer);
        mSwipeToLoadLayout.setRefreshHeaderView(header);
        mSwipeToLoadLayout.setOnRefreshListener(this);
        mSwipeToLoadLayout.setOnLoadMoreListener(this);
    }

    private void initEvent() {

        mLayoutCity.setOnClickListener(this);
        mLayoutArea.setOnClickListener(this);
        mLayoutLength.setOnClickListener(this);
        mLayoutType.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.layout_city:
                isStart = true;
                setImgUp(mImgCity);
                mCityPickerView.showCityPicker();
                break;
            case R.id.layout_area:
                if (TextUtils.isEmpty(sCity) || TextUtils.isEmpty(sDistrict)) {
                    confirmFind("请先选择起始地！");
                    return;
                }
                isEnd = true;
                setImgUp(mImgArea);
                mCityPickerView.showCityPicker();
                break;
            case R.id.layout_length:
                if (TextUtils.isEmpty(eCity) || TextUtils.isEmpty(eDistrict)) {
                    confirmFind("请先选择目的地！");
                    return;
                }
                setImgUp(mImgLength);
                carLengthPopWindow = new CarLengthPopWindow(getActivity(), layout_main);
                carLengthPopWindow.setOnSelectListener(new CarLengthPopWindow.OnSelectListener() {
                    @Override
                    public void ontSelect(String select) {
                       // carLength = select;
                        mTextLength.setText(select);
                        setImgDown(mImgLength);
                        //query_sources();

                    }
                });
                carLengthPopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        setImgDown(mImgLength);
                    }
                });
                break;
            case R.id.layout_type:
                if (TextUtils.isEmpty(eCity) || TextUtils.isEmpty(eDistrict)) {
                    confirmFind("请先选择目的地！");
                    return;
                }
                setImgUp(mImgType);
                carTypePopWindow = new CarTypePopWindow(getActivity(), layout_main);
                carTypePopWindow.setOnSelectListener(new CarTypePopWindow.OnSelectListener() {
                    @Override
                    public void ontSelect(String select) {
                        //carType = select;
                        mTextType.setText(select);
                        setImgDown(mImgType);
                        //query_sources();

                    }
                });
                carTypePopWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        setImgDown(mImgType);
                    }
                });
                break;
        }
    }


    private void query_sources() {
        if (user == null) {
            confirmFind("用户信息为空，请重新登录！");
            return;
        }

        if(adapter!=null){
            list.clear();
            adapter.setList(list);
        }
        mLayoutLoading.setVisibility(View.VISIBLE);
        sbf.setLength(0);
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
        if (!TextUtils.isEmpty(carLength)) {
            sbf.append(".*?\\|");
            sbf.append(carLength);
            sbf.append(".*?");

        }
        if (!TextUtils.isEmpty(carType)) {
            sbf.append("\\|");
            sbf.append(carType);
            sbf.append(".*?");
        }
        String str=sbf.toString();
        if(!TextUtils.isEmpty(str)){
            if(str.endsWith("-")){
                str=str.substring(0,str.length()-2);
            }
        }
        Map<String, String> map = new HashMap<>();
        map.put("token", user.getToken());
        map.put("mobile", user.getUserId());
        map.put("app", Constants.AppId);
        map.put("line", str);
        map.put("excludes", "");

        Log.e("prams", str);
        RequestManager.getInstance()
                .mServiceStore
                .query_sources(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        final MainActivity activity = mActivityReference.get();
                        if (activity == null) {
                            return;
                        }
                        mLayoutLoading.setVisibility(View.GONE);
                        if(mSwipeToLoadLayout.isRefreshing()){
                            mSwipeToLoadLayout.setRefreshing(false);

                        }
                        if(mSwipeToLoadLayout.isLoadingMore()){
                            mSwipeToLoadLayout.setLoadingMore(false);
                        }
                        Log.e("query_sources onSuccess", msg);
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        if ("[]".equals(str)) {
                            mLayoutNoMsg.setVisibility(View.VISIBLE);
                        } else {
                            mLayoutNoMsg.setVisibility(View.GONE);
                            analysisJson(str);
                        }
                    }
                    @Override
                    public void onError(String msg) {
                        mLayoutLoading.setVisibility(View.GONE);
                        Log.e("query_sources onError", msg);
                    }
                }));
    }

    private void analysisJson(String str) {
        boolean isOfwlgsinfo=false;
        if(TextUtils.isEmpty(user.getOfwlgsinfo())){
            isOfwlgsinfo=true;
        }else {
            isOfwlgsinfo=false;
        }

        List<GoodsEntity> mList=new ArrayList<>();
        try {
            JSONArray array=new JSONArray(str);
            System.out.println("========>"+str);
            for (int i=0;i<array.length();i++){
                String val=array.getString(i);
                Log.e("val",val);
                GoodsEntity entity=new GoodsEntity();
                String vals[]=val.split("\\$");
                String rowid=vals[0];
                String strDetial=vals[1];
                entity.setRowid(rowid);
                String rowids[]=rowid.split("\\|");
                String time=rowids[0];
                entity.setTime(time);
                String address=rowids[2];
                String startAddress=address.split("\\-")[0];
                String endAddress=address.split("\\-")[1];
                entity.setEndAddress(endAddress);
                entity.setStartAddress(startAddress);
                String strs[]=strDetial.split("\\|");
                String sid=strs[2];
                entity.setSid(sid);
                String name=strs[3];
                entity.setName(name);
                String w=strs[4];
                entity.setWeight(w);
                String v=strs[5];
                entity.setVolume(v);
                String bz=strs[6];
                entity.setBz(bz);
                String xm=strs[7];
                entity.setHzxm(xm);
                String yf=strs[9];
                entity.setZyf(yf);
                String bl=strs[10];
                entity.setBl(bl);
                String form_lat=strs[11];
                entity.setLat_from(form_lat);
                String form_lon=strs[12];
                entity.setLon_from(form_lon);
                String to_lat=strs[13];
                entity.setLat_to(to_lat);
                String to_lon=strs[14];
                entity.setLon_to(to_lon);
                entity.setOfwlgsinfo(isOfwlgsinfo);
                mList.add(entity);
            }
            final MainActivity activity=mActivityReference.get();
            if(activity==null){
                return;
            }
            if(isRefrush){
                list.clear();
            }
            list.addAll(mList);
            if(adapter==null){
                adapter = new GoodsListAdapter(activity, list);
                mListView.setAdapter(adapter);
                adapter.setOnItemButtonClickListener(this);
                adapter.setOnItemClickListener(new GoodsListAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int pos, GoodsEntity entity) {
                        GoodsEntity e= (GoodsEntity) adapter.getItem(pos);
                        Intent intent=new Intent(getActivity(), GoodsListDetailActivity.class);
                        intent.putExtra("type",200);
                        intent.putExtra("entity",e);
                        startActivity(intent);
                        getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                    }
                });
            }else {
                adapter.notifyDataSetChanged();
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }finally {
            isRefrush=false;

        }

    }

    private void confirmFind(String s) {
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(s);
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
        dialog.show(getChildFragmentManager(), "TipsDialog");
    }


    @Override
    public void onLoadMore() {
        isRefrush = true;
        query_sources();
    }

    @Override
    public void onRefresh() {
        isRefrush=true;
        query_sources();

    }

    private void setImgUp(ImageView img) {
        img.setImageResource(R.drawable.ic_action_up);

    }

    private void setImgDown(ImageView img) {
        img.setImageResource(R.drawable.ic_action_down);
    }

    @Override
    public void onButtonClick(View view, int index, GoodsEntity entity, int type) {
        if(type==1){
            if(user!=null){
                String rzType = user.getRz();
                if ("0".equals(rzType)) {
                    confirmRZTips("用户认证未通过！");
                } else if ("1".equals(rzType)) {
                    doOrder(entity);
                } else if (TextUtils.isEmpty(rzType)) {
                    confirmRZTips("用户未认证！");
                }

            }
        }
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
        dialog.show(getChildFragmentManager(), "confirmRZTips");

    }

    private void doOrder(final GoodsEntity entity) {
        final LoadingDialogFragment dialogFragment=LoadingDialogFragment.getInstance();
        dialogFragment.show(getChildFragmentManager(),"orderLoading");
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
                        final MainActivity activity=mActivityReference.get();
                        if(activity==null){
                            return;
                        }
                        Log.e("doOrder","==="+msg);
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject object=new JSONObject(str);
                            if(object.getBoolean("success")){
                                String success=object.getString("message");
                                Toast.makeText(activity, success, Toast.LENGTH_SHORT).show();
                                shoPayMoneyViews(entity.getZyf(),Double.parseDouble(entity.getBl()),entity);
                            }else {
                                String error=object.getString("message");
                                Toast.makeText(activity, error, Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            dialogFragment.dismiss();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        final MainActivity activity=mActivityReference.get();
                        if(activity==null){
                            return;
                        }
                        dialogFragment.dismiss();
                        Log.e("query_sources onError", msg);
                    }
                }));
    }

    private void shoPayMoneyViews(String price,double bl,final GoodsEntity entity) {

        Log.e("doPayMoney==",entity.toString());
        System.out.println(entity.toString());
        final PayMoneyDialog dialog=PayMoneyDialog.getInstance(price,bl,entity.getSid(),user.getUserName(),entity);
        dialog.show(getChildFragmentManager(),"paymoneydialog");
        dialog.setOnOrderListener(new PayMoneyDialog.OnOrderListener() {
            @Override
            public void onOrder(String psd,String money) {
                dialog.dismiss();
                if(user!=null){
                    doPayMoney(psd,money,entity);
                }
            }
        });
        dialog.setOnDismissListener(new PayMoneyDialog.onDismissListener() {
            @Override
            public void onDismiss() {
                dialog.dismiss();
                mSwipeToLoadLayout.setRefreshing(true);
                RxBus.getInstance().send(new EventEntity("刷新","刷新"));
            }
        });
    }

    private void doPayMoney(final String psd,final String money,final GoodsEntity entity){
      final   LoadingDialogFragment loadingDialogFragment=LoadingDialogFragment.getInstance();
        loadingDialogFragment.show(getChildFragmentManager(),"paydialog");
        Map<String,String> map=new HashMap<>();
        map.put("mobile",user.getUserId());
        map.put("app",Constants.AppId);
        map.put("token",user.getToken());
        map.put("amount",money);
        map.put("pwd",psd);
        map.put("req_type","DEPS");
        map.put("type_name","运输保证金支付");
        JSONObject object=new JSONObject();
        try {
            object.put("rowid",entity.getRowid());
            object.put("flag","driver_deps");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        map.put("ext",object.toString());
        RequestManager.getInstance()
                .mServiceStore
                .create_request(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        final MainActivity activity=mActivityReference.get();
                        if(activity==null){
                            return;
                        }
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject jsonObject=new JSONObject(str);
                            if(jsonObject.getBoolean("success")){
                                String orderno=jsonObject.getString("order_no");
                                String tokenid=jsonObject.getString("tokenid");
                                SharePreferenceUtil.getInstance(getActivity()).setPayPwd(psd);
                                submit_order(orderno,tokenid,entity.getRowid(),loadingDialogFragment);
                            }else {
                                String error=jsonObject.getString("message");
                                mSwipeToLoadLayout.setRefreshing(true);
                                RxBus.getInstance().send(new EventEntity("刷新","刷新"));
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
                        final MainActivity activity=mActivityReference.get();
                        if(activity==null){
                            return;
                        }
                        mSwipeToLoadLayout.setRefreshing(true);
                        RxBus.getInstance().send(new EventEntity("刷新","刷新"));
                        loadingDialogFragment.dismiss();
                        Toast.makeText(activity, "支付失败！", Toast.LENGTH_SHORT).show();
                        Log.e("create_request onError", msg);
                    }
                }));
    }


    private void submit_order(String orderno, String tokenid, final String rowid, final  LoadingDialogFragment loadingDialogFragment){

        Map<String,String> map=new HashMap<>();
        map.put("mobile",user.getUserId());
        map.put("app",Constants.AppId);
        map.put("token",user.getToken());
        map.put("tokenid",tokenid);
        map.put("order_no",orderno);
        map.put("req_type","DEPS");
        map.put("rowid",rowid);
        RequestManager.getInstance()
                .mServiceStore
                .submit_order(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        loadingDialogFragment.dismiss();
                        final MainActivity activity=mActivityReference.get();
                        if(activity==null){
                            return;
                        }
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject jsonObject=new JSONObject(str);
                            if(jsonObject.getBoolean("success")){
                                //doAnsycWayBill(rowid);
                                Toast.makeText(activity, "支付成功！", Toast.LENGTH_SHORT).show();
                                mSwipeToLoadLayout.setRefreshing(true);
                                RxBus.getInstance().send(new EventEntity("刷新","刷新"));

                            }else {
                                String error=jsonObject.getString("message");
                                mSwipeToLoadLayout.setRefreshing(true);
                                RxBus.getInstance().send(new EventEntity("刷新","刷新"));
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
                        final MainActivity activity=mActivityReference.get();
                        if(activity==null){
                            return;
                        }
                        mSwipeToLoadLayout.setRefreshing(true);
                        RxBus.getInstance().send(new EventEntity("刷新","刷新"));
                        loadingDialogFragment.dismiss();
                        Toast.makeText(activity, "支付失败！", Toast.LENGTH_SHORT).show();
                        Log.e("create_request onError", msg);
                    }
                }));
    }

   /* private void  doAnsycWayBill(String rowid){
        Map<String,String> map=new HashMap<>();
        map.put("rowid",rowid);
        RequestManager.getInstance()
                .mServiceStore
                .alctyydtb(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        final MainActivity activity=mActivityReference.get();
                        if(activity==null){
                            return;
                        }
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        Log.e("doAnsycWayBill","str==="+str);
                    }

                    @Override
                    public void onError(String msg) {
                        final MainActivity activity=mActivityReference.get();
                        if(activity==null){
                            return;
                        }
                        Log.e("doAnsycWayBill onError","str==="+msg);
                    }
                }));



    }

*/


}
