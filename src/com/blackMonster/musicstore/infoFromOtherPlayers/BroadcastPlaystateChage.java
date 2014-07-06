package com.blackMonster.musicstore.infoFromOtherPlayers;

import android.content.Context;
import android.util.Log;

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

	private void handlePlay(Context context) {
		Log.i(TAG, "handleplay");
		if (PlayingSong.isVirtuallyCompleted(context))
			PlayingSong.moveToCompleted(System.currentTimeMillis(), context);

		TablePausedSongs tps = TablePausedSongs.search(getTrack(), getArtist(),
				context);
		long pastPlayed;
		if (tps != null) {
			pastPlayed = tps.getPastPlayed();
			Log.d(TAG, "pastplayed" + pastPlayed);
			TablePausedSongs.remove(getTrack(), getArtist(), context);
			Log.d(TAG, "pastplayed" + pastPlayed);
		} else
			pastPlayed = 0;

		long startTs = System.currentTimeMillis();
		PlayingSong.set(getSong(), pastPlayed, startTs, context);
	}

	private void handlePause(Context context) {
		Log.i(TAG, "handlepause");

		long pastPlayed = System.currentTimeMillis()
				- PlayingSong.getStartTs(context)
				+ PlayingSongPrefs.getPastPlayed(context);
		long pauseTS = System.currentTimeMillis();
		if (PlayingSong.isCompleted(context))
			TableCompletedSongs.insert(getSong(), System.currentTimeMillis(),
					context);
		else
			TablePausedSongs.insert(new TablePausedSongs(getSong(), pastPlayed,
					pauseTS), context);

		PlayingSong.reset(context);
	}

}
