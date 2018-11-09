package com.tuoying.hykc.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuoying.hykc.R;
import com.tuoying.hykc.entity.CardEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyCardAdapter extends BaseAdapter {
    private Context context;
    private List<CardEntity> mList;
    private LayoutInflater mInflater;
    OnCardItemClickListener listener;
    private Map<Integer,Boolean> map=new HashMap<>();
    public MyCardAdapter(Context context, List<CardEntity> list){
        this.context=context;
        this.mList=list;
        mInflater=LayoutInflater.from(context);
        initDatas();

    }

    private void initDatas() {
        for (int i = 0; i <mList.size() ; i++) {
            map.put(i,false);
        }
    }

    public void setList(List<CardEntity> list){
        this.mList=list;
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return mList==null?0:mList.size();
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        ViewHolder holder=null;
        if(convertView==null){
            holder=new ViewHolder();
            convertView=mInflater.inflate(R.layout.mycard_item,null);
            holder.itemView=convertView.findViewById(R.id.cardview);
            holder.mTextAccount=convertView.findViewById(R.id.tv_account);
            holder.mTextType=convertView.findViewById(R.id.tv_type);
            holder.mTextName=convertView.findViewById(R.id.tv_name);
            holder.mImgDel=convertView.findViewById(R.id.img_del);
            convertView.setTag(holder);
        }else {
            holder= (ViewHolder) convertView.getTag();
        }
        if(map.get(position)){
            holder.mImgDel.setVisibility(View.VISIBLE);
        }else {
            holder.mImgDel.setVisibility(View.GONE);
        }
        final CardEntity entity=mList.get(position);
        holder.mImgDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onCardDelClick(position,entity);
                }
            }
        });
        holder.mTextType.setText(entity.getType());
        String num=entity.getAccount();
        if(num.length()>4){
            holder.mTextAccount.setText(num.substring(num.length()-4,num.length()));
        }else {
            holder.mTextAccount.setText(num);
        }
        if(!TextUtils.isEmpty(entity.getAddress())&& !TextUtils.isEmpty(entity.getBank())){
            holder.mTextName.setText(entity.getBank());
        }else {
            holder.mTextName.setText(entity.getName());
        }
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener!=null){
                    listener.onCardItemClick(position,entity);
                }
            }
        });
        return convertView;
    }


    public static class ViewHolder{
        public CardView itemView;
        public TextView mTextName;
        public TextView mTextType;
        public TextView mTextAccount;
        public ImageView mImgDel;
    }

    public void setOnCardItemClickListener(OnCardItemClickListener listener){
        this.listener=listener;

    }


    public interface OnCardItemClickListener{
        void onCardItemClick(int pos, CardEntity entity);
        void onCardDelClick(int pos, CardEntity entity);
    }

    public void showDel(){
        for (int i = 0; i <mList.size() ; i++) {
            map.put(i,true);
        }
        notifyDataSetChanged();

    }
    public void hideDel(){
        for (int i = 0; i <mList.size() ; i++) {
            map.put(i,false);
        }
        notifyDataSetChanged();
    }

}
