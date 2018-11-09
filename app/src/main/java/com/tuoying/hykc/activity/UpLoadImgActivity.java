package com.tuoying.hykc.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.alct.mdp.MDPLocationCollectionManager;
import com.alct.mdp.callback.OnResultListener;
import com.alct.mdp.model.Image;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.tuoying.hykc.R;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.GoodsEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.utils.GlideImageLoader;
import com.tuoying.hykc.utils.HttpTools;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.LoadingDialogFragment;
import com.tuoying.hykc.view.SelectDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;

public class UpLoadImgActivity extends BaseActivity {
    private Toolbar mToolbar;
    private ImageView mImgXHZ;
    private ImageView mImgHDZ;
    private Button mBtnOk;
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    public static final int XHZ = 1;//身份证正面
    public static final int HDZ = 2;//身份证反面
    private int selectType = -1;
    private GoodsEntity entity;
    private boolean isSuccessXHZ=false;
    private boolean isSuccessHDZ=false;
    private User user;
    private String userid;
    private DBDao dao;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private String address;
    private Map<Integer,String> map=new HashMap<>();
    private static final String TYPE_HDZ="personal1.jpg";
    private static final String TYPE_XHZ="personal0.jpg";
    private LocationClient mLocClient;
    public MyLocationListenner myListener = new MyLocationListenner();
    private final LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.getInstance();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_img);
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
    public class MyLocationListenner implements BDLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null ) {
                return;
            }
            mCurrentLat = location.getLatitude();//维度
            mCurrentLon = location.getLongitude();//经度
            address=location.getAddrStr();
        }
        public void onReceivePoi(BDLocation poiLocation) {

        }
    }
    @Override
    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("选择照片");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        entity = (GoodsEntity) getIntent().getSerializableExtra("entity");

        mImgXHZ = findViewById(R.id.img_xhz);
        mImgHDZ = findViewById(R.id.img_hdz);
        mBtnOk = findViewById(R.id.btn_ok);
        userid=SharePreferenceUtil.getInstance(this).getUserId();
        dao=new DBDaoImpl(this);
        if(!TextUtils.isEmpty(userid)){
            user=dao.findUserInfoById(userid);
        }
        mLocClient = new LocationClient(this);
        mLocClient.registerLocationListener(myListener);
        LocationClientOption option = new LocationClientOption();
        option.setOpenGps(false); // 打开gps
        option.setLocationNotify(true);
        option.setCoorType("bd09ll"); // 设置坐标类型
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setIsNeedLocationDescribe(true);//可选，设置是否需要地址描述
        option.setScanSpan(10000);
        mLocClient.setLocOption(option);
        mLocClient.start();
        initEvent();
    }

    private void initEvent() {
        mImgXHZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSuccessXHZ){
                    Toast.makeText(UpLoadImgActivity.this, "卸货照只能上传一次！", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectType = XHZ;
                setImg();
            }
        });
        mImgHDZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isSuccessHDZ){
                    Toast.makeText(UpLoadImgActivity.this, "回单照只能上传一次！", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectType = HDZ;
                setImg();
            }
        });
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSave();

            }
        });
    }

    private void doSave() {
        if(TextUtils.isEmpty(map.get(XHZ))){
            Toast.makeText(this, "请选择卸货照！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(map.get(HDZ))){
            Toast.makeText(this, "请选择回单照！", Toast.LENGTH_SHORT).show();
            return;
        }
        doUpLoad();
    }

    private void doUpLoad() {
        final LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.getInstance();
        loadingDialogFragment.show(getSupportFragmentManager(), "Alctdialog");
        uploadUnloadImage(map.get(XHZ),loadingDialogFragment);
        mBtnOk.setClickable(false);
        mBtnOk.setEnabled(false);
        mBtnOk.setBackgroundResource(R.drawable.btn_no_click_bg);
    }

    private String getNowtime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        String time = sdf.format(new Date());
        return  time;
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
        ImageView imageView = null;
        if (item == null) {
            return;
        }
        switch (selectType) {
            case XHZ:
                imageView = mImgXHZ;
                break;
            case HDZ:
                imageView = mImgHDZ;
                break;

        }
        if (imageView != null) {
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            ImagePicker.getInstance().getImageLoader().displayImage(UpLoadImgActivity.this, item.path, imageView, 0, 0);
            uploadImg(item.path);
        }
    }

    private void uploadImg(String path) {
        Bitmap bitmap = compressImg(path);
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,
                50, baos2);
        byte[] bytes = baos2.toByteArray();
        String uploadBuffer = Base64.encodeToString(bytes, Base64.DEFAULT).replaceAll(" ","");
        if(selectType==XHZ){
            map.put(XHZ,uploadBuffer);
            upLoadImgToService(TYPE_XHZ,uploadBuffer);
        }else if(selectType==HDZ){
            map.put(HDZ,uploadBuffer);
            upLoadImgToService(TYPE_HDZ,uploadBuffer);
        }
    }

    private class MyTask extends AsyncTask<String,Integer,String> {
        private String imgType;
        @Override
        protected String doInBackground(String... voids) {
            loadingDialogFragment.show(getSupportFragmentManager(), "uploadLoading");
            String type=voids[0];
            imgType=voids[0];
            Map<String, String> m = new HashMap<>();
            m.put("mobile", user.getUserId());
            m.put("app", Constants.AppId);
            m.put("token", user.getToken());
            m.put("type", type);
            m.put("rowid", entity.getRowid());
            if(type.equals(TYPE_XHZ)){
                m.put("base64", map.get(XHZ));
            }else if(type.equals(TYPE_HDZ)){
                m.put("base64", map.get(HDZ));
            }

           return HttpTools.submitPostData(Constants.WEBSERVICE_URL+"showdata/uploadalctimages.jsp",m,"UTF-8");
        }

        @Override
        protected void onPostExecute(String s) {
            if(loadingDialogFragment!=null){
                loadingDialogFragment.dismiss();
            }
            String str = s.replaceAll("\r", "").replaceAll("\n", "");
            Log.e("upLoadImgToService","str=="+str);
            if(!TextUtils.isEmpty(str)){
                if(str.contains("{") && str.contains("}")){
                    int firstIndex=str.indexOf("{");
                    int lastIndex=str.lastIndexOf("}");
                    try {
                        JSONObject object=new JSONObject(str.substring(firstIndex,lastIndex+1));
                        if(object.getBoolean("success")){
                            if(imgType.equals(TYPE_XHZ)){
                                isSuccessXHZ=true;
                            }else if(imgType.equals(TYPE_HDZ)){
                                isSuccessHDZ=true;
                            }
                            Toast.makeText(UpLoadImgActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(UpLoadImgActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }




            }

        }
    }



   private void upLoadImgToService(String type,String buffer){
        if(user==null){
            return;
        }
       MyTask task=new MyTask();
        task.execute(type);
      /* Map<String, String> map = new HashMap<>();
       map.put("mobile", user.getUserId());
       map.put("app", Constants.AppId);
       map.put("token", user.getToken());
       map.put("type", type);
       map.put("rowid", entity.getRowid());
       map.put("base64", buffer);
       Log.e("base64","data:image/jpeg;base64,"+buffer);
       RequestManager.getInstance()
               .mServiceStore
               .uoload(map)
               .subscribeOn(io.reactivex.schedulers.Schedulers.io())
               .observeOn(AndroidSchedulers.mainThread())
               .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                   @Override
                   public void onSuccess(String msg) {
                       String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                       Log.e("upLoadImgToService","str=="+str);
                       Toast.makeText(UpLoadImgActivity.this, "上传成功！", Toast.LENGTH_SHORT).show();
                   }

                   @Override
                   public void onError(String msg) {
                       Log.e("upLoadImgToService", msg);
                       Toast.makeText(UpLoadImgActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
                   }
               }));*/
   }
    private void doUploadErrorMsg(String enterpriseCode, String s, String s1) {
        Map<String, String> map = new HashMap<>();
        map.put("alctErrorCode", s);
        map.put("rowid", enterpriseCode);
        map.put("erromessage", s1);
        RequestManager.getInstance()
                .mServiceStore
                .uplaodErrorInfo(map)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {

                    }

                    @Override
                    public void onError(String msg) {

                        Log.e("alct onError", msg);
                    }
                }));
    }

    private Image getImage(String buffer,String imgName){
        Image img=new Image();
        img.setBaiduLatitude(mCurrentLat);
        img.setBaiduLongitude(mCurrentLon);
        img.setFileExt("jpg");
        img.setFileData("data:image/jpeg;base64,"+buffer);
        img.setImageTakenDate(getNowtime());
        img.setFileName(imgName);
        img.setLocation(address);
        Log.e(imgName+"address==","address="+address+",mCurrentLat="+mCurrentLat+",mCurrentLon="+mCurrentLon);
        return img;

    }

    //卸货照
    private void uploadUnloadImage(String uploadBuffer,final LoadingDialogFragment loadingDialogFragment) {
        if(entity==null){
            return;
        }
        MDPLocationCollectionManager.uploadUnloadImage(UpLoadImgActivity.this, entity.getRowid(),
                entity.getAlctCode(), getImage(uploadBuffer,"unload"), new OnResultListener() {
                    @Override
                    public void onSuccess() {
                        Log.e("Unload","AlctCode="+entity.getAlctCode()+",Rowid="+entity.getRowid());
                        Toast.makeText(UpLoadImgActivity.this, "卸货照上传成功！", Toast.LENGTH_SHORT).show();
                        uploadPODImage(map.get(HDZ),loadingDialogFragment);

                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        Log.e("卸货照 s111",s1);
                        uploadPODImage(map.get(HDZ),loadingDialogFragment);
                        doUploadErrorMsg(entity.getAlctCode(),s,s1);
                    }
                });

    }

    //回单照
    private void uploadPODImage(String uploadBuffer,final LoadingDialogFragment loadingDialogFragment) {
        if(entity==null){
            return;
        }
        MDPLocationCollectionManager.uploadPODImage(UpLoadImgActivity.this, entity.getRowid(),
                entity.getAlctCode(), getImage(uploadBuffer,"pod"), new OnResultListener() {
                    @Override
                    public void onSuccess() {
                        Log.e("POD","AlctCode="+entity.getAlctCode()+",Rowid="+entity.getRowid());
                        Toast.makeText(UpLoadImgActivity.this, "回单照上传成功！", Toast.LENGTH_SHORT).show();
                        if(loadingDialogFragment!=null){
                            loadingDialogFragment.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        Log.e("回单照 s111",s1);
                        doUploadErrorMsg(entity.getAlctCode(),s,s1);
                        if(loadingDialogFragment!=null){
                            loadingDialogFragment.dismiss();

                        }
                    }
                });

    }


    //图片压缩
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

    private void setImg() {
        List<String> names = new ArrayList<>();
        names.add("拍照");
        showDialog(new SelectDialog.SelectDialogListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        Intent intent = new Intent(UpLoadImgActivity.this, ImageGridActivity.class);
                        intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                        startActivityForResult(intent, REQUEST_CODE_SELECT);
                        break;
                    case 1:
                        Intent intent1 = new Intent(UpLoadImgActivity.this, ImageGridActivity.class);
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
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mLocClient != null) {
            mLocClient.stop();
            mLocClient.unRegisterLocationListener(myListener);
        }

    }

}
