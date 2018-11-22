package com.tuoying.hykc.entity;

/**
 * Created by Administrator on 2018/3/21.
 */

public class EventEntity {
    public String type;
    public String value;
    public EventEntity(){

    }
    public EventEntity(String type){
        this.type=type;;
    }
    public EventEntity(String type, String value){
        this.type=type;
        this.value=value;
    }



}
