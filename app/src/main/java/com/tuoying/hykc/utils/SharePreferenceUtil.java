package com.tuoying.hykc.utils;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author Mersens
 * @title SharePreferenceUtil
 * @description:SharePreference工具类，数据存储
 * @time 2016年4月6日
 */
public class SharePreferenceUtil {
    private static SharePreferenceUtil sp;
    private static SharedPreferences mSharedPreferences;
    private static SharedPreferences.Editor editor;
    private static final String PREFERENCE_NAME = "_sharedinfo";
    private static final String IS_FIRST = "is_first";
    private static final String USER_ID = "user_id";
    private static final String BZJ_BL = "bzj_bl";
    private static final String MQTT_URL = "mqtt_url";
    private static final String NOTICE_MSG = "notice_msg";
    private static final String SD_MSG = "sd_msg";
    private static final String ALCT_MSG = "alct_msg";

    public static Boolean getIsFirst() {
        return mSharedPreferences.getBoolean(IS_FIRST, true);
    }

    public static void setIsFirst(Boolean isIsFirst) {
        editor.putBoolean(IS_FIRST, isIsFirst);
        editor.commit();

    }

    private SharePreferenceUtil() {

    }

    public static synchronized SharePreferenceUtil getInstance(Context context) {
        if (sp == null) {
            sp = new SharePreferenceUtil();
            mSharedPreferences = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
            editor = mSharedPreferences.edit();
        }
        return sp;
    }


    public void setBZJ(String bzj) {
        editor.putString(BZJ_BL, bzj);
        editor.commit();
    }

    public String getBZJ() {
        return mSharedPreferences.getString(BZJ_BL, null);
    }

    public String getUserId() {
        return mSharedPreferences.getString(USER_ID, null);
    }

    public void setUserId(String userid) {
        editor.putString(USER_ID, userid);
        editor.commit();
    }

    public void setMqttUrl(String mqttUrl) {
        editor.putString(MQTT_URL, mqttUrl);
        editor.commit();
    }

    public String getMqttUrl() {
        return mSharedPreferences.getString(MQTT_URL, null);
    }

    public void setNoticeMsg(String msg) {
        editor.putString(NOTICE_MSG, msg);
        editor.commit();
    }

    public String getNoticeMsg() {
        return mSharedPreferences.getString(NOTICE_MSG, null);
    }


    public void setSDMsg(String msg) {
        editor.putString(SD_MSG, msg);
        editor.commit();
    }

    public String getSDMsg() {
        return mSharedPreferences.getString(SD_MSG, null);
    }

    public void setALCTMsg(String msg) {
        editor.putString(ALCT_MSG, msg);
        editor.commit();
    }

    public String getALCTMsg() {
        return mSharedPreferences.getString(ALCT_MSG, null);
    }
}
