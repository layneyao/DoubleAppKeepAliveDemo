package com.itman.doubleappkeepalivedemo;

import android.app.Application;

public class APP extends Application {
    private static Application instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static Application getInstance() {
        return instance;
    }
}
