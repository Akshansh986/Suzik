package com.blackMonster.suzik.sync.music;


import static com.blackMonster.suzik.util.LogUtils.LOGD;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

public class ObserverMusic extends ContentObserver{
	private static final long LOCK_TIME_MS = 1000;
	private static long lastChanged=0;
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

		if (isLocked()) {
			LOGD("ObserverMusic", "locked");
			return;
		}
			
		lastChanged = System.currentTimeMillis();

		context.startService(new Intent(context, SongsSyncer.class));
	}
	private boolean isLocked() {
		return System.currentTimeMillis() - lastChanged < LOCK_TIME_MS;
	}
	

}
