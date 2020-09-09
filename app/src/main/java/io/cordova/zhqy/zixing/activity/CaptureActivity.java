/*
 * Copyright (C) 2008 ZXing authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.cordova.zhqy.zixing.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;


import java.io.IOException;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Pattern;

import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.activity.CAResultActivity;
import io.cordova.zhqy.activity.LoginActivity2;
import io.cordova.zhqy.bean.CaBean;
import io.cordova.zhqy.utils.DensityUtil;
import io.cordova.zhqy.utils.JsonUtil;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.ScreenSizeUtils;
import io.cordova.zhqy.utils.StringUtils;
import io.cordova.zhqy.utils.ViewUtils;
import io.cordova.zhqy.web.BaseWebActivity4;
import io.cordova.zhqy.web.BaseWebCloseActivity;
import io.cordova.zhqy.zixing.FlowLineView;
import io.cordova.zhqy.zixing.MyDialog;
import io.cordova.zhqy.zixing.camera.CameraManager;
import io.cordova.zhqy.zixing.decode.DecodeBitmap;
import io.cordova.zhqy.zixing.decode.DecodeThread;
import io.cordova.zhqy.zixing.utils.BeepManager;
import io.cordova.zhqy.zixing.utils.CaptureActivityHandler;
import io.cordova.zhqy.zixing.utils.InactivityTimer;
import io.cordova.zhqy.zixing.utils.SelectAlbumUtils;

import static io.cordova.zhqy.UrlRes.caQrCodeVerifyUrl;

/**
 * This activity opens the camera and does the actual scanning on a background
 * thread. It draws a viewfinder to help the user place the barcode correctly,
 * shows feedback as the image processing is happening, and then overlays the
 * results when a scan is successful.
 *
 * @author dswitkin@google.com (Daniel Switkin)
 * @author Sean Owen
 */

public class CaptureActivity extends Activity implements
        SurfaceHolder.Callback, View.OnClickListener {

    private static final String TAG = CaptureActivity.class.getSimpleName();
    public static final int RESULT_MULLT = 5;
    private CameraManager cameraManager;
    private CaptureActivityHandler handler;
    private InactivityTimer inactivityTimer;
    private BeepManager beepManager;

    private SurfaceView scanPreview = null;
    private RelativeLayout scanContainer;
    private RelativeLayout scanCropView;
    private ImageView scanLine;

    private Rect mCropRect = null;
    private boolean isHasSurface = false;
    private ImageView ivBack, ivMullt;
    private int captureType = 0;
    private TextView tvAlbum;
    private static final int CODE_GALLERY_REQUEST = 101;

    public Handler getHandler() {
        return handler;
    }

    public CameraManager getCameraManager() {
        return cameraManager;
    }

    private DialogInterface.OnClickListener mOnClickListener;
    TextView photoTv;
    TextView photoTv2;
    TextView tv_left;
    TextView tv_right;
    TextView capture_mask_bottom;
    FlowLineView flowLineView;
    RelativeLayout capture_crop_view2;
    RelativeLayout rl_title;
    LinearLayout ll_left;
    LinearLayout ll_right;
    ImageView iv_left;
    ImageView iv_right;
    ImageView iv_back2;
    private int tag = 0;



    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_capture);
        captureType = getIntent().getIntExtra("type", 0);
        String string = getApplication().getResources().getString(R.string.jwstr_scan_it);
