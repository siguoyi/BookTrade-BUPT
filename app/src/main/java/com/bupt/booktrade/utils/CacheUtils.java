package com.bupt.booktrade.utils;

import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static android.os.Environment.MEDIA_MOUNTED;

public class CacheUtils {

    private static final String TAG = "CacheUtils";
    private static final String EXTERNAL_STORAGE_PERMISSION = "android.permission.WRITE_EXTERNAL_STORAGE";
    private static ArrayList<File> filesInCache = new ArrayList<>();

    private static boolean hasExternalStoragePermission(Context context) {
        int perm = context.checkCallingOrSelfPermission(EXTERNAL_STORAGE_PERMISSION);
        return perm == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 获取/data/data/files目录
     *
     * @param context
     * @return
     */
    public static File getFileDirectory(Context context) {
        File appCacheDir = null;
        if (appCacheDir == null) {
            appCacheDir = context.getFilesDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/files/";
            appCacheDir = new File(cacheDirPath);
        }
        filesInCache.add(appCacheDir);
        return appCacheDir;
    }


    public static File getCacheDirectory(Context context, boolean preferExternal, String dirName) {
        File appCacheDir = null;
        if (preferExternal && MEDIA_MOUNTED
                .equals(Environment.getExternalStorageState()) && hasExternalStoragePermission(context)) {
            appCacheDir = getExternalCacheDir(context, dirName);
        }
        if (appCacheDir == null) {
            appCacheDir = context.getCacheDir();
        }
        if (appCacheDir == null) {
            String cacheDirPath = "/data/data/" + context.getPackageName() + "/cache/";
            LogUtils.w("Can't define system cache directory! '%s' will be used.", cacheDirPath);
            appCacheDir = new File(cacheDirPath);
        }
        filesInCache.add(appCacheDir);
        //LogUtils.i(TAG, appCacheDir.getAbsolutePath());
        return appCacheDir;
    }


    private static File getExternalCacheDir(Context context, String dirName) {
        File dataDir = new File(new File(Environment.getExternalStorageDirectory(), "Android"), "data");
        File appCacheDir2 = new File(new File(dataDir, context.getPackageName()), "cache");
        File appCacheDir = new File(appCacheDir2, dirName);
        if (!appCacheDir.exists()) {
            if (!appCacheDir.mkdirs()) {
                LogUtils.w(TAG, "Unable to create external cache directory");
                return null;
            }
            try {
                new File(appCacheDir, ".nomedia").createNewFile();
            } catch (IOException e) {
                LogUtils.i(TAG, "Can't create \".nomedia\" file in application external cache directory");
            }
        }
        filesInCache.add(appCacheDir2);
        return appCacheDir;
    }

    public static void deleteFilesInCache() {
        if (filesInCache == null) {
            return;
        } else {
            for (File file : filesInCache) {
                if (!file.exists()) continue;
                if (file.isFile()) {
                    file.delete();
                } else if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        if (!files[i].exists()) continue;
                        if (files[i].isFile()) {
                            files[i].delete();
                        }
                    }
                }
            }
        }
        filesInCache.clear();
    }

    /**
     * in KB
     *
     * @return KB
     */
    public static long getCacheSize() {
        long sum = 0;
        if (filesInCache == null) {
            return 0;
        } else {
            for (File file : filesInCache) {

                if (!file.exists()) continue;
                if (file.isFile()) {
                    sum += file.length();
                    //LogUtils.i(TAG, file.getAbsolutePath() + "isFile");
                } else if (file.isDirectory()) {
                    File[] files = file.listFiles();
                    for (int i = 0; i < files.length; i++) {
                        if (!files[i].exists()) continue;
                        if (files[i].isFile()) {
                            //LogUtils.i(TAG, file.getAbsolutePath() + "isDir");
                            sum += file.length();
                        }
                    }
                }
            }
        }
        return sum / 1024;
    }
}
