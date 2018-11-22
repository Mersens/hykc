package com.tuoying.hykc.view;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.MyCardActivity;
import com.tuoying.hykc.adapter.SelectMyCardAdapter;
import com.tuoying.hykc.entity.CardEntity;

import java.io.Serializable;
import java.util.List;


/**
 * Created by Administrator on 2018/3/30.
 */

public class TXMoneyDialog extends DialogFragment {
    OnSelectListener listener;
    private ImageView mImageClose;
    private TextView mTitle;
    private List<CardEntity> mList;
    private ImageView mImgAdd;
    private RelativeLayout mLayoutNo;
    private ListView mListView;

    public static TXMoneyDialog getInstance(List<CardEntity> list) {
        TXMoneyDialog dialog = new TXMoneyDialog();
        Bundle bundle = new Bundle();
        bundle.putSerializable("list", (Serializable) list);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        return inflater.inflate(R.layout.layout_txmoney, container, true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        mList= (List<CardEntity>) getArguments().getSerializable("list");
        mTitle = view.findViewById(R.id.tv_title);
        mImageClose = view.findViewById(R.id.img_close);
        mLayoutNo=view.findViewById(R.id.layout_nocard);
        mImgAdd=view.findViewById(R.id.img_add);
        mListView=view.findViewById(R.id.listView);
        mImageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        mImgAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                Intent intent=new Intent(getActivity(), MyCardActivity.class);
                startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });
        if(mList==null || mList.size()==0){
            mLayoutNo.setVisibility(View.VISIBLE);
        }
        initDatas();

    }

    private void initDatas() {
        SelectMyCardAdapter adapter=new SelectMyCardAdapter(getActivity(),mList);
        mListView.setAdapter(adapter);
        adapter.setOnCardItemClickListener(new SelectMyCardAdapter.OnCardItemClickListener() {
            @Override
            public void onCardItemClick(int pos, CardEntity entity) {
                dismiss();
                if(listener!=null){
                    listener.onSelect(pos,entity);
                }

            }
        });

    }

    public void setOnSelectListener(OnSelectListener listener){
        this.listener=listener;

    }
    public interface OnSelectListener{
        void onSelect(int pos, CardEntity entity);
    }


}
