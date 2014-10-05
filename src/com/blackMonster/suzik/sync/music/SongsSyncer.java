package com.blackMonster.suzik.sync.music;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import android.content.Context;

import com.blackMonster.suzik.sync.music.AndroidHelper.AndroidData;
import com.blackMonster.suzik.sync.music.CacheTable.CacheData;
import com.blackMonster.suzik.sync.music.InAapSongTable.InAppSongData;
import com.blackMonster.suzik.sync.music.QueueAddedSongs.QueueData;

public class SongsSyncer {
	private static final String TAG = "SongsSyncer";

	
	public static boolean startSync(Context context) throws Exception  {
		List<AndroidData> androidDataList = AndroidHelper.getAllMySongs(context);
		LOGD(TAG," " + androidDataList.size());
		
		List<CacheData> cacheDataList = CacheTable.getAllData(context);
		LOGD(TAG," " + cacheDataList.size());
		ChangesHandler changes = new ChangesHandler(androidDataList, cacheDataList,context);
		LOGD(TAG,"changes done");
		
		if (changes.noChanges()) return true;

	/*	
		int count=0;
		for (AndroidData son : changes.getAddedSongs()) {
			QueueAddedSongs.remove(son.getfPrint(), context);
			CacheTable.insert(new CacheData(count++, son.getSong(), son.getfPrint(),son.getFileName()) , context);
		}
		if (true ) return false;
	*/
		
		handleModifiedSongs(changes.getModifiedSongs(), context);
		handleAddedSongsIfAlreadyInAppSong(changes.getAddedSongs(),context);
		removeSongsIfAlreadyInQueue(changes.getAddedSongs(), context);   //checked
		
		if (handleDeletedSongs(changes.getDeletedSongs(), context) == false) return false;   
		
		if (ServerHelper.postAddedSongs(changes.getAddedSongs()) == false ) return false;
		
		moveAddedSongsToQueue(changes.getAddedSongs(),context);
		if (!QueueAddedSongs.isEmpty(context)) createAlarm(context);
		
		LOGD(TAG,"All done");
		return true;
	}


	private static void createAlarm(Context context) {
		// TODO Auto-generated method stub
		
	}


	private static void moveAddedSongsToQueue(List<AndroidData> addedSongs,Context context) {
		for (AndroidData song : addedSongs) {
			LOGD(TAG,"Moving to queue : " + song.toString());
			QueueAddedSongs.insert(new QueueData(song.getSong(), song.getfPrint(),song.getFileName()),context);
		}
		
	}


	private static boolean handleDeletedSongs(List<CacheData> deletedSongs,Context context) throws JSONException, InterruptedException, ExecutionException {
		if (true) return true;
		
		if (deletedSongs.isEmpty()) return true;
		List<Long> ids = new ArrayList<Long>();
		for (CacheData data : deletedSongs) {
			ids.add(data.getId());
		}
		HashMap<Long, Integer> idStatus = ServerHelper.postDeletedSongs(ids);
		
		boolean res = true;
		
		for (Map.Entry<Long, Integer> entry : idStatus.entrySet()) {
			if (entry.getValue() == 0) {
				res = false;
			}
			else {
				CacheTable.remove(entry.getKey(), context);
			}
		
		}
		
		return res;
		
	}


	private static void removeSongsIfAlreadyInQueue(List<AndroidData> addedSongs, Context context) {
		if (QueueAddedSongs.isEmpty(context)) return;
		List<AndroidData> removed = new ArrayList<AndroidHelper.AndroidData>();
		for (AndroidData added : addedSongs) {
				LOGD(TAG,"removing already in queue : " + added.getSong().toString());
				if (QueueAddedSongs.search(added.getfPrint(), context) !=  null)
					removed.add(added);
		}
		addedSongs.removeAll(removed);
	}


	private static void handleAddedSongsIfAlreadyInAppSong(
			List<AndroidData> addedSongs, Context context) {

		List<AndroidData> removed = new ArrayList<AndroidHelper.AndroidData>();
		
		for (AndroidData song : addedSongs) {
			
			InAppSongData inAppSong = InAapSongTable.getData(song.getfPrint(), context);

			if (inAppSong != null) {
				LOGD(TAG,"handling in app " + inAppSong.toString());
				InAapSongTable.remove(inAppSong.getId(), context);
				CacheTable.insert(new CacheData(inAppSong.getId(), inAppSong.getSong(), inAppSong.getfPrint(),song.getFileName()), context);
				//addedSongs.remove(song);         //debug check this
				removed.add(song);
			}
			
			
		}
		addedSongs.removeAll(removed);
					
	}


	private static void handleModifiedSongs(List<CacheData> modifiedSongs,
			Context context) {

		for (CacheData modified : modifiedSongs) {
			LOGD(TAG,"Updating : " + modified.toString());
			CacheTable.update(modified, context);
		}

	}

}
