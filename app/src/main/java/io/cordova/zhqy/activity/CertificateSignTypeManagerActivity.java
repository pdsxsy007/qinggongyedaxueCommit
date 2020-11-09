package io.cordova.zhqy.activity;

import android.content.Intent;
import android.os.Build;
import android.security.keystore.KeyProperties;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import cn.org.bjca.signet.coss.api.SignetCossApi;
import cn.org.bjca.signet.coss.bean.CossClearCertResult;
import cn.org.bjca.signet.coss.component.core.enums.CertType;
import io.cordova.zhqy.R;
import io.cordova.zhqy.Constants;
import io.cordova.zhqy.fingerprint.FingerprintHelper;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.BaseActivity;
import io.cordova.zhqy.utils.FinishActivity;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.PermissionsUtil;
import io.cordova.zhqy.utils.SPUtil;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.fingerUtil.FingerprintUtil;
import io.cordova.zhqy.widget.finger.CommonTipDialog;
import io.cordova.zhqy.widget.finger.FingerprintVerifyDialog2;

public class CertificateSignTypeManagerActivity extends BaseActivity implements View.OnClickListener, FingerprintHelper.SimpleAuthenticationCallback , PermissionsUtil.IPermissionsCallback{


    @BindView(R.id.iv_back)
    ImageView iv_back;

    @BindView(R.id.rl_01)
    RelativeLayout rl_01;

    @BindView(R.id.rl_02)
    RelativeLayout rl_02;

    @BindView(R.id.rl_03)
    RelativeLayout rl_03;

    @BindView(R.id.iv_01)
    ImageView iv_01;

    @BindView(R.id.iv_02)
    ImageView iv_02;

    @BindView(R.id.iv_03)
    ImageView iv_03;

    @BindView(R.id.tv_title)
    TextView tv_title;

    private FingerprintHelper helper;

    private boolean isOpenFinger;

    String signTypeFromWhere;

    @Override
    protected int getResourceId() {
        return R.layout.activity_cert_sign_type;
    }

    @Override
    protected void initListener() {
        super.initListener();
        rl_01.setVisibility(View.GONE);
        tv_title.setText("验证方式");
       /* signTypeFromWhere = getIntent().getStringExtra("signType");
        if(signTypeFromWhere.equals("0")){
            iv_01.setBackgroundResource(R.mipmap.check_box02);
            iv_02.setBackgroundResource(R.mipmap.check_box01);
            iv_03.setBackgroundResource(R.mipmap.check_box01);
        }else if(signTypeFromWhere.equals("1")){
            iv_01.setBackgroundResource(R.mipmap.check_box01);
            iv_02.setBackgroundResource(R.mipmap.check_box02);
            iv_03.setBackgroundResource(R.mipmap.check_box01);
        }else {
            iv_01.setBackgroundResource(R.mipmap.check_box01);
            iv_02.setBackgroundResource(R.mipmap.check_box01);
            iv_03.setBackgroundResource(R.mipmap.check_box02);
        }*/

        iv_back.setOnClickListener(this);
        rl_01.setOnClickListener(this);
        rl_02.setOnClickListener(this);
        rl_03.setOnClickListener(this);



    }



    @Override
    public void onPermissionsGranted(int requestCode, String... permission) {
        isOpen = 1;

    }

    @Override
    public void onPermissionsDenied(int requestCode, String... permission) {

    }




    private PermissionsUtil permissionsUtil;
    private int isOpen = 0;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_02:
                /*if(fingerprintVerifyDialog != null){
                    fingerprintVerifyDialog.dismiss();
                    //helper.stopAuthenticate();
                    //fingerprintVerifyErrorTipDialog.dismiss();
                    fingerprintVerifyDialog = null;

                }*/
                checkSupportFingerprint();

