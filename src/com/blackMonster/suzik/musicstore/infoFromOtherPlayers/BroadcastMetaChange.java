package com.blackMonster.suzik.musicstore.infoFromOtherPlayers;

import android.content.Context;
import android.util.Log;

public class BroadcastMetaChange extends MusicBroadcastManager {
	private static final String TAG = "BroadcastMetaChange";

	@Override
	public void run(Context context) {
		Log.d(TAG, "run");
		if (!isPlaying())
			return;
		if (PlayingSong.isVirtuallyCompleted(context))
			PlayingSong.moveToCompleted(System.currentTimeMillis(), context);

		setPlayingSong(context);

	}

	private void setPlayingSong(Context context) {
		Log.i(TAG, "setplayingsong");
		//PlayingSong.set(getSong(), 0, System.currentTimeMillis(), context);
	}

}
