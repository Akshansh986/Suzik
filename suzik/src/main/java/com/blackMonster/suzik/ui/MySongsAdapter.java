package com.blackMonster.suzik.ui;


import android.widget.BaseAdapter;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

import android.content.Context;
import android.database.Cursor;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blackMonster.suzik.R;
import com.blackMonster.suzik.musicstore.Timeline.Playable;
import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.sync.music.AllSongsTable;
import com.blackMonster.suzik.sync.music.InAapSongTable;


/**
 * Created by akshanshsingh on 06/01/15.
 */
public class MySongsAdapter extends BaseAdapter implements Playlist {

    public static final String TAG = "MySongsAdapter";

    Cursor androidCurosr, inappCursor;
    int androidCount, inappCount;
    Context context;
    LayoutInflater inflater;
    LazyImageLoader lazyImageLoader;

    public MySongsAdapter(Cursor androidC, Cursor inappCursor, Context context) {
        this.androidCurosr = androidC;
        this.inappCursor = inappCursor;
        this.context = context;
        inflater = LayoutInflater.from(this.context);

        setCount();

        lazyImageLoader = new LazyImageLoader(context);
    }

    private void setCount() {
        if (inappCursor != null) inappCount = inappCursor.getCount();
        else inappCount = 0;

        if (androidCurosr != null) androidCount = androidCurosr.getCount();
        else androidCount = 0;
    }

