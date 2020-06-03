package com.itman.doubleappkeepalivedemo.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.itman.doubleappkeepalivedemo.management.CheckAppManagement;


public class CSBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //更新通信时间
        CheckAppManagement.getInstance().upLastCheckTime(System.currentTimeMillis());
        Log.e("csTest","接收到副app的保活广播");
    }

}
