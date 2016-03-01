package com.meplus.client.app;

import android.app.Application;

import com.crashlytics.android.Crashlytics;

import im.fir.sdk.FIR;
import io.fabric.sdk.android.Fabric;

/**
 * Created by dandanba on 3/1/16.
 */
public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        FIR.init(this);
    }
}