                break;
            case R.id.rl_03:
                iv_01.setBackgroundResource(R.mipmap.check_box01);
                iv_02.setBackgroundResource(R.mipmap.check_box01);
                iv_03.setBackgroundResource(R.mipmap.check_box02);
                Intent intent = new Intent(CertificateSignTypeManagerActivity.this,CertificateActivateNextTwoActivity.class);
                intent.putExtra("title","使用PIN码验证");
                intent.putExtra("type","1");
                startActivity(intent);
                FinishActivity.addActivity(CertificateSignTypeManagerActivity.this);
                break;
        }
    }

    /**
     * 是否支持指纹
     */
    private void checkSupportFingerprint() {
        helper = FingerprintHelper.getInstance();
        helper.init(getApplicationContext());
        helper.setCallback(this);
        if (helper.checkFingerprintAvailable(this) != -1) {

            //判断指纹功能是否开启
            String personName = (String) SPUtils.get(MyApp.getInstance(), "personName", "");
            isOpenFinger =SPUtil.getInstance().getBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN+"_"+personName, true);
            if(isOpenFinger){
                iv_01.setBackgroundResource(R.mipmap.check_box01);
                iv_02.setBackgroundResource(R.mipmap.check_box02);
                iv_03.setBackgroundResource(R.mipmap.check_box01);
                openFingerprintLogin();
            }else {
                Intent intent = new Intent(CertificateSignTypeManagerActivity.this,FingerManagerActivity.class);
                startActivity(intent);
            }

        }else {
            ToastUtils.showToast(this,"设备不支持指纹登录");
        }

    }

    private FingerprintVerifyDialog2 fingerprintVerifyDialog;
    /**
     * @description 开启指纹登录功能
     * @author HaganWu
     * @date 2019/1/29-10:20
     */
    private void openFingerprintLogin() {
        Log.e("hagan", "openFingerprintLogin");

        helper.generateKey();
        if (fingerprintVerifyDialog == null) {
            fingerprintVerifyDialog = new FingerprintVerifyDialog2(this,1);
        }
        fingerprintVerifyDialog.setContentText("请验证指纹");
        fingerprintVerifyDialog.setOnCancelButtonClickListener(new FingerprintVerifyDialog2.OnDialogCancelButtonClickListener() {
            @Override
            public void onCancelClick(View v) {
                helper.stopAuthenticate();
            }
        });
        fingerprintVerifyDialog.show();
        helper.setPurpose(KeyProperties.PURPOSE_ENCRYPT);
        helper.authenticate();
    }


    @Override
    public void onAuthenticationSucceeded(String value) {
        if (fingerprintVerifyDialog != null && fingerprintVerifyDialog.isShowing()) {
            fingerprintVerifyDialog.dismiss();
            //Toast.makeText(this, "指纹登录已开启", Toast.LENGTH_SHORT).show();
            isOpenFinger = true;
            SPUtil.getInstance().putBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN, true);
            saveLocalFingerprintInfo();

          /*  Intent intent = new Intent(CertificateSignTypeManagerActivity.this,CertificateActivateActivity.class);
            startActivity(intent);
            FinishActivity.addActivity(this);*/
            String deleteOrSign = (String) SPUtils.get(CertificateSignTypeManagerActivity.this,"deleteOrSign","");
            if(deleteOrSign.equals("0")) {//删除
                String msspID = (String) SPUtils.get(CertificateSignTypeManagerActivity.this, "msspID", "");
                CossClearCertResult result = SignetCossApi.getCossApiInstance(AesEncryptUtile.APP_ID, AesEncryptUtile.CA_ADDRESS).cossClearCert(CertificateSignTypeManagerActivity.this, msspID, CertType.ALL_CERT);
                if (result.getErrCode().equalsIgnoreCase("0x00000000")) {
                    ToastUtils.showToast(CertificateSignTypeManagerActivity.this,"删除成功!");

                } else {
                    ToastUtils.showToast(CertificateSignTypeManagerActivity.this,result.getErrMsg());

                }
                Intent intent2 = new Intent();
                intent2.setAction("refreshCAResult");
                sendBroadcast(intent2);
                FinishActivity.clearActivity();
                finish();
            }else {
                Intent intent = new Intent(CertificateSignTypeManagerActivity.this,CertificateActivateActivity.class);
                startActivity(intent);
                FinishActivity.addActivity(this);
            }

        }
    }

    @Override
    public void onAuthenticationFail() {
        showFingerprintVerifyErrorInfo("指纹不匹配");
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        /*if (fingerprintVerifyDialog != null && fingerprintVerifyDialog.isShowing()) {
            fingerprintVerifyDialog.dismiss();
        }
        showTipDialog(errorCode, errString.toString());*/
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        showFingerprintVerifyErrorInfo(helpString.toString());
    }

    private CommonTipDialog fingerprintVerifyErrorTipDialog;
    private void showTipDialog(int errorCode, CharSequence errString) {
        if (fingerprintVerifyErrorTipDialog == null) {
            fingerprintVerifyErrorTipDialog = new CommonTipDialog(this);
        }
        fingerprintVerifyErrorTipDialog.setContentText(errString+"");
        fingerprintVerifyErrorTipDialog.setSingleButton(true);
        fingerprintVerifyErrorTipDialog.setOnSingleConfirmButtonClickListener(new CommonTipDialog.OnDialogSingleConfirmButtonClickListener() {
            @Override
            public void onConfirmClick(View v) {
                helper.stopAuthenticate();
            }
        });
        fingerprintVerifyErrorTipDialog.show();
    }


    private void showFingerprintVerifyErrorInfo(String info) {
        if (fingerprintVerifyDialog != null && fingerprintVerifyDialog.isShowing()) {
            fingerprintVerifyDialog.setContentText(info);
        }
    }

    private void saveLocalFingerprintInfo() {
        SPUtil.getInstance().putString(Constants.SP_LOCAL_FINGERPRINT_INFO, FingerprintUtil.getFingerprintInfoString(getApplicationContext()));
    }



}
