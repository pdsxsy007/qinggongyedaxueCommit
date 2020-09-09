package io.cordova.zhqy.activity;



import android.annotation.TargetApi;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.alibaba.fastjson.JSON;
import com.joanzapata.pdfview.PDFView;
import com.lidroid.xutils.exception.HttpException;
import com.lidroid.xutils.http.ResponseInfo;
import com.lidroid.xutils.http.callback.RequestCallBack;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.FileCallback;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.io.File;

import butterknife.BindView;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.bean.ServiceAppListBean;
import io.cordova.zhqy.utils.BaseActivity2;

/**
 * Created by Administrator on 2019/2/18 0018.
 */
@RequiresApi(api = Build.VERSION_CODES.KITKAT)
public class ShowPDFActivity extends BaseActivity2  {

    @BindView(R.id.pdfview)
    PDFView pdfview;
    @BindView(R.id.tv_app_setting)
    ImageView tv_app_setting;
    @Override
    protected int getResourceId() {
        return R.layout.activity_showpdf;
    }

    @Override
    protected void initView() {
        super.initView();

        String appUrl = getIntent().getStringExtra("appUrl");

        downloadPdf(appUrl);

        tv_app_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void downloadPdf(String appUrl) {

        try {
            com.lidroid.xutils.HttpUtils http = new com.lidroid.xutils.HttpUtils();
            http.download(appUrl,  Environment.getExternalStorageDirectory()+"/provisional.pdf", true, false, new RequestCallBack<File>() {
                @Override
                public void onStart() {
                }
                @Override
                public void onLoading(long total, long current, boolean isUploading) {
                }
                @Override
                public void onFailure(HttpException error, String msg) {
                }

                @Override
                public void onSuccess(ResponseInfo<File> responseInfo) {
                    File file = new File(Environment.getExternalStorageDirectory()+"/provisional.pdf");
                    readPdf(file);
                }
            });
        } catch (Exception e) {
            Log.d("error", e.toString());
        }
    }


    private void readPdf(File file) {
        pdfview.fromFile(file)
                .defaultPage(1)
                .load();
    }

    @Override
    protected void onPause() {
        super.onPause();
        File file = new File(Environment.getExternalStorageDirectory()+"/provisional.pdf");
        if (file.exists()) {
            file.delete();
        }

    }



}
