package io.cordova.zhqy.activity;

import io.cordova.zhqy.R;
import io.cordova.zhqy.utils.BaseActivity3;

public class DialogActivity extends BaseActivity3 {
    @Override
    protected int getResourceId() {
        return R.layout.activity_show_dialog;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.bottom_silent,R.anim.bottom_out);
    }
}
