package com.tuoying.hykc.entity;

import java.io.Serializable;

public class FuelsEntity implements Serializable {
    private String sf_id;
    private String fuel_name;//油品名称
    private String status;//0正常使用1停用
    private String price;
    private String guide_price;//发改委价格（分
    public String getSf_id() {
        return sf_id;
    }

    public void setSf_id(String sf_id) {
        this.sf_id = sf_id;
    }

    public String getFuel_name() {
        return fuel_name;
    }

    public void setFuel_name(String fuel_name) {
        this.fuel_name = fuel_name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getGuide_price() {
        return guide_price;
    }

    public void setGuide_price(String guide_price) {
        this.guide_price = guide_price;
    }


    @Override
    public String toString() {
        return "FuelsEntity{" +
                "sf_id='" + sf_id + '\'' +
                ", fuel_name='" + fuel_name + '\'' +
                ", status='" + status + '\'' +
                ", price='" + price + '\'' +
                ", guide_price='" + guide_price + '\'' +
                '}';
    }
}
