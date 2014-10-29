package com.blackMonster.suzik.sync;


import static com.blackMonster.suzik.util.LogUtils.LOGD;

import com.blackMonster.suzik.sync.Syncer;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

public abstract class MyContentObserver extends ContentObserver{
	private static final long WAIT_MS = 3000;
	private Context context;
	public MyContentObserver(Handler handler, Context context) {
		super(handler);
		this.context = context.getApplicationContext();
	}
	@Override
	public void onChange(boolean selfChange) {
		this.onChange(selfChange, null);
	}
	
	@Override
	public void onChange(boolean selfChange, Uri uri) {
		LOGD(getClass().getSimpleName(), "onChange");
		Syncer.callFuture(onContentChange(), WAIT_MS, context);	//THIS ensures batching of requests.
	}
	
	@SuppressWarnings("rawtypes")
	public abstract Class onContentChange();



}
