package com.blackMonster.suzik.sync.music;


import android.content.Context;
import android.os.Handler;

import com.blackMonster.suzik.sync.MyContentObserver;

public class ObserverMusic extends MyContentObserver{
	public ObserverMusic(Handler handler, Context context) {
		super(handler, context);
	}

	@Override
	public Class onContentChange() {
		return SongsSyncer.class;
	}



}
