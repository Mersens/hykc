package com.tuoying.hykc.app;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.alct.mdp.MDPLocationCollectionManager;



import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/21.
 */

public class App extends Application {
    private static App sApp;
    private List<Activity> mList;
    public App(){
        mList=new ArrayList<>();
    }

    private static String getCurrentProcessName(Context context) {
        String currentProcessName = "";
        int pid = android.os.Process.myPid();
        ActivityManager mActivityManager = (ActivityManager) context .getSystemService(Context.ACTIVITY_SERVICE);

        if (mActivityManager.getRunningAppProcesses() != null && mActivityManager.getRunningAppProcesses().size() > 0) {
            for (ActivityManager.RunningAppProcessInfo appProcess : mActivityManager.getRunningAppProcesses()) {
                if (appProcess.pid == pid) {
                    currentProcessName = appProcess.processName;
                }
            }
        }
        return currentProcessName;
    }

    public static App getInstance(){
        if(sApp==null){
            synchronized (App.class){
                if(sApp==null){
                    sApp=new App();
                }
            }
        }
        return sApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        sApp=this;
        //SDKInitializer.initialize(this);
        //SDKInitializer.setCoordType(CoordType.BD09LL);
        //ALCTSDKHealth.initialize(this,Constants.ALCT_URL);
        initALCT(getApplicationContext());
        CrashHandler.getInstance().init(getApplicationContext());
        addLifecycleCallbacks();
    }

    private void initALCT(Context context) {
        if (context.getPackageName().equals(getCurrentProcessName(context))) {
            MDPLocationCollectionManager.initialize(this, Constants.ALCT_URL);

        }
    }

    private void addLifecycleCallbacks(){
        registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle bundle) {
                Log.e("onActivityCreated","onActivityCreated==="+activity.getClass().getName());
                mList.add(activity);
            }

            @Override
            public void onActivityStarted(Activity activity) {

            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {

            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                mList.remove(activity);
                Log.e("onActivityDestroyed","onActivityDestroyed==="+activity.getClass().getName());
            }
        });
    }
    public void exit() {
        try {
            for (int i=0;i<mList.size();i++) {
                Activity activity=mList.get(i);
                if (activity != null)
                    activity.finish();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            //System.exit(0);
        }
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }
}
