package com.itman.doubleappkeepalivedemo.management;

import android.os.Handler;
import android.util.Log;

import com.itman.doubleappkeepalivedemo.APP;
import com.itman.doubleappkeepalivedemo.utils.LauncherUtils;
import com.itman.doubleappkeepalivedemo.utils.TimeUtils;


/**
 * 检测APP通讯 - 副APP是否正常运行
 */
public class CheckAppManagement {

    private static volatile CheckAppManagement instance;

    private Handler handler = new Handler();

    private long lastCheckTime = 0;//最后一次通信时间

    private long CHECK_TIME = 5000 * 2;//检测时间间隔 2倍超时时间


    private boolean isAppCheck = true;

    /**
     * 单例
     *
     * @return
     */
    public static synchronized CheckAppManagement getInstance() {
        if (instance == null) {
            synchronized (CheckAppManagement.class) {
                if (instance == null) {
                    instance = new CheckAppManagement();
                    instance.init();
                }
            }
        }
        return instance;
    }

    /**
     * 更新最后通信时间
     *
     * @param time
     */
    public void upLastCheckTime(long time) {
        lastCheckTime = time;
    }

    /**
     * 检测副app是否运行
     */
    private void init() {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                long t = System.currentTimeMillis();
                if (isAppCheck) {
                    if (lastCheckTime > 0) {
                        if (t - lastCheckTime > CHECK_TIME) {
                            if (LauncherUtils.isAppInstalled()) {
                                if (isAppCheck && !LauncherUtils.isFrontDesk(APP.getInstance())) {
                                    Log.e("csTest", TimeUtils.getTimeTamp() + "&&&&&&&&&&&& 检测到副APP进程被退出，尝试重启APP &&&&&&&&&&&&");
                                    LauncherUtils.startLauncherActivity(APP.getInstance());
                                }
                            } else
                                Log.e("csTest", TimeUtils.getTimeTamp() + "&&&&&&&&&&&& 应用未安装！！！ &&&&&&&&&&&&");
                        }
                    } else {
                        if (LauncherUtils.isAppInstalled()) {
                            if (isAppCheck && !LauncherUtils.isFrontDesk(APP.getInstance())) {
                                Log.e("csTest", TimeUtils.getTimeTamp() + "&&&&&&&&&&&& 检测到副APP进程被退出，尝试重启APP &&&&&&&&&&&&");
                                LauncherUtils.startLauncherActivity(APP.getInstance());
                            }
                        } else
                            Log.e("csTest", TimeUtils.getTimeTamp() + "&&&&&&&&&&&& 应用未安装！！！ &&&&&&&&&&&&");
                    }
                }
                handler.postDelayed(this, CHECK_TIME);
            }
        }, CHECK_TIME);
    }

}
