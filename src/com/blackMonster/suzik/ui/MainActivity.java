package com.blackMonster.suzik.ui;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.blackMonster.suzik.sync.ContentObserverService;
import com.blackMonster.suzik.sync.contacts.ContactsSyncer;
import com.blackMonster.suzik.sync.music.InitMusicDb;
import com.crashlytics.android.Crashlytics;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
			    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Crashlytics.start(this);
			
		//for (int i=0 ; i<10000 ; ++i);
		//if (ContentObserverService.musicObserver == null) LOGD(TAG,"null");
		//else
		//	LOGD(TAG,"not null");
		
		startService(new Intent(this, ContentObserverService.class));
		//startService(new Intent(this, ContentObserverService.class));
		
		startService(new Intent(this, InitMusicDb.class));
		//startService(new Intent(this, ContactsSyncer.class));

		
	//startService(new Intent(this, SongsSyncer.class));
	//startService(new Intent(this, AddedSongsResponseHandler.class));

//	startService(new Intent(this, SongsSyncer.class));
//	startService(new Intent(this, SongsSyncer.class));


		//new DbHelper(this.getApplicationContext());
	/*	new Thread() {
			public void run() {
				try {
					SongsSyncer.startSync(getBaseContext().getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();
		
	*/
	}
	
	
	
	
	
	
	

	

	
	
}
