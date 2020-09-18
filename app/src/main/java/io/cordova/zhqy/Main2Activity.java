package io.cordova.zhqy;



import android.Manifest;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.Notification;

import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.PersistableBundle;
import android.os.StatFs;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.CookieManager;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.baidu.location.BDAbstractLocationListener;
import com.baidu.location.BDLocation;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;


import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;


import butterknife.BindView;
import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPluginPlatformInterface;
import cn.jpush.android.api.JPushInterface;

import io.cordova.zhqy.activity.DialogActivity;
import io.cordova.zhqy.activity.FaceNewActivity;
import io.cordova.zhqy.activity.InfoDetailsActivity;
import io.cordova.zhqy.activity.InfoDetailsActivity2;
import io.cordova.zhqy.activity.LoginActivity2;

import io.cordova.zhqy.bean.AddFaceBean;
import io.cordova.zhqy.bean.AddTrustBean;
import io.cordova.zhqy.bean.BaseBean;
import io.cordova.zhqy.bean.CountBean;
import io.cordova.zhqy.bean.CurrencyBean;
import io.cordova.zhqy.bean.LoginBean;
import io.cordova.zhqy.bean.NewStudentBean;
import io.cordova.zhqy.bean.UpdateBean;
import io.cordova.zhqy.fragment.home.FindPreFragment;
import io.cordova.zhqy.fragment.home.HomePreFragment;
import io.cordova.zhqy.fragment.home.MyPre2Fragment;

import io.cordova.zhqy.fragment.home.ServicePreFragment;
import io.cordova.zhqy.jpushutil.NotificationsUtils;

import io.cordova.zhqy.utils.ActivityUtils;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.BadgeView;
import io.cordova.zhqy.utils.BaseActivity3;
import io.cordova.zhqy.utils.CookieUtils;
import io.cordova.zhqy.utils.DensityUtil;
import io.cordova.zhqy.utils.JsonUtil;
import io.cordova.zhqy.utils.MobileInfoUtils;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.MyBaseActivity;
import io.cordova.zhqy.utils.PermissionsUtil;
import io.cordova.zhqy.utils.SPUtil;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.ScreenSizeUtils;
import io.cordova.zhqy.utils.StringUtils;
import io.cordova.zhqy.utils.SystemInfoUtils;
import io.cordova.zhqy.utils.T;
import io.cordova.zhqy.utils.TestShowDig;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.ViewUtils;
import io.cordova.zhqy.utils.netState;

import io.cordova.zhqy.web.BaseLoadActivity;
import io.cordova.zhqy.web.BaseWebActivity4;
import io.cordova.zhqy.web.BaseWebCloseActivity;
import io.cordova.zhqy.web.ChangeUpdatePwdWebActivity;
import io.cordova.zhqy.widget.MyDialog;
import io.cordova.zhqy.zixing.OnQRCodeListener;
import io.cordova.zhqy.zixing.QRCodeManager;
import io.reactivex.functions.Consumer;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static io.cordova.zhqy.UrlRes.HOME2_URL;
import static io.cordova.zhqy.UrlRes.caQrCodeVerifyUrl;
import static io.cordova.zhqy.UrlRes.changePwdUrl;
import static io.cordova.zhqy.UrlRes.insertPortalPositionUrl;
import static io.cordova.zhqy.activity.SplashActivity.getLocalVersionName;
import static io.cordova.zhqy.utils.AesEncryptUtile.key;

import static io.cordova.zhqy.utils.MyApp.*;

@TargetApi(Build.VERSION_CODES.KITKAT)
public class Main2Activity extends BaseActivity3 implements PermissionsUtil.IPermissionsCallback{

    @BindView(R.id.frameLayout)
    FrameLayout frameLayout;
    @BindView(R.id.rb_my)
     RadioButton rbMy;

    @BindView(R.id.main_radioGroup)
    RadioGroup mainRadioGroup;




    boolean isFirst = true,isHome = true,isFind = true,isService = true, isMy = true,isFive = true,isLogin = false;
    String insertPortalAccessLog ;
    HomePreFragment homePreFragment;


    FindPreFragment findPreFragment;
    ServicePreFragment servicePreFragment;

    MyPre2Fragment myPre2Fragment;
    CurrencyBean currencyBean;

    private Button button4;
    private BadgeView badge1;
    int perTag;
    private int flag = 0;
    public static boolean isForeground = false;
    String time;
    @BindView(R.id.webView)
    WebView webView;

    String userId;