    public void updateCursors(Cursor androidC, Cursor inappCursor) {
        this.androidCurosr = androidC;
        this.inappCursor = inappCursor;
        setCount();
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        LOGD(TAG, "count : " + inappCount + "  " + androidCount + "  " + (inappCount + androidCount));
        return inappCount + androidCount;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public int getActualCursorPosition(int position) {
        if (position >= inappCount) return position - inappCount;
        else return position;
    }

    public boolean isAndroidSong(int position) {
        return position >= inappCount;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        if (convertView == null) {
            convertView = inflater.inflate(R.layout.my_songs_list_row, null);
        }

        Playable playable =  getPlayable(position);

        ((TextView) convertView.findViewById(R.id.inapp_title)).setText(playable.getSong().getTitle());
        ((TextView) convertView.findViewById(R.id.inapp_artist)).setText(playable.getSong().getArtist());

        ImageView imv = ((ImageView) convertView.findViewById(R.id.inapp_image));

        if (playable.isCached()) {
            String location = playable.getAlbumArtPath();
            LOGD(TAG,playable.getSong().getTitle() + "  " + location);
            if (FileDownloader.doesFileExist(location)) lazyImageLoader.loadBitmap(location, imv, playable.isCached());
            else  imv.setImageBitmap(lazyImageLoader.defaultImage);
        }
        else {
            LOGD(TAG,playable.getSong().getTitle() + "  " + playable.getAlbumArtPath());

            lazyImageLoader.loadBitmap(playable.getAlbumArtPath(), imv,playable.isCached());
        }

//        String title, artist;
//        int pos;
//        if (isAndroidSong(position)) {
//            pos = getActualCursorPosition(position);
//            LOGD(TAG, "outapp " + position + "  " + pos);
//            androidCurosr.moveToPosition(pos);
//            LOGD(TAG, "done " + position + "  " + pos);
//
//            title = androidCurosr.getString(androidCurosr.getColumnIndex(MediaStore.Audio.Media.TITLE));
//            artist = androidCurosr.getString(androidCurosr.getColumnIndex(MediaStore.Audio.Media.ARTIST));
//            int albumId = androidCurosr.getInt(androidCurosr.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
////            Bitmap bmp = getArtwork(context,albumId);
//            ImageView imv = ((ImageView) convertView.findViewById(R.id.inapp_image));
//
//            lazyImageLoader.loadBitmap(albumId, imv);
//
//
//        } else {
//            LOGD(TAG, "inapp " + position);
//            pos = getActualCursorPosition(position);
//            inappCursor.moveToPosition(pos);
//            title = inappCursor.getString(inappCursor.getColumnIndex(AllSongsTable.C_TITLE));
//            artist = inappCursor.getString(inappCursor.getColumnIndex(AllSongsTable.C_ARTIST));
//            ImageView imv = ((ImageView) convertView.findViewById(R.id.inapp_image));
//
//            String location = inappCursor.getString(inappCursor.getColumnIndex(InAapSongTable.C_ALBUM_ART_LOCATION));
//
//            if (FileDownloader.doesFileExist(location)) lazyImageLoader.loadBitmap(location, imv);
//            else {
//                imv.setImageBitmap(lazyImageLoader.defaultImage);
//
//                String fileName = FileDownloader.getNewAlbumArtName();
//                location = FileDownloader.getLocationFromFilename(fileName,context);
//                FileDownloader.saveImageToDisk( inappCursor.getString(inappCursor.getColumnIndex(InAapSongTable.C_ALBUMART_LINK)),
//                        location);
//
//                LOGD(TAG, inappCursor.getLong(inappCursor.getColumnIndex(InAapSongTable.C_ID)) + "  " + location );
//                InAapSongTable.updateAlbumArtLocation(inappCursor.getLong(inappCursor.getColumnIndex(InAapSongTable.C_ID)),location,context);
//
//            }
//
//
//        }
//
//        LOGD(TAG, title + "  " + artist);
//        ((TextView) convertView.findViewById(R.id.inapp_title)).setText(title);
//        ((TextView) convertView.findViewById(R.id.inapp_artist)).setText(artist);


        return convertView;
    }


    @Override
    public Playable getPlayable(int position) {

        String title, artist, album, albumartPath, songPath, songUrl, albumartUrl;
        int pos;
        long duration,id;
        boolean isCached;
        
        if (isAndroidSong(position)) {
            pos = getActualCursorPosition(position);
            
            LOGD(TAG, "outapp " + position + "  " + pos);
            androidCurosr.moveToPosition(pos);
            LOGD(TAG, "done " + position + "  " + pos);

            title = androidCurosr.getString(androidCurosr.getColumnIndex(MediaStore.Audio.Media.TITLE));
            artist = androidCurosr.getString(androidCurosr.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            album = androidCurosr.getString(androidCurosr.getColumnIndex(MediaStore.Audio.Media.ALBUM));
            duration = androidCurosr.getLong(androidCurosr.getColumnIndex(MediaStore.Audio.Media.DURATION));
            id = androidCurosr.getLong(androidCurosr.getColumnIndex(MediaStore.Audio.Media._ID));
            
            songPath = androidCurosr.getString(androidCurosr.getColumnIndex(MediaStore.Audio.Media.DATA));
            albumartPath = androidCurosr.getInt(androidCurosr.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID)) + "";

            songUrl = null;
            albumartUrl = null;
            isCached = false;


        } else {
            pos = getActualCursorPosition(position);
            inappCursor.moveToPosition(pos);

            title = inappCursor.getString(inappCursor.getColumnIndex(AllSongsTable.C_TITLE));
            artist = inappCursor.getString(inappCursor.getColumnIndex(AllSongsTable.C_ARTIST));
            album = inappCursor.getString(inappCursor.getColumnIndex(AllSongsTable.C_ALBUM));
            duration = inappCursor.getLong(inappCursor.getColumnIndex(AllSongsTable.C_DURATION));
            id = inappCursor.getLong(inappCursor.getColumnIndex(InAapSongTable.C_ID));

            songPath = inappCursor.getString(inappCursor.getColumnIndex(InAapSongTable.C_SONG_LOCATION));
            albumartPath = inappCursor.getString(inappCursor.getColumnIndex(InAapSongTable.C_ALBUM_ART_LOCATION));

            songUrl = inappCursor.getString(inappCursor.getColumnIndex(InAapSongTable.C_SONG_LINK));
            albumartUrl = inappCursor.getString(inappCursor.getColumnIndex(InAapSongTable.C_ALBUMART_LINK));
            isCached = true;

        }

        return  new MySong(id, new Song(title,artist,album,duration),songPath,albumartPath,songUrl,albumartUrl,isCached);
    }

    @Override
    public int getSongCount() {
        return getCount();
    }
}
