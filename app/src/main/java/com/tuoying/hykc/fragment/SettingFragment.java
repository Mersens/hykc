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
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.allenliu.versionchecklib.v2.AllenVersionChecker;
import com.allenliu.versionchecklib.v2.builder.DownloadBuilder;
import com.allenliu.versionchecklib.v2.builder.NotificationBuilder;
import com.allenliu.versionchecklib.v2.builder.UIData;
import com.allenliu.versionchecklib.v2.callback.ForceUpdateListener;
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener;
import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.FindPsdActivity;
import com.tuoying.hykc.activity.LoginActivity;
import com.tuoying.hykc.activity.MainActivity;
import com.tuoying.hykc.activity.SetWalletPsdActivity;
import com.tuoying.hykc.activity.SettingActivity;
import com.tuoying.hykc.activity.UpdatePsdActivity;
import com.tuoying.hykc.app.App;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.utils.APKVersionCodeUtils;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.utils.ViewClickHelper;
import com.tuoying.hykc.view.ExitDialogFragment;
import com.tuoying.hykc.view.LoadingDialogFragment;
import com.tuoying.hykc.view.LoadingView;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Administrator on 2018/3/26.
 */

public class SettingFragment extends BaseFragment {
    private static final long SPLASH_DELAY_SECONDS = 3;
    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();
    private RelativeLayout mLayoutXgmm;
    private RelativeLayout mLayoutQchc;
    private RelativeLayout mLayoutUpdate;
    private RelativeLayout mLayoutExit;
    private RelativeLayout mLayoutQbmm;
    private LoadingView mLoadView;
    private DBDao dao;
    private DownloadBuilder builder;
    WeakReference<SettingActivity> mActivityReference;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityReference=new WeakReference<>((SettingActivity)context);
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_setting,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        dao=new DBDaoImpl(getActivity());
        init(view);
    }

    @Override
    public void init(View v) {
        mLayoutXgmm=v.findViewById(R.id.layout_xgmm);
        mLayoutQchc=v.findViewById(R.id.layout_qchc);
        mLayoutUpdate=v.findViewById(R.id.layout_update);
        mLayoutExit=v.findViewById(R.id.layout_exit);
        mLoadView=v.findViewById(R.id.loadView);
        mLayoutQbmm=v.findViewById(R.id.layout_qbmm);
        initEvent();
    }

    private void initEvent() {
        mLayoutXgmm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent findIntent=new Intent(getActivity(), FindPsdActivity.class);
                startActivity(findIntent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        mLayoutQbmm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent findIntent=new Intent(getActivity(), SetWalletPsdActivity.class);
                startActivity(findIntent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        ViewClickHelper.clicks(mLayoutQchc, new ViewClickHelper.onViewClickListener() {
            @Override
            public void onAccept() {
                mLayoutQchc.setClickable(false);
                mLoadView.setVisibility(View.VISIBLE);
                doInterval();
            }
        });
        mLayoutUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkVersion();
            }
        });

        mLayoutExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                confirmExit("确定退出登录?");
            }
        });
    }

    private void confirmExit(String msg) {

        //退出操作
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
            }

            @Override
            public void onClickOk() {
                String userid = SharePreferenceUtil.getInstance(getActivity()).getUserId();
                if (!TextUtils.isEmpty(userid)) {
                    dao.delUserInfoById(userid);
                }
                SharePreferenceUtil.getInstance(getActivity()).setUserId(null);
                Intent intent=new Intent(getActivity(),LoginActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                dialog.dismiss();
                App.getInstance().exit();

            }
        });
        dialog.show(getChildFragmentManager(), "ExitDialog");

    }
    private void checkVersion() {
        final LoadingDialogFragment checkVersionDialog=LoadingDialogFragment.getInstance();
        checkVersionDialog.show(getChildFragmentManager(),"checkVersionDialog");
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
                            float apkCode = APKVersionCodeUtils.getVerName(getActivity());
                            float verson=Float.parseFloat(strVerson);
                            if (verson > apkCode) {
                                boolean isNeed=false;
                                if("yes".equals(sfqz)){
                                    isNeed=true;
                                }else if("no".equals(sfqz)){
                                    isNeed=false;
                                }
                                showVersonView(content, url, isNeed);
                            }else {
                                Toast.makeText(getActivity(), "该版本已是最新版！", Toast.LENGTH_SHORT).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }finally {
                            if(checkVersionDialog!=null){
                                checkVersionDialog.dismiss();

                            }
                        }
                    }
                    @Override
                    public void onError(String msg) {
                        if(checkVersionDialog!=null){
                            checkVersionDialog.dismiss();

                        }
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
        builder.excuteMission(getActivity());

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

    private void doUpdate() {
        //更新

    }

    private NotificationBuilder createCustomNotification() {
        return NotificationBuilder.create()
                .setRingtone(true)
                .setIcon(R.mipmap.ic_launcher)
                .setTicker("custom_ticker")
                .setContentTitle("custom title")
                .setContentText(getString(R.string.custom_content_text));
    }
    private UIData crateUIData() {
        UIData uiData = UIData.create();
        uiData.setTitle(getString(R.string.update_title));
        uiData.setDownloadUrl("http://test-1251233192.coscd.myqcloud.com/1_1.apk");
        uiData.setContent(getString(R.string.updatecontent));
        return uiData;
    }
    private void doInterval() {
        Disposable mIntervalDisposable = Observable.interval(0, 1, TimeUnit.SECONDS)
                .take(SPLASH_DELAY_SECONDS + 1)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Long, Long>() {
                    @Override
                    public Long apply(@NonNull Long aLong) throws Exception {
                        return SPLASH_DELAY_SECONDS - aLong;
                    }
                }).subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        if (aLong == 0) {
                            mLoadView.setVisibility(View.GONE);
                            mLayoutQchc.setClickable(true);
                        }
                    }
                });
        mCompositeDisposable.add(mIntervalDisposable);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.clear();
    }

    public static SettingFragment getInstance(){
        return new SettingFragment();
    }
}
