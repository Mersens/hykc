package com.tuoying.hykc.entity;

import java.math.BigDecimal;

/**
 * app 司机用户实体类
 */
public class UDriver extends BaseObject{
    private long id;
    private String driverName;//司机姓名
    private String mobile;//司机手机号
    private String pwd;//密码
    private String walletPwd;//钱包密码
    private String identityNo;//身份证号
    private String identity_effectiveStartDate;//	身份证有效期开始日期
    private String isEndless;//是否永久有效
    private String identity_effectiveEndDate;//身份证有效期结束日期
    private String licenseNo;//	驾驶证号
    private String licenseFirstGetDate;//首次驾照签发日期
    private String licenseType;//驾驶证类型
    private String drivingLicense_effectiveStartDate;//驾驶证有效期开始日期
    private String drivingLicense_effectiveEndDate;//驾驶证结束日期
    private String taxpayerName;//以下为税务登记人信息  纳税人姓名
    private String taxpayerIdentityNo;//纳税人身份证号
    private String taxpayerAddress;//身份证地址
    private String taxpayerMobile;//纳税人手机号
    private String bankName;//开户行名称
    private String bankAccount;//开户行账号
    private String regAlctMsg;//安联注册返回结果
    private Double money;//用户金额
    private double oilPrice;//油卡钱包
    private String createTime;//创建时间
    private String lastTime;//最后登录时间
    private String mobileModel;//手机型号
    private String mobileVersion;//手机系统版本
    private String token;//token
    private String sms;//验证码
    private String sysSms;//系统验证码
    private int car1Id;//绑定车辆id
    private int car2Id;
    private int car3Id;
    private int status;//是否实名认证 0未实名 1已通过 2未通过 3未审核
    private int type;//0=未完成 1已完成 2已撤销
    private  String sfz_z_url;//身份证正面
    private  String sfz_f_url;//身份证反面
    private  String jsz_url ;//驾驶证
    private  String xsz_url ;//行驶证
    private  String dlysz_url ;//道路运输证
    private  String cyzgz_url ;//从业资格证
    private  String xsz_z_url ;//行驶证副页正
    private  String xsz_f_url ;//行驶证副页反
    private  String gczzy_url ;//挂车证主页
    private  String gczfy_z_url ;//挂车证副页正
    private  String gczfy_f_url ;//挂车证副页反
    private int imgType;
    public int getImgType() {
        return imgType;
    }

    public void setImgType(int imgType) {
        this.imgType = imgType;
    }





    public String getSfz_z_url() {
        return sfz_z_url;
    }

    public void setSfz_z_url(String sfz_z_url) {
        this.sfz_z_url = sfz_z_url;
    }

    public String getSfz_f_url() {
        return sfz_f_url;
    }

    public void setSfz_f_url(String sfz_f_url) {
        this.sfz_f_url = sfz_f_url;
    }

    public String getJsz_url() {
        return jsz_url;
    }

    public void setJsz_url(String jsz_url) {
        this.jsz_url = jsz_url;
    }

    public String getXsz_url() {
        return xsz_url;
    }

    public void setXsz_url(String xsz_url) {
        this.xsz_url = xsz_url;
    }

    public String getDlysz_url() {
        return dlysz_url;
    }

    public void setDlysz_url(String dlysz_url) {
        this.dlysz_url = dlysz_url;
    }

    public String getCyzgz_url() {
        return cyzgz_url;
    }

    public void setCyzgz_url(String cyzgz_url) {
        this.cyzgz_url = cyzgz_url;
    }

    public String getXsz_z_url() {
        return xsz_z_url;
    }

    public void setXsz_z_url(String xsz_z_url) {
        this.xsz_z_url = xsz_z_url;
    }

    public String getXsz_f_url() {
        return xsz_f_url;
    }

    public void setXsz_f_url(String xsz_f_url) {
        this.xsz_f_url = xsz_f_url;
    }

    public String getGczzy_url() {
        return gczzy_url;
    }

    public void setGczzy_url(String gczzy_url) {
        this.gczzy_url = gczzy_url;
    }

    public String getGczfy_z_url() {
        return gczfy_z_url;
    }

    public void setGczfy_z_url(String gczfy_z_url) {
        this.gczfy_z_url = gczfy_z_url;
    }

    public String getGczfy_f_url() {
        return gczfy_f_url;
    }

