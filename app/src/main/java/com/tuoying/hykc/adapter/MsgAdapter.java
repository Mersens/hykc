package com.tuoying.hykc.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tuoying.hykc.R;
import com.tuoying.hykc.entity.MsgEntity;

import java.util.List;

public class MsgAdapter extends BaseAdapter {
    private int resId;
    private Context context;
    private LayoutInflater mInflater;
    private List<MsgEntity> mList;
    private OnItemRemoveListener listener;

   public MsgAdapter(Context context, int resid, List<MsgEntity> list){
       this.context=context;
       this.resId=resid;
       this.mList=list;
       mInflater=LayoutInflater.from(context);
    }

    public void setList(List<MsgEntity> list){
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
           convertView=mInflater.inflate(resId,null);
           holder.mDelView=convertView.findViewById(R.id.delete_button);
           holder.mTextTime=convertView.findViewById(R.id.tv_time);
           holder.mTextType=convertView.findViewById(R.id.tv_msg_type);
           convertView.setTag(holder);
       }else {
           holder= (ViewHolder) convertView.getTag();
       }
       final MsgEntity entity=mList.get(position);
       holder.mTextTime.setText(entity.getMsg());
       holder.mTextType.setText(entity.getTime());
       holder.mDelView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               if(listener!=null){
                   listener.onItemRemove(position,entity);
               }
           }
       });
       return convertView;
    }

    public static class ViewHolder{
       public TextView mTextType;
       public TextView mTextTime;
       public RelativeLayout mDelView;

    }


    public void setOnItemRemoveListener(OnItemRemoveListener listener){

       this.listener=listener;

    }

    public interface OnItemRemoveListener{
       void onItemRemove(int position, MsgEntity entity);

    }

}
