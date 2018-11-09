package com.tuoying.hykc.utils;

import android.view.View;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.concurrent.TimeUnit;

import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by Mersens on 2018/1/8 17:05
 * Email:626168564@qq.com
 */

public class ViewClickHelper {

    public static void clicks(View view, final onViewClickListener listener){
        if(listener==null){
            return;
        }
       RxView.clicks(view)
                .throttleFirst(3, TimeUnit.SECONDS)
                .subscribe(new Consumer<Object>() {
                    @Override
                    public void accept(@NonNull Object o) throws Exception {
                        listener.onAccept();
                    }
                });

    }

    public interface onViewClickListener{
        void onAccept();
    }


}
