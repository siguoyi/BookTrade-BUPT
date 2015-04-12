package com.bupt.booktrade.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.bupt.booktrade.MyApplication;

/**
 * Created by LiuYan on 2015/3/17.
 */
public class BaseActivity extends FragmentActivity {
    //protected static String TAG;
    protected MyApplication mMyApplication;
    protected Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TAG = this.getClass().getSimpleName();
        mContext = this;
        if (null == mMyApplication) {
            mMyApplication = MyApplication.getMyApplication();
        }
        mMyApplication.addActivity(this);
    }
}
