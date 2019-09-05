package com.tuoying.hykc.view;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tuoying.hykc.R;

public class FactTestDialog extends DialogFragment {
    private OnCheckListener listener;

    private ImageView mImgClose;
    private TextView mTextAccount;
    private TextView mTextName;
    private TextView mTextIdNo;
    private Button mBtn;

    public static FactTestDialog newInstance(String account,String name,String idNo){
        FactTestDialog dialog=new FactTestDialog();
        Bundle bundle=new Bundle();
        bundle.putString("account",account);
        bundle.putString("name",name);
        bundle.putString("idNo",idNo);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Override
    public void show(FragmentManager manager, String tag) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, tag);
        ft.commitAllowingStateLoss();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        return inflater.inflate(R.layout.layout_facetest,container,false);
    }

    @Override
    public void onViewCreated(@NonNull View view,
                              @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initViews(view);
    }


    private void initViews(View view) {

        mImgClose=view.findViewById(R.id.img_close);
        mTextAccount=view.findViewById(R.id.tv_account);
        mTextName=view.findViewById(R.id.tv_name);
        mTextIdNo=view.findViewById(R.id.tv_idno);
        mBtn=view.findViewById(R.id.btn_ok);
        Bundle bundle=getArguments();
        if(bundle==null){
            Toast.makeText(getActivity(), "数据为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        String account=bundle.getString("account");
        String name=bundle.getString("name");
        String idNo=bundle.getString("idNo");
        mTextAccount.setText(account);
        mTextName.setText(name);
        mTextIdNo.setText(idNo);
        mImgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onDismiss();
                }
            }
        });
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onCheck();
                }
            }
        });

    }



    public void setOnCheckListener(OnCheckListener listener){
        this.listener=listener;
    }

    public interface OnCheckListener{
        void onCheck();
        void onDismiss();
    }


}
