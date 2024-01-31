package com.lh.superresolution.core.huawei;

import android.util.Log;

import com.huawei.hiai.vision.common.ConnectionCallback;

public class VisionBaseConnectManager {
    private static final String TAG = "ConnectManager";
    private static VisionBaseConnectManager mInstance = null;
    private Object mWaitConnect = new Object();
    private boolean isConnected = false;

    protected VisionBaseConnectManager() {
    }

    public static VisionBaseConnectManager getInstance() {
        if (mInstance == null) {
            mInstance = new VisionBaseConnectManager();
        }
        return mInstance;
    }

    public ConnectionCallback getmConnectionCallback() {
        return mConnectionCallback;
    }

    private ConnectionCallback mConnectionCallback = new ConnectionCallback() {
        @Override
        public void onServiceConnect() {
            Log.d(TAG, "onServiceConnect");
            synchronized (mWaitConnect) {
                setConnected(true);
                mWaitConnect.notifyAll();
            }
        }

        @Override
        public void onServiceDisconnect() {
            synchronized (mWaitConnect) {
                setConnected(false);
                mWaitConnect.notifyAll();
            }
        }
    };

    public synchronized boolean isConnected() {
        return isConnected;
    }

    public synchronized void setConnected(boolean connected) {
        isConnected = connected;
    }

    public void waitConnect() {
        try {
            synchronized (mWaitConnect) {
                Log.d(TAG, "before start connect!!!");
                mWaitConnect.wait(3000); // Wait for 3 seconds at most.
                Log.d(TAG, "after stop connect !!!");
            }
        } catch (InterruptedException e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
