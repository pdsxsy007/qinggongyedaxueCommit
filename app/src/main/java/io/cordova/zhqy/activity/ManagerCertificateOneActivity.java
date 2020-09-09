package io.cordova.zhqy.activity;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import butterknife.BindView;
import io.cordova.zhqy.R;
import io.cordova.zhqy.utils.BaseActivity;
import io.cordova.zhqy.utils.DensityUtil;
import io.cordova.zhqy.utils.ScreenSizeUtils;
import io.cordova.zhqy.widget.MyDialog;

public class ManagerCertificateOneActivity extends BaseActivity implements View.OnClickListener {
    @BindView(R.id.rl_jihuo)
    RelativeLayout rl_jihuo;

    @BindView(R.id.rl_delete)
    RelativeLayout rl_delete;

    @Override
    protected int getResourceId() {
        return R.layout.activity_zhengshu_one;
    }

    @Override
    protected void initListener() {
        super.initListener();

        rl_jihuo.setOnClickListener(this);
        rl_delete.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()){
            case R.id.rl_jihuo://证书激活
                Intent intent = new Intent(ManagerCertificateOneActivity.this,CertificateActivateActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_delete://证书删除
                deleteCertificate();
                break;
        }
    }

    private MyDialog m_Dialog;
    private void deleteCertificate() {
        m_Dialog = new MyDialog(this,R.style.dialogdialog);
        Window window = m_Dialog.getWindow();
        window.setGravity(Gravity.CENTER);
        View view = View.inflate(this,R.layout.dialog_certificate_delete, null);
        RelativeLayout rl_sure = view.findViewById(R.id.rl_sure);
        RelativeLayout rl_sure1 = view.findViewById(R.id.rl_sure1);

        int width = ScreenSizeUtils.getWidth(this);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(width - DensityUtil.dip2px(this,24),
                LinearLayout.LayoutParams.WRAP_CONTENT);
        m_Dialog.setContentView(view, layoutParams);
        m_Dialog.show();
        m_Dialog.setCanceledOnTouchOutside(true);
        rl_sure1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_Dialog.dismiss();
            }
        });
        rl_sure.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                m_Dialog.dismiss();
                //netExit();
            }
        });
    }
}
