package com.tuoying.hykc.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tuoying.hykc.R;
import com.tuoying.hykc.activity.BaseActivity;
import com.tuoying.hykc.app.Constants;

public class WXPayEntryActivity extends BaseActivity implements IWXAPIEventHandler{
	
	private static final String TAG = "MicroMsg.SDKSample.WXPayEntryActivity";

	private TextView wxpay_jieguo;
	private Toolbar mToolbar;
    private IWXAPI api;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_pay_result);
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
		mToolbar.setTitle("充值提示");
		setSupportActionBar(mToolbar);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);
		wxpay_jieguo=findViewById(R.id.tv_result);
    	api = WXAPIFactory.createWXAPI(this, Constants.wxAPP_ID);
        api.handleIntent(getIntent(), this);
    }

	@Override
	public void init() {

	}



	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
        api.handleIntent(intent, this);
	}

	@Override
	public void onReq(BaseReq req) {
	}

	@Override
	public void onResp(BaseResp resp) {
		if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
			int type = resp.errCode;
			switch (type) {
				case 0:
					wxpay_jieguo.setText("支付成功");
					break;
				case -1:
					wxpay_jieguo.setText("支付失败");
					break;
				case -2:
					wxpay_jieguo.setText("您已经取消交易");
					break;
			}
		}
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		if (id == android.R.id.home) {
			finish();
			overridePendingTransition(R.anim.slide_from_left, R.anim.slide_to_right);
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
}