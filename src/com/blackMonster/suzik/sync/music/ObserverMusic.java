package com.blackMonster.suzik.sync.music;


import static com.blackMonster.suzik.util.LogUtils.LOGD;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;

public class ObserverMusic extends ContentObserver{
	
	Context context;
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

		context.startService(new Intent(context, SongsSyncer.class));
	}
	

}
