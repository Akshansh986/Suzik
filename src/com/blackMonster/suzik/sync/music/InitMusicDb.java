package com.blackMonster.suzik.sync.music;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import com.blackMonster.suzik.sync.music.InAapSongTable.InAppSongData;
import com.crashlytics.android.Crashlytics;

import android.app.IntentService;
import android.content.Intent;

public class InitMusicDb extends IntentService {
	private static final String TAG = "InitMusicDb";

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
			//startService(new Intent(this, SongsSyncer.class));
		} catch (InterruptedException e) {
			e.printStackTrace();
			Crashlytics.logException(e);

		} catch (ExecutionException e) {
			e.printStackTrace();
			Crashlytics.logException(e);

		} catch (JSONException e) {
			e.printStackTrace();
			Crashlytics.logException(e);

		}

	}

}
