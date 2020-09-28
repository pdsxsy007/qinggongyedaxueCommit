package io.cordova.zhqy.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.webkit.ValueCallback;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.lzy.okgo.request.base.Request;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import cn.org.bjca.signet.coss.api.SignetCossApi;
import cn.org.bjca.signet.coss.bean.CossClearCertResult;
import cn.org.bjca.signet.coss.bean.CossGetCertResult;
import cn.org.bjca.signet.coss.bean.CossGetUserStateResult;
import cn.org.bjca.signet.coss.component.core.enums.CertType;
import cn.org.bjca.signet.coss.interfaces.CossGetUserStateCallBack;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.bean.AddFaceBean;
import io.cordova.zhqy.bean.BaseBean;
import io.cordova.zhqy.bean.CertInfoBean;
import io.cordova.zhqy.bean.FaceBean;
import io.cordova.zhqy.bean.LocalFaceBean;
import io.cordova.zhqy.bean.LoginBean;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.BaseActivity;
import io.cordova.zhqy.utils.DargeFaceByMefgUtils;
import io.cordova.zhqy.utils.DensityUtil;
import io.cordova.zhqy.utils.FinishActivity;
import io.cordova.zhqy.utils.JsonUtil;
import io.cordova.zhqy.utils.MobileInfoUtils;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.PermissionsUtil;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.ScreenSizeUtils;
import io.cordova.zhqy.utils.T;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.ViewUtils;
import io.cordova.zhqy.web.BaseWebActivity4;
import io.cordova.zhqy.widget.CustomDialog;
import io.cordova.zhqy.widget.MyDialog;
import io.cordova.zhqy.zixing.QRCodeManager;

import static io.cordova.zhqy.UrlRes.HOME2_URL;
import static io.cordova.zhqy.utils.AesEncryptUtile.key;

public class ManagerCertificateOneActivity extends BaseActivity implements View.OnClickListener, PermissionsUtil.IPermissionsCallback {
    @BindView(R.id.rl_jihuo)
    RelativeLayout rl_jihuo;

    @BindView(R.id.rl_delete)
    RelativeLayout rl_delete;

    @BindView(R.id.ll_device)
    LinearLayout ll_device;

    @BindView(R.id.ll_faceService)
    LinearLayout ll_faceService;

    @BindView(R.id.ll_sign_type)
    LinearLayout ll_sign_type;

    @BindView(R.id.ll_finger_sign)
    LinearLayout ll_finger_sign;

    private int flag = 0;
    @Override
    protected int getResourceId() {
        return R.layout.activity_zhengshu_one;
    }

    @Override
    protected void initListener() {
        super.initListener();

        rl_jihuo.setOnClickListener(this);
        rl_delete.setOnClickListener(this);
        ll_device.setOnClickListener(this);
        ll_faceService.setOnClickListener(this);
        ll_sign_type.setOnClickListener(this);
        ll_finger_sign.setOnClickListener(this);
        registerBoradcastReceiver1();
        registerBoradcastReceiver2();
        registerBoradcastReceiver3();
    }

