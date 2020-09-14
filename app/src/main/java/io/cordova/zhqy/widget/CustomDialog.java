package io.cordova.zhqy.widget;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;

import io.cordova.zhqy.R;

public class CustomDialog extends Dialog {

    public CustomDialog(@NonNull Context context, int layoutId) {
        super(context, R.style.custom_dialog);
        setContentView(layoutId);
    }
}
