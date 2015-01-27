package com.blackMonster.suzik.musicPlayer;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

/**
 * Created by akshanshsingh on 27/01/15.
 */
public  class WorkerThread extends HandlerThread implements Handler.Callback {

    private Handler mHandler;

    public WorkerThread() {
        super("Worker");
    }

    public void doRunnable(Runnable runnable) {
        if (mHandler == null) {
            mHandler = new Handler(getLooper(), this);
        }
        Message msg = mHandler.obtainMessage(0, runnable);
        mHandler.sendMessage(msg);
    }

    @Override
    public boolean handleMessage(Message msg) {
        Runnable runnable = (Runnable) msg.obj;
        runnable.run();
        return true;
    }



}