package com.blackMonster.suzik.sync.music;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;

import android.content.Context;

import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.sync.music.CacheTable.CacheData;
import com.blackMonster.suzik.sync.music.QueueAddedSongs.QueueData;

public class AddedSongsResponseHandler {


	public static boolean performSync(Context context) throws JSONException, InterruptedException, ExecutionException {

		if (QueueAddedSongs.isEmpty(context))
			return true;

		HashMap<String, Long> fPrintIdMap;
		
		
			fPrintIdMap = getQueueStatusFromServer(context);
		

		if (fPrintIdMap.size() > 0) {

			for (Map.Entry<String, Long> entry : fPrintIdMap.entrySet()) {
				
				long id = entry.getValue();
				if (id == 0) 
					id = getNewSongId(context);
				QueueData qData = QueueAddedSongs.search(entry.getKey(),
						context);

				CacheTable.insert(
						new CacheData(id, qData.getSong(), qData
								.getfPrint(), qData.getFileName()), context);
				QueueAddedSongs.remove(entry.getKey(), context);

			}

			if (!QueueAddedSongs.isEmpty(context)) {
				createAlarm();
			}
		}
		else
		{
			QueueAddedSongs.clearAll(context);
			//SongsSyncer.syncNow(context);  //test this
		}

		return true;
	}

	private static void createAlarm() {
		// TODO Auto-generated method stub
		
	}

	private static long getNewSongId(Context context) {
		int id = MainPrefs.getUnIdentifiableSongId(context) + 1;
		MainPrefs.setUnIndetifiableSongId(id, context);
		return id;
	}

	
	private static HashMap<String, Long> getQueueStatusFromServer(
			Context context) throws JSONException, InterruptedException,
			ExecutionException {
		return ServerHelper.postFingerPrints(
				QueueAddedSongs.getAllFprints(context), context);

	}

}
