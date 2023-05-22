package com.cl.test.app;

import android.app.Application;

import com.hjq.toast.Toaster;


public class AppApplication extends Application {


    private static Application sInstance;


    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化 Toast 框架
        Toaster.init(this);
    }


}
