package com.meplus.robot.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import com.meplus.robot.app.MPApplication;
import com.squareup.leakcanary.RefWatcher;
//import com.squareup.leakcanary.RefWatcher;

public class BaseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MPApplication.getsInstance().getRefWatcher();
        refWatcher.watch(this);
    }

    public void replaceContainer(int containerViewId, Fragment fragment) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        final FragmentTransaction transaction = fragmentManager.beginTransaction();
        transaction.replace(containerViewId, fragment);
        transaction.commitAllowingStateLoss();
    }

    public Fragment findFragmentById(int containerViewId) {
        final FragmentManager fragmentManager = getSupportFragmentManager();
        return fragmentManager.findFragmentById(containerViewId);
    }
}
