package io.cordova.zhqy.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.HashMap;

import butterknife.BindView;
import cn.org.bjca.signet.coss.api.SignetCossApi;
import cn.org.bjca.signet.coss.bean.CossClearCertResult;
import cn.org.bjca.signet.coss.bean.CossGetCertResult;
import cn.org.bjca.signet.coss.bean.CossReqCertResult;
import cn.org.bjca.signet.coss.bean.CossSignPinResult;
import cn.org.bjca.signet.coss.component.core.enums.CertType;
import cn.org.bjca.signet.coss.interfaces.CossReqCertCallBack;
import cn.org.bjca.signet.coss.interfaces.CossSignPinCallBack;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.bean.BaseBean;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.BaseActivity;
import io.cordova.zhqy.utils.CAErrorUtils;
import io.cordova.zhqy.utils.FinishActivity;
import io.cordova.zhqy.utils.JsonUtil;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.ViewUtils;

public class CertificateActivateNextTwoActivity extends BaseActivity {

    @BindView(R.id.rl_sign)
    RelativeLayout rl_sign;

    @BindView(R.id.et_01)
    EditText et_01;

    @BindView(R.id.et_02)
    EditText et_02;

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.ll_bottom)
    LinearLayout ll_bottom;



    private String successCode = "0x00000000";


    @Override
    protected int getResourceId() {
        return R.layout.activity_zhengshu_jihuo_next_two;
    }

    @Override
    protected void initListener() {
        super.initListener();

        final String jihuoma = getIntent().getStringExtra("jihuoma");
        String title = getIntent().getStringExtra("title");
        final String type = getIntent().getStringExtra("type");
        if(type.equals("0")){//从证书激活第二步传过来的

        }else if(type.equals("1")){//从FaceDialog传过来了
            et_01.setHint("请输入6位数字PIN码");
            et_02.setVisibility(View.GONE);
            ll_bottom.setVisibility(View.GONE);
        }
        tv_title.setText(title);
        rl_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String et_01content = et_01.getText().toString().trim();
                if(type.equals("0")){//从证书激活第二步传过来的

                    String et_02content = et_02.getText().toString().trim();
                    if(null != jihuoma){
                        if(jihuoma.equals("") ){
                            ToastUtils.showToast(CertificateActivateNextTwoActivity.this,"激活码不能为空!");
                            return;
                        }
                    }


                    if(et_01content.equals("") || et_02content.equals("")){
                        ToastUtils.showToast(CertificateActivateNextTwoActivity.this,"请输入PIN码");
                        return;
                    }

                    if(!et_01content.equals(et_02content) ){
                        ToastUtils.showToast(CertificateActivateNextTwoActivity.this,"两次输入的PIN码不一致!");
                        return;
                    }



                    SignetCossApi.getCossApiInstance(AesEncryptUtile.APP_ID, AesEncryptUtile.CA_ADDRESS).cossReqCertWithPin(CertificateActivateNextTwoActivity.this, jihuoma, et_01content, new CossReqCertCallBack() {
                        @Override
                        public void onCossReqCert(final CossReqCertResult result) {
                            if (result.getErrCode().equalsIgnoreCase(successCode)) {
                                ToastUtils.showToast(CertificateActivateNextTwoActivity.this,"激活成功!");
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        String msspID = result.getMsspID();
                                        Log.e("msspID",msspID+"");
                                        String userName = (String) SPUtils.get(CertificateActivateNextTwoActivity.this,"username","");
                                        SPUtils.put(CertificateActivateNextTwoActivity.this,"msspID",msspID);
                                        SPUtils.put(CertificateActivateNextTwoActivity.this,userName,userName+"_"+et_01content);
                                        FinishActivity.clearActivity();
                                        finish();

                                        Intent intent2 = new Intent();
                                        intent2.setAction("refreshCAResult");
                                        sendBroadcast(intent2);

                                        addDataToServer(et_01content);

                                    }
                                });
                            }else if (result.getErrCode().equalsIgnoreCase("0x81400003")) {

                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.showToast(CertificateActivateNextTwoActivity.this,result.getErrMsg());
                                    }
                                });
                            } else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        ToastUtils.showToast(CertificateActivateNextTwoActivity.this, result.getErrMsg());

                                    }
                                });
                            }
                        }
                    });

                }else if(type.equals("1")){//从FaceDialog传过来了
                    if(et_01content.equals("")){
                        ToastUtils.showToast(CertificateActivateNextTwoActivity.this,"请输入PIN码");
                        return;
                    }

                    //调用立即签名方法
                    String userName = (String) SPUtils.get(CertificateActivateNextTwoActivity.this,"username","");
                    String pin = (String) SPUtils.get(CertificateActivateNextTwoActivity.this,userName,"");
                    String sName = pin.split("_")[0];
                    String s = pin.split("_")[1];
                    if(!et_01content.equals(s) || !userName.equals(sName)){
                        ToastUtils.showToast(CertificateActivateNextTwoActivity.this,"您输入PIN码错误，请重新输入");
                        return;
                    }

                    String deleteOrSign = (String) SPUtils.get(CertificateActivateNextTwoActivity.this,"deleteOrSign","");
                    if(deleteOrSign.equals("0")){//删除
                        String msspID = (String) SPUtils.get(CertificateActivateNextTwoActivity.this, "msspID", "");
                        CossClearCertResult result = SignetCossApi.getCossApiInstance(AesEncryptUtile.APP_ID, AesEncryptUtile.CA_ADDRESS).cossClearCert(CertificateActivateNextTwoActivity.this, msspID, CertType.ALL_CERT);
                        if (result.getErrCode().equalsIgnoreCase(successCode)) {
                            ToastUtils.showToast(CertificateActivateNextTwoActivity.this,"删除成功!");

                        } else {
                            ToastUtils.showToast(CertificateActivateNextTwoActivity.this,result.getErrMsg());

                        }
                        Intent intent2 = new Intent();
                        intent2.setAction("refreshCAResult");
                        sendBroadcast(intent2);
                        FinishActivity.clearActivity();
                        finish();
                    }else if(deleteOrSign.equals("1")){//重新激活

                        Intent intent = new Intent(CertificateActivateNextTwoActivity.this,CertificateActivateActivity.class);
                        startActivity(intent);
                        FinishActivity.addActivity(CertificateActivateNextTwoActivity.this);

                    }else {//立即签名

                        getSDKSignData();
                    }


                }




            }
        });
    }

    private String certContent = "";
    /**
     * 上传msPid到服务器
     * @param et_01content
     */
    private void addDataToServer(String et_01content) {
        String msspID = (String) SPUtils.get(this, "msspID", "");
        String userId = (String) SPUtils.get(MyApp.getInstance(), "userId", "");
        final CossGetCertResult result = SignetCossApi.getCossApiInstance(AesEncryptUtile.APP_ID, AesEncryptUtile.CA_ADDRESS).cossGetCert(this, msspID, CertType.ALL_CERT);
        HashMap<CertType, String> certMap = result.getCertMap();

        for(HashMap.Entry<CertType, String> entry : certMap.entrySet()){
            //System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
            if(entry.getValue() != null){
                certContent = entry.getValue();
            }
        }


        OkGo.<String>post(UrlRes.HOME_URL+ UrlRes.saveMemberAndCAUrl)
                .params( "memberCaMsspid",msspID)
                .params( "memberCaMemberId",userId)
                .params( "memberCaPin",et_01content)
                .params( "memberCaCertificate",certContent)
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {


                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);


                    }
                });
    }

    String signId;
    private void getSDKSignData() {
        String msspID = (String) SPUtils.get(CertificateActivateNextTwoActivity.this, "msspID", "");
        String userName = (String) SPUtils.get(CertificateActivateNextTwoActivity.this,"username","");

        signId = (String) SPUtils.get(CertificateActivateNextTwoActivity.this,"signId","");
        String pin = (String) SPUtils.get(CertificateActivateNextTwoActivity.this,userName,"");

        String sName = pin.split("_")[0];
        String s = pin.split("_")[1];
        SignetCossApi.getCossApiInstance(AesEncryptUtile.APP_ID, AesEncryptUtile.CA_ADDRESS).cossSignWithPin(this, msspID, signId, s, new CossSignPinCallBack() {
            @Override
            public void onCossSignPin(final CossSignPinResult result) {
                if (result.getErrCode().equalsIgnoreCase(successCode)) {
                    ToastUtils.showToast(CertificateActivateNextTwoActivity.this,"签名成功!");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String signature = result.getSignature();
                            String cert = result.getCert();

                            clickSignData(signature,cert);
                        }
                    });
                } else {
                    ToastUtils.showToast(CertificateActivateNextTwoActivity.this,result.getErrMsg());
                    clickSignErrorData(signId,result.getErrMsg());
                    Intent intent2 = new Intent();
                    intent2.setAction("addJsResultData");
                    intent2.putExtra("message",result.getErrMsg());
                    intent2.putExtra("signId",signId);
                    sendBroadcast(intent2);
                    FinishActivity.clearActivity();
                    finish();

                }
            }
        });
    }

    /**
     * 上传错误信息到服务器
     * @param signId
     * @param errMsg
     */
    private void clickSignErrorData(String signId, String errMsg) {
        OkGo.<String>post(UrlRes.HOME_URL+ UrlRes.signFailedUrl)
                .params( "signId",signId)
                .params( "message ",errMsg)
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {
                        ViewUtils.cancelLoadingDialog();
                        Log.e("App签名错误信息日志",response.body());


                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }

    /**
     * 立即签名
     * @param signature
     * @param cert
     */
    private void clickSignData(final String signature, final String cert) {
        ViewUtils.createLoadingDialog(this);
        OkGo.<String>post(UrlRes.HOME_URL+ UrlRes.clickSignUrl)
                .params( "signId",signId)
                .params( "cert",cert)
                .params( "signature",signature)
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {
                        ViewUtils.cancelLoadingDialog();
                        Log.e("App签名",response.body());
                        BaseBean baseBean = JsonUtil.parseJson(response.body(),BaseBean.class);
                        if(baseBean.isSuccess()){

                            Intent intent2 = new Intent();
                            intent2.setAction("addJsResultData");
                            intent2.putExtra("signature",signature);
                            intent2.putExtra("cert",cert);
                            intent2.putExtra("signId",signId);
                            sendBroadcast(intent2);


                        }else {
                            ToastUtils.showToast(CertificateActivateNextTwoActivity.this,baseBean.getMsg());
                            Intent intent2 = new Intent();
                            intent2.putExtra("signId",signId);
                            intent2.setAction("addJsResultData");
                            sendBroadcast(intent2);
                        }

                        FinishActivity.clearActivity();
                        finish();

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ViewUtils.cancelLoadingDialog();

                    }
                });
    }


}
