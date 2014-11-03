package com.blackMonster.suzik.musicstore.timeline;

import android.content.Context;

import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.sync.Syncer;

public class UserActivityManager {
	
	public static void add(UserActivity activity, Context context) {
		QueueUserActivity.insert(activity, context);
		Syncer.batchRequest(UserActivityQueueSyncer.class, context);
	}

}
