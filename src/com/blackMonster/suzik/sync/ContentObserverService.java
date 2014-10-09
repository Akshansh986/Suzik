package com.blackMonster.suzik.sync;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;

import com.blackMonster.suzik.sync.music.AndroidHelper;
import com.blackMonster.suzik.sync.music.ObserverMusic;

public class ContentObserverService extends Service{
	private static final String TAG = "ContentObserverService";
	private static ObserverMusic musicObserver=null ;
	
	

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		LOGD(TAG,"onStartCommand");
		super.onStartCommand(intent, flags, startId);
		createObserver();
		
		return START_STICKY;
	}

	
	private  void createObserver() {
		if (musicObserver == null) {
			LOGD(TAG,"creating Observer");
			musicObserver = new ObserverMusic(new Handler(), this);
			getContentResolver().registerContentObserver(AndroidHelper.URI, true, musicObserver);
		}		
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		LOGD(TAG,"onDestroy");
		if (musicObserver!=null) {
			getContentResolver().unregisterContentObserver(musicObserver);
			musicObserver =null;
			
		}
	}
	@Override
	public IBinder onBind(Intent arg0) {
		return null;
	}

}
