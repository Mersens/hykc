package com.tuoying.hykc.entity;

import java.io.Serializable;

public class MsgEntity implements Serializable{
    public static final String MSG="MSG";
    public static final String TIME="TIME";
    public static final String TABLE_NAME="TABLE_NAME";
    private String msg;
    private String time;
    private String id;
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }


}
