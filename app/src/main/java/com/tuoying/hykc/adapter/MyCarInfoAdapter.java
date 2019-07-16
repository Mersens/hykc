package com.tuoying.hykc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tuoying.hykc.R;
import com.tuoying.hykc.entity.UCarEntity;

import java.util.List;

public class MyCarInfoAdapter extends BaseAdapter {
    private List<UCarEntity> mList;
    private Context context;
    private LayoutInflater inflater;

    public MyCarInfoAdapter(Context context, List<UCarEntity> mList){
        this.context=context;
        this.mList=mList;
        inflater=LayoutInflater.from(context);
    }


    public void setList(List<UCarEntity> mList){
        this.mList=mList;
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return  mList==null?0:mList.size() ;
    }

    @Override
    public Object getItem(int position) {

        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder=null;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=inflater.inflate(R.layout.layout_carinfo_item,null);
            holder.mTextCPH=convertView.findViewById(R.id.tv_cph);
            holder.mTextPP=convertView.findViewById(R.id.tv_pp);
            holder.mTextType=convertView.findViewById(R.id.tv_type);
            holder.mTextStatu=convertView.findViewById(R.id.tv_statu);
            convertView.setTag(holder);
        }else {
            holder=(ViewHolder)convertView.getTag();
        }
        final UCarEntity entity=mList.get(position);
        String type=entity.getCartype();
        if("0".equals(type)){
            holder.mTextType.setText("普通车辆");
        }else if("1".equals(type)){
            holder.mTextType.setText("牵引车");
        }else if("2".equals(type)){
            holder.mTextType.setText("挂车");
        }else {
            holder.mTextType.setText("");
        }
        int statu=entity.getStatus();
        if(statu==1){
            holder.mTextStatu.setText("绑定成功");
        }else {
            holder.mTextStatu.setText("未绑定");
        }
        holder.mTextPP.setText(entity.getBrand());
        holder.mTextCPH.setText(entity.getLicensePlateNo());
        return convertView;
    }

    public static class ViewHolder{
        public TextView mTextCPH;
        public TextView mTextPP;
        public TextView mTextType;
        public TextView mTextStatu;
    }


}
