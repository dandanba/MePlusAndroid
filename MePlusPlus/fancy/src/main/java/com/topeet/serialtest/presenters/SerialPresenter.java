package com.topeet.serialtest.presenters;

import android.util.Log;

import com.meplus.fancy.events.ErrorEvent;
import com.meplus.fancy.events.LogEvent;
import com.meplus.fancy.events.ScannerEvent;
import com.topeet.serialtest.serial;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by dandanba on 4/23/16.
 */
public class SerialPresenter {
    private static final String TAG = SerialPresenter.class.getSimpleName();
    public static final String METHOD_RUN = "SerialScannerRun";

    // 充磁 0x02 0x56 0x52 0x32 0x03 0x37
    private static final int[] MAGNETIZE_BUFFER = new int[]{0x02, 0x56, 0x52, 0x32, 0x03, 0x37};
    // 消磁 0x02 0x56 0x52 0x31 0x03 0x34
    private static final int[] DEMAGNETIZE_BUFFER = new int[]{0x02, 0x56, 0x52, 0x31, 0x03, 0x34};

    private serial mCom = new serial();
    private StringBuffer mBuffer = new StringBuffer();
    private ReadThread mReadThread;

    public void start() {
        mCom.Open(1, 115200);
        /* Create a receiving thread */
        mReadThread = new ReadThread();
        mReadThread.start();
    }

    public void destroy() {
        if (mReadThread != null && !mReadThread.isInterrupted()) {
            mReadThread.interrupt();
        }
        if (mCom != null) {
            mCom.Close();
        }
        mCom = null;
    }

    public int magnetize() {
        return write(MAGNETIZE_BUFFER, MAGNETIZE_BUFFER.length);
    }

    public int demagnetize() {
        return write(DEMAGNETIZE_BUFFER, DEMAGNETIZE_BUFFER.length);
    }

    private int write(int[] buffer, int len) {
        return mCom.Write(buffer, len);
    }

    class ReadThread extends Thread {

        @Override
        public void run() {
            super.run();
            while (!isInterrupted()) {
                if (mCom != null) {
                    final int[] RX = mCom.Read();// 扫描二维码和条形码的结果
                    handleRX(RX);
                } else { // 处理底盘异常崩溃的情况
                    interrupt();
                    EventBus.getDefault().post(new ErrorEvent(METHOD_RUN, new IllegalArgumentException("mCom == null")));
                }
            }
        }

        private void handleRX(int[] RX) {
            if (RX != null && RX.length > 0) { // 数据有效
                mBuffer.append(new String(RX, 0, RX.length));
                final String buffer = mBuffer.toString();
                Log.i(TAG, buffer);
                EventBus.getDefault().post(new LogEvent(buffer));
                if (buffer.contains("}")) {
                    mBuffer.setLength(0);
                }
                if (buffer.contains("{") && buffer.contains("}")) {// JSON 格式
                    final int start = buffer.indexOf("{");
                    final int end = buffer.indexOf("}", start);
                    if (end != -1) {// JSON 格式结束
                        final String content = buffer.substring(start, end + 1);
                        EventBus.getDefault().post(new ScannerEvent(content));
                    }
                } else {
                    if (buffer.length() == 13) { // 13位ISBN
                        EventBus.getDefault().post(new ScannerEvent(buffer));
                        mBuffer.setLength(0);
                    }
                }

            }
        }
    }

    static {
        System.loadLibrary("serialtest");
    }

}
