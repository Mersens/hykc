package com.tuoying.hykc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.OrderActivity;
import com.tuoying.hykc.entity.GoodsEntity;

public class BottomMsgFragment extends BaseFragment {
    @Override
    public void init(View v) {

    }
   /* private GoodsEntity entity;
    private int type;
    private TextView mTextFhr;
    private TextView mTextTel;
    private TextView mTextName;
    private TextView mTextStartAddress;
    private TextView mTextEndAddress;
    private Button mBtn;
    private TextView mTextCancel;
    private TextView mTextWc;
    private OrderActivity orderActivity;
    private TextView mTextShr;
    private TextView mTextShrPhone;


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        orderActivity=(OrderActivity)context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.layout_bottom_msg,null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Bundle bundle=getArguments();
        entity= (GoodsEntity) bundle.getSerializable("entity");
        type=bundle.getInt("type");
        init(view);
    }

    @Override
    public void init(View v) {
        mTextFhr=v.findViewById(R.id.tv_fhr);
        mTextName=v.findViewById(R.id.tv_name);
        mTextStartAddress=v.findViewById(R.id.tv_start);
        mTextEndAddress=v.findViewById(R.id.tv_end);
        mTextCancel=v.findViewById(R.id.tv_qx);
        mTextWc=v.findViewById(R.id.tv_wc);
        mTextShr=v.findViewById(R.id.tv_shr);
        mTextShrPhone=v.findViewById(R.id.tv_shr_tel);
        mBtn=v.findViewById(R.id.btn);
        initEvent();
        initDatas();
    }

    private void initEvent() {
        mTextCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderActivity!=null){
                    orderActivity.onCancelClick(entity);
                }

            }
        });
        mTextWc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(orderActivity!=null){
                    orderActivity.onComplateClick(entity);
                }
            }
        });
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean isGuiding=entity.isGuiding();
                if(isGuiding){
                    mBtn.setText("导航");
                    entity.setGuiding(false);

                }else {
                    entity.setGuiding(true);
                    mBtn.setText("正在导航");

                }
                if(orderActivity!=null){
                    orderActivity.onButtonClick(entity);
                }
            }
        });
    }

    private void initDatas() {
        mTextFhr.setText(entity.getHzxm());
        mTextName.setText(entity.getName());
        mTextStartAddress.setText(entity.getStartAddress());
        mTextEndAddress.setText(entity.getEndAddress());
        mTextShr.setText(entity.getShrName());
        mTextShrPhone.setText(entity.getShrTel());
        boolean isGuiding=entity.isGuiding();
        if(isGuiding){
            mBtn.setText("正在导航");
        }
        if(type==1){
            mTextWc.setText("配送");
        }
    }

    public static BottomMsgFragment getInstance(GoodsEntity entity,int type){
        BottomMsgFragment  fragment=new BottomMsgFragment();
        Bundle bundle=new Bundle();
        bundle.putInt("type",type);
        bundle.putSerializable("entity",entity);
        fragment.setArguments(bundle);
        return fragment;
    }

    private OnBottomClickListener listener;

    public interface OnBottomClickListener{
        void onCancelClick(GoodsEntity entity);
        void onComplateClick(GoodsEntity entity);
        void onButtonClick(GoodsEntity entity);
    }
*/
}
