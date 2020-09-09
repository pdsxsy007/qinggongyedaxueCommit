package io.cordova.zhqy.activity;


import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import io.cordova.zhqy.R;
import io.cordova.zhqy.utils.BaseActivity2;


public class CAResultActivity extends BaseActivity2 {

    @BindView(R.id.iv_result)
    ImageView iv_result;
    @BindView(R.id.ll_bottom)
    LinearLayout ll_bottom;
    @BindView(R.id.tv_shijian)
    TextView tv_shijian;
    @BindView(R.id.tv_time)
    TextView tv_time;
    @BindView(R.id.tv_result)
    TextView tv_result;
    @BindView(R.id.tv_app_setting)
    ImageView tv_app_setting;
    @BindView(R.id.rl_show)
    RelativeLayout rl_show;
    @BindView(R.id.btn_show)
    Button btn_show;

    @Override
    protected int getResourceId() {
        return R.layout.activity_ca_result;
    }

    @Override
    protected void initView() {
        super.initView();

        String result = getIntent().getStringExtra("result");
        String certDn = getIntent().getStringExtra("certDn");
        String signTime = getIntent().getStringExtra("signTime");
        final String pdfurl = getIntent().getStringExtra("pdfurl");
        if(result.equals("0")){//成功
            iv_result.setImageResource(R.mipmap.icon_success);
            ll_bottom.setVisibility(View.VISIBLE);
            //tv_shijian.setText(""+certDn);
            //tv_time.setText(""+signTime);
            tv_result.setText("验证通过!");
            tv_result.setTextColor(Color.parseColor("#5CA143"));
            rl_show.setVisibility(View.VISIBLE);
            testText1(certDn,signTime);
        }else {
            iv_result.setImageResource(R.mipmap.icon_faild);
            ll_bottom.setVisibility(View.GONE);
            tv_result.setText("验证失败!");
            tv_result.setTextColor(Color.parseColor("#E57470"));
            tv_shijian.setText("当前签名无法识别，请认真核实");
            rl_show.setVisibility(View.GONE);
        }

        tv_app_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_show.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CAResultActivity.this, ShowPDFActivity.class);
                intent.putExtra("appUrl",pdfurl);
                startActivity(intent);
            }
        });
    }


    private void testText1(String posName,String time) {
        String text = "该文件由" + posName + "于"+time+"签发，请认真核对原文件内容是否一致";
        SpannableString span = new SpannableString(text);
        /*span.setSpan(new ForegroundColorSpan(Color.CYAN), 4, 4 + posName.length() + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new ForegroundColorSpan(Color.BLACK), text.length() - 5, text.length() - 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);*/
        span.setSpan(new ForegroundColorSpan(Color.BLACK), 4, 4 + posName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        span.setSpan(new StyleSpan(Typeface.BOLD), 4, 4 + posName.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        tv_shijian.setText(span);
        /**
         * 其中最后的参数flag：
         * Spanned.SPAN_EXCLUSIVE_EXCLUSIVE --- 不包含两端start和end所在的端点 (a,b)
         * Spanned.SPAN_EXCLUSIVE_INCLUSIVE --- 不包含端start，但包含end所在的端点 (a,b]
         * Spanned.SPAN_INCLUSIVE_EXCLUSIVE --- 包含两端start，但不包含end所在的端点 [a,b)
         * Spanned.SPAN_INCLUSIVE_INCLUSIVE --- 包含两端start和end所在的端点 [a,b]
         */
    }



}
