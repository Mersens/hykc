package com.tuoying.hykc.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cb.ratingbar.CBRatingBar;
import com.tuoying.hykc.R;
import com.tuoying.hykc.entity.GoodsEntity;

import java.util.List;

public class DPJAdapter extends BaseAdapter {
    private Context mContext;
    private List<GoodsEntity> mList;
    private LayoutInflater mInflater;
    OnItemButtonClickListener listener;
    OnItemClickListener clickListener;

    public void setOnItemClickListener(OnItemClickListener clickListener) {
        this.clickListener = clickListener;

    }

    public interface OnItemClickListener {
        void onItemClick(int pos, GoodsEntity entity);
    }

    public DPJAdapter(Context context, List<GoodsEntity> list) {
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        ViewHolder holder = null;
        if (view == null) {
            holder = new ViewHolder();
            view = mInflater.inflate(R.layout.layout_dpj_item, null);
            holder.mView = view.findViewById(R.id.cardview);
            holder.mPrice = view.findViewById(R.id.tv_price);
            holder.mGoodsName = view.findViewById(R.id.tv_goods_name);
            holder.mTime = view.findViewById(R.id.tv_time);
            holder.mStartAddress = view.findViewById(R.id.tv_start);
            holder.mEndAddress = view.findViewById(R.id.tv_end);
            holder.mBtn = view.findViewById(R.id.btn);
            holder.fhrName = view.findViewById(R.id.tv_hz);
            holder.mLayoutPj = view.findViewById(R.id.layout_pj);
            holder.mCBRatingBar = view.findViewById(R.id.rating_bar);
            holder.mImageYPJ = view.findViewById(R.id.img_pj);
            holder.mTextJDTime=view.findViewById(R.id.tv_jd_time);
            view.setTag(holder);
        } else {
            holder = (ViewHolder) view.getTag();
        }
        final GoodsEntity entity = mList.get(i);
        holder.fhrName.setText(entity.getHzxm());
        if(entity.isOfwlgsinfo()){
            holder.mPrice.setText(entity.getZyf()+"元");
        }else {
            holder.mPrice.setText("****");
        }
        if(!TextUtils.isEmpty(entity.getJdTime())){
            holder.mTextJDTime.setText(entity.getJdTime());
        }
        holder.mGoodsName.setText(entity.getName());
        holder.mStartAddress.setText(entity.getStartAddress());
        holder.mEndAddress.setText(entity.getEndAddress());
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
                if (clickListener != null) {
                    clickListener.onItemClick(i, entity);
                }
            }
        });
        String driver_score = entity.getDriver_score();
        if (!TextUtils.isEmpty(driver_score)) {
            holder.mLayoutPj.setVisibility(View.VISIBLE);
            holder.mBtn.setVisibility(View.GONE);
            holder.mImageYPJ.setVisibility(View.VISIBLE);

            holder.mCBRatingBar.setStarProgress(Float.parseFloat(driver_score));


        } else {
            holder.mLayoutPj.setVisibility(View.GONE);
            holder.mBtn.setVisibility(View.VISIBLE);
            holder.mImageYPJ.setVisibility(View.GONE);
        }
        return view;
    }

    public interface OnItemButtonClickListener {
        void onButtonClick(View view, int index, GoodsEntity entity, int type);

    }

    public void setOnItemButtonClickListener(OnItemButtonClickListener listener) {
        this.listener = listener;

    }

    static class ViewHolder {
        public TextView fhrName;
        public TextView mPrice;
        public TextView mStartAddress;
        public TextView mEndAddress;
        public TextView mGoodsName;
        public TextView mTime;
        public Button mBtn;
        public CardView mView;
        public RelativeLayout mLayoutPj;
        public CBRatingBar mCBRatingBar;
        public ImageView mImageYPJ;
        public TextView mTextJDTime;
    }

}
