package io.cordova.zhqy.utils;

import java.util.HashMap;

public class CAErrorUtils {

    public static HashMap<String,String> getErrorInfo(){
        HashMap<String,String> hashMap = new HashMap<>();
        hashMap.put("0x00000000","交互成功");
        hashMap.put("0x12200001","参数为空");
        hashMap.put("0x11000001","用户取消操作");
        hashMap.put("0x12200000","异常");
        hashMap.put("0x12100000","参数异常");
        hashMap.put("0x14100001","权限异常");
        hashMap.put("0x18000002","网络不可用");
        hashMap.put("0x81200001","证书下载码无效");
        hashMap.put("0x8120000A","无用户");
        hashMap.put("0x81200006","用户锁定");
        hashMap.put("0x14300001","本地无证书");
        hashMap.put("0x81800009","用户无权限签署当前数据");
        hashMap.put("0x81400003","证书生成失败,请等待证书导入");
        return hashMap;
    }
}
