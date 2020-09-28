package io.cordova.zhqy.jpushutil;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import cn.jpush.android.api.JPushInterface;

import io.cordova.zhqy.Main2Activity;
import io.cordova.zhqy.R;
import io.cordova.zhqy.activity.DialogActivity;
import io.cordova.zhqy.activity.MyShenqingActivity;
import io.cordova.zhqy.activity.OaMsgActivity;
import io.cordova.zhqy.activity.OpenClickActivity;
import io.cordova.zhqy.activity.SplashActivity;
import io.cordova.zhqy.activity.SystemMsgActivity;
import io.cordova.zhqy.bean.NoticeCaBean;
import io.cordova.zhqy.utils.ActivityUtils;
import io.cordova.zhqy.utils.ExampleUtil;
import io.cordova.zhqy.utils.JsonUtil;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.StringUtils;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.web.BaseWebActivity4;
import io.cordova.zhqy.web.BaseWebCloseActivity;

import static android.content.Context.NOTIFICATION_SERVICE;
import static cn.jpush.android.api.JPushInterface.clearAllNotifications;


/**
 * Created by Administrator on 2019/1/9 0009.
 *  推送接收器
 */

public class JPushReceiver extends BroadcastReceiver {

    private static final String TAG = "JPushReceiver";


 private LocalBroadcastManager broadcastManager;

