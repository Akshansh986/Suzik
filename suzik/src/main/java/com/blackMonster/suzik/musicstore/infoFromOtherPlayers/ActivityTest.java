package com.blackMonster.suzik.musicstore.infoFromOtherPlayers;

import android.app.Activity;
import android.os.Bundle;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.R;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

public class ActivityTest extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_verifier);
		LOGD("ACtivitytest", "oncreate");
		DbHelper.getInstance(this);
		//SendMyMusic.send(this);
		//TablePausedSongs.insert(new TablePausedSongs(123, "18 till i die", "akshansh", 234344, 0, 12, 12232), this);
		//TablePausedSongs trow = TablePausedSongs.search("18 till i die", "akshansh", this);
	//	if (trow == null) LOGD("Act", "null");
	//	else
	//		LOGD("act", trow.artist + trow.streaming + trow.track);
	//	TablePausedSongs.clearTable(this);
		
	}
}
