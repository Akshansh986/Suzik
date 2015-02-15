package com.blackMonster.suzik.ui.Screens;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.blackMonster.suzik.musicPlayer.UIcontroller;
import com.blackMonster.suzik.musicstore.Timeline.Playable;
import com.blackMonster.suzik.ui.Playlist;

/**
 * Created by akshanshsingh on 07/01/15.
 */
public class SearchResultFragment extends MySongListFragement {

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        showTryAgainMessageIfNecessary();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void loadData() {
            androidCursor = null;
            inAppCursor = null;
    }

    public void setData(Cursor andrCursor, Cursor inAppCursor) {
        adapter.updateCursors(andrCursor,inAppCursor);
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {

        final Playable playable = adapter.getPlayable(position);

        Playlist playlist = new Playlist() {
            @Override
            public Playable getPlayable(int position) {
                return playable;
            }

            @Override
            public int getSongCount() {
                return 1;
            }
        };

        UIcontroller.getInstance(getActivity()).setList(playlist);
        UIcontroller.getInstance(getActivity()).setSongpos(0);

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }
}
