package com.blackMonster.suzik.musicstore.infoFromOtherPlayers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;

import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.musicstore.infoFromOtherPlayers.testing.TableAllPlayed;
import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.sync.music.MusicSyncManager;
import com.crashlytics.android.Crashlytics;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGE;
import static com.blackMonster.suzik.util.LogUtils.LOGI;

public abstract class MusicBroadcastManager extends BroadcastReceiver {
    private static final String TAG = "MusicBroadcastManager";
    final String P_TRACK = "track";
    final String P_ARTIST = "artist";
    final String P_ALBUM = "album";

    final String P_PLAYING = "playing";
    final String P_PLAYING_2 = "isplaying";
    final String P_ID = "id";
    final String P_DURATION = "duration";
    final String P_DURATION_2 = "trackLength";

    String track, artist, album;
    long id, duration;
    boolean playing;
    int streaming;

    private static DuplicateBroadcastFilter duplicateFilter = new DuplicateBroadcastFilter();

    @Override
    public void onReceive(final Context context, final Intent intent) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!MainPrefs.isLoginDone(context)) return;
                String action = intent.getAction();


                try {
                    LOGE(TAG,
                            action + "  " + "started  "  );
                    fixBroadcastParameters(intent, context);
                    if (!duplicateFilter.isDuplicate(getSong(), action, playing,
                            System.currentTimeMillis())) {
                        TableAllPlayed.insert(getSong(), System.currentTimeMillis(),
                                context);
                        runIt(context);
                    }
                } catch (ExceptionUnknownBroadcast e) {
                    e.printStackTrace();
                    Crashlytics.logException(e);
                }
                LOGE(TAG, action + "  " + "completed");

            }
        }).start();

    }

    public abstract void runIt(Context context);

    public void fixBroadcastParameters(Intent intent, Context context)
            throws ExceptionUnknownBroadcast {
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

        duration = getDurationFromIntent(intent,true);


        playing = getPlaying(intent,true);
        Song tempSong = new Song(track, artist, album, duration);
        // if (track == null || artist == null)
        // throw new ExceptionUnknownBroadcast();

        LOGD(TAG, track + "   " + artist);
        Pair<Long, Song> song = MusicSyncManager.getSong(tempSong, context);

        if (song == null) {
            Bundle bundle = intent.getExtras();
            id = getFromBundle(bundle, P_ID); // Using getFromBundle instead of
            // getExtraLong because
            // getFromBundle can throw
            // exception.
            duration = getDurationFromIntent(intent,true);
            streaming = UserActivity.STREAMING_TRUE;
            LOGD(TAG, "song not found in database");
        } else {
            id = song.first;
            duration = song.second.getDuration();
            streaming = UserActivity.STREAMING_FALSE;
            LOGD(TAG, "song found in database");

        }

    }

    Long getDurationFromIntent(Intent intent, boolean shouldThrowException) throws ExceptionUnknownBroadcast {
        try {
            Bundle bundle = intent.getExtras();
            Object value = bundle.get(P_DURATION);
            if (value != null) {
                if (value.toString().equals("")) return Long.valueOf(0);
                return Long.parseLong(value.toString());
            }

            value = bundle.get(P_DURATION_2);
            if (value != null) {
                if (value.toString().equals("")) return Long.valueOf(0);
                return Long.parseLong(value.toString());
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new ExceptionUnknownBroadcast();
        }

        if (shouldThrowException) throw  new ExceptionUnknownBroadcast();
        return null;

    }

    boolean getPlaying(Intent intent, boolean shouldThrowException) throws ExceptionUnknownBroadcast {
        if (intent.hasExtra(P_PLAYING))
            return intent.getBooleanExtra(P_PLAYING,false);

        if (intent.hasExtra(P_PLAYING_2))
            return  intent.getBooleanExtra(P_PLAYING_2,false);

        if (shouldThrowException) throw new ExceptionUnknownBroadcast();
        return false;
    }

    private long getFromBundle(Bundle bundle, String key)
            throws ExceptionUnknownBroadcast {
        Object value = bundle.get(key);
        if (value == null)
            throw new ExceptionUnknownBroadcast();
        try {
        return Long.parseLong(value.toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
            throw new ExceptionUnknownBroadcast();
        }

    }

    public BroadcastSong getSong() {
        return new BroadcastSong(getID(), getTrack(), getArtist(), getAlbum(),
                getDuration(), isStreaming());
    }

    public long getID() {
        return id;
    }

    public String getTrack() {
        return track;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public long getDuration() {
        return duration;
    }

    public int isStreaming() {
        return streaming;

    }

    public boolean isPlaying() {
        return playing;
    }

}
