package io.cordova.zhqy.utils;

import android.app.Application;
import android.app.Notification;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.Looper;
import android.support.multidex.MultiDex;
import android.util.Log;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.cache.CacheEntity;
import com.lzy.okgo.cache.CacheMode;
import com.lzy.okgo.cookie.CookieJarImpl;
import com.lzy.okgo.cookie.store.DBCookieStore;
import com.lzy.okgo.cookie.store.SPCookieStore;
import com.lzy.okgo.interceptor.HttpLoggingInterceptor;
import com.umeng.analytics.MobclickAgent;
import com.umeng.commonsdk.UMConfigure;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareConfig;


import java.util.HashMap;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;

import cn.jiguang.verifysdk.api.JVerificationInterface;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import io.cordova.zhqy.AppException;
import io.cordova.zhqy.R;
import io.cordova.zhqy.Constants;
import okhttp3.OkHttpClient;


public class MyApp extends Application {
    private static MyApp instance;

    public static String registrationId;
    public static String isrRunIng;
    public static HashMap<String,String> map = new HashMap<>();
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        isrRunIng = "0";

        //initThirdConfig();
        initCatch();


    }

    private void initCatch() {
        Cockroach.install(new Cockroach.ExceptionHandler() {

            // handlerException内部建议手动try{  你的异常处理逻辑  }catch(Throwable e){ } ，以防handlerException内部再次抛出异常，导致循环调用handlerException

            @Override
            public void handlerException(final Thread thread, final Throwable throwable) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {

                        } catch (Throwable e) {

                        }
                    }
                });
            }
        });
    }

    private void initThirdConfig() {

        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
        //MyIntentService.start(this);

        SPUtil.init(this, Constants.SHARE_PREFERENCE_NAME, Context.MODE_PRIVATE);
        String localVersionName = getLocalVersionName(instance);
        String imei = MobileInfoUtils.getIMEI(instance);

        SPUtils.put(instance,"versionName",localVersionName);
        SPUtils.put(instance,"imei",imei);
        AppException appException = AppException.getInstance();
        appException.init(instance);

        UMShareAPI.get(this);

        UMConfigure.init(this,Constants.umeng_init_data,Constants.umeng_init_name,UMConfigure.DEVICE_TYPE_PHONE,"");
        UMConfigure.setLogEnabled(true);
        UMConfigure.isDebugLog();
        MobclickAgent.setScenarioType(this, MobclickAgent.EScenarioType.E_UM_NORMAL);
        UMShareConfig config = new UMShareConfig();
        config.isNeedAuthOnGetUserInfo(true);
        UMShareAPI.get(instance).setShareConfig(config);
        PlatformConfig.setWeixin(Constants.umeng_init_data_weixin, Constants.umeng_init_data_weixin_value);
        PlatformConfig.setSinaWeibo(Constants.umeng_init_data_weibo, Constants.umeng_init_data_weibo_value, Constants.umeng_init_data_weibo_address);
        PlatformConfig.setQQZone(Constants.umeng_init_data_qq, Constants.umeng_init_data_qq_value);

        //本机号码一键登录
        JVerificationInterface.setDebugMode(true);
        JVerificationInterface.init(instance);
    }


    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(base);
    }



    public static Context getInstance() {
        return instance;
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
            Log.d("TAG", "本软件的版本号。。" + localVersion);

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return localVersion;
    }



}
