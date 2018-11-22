package com.tuoying.hykc.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tuoying.hykc.R;


/**
 * Created by Administrator on 2018/3/30.
 */

public class ZFB_WXBandDialog extends DialogFragment {
    OnOrderListener listener;
    private ImageView mImageClose;
    private Button mBtn;
    private EditText editName;
    private EditText mEditAccount;
    private TextView mTitle;
    private int type;

    public static ZFB_WXBandDialog getInstance(int type) {
        ZFB_WXBandDialog dialog = new ZFB_WXBandDialog();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        return inflater.inflate(R.layout.layout_zfb_wx_band, container, true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        type = getArguments().getInt("type");
        mTitle = view.findViewById(R.id.tv_title);
        mImageClose = view.findViewById(R.id.img_close);
        mEditAccount = view.findViewById(R.id.editAccount);
        mBtn = view.findViewById(R.id.btn_ok);
        editName = view.findViewById(R.id.editName);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = editName.getText().toString().trim();
                String account = mEditAccount.getText().toString().trim();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getActivity(), "姓名不能为空！", Toast.LENGTH_SHORT).show();
                    return;

                }
                if (TextUtils.isEmpty(account)) {
                    Toast.makeText(getActivity(), "账户不能为空！", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(account.length()<4){
                    Toast.makeText(getActivity(), "请输入正确账户！", Toast.LENGTH_SHORT).show();
                }
                if (listener != null) {
                    listener.onOrder(name, account);
                }

            }
        });
        mImageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        initDatas();

    }

    private void initDatas() {
        if (type == 1) {
            mTitle.setText("支付宝绑定");
            mEditAccount.setHint("支付宝账户");
        } else if (type == 2) {
            mTitle.setText("微信绑定");
            mEditAccount.setHint("微信账户");
        }
    }

    public void setOnOrderListener(OnOrderListener listener) {
        this.listener = listener;
    }


    public interface OnOrderListener {
        void onOrder(String psd, String account);
    }
}
