package com.blackMonster.suzik.sync;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

import com.blackMonster.suzik.sync.music.SongsSyncer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.sax.StartElementListener;

public class BootReceiver extends BroadcastReceiver {
	private static final String TAG = "BootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
			LOGD(TAG, "Boot complete");
			context.startService(new Intent(context, SongsSyncer.class));

        }
    }
}
