package com.blackMonster.suzik.musicstore.userActivity;

import android.content.Context;

import com.blackMonster.suzik.musicstore.MusicServerUtils;
import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.sync.Syncer;
import com.blackMonster.suzik.sync.music.MusicSyncManager;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGE;

public class UserActivityManager {
	private static final String TAG = "UserActivityManager";
	
	public static void add(UserActivity activity, Context context) {
		LOGD(TAG,"add");
        if (!activity.isOnlineSong() )
        {
            Long serverId = MusicSyncManager.getServerId(activity.songId(),context);
            if (serverId == null) {
                LOGE(TAG, "ServerId not found, Skipped..");
                return;
            }

            if (MusicServerUtils.isDummyServerSongId(serverId)) {
                LOGD(TAG, "Song id dummy, Skipped..");
                return;
            }
        }
		QueueUserActivity.insert(activity, context);
		Syncer.batchRequest(UserActivityQueueSyncer.class, context);
	}

}
