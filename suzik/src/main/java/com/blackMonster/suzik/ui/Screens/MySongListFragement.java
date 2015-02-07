package com.blackMonster.suzik.ui.Screens;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.R;
import com.blackMonster.suzik.musicPlayer.UIcontroller;
import com.blackMonster.suzik.sync.music.AddedSongsResponseHandler;
import com.blackMonster.suzik.sync.music.InAapSongTable;
import com.blackMonster.suzik.ui.MySongsAdapter;
import com.blackMonster.suzik.ui.UiBroadcasts;

import java.math.BigDecimal;

import static com.blackMonster.suzik.util.LogUtils.LOGD;


public class MySongListFragement extends Fragment implements OnItemClickListener{
    private static final String TAG = "MySongListFragement";

    ListView listView;
    MySongsAdapter adapter;
    Cursor androidCursor, inAppCursor;
    UIcontroller uiController;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerReceivers();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.list_view,
                container, false);


        uiController=UIcontroller.getInstance(getActivity());
        listView = (ListView) rootView.findViewById(R.id.list_view);
        loadData();

        adapter = new MySongsAdapter(androidCursor, inAppCursor, getActivity());
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);
        return rootView;


    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {

       //Everyting you do here is reflected in serarch result fragment

        LOGD(TAG,"setuservisiblehint " + isVisibleToUser);
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser) {
            showTryAgainMessageIfNecessary();
        }
    }

    void showTryAgainMessageIfNecessary() {

        if (!MainPrefs.isFirstTimeMusicSyncDone(getActivity())) {
            long time = AddedSongsResponseHandler.getRemainingTimeMs(getActivity());
            double ftime = time / (double)AppConfig.MINUTE_IN_MILLISEC;
            String message;

            Double truncatedDouble=new BigDecimal(ftime ).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();

            if (ftime == 0)
                message = "Syncing music with servers";
            else
                message = "Syncing music with servers. Will Complete in  " + truncatedDouble + " minutes from the time it started." ;
            Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
        }
    }

    public void loadData() {
        if (MainPrefs.getFirstTimeSongPostedToServer(getActivity())) {
            androidCursor = getAndroidSongs();
            inAppCursor = getInAppSong();
        } else {
            androidCursor = getAndroidSongs();
            inAppCursor = null;
        }

    }

   public  Cursor getInAppSong() {
       return InAapSongTable.getAllDataCursor(getActivity());
    }



   public Cursor getAndroidSongs() {

        Uri URI = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
        final String[] projection = new String[]{MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA, MediaStore.Audio.Media.DISPLAY_NAME, MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID};


        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE
                + " COLLATE LOCALIZED ASC";

        return getActivity().getContentResolver().query(URI, projection,
                selection, null, sortOrder);
    }


    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, final int position, long arg3) {

        LOGD(TAG, " " + position);
        uiController.setList(adapter);
        uiController.setSongpos(position);


    }




    private BroadcastReceiver broadcastMusicDataChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LOGD(TAG, "received : broadcastMusicDataChanged");
            loadData();
            adapter.updateCursors(androidCursor, inAppCursor);

        }
    };

    private BroadcastReceiver broadcastPlayerDataUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "uiUpdate called");
            adapter.notifyDataSetChanged();
//            adapter.updatePlayingOnSongChange(listView);
        }
    };


    private void unregisterReceivers() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
                broadcastMusicDataChanged);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
                broadcastPlayerDataUpdate);

    }

    private void registerReceivers() {

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                broadcastMusicDataChanged,
                new IntentFilter(UiBroadcasts.MUSIC_DATA_CHANGED));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                broadcastPlayerDataUpdate,
                new IntentFilter(UIcontroller.brodcast_uidataupdate));

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
    }


}
