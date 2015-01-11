package com.blackMonster.suzik.ui;

import com.blackMonster.suzik.musicstore.Timeline.Playable;
import com.blackMonster.suzik.musicstore.module.Song;

/**
 * Created by akshanshsingh on 11/01/15.
 */
public class MySong implements Playable {
    protected long id;
    protected Song song;
    protected String localSongPath;
    protected String localAlbumArtPath;
    private String songUrl,albumArtUrl;
    boolean isCached;

    public MySong(long id, Song song, String localSongPath, String localAlbumArtPath, String songUrl, String albumArtUrl, boolean isCached) {
        this.id = id;
        this.song = song;
        this.localSongPath = localSongPath;
        this.localAlbumArtPath = localAlbumArtPath;
        this.songUrl = songUrl;
        this.albumArtUrl = albumArtUrl;
        this.isCached = isCached;
    }

    @Override
    public boolean isOffline() {
        return true;
    }

    @Override
    public boolean isCached() {
        return isCached;
    }

    @Override
    public Song getSong() {
        return song;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public String getAlbumArtPath() {
        return localAlbumArtPath;
    }

    @Override
    public String getSongPath() {
        return localSongPath;
    }
}
