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
import com.blackMonster.suzik.sync.music.AllSongsTable;


/**
 * Created by akshanshsingh on 06/01/15.
 */
public class MySongsAdapter extends BaseAdapter {

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

        if (androidCurosr !=null) androidCount = androidCurosr.getCount();
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

    public int getActualPosition(int position) {
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

        String title, artist;
        int pos;
        if (isAndroidSong(position)) {
            pos = getActualPosition(position);
            LOGD(TAG, "outapp " + position + "  " + pos);
            androidCurosr.moveToPosition(pos);
            LOGD(TAG, "done " + position + "  " + pos);

            title = androidCurosr.getString(androidCurosr.getColumnIndex(MediaStore.Audio.Media.TITLE));
            artist = androidCurosr.getString(androidCurosr.getColumnIndex(MediaStore.Audio.Media.ARTIST));
            int albumId = androidCurosr.getInt(androidCurosr.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID));
//            Bitmap bmp = getArtwork(context,albumId);
            ImageView imv = ((ImageView) convertView.findViewById(R.id.inapp_image));

            lazyImageLoader.loadBitmap(albumId, imv);


        } else {
            LOGD(TAG, "inapp " + position);
             pos = getActualPosition(position);
            inappCursor.moveToPosition(pos);
            title = inappCursor.getString(inappCursor.getColumnIndex(AllSongsTable.C_TITLE));
            artist = inappCursor.getString(inappCursor.getColumnIndex(AllSongsTable.C_ARTIST));
            ImageView imv = ((ImageView) convertView.findViewById(R.id.inapp_image));
            imv.setImageBitmap(lazyImageLoader.defaultImage);

        }

        LOGD(TAG, title + "  " + artist);
        ((TextView) convertView.findViewById(R.id.inapp_title)).setText(title);
        ((TextView) convertView.findViewById(R.id.inapp_artist)).setText(artist);


        return convertView;
    }


}
