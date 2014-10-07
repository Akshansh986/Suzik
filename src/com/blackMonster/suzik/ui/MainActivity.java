package com.blackMonster.suzik.ui;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.blackMonster.suzik.sync.ContentObserverService;
import com.blackMonster.suzik.sync.music.JsonHelper;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
			    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		startService(new Intent(this, ContentObserverService.class));
		List<Long> id = new ArrayList<Long>();
		id.add(12234L);
		id.add(324234L);
		id.add(324234L);
		id.add(324234L);

		try {
			JsonHelper.DeletedSong.toJson(id);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//for (int i=0 ; i<10000 ; ++i);
		//if (ContentObserverService.musicObserver == null) LOGD(TAG,"null");
		//else
		//	LOGD(TAG,"not null");
		//startService(new Intent(this, ContentObserverService.class));
		//startService(new Intent(this, ContentObserverService.class));
		
		//startService(new Intent(this, InitMusicDb.class));
		
		
		//startService(new Intent(this, AddedSongsResponseHandler.class));

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
