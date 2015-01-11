package com.blackMonster.suzik.ui;

import com.blackMonster.suzik.musicstore.Timeline.Playable;

/**
 * Created by akshanshsingh on 11/01/15.
 */
public interface Playlist {

    public Playable getPlayable(int position);
    public int getSongCount();
}
