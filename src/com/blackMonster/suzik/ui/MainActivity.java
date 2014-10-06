package com.blackMonster.suzik.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.blackMonster.suzik.sync.ContentObserverService;
import com.blackMonster.suzik.sync.music.AddedSongsResponseHandler;
import com.blackMonster.suzik.sync.music.SongsSyncer;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
			    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		startService(new Intent(this, ContentObserverService.class));
		
		//startService(new Intent(this, SongsSyncer.class));
	
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
