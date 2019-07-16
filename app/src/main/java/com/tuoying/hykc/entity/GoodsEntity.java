package com.tuoying.hykc.entity;

import java.io.Serializable;

public class GoodsEntity implements Serializable {
    private String startAddress;
    private String endAddress;
    private String sid;
    private String rowid;
    private String name;
    private String weight;
    private String volume;
    private String bz;
    private String hzxm;
    private String zyf;
    private String bl;
    private String time;
    private String shrName;
    private String shrTel;
    private String statusName;
    private String lon_to;//经度
    private String lat_to;//维度
    private String lon_from;//经度
    private String lat_from;//维度
    private String status;
    private String driver;
    private String huozhu;
    private String alctCode;
    private String  alctId ;
    private String alctKey;
    private String driver_score;
    private boolean isOfwlgsinfo;
    private String policyNo;
    private String yf_detain;//未结算运费
    private String yf_difference;//货损货差
    private String yf_remark;//结算运费备注
    private String driver_appraise;
    private String moxid;
    private String jdTime;//接单时间
    private String pdwlgs;//派单物流公司
    private boolean isGuiding = false;
    private String task_id;
    private String driverPrice;
    private String yd_cph;
    private String to_addr;
    private String from_addr;
    public String getYd_cph() {
        return yd_cph;
    }

    public void setYd_cph(String yd_cph) {
        this.yd_cph = yd_cph;
    }

    public String getTo_addr() {
        return to_addr;
    }

    public void setTo_addr(String to_addr) {
        this.to_addr = to_addr;
    }

    public String getFrom_addr() {
        return from_addr;
    }

    public void setFrom_addr(String from_addr) {
        this.from_addr = from_addr;
    }



    private String pd_ext;

    public String getPd_ext() {
        return pd_ext;
    }

    public void setPd_ext(String pd_ext) {
        this.pd_ext = pd_ext;
    }

    private String req_length;

    public String getReq_length() {
        return req_length;
    }

    public void setReq_length(String req_length) {
        this.req_length = req_length;
    }

    public String getTask_id() {
        return task_id;
    }

    public void setTask_id(String task_id) {
        this.task_id = task_id;
    }

    public String getDriverPrice() {
        return driverPrice;
    }

    public void setDriverPrice(String driverPrice) {
        this.driverPrice = driverPrice;
    }


    public String getPdwlgs() {
        return pdwlgs;
    }

    public void setPdwlgs(String pdwlgs) {
        this.pdwlgs = pdwlgs;
    }

    public String getJdTime() {
        return jdTime;
    }

    public void setJdTime(String jdTime) {
        this.jdTime = jdTime;
    }

    public String getMoxid() {
        return moxid;
    }

    public void setMoxid(String moxid) {
        this.moxid = moxid;
    }

    public String getYf_detain() {
        return yf_detain;
    }

    public void setYf_detain(String yf_detain) {
        this.yf_detain = yf_detain;
    }

    public String getYf_difference() {
        return yf_difference;
    }

    public void setYf_difference(String yf_difference) {
        this.yf_difference = yf_difference;
    }

    public String getYf_remark() {
        return yf_remark;
    }

    public void setYf_remark(String yf_remark) {
        this.yf_remark = yf_remark;
    }

    public String getPolicyNo() {
        return policyNo;
    }

    public void setPolicyNo(String policyNo) {
        this.policyNo = policyNo;
    }

    public boolean isOfwlgsinfo() {
        return isOfwlgsinfo;
    }

    public void setOfwlgsinfo(boolean ofwlgsinfo) {
        isOfwlgsinfo = ofwlgsinfo;
    }

    public String getDriver_score() {
        return driver_score;
    }

    public void setDriver_score(String driver_score) {
        this.driver_score = driver_score;
    }

    public String getDriver_appraise() {
        return driver_appraise;
    }

    public void setDriver_appraise(String driver_appraise) {
        this.driver_appraise = driver_appraise;
    }

    public String getAlctCode() {
        return alctCode;
    }

    public void setAlctCode(String alctCode) {
        this.alctCode = alctCode;
    }

    public String getAlctId() {
        return alctId;
    }

    public void setAlctId(String alctId) {
        this.alctId = alctId;
    }

