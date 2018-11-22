package com.tuoying.hykc.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuoying.hykc.R;
import com.tuoying.hykc.entity.GoodsEntity;

import java.util.List;

public class WWCAdapter extends BaseAdapter {
    OnItemButtonClickListener listener;
    OnItemClickListener clickListener;
    private Context mContext;
    private List<GoodsEntity> mList;
    private LayoutInflater mInflater;

    public WWCAdapter(Context context, List<GoodsEntity> list) {
        this.mContext = context;
        this.mList = list;
        mInflater = LayoutInflater.from(mContext);
    }

    public void setList(List<GoodsEntity> mList){
        this.mList=mList;
        notifyDataSetChanged();
    }

    public void setOnItemClickListener(OnItemClickListener clickListener){
        this.clickListener=clickListener;

    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.layout_wwc_item, null);
            holder.mView=view.findViewById(R.id.cardview);
            holder.mPrice = view.findViewById(R.id.tv_price);
            holder.mGoodsName = view.findViewById(R.id.tv_goods_name);
            holder.mTime = view.findViewById(R.id.tv_time);
            holder.mStartAddress = view.findViewById(R.id.tv_start);
            holder.mEndAddress = view.findViewById(R.id.tv_end);
            holder.mBtn = view.findViewById(R.id.btn);
            holder.mBtnTB = view.findViewById(R.id.btn_tb);
            holder.mFhrName=view.findViewById(R.id.tv_hz);
            holder.mTextBz = view.findViewById(R.id.tv_bz);
            holder.mTextTj = view.findViewById(R.id.tv_tj);
            holder.mTextZl=view.findViewById(R.id.tv_zl);
            holder.shrName=view.findViewById(R.id.tv_shr);
            holder.shrPhone=view.findViewById(R.id.tv_shr_tel);
            holder.viewLin=view.findViewById(R.id.view_lin);
            holder.layoutBottom=view.findViewById(R.id.layout_bottom);
            holder.mTextCancel=view.findViewById(R.id.tv_qx);
            holder.mTextDH=view.findViewById(R.id.tv_dh);
            holder.mBtnPZ=view.findViewById(R.id.btn_pz);
            holder.mTextJDTime=view.findViewById(R.id.tv_jd_time);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        final GoodsEntity entity = mList.get(i);
        if(TextUtils.isEmpty(entity.getPolicyNo())){
            holder.mBtnTB.setVisibility(View.VISIBLE);
        }else {
            holder.mBtnTB.setVisibility(View.GONE);
        }
        String statusName=entity.getStatusName();
        int type=-1;
        if("待支付".equals(statusName)){
            holder.mBtn.setText("支付");
            type=1;
            holder.mBtn.setEnabled(true);
            holder.mBtn.setClickable(true);
            holder.viewLin.setVisibility(View.GONE);
            holder.layoutBottom.setVisibility(View.GONE);
            holder.mBtnPZ.setVisibility(View.GONE);
        }
        else if("待支付运费".equals(statusName)){
            holder.mBtn.setText("待支付运费");
            holder.mBtn.setEnabled(false);
            holder.mBtn.setClickable(false);
            holder.viewLin.setVisibility(View.VISIBLE);
            holder.layoutBottom.setVisibility(View.VISIBLE);
            holder.mBtnPZ.setVisibility(View.GONE);
        }
        else if("待结算尾款".equals(statusName)){
            holder.mBtn.setText("待结算尾款");
            holder.mBtn.setEnabled(false);
            holder.mBtn.setClickable(false);
            holder.viewLin.setVisibility(View.VISIBLE);
            holder.layoutBottom.setVisibility(View.VISIBLE);
            holder.mBtnPZ.setVisibility(View.VISIBLE);
        }
        else if("待装货".equals(statusName)){
            holder.mBtn.setText("配送");
            type=2;
            holder.mBtn.setEnabled(true);
            holder.mBtn.setClickable(true);
            holder.mBtnPZ.setVisibility(View.GONE);
            holder.viewLin.setVisibility(View.VISIBLE);
            holder.layoutBottom.setVisibility(View.VISIBLE);
        }
        else if("配送中".equals(statusName)){
            holder.mBtn.setText("送达");
            type=3;
            holder.mBtnPZ.setVisibility(View.GONE);
            holder.mBtn.setEnabled(true);
            holder.mBtn.setClickable(true);
            holder.viewLin.setVisibility(View.VISIBLE);
            holder.layoutBottom.setVisibility(View.VISIBLE);
        }
        else if("已撤销".equals(statusName)){
            holder.mBtn.setText("已撤销");
            holder.viewLin.setVisibility(View.GONE);
            holder.mBtnPZ.setVisibility(View.GONE);
            holder.layoutBottom.setVisibility(View.GONE);
        }else if("待确认".equals(statusName)){
            holder.mBtn.setText("待确认");
            holder.viewLin.setVisibility(View.GONE);
            holder.mBtnPZ.setVisibility(View.VISIBLE);
            holder.layoutBottom.setVisibility(View.GONE);
        }else if("已完成".equals(statusName)){
            holder.mBtn.setText("已完成");
            holder.viewLin.setVisibility(View.GONE);
            holder.layoutBottom.setVisibility(View.GONE);
            holder.mBtnPZ.setVisibility(View.VISIBLE);
        }

