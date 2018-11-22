package com.tuoying.hykc.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.tuoying.hykc.R;
import com.tuoying.hykc.entity.CardEntity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SelectMyCardAdapter extends BaseAdapter {
    OnCardItemClickListener listener;
    private Context context;
    private List<CardEntity> mList;
    private LayoutInflater mInflater;
    private Map<Integer, CheckBox> map = new HashMap<>();

    public SelectMyCardAdapter(Context context, List<CardEntity> list) {
        this.context = context;
        this.mList = list;
        mInflater = LayoutInflater.from(context);

    }

    public void setList(List<CardEntity> list) {
        this.mList = list;
        notifyDataSetChanged();

    }

    @Override
    public int getCount() {
        return mList == null ? 0 : mList.size();
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
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.layout_select_card_item, null);
            holder.itemView = convertView;
            holder.mCheckBox = convertView.findViewById(R.id.checkBox);
            holder.mImgType = convertView.findViewById(R.id.img_type);
            holder.mTextType = convertView.findViewById(R.id.tv_type);
            holder.mTextAccount = convertView.findViewById(R.id.tv_zh);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        map.put(position, holder.mCheckBox);
        final CardEntity entity = mList.get(position);
        final ViewHolder myHolder = holder;

        myHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    restCheckBox();
                    myHolder.mCheckBox.setChecked(true);
                    myHolder.mCheckBox.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            listener.onCardItemClick(position, entity);
                        }
                    }, 300);

                }
            }
        });
        myHolder.mCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (listener != null) {
                        restCheckBox();
                        myHolder.mCheckBox.setChecked(true);
                        myHolder.mCheckBox.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                listener.onCardItemClick(position, entity);
                            }
                        }, 300);

                    }
                }
            }
        });
        if (!TextUtils.isEmpty(entity.getAddress()) && !TextUtils.isEmpty(entity.getBank())) {
            myHolder.mImgType.setImageResource(R.mipmap.ic_yjk);
            String num = entity.getAccount();
            myHolder.mTextAccount.setText(num.substring(num.length() - 4, num.length()));
            myHolder.mTextType.setText(entity.getType());

        } else {
            if ("支付宝".equals(entity.getType())) {
                myHolder.mImgType.setImageResource(R.mipmap.icon_zfb);
                String num = entity.getAccount();
                myHolder.mTextAccount.setText(num);
                myHolder.mTextType.setText(entity.getType());
            } else if ("微信".equals(entity.getType())) {
                myHolder.mImgType.setImageResource(R.mipmap.weixin);
                String num = entity.getAccount();
                myHolder.mTextAccount.setText(num);
                myHolder.mTextType.setText(entity.getType());
            }
        }
        return convertView;
    }

    public void setOnCardItemClickListener(OnCardItemClickListener listener) {
        this.listener = listener;

    }

    public void restCheckBox() {
        for (Map.Entry<Integer, CheckBox> entry : map.entrySet()) {
            entry.getValue().setChecked(false);
        }


    }


    public interface OnCardItemClickListener {
        void onCardItemClick(int pos, CardEntity entity);
    }

    public static class ViewHolder {
        private View itemView;
        private ImageView mImgType;
        private TextView mTextType;
        private TextView mTextAccount;
        private CheckBox mCheckBox;

    }

}
