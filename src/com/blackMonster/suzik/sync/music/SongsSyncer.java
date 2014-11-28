package com.blackMonster.suzik.sync.music;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGI;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import com.blackMonster.suzik.sync.Syncer;
import com.blackMonster.suzik.sync.music.AndroidMusicHelper.AndroidData;
import com.blackMonster.suzik.sync.music.CacheTable.CacheData;
import com.blackMonster.suzik.sync.music.InAapSongTable.InAppSongData;
import com.blackMonster.suzik.sync.music.QueueAddedSongs.QueueData;

public class SongsSyncer extends Syncer {
	private static final String TAG = "SongsSyncer";
	static final String LOCK = "lock";
	
	@Override
	public  boolean onPerformSync() throws Exception {

		synchronized (SongsSyncer.LOCK) {
			return startSync();
		}
		
	}




	private boolean startSync() throws Exception {
		LOGI(TAG,"performing Sync");

		List<AndroidData> androidDataList = AndroidMusicHelper.getAllMySongs(this);
		LOGD(TAG,"android size " + androidDataList.size());
		
		List<CacheData> cacheDataList = CacheTable.getAllData(this);
		LOGD(TAG,"cache table size " + cacheDataList.size());
		ChangesHandler changes = new ChangesHandler(androidDataList, cacheDataList,this);
		LOGD(TAG,"changes done");
		LOGD(TAG,"added : " + changes.getAddedSongs().size() + " removed : " + changes.getDeletedSongs().size());
		
		if (changes.noChanges()) return true;
	/*	
		int count=0;
		for (AndroidData son : changes.getAddedSongs()) {
			QueueAddedSongs.remove(son.getfPrint(), context);
			CacheTable.insert(new CacheData(count++, son.getSong(), son.getfPrint(),son.getFileName()) , context);
		}
		if (true ) return false;
	*/
		
		//handleModifiedSongs(changes.getModifiedSongs());
		//LOGI(TAG,"modified song handle done");
	
		handleAddedSongsIfAlreadyInAppSong(changes.getAddedSongs());
		LOGI(TAG,"handleAddedSongsIfAlreadyInAppSong done");

		
		removeSongsIfAlreadyInQueue(changes.getAddedSongs());   //checked
		LOGI(TAG,"removeSongsIfAlreadyInQueue done");

		
		if (handleDeletedSongs(changes.getDeletedSongs()) == false) return false; 
		LOGI(TAG,"delete songs done");

		
		if (ServerHelper.postAddedSongs(changes.getAddedSongs()) == false ) return false;
		LOGI(TAG,"post added songs done");

		
		moveAddedSongsToQueue(changes.getAddedSongs());
		LOGI(TAG,"move to queue done");

		if (!QueueAddedSongs.isEmpty(this)) AddedSongsResponseHandler.futureCall(this);
		
		LOGI(TAG,"All done");
		return true;		
	}




	private  void moveAddedSongsToQueue(List<AndroidData> addedSongs) {
		for (AndroidData song : addedSongs) {
			LOGD(TAG,"Moving to queue : " + song.getSong());
			QueueAddedSongs.insert(new QueueData(null,song.getSong(), song.getfPrint(),song.getFileName()),this);
		}
		
	}


	private  boolean handleDeletedSongs(List<CacheData> deletedSongs) throws JSONException, InterruptedException, ExecutionException {
		//if (true) return true;
		
		if (deletedSongs.isEmpty()) return true;
		
		HashMap<Long, Long> serverLocalMap = new HashMap<Long, Long>(); 
		
		List<Long> ids = new ArrayList<Long>();
		
		for (Iterator<CacheData> it = deletedSongs.iterator(); it.hasNext(); ) {
		    CacheData cacheData = it.next();
		    if (CacheTable.noOfCopies(cacheData.getfPrint(), this) > 1) {
		    		LOGD(TAG,"removed from only cache table : " + cacheData.toString());
		    		CacheTable.remove(cacheData.getId(), this);
		    }
		    else
		    {
		    	long serverId = AllSongsTable.getServerId(cacheData.getId(),this);
		    	serverLocalMap.put(serverId, cacheData.getId());
		    	ids.add(serverId);
		    }
		   
		}		
		
		
		
		LOGD(TAG,"Removing ids from server : " + ids.toString());
		
		
		if (ids.size()==0) return true;
		
		/*
		for (CacheData data : deletedSongs) {
			ids.add(data.getId());
		}*/
		HashMap<Long, Integer> idStatus = ServerHelper.postDeletedSongs(ids);
		
		boolean res = true;
		
		for (Map.Entry<Long, Integer> entry : idStatus.entrySet()) {
			if (entry.getValue() == JsonHelper.DeletedSong.RESPONSE_SQL_ERROR) {
				res = false;
			}
			else {
				CacheTable.remove(serverLocalMap.get(entry.getKey()), this);
			}
		
		}
		
		return res;
		
	}


	private  void removeSongsIfAlreadyInQueue(List<AndroidData> addedSongs) {
		if (QueueAddedSongs.isEmpty(this)) return;
		List<QueueData> queue = QueueAddedSongs.getAllData(this);
		ChangesHandler.removeCommons(addedSongs, queue);
		/*
		
		
		List<AndroidData> removed = new ArrayList<AndroidMusicHelper.AndroidData>();
		for (AndroidData added : addedSongs) {
				LOGD(TAG,"removing already in queue : " + added.getSong().toString());
				if (QueueAddedSongs.search(added.getfPrint(), this) !=  null)
					removed.add(added);
		}
		addedSongs.removeAll(removed);*/
	}


	private void handleAddedSongsIfAlreadyInAppSong(
			List<AndroidData> addedSongs) {

		List<AndroidData> removed = new ArrayList<AndroidMusicHelper.AndroidData>();
		
		for (AndroidData song : addedSongs) {
			
			InAppSongData inAppSong = InAapSongTable.getData(song.getfPrint(), this);

			if (inAppSong != null) {
				LOGD(TAG,"handling in app " + inAppSong.toString());
				boolean r = InAapSongTable.remove(inAppSong.getId(), this);
				CacheTable.insert(new CacheData(null, inAppSong.getServerId(), song.getSong(), inAppSong.getfPrint(), song.getFileName()), this);
				//addedSongs.remove(song);         //debug check this
				removed.add(song);
			}
			
			
		}
		addedSongs.removeAll(removed);
					
	}

/*
	private void handleModifiedSongs(List<CacheData> modifiedSongs	) {

		for (CacheData modified : modifiedSongs) {
			LOGD(TAG,"Updating : " + modified.toString());
			CacheTable.update(modified, this);
		}

	}

*/
	

}
