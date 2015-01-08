package com.blackMonster.suzik.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

/**
 * Created by akshanshsingh on 08/01/15.
 */
public class UiBroadcasts {

    public static final String  MUSIC_DATA_CHANGED = "MUSIC_DATA_CHANGED";

    public static  void broadcastMusicDataChanged(Context context) {
        Intent intent = new Intent(MUSIC_DATA_CHANGED);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }



}
