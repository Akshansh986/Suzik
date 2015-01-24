package com.blackMonster.suzik.sync.music;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.sync.music.AllSongsTable.AllSongData;

import java.util.HashSet;
import java.util.List;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGE;

public class InAapSongTable {


    public static final String TAG = "InAppSongTable";
    public static final String C_ID = "id";
    public static final String C_FPRINT = "fPrint";
    public static final String C_ALBUMART_LINK = "albumartLink";
    public static final String C_SONG_LINK = "songLink";
    public static final String C_SONG_LOCATION = "songLocation";
    public static final String C_ALBUM_ART_LOCATION = "albumartLocation";

    private static final String TABLE = "InAppSongTable";

    public static void createTable(SQLiteDatabase db) {
        String sql = String
                .format("create table %s"
                                + "(%s INTEGER primary key,%s text,%s text, %s text, %s text, %s text, FOREIGN KEY (%s) REFERENCES %s (%s) )",
                        TABLE, C_ID, C_FPRINT, C_ALBUMART_LINK, C_SONG_LINK,
                        C_SONG_LOCATION, C_ALBUM_ART_LOCATION, C_ID, AllSongsTable.TABLE,
                        AllSongsTable.C_LOCAL_ID);
        db.execSQL(sql);
    }

    public static InAppSongData getData(long id, Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        AllSongData asd = AllSongsTable.getSong(id, context);
        InAppSongData inAppSongData = null;
        if (asd != null) {
            Cursor cursor = db.query(TABLE, null, C_ID + "='" + id + "'", null,
                    null, null, null);

            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    String fp = cursor.getString(cursor
                            .getColumnIndex(C_FPRINT));
                    String albumartLink = cursor.getString(cursor
                            .getColumnIndex(C_ALBUMART_LINK));
                    String songLink = cursor.getString(cursor
                            .getColumnIndex(C_SONG_LINK));
                    String songLocation = cursor.getString(cursor
                            .getColumnIndex(C_SONG_LOCATION));
                    String albumartLocation = cursor.getString(cursor
                            .getColumnIndex(C_ALBUM_ART_LOCATION));
                    inAppSongData = new InAppSongData(id, asd.getServerId(),
                            asd.getSong(), fp, albumartLink, songLink, songLocation, albumartLocation);
                }
                cursor.close();
            }
        }

        return inAppSongData;
    }


    public static InAppSongData getDataFromServerId(long serverId, Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

//        String[] args = {InAapSongTable.TABLE,AllSongsTable.TABLE,AllSongsTable.C_SERVER_ID,serverId +"",InAapSongTable.C_ID, AllSongsTable.C_LOCAL_ID};

        String query = String.format("SELECT t1.*, t2.* FROM %s t1, %s t2 WHERE t2.%s = %s and t1.%s = t2.%s",
                InAapSongTable.TABLE, AllSongsTable.TABLE, AllSongsTable.C_SERVER_ID, serverId + "",
                InAapSongTable.C_ID, AllSongsTable.C_LOCAL_ID);


        Cursor cursor = db.rawQuery(query, null);

        InAppSongData inAppSongData = null;

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                long id = cursor.getLong(cursor
                        .getColumnIndex(C_ID));
                String fp = cursor.getString(cursor
                        .getColumnIndex(C_FPRINT));
                String albumartLink = cursor.getString(cursor
                        .getColumnIndex(C_ALBUMART_LINK));
                String songLink = cursor.getString(cursor
                        .getColumnIndex(C_SONG_LINK));
                String songLocation = cursor.getString(cursor
                        .getColumnIndex(C_SONG_LOCATION));
                String albumartLocation = cursor.getString(cursor
                        .getColumnIndex(C_ALBUM_ART_LOCATION));

                Song song = new Song(cursor.getString(cursor
                        .getColumnIndex(AllSongsTable.C_TITLE)), cursor.getString(cursor
                        .getColumnIndex(AllSongsTable.C_ARTIST)), cursor.getString(cursor
                        .getColumnIndex(AllSongsTable.C_ALBUM)), cursor.getLong(cursor
                        .getColumnIndex(AllSongsTable.C_DURATION)));

                inAppSongData = new InAppSongData(id, serverId,
                        song, fp, albumartLink, songLink, songLocation, albumartLocation);
            }
            cursor.close();
        }


        return inAppSongData;
    }


    public static InAppSongData getData(String fingerPrint, Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

        InAppSongData inAppSongData = null;

        Cursor cursor = db.query(TABLE, null, C_FPRINT + "=?",
                new String[]{fingerPrint}, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                String fp = cursor.getString(cursor.getColumnIndex(C_FPRINT));
                String albumartLink = cursor.getString(cursor
                        .getColumnIndex(C_ALBUMART_LINK));
                String songLink = cursor.getString(cursor
                        .getColumnIndex(C_SONG_LINK));

                String songLocation = cursor.getString(cursor
                        .getColumnIndex(C_SONG_LOCATION));
                String albumartLocation = cursor.getString(cursor
                        .getColumnIndex(C_ALBUM_ART_LOCATION));
                long id = cursor.getLong(cursor.getColumnIndex(C_ID));

                AllSongData asd = AllSongsTable.getSong(id, context);
                if (asd != null)
                    inAppSongData = new InAppSongData(id, asd.getServerId(),
                            asd.getSong(), fp, albumartLink, songLink, songLocation, albumartLocation);

            }
            cursor.close();
        }

        return inAppSongData;
    }

    public static HashSet<InAppSongData> getAllData(Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        HashSet<InAppSongData> inAppSongDataSet = null;

        Cursor cursor = db.query(TABLE, null, null, null, null, null, null);

        if (cursor != null) {
            inAppSongDataSet = new HashSet<InAppSongData>();
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                long id = cursor.getLong(cursor.getColumnIndex(C_ID));
                String fp = cursor.getString(cursor.getColumnIndex(C_FPRINT));
                String albumartLink = cursor.getString(cursor
                        .getColumnIndex(C_ALBUMART_LINK));
                String songLink = cursor.getString(cursor
                        .getColumnIndex(C_SONG_LINK));
                String songLocation = cursor.getString(cursor
                        .getColumnIndex(C_SONG_LOCATION));
                String albumartLocation = cursor.getString(cursor
                        .getColumnIndex(C_ALBUM_ART_LOCATION));
                AllSongData asd = AllSongsTable.getSong(id, context);
                if (asd != null)
                    inAppSongDataSet.add(new InAppSongData(id, asd
                            .getServerId(), asd.getSong(), fp, albumartLink,
                            songLink, songLocation, albumartLocation));
                else
                    LOGE(TAG, "Song no found");

                cursor.moveToNext();

            }

            cursor.close();

        }

        return inAppSongDataSet;
    }


    public static Cursor getAllDataCursor(Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.rawQuery(

                "SELECT id as _id, t1.*, t2.* FROM " + InAapSongTable.TABLE + " t1, " + AllSongsTable.TABLE + " t2 WHERE t1." + InAapSongTable.C_ID + " = t2." + AllSongsTable.C_LOCAL_ID,
                null);

        return cursor;
    }

    public static Cursor getAllDataCursorLike(String search, Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();

        Cursor cursor = db.rawQuery(
                "SELECT id as _id, t1.*, t2.* FROM " +
                        InAapSongTable.TABLE + " t1, " + AllSongsTable.TABLE + " t2 WHERE t1." + InAapSongTable.C_ID
                        + " = t2." + AllSongsTable.C_LOCAL_ID + " and t2." + AllSongsTable.C_TITLE + " LIKE '%" + search + "%'",
                null);

        return cursor;
    }


    public static long insert(InAppSongData inAppSongData, Context context) {
        LOGD(TABLE, "insert");
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

        long localId = AllSongsTable.insert(
                new AllSongData(null, inAppSongData.getServerId(),
                        inAppSongData.getSong()), context);

        ContentValues values = new ContentValues();

        values.put(C_ID, localId);
        values.put(C_FPRINT, inAppSongData.getfPrint());
        values.put(C_ALBUMART_LINK, inAppSongData.getAlbumartLink());
        values.put(C_SONG_LINK, inAppSongData.getSongLink());
        values.put(C_SONG_LOCATION, inAppSongData.getSongLocation());
        values.put(C_ALBUM_ART_LOCATION, inAppSongData.getAlbumartLocation());

        db.insert(TABLE, null, values);
        return localId;
    }

    public static void insert(List<InAppSongData> inAppSongData, Context context) {
        LOGD(TABLE, "Bulk insert");

        for (InAppSongData data : inAppSongData) {
            insert(data, context);
        }
    }








    public static boolean updateAlbumArtLocation(long id, String newLocation, Context context) {
        LOGD(TABLE, "update");
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

        ContentValues updates = new ContentValues();
        updates.put(C_ALBUM_ART_LOCATION, newLocation);


        return db.update(TABLE, updates, C_ID + "='" + id + "'", null) > 0 ;

    }

    public static boolean updateSongLocation(long id, String newLocation, Context context) {
        LOGD(TABLE, "update");
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

        ContentValues updates = new ContentValues();
        updates.put(C_SONG_LOCATION, newLocation);


        return db.update(TABLE, updates, C_ID + "='" + id + "'", null) > 0 ;

    }

    public static boolean remove(Long id, Context context) {
        if (id == null) return true;
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();
        boolean rr = AllSongsTable.remove(id, context);
        boolean r = db.delete(TABLE, C_ID + "='" + id + "'", null) > 0;
        return r && rr;
    }

    public static class InAppSongData {
        private Long id;
        private long serverId;
        private Song song;
        private String fPrint, albumartLink, songLink, songLocation, albumartLocation;

        public InAppSongData(Long id, long serverId, Song song, String fPrint,
                             String albumartLink, String songLink, String songLocation, String albumartLocation) {
            super();
            this.id = id;
            this.serverId = serverId;
            this.song = song;
            this.fPrint = fPrint;
            this.albumartLink = albumartLink;
            this.songLink = songLink;
            this.songLocation = songLocation;
            this.albumartLocation = albumartLocation;
        }

        public Long getId() {
            return id;
        }

        public long getServerId() {
            return serverId;
        }

        public Song getSong() {
            return song;
        }

        public String getfPrint() {
            return fPrint;
        }

        public String getAlbumartLink() {
            return albumartLink;
        }

        public String getSongLink() {
            return songLink;
        }

        public String getAlbumartLocation() {
            return albumartLocation;
        }

        public String getSongLocation() {
            return songLocation;
        }

        public void setAlbumartLink(String link) {
            this.albumartLink = link;
        }

        public void setSongLink(String link) {
            this.songLink = link;
        }

        public void setSongLocation(String songLocation) {
            this.songLocation = songLocation;
        }

        public void setAlbumartLocation(String albumartLocation) {
            this.albumartLocation = albumartLocation;
        }

        @Override
        public String toString() {
            return "InAppSongData [id=" + id + ", serverId=" + serverId
                    + ", song=" + song + ", fPrint=" + fPrint
                    + ", albumartLink=" + albumartLink + ", songLink="
                    + songLink + ", location=" + songLocation + "]";
        }

    }
}
