package com.blackMonster.suzik.musicstore.infoFromOtherPlayers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.blackMonster.suzik.MainPrefs;
import com.crashlytics.android.Crashlytics;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGI;

public class BroadcastMediaStoreChanged extends BroadcastReceiver{
	private static final String TAG = "BroadcastMediaStoreChanged";

	@Override
	public void onReceive(Context context, Intent intent) {

        if (!MainPrefs.isLoginDone(context)) return;
		String action = intent.getAction();
		LOGI(TAG, action + "  " + "started");

//		String cmd = intent.getStringExtra("command");
		Bundle bundle = intent.getExtras();
		printBundle(bundle,TAG);
		LOGI(TAG, action + "  " + "end");
	}
	
	public static void printBundle(Bundle bundle, String TAG) {
		if (bundle == null) return;
		try {
			LOGI(TAG, "Bundle started");
			for (String key : bundle.keySet()) {
				Object value = bundle.get(key);
				if (value == null) continue;
				LOGD(TAG, String.format("%s %s (%s)", key,
                        value.toString(), value.getClass().getName()));
			}
			LOGI(TAG, "Bundle end");
			
		}
		catch (Exception e) {
			e.printStackTrace();
			Crashlytics.logException(e);
		}
	}

}