    public String getAlctKey() {
        return alctKey;
    }

    public void setAlctKey(String alctKey) {
        this.alctKey = alctKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
        this.driver = driver;
    }

    public String getHuozhu() {
        return huozhu;
    }

    public void setHuozhu(String huozhu) {
        this.huozhu = huozhu;
    }

    public String getLon_to() {
        return lon_to;
    }

    public void setLon_to(String lon_to) {
        this.lon_to = lon_to;
    }

    public String getLat_to() {
        return lat_to;
    }

    public void setLat_to(String lat_to) {
        this.lat_to = lat_to;
    }

    public String getLon_from() {
        return lon_from;
    }

    public void setLon_from(String lon_from) {
        this.lon_from = lon_from;
    }

    public String getLat_from() {
        return lat_from;
    }

    public void setLat_from(String lat_from) {
        this.lat_from = lat_from;
    }

    public boolean isGuiding() {
        return isGuiding;
    }

    public void setGuiding(boolean guiding) {
        isGuiding = guiding;
    }

    public String getStatusName() {
        return statusName;
    }

    public void setStatusName(String statusName) {
        this.statusName = statusName;
    }

    public String getShrName() {
        return shrName;
    }

    public void setShrName(String shrName) {
        this.shrName = shrName;
    }

    public String getShrTel() {
        return shrTel;
    }

    public void setShrTel(String shrTel) {
        this.shrTel = shrTel;
    }


    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


    public String getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(String startAddress) {
        this.startAddress = startAddress;
    }

    public String getEndAddress() {
        return endAddress;
    }

    public void setEndAddress(String endAddress) {
        this.endAddress = endAddress;
    }

    public String getSid() {
        return sid;
    }

    public void setSid(String sid) {
        this.sid = sid;
    }

    public String getRowid() {
        return rowid;
    }

    public void setRowid(String rowid) {
        this.rowid = rowid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getVolume() {
        return volume;
    }

    public void setVolume(String volume) {
        this.volume = volume;
    }

    public String getBz() {
        return bz;
    }

    public void setBz(String bz) {
        this.bz = bz;
    }

    public String getHzxm() {
        return hzxm;
    }

    public void setHzxm(String hzxm) {
        this.hzxm = hzxm;
    }

    public String getZyf() {
        return zyf;
    }

    public void setZyf(String zyf) {
        this.zyf = zyf;
    }

    public String getBl() {
        return bl;
    }

    public void setBl(String bl) {
        this.bl = bl;
    }

    @Override
    public String toString() {
        return "GoodsEntity{" +
                "startAddress='" + startAddress + '\'' +
                ", endAddress='" + endAddress + '\'' +
                ", sid='" + sid + '\'' +
                ", rowid='" + rowid + '\'' +
                ", name='" + name + '\'' +
                ", weight='" + weight + '\'' +
                ", volume='" + volume + '\'' +
                ", bz='" + bz + '\'' +
                ", hzxm='" + hzxm + '\'' +
                ", zyf='" + zyf + '\'' +
                ", bl='" + bl + '\'' +
                ", time='" + time + '\'' +
                ", shrName='" + shrName + '\'' +
                ", shrTel='" + shrTel + '\'' +
                ", statusName='" + statusName + '\'' +
                ", lon_to='" + lon_to + '\'' +
                ", lat_to='" + lat_to + '\'' +
                ", lon_from='" + lon_from + '\'' +
                ", lat_from='" + lat_from + '\'' +
                ", status='" + status + '\'' +
                ", driver='" + driver + '\'' +
                ", huozhu='" + huozhu + '\'' +
                ", alctCode='" + alctCode + '\'' +
                ", alctId='" + alctId + '\'' +
                ", alctKey='" + alctKey + '\'' +
                ", driver_score='" + driver_score + '\'' +
                ", isOfwlgsinfo=" + isOfwlgsinfo +
                ", policyNo='" + policyNo + '\'' +
                ", yf_detain='" + yf_detain + '\'' +
                ", yf_difference='" + yf_difference + '\'' +
                ", yf_remark='" + yf_remark + '\'' +
                ", driver_appraise='" + driver_appraise + '\'' +
                ", moxid='" + moxid + '\'' +
                ", isGuiding=" + isGuiding +
                '}';
    }
}
