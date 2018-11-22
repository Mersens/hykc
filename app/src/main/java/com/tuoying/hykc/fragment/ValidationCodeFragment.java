package com.tuoying.hykc.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.tuoying.hykc.R;
import com.tuoying.hykc.view.ValidationCodeView;

public class ValidationCodeFragment extends DialogFragment {
    OnValidationComplListener listener;
    private String msg;
    private TextView mTextCancel;
    private TextView mTextOk;
    private TextView mTextTitle;
    private EditText mEdtCode;
    private ValidationCodeView validationCode;

    public static ValidationCodeFragment getInstance(String msg){
        ValidationCodeFragment fragment= new ValidationCodeFragment();
        Bundle bundle=new Bundle();
        bundle.putString("msg",msg);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
        return inflater.inflate(R.layout.layout_validation, null);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }

    private void initViews(View view) {
        msg=getArguments().getString("msg");
        mTextCancel=view.findViewById(R.id.tv_cancel);
        mTextOk=view.findViewById(R.id.tv_ok);
        mTextTitle=view.findViewById(R.id.tv_title);
        if(!TextUtils.isEmpty(msg)){
            mTextTitle.setText(msg);
        }
        mEdtCode=view.findViewById(R.id.editcode);
        validationCode=view.findViewById(R.id.validationCode);
        initEvent();

    }

    private void initEvent() {
        mTextCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onClickCancel();
                }
            }
        });
        mTextOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    String proCode=validationCode.getCodeString().toUpperCase();
                    String setCode=mEdtCode.getText().toString().toUpperCase();
                    if(proCode.equals(setCode)){
                        listener.onClickOk();
                    }else {
                        Toast.makeText(getActivity(), "验证码错误，请重新输入！", Toast.LENGTH_SHORT).show();

                    }

                }

            }
        });
    }

    public void showF(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    public void setOnValidationComplListener(OnValidationComplListener listener){
        this.listener=listener;
    }

    public interface OnValidationComplListener{
        void onClickCancel();
        void onClickOk();
    }

}
