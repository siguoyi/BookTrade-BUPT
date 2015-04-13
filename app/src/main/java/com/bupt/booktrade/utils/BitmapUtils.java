package com.bupt.booktrade.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LiuYan on 2015/4/13.
 */
public class BitmapUtils {

    private static Bitmap bitmap;
    private static final String TAG = "BitmapUtils";

    /**
     * 根据目标图片大小计算采样率
     * @param options   BitmapFactory.Options
     * @param reqHeight 目标图片高度
     * @param reqWidth  目标图片宽度
     * @return  采样率
     */
    private static int getSampleSize(BitmapFactory.Options options, int reqHeight, int reqWidth) {
        int height = options.outHeight;
        int width = options.outWidth;
        int sampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            int heightRatio = (int) (height / (float) reqHeight);
            int widthRatio = (int) (width / (float) reqWidth);
            sampleSize = (heightRatio < widthRatio) ? heightRatio : widthRatio;
        }
        return sampleSize;
    }

    /**
     * 从文件压缩图片
     * @param srcPath   图片路径
     * @return  压缩后图片
     */
    public static Bitmap compressBitmapFromFile(String srcPath, int reqHeight, int reqWidth) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;//只读边,不读内容
        BitmapFactory.decodeFile(srcPath, options);
        int sampleSize = getSampleSize(options, reqHeight, reqWidth);

        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;//该模式是默认的,可不设
        options.inPurgeable = true;// 同时设置才会有效
        options.inInputShareable = true;//当系统内存不够时候图片自动被回收
        bitmap = BitmapFactory.decodeFile(srcPath, options);
        return bitmap;
    }

    public static String saveToSdCard(Context mContext, Bitmap bitmap){
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String files =CacheUtils.getCacheDirectory(mContext, true, "pic") + timeStamp+".jpg";
        File file=new File(files);
        try {
            FileOutputStream out=new FileOutputStream(file);
            if(bitmap.compress(Bitmap.CompressFormat.JPEG, 80, out)){
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        LogUtils.i(TAG, file.getAbsolutePath());
        return file.getAbsolutePath();
    }


    public static void recyle(){
        if (bitmap.isRecycled() || bitmap == null) {
            return;
        } else {
            bitmap.recycle();
        }
    }
}
