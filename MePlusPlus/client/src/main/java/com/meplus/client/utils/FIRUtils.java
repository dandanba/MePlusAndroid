package com.meplus.client.utils;

import android.content.Context;
import android.util.Log;

import com.marvinlabs.intents.MediaIntents;
import com.meplus.client.Constants;
import com.meplus.client.api.model.FirVersion;

import cn.trinea.android.common.util.PackageUtils;
import cn.trinea.android.common.util.ToastUtils;
import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;

/**
 * Created by dandanba on 3/2/16.
 */
public class FIRUtils {
    public static void checkForUpdateInFIR(final Context context, VersionCheckCallback callback) {
        FIR.checkForUpdateInFIR(Constants.FIR_TOKEN, callback);
    }

    public static void onSuccess(Context context, String json) {
        Log.i("fir", "check from fir.im success! " + "\n" + json);
        final FirVersion version = JsonUtils.readValue(json, FirVersion.class);
        final int versionCode = PackageUtils.getAppVersionCode(context);
        if (versionCode < version.version) {
            context.startActivity(MediaIntents.newOpenWebBrowserIntent(version.update_url));
        } else {
            ToastUtils.show(context, "已经是最新版本！");
        }
    }
}
