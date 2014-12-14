package com.blackMonster.suzik.musicstore.infoFromOtherPlayers;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import static com.blackMonster.suzik.util.LogUtils.LOGD;

import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.musicstore.userActivity.UserActivityManager;

public class BroadcastPlaystateChage extends MusicBroadcastManager {
	private static final String TAG = "BroadcastPlaystateChage";

	@Override
	public void run(Context context) {
		Log.d(TAG, "run");
		if (isPlaying())
			handlePlay(context);
		else
			handlePause(context);

	}

	@Override
	public void fixBroadcastParameters(Intent intent, Context context)
			throws ExceptionUnknownBroadcast {

		track = intent.getStringExtra(P_TRACK);
		artist = intent.getStringExtra(P_ARTIST);

		if (track == null || artist == null) {
			BroadcastSong song = MetaChangePrefs.getSong(context);
			track = song.getTitle();
			artist = song.getArtist();
			album = song.getAlbum();
			id = song.getId();
			duration = song.getDuration();
			playing = intent.getBooleanExtra(P_PLAYING, false);
			streaming = song.isStreaming();
			LOGD(TAG,"track or artist is null, using metaPrefs "+ song.toString()	);
		} else {
			super.fixBroadcastParameters(intent, context);
		}

	}

	private void handlePlay(Context context) {
		Log.i(TAG, "handleplay");
		if (PlayingSong.isVirtuallyCompleted(context))
			PlayingSong.moveToCompleted(System.currentTimeMillis(), context);

		TablePausedSongs tps = TablePausedSongs.search((Song) getSong(),
				context);
		long pastPlayed;
		if (tps != null) {
			pastPlayed = tps.getPastPlayed();
			Log.d(TAG, "pastplayed" + pastPlayed);
			TablePausedSongs.remove(getSong(), context);
			Log.d(TAG, "pastplayed" + pastPlayed);
		} else
			pastPlayed = 0;

		long startTs = System.currentTimeMillis();
		PlayingSong.set(getSong(), pastPlayed, startTs, context);
		
		MetaChangePrefs.setAll(getSong(), context);
	}

	private void handlePause(Context context) {
		Log.i(TAG, "handlepause");

		long pastPlayed = System.currentTimeMillis()
				- PlayingSong.getStartTs(context)
				+ PlayingSongPrefs.getPastPlayed(context);
		long pauseTS = System.currentTimeMillis();
		if (PlayingSong.isPlaying(context)) {
			if (PlayingSong.isCompleted(context)) {
				TableCompletedSongs.insert(PlayingSong.getSong(context),
						System.currentTimeMillis(), context);
				UserActivityManager.add(
						new UserActivity(null, PlayingSong.getSong(context)
								.getId(), UserActivity.ACTION_OUT_APP_PLAYED,
								PlayingSong.getSong(context).isStreaming(),
								System.currentTimeMillis()), context);

			} else
				TablePausedSongs.insert(
						new TablePausedSongs(PlayingSong.getSong(context),
								pastPlayed, pauseTS), context);
			PlayingSong.reset(context);
		}
	}

}
