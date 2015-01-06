package com.blackMonster.suzik.ui.Screens;

import  static com.blackMonster.suzik.util.LogUtils.*;
import android.app.SearchManager;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;


import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;

import com.blackMonster.suzik.R;
import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.ui.Screens.MySongListFragement;

/**
 * Created by akshanshsingh on 03/01/15.
 */
public class MainActivity extends ActionBarActivity implements SearchView.OnQueryTextListener{
    public static final String TAG = "MainActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);




//        Uri URI = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
//
//
//
//        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
//        final String[] projection = new String[] { "'1' as identifier",
//                MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
//                MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION,
//                MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DISPLAY_NAME ,  MediaStore.Audio.Media._ID + " as  id",
//                MediaStore.Audio.Media.ALBUM_ID };
//
//
//
//
//        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE
//                + " COLLATE LOCALIZED ASC";
//
//        Cursor    androidCursor = getContentResolver().query(URI, projection,
//                    selection, null, sortOrder);
//
//        Cursor inAppCursor = InAapSongTable.getAllDataCursor(this);
//
//
//        Cursor[] a = {inAppCursor,androidCursor};
//        MergeCursor mc = new MergeCursor(a);
//
//
//
//
//        String[] arr = androidCursor.getColumnNames();
//        for (String s : arr) {
//            LOGD(TAG,s);
//        }
//        LOGI(TAG,"diff");
//
//        arr = inAppCursor.getColumnNames();
//        for (String s : arr) {
//            LOGD(TAG,s);
//        }
//        LOGI(TAG,"diff");
//
//        arr = mc.getColumnNames();
//        for (String s : arr) {
//            LOGD(TAG,s);
//        }
//
//
//
//
//
//        LOGD(TAG,mc.getColumnNames().toString());
//
//        LOGD(TAG,androidCursor.getCount() + "  " + inAppCursor.getCount() + "  " + mc.getCount());
//
//
//        List<FullSongInfo> fullSongInfoList = new ArrayList<FullSongInfo>();
//
//
////        LOGD(TAG,DatabaseUtils.dumpCursorToString(mc));
//        mc.moveToFirst();
//        while(mc.moveToNext()) {
////            mc.isNull(mc.getColumnIndex(AllSongsTable.C_SERVER_ID));
////            mc.
//
//            LOGD(TAG, (mc.getInt(mc.getColumnIndex("identifier")) == 1) + "  " + DatabaseUtils.dumpCurrentRowToString(mc));
//        }















//        startService(new Intent(this, SongsSyncer.class));




//
//        // find the retained fragment on activity restarts
//        FragmentManager fm = getSupportFragmentManager();
//        TimelineFragement timelineFragment = (TimelineFragement) fm.findFragmentByTag(TimelineFragement.FRAGMENT_TAG);
//
//        // create the fragment and data the first time
//        if (timelineFragment == null) {
//            // add the fragment
//            timelineFragment = new TimelineFragement();
//            fm.beginTransaction().add(R.id.mainLL,timelineFragment, TimelineFragement.FRAGMENT_TAG).commit();
//            // load the data from the web
////            timelineFragment.setData(loadMyData());
//        }



//
//
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//
//
        MySongListFragement fragment = new MySongListFragement();
//        TimelineFragement fragment = new TimelineFragement();
        fragmentTransaction.add(R.id.mainLL, fragment);
        fragmentTransaction.commit();
//



//
//        SearchView searchView = (SearchView) findViewById(R.id.search);
//        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
//        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
//
//




    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        LOGD(TAG,"OncrateOptionsmenu");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setOnQueryTextListener(this);

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));



        return true;    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        LOGD(TAG,s);
        return false;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        LOGD(TAG,"change " + s);
        return false;
    }


    public class FullSongInfo {

        Song song;
        String song_path;
        String album_path;
        boolean isAndroid;


        public FullSongInfo(Song song, String song_path, String album_path, boolean isAndroid) {
            this.song = song;
            this.song_path = song_path;
            this.album_path = album_path;
            this.isAndroid = isAndroid;
        }
    }
}
