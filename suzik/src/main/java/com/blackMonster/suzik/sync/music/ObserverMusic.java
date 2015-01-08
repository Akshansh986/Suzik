package com.blackMonster.suzik.sync.music;


import android.content.Context;
import android.os.Handler;

import com.blackMonster.suzik.sync.MyContentObserver;
import com.blackMonster.suzik.ui.UiBroadcasts;

public class ObserverMusic extends MyContentObserver{
    Context context;
	public ObserverMusic(Handler handler, Context context) {
		super(handler, context);
        this.context = context;
	}

	@Override
	public Class onContentChange() {
        UiBroadcasts.broadcastMusicDataChanged(context);
        return SongsSyncer.class;
	}



}
