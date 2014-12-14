package com.blackMonster.suzik.musicstore.userActivity;

import android.content.Context;
import static com.blackMonster.suzik.util.LogUtils.LOGD;

import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.sync.Syncer;

public class UserActivityManager {
	private static final String TAG = "UserActivityManager";
	
	public static void add(UserActivity activity, Context context) {
		LOGD(TAG,"add");
		QueueUserActivity.insert(activity, context);
		Syncer.batchRequest(UserActivityQueueSyncer.class, context);
	}

}
