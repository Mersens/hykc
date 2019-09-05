package com.tuoying.hykc.utils;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.tuoying.hykc.app.App;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.entity.GetFaceId;

import com.webank.faceaction.tools.ErrorCode;
import com.webank.faceaction.tools.WbCloudFaceVerifySdk;
import com.webank.faceaction.ui.FaceVerifyStatus;
import com.webank.mbank.wehttp.WeLog;
import com.webank.mbank.wehttp.WeOkHttp;
import com.webank.mbank.wehttp.WeReq;

import java.io.IOException;

public class WbCoudFaceManager {
    private static final String TAG = "WbCoudFaceManager";
    public static final String DATA_MODE_MID = "data_mode_mid";
    public static final String DATA_MODE_DESENSITIZATION = "data_mode_desensitization";
    private String userId = "";
    private String nonce = "";
    private  String order = "";
    private  String appId = "";
    private String id;
    private String name;
    private String sign;
    private SignUseCase signUseCase=new SignUseCase();
    private WeOkHttp myOkHttp = new WeOkHttp();
    OnFaceListener listener;
    private Context context;

    public WbCoudFaceManager(String userId,
                             String nonce,
                             String order,
                             String appId,
                             String id,
                             String name,
                             String sign,
                             Context context
                             ){
        this.userId=userId;
        this.nonce=nonce;
        this.order=order;
        this.appId=appId;
        this.id=id;
        this.name=name;
        this.sign=sign;
        this.context=context;
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
    public void execute(){
        getFaceid(sign);
    }

    private void getFaceid(final String sign) {
        String url = "https://idasc.webank.com/api/server/getfaceid";
        Log.d(TAG, "get faceId url=" + url);
        GetFaceId.GetFaceIdParam param = new GetFaceId.GetFaceIdParam();
        param.orderNo = order;
        param.webankAppId = appId;
        param.version = "1.0.0";
        param.userId = userId;
        param.sign = sign;
        param.name = name;
        param.idNo = id;
        Log.d(TAG, "GetFaceId params=" + param.toJson());
        GetFaceId.requestExec(myOkHttp, url, param, new WeReq.WeCallback<GetFaceId.GetFaceIdResponse>() {
            @Override
            public void onStart(WeReq weReq) {

            }

            @Override
            public void onFinish() {

            }

            @Override
            public void onFailed(WeReq weReq, int i, int code, String message, IOException e) {
                Log.d(TAG, "faceId请求失败:code=" + code + ",message=" + message);
                if(listener!=null){
                    listener.onFail("faceId请求失败:code=" + code + ",message=" + message);
                }
            }

            @Override
            public void onSuccess(WeReq weReq, GetFaceId.GetFaceIdResponse getFaceIdResponse) {
                if (getFaceIdResponse != null) {
                    String code = getFaceIdResponse.code;
                    if (code.equals("0")) {
                        GetFaceId.Result result = getFaceIdResponse.result;
                        if (result != null) {
                            String faceId = result.faceId;
                            if (!TextUtils.isEmpty(faceId)) {
                                Log.d(TAG, "faceId请求成功:" + faceId);
                                openCloudFaceService(appId, order, sign, faceId);
                            } else {
                                if(listener!=null){
                                    listener.onFail("faceId为空");
                                }
                                Log.e(TAG, "faceId为空");
                            }
                        } else {
                            if(listener!=null){
                                listener.onFail("faceId请求失败:getFaceIdResponse result is null.");
                            }
                            Log.e(TAG, "faceId请求失败:getFaceIdResponse result is null.");
                        }
                    } else {
                        if(listener!=null){
                            listener.onFail("\"faceId请求失败:code=\" + code + \"msg=\" + getFaceIdResponse.msg");
                        }
                        Log.e(TAG, "faceId请求失败:code=" + code + "msg=" + getFaceIdResponse.msg);
                    }
                } else {
                    if(listener!=null){
                        listener.onFail("faceId请求失败:getFaceIdResponse is null.");
                    }
                    Log.e(TAG, "faceId请求失败:getFaceIdResponse is null.");
                }
            }
        });
    }

    public void openCloudFaceService(String appId, String order, String sign, String faceId) {
        Bundle data = new Bundle();
        WbCloudFaceVerifySdk.InputData inputData = new WbCloudFaceVerifySdk.InputData(
                faceId,
                order,
                "ip=xxx.xxx.xxx.xxx",
                "lgt=xxx,xxx;lat=xxx.xxx",
                appId,
                "1.0.0",
                nonce,
                userId,
                sign,
                FaceVerifyStatus.Mode.MIDDLE,
                Constants.FACE_LICENSE
                );

        data.putSerializable(WbCloudFaceVerifySdk.INPUT_DATA, inputData);
        //是否展示刷脸成功页面，默认展示
        data.putBoolean(WbCloudFaceVerifySdk.SHOW_SUCCESS_PAGE, true);
        //是否展示刷脸失败页面，默认展示
        data.putBoolean(WbCloudFaceVerifySdk.SHOW_FAIL_PAGE, true);
        //颜色设置
        data.putString(WbCloudFaceVerifySdk.COLOR_MODE, WbCloudFaceVerifySdk.BLACK);
        //是否需要录制视频存证
        data.putBoolean(WbCloudFaceVerifySdk.RECORD_VIDEO, true);
        //是否对录制视频进行检查,默认不检查
//        data.putBoolean(WbCloudFaceVerifySdk.VIDEO_CHECK, true);
        //比较模式设定  身份证比对/自带图片比对/不比对
        //默认身份证对比


        WbCloudFaceVerifySdk.getInstance().initSdk(context, data, new WbCloudFaceVerifySdk.FaceVerifyLoginListener() {
            @Override
            public void onLoginSuccess() {
                Log.i(TAG, "initSdk onLoginSuccess");

                WbCloudFaceVerifySdk.getInstance().startFaceVerifyActivity(context, new WbCloudFaceVerifySdk.FaceVerifyResultForSecureListener() {
                    @Override
                    public void onFinish(int resultCode, boolean nextShowGuide, String faceCode, String faceMsg, String sign, Bundle extendData) {
                        String liveRate = null;
                        String similarity = null;
                        String userImage = null;
                        if (extendData != null) {
                            liveRate = extendData.getString(WbCloudFaceVerifySdk.FACE_RESULT_LIVE_RATE);
                            similarity = extendData.getString(WbCloudFaceVerifySdk.FACE_RESULT_SIMILIRATY);
                            userImage = extendData.getString(WbCloudFaceVerifySdk.FACE_RESULT_USER_IMG);
                        }
                        if (resultCode == 0) {
                            Log.d(TAG, "刷脸成功！可以拿到刷脸照片！errorCode=" + resultCode + " ;faceCode=" + faceCode + "; faceMsg=" + faceMsg
                                    + "; Sign=" + sign + "; liveRate=" + liveRate + "; similarity=" + similarity + "; userImg=" + userImage);
                            if(listener!=null){
                                listener.onSucccess();
                            }
                        } else {
                            Log.d(TAG, "刷脸失败！errorCode=" + resultCode + " ;faceCode=" + faceCode + "; faceMsg=" + faceMsg + "; Sign=" + sign);
                            if (resultCode == ErrorCode.FACEVERIFY_ERROR_DEFAULT) {
                                Log.d(TAG, "后台对比失败! liveRate=" + liveRate + "; similarity=" + similarity);
                                if (faceCode.equals("66660004")) {
                                    if(listener!=null){
                                        listener.onFail("但是还是可以拿到刷脸图片！");
                                    }
                                    Log.d(TAG, "但是还是可以拿到刷脸图片!");
                                } else {
                                    if(listener!=null){
                                        listener.onFail("拿不到刷脸图片！");
                                    }
                                    Log.d(TAG, "拿不到刷脸图片！");
                                }
                            } else {
                                if(listener!=null){
                                    listener.onFail("前端失败！");
                                }
                                Log.d(TAG, "前端失败！");
                            }
                        }
                        //测试用代码
                        //不管刷脸成功失败，只要结束了，自带对比和活体检测都更新userId
                    }
                });
            }

            @Override
            public void onLoginFailed(String errorCode, String errorMsg) {
                Log.i(TAG, "onLoginFailed!");
                if (errorCode.equals(ErrorCode.FACEVERIFY_LOGIN_PARAMETER_ERROR)) {
                    Log.d(TAG, "传入参数有误！" + errorMsg);
                    if(listener!=null){
                        listener.onFail("传入参数有误！" + errorMsg);
                    }
                } else {
                    if(listener!=null){
                        listener.onFail("登录刷脸sdk失败！" + "errorCode= " + errorCode + " ;errorMsg=" + errorMsg);
                    }
                    Log.d(TAG, "登录刷脸sdk失败！" + "errorCode= " + errorCode + " ;errorMsg=" + errorMsg);
                }
            }
        });
    }

    public void setOnFaceListener(OnFaceListener listener){
        this.listener=listener;
    }

    public interface  OnFaceListener{
        void onSucccess();
        void onFail(String msg);
    }

}