    public void setGczfy_f_url(String gczfy_f_url) {
        this.gczfy_f_url = gczfy_f_url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    public String getIdentityNo() {
        return identityNo;
    }

    public void setIdentityNo(String identityNo) {
        this.identityNo = identityNo;
    }

    public String getIdentity_effectiveStartDate() {
        return identity_effectiveStartDate;
    }

    public void setIdentity_effectiveStartDate(String identity_effectiveStartDate) {
        this.identity_effectiveStartDate = identity_effectiveStartDate;
    }

    public String getIsEndless() {
        return isEndless;
    }

    public void setIsEndless(String isEndless) {
        this.isEndless = isEndless;
    }

    public String getIdentity_effectiveEndDate() {
        return identity_effectiveEndDate;
    }

    public void setIdentity_effectiveEndDate(String identity_effectiveEndDate) {
        this.identity_effectiveEndDate = identity_effectiveEndDate;
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

    public String getDrivingLicense_effectiveStartDate() {
        return drivingLicense_effectiveStartDate;
    }

    public void setDrivingLicense_effectiveStartDate(String drivingLicense_effectiveStartDate) {
        this.drivingLicense_effectiveStartDate = drivingLicense_effectiveStartDate;
    }

    public String getDrivingLicense_effectiveEndDate() {
        return drivingLicense_effectiveEndDate;
    }

    public void setDrivingLicense_effectiveEndDate(String drivingLicense_effectiveEndDate) {
        this.drivingLicense_effectiveEndDate = drivingLicense_effectiveEndDate;
    }

    public String getTaxpayerName() {
        return taxpayerName;
    }

    public void setTaxpayerName(String taxpayerName) {
        this.taxpayerName = taxpayerName;
    }

    public String getTaxpayerIdentityNo() {
        return taxpayerIdentityNo;
    }

    public void setTaxpayerIdentityNo(String taxpayerIdentityNo) {
        this.taxpayerIdentityNo = taxpayerIdentityNo;
    }

    public String getTaxpayerAddress() {
        return taxpayerAddress;
    }

    public void setTaxpayerAddress(String taxpayerAddress) {
        this.taxpayerAddress = taxpayerAddress;
    }

    public String getTaxpayerMobile() {
        return taxpayerMobile;
    }

    public void setTaxpayerMobile(String taxpayerMobile) {
        this.taxpayerMobile = taxpayerMobile;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getBankAccount() {
        return bankAccount;
    }

    public void setBankAccount(String bankAccount) {
        this.bankAccount = bankAccount;
    }

    public String getRegAlctMsg() {
        return regAlctMsg;
    }

    public void setRegAlctMsg(String regAlctMsg) {
        this.regAlctMsg = regAlctMsg;
    }

    public Double getMoney() {
        return money;
    }

    public void setMoney(Double money) {
        this.money = money;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public String getMobileModel() {
        return mobileModel;
    }

    public void setMobileModel(String mobileModel) {
        this.mobileModel = mobileModel;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getSms() {
        return sms;
    }

    public void setSms(String sms) {
        this.sms = sms;
    }

    public String getSysSms() {
        return sysSms;
    }

    public void setSysSms(String sysSms) {
        this.sysSms = sysSms;
    }

    public int getCar1Id() {
        return car1Id;
    }

    public void setCar1Id(int car1Id) {
        this.car1Id = car1Id;
    }

    public int getCar2Id() {
        return car2Id;
    }

    public void setCar2Id(int car2Id) {
        this.car2Id = car2Id;
    }

    public int getCar3Id() {
        return car3Id;
    }

    public void setCar3Id(int car3Id) {
        this.car3Id = car3Id;
    }

    public String getMobileVersion() {
        return mobileVersion;
    }

    public void setMobileVersion(String mobileVersion) {
        this.mobileVersion = mobileVersion;
    }

    public String getWalletPwd() {
        return walletPwd;
    }

    public void setWalletPwd(String walletPwd) {
        this.walletPwd = walletPwd;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public double getOilPrice() {
        return oilPrice;
    }

    public void setOilPrice(double oilPrice) {
        this.oilPrice = oilPrice;
    }
    /*@Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"id\":")
                .append(id);
        sb.append(",\"driverName\":\"")
                .append(driverName).append('\"');
        sb.append(",\"mobile\":\"")
                .append(mobile).append('\"');
        sb.append(",\"pwd\":\"")
                .append(pwd).append('\"');
        sb.append(",\"walletPwd\":\"")
                .append(walletPwd).append('\"');
        sb.append(",\"identityNo\":\"")
                .append(identityNo).append('\"');
        sb.append(",\"identity_effectiveStartDate\":\"")
                .append(identity_effectiveStartDate).append('\"');
        sb.append(",\"isEndless\":\"")
                .append(isEndless).append('\"');
        sb.append(",\"identity_effectiveEndDate\":\"")
                .append(identity_effectiveEndDate).append('\"');
        sb.append(",\"licenseNo\":\"")
                .append(licenseNo).append('\"');
        sb.append(",\"licenseFirstGetDate\":\"")
                .append(licenseFirstGetDate).append('\"');
        sb.append(",\"licenseType\":\"")
                .append(licenseType).append('\"');
        sb.append(",\"drivingLicense_effectiveStartDate\":\"")
                .append(drivingLicense_effectiveStartDate).append('\"');
        sb.append(",\"drivingLicense_effectiveEndDate\":\"")
                .append(drivingLicense_effectiveEndDate).append('\"');
        sb.append(",\"taxpayerName\":\"")
                .append(taxpayerName).append('\"');
        sb.append(",\"taxpayerIdentityNo\":\"")
                .append(taxpayerIdentityNo).append('\"');
        sb.append(",\"taxpayerAddress\":\"")
                .append(taxpayerAddress).append('\"');
        sb.append(",\"taxpayerMobile\":\"")
                .append(taxpayerMobile).append('\"');
        sb.append(",\"bankName\":\"")
                .append(bankName).append('\"');
        sb.append(",\"bankAccount\":\"")
                .append(bankAccount).append('\"');
        sb.append(",\"regAlctMsg\":\"")
                .append(regAlctMsg).append('\"');
        sb.append(",\"money\":")
                .append(money);
        sb.append(",\"createTime\":\"")
                .append(createTime).append('\"');
        sb.append(",\"lastTime\":\"")
                .append(lastTime).append('\"');
        sb.append(",\"mobileModel\":\"")
                .append(mobileModel).append('\"');
        sb.append(",\"mobileVersion\":\"")
                .append(mobileVersion).append('\"');
        sb.append(",\"token\":\"")
                .append(token).append('\"');
        sb.append(",\"sms\":\"")
                .append(sms).append('\"');
        sb.append(",\"sysSms\":\"")
                .append(sysSms).append('\"');
        sb.append(",\"car1Id\":")
                .append(car1Id);
        sb.append(",\"car2Id\":")
                .append(car2Id);
        sb.append(",\"car3Id\":")
                .append(car3Id);
        sb.append(",\"status\":")
                .append(status);
        sb.append('}');
        return sb.toString();
    }*/
}
