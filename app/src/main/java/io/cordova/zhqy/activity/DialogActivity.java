package io.cordova.zhqy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Build;
import android.security.keystore.KeyProperties;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.graphics.ColorUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.util.HashMap;

import butterknife.BindView;
import cn.org.bjca.signet.coss.api.SignetCossApi;
import cn.org.bjca.signet.coss.bean.CossGetCertResult;
import cn.org.bjca.signet.coss.bean.CossGetUserListResult;
import cn.org.bjca.signet.coss.bean.CossSignPinResult;
import cn.org.bjca.signet.coss.component.core.enums.CertType;
import cn.org.bjca.signet.coss.interfaces.CossSignPinCallBack;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.bean.BaseBean;
import io.cordova.zhqy.Constants;
import io.cordova.zhqy.fingerprint.FingerprintHelper;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.BaseActivity3;
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


public class DialogActivity extends BaseActivity3 implements View.OnClickListener, PermissionsUtil.IPermissionsCallback, FingerprintHelper.SimpleAuthenticationCallback {

    @BindView(R.id.tv_sign)
    TextView tv_sign;

    @BindView(R.id.tv_download_ca)
    TextView tv_download_ca;

    @BindView(R.id.ll_true)
    LinearLayout ll_true;

    @BindView(R.id.ll_wrong)
    LinearLayout ll_wrong;

    @BindView(R.id.tv_cancel_sign)
    TextView tv_cancel_sign;

    @BindView(R.id.iv_close)
    ImageView iv_close;

    @BindView(R.id.tv_signId)
    TextView tv_signId;

    @BindView(R.id.tv_sign_title)
    TextView tv_sign_title;

    @BindView(R.id.tv_signName)
    TextView tv_signName;

    @BindView(R.id.tv_sign_guoqi)
    TextView tv_sign_guoqi;

    String signId;

    String sendTime;

    private FingerprintHelper helper;

    private boolean isOpenFinger;


    @Override
    protected int getResourceId() {
        return R.layout.activity_show_dialog;
    }

    private LocalBroadcastManager broadcastManager;

    @Override
    protected void initListener() {
        super.initListener();
        setStatusBar(Color.parseColor("#ffffff"));

        String fromWhere = getIntent().getStringExtra("fromWhere");

        sendTime = getIntent().getStringExtra("sendTime");
        String messageId = getIntent().getStringExtra("messageId");
        String title = getIntent().getStringExtra("title");
        if(null != sendTime){
            Log.e("sendTime",sendTime);
        }

        signId = getIntent().getStringExtra("signId");
        if(null != signId){
            tv_signId.setText(signId);
            tv_sign_title.setText(title);
            getCAUserInfoList();
        }

        chooseHttpData(fromWhere,signId,messageId);

        checkCertInfo();
        changeTVBgColor();
        tv_sign.setOnClickListener(this);
        tv_download_ca.setOnClickListener(this);
        tv_cancel_sign.setOnClickListener(this);
        iv_close.setOnClickListener(this);
        registerBoradcastReceiver1();
        registerBoradcastReceiver5();
        //noticeData("123456");
    }

