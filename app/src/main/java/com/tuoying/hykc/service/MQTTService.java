package com.tuoying.hykc.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.entity.EventEntity;
import com.tuoying.hykc.utils.NotificationUtils;
import com.tuoying.hykc.utils.RxBus;
import com.tuoying.hykc.utils.SharePreferenceUtil;

import personalmqttofw.MyMessage;
import personalmqttofw.MyMqttClient;
import personalmqttofw.MyMqttListener;


public class MQTTService extends Service {
    private MyMqttClient client;
    private String mqtturl = null;
    private String rowid = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        init();
        return super.onStartCommand(intent, flags, startId);
    }

    private void init() {
        String userid = SharePreferenceUtil.getInstance(this).getUserId();
        rowid = "newmqttUSER-" + userid + "-" + Constants.AppId;
        mqtturl = SharePreferenceUtil.getInstance(this).getMqttUrl();
        if (!TextUtils.isEmpty(mqtturl)) {
            client = new MyMqttClient(mqtturl, rowid, "hykcmqttclient", new MqttListener());


        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onDestroy() {
        if (client != null) {
            client.disConnect();
        }
        super.onDestroy();
        Log.e("disConnect", "disConnect");

    }

    class MqttListener implements MyMqttListener {

        @Override
        public void sendFailed(String s) {

        }

        @Override
        public void sendSuccess(String s) {

        }

        @Override
        public void getChatMessage(MyMessage myMessage) {

        }

        @Override
        public void getHeartMessage(MyMessage myMessage) {

        }

        @Override
        public void getBrocastMessage(MyMessage myMessage) {

        }

        @Override
        public void getAnswer(MyMessage myMessage) {

        }

        @Override
        public void getWayBill(MyMessage myMessage) {
            RxBus.getInstance().send(new EventEntity("waybill", myMessage.getMessageInfo()));
            Log.e("getWayBill", myMessage.getMessageInfo());
        }

        @Override
        public void getNotice(MyMessage myMessage) {
            Log.e("getNotice", myMessage.getMessageInfo());
            RxBus.getInstance().send(new EventEntity("notice", myMessage.getMessageInfo()));
        }

        @Override
        public void lostConnect() {
            Log.e("lostConnect", "MQTT断开连接！");
            RxBus.getInstance().send(new EventEntity("reconnect_timeout", "reconnect_timeout"));
        }

        @Override
        public void getConnect() {
            Log.e("getConnect", "MQTT已连接！");
        }
    }

}
