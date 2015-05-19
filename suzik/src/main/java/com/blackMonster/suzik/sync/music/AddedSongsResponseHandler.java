package com.blackMonster.suzik.sync.music;

import android.content.Context;
import android.content.Intent;
import android.database.sqlite.SQLiteException;

import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.musicstore.userActivity.UserActivityManager;
import com.blackMonster.suzik.sync.Syncer;
import com.blackMonster.suzik.sync.music.CacheTable.CacheData;
import com.blackMonster.suzik.sync.music.QueueAddedSongs.QueueData;
import com.blackMonster.suzik.ui.UiBroadcasts;

import org.json.JSONException;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGI;

public class AddedSongsResponseHandler extends Syncer {
    private static final String TAG = "AddedSongsResponseHandler";

    private static final int BATCH_SIZE = 60;

    @Override
    public boolean onPerformSync() throws Exception {

        synchronized (SongsSyncer.LOCK) {
            return startSync();
        }

    }

    List<QueueData> queueSongs;


    private boolean startSync() throws JSONException, InterruptedException, ExecutionException, SQLiteException {
        LOGI(TAG, "onPerformSync start");

        queueSongs = QueueAddedSongs.getAllData(this);
        if (queueSongs == null) throw new SQLiteException();

        int n = queueSongs.size();
        if (n==0) return true;

        LOGD(TAG, "batch size : " + BATCH_SIZE);
        int last;

        boolean anySongProcessed = false;

        for (int i = 0; i < n; i += BATCH_SIZE) {
            last = i + BATCH_SIZE;
            if (last > n) last = n;
            anySongProcessed = processBatch(i, last) || anySongProcessed;

        }


        if (anySongProcessed) {

            if (!QueueAddedSongs.isEmpty(this)) {
                futureCall(this);
            } else {
                MainPrefs.setFirstTimeMusicSyncDone(this);
                UiBroadcasts.broadcastMusicDataChanged(this);
            }

        } else {
            LOGD(TAG, "reply size zero");
            QueueAddedSongs.clearAll(this);
            startService(new Intent(this, SongsSyncer.class));
        }

        LOGI(TAG, "onperformSync done");

        return true;
    }

    private boolean processBatch(int first, int last) throws InterruptedException, ExecutionException, JSONException {


        HashMap<String, Long> fPrintIdMap;
        LOGD(TAG, "going to contact server");

        List<QueueData> songsList = queueSongs.subList(first,last);

        fPrintIdMap = getQueueStatusFromServer(songsList);
        LOGD(TAG, "server response and json parsing done");

        if (fPrintIdMap.size() > 0) {
            LOGD(TAG, "replied " + fPrintIdMap.size());


            for (QueueData currQd : songsList) {
                Long serverId = fPrintIdMap.get(currQd.getfPrint());
                if (serverId == null) continue;


                LOGD(TAG, "inserting " + currQd.getFileName() + " to cachel table");

                long id = CacheTable.insert(
                        new CacheData(null, serverId, currQd.getSong(), currQd.getfPrint(), currQd.getFileName()), this);
                LOGD(TAG, "removing from queue");

                QueueAddedSongs.remove(currQd.getId(), this);

                if (MainPrefs.isFirstTimeMusicSyncDone(this)) {
                    UserActivityManager.add(new UserActivity(currQd.getSong(), null, id, UserActivity.ACTION_OUT_APP_DOWNLOAD, System.currentTimeMillis(), null), this);
                }

            }

            return true;
        } else {
            return false;
        }


    }

	/*private long getNewSongId() {
        int id = MainPrefs.getUnIdentifiableSongId(this) + 1;
		MainPrefs.setUnIndetifiableSongId(id, this);
		return id;
	}*/

    private HashMap<String, Long> getQueueStatusFromServer(List<QueueData> data)
            throws JSONException, InterruptedException, ExecutionException {
        return ServerHelper.postFingerPrints(data);

    }

    static void futureCall(Context context) {
        MainPrefs.setAddedsongsResponseHandlerInitTime(System.currentTimeMillis(),context);
        long time = getRemainingTimeMs(context);
        Syncer.callFuture(AddedSongsResponseHandler.class, time, context);

    }

    public static long getRemainingTimeMs(Context context) {

        int batchSize = SongsSyncer.getBatchSize(QueueAddedSongs.getRowCount(context));
        return batchSize
                * AppConfig.TIME_PROCESSING_NEW_SONG_SERVER_MS;
    }

    @Override
    public String getBroadcastString() {
        return null;
    }

}
