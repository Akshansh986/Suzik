package com.blackMonster.suzik.musicstore.Timeline;

import com.blackMonster.suzik.musicstore.module.Song;

/**
 * Created by akshanshsingh on 10/01/15.
 */
public interface Playable {

    public boolean isOffline();
    public boolean isCached();


    public Song getSong();

    public long getId();
//
//    public String getLowAlbumArt() {
//        if (albumArtPath == null) return null;
//        return albumArtPath.replace("1200x1200", "100x100");
//    }

    public String getAlbumArtPath() ;

//    public String getHighAlbumArt() {
//        return albumArtPath;
//    }

    public String getSongPath();
}
