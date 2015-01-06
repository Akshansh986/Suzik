package com.blackMonster.suzik.sync;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.util.NetworkUtils;

public class ConnectivityChangeReceiver extends BroadcastReceiver {
	private static final String TAG = "ConnectivityChangeReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
        if (!MainPrefs.isLoginDone(context)) return;


        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
			LOGD(TAG, "connectivity change");
			if (NetworkUtils.isInternetAvailable(context)) {
				LOGD(TAG, "Net available");
				Syncer.startOnNetAvaiable(context);
			}
			else
			{
				LOGD(TAG, "Net not available");
			}
		}
	}
}
