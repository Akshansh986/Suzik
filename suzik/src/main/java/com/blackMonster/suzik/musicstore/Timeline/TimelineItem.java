package com.blackMonster.suzik.musicstore.Timeline;

import android.content.Context;

import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.musicstore.Flag.Flag;
import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.sync.music.InAapSongTable;

public class TimelineItem implements Playable{
    protected Song song;
    protected long id;
    protected String albumArtPath;
    protected String songPath;

    private InAapSongTable.InAppSongData inAppSongMirror=null;

    private Flag flag;

    public TimelineItem(Song song, long id, String albumArtUrl, String songUrl,Flag flag, Context context) {
        this.song = song;
        this.id = id;
        this.albumArtPath = albumArtUrl;
        this.songPath = songUrl;
        this.flag = flag;
        setInappMirrorIfAvailable(context);
	}

    public void setInappMirrorIfAvailable(Context context) {
        if (!MainPrefs.getFirstTimeSongPostedToServer(context)) return;
        inAppSongMirror = InAapSongTable.getDataFromServerId(id,context);
      }

    public long getServerId() {
        return id;
    }

    public Flag getFlag() {
        return flag;
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
    public Playable getAlternatePlayable() {

        if (!isCached()) return null;

        return new Playable() {
            @Override
            public boolean isOffline() {
                return false;
            }

            @Override
            public boolean isCached() {
                return false;
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
                return getOnlineAlbumArtUrl();
            }

            @Override
            public String getSongPath() {
                return songPath;
            }

            @Override
            public Playable getAlternatePlayable() {
                return null;
            }
        };



    }


    @Override
    public String toString() {
        return "TimelineItem [song=" + song + ", id=" + id + ", albumArt="
                + albumArtPath + ", audio=" + songPath + "]";
    }



}
