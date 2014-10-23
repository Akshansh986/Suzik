package com.blackMonster.suzik.sync.music;


import static com.blackMonster.suzik.util.LogUtils.LOGD;

import com.blackMonster.suzik.sync.Syncer;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

public class ObserverMusic extends ContentObserver{
	private static final long WAIT_MS = 2000;
	private Context context;
	public ObserverMusic(Handler handler, Context context) {
		super(handler);
		this.context = context.getApplicationContext();
	}
	@Override
	public void onChange(boolean selfChange) {
		this.onChange(selfChange, null);
	}
	
	@Override
	public void onChange(boolean selfChange, Uri uri) {
		LOGD("ObserverMusic", "onChange");
		Syncer.callFuture(SongsSyncer.class, WAIT_MS, context);	//THIS ensures batching of requests.
	}



}