    @Override
    public void onReceive(Context context, Intent intent) {
        //initChannel(context);
        try {
            Bundle bundle = intent.getExtras();
            if (JPushInterface.ACTION_REGISTRATION_ID.equals(intent.getAction())) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                Log.e(TAG, "[MyReceiver] 接收Registration Id : " + regId);
                MyApp.registrationId  = regId;

            } else if(JPushInterface.ACTION_MESSAGE_RECEIVED.equals(intent.getAction())) {

                Log.e(TAG, "[MyReceiver] 接收自定义消息 : "+ bundle.getString(JPushInterface.EXTRA_EXTRA));
                //ToastUtils.showToast(context,"[MyReceiver] 接收自定义消息");
                //processCustomMessage(context,bundle);
                clearAllNotifications(context);
                String string = bundle.getString(JPushInterface.EXTRA_EXTRA);
                if(string.contains("sign")){

                    NoticeCaBean noticeCaBean = JsonUtil.parseJson(string,NoticeCaBean.class);
                    String sign = noticeCaBean.getSign();
                    String signDataId = noticeCaBean.getSignDataId();
                    String sendTime = noticeCaBean.getSendTime();
                    String messageId = noticeCaBean.getMessageId();
                    String title = noticeCaBean.getTitle();



                    if(null != messageId){
                        Intent intent2 = new Intent();
                        intent2.putExtra("fromWhere","notice");
                        intent2.putExtra("signId",signDataId);
                        intent2.putExtra("sendTime",sendTime);
                        intent2.putExtra("messageId",messageId);
                        intent2.putExtra("title",title);
                        intent2.setAction("refreshJpush");
                        context.sendBroadcast(intent2);
                    }else {
                        if(isActivityTop(DialogActivity.class,context)) {
                            ActivityUtils.getActivityManager().finishLastActivity();
                        }
                        Intent intent1 = new Intent(context, DialogActivity.class);;
                        intent1.putExtra("fromWhere","notice");
                        intent1.putExtra("signId",signDataId);
                        intent1.putExtra("sendTime",sendTime);

                        intent1.putExtra("messageId",messageId);
                        intent1.putExtra("title",title);
                        context.startActivity(intent1);

                    }


                    if(!Main2Activity.isForeground){
                        ActivityUtils.getActivityManager().finishClassActivity(DialogActivity.class);
                    }
                }else {
                    String msg =bundle.getString(JPushInterface.EXTRA_MESSAGE);
                    String title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);
                    showNotification(context,bundle.getString(JPushInterface.EXTRA_EXTRA),msg,title);
                }


            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(intent.getAction())) {
                    Log.e(TAG, "[MyReceiver] 接收到推送下来的通知"+ JPushInterface.EXTRA_MESSAGE);

                    //ToastUtils.showToast(context,"[MyReceiver] 接收到推送下来的通知");
                //showNotification(context,bundle.getString(JPushInterface.EXTRA_EXTRA));

            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(intent.getAction())) {

                Log.e(TAG, "[MyReceiver] 用户点击打开了通知"+ bundle.getString(JPushInterface.EXTRA_EXTRA));

                if (!Main2Activity.isForeground){
                    SPUtils.put(MyApp.getInstance(),"InfoType","1");
                    Intent i = new Intent(context, SplashActivity.class);
                    i.putExtras(bundle);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    context.startActivity(i);

                }else {
                    SPUtils.put(MyApp.getInstance(),"InfoType","0");
                    processCustomMessage(context,bundle);
                }

            } else if (JPushInterface.ACTION_RICHPUSH_CALLBACK.equals(intent.getAction())) {
                Log.e(TAG, "[MyReceiver] 用户收到到RICH PUSH CALLBACK: " + bundle.getString(JPushInterface.EXTRA_EXTRA));
                //在这里根据 JPushInterface.EXTRA_EXTRA 的内容处理代码，比如打开新的Activity， 打开一个网页等..
            } else if (JPushInterface.ACTION_CONNECTION_CHANGE.equals(intent.getAction())) {
                boolean connected = intent.getBooleanExtra(JPushInterface.EXTRA_CONNECTION_CHANGE, false);
                Log.e(TAG, "[MyReceiver1]" + intent.getAction() + " connected state change to " + connected);
            } else {
                Log.e(TAG, "[MyReceiver] Unhandled intent - " + intent.getAction());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     *
     * 判断某activity是否处于栈顶
     * @return true在栈顶 false不在栈顶
     */
    private boolean isActivityTop(Class cls,Context context){
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(1).get(0).topActivity.getClassName();
        return name.equals(cls.getName());
    }



    private void processCustomMessage(Context context, Bundle bundle) {
        String msg = null;
        String title = null;

        String extrasBean = null;
        String extrasBean2 = null;
        String msgId = null;
        String msgType = null;

        if(bundle!=null){
            msg =bundle.getString(JPushInterface.EXTRA_MESSAGE);
            title = bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE);

            extrasBean = bundle.getString(JPushInterface.EXTRA_EXTRA);
            extrasBean2 = bundle.getString("JMessageExtra");
            if(!StringUtils.isEmpty(extrasBean)){
                try {
                    JSONObject extraJson = new JSONObject(extrasBean);
                    if (extraJson.length() > 0) {
                        Log.e("extraJson", extrasBean);

                        msgId =  extraJson.getString("messageId");
                        msgType =  extraJson.getString("messageType");
                        Log.e("msgId", msgId);
                        Log.e("msgType", msgType);
                        SPUtils.put(MyApp.getInstance(),"msgId",msgId);
                        SPUtils.put(MyApp.getInstance(),"msgType",msgType);

                    }
                } catch (JSONException ignored) {
                    Log.e("JPush",ignored.getMessage());
                }
            }

        }
        if (StringUtils.isEmpty(title)) {
            title = " i 轻工大";
        }


            /*当前页面后台隐藏*/
        if (!StringUtils.isEmpty(msgType)){
            Intent intent;
            if (msgType.equals("0")){
                intent = new Intent(MyApp.getInstance(), SystemMsgActivity.class);
                intent.putExtra("msgType","系统消息");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else if (msgType.equals("1")){
                intent = new Intent(MyApp.getInstance(), OaMsgActivity.class);
                intent.putExtra("type","db");
                intent.putExtra("msgType","待办消息");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else if (msgType.equals("2")){
                intent = new Intent(MyApp.getInstance(), OaMsgActivity.class);
                intent.putExtra("type","dy");
                intent.putExtra("msgType","待阅消息");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else if (msgType.equals("3")){
                intent = new Intent(MyApp.getInstance(), OaMsgActivity.class);
                intent.putExtra("type","yb");
                intent.putExtra("msgType","已办消息");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else if (msgType.equals("4")){
                intent = new Intent(MyApp.getInstance(), OaMsgActivity.class);
                intent.putExtra("type","yy");
                intent.putExtra("msgType","已阅消息");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else if (msgType.equals("5")){
                intent = new Intent(MyApp.getInstance(), MyShenqingActivity.class);
                intent.putExtra("type","sq");
                intent.putExtra("msgType","我的申请");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }else if (msgType.equals("6")){
                String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                if(isOpen.equals("") || isOpen.equals("1")){
                    intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                }else {
                    intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                }
                intent.putExtra("appUrl","http://kys.zzuli.edu.cn/cas/login?service=http://mail.zzuli.edu.cn/coremail/cmcu_addon/sso.jsp?face=hxphone");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intent);
            }
            SPUtils.remove(MyApp.getInstance(),"msgType");
            SPUtils.remove(MyApp.getInstance(),"msgId");

        }


    }


    int requestCode;
    /**
     * 显示通知栏
     * @param context 上下文对象
     * @param pushMsg 消息bean类
     */
    private void showNotification(Context context, String pushMsg,String msg,String title) {



        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        // 兼容 8.0 系统
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(context, nm);
        }
        NotificationCompat.Builder builder = createNotificationCompatBuilder(context, pushMsg,msg,title);

        nm.notify(requestCode, builder.build());
    }

    @NonNull
    private NotificationCompat.Builder createNotificationCompatBuilder(Context context, String pushMsg,String msg,String title) {
        requestCode = (int) System.currentTimeMillis();
        Intent broadcastIntent = new Intent(context, GeTuiNotificationClickReceiver.class);
        broadcastIntent.putExtra("message", pushMsg);
        PendingIntent pendingIntent = PendingIntent.
                getBroadcast(context, requestCode, broadcastIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "message_id");;
        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.drawable.icon_logo);
        builder.setContentTitle(title);
        builder.setContentText(msg);
        builder.setAutoCancel(true);
        builder.setDefaults(Notification.DEFAULT_ALL);

        return builder;
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(Context context, NotificationManager notificationManager) {
        // 通知渠道
        NotificationChannel mChannel = new NotificationChannel("pre213", "消息通知订阅", NotificationManager.IMPORTANCE_HIGH);
        // 开启指示灯，如果设备有的话。
        mChannel.enableLights(true);
        // 开启震动
        mChannel.enableVibration(true);
        //  设置指示灯颜色
        mChannel.setLightColor(Color.RED);
        // 设置是否应在锁定屏幕上显示此频道的通知
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        // 设置是否显示角标
        mChannel.setShowBadge(true);
        //  设置绕过免打扰模式
        mChannel.setBypassDnd(true);
        // 设置震动频率
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400});
        //最后在notificationmanager中创建该通知渠道
        notificationManager.createNotificationChannel(mChannel);
    }


}



