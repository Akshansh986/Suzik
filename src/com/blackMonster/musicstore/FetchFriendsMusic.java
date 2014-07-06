package com.blackMonster.musicstore;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;
import android.util.Log;

import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.MainStaticElements;

public class FetchFriendsMusic {
	private static final String TAG = "FetchFriendsMusic";

	public static List<MusicInfo> getData(Context context) {

		Log.d(TAG, "getdata");
		List<MusicInfo> mi = new ArrayList<MusicInfo>();
		getMusicInfo(mi, context);
		for (MusicInfo a : mi) {
			Log.d(TAG, "title " + a.title + " artist  " + a.artist + " phoneNo"
					+ a.phoneNo + " time " + a.durartion);
		}
		return mi;

	}

	private static void getMusicInfo(List<MusicInfo> mi, Context context) {

		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(MainStaticElements.MAIN_URL + "playlist_request.php");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("no", MainPrefs
					.getMyNo(context)));
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));

			extractData(mi, reader);

			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		httpclient.getConnectionManager().shutdown();

	}

	private static void extractData(List<MusicInfo> mi, BufferedReader reader)
			throws IOException {
		String currentNo = "";
		while (true) {
			String tmp = reader.readLine();
			if (tmp == null)
				break;
			Log.d(TAG, tmp);

			if (tmp.contains("~num~")) {
				currentNo = tmp.substring(5);
			} else {
				String[] inf = tmp.split("`");
				MusicInfo songInfo = new MusicInfo();
				songInfo.title = inf[1];
				songInfo.artist = inf[2];
				songInfo.durartion = inf[3];
				songInfo.sid = inf[4];
				songInfo.phoneNo = currentNo;
				
				mi.add(songInfo);
			}

		}

	}

	public static class MusicInfo {
		public String title;
		public String artist;
		public String phoneNo;
		public String durartion;
		public String sid;
	}
}
