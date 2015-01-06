package com.blackMonster.suzik.sync;


import static com.blackMonster.suzik.util.LogUtils.LOGD;
import android.content.Context;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

import com.blackMonster.suzik.MainPrefs;

public abstract class MyContentObserver extends ContentObserver{
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

        if (!MainPrefs.isLoginDone(context)) return;
        LOGD(getClass().getSimpleName(), "onChange");
		Syncer.batchRequest(onContentChange(), context);
	}
	
	@SuppressWarnings("rawtypes")
	public abstract Class onContentChange();



}
