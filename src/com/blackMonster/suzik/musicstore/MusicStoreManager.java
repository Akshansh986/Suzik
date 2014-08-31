package com.blackMonster.suzik.musicstore;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;

import com.blackMonster.suzik.musicstore.FetchFriendsMusic.MusicInfo;

public class MusicStoreManager {
	public static final String BROADCAST_UPDATE_DATABASE_RESULT = "BROADCAST_UPDATE_DATABASE_RESULT";

	public static void updateDatabase(final Context context) {
		new Thread() {
			public void run() {
				try {
					List<MusicInfo> listMi = FetchFriendsMusic.getData(context);
					Database.TableNOSongID.insert(listMi, context);
					Database.TableSongsInfo.insert(listMi, context);
					broadcastResult(true,context);
				} catch (Exception e) {
					e.printStackTrace();
					broadcastResult(false,context);
				}
			}

		}.start();
	}
	private static void broadcastResult(boolean result,Context context) {
		Intent intent = new Intent(BROADCAST_UPDATE_DATABASE_RESULT).putExtra(BROADCAST_UPDATE_DATABASE_RESULT, result);
		LocalBroadcastManager.getInstance(context).sendBroadcast(intent);		
	}

	public static List<MusicInfo> getSongsListOfFriend(String uid, Context context) {
		List<String> sidList = Database.TableNOSongID.getSIDFromUID(uid, context);
		List<MusicInfo> songsList = new ArrayList<MusicInfo>();
		for (String sid : sidList) {
			songsList.add(Database.TableSongsInfo.getSongInfo(sid, context));
		}
		return songsList;
	}
	
}
