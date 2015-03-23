package com.bupt.booktrade;

import android.app.Activity;
import android.app.Application;

import com.bupt.booktrade.entity.User;
import com.bupt.booktrade.utils.ActivityManagerUtils;

import cn.bmob.v3.BmobUser;


/**
 * Created by LiuYan on 2015/3/17.
 */
public class MyApplication extends Application {
    public static final String TAG = "MyApplication";
    public static MyApplication myApplication = null;

    public static MyApplication getMyApplication() {
        return myApplication;
    }

    public User getCurrentUser() {
        User user = BmobUser.getCurrentUser(myApplication, User.class);
        if (user != null) {
            return user;
        }
        return null;
    }

    @Override
    public void onCreate() {
        myApplication = this;
        super.onCreate();
    }

    public void addActivity(Activity activity) {
        ActivityManagerUtils.getInstance().addActivity(activity);
    }

    public void exit() {
        ActivityManagerUtils.getInstance().removeAllActivity();
    }

    public Activity getTopActivity() {
        return ActivityManagerUtils.getInstance().getTopActivity();
    }
}
