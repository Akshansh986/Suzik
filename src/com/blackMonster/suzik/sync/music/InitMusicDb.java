package com.blackMonster.suzik.sync.music;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

import java.util.List;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.blackMonster.suzik.sync.music.InAapSongTable.InAppSongData;
import com.crashlytics.android.Crashlytics;

public class InitMusicDb extends IntentService {
	private static final String TAG = "InitMusicDb";
	public static final String BROADCAST_INIT_MUSIC_DB_RESULT = "BROADCAST_INIT_MUSIC_DB_RESULT";


	public InitMusicDb() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		LOGD(TAG, "onHandleIntent ");
		init();
	}

	private void init() {
		try {
			LOGD(TAG, "starting init music db");

			List<InAppSongData> songList = ServerHelper.getAllMySongs(this);
			LOGD(TAG, "server done");
			InAapSongTable.insert(songList, this);
			LOGD(TAG, "in app insert complete");
			broadcastResult(BROADCAST_INIT_MUSIC_DB_RESULT, true);
			//startService(new Intent(this, SongsSyncer.class));
		} catch (Exception e) {
			e.printStackTrace();
			Crashlytics.logException(e);
			broadcastResult(BROADCAST_INIT_MUSIC_DB_RESULT, false);


		}

	}
	
	private void broadcastResult(String type, boolean result) {
		Intent intent = new Intent(type).putExtra(type, result);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		
	}

}
