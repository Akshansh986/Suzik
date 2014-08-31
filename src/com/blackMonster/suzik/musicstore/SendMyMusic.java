package com.blackMonster.suzik.musicstore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.blackMonster.suzik.MainPrefs;

public class SendMyMusic {

	public static void send(final Context context) {
		new Thread() {
			public void run() {
				try {
					BindAllSongs(context);
					//sendSongsInfo(context);
				} catch (Exception e) {
				}
			}

		}.start();

	}

	private static ArrayList<genericSongClass> songs = null;
	public static String songsPostData = "";

	public static void BindAllSongs(Context context) {
		/** Making custom drawable */
		int count = 0;

		String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		final String[] projection = new String[] {
				MediaStore.Audio.Media.DISPLAY_NAME,
				MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
				MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION, MediaStore.Audio.Media._ID};
		final String sortOrder = MediaStore.Audio.AudioColumns.TITLE
				+ " COLLATE LOCALIZED ASC";
		Cursor cursor = null;
		try {
			// the uri of the table that we want to query
			Uri uri = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			// query the db
			cursor = context.getContentResolver().query(uri, projection,
					selection, null, sortOrder);
			if (cursor != null) {
				songs = new ArrayList<genericSongClass>(cursor.getCount());
				cursor.moveToFirst();
				while (!cursor.isAfterLast()) {
					genericSongClass GSC = new genericSongClass();
					count++;

					GSC.songFilename = cursor.getString(0);

					GSC.songTitle = cursor.getString(1);

					GSC.songArtist = cursor.getString(2);

					GSC.songAlbum = cursor.getString(3);
					
					long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
					
					long id = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media._ID));

					

					songs.add(GSC);
					Log.d("searching ", id + "  " + GSC.songTitle + "   " + GSC.songArtist + "  " + duration );
					cursor.moveToNext();
				}
			}

		} catch (Exception ex) {

		} finally {
			if (cursor != null) {
				cursor.close();
			}
		}

		int ab = 0;
		for (genericSongClass s : songs) {
			ab++;
			if (s.songArtist.equals("<unknown>"))
				s.songArtist = "";
			if (s.songAlbum.equals("<unknown>"))
				s.songAlbum = "";

			songsPostData = songsPostData + "`Fn" + s.songFilename + ",`Ti"
					+ s.songTitle + ",`Ar" + s.songArtist + ",`Al"
					+ s.songAlbum + ",`##";

			Log.d("sdf", "  " + ab);
		}

	}
	
	private static String getReadableDate(long seconds) {
		 SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");
		 Date date = new Date(seconds * 1000);
		 return sdf.format(date);
	}

	public static void sendSongsInfo(Context context) {
		Log.d("searching", "Reaching function");
		Log.d("searching", songsPostData);

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost1 = new HttpPost(
				"http://niksqwer.5gbfree.com/songs_info_rec.php");
		// HttpPost httppost2 = new HttpPost(
		// "http://niksqwer.5gbfree.com/organise_bad_song.php");

		try {
			// Add your data
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("songs", songsPostData));

			// YAha user ka mobile no chaiye submit press karrte hi yeh function
			// call hojaye

			nameValuePairs.add(new BasicNameValuePair("no", MainPrefs
					.getMyNo(context)));
			httppost1.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			// Execute HTTP Post Request
			HttpResponse response = httpclient.execute(httppost1);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			printAll(reader, "req1");
			Log.d("sendsongsinfo", "req1Done");
			reader.close();
			Log.d("sendsongsinfo", "req1Done");
			httpclient.getConnectionManager().shutdown();
			Log.d("sendsongsinfo", "req1Done");
			post2();
			Log.d("sendsongsinfo", "req1Done");

			// httpclient.execute(httppost2);
			// httpclient.getConnectionManager().shutdown();

		} catch (ClientProtocolException e) {
			Log.d("sendSongsInfo", "exception");
			e.printStackTrace();
		} catch (IOException e) {
			Log.d("sendSongsInfo", "exception");
			e.printStackTrace();
		}
	}


	private static void post2() throws ClientProtocolException, IOException {
		Log.d("sendmymusic", "post2");
		HttpClient httpclient = new DefaultHttpClient();
		BufferedReader reader = null;
		HttpGet httpget = new HttpGet("http://niksqwer.5gbfree.com/music.php");
		try {
			HttpResponse response = httpclient.execute(httpget);
			reader = new BufferedReader(new InputStreamReader(response
					.getEntity().getContent()));
			
			printAll(reader, "req2");
			reader.close();
			httpclient.getConnectionManager().shutdown();
		} catch (Exception e) {
			httpget.abort();
		}
	}

	private static void printAll(BufferedReader reader, String tag)
			throws IOException {
		while (true) {
			String tmp = reader.readLine();
			if (tmp == null)
				break;
			Log.d(tag," " + tmp);

		}
	}

	public static class genericSongClass {
		String songFilename = "";
		String songTitle = "";
		String songArtist = "";
		String songAlbum = "";
		String songDuration = "";
		String isChecked = "false";
	}

}
