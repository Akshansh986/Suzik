package com.blackMonster.suzik.musicstore.userActivity;

import android.content.Context;
import static com.blackMonster.suzik.util.LogUtils.LOGD;

import com.blackMonster.suzik.musicstore.MusicServerUtils;
import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.sync.Syncer;

public class UserActivityManager {
	private static final String TAG = "UserActivityManager";
	
	public static void add(UserActivity activity, Context context) {
		LOGD(TAG,"add");
        if (!activity.isOnlineSong() && MusicServerUtils.isDummyServerSongId(activity.songId())) {
            LOGD(TAG,"Song id dummy, Skipped..");
            return;
        }
		QueueUserActivity.insert(activity, context);
		Syncer.batchRequest(UserActivityQueueSyncer.class, context);
	}

}
