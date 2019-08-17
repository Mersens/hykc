package com.tuoying.hykc.entity;

import com.webank.mbank.wehttp.WeOkHttp;
import com.webank.mbank.wehttp.WeReq;
import com.webank.normal.net.BaseResponse;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class GetFaceId {

    public static class GetFaceIdParam {
        public String webankAppId;
        public String orderNo;
        public String name;
        public String idNo;
        public String userId;
        public String sourcePhotoStr;
        public String sourcePhotoType;
        public String version = "1.0.0";
        public String sign;

        public String toJson() {
            Map<String, String> map = new HashMap<>();
            map.put("webankAppId", webankAppId);
            map.put("orderNo", orderNo);//设备信息（IOS/Android）
            map.put("name", name);//接口版本号，默认1.0.0
            map.put("idNo", idNo);//	订单号，由合作方上送，每次唯一
            map.put("userId", userId);//faceId
            map.put("sourcePhotoStr", sourcePhotoStr);
            map.put("sourcePhotoType", sourcePhotoType);
            map.put("version", version);
            map.put("sign", sign);
            JSONObject json = new JSONObject(map);
            return json.toString();
        }
    }

    public static class Result implements Serializable {
        public String bizSeqNo;  //openApi给的业务流水号
        public String orderNo;  //合作方上送的订单号
        public String faceId;  //32位唯一标识
    }

    public static class GetFaceIdResponse extends BaseResponse<Result> {

    }

    public static void requestExec(WeOkHttp myOkHttp, String url, GetFaceIdParam param,
                                   WeReq.WeCallback<GetFaceIdResponse> callback) {
        myOkHttp.<GetFaceIdResponse>post(url).body(param).execute(GetFaceIdResponse.class, callback);
    }


}
