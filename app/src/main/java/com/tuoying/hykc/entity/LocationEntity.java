package com.tuoying.hykc.entity;

public class LocationEntity {
    public static final String TABLE_NAME="ORDER_LOCATION";
    public static final String ROWID="_rowid";
    public static final String LOCATION="location";
    private String rowid;
    private String location;

    public String getRowid() {
        return rowid;
    }

    public void setRowid(String rowid) {
        this.rowid = rowid;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }




}
