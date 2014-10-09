package com.blackMonster.suzik.sync.music;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGE;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import android.content.Context;

import com.blackMonster.suzik.sync.music.AndroidHelper.AndroidData;
import com.gracenote.mmid.MobileSDK.GNConfig;
import com.gracenote.mmid.MobileSDK.GNFingerprintResult;
import com.gracenote.mmid.MobileSDK.GNFingerprintResultReady;
import com.gracenote.mmid.MobileSDK.GNOperations;

class Fingerprinter{
	private static final String TAG = "Fingerprinter";
	private GNConfig config;
	private List<AndroidData> songList;
	private CountDownLatch startSignal, doneSignal;

	
	private List<AndroidData> fpErrorSong = new ArrayList<AndroidHelper.AndroidData>();
	
	Fingerprinter(Context context,List<AndroidData> songList){
		this.songList = songList;
		config = GNConfig.init("7112704-2CF3D8F828D42A95CE718E9553591494",
				context.getApplicationContext());
	}

	void addFingerPrint() throws InterruptedException {
		doneSignal = new CountDownLatch(songList.size());
		LOGD(TAG,"No of done signal " + songList.size());
		startSignal = new CountDownLatch(1);

		for (AndroidData song : songList) {
			FingerPrintResultReady rr = new FingerPrintResultReady(song);
			LOGD(TAG,song.getLocation() );
			GNOperations.fingerprintMIDFileFromFile(rr, config, song.getLocation());
			LOGD(TAG,song.getLocation());
		}	
		startSignal.countDown();
		doneSignal.await();	

		
	}

	private class FingerPrintResultReady implements GNFingerprintResultReady {
		AndroidData androidSong;
		
		public FingerPrintResultReady(AndroidData androidSong) {
			this.androidSong = androidSong;
		}
		@Override
		public void GNResultReady(GNFingerprintResult result) {
			
			try {
				startSignal.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
					
			if (result == null) {
				LOGE(TAG,"null");
				onError();
				doneSignal.countDown();
				return;
			}
			
			if (result.isFingerprintingFailure()) {
				LOGE(TAG,"fingerprint failure");
				onError();
				doneSignal.countDown();
				return;
			}

			String fingerPrint = result.getFingerprintData();
			if (fingerPrint == null) {
				LOGE(TAG,"Null fingerprint");
				onError();
				doneSignal.countDown();
				return;
			}
			
			LOGD(TAG, fingerPrint);
			androidSong.setfPrint(fingerPrint);
			doneSignal.countDown();
		}
		
		private void onError() {
			fpErrorSong.add(androidSong);
			songList.remove(androidSong);
		}

	}
	
	List<AndroidData> getFingerPrintErrorSongList() {
		return fpErrorSong;
	}

	

}