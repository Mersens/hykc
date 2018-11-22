package com.tuoying.hykc.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.tuoying.hykc.R;
import com.tuoying.hykc.entity.GoodsEntity;

import java.util.List;

public class GoodsListAdapter extends BaseAdapter {
    OnItemButtonClickListener listener;
    OnItemClickListener clickListener;
    private Context mContext;
    private List<GoodsEntity> mList;
    private LayoutInflater mInflater;
    public GoodsListAdapter(Context context, List<GoodsEntity> list) {
        this.mContext = context;
        this.mList = list;
        mInflater = LayoutInflater.from(mContext);
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
            view = mInflater.inflate(R.layout.goods_list_item, null);
            holder.mView=view.findViewById(R.id.cardview);
            holder.mPrice = view.findViewById(R.id.tv_price);
            holder.mGoodsName = view.findViewById(R.id.tv_goods_name);
            holder.mTime = view.findViewById(R.id.tv_time);
            holder.mStartAddress = view.findViewById(R.id.tv_start);
            holder.mEndAddress = view.findViewById(R.id.tv_end);
            holder.mBtn = view.findViewById(R.id.btn);
            holder.mFhrName=view.findViewById(R.id.tv_hz);
            holder.mTextBz = view.findViewById(R.id.tv_bz);
            holder.mTextTj = view.findViewById(R.id.tv_tj);
            holder.mTextZl=view.findViewById(R.id.tv_zl);
            view.setTag(holder);

        } else {
            holder = (ViewHolder) view.getTag();
        }

        final GoodsEntity entity = mList.get(i);
        holder.mFhrName.setText(entity.getHzxm());
        double bl= Double.parseDouble(entity.getBl());
        double yf=Double.parseDouble(entity.getZyf());
        double price=yf*(1-bl);
        String strMoney = String.format("%.2f", price);
        holder.mPrice.setText(strMoney+"元");
        holder.mGoodsName.setText(entity.getName());
        holder.mTextTj.setText(entity.getVolume()+"立方");
        holder.mTextZl.setText(entity.getWeight()+"吨");
        holder.mStartAddress.setText(entity.getStartAddress());
        holder.mEndAddress.setText(entity.getEndAddress());
        holder.mTextBz.setText(entity.getBz());
        holder.mTime.setText(entity.getTime());
        holder.mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (listener != null) {
                    listener.onButtonClick(view, i, entity, 1);
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
        public CardView mView;

    }

}
