package com.itman.doubleappkeepalivedemo.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {


    /**
     * 获取系统当前时间
     * @return
     */
    public static long getCurrentTime() {
        long currentTimeMillis = System.currentTimeMillis();
        return currentTimeMillis;
    }

    /**
     * 获取时间戳
     * @return
     */
    public static String getTimeTamp() {
        Date data = new Date(getCurrentTime());
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String timeTamp = formatter.format(data);
        return timeTamp;
    }
}
