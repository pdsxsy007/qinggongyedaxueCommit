package io.cordova.zhqy.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;

public class MyDialog extends Dialog {

	public MyDialog(Context context, int theme) {
		super(context, theme);

	}

	protected MyDialog(Context context, boolean cancelable,
                       OnCancelListener cancelListener) {
		super(context, cancelable, cancelListener);

	}

	public MyDialog(Context context) {
		super(context);

	}
}
