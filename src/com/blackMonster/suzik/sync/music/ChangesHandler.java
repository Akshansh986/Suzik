package com.blackMonster.suzik.sync.music;
import java.util.Iterator;
import java.util.List;

import android.content.Context;

import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.sync.music.AndroidMusicHelper.AndroidData;
import com.blackMonster.suzik.sync.music.CacheTable.CacheData;

class ChangesHandler {
	private static final String TAG = "ChangesHandler";
	//private HashMap<CompareParams, AndroidData> androidDataMap;
	//private HashMap<CompareParams, CacheData> cacheDataMap;

	//private List<AndroidData> addedSongs = new ArrayList<AndroidMusicHelper.AndroidData>();
	//private List<CacheData> deletedSongs = new ArrayList<CacheTable.CacheData>();
	//private List<CacheData> modifiedSongs = new ArrayList<CacheTable.CacheData>();
	List<AndroidData> androidDataList;
	List<CacheData> cacheDataList;
	private Context context;

	boolean noChanges() {
		return androidDataList.isEmpty() && cacheDataList.isEmpty();
	}

	ChangesHandler(List<AndroidData> androidDataList,
			List<CacheData> cacheDataList, Context context)
			throws InterruptedException {
		super();
		//this.androidDataMap = androidDataListToHashMap(androidDataList);
		//this.cacheDataMap = cacheDataListToHashMap(cacheDataList);
		this.context = context.getApplicationContext();
		this.androidDataList = androidDataList;
		this.cacheDataList = cacheDataList;
		setChanges();
		addFingerPrint(androidDataList);
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		
		//LOGD(TAG,androidDataMap.toString());
		//LOGD(TAG,cacheDataMap.toString());
		
		//setAddedSongs();
		///LOGD(TAG,addedSongs.toString());
		
//		setDeletedSongs();
//		LOGD(TAG,deletedSongs.toString());
//		addFingerPrint(addedSongs);
//		handleDuplicateSongs(); // optimize it
//		setModifiedSongs();

	}
	
	
	
	
	
	
	
	
	
	
	
	
	
private void setChanges() {
	
	for (Iterator<AndroidData> it = androidDataList.iterator(); it.hasNext(); ) {
	    AndroidData androidData = it.next();
	    CacheData cacheData = inCacheData(androidData);
	    
	    if (cacheData != null) {
	    		it.remove();
	    		cacheDataList.remove(cacheData);
	    }
	    
	}
		
}

	private CacheData inCacheData(AndroidData androidData) {
		if (androidData == null) return null;
		
		for (CacheData cd : cacheDataList) {
			if (cd.getFileName().equals(androidData.getFileName()) && cd.getSong().equals(androidData.getSong())) {
				return cd;
			}
		}
			
	return null;
}


	private void addFingerPrint(final List<AndroidData> songList)
			throws InterruptedException {
		new Fingerprinter(context, songList).addFingerPrint();
	}


















	/*
	private void handleDuplicateSongs() {
		filterDuplicateFromList();
		filterFromCacheData();
	}

	private void filterFromCacheData() {
		List<AndroidData> removed = new ArrayList<AndroidMusicHelper.AndroidData>();

		for (AndroidData aSong : addedSongs) {

			for (Map.Entry<CompareParams, CacheData> entry : cacheDataMap
					.entrySet()) {
				if (aSong.getfPrint().equals(entry.getValue().getfPrint())) {
					removed.add(aSong);
					break;
				}
			}

		}
		addedSongs.removeAll(removed);		
	}

	private void filterDuplicateFromList() {
		List<AndroidData> removed = new ArrayList<AndroidMusicHelper.AndroidData>();

		int n = addedSongs.size();
		for (int i = 0; i < n; ++i) {
			for (int j = i + 1; j < n; ++j) {
				if (addedSongs.get(i).getfPrint()
						.equals(addedSongs.get(j).getfPrint())) {
					if (!removed.contains(addedSongs.get(j)))
						removed.add(addedSongs.get(j));
				}
			}
		}

		addedSongs.removeAll(removed);

	}


	private void setModifiedSongs() {
		List<AndroidData> tempAddedSong = new ArrayList<AndroidMusicHelper.AndroidData>();
		List<CacheData> tempDeletedSong = new ArrayList<CacheTable.CacheData>();

		for (AndroidData added : addedSongs) {
			for (CacheData deleted : deletedSongs) {
				if (added.getfPrint().equals(deleted.getfPrint())) {
					modifiedSongs
							.add(new CacheData(deleted.getId(),
									added.getSong(), added.getfPrint(), added
											.getFileName()));
					// addedSongs.remove(added);
					tempAddedSong.add(added);
					// deletedSongs.remove(deleted);
					tempDeletedSong.add(deleted);
					break;
				}
			}
		}

		addedSongs.removeAll(tempAddedSong);
		deletedSongs.removeAll(tempDeletedSong);

	}


	private HashMap<CompareParams, CacheData> cacheDataListToHashMap(
			List<CacheData> cacheDataSet) {
		HashMap<CompareParams, CacheData> map = new HashMap<CompareParams, CacheData>();

		for (CacheData data : cacheDataSet) {
			map.put(new CompareParams(data.getSong(), data.getFileName()), data);
		}

		return map;
	}

	private HashMap<CompareParams, AndroidData> androidDataListToHashMap(
			List<AndroidData> androidDataSet) {
		HashMap<CompareParams, AndroidData> map = new HashMap<CompareParams, AndroidMusicHelper.AndroidData>();

		for (AndroidData data : androidDataSet) {
			map.put(new CompareParams(data.getSong(), data.getFileName()), data);
		}

		return map;
	}

	private void setAddedSongs() {
		for (Map.Entry<CompareParams, AndroidData> entry : androidDataMap
				.entrySet()) {
			if (!cacheDataMap.containsKey(entry.getKey()))
				addedSongs.add(entry.getValue());
		}
	}

	private void setDeletedSongs() {
		for (Map.Entry<CompareParams, CacheData> entry : cacheDataMap
				.entrySet()) {
			if (!androidDataMap.containsKey(entry.getKey()))
				deletedSongs.add(entry.getValue());
		}
	}
*/
	List<AndroidData> getAddedSongs() {
		return androidDataList;
	}

	List<CacheData> getDeletedSongs() {
		return cacheDataList;
	}

	/*List<CacheData> getModifiedSongs() {
		return modifiedSongs;
	}
*/
	private class CompareParams {
		private Song song;
		private String fileName;

		CompareParams(Song song, String fileName) {
			super();
			this.song = song;
			this.fileName = fileName;
		}

		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + getOuterType().hashCode();
			result = prime * result
					+ ((fileName == null) ? 0 : fileName.hashCode());
			result = prime * result + ((song == null) ? 0 : song.hashCode());
			return result;
		}

		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			CompareParams other = (CompareParams) obj;
			if (!getOuterType().equals(other.getOuterType()))
				return false;
			if (fileName == null) {
				if (other.fileName != null)
					return false;
			} else if (!fileName.equals(other.fileName))
				return false;
			if (song == null) {
				if (other.song != null)
					return false;
			} else if (!song.equals(other.song))
				return false;
			return true;
		}

		private ChangesHandler getOuterType() {
			return ChangesHandler.this;
		}

		@Override
		public String toString() {
			return "CompareParams [song=" + song + ", fileName=" + fileName
					+ "]";
		}
		
		

	}

}
