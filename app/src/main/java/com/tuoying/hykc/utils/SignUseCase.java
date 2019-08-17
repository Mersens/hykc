package com.tuoying.hykc.utils;

import android.util.Log;

import com.webank.mbank.wehttp.WeLog;
import com.webank.mbank.wehttp.WeOkHttp;
import com.webank.mbank.wehttp.WeReq;

import java.io.IOException;
import java.io.Serializable;
public class SignUseCase {
    private static final String TAG = "SignUseCase";
    private boolean isDesensitization;
    private WeOkHttp myOkHttp = new WeOkHttp();
    public SignUseCase() {
        initHttp();
    }

    private void initHttp() {
        //拿到OkHttp的配置对象进行配置
        //WeHttp封装的配置
        myOkHttp.config()
                //配置超时,单位:s
                .timeout(20, 20, 20)
                //添加PIN
                .log(WeLog.Level.BODY);
    }
    public void setDesensitization(boolean desensitization) {
        isDesensitization = desensitization;
    }
    public void execute(final String mode, String appId, String userId, String nonce,final OnSignListener listener) {
        final String url = getUrl(appId, userId, nonce);

        requestExec(url, new WeReq.WeCallback<SignResponse>() {
            @Override
            public void onStart(WeReq weReq) {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onFailed(WeReq weReq, int i, int i1, String s, IOException e) {
                if(listener!=null){
                    listener.onError(i1, s);
                }
            }

            @Override
            public void onSuccess(WeReq weReq, SignResponse signResponse) {
                if (signResponse != null) {
                    String sign = signResponse.sign;
                    if(listener!=null){
                        listener.onSuccess(mode, sign);
                    }
                } else {
                    if(listener!=null){
                        listener.onError(-100, "signResponse is null");
                    }
                }
            }
        });
    }
    public static class SignResponse implements Serializable {
        public String sign;     //签名
    }

    public void requestExec(String url, WeReq.WeCallback<SignResponse> callback) {
        myOkHttp.<SignResponse>get(url).execute(SignResponse.class, callback);
    }

    private String getUrl(String appId, String userId, String nonce) {
        final String s = "https://ida.webank.com/" + "/ems-partner/cert/signature?appid=" + appId + "&nonce=" + nonce + "&userid=" + userId;
        Log.d(TAG, "get sign url=" + s);
        return s;
    }


    public interface OnSignListener{
        void onSuccess(String mode, String sign);
        void onError(int code,String msg);
    }

}
