package com.blackMonster.suzik.ui.Screens;

import android.database.Cursor;

/**
 * Created by akshanshsingh on 07/01/15.
 */
public class SearchResultFragment extends MySongListFragement {



    @Override
    public void loadData() {
            androidCursor = null;
            inAppCursor = null;
    }

    public void setData(Cursor andrCursor, Cursor inAppCursor) {
        adapter.updateCursors(andrCursor,inAppCursor);
    }
}
