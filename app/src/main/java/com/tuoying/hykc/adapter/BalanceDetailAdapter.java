package com.tuoying.hykc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.tuoying.hykc.R;
import com.tuoying.hykc.entity.BalanceEntity;
import com.tuoying.hykc.entity.JLEntity;

import java.util.List;

import retrofit2.http.POST;

/**
 * Created by Administrator on 2018/3/27.
 */

public class BalanceDetailAdapter extends BaseAdapter {
    private Context mContext;
    private List<JLEntity> mList;
    private LayoutInflater mInflater;

    public BalanceDetailAdapter(Context context, List<JLEntity> list) {
        this.mContext = context;
        this.mList = list;
        mInflater = LayoutInflater.from(mContext);

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
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder=null;
        if(view==null){
            holder=new ViewHolder();
            view=mInflater.inflate(R.layout.layout_balance_detial_item,null);
            holder.mTextType=view.findViewById(R.id.tv_type);
            holder.mTextTime=view.findViewById(R.id.tv_time);
            holder.mTextYue=view.findViewById(R.id.tv_yue);
            holder.mTextType_Value=view.findViewById(R.id.tv_zc);
            view.setTag(holder);

        }else {
            holder=(ViewHolder)view.getTag();
        }
        final JLEntity entity=mList.get(i);
        holder.mTextType.setText(entity.getName());
        holder.mTextTime.setText(entity.getTime());
        holder.mTextYue.setText("余额:"+entity.getBalance());
        holder.mTextType_Value.setText(entity.getChangeMoney());
        return view;
    }

    static class ViewHolder {
        public TextView mTextType;
        public TextView mTextTime;
        public TextView mTextYue;
        public TextView mTextType_Value;

    }

}
