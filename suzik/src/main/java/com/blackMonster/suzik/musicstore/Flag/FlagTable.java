package com.blackMonster.suzik.musicstore.Flag;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.blackMonster.suzik.DbHelper;

/**
 * Created by akshanshsingh on 28/01/15.
 */
public class FlagTable {

    public static final String TABLE = "FlagTable";
    public static final String C_SERVER_ID = "ID";

    public static void createTable(SQLiteDatabase db) {
        String sql = String
                .format("create table %s"
                                + "(%s INTEGER PRIMARY KEY)",
                        TABLE, C_SERVER_ID);
        db.execSQL(sql);
    }

    public static void insert(long serverId,
                              Context context) {
        Log.d(TABLE, "insert");
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(C_SERVER_ID, serverId);
        db.insert(TABLE, null, values);

    }

    public static boolean isPresent(long serverId, Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getReadableDatabase();
        Integer result = null;
        Cursor cursor = db.query(TABLE, null, C_SERVER_ID + "='" + serverId + "'", null, null, null, null);

        if (cursor != null) {
            result = cursor.getCount();
            cursor.close();
        }

        return result > 0;
    }


    public static boolean remove(long serverId, Context context) {
        SQLiteDatabase db = DbHelper.getInstance(context).getWritableDatabase();

        return db.delete(TABLE, C_SERVER_ID + "='" + serverId + "'", null) > 0;
    }

    public static int clearTable(Context context) {
        return DbHelper.getInstance(context).getWritableDatabase()
                .delete(TABLE, null, null);
    }
}
