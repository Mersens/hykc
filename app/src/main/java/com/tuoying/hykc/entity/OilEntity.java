package com.tuoying.hykc.entity;

import com.tuoying.hykc.service.ServiceStore;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class OilEntity implements Serializable {
    private String stationId;//油站id
    private String stationName;////油站名称
    private String provinceCode;//
    private String cityCode;//
    private String areaCode;//
    private String lng;//
    private String lat;//
    private String address;//
    private int isStop;//油站是否停用0正常1停用
    private int isHighspeed;//是否在高速上
    private List<FuelsEntity> fuels=new ArrayList<>();//下属油品
    public String getStationId() {
        return stationId;
    }

    public void setStationId(String stationId) {
        this.stationId = stationId;
    }

    public String getStationName() {
        return stationName;
    }

    public void setStationName(String stationName) {
        this.stationName = stationName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }

    public String getCityCode() {
        return cityCode;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public String getAreaCode() {
        return areaCode;
    }

    public void setAreaCode(String areaCode) {
        this.areaCode = areaCode;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getIsStop() {
        return isStop;
    }

    public void setIsStop(int isStop) {
        this.isStop = isStop;
    }

    public int getIsHighspeed() {
        return isHighspeed;
    }

    public void setIsHighspeed(int isHighspeed) {
        this.isHighspeed = isHighspeed;
    }

    public List<FuelsEntity> getFuels() {
        return fuels;
    }

    public void setFuels(List<FuelsEntity> fuels) {
        this.fuels = fuels;
    }





}
