package com.blackMonster.suzik.sync;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.util.NetworkUtils;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

public class ConnectivityChangeReceiver extends BroadcastReceiver {
    private static final String TAG = "ConnectivityChangeReceiver";

    @Override
    public void onReceive(final Context context, final Intent intent) {
        if (!MainPrefs.isLoginDone(context)) return;

        new Thread(new Runnable() {
            @Override
            public void run() {
                if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
                    LOGD(TAG, "connectivity change");
                    if (NetworkUtils.isInternetAvailable(context)) {
                        LOGD(TAG, "Net available");
                        Syncer.startOnNetAvaiable(context);
                    } else {
                        LOGD(TAG, "Net not available");
                    }
                }

            }
        }).start();
    }
}
