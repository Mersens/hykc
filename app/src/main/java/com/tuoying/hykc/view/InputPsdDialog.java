package com.tuoying.hykc.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.tuoying.hykc.R;


/**
 * Created by Administrator on 2018/3/30.
 */

public class InputPsdDialog extends DialogFragment {
    private ImageView mImageClose;
    private Button mBtn;
    OnOrderListener listener;
    private EditText mEditPsd;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        return inflater.inflate(R.layout.layout_inputpsd, container, true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mImageClose = view.findViewById(R.id.img_close);
        mBtn = view.findViewById(R.id.btn_ok);
        mEditPsd = view.findViewById(R.id.editpsd);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String psd=mEditPsd.getText().toString().trim();
                if(TextUtils.isEmpty(psd)){
                    Toast.makeText(getActivity(), "请输入钱包密码", Toast.LENGTH_SHORT).show();
                    return;

                }
                if(listener!=null){
                    listener.onOrder(psd);

                }


            }
        });
        mImageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }

    public static InputPsdDialog getInstance() {
        InputPsdDialog dialog = new InputPsdDialog();
        return dialog;
    }


    public void setOnOrderListener(OnOrderListener listener) {
        this.listener = listener;
    }


    public interface OnOrderListener {
        void onOrder(String psd);
    }
}
