package com.tuoying.hykc.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.MainActivity;
import com.tuoying.hykc.adapter.MsgAdapter;
import com.tuoying.hykc.entity.MsgEntity;
import com.tuoying.swipelayout.OnLoadMoreListener;
import com.tuoying.swipelayout.OnRefreshListener;
import com.tuoying.swipelayout.SwipeToLoadLayout;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2018/3/26.
 */

public class MyMsgFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener ,MsgAdapter.OnItemRemoveListener {
    private View rootView;
    private RelativeLayout mLayoutLoading;
    private RelativeLayout mLayoutNoMsg;
    private ListView mListView;
    private View header;
    private View footer;
    private LayoutInflater mInflater;
    private SwipeToLoadLayout mSwipeToLoadLayout;
    private List<MsgEntity> mList=new ArrayList<>();
    private MsgAdapter adapter;
    WeakReference<MainActivity> mActivityReference;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivityReference = new WeakReference<>((MainActivity) context);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.layout_msg, null);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        init(view);
    }


    @Override
    public void init(View view) {
        mListView = (ListView) view.findViewById(R.id.swipe_target);
        mLayoutLoading = view.findViewById(R.id.layout_loading);
        mLayoutNoMsg = view.findViewById(R.id.layout_nomsg);
        mInflater = LayoutInflater.from(getContext());
        mSwipeToLoadLayout = (SwipeToLoadLayout) view.findViewById(R.id.swipeToLoadLayout);
        footer = mInflater.inflate(R.layout.layout_classic_footer, mSwipeToLoadLayout, false);
        header = mInflater.inflate(R.layout.layout_twitter_header, mSwipeToLoadLayout, false);
        mSwipeToLoadLayout.setSwipeStyle(SwipeToLoadLayout.STYLE.CLASSIC);
        mSwipeToLoadLayout.setLoadMoreFooterView(footer);
        mSwipeToLoadLayout.setRefreshHeaderView(header);
        mSwipeToLoadLayout.setOnRefreshListener(this);
        mSwipeToLoadLayout.setOnLoadMoreListener(this);
        mLayoutLoading.setVisibility(View.GONE);
        initLocDatas();
        adapter=new MsgAdapter(getActivity(),R.layout.layout_msg_item,mList);
        mListView.setAdapter(adapter);
        adapter.setOnItemRemoveListener(this);
    }


    private void initLocDatas(){
            MsgEntity entity=new MsgEntity();
            entity.setMsg("在线客服");
            entity.setTime("在线联系客服");
            mList.add(entity);

    }
    public static MyMsgFragment getInstance(){
        return new MyMsgFragment();
    }
    @Override
    public void onLoadMore() {
        mSwipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeToLoadLayout.setLoadingMore(false);
            }
        }, 3000);
    }

    @Override
    public void onRefresh() {
        mSwipeToLoadLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeToLoadLayout.setRefreshing(false);

            }
        }, 3000);
    }

    @Override
    public void onItemRemove(int position, MsgEntity entity) {

    }
}
