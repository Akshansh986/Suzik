package com.blackMonster.suzik.ui;

import android.app.Activity;
import android.os.Bundle;

import com.blackMonster.suzik.sync.music.SongsSyncer;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
			    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		
	
		//new DbHelper(this.getApplicationContext());
		new Thread() {
			public void run() {
				try {
					SongsSyncer.startSync(getBaseContext().getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();
		
	
	}
	
	
	
	
	
	
	

	

	
	
}
