package com.blackMonster.suzik.musicstore.Timeline;

import android.content.Context;

import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.sync.music.InAapSongTable;

public class TimelineItem implements Playable{
    protected Song song;
    protected long id;
    protected String albumArtPath;
    protected String songPath;

    private InAapSongTable.InAppSongData inAppSongMirror;

    public TimelineItem(Song song, long id, String albumArtUrl, String songUrl, Context context) {
        this.song = song;
        this.id = id;
        this.albumArtPath = albumArtUrl;
        this.songPath = songUrl;
        setInappMirrorIfAvailable(context);
	}

    public void setInappMirrorIfAvailable(Context context) {
        inAppSongMirror = InAapSongTable.getDataFromServerId(id,context);
      }


    public InAapSongTable.InAppSongData getInAppSongMirror() {
        return inAppSongMirror;
    }

    public String getOnlineAlbumArtUrl() {
        return albumArtPath.replace("1200x1200", "400x400");
    }

    @Override
    public boolean isCached() {
        return inAppSongMirror != null;
    }

    @Override
    public boolean isOffline() {
      return isCached();
    }


    @Override
    public Song getSong() {
        if (isCached()) return  inAppSongMirror.getSong();
        else  return song;
    }

    @Override
    public long getId() {
       if (isCached()) return  inAppSongMirror.getId();
        else return  id;
    }

    @Override
    public String getAlbumArtPath() {
        if (isCached()) return  inAppSongMirror.getAlbumartLocation();
        else return  getOnlineAlbumArtUrl();
    }

    @Override
    public String getSongPath() {
        if (isCached()) return inAppSongMirror.getSongLocation();
        else  return  songPath;
    }


    @Override
    public String toString() {
        return "TimelineItem [song=" + song + ", id=" + id + ", albumArt="
                + albumArtPath + ", audio=" + songPath + "]";
    }


}
