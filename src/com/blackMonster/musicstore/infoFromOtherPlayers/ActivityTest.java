package com.blackMonster.musicstore.infoFromOtherPlayers;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.blackMonster.musicstore.SendMyMusic;
import com.blackMonster.suzik.R;

public class ActivityTest extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		Log.d("ACtivitytest", "oncreate");
		DbHelper.getInstance(this);
		SendMyMusic.send(this);
		//TablePausedSongs.insert(new TablePausedSongs(123, "18 till i die", "akshansh", 234344, 0, 12, 12232), this);
		//TablePausedSongs trow = TablePausedSongs.search("18 till i die", "akshansh", this);
	//	if (trow == null) Log.d("Act", "null");
	//	else
	//		Log.d("act", trow.artist + trow.streaming + trow.track);
	//	TablePausedSongs.clearTable(this);
		
	}
}
