package com.tuoying.hykc.entity;

import java.io.Serializable;

public class UCarEntity implements Serializable {
    /**
     * id
     */
    private int id;

    /**
     * 车牌号
     */
    private String licensePlateNo;
    /**
     * 车辆类型
     */
    private String standardVehicleType;
    /**
     * 车架号
     */
    private String vehicleIdentityCode;
    /**
     * 汽车品牌
     */
    private String brand;
    /**
     * 发动机号
     */
    private String engineNumber;
    /**
     * 车辆所有人
     */
    private String owner_p;
    /**
     * 使用性质
     */
    private String usage_p;
    /**
     * 载重
     */
    private String load_p;
    /**
     * 挂靠企业名称
     */
    private String affiliatedEnterprise;
    /**
     * 道路经营许可证编号
     */
    private String transportLicenseNo;
    /**
     * 道路经营许可证有效期
     */
    private String transportLicenseExpireDate;
    /**
     * 机动车登记证书编号
     */
    private String vehicleRegistrationCertificateNo;
    /**
     * Nfc标签
     */
    private String nfcId;
    /**
     * 注册时间
     */
    private String createtime;
    /**
     * 车辆返回信息
     */
    private String carmsg;
    /**
     * 车辆类别 0普通车辆 1牵引车 2挂车
     */
    private String cartype;

    /**
     * 是否注册成功 0未成功1成功
     */
    private int status;



    /**
     * 绑定司机
     */
    private String licensePlateNoold;//绑定挂车用

    private long driverId1;
    private long driverId2;
    private long driverId3;
    private String cplx;//车牌类型
    private String clfl;//车辆分类
    private String car_len;//车长
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

    public String getLicensePlateNoold() {
        return licensePlateNoold;
    }

    public void setLicensePlateNoold(String licensePlateNoold) {
        this.licensePlateNoold = licensePlateNoold;
    }

    /**
     * 司机手机号
     */
    private String mobile;

    public String getCar_len() {
        return car_len;
    }

    public void setCar_len(String car_len) {
        this.car_len = car_len;
    }



    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getLicensePlateNo() {
        return licensePlateNo;
    }

    public void setLicensePlateNo(String licensePlateNo) {
        this.licensePlateNo = licensePlateNo;
    }

    public String getStandardVehicleType() {
        return standardVehicleType;
    }

    public void setStandardVehicleType(String standardVehicleType) {
        this.standardVehicleType = standardVehicleType;
    }

    public String getVehicleIdentityCode() {
        return vehicleIdentityCode;
    }

    public void setVehicleIdentityCode(String vehicleIdentityCode) {
        this.vehicleIdentityCode = vehicleIdentityCode;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getEngineNumber() {
        return engineNumber;
    }

    public void setEngineNumber(String engineNumber) {
        this.engineNumber = engineNumber;
    }

    public String getOwner_p() {
        return owner_p;
    }

    public void setOwner_p(String owner_p) {
        this.owner_p = owner_p;
    }

    public String getUsage_p() {
        return usage_p;
    }

    public void setUsage_p(String usage_p) {
        this.usage_p = usage_p;
    }

    public String getLoad_p() {
        return load_p;
    }

    public void setLoad_p(String load_p) {
        this.load_p = load_p;
    }

    public String getAffiliatedEnterprise() {
        return affiliatedEnterprise;
    }

    public void setAffiliatedEnterprise(String affiliatedEnterprise) {
        this.affiliatedEnterprise = affiliatedEnterprise;
    }

    public String getTransportLicenseNo() {
        return transportLicenseNo;
    }

    public void setTransportLicenseNo(String transportLicenseNo) {
        this.transportLicenseNo = transportLicenseNo;
    }

    public String getTransportLicenseExpireDate() {
        return transportLicenseExpireDate;
    }

    public void setTransportLicenseExpireDate(String transportLicenseExpireDate) {
        this.transportLicenseExpireDate = transportLicenseExpireDate;
    }

    public String getVehicleRegistrationCertificateNo() {
        return vehicleRegistrationCertificateNo;
    }

    public void setVehicleRegistrationCertificateNo(String vehicleRegistrationCertificateNo) {
        this.vehicleRegistrationCertificateNo = vehicleRegistrationCertificateNo;
    }

    public String getNfcId() {
        return nfcId;
    }

    public void setNfcId(String nfcId) {
        this.nfcId = nfcId;
    }

    public String getCreatetime() {
        return createtime;
    }

    public void setCreatetime(String createtime) {
        this.createtime = createtime;
    }

    public String getCarmsg() {
        return carmsg;
    }

    public void setCarmsg(String carmsg) {
        this.carmsg = carmsg;
    }

    public String getCartype() {
        return cartype;
    }

    public void setCartype(String cartype) {
        this.cartype = cartype;
    }


    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getDriverId1() {
        return driverId1;
    }

    public void setDriverId1(long driverId1) {
        this.driverId1 = driverId1;
    }

    public long getDriverId2() {
        return driverId2;
    }

    public void setDriverId2(long driverId2) {
        this.driverId2 = driverId2;
    }

    public long getDriverId3() {
        return driverId3;
    }

    public void setDriverId3(long driverId3) {
        this.driverId3 = driverId3;
    }
}
