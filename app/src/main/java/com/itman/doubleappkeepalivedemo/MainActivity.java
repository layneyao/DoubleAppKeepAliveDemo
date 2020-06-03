package com.itman.doubleappkeepalivedemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Environment;

import com.itman.doubleappkeepalivedemo.utils.AssetsUtil;
import com.itman.doubleappkeepalivedemo.utils.CommandUtil;
import com.itman.doubleappkeepalivedemo.utils.LauncherUtils;

public class MainActivity extends AppCompatActivity {

    String mBootFolder=Environment.getExternalStorageDirectory().getAbsolutePath() + "/BOOT/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean isAppInstalled = LauncherUtils.isAppInstalled();
        if (!isAppInstalled) {
            //安装apk
            new Thread() {
                @Override
                public void run() {
                    super.run();
                    AssetsUtil.copyFileToSdCard(APP.getInstance(), "boot", mBootFolder);
                    if (CommandUtil.clientInstall(1, mBootFolder + "BOOT.apk")) {
                        //启动程序
                        LauncherUtils.startLauncherActivity(APP.getInstance());
                    }
                }
            }.start();
        }
    }
}
