package com.blackMonster.suzik.sync.music;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import com.blackMonster.suzik.sync.music.InAapSongTable.InAppSongData;

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

		new Thread() {
			public void run() {
				try {
					init();
				} catch (Exception e) {
				}
			}

		}.start();
	}

	private void init() {
		try {
			List<InAppSongData> songList = ServerHelper.getAllMySongs(this);
			InAapSongTable.insert(songList, this);
			startService(new Intent(this, SongsSyncer.class));
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

}
