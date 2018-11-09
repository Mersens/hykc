package com.tuoying.hykc.view;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.tuoying.hykc.R;
import com.tuoying.hykc.view.wheelview.OnWheelScrollListener;
import com.tuoying.hykc.view.wheelview.WheelView;
import com.tuoying.hykc.view.wheelview.adapter.ArrayWheelAdapter;
import com.tuoying.hykc.view.wheelview.adapter.NumericWheelAdapter;


/**
 * Created by Mersens on 2016/9/28.
 */

public class CarLengthPopWindow extends MyPopwindow implements View.OnClickListener {
    private Context context;
    private String selectLength;
    private WheelView wheelView;
    private TextView tvcancle, tvok;
    private boolean isScroll = false;
    private String items[];
    private OnSelectListener listener;
    private int index;

    public void setOnSelectListener(OnSelectListener listener) {
        this.listener = listener;
    }

    public interface OnSelectListener {
        void ontSelect(String select);
    }

    public CarLengthPopWindow(Context mContext, View parent) {
        this.context = mContext;
        items = context.getResources().getStringArray(R.array.cc);
        setOutsideTouchable(true);
        selectLength = items[0];
        View view = View
                .inflate(mContext, R.layout.item_age_layout, null);
        view.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.fade_ins));
        LinearLayout ll_popup = (LinearLayout) view
                .findViewById(R.id.ll_popup);
        ll_popup.startAnimation(AnimationUtils.loadAnimation(mContext,
                R.anim.push_bottom_in_2));
        tvcancle = (TextView) view.findViewById(R.id.tv_cancel);
        tvok = (TextView) view.findViewById(R.id.tv_ok);
        setWidth(ViewGroup.LayoutParams.FILL_PARENT);
        setHeight(ViewGroup.LayoutParams.FILL_PARENT);
        setBackgroundDrawable(new BitmapDrawable());
        setFocusable(true);
        setOutsideTouchable(true);
        setContentView(view);
        showAtLocation(parent, Gravity.BOTTOM, 0, 0);
        wheelView = (WheelView) view.findViewById(R.id.wheelView);
        initEvnets();


    }

    private void initEvnets() {
        tvok.setOnClickListener(this);
        tvcancle.setOnClickListener(this);
        ArrayWheelAdapter numericWheelAdapter3 = new ArrayWheelAdapter(context, items);
        wheelView.setViewAdapter(numericWheelAdapter3);
        wheelView.setCyclic(true);
        wheelView.addScrollingListener(scrollListener);
    }

    OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {


        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            isScroll = true;
            index=wheelView.getCurrentItem();
        }
    };

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_ok:
                double select = 0;
                if (isScroll) {
                    listener.ontSelect(items[index]);
                } else {
                    listener.ontSelect(items[0]);
                }
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
        }
    }
}
