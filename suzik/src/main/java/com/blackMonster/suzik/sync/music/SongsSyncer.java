package com.blackMonster.suzik.sync.music;

import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.sync.Syncer;
import com.blackMonster.suzik.sync.music.AndroidMusicHelper.AndroidData;
import com.blackMonster.suzik.sync.music.CacheTable.CacheData;
import com.blackMonster.suzik.sync.music.InAapSongTable.InAppSongData;
import com.blackMonster.suzik.sync.music.QueueAddedSongs.QueueData;
import com.blackMonster.suzik.ui.UiBroadcasts;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGI;

public class SongsSyncer extends Syncer {
    private static final String TAG = "SongsSyncer";
    static final String LOCK = "lock";

    private static final int MIN_BATCH_SIZE = 10;
    private static final int MAX_BATCH_SIZE = 100;
    private static final int PREFERRED_NO_BATCH = 5;

    @Override
    public boolean onPerformSync() throws Exception {

        synchronized (SongsSyncer.LOCK) {
            return startSync();
        }

    }


    private boolean startSync() throws Exception {
        LOGI(TAG, "performing Sync");

        List<AndroidData> androidDataList = AndroidMusicHelper.getAllMySongs(this);
        LOGD(TAG, "android size " + androidDataList.size());

        List<CacheData> cacheDataList = CacheTable.getAllData(this);
        LOGD(TAG, "cache table size " + cacheDataList.size());
        ChangesHandler changes = new ChangesHandler(androidDataList, cacheDataList, this);
        LOGD(TAG, "changes done");
        LOGD(TAG, "added : " + changes.getAddedSongs().size() + " removed : " + changes.getDeletedSongs().size());

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
        LOGI(TAG, "handleAddedSongsIfAlreadyInAppSong done");


        removeSongsIfAlreadyInQueue(changes.getAddedSongs());   //checked
        LOGI(TAG, "removeSongsIfAlreadyInQueue done");


        if (handleDeletedSongs(changes.getDeletedSongs()) == false) return false;
        LOGI(TAG, "delete songs done");


        if (postInBatches(changes.getAddedSongs()) == false) return false;
        LOGI(TAG, "post added songs done");


        moveAddedSongsToQueue(changes.getAddedSongs());
        LOGI(TAG, "move to queue done");

        if (!QueueAddedSongs.isEmpty(this)) AddedSongsResponseHandler.futureCall(this);


        if (!MainPrefs.getFirstTimeSongPostedToServer(this)) {
            MainPrefs.setFirstTimeSongPostedToServer(this);
            UiBroadcasts.broadcastMusicDataChanged(this);
        }

        LOGI(TAG, "All done");
        return true;
    }

    private boolean postInBatches(List<AndroidData> addedSongs) throws InterruptedException, ExecutionException, JSONException {
//            ServerHelper.postAddedSongs(addedSongs);
//        return true;

        int n = addedSongs.size();
        int batchSize = getBatchSize(n);
        LOGD(TAG,"batch size : " + batchSize);
        int last;

        for (int i = 0; i < n; i += batchSize) {
            last = i + batchSize;
            if (last > n) last = n;
            LOGD(TAG,"batch " + i + " range : " + i + "  " + last);
            ServerHelper.postAddedSongs(addedSongs.subList(i, last));
        }
        return true;
    }




    public static  int getBatchSize(int totalSongs) {
        if (totalSongs ==0 )return 0;
        int batchSize = (int) Math.ceil( (double) totalSongs / PREFERRED_NO_BATCH );

        if (batchSize < MIN_BATCH_SIZE)  batchSize = MIN_BATCH_SIZE;
        else if (batchSize > MAX_BATCH_SIZE)  batchSize = MAX_BATCH_SIZE;

        return batchSize;
    }


    private void moveAddedSongsToQueue(List<AndroidData> addedSongs) {
        for (AndroidData song : addedSongs) {
            LOGD(TAG, "Moving to queue : " + song.getSong());
            QueueAddedSongs.insert(new QueueData(null, song.getSong(), song.getfPrint(), song.getFileName()), this);
        }

    }


    private boolean handleDeletedSongs(List<CacheData> deletedSongs) throws JSONException, InterruptedException, ExecutionException {
        //if (true) return true;

        if (deletedSongs.isEmpty()) return true;

        HashMap<Long, Long> serverLocalMap = new HashMap<Long, Long>();

        List<Long> ids = new ArrayList<Long>();

        for (Iterator<CacheData> it = deletedSongs.iterator(); it.hasNext(); ) {
            CacheData cacheData = it.next();
            if (CacheTable.noOfCopies(cacheData.getfPrint(), this) > 1) {
                LOGD(TAG, "removed from only cache table : " + cacheData.toString());
                CacheTable.remove(cacheData.getId(), this);
            } else {
                long serverId = AllSongsTable.getServerId(cacheData.getId(), this);
                serverLocalMap.put(serverId, cacheData.getId());
                ids.add(serverId);
            }

        }


        LOGD(TAG, "Removing ids from server : " + ids.toString());


        if (ids.size() == 0) return true;
		
		/*
		for (CacheData data : deletedSongs) {
			ids.add(data.getId());
		}*/
        HashMap<Long, Integer> idStatus = ServerHelper.postDeletedSongs(ids);

        boolean res = true;

        for (Map.Entry<Long, Integer> entry : idStatus.entrySet()) {
            if (entry.getValue() == JsonHelper.DeletedSong.RESPONSE_SQL_ERROR) {
                res = false;
            } else {
                CacheTable.remove(serverLocalMap.get(entry.getKey()), this);
            }

        }

        return res;

    }


    private void removeSongsIfAlreadyInQueue(List<AndroidData> addedSongs) {
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
                LOGD(TAG, "handling in app " + inAppSong.toString());
                boolean r = InAapSongTable.remove(inAppSong.getId(), this);
                CacheTable.insert(new CacheData(null, inAppSong.getServerId(), song.getSong(), inAppSong.getfPrint(), song.getFileName()), this);
                //addedSongs.remove(song);         //debug check this
                removed.add(song);
            }


        }
        addedSongs.removeAll(removed);

    }


    @Override
    public String getBroadcastString() {
        return null;
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
