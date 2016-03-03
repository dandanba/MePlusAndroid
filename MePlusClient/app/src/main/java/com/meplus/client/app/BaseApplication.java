package com.meplus.client.app;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVObject;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.answers.Answers;
import com.meplus.client.Constants;
import com.meplus.client.api.model.User;

import im.fir.sdk.FIR;
import io.agora.sample.agora.AgoraApplication;
import io.fabric.sdk.android.Fabric;

/**
 * Created by dandanba on 3/1/16.
 */
public class BaseApplication extends AgoraApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics(), new Answers());
        FIR.init(this);

        AVObject.registerSubclass(User.class);

        AVOSCloud.initialize(this, Constants.AVOS_APP_ID, Constants.AVOS_APP_KEY); // 初始化参数依次为 this, AppId, AppKey
        // AVOSCloud.useAVCloudUS(); // 启用北美节点
        // AVAnalytics.enableCrashReport(this.getApplicationContext(), true);  // 启用崩溃错误统计
        AVOSCloud.setLastModifyEnabled(true);
        AVOSCloud.setDebugLogEnabled(true);
    }
}
