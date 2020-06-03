package com.itman.doubleappkeepalivedemo.utils;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import com.itman.doubleappkeepalivedemo.APP;

import java.io.File;
import java.util.List;

public class LauncherUtils {

    public static final String homePackName = "com.itman.doubleappkeepalivedemo";
    public static final String bootPackName = "com.itman.boot";
    public static final String className = "com.itman.boot.MainActivity";


    /**
     * 启动Chargespot app
     *
     * @param context
     */
    public static void startCSActivity(Context context) {
        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(homePackName);
        context.startActivity(LaunchIntent);
    }

    /**
     * 启动B app
     *
     * @param context
     */
    public static void startLauncherActivity(Context context) {
        /**
         * 副app设置了隐藏图标，直接获取意图是null，需要设置回显示状态才可唤起。
         */
        PackageManager packageManager = APP.getInstance().getPackageManager();
        ComponentName componentName = new ComponentName(bootPackName,className);
        packageManager.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DEFAULT,
                PackageManager.DONT_KILL_APP);

        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(bootPackName);
        context.startActivity(LaunchIntent);
    }

    /**
     * 启动B app
     *
     * @param context
     * @param extras
     */
    public static void startLauncherActivity(Context context, Bundle extras) {
        Intent LaunchIntent = context.getPackageManager().getLaunchIntentForPackage(bootPackName);
        LaunchIntent.putExtras(extras);
        context.startActivity(LaunchIntent);
    }


    /**
     * 判断应用B是否是在前台
     *
     * @param context
     * @return
     */
    public static boolean isFrontDesk(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(bootPackName)) {
                if (appProcess.importance != ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
                    return false;
                else
                    return true;
            }
        }
        return false;
    }


    /**
     * 应用是否安装
     */
    public static boolean isAppInstalled() {
        return new File("/data/data/" + bootPackName).exists();
    }


    /**
     * 发送消息
     *
     * @param context
     * @param event
     * @param msg
     */
    public static void sendMsg(Context context, String event, String msg) {
        Intent intent = new Intent("CS_Broadcast_02");
        intent.putExtra("event", event);
        intent.putExtra("content", msg);
        context.sendBroadcast(intent);
        Intent intent1 = new Intent("CS_Broadcast_03");
        intent1.putExtra("event", event);
        intent1.putExtra("content", msg);
        context.sendBroadcast(intent1);
    }


}
