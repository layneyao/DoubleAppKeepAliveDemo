package com.itman.boot.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;

import androidx.annotation.Nullable;

import com.itman.boot.APP;
import com.itman.boot.utils.LauncherUtils;


public class Service1 extends Service {

    private static Context mContext;
    private static boolean checkApp = true;
    private static long DELAY = 5000;
    private static boolean isRun = false;

    public static void setCheckApp() {
//        checkApp = LogWriteManagement.getInstance().getCheckApp();
        if (checkApp) {
            init();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mContext = this;
//        checkApp = LogWriteManagement.getInstance().getCheckApp();
//        stopService(new Intent(this, ChargeSpotService.class));
//        startService(new Intent(this, ChargeSpotService.class));
        if (checkApp) {
            init();
        }
    }


    public static void init() {
        if (isRun)
            return;
        new Thread() {
            @Override
            public void run() {
                super.run();
                try {

                    while (checkApp) {
                        sleep(DELAY);

                        if (checkApp) {
                            handler.removeMessages(0);
                            handler.sendEmptyMessage(0);
                        }
                    }
                    isRun = false;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        if (checkApp)
            isRun = true;
    }

    static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    boolean isStart = LauncherUtils.isFrontDesk(APP.getInstance());
                    Log.e("csTest","主app是否在前台："+isStart);
                    if (!isStart) {
                        boolean isAppInstalled = LauncherUtils.isAppInstalled();
                        if (checkApp && isAppInstalled && LauncherUtils.isOpen()) {
                            Bundle extras = new Bundle();
                            String data = "============= 1 检测到APP进程被退出，尝试重启APP =============";
                            Log.e("csTest","***** 1 检测到APP进程被退出，尝试重启APP *****");
                            extras.putString("RESTART", data);
                            Log.v("csTest", data);
                            LauncherUtils.startCSActivity(mContext, extras);
                        }
                    }
                    Intent intent = new Intent("CS_Broadcast_01");
                    Log.e("csTest","boot 发送保活广播");
                    mContext.sendBroadcast(intent);
                    break;
            }
        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

}
