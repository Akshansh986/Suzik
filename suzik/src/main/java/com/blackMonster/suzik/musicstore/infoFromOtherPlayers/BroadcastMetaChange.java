package com.blackMonster.suzik.musicstore.infoFromOtherPlayers;

import android.content.Context;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGI;

public class BroadcastMetaChange extends MusicBroadcastManager {
	private static final String TAG = "BroadcastMetaChange";

	@Override
	public void runIt(Context context) {
		LOGD(TAG, "runIt");
		if (isPlaying()) {
			if (PlayingSong.isVirtuallyCompleted(context))
				PlayingSong
						.moveToCompleted(System.currentTimeMillis(), context);

			setPlayingSong(context);
		}
		setMetaChangePrefs(context);
	}

	private void setMetaChangePrefs(Context context) {
		LOGI(TAG, "setplayingsong");
		MetaChangePrefs.setAll(getSong(), context);		
	}

	private void setPlayingSong(Context context) {
		LOGI(TAG, "setplayingsong");
		PlayingSong.set(getSong(), 0, System.currentTimeMillis(), context);
	}

}
