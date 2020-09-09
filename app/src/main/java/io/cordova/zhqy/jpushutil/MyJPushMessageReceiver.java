package io.cordova.zhqy.jpushutil;

import android.content.Context;
import android.util.Log;

import cn.jpush.android.api.CmdMessage;
import cn.jpush.android.api.CustomMessage;
import cn.jpush.android.api.JPushMessage;
import cn.jpush.android.api.NotificationMessage;
import cn.jpush.android.service.JPushMessageReceiver;

public class MyJPushMessageReceiver extends JPushMessageReceiver {
    @Override
    public void onMessage(Context context, CustomMessage customMessage) {
        super.onMessage(context, customMessage);
    }

    @Override
    public void onRegister(Context context, String s) {
        super.onRegister(context, s);
    }

    @Override
    public void onConnected(Context context, boolean b) {
        super.onConnected(context, b);
    }

    @Override
    public void onCommandResult(Context context, CmdMessage cmdMessage) {
        super.onCommandResult(context, cmdMessage);
    }

    @Override
    public void onNotifyMessageArrived(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageArrived(context, notificationMessage);
    }

    @Override
    public void onNotifyMessageDismiss(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageDismiss(context, notificationMessage);
    }

    @Override
    public void onNotifyMessageOpened(Context context, NotificationMessage notificationMessage) {
        super.onNotifyMessageOpened(context, notificationMessage);
    }
}