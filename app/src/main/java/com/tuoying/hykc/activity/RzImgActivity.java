package com.tuoying.hykc.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.tuoying.hykc.R;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.EventEntity;
import com.tuoying.hykc.entity.RZEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.utils.GlideImageLoader;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.RxBus;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.LoadingDialogFragment;
import com.tuoying.hykc.view.SelectDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class RzImgActivity extends BaseActivity implements View.OnClickListener {
    public static final int SFZ_Z = 1;//身份证正面
    public static final int SFZ_F = 2;//身份证反面
    public static final int JSZ = 3;//驾驶证
    public static final int XSZ = 4;//行驶证
    public static final int DLYSZ = 5;//道路运输证
    public static final int CYZGZ = 6;//从业资格证
    public static final int XSZ_Z = 7;//行驶证副页正
    public static final int XSZ_F = 8;//行驶证副页反
    public static final int GCZZY = 9;//挂车证主页
    public static final int GCZFY_Z = 10;//挂车证副页正
    public static final int GCZFY_F = 11;//挂车证副页反
    public static final int IMAGE_ITEM_ADD = -1;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    private final LoadingDialogFragment loadView = LoadingDialogFragment.getInstance();
/*    public  boolean isSFZ_Z = false;//身份证正面是否成功
    public  boolean isSFZ_F = false;//身份证反面是否成功
    public  boolean isJSZ = false;//驾驶证是否成功
    public  boolean isXSZ = false;//行驶证是否成功
    public  boolean isDLYSZ = false;//道路运输证是否成功
    public  boolean isCYZGZ = false;//从业资格证是否成功
    public  boolean isXSZ_Z = false;//行驶证副页正是否成功
    public  boolean isXSZ_F = false;//行驶证副页反是否成功
    public  boolean isGCZZY = false;//挂车证主页是否成功
    public boolean isGCZFY_Z = false;//挂车证副页正是否成功
    public boolean isGCZFY_F = false;//挂车证副页反是否成功*/
    private int selectType = -1;
    private Toolbar mToolbar;
    private DBDao dao;
    private String userId;
    private User user;
    private RZEntity entity = null;
    private ImageView mImgCard_Z;
    private ImageView mImgCard_F;
    private ImageView mImgJsz;
    private ImageView mImgXsz;
    private ImageView mImgDlysz;
    private ImageView mImgCyzgz;
    private ImageView mImgXSZ_Z;
    private ImageView mImgXSZ_F;
    private ImageView mImgXSZFB;
    private ImageView mImgGCZL;
    private ImageView mImgGCZLFYF;
    private Button mBtnOk;
    private ArrayList<ImageItem> images = new ArrayList<>();
    private Map<String, File> imgMap = new HashMap<>();
    private Map<String, String> textMap = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rz_img);
        initImagePicker();
        init();
    }

    private void initImagePicker() {
        ImagePicker imagePicker = ImagePicker.getInstance();
        imagePicker.setImageLoader(new GlideImageLoader());   //设置图片加载器
        imagePicker.setShowCamera(true);                      //显示拍照按钮
        imagePicker.setCrop(false);                            //允许裁剪（单选才有效）
        imagePicker.setSaveRectangle(true);                   //是否按矩形区域保存
        imagePicker.setSelectLimit(1);                        //选中数量限制
        imagePicker.setStyle(CropImageView.Style.RECTANGLE);  //裁剪框的形状
        imagePicker.setFocusWidth(800);                       //裁剪框的宽度。单位像素（圆形自动取宽高最小值）
        imagePicker.setFocusHeight(800);                      //裁剪框的高度。单位像素（圆形自动取宽高最小值）
        imagePicker.setOutPutX(1000);                         //保存文件的宽度。单位像素
        imagePicker.setOutPutY(1000);                         //保存文件的高度。单位像素
    }

    @Override
    public void init() {

        entity = (RZEntity) getIntent().getSerializableExtra("entity");
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("选择照片");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        dao = new DBDaoImpl(this);
        userId = SharePreferenceUtil.getInstance(this).getUserId();
        if (!TextUtils.isEmpty(userId)) {
            user = dao.findUserInfoById(userId);
        }else {
            Toast.makeText(this,
                    "请重新登录！", Toast.LENGTH_SHORT).show();
        }

        initViews();
        initEvent();
        downLoadImg(userId);
    }

    private void initViews() {
        mImgXSZ_Z = findViewById(R.id.img_xsz_z);
        mImgXSZ_F = findViewById(R.id.img_xsz_f);
        mImgXSZFB = findViewById(R.id.img_xszfb);
        mImgGCZL = findViewById(R.id.img_gczl);
        mImgCard_Z = findViewById(R.id.img_card_z);
        mImgCard_F = findViewById(R.id.img_card_f);
        mImgJsz = findViewById(R.id.img_jsz);
        mImgXsz = findViewById(R.id.img_xsz);
        mImgDlysz = findViewById(R.id.img_dlysz);
        mImgCyzgz = findViewById(R.id.img_cyzgz);
        mBtnOk = findViewById(R.id.btn_ok);
        mImgGCZLFYF=findViewById(R.id.img_gczfyf);
    }

    private void initEvent() {
        mImgCard_Z.setOnClickListener(this);
        mImgCard_F.setOnClickListener(this);
        mImgJsz.setOnClickListener(this);
        mImgXsz.setOnClickListener(this);
        mImgDlysz.setOnClickListener(this);
        mImgCyzgz.setOnClickListener(this);
        mBtnOk.setOnClickListener(this);
        mImgXSZ_Z.setOnClickListener(this);
        mImgXSZ_F.setOnClickListener(this);
        mImgXSZFB.setOnClickListener(this);
        mImgGCZL.setOnClickListener(this);
        mImgGCZLFYF.setOnClickListener(this);
    }

    private void downLoadImg(String tel){
        String urls[]=getResources().getStringArray(R.array.rzimgs);
        Glide.with(this)
                .load(getUrl(tel,urls[0]))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgCard_Z);
        Glide.with(this)
                .load(getUrl(tel,urls[1]))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgCard_F);
        Glide.with(this)
                .load(getUrl(tel,urls[2]))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgJsz);
        Glide.with(this)
                .load(getUrl(tel,urls[3]))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgXsz);
        Glide.with(this)
                .load(getUrl(tel,urls[4]))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgDlysz);
        Glide.with(this)
                .load(getUrl(tel,urls[5]))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgCyzgz);
        Glide.with(this)
                .load(getUrl(tel,urls[6]))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgXSZ_Z);
        Glide.with(this)
                .load(getUrl(tel,urls[7]))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgXSZ_F);
        Glide.with(this)
                .load(getUrl(tel,urls[8]))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgXSZFB);
        Glide.with(this)
                .load(getUrl(tel,urls[9]))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgGCZL);
        Glide.with(this)
                .load(getUrl(tel,urls[10]))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(mImgGCZLFYF);

    }
    private String getUrl(String tel,String imhName){

        return Constants.WEBSERVICE_URL+"files/temp/"+tel+"/"+imhName;
    }
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.img_card_z:
                selectType = SFZ_Z;
                setImg();
                break;
            case R.id.img_card_f:
                selectType = SFZ_F;
                setImg();
                break;
            case R.id.img_jsz:
                selectType = JSZ;
                setImg();
                break;
            case R.id.img_xsz:
                selectType = XSZ;
                setImg();
                break;
            case R.id.img_dlysz:
                selectType = DLYSZ;
                setImg();
                break;
            case R.id.img_cyzgz:
                selectType = CYZGZ;
                setImg();
                break;
            case R.id.btn_ok:
                doSave();
                break;
            case R.id.img_xsz_z:
                selectType = XSZ_Z;
                setImg();
                break;
            case R.id.img_xsz_f:
                selectType = XSZ_F;
                setImg();
                break;
            case R.id.img_xszfb:
                selectType = GCZZY;
                setImg();
                break;
            case R.id.img_gczl:
                selectType = GCZFY_Z;
                setImg();
                break;
            case R.id.img_gczfyf:
                selectType = GCZFY_F;
                setImg();
                break;
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> imgs = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                if (imgs != null && imgs.size() > 0) {
                    setImage(imgs.get(0));
                }
            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                ArrayList<ImageItem> imgs = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if (imgs != null && imgs.size() > 0) {
                    setImage(imgs.get(0));
                }
            }
        }
    }

    private void setImage(ImageItem item) {
        String type=null;
        ImageView imageView = null;
        if (item == null) {
            return;
        }
        if(TextUtils.isEmpty(item.path)){
            return;
        }
        switch (selectType) {
            case SFZ_Z:
                type = "personal1.jpg";
                imageView = mImgCard_Z;
                break;
            case SFZ_F:
                type = "personal2.jpg";
                imageView = mImgCard_F;
                break;
            case JSZ:
                type = "personal3.jpg";
                imageView = mImgJsz;
                break;
            case XSZ:
                type = "vehicle0.jpg";
                imageView = mImgXsz;
                break;
            case DLYSZ:
                type = "vehicle2.jpg";
                imageView = mImgDlysz;
                break;
            case CYZGZ:
                type = "vehicle3.jpg";
                imageView = mImgCyzgz;
                break;
            case XSZ_Z:
                type = "personal5.jpg";
                imageView = mImgXSZ_Z;
                break;
            case XSZ_F:
                type = "personal6.jpg";
                imageView = mImgXSZ_F;
                break;
            case GCZZY:
                type = "personal7.jpg";
                imageView = mImgXSZFB;
                break;
            case GCZFY_Z:
                type = "personal8.jpg";
                imageView = mImgGCZL;
                break;
            case GCZFY_F:
                type = "personal9.jpg";
                imageView = mImgGCZLFYF;
                break;
        }
        if (imageView != null) {
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            ImagePicker.getInstance().getImageLoader().displayImage(RzImgActivity.this, item.path, imageView, 0, 0);
            saveDataToMap(type,item.path);
        }
    }

    private void uploadImg(String fileName,int type,String uploadBuffer) {
        upLoadImg(fileName,type,uploadBuffer);

    }


    private void setImgLayoutParams(ImageView imageView){
        ViewGroup.LayoutParams para = imageView.getLayoutParams();
        para.width=240;
        para.height=120;
        imageView.setLayoutParams(para);
    }


    private void saveDataToMap(String fileName,String path) {
        Bitmap bitmap = compressImg(path);
        String ImgBuffer = bitmapToBase64(bitmap);
        String uploadBuffer=ImgBuffer.replaceAll("\\+","-");
       // saveBase64(uploadBuffer);
        if(TextUtils.isEmpty(uploadBuffer)){
            Toast.makeText(this, "照片为空，请重新选择！", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (selectType) {
            case SFZ_Z:
                uploadImg(fileName,SFZ_Z,uploadBuffer);
                break;
            case SFZ_F:

                uploadImg(fileName,SFZ_F,uploadBuffer);
                break;
            case JSZ:

                uploadImg(fileName,JSZ,uploadBuffer);
                break;
            case XSZ:

                uploadImg(fileName,XSZ,uploadBuffer);
                break;
            case DLYSZ:

                uploadImg(fileName,DLYSZ,uploadBuffer);
                break;
            case CYZGZ:

                uploadImg(fileName,CYZGZ,uploadBuffer);
                break;
            case XSZ_Z:

                uploadImg(fileName,XSZ_Z,uploadBuffer);
                break;
            case XSZ_F:

                uploadImg(fileName,XSZ_F,uploadBuffer);
                break;
            case GCZZY:

                uploadImg(fileName,GCZZY,uploadBuffer);
                break;
            case GCZFY_Z:

                uploadImg(fileName,GCZFY_Z,uploadBuffer);
                break;
            case GCZFY_F:

                uploadImg(fileName,GCZFY_F,uploadBuffer);
                break;
        }
    }
    private String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT).replaceAll(" ", "");
                try {
                    if (baos != null) {
                        baos.flush();
                        baos.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        return result;
    }

    private Bitmap compressImg(String imagePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true; // 只获取图片的大小信息，而不是将整张图片载入在内存中，避免内存溢出
        BitmapFactory.decodeFile(imagePath, options);
        int h = options.outHeight;
        int w = options.outWidth;
        float hh = 1280f;//这里设置高度为1280f
        float ww = 720f;//这里设置宽度为720f
        int be = 1;//be=1表示不缩放
        if (w > h && w > ww) {//如果宽度大的话根据宽度固定大小缩放
            be = (int) (options.outWidth / ww);
        } else if (w < h && h > hh) {//如果高度高的话根据宽度固定大小缩放
            be = (int) (options.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        options.inJustDecodeBounds = false; // 计算好压缩比例后，这次可以去加载原图了
        options.inSampleSize = be; // 设置为刚才计算的压缩比例
        return BitmapFactory.decodeFile(imagePath, options); // 解码文件
    }

    private void doSave() {
        if (entity == null) {
            Toast.makeText(this, "基本信息为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (user == null) {
            Toast.makeText(this, "用户信息为空，请重新登录！", Toast.LENGTH_SHORT).show();
            return;
        }
        textMap.put("sfzh", entity.getSfzh());
        textMap.put("mobile", entity.getMobile());
        textMap.put("app", Constants.AppId);
        textMap.put("xm", entity.getXm());
        Log.e("xmmmm","xmmmm=="+entity.getXm());
        textMap.put("cph", entity.getCph());
        textMap.put("pp", entity.getPp());
        textMap.put("token", user.getToken());
        textMap.put("type", "request");
        textMap.put("cx", entity.getCx());
        textMap.put("cc", entity.getCc());
        textMap.put("nf", "2018");
        textMap.put("zz", entity.getZz());
        textMap.put("dlysz", entity.getDlysz());
        textMap.put("cplx", entity.getCplx());
        textMap.put("clfl", entity.getClfl());
        textMap.put("sfzStartTime", entity.getSfzStartTime());
        textMap.put("sfzEndTime", entity.getSfzEndTime());
        textMap.put("licenseNo", entity.getLicenseNo());
        textMap.put("licenseFirstGetDate", entity.getLicenseFirstGetDate());
        textMap.put("licenseType", entity.getLicenseType());
        textMap.put("licenseStartTime", entity.getLicenseStartTime());
        textMap.put("licenseEndTime", entity.getLicenseEndTime());
        textMap.put("vehicleIdentityCode", entity.getVehicleIdentityCode());
        textMap.put("engineNumber", entity.getEngineNumber());
        textMap.put("owner", entity.getSyr());
/*        if (!isSFZ_Z) {
            Toast.makeText(this, "请选择身份证正面照！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isSFZ_F) {
            Toast.makeText(this, "请选择身份证反面照！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isJSZ) {
            Toast.makeText(this, "请选择驾驶证照片！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isXSZ) {
            Toast.makeText(this, "请选择行驶证照片！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isDLYSZ) {
            Toast.makeText(this, "请选择道路运输证照片！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isCYZGZ) {
            Toast.makeText(this, "请选择从业资格证照片！", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!isXSZ_Z) {
            Toast.makeText(this, "请选择行驶证副页正面照片！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isXSZ_F) {
            Toast.makeText(this, "请选择行驶证副页反面照片！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isGCZZY) {
            Toast.makeText(this, "请选择行挂车证主页照片！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isGCZFY_Z) {
            Toast.makeText(this, "请选择挂车证副页正照片！", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!isGCZFY_F) {
            Toast.makeText(this, "请选择挂车证副页反照片！", Toast.LENGTH_SHORT).show();
            return;
        }*/
        uploadData();

    }

    private void uploadData() {
        final LoadingDialogFragment uploadDataLoadView = LoadingDialogFragment.getInstance();
        uploadDataLoadView.showF(getSupportFragmentManager(),"uploadDataLoadView");
        RequestManager.getInstance()
                .mServiceStore
                .image_upload_new(textMap)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("image_upload_new","=="+msg);
                        if (!TextUtils.isEmpty(msg)) {
                            try {
                                JSONObject jsonObject = new JSONObject(msg);
                                boolean isSuccess = jsonObject.getBoolean("success");
                                if (isSuccess) {
                                    mBtnOk.setEnabled(false);
                                    mBtnOk.setClickable(false);
                                    mBtnOk.setBackgroundResource(R.drawable.btn_clickable_false_bg);
                                    Toast.makeText(RzImgActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
                                    if(uploadDataLoadView!=null){
                                        uploadDataLoadView.dismissAllowingStateLoss();
                                    }
                                    RxBus.getInstance().send(new EventEntity("heardview_rz","heardview_rz"));
                                    SharePreferenceUtil.getInstance(RzImgActivity.this).setUserId(textMap.get("mobile"));
                                    Intent intentReg = new Intent(RzImgActivity.this, MainActivity.class);
                                    startActivity(intentReg);
                                    overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
                                } else {
                                    if(uploadDataLoadView!=null){
                                        uploadDataLoadView.dismissAllowingStateLoss();
                                    }
                                    Toast.makeText(RzImgActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }finally {

                            }
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        if(uploadDataLoadView!=null){
                            uploadDataLoadView.dismissAllowingStateLoss();
                        }
                        Log.e("upLoadImgToService", msg);
                        Toast.makeText(RzImgActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
                    }
                }));
    }


    private void upLoadImg(final String fileName,final int imgType,String uploadBuffer){
        loadView.show(getSupportFragmentManager(), "uploadLoading");
        Log.e("fileName imgType",fileName+"=="+imgType);
        Map<String, String> m = new HashMap<>();
        m.put("mobile", user.getUserId());
        m.put("fileName",  fileName);
        m.put("base64", uploadBuffer);
        m.put("token",user.getToken());
        m.put("app",Constants.AppId);
        RequestManager.getInstance()
                .mServiceStore
                .upLoadImg(m)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String s) {
                        if (loadView != null) {
                            loadView.dismiss();
                        }
                        if (!TextUtils.isEmpty(s)) {
                            try {
                                if(s.contains("{") && s.contains("}")){
                                    int startIndex=s.indexOf("{");
                                    int lastIndex=s.lastIndexOf("}")+1;
                                    String strs=s.substring(startIndex,lastIndex);
                                    Log.e("onPostExecute", "str====" + strs);
                                    JSONObject object=new JSONObject(new String(strs));
                                    boolean isSuccess = object.getBoolean("success");
                                    if (isSuccess) {
                                        setViewEnable(imgType,true);
                                        Toast.makeText(RzImgActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(RzImgActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
                                        setViewEnable(imgType,false);
                                    }
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("upLoadImgToService", msg);
                        Toast.makeText(RzImgActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
                        if (loadView != null) {
                            loadView.dismiss();
                        }
                    }
                }));
    }


    private void setViewEnable(int imgType,boolean b) {
        /*switch (imgType) {
            case SFZ_Z:
               isSFZ_Z=b;
                break;
            case SFZ_F:
                isSFZ_F=b;
                break;
            case JSZ:
                isJSZ=b;
                break;
            case XSZ:
                isXSZ=b;
                break;
            case DLYSZ:
                isDLYSZ=b;
                break;
            case CYZGZ:
                isCYZGZ=b;
                break;
            case XSZ_Z:
                isXSZ_Z=b;
                break;
            case XSZ_F:
                isXSZ_F=b;
                break;
            case GCZZY:
                isGCZZY=b;
                break;
            case GCZFY_Z:
                isGCZFY_Z=b;
                break;
            case GCZFY_F:
                isGCZFY_F=b;
                break;*/
        //}

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
       /* final List<MyTask> list=mTaskReference.get();
        if(list!=null){
            for (int i=0;i<list.size();i++){
                MyTask task=list.get(i);
                if(task!=null){
                    if(task.cancel(true)){
                    }
                }
            }
        }*/
    }

    private void setImg() {
        List<String> names = new ArrayList<>();
        names.add("拍照");
        names.add("相册");
        showDialog(new SelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        ImagePicker.getInstance().setSelectLimit(1);
                        Intent intent = new Intent(RzImgActivity.this, ImageGridActivity.class);
                        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                        startActivityForResult(intent, REQUEST_CODE_SELECT);
                        break;
                    case 1:
                        ImagePicker.getInstance().setSelectLimit(1);
                        Intent intent1 = new Intent(RzImgActivity.this, ImageGridActivity.class);
                        startActivityForResult(intent1, REQUEST_CODE_SELECT);
                        break;
                    default:
                        break;
                }
            }
        }, names);
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

    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style
                .transparentFrameWindowStyle,
                listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }
}