    public LocationClient mLocationClient;
    private MyLocationListener myListener = new MyLocationListener();
    private PermissionsUtil permissionsUtil;
    @Override
    protected int getResourceId() {
        return R.layout.activity_main2;
    }



    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void initView() {
        super.initView();

        isrRunIng = "1";
        showState();

        button4 = (Button) findViewById(R.id.btn_my);
//        提示消息
        badge1 = new BadgeView(this, button4);
        badge1.hide();
        Log.e("TAG", " registrationId : " + MyApp.registrationId);

        //mainRadioGroup.check(R.id.rb_home_page);
        showFragment(flag);
        insertPortalAccessLog = "1";
        if (isHome){
            netInsertPortal(insertPortalAccessLog);
        }


        mLocationClient = new LocationClient(this);
        mLocationClient.startIndoorMode();
        mLocationClient.registerLocationListener(myListener);


        isOpen();//判断悬浮窗权限

        getUpdateInfo();

        getDownLoadType();

        registerBoradcastReceiver();

        String splash =getIntent().getStringExtra("splash");
        String  st = (String)SPUtils.get(this,"showys","");
        if(st.equals("") || st == null){
            showDialogs2();
        }else {
            if(splash == null){
                permissionsUtil=  PermissionsUtil
                        .with(Main2Activity.this)
                        .requestCode(1)
                        .isDebug(true)//开启log
                        .permissions(PermissionsUtil.Permission.Camera.CAMERA,
                                PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE,
                                PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                        .request();
            }

            String showQuanxian = (String) SPUtils.get(this, "showQuanxian", "");
            if(!showQuanxian.equals("1")){
                permissionsUtil=  PermissionsUtil
                        .with(this)
                        .requestCode(0)
                        .isDebug(true)//开启log
                        .permissions(PermissionsUtil.Permission.Phone.READ_PHONE_STATE,
                                PermissionsUtil.Permission.Location.ACCESS_FINE_LOCATION)
                        .request();
            }
        }

        /*startActivity(new Intent(Main2Activity.this, DialogActivity.class));
        overridePendingTransition(R.anim.bottom_in,R.anim.bottom_silent);*/


        //initChannel();

        //jPluginPlatformInterface = new JPluginPlatformInterface(this);

    }
   /*
   //此方法是华为EMUI服务版本过低的情况下，提示更新服务，更新完成之后可正常接收到厂商通知信息

   JPluginPlatformInterface jPluginPlatformInterface;

    protected void onStart() {
        super.onStart();
        jPluginPlatformInterface.onStart(this);
    }
    protected void onStop() {
        super.onStop();
        jPluginPlatformInterface.onStop(this);
    }*/

    private NotificationCompat.Builder getChannelNotification(String subject, String message, PendingIntent intent) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            return null;
        }
        return new NotificationCompat.Builder(this, "xsy_id").setLargeIcon(BitmapFactory.decodeResource(this.getResources
                (), R.drawable.icon_logo)).setSmallIcon(R.drawable.icon_logo).setContentIntent(intent).setContentTitle(subject).setContentText(message)
                .setAutoCancel(true).setShowWhen(true).setVisibility(Notification.VISIBILITY_PUBLIC).setPriority(NotificationCompat.PRIORITY_HIGH);
    }





    private void initChannel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
           /* nm.deleteNotificationChannel("developer-default");
            nm.deleteNotificationChannel("MyChannelId");
            nm.deleteNotificationChannel("my_channel_id");*/
            if (nm != null){
                NotificationChannelGroup notificationChannelGroup = new NotificationChannelGroup("MyGroupId", "消息订阅");
                nm.createNotificationChannelGroup(notificationChannelGroup);
                NotificationChannel notificationChannel = new NotificationChannel("message_id", "消息订阅", NotificationManager.IMPORTANCE_HIGH);
                notificationChannel.setGroup("MyGroupId");
                notificationChannel.enableLights(true);
                notificationChannel.enableVibration(true);
//                notificationChannel.setSound("android.resource://包名/raw/铃声文件", null);   //设置自定义铃声
                nm.createNotificationChannel(notificationChannel);

                NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "message_id");
                builder.setSmallIcon(R.drawable.icon_logo);
                nm.notify(1,builder.build());

            }
        }
    }



    private MyDialog m_Dialog3;

    private void showDialogs2() {

        m_Dialog3 = new MyDialog(this, R.style.dialogdialog);

        View view = View.inflate(this, R.layout.alert_ys, null);

        int width = ScreenSizeUtils.getWidth(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
        m_Dialog3.setContentView(view, layoutParams);
        final TextView agreetv = view.findViewById(R.id.agree_tv);
        CheckBox argeeCheck = view.findViewById(R.id.agree_check);
        TextView gongneng = view.findViewById(R.id.gongneng);


        argeeCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked()){
                    agreetv.setTextColor(Color.parseColor("#1d4481"));
                    agreetv.setClickable(true);
                    agreetv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            m_Dialog3.dismiss();
                            SPUtils.put(Main2Activity.this,"showys","1");
                            String splash =getIntent().getStringExtra("splash");
                            if(splash == null){
                                permissionsUtil=  PermissionsUtil
                                        .with(Main2Activity.this)
                                        .requestCode(1)
                                        .isDebug(true)//开启log
                                        .permissions(PermissionsUtil.Permission.Camera.CAMERA,
                                                PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE,
                                                PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                                        .request();
                            }

                            String showQuanxian = (String) SPUtils.get(Main2Activity.this, "showQuanxian", "");
                            if(!showQuanxian.equals("1")){
                                permissionsUtil=  PermissionsUtil
                                        .with(Main2Activity.this)
                                        .requestCode(0)
                                        .isDebug(true)//开启log
                                        .permissions(PermissionsUtil.Permission.Phone.READ_PHONE_STATE,
                                                PermissionsUtil.Permission.Location.ACCESS_FINE_LOCATION)
                                        .request();
                            }
                        }
                    });
                }else{
                    agreetv.setTextColor(Color.parseColor("#A0A0A0"));
                    agreetv.setClickable(false);
                }
            }
        });
        gongneng.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              /*  Intent intent = new Intent(Main2Activity.this,BaseWebActivity4.class);
                intent.putExtra("appUrl","http://platform.gilight.cn/authentication/authentication/views/appNative/privacyProtocol.html" );
                startActivity(intent);*/
                String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                if(isOpen.equals("") || isOpen.equals("1")){
                    Intent intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                    intent.putExtra("appUrl",UrlRes.xieyiUrl);
                    startActivity(intent);
                }else {
                    Intent intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                    intent.putExtra("appUrl",UrlRes.xieyiUrl);
                    startActivity(intent);
                }
            }
        });




        m_Dialog3.setCanceledOnTouchOutside(false);
        m_Dialog3.show();

        m_Dialog3.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode == KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount() == 0) {
                    return true;
                } else {
                    return false;
                }
            }
        });


    }


    public class MyLocationListener extends BDAbstractLocationListener {
        @Override
        public void onReceiveLocation(BDLocation location){

            double latitude = location.getLatitude();    //获取纬度信息
            double longitude = location.getLongitude();    //获取经度信息
            float radius = location.getRadius();    //获取定位精度，默认值为0.0f

            String coorType = location.getCoorType();
            //获取经纬度坐标类型，以LocationClientOption中设置过的坐标类型为准

            int errorCode = location.getLocType();
            boolean b = location.hasAltitude();
            double altitude = location.getAltitude();
            float speed = location.getSpeed();
            float direction = location.getDirection();
            int locationWhere = location.getLocationWhere();

            //获取定位类型、定位错误返回码，具体信息可参照类参考中BDLocation类中的说明
            Log.e("errorCode",errorCode+"");
            Log.e("latitude",latitude+"");
            Log.e("longitude",longitude+"");
            Log.e("altitude主页",altitude+"");
            Log.e("speed",speed+"");
            Log.e("direction",direction+"");
            Log.e("locationWhere",locationWhere+"");

            com.baidu.location.Address address = location.getAddress();
            String addressLine = address.address;
            if(addressLine != null){
                SPUtils.put(Main2Activity.this,"addressLine",addressLine);
            }

            String country = address.country;
            String countryCode = address.countryCode;
            String province = address.province;
            String city = address.city;
            String cityCode = address.cityCode;
            String district = address.district;
            String town = address.town;
            String adcode = address.adcode;
            String street = address.street;
            String streetNumber = address.streetNumber;
            SPUtils.put(Main2Activity.this,"latitude",latitude+"");
            SPUtils.put(Main2Activity.this,"longitude",longitude+"");
            SPUtils.put(Main2Activity.this,"altitude",altitude+"");
            SPUtils.put(Main2Activity.this,"speed",speed+"");
            SPUtils.put(Main2Activity.this,"direction",direction+"");
            SPUtils.put(Main2Activity.this,"locationWhere",locationWhere+"");
            if(addressLine != null){
                SPUtils.put(Main2Activity.this,"addressLine",addressLine);
            }
            if(country != null){
                SPUtils.put(Main2Activity.this,"country",country);
            }

            if(countryCode != null){
                SPUtils.put(Main2Activity.this,"countryCode",countryCode);
            }

            if(province != null){
                SPUtils.put(Main2Activity.this,"province",province);
            }

            if(city != null){
                SPUtils.put(Main2Activity.this,"city",city);
            }

            if(cityCode != null){
                SPUtils.put(Main2Activity.this,"cityCode",cityCode);
            }

            if(district != null){
                SPUtils.put(Main2Activity.this,"district",district);
            }

            if(adcode != null){
                SPUtils.put(Main2Activity.this,"adcode",adcode);
            }

            if(street != null){
                SPUtils.put(Main2Activity.this,"street",street);
            }

            if(streetNumber != null){
                SPUtils.put(Main2Activity.this,"streetNumber",streetNumber);
            }
            if(town != null){
                SPUtils.put(Main2Activity.this,"town",town);
            }
            OkGo.<String>post(UrlRes.HOME_URL+insertPortalPositionUrl)
                    .params("memberId", (String) SPUtils.get(MyApp.getInstance(), "userId", ""))
                    .params("positionLongitude", longitude)
                    .params("positionLatitude", latitude)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.e("五分钟阅读",response.body());
                           /* FaceBean2 faceBean2 = JsonUtil.parseJson(response.toString(),FaceBean2.class);

                            boolean success = faceBean2.getSuccess();
                            if(success == true){

                            }*/
                        }

                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            Log.e("s",response.toString());
                        }
                    });
        }
    }


    private void showNewSuDialog() {
        userId = (String) SPUtils.get(MyApp.getInstance(), "userId", "");

        if(!userId.equals("")){
            //是否弹窗人脸识别
            checkIsNewStudent(userId);

            //是否强制修改密码
            checkIsUpdatepwd(userId);


        }
    }


    private void checkIsUpdatepwd(String userId) {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.newStudentUpdatePwdStateUrl)
                .params("userName",userId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("checkIsUpdatepwd",response.body());

                        NewStudentBean newStudentBean = JsonUtil.parseJson(response.body(),NewStudentBean.class);
                        if(newStudentBean != null){
                            boolean success = newStudentBean.getSuccess();
                            if(success){
                                NewStudentBean.Obj obj = newStudentBean.getObj();
                                if(obj != null){
                                    String type = obj.getType();
                                    if(type.equals("0")){//弹出修改密码
                                        Intent intent = new Intent(Main2Activity.this, ChangeUpdatePwdWebActivity.class);
                                        intent.putExtra("appUrl",HOME2_URL+changePwdUrl);
                                        startActivity(intent);
                                    }


                                }
                            }else {

                            }
                        }

                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Log.e("onError",response.body());
                    }
                });
    }

    private void checkIsNewStudent(String userId) {
        Log.e("弹出了"," showNewSuDialog();");
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.jugdeFaceUrl)
                .params("userName",userId)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("checkIsNewStudent",response.body());

                        NewStudentBean newStudentBean = JsonUtil.parseJson(response.body(),NewStudentBean.class);
                        if(newStudentBean != null){
                            boolean success = newStudentBean.getSuccess();
                            if(success){
                                NewStudentBean.Obj obj = newStudentBean.getObj();
                                if(obj != null){
                                    String type = obj.getType();
                                    if(type.equals("0")){//弹出人脸识别框
                                        permissionsUtil=  PermissionsUtil
                                                .with(Main2Activity.this)
                                                .requestCode(4)
                                                .isDebug(true)//开启log
                                                .permissions(PermissionsUtil.Permission.Camera.CAMERA)
                                                .request();
                                        //cameraTask();

                                    }
                                }
                            }
                        }


                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Log.e("onError",response.body());
                    }
                });
    }
    private static final int RC_CAMERA_PERM = 123;
    @AfterPermissionGranted(RC_CAMERA_PERM)
    public void cameraTask() {
        if (EasyPermissions.hasPermissions(this, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            logOut();
        } else {//没有相应权限，获取相机权限
            EasyPermissions.requestPermissions(this, "获取照相机权限",
                    RC_CAMERA_PERM, Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void getDownLoadType() {
        OkGo.<String>get(UrlRes.HOME_URL + UrlRes.getDownLoadTypeUrl)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("下载类型",response.body());
                        SPUtils.put(Main2Activity.this,"downLoadType",response.body());

                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }

    private void addMobieInfo() {

        //获取运行内存的信息
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo info = new ActivityManager.MemoryInfo();
        manager.getMemoryInfo(info);
        long totalMem = info.totalMem;

        final StatFs statFs = new StatFs(Environment.getDataDirectory().getPath());
        long totalCounts = statFs.getBlockCountLong();//总共的block数
        long size = statFs.getBlockSizeLong(); //每格所占的大小，一般是4KB==
        long totalROMSize = totalCounts *size; //内部存储总大小


        String imei = MobileInfoUtils.getIMEI(this);

        String s = totalMem / 1024/1024/1024 + "GB";
        String s3 = totalROMSize / 1024/1024/1024 + "GB";
        String s4 = SystemInfoUtils.getWindowWidth(this) + "X" + SystemInfoUtils.getWindowHeigh(this);

        String userId = (String) SPUtils.get(getInstance(), "userId", "");
        String deviceModel = SystemInfoUtils.getDeviceModel();
        String displayVersion = SystemInfoUtils.getDISPLAYVersion();
        String deviceAndroidVersion = SystemInfoUtils.getDeviceAndroidVersion();
        String deviceCpu = SystemInfoUtils.getDeviceCpu();
        String s5 = SystemInfoUtils.getWindowWidth(this) + "X" + SystemInfoUtils.getWindowHeigh(this);
        String localVersionName = getLocalVersionName(this);
        Log.e("userId",userId);
        Log.e("mobilePhoneModel",deviceModel);
        Log.e("mobileVersion",displayVersion);
        Log.e("mobileSystemVersion",deviceAndroidVersion);
        Log.e("mobileSystemVersion",deviceCpu);
        Log.e("mobileEquipmentId",imei);
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.addMobileInfoUrl)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("mobilePhoneModel", SystemInfoUtils.getDeviceModel())
                .params("mobileVersion", SystemInfoUtils.getDISPLAYVersion())
                .params("mobileSystemVersion", SystemInfoUtils.getDeviceAndroidVersion())
                .params("mobileCpu", SystemInfoUtils.getDeviceCpu())
                .params("mobileMemory", s)
                .params("mobileStorageSpace", s3)
                .params("mobileScreen", SystemInfoUtils.getWindowWidth(this)+"X"+SystemInfoUtils.getWindowHeigh(this))
                .params("mobileEquipmentId", imei)
                .params("mobileSystemCategory", 1)
                .params("mobileAppVersion", getLocalVersionName(this))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("设备信息",response.body());


                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }

    private AlertDialog mAlertDialogNotice;
    private Window mDialogWindow;
    private void showState() {
        isLogin = !StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username",""));
        if (isLogin){

            if (StringUtils.isEmpty(MyApp.registrationId)){
                MyApp.registrationId =  JPushInterface.getRegistrationID(this);
            }

            SPUtils.put(this,"registrationId", MyApp.registrationId);
            bindJpush();
            /*悬浮窗权限检测*/
           /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    if (!Settings.canDrawOverlays(this)) {
                        //若未授权则请求权限

                        if(perTag == 0){
                            showDialog();
                        }

                    }else if (!NotificationsUtils.isNotificationEnabled(getApplicationContext())){
                        perTag = 1;
                        showDialog();
                    }


                }
            }*/
            //long time = new Date().getTime();

            //showDialogs();

            NotificationManagerCompat manager = NotificationManagerCompat.from(getApplicationContext());
            boolean isOpened = manager.areNotificationsEnabled();
            //ToastUtils.showToast(this,isOpened+"");
            if(!isOpened){
                String noticeTime = (String) SPUtils.get(Main2Activity.this,"noticeTime","");
                if(noticeTime.equals("")){
                    showDialogs();
                }else {
                    long lastNoticeTime = Long.parseLong(noticeTime);
                    long time = new Date().getTime();
                    Long s = (time - lastNoticeTime) / (1000 * 60*60*24);
                    if(s >= 7){
                        showDialogs();
                    }
                }



            }
        }
    }


    private void remind() { //BadgeView的具体使用
        String count = (String) SPUtils.get(this, "count", "");
        if(count.equals("")){
            count = "0";
        }
        // 创建一个BadgeView对象，view为你需要显示提醒的控件
        //badge1.setText(countBean2.getCount()+Integer.parseInt(countBean1.getObj())+countBean3.getCount()+""); // 需要显示的提醒类容
        int i = countBean2.getCount() + Integer.parseInt(countBean1.getObj()) + countBean3.getCount()+countBeanEmail.getCount();
        int i1 = Integer.parseInt(count);
        if(i1>99){
            badge1.setText("99+"); // 需要显示的提醒类容
        }else {
            badge1.setText(count); // 需要显示的提醒类容
        }



        if(i >9){
            // 需要显示的提醒类容
            badge1.setBadgePosition(BadgeView.POSITION_TOP_LEFT1);// 显示的位置.右上角,BadgeView.POSITION_BOTTOM_LEFT,下左，还有其他几个属性
        }else {
            badge1.setBadgePosition(BadgeView.POSITION_TOP_LEFT);// 显示的位置.右上角,BadgeView.POSITION_BOTTOM_LEFT,下左，还有其他几个属性
        }

        badge1.setTextColor(Color.WHITE); // 文本颜色
        badge1.setBadgeBackgroundColor(Color.RED); // 提醒信息的背景颜色，自己设置
        badge1.setTextSize(10); // 文本大小
        badge1.show();// 只有显示

    }




    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction("refresh3");
        //注册广播
        registerReceiver(broadcastReceiver, myIntentFilter);
    }

    private int imageid = 0;
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (StringUtils.isEmpty(MyApp.registrationId)){
                MyApp.registrationId =  JPushInterface.getRegistrationID(Main2Activity.this);
            }

            SPUtils.put(Main2Activity.this,"registrationId", MyApp.registrationId);
            bindJpush();

            String action = intent.getAction();

            if(action.equals("refresh3")){
                String faceNewActivity = intent.getStringExtra("FaceNewActivity");
                Log.e("imageid",imageid+"");
                if(imageid == 0){
                    if(faceNewActivity != null){
                        imageid = 1;
                        Log.e("收到图片通知",faceNewActivity);
                        String bitmap  = (String) SPUtils.get(Main2Activity.this, "bitmapnewsd", "");;
                        upToServer(bitmap);
                    }else {
                        imageid = 0;
                        ViewUtils.cancelLoadingDialog();

                        getUpdateInfo();
                    }
                }


            }
        }
    };

    public void upToServer(String sresult){
        OkGo.<String>post(HOME2_URL+ UrlRes.addFaceUrl)
                .params( "openId",AesEncryptUtile.openid)
                .params( "memberId",(String) SPUtils.get(MyApp.getInstance(), "userId", ""))
                .params( "img",sresult )
                .params( "code","newstudentfacecode" )
                .execute(new StringCallback(){
                    @Override
                    public void onSuccess(Response<String> response) {
                        ViewUtils.cancelLoadingDialog();
                        Log.e("上传图片zhuye",response.body());
                        AddFaceBean faceBean = JsonUtil.parseJson(response.body(),AddFaceBean.class);
                        boolean success = faceBean.getSuccess();
                        String msg = faceBean.getMsg();
                        ViewUtils.cancelLoadingDialog();
                        if(success == true){
                            Intent intent = null;
                            String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                            if(isOpen.equals("") || isOpen.equals("1")){
                                intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                            }else {
                                intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                            }
                            intent.putExtra("appUrl",UrlRes.newStudentDbUrl);
                            startActivity(intent);

                            m_Dialog2.dismiss();
                            addDevice();
                        }else {
                            ToastUtils.showToast(Main2Activity.this,msg);
                            imageid = 0;
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        imageid = 0;
                        ViewUtils.cancelLoadingDialog();
                        T.showShort(getApplicationContext(),"找不到服务器了，请稍后再试");
                    }
                });
    }

    /**
     * 添加信任设备
     */
    private void addDevice() {

        String DEVICE_ID = MobileInfoUtils.getIMEI(this);
        Log.e("获取到的数据为",DEVICE_ID);

        OkGo.<String>post(UrlRes.HOME_URL+UrlRes.addTrustDevice)
                .params("portalTrustDeviceNumber",DEVICE_ID)
                .params("portalTrustDeviceType","android")
                .params("portalTrustDeviceName", android.os.Build.DEVICE )
                .params("portalTrustDeviceInfo",android.os.Build.MANUFACTURER + "  "+ SystemInfoUtils.getDeviceModel())
                .params("portalTrustDeviceMaster",0)
                .params("portalTrustDeviceDelete",0)
                .params("userName",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 Log.e("添加信任",response.body());
                                 AddTrustBean addTrustBean = JSON.parseObject(response.body(),AddTrustBean.class);
                                 if(addTrustBean.isSuccess()){

                                 }
                             }

                             @Override
                             public void onError(Response<String> response) {
                                 super.onError(response);

                             }
                         }
                );


    }
    private long mLastClickTime = 0;
    public static final long TIME_INTERVAL = 500L;
    @Override
    protected void initListener() {
        super.initListener();
        mainRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
                switch (checkedId) {

                    case R.id.rb_home_page:
                        flag = 0;
                        showFragment(0);

                        insertPortalAccessLog = "5";
                        if (isFive){
                            netInsertPortal(insertPortalAccessLog);
                        }
                        break;
                    case R.id.rb_recommend:
                        flag = 1;
                        showFragment(1);
                        insertPortalAccessLog = "2";
                        if (isFind){
                            netInsertPortal(insertPortalAccessLog);
                        }
                        break;
                    case R.id.rb_shopping:
                        flag = 2;
                        showFragment(2);
                        insertPortalAccessLog = "3";
                        if (isService){
                            netInsertPortal(insertPortalAccessLog);
                        }
                        break;
                    case R.id.rb_my:
                        //if (isLogin && netState.isConnect(getApplicationContext())){
                        if (isLogin ){
                            showFragment(3);
                            flag = 3;
                            insertPortalAccessLog = "4";
                            if (isMy){
                                netInsertPortal(insertPortalAccessLog);
                            }
                        }else{

                            switch (flag){
                                case 0:
                                    mainRadioGroup.check(R.id.rb_home_page);
                                    break;
                                case 1:
                                    mainRadioGroup.check(R.id.rb_recommend);
                                    break;
                                case 2:
                                    mainRadioGroup.check(R.id.rb_shopping);
                                    break;
                                case 3:
                                    mainRadioGroup.check(R.id.rb_my);
                                    break;
                            }
                            showFragment(flag);
                            long nowTime = System.currentTimeMillis();
                            if (nowTime - mLastClickTime > TIME_INTERVAL) {
                                mLastClickTime = nowTime;
                                Intent intent = new Intent(getApplicationContext(),LoginActivity2.class);
                                startActivity(intent);
                            } else {
                                //Toast.makeText(MainActivity.this, "不要重复点击", Toast.LENGTH_SHORT).show();
                            }


                        }
                        break;
                }
            }
        });
    }



    public void showFragment(int i) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        hideFragments(ft);
        Bundle mBundle = null;
        switch (i) {
            case 0:
                if (homePreFragment != null)
                    ft.show(homePreFragment);
                else {
                    homePreFragment = new HomePreFragment();
                    ft.add(R.id.frameLayout, homePreFragment);
                }
                break;
            case 1:
                // 如果fragment1已经存在则将其显示出来
                if (findPreFragment != null)
                    ft.show(findPreFragment);
                else {
                    findPreFragment = new FindPreFragment();
                    ft.add(R.id.frameLayout, findPreFragment);
                }

                break;
            case 2:
                // 如果fragment1已经存在则将其显示出来
                if (servicePreFragment != null)
                    ft.show(servicePreFragment);
                    // 否则添加fragment1，注意添加后是会显示出来的，replace方法也是先remove后add
                else {
                    servicePreFragment = new ServicePreFragment();
                    ft.add(R.id.frameLayout, servicePreFragment);
                }

                break;
            case 3:
                if (!netState.isConnect(Main2Activity.this) ){
                    if(myPre2Fragment != null){
                        ft.show(myPre2Fragment);
                    }else {
                        myPre2Fragment = new MyPre2Fragment();
                        ft.add(R.id.frameLayout, myPre2Fragment);
                    }
                    ToastUtils.showToast(Main2Activity.this,"网络连接异常!");
                }else {
                    if(myPre2Fragment != null){
                        ft.show(myPre2Fragment);
                    }else {
                        myPre2Fragment = new MyPre2Fragment();
                        ft.add(R.id.frameLayout, myPre2Fragment);
                    }

                }

                break;
        }
        ft.commit();
    }

    private void hideFragments(FragmentTransaction ft2) {
        if (homePreFragment != null)
            ft2.hide(homePreFragment);
        if (servicePreFragment != null)
            ft2.hide(servicePreFragment);
        if (findPreFragment != null)
            ft2.hide(findPreFragment);
       if (myPre2Fragment != null)
           ft2.hide(myPre2Fragment);
    }

    /**
     * 第一次进入统计四大模块是否点击
     *
     * @param insertPortalAccessLog*/
    BaseBean baseBean ;
    private void netInsertPortal(final String insertPortalAccessLog) {
        String imei = MobileInfoUtils.getIMEI(this);
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Four_Modules)
                .params("portalAccessLogMemberId",(String) SPUtils.get(getInstance(),"userId",""))
                .params("portalAccessLogEquipmentId",imei)//设备ID
                .params("portalAccessLogTarget", insertPortalAccessLog)//访问目标
                .params("portalAccessLogVersionNumber", (String) SPUtils.get(getApplicationContext(),"versionName", ""))//版本号
                .params("portalAccessLogOperatingSystem", "ANDROID")//版本号
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("sdsaas",response.body());
                        if (null != response.body()){
                            baseBean = JSON.parseObject(response.body(),BaseBean.class);
                            if (baseBean.isSuccess()){
                                if (insertPortalAccessLog.equals("1")){
                                    isHome = false;
                                }else  if (insertPortalAccessLog.equals("2")){
                                    isFind = false;
                                }else  if (insertPortalAccessLog.equals("3")){
                                    isService = false;
                                }else  if (insertPortalAccessLog.equals("4")){
                                    isMy = false;
                                }else  if (insertPortalAccessLog.equals("5")){
                                    isFive = false;
                                }
                            }
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }




    /**
     * 根据UserId 绑定Jpush*/
    private void bindJpush() {
        OkGo.<String>get(UrlRes.HOME_URL+UrlRes.Registration_Id)
                .tag("Jpush")
                .params("equipType","android")
                .params("userId",(String) SPUtils.get(getInstance(),"userId",""))
                .params("portalEquipmentMemberEquipmentId",(String)SPUtils.get(Main2Activity.this,"registrationId",""))
                .params("portalEquipmentMemberGroup", "")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("JPush",response.body());
                        currencyBean = JSON.parseObject(response.body(),CurrencyBean.class);
                        if (currencyBean.isSuccess()){
                            //绑定成功
                            isFirst = false;
                            Log.e("JPush","绑定成功");
                        }else {
                            //绑定失败
                            isFirst = true;
                            Log.e("JPush","绑定失败");
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        //绑定失败
                        isFirst = true;
                        Log.e("JPush","绑定失败");
                    }
                });
    }


    /** 开启通知权限 */
    private AlertDialog mAlertDialog;
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private void setPer() {
        if ( !NotificationsUtils.isNotificationEnabled(getApplicationContext())){
            showDialogs();
        }
    }
    /**推送设置弹窗*/
    private void showDialogs() {
/*
        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(this)
                    .setMessage("您需要去开启推送权限么?\n")
                    .setNegativeButton("不需要", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mAlertDialog != null) {
                                mAlertDialog.dismiss();
                            }
                        }
                    })//
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            if (mAlertDialog != null) {
                                mAlertDialog.dismiss();
                            }
                            Intent intent = new Intent();
                            intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                            Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                            intent.setData(uri);
                            startActivity(intent);
                        }
                    }).create();
        }
        mAlertDialog.show();
*/


        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.MyDialog);
        mAlertDialogNotice = builder.create();
        mAlertDialogNotice.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                mAlertDialogNotice.dismiss();
            }
        });
        mAlertDialogNotice.show();
        mAlertDialogNotice.setCanceledOnTouchOutside(false);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_notice, null);
        RelativeLayout rl_close = dialogView.findViewById(R.id.rl_close);
        Button btn_open = dialogView.findViewById(R.id.btn_open);
        rl_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlertDialogNotice.dismiss();
                long time = new Date().getTime();
                SPUtils.put(Main2Activity.this,"noticeTime",time+"");
            }
        });
        btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAlertDialogNotice.dismiss();
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                Uri uri = Uri.fromParts("package", getApplicationContext().getPackageName(), null);
                intent.setData(uri);
                startActivity(intent);
            }
        });
        mAlertDialogNotice.setContentView(dialogView);
        mDialogWindow = mAlertDialogNotice.getWindow();
        //计算屏幕宽高
        Integer[] widthAndHeight =getWidthAndHeight(this.getWindow());
        if (mDialogWindow != null) {
            mDialogWindow.setGravity(Gravity.CENTER);
            mDialogWindow.setWindowAnimations(R.style.PopupWindow);
            mDialogWindow.setLayout((int)(widthAndHeight[0] / 1.3), (int)(widthAndHeight[1] / 1.5));
            mDialogWindow.setBackgroundDrawableResource(R.drawable.shape_button_touming);
        }
    }

    public static Integer[] getWidthAndHeight(Window window) {
        if (window == null) {
            return null;
        }
        Integer[] integer = new Integer[2];
        DisplayMetrics dm = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            window.getWindowManager().getDefaultDisplay().getRealMetrics(dm);
        } else {
            window.getWindowManager().getDefaultDisplay().getMetrics(dm);
        }
        integer[0] = dm.widthPixels;
        integer[1] = dm.heightPixels;
        return integer;
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

    }


    long waitTime = 2000;
    long touchTime = 0;
    public boolean onKeyDown(int keyCode, KeyEvent event) {



        if (event.getAction() == KeyEvent.ACTION_DOWN && KeyEvent.KEYCODE_BACK == keyCode) {

            long currentTime = System.currentTimeMillis();
            if ((currentTime - touchTime) >= waitTime) {
                T.showShort(Main2Activity.this, "再按一次退出");
                touchTime = currentTime;
            } else {
                ActivityUtils.getActivityManager().finishAllActivity();
            }

            return true;

        }
        return super.onKeyDown(keyCode, event);
    }
    String portalVersionDownloadAdress;
    private void getUpdateInfo() {

        OkGo.<String>get(UrlRes.HOME_URL+UrlRes.getNewVersionInfo)
                .params("system","android")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("更新",response.body());

                        UpdateBean updateBean = JSON.parseObject(response.body(),UpdateBean.class);
                        String portalVersionNumber = updateBean.getObj().getPortalVersionNumber();
                        int portalVersionUpdate = updateBean.getObj().getPortalVersionUpdate();

                        portalVersionDownloadAdress = updateBean.getObj().getPortalVersionDownloadAdress();
                        logShow(portalVersionUpdate,portalVersionDownloadAdress,portalVersionNumber);

                    }
                });

    }
    String localVersionName;
    private MyDialog m_Dialog;
    private int isUpdate = 0;
    private void logShow(int portalVersionUpdate, final String portalVersionDownloadAdress, final String portalVersionNumber) {
        localVersionName = getLocalVersionName(this);
        m_Dialog = new MyDialog(this, R.style.dialogdialog);
        Window window = m_Dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_update, null);
        RelativeLayout rl_down = view.findViewById(R.id.rl_down);
        RelativeLayout rl_down2 = view.findViewById(R.id.rl_down2);
        TextView tv_version = view.findViewById(R.id.tv_version);
        RelativeLayout rl_cancel = view.findViewById(R.id.rl_cancel);

        int width = ScreenSizeUtils.getWidth(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width - DensityUtil.dip2px(this,24),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        m_Dialog.setContentView(view, layoutParams);
        tv_version.setText(portalVersionNumber);
        rl_down2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SPUtils.put(MyApp.getInstance(),"update",portalVersionNumber);
                m_Dialog.dismiss();
                showNewSuDialog();
            }
        });
        String update = (String) SPUtils.get(MyApp.getInstance(), "update", "");
        Log.e("update",update);
        String a = portalVersionNumber.replace(".", "");
        String b = localVersionName.replace(".", "");
        int result = a.compareTo(b);
        System.out.println(result);
        if(result < 0){//a<b
            showNewSuDialog();
        }else if(result == 0){ //a==b
            showNewSuDialog();
        }else { //a>b
            if(update.equals(portalVersionNumber)){

                showNewSuDialog();
            }else {
                dealData(portalVersionNumber,rl_down,rl_down2,rl_cancel,portalVersionUpdate);
            }
        }

        rl_down.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_Dialog.dismiss();
                permissionsUtil=  PermissionsUtil
                        .with(Main2Activity.this)
                        .requestCode(3)
                        .isDebug(true)//开启log
                        .permissions(PermissionsUtil.Permission.Storage.READ_EXTERNAL_STORAGE,
                                PermissionsUtil.Permission.Storage.WRITE_EXTERNAL_STORAGE)
                        .request();

               /* Intent intent = new Intent(Main2Activity.this,InfoDetailsActivity2.class);
                intent.putExtra("appUrl",portalVersionDownloadAdress);
                intent.putExtra("title2","下载地址");
                startActivity(intent);
                if(isUpdate == 1){
                    finish();
                }*/

            }
        });
        rl_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_Dialog.dismiss();

                showNewSuDialog();
            }
        });


    }


    private void dealData(String portalVersionNumber, RelativeLayout rl_down, RelativeLayout rl_down2, RelativeLayout rl_cancel, int portalVersionUpdate) {
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
                        m_Dialog.show();
                        if (portalVersionUpdate == 1) {//1代表强制更新

                            isUpdate = 1;
                            m_Dialog.setCanceledOnTouchOutside(false);
                            m_Dialog.setCancelable(false);
                            rl_cancel.setVisibility(View.GONE);
                            rl_down2.setVisibility(View.GONE);
                        } else {//普通更新
                            m_Dialog.setCanceledOnTouchOutside(false);
                            rl_cancel.setClickable(false);
                            isUpdate = 0;

                        }
                    }


                } else if (sp1 < sl1) {//服务器第二位版本号小于本地第二位版本号 不执行更新操作

                } else {//服务器第二位版本号大于本地第二位版本号 不执行更新操作
                    m_Dialog.show();
                    if (portalVersionUpdate == 1) {//1代表强制更新

                        isUpdate = 1;
                        m_Dialog.setCanceledOnTouchOutside(false);
                        m_Dialog.setCancelable(false);
                        rl_cancel.setVisibility(View.GONE);
                        rl_down2.setVisibility(View.GONE);
                    } else {//普通更新
                        m_Dialog.setCanceledOnTouchOutside(false);
                        rl_cancel.setClickable(false);
                        isUpdate = 0;

                    }
                }

            } else if (sp0 < sl0) {//服务器主版本号小于本地主版本号 不执行更新操作

            } else {//服务器主版本号大于本地版本号 执行更新操作
                m_Dialog.show();
                if (portalVersionUpdate == 1) {//1代表强制更新

                    isUpdate = 1;
                    m_Dialog.setCanceledOnTouchOutside(false);
                    m_Dialog.setCancelable(false);
                    rl_cancel.setVisibility(View.GONE);
                    rl_down2.setVisibility(View.GONE);
                } else {//普通更新
                    m_Dialog.setCanceledOnTouchOutside(false);
                    rl_cancel.setClickable(false);
                    isUpdate = 0;

                }
            }
        }
    }


    @Override
    protected void onPause() {
        isForeground = false;
        super.onPause();

    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.e("onResume","执行了");
        String isLoading3 = (String) SPUtils.get(Main2Activity.this, "isloading3", "");
        if(!isLoading3 .equals("")){
            SPUtils.put(getApplicationContext(),"isloading3","");
            ViewUtils.createLoadingDialog2(Main2Activity.this,true,"人脸上传中");

        }

        String count = (String) SPUtils.get(this, "count", "");

        isForeground = true;
        //showFragment(flag);
        isLogin = !StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username",""));


        if (isLogin){

            time = (String) SPUtils.get(MyApp.getInstance(), "time", "");
            if(time.equals("")){
                time = Calendar.getInstance().getTimeInMillis()+"";
            }
            long nowTime = Calendar.getInstance().getTimeInMillis();
            long l = nowTime - Long.parseLong(time);

            long l1 = l / 1000 / 60 / 60;

               if(l1>=1){
                   netWorkLogin();

               }else {
                   webView.setWebViewClient(mWebViewClient);
                   webView.loadUrl("http://iapp.zzuli.edu.cn/portal/login/appLogin");

                   if(count.equals("")){
                       netWorkSystemMsg();
                   }else {

                       netWorkSystemMsg();
                   }



               }
           /* if (Build.MANUFACTURER.equalsIgnoreCase("huaWei")) {
                addHuaWeiCut(count);
            }else if(Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")){
                xiaoMiShortCut(count);
            }else if (Build.MANUFACTURER.equalsIgnoreCase("vivo")) {

                vivoShortCut(count);
            }*/

        }else {
            badge1.hide();


        }


    }



    private String s1;
    private String s2;
    LoginBean loginBean;
    String tgt;
    private void netWorkLogin() {
        try {
            String phone = (String) SPUtils.get(MyApp.getInstance(), "phone", "");
            String pwd = (String) SPUtils.get(MyApp.getInstance(), "pwd", "");

            s1 = URLEncoder.encode(AesEncryptUtile.encrypt(phone,key),"UTF-8");
            s2 =  URLEncoder.encode(AesEncryptUtile.encrypt(pwd,key),"UTF-8");


        } catch (Exception e) {
            e.printStackTrace();
        }


        OkGo.<String>get(HOME2_URL +"/cas/casApiLoginController")
                .params("openid",AesEncryptUtile.openid)
                .params("username",s1)
                .params("password",s2)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("result1",response.body());

                        loginBean = JSON.parseObject(response.body(),LoginBean.class);
                        if (loginBean.isSuccess() ) {

                            try {
                                CookieManager cookieManager =  CookieManager.getInstance();
                                cookieManager.removeAllCookie();

                                tgt = AesEncryptUtile.decrypt(loginBean.getAttributes().getTgt(),key);
                                String userName = AesEncryptUtile.decrypt(loginBean.getAttributes().getUsername(),key) ;


                                String userId  = AesEncryptUtile.encrypt(userName+ "_"+ Calendar.getInstance().getTimeInMillis(),key);
                                SPUtils.put(MyApp.getInstance(),"time",Calendar.getInstance().getTimeInMillis()+"");
                                SPUtils.put(MyApp.getInstance(),"userId",userId);
                                SPUtils.put(MyApp.getInstance(),"personName",userName);
                                SPUtils.put(getApplicationContext(),"TGC",tgt);
                                SPUtils.put(getApplicationContext(),"username",s1);
                                SPUtils.put(getApplicationContext(),"password",s2);

                                String msspid = loginBean.getAttributes().getMsspid();
                                SPUtils.put(getApplicationContext(),"msspID",msspid);

                                webView.setWebViewClient(mWebViewClient);
                                webView.loadUrl("http://iapp.zzuli.edu.cn/portal/login/appLogin");
                                Intent intent = new Intent();
                                intent.putExtra("refreshService","dongtai");
                                intent.setAction("refresh2");
                                sendBroadcast(intent);


                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }else {
                            SPUtils.put(getApplicationContext(),"username","");
                            Intent intent = new Intent(MyApp.getInstance(),Main2Activity.class);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
    }

    CountBean countBean1;
    private void netWorkSystemMsg() {

        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_countUnreadMessagesForCurrentUser)
                .tag(this)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.toString());

                        countBean1 = JSON.parseObject(response.body(), CountBean.class);
                        netWorkOAToDoMsg();//OA待办

                    }
                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }
    CountBean countBean2;
    /**OA消息列表*/
    private void netWorkOAToDoMsg() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_count)
                .tag(this)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "db")
                .params("workType", "workdb")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.toString());

                        countBean2 = JSON.parseObject(response.body(), CountBean.class);
                        netWorkEmailMsg();

                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }
    CountBean countBeanEmail;
    private void netWorkEmailMsg() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_emai_count)
                .tag(this)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("邮件数量首页",response.body());
                        countBeanEmail = JSON.parseObject(response.body(), CountBean.class);
                        netWorkDyMsg();
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }


    CountBean countBean3;
    private void netWorkDyMsg() {
        OkGo.<String>post(UrlRes.HOME_URL + UrlRes.Query_count)
                .tag(this)
                .params("userId",(String) SPUtils.get(MyApp.getInstance(),"userId",""))
                .params("type", "dy")
                .params("workType", "workdb")
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("s",response.toString());

                        countBean3 = JSON.parseObject(response.body(), CountBean.class);

                        //tvMyToDoMsgNum.setText(countBean2.getCount()+Integer.parseInt(countBean1.getObj())+countBean3.getCount()+"");
                        String s = countBean2.getCount() + Integer.parseInt(countBean1.getObj()) + countBean3.getCount()+countBeanEmail.getCount() + "";

                        if(null == s){
                            s = "0";
                        }
                        SPUtils.put(MyApp.getInstance(),"count",s+"");
                        String count = (String) SPUtils.get(MyApp.getInstance(), "count", "");
                       /* if (Build.MANUFACTURER.equalsIgnoreCase("huaWei")) {

                            addHuaWeiCut(count);

                        }else if(Build.MANUFACTURER.equalsIgnoreCase("Xiaomi")){
                            xiaoMiShortCut(count);
                        }else if (Build.MANUFACTURER.equalsIgnoreCase("vivo")) {
                            vivoShortCut(count);
                        }*/

                        remind();
                        if(s.equals("0")){

                            badge1.hide();
                        }else {
                            badge1.show();
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);

                    }
                });
    }

    private void vivoShortCut(String count) {
        Intent localIntent = new Intent("launcher.action.CHANGE_APPLICATION_NOTIFICATION_NUM");
        localIntent.putExtra("packageName", "io.cordova.zhqy");
        String launchClassName = getPackageManager().getLaunchIntentForPackage(getPackageName()).getComponent().getClassName();
        localIntent.putExtra("className", launchClassName);
        if (TextUtils.isEmpty(count)) {
            count = "";
        } else { int numInt = Integer.valueOf(count);
            if (numInt > 0) { if (numInt > 99) { count = "99";
            }
            } else { count = "0";
            }
        }
        localIntent.putExtra("notificationNum", Integer.parseInt(count));
       sendBroadcast(localIntent);

    }

    private void xiaoMiShortCut(String count) {
        int number = Integer.parseInt(count);
        try {
            Notification notification = new Notification();
            Field field = notification.getClass().getDeclaredField("extraNotification");
            Object extraNotification = field.get(notification);
            Method method = extraNotification.getClass().getDeclaredMethod("setMessageCount", int.class);
            method.invoke(extraNotification, number);
        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void addHuaWeiCut(String count) {
        try {
            if(count.equals("")){
                count = "0";
            }
            int number = Integer.parseInt(count);
            if (number < 0) {
                number = 0;
            }
            Bundle bundle = new Bundle();
            bundle.putString("package","io.cordova.zhqy");
            String launchClassName = getPackageManager().getLaunchIntentForPackage(getPackageName()).getComponent().getClassName();
            //bundle.putString("class", "io.cordova.zhqy.Main2Activity");
            bundle.putString("class",launchClassName);
            bundle.putInt("badgenumber", number);
            getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, bundle);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getMyLocation() {

        LocationClientOption option = new LocationClientOption();

        //设置坐标类型
        option.setCoorType("bd09ll");
        //设置是否需要地址信息，默认为无地址
        option.setIsNeedAddress(true);
        option.setIsNeedAltitude(true);
        option.setIsNeedLocationDescribe(true);
        option.setNeedDeviceDirect(true);
        //设置是否打开gps进行定位
        option.setOpenGps(true);
        //设置扫描间隔为1秒
        option.setScanSpan(300000);
        //传入设置好的信息
        mLocationClient.setLocOption(option);

        mLocationClient.start();
    }

    private void isOpen() {
        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
        boolean areNotificationsEnabled = notificationManagerCompat.areNotificationsEnabled();
        Log.e("isOpen",areNotificationsEnabled+"");

    }

    //请求悬浮窗权限
    @TargetApi(Build.VERSION_CODES.O)
    private void getOverlayPermission() {
        perTag = 1;
        Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION);
        intent.setData(Uri.parse("package:" + getPackageName()));
        startActivityForResult(intent, 0);
    }
    /**推送设置弹窗*/
    @RequiresApi(api = Build.VERSION_CODES.O)
    private void showDialog() {

        if (mAlertDialog == null) {
            mAlertDialog = new AlertDialog.Builder(this)
                    .setMessage("您需要去开启推送权限么?\n")
                    .setNegativeButton("不需要", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mAlertDialog != null) {
                                mAlertDialog.dismiss();
                            }
                        }
                    })//
                    .setPositiveButton("去设置", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (mAlertDialog != null) {
                                mAlertDialog.dismiss();
                            }
                            if (perTag == 0){
                                getOverlayPermission();
                            }else if (perTag == 1){
                                perTag =0;
                                NotificationsUtils.requestNotify(getApplicationContext());
                            }
                        }
                    }).create();
        }

        mAlertDialog.show();
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isrRunIng = "0";
        unregisterReceiver(broadcastReceiver);
        mLocationClient.stop();
        mLocationClient.stopIndoorMode();
    }

    private WebViewClient mWebViewClient = new WebViewClient() {
        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            Log.i("userAgent4",  view.getSettings().getUserAgentString());


        }

        //        /**网址拦截*/
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
            String url =  null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                url = request.getUrl().toString();
            }


            if (url.contains(UrlRes.HOME2_URL+"/cas/login")) {
                if (StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username",""))){
                    Intent intent = new Intent(getApplicationContext(),LoginActivity2.class);
                    startActivity(intent);
                    finish();

                    return true;
                }
            }
            return super.shouldOverrideUrlLoading(view, request);
        }

        /**网址拦截*/
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            if (url.contains(UrlRes.HOME2_URL+"/cas/login")) {
                if (StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username",""))){
                    Intent intent = new Intent(getApplicationContext(),LoginActivity2.class);
                    startActivity(intent);
                    finish();

                    return true;
                }
            }

            return super.shouldOverrideUrlLoading(view, url);
        }

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {


            String tgc = (String) SPUtils.get(Main2Activity.this, "TGC", "");
            CookieUtils.syncCookie(HOME2_URL,"CASTGC="+tgc,getApplication());


        }

    };


    private MyDialog m_Dialog2;
    boolean allowedScan = false;
    private void logOut() {
        m_Dialog2 = new MyDialog(this, R.style.dialogdialog);
        Window window = m_Dialog2.getWindow();
        window.setGravity(Gravity.CENTER);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_new_student, null);
        RelativeLayout rl_sure = view.findViewById(R.id.rl_sure);

        //int width = ScreenSizeUtils.getWidth(getActivity());
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1,-1);
        m_Dialog2.setContentView(view, layoutParams);
        m_Dialog2.show();
        m_Dialog2.setCanceledOnTouchOutside(false);

        rl_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Main2Activity.this, FaceNewActivity.class);
                startActivity(intent);

            }
        });

        m_Dialog2.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialogInterface, int keyCode, KeyEvent keyEvent) {
                if (keyCode==KeyEvent.KEYCODE_BACK && keyEvent.getRepeatCount()==0)
                {
                    return true;
                }
                else
                {
                    return false;
                }
            }
        });
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        //需要调用onRequestPermissionsResult
        permissionsUtil.onRequestPermissionsResult(requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        QRCodeManager.getInstance().with(this).onActivityResult(requestCode, resultCode, data);
        //监听跳转到权限设置界面后再回到应用
        permissionsUtil.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPermissionsGranted(int requestCode, String... permission) {
        Log.e("权限获取回调主页","onPermissionsGranted");

        if(requestCode == 0){
            getMyLocation();
            addMobieInfo();
        }else if(requestCode == 1){
            onScanQR();
        }else if(requestCode == 3){

            Intent intent = new Intent(Main2Activity.this, BaseLoadActivity.class);
            if(portalVersionDownloadAdress != null){
                intent.putExtra("appUrl",portalVersionDownloadAdress);
            }
            intent.putExtra("title2","下载地址");
            startActivity(intent);
            if(isUpdate == 1){
                finish();
            }
        }else if(requestCode == 4){
            logOut();
        }


    }

    private void onScanQR() {
        QRCodeManager.getInstance()
                .with(this)
                .setReqeustType(0)
                .setRequestCode(55846)
                .scanningQRCode(new OnQRCodeListener() {
                    @Override
                    public void onCompleted(String result) {

                        Log.e("result",result);
                       /* String[] split = result.split("_");
                        String s = split[1];
                        if(s.equals("1")){
                            yanqianData(split[0]);

                        }else {

                        }*/

                        String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                        if(isOpen.equals("") || isOpen.equals("1")){
                            Intent intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                            intent.putExtra("appUrl",result);
                            intent.putExtra("scan","scan");
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                            intent.putExtra("appUrl",result);
                            intent.putExtra("scan","scan");
                            startActivity(intent);
                        }

                    }

                    @Override
                    public void onError(Throwable errorMsg) {

                    }

                    @Override
                    public void onCancel() {

                    }


                    @Override
                    public void onManual(int requestCode, int resultCode, Intent data) {

                    }
                });
    }


    @Override
    public void onPermissionsDenied(int requestCode, String... permission) {
        Log.e("权限被拒绝回调","onPermissionsDenied");
        getMyLocation();
        addMobieInfo();



    }


}
