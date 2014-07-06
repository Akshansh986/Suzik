package com.blackMonster.musicstore.infoFromOtherPlayers;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

public class BroadcastMediaStoreChanged extends BroadcastReceiver{
	private static final String TAG = "BroadcastMediaStoreChanged";

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		Log.i(TAG, action + "  " + "started");

//		String cmd = intent.getStringExtra("command");
		Bundle bundle = intent.getExtras();
		printBundle(bundle,TAG);
		Log.i(TAG, action + "  " + "end");		
	}
	
	public static void printBundle(Bundle bundle, String TAG) {
		if (bundle == null) return;
		try {
			Log.i(TAG, "Bundle started");
			for (String key : bundle.keySet()) {
				Object value = bundle.get(key);
				if (value == null) continue;
				Log.d(TAG, String.format("%s %s (%s)", key,  
						value.toString(), value.getClass().getName()));
			}
			Log.i(TAG, "Bundle end");
			
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}

}
