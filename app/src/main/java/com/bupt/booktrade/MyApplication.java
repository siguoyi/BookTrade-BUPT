package com.bupt.booktrade;

import android.app.Activity;
import android.app.Application;

import com.bupt.booktrade.entity.User;
import com.bupt.booktrade.utils.ActivityManagerUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.UsingFreqLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;

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
        setUpImageLoader();
        super.onCreate();
    }

    private void setUpImageLoader() {
        File cacheDir = StorageUtils.getOwnCacheDirectory(getApplicationContext(), "imageloader/Cache");

        ImageLoaderConfiguration config = new ImageLoaderConfiguration
                .Builder(myApplication)
                .memoryCacheExtraOptions(480, 800) // max width, max height，即保存的每个缓存文件的最大长宽
                .threadPoolSize(3)//线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
                .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 * 1024)) // You can pass your own memory cache implementation/你可以通过自己的内存缓存实现
                .memoryCacheSize(2 * 1024 * 1024)
                .discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
                .discCacheSize(50 * 1024 * 1024)
                .discCacheFileNameGenerator(new Md5FileNameGenerator())//将保存的时候的URI名称用MD5 加密
                .tasksProcessingOrder(QueueProcessingType.LIFO)
                .discCacheFileCount(100) //缓存的文件数量
                .discCache(new UnlimitedDiscCache(cacheDir))//自定义缓存路径
                .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
                .imageDownloader(new BaseImageDownloader(myApplication, 5 * 1000, 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)超时时间
                .writeDebugLogs() // Remove for release app
                .build();//开始构建
        // Initialize ImageLoader with configuration.

        ImageLoader.getInstance().init(config);//全局初始化此配置
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