    private void registerBoradcastReceiver1() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("faceCert");
        //注册广播
        registerReceiver(broadcastReceiver, myIntentFilter);
    }

    private void registerBoradcastReceiver2() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("faceCert2");
        //注册广播
        registerReceiver(broadcastReceiver, myIntentFilter);
    }

    private void registerBoradcastReceiver3() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("faceDialogManager");
        //注册广播
        registerReceiver(broadcastReceiver, myIntentFilter);
    }

    private int imageid = 0;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            String FaceActivity = intent.getStringExtra("FaceActivity");
            if(action.equals("faceCert")){

                SPUtils.put(ManagerCertificateOneActivity.this,"isloading2","");

                if(imageid == 0){
                    if(FaceActivity != null){
                        imageid = 1;
                        String bitmap = (String)SPUtils.get(ManagerCertificateOneActivity.this, "bitmap", "");

                        upToServer(bitmap);
                    }else {
                        imageid = 0;
                    }
                }

            }else if(action.equals("faceCert2")){//用户取消了人脸上传

                SPUtils.put(ManagerCertificateOneActivity.this,"isloading2","");

            }else if(action.equals("faceDialogManager")){

                checkFaceResult(FaceActivity);


            }

        }
    };

    private void checkFaceResult(String faceActivity) {
        if(imageid == 0){
            if(faceActivity != null){
                imageid = 1;

                String s = (String)SPUtils.get(ManagerCertificateOneActivity.this, "bitmap", "");
                String imei = (String) SPUtils.get(ManagerCertificateOneActivity.this, "imei", "");
                String username = (String) SPUtils.get(ManagerCertificateOneActivity.this, "phone", "");

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
                                        if(flag == 0){//删除
                                            String msspID = (String) SPUtils.get(ManagerCertificateOneActivity.this, "msspID", "");
                                            CossClearCertResult result = SignetCossApi.getCossApiInstance(AesEncryptUtile.APP_ID, AesEncryptUtile.CA_ADDRESS).cossClearCert(ManagerCertificateOneActivity.this, msspID, CertType.ALL_CERT);
                                            if (result.getErrCode().equalsIgnoreCase(successCode)) {
                                                ToastUtils.showToast(ManagerCertificateOneActivity.this,"删除成功!");
                                                finish();
                                            } else {
                                                ToastUtils.showToast(ManagerCertificateOneActivity.this,result.getErrMsg());

                                            }

                                            Intent intent2 = new Intent();
                                            intent2.setAction("refreshCAResult");
                                            sendBroadcast(intent2);


                                        }else {//激活
                                            Intent intent = new Intent(ManagerCertificateOneActivity.this,CertificateActivateActivity.class);
                                            startActivity(intent);
                                            FinishActivity.addActivity(ManagerCertificateOneActivity.this);
                                        }

                                    }else {
                                        ToastUtils.showToast(ManagerCertificateOneActivity.this,baseBean.getMsg());
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

    String userId;
    public void upToServer(String sresult){
        userId = (String) SPUtils.get(MyApp.getInstance(), "userId", "");
        OkGo.<String>post(UrlRes.HOME2_URL+ UrlRes.addFaceUrl)
                .params( "openId",AesEncryptUtile.openid)
                .params( "memberId",userId)
                .params( "img",sresult )
                .params( "code","")
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {

                        Log.e("上传图片",response.body());
                        AddFaceBean faceBean = JsonUtil.parseJson(response.body(),AddFaceBean.class);
                        boolean success = faceBean.getSuccess();
                        String msg = faceBean.getMsg();
                        ViewUtils.cancelLoadingDialog();
                        if(success == true){
                            ToastUtils.showToast(ManagerCertificateOneActivity.this,msg);
                            imageid = 0;
                        }else {
                            ToastUtils.showToast(ManagerCertificateOneActivity.this,msg);
                            imageid = 0;
                        }

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ViewUtils.cancelLoadingDialog();
                        T.showShort(getApplicationContext(),"找不到服务器了，请稍后再试");
                        imageid = 0;
                    }
                });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(broadcastReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        SPUtils.put(this,"isloading2","");
    }



    private String certContent = "";
    private PermissionsUtil permissionsUtil;
    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.ll_device://查看证书
                String msspID = (String) SPUtils.get(ManagerCertificateOneActivity.this, "msspID", "");
                final CossGetCertResult result = SignetCossApi.getCossApiInstance(AesEncryptUtile.APP_ID, AesEncryptUtile.CA_ADDRESS).cossGetCert(this, msspID, CertType.ALL_CERT);
                HashMap<CertType, String> certMap = result.getCertMap();

                for(HashMap.Entry<CertType, String> entry : certMap.entrySet()){
                    //System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
                    if(entry.getValue() != null){
                        certContent = entry.getValue();
                    }
                }
                if(certContent.equals("")){

                    ToastUtils.showToast(ManagerCertificateOneActivity.this,"当前无证书");
                }else {
                    getCerInfo(certContent);
                }



                break;
            case R.id.rl_jihuo://证书激活

                flag = 1;
                SPUtils.put(ManagerCertificateOneActivity.this,"deleteOrSign","1");
                permissionsUtil=  PermissionsUtil
                        .with(this)
                        .requestCode(12)
                        .isDebug(true)//开启log
                        .permissions(PermissionsUtil.Permission.Camera.CAMERA)
                        .request();

                if(isOpen == 1){
                    SPUtils.put(this,"bitmap","");
                    Intent intent = new Intent(this,FaceDialogManagerActivity.class);
                    startActivityForResult(intent,99);
                    imageid = 0;
                }


                break;
            case R.id.rl_delete://证书删除
                flag = 0;
                SPUtils.put(ManagerCertificateOneActivity.this,"deleteOrSign","0");

                deleteCertificate();

                break;
            case R.id.ll_faceService://人脸信息维护

                showUserDialog();
                break;
            case R.id.ll_sign_type://验证方式

                Intent intent = new Intent(ManagerCertificateOneActivity.this,CertificateSignTypeActivity.class);
                startActivity(intent);
                break;

            case R.id.ll_finger_sign://指纹开启

                Intent intent1 = new Intent(ManagerCertificateOneActivity.this,FingerManagerActivity.class);
                startActivity(intent1);
                break;

        }
    }

    private void showUserDialog() {
        final CustomDialog dialog = new CustomDialog(ManagerCertificateOneActivity.this,R.layout.custom_dialog_cert_sign);
        RelativeLayout rl_sure = dialog.findViewById(R.id.rl_sure);
        RelativeLayout rl_sure1 = dialog.findViewById(R.id.rl_sure1);
        final EditText et_userPwd = dialog.findViewById(R.id.et_userPwd);


        rl_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String trim = et_userPwd.getText().toString().trim();
                if(trim.equals("")){
                    ToastUtils.showToast(ManagerCertificateOneActivity.this,"请输入用户密码");
                    return;
                }
                String userPwd =  (String) SPUtils.get(MyApp.getInstance(),"pwd","");
                if(userPwd.equals(trim)){//用户输入密码正确，进行人脸校验

                    permissionsUtil=  PermissionsUtil
                            .with(ManagerCertificateOneActivity.this)
                            .requestCode(1)
                            .isDebug(true)//开启log
                            .permissions(PermissionsUtil.Permission.Camera.CAMERA)
                            .request();
                }else {
                    ToastUtils.showToast(ManagerCertificateOneActivity.this,"您输入的用户名密码错误");
                }
                dialog.dismiss();


            }
        });

        rl_sure1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();

    }

    private void getCerInfo(String certContent) {
        ViewUtils.createLoadingDialog(this);
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.getCertInfoUrl)
                .tag(this)
                .params("cert",certContent)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("查看证书信息",response.body());
                        ViewUtils.cancelLoadingDialog();
                        CertInfoBean loginBean = JsonUtil.parseJson(response.body(),CertInfoBean.class);
                        boolean success = loginBean.getSuccess();
                        if(success){
                           /* "success": true,
                                    "msg": "SUCCESS",
                                    "obj": {
                                "serialNumber": "2002BAEB54C58FD9F69CF3FCC5DC3BD11F50",
                                        "certDN": "C=CN,CN=邢少印,UID=c4dc5f4bc3b0cff7f9581605c6d1be74b783daf0a86601c1bb24e3df452abd9a",
                                        "certCN": "邢少印",
                                        "userId": "c4dc5f4bc3b0cff7f9581605c6d1be74b783daf0a86601c1bb24e3df452abd9a",
                                        "sigAlgName": "SM3withSM2"
                            },
                            "count": null,
                                    "attributes": null*/
                            CertInfoBean.Obj obj = loginBean.getObj();
                            if(null != obj){
                                String certCN = obj.getCertCN();
                                String certDN = obj.getCertDN();
                                String sigAlgName = obj.getSigAlgName();
                                String serialNumber = obj.getSerialNumber();

                                final CustomDialog dialog = new CustomDialog(ManagerCertificateOneActivity.this,R.layout.custom_dialog_cert_info);
                                RelativeLayout rl_sure = dialog.findViewById(R.id.rl_sure);
                                TextView tv_01 = dialog.findViewById(R.id.tv_01);
                                TextView tv_02 = dialog.findViewById(R.id.tv_02);
                                TextView tv_03 = dialog.findViewById(R.id.tv_03);
                                TextView tv_04 = dialog.findViewById(R.id.tv_04);
                                ImageView iv_close = dialog.findViewById(R.id.iv_close);

                                tv_01.setText(certCN);
                                tv_02.setText(certDN);
                                tv_03.setText(sigAlgName);
                                tv_04.setText(serialNumber);
                                iv_close.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });

                                rl_sure.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View view) {
                                        dialog.dismiss();
                                    }
                                });

                                dialog.show();

                            }
                        }else {
                            ToastUtils.showToast(ManagerCertificateOneActivity.this,loginBean.getMsg());
                        }


                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ViewUtils.cancelLoadingDialog();
                    }
                });

    }

    private String successCode = "0x00000000";

    private void deleteCertificate() {


        String msspID = (String) SPUtils.get(ManagerCertificateOneActivity.this, "msspID", "");
        final CossGetCertResult result = SignetCossApi.getCossApiInstance(AesEncryptUtile.APP_ID, AesEncryptUtile.CA_ADDRESS).cossGetCert(this, msspID, CertType.ALL_CERT);
        HashMap<CertType, String> certMap = result.getCertMap();

        for(HashMap.Entry<CertType, String> entry : certMap.entrySet()){
            //System.out.println("key = " + entry.getKey() + ", value = " + entry.getValue());
            if(entry.getValue() != null){
                certContent = entry.getValue();
            }
        }
        if(certContent.equals("")){//无证书
            ToastUtils.showToast(ManagerCertificateOneActivity.this,"当前无证书可删除！");
        }else {
            final CustomDialog dialog = new CustomDialog(ManagerCertificateOneActivity.this,R.layout.custom_dialog_cert_delete);
            RelativeLayout rl_sure = dialog.findViewById(R.id.rl_sure);
            RelativeLayout rl_sure1 = dialog.findViewById(R.id.rl_sure1);


            rl_sure.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                    permissionsUtil=  PermissionsUtil
                            .with(ManagerCertificateOneActivity.this)
                            .requestCode(12)
                            .isDebug(true)//开启log
                            .permissions(PermissionsUtil.Permission.Camera.CAMERA)
                            .request();

                    if(isOpen == 1){
                        SPUtils.put(ManagerCertificateOneActivity.this,"bitmap","");
                        Intent intent = new Intent(ManagerCertificateOneActivity.this,FaceDialogManagerActivity.class);
                        startActivityForResult(intent,99);
                        imageid = 0;
                    }

                }
            });

            rl_sure1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialog.dismiss();
                }
            });

            dialog.show();
        }



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

    private int isOpen = 0;

    @Override
    public void onPermissionsGranted(int requestCode, String... permission) {
        isOpen = 1;
        if(requestCode == 1){
            startActivity(new Intent(ManagerCertificateOneActivity.this,FaceCertActivity.class));
        }else if(requestCode == 12){

        }

    }

    @Override
    public void onPermissionsDenied(int requestCode, String... permission) {

    }
}
