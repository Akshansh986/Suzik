package com.blackMonster.suzik.util;

import android.content.ContentUris;
import android.net.Uri;

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

}