    private void registerBoradcastReceiver5() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("refreshCAResult");
        //注册广播
        registerReceiver(broadcastReceiverCAResult, myIntentFilter);
    }

    private BroadcastReceiver broadcastReceiverCAResult = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if(action.equals("refreshCAResult")){

                checkCertInfo();
            }
        }
    };



    private String nameResult = "";
    private void getCAUserInfoList() {
        String msspID = (String) SPUtils.get(this, "msspID", "");
        CossGetUserListResult result = SignetCossApi.getCossApiInstance(AesEncryptUtile.APP_ID, AesEncryptUtile.CA_ADDRESS).cossGetUserList(this);
        if (result.getErrCode().equalsIgnoreCase(successCode)) {
            HashMap<String, String> userListMap = result.getUserListMap();
            for(HashMap.Entry<String, String> entry : userListMap.entrySet()){
                //System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());

                if(entry.getKey() != null){
                    if(entry.getKey().contains(msspID)){
                        nameResult = entry.getValue();
                    }
                }
            }
        } else {

        }

        tv_signName.setText(nameResult);

    }

    private void registerBoradcastReceiver1() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("faceDialog");
        //注册广播
        registerReceiver(broadcastReceiver, myIntentFilter);
    }

    private int imageid = 0;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String FaceActivity = intent.getStringExtra("FaceActivity");
            if(action.equals("faceDialog")){

                SPUtils.put(DialogActivity.this,"isloading2","");

                if(imageid == 0){
                    String closeFaceFlag = (String) SPUtils.get(DialogActivity.this, "closeFaceFlag", "");
                    if(!closeFaceFlag.equals("1")){
                        checkFaceResult(FaceActivity);
                        SPUtils.put(DialogActivity.this,"closeFaceFlag","");
                    }

                }

            }

        }
    };

    private void checkFaceResult(String faceActivity) {
        if(imageid == 0){
            if(faceActivity != null){
                imageid = 1;

                String s = (String)SPUtils.get(DialogActivity.this, "bitmap", "");
                String imei = (String) SPUtils.get(DialogActivity.this, "imei", "");
                String username = (String) SPUtils.get(DialogActivity.this, "phone", "");

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
                                        //签名方式签名方式(0:人脸识别,1:指纹,2:PIN码,3:面部识别)
                                        SPUtils.put(DialogActivity.this,"caSignType","0");
                                        getSDKSignData();

                                    }else {
                                        ToastUtils.showToast(DialogActivity.this,baseBean.getMsg());
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



    /**
     * 判断是从扫码页面跳转进来还是通知消息跳转进来，区分调用不同的接口
     * @param fromWhere
     * @param signId
     * @param messageId
     */
    private void chooseHttpData(String fromWhere, String signId, String messageId) {
        if(null != fromWhere){
            if(fromWhere.equals("notice")){//从通知跳转进来


                noticeData(signId,messageId);

            }else if(fromWhere.equals("qrScan")){//从扫码跳转进来


                qrScanData(signId);
            }
        }


    }

    private void noticeData(String signId, String messageId) {
        if(null == signId){
            return;
        }
        if(null != messageId){
            OkGo.<String>post(UrlRes.HOME_URL+ UrlRes.waiteSignUrl)
                    .params( "signId",signId)
                    .params( "messageId",messageId)
                    .execute(new StringCallback(){
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.e("通知弹出页面结果",response.body());

                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            ViewUtils.cancelLoadingDialog();

                        }
                    });
        }else {
            Log.e("signId",signId);
            OkGo.<String>post(UrlRes.HOME_URL+ UrlRes.waiteSignUrl)
                    .params( "signId",signId)
                    .execute(new StringCallback(){
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.e("通知弹出页面结果",response.body());

                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            ViewUtils.cancelLoadingDialog();

                        }
                    });
        }

    }

    private void qrScanData(String signId) {
        if(null == signId){
            return;
        }


        OkGo.<String>post(UrlRes.HOME_URL+ UrlRes.scanQrCodeUrl)
                .params( "signId",signId)
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("扫码跳转弹出页面结果",response.body());

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ViewUtils.cancelLoadingDialog();

                    }
                });


    }

    private String certContent = "";
    /**
     * 查看证书
     */
    private void checkCertInfo() {

        String msspID = (String) SPUtils.get(this, "msspID", "");
        if(msspID.equals("")){
            ll_true.setVisibility(View.GONE);
            ll_wrong.setVisibility(View.VISIBLE);
            tv_sign.setVisibility(View.GONE);
            tv_download_ca.setVisibility(View.VISIBLE);

        }else {
            final CossGetCertResult result = SignetCossApi.getCossApiInstance(AesEncryptUtile.APP_ID, AesEncryptUtile.CA_ADDRESS).cossGetCert(this, msspID, CertType.ALL_CERT);
            HashMap<CertType, String> certMap = result.getCertMap();

            for(HashMap.Entry<CertType, String> entry : certMap.entrySet()){
                //System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
                if(entry.getValue() != null){
                    certContent = entry.getValue();
                }
            }

            if(!certContent.equals("")){//证书已存在
                tv_sign.setVisibility(View.VISIBLE);
                tv_download_ca.setVisibility(View.GONE);
                ll_true.setVisibility(View.VISIBLE);
                ll_wrong.setVisibility(View.GONE);
                tv_sign.setVisibility(View.VISIBLE);
                tv_download_ca.setVisibility(View.GONE);


            }else {//证书不存在
                tv_sign.setVisibility(View.GONE);
                tv_download_ca.setVisibility(View.VISIBLE);
                ll_true.setVisibility(View.GONE);
                ll_wrong.setVisibility(View.VISIBLE);
                tv_sign.setVisibility(View.GONE);
                tv_download_ca.setVisibility(View.VISIBLE);
            }

        }




    }


    private void changeTVBgColor() {
        long nowTime = System.currentTimeMillis();
        if(null != sendTime){

            long beforeTime = Long.parseLong(sendTime);

            if((nowTime-beforeTime) > 600*1000){
                tv_sign_guoqi.setBackgroundColor(getResources().getColor(R.color.view2));
                tv_sign_guoqi.setTextColor(Color.parseColor("#000000"));
                tv_sign_guoqi.setAlpha(0.5f);
                tv_sign_guoqi.setVisibility(View.VISIBLE);
                tv_sign.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_silent,R.anim.bottom_out);
    }

    /**
     * Android 6.0 以上设置状态栏颜色
     */
    protected void setStatusBar(@ColorInt int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 设置状态栏底色颜色
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().setStatusBarColor(color);

            // 如果亮色，设置状态栏文字为黑色
            if (isLightColor(color)) {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            } else {
                getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }

    }


    private boolean isLightColor(@ColorInt int color) {
        return ColorUtils.calculateLuminance(color) >= 0.5;
    }

    /**
     * 获取StatusBar颜色，默认白色
     *
     * @return
     */
    protected @ColorInt int getStatusBarColor() {
        return Color.WHITE;
    }


    private PermissionsUtil permissionsUtil;
    private int isOpen = 0;
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_cancel_sign://取消签名

                cancelSignData();
                break;
            case R.id.tv_sign://立即签名
                //验证消息是否过期
                long nowTime = System.currentTimeMillis();
                if(null != sendTime){

                    long beforeTime = Long.parseLong(sendTime);

                    if((nowTime-beforeTime) > 600*1000){
                        ToastUtils.showToast(DialogActivity.this,"当前签名任务已过期");
                        FinishActivity.clearActivity();
                        finish();
                    }else {//默认弹出人脸识别页面
                        //0.弹出人脸验证（1.人脸识别 2.切换到PIN码）
                        //1.调用SDK签名
                        //2.将签名结果提交到服务器
                        SPUtils.put(DialogActivity.this,"deleteOrSign","2");
                        SPUtils.put(DialogActivity.this,"signId",signId);

                        String signType = (String) SPUtils.get(DialogActivity.this, "signType", "");//0人脸 1指纹 2pin码
                        if(signType.equals("0") || signType.equals("")){
                            permissionsUtil=  PermissionsUtil
                                    .with(this)
                                    .requestCode(12)
                                    .isDebug(true)//开启log
                                    .permissions(PermissionsUtil.Permission.Camera.CAMERA)
                                    .request();

                            if(isOpen == 1){
                                SPUtils.put(this,"bitmap","");
                                Intent intent = new Intent(this,FaceDialogActivity.class);
                                startActivityForResult(intent,99);
                                FinishActivity.addActivity(this);
                                imageid = 0;
                            }
                        }else if(signType.equals("1")){
                            /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

                            }*/
                            helper = FingerprintHelper.getInstance();
                            helper.init(getApplicationContext());
                            helper.setCallback(this);
                            if (helper.checkFingerprintAvailable(this) != -1) {

                                //判断指纹功能是否开启
                                //isOpenFinger = SPUtil.getInstance().getBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN);
                                String personName = (String) SPUtils.get(MyApp.getInstance(), "personName", "");
                                isOpenFinger =SPUtil.getInstance().getBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN+"_"+personName, true);
                                //isOpenFinger = SPUtil.getInstance().getBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN);
                                if(isOpenFinger){

                                    openFingerprintLogin();
                                }else {
                                    Intent intent = new Intent(DialogActivity.this,FingerManagerActivity.class);
                                    startActivity(intent);
                                }

                            }else {
                                ToastUtils.showToast(this,"设备不支持指纹登录");
                            }

                        }else {

                            Intent intent = new Intent(DialogActivity.this,CertificateActivateNextTwoActivity.class);
                            intent.putExtra("title","使用PIN码验证");
                            intent.putExtra("type","1");
                            intent.putExtra("isShow","1");
                            startActivity(intent);
                            FinishActivity.addActivity(DialogActivity.this);
                        }

                    }
                }



                break;
            case R.id.tv_download_ca://立即下载
                Intent intent = new Intent(DialogActivity.this,CertificateActivateActivity.class);
                startActivity(intent);
                //FinishActivity.addActivity(this);
                break;
            case R.id.iv_close:
                cancelSignData();
                break;
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
            fingerprintVerifyDialog = new FingerprintVerifyDialog2(this,3);
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
    protected void onPause() {
        super.onPause();
        Log.e("执行onPause","执行onPause");
        if(fingerprintVerifyDialog != null){
            fingerprintVerifyDialog.dismiss();
            helper.stopAuthenticate();
            fingerprintVerifyDialog = null;
        }
        SPUtils.put(this,"isloading2","");
    }

    /**
     * 取消签名
     */
    private void cancelSignData() {

        ViewUtils.createLoadingDialog(this);
        OkGo.<String>post(UrlRes.HOME_URL+ UrlRes.cancelSignUrl)
                .params( "signId",signId)
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {
                        ViewUtils.cancelLoadingDialog();
                        Log.e("取消签名",response.body());

                        BaseBean baseBean = JsonUtil.parseJson(response.body(),BaseBean.class);
                        if(baseBean.isSuccess()){
                            FinishActivity.clearActivity();
                            finish();
                        }else {
                            ToastUtils.showToast(DialogActivity.this,baseBean.getMsg());
                            FinishActivity.clearActivity();
                            finish();
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ViewUtils.cancelLoadingDialog();
                        FinishActivity.clearActivity();
                        finish();
                    }
                });
    }

    private String successCode = "0x00000000";
    private void getSDKSignData() {
        String msspID = (String) SPUtils.get(DialogActivity.this, "msspID", "");
        String userName = (String) SPUtils.get(DialogActivity.this,"username","");
        String pin = (String) SPUtils.get(DialogActivity.this,userName,"");

        String sName = pin.split("_")[0];
        String s = pin.split("_")[1];
        Log.e("msspID",msspID);
        Log.e("signId",signId);
        Log.e("pin",s);
        SignetCossApi.getCossApiInstance(AesEncryptUtile.APP_ID, AesEncryptUtile.CA_ADDRESS).cossSignWithPin(this, msspID, signId, s, new CossSignPinCallBack() {
            @Override
            public void onCossSignPin(final CossSignPinResult result) {
                if (result.getErrCode().equalsIgnoreCase(successCode)) {
                    //ToastUtils.showToast(DialogActivity.this,"签名成功!");
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            String signature = result.getSignature();
                            String cert = result.getCert();

                            clickSignData(signature,cert);
                        }
                    });
                } else {
                    String errMsg = result.getErrMsg();
                    //ToastUtils.showToast(DialogActivity.this,"签名失败!");
                    ToastUtils.showToast(DialogActivity.this,errMsg);

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
                .params( "signType",(String) SPUtils.get(this,"caSignType",""))
                .params( "singer",(String) SPUtils.get(this,"userId",""))
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {

                        Log.e("App签名成功!",response.body());
                        BaseBean baseBean = JsonUtil.parseJson(response.body(),BaseBean.class);
                        if(baseBean.isSuccess()){
                            Intent intent2 = new Intent();
                            intent2.setAction("addJsResultData");
                            intent2.putExtra("signature",signature);
                            intent2.putExtra("cert",cert);
                            intent2.putExtra("signId",signId);
                            sendBroadcast(intent2);


                        }else {
                            ToastUtils.showToast(DialogActivity.this,baseBean.getMsg());
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
        Log.e("dialogActivity","dialogActivity");
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
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
        unregisterReceiver(broadcastReceiverCAResult);
    }

    @Override
    public void onBackPressed() {
        //super.onBackPressed();
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
            //签名方式签名方式(0:人脸识别,1:指纹,2:PIN码,3:面部识别)
            SPUtils.put(DialogActivity.this,"caSignType","1");
            getSDKSignData();
        }
    }

    @Override
    public void onAuthenticationFail() {
        showFingerprintVerifyErrorInfo("指纹不匹配");
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        if (fingerprintVerifyDialog != null && fingerprintVerifyDialog.isShowing()) {
            fingerprintVerifyDialog.dismiss();
        }
        //showTipDialog(errorCode, errString.toString());
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
