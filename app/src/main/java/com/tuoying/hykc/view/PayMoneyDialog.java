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
import com.tuoying.hykc.entity.EventEntity;
import com.tuoying.hykc.entity.GoodsEntity;
import com.tuoying.hykc.entity.User;
import com.tuoying.hykc.service.ServiceStore;
import com.tuoying.hykc.utils.RequestManager;
import com.tuoying.hykc.utils.ResultObserver;
import com.tuoying.hykc.utils.RxBus;
import com.tuoying.hykc.utils.SharePreferenceUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;


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
    private Map<String, String> map = new HashMap<>();

    public static PayMoneyDialog getInstance(String price, double bl, String sid, String name, GoodsEntity entity) {
        PayMoneyDialog dialog = new PayMoneyDialog();
        Bundle bundle = new Bundle();
        bundle.putString("price", price);
        bundle.putDouble("bl", bl);
        bundle.putString("sid", sid);
        bundle.putString("name", name);
        bundle.putSerializable("entity", entity);
        dialog.setArguments(bundle);
        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(false);
        this.getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    if (dislistener != null) {
                        dislistener.onDismiss();
                    }
                    return true;
                } else
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
        if (!TextUtils.isEmpty(userid)) {
            user = dao.findUserInfoById(userid);
        }
        if (user != null) {
            if (TextUtils.isEmpty(user.getOfwlgsinfo())) {
                isOfwlgsinfo = true;
            } else {
                isOfwlgsinfo = false;
            }
        }
        price = getArguments().getString("price");
        bl = getArguments().getDouble("bl");
        name = getArguments().getString("name");
        sid = getArguments().getString("sid");
        entity = (GoodsEntity) getArguments().getSerializable("entity");
        checkBox_rem_paypwd = view.findViewById(R.id.checkBox_rem_paypwd);

        checkBox = view.findViewById(R.id.checkBox);
        checkBox1 = view.findViewById(R.id.checkBox1);
        mImageClose = view.findViewById(R.id.img_close);
        mTextBL = view.findViewById(R.id.tv_bzj);
        mTextYe = view.findViewById(R.id.tv_yue);
        mBtn = view.findViewById(R.id.btn_ok);
        mTextTips = view.findViewById(R.id.tv_tips);
        mEditPsd = view.findViewById(R.id.editpsd);
        String paypwd = SharePreferenceUtil.getInstance(getActivity()).getPayPwd();
        if (!TextUtils.isEmpty(paypwd)) {
            if (checkBox_rem_paypwd.isChecked()) {
                mEditPsd.setText(paypwd);
            }

        }
        mTextXY = view.findViewById(R.id.tv_xy);
        mTextXY1 = view.findViewById(R.id.tv_xy1);
        mTextXY1.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox.isChecked()) {
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
                                submitBestSignInfo();
                                listener.onOrder(psd, String.format("%.2f", dbl));
                            }
                        }
                    }
                } else {
                    Toast.makeText(getActivity(), "暂无余额信息，请充值0.01元!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        mImageClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (dislistener != null) {
                    dislistener.onDismiss();

                }
                dismiss();
            }
        });

        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (checkBox1.isChecked()) {
                        mBtn.setClickable(true);
                        mBtn.setEnabled(true);
                        mBtn.setBackgroundResource(R.drawable.btn_cz_bg);
                    }

                } else {
                    mBtn.setClickable(false);
                    mBtn.setEnabled(false);
                    mBtn.setBackgroundResource(R.drawable.btn_clickable_false_bg);
                }
            }
        });
        checkBox1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    if (checkBox.isChecked()) {
                        mBtn.setClickable(true);
                        mBtn.setEnabled(true);
                        mBtn.setBackgroundResource(R.drawable.btn_cz_bg);
                    }
                } else {
                    mBtn.setClickable(false);
                    mBtn.setEnabled(false);
                    mBtn.setBackgroundResource(R.drawable.btn_clickable_false_bg);
                }
            }
        });
        mTextXY1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String baseurl=Constants.WEBSERVICE_URL+"showdata/cySiJi.jsp?"+getParam(map);
                Intent intent = new Intent(getActivity(), XYActivity.class);
                Log.e("baseurl====", baseurl);
                intent.putExtra("url", baseurl);
                getActivity().startActivity(intent);
                getActivity().overridePendingTransition(R.anim.slide_from_right, R.anim.slide_to_left);
            }
        });

        checkBox_rem_paypwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    String paypwd = SharePreferenceUtil.getInstance(getActivity()).getPayPwd();
                    if (!TextUtils.isEmpty(paypwd)) {
                        mEditPsd.setText(paypwd);
                    }
                } else {
                    mEditPsd.setText("");
                }

            }
        });

        initDatas();

    }

    private void submitBestSignInfo(){
        Retrofit retrofit = new Retrofit.Builder().baseUrl(Constants.BESTSIGN_URL_TEST).build();
        ServiceStore serviceStore = retrofit.create(ServiceStore.class);
        Call<ResponseBody> call=serviceStore.autoSignAgre(map);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                ResponseBody body=response.body();
                String str= null;
                try {
                    if(null!=body){
                        str = body.string();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.e("autoSignAgre","autoSignAgre onResponse=="+str);
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }
    private  String getParam(Map map){
        StringBuffer buffer = new StringBuffer();
        if (map != null && !map.isEmpty()) {
            Iterator var4 = map.entrySet().iterator();

            while(var4.hasNext()) {
                Map.Entry<String, String> entry = (Map.Entry)var4.next();
                buffer.append((String)entry.getKey()).append("=").append((String)entry.getValue()).append("&");
            }

            buffer.deleteCharAt(buffer.length() - 1);
        }
        return buffer.toString();
    }
    private void initDatas() {
        if (!TextUtils.isEmpty(userid)) {
            User user = dao.findUserInfoById(userid);
            getMoneyInfo(user);
            getInfo(user, userid);

        }
    }

    private void getInfo(final User user, final String id) {
        RequestManager.getInstance()
                .mServiceStore
                .findMyRz(user.getToken(), user.getUserId(), Constants.AppId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new ResultObserver(new RequestManager.onRequestCallBack() {
                    @Override
                    public void onSuccess(String msg) {
                        Log.e("getRzInfo onSuccess", msg);
                        boolean isSuccess = false;
                        try {
                            JSONObject jsonObject = new JSONObject(msg.replaceAll("\r", "").replaceAll("\n", ""));
                            analysisJson(jsonObject);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(String msg) {
                    }
                }));
    }

    private void complateParms(GoodsEntity entity, String xm, String sfzh, String scph) {
        Calendar calendar = Calendar.getInstance();
        String rowid = entity.getSid();
        map.put("rowid", rowid);
        String account = userid;
        map.put("account",account);
        String name = xm;
        map.put("name",name);
        String userType = "1";
        map.put("userType",userType);
        map.put("agreType","1");
        String identity = sfzh;
        map.put("identity",identity);
        String sjcyr = xm;
        map.put("sjcyr",sjcyr);
        String hwmc = entity.getName();
        map.put("hwmc",hwmc);
        String fhr = entity.getHzxm();
        map.put("fhr",fhr);
        String fhrdh = entity.getFhrdh();
        map.put("fhrdh",fhrdh);
        String qyd = entity.getFrom_addr();
        map.put("qyd",qyd);
        String shr = entity.getShrName();
        map.put("shr",shr);
        String shrdh = entity.getShrTel();
        map.put("shrdh",shrdh);
        String mdd = entity.getTo_addr();
        map.put("mdd",mdd);
        String jsy = xm;
        map.put("jsy",jsy);
        String jsydh = userid;
        map.put("jsydh",jsydh);
        String dw = entity.getVolume();
        map.put("dw",dw);
        String bzfs = "";
        map.put("bzfs",bzfs);
        String cph = scph;
        map.put("cph",cph);
        String zhsj = "";
        map.put("zhsj",zhsj);
        String ydsj = "";
        map.put("ydsj",ydsj);
        String hz = entity.getZyf();
        map.put("hz",hz);
        String yunfei = entity.getZyf();
        map.put("yunfei",yunfei);
        String jf = "河南省脱颖实业有限公司";
        map.put("jf",jf);
        String yf = xm;
        map.put("yf",yf);
        String n = calendar.get(Calendar.YEAR) + "";
        map.put("n",n);
        String y = (calendar.get(Calendar.MONTH )+1) + "";
        map.put("y",y);
        String r = calendar.get(Calendar.DATE) + "";
        map.put("r",r);
        String hth = entity.getSid();
        map.put("hth",hth);
        String qdd = entity.getFrom_addr();
        map.put("qdd",qdd);
        Log.e("autoSignParms",map.toString());
    }

    private void analysisJson(JSONObject json) throws JSONException {
        String xm = null;
        String sfzh = null;
        String cph = null;
        if (json.has("rz#xm")) {
            xm = json.getString("rz#xm");
        } else {
            xm = "";
        }
        if (json.has("rz#sfzh")) {
            sfzh = json.getString("rz#sfzh");

        } else {
            sfzh = "";
        }
        if (json.has("rz#cph")) {
            cph = json.getString("rz#cph");

        } else {
            cph = "";
        }
        complateParms(entity, xm, sfzh, cph);
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
                                    String pdwlgs = entity.getPdwlgs();
                                    if (!"-1".equals(pdwlgs)) {
                                        dbl = 0;
                                    } else {
                                        dbl = (1 - bl) * d * 0.03;
                                    }
                                    if (TextUtils.isEmpty(pdwlgs)) {
                                        mTextYe.setText(money + "元");
                                    } else {
                                        mTextYe.setText("****元");
                                    }
                                    String strMoney = String.format("%.2f", dbl);
                                    mTextBL.setText(strMoney + "元");
                                    String task_id = entity.getTask_id();
                                    String driverPrice = entity.getDriverPrice();
                                    if (!TextUtils.isEmpty(task_id) && !TextUtils.isEmpty(driverPrice)) {
                                        dbl = 0;
                                        mTextBL.setText(0 + "元");
                                    }
                                    if (!TextUtils.isEmpty(money)) {
                                        double d1 = Double.valueOf(money);
                                        if (dbl > d1) {
                                            mTextTips.setVisibility(View.VISIBLE);
                                        }
                                    }
                                }
                            } else {
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
        try {
            super.show(manager, tag);
        } catch (IllegalStateException ignore) {
        }
    }

    public void setOnOrderListener(OnOrderListener listener) {
        this.listener = listener;
    }

    public void setOnDismissListener(onDismissListener dislistener) {
        this.dislistener = dislistener;
    }

    public interface OnOrderListener {
        void onOrder(String psd, String money);
    }

    public interface onDismissListener {
        void onDismiss();
    }
}
