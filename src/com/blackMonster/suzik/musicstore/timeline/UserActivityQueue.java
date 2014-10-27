package com.blackMonster.suzik.musicstore.timeline;

import android.content.Context;

import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.sync.Syncer;

public class UserActivityQueue extends Syncer{

	@Override
	public boolean onPerformSync() throws Exception {

		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		return false;
	}
	
	public static void add(UserActivity activity, Context context) {
		TableUserActivityQueue.insert(activity, context);
	}

}
