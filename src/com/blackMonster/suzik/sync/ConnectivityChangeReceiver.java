package com.blackMonster.suzik.sync;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

import com.blackMonster.suzik.util.NetworkUtils;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class ConnectivityChangeReceiver extends BroadcastReceiver {
	private static final String TAG = "ConnectivityChangeReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
			LOGD(TAG, "connectivity change");
			if (NetworkUtils.isInternetAvailable(context)) {
				Syncer.startOnNetAvaiable(context);
			}
		}
	}
}
