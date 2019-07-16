package com.tuoying.hykc.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.tuoying.hykc.R;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.UCarEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.service.ServiceStore;
import com.tuoying.hykc.utils.FileUtil;
import com.tuoying.hykc.utils.RecognizeService;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.ExitDialogFragment;
import com.tuoying.hykc.view.LoadingDialogFragment;


import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MyTrailerDetailsActivity extends BaseActivity {
    private static final int REQUEST_CODE_CAMERA = 102;
    private static final int REQUEST_CODE_VEHICLE_LICENSE = 120;
    private static final int REQUEST_CODE_DRIVING_LICENSE = 121;
    private Toolbar mToolbar;
    private EditText mEditTel;
    private EditText mEditsyr;
    private EditText mEditppxh;
    private EditText mEditcph;
    private EditText mEditcllx;
    private EditText mEditsbm;
    private EditText mEditzz;
    private EditText mEditsyxz;
    private EditText mEditNfcid;
    private Button mBtnNext;
    private TextView mTextSB;
    private ImageView mImg;
    private UCarEntity uCarEntity ;
    private String id;
    private boolean hasGotToken = false;
    private View viewLin;
    private Button btn_dj;
    private TextView mTextRightTitle;
    private DBDao dao;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_add_carinfo);
        dao=new DBDaoImpl(this);
        init();
    }

    @Override
    public void init() {
        uCarEntity= (UCarEntity) getIntent().getSerializableExtra("entity");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("挂车详情信息");
        mTextRightTitle=mToolbar.findViewById(R.id.tv_right_title);
/*        mTextRightTitle.setVisibility(View.VISIBLE);
        mTextRightTitle.setText("解绑");*/
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        initViews();
        initDatas();
        initAccessToken();
        initEvent();
    }

    private boolean checkTokenStatus() {
        if (!hasGotToken) {
            Toast.makeText(getApplicationContext(), "token还未成功获取", Toast.LENGTH_LONG).show();
        }
        return hasGotToken;
    }

    private void initAccessToken() {
        OCR.getInstance(this).initAccessToken(new OnResultListener<AccessToken>() {
            @Override
            public void onResult(AccessToken accessToken) {
                String token = accessToken.getAccessToken();
                hasGotToken = true;
            }

            @Override
            public void onError(OCRError error) {
                error.printStackTrace();
            }
        }, getApplicationContext());
    }

    private void initViews() {
        mImg=findViewById(R.id.img);
        mTextSB=findViewById(R.id.tv_sb);
        mBtnNext = findViewById(R.id.btn_next);
        mBtnNext.setText("修改");
        int statu=uCarEntity.getStatus();
        if(statu!=1){
            mBtnNext.setVisibility(View.VISIBLE);
            mTextSB.setVisibility(View.VISIBLE);
            mImg.setVisibility(View.VISIBLE);
        }else {
            mBtnNext.setVisibility(View.GONE);
            mTextSB.setVisibility(View.GONE);
            mImg.setVisibility(View.GONE);
        }

        mEditTel = findViewById(R.id.editTel);

        mEditsyr = findViewById(R.id.editSYR);
        mEditppxh = findViewById(R.id.editppxh);
        mEditcph = findViewById(R.id.editCph);
        mEditcllx = findViewById(R.id.editCllx);
        mEditsbm=findViewById(R.id.editClsbm);
        mEditzz=findViewById(R.id.editZz);
        mEditsyxz=findViewById(R.id.editSyxz);
        mEditNfcid=findViewById(R.id.editNFCid);


        viewLin=findViewById(R.id.view_lin);
        viewLin.setVisibility(View.GONE);
        btn_dj=findViewById(R.id.btn_dj);
        btn_dj.setVisibility(View.GONE);
    }

    private void initDatas() {
        id=SharePreferenceUtil.getInstance(this).getUserId();
        if(TextUtils.isEmpty(id) || uCarEntity==null){
            Toast.makeText(this, "用户信息为空，重新登录", Toast.LENGTH_SHORT).show();
            return;
        }
        User user=dao.findUserInfoById(id);
        if(user==null){
            Toast.makeText(this, "请重新登录！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(!TextUtils.isEmpty(id)){
            mEditTel.setText(id);
        }
        setDatas();

    }


    private void setDatas() {
        mEditTel.setText(uCarEntity.getMobile());
        mEditsyr.setText(uCarEntity.getOwner_p());
        mEditppxh.setText(uCarEntity.getBrand());
        mEditcph.setText(uCarEntity.getLicensePlateNo());
        mEditcllx.setText(uCarEntity.getStandardVehicleType());
        mEditsbm.setText(uCarEntity.getVehicleIdentityCode());
        mEditzz.setText(uCarEntity.getLoad_p());
        mEditsyxz.setText(uCarEntity.getUsage_p());
        mEditNfcid.setText(uCarEntity.getNfcId());
    }

    private void initEvent() {

        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntentDatas();

            }
        });
        btn_dj.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MyTrailerDetailsActivity.this, "登记", Toast.LENGTH_SHORT).show();
            }
        });
        mTextSB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(MyTrailerDetailsActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_VEHICLE_LICENSE);
            }
        });
        mTextRightTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDelView("确定解除挂车？");
            }
        });
    }



    private void  showDelView(String msg){
        final ExitDialogFragment dialogFragment=ExitDialogFragment.getInstance(msg);
        dialogFragment.show(getSupportFragmentManager(),"delView");
        dialogFragment.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialogFragment.dismissAllowingStateLoss();
            }

            @Override
            public void onClickOk() {
                dialogFragment.dismissAllowingStateLoss();
                doDel();
            }
        });
    }

    private void doDel(){
        Map<String,String> map=new HashMap<>();
        map.put("licensePlateNo",uCarEntity.getLicensePlateNo());
        map.put("mobile",uCarEntity.getMobile());
        map.put("cartype","2");
        final LoadingDialogFragment dialogFragment = LoadingDialogFragment.getInstance();
        dialogFragment.showF(getSupportFragmentManager(), "delcarinfo");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.TRAILERINFO_URL).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call = serviceStore.delTrailerInfo(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialogFragment.dismissAllowingStateLoss();
                ResponseBody body=response.body();
                try {
                    String str= body.string();
                    try {
                        JSONObject object=new JSONObject(str);
                        if(object.getBoolean("success")){
                            Toast.makeText(MyTrailerDetailsActivity.this, "解绑成功", Toast.LENGTH_SHORT).show();
                            finish();
                            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                        }else {
                            String errorMsg=object.getString("msg");
                            Toast.makeText(MyTrailerDetailsActivity.this, "解绑失败！"+errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("onResponse","onResponse=="+str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure","onFailure=="+t.getMessage());
                Toast.makeText(MyTrailerDetailsActivity.this, "解绑失败！"+t.getMessage(), Toast.LENGTH_SHORT).show();
                dialogFragment.dismissAllowingStateLoss();
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CAMERA && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                String contentType = data.getStringExtra(CameraActivity.KEY_CONTENT_TYPE);
                String filePath = FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath();
                if (!TextUtils.isEmpty(contentType)) {
                    if (CameraActivity.CONTENT_TYPE_ID_CARD_FRONT.equals(contentType)) {
                        //recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
                    } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
                        //recIDCardBack(IDCardParams.ID_CARD_SIDE_BACK, filePath);
                    }
                }
            }
        }
        // 识别成功回调，行驶证识别
        else if (requestCode == REQUEST_CODE_VEHICLE_LICENSE && resultCode == Activity.RESULT_OK) {
            RecognizeService.recVehicleLicense(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {

                            Log.e("recVehicleLicense","==="+result);

                            if(!TextUtils.isEmpty(result)){
                                try {
                                    JSONObject jsonObject=new JSONObject(result);
                                    JSONObject object=new JSONObject(jsonObject.getString("words_result"));
                                    if(object.has("号牌号码")){
                                        JSONObject object1=new JSONObject(object.getString("号牌号码"));
                                        String cph=object1.getString("words");
                                        if(!TextUtils.isEmpty(cph)){
                                            mEditcph.setText(cph);

                                        }
                                    }
                                    if(object.has("车辆识别代号")){
                                        JSONObject object2=new JSONObject(object.getString("车辆识别代号"));
                                        String clsbm=object2.getString("words");
                                        if(!TextUtils.isEmpty(clsbm)){
                                            mEditsbm.setText(clsbm);

                                        }
                                    }

                                    if(object.has("所有人")){
                                        JSONObject object2=new JSONObject(object.getString("所有人"));
                                        String syr=object2.getString("words");
                                        if(!TextUtils.isEmpty(syr)){
                                            mEditsyr.setText(syr);
                                        }
                                    }
                                    if(object.has("品牌型号")){
                                        JSONObject object2=new JSONObject(object.getString("品牌型号"));
                                        String ppxh=object2.getString("words");
                                        if(!TextUtils.isEmpty(ppxh)){
                                            mEditppxh.setText(ppxh);
                                        }
                                    }
                                    if(object.has("车辆类型")){
                                        JSONObject object2=new JSONObject(object.getString("车辆类型"));
                                        String cllx=object2.getString("words");
                                        if(!TextUtils.isEmpty(cllx)){
                                            mEditcllx.setText(cllx);
                                        }
                                    }
                                    if(object.has("使用性质")){
                                        JSONObject object2=new JSONObject(object.getString("使用性质"));
                                        String syxz=object2.getString("words");
                                        if(!TextUtils.isEmpty(syxz)){
                                            mEditsyxz.setText(syxz);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    });
        }

        // 识别成功回调，驾驶证识别
        else if (requestCode == REQUEST_CODE_DRIVING_LICENSE && resultCode == Activity.RESULT_OK) {

        }
    }

    private void setIntentDatas() {
        final String tel = mEditTel.getText().toString().trim();
        final String syr = mEditsyr.getText().toString().trim();
        final String ppxh = mEditppxh.getText().toString().trim();
        final String cph = mEditcph.getText().toString().trim();
        final String cllx = mEditcllx.getText().toString().trim();
        final String sbm = mEditsbm.getText().toString().trim();
        final String zz=mEditzz.getText().toString().trim();
        String syxz=mEditsyxz.getText().toString();
        String nfcid=mEditNfcid.getText().toString().trim();

        if (TextUtils.isEmpty(tel)) {
            Toast.makeText(this, "手机号不能为空！", Toast.LENGTH_SHORT).show();
            return;

        }
        if (tel.length()!=11) {
            Toast.makeText(this, "请输入正确手机号！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(cph)) {
            Toast.makeText(this, "车牌号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if(cph.length()!=7){
            Toast.makeText(this, "请输入正确车牌号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(zz)) {
            Toast.makeText(this, "车辆载重不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(ppxh)){
            Toast.makeText(this, "品牌型号不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(syr)) {
            Toast.makeText(this, "所有人不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(cllx)) {
            Toast.makeText(this, "车辆类型不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(sbm)){
            Toast.makeText(this, "请输入车辆识别码！", Toast.LENGTH_SHORT).show();
            return;
        }

        if(TextUtils.isEmpty(syxz)){
            Toast.makeText(this, "使用性质不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(uCarEntity.getLicensePlateNoold())){
            Toast.makeText(this, "头车车牌号为空！", Toast.LENGTH_SHORT).show();
            return;

        }

        Map<String,String> map=new HashMap<>();
        map.put("mobile",tel);
        map.put("cartype","2");
        map.put("owner_p",syr);
        map.put("brand",ppxh);
        map.put("licensePlateNo",cph);
        map.put("standardVehicleType",cllx);
        map.put("vehicleIdentityCode",sbm);
        map.put("load_p",zz);
        map.put("usage_p",syxz);
        map.put("nfcid",nfcid);
        map.put("licensePlateNoold",uCarEntity.getLicensePlateNoold());
        doSave(map);
    }

    private void doSave(Map<String,String> map) {
        final LoadingDialogFragment dialogFragment = LoadingDialogFragment.getInstance();
        dialogFragment.showF(getSupportFragmentManager(), "addcarinfo");
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.TRAILERINFO_URL).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call = serviceStore.addTrailerInfo(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialogFragment.dismissAllowingStateLoss();
                ResponseBody body=response.body();
                try {
                    String str= body.string();
                    try {
                        JSONObject object=new JSONObject(str);
                        if(object.getBoolean("success")){
                            Toast.makeText(MyTrailerDetailsActivity.this, "绑定成功", Toast.LENGTH_SHORT).show();
                            finish();
                            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                        }else {
                            String errorMsg=object.getString("msg");
                            Toast.makeText(MyTrailerDetailsActivity.this, "绑定失败！"+errorMsg, Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("onResponse","onResponse=="+str);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure","onFailure=="+t.getMessage());
                Toast.makeText(MyTrailerDetailsActivity.this, "绑定失败！"+t.getMessage(), Toast.LENGTH_SHORT).show();
                dialogFragment.dismissAllowingStateLoss();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean isLegalId(String id) {
        if (id.toUpperCase().matches("(^\\d{15}$)|(^\\d{17}([0-9]|X)$)")) {
            return true;
        } else {
            return false;
        }
    }


}
