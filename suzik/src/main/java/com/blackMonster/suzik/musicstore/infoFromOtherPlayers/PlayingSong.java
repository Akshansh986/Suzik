package com.blackMonster.suzik.musicstore.infoFromOtherPlayers;

import android.content.Context;

import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.musicstore.userActivity.UserActivityManager;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGE;
import static com.blackMonster.suzik.util.LogUtils.LOGI;

public class PlayingSong {
	private static final String TAG = "PlayingSong";
	private static final double VIRTUALLY_COMPLETED_LOWER_LIMIT = .6; // 60%
	private static final double VIRTYALLY_COMPLETED_UPPER_LIMIT = 2; // 200%

	public static boolean set(BroadcastSong song, long pastPlayed,
			long startTS, Context context) {
		LOGI(TAG, "set");
		if (isInTimeNSameSong(song, context)) {
			LOGE(TAG, "Unable to write");
			return false;
		} else {
			PlayingSongPrefs.setAll(song, pastPlayed, startTS, context);
			LOGD(TAG, "written");
			return true;
		}
	}

	private static boolean isInTimeNSameSong(BroadcastSong song, Context context) {
		LOGI(TAG, "getWriteLock");

		if (!isPlaying(context))
			return false;
		boolean inTime = (System.currentTimeMillis() - PlayingSongPrefs
				.getStartTS(context)) <= 500;
		boolean sameSong = song.equals(PlayingSongPrefs.getSong(context));

		LOGD(TAG, "intime " + inTime + "samesong" + sameSong);
		if (inTime && sameSong)
			return true;
		else
			return false;
	}

	public static void reset(Context context) {
		LOGI(TAG, "reset");
		PlayingSongPrefs.setAll(
				new BroadcastSong(-1, "NA", "NA", "NA", -1, -1), -1, -1,
				context);
	}

	public static boolean isCompleted(Context context) {
		LOGI(TAG, "iscompleted");
		if (!isPlaying(context))
			return false;
		long tElapsed = System.currentTimeMillis()
				- PlayingSongPrefs.getStartTS(context)
				+ PlayingSongPrefs.getPastPlayed(context);

		LOGD(TAG,
                "telapsed " + tElapsed + " duration "
                        + PlayingSongPrefs.getDuration(context));
		if (tElapsed > PlayingSongPrefs.getDuration(context) - 3000
				&& tElapsed < PlayingSongPrefs.getDuration(context) + 3000)
			return true;
		else
			return false;
	}

	public static boolean isVirtuallyCompleted(Context context) {
		LOGI(TAG, "isvirtuallycompleted");
		if (!isPlaying(context))
			return false;

		long tElapsed = System.currentTimeMillis()
				- PlayingSongPrefs.getStartTS(context)
				+ PlayingSongPrefs.getPastPlayed(context);

		long duration = PlayingSongPrefs.getDuration(context);

		LOGD(TAG, "telapsed" + tElapsed + " duration " + duration);
		if (tElapsed > VIRTUALLY_COMPLETED_LOWER_LIMIT * duration
				&& tElapsed < VIRTYALLY_COMPLETED_UPPER_LIMIT * duration)
			return true;
		else
			return false;
	}

	public static boolean isPlaying(Context context) {
		LOGI(TAG, "isplaying");
		if (PlayingSongPrefs.getId(context) == -1)
			return false;
		else
			return true;
	}

	public static void moveToCompleted(long completedTS, Context context) {
		LOGI(TAG, "movetocompleted");
		TableCompletedSongs.insert(PlayingSong.getSong(context), completedTS,
				context);
		Song song = new Song(PlayingSong.getSong(context).getTitle(),
				PlayingSong.getSong(context).getArtist(), PlayingSong
						.getSong(context).getAlbum(), PlayingSong
						.getSong(context).getDuration());
		UserActivityManager.add(new UserActivity(song,null, PlayingSong.getSong(context).getId(), UserActivity.getOutappPlayAction(PlayingSong.getSong(context).isStreaming()), System.currentTimeMillis(), null), context);
		PlayingSong.reset(context);

	}

	public static BroadcastSong getSong(Context context) {
		LOGI(TAG, "getsong");
		return new BroadcastSong(PlayingSongPrefs.getId(context),
				PlayingSongPrefs.getTrack(context),
				PlayingSongPrefs.getArtist(context),
				PlayingSongPrefs.getAlbum(context),
				PlayingSongPrefs.getDuration(context),
				PlayingSongPrefs.getStreaming(context));
	}

	public static long pastPlayed(Context context) {
		return PlayingSongPrefs.getPastPlayed(context);
	}

	public static long getStartTs(Context context) {
		return PlayingSongPrefs.getStartTS(context);
	}

}
