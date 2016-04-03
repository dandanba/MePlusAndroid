package com.meplus.client.app;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp3.StethoInterceptor;
import com.meplus.avos.objects.AVOSRobot;
import com.meplus.avos.objects.AVOSUser;
import com.meplus.client.BuildConfig;
import com.meplus.utils.AVOSUtils;
import com.meplus.utils.FIRUtils;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import io.agora.sample.agora.AgoraApplication;
import io.fabric.sdk.android.Fabric;
import okhttp3.OkHttpClient;

/**
 * Created by dandanba on 3/1/16.
 */
public class BaseApplication extends AgoraApplication {
    public RefWatcher getRefWatcher() {
        return mRefWatcher;
    }

    protected RefWatcher mRefWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics(), new Answers());
        if (BuildConfig.DEBUG) {
            mRefWatcher = LeakCanary.install(this);
        } else {
            mRefWatcher = RefWatcher.DISABLED;
        }

        // stetho
        Stetho.initializeWithDefaults(this);

        new OkHttpClient.Builder()
                .addNetworkInterceptor(new StethoInterceptor())
                .build();


        FIRUtils.init(this);

        AVOSUser.registerSubclass();
        AVOSRobot.registerSubclass();

        AVOSUtils.init(this);
    }
}
