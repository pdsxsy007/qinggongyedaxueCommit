package io.cordova.zhqy.activity;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import butterknife.BindView;
import cn.org.bjca.signet.coss.api.SignetCossApi;
import cn.org.bjca.signet.coss.bean.CossReqCertResult;
import cn.org.bjca.signet.coss.interfaces.CossReqCertCallBack;
import io.cordova.zhqy.R;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.BaseActivity;
import io.cordova.zhqy.utils.ToastUtils;

public class CertificateActivateNextActivity extends BaseActivity {

    @BindView(R.id.rl_sign)
    RelativeLayout rl_sign;

    @BindView(R.id.et_jihuoma)
    EditText et_jihuoma;

    private String successCode = "0x00000000";


    @Override
    protected int getResourceId() {
        return R.layout.activity_zhengshu_jihuo_next;
    }

    @Override
    protected void initListener() {
        super.initListener();
        rl_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String jihuoma = et_jihuoma.getText().toString().trim();
                if(jihuoma.equals("") ){
                    ToastUtils.showToast(CertificateActivateNextActivity.this,"请输入激活码!");
                    return;
                }

                SignetCossApi.getCossApiInstance(AesEncryptUtile.APP_ID, AesEncryptUtile.CA_ADDRESS).cossReqCert(CertificateActivateNextActivity.this, jihuoma, new CossReqCertCallBack() {
                    @Override
                    public void onCossReqCert(final CossReqCertResult result) {
                        if (result.getErrCode().equalsIgnoreCase(successCode)) {
                            ToastUtils.showToast(CertificateActivateNextActivity.this,"激活成功!");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    String msspID = result.getMsspID();
                                    Log.e("msspID",msspID+"");
                                }
                            });
                        }else if (result.getErrCode().equalsIgnoreCase("0x81400003")) {
                            ToastUtils.showToast(CertificateActivateNextActivity.this,result.getErrMsg());
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {

                                }
                            });
                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    ToastUtils.showToast(CertificateActivateNextActivity.this,result.getErrCode() + " " + result.getErrMsg());

                                }
                            });
                        }
                    }
                });
            }
        });
    }
}
