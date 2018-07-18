package com.weijie.stdmgr;

import android.app.Application;

import java.lang.ref.WeakReference;

public class MyApplication extends Application {
    private static WeakReference <MyApplication> instance = null;
    AuthUserData authUser;

    @Override
    public void onCreate() {
        super.onCreate();

        if (instance == null) {
            instance = new WeakReference<>(this);
        }
        authUser = new AuthUserData(this);
    }

    public synchronized static MyApplication getInstance(){
        if (instance != null) {
            return instance.get();
        }
        else {
            return null;
        }
    }
}
