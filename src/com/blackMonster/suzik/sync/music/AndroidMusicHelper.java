package com.blackMonster.suzik.sync.music;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import com.blackMonster.suzik.musicstore.module.Song;

public class AndroidMusicHelper {
	public static Uri URI = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;


	 static List<AndroidData> getAllMySongs(Context context)
			throws Exception {
		List<AndroidData> androidDataSet = null;

		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		final String[] projection = new String[] {
				MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION,
				MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DISPLAY_NAME };
		final String sortOrder = MediaStore.Audio.AudioColumns.TITLE
				+ " COLLATE LOCALIZED ASC";
		Cursor cursor = null;
		try {

			cursor = context.getContentResolver().query(URI, projection,
					selection, null, sortOrder);

			androidDataSet = new ArrayList<AndroidData>();

			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
				if (title.equals("<unknown>") || title.equals("")) 
					LOGD("AndroidHelper","title unknown");
				Song song = new Song(cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST)),
						cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM)),
						cursor.getLong(cursor
								.getColumnIndex(MediaStore.Audio.Media.DURATION)));
						

				String path = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.DATA));
				path = path.replace("/storage/emulated/0", "");
				path = "/sdcard" + path;
				
				String fileName = cursor.getString(cursor
						.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
				
				AndroidData d = new AndroidData(path, fileName,song);
				androidDataSet.add(d);
				LOGD("searching ", d.toString());
				cursor.moveToNext();
			}

		} catch (Exception ex) {
			throw ex;

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		return androidDataSet;

	}

	static class AndroidData {
	

		private String location = "";
		private String fPrint = "";
		private Song song;
		private String fileName ="";
		
				
		public AndroidData(String location,String fileName, Song song) {
			super();
			this.location = location;
			this.fileName = fileName;
			this.song = song;
		}
		String getLocation() {
			return location;
		}
		
		String getfPrint() {
			return fPrint;
		}
		String getFileName() {
			return fileName;
		}
		void setfPrint(String fPrint) {
			this.fPrint = fPrint;
		}
		Song getSong() {
			return song;
		}
		

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((fPrint == null) ? 0 : fPrint.hashCode());
			result = prime * result
					+ ((fileName == null) ? 0 : fileName.hashCode());
			result = prime * result
					+ ((location == null) ? 0 : location.hashCode());
			result = prime * result + ((song == null) ? 0 : song.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			AndroidData other = (AndroidData) obj;
			if (fPrint == null) {
				if (other.fPrint != null)
					return false;
			} else if (!fPrint.equals(other.fPrint))
				return false;
			if (fileName == null) {
				if (other.fileName != null)
					return false;
			} else if (!fileName.equals(other.fileName))
				return false;
			if (location == null) {
				if (other.location != null)
					return false;
			} else if (!location.equals(other.location))
				return false;
			if (song == null) {
				if (other.song != null)
					return false;
			} else if (!song.equals(other.song))
				return false;
			return true;
		}





				
	
		
		@Override
		public String toString() {
			return "AndroidSongData [location=" + location + ", fPrint="
					+ fPrint + ", song=" + song.toString() + "]";
		}
		

	}

}
