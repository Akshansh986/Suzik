package com.blackMonster.suzik.sync.music;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import static com.blackMonster.suzik.util.LogUtils.*;
import android.content.Context;

import com.blackMonster.suzik.sync.music.AndroidHelper.AndroidData;
import com.blackMonster.suzik.sync.music.CacheTable.CacheData;
import com.blackMonster.suzik.sync.music.InAapSongTable.InAppSongData;

public class SongsSyncer {
	private static final String TAG = "SongsSyncer";
	
	
	public static boolean startSync(Context context) throws Exception {
		List<AndroidData> androidDataList = AndroidHelper.getAllMySongs(context);
		LOGD(TAG," " + androidDataList.size());
		List<CacheData> cacheDataList = CacheTable.getAllData(context);
		LOGD(TAG," " + cacheDataList.size());
		ChangesHandler changes = new ChangesHandler(androidDataList, cacheDataList,context);
		LOGD(TAG,"changes done");

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
		
		if (postDeletedSongs(changes.getDeletedSongs())== false) return false;    ///incomplete
		
		if (ServerHelper.postAddedSongs(changes.getAddedSongs()) == false ) return false;
		
		moveAddedSongsToQueue(changes.getAddedSongs(),context);
		createAlarm();
		return true;
	}


	private static void createAlarm() {
		// TODO Auto-generated method stub
		
	}


	private static void moveAddedSongsToQueue(List<AndroidData> addedSongs,Context context) {
		for (AndroidData song : addedSongs) {
			LOGD(TAG,"Moving to queue : " + song.toString());
			QueueAddedSongs.insert(song.getSong(), song.getfPrint(),song.getFileName(),context);
		}
		
	}


	private static boolean postDeletedSongs(List<CacheData> deletedSongs) throws JSONException, InterruptedException, ExecutionException {
		List<Long> ids = new ArrayList<Long>();
		for (CacheData data : deletedSongs) {
			ids.add(data.getId());
		}
		return ServerHelper.postDeletedSongs(ids);
		
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
