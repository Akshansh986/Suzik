package com.blackMonster.suzik.sync.music;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.sync.music.AndroidHelper.AndroidData;
import com.blackMonster.suzik.sync.music.CacheTable.CacheData;

class ChangesHandler {
	private HashMap<Song, AndroidData> androidDataMap;
	private HashMap<Song, CacheData> cacheDataMap;

	private List<AndroidData> addedSongs = new ArrayList<AndroidHelper.AndroidData>();
	private List<CacheData> deletedSongs = new ArrayList<CacheTable.CacheData>();
	private List<CacheData> modifiedSongs = new ArrayList<CacheTable.CacheData>();

	private Context context;
	

	ChangesHandler(List<AndroidData> androidDataList,
			List<CacheData> cacheDataList, Context context) throws InterruptedException {
		super();
		this.androidDataMap = androidDataListToHashMap(androidDataList);
		this.cacheDataMap = cacheDataListToHashMap(cacheDataList);
		this.context = context.getApplicationContext();
		setAddedSongs();
		setDeletedSongs();
		addFingerPrint(addedSongs);
		setModifiedSongs();

	}

	private void setModifiedSongs() {
		for (AndroidData added : addedSongs) {
			for (CacheData deleted : deletedSongs) {
				if (added.getfPrint().equals(deleted.getfPrint())) {
					modifiedSongs.add(new CacheData(deleted.getId(), added.getSong(), added.getfPrint()));
					addedSongs.remove(added);
					deletedSongs.remove(deleted);
					break;
				}
			}
		}

	}

	
	private void addFingerPrint(final List<AndroidData> songList) throws InterruptedException {
		new Fingerprinter(context, songList).addFingerPrint();
	}

	private HashMap<Song, CacheData> cacheDataListToHashMap(
			List<CacheData> cacheDataSet) {
		HashMap<Song, CacheData> map = new HashMap<Song, CacheData>();

		for (CacheData data : cacheDataSet) {
			map.put(data.getSong(), data);
		}

		return map;
	}

	private HashMap<Song, AndroidData> androidDataListToHashMap(
			List<AndroidData> androidDataSet) {
		HashMap<Song, AndroidData> map = new HashMap<Song, AndroidHelper.AndroidData>();

		for (AndroidData data : androidDataSet) {
			map.put(data.getSong(), data);
		}

		return map;
	}

	private void setAddedSongs() {
		for (Map.Entry<Song, AndroidData> entry : androidDataMap.entrySet()) {
			if (!cacheDataMap.containsKey(entry.getKey()))
				addedSongs.add(entry.getValue());
		}
	}

	private void setDeletedSongs() {
		for (Map.Entry<Song, CacheData> entry : cacheDataMap.entrySet()) {
			if (!androidDataMap.containsKey(entry.getKey()))
				deletedSongs.add(entry.getValue());
		}
	}

	List<AndroidData> getAddedSongs() {
		return addedSongs;
	}

	List<CacheData> getDeletedSongs() {
		return deletedSongs;
	}
	
	List<CacheData> getModifiedSongs() {
		return modifiedSongs;
	}

	

}
