package com.tuoying.hykc.entity;

import java.io.Serializable;

public class RZEntity implements Serializable {
    private String sfzh;//身份证号
    private String mobile;//手机号
    private String xm;//司机姓名
    private String cph;//车牌号
    private String pp;//车辆品牌
    private String cc;//车长
    private String cx;//车型
    private String zz;//载重
    private String nf;//年份
    private String dlysz;//道路运输证号
    private String cplx;//车牌类型
    private String clfl;//车辆分类
    private String zt;//状态 0未通过，1已通过，空未认证
    private String sfzStartTime;//身份证开始有效期
    private String sfzEndTime;//身份证结束有效期
    private String licenseNo;//驾驶证编号
    private String licenseFirstGetDate;//首次驾驶证获得时间
    private String licenseType;//驾驶证类型
    private String licenseStartTime;//驾驶证开始有效日期
    private String licenseEndTime;//驾驶证套有效结束日期
    private String vehicleIdentityCode;//车辆识别码
    private String syr;//所有人
    public String getSyr() {
        return syr;
    }

    public void setSyr(String syr) {
        this.syr = syr;
    }



    public String getSfzStartTime() {
        return sfzStartTime;
    }

    public void setSfzStartTime(String sfzStartTime) {
        this.sfzStartTime = sfzStartTime;
    }

    public String getSfzEndTime() {
        return sfzEndTime;
    }

    public void setSfzEndTime(String sfzEndTime) {
        this.sfzEndTime = sfzEndTime;
    }

    public String getLicenseNo() {
        return licenseNo;
    }

    public void setLicenseNo(String licenseNo) {
        this.licenseNo = licenseNo;
    }

    public String getLicenseFirstGetDate() {
        return licenseFirstGetDate;
    }

    public void setLicenseFirstGetDate(String licenseFirstGetDate) {
        this.licenseFirstGetDate = licenseFirstGetDate;
    }

    public String getLicenseType() {
        return licenseType;
    }

    public void setLicenseType(String licenseType) {
        this.licenseType = licenseType;
    }

    public String getLicenseStartTime() {
        return licenseStartTime;
    }

    public void setLicenseStartTime(String licenseStartTime) {
        this.licenseStartTime = licenseStartTime;
    }

    public String getLicenseEndTime() {
        return licenseEndTime;
    }

    public void setLicenseEndTime(String licenseEndTime) {
        this.licenseEndTime = licenseEndTime;
    }

    public String getVehicleIdentityCode() {
        return vehicleIdentityCode;
    }

    public void setVehicleIdentityCode(String vehicleIdentityCode) {
        this.vehicleIdentityCode = vehicleIdentityCode;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    private String engineNumber;//引擎编号


    public String getZt() {
        return zt;
    }

    public void setZt(String zt) {
        this.zt = zt;
    }



    public String getSfzh() {
        return sfzh;
    }

    public void setSfzh(String sfzh) {
        this.sfzh = sfzh;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getXm() {
        return xm;
    }

    public void setXm(String xm) {
        this.xm = xm;
    }

    public String getCph() {
        return cph;
    }

    public void setCph(String cph) {
        this.cph = cph;
    }

    public String getPp() {
        return pp;
    }

    public void setPp(String pp) {
        this.pp = pp;
    }

    public String getCc() {
        return cc;
    }

    public void setCc(String cc) {
        this.cc = cc;
    }

    public String getCx() {
        return cx;
    }

    public void setCx(String cx) {
        this.cx = cx;
    }

    public String getZz() {
        return zz;
    }

    public void setZz(String zz) {
        this.zz = zz;
    }

    public String getNf() {
        return nf;
    }

    public void setNf(String nf) {
        this.nf = nf;
    }

    public String getDlysz() {
        return dlysz;
    }

    public void setDlysz(String dlysz) {
        this.dlysz = dlysz;
    }

    public String getCplx() {
        return cplx;
    }

    public void setCplx(String cplx) {
        this.cplx = cplx;
    }

    public String getClfl() {
        return clfl;
    }

    public void setClfl(String clfl) {
        this.clfl = clfl;
    }



}
