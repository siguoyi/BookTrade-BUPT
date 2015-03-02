package com.bupt.booktrade.utils;

import android.content.Context;
import android.widget.Toast;


public class ToastUtils{
	
	private static Toast mToast;

	public static void showToast(Context context, int msg, int duration) {
	
		if (mToast == null) {
			mToast = Toast.makeText(context, msg, duration);
		} else {
			mToast.setText(msg);
		}
		mToast.show();
	}
    public static void showToast(Context context, String msg, int duration) {

        if (mToast == null) {
            mToast = Toast.makeText(context, msg, duration);
        } else {
            mToast.setText(msg);
        }
        mToast.show();
    }
	public static void clearToast(){
		mToast.cancel();
	}
}

