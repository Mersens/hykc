package com.tuoying.hykc.entity;

public class BugMsgEntity {
    public static final String TABLE_NAME="BUG_MSG";
    public static final String USERID="userId";
    public static final String ROWID="_rowId";
    public static final String PHONEMODEL="phoneModel";
    public static final String VERSIONCODE="versionCode";
    public static final String DEVICEBRAND="deviceBrand";
    public static final String IMEI="iMEI";
    public static final String TIME="time";
    public static final String ERRORMSG="errormsg";
    public static final String SUBMIT="submit";

    private String userId;//用户id
    private String rowId;//运单id
    private String phoneModel;//手机型号
    private String versionCode;//系统版本号
    private String deviceBrand;//手机厂商
    private String iMEI;//设备IMEI
    private String time;//时间
    private String errorMsg;//异常信息

    private int submit=0;//是否上传 0否，1是

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getRowId() {
        return rowId;
    }

    public void setRowId(String rowId) {
        this.rowId = rowId;
    }

    public String getPhoneModel() {
        return phoneModel;
    }

    public void setPhoneModel(String phoneModel) {
        this.phoneModel = phoneModel;
    }

    public String getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(String versionCode) {
        this.versionCode = versionCode;
    }

    public String getDeviceBrand() {
        return deviceBrand;
    }

    public void setDeviceBrand(String deviceBrand) {
        this.deviceBrand = deviceBrand;
    }

    public String getiMEI() {
        return iMEI;
    }

    public void setiMEI(String iMEI) {
        this.iMEI = iMEI;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public int getSubmit() {
        return submit;
    }

    public void setSubmit(int submit) {
        this.submit = submit;
    }

}
