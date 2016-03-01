package com.meplus.client.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.meplus.client.Constants;
import com.meplus.client.R;

import im.fir.sdk.FIR;
import im.fir.sdk.VersionCheckCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        checkForUpdateInFIR();
    }

    public void checkForUpdateInFIR() {
        FIR.checkForUpdateInFIR(Constants.FIR_TOKEN, new VersionCheckCallback() {
                    @Override
                    public void onSuccess(String versionJson) {
                        Log.i("fir", "check from fir.im success! " + "\n" + versionJson);
                        //   {"name":"MePlusClient","version":"1","changelog":"First release","updated_at":1456829748,"versionShort":"1.0","build":"1","installUrl":"http://download.fir.im/v2/app/install/56d57525748aac6cae000037?download_token=8122f240c21a2f3fef1481c112e666f6","install_url":"http://download.fir.im/v2/app/install/56d57525748aac6cae000037?download_token=8122f240c21a2f3fef1481c112e666f6","direct_install_url":"http://download.fir.im/v2/app/install/56d57525748aac6cae000037?download_token=8122f240c21a2f3fef1481c112e666f6","update_url":"http://fir.im/meplusclien","binary":{"fsize":1416518}}
//                        FirVersion version = JsonUtils.readValue(versionJson, FirVersion.class);
//                        Log.i("fir", version.toString());
                    }

                    @Override
                    public void onFail(Exception exception) {
                        Log.i("fir", "check fir.im fail! " + "\n" + exception.getMessage());
                    }

                    @Override
                    public void onStart() {
                        Toast.makeText(getApplicationContext(), "正在获取", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFinish() {
                        Toast.makeText(getApplicationContext(), "获取完成", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }
}
