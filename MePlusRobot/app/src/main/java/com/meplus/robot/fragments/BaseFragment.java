package com.meplus.robot.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.meplus.robot.activity.BaseActivity;
import com.meplus.robot.app.MPApplication;
import com.squareup.leakcanary.RefWatcher;
//import com.squareup.leakcanary.RefWatcher;

public class BaseFragment extends Fragment {
    private static final String TAG = BaseFragment.class.getSimpleName();

    private BaseActivity mActivity;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (BaseActivity) context;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        RefWatcher refWatcher = MPApplication.getsInstance().getRefWatcher();
        refWatcher.watch(this);
    }

}
