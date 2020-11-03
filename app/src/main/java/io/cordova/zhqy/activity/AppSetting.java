package io.cordova.zhqy.activity;


import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.security.keystore.KeyProperties;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationManagerCompat;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;



import butterknife.BindView;
import butterknife.OnClick;

import io.cordova.zhqy.Main2Activity;

import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.Constants;
import io.cordova.zhqy.bean.CurrencyBean;
import io.cordova.zhqy.bean.UpdateBean;
import io.cordova.zhqy.fingerprint.FingerprintHelper;
import io.cordova.zhqy.utils.BaseActivity2;
import io.cordova.zhqy.utils.FinishActivity;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.MyDataCleanManager;
import io.cordova.zhqy.utils.SPUtil;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.T;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.ViewUtils;
import io.cordova.zhqy.utils.fingerUtil.FingerprintUtil;
import io.cordova.zhqy.widget.CustomDialog;
import io.cordova.zhqy.widget.finger.CommonTipDialog;
import io.cordova.zhqy.widget.finger.FingerprintVerifyDialog2;

/**
 * Created by Administrator on 2019/2/18 0018.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class AppSetting extends BaseActivity2 implements FingerprintHelper.SimpleAuthenticationCallback, View.OnClickListener {
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.tv_1)
    TextView tv1;
    @BindView(R.id.msg_notice)
    ImageView msgNotice;

    @BindView(R.id.iv_fingerprint_login_switch)
    ImageView iv_fingerprint_login_switch;

    @BindView(R.id.tv_version)
    TextView tv_version;

    @BindView(R.id.ll_clear_cache)
    LinearLayout ll_clear_cache;

    @BindView(R.id.ll_finger)
    LinearLayout ll_finger;

    @BindView(R.id.ll_shengwu)
    LinearLayout ll_shengwu;
    @BindView(R.id.ll_notice)
    LinearLayout ll_notice;


    @BindView(R.id.tv_clear)
    TextView tv_clear;

    @BindView(R.id.ll_fankui)
    LinearLayout ll_fankui;

    String dataSize = null;
    String localVersionName;
    private int flag = 0;
    private boolean isOpen;
    private FingerprintHelper helper;
    private FingerprintVerifyDialog2 fingerprintVerifyDialog;
    private CommonTipDialog fingerprintVerifyErrorTipDialog;
    private CommonTipDialog closeFingerprintTipDialog;
    private int type = 0;
    @Override
    protected int getResourceId() {
        return R.layout.app_setting_activity;
    }

    @Override
    protected void initView() {
        super.initView();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            helper = FingerprintHelper.getInstance();
            helper.init(getApplicationContext());
            helper.setCallback(this);
            if (helper.checkFingerprintAvailable(this) != -1) {
                //设备支持指纹登录
                iv_fingerprint_login_switch.setEnabled(true);
            }else {
                ToastUtils.showToast(this,"设备不支持指纹登录");
            }
        }else {
            ll_finger.setVisibility(View.GONE);
        }


        tvTitle.setText("设置");
        //startNoticeON();
        setcachesize();
        checkVersion2();

        localVersionName = getLocalVersionName(this);
        tv_version.setText(localVersionName);
        isOpen = SPUtil.getInstance().getBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN);
        setSwitchStatus();

        ll_shengwu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AppSetting.this,ShengWuActivity.class));
            }
        });

        ll_fankui.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AppSetting.this, FankuiWebActivity.class);
                intent.putExtra("appUrl",UrlRes.fankuiUrl);
                startActivity(intent);
            }
        });
    }


    private void checkVersion2() {
        OkGo.<String>get(UrlRes.HOME_URL+UrlRes.getNewVersionInfo)
                .params("system","android")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("ss",response.body());
                        UpdateBean updateBean = JSON.parseObject(response.body(),UpdateBean.class);

                        portalVersionNumber = updateBean.getObj().getPortalVersionNumber();

                    }
                });
    }

    /**
     * 获取本地软件版本号名称
     */
    public  String getLocalVersionName(Context ctx) {
        String localVersion = "";
        try {
            PackageInfo packageInfo = ctx.getApplicationContext()
                    .getPackageManager()
                    .getPackageInfo(ctx.getPackageName(), 0);
            localVersion = packageInfo.versionName;

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }
    private void startNoticeON() {
        NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
        boolean isOpened = manager.areNotificationsEnabled();
        if(!isOpened){
            msgNotice.setImageResource(R.mipmap.switch_close_icon);
            flag = 0;
        }else {
            msgNotice.setImageResource(R.mipmap.switch_open_icon);
            flag = 1;
        }
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (!NotificationsUtils.isNotificationEnabled(getApplicationContext())) {
                msgNotice.setImageResource(R.mipmap.switch_close_icon);
                flag = 0;
            }else {
                msgNotice.setImageResource(R.mipmap.switch_open_icon);
                flag = 1;
            }
        }*/
    }

    private void setcachesize() {
        try {
            dataSize = MyDataCleanManager.getTotalCacheSize(MyApp.getInstance());
            tv1.setText(dataSize);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initListener() {
        super.initListener();
        msgNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(flag == 0){
                    msgNotice.setImageResource(R.mipmap.switch_open_icon);
                }else {
                    msgNotice.setImageResource(R.mipmap.switch_close_icon);
                }
                //NotificationsUtils.requestNotify(AppSetting.this);
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });


        iv_fingerprint_login_switch.setOnClickListener(this);

    }

    @OnClick({R.id.ll_version_information, R.id.ll_clear_cache, R.id.ll_feedback, R.id.ll_about, R.id.ll_sign_out,R.id.ll_device,R.id.ll_notice})
    public void onViewClicked(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.ll_version_information:
                checkVersion();
                break;
            case R.id.ll_notice:
                intent = new Intent(this,NoticeManagerActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_clear_cache:
                MyDataCleanManager.clearAllCache(this);
                try {
                    dataSize = MyDataCleanManager.getTotalCacheSize(MyApp.getInstance());
                    tv1.setText(dataSize);
                    ToastUtils.showToast(this,"已清除");
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.ll_feedback:
                intent = new Intent(this,UpdatePwdInfoActivity.class);
                startActivity(intent);
                FinishActivity.addActivity(this);
                break;
            case R.id.ll_about:
                intent = new Intent(this,VersionMsgActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_sign_out:
                logOut();
                break;
            case R.id.ll_device:
                intent = new Intent(this,DeviceManagerActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_fankui:
                intent = new Intent(this,DeviceManagerActivity.class);
                startActivity(intent);
                break;
        }
    }
    String portalVersionNumber;
    private void checkVersion() {
        ViewUtils.createLoadingDialog(this);
        OkGo.<String>get(UrlRes.HOME_URL+UrlRes.getNewVersionInfo)
                .params("system","android")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("ss",response.body());
                        UpdateBean updateBean = JSON.parseObject(response.body(),UpdateBean.class);

                        portalVersionNumber = updateBean.getObj().getPortalVersionNumber();
                        String portalVersionDownloadAdress = updateBean.getObj().getPortalVersionDownloadAdress();
                        if(localVersionName.equals(portalVersionNumber)){
                            ToastUtils.showToast(AppSetting.this,"当前已是最新版本!");
                        }else {
                           /* Intent intent = new Intent(AppSetting.this,InfoDetailsActivity.class);
                            intent.putExtra("appUrl",portalVersionDownloadAdress);
                            intent.putExtra("title2","下载地址");
                            startActivity(intent);*/

                            String[] split = portalVersionNumber.split("\\.");
                            String[] splitLocal = localVersionName.split("\\.");
                            if(split.length == 3 && splitLocal.length == 3) {
                                int sp0 = Integer.parseInt(split[0]);//服务器版本号
                                int sp1 = Integer.parseInt(split[1]);
                                int sp2 = Integer.parseInt(split[2]);
                                int sl0 = Integer.parseInt(splitLocal[0]);//本地版本号
                                int sl1 = Integer.parseInt(splitLocal[1]);
                                int sl2 = Integer.parseInt(splitLocal[2]);

                                if (sp0 == sl0) {//主版本号相等
                                    //判断第二位
                                    if (sp1 == sl1) {//服务器第二位版本号等于于本地第二位版本号 不执行更新操作

                                        if (sp2 <= sl2) {//服务器第三位版本号等于于本地第三位版本号 不执行更新操作

                                        } else {
                                            Intent intent = new Intent(AppSetting.this,InfoDetailsActivity2.class);
                                            intent.putExtra("appUrl",portalVersionDownloadAdress);
                                            intent.putExtra("title2","下载地址");
                                            startActivity(intent);
                                        }


                                    } else if (sp1 < sl1) {//服务器第二位版本号小于本地第二位版本号 不执行更新操作

                                    } else {//服务器第二位版本号大于本地第二位版本号 不执行更新操作
                                        Intent intent = new Intent(AppSetting.this,InfoDetailsActivity2.class);
                                        intent.putExtra("appUrl",portalVersionDownloadAdress);
                                        intent.putExtra("title2","下载地址");
                                        startActivity(intent);
                                    }

                                } else if (sp0 < sl0) {//服务器主版本号小于本地主版本号 不执行更新操作

                                } else {//服务器主版本号大于本地版本号 执行更新操作
                                    Intent intent = new Intent(AppSetting.this,InfoDetailsActivity2.class);
                                    intent.putExtra("appUrl",portalVersionDownloadAdress);
                                    intent.putExtra("title2","下载地址");
                                    startActivity(intent);
                                }
                            }
                        }
                        ViewUtils.cancelLoadingDialog();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        ViewUtils.cancelLoadingDialog();
                    }
                });
    }



    private void logOut() {

        final CustomDialog dialog = new CustomDialog(AppSetting.this,R.layout.custom_dialog);
        RelativeLayout rl_sure = dialog.findViewById(R.id.rl_sure);
        RelativeLayout rl_sure1 = dialog.findViewById(R.id.rl_sure1);
        rl_sure1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        rl_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                netExit();
            }
        });
        dialog.show();


    }

    private void netExit() {

        OkGo.<String>post(UrlRes.HOME2_URL+UrlRes.Exit_Out)
                .tag(this)
                .execute(new StringCallback() {

                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("netExit", response.body());
                        initRelieve();
                        String update = (String) SPUtils.get(MyApp.getInstance(), "update", "");
                        String home01 = (String) SPUtils.get(MyApp.getInstance(), "home01", "");
                        String home02 = (String) SPUtils.get(MyApp.getInstance(), "home02", "");
                        String home03 = (String) SPUtils.get(MyApp.getInstance(), "home03", "");
                        String home04 = (String) SPUtils.get(MyApp.getInstance(), "home04", "");
                        String home05 = (String) SPUtils.get(MyApp.getInstance(), "home05", "");
                        String home06 = (String) SPUtils.get(MyApp.getInstance(), "home06", "");

                        SPUtils.put(getApplicationContext(),"username","");
                        SPUtils.put(getApplicationContext(),"TGC","");
                        SPUtils.put(getApplicationContext(),"userId","");
                        SPUtils.put(getApplicationContext(),"rolecodes","");
                        SPUtils.put(getApplicationContext(),"count","0");
                        SPUtils.put(getApplicationContext(),"bitmap","");
                        SPUtils.put(getApplicationContext(),"bitmap2","");
                        SPUtils.put(getApplicationContext(),"deleteOrSign","");
                        SPUtils.put(getApplicationContext(),"signId","");
                        if(!update.equals("")){
                            SPUtils.put(MyApp.getInstance(),"update",portalVersionNumber);
                        }
                        if(home01.equals("1")){
                            SPUtils.put(MyApp.getInstance(),"home01","1");
                        }
                        if(home02.equals("1")){
                            SPUtils.put(MyApp.getInstance(),"home02","1");
                        }
                        if(home03.equals("1")){
                            SPUtils.put(MyApp.getInstance(),"home03","1");
                        }
                        if(home04.equals("1")){
                            SPUtils.put(MyApp.getInstance(),"home04","1");
                        }
                        if(home05.equals("1")){
                            SPUtils.put(MyApp.getInstance(),"home05","1");
                        }
                        if(home06.equals("1")){
                            SPUtils.put(MyApp.getInstance(),"home06","1");
                        }
                        T.showShort(MyApp.getInstance(), "退出成功");
                        //closeFingerprintLogin();
                        isOpen = false;
                        //SPUtil.getInstance().putBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN, false);//指纹退出
                        SPUtils.put(MyApp.getInstance(),"closeFaceFlag","");
                        SPUtils.put(MyApp.getInstance(),"closeFaceFlag2","");
                        setSwitchStatus();
                        if(helper != null){
                            helper.closeAuthenticate();
                        }



                        Intent intent = new Intent();
                        intent.putExtra("refreshService","dongtai");
                        intent.setAction("refresh");
                        sendBroadcast(intent);
                        Intent intent1 = new Intent(AppSetting.this,Main2Activity.class);
                        intent1.putExtra("splash","splash");
                        startActivity(intent1);
                        //finish();

                        FinishActivity.clearActivity();

                    }
                });

    }

    CurrencyBean currencyBean;
    private void initRelieve() {
        OkGo.<String>get(UrlRes.HOME_URL + UrlRes.Relieve_Registration_Id)
                .tag("Jpush")
                .params("userId", (String) SPUtils.get(MyApp.getInstance(), "userId", ""))
                .params("portalEquipmentMemberEquipmentId", (String) SPUtils.get(MyApp.getInstance(), "registrationId", ""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("JPush", response.body());
                        currencyBean = JSON.parseObject(response.body(), CurrencyBean.class);
                        if (currencyBean.isSuccess()) {
                            //解除绑定成功
                            Log.e("JPush", currencyBean.getMsg());
                        } else {
                            //解除绑定失败
                            Log.e("JPush", currencyBean.getMsg());
                        }
                    }
                });
    }


    @Override
    protected void onResume() {
        super.onResume();
        startNoticeON();
    }

    private void dealOnOff(boolean isOpen) {
        if (isOpen) {
            type = 0;
            showCloseFingerprintTipDialog();
        } else {
            //type = 1;
            openFingerprintLogin();
        }
    }

    private void showCloseFingerprintTipDialog() {
        if (closeFingerprintTipDialog == null) {
            closeFingerprintTipDialog = new CommonTipDialog(this);
        }
        closeFingerprintTipDialog.setContentText("确定关闭指纹登录?");
        closeFingerprintTipDialog.setSingleButton(false);
        closeFingerprintTipDialog.setOnDialogButtonsClickListener(new CommonTipDialog.OnDialogButtonsClickListener() {
            @Override
            public void onCancelClick(View v) {

            }

            @Override
            public void onConfirmClick(View v) {
                closeFingerprintLogin();
            }
        });
        closeFingerprintTipDialog.show();
    }


    /**
     * @description 开启指纹登录功能
     * @author HaganWu
     * @date 2019/1/29-10:20
     */
    private void openFingerprintLogin() {
        Log.e("hagan", "openFingerprintLogin");

        helper.generateKey();
        if (fingerprintVerifyDialog == null) {
            fingerprintVerifyDialog = new FingerprintVerifyDialog2(this,2);
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

        if(type == 0){
            if (fingerprintVerifyDialog != null && fingerprintVerifyDialog.isShowing()) {
                fingerprintVerifyDialog.dismiss();
                Toast.makeText(this, "指纹登录已开启", Toast.LENGTH_SHORT).show();
                isOpen = true;
                SPUtil.getInstance().putBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN, true);
                setSwitchStatus();
                saveLocalFingerprintInfo();
                type = 1;
            }
        }else {
            if (fingerprintVerifyDialog != null && fingerprintVerifyDialog.isShowing()) {
                fingerprintVerifyDialog.dismiss();
                Toast.makeText(this, "指纹登录已关闭", Toast.LENGTH_SHORT).show();
                isOpen = false;
                SPUtil.getInstance().putBoolean(Constants.SP_HAD_OPEN_FINGERPRINT_LOGIN, false);
                setSwitchStatus();
                helper.closeAuthenticate();
                type = 0;
            }
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
        Log.e("指纹多次登录失败",errorCode+"---------"+errString.toString());
        showTipDialog(errorCode, errString.toString());
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        showFingerprintVerifyErrorInfo(helpString.toString());
    }

    private void setSwitchStatus() {
        iv_fingerprint_login_switch.setImageResource(isOpen ? R.mipmap.switch_open_icon : R.mipmap.switch_close_icon);
    }


    private void closeFingerprintLogin() {
        type = 1;
        openFingerprintLogin();

    }




    private void saveLocalFingerprintInfo() {
        SPUtil.getInstance().putString(Constants.SP_LOCAL_FINGERPRINT_INFO, FingerprintUtil.getFingerprintInfoString(getApplicationContext()));
    }

    private void showFingerprintVerifyErrorInfo(String info) {
        if (fingerprintVerifyDialog != null && fingerprintVerifyDialog.isShowing()) {
            fingerprintVerifyDialog.setContentText(info);
        }
    }

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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_fingerprint_login_switch:
                dealOnOff(isOpen);
                break;
        }
    }


}
