package com.itman.boot.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.itman.boot.service.Service1;


/**
 * Created by Layne on 2020-05-29
 *
 */

public class Receiver1 extends BroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        String event = intent.getStringExtra("event");
        if (event == null)
            return;
        switch (event) {
            case "appCheck"://检查APP
//                String content = intent.getStringExtra("content");
//                boolean isFlag = Boolean.parseBoolean(content);
//                BootService.setCheckApp();
                Service1.setCheckApp();
                break;
//            case "download":
//                ChargeSpotService.dwonloadFile();
//                break;
//            case "adbCmd":
//                CommandUtil.adbCmd(UUID.randomUUID().toString(), intent.getStringExtra("content"));
//                break;
        }
    }
}
