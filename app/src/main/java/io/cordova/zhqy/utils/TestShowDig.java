package io.cordova.zhqy.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;

/**
 * Created by Administrator on 2019/9/4 0004.
 */

public class TestShowDig {
    public static void AskForPermission(final Context context,String name) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("("+name+")"+"权限尚未开启,请允许，否则将影响部分功能的正常使用!");
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setPositiveButton("去授权", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                intent.setData(Uri.parse("package:" + context.getPackageName())); // 根据包名打开对应的设置界面
                context.startActivity(intent);
            }
        });
        builder.create().show();
    }


}
