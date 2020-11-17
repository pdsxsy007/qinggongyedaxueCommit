package io.cordova.zhqy.activity;

import android.content.Intent;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.io.File;

import butterknife.BindView;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.bean.BaseBean;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.BaseActivity;
import io.cordova.zhqy.utils.FinishActivity;
import io.cordova.zhqy.utils.JsonUtil;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.ViewUtils;

import static io.cordova.zhqy.utils.AesEncryptUtile.key;

public class FileManagerActivity extends BaseActivity {


    @Override
    protected int getResourceId() {
        return R.layout.activity_zhengshu_jihuo;
    }

    @Override
    protected void initListener() {
        super.initListener();

        String destPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                .getAbsolutePath() + File.separator;
        File path = new File(destPath);
        File[] files = path.listFiles();
        for (int i = 0; i < files.length; i++) {
            String name = files[i].getName();
            Log.e("name",name);
        }
    }


}
