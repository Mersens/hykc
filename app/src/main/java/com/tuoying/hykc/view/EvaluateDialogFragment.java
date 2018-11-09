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
import android.widget.ImageView;
import android.widget.Toast;

import com.cb.ratingbar.CBRatingBar;
import com.tuoying.hykc.R;

public class EvaluateDialogFragment extends DialogFragment {
    private ImageView mImageClose;
    private Button mBtnOk;
    private ContainsEmojiEditText mEdit;
    private OnReasonDialogListener listener;
    private String reason;
    private CBRatingBar rating_bar;
    private int index=0;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        return inflater.inflate(R.layout.layout_evaluate,container,true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mImageClose=view.findViewById(R.id.img_close);
        mBtnOk=view.findViewById(R.id.btn_ok);
        mEdit=view.findViewById(R.id.editText);
        rating_bar=view.findViewById(R.id.rating_bar);
        rating_bar.setOnStarTouchListener(new CBRatingBar.OnStarTouchListener() {
            @Override
            public void onStarTouch(int touchCount) {
                index=touchCount;

            }
        });
        mImageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    listener.onCloseListener();
                }
            }
        });
        mBtnOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(listener!=null){
                    reason=mEdit.getText().toString().trim();
                    if(index==0){
                        Toast.makeText(getActivity(), "请选择分值！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if(TextUtils.isEmpty(reason)){
                        Toast.makeText(getActivity(), "请填写原因！", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    listener.onComplateListener(index+"",reason);
                }
            }
        });
    }


    public static EvaluateDialogFragment getInstance(){
        EvaluateDialogFragment fragment=new EvaluateDialogFragment();
        return  fragment;

    }

    public void setOnReasonDialogListener(OnReasonDialogListener listener){
        this.listener=listener;

    }


    public interface  OnReasonDialogListener{
        void onCloseListener();
        void onComplateListener(String index, String reasons);
    }

}
