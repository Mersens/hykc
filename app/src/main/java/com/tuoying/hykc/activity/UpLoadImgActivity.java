package com.tuoying.hykc.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import android.widget.TextView;
import android.widget.Toast;

import com.alct.mdp.MDPLocationCollectionManager;
import com.alct.mdp.callback.OnDownloadResultListener;
import com.alct.mdp.callback.OnResultListener;
import com.alct.mdp.model.Image;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.tuoying.hykc.R;
import com.tuoying.hykc.adapter.ImagePickerAdapter;
import com.tuoying.hykc.adapter.OthersImgAdapter;
import com.tuoying.hykc.app.App;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.GoodsEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.utils.GlideImageLoader;
import com.tuoying.hykc.utils.HttpTools;
import com.tuoying.hykc.utils.ImageDownloader;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.ExitDialogFragment;
import com.tuoying.hykc.view.ImageExampleDialog;
import com.tuoying.hykc.view.LoadingDialogFragment;
import com.tuoying.hykc.view.SelectDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

public class UpLoadImgActivity extends BaseActivity implements ImagePickerAdapter.OnRecyclerViewItemClickListener {
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    public static final int XHZ = 1;//卸货照
    public static final int HDZ = 2;//回单照
    public static final int IMAGE_ITEM_ADD = -1;
    private static final String TYPE_HDZ="personal1.jpg";
    private static final String TYPE_XHZ="personal0.jpg";
    private static final String OTHERS_NAME="img_others";
    private final LoadingDialogFragment loadingDialogFragment = LoadingDialogFragment.getInstance();
    public MyLocationListenner myListener = new MyLocationListenner();
    ImageDownloader downloader=new ImageDownloader();
    private Toolbar mToolbar;
    private ImageView mImgXHZ;
    private ImageView mImgHDZ;
    private Button mBtnOk;
    private int maxImgCount = 6;
    private int imgNameIndex=1;
    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private RecyclerView recyclerView;
    private RecyclerView showRecyclerView;
    private int selectType = -1;
    private GoodsEntity entity;
    private boolean isSuccessXHZ=false;
    private boolean isSuccessHDZ=false;
    private boolean isAnlXHZ=false;
    private boolean isAnlHDZ=false;
    private User user;
    private String userid;
    private DBDao dao;
    private double mCurrentLat = 0.0;
    private double mCurrentLon = 0.0;
    private String address;
    private  Map<Integer,String> map=new HashMap<>();
    private LocationClient mLocClient;
    private View imglinView;
    private boolean isOthers=false;
    private TextView tv_ysc;
    private TextView mTextXHZExample;
    private TextView mTextHDZExample;

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

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case IMAGE_ITEM_ADD:
                List<String> names = new ArrayList<>();
                names.add("拍照");
                showDialog(new SelectDialog.SelectDialogListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0: // 直接调起相机
                                isOthers=true;
                                ImagePicker.getInstance().setSelectLimit(maxImgCount);
                                Intent intent = new Intent(UpLoadImgActivity.this, ImageGridActivity.class);
                                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                                startActivityForResult(intent, REQUEST_CODE_SELECT);
                                break;
                            case 1:
                                ImagePicker.getInstance().setSelectLimit(maxImgCount);
                                Intent intent1 = new Intent(UpLoadImgActivity.this, ImageGridActivity.class);
                                startActivityForResult(intent1, REQUEST_CODE_SELECT);
                                break;
                            default:
                                break;
                        }

                    }
                }, names);

                break;
            default:
                //打开预览
                Intent intentPreview = new Intent(this, ImagePreviewDelActivity.class);
                intentPreview.putExtra(ImagePicker.EXTRA_IMAGE_ITEMS, (ArrayList<ImageItem>) adapter.getImages());
                intentPreview.putExtra(ImagePicker.EXTRA_SELECTED_IMAGE_POSITION, position);
                intentPreview.putExtra(ImagePicker.EXTRA_FROM_ITEMS, true);
                startActivityForResult(intentPreview, REQUEST_CODE_PREVIEW);
                break;
        }
    }

    @Override
    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("选择照片");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        mTextXHZExample=findViewById(R.id.tv_xhz_example);
        mTextHDZExample=findViewById(R.id.tv_hdz_example);
        entity = (GoodsEntity) getIntent().getSerializableExtra("entity");
        mImgXHZ = findViewById(R.id.img_xhz);
        mImgHDZ = findViewById(R.id.img_hdz);
        tv_ysc=findViewById(R.id.tv_ysc);
        mBtnOk = findViewById(R.id.btn_ok);
        userid=SharePreferenceUtil.getInstance(this).getUserId();
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        showRecyclerView= (RecyclerView) findViewById(R.id.showRecyclerView);
        showRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        showRecyclerView.setHasFixedSize(true);
        selImageList = new ArrayList<>();
        imglinView=findViewById(R.id.imglinView);
        adapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
        adapter.setOnItemClickListener(this);
        recyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(adapter);
        dao=new DBDaoImpl(this);
        if(!TextUtils.isEmpty(userid)){
            user=dao.findUserInfoById(userid);
            getPicInfo();
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
        checkAlctImg();
    }

    private void checkAlctImg(){
        if(entity==null){
            return;
        }
        String shipmentCode=entity.getRowid();
        String enterpriseCode=entity.getAlctCode();
        MDPLocationCollectionManager.getUnloadImageNames(this, shipmentCode, enterpriseCode, new OnDownloadResultListener() {
            @Override
            public void onSuccess(Object o) {
                try {
                    JSONArray array=new JSONArray(o.toString());
                    if(array.length()>0){
                        isAnlXHZ=true;
                        setBtnStatu();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });

        MDPLocationCollectionManager.getPODImageNames(this, shipmentCode, enterpriseCode, new OnDownloadResultListener() {
            @Override
            public void onSuccess(Object o) {
                try {
                    JSONArray array=new JSONArray(o.toString());
                    if(array.length()>0){
                        isAnlHDZ=true;
                        setBtnStatu();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(String s, String s1) {

            }
        });
    }

    private void  setBtnStatu(){
        if(isSuccessHDZ && isSuccessXHZ && isAnlHDZ && isAnlXHZ){
            mBtnOk.setClickable(false);
            mBtnOk.setEnabled(false);
            mBtnOk.setBackgroundResource(R.drawable.btn_no_click_bg);

        }

    }

    private void getPicInfo() {
        loadingDialogFragment.showF(getSupportFragmentManager(),"PicInfoDialog");
        Map<String, String> m = new HashMap<>();
        m.put("mobile", user.getUserId());
        m.put("app", Constants.AppId);
        m.put("token", user.getToken());
        m.put("rowid", entity.getRowid());
        RequestManager.getInstance()
                .mServiceStore
                .findphoto(m)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("findphoto","msg="+msg);
                        loadingDialogFragment.dismissAllowingStateLoss();
                        try {
                            JSONObject object=new JSONObject(msg);
                            JSONObject xhzObject=new JSONObject(object.getString("xhz"));
                            String xhzUrl=xhzObject.getString("msg");
                            Log.e("xhzUrl","xhzUrl="+xhzUrl.length());
                            if(!TextUtils.isEmpty(xhzUrl)){
                               Bitmap bitmap= downloader.getBitmapFromMemCache(entity.getRowid()+ImageDownloader.XHZ);
                               if(bitmap==null){
                                   new BitmapThread(Constants.WEBSERVICE_URL+xhzUrl,entity.getRowid(),ImageDownloader.XHZ).start();
                                }else {
                                   ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                                   bitmap.compress(Bitmap.CompressFormat.JPEG,
                                           50, baos2);
                                   byte[] bytes = baos2.toByteArray();
                                   String uploadBuffer = Base64.encodeToString(bytes, Base64.DEFAULT).replaceAll(" ","");
                                   map.put(XHZ,uploadBuffer);
                               }
                                isSuccessXHZ=true;
                                Glide.with(UpLoadImgActivity.this)
                                        .load(Constants.WEBSERVICE_URL+xhzUrl)
                                        .skipMemoryCache(true)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .into(mImgXHZ);
                            }else {
                                isSuccessXHZ=false;

                            }
                            JSONObject hdzObject=new JSONObject(object.getString("hdz"));
                            String hdzUrl=hdzObject.getString("msg");
                            if(!TextUtils.isEmpty(hdzUrl)){
                                Bitmap bitmap= downloader.getBitmapFromMemCache(entity.getRowid()+ImageDownloader.HDZ);
                                if(bitmap==null){
                                    new BitmapThread(Constants.WEBSERVICE_URL+hdzUrl,entity.getRowid(),ImageDownloader.HDZ).start();
                                }else {
                                    ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                                    bitmap.compress(Bitmap.CompressFormat.JPEG,
                                            50, baos2);
                                    byte[] bytes = baos2.toByteArray();
                                    String uploadBuffer = Base64.encodeToString(bytes, Base64.DEFAULT).replaceAll(" ","");
                                    map.put(HDZ,uploadBuffer);
                                }
                                isSuccessHDZ=true;
                                Glide.with(UpLoadImgActivity.this)
                                        .load(Constants.WEBSERVICE_URL+hdzUrl)
                                        .skipMemoryCache(true)
                                        .diskCacheStrategy(DiskCacheStrategy.NONE)
                                        .into(mImgHDZ);
                            }else {
                                isSuccessHDZ=false;
                            }
                            setBtnStatu();
                            JSONObject othersObject=new JSONObject(object.getString("other"));
                            JSONArray array=new JSONArray(othersObject.getString("msg"));
                            List<String> urls=new ArrayList<>();
                            if(array.length()>0){
                                String baseUrl=Constants.WEBSERVICE_URL+"showdata/alctupload/"+entity.getRowid()+"/";
                                for (int i = 0; i <array.length() ; i++) {
                                    String name=array.getString(i);
                                    if(!name.contains("personal")){
                                        urls.add(baseUrl+name) ;
                                    }
                                }
                                if(urls.size()>0){
                                    imgNameIndex=urls.size()+1;
                                    imglinView.setVisibility(View.VISIBLE);
                                    tv_ysc.setVisibility(View.VISIBLE);
                                }else {
                                    imglinView.setVisibility(View.GONE);
                                    tv_ysc.setVisibility(View.GONE);
                                }
                                if(urls.size()==6){
                                    recyclerView.setVisibility(View.GONE);
                                    imglinView.setVisibility(View.GONE);
                                }
                                maxImgCount=maxImgCount-urls.size();
                                OthersImgAdapter adapter1=new OthersImgAdapter(urls,UpLoadImgActivity.this);
                                showRecyclerView.setAdapter(adapter1);
                                adapter.setMaxImgCount(maxImgCount);
           /*                     if(isSuccessHDZ && isSuccessXHZ){
                                    mBtnOk.setText("上传照片");
                                }else {
                                    mBtnOk.setText("返回");
                                }*/
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }

                    @Override
                    public void onError(String msg) {
                        loadingDialogFragment.dismissAllowingStateLoss();
                        Log.e("findphoto onError","msg="+msg);

                    }
                }));

    }

    private void initEvent() {
        mImgXHZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showXHZPicView(1,"xhzview");
            }
        });
        mImgHDZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHDZPicView(2,"hdzview");
            }
        });

        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                String text=mBtnOk.getText().toString();
                if("返回".equals(text)){
                    finish();
                    overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
                }else if("上传照片".equals(text)){

                }*/

                doSave();
            }
        });
        mTextXHZExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogView(1,"xhzDialog");
            }
        });
        mTextHDZExample.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDialogView(2,"hdzDialog");
            }
        });

    }

    private void showDialogView(int type,String tag){
        final ImageExampleDialog dialog=ImageExampleDialog.getInstance(type);
        dialog.showF(getSupportFragmentManager(),tag);
        dialog.setOnButtonClickListener(new ImageExampleDialog.OnButtonClickListener() {
            @Override
            public void onClick() {
                dialog.dismissAllowingStateLoss();
            }
        });
    }

    private void showXHZPicView(int type,String tag){
        final ImageExampleDialog dialog=ImageExampleDialog.getInstance(type);
        dialog.showF(getSupportFragmentManager(),tag);
        dialog.setOnButtonClickListener(new ImageExampleDialog.OnButtonClickListener() {
            @Override
            public void onClick() {
                dialog.dismissAllowingStateLoss();
                selectType = XHZ;
                setImg();
            }
        });
    }
    private void showHDZPicView(int type,String tag){
        final ImageExampleDialog dialog=ImageExampleDialog.getInstance(type);
        dialog.showF(getSupportFragmentManager(),tag);
        dialog.setOnButtonClickListener(new ImageExampleDialog.OnButtonClickListener() {
            @Override
            public void onClick() {
                dialog.dismissAllowingStateLoss();
                selectType = HDZ;
                setImg();
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
        mBtnOk.setClickable(false);
        mBtnOk.setEnabled(false);
        mBtnOk.setBackgroundResource(R.drawable.btn_no_click_bg);
        uploadUnloadImage(map.get(XHZ));
        uploadPODImage(map.get(HDZ));
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
                if(isOthers){
                    if (imgs != null) {
                        selImageList.addAll(imgs);
                        adapter.setImages(selImageList);
                        if (imgs != null && imgs.size() > 0) {
                            saveImgToService(imgs.get(0));
                        }
                    }
                }else {
                    if (imgs != null && imgs.size() > 0) {
                        setImage(imgs.get(0));
                    }
                }


            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                ArrayList<ImageItem> imgs = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                if(isOthers){
                    if (imgs != null) {
                        selImageList.addAll(imgs);
                        adapter.setImages(selImageList);
                        if (imgs != null && imgs.size() > 0) {
                            saveImgToService(imgs.get(0));
                        }
                    }
                }else {
                    if (imgs != null && imgs.size() > 0) {
                        setImage(imgs.get(0));
                    }
                }

            }
        }
    }

    private void saveImgToService(ImageItem item){
        if (item == null) {
            return;
        }
        Bitmap bitmap = compressImg(item.path);
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,
                50, baos2);
        byte[] bytes = baos2.toByteArray();
        String uploadBuffer = Base64.encodeToString(bytes, Base64.DEFAULT).replaceAll(" ","");
        OthersImgTask task=new OthersImgTask(OTHERS_NAME+imgNameIndex+".jpg",uploadBuffer);
        task.execute();
    }

    private void setImage(ImageItem item) {
        ImageView imageView = null;
        if (item == null) {
            return;
        }
        switch (selectType) {
            case XHZ:
                Bitmap bitmap1 = compressImg(item.path);
                downloader.addBitmapToMemory(entity.getRowid()+ImageDownloader.HDZ,bitmap1);
                imageView = mImgXHZ;
                break;
            case HDZ:
                Bitmap bitmap2 = compressImg(item.path);
                downloader.addBitmapToMemory(entity.getRowid()+ImageDownloader.HDZ,bitmap2);
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
    private void uploadUnloadImage(String uploadBuffer) {
        if(entity==null){
            return;
        }
        MDPLocationCollectionManager.uploadUnloadImage(UpLoadImgActivity.this, entity.getRowid(),
                entity.getAlctCode(), getImage(uploadBuffer,"unload"), new OnResultListener() {
                    @Override
                    public void onSuccess() {
                        //step:26
                        //tel:157****0385
                        //msg:登录获取用户信息成功
                        //time:2018-12-18:11:11:10
                        //rowid:
                        isAnlXHZ=true;
                        Map<String,String> m=new HashMap<>();
                        m.put("step","26");
                        m.put("tel",user.getUserId());
                        m.put("msg","安联卸货照上传成功");
                        m.put("time",getNowtime());
                        m.put("rowid",entity.getRowid());
                        upLoadUserLog(m);
                        Log.e("Unload","AlctCode="+entity.getAlctCode()+",Rowid="+entity.getRowid());
                        Toast.makeText(UpLoadImgActivity.this, "卸货照上传成功！", Toast.LENGTH_SHORT).show();


                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        //step:27
                        //tel:157****0385
                        //msg:登录获取用户信息成功
                        //time:2018-12-18:11:11:10
                        //rowid:
                        isAnlXHZ=false;
                        Map<String,String> m1=new HashMap<>();
                        m1.put("step","27");
                        m1.put("tel",user.getUserId());
                        m1.put("msg","安联卸货照上传失败，失败原因："+"s="+s+":s1="+s1);
                        m1.put("time",getNowtime());
                        m1.put("rowid",entity.getRowid());
                        String param="AlctCode="+entity.getAlctCode()+"; Rowid="+entity.getRowid()+";货源信息="+entity.toString();
                        m1.put("param",param);
                        upLoadUserLog(m1);
                        Log.e("卸货照 s111",s1);
                        doUploadErrorMsg(entity.getAlctCode(),s,s1);
                        mBtnOk.setClickable(true);
                        mBtnOk.setEnabled(true);
                        mBtnOk.setBackgroundResource(R.drawable.btn_cz_bg);
                    }
                });
    }

    //回单照
    private void uploadPODImage(String uploadBuffer) {
        if(entity==null){
            return;
        }
        MDPLocationCollectionManager.uploadPODImage(UpLoadImgActivity.this, entity.getRowid(),
                entity.getAlctCode(), getImage(uploadBuffer,"pod"), new OnResultListener() {
                    @Override
                    public void onSuccess() {
                        //step:28
                        //tel:157****0385
                        //msg:登录获取用户信息成功
                        //time:2018-12-18:11:11:10
                        //rowid:
                        isAnlHDZ=true;
                        Map<String,String> map=new HashMap<>();
                        map.put("step","28");
                        map.put("tel",user.getUserId());
                        map.put("msg","安联回单照上传成功");
                        map.put("time",getNowtime());
                        map.put("rowid",entity.getRowid());
                        upLoadUserLog(map);
                        Log.e("POD","AlctCode="+entity.getAlctCode()+",Rowid="+entity.getRowid());
                        Toast.makeText(UpLoadImgActivity.this, "回单照上传成功！", Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onFailure(String s, String s1) {
                        //step:29
                        //tel:157****0385
                        //msg:登录获取用户信息成功
                        //time:2018-12-18:11:11:10
                        //rowid:
                        isAnlHDZ=false;
                        Map<String,String> map=new HashMap<>();
                        map.put("step","29");
                        map.put("tel",user.getUserId());
                        map.put("msg","安联回单照上传失败，失败原因："+"s="+s+":s1="+s1);
                        map.put("time",getNowtime());
                        map.put("rowid",entity.getRowid());
                        String param="AlctCode="+entity.getAlctCode()+"; Rowid="+entity.getRowid()+";货源信息="+entity.toString();
                        map.put("param",param);
                        upLoadUserLog(map);
                        Log.e("回单照 s111",s1);
                        doUploadErrorMsg(entity.getAlctCode(),s,s1);
                        mBtnOk.setClickable(true);
                        mBtnOk.setEnabled(true);
                        mBtnOk.setBackgroundResource(R.drawable.btn_cz_bg);
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
                        isOthers=false;
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
            onBackPressed();

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

    private void upLoadUserLog(Map<String, String> params){
        RequestManager.getInstance()
                .mServiceStore
                .uplog(params)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {

                    }

                    @Override
                    public void onError(String msg) {

                    }
                }));

    }

    @Override
    public void onBackPressed() {
        if(isSuccessXHZ && isSuccessHDZ){
            if(isAnlHDZ && isAnlXHZ){
                super.onBackPressed();
            }else {
                confirmExit("请点击按钮上传图片");
            }

        }else {
            confirmExit("请上传图片");
        }

    }

    class BitmapThread extends Thread {
        private String bitmapUrl;
        private String rowid;
        private String type;
        BitmapThread(String bitmapUrl,String rowid,String type) {
            this.bitmapUrl = bitmapUrl;
            this.rowid=rowid;
            this.type=type;
        }
        @Override
        public void run() {
            Bitmap bitmap = null;
            HttpURLConnection connection = null;
            InputStream inputStream = null;
            try {
                URL url = new URL(bitmapUrl);
                connection = (HttpURLConnection) url.openConnection();
                connection.setConnectTimeout(5000);
                connection.setRequestMethod("GET");
                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    inputStream = connection.getInputStream();
                    bitmap = BitmapFactory.decodeStream(inputStream);
                }
                if(bitmap!=null){
                    if(ImageDownloader.XHZ.equals(type)){
                        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,
                                50, baos2);
                        byte[] bytes = baos2.toByteArray();
                        String uploadBuffer = Base64.encodeToString(bytes, Base64.DEFAULT).replaceAll(" ","");
                        map.put(XHZ,uploadBuffer);
                    }else if(ImageDownloader.HDZ.equals(type)){
                        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
                        bitmap.compress(Bitmap.CompressFormat.JPEG,
                                50, baos2);
                        byte[] bytes = baos2.toByteArray();
                        String uploadBuffer = Base64.encodeToString(bytes, Base64.DEFAULT).replaceAll(" ","");
                        map.put(HDZ,uploadBuffer);
                    }
                    downloader. addBitmapToMemory(rowid+type, bitmap);
                }

            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private class OthersImgTask extends AsyncTask<String,Integer,String> {
        private String type;
        private String imgBuffer;
        public OthersImgTask(String type,String imgBuffer){
            this.type=type;
            this.imgBuffer=imgBuffer;
        }

        @Override
        protected String doInBackground(String... voids) {
            loadingDialogFragment.showF(getSupportFragmentManager(), "uploadLoading");
            Map<String, String> m = new HashMap<>();
            m.put("mobile", user.getUserId());
            m.put("app", Constants.AppId);
            m.put("token", user.getToken());
            m.put("type", type);
            m.put("rowid", entity.getRowid());
            m.put("base64", imgBuffer);
            return HttpTools.submitPostData(Constants.WEBSERVICE_URL+"showdata/uploadalctimages.jsp",m,"UTF-8");
        }

        @Override
        protected void onPostExecute(String s) {
            if(loadingDialogFragment!=null){
                loadingDialogFragment.dismissAllowingStateLoss();
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
                            if(UpLoadImgActivity.this!=null){
                                Toast.makeText(UpLoadImgActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                            imgNameIndex=imgNameIndex+1;
                            }
                        }else {
                            if(UpLoadImgActivity.this!=null) {
                                Toast.makeText(UpLoadImgActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();
                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
    }

    private void confirmExit(String msg) {
        //退出操作
        final ExitDialogFragment dialog = ExitDialogFragment.getInstance(msg);
        dialog.show(getSupportFragmentManager(), "uploadImg");
        dialog.setOnDialogClickListener(new ExitDialogFragment.OnDialogClickListener() {
            @Override
            public void onClickCancel() {
                dialog.dismiss();
                finish();
                overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);

            }

            @Override
            public void onClickOk() {
                dialog.dismiss();

            }
        });


    }

    private class MyTask extends AsyncTask<String,Integer,String> {
        private String imgType;
        @Override
        protected String doInBackground(String... voids) {
            loadingDialogFragment.showF(getSupportFragmentManager(), "uploadLoading");
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
                loadingDialogFragment.dismissAllowingStateLoss();
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
/*                            if(isSuccessHDZ && isSuccessXHZ){
                                mBtnOk.setText("上传照片");
                            }else {
                                mBtnOk.setText("返回");
                            }*/
                            Toast.makeText(UpLoadImgActivity.this, "保存成功！", Toast.LENGTH_SHORT).show();
                        }else {
                            Toast.makeText(UpLoadImgActivity.this, "保存失败！", Toast.LENGTH_SHORT).show();

                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

            }

        }
    }
}
