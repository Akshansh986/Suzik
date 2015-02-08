package com.blackMonster.suzik.musicstore.Flag;

import android.content.Context;

/**
 * Created by akshanshsingh on 28/01/15.
 */
public  class Flag {
    private boolean display, serverBadSong, localBadSong;

    public Flag(long serverId, boolean display, boolean serverBadSong, Context context) {
        this.display = display;
        this.serverBadSong = serverBadSong;
        localBadSong = FlagTable.isPresent(serverId,context);
    }

    public boolean shouldDisplay() {
        return display;
    }

    public boolean isServerBadSong() {
        return serverBadSong;
    }

    public boolean isLocalBadSong() {
        return localBadSong;
    }

    public void setLocalBadSong(boolean localBadSong) {
        this.localBadSong = localBadSong;
    }
}