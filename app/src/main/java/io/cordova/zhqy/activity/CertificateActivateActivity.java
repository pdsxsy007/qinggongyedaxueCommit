package io.cordova.zhqy.activity;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import butterknife.BindView;
import io.cordova.zhqy.Main2Activity;
import io.cordova.zhqy.R;
import io.cordova.zhqy.UrlRes;
import io.cordova.zhqy.bean.LoginBean;
import io.cordova.zhqy.bean.NewStudentBean;
import io.cordova.zhqy.utils.AesEncryptUtile;
import io.cordova.zhqy.utils.BaseActivity;
import io.cordova.zhqy.utils.JsonUtil;
import io.cordova.zhqy.utils.MyApp;
import io.cordova.zhqy.utils.PermissionsUtil;
import io.cordova.zhqy.utils.SPUtils;
import io.cordova.zhqy.utils.ToastUtils;
import io.cordova.zhqy.utils.ViewUtils;
import io.cordova.zhqy.utils.fingerUtil.JsonUtils;

import static io.cordova.zhqy.utils.AesEncryptUtile.key;

public class CertificateActivateActivity extends BaseActivity {

    @BindView(R.id.rl_sign)
    RelativeLayout rl_sign;

    @BindView(R.id.et_name)
    EditText et_name;

    @BindView(R.id.et_cardnum)
    EditText et_cardnum;

    @Override
    protected int getResourceId() {
        return R.layout.activity_zhengshu_jihuo;
    }

    @Override
    protected void initListener() {
        super.initListener();
        rl_sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String name = et_name.getText().toString().trim();
                String cardNum = et_cardnum.getText().toString().trim();
                if(name.equals("") || cardNum.equals("")){
                    ToastUtils.showToast(CertificateActivateActivity.this,"请输入姓名或身份证号码!");
                    return;
                }

                addData(name,cardNum);

            }
        });
    }

    private void addData(String name, String cardNum) {
        ViewUtils.createLoadingDialog(this);
        String userId = (String) SPUtils.get(MyApp.getInstance(), "userId", "");
        try {
            String memberIdNumber = AesEncryptUtile.encrypt(cardNum,key) ;
            OkGo.<String>post(UrlRes.HOME_URL + UrlRes.getAuthCodeUrl)
                    .tag(this)
                    .params("msspid","")
                    .params("memberIdNumber",memberIdNumber)
                    .params("memberId",userId)
                    .params("memberNickname",name)
                    .execute(new StringCallback() {
                        @Override
                        public void onSuccess(Response<String> response) {
                            Log.e("获取激活码",response.body());
                            ViewUtils.cancelLoadingDialog();
                            LoginBean loginBean = JsonUtil.parseJson(response.body(),LoginBean.class);
                            boolean success = loginBean.isSuccess();
                            if(success){
                                Intent intent = new Intent(CertificateActivateActivity.this,CertificateActivateNextActivity.class);
                                startActivity(intent);
                            }else {
                                ToastUtils.showToast(CertificateActivateActivity.this,loginBean.getMsg());
                            }


                        }
                        @Override
                        public void onError(Response<String> response) {
                            super.onError(response);
                            Log.e("onError",response.body());
                            ViewUtils.cancelLoadingDialog();
                        }
                    });

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
