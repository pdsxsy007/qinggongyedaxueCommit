package io.cordova.zhqy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.security.keystore.KeyProperties;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import butterknife.BindView;

import cn.org.bjca.signet.coss.api.SignetCossApi;
import cn.org.bjca.signet.coss.bean.CossSignPinResult;
import cn.org.bjca.signet.coss.interfaces.CossSignPinCallBack;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.bean.BaseBean;
import io.cordova.zhqy.Constants;
import io.cordova.zhqy.fingerprint.FingerprintHelper;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.BaseActivity;
import io.cordova.zhqy.utils.FinishActivity;
import io.cordova.zhqy.utils.JsonUtil;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.PermissionsUtil;
import io.cordova.zhqy.utils.SPUtil;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.ViewUtils;
import io.cordova.zhqy.utils.fingerUtil.FingerprintUtil;
import io.cordova.zhqy.widget.finger.CommonTipDialog;
import io.cordova.zhqy.widget.finger.FingerprintVerifyDialog2;

import static io.cordova.zhqy.utils.AesEncryptUtile.key;

public class CertificateSignTypeActivity extends BaseActivity implements View.OnClickListener, FingerprintHelper.SimpleAuthenticationCallback , PermissionsUtil.IPermissionsCallback{


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

        signTypeFromWhere = getIntent().getStringExtra("signType");
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
        }

        iv_back.setOnClickListener(this);
        rl_01.setOnClickListener(this);
        rl_02.setOnClickListener(this);
        rl_03.setOnClickListener(this);

        registerBoradcastReceiver1();

    }

    private void registerBoradcastReceiver1() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("faceDialogChoose");
        //注册广播
        registerReceiver(broadcastReceiver, myIntentFilter);
    }

    private int imageid = 0;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String FaceActivity = intent.getStringExtra("FaceActivity");
            if(action.equals("faceDialogChoose")){

                SPUtils.put(CertificateSignTypeActivity.this,"isloading2","");

                if(imageid == 0){
                    String closeFaceFlag = (String) SPUtils.get(CertificateSignTypeActivity.this, "closeFaceFlag2", "");
                    if(!closeFaceFlag.equals("1")){
                        checkFaceResult(FaceActivity);
                        SPUtils.put(CertificateSignTypeActivity.this,"closeFaceFlag2","");
                    }

                }

            }

        }
    };

    private void checkFaceResult(String faceActivity) {
        if(imageid == 0){
            if(faceActivity != null){
                imageid = 1;

                String s = (String)SPUtils.get(CertificateSignTypeActivity.this, "bitmap", "");
                String imei = (String) SPUtils.get(CertificateSignTypeActivity.this, "imei", "");
                String username = (String) SPUtils.get(CertificateSignTypeActivity.this, "phone", "");

                try {
                    //String secret  = AesEncryptUtile.encrypt(Calendar.getInstance().getTimeInMillis()+ "_"+"123456",key);
                    String secret = AesEncryptUtile.encrypt(username,key);
                    OkGo.<String>post(UrlRes.HOME2_URL+UrlRes.distinguishFaceUrl)
                            .tag(this)
                            .params( "openId",AesEncryptUtile.openid)
                            .params( "memberId",secret)
                            .params( "img",s )
                            .params( "equipmentId",imei)
                            .execute(new StringCallback(){

                                @Override
                                public void onStart(Request<String, ? extends Request> request) {
                                    super.onStart(request);
                                    //ViewUtils.createLoadingDialog2(LoginActivity2.this,true,"人脸识别中");

                                }

                                @Override
                                public void onSuccess(Response<String> response) {
                                    SPUtils.put(getApplicationContext(),"isloading2","");
                                    ViewUtils.cancelLoadingDialog();
                                    Log.e("tag",response.body());
                                    BaseBean baseBean = JsonUtil.parseJson(response.body(),BaseBean.class);
                                    boolean success = baseBean.isSuccess();
                                    imageid = 0;
                                    if(success){

                                        getSDKSignData();

                                    }else {
                                        ToastUtils.showToast(CertificateSignTypeActivity.this,baseBean.getMsg());
                                    }


                                }

                                @Override
                                public void onError(Response<String> response) {
                                    super.onError(response);
                                    SPUtils.put(getApplicationContext(),"isloading2","");

                                    ViewUtils.cancelLoadingDialog();
                                    imageid = 0;

                                }
                            });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }else {
                imageid = 0;
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //需要调用onRequestPermissionsResult
        if(null != permissionsUtil){
            permissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        //监听跳转到权限设置界面后再回到应用
        if(null != permissionsUtil){
            permissionsUtil.onActivityResult(requestCode, resultCode, intent);
        }

    }


    @Override
    public void onPermissionsGranted(int requestCode, String... permission) {
        isOpen = 1;

    }

    @Override
    public void onPermissionsDenied(int requestCode, String... permission) {

    }

    @Override
    protected void onResume() {
        super.onResume();
        String isLoading2 = (String) SPUtils.get(this, "isloading2", "");
        Log.e("isLoading2",isLoading2);
        if(!isLoading2 .equals("")){
            ViewUtils.createLoadingDialog2(this,true,"人脸识别中");
            SPUtils.put(this,"isloading2","");


        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        SPUtils.put(this,"isloading2","");

    }

    private PermissionsUtil permissionsUtil;
    private int isOpen = 0;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_back:
                finish();
                break;
            case R.id.rl_01:
                iv_01.setBackgroundResource(R.mipmap.check_box02);
                iv_02.setBackgroundResource(R.mipmap.check_box01);
                iv_03.setBackgroundResource(R.mipmap.check_box01);
                SPUtils.put(CertificateSignTypeActivity.this,"signType","0");
                String signType = (String) SPUtils.get(CertificateSignTypeActivity.this, "signType", "");
                if(signTypeFromWhere.equals("0") && signType.equals("0")){

                    finish();
                }else {

                    permissionsUtil=  PermissionsUtil
                            .with(this)
                            .requestCode(12)
                            .isDebug(true)//开启log
                            .permissions(PermissionsUtil.Permission.Camera.CAMERA)
                            .request();

                    if(isOpen == 1){
                        SPUtils.put(CertificateSignTypeActivity.this,"signType","0");
                        SPUtils.put(this,"bitmap","");
                        Intent intent = new Intent(this,FaceDialogChooseActivity.class);
                        startActivityForResult(intent,99);
                        FinishActivity.addActivity(this);
                        imageid = 0;
                    }

                   /* Intent intent = new Intent(CertificateSignTypeActivity.this,FaceDialogActivity.class);
                    startActivity(intent);
                    FinishActivity.addActivity(CertificateSignTypeActivity.this);*/
                }

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
                SPUtils.put(CertificateSignTypeActivity.this,"signType","2");
                String signType3 = (String) SPUtils.get(CertificateSignTypeActivity.this, "signType", "");
                if(signTypeFromWhere.equals("2") && signType3.equals("2")){
                    finish();
                }else {
                    Intent intent = new Intent(CertificateSignTypeActivity.this,CertificateActivateNextTwoActivity.class);
                    intent.putExtra("title","使用PIN码验证");
                    intent.putExtra("type","1");
                    startActivity(intent);
                    FinishActivity.addActivity(CertificateSignTypeActivity.this);
                }

                break;
        }
    }

    /**
     * 是否支持指纹
     */
    private void checkSupportFingerprint() {
        SPUtils.put(CertificateSignTypeActivity.this,"signType","1");
        String signType3 = (String) SPUtils.get(CertificateSignTypeActivity.this, "signType", "");
        if(signTypeFromWhere.equals("1") && signType3.equals("1")){
            finish();
        }else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
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
                        Intent intent = new Intent(CertificateSignTypeActivity.this,FingerManagerActivity.class);
                        startActivity(intent);
                    }

                }else {
                    ToastUtils.showToast(this,"设备不支持指纹登录");
                }
            }
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
            //SPUtils.put(CertificateSignTypeActivity.this,"signType","1");
            getSDKSignData();
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

    private String successCode = "0x00000000";
    String signId;
    private void getSDKSignData() {
        String msspID = (String) SPUtils.get(this, "msspID", "");
        String userName = (String) SPUtils.get(this,"username","");
        String pin = (String) SPUtils.get(this,userName,"");

        signId = (String) SPUtils.get(this,"signId","");
        String sName = pin.split("_")[0];
        String s = pin.split("_")[1];
        Log.e("msspID",msspID);
        Log.e("signId",signId);
        Log.e("pin",s);
        SignetCossApi.getCossApiInstance(AesEncryptUtile.APP_ID, AesEncryptUtile.CA_ADDRESS).cossSignWithPin(this, msspID, signId, s, new CossSignPinCallBack() {
            @Override
            public void onCossSignPin(final CossSignPinResult result) {
                if (result.getErrCode().equalsIgnoreCase(successCode)) {
//                    ToastUtils.showToast(DialogActivity.this,"签名成功!");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String signature = result.getSignature();
                            String cert = result.getCert();

                            clickSignData(signature,cert);
                        }
                    });
                } else {
                    ToastUtils.showToast(CertificateSignTypeActivity.this,"签名失败!");

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

        OkGo.<String>post(UrlRes.HOME_URL+ UrlRes.clickSignUrl)
                .params( "signId",signId)
                .params( "cert",cert)
                .params( "signature",signature)
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {

                        Log.e("App签名成功!",response.body());

                        BaseBean baseBean = JsonUtil.parseJson(response.body(),BaseBean.class);
                        if(baseBean.isSuccess()){
                            ToastUtils.showToast(CertificateSignTypeActivity.this,baseBean.getMsg());
                            Intent intent2 = new Intent();
                            intent2.setAction("addJsResultData");
                            intent2.putExtra("signature",signature);
                            intent2.putExtra("cert",cert);
                            intent2.putExtra("signId",signId);
                            sendBroadcast(intent2);


                        }else {
                            ToastUtils.showToast(CertificateSignTypeActivity.this,baseBean.getMsg());
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


                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }
}
