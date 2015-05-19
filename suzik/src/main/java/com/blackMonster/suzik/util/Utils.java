package com.blackMonster.suzik.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.blackMonster.suzik.ui.FileDownloader;

public class Utils {

	public static String formatPhoneNumberForJson(String number) {
		if (number==null) return null;
		return number.replace("+", "%2B");
	}


    public static String formatStringForUIL(String s) {

        if (NetworkUtils.isValidUrl(s)) return  s;

        if (s.contains(FileDownloader.ALBUM_ART_FILE_EXTENSION) && !s.contains("file://"))
            return "file://" + s;

        return s;

    }


    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    public static Uri getAndroidAlumartUri(int albumId) {
        return ContentUris.withAppendedId(sArtworkUri, albumId);
    }

    public static String getContactName(Context context, String phoneNumber) {
        ContentResolver cr = context.getContentResolver();
        Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
        Cursor cursor = cr.query(uri, new String[]{ContactsContract.PhoneLookup.DISPLAY_NAME}, null, null, null);
        if (cursor == null) {
            return null;
        }
        String contactName = null;
        if(cursor.moveToFirst()) {
            contactName = cursor.getString(cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME));
        }

        if(cursor != null && !cursor.isClosed()) {
            cursor.close();
        }

        return contactName;
    }


}
