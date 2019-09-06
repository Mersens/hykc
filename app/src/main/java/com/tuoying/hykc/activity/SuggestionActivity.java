package com.tuoying.hykc.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.lzy.imagepicker.ImagePicker;
import com.lzy.imagepicker.bean.ImageItem;
import com.lzy.imagepicker.ui.ImageGridActivity;
import com.lzy.imagepicker.ui.ImagePreviewDelActivity;
import com.lzy.imagepicker.view.CropImageView;
import com.tuoying.hykc.R;
import com.tuoying.hykc.adapter.ImagePickerAdapter;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.service.ServiceStore;
import com.tuoying.hykc.utils.GlideImageLoader;
import com.tuoying.hykc.utils.SharePreferenceUtil;
import com.tuoying.hykc.view.ContainsEmojiEditText;
import com.tuoying.hykc.view.LoadingDialogFragment;
import com.tuoying.hykc.view.SelectDialog;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SuggestionActivity extends BaseActivity implements View.OnClickListener , ImagePickerAdapter.OnRecyclerViewItemClickListener {
    public static final int REQUEST_CODE_SELECT = 100;
    public static final int REQUEST_CODE_PREVIEW = 101;
    public static final int IMAGE_ITEM_ADD = -1;
    private Toolbar mToolbar;
    private EditText mEditMobil;
    private TextView mEditSYWT;
    private TextView mEditGNJY;
    private TextView mEditCXWT;
    private TextView mEditQTFK;
    private ContainsEmojiEditText mEditText;
    private RecyclerView mRecyclerView;
    private Button mBtn;
    private int selectBg;
    private int unSelectBg;
    private int selectColor;
    private int unSelectColor;
    private int maxImgCount = 3;
    private String id;
    private ImagePickerAdapter adapter;
    private ArrayList<ImageItem> selImageList; //当前选择的所有图片
    private String []types=new String[]{
            "使用问题",
            "功能建议",
            "程序问题",
            "其他反馈"};
    private int index;
    private StringBuffer sbf=new StringBuffer();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_suggestin);
        init();
    }

    @Override
    public void init() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitle("意见反馈");
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        selectBg=R.drawable.suggestion_type_select_bg;
        unSelectBg=R.drawable.suggestion_type_normal_bg;
        selectColor=getResources().getColor(R.color.white);
        unSelectColor=getResources().getColor(R.color.text_color);
        initImagePicker();
        initView();
        initEvent();
    }
    private void initEvent() {
        mEditSYWT.setOnClickListener(this);
        mEditGNJY.setOnClickListener(this);
        mEditCXWT.setOnClickListener(this);
        mEditQTFK.setOnClickListener(this);
        mBtn.setOnClickListener(this);

    }

    private void initView() {
        id=SharePreferenceUtil.getInstance(this).getUserId();
        mEditMobil=findViewById(R.id.editPhone);
        mEditMobil.setText(id);
        mEditSYWT=findViewById(R.id.tv_sywt);
        mEditGNJY=findViewById(R.id.tv_gnjy);
        mEditCXWT=findViewById(R.id.tv_cxwt);
        mEditQTFK=findViewById(R.id.tv_qtfk);
        mEditText=findViewById(R.id.editText);
        mBtn=findViewById(R.id.btn_ok);
        mRecyclerView=findViewById(R.id.recyclerView);
        selImageList = new ArrayList<>();
        adapter = new ImagePickerAdapter(this, selImageList, maxImgCount);
        adapter.setOnItemClickListener(this);
        mRecyclerView.setLayoutManager(new GridLayoutManager(this, 3));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(adapter);
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


    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_sywt:
                index=0;
                setSelectColor(1);
                break;
            case R.id.tv_gnjy:
                index=1;
                setSelectColor(2);
                break;
            case R.id.tv_cxwt:
                index=2;
                setSelectColor(3);
                break;
            case R.id.tv_qtfk:
                index=3;
                setSelectColor(4);
                break;
            case R.id.btn_ok:
                doSave();
                break;
        }
    }

    private void setSelectColor(int pos){
        resetColor();
        switch (pos){
            case 1:
                mEditSYWT.setBackgroundResource(selectBg);
                mEditSYWT.setTextColor(selectColor);
                break;
            case 2:
                mEditGNJY.setBackgroundResource(selectBg);
                mEditGNJY.setTextColor(selectColor);
                break;
            case 3:
                mEditCXWT.setBackgroundResource(selectBg);
                mEditCXWT.setTextColor(selectColor);
                break;
            case 4:
                mEditQTFK.setBackgroundResource(selectBg);
                mEditQTFK.setTextColor(selectColor);
                break;

        }
    }

    private void resetColor() {
        mEditSYWT.setBackgroundResource(unSelectBg);
        mEditSYWT.setTextColor(unSelectColor);

        mEditGNJY.setBackgroundResource(unSelectBg);
        mEditGNJY.setTextColor(unSelectColor);

        mEditCXWT.setBackgroundResource(unSelectBg);
        mEditCXWT.setTextColor(unSelectColor);

        mEditQTFK.setBackgroundResource(unSelectBg);
        mEditQTFK.setTextColor(unSelectColor);
    }

    private void doSave() {
        final String mobile=mEditMobil.getText().toString().trim();
        final String questionType=types[index];
        final String questionContent=mEditText.getText().toString().trim();
        if(TextUtils.isEmpty(mobile)){
            Toast.makeText(this, "请填写联系方式！", Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(questionContent)){
            Toast.makeText(this, "请填写反馈内容", Toast.LENGTH_SHORT).show();
            return;
        }
        String urls=sbf.toString();
        if(urls.length()>0){
            urls=urls.substring(0,urls.length()-1);
        }
        final LoadingDialogFragment dialogFragment=LoadingDialogFragment.getInstance();
        dialogFragment.showF(getSupportFragmentManager(),"suggView");
        Map<String,String> map=new HashMap<>();
        map.put("mobile",mobile);
        map.put("questionType",questionType);
        map.put("questionContent",questionContent);
        map.put("source","1");
        map.put("imgUrl",urls);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.MOX_URL).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call=serviceStore.addSuggestionInfo(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                dialogFragment.dismissAllowingStateLoss();
                ResponseBody body=response.body();
                String str= null;
                try {
                    if(null!=body){
                        str = body.string();
                        JSONObject object=new JSONObject(str);
                        if(object.getBoolean("success")){
                            Toast.makeText(SuggestionActivity.this, "提交成功！", Toast.LENGTH_SHORT).show();

                        }else {
                            String string=object.getString("message");
                            Toast.makeText(SuggestionActivity.this, string, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(SuggestionActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("uploadSuggImg","uploadSuggImg=="+str);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                dialogFragment.dismissAllowingStateLoss();
                Log.e("onFailure","onFailure=="+t.getMessage());
                Toast.makeText(SuggestionActivity.this, "提交失败", Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        switch (position) {
            case IMAGE_ITEM_ADD:
                List<String> names = new ArrayList<>();
                names.add("拍照");
                names.add("相册");
                showDialog(new SelectDialog.SelectDialogListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        switch (position) {
                            case 0: // 直接调起相机
                                ImagePicker.getInstance().setSelectLimit(1);
                                Intent intent = new Intent(SuggestionActivity.this, ImageGridActivity.class);
                                intent.putExtra(ImageGridActivity.EXTRAS_TAKE_PICKERS, true); // 是否是直接打开相机
                                startActivityForResult(intent, REQUEST_CODE_SELECT);
                                break;
                            case 1:
                                ImagePicker.getInstance().setSelectLimit(1);
                                Intent intent1 = new Intent(SuggestionActivity.this, ImageGridActivity.class);
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == ImagePicker.RESULT_CODE_ITEMS) {
            if (data != null && requestCode == REQUEST_CODE_SELECT) {
                ArrayList<ImageItem> imgs = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_RESULT_ITEMS);
                    if (imgs != null) {
                        selImageList.addAll(imgs);
                        adapter.setImages(selImageList);
                        if (imgs != null && imgs.size() > 0) {
                            saveImgToService(imgs.get(0));
                        }
                    }

            }
        } else if (resultCode == ImagePicker.RESULT_CODE_BACK) {
            if (data != null && requestCode == REQUEST_CODE_PREVIEW) {
                ArrayList<ImageItem> imgs = (ArrayList<ImageItem>) data.getSerializableExtra(ImagePicker.EXTRA_IMAGE_ITEMS);
                    if (imgs != null) {
                        selImageList.addAll(imgs);
                        adapter.setImages(selImageList);
                        if (imgs != null && imgs.size() > 0) {
                            saveImgToService(imgs.get(0));
                        }
                    }
            }
        }
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
    private SelectDialog showDialog(SelectDialog.SelectDialogListener listener, List<String> names) {
        SelectDialog dialog = new SelectDialog(this, R.style
                .transparentFrameWindowStyle,
                listener, names);
        if (!this.isFinishing()) {
            dialog.show();
        }
        return dialog;
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
    private void saveImgToService(ImageItem item){
        Bitmap bitmap = compressImg(item.path);
        ByteArrayOutputStream baos2 = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,
                50, baos2);
        byte[] bytes = baos2.toByteArray();
        String uploadBuffer = Base64.encodeToString(bytes, Base64.DEFAULT).replaceAll("\\+","-");
        upLoadImg(id,uploadBuffer);
    }


    private void upLoadImg(String mobile ,String base64){
        Map<String,String> map=new HashMap<>();
        map.put("mobile",mobile);
        map.put("fileName",new Date().getTime()+".jpg");
        map.put("base64",base64);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.MOX_URL).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call=serviceStore.uploadSuggImg(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody body=response.body();
                String str= null;
                try {
                    if(null!=body){
                        str = body.string();
                        JSONObject object=new JSONObject(str);

                        if(object.getBoolean("success")){
                            Toast.makeText(SuggestionActivity.this, "上传成功", Toast.LENGTH_SHORT).show();
                            if(object.has("url")){
                                String url=object.getString("url");
                                sbf.append(url+";");
                            }

                        }else {
                            String string=object.getString("message");
                            Toast.makeText(SuggestionActivity.this, string, Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(SuggestionActivity.this, "上传失败！", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.e("uploadSuggImg","uploadSuggImg=="+str);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("onFailure","onFailure=="+t.getMessage());
                Toast.makeText(SuggestionActivity.this, "上传失败", Toast.LENGTH_SHORT).show();

            }
        });

    }

}
