package com.tuoying.hykc.entity;

/**
 * Created by Administrator on 2018/3/23.
 */

public class User {
    public static final String TABLE_NAME="USER";
    public static final String USERNAME="username";
    public static final String USERID="userid";
    public static final String PSD="psd";
    public static final String TOKEN="token";
    public static final String RZ="rz";
    public static final String OFWLGSINFO="ofwlgsinfo";
    private String userName;
    private String userId;
    private String psd;
    private String token;
    private String rz="";//认证
    private String ofwlgsinfo;

    public String getOfwlgsinfo() {
        return ofwlgsinfo;
    }

    public void setOfwlgsinfo(String ofwlgsinfo) {
        this.ofwlgsinfo = ofwlgsinfo;
    }



    public String getRz() {
        return rz;
    }
    public void setRz(String rz) {
        this.rz = rz;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPsd() {
        return psd;
    }

    public void setPsd(String psd) {
        this.psd = psd;
    }



}
