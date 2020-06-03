package com.itman.doubleappkeepalivedemo.utils;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * @author Created by HeCh.
 * @time 2018/8/7 0007 14:19
 * Description:
 */

public class AssetsUtil {

    /**
     * 读取Assets文件夹中的图片资源
     *
     * @param context
     * @param fileName 图片名称
     * @return
     */
    public static byte[] getImageFromAssets(Context context, String fileName) {
        Bitmap image = null;
        byte[] bytes = new byte[0];
        AssetManager am = context.getResources().getAssets();
        try {
            InputStream is = am.open(fileName);
            int lenght = is.available();
            bytes = new byte[lenght];
            is.read(bytes);
//            image = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bytes;
    }

    /**
     * copy到sdCard
     *
     * @param context
     * @param srcFolder
     * @param sdPath
     */
    public static void copyToSdCard(Context context, String srcFolder, String sdPath) {
        AssetManager assets = context.getResources().getAssets();
        try {
            File defaultAdFolder = new File(sdPath);
            if (!defaultAdFolder.exists()) {
                defaultAdFolder.mkdirs();
            }
            String[] list = assets.list(srcFolder);
            List<String> strings = Arrays.asList(list);
            Collections.sort(strings, new Comparator<String>() {
                public int compare(String str1, String str2) {
                    String suffix = str1.substring(str1.lastIndexOf("."));
                    if (".gif".equals(suffix)) {
                        return -1;
                    } else {
                        return 1;
                    }
                }
            });
            for (String fileName : strings) {
                String srcPath = srcFolder + File.separator + fileName;
                InputStream open = assets.open(srcPath);
                File file = new File(sdPath + fileName);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = open.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                open.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * copy到sdCard
     *
     * @param context
     * @param srcFolder
     * @param sdPath
     */
    public static void copyFileToSdCard(Context context, String srcFolder, String sdPath) {
        AssetManager assets = context.getResources().getAssets();
        try {
            File defaultAdFolder = new File(sdPath);
            if (!defaultAdFolder.exists()) {
                defaultAdFolder.mkdirs();
            }
            String[] list = assets.list(srcFolder);
            List<String> strings = Arrays.asList(list);
            for (String fileName : strings) {
                String srcPath = srcFolder + File.separator + fileName;
                InputStream open = assets.open(srcPath);
                File file = new File(sdPath + fileName);
                FileOutputStream fos = new FileOutputStream(file);
                byte[] buffer = new byte[1024];
                int byteCount;
                while ((byteCount = open.read(buffer)) != -1) {
                    fos.write(buffer, 0, byteCount);
                }
                fos.flush();
                open.close();
                fos.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
