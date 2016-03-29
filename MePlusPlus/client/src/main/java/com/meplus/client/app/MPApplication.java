package com.meplus.client.app;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

/**
 * Created by dandanba on 3/1/16.
 * MP 代表 MePlus
 */
public class MPApplication extends ModelApplication {
    private static MPApplication sInstance;

    public static MPApplication getsInstance() {
        return sInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics());
        sInstance = this;
    }

}
