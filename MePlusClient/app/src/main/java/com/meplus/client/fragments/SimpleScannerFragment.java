package com.meplus.client.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.meplus.client.utils.ISBNUtils;

import me.dm7.barcodescanner.zbar.BarcodeFormat;
import me.dm7.barcodescanner.zbar.Result;
import me.dm7.barcodescanner.zbar.ZBarScannerView;

public class SimpleScannerFragment extends BaseFragment implements ZBarScannerView.ResultHandler {
    private static final String TAG = SimpleScannerFragment.class.getSimpleName();
    private ZBarScannerView mScannerView;

    public static SimpleScannerFragment newInstance() {
        SimpleScannerFragment fragment = new SimpleScannerFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mScannerView = new ZBarScannerView(getActivity());
        return mScannerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this);
        mScannerView.startCamera();
    }

    @Override
    public void handleResult(Result rawResult) {

        final String content;
        final BarcodeFormat format = rawResult.getBarcodeFormat();
        if (format.equals(BarcodeFormat.ISBN10)) {
            content = ISBNUtils.getISBN13(rawResult.getContents());
        } else {
            content = rawResult.getContents();
        }
        Toast.makeText(getContext(), content, Toast.LENGTH_SHORT).show();

        // Note:
        // * Wait 2 seconds to resume the preview.
        // * On older devices continuously stopping and resuming camera preview can result in freezing the app.
        // * I don't know why this is the case but I don't have the time to figure out.
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScannerView.resumeCameraPreview(SimpleScannerFragment.this);
            }
        }, 2000);
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();
    }


}
