package com.tuoying.hykc.service;

import com.tuoying.hykc.app.Constants;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Created by Mersens on 2016/9/28.
 */

public interface ServiceStore {

    @FormUrlEncoded
    @POST("ds/opers.execLoginNew")
    Observable<ResponseBody> login(@Field("mobile") String mobile, @Field("pwd") String pwd, @Field("app") String app);

    @GET("ds/opers.regMobileUserNew")
    Observable<ResponseBody> reg(@Query("mobile") String mobile, @Query("newpwd") String newpwd, @Query("sms") String sms, @Query("app") String app);

    @GET("ds/opers.execResetPwdNew")
    Observable<ResponseBody> resetPsd(@Query("mobile") String mobile, @Query("newpwd") String newpwd, @Query("sms") String sms, @Query("app") String app);

    @FormUrlEncoded
    @POST("files/load_info_new.jsp")
    Observable<ResponseBody> findMyRz(@Field("token") String token, @Field("mobile") String mobile, @Field("app") String app);

    @FormUrlEncoded
    @POST("ds/opers.getSmsNew")
    Observable<ResponseBody> getCode(@Field("mobile") String mobile, @Field("cat") String cat);

    @FormUrlEncoded
    @POST("ds/opers.updateAccountInfoNew")
    Observable<ResponseBody> uploadCardInfo(@Field("rowid") String rowid, @Field("token") String token, @Field("mobile") String mobile, @Field("app") String app, @Field("arg") String arg);

    @FormUrlEncoded
    @POST("files/find_yd_bystatus_new.jsp")
    Observable<ResponseBody> findOrderInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("ds/opers.deleteAccountNew")
    Observable<ResponseBody> deleteAccount(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("files/create_request_new.jsp")
    Observable<ResponseBody> create_request(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("files/load_acct_detail_new.jsp")
    Observable<ResponseBody> load_acct_detail(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("files/create_order_new.jsp")
    Observable<ResponseBody> create_order(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("files/set_pwd_new.jsp")
    Observable<ResponseBody> set_pwd(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("files/query_sources_new.jsp")
    Observable<ResponseBody> query_sources(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("files/create_yd_new.jsp")
    Observable<ResponseBody> create_yd(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("files/get_source_detail_new.jsp")
    Observable<ResponseBody> get_source_detail(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("files/submit_order_new.jsp")
    Observable<ResponseBody> submit_order(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("files/change_yd_status_new.jsp")
    Observable<ResponseBody> change_yd_status(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("files/cancel_yd_new.jsp")
    Observable<ResponseBody> cancel_yd(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("files/loadmqttmessage.jsp")
    Observable<ResponseBody> loadmqttmessage(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("files/create_order_new.jsp")
    //钱包微信充值
    Observable<ResponseBody> createWXPayOrder(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("showdata/alct/alctyydtb.jsp")
    Observable<ResponseBody> alctyydtb(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("showdata/alct/alctErrorLog.jsp")
    Observable<ResponseBody> uplaodErrorInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("showdata/uploadalctimages.jsp")
    Observable<ResponseBody> uoload(@FieldMap(encoded = true) Map<String, String> params);

    @FormUrlEncoded
    @POST("searchversion.jsp")
    Observable<ResponseBody> checkVerson(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("files/save_appraise_new.jsp")
    Observable<ResponseBody> save_appraise(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("files/create_bdxx_new.jsp")
    Observable<ResponseBody> create_bdxx(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("files/image_upload_new.jsp")
    Observable<ResponseBody> image_upload_new(@FieldMap Map<String, String> params);

    //测试接口
    @FormUrlEncoded
    @POST("showdata/mox/pickup.jsp")
    Observable<ResponseBody> pickup(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("showdata/mox/sendmoxlocation.jsp")
    Observable<ResponseBody> getLocation(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("files/loadrzrequest_new.jsp")
    Observable<ResponseBody> loadRzInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST(Constants.UPLOADE_URL)
    Observable<ResponseBody> upLoadImg(@FieldMap(encoded = true) Map<String, String> params);

    @FormUrlEncoded
    @POST("files/findphoto_new.jsp")
    Observable<ResponseBody> findphoto(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("showdata/alct/log/uplog3.jsp")
    Observable<ResponseBody> uplog(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("showdata/mox/checkPickupOk.jsp")
    Observable<ResponseBody> checkPickupOk(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("/acceptBillOfTask.jsp")
    Observable<ResponseBody> acceptBillOfTask(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("showdata/alct/updata_yd_upalctimg.jsp")
    Observable<ResponseBody> updata_yd_upalctimg(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/selectTrailerInfo")
    Call<ResponseBody> selectTrailerInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/addTrailerInfo")
    Call<ResponseBody> addTrailerInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/delTrailerInfo")
    Call<ResponseBody> delTrailerInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/getDirverInfo")
    Call<ResponseBody> getDirverInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("app/selectCarInfoByDriverId")
    Call<ResponseBody> selectCarInfoByDriverId(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("etc/start/")
    Call<ResponseBody> submitOrderEctInfoStart(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("etc/end/")
    Call<ResponseBody> submitOrderEctInfoEnd(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("bestsign/autoSignAgre/")
    Call<ResponseBody> autoSignAgre(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("oil/queryDriverMoney/")
    Call<ResponseBody> queryDriverMoney(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("oil/getPayCode/")
    Call<ResponseBody> getPayCode(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("oil/getStationAndFuels/")
    Call<ResponseBody> getStationAndFuels(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("bestsign/selectUDriverIsFaceTest/")
    Call<ResponseBody> selectUDriverIsFaceTest(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("bestsign/idcardFaceVerify/")
    Call<ResponseBody> idcardFaceVerify(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("bestsign/addDriverSignInfo/")
    Call<ResponseBody> addDriverSignInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("bestsign/checkAgreByRowid/")
    Call<ResponseBody> checkAgreByRowid(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("sugg/addSuggestionInfo/")
    Call<ResponseBody> addSuggestionInfo(@FieldMap Map<String, String> params);

    @FormUrlEncoded
    @POST("sugg/uploadSuggImg/")
    Call<ResponseBody> uploadSuggImg(@FieldMap Map<String, String> params);


    @GET
    Observable<ResponseBody> download(@Url String fileUrl);

}
