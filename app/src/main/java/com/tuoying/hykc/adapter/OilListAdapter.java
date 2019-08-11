package com.tuoying.hykc.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.tuoying.hykc.R;
import com.tuoying.hykc.entity.FuelsEntity;
import com.tuoying.hykc.entity.OilEntity;

import java.util.List;

public class OilListAdapter extends RecyclerView.Adapter<OilListAdapter.ViewHolder>  {
    private Context mContext;
    private List<OilEntity> mList;
    private LayoutInflater mInflater;
    private OnItemBtnClickListener listener;
    public OilListAdapter(Context mContext,List<OilEntity> mList){
        this.mContext=mContext;
        this.mList=mList;
        mInflater=LayoutInflater.from(mContext);
    }
    public void setDatas(List<OilEntity> mList){
        this.mList=mList;
        notifyItemRangeChanged(0,this.mList.size());
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v1=mInflater.inflate(R.layout.layout_oil_item,parent,false);
        View v2=mInflater.inflate(R.layout.layout_fuels_item,parent,false);
        return new ViewHolder(v1,v2);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        final OilEntity entity=mList.get(position);
        holder.mTextOilName.setText(entity.getStationName());
        holder.mTextOilAddress.setText(entity.getAddress());
        int oilStatus=entity.getIsStop();
        if(oilStatus==0){
            holder.mTextOilStatus.setText("正常营业");
            holder.mTextOilStatus.setTextColor(mContext.getResources()
                    .getColor(R.color.actionbar_color));
        }else {
            holder.mTextOilStatus.setText("停止营业");
            holder.mTextOilStatus.setTextColor(mContext.getResources()
                    .getColor(R.color.colorAccent));
        }
        List<FuelsEntity> fuelsEntityList=entity.getFuels();
        if(fuelsEntityList.size()>0){
            for (int i = 0; i <fuelsEntityList.size() ; i++) {
                FuelsEntity fuelsEntity=fuelsEntityList.get(i);
                Log.e("fuelsEntity",fuelsEntity.toString());
                holder.mTextFuelName.setText(fuelsEntity.getFuel_name());
                String strPrice=fuelsEntity.getPrice();
                if(!TextUtils.isEmpty(strPrice) && !"null".equals(strPrice)){
                    double price=Double.valueOf(strPrice);
                    double d1=price/1000;
                    holder.mTextPrice.setText(String.format("%.2f", d1)+"元");
                }
                String strGuidePrice=fuelsEntity.getGuide_price();
                if(!TextUtils.isEmpty(strGuidePrice) && !"null".equals(strGuidePrice)){
                    double guideprice=Double.valueOf(strGuidePrice);
                    double d2=guideprice/1000;
                    holder.mTextGuidePrice.setText(String.format("%.2f", d2)+"元");
                }

                String status=fuelsEntity.getStatus();
                if("0".equals(status)){
                    holder.mTextFuelStatus.setText("正常");
                    holder.mTextFuelStatus.setTextColor(mContext.getResources()
                            .getColor(R.color.actionbar_color));
                }else {
                    holder.mTextFuelStatus.setText("停用");
                    holder.mTextFuelStatus.setTextColor(mContext.getResources()
                            .getColor(R.color.colorAccent));
                }
                holder.mLayoutFuels.addView(holder.fuelview);

            }
        }

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onItemClick(position,entity);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return mList==null?0:mList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{
        public CardView cardView;
        public TextView mTextOilName;
        public TextView mTextOilAddress;
        public TextView mTextOilStatus;
        public LinearLayout mLayoutFuels;

        public View fuelview;
        public TextView mTextFuelName;
        public TextView mTextPrice;
        public TextView mTextGuidePrice;
        public TextView mTextFuelStatus;


        public ViewHolder(View itemView) {
            super(itemView);
        }
        public ViewHolder(View itemView,View fuelview) {
            super(itemView);
            cardView=itemView.findViewById(R.id.cardview);
            mTextOilName=itemView.findViewById(R.id.tv_oil_name);
            mTextOilAddress=itemView.findViewById(R.id.tv_oil_address);
            mTextOilStatus=itemView.findViewById(R.id.tv_oil_status);
            mLayoutFuels=itemView.findViewById(R.id.layout_fuels);

            this.fuelview=fuelview;
            mTextFuelName=fuelview.findViewById(R.id.tv_fuels_name);
            mTextPrice=fuelview.findViewById(R.id.tv_price);
            mTextGuidePrice=fuelview.findViewById(R.id.tv_guide_price);
            mTextFuelStatus=fuelview.findViewById(R.id.tv_fuel_status);
        }
    }

    public void setOnItemBtnClickListener(OnItemBtnClickListener listener){
        this.listener=listener;
    }

    public interface OnItemBtnClickListener{
        void onItemClick(int pos,OilEntity entity);
    }

}
