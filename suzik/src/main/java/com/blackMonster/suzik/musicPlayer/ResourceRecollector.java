package com.blackMonster.suzik.musicPlayer;

import android.content.Context;

import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.sync.music.InAapSongTable;
import com.blackMonster.suzik.ui.FileDownloader;
import com.blackMonster.suzik.ui.UiBroadcasts;
import com.blackMonster.suzik.util.NetworkUtils;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

/**
 * Created by akshanshsingh on 25/01/15.
 */
public class ResourceRecollector {

    public static final String TAG = "ResourceRecollector";

    public static void saveAlbumart(final long id, final String albumartUrl, final Context context) {
        if (!NetworkUtils.isInternetAvailable(context) || !NetworkUtils.isValidUrl(albumartUrl)) return;

        new Thread() {

            public void run() {
                LOGD(TAG, "saving albumart");
                String albumartLocation = FileDownloader.getLocationFromFilename(FileDownloader.getNewAlbumArtName(), context);
                FileDownloader.saveImageToDisk(albumartUrl, albumartLocation);
                InAapSongTable.updateAlbumArtLocation(id, albumartLocation, context);
                UiBroadcasts.broadcastMusicDataChanged(context);
            }
        }.start();


    }

    public static void saveSong(final long id,final Song song,final String songUrl,final Context context) {
        if (!NetworkUtils.isInternetAvailable(context) || !NetworkUtils.isValidUrl(songUrl)) return;

        new Thread() {

            public void run() {
                LOGD(TAG, "saving song");
                String songFileName = FileDownloader.getNewSongFileName();
                String songLocation = FileDownloader.getLocationFromFilename(songFileName, context);

                FileDownloader.saveSongToDisk(song.getTitle(),song.getArtist(),songUrl,songFileName,context);
                InAapSongTable.updateSongLocation(id,songLocation,context);
                UiBroadcasts.broadcastMusicDataChanged(context);
            }
        }.start();

    }
}
