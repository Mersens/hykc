package com.tuoying.hykc.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.XYActivity;
import com.tuoying.hykc.app.Constants;
import com.tuoying.hykc.db.DBDao;
import com.tuoying.hykc.db.DBDaoImpl;
import com.tuoying.hykc.entity.GoodsEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import io.reactivex.android.schedulers.AndroidSchedulers;


/**
 * Created by Administrator on 2018/3/30.
 */

public class PayMoneyDialog extends DialogFragment {
    OnOrderListener listener;
    private ImageView mImageClose;
    private TextView mTextBL;
    private TextView mTextYe;
    private String price;
    private String money;
    private String userid;
    private DBDao dao;
    private Button mBtn;
    private double dbl;
    private EditText mEditPsd;
    private TextView mTextTips;
    private double bl;
    private onDismissListener dislistener;
    private CheckBox checkBox;
    private TextView mTextXY;
    private CheckBox checkBox1;
    private TextView mTextXY1;
    private String sid;
    private String name;
    private User user;
    private boolean isOfwlgsinfo;
    private GoodsEntity entity;
    private CheckBox checkBox_rem_paypwd;

    public static PayMoneyDialog getInstance(String price, double bl,String sid,String name,GoodsEntity entity) {
        PayMoneyDialog dialog = new PayMoneyDialog();
        Bundle bundle = new Bundle();
        bundle.putString("price", price);
        bundle.putDouble("bl", bl);
        bundle.putString("sid", sid);
        bundle.putString("name", name);
        bundle.putSerializable("entity",entity);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener()
        {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event){
                if (keyCode == KeyEvent.KEYCODE_BACK){
                    if(dislistener!=null){
                        dislistener.onDismiss();
                    }
                    return true;
                }

                else
                    return false; // pass on to be processed as normal
            }
        });
        return inflater.inflate(R.layout.layout_paymoney, container, true);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initView(view);
    }

    private void initView(View view) {
        userid = SharePreferenceUtil.getInstance(getActivity()).getUserId();
        dao = new DBDaoImpl(getActivity());
        if(!TextUtils.isEmpty(userid)){
            user=dao.findUserInfoById(userid);
        }
        if(user!=null){
            if(TextUtils.isEmpty(user.getOfwlgsinfo())){
                isOfwlgsinfo=true;
            }else {
                isOfwlgsinfo=false;
            }
        }
        price = getArguments().getString("price");
        bl = getArguments().getDouble("bl");
        name=getArguments().getString("name");
        sid=getArguments().getString("sid");
        entity=(GoodsEntity)getArguments().getSerializable("entity");
        checkBox_rem_paypwd=view.findViewById(R.id.checkBox_rem_paypwd);

        checkBox=view.findViewById(R.id.checkBox);
        checkBox1=view.findViewById(R.id.checkBox1);
        mImageClose = view.findViewById(R.id.img_close);
        mTextBL = view.findViewById(R.id.tv_bzj);
        mTextYe = view.findViewById(R.id.tv_yue);
        mBtn = view.findViewById(R.id.btn_ok);
        mTextTips = view.findViewById(R.id.tv_tips);
        mEditPsd = view.findViewById(R.id.editpsd);
        String paypwd=SharePreferenceUtil.getInstance(getActivity()).getPayPwd();
        if(!TextUtils.isEmpty(paypwd)){
            if(checkBox_rem_paypwd.isChecked()){
                mEditPsd.setText(paypwd);
            }

        }
        mTextXY=view.findViewById(R.id.tv_xy);
        mTextXY1=view.findViewById(R.id.tv_xy1);
        mTextXY1.getPaint().setFlags(Paint. UNDERLINE_TEXT_FLAG );
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!checkBox.isChecked()){
                    return;
                }

                if (!TextUtils.isEmpty(money)) {
                    double d1 = Double.valueOf(money);
                    if (dbl > d1) {
                        Toast.makeText(getActivity(), "钱包余额不足！", Toast.LENGTH_SHORT).show();
                        return;
                    } else {
                        String psd = mEditPsd.getText().toString().trim();
                        if (!TextUtils.isEmpty(psd)) {
                            if (listener != null) {
                                listener.onOrder(psd, String.format("%.2f", dbl));
                            }
                        }
                    }
                }else {
                    Toast.makeText(getActivity(), "暂无余额信息，请充值0.01元!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mImageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(dislistener!=null){
                    dislistener.onDismiss();

                }
                dismiss();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(checkBox1.isChecked()){
                        mBtn.setClickable(true);
                        mBtn.setEnabled(true);
                        mBtn.setBackgroundResource(R.drawable.btn_cz_bg);
                    }

                }else {
                    mBtn.setClickable(false);
                    mBtn.setEnabled(false);
                    mBtn.setBackgroundResource(R.drawable.btn_clickable_false_bg);
                }
            }
        });
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    if(checkBox.isChecked()){
                        mBtn.setClickable(true);
                        mBtn.setEnabled(true);
                        mBtn.setBackgroundResource(R.drawable.btn_cz_bg);
                    }
                }else {
                    mBtn.setClickable(false);
                    mBtn.setEnabled(false);
                    mBtn.setBackgroundResource(R.drawable.btn_clickable_false_bg);
                }
            }
        });
        mTextXY1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(getActivity(), XYActivity.class);
                String url=Constants.WEBSERVICE_URL+"demos/ysht.jsp?sid="+sid+"&name="+name;
                intent.putExtra("url",url);
                getActivity().startActivity(intent);
                getActivity(). overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        checkBox_rem_paypwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    String paypwd=SharePreferenceUtil.getInstance(getActivity()).getPayPwd();
                    if(!TextUtils.isEmpty(paypwd)){
                     mEditPsd.setText(paypwd);
                    }
                }else {
                    mEditPsd.setText("");
                }

            }
        });

        initDatas();

    }

    private void initDatas() {
        if (!TextUtils.isEmpty(userid)) {
            User user = dao.findUserInfoById(userid);
            getMoneyInfo(user);
        }
    }

    private void getMoneyInfo(final User user) {
        RequestManager.getInstance()
                .mServiceStore
                .findMyRz(user.getToken(), user.getUserId(), Constants.AppId)
                .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e(" getMoneyInfo onSuccess", "====" + msg);
                        String str = msg.replaceAll("\r", "").replaceAll("\n", "");
                        try {
                            JSONObject object = new JSONObject(str);
                            if (object.has("balance")) {
                                money = object.getString("balance");
                                if (!TextUtils.isEmpty(price)) {
                                    double d = Double.valueOf(price);
                                    String pdwlgs=entity.getPdwlgs();
                                    if(!"-1".equals(pdwlgs)){
                                        dbl = 0;
                                    }else {
                                        dbl = (1-bl)*d*0.03;
                                    }
                                    if (TextUtils.isEmpty(pdwlgs)){
                                        mTextYe.setText(money + "元");
                                    }else {
                                        mTextYe.setText("****元");
                                    }
                                    String strMoney = String.format("%.2f", dbl);
                                    mTextBL.setText(strMoney + "元");
                                    String task_id=entity.getTask_id();
                                    String driverPrice=entity.getDriverPrice();
                                    if(!TextUtils.isEmpty(task_id) && !TextUtils.isEmpty(driverPrice)){
                                        dbl = 0;
                                        mTextBL.setText(0+"元");
                                    }
                                    if (!TextUtils.isEmpty(money)) {
                                        double d1 = Double.valueOf(money);
                                        if (dbl > d1) {
                                            mTextTips.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            }else {
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } finally {
                        }
                    }

                    @Override
                    public void onError(String msg) {
                        Log.e("onError", "====" + msg);

                    }
                }));

    }

    @Override
    public void show(FragmentManager manager, String tag) {
        try{
            super.show(manager,tag);
        }catch (IllegalStateException ignore){
        }
    }

    public void setOnOrderListener(OnOrderListener listener) {
        this.listener = listener;
    }

    public void setOnDismissListener(onDismissListener dislistener){
        this.dislistener=dislistener;
    }

    public interface OnOrderListener {
        void onOrder(String psd, String money);
    }

    public interface onDismissListener{
        void onDismiss();
    }
}
