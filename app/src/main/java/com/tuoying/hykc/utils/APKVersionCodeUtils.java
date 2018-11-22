package com.tuoying.hykc.utils;

import android.content.Context;
import android.content.pm.PackageManager;

public class APKVersionCodeUtils {
    /**
     * 获取当前本地apk的版本
     *
     * @param mContext
     * @return
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        try {
            //获取软件版本号，对应AndroidManifest.xml下android:versionCode
            versionCode = mContext.getPackageManager().
                    getPackageInfo(mContext.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
    }

    /**
     * 获取版本号名称
     *
     * @param context 上下文
     * @return
     */
    public static float getVerName(Context context) {
        float verName = 0;
        try {
            String name = context.getPackageManager().
                    getPackageInfo(context.getPackageName(), 0).versionName;
            verName=Float.parseFloat(name);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return verName;
    }
}