        holder.mFhrName.setText(entity.getHzxm());

        String pdwlgs=entity.getPdwlgs();
        if(!TextUtils.isEmpty(pdwlgs)){
            if("-1".equals(pdwlgs)){
                double bl= Double.parseDouble(entity.getBl());
                double yf=Double.parseDouble(entity.getZyf());
                double price=yf*(1-bl);
                String strMoney = String.format("%.2f", price);
                holder.mPrice.setText(strMoney+"元");

            }else {
/*                double bl= Double.parseDouble(entity.getBl());
                double d = Double.valueOf(entity.getZyf());
                String str=String.format("%.2f", (1-bl)*d);*/
                holder.mPrice.setText("****元");
            }

        }else {
            double bl= Double.parseDouble(entity.getBl());
            double d = Double.valueOf(entity.getZyf());
            String str=String.format("%.2f", (1-bl)*d);
            holder.mPrice.setText(str+"元");
        }

        holder.mGoodsName.setText(entity.getName());
        holder.mTextTj.setText(entity.getVolume()+"立方");
        holder.mTextZl.setText(entity.getWeight()+"吨");
        holder.mStartAddress.setText(entity.getStartAddress());
        holder.mEndAddress.setText(entity.getEndAddress());
        holder.mTextBz.setText(entity.getBz());
        holder.mTime.setText(entity.getTime());
        holder.shrName.setText(entity.getShrName());
        holder.shrPhone.setText(entity.getShrTel());
        if(!TextUtils.isEmpty(entity.getJdTime())){
            holder.mTextJDTime.setText(entity.getJdTime());
        }else {
            holder.mTextJDTime.setText("");
        }


        holder.mTextCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onButtonClick(v, i, entity, 12);
                }
            }
        });
        holder.mTextDH.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onButtonClick(v, i, entity, 13);
                }
            }
        });

        final int t=type;
        final ViewHolder fholder=holder;
        holder.mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (listener != null) {
                    listener.onButtonClick(view, i, entity, t);
                }
                fholder.mBtn.setBackgroundResource(R.drawable.btn_no_click_bg);
                fholder.mBtn.setEnabled(false);
                fholder.mBtn.setClickable(false);
                fholder.mBtn.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        fholder.mBtn.setBackgroundResource(R.drawable.btn_cz_bg);
                        fholder.mBtn.setEnabled(true);
                        fholder.mBtn.setClickable(true);
                    }
                },3000);
            }
        });

        holder.mBtnTB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.onButtonClick(v, i, entity, 14);
                }
            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(clickListener!=null){
                    clickListener.onItemClick(i,entity);
                }
            }
        });
        holder.mBtnPZ.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onButtonClick(v, i, entity, 15);
                }
            }
        });
        return view;
    }

    public void setOnItemButtonClickListener(OnItemButtonClickListener listener) {
        this.listener = listener;

    }

    public interface  OnItemClickListener{
        void onItemClick(int pos, GoodsEntity entity);
    }

    public interface OnItemButtonClickListener {
        void onButtonClick(View view, int index, GoodsEntity entity, int type);
    }

    static class ViewHolder {
        public TextView mFhrName;
        public TextView mPrice;
        public TextView mGoodsName;
        public TextView mTextTj;
        public TextView mTextZl;
        public TextView mStartAddress;
        public TextView mEndAddress;
        public TextView mTextBz;
        public TextView mTime;
        public Button mBtn;
        public Button mBtnTB;
        public CardView mView;
        public TextView shrName;
        public TextView shrPhone;
        public View viewLin;
        public LinearLayout layoutBottom;
        public TextView mTextCancel;
        public TextView mTextDH;
        public Button mBtnPZ;
        public TextView mTextJDTime;

    }

}
