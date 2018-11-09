package com.tuoying.hykc.activity;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alct.mdp.MDPLocationCollectionManager;
import com.alct.mdp.callback.OnDownloadResultListener;
import com.alct.mdp.callback.OnResultListener;
import com.alct.mdp.model.EnterpriseIdentity;
import com.alct.mdp.model.Invoice;
import com.alct.mdp.model.MultiIdentity;
import com.alct.mdp.response.GetInvoicesResponse;
import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.ForceUpdateListener;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.tuoying.hykc.R;
import com.tuoying.hykc.app.App;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.EventEntity;
import com.tuoying.hykc.entity.GoodsEntity;
import com.tuoying.hykc.entity.MsgEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.fragment.AboutFragment;
import com.tuoying.hykc.fragment.CommonFragment;
import com.tuoying.hykc.fragment.ContactFragment;
import com.tuoying.hykc.fragment.GoodsListFragment;
import com.tuoying.hykc.fragment.MapFragment;
import com.tuoying.hykc.fragment.MyMsgFragment;
import com.tuoying.hykc.fragment.MyWalletFragment;
import com.tuoying.hykc.fragment.MyWayBillFragment;
import com.tuoying.hykc.fragment.OthersFragment;
import com.tuoying.hykc.fragment.SettingFragment;
import com.tuoying.hykc.service.MQTTService;
import com.tuoying.hykc.utils.APKVersionCodeUtils;
import com.tuoying.hykc.utils.DateUtils;
import com.tuoying.hykc.utils.NotificationUtils;
import com.tuoying.hykc.utils.Questions;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.RxBus;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.ExitDialogFragment;
import com.tuoying.hykc.view.QuestionDialogFragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity
        implements View.OnClickListener {
    private final int SDK_PERMISSION_REQUEST = 127;
    private String permissionInfo;
    private static final int MY_WAYBILL = 0;//我的运单
    private static final int GOODS_MSG_LIST = 1; //货源列表
    private static final int OTHERS = 2; //其他
    private static final int GOODS_MSG = 3;//货源信息
    private Toolbar toolbar;
    private Fragment[] fragments;
    private int index;
    private int currentTabIndex;
    private boolean isMap = false;
    private DBDao dao;
    private String userid;
    private boolean optionMenuOn = true;  //标示是否要显示optionmenu
    private Menu aMenu;         //获取optionmenu
    private CompositeDisposable mCompositeDisposable;
    private Intent intentMqttService;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private String address;
    private DownloadBuilder builder;
    private int ConnectCount=0;
    private MyConn conn;
    private TextView mTextMsgNum;
    private int selectColor;
    private int unSelectColor;
    private RelativeLayout mLayoutHY;
    private RelativeLayout mLayoutYD;
    private RelativeLayout mLayoutOTHERS;
    private TextView mTextHY;
    private TextView mTextYD;
    private TextView mTextOTHERS;
    private ImageView mImgHY;
    private ImageView mImgYD;
    private ImageView mImgOTHERS;
    private FrameLayout mLayoutMsg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dao = new DBDaoImpl(this);
        getPersimmions();
        init();
    }

    @TargetApi(23)
    private void getPersimmions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            ArrayList<String> permissions = new ArrayList<String>();
            /***
             * 定位权限为必须权限，用户如果禁止，则每次进入都会申请
             */
            // 定位精确位置
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.ACCESS_COARSE_LOCATION);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            }
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
            /*
             * 读写权限和电话状态权限非必要权限(建议授予)只会申请一次，用户同意或者禁止，只会弹一次
             */
            // 读写权限
            if (addPermission(permissions, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                permissionInfo += "Manifest.permission.WRITE_EXTERNAL_STORAGE Deny \n";
            }
            // 读取电话状态权限
            if (addPermission(permissions, Manifest.permission.READ_PHONE_STATE)) {
                permissionInfo += "Manifest.permission.READ_PHONE_STATE Deny \n";
            }

            if (permissions.size() > 0) {
                requestPermissions(permissions.toArray(new String[permissions.size()]), SDK_PERMISSION_REQUEST);
            }
        }
    }

    @TargetApi(23)
    private boolean addPermission(ArrayList<String> permissionsList, String permission) {
        if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) { // 如果应用没有获得对应权限,则添加到列表中,准备批量申请
            if (shouldShowRequestPermissionRationale(permission)) {
                return true;
            } else {
                permissionsList.add(permission);
                return false;
            }

        } else {
            return true;
        }
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // TODO Auto-generated method stub
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_hy:
                index=GOODS_MSG_LIST;
                setTab(index);
                break;
            case R.id.layout_yd:
                index=MY_WAYBILL;
                setTab(index);
                break;
            case R.id.layout_others:
                index=OTHERS;
                setTab(index);
                break;
        }
        setToolbarTitle(index);
        addFragment();
    }

    private void setTab(int i) {
        resetTabs();
        switch (i) {
            case GOODS_MSG_LIST:
                mTextHY.setTextColor(selectColor);
                mImgHY.setImageResource(R.drawable.ic_hy_select);
                break;
            case MY_WAYBILL:
                mTextYD.setTextColor(selectColor);
                mImgYD.setImageResource(R.drawable.ic_yd_select);
                break;
            case OTHERS:
                mTextOTHERS.setTextColor(selectColor);
                mImgOTHERS.setImageResource(R.drawable.ic_others_select);
                break;
        }
    }

    private void resetTabs() {
        mTextHY.setTextColor(unSelectColor);
        mImgHY.setImageResource(R.drawable.ic_hy_normal);

        mTextYD.setTextColor(unSelectColor);
        mImgYD.setImageResource(R.drawable.ic_yd_normal);

        mTextOTHERS.setTextColor(unSelectColor);
        mImgOTHERS.setImageResource(R.drawable.ic_others_normal);

    }

    private class MyConn implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d("onServiceConnected", componentName.toString());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {

        }
    }
    @Override
    public void init() {
        selectColor = getResources().getColor(R.color.bottom_select_color);
        unSelectColor = getResources().getColor(R.color.bottom_normal_color);
        intentMqttService = new Intent(this, MQTTService.class);
        conn = new MyConn();
        bindService(intentMqttService, conn, BIND_AUTO_CREATE);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("我的运单");
        optionMenuOn = false;
        checkOptionMenu();
        mLayoutMsg = toolbar.findViewById(R.id.layout_msg);
        mTextMsgNum=findViewById(R.id.tv_msg_num);
        userid = SharePreferenceUtil.getInstance(this).getUserId();
        initViews();
        initFragment();
        initEvent();
        initDatas();
        checkVerson();
        initQuestion();
        mCompositeDisposable = new CompositeDisposable();
        //监听订阅事件
        Disposable d = RxBus.getInstance().toObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(Object o) throws Exception {
                        if (o instanceof EventEntity) {
                            EventEntity e = (EventEntity) o;
                            String type = e.type;
                            if (type.equals("heardview_rz")) {
                                initDatas();
                            } else if (type.equals("location")) {
                                String[] locations = e.value.split(",");
                                mCurrentLat = Double.parseDouble(locations[0]);
                                mCurrentLon = Double.parseDouble(locations[1]);
                                address = locations[2];
                            } else if (type.equals("skip")) {
                                index = MY_WAYBILL;
                                setTab(index);
                                setToolbarTitle(index);
                                addFragment();

                            } else if (type.equals("notice")) {
                                Log.e("notice msg", e.value);
                                String info = e.value.replaceAll("\r", "").replaceAll("\n", "");
                                MsgEntity entity=new MsgEntity();
                                entity.setTime(DateUtils.getStringNowTime());
                                entity.setMsg(info);
                                dao.addMsgInfo(entity);
                                Intent intent = new Intent(MainActivity.this, MyMsgActivity.class);
                                PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, -1, intent
                                        , PendingIntent.FLAG_UPDATE_CURRENT);
                                NotificationUtils notificationUtils = new NotificationUtils(MainActivity.this, pendingIntent);
                                notificationUtils.sendNotification("通知消息", info);

                            } else if (type.equals("waybill")) {
                                String info = e.value.replaceAll("\r", "").replaceAll("\n", "");
                                Log.e("waybill info","===="+info);
                                if (!TextUtils.isEmpty(info) && !"{}".equals(info)) {
                                    GoodsEntity entity=getGoodsEntityFromJson(info);
                                    Intent intent = new Intent(MainActivity.this, GoodsListDetailActivity.class);
                                    intent.putExtra("entity",entity);
                                    intent.putExtra("type",100);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, -1, intent
                                            , PendingIntent.FLAG_UPDATE_CURRENT);
                                    NotificationUtils notificationUtils = new NotificationUtils(MainActivity.this, pendingIntent);
                                    notificationUtils.sendNotification("通知消息", "运单信息");
                                }
                            }else if(type.equals("reconnect_timeout")){
                                Log.e("reconnect_timeout info","====reconnect_timeout");
                                confirmUserExit("MQTT连接服失败，请重新登录！");
                                ConnectCount=ConnectCount+1;
                                if(ConnectCount<2){

                                }
                            }
                        }
                    }
                });
        //subscription交给compositeSubscription进行管理，防止内存溢出
        mCompositeDisposable.add(d);
    }


    private void initViews() {
        mLayoutHY = findViewById(R.id.layout_hy);
        mLayoutYD = findViewById(R.id.layout_yd);
        mLayoutOTHERS = findViewById(R.id.layout_others);

        mTextHY = findViewById(R.id.tv_hy);
        mTextYD = findViewById(R.id.tv_yd);
        mTextOTHERS = findViewById(R.id.tv_others);

        mImgHY = findViewById(R.id.img_hy);
        mImgYD = findViewById(R.id.img_yd);
        mImgOTHERS = findViewById(R.id.img_others);

    }

    private void initQuestion() {
        final QuestionDialogFragment questionDialogFragment=QuestionDialogFragment.getInstance();
        questionDialogFragment.show(getSupportFragmentManager(),"questionDialog");
        questionDialogFragment.setOnQuestionSelectListener(new QuestionDialogFragment.OnQuestionSelectListener() {
            @Override
            public void onQuestionSelect(boolean isTrue) {
                questionDialogFragment.dismiss();
                if(isTrue){
                    Toast.makeText(MainActivity.this, "回答正确！", Toast.LENGTH_SHORT).show();

                }else {
                    Toast.makeText(MainActivity.this, "回答错误！答案是正确", Toast.LENGTH_SHORT).show();

                }
            }
        });



    }

    private GoodsEntity getGoodsEntityFromJson(String string){
        GoodsEntity entity = new GoodsEntity();
        try {
        JSONObject object = new JSONObject(string);
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
        String bz = object.getString("data:bz");
        entity.setBz(bz);
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
        entity.setHuozhu(huozhu);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return entity;
    }

    private void initEvent() {
        mLayoutHY.setOnClickListener(this);
        mLayoutYD.setOnClickListener(this);
        mLayoutOTHERS.setOnClickListener(this);
        mLayoutMsg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mTextMsgNum.setVisibility(View.GONE);
                Intent myInfoIntent = new Intent(MainActivity.this, MyMsgActivity.class);
                startActivity(myInfoIntent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
    }
    private void confirmTips(String msg) {
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getSupportFragmentManager(), "confirmTips111");
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


    }

    private void setToolbarTitle(int index) {
        switch (index) {
            case GOODS_MSG_LIST:
                optionMenuOn = true;
                checkOptionMenu();
                toolbar.setTitle("货源信息");
                break;
            case MY_WAYBILL:
                optionMenuOn = false;
                checkOptionMenu();
                toolbar.setTitle("我的运单");
                break;
            case OTHERS:
                optionMenuOn = false;
                checkOptionMenu();
                toolbar.setTitle("系统设置");
                break;

        }
    }

    private void initFragment() {
        fragments = new Fragment[4];
        fragments[0] = MyWayBillFragment.getInstance();
        fragments[1] = GoodsListFragment.getInstance();
        fragments[2] = OthersFragment.getInstance();
        fragments[3] = MapFragment.getInstance();
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.frame_content, fragments[0]).commitAllowingStateLoss();
    }

    private void initDatas() {
        userid = SharePreferenceUtil.getInstance(this).getUserId();
        if (!TextUtils.isEmpty(userid)) {
            User user = dao.findUserInfoById(userid);
            if (user != null) {
                getRzInfo(user, userid);
                loadmqttmessage(user, userid);
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    private void checkVerson() {
        Map<String,String> map=new HashMap<>();
        map.put("app",Constants.AppId);
        RequestManager.getInstance()
                .mServiceStore
                .checkVerson(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("checkVerson onSuccess", "msg=="+msg);
                        String info = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject object = new JSONObject(info);
                            String content = object.getString("title");
                            String sfqz = object.getString("sfqz");
                            String url = object.getString("appUrl");
                            String strVerson = object.getString("versionCode");
                            float apkCode = APKVersionCodeUtils.getVerName(MainActivity.this);
                            float verson=Float.parseFloat(strVerson);
                            if (verson > apkCode) {
                                boolean isNeed=false;
                                if("yes".equals(sfqz)){
                                    isNeed=true;
                                }else if("no".equals(sfqz)){
                                    isNeed=false;
                                }
                                showVersonView(content, url, isNeed);
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onError(String msg) {
                        Log.e("checkVerson onError", "msg=="+msg);
                    }
                }));
    }

    private void showVersonView(final String content, final String url, boolean isNeed) {
        builder = AllenVersionChecker
                .getInstance()
                .requestVersion()
                .setRequestUrl(Constants.APPUPDATEURL)
                .request(new RequestVersionListener() {
                    @Nullable
                    @Override
                    public UIData onRequestVersionSuccess(String result) {
                        return crateUIData(content, url);
                    }

                    @Override
                    public void onRequestVersionFailure(String message) {


                    }
                });

        if (isNeed) {
            builder.setForceUpdateListener(new ForceUpdateListener() {
                @Override
                public void onShouldForceUpdate() {
                    forceUpdate();
                }
            });
        }
        builder.setForceRedownload(true);
        builder.setDownloadAPKPath(Constants.UPDATEAPP_LOCATION);
        builder.excuteMission(MainActivity.this);

    }

    private void forceUpdate() {
        App.getInstance().exit();
    }


    private UIData crateUIData(String content, String url) {
        UIData uiData = UIData.create();
        uiData.setTitle("新版本更新");
        uiData.setDownloadUrl(url);
        uiData.setContent(content);
        return uiData;
    }

    private void loadmqttmessage(final User user, final String userid) {
        Map<String, String> map = new HashMap<>();
        map.put("mobile", userid);
        map.put("token", user.getToken());
        map.put("app", Constants.AppId);
        RequestManager.getInstance()
                .mServiceStore
                .loadmqttmessage(map)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("loadmqttmessage", msg);
                        String info = msg.replaceAll("\r", "").replaceAll("\n", "");
                        if (!TextUtils.isEmpty(info) && !"[]".equals(info)) {
                            try {
                                JSONObject jsonObject = new JSONObject(info);
                                String notice = jsonObject.getString("notice");
                                String waybill = jsonObject.getString("waybill");
                                if ((!TextUtils.isEmpty(waybill) && !"[]".equals(waybill))) {
                                    GoodsEntity entity=getGoodsEntityFromJson(info);
                                    Intent intent = new Intent(MainActivity.this, GoodsListDetailActivity.class);
                                    intent.putExtra("entity",entity);
                                    intent.putExtra("type",100);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, -1, intent
                                            , PendingIntent.FLAG_UPDATE_CURRENT);
                                    NotificationUtils notificationUtils = new NotificationUtils(MainActivity.this, pendingIntent);
                                    notificationUtils.sendNotification("通知消息", "运单信息");
                                }if((!TextUtils.isEmpty(notice) && !"[]".equals(notice))){
                                    mTextMsgNum.setVisibility(View.VISIBLE);
                                    MsgEntity entity=new MsgEntity();
                                    entity.setTime(DateUtils.getStringNowTime());
                                    entity.setMsg(info);
                                    dao.addMsgInfo(entity);
                                    Intent intent = new Intent(MainActivity.this, MyMsgActivity.class);
                                    PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.this, -1, intent
                                            , PendingIntent.FLAG_UPDATE_CURRENT);
                                    NotificationUtils notificationUtils = new NotificationUtils(MainActivity.this, pendingIntent);
                                    notificationUtils.sendNotification("通知消息", info);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(String msg) {
                    }
                }));

    }

    private void getRzInfo(final User user, final String id) {
        RequestManager.getInstance()
                .mServiceStore
                .findMyRz(user.getToken(), user.getUserId(), Constants.AppId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("getRzInfo onSuccess", msg);
                        if(TextUtils.isEmpty(msg)){
                            return;
                        }
                        boolean isSuccess = false;
                        try {
                            String alct=SharePreferenceUtil.getInstance(MainActivity.this).getALCTMsg();
                            if(!TextUtils.isEmpty(alct)){
                                doRegisterAlct(alct,msg);
                            }
                            JSONObject jsonObject = new JSONObject(msg.replaceAll("\r", "").replaceAll("\n", ""));
                            User locUser = user;
                            if(jsonObject.has("ofwlgsinfo")){
                                String string=jsonObject.getString("ofwlgsinfo");
                                if("{}".equals(string)){
                                    locUser.setOfwlgsinfo(null);
                                }else {
                                    locUser.setOfwlgsinfo(jsonObject.getString("ofwlgsinfo"));
                                }

                            }
                            if (jsonObject.has("rz#zt")) {
                                String info = jsonObject.getString("rz#zt");
                                if (info.equals("1")) {

                                    locUser.setRz(info);
                                    if(jsonObject.has("rz#xm")) {
                                        locUser.setUserName(jsonObject.getString("rz#xm"));
                                    }
                                } else if (info.equals("0")) {

                                    locUser.setRz(info);
                                    confirmRZTips("用户未认证未通过！");
                                }
                            } else {
                                locUser.setRz("");
                                confirmRZTips("用户未认证,请认证！");
                            }

                            dao.updateUserInfo(locUser, id);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                    }
                }));
    }

    private void doRegisterAlct(String alct, String json) {
        User user=null;
        String userid=SharePreferenceUtil.getInstance(this).getUserId();
        if(!TextUtils.isEmpty(userid)){
            user=dao.findUserInfoById(userid);
        }
        final MultiIdentity mMultiIdentity =new MultiIdentity();
        try {
            JSONObject mySO = new JSONObject(json);
            if(mySO.has("ofwlgsinfo")){
                if(user!=null){
                    user.setOfwlgsinfo(mySO.getString("ofwlgsinfo"));
                    if (dao.findUserIsExist(userid)) {
                        dao.updateUserInfo(user, userid);
                    } else {
                        dao.addUserInfo(user);
                    }
                }
            }

            JSONArray array=new JSONArray(alct);
            List<EnterpriseIdentity> mList=new ArrayList<>();
            for (int i=0;i<array.length();i++){
                JSONObject object=array.getJSONObject(i);
                EnterpriseIdentity enterpriseIdentity=new EnterpriseIdentity();
                enterpriseIdentity.setAppIdentity(object.getString("alctid"));
                enterpriseIdentity.setAppKey(object.getString("alctkey"));
                enterpriseIdentity.setEnterpriseCode(object.getString("alctcode"));
                mList.add(enterpriseIdentity);
            }
            //总公司
            EnterpriseIdentity enterpriseIdentity=new EnterpriseIdentity();
            enterpriseIdentity.setAppIdentity(Constants.APPIDENTITY);
            enterpriseIdentity.setAppKey(Constants.APPKEY);
            enterpriseIdentity.setEnterpriseCode(Constants.ENTERPRISECODE);
            mList.add(enterpriseIdentity);
            mMultiIdentity.setEnterpriseIdentities(mList);
            mMultiIdentity.setDriverIdentity(mySO.getString("rz#sfzh"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

        MDPLocationCollectionManager.register(MainActivity.this, mMultiIdentity, new OnResultListener() {
            @Override
            public void onSuccess() {
                //Toast.makeText(MainActivity.this, "安联注册成功！", Toast.LENGTH_SHORT).show();
                getInvoices(mMultiIdentity);
                SharePreferenceUtil.getInstance(MainActivity.this).setSDMsg("0");
            }

            @Override
            public void onFailure(String s, String s1) {
                Log.e("ssss===","s="+s+":s1="+s1);
                SharePreferenceUtil.getInstance(MainActivity.this).setSDMsg("1");
                confirmTips("联系客服,是否税登");
            }
        });

    }
    private void getInvoices(final MultiIdentity mMultiIdentity){
        List<EnterpriseIdentity> mList=mMultiIdentity.getEnterpriseIdentities();
        for(EnterpriseIdentity identity:mList){
            MDPLocationCollectionManager.getInvoices(getApplicationContext(), identity.getEnterpriseCode(), 10, 1, new OnDownloadResultListener() {
                @Override
                public void onSuccess(Object o) {
                    if(o instanceof GetInvoicesResponse){
                        GetInvoicesResponse getInvoicesResponse=(GetInvoicesResponse) o;
                        List<Invoice> list=getInvoicesResponse.getDriverInvoices();
                        for (Invoice invoice:list){
                            confirmInvoice(invoice);
                        }
                    }

                }

                @Override
                public void onFailure(String s, String s1) {

                }
            });
        }

    }
    private void confirmInvoice(Invoice invoice){
        MDPLocationCollectionManager.confirmInvoice(getApplicationContext(), invoice.getEnterpriseCode(), invoice.getDriverInvoiceCode(), new OnResultListener() {
            @Override
            public void onSuccess() {

            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
    }
    private void confirmUserExit(String msg) {

        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getSupportFragmentManager(), "ExitDialogUser");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @Override
            public void onClickOk() {
                String userid = SharePreferenceUtil.getInstance(MainActivity.this).getUserId();
                if (!TextUtils.isEmpty(userid)) {
                    dao.delUserInfoById(userid);
                }
                SharePreferenceUtil.getInstance(MainActivity.this).setUserId(null);
                dialog.dismiss();
                App.getInstance().exit();
            }
        });


    }

    @Override
    public void onBackPressed() {
        confirmExit("确定退出应用?");
    }

    private void confirmExit(String msg) {
        //退出操作
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getSupportFragmentManager(), "ExitDialog");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @Override
            public void onClickOk() {
                dialog.dismiss();
                App.getInstance().exit();

            }
        });


    }

    private void confirmRZTips(String msg) {
        //退出操作
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getSupportFragmentManager(), "ExitDialog");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @Override
            public void onClickOk() {
                dialog.dismiss();
                Intent intent = new Intent(MainActivity.this, RzTextActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);


            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.goods_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_list) {
            if (isMap) {
                item.setIcon(R.drawable.ic_action_map);
                isMap = false;
                index = GOODS_MSG_LIST;
            } else {
                item.setIcon(R.drawable.ic_action_list);
                isMap = true;
                index = GOODS_MSG;
            }
            addFragment();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addFragment() {
        if (currentTabIndex != index) {
            FragmentTransaction trx = getSupportFragmentManager()
                    .beginTransaction();
            trx.setCustomAnimations(R.anim.slide_right_in, R.anim.slide_left_out);
            trx.hide(fragments[currentTabIndex]);
            if (!fragments[index].isAdded()) {
                trx.add(R.id.frame_content, fragments[index]);
            }
            trx.show(fragments[index]).commitAllowingStateLoss();
        }
        currentTabIndex = index;

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        aMenu = menu;
        checkOptionMenu();
        return super.onPrepareOptionsMenu(menu);
    }

    private void checkOptionMenu() {
        if (null != aMenu) {
            if (optionMenuOn) {
                for (int i = 0; i < aMenu.size(); i++) {
                    if (i == 0) {
                        aMenu.getItem(0).setIcon(R.drawable.ic_action_map);
                        isMap = false;
                        index = GOODS_MSG_LIST;
                    }
                    aMenu.getItem(i).setVisible(true);
                    aMenu.getItem(i).setEnabled(true);
                }
            } else {
                for (int i = 0; i < aMenu.size(); i++) {
                    aMenu.getItem(i).setVisible(false);
                    aMenu.getItem(i).setEnabled(false);
                }
            }
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e("MainActivity onDestroy","onDestroy");
        if(conn!=null){
            unbindService(conn);
        }

    }

}
