package com.tuoying.hykc.view;

import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.OilActivity;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.service.ServiceStore;
import com.tuoying.hykc.utils.QRCodeHelper;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class QRCDialogView extends DialogFragment {
    private String username;
    private ImageView imgClose;
    private ImageView imgQRC;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);
        return inflater.inflate(R.layout.layout_qrc,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        username=getArguments().getString("username");
        initView(view);
        getPayCode();
    }

    private void initView(View view) {
        imgClose=view.findViewById(R.id.img_close);
        imgQRC=view.findViewById(R.id.img_qrc);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }
    private void getPayCode() {
        Map<String,String> map=new HashMap<>();
        map.put("username",username);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.OIL_URL_TEST).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call=serviceStore.getPayCode(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody body=response.body();
                String str= null;
                try {
                    if(null!=body){
                        str = body.string();
                        JSONObject object=new JSONObject(str);
                        int code=object.getInt("code");
                        if(code==0){
                            String strResult=object.getString("result");
                            JSONObject jsonObject=new JSONObject(strResult);
                            String pay_code=jsonObject.getString("pay_code").replaceAll("\\\\","");
                            Log.e("pay_code","pay_code==="+pay_code);
                           Bitmap bitmap = QRCodeHelper.createQRCodeBitmap(pay_code,
                                   200, 200, "UTF-8", "H", "1", Color.BLACK, Color.WHITE, null, 0.2F);
                           if(bitmap!=null){
                               imgQRC.setImageBitmap(bitmap);
                           }
                        }else {
                            imgQRC.setImageResource(R.drawable.ocr_error);
                        }
                    }else {
                        imgQRC.setImageResource(R.drawable.ocr_error);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    imgQRC.setImageResource(R.drawable.ocr_error);
                }
                Log.e("getPayCode","getPayCode=="+str);
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                imgQRC.setImageResource(R.drawable.ocr_error);
                Log.e("onFailure","onFailure=="+t.getMessage());
            }
        });
    }
    public static QRCDialogView newInstance(String username){
        QRCDialogView qrcDialogView=new QRCDialogView();
        Bundle bundle=new Bundle();
        bundle.putString("username",username);
        qrcDialogView.setArguments(bundle);
        return qrcDialogView;
    }

}
