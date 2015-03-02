package com.bupt.booktrade.utils;

import android.content.Context;
import android.widget.Toast;


public class ToastUtils extends Toast {

    /**
     * Construct an empty Toast object.  You must call {@link #setView} before you
     * can call {@link #show}.
     *
     * @param context The context to use.  Usually your {@link android.app.Application}
     *                or {@link android.app.Activity} object.
     */
    public ToastUtils(Context context) {
        super(context);
    }

    private static Toast mToast;


    public static void showToast(Context context, int resId, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, resId, duration);
        } else {
            mToast.setText(resId);
        }
        mToast.show();
    }

    public static void showToast(Context context, String text, int duration) {
        if (mToast == null) {
            mToast = Toast.makeText(context, text, duration);
        } else {
            mToast.setText(text);
        }
        mToast.show();
    }

    public static void clearToast() {
        mToast.cancel();
    }
}

