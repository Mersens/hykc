package com.tuoying.hykc.view;

import android.content.Context;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;

/**
 * Created by Mersens on 2017/8/31 13:03
 * Email:626168564@qq.com
 */

public class MyPopwindow extends PopupWindow{

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff)  {
        if (Build.VERSION.SDK_INT >= 24) {
            int[] location = new int[2];
            anchor.getLocationOnScreen(location);
            // 7.1 版本处理
            if (Build.VERSION.SDK_INT == 25) {
                WindowManager wm = (WindowManager) getContentView().getContext().getSystemService(Context.WINDOW_SERVICE);
                int screenHeight = wm.getDefaultDisplay().getHeight();
                setHeight(screenHeight - location[1] - anchor.getHeight() - yoff);
            }
           showAtLocation(anchor, Gravity.NO_GRAVITY, xoff, location[1] + anchor.getHeight() + yoff);
        } else {
            super.showAsDropDown(anchor,xoff, yoff);
        }
    }
}
