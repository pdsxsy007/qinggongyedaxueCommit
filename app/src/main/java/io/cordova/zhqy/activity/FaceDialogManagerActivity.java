package io.cordova.zhqy.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Paint;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import butterknife.BindView;
import io.cordova.zhqy.R;
import io.cordova.zhqy.face.view.RefreshProgress;
import io.cordova.zhqy.face2.FaceView;
import io.cordova.zhqy.utils.BaseActivity3;
import io.cordova.zhqy.utils.BitmapUtils;
import io.cordova.zhqy.utils.FinishActivity;
import io.cordova.zhqy.utils.SPUtils;


/**
 * Created by Administrator on 2019/7/4 0004.
 */

public class FaceDialogManagerActivity extends BaseActivity3 {

    io.cordova.zhqy.face2.CameraView cameraView;
    FaceView faceView;
    Bitmap fullBitmap;

    private SensorManager sensorManager;
    private Sensor sensor;
    private FaceDialogManagerActivity.MySensorListener mySensorListener;
    private int sensorBright = 0;
    private ImageView iv;

    @BindView(R.id.iv_wai)
    RefreshProgress iv_wai;

    @BindView(R.id.iv_nei)
    RefreshProgress iv_nei;

    @BindView(R.id.iv_close)
    ImageView iv_close;

    @BindView(R.id.tv_pin)
    TextView tv_pin;

    @Override
    protected int getResourceId() {
        return R.layout.activity_face_dialog;
    }
    long l0;
    @Override
    protected void initView() {
        super.initView();
        if(!hasFrontCamera()) {
            Toast.makeText(this, "没有前置摄像头", Toast.LENGTH_SHORT).show();
            return ;
        }
        iv_wai.Animation2();
        iv_nei.Animation();
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT);
        mySensorListener = new MySensorListener();
        sensorManager.registerListener(mySensorListener, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        cameraView = (io.cordova.zhqy.face2.CameraView) findViewById(R.id.camera_view);
        faceView = (FaceView) findViewById(R.id.face_view);
        iv = (ImageView) findViewById(R.id.iv);
        //cameraView.setFaceView(faceView);
        cameraView.setOnFaceDetectedListener(new io.cordova.zhqy.face2.CameraView.OnFaceDetectedListener() {
            @Override
            public void onFaceDetected(Bitmap bm) {

                Log.e("test","检测2");
                //检测到人脸后的回调方法
                SPUtils.put(getApplicationContext(),"isloading2","112");
                finish();
                fullBitmap = bm;
                Message message = new Message();
                message.obj = fullBitmap;
                if(handler !=null){
                    handler.sendMessage(message);
                }




              /*  try {
                    saveMyBitmap(scaledBitmap,"test");
                } catch (IOException e) {
                    e.printStackTrace();
                }*/
            }
        });

          iv_close.setOnClickListener(new View.OnClickListener() {
              @Override
              public void onClick(View view) {
                  finish();
                  handler = null;
                  SPUtils.put(getApplicationContext(),"isloading2","");
              }
          });


        tv_pin.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG );
        tv_pin.setText(Html.fromHtml("<u>"+"使用PIN码验证"+"<u/>"));
        tv_pin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(FaceDialogManagerActivity.this,CertificateActivateNextTwoActivity.class);
                intent.putExtra("title","使用PIN码验证");
                intent.putExtra("type","1");
                startActivity(intent);
                FinishActivity.addActivity(FaceDialogManagerActivity.this);

            }
        });
    }
    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {

            super.handleMessage(msg);

            fullBitmap = (Bitmap) msg.obj;

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    Bitmap scaledBitmap = BitmapUtils.compressByWidth(fullBitmap, 150);
                    String s = bitmapToBase64(scaledBitmap);

                     Log.e("人脸",s+"");
                    SPUtils.put(FaceDialogManagerActivity.this,"bitmap",s);
                    Intent intent = new Intent();
                    intent.setAction("faceDialogManager");
                    intent.putExtra("FaceActivity","FaceActivity");
                    sendBroadcast(intent);
                }
            }.start();


        }
    };

    private class MySensorListener implements SensorEventListener {

        @Override
        public void onSensorChanged(SensorEvent sensorEvent) {
            //光线传感器亮度改变
            sensorBright = (int) sensorEvent.values[0];
        }

        @Override
        public void onAccuracyChanged(Sensor sensor, int i) {

        }
    }


    /**
     * 判断是否有前置摄像
     * @return
     */
    @SuppressLint("NewApi")
    public static boolean hasFrontCamera(){
        Camera.CameraInfo info = new Camera.CameraInfo();
        int count = Camera.getNumberOfCameras();
        for(int i = 0; i < count; i++){
            Camera.getCameraInfo(i, info);
            if(info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT){
                return true;
            }
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        sensorManager.unregisterListener(mySensorListener);
    }


    /**
     * bitmap转为base64
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {

        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, baos);

                baos.flush();
                baos.close();

                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }


}
