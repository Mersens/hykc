package com.tuoying.hykc.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.tuoying.hykc.R;
import com.tuoying.hykc.entity.GoodsEntity;

import java.util.List;

public class YWCAdapter extends BaseAdapter {
    OnItemButtonClickListener listener;
    OnItemClickListener clickListener;
    private Context mContext;
    private List<GoodsEntity> mList;
    private LayoutInflater mInflater;
    public YWCAdapter(Context context, List<GoodsEntity> list) {
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
            view = mInflater.inflate(R.layout.layout_ywc_item, null);
            holder.mView = view.findViewById(R.id.cardview);
            holder.mPrice = view.findViewById(R.id.tv_price);
            holder.mGoodsName = view.findViewById(R.id.tv_goods_name);
            holder.mTime = view.findViewById(R.id.tv_time);
            holder.mStartAddress = view.findViewById(R.id.tv_start);
            holder.mEndAddress = view.findViewById(R.id.tv_end);
            holder.fhrName = view.findViewById(R.id.tv_hz);
            holder.mTextJDTime=view.findViewById(R.id.tv_jd_time);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }

        final GoodsEntity entity = mList.get(i);
        holder.fhrName.setText(entity.getHzxm());
        double bl= Double.parseDouble(entity.getBl());
        double yf=Double.parseDouble(entity.getZyf());
        double p=yf*(1-bl);
        String strMoney = String.format("%.2f", p);
        holder.mPrice.setText(strMoney+"元");
        String task_id=entity.getTask_id();
        String driverPrice=entity.getDriverPrice();
        if(!TextUtils.isEmpty(task_id) && !TextUtils.isEmpty(driverPrice)){
            holder.mPrice.setText(driverPrice+"元/吨");
        }
        if(!TextUtils.isEmpty(entity.getJdTime())){
            holder.mTextJDTime.setText(entity.getJdTime());
        }else {
            holder.mTextJDTime.setText("");
        }
        holder.mGoodsName.setText(entity.getName());
        holder.mStartAddress.setText(entity.getStartAddress());
        holder.mEndAddress.setText(entity.getEndAddress());
        holder.mTime.setText(entity.getTime());
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
        public TextView fhrName;
        public TextView mPrice;
        public TextView mStartAddress;
        public TextView mEndAddress;
        public TextView mGoodsName;
        public TextView mTime;
        public CardView mView;
        public TextView mTextJDTime;
    }

}
