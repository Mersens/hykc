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
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.ocr.sdk.OCR;
import com.baidu.ocr.sdk.OnResultListener;
import com.baidu.ocr.sdk.exception.OCRError;
import com.baidu.ocr.sdk.model.AccessToken;
import com.baidu.ocr.sdk.model.IDCardParams;
import com.baidu.ocr.sdk.model.IDCardResult;
import com.baidu.ocr.ui.camera.CameraActivity;
import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.tuoying.hykc.R;
import com.tuoying.hykc.entity.RZEntity;
import com.tuoying.hykc.utils.CarInfoUtils;
import com.tuoying.hykc.utils.DateUtils;
import com.tuoying.hykc.utils.FileUtil;
import com.tuoying.hykc.utils.IOUtils;
import com.tuoying.hykc.utils.RecognizeService;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.ExitDialogFragment;
import com.tuoying.hykc.view.LoadingDialogFragment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class RzTextActivity extends BaseActivity {
    private Toolbar mToolbar;
    private Button mBtnNext;
    private EditText mEditName;
    private EditText mEditCardNum;
    private EditText mEditTel;
    private EditText mEditCph;
    private EditText mEditZz;
    private EditText mEditDlysz;
    private Spinner mCXSpinner;
    private Spinner mPPSpinner;
    private Spinner mCDSpinner;
    private Spinner mCPLXSpinner;
    private Spinner mCLFLSpinner;
    private String ppItems[] = null;
    private String cxItems[] = null;
    private String cdItems[] = null;
    private String cplxItems[] = null;
    private String clflItems[] = null;
    private RelativeLayout layout_idCard_start;
    private RelativeLayout layout_idCard_end;
    private TextView num_start_name;
    private TextView num_start_end;
    private EditText editJSZ;
    private RelativeLayout layout_jsz_start;
    private RelativeLayout layout_jsz_end;
    private TextView jsz_start_name;
    private TextView jsz_start_end;
    private RelativeLayout layout_jsz_time;
    private TextView tv_jsz_time;
    private Spinner jszlxSpinner;
    private String jszlxItems[]=null;
    private EditText editClsbm;
    private EditText editFdjh;
    private EditText editSYR;
    private TimePickerView idCardStartPicker;
    private TimePickerView idCardEndPicker;
    private TimePickerView jszStartPicker;
    private TimePickerView jszEndPicker;
    private TimePickerView jszTimePicker;
    private TextView mTextSB_SFZ;
    private TextView mTextSB_JSZ;
    private TextView mTextSB_XSZ;
    private RZEntity entity = new RZEntity();
    private String id;
    private static final int REQUEST_CODE_CAMERA = 102;
    private static final int REQUEST_CODE_VEHICLE_LICENSE = 120;
    private static final int REQUEST_CODE_DRIVING_LICENSE = 121;
    private boolean hasGotToken = false;
    final LoadingDialogFragment loadingDialogFragment=LoadingDialogFragment.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rz);
        init();
    }

    @Override
    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("实名认证");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        id= SharePreferenceUtil.getInstance(this).getUserId();
        initViews();
        initDatas();
        initTimePicker();
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
    private void initTimePicker() {
        idCardStartPicker = new TimePickerBuilder(RzTextActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                num_start_name.setText(getTime(date));
                num_start_name.setTextColor(getResources().getColor(R.color.text_color_black));
                entity.setSfzStartTime(getTime(date));

            }
        }).build();
        idCardEndPicker = new TimePickerBuilder(RzTextActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                num_start_end.setText(getTime(date));
                num_start_end.setTextColor(getResources().getColor(R.color.text_color_black));
                entity.setSfzEndTime(getTime(date));
        }
        }).build();
        jszStartPicker = new TimePickerBuilder(RzTextActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                jsz_start_name.setText(getTime(date));
                jsz_start_name.setTextColor(getResources().getColor(R.color.text_color_black));
                entity.setLicenseStartTime(getTime(date));
            }
        }).build();
        jszEndPicker = new TimePickerBuilder(RzTextActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                jsz_start_end.setText(getTime(date));
                jsz_start_end.setTextColor(getResources().getColor(R.color.text_color_black));
                entity.setLicenseEndTime(getTime(date));
            }
        }).build();
        jszTimePicker = new TimePickerBuilder(RzTextActivity.this, new OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tv_jsz_time.setText(getTime(date));
                tv_jsz_time.setTextColor(getResources().getColor(R.color.text_color_black));
                entity.setLicenseFirstGetDate(getTime(date));
            }
        }).build();

    }
    private String getTime(Date date){
        SimpleDateFormat format0 = new SimpleDateFormat("yyyy-MM-dd");
        String time = format0.format(date);
        return time;
    }
    private void initViews() {
        mBtnNext = findViewById(R.id.btn_next);

        mEditName = findViewById(R.id.editname);
        mEditCardNum = findViewById(R.id.editnum);
        mEditTel = findViewById(R.id.editTel);
        if(!TextUtils.isEmpty(id)){
            mEditTel.setText(id);
        }
        mEditCph = findViewById(R.id.editCph);
        mEditZz = findViewById(R.id.editZz);
        mEditDlysz = findViewById(R.id.editDlysz);

        mPPSpinner = findViewById(R.id.ppSpinner);
        mCXSpinner = findViewById(R.id.cxSpinner);
        mCDSpinner = findViewById(R.id.ccSpinner);
        mCPLXSpinner = findViewById(R.id.cplxSpinner);
        mCLFLSpinner = findViewById(R.id.clflSpinner);
        layout_idCard_start=findViewById(R.id.layout_idCard_start);

        layout_idCard_start=findViewById(R.id.layout_idCard_start);
        layout_idCard_end=findViewById(R.id.layout_idCard_end);
        num_start_name=findViewById(R.id.num_start_name);
        num_start_end=findViewById(R.id.num_start_end);
        editJSZ=findViewById(R.id.editJSZ);
        layout_jsz_start=findViewById(R.id.layout_jsz_start);
        layout_jsz_end=findViewById(R.id.layout_jsz_end);
        jsz_start_name=findViewById(R.id.jsz_start_name);
        jsz_start_end=findViewById(R.id.jsz_start_end);
        layout_jsz_time=findViewById(R.id.layout_jsz_time);
        tv_jsz_time=findViewById(R.id.tv_jsz_time);
        jszlxSpinner=findViewById(R.id.jszlxSpinner);
        editClsbm=findViewById(R.id.editClsbm);
        editFdjh=findViewById(R.id.editFdjh);
        mTextSB_SFZ=findViewById(R.id.tv_sfz_sb);
        mTextSB_JSZ=findViewById(R.id.tv_jsz_sb);
        mTextSB_XSZ=findViewById(R.id.tv_xsz_sb);
        editSYR=findViewById(R.id.editSYR);
    }

    private void initDatas() {
        ppItems = getResources().getStringArray(R.array.clpp);
        entity.setPp(ppItems[0]);
        cxItems = getResources().getStringArray(R.array.cx);
        entity.setCx(cxItems[0]);
        cdItems = getResources().getStringArray(R.array.cc);
        entity.setCc(cdItems[0]);
        cplxItems = getResources().getStringArray(R.array.cplx);
        String cplx = CarInfoUtils.getInstance().getLxIdByValue(cplxItems[0]);
        entity.setCplx(cplx);
        clflItems = getResources().getStringArray(R.array.clfl);
        String clfl = CarInfoUtils.getInstance().getFlIdByValue(clflItems[0]);
        entity.setClfl(clfl);
        jszlxItems= getResources().getStringArray(R.array.jszlx);
        entity.setLicenseType(jszlxItems[1]);

        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(RzTextActivity.this, android.R.layout.simple_spinner_item, ppItems);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPPSpinner.setAdapter(adapter1);
        mPPSpinner.setDropDownVerticalOffset(dp2px(RzTextActivity.this, 36));

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(RzTextActivity.this, android.R.layout.simple_spinner_item, cxItems);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCXSpinner.setAdapter(adapter2);
        mCXSpinner.setDropDownVerticalOffset(dp2px(RzTextActivity.this, 36));

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(RzTextActivity.this, android.R.layout.simple_spinner_item, cdItems);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCDSpinner.setAdapter(adapter3);
        mCDSpinner.setDropDownVerticalOffset(dp2px(RzTextActivity.this, 36));

        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(RzTextActivity.this, android.R.layout.simple_spinner_item, cplxItems);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCPLXSpinner.setAdapter(adapter4);
        mCPLXSpinner.setDropDownVerticalOffset(dp2px(RzTextActivity.this, 36));

        ArrayAdapter<String> adapter5 = new ArrayAdapter<String>(RzTextActivity.this, android.R.layout.simple_spinner_item, jszlxItems);
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCLFLSpinner.setAdapter(adapter5);
        mCLFLSpinner.setSelection(1,true);
        mCLFLSpinner.setDropDownVerticalOffset(dp2px(RzTextActivity.this, 36));

        ArrayAdapter<String> adapter6 = new ArrayAdapter<String>(RzTextActivity.this, android.R.layout.simple_spinner_item, clflItems);
        adapter5.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        jszlxSpinner.setAdapter(adapter6);
        jszlxSpinner.setDropDownVerticalOffset(dp2px(RzTextActivity.this, 36));

    }

    private static int dp2px(Context context, float dipValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    private void initEvent() {
        layout_idCard_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idCardStartPicker.show();
            }
        });

        layout_idCard_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                idCardEndPicker.show();
            }
        });
        layout_jsz_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jszStartPicker.show();
            }
        });

        layout_jsz_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jszEndPicker.show();
            }
        });

        layout_jsz_time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                jszTimePicker.show();
            }
        });
        mBtnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setIntentDatas();

            }
        });

        mPPSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                entity.setPp(ppItems[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mCXSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                entity.setCx(cxItems[position]);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mCDSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                entity.setCc(cdItems[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mCPLXSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String cplx = CarInfoUtils.getInstance().getLxIdByValue(cplxItems[position]);
                entity.setCplx(cplx);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        mCLFLSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String clfl = CarInfoUtils.getInstance().getFlIdByValue(clflItems[position]);
                entity.setClfl(clfl);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mTextSB_SFZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(RzTextActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE, CameraActivity.CONTENT_TYPE_ID_CARD_FRONT);
                startActivityForResult(intent, REQUEST_CODE_CAMERA);
            }
        });
        mTextSB_JSZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(RzTextActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_DRIVING_LICENSE);

            }
        });
        mTextSB_XSZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkTokenStatus()) {
                    return;
                }
                Intent intent = new Intent(RzTextActivity.this, CameraActivity.class);
                intent.putExtra(CameraActivity.KEY_OUTPUT_FILE_PATH,
                        FileUtil.getSaveFile(getApplication()).getAbsolutePath());
                intent.putExtra(CameraActivity.KEY_CONTENT_TYPE,
                        CameraActivity.CONTENT_TYPE_GENERAL);
                startActivityForResult(intent, REQUEST_CODE_VEHICLE_LICENSE);
            }
        });
    }
    private void recIDCard(String idCardSide, String filePath) {
        if(loadingDialogFragment!=null){
            loadingDialogFragment.show(getSupportFragmentManager(),"rzLoadingDialog");

        }
        IDCardParams param = new IDCardParams();
        param.setImageFile(new File(filePath));
        // 设置身份证正反面
        param.setIdCardSide(idCardSide);
        // 设置方向检测
        param.setDetectDirection(true);
        // 设置图像参数压缩质量0-100, 越大图像质量越好但是请求时间越长。 不设置则默认值为20
        param.setImageQuality(20);
        OCR.getInstance(this).recognizeIDCard(param, new OnResultListener<IDCardResult>() {
            @Override
            public void onResult(IDCardResult result) {
                if(loadingDialogFragment!=null){
                    loadingDialogFragment.dismiss();
                }
                if (result != null) {
                    //IDCardResult front{direction=0, wordsResultNumber=6, address=河南省孟津县常袋乡马岭村, idNumber=410322199304228314, birthday=19930422, name=马新新, gender=男, ethnic=汉}
                    Log.e("recognizeIDCard","==="+result.toString());
                    mEditName.setText(result.getName().toString());
                    mEditCardNum.setText(result.getIdNumber().toString());
                }
            }

            @Override
            public void onError(OCRError error) {
                if(loadingDialogFragment!=null){
                    loadingDialogFragment.dismiss();
                }
                Toast.makeText(RzTextActivity.this, error.getErrorCode(), Toast.LENGTH_SHORT).show();

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
                        recIDCard(IDCardParams.ID_CARD_SIDE_FRONT, filePath);
                    } else if (CameraActivity.CONTENT_TYPE_ID_CARD_BACK.equals(contentType)) {
                        recIDCard(IDCardParams.ID_CARD_SIDE_BACK, filePath);
                    }
                }
            }
        }
        // 识别成功回调，行驶证识别
        else if (requestCode == REQUEST_CODE_VEHICLE_LICENSE && resultCode == Activity.RESULT_OK) {
            if(loadingDialogFragment!=null){
                loadingDialogFragment.show(getSupportFragmentManager(),"rzLoadingDialog");

            }
            RecognizeService.recVehicleLicense(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            if(loadingDialogFragment!=null){
                                loadingDialogFragment.dismiss();
                            }
                          Log.e("recVehicleLicense","==="+result);
                            if(!TextUtils.isEmpty(result)){
                                try {
                                    JSONObject jsonObject=new JSONObject(result);
                                    JSONObject object=new JSONObject(jsonObject.getString("words_result"));
                                    if(object.has("号牌号码")){
                                        JSONObject object1=new JSONObject(object.getString("号牌号码"));
                                        String cph=object1.getString("words");
                                        if(!TextUtils.isEmpty(cph)){
                                            mEditCph.setText(cph);
                                        }
                                    }
                                    if(object.has("车辆识别代号")){
                                        JSONObject object2=new JSONObject(object.getString("车辆识别代号"));
                                        String clsbm=object2.getString("words");
                                        if(!TextUtils.isEmpty(clsbm)){
                                            editClsbm.setText(clsbm);
                                        }

                                    }
                                    if(object.has("发动机号码")){
                                        JSONObject object3=new JSONObject(object.getString("发动机号码"));
                                        String fsjh=object3.getString("words");
                                        if(!TextUtils.isEmpty(fsjh)){
                                            editFdjh.setText(fsjh);
                                        }
                                    }
                                    if(object.has("所有人")){
                                        JSONObject object2=new JSONObject(object.getString("所有人"));
                                        String syr=object2.getString("words");
                                        if(!TextUtils.isEmpty(syr)){
                                            editSYR.setText(syr);
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
            if(loadingDialogFragment!=null){
                loadingDialogFragment.show(getSupportFragmentManager(),"rzLoadingDialog");
            }

            RecognizeService.recDrivingLicense(this, FileUtil.getSaveFile(getApplicationContext()).getAbsolutePath(),
                    new RecognizeService.ServiceListener() {
                        @Override
                        public void onResult(String result) {
                            if(loadingDialogFragment!=null){
                                loadingDialogFragment.dismiss();
                            }
                            if(!TextUtils.isEmpty(result)){
                                try {
                                    JSONObject jsonObject=new JSONObject(result);
                                    JSONObject object=new JSONObject(jsonObject.getString("words_result"));
                                    if(object.has("证号")){
                                        JSONObject object1=new JSONObject(object.getString("证号"));
                                        String zh=object1.getString("words");
                                        if(!TextUtils.isEmpty(zh)){
                                            editJSZ.setText(zh);
                                        }
                                    }
                                    if(object.has("初次领证日期")){
                                        JSONObject object2=new JSONObject(object.getString("初次领证日期"));
                                        String cclzrq=object2.getString("words");
                                        if(!TextUtils.isEmpty(cclzrq)){
                                            String time= formatTime(cclzrq);
                                            tv_jsz_time.setText(time);
                                            tv_jsz_time.setTextColor(getResources().getColor(R.color.text_color_black));
                                            entity.setLicenseFirstGetDate(time);
                                        }
                                    }
                                    if(object.has("有效期限")){
                                        JSONObject object3=new JSONObject(object.getString("有效期限"));
                                        String jszStart=object3.getString("words");
                                        if(!TextUtils.isEmpty(jszStart)){
                                            String time= formatTime(jszStart);
                                            jsz_start_name.setText(time);
                                            jsz_start_name.setTextColor(getResources().getColor(R.color.text_color_black));
                                            entity.setLicenseStartTime(time);
                                        }
                                    }
                                    if(object.has("至")){
                                        JSONObject object3=new JSONObject(object.getString("至"));
                                        String jszEnd=object3.getString("words");
                                        if(!TextUtils.isEmpty(jszEnd)){
                                            String time= formatTime(jszEnd);
                                            jsz_start_end.setText(time);
                                            jsz_start_end.setTextColor(getResources().getColor(R.color.text_color_black));
                                            entity.setLicenseEndTime(time);
                                        }
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                            Log.e("recDrivingLicense","==="+result);

                        }
                    });
        }
    }
    private static String formatTime(String time){
        String t="";
        SimpleDateFormat d1 = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat d2 = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date date = d1.parse(time);
            t=d2.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return t;

    }
    private void setIntentDatas() {
        final String name = mEditName.getText().toString().trim();
        final String cardNum = mEditCardNum.getText().toString().trim();
        final String tel = mEditTel.getText().toString().trim();
        final String cph = mEditCph.getText().toString().trim();
        final String zz = mEditZz.getText().toString().trim();
        final String dlysz = mEditDlysz.getText().toString().trim();
        final String jszh = editJSZ.getText().toString().trim();
        final String clsbm = editClsbm.getText().toString().trim();
        final String fdjh = editFdjh.getText().toString().trim();
        final String syr=editSYR.getText().toString().toString();
        if (TextUtils.isEmpty(name)) {
            Toast.makeText(this, "姓名不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(cardNum)) {
            Toast.makeText(this, "身份证号不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isLegalId(cardNum)) {
            Toast.makeText(this, "请输入正确身份证号", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(tel)) {
            Toast.makeText(this, "手机号不能为空！", Toast.LENGTH_SHORT).show();
            return;

        }
        if (!IOUtils.isMobileNO(tel)) {
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
        if (TextUtils.isEmpty(dlysz)) {
            Toast.makeText(this, "道路运输证不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(jszh)) {
            Toast.makeText(this, "驾驶证号不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (jszh.length()!=18) {
            Toast.makeText(this, "请输入正确18位驾驶证号！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(syr)) {
            Toast.makeText(this, "所有人不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(clsbm)) {
            Toast.makeText(this, "车辆识别码不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (clsbm.length()!=17) {
            Toast.makeText(this, "请输入正确的17位车辆识别码！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(entity.getPp())){
            Toast.makeText(this, "车辆品牌不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(entity.getCx())){
            Toast.makeText(this, "车型不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(entity.getCc())){
            Toast.makeText(this, "车长不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(entity.getCplx())){
            Toast.makeText(this, "车牌类型不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(entity.getClfl())){
            Toast.makeText(this, "车辆分类不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(entity.getSfzStartTime())){
            Toast.makeText(this, "身份证开始有效期不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(entity.getSfzEndTime())){
            Toast.makeText(this, "身份证结束有效期不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(entity.getLicenseFirstGetDate())){
            Toast.makeText(this, "首次驾驶证获得时间不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(entity.getLicenseStartTime())){
            Toast.makeText(this, "驾驶证开始有效日期不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(entity.getLicenseEndTime())){
            Toast.makeText(this, "驾驶证结束有效日期不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(entity.getLicenseType())){
            Toast.makeText(this, "驾驶证类型不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        entity.setXm(name);
        entity.setMobile(tel);
        entity.setSfzh(cardNum);
        entity.setCph(cph);
        entity.setZz(zz);
        entity.setDlysz(dlysz);
        entity.setEngineNumber(fdjh);
        entity.setVehicleIdentityCode(clsbm);
        entity.setLicenseNo(jszh);
        entity.setSyr(syr);
        Intent intent = new Intent(RzTextActivity.this, RzImgActivity.class);
        intent.putExtra("entity", entity);
        startActivity(intent);
        overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
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
