package com.huasuan.myzhbj.application;

import android.app.Application;

import org.xutils.x;

/**
 * Created by john on 2017/3/3.
 */

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);//Xutils初始化
    }
}
