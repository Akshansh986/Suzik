package com.blackMonster.suzik.musicstore.infoFromOtherPlayers;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;

import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.sync.music.MusicSyncManager;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGI;

/**
 * Created by Akshansh on 2/13/2015.
 */
public class SonyMetaChangeBroadcast extends  BroadcastMetaChange {
    public static final String TAG = "SonyMetaChangeBroadcast";
    final String P_TRACK = "TRACK_NAME";
    final String P_ARTIST = "ARTIST_NAME";
    final String P_ALBUM = "ALBUM_NAME";
    final String P_ID = "TRACK_ID";
    final String P_DURATION = "TRACK_DURATION";

    @Override
    public void fixBroadcastParameters(Intent intent, Context context) throws ExceptionUnknownBroadcast {

        LOGI(TAG, "fixBroadcastParameters");
        BroadcastMediaStoreChanged.printBundle(intent.getExtras(), TAG);

        track = intent.getStringExtra(P_TRACK);
        if (track == null)
            throw new ExceptionUnknownBroadcast();
        if (track.equals(""))
            track = "<unknown>";

        artist = intent.getStringExtra(P_ARTIST);
        if (artist == null)
            throw new ExceptionUnknownBroadcast();
        if (artist.equals(""))
            artist = "<unknown>";

        album = intent.getStringExtra(P_ALBUM);
        if (album != null && album.equals(""))
            album = "<unknown>";

        duration = intent.getIntExtra(P_DURATION,0);


        playing = true;
        Song tempSong = new Song(track, artist, album, duration);
        // if (track == null || artist == null)
        // throw new ExceptionUnknownBroadcast();

        LOGD(TAG, track + "   " + artist);
        Pair<Long, Song> song = MusicSyncManager.getSong(tempSong, context);

        if (song == null) {
            Bundle bundle = intent.getExtras();
            id = intent.getIntExtra(P_ID, 0); // Using getFromBundle instead of
            // getExtraLong because
            // getFromBundle can throw
            // exception.
            duration = intent.getIntExtra(P_DURATION,0);
            streaming = UserActivity.STREAMING_TRUE;
            LOGD(TAG, "song not found in database");
        } else {
            id = song.first;
            duration = song.second.getDuration();
            streaming = UserActivity.STREAMING_FALSE;
            LOGD(TAG, "song found in database");

        }

    }


}