//        Log.e("hdltag", "onCreate(CaptureActivity.java:102):" +string);
        scanPreview = (SurfaceView) findViewById(R.id.capture_preview);
        scanContainer = (RelativeLayout) findViewById(R.id.capture_container);
        scanCropView = (RelativeLayout) findViewById(R.id.capture_crop_view);
        scanLine = (ImageView) findViewById(R.id.capture_scan_line);
        ivBack = (ImageView) findViewById(R.id.iv_back);
        ivMullt = (ImageView) findViewById(R.id.iv_mudle);
        tvAlbum = (TextView) findViewById(R.id.tv_capture_select_album_jwsd);

        tv_left = (TextView) findViewById(R.id.tv_left);
        tv_right = (TextView) findViewById(R.id.tv_right);
        flowLineView = (FlowLineView) findViewById(R.id.autoscanner_view);
        capture_crop_view2 = (RelativeLayout) findViewById(R.id.capture_crop_view2);
        rl_title = (RelativeLayout) findViewById(R.id.rl_title);
        capture_mask_bottom = (TextView) findViewById(R.id.capture_mask_bottom);
        ll_left = (LinearLayout) findViewById(R.id.ll_left);
        ll_right = (LinearLayout) findViewById(R.id.ll_right);
        iv_left = (ImageView) findViewById(R.id.iv_left);
        iv_right = (ImageView) findViewById(R.id.iv_right);


        photoTv = (TextView) findViewById(R.id.photo);
        photoTv2 = (TextView) findViewById(R.id.photo2);
        iv_back2 = (ImageView) findViewById(R.id.iv_back2);

        photoTv.setTag(128);
        photoTv2.setTag(130);
        photoTv.setOnClickListener(this);
        ivBack.setTag(123);
        ivMullt.setTag(124);
        tvAlbum.setTag(125);
        iv_back2.setTag(131);
        tvAlbum.setOnClickListener(this);
        ivBack.setOnClickListener(this);
        ivMullt.setOnClickListener(this);
        photoTv2.setOnClickListener(this);
        iv_back2.setOnClickListener(this);
        inactivityTimer = new InactivityTimer(this);
        beepManager = new BeepManager(this);

        if (captureType == 1) {
            ivMullt.setVisibility(View.INVISIBLE);
        }

        TranslateAnimation animation = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.9f);
        animation.setDuration(4500);
        animation.setRepeatCount(-1);
        animation.setRepeatMode(Animation.RESTART);
        scanLine.startAnimation(animation);

        ll_left.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cameraManager = null;
                //cameraManager = new CameraManager(getApplication());
                cameraManager.stopPreview();
                handler = null;
                if (isHasSurface) {
                    initCamera(scanPreview.getHolder());
                } else {
                    scanPreview.getHolder().addCallback(CaptureActivity.this);
                }

                inactivityTimer.onResume();
                capture_crop_view2.setVisibility(View.GONE);
                scanCropView.setVisibility(View.VISIBLE);
                rl_title.setVisibility(View.VISIBLE);
                tag = 0;
                iv_left.setImageResource(R.drawable.code2);
                tv_left.setTextColor(Color.parseColor("#1d4481"));
                tv_right.setTextColor(Color.parseColor("#ffffff"));
                iv_right.setImageResource(R.drawable.yq2);
                capture_mask_bottom.setVisibility(View.VISIBLE);
            }
        });
        ll_right.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //cameraManager = null;
                //cameraManager = new CameraManager(getApplication());
                cameraManager.stopPreview();
                handler = null;
                if (isHasSurface) {
                    initCamera(scanPreview.getHolder());
                } else {
                    scanPreview.getHolder().addCallback(CaptureActivity.this);
                }

                inactivityTimer.onResume();
                capture_crop_view2.setVisibility(View.VISIBLE);
                flowLineView.setCameraManager(cameraManager);
                scanCropView.setVisibility(View.GONE);
                rl_title.setVisibility(View.GONE);
                capture_mask_bottom.setVisibility(View.INVISIBLE);
                tag = 1;
                iv_left.setImageResource(R.drawable.code1);
                tv_left.setTextColor(Color.parseColor("#ffffff"));
                tv_right.setTextColor(Color.parseColor("#1d4481"));
                iv_right.setImageResource(R.drawable.yq1);
            }
        });


        tag = 0;
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraManager = new CameraManager(getApplication());
        handler = null;
        if (isHasSurface) {
            initCamera(scanPreview.getHolder());
        } else {
            scanPreview.getHolder().addCallback(this);
        }

        inactivityTimer.onResume();
    }

    @Override
    protected void onPause() {
        if (handler != null) {
            handler.quitSynchronously();
            handler = null;
        }
        inactivityTimer.onPause();
        beepManager.close();
        cameraManager.closeDriver();
        if (!isHasSurface) {
            scanPreview.getHolder().removeCallback(this);
        }

        super.onPause();
    }

    @Override
    protected void onDestroy() {
        inactivityTimer.shutdown();
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        if (holder == null) {
            Log.e(TAG, "*** WARNING *** surfaceCreated() gave us a null surface!");
        }
        if (!isHasSurface) {
            isHasSurface = true;
            initCamera(holder);
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        isHasSurface = false;
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
    }

    boolean isLogin =false;

    /**
     * A valid barcode has been found, so give an indication of success and show
     * the results.
     *
     * @param rawResult The contents of the barcode.
     * @param bundle    The extras
     */
    public void handleDecode(Result rawResult, Bundle bundle) {
        isLogin = !StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username",""));
        if (!isAllNumer(rawResult.getText())){
            inactivityTimer.onActivity();
            beepManager.playBeepSoundAndVibrate();
            String text = rawResult.getText();
            String result = rawResult.getText() + "_" + tag;
            if(result.startsWith("http") || result.contains("gilight")  ){

                if(!isLogin){
                    Intent intent = new Intent(MyApp.getInstance(), LoginActivity2.class);
                    startActivity(intent);
                }else {
                    String[] split = result.split("_");
                    String s = split[1];
                    if(s.equals("1")){
                        yanqianData(split[0]);

                    }else {
                        String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                        if(isOpen.equals("") || isOpen.equals("1")){
                            Intent intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                            intent.putExtra("appUrl",text);
                            intent.putExtra("scan","scan");
                            startActivity(intent);
                            finish();
                        }else {
                            Intent intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                            intent.putExtra("appUrl",text);
                            intent.putExtra("scan","scan");
                            startActivity(intent);
                            finish();
                        }
                    }



                }

            }else{
                if(tag == 0){
                    logOut(text);
                }else {

                    if(!isLogin){
                        Intent intent = new Intent(MyApp.getInstance(), LoginActivity2.class);
                        startActivity(intent);
                    }else {
                        String[] split = result.split("_");
                        String s = split[1];
                        if(s.equals("1")){
                            yanqianData(split[0]);

                        }else {
                            String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                            if(isOpen.equals("") || isOpen.equals("1")){
                                Intent intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                                intent.putExtra("appUrl",text);
                                intent.putExtra("scan","scan");
                                startActivity(intent);
                                finish();
                            }else {
                                Intent intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                                intent.putExtra("appUrl",text);
                                intent.putExtra("scan","scan");
                                startActivity(intent);
                                finish();
                            }
                        }



                    }

                }

            }

        }else {
            handler.restartPreviewAndDecode();
        }


    }


    private void yanqianData(String s) {
        ViewUtils.createLoadingDialog(this);
        OkGo.<String>post(UrlRes.HOME_URL+caQrCodeVerifyUrl)
                .params("userId", (String) SPUtils.get(MyApp.getInstance(), "userId", ""))
                .params("content", s)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        Log.e("验签结果",response.body());
                        ViewUtils.cancelLoadingDialog();
                        CaBean faceBean2 = JsonUtil.parseJson(response.body(),CaBean.class);

                        boolean success = faceBean2.getSuccess();
                        if(success == true){

                            Intent intent = new Intent(CaptureActivity.this,CAResultActivity.class);
                            intent.putExtra("result","0");
                            String certDn = faceBean2.getObj().getCertDn();
                            String[] split = certDn.split(",");
                            if(split != null){

                                if(split.length > 0){
                                    String s1 = split[0];
                                    String substring = s1.substring(3, s1.length());
                                    intent.putExtra("certDn",substring);
                                }else {
                                    intent.putExtra("certDn","");
                                }
                            }else {
                                intent.putExtra("certDn","");
                            }

                            intent.putExtra("signTime",faceBean2.getObj().getSignTime());
                            String plainData = faceBean2.getObj().getPlainData();
                            int index = plainData.indexOf("http");
                            String pdfurl= plainData.substring(index,plainData.lastIndexOf("pdf")+3);
                            intent.putExtra("pdfurl",pdfurl);
                            startActivity(intent);
                        }else {
                            Intent intent = new Intent(CaptureActivity.this,CAResultActivity.class);
                            intent.putExtra("result","1");
                            startActivity(intent);
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        Log.e("s",response.toString());
                    }
                });

    }



    private MyDialog m_Dialog2;
    private void logOut(String text) {
        m_Dialog2 = new MyDialog(this, R.style.dialogdialog);
        Window window = m_Dialog2.getWindow();
        window.setGravity(Gravity.CENTER);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_camara_show, null);
        RelativeLayout rl_sure = view.findViewById(R.id.rl_sure);
        TextView tv_result = view.findViewById(R.id.tv_result);
        tv_result.setText(text);
        int width = ScreenSizeUtils.getWidth(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width - DensityUtil.dip2px(this,24),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        m_Dialog2.setContentView(view, layoutParams);
        m_Dialog2.show();
        m_Dialog2.setCanceledOnTouchOutside(true);
        rl_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_Dialog2.dismiss();
            }
        });

    }


    public static boolean isAllNumer(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        return pattern.matcher(str).matches();
    }



    /**
     * 扫描设备二维码成功
     *
     * @param rawResult
     * @param bundle
     */
    private void scanDeviceSuccess(String rawResult, Bundle bundle) {
       /* Intent resultIntent = new Intent();
        //bundle.putString("result", rawResult+"_"+tag);
        bundle.putString("result", rawResult+"_"+tag);
        resultIntent.putExtras(bundle);
        this.setResult(RESULT_OK, resultIntent);
        CaptureActivity.this.finish();*/
        isLogin = !StringUtils.isEmpty((String)SPUtils.get(MyApp.getInstance(),"username",""));
        String text = rawResult;
        String s = rawResult + "_" + tag;
        if(s.startsWith("http") || s.contains("gilight")  ){
          /*
            Intent resultIntent = new Intent();
            bundle.putString("result", rawResult+"_"+tag);
            bundle.putString("result", rawResult+"_"+tag);
            resultIntent.putExtras(bundle);
            this.setResult(RESULT_OK, resultIntent);
            CaptureActivity.this.finish();*/


            String[] split = s.split("_");
            String s1 = split[1];
            if(s1.equals("1")){
                yanqianData(split[0]);

            }else {
                String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                if(isOpen.equals("") || isOpen.equals("1")){
                    Intent intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                    intent.putExtra("appUrl",text);
                    intent.putExtra("scan","scan");
                    startActivity(intent);
                    finish();
                }else {
                    Intent intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                    intent.putExtra("appUrl",text);
                    intent.putExtra("scan","scan");
                    startActivity(intent);
                    finish();
                }
            }


        }else{
            if(tag == 0){
                logOut(text);
            }else {
               /* Intent resultIntent = new Intent();
                bundle.putString("result", rawResult+"_"+tag);
                resultIntent.putExtras(bundle);
                this.setResult(RESULT_OK, resultIntent);
                CaptureActivity.this.finish();*/

                if(!isLogin){
                    Intent intent = new Intent(MyApp.getInstance(), LoginActivity2.class);
                    startActivity(intent);
                }else {
                    String[] split = s.split("_");
                    String s1 = split[1];
                    if(s1.equals("1")){
                        yanqianData(split[0]);

                    }else {
                        String isOpen = (String) SPUtils.get(MyApp.getInstance(), "isOpen", "");
                        if(isOpen.equals("") || isOpen.equals("1")){
                            Intent intent = new Intent(MyApp.getInstance(), BaseWebCloseActivity.class);
                            intent.putExtra("appUrl",rawResult);
                            intent.putExtra("scan","scan");
                            startActivity(intent);
                            finish();
                        }else {
                            Intent intent = new Intent(MyApp.getInstance(), BaseWebActivity4.class);
                            intent.putExtra("appUrl",rawResult);
                            intent.putExtra("scan","scan");
                            startActivity(intent);
                            finish();
                        }
                    }



                }

            }

        }

    }

    private void initCamera(SurfaceHolder surfaceHolder) {
        if (surfaceHolder == null) {
            throw new IllegalStateException("No SurfaceHolder provided");
        }
        if (cameraManager.isOpen()) {
            Log.w(TAG,
                    "initCamera() while already open -- late SurfaceView callback?");
            return;
        }
        try {
            cameraManager.openDriver(surfaceHolder);
            // Creating the handler starts the preview, which can also throw a
            // RuntimeException.
            if (handler == null) {
                handler = new CaptureActivityHandler(this, cameraManager, DecodeThread.ALL_MODE);
            }

            initCrop();
            //initCrop2();
        } catch (IOException ioe) {
            Log.w(TAG, ioe);
           // displayFrameworkBugMessageAndExit();
            showAlertDialog(this);
        } catch (RuntimeException e) {
            // Barcode Scanner has seen crashes in the wild of this variety:
            // java.?lang.?RuntimeException: Fail to connect to camera service
            Log.w(TAG, "Unexpected error initializing camera", e);
            //displayFrameworkBugMessageAndExit();
            showAlertDialog(this);
        }
    }

    private void displayFrameworkBugMessageAndExit() {
        // camera error
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(getString(R.string.jwstr_prompt));
        builder.setMessage(getString(R.string.jwstr_camera_error));
        builder.setPositiveButton(getString(R.string.jwstr_confirm), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

        });
        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {

            @Override
            public void onCancel(DialogInterface dialog) {
                finish();
            }
        });
        builder.show();
    }

    private MyDialog m_Dialog1;
    private void showAlertDialog(final Object object) {

        m_Dialog1 = new MyDialog(this, R.style.dialogdialog);
        Window window = m_Dialog1.getWindow();
        window.setGravity(Gravity.CENTER);

        View view = LayoutInflater.from(this).inflate(R.layout.dialog_camera, null);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);
        m_Dialog1.setContentView(view, layoutParams);
        m_Dialog1.show();
        TextView optenTv = (TextView) m_Dialog1.findViewById(R.id.open);
        TextView tv_content = (TextView) m_Dialog1.findViewById(R.id.tv_content);
        TextView cancel = (TextView) m_Dialog1.findViewById(R.id.cancel);
        TextView help = (TextView) m_Dialog1.findViewById(R.id.help);
        tv_content.setText("您的权限尚未开启，请开启相机权限");
        optenTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                goSetting(object);
                m_Dialog1.cancel();

            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_Dialog1.cancel();
                finish();
            }
        });

        help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_Dialog1.cancel();
                Intent intent  = new Intent(CaptureActivity.this, BaseWebActivity4.class);
                intent.putExtra("appUrl",UrlRes.HOME_URL+"/portal/portal-app/qxyd/qxkqyd_android.html");
                startActivity(intent);

            }
        });
    }
    public static final int SETTINGS_REQ_CODE = 99;
    private void goSetting(Object object) {

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        if (object instanceof Activity) {
            ((Activity) object).startActivityForResult(intent, SETTINGS_REQ_CODE);
        } else if (object instanceof Fragment) {
            ((Fragment) object).startActivityForResult(intent, SETTINGS_REQ_CODE);
        } else if (object instanceof android.app.Fragment) {
            ((android.app.Fragment) object).startActivityForResult(intent, SETTINGS_REQ_CODE);
        }
    }

    public void restartPreviewAfterDelay(long delayMS) {
        if (handler != null) {
            handler.sendEmptyMessageDelayed(R.id.jwstr_restart_preview, delayMS);
        }
    }

    public Rect getCropRect() {
        return mCropRect;
    }

    /**
     * 初始化截取的矩形区域
     */
    private void initCrop() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        scanCropView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = scanCropView.getWidth();
        int cropHeight = scanCropView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    /**
     * 初始化六边形截取的矩形区域
     */
    private void initCrop2() {
        int cameraWidth = cameraManager.getCameraResolution().y;
        int cameraHeight = cameraManager.getCameraResolution().x;

        /** 获取布局中扫描框的位置信息 */
        int[] location = new int[2];
        flowLineView.getLocationInWindow(location);

        int cropLeft = location[0];
        int cropTop = location[1] - getStatusBarHeight();

        int cropWidth = flowLineView.getWidth();
        int cropHeight = flowLineView.getHeight();

        /** 获取布局容器的宽高 */
        int containerWidth = scanContainer.getWidth();
        int containerHeight = scanContainer.getHeight();

        /** 计算最终截取的矩形的左上角顶点x坐标 */
        int x = cropLeft * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的左上角顶点y坐标 */
        int y = cropTop * cameraHeight / containerHeight;

        /** 计算最终截取的矩形的宽度 */
        int width = cropWidth * cameraWidth / containerWidth;
        /** 计算最终截取的矩形的高度 */
        int height = cropHeight * cameraHeight / containerHeight;

        /** 生成最终的截取的矩形 */
        mCropRect = new Rect(x, y, width + x, height + y);
    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void onClick(View v) {
        switch (Integer.parseInt(v.getTag().toString())) {
            case 123:
                Intent resultIntent = new Intent();
                this.setResult(RESULT_CANCELED, resultIntent);
                CaptureActivity.this.finish();
                break;
            case 124:
                if (captureType == 0) {
                    Intent mullt = new Intent();
                    this.setResult(RESULT_MULLT, mullt);
                    CaptureActivity.this.finish();
                } else {
//                    Intent add = new Intent(this, AddContactActivity.class);
//                    CaptureActivity.this.startActivity(add);
//                    CaptureActivity.this.finish();
                    Intent resultInten1t = new Intent();
                    this.setResult(RESULT_CANCELED, resultInten1t);
                    CaptureActivity.this.finish();
                }
                break;
            case 125:
                //打开相册选择图片
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                intent.setType("image/*");
                startActivityForResult(intent, CODE_GALLERY_REQUEST);
                break;
            case 128:
                //打开相册选择图片
                Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
                intent1.addCategory(Intent.CATEGORY_OPENABLE);
                intent1.setType("image/*");
                startActivityForResult(intent1, CODE_GALLERY_REQUEST);
                break;
            case 130:
                //打开相册选择图片
                Intent intent2 = new Intent(Intent.ACTION_GET_CONTENT);
                intent2.addCategory(Intent.CATEGORY_OPENABLE);
                intent2.setType("image/*");
                startActivityForResult(intent2, CODE_GALLERY_REQUEST);
                break;
            case 131:
                Intent resultIntent131 = new Intent();
                this.setResult(RESULT_CANCELED, resultIntent131);
                CaptureActivity.this.finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == CODE_GALLERY_REQUEST) {
            String picPath = SelectAlbumUtils.getPicPath(this, data);
            Result result = DecodeBitmap.scanningImage(picPath);
            if (result == null) {
                //Toast.makeText(this, getString(R.string.jwstr_pic_no_qrcode), Toast.LENGTH_SHORT).show();
                scanPreview.setVisibility(View.INVISIBLE);

                showMyDialog();
            } else {
                beepManager.playBeepSoundAndVibrate();
                String scanResult = DecodeBitmap.parseReuslt(result.toString());
                scanDeviceSuccess(scanResult, new Bundle());
            }
        }
    }

    private MyDialog m_Dialog3;
    private void showMyDialog() {

        m_Dialog3 = new MyDialog(this, R.style.Plane_Dialog);
        Window window = m_Dialog3.getWindow();

        //window.setGravity(Gravity.CENTER);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_camara_show_error, null);
        //RelativeLayout rl_sure = view.findViewById(R.id.rl_sure);

        int width = ScreenSizeUtils.getWidth(this);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(layoutParams);
        RelativeLayout rl_bg = view.findViewById(R.id.rl_bg);

        m_Dialog3.setContentView(view, layoutParams);
        m_Dialog3.show();
        m_Dialog3.setCanceledOnTouchOutside(true);
        rl_bg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_Dialog3.dismiss();
                scanPreview.setVisibility(View.VISIBLE);
            }
        });
        m_Dialog3.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                scanPreview.setVisibility(View.VISIBLE);
            }
        });

    }




}