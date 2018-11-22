package com.tuoying.hykc.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.UpLoadImgActivity;
import com.tuoying.hykc.app.Constants;

import java.util.List;

public class OthersImgAdapter extends RecyclerView.Adapter<OthersImgAdapter.ViewHolder>{
    private List<String> urls;
    private Context context;
    public OthersImgAdapter(List<String> urls, Context context){
        this.urls=urls;
        this.context=context;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_image,parent,false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Glide.with(context)
                .load(urls.get(position))
                .skipMemoryCache(true)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .into(holder.iv_img);
    }

    @Override
    public int getItemCount() {
        return urls==null?0:urls.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView iv_img;

        public ViewHolder(View view) {
            super(view);
            iv_img = (ImageView) view.findViewById(R.id.iv_img);
        }
    }

}
