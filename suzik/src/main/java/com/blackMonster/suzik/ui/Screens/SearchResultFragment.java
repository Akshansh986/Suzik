package com.blackMonster.suzik.ui.Screens;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

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
//        super.onItemClick(arg0, arg1, position, arg3);
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
    }
}
