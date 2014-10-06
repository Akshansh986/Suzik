package com.blackMonster.suzik.sync;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;

import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.sync.music.AddedSongsResponseHandler;
import com.blackMonster.suzik.sync.music.SongsSyncer;
import com.blackMonster.suzik.util.NetworkUtils;

//
/**
 * Steps to use syncer
 * 1) extend syncer.
 * 2) add unimplemented method.
 * 3) register as service in manifest.
 * 4) register in startOnNetAvailable funcion in syncer class.
 * 5) make onPerformSync synchronized if required.
 *  @author akshanshsingh
 *
 */
public abstract class Syncer extends IntentService{
	private static final String TAG = "SyncManager";
	
	
	public Syncer() {
		super(TAG);
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		 super.onStartCommand(intent, flags, startId);
		 return START_STICKY;
	}
	
	
	@Override
	protected void onHandleIntent(Intent intent) {
		
		LOGD(TAG,"onHandleIntent " + getClassName());
	
		new Thread() {
			public void run() {
				try {
					syncNow();
				} catch (Exception e) {
				}
			}
			
		}.start();
		
	}
	
	private  void syncNow() {

		if (!NetworkUtils.isInternetAvailable(this)) {
			LOGD(TAG,"Net not available");
			MainPrefs.setCallOnNetAvailable(getClassName(), true, this);
			return;
		}
		
		try {
			LOGD(TAG,"calling : " + getClassName());

			if (onPerformSync() == false) {
			LOGD(TAG,"failedSync " + getClassName());
			callFuture(AppConfig.SYNCER_TIME_RETRY_MS);	
			}
			
		} catch (Exception e1) {
			e1.printStackTrace();
			LOGD(TAG,"failedSync " + getClassName());
			callFuture(AppConfig.SYNCER_TIME_RETRY_MS);	

		}
		

	}

	private  void callFuture(long time) {
		callFuture(getClass(), time, this);
		
//		LOGD(TAG,"calling after  "+ time + "  ms " + getClassName());
//		Intent intent = new Intent(this,getClass());
//		
//		PendingIntent operation = PendingIntent.getService(this, -1,
//				intent,PendingIntent.FLAG_CANCEL_CURRENT);
//		
//		AlarmManager am = ((AlarmManager) this.getSystemService(Context.ALARM_SERVICE));
//		am.cancel(operation);
//		am.set(AlarmManager.ELAPSED_REALTIME,SystemClock.elapsedRealtime() + time, operation);
	}
	
	public  static void callFuture(Class cls, long time,Context context) {
		LOGD(TAG,"calling after  "+ time + "  ms " + cls.getSimpleName());
		Intent intent = new Intent(context,cls);
		
		PendingIntent operation = PendingIntent.getService(context, -1,
				intent,PendingIntent.FLAG_CANCEL_CURRENT);
		
		AlarmManager am = ((AlarmManager) context.getSystemService(Context.ALARM_SERVICE));
		am.cancel(operation);
		am.set(AlarmManager.ELAPSED_REALTIME,SystemClock.elapsedRealtime() + time, operation);
	}
	
	
	
	public  abstract  boolean onPerformSync() throws Exception;

	
	public String getClassName() {
		return getClass().getSimpleName();
	}

	public static void startOnNetAvaiable(Context context) {
		LOGD(TAG, "startOnNetAvaiable");

		String caller = SongsSyncer.class.getSimpleName();
		if (MainPrefs.shouldCallOnNetAvailable(caller, context)) {
			MainPrefs.setCallOnNetAvailable(caller, false, context);
			context.startService(new Intent(context, SongsSyncer.class));
		}
		
		caller = AddedSongsResponseHandler.class.getSimpleName();
		if (MainPrefs.shouldCallOnNetAvailable(caller, context)) {
			MainPrefs.setCallOnNetAvailable(caller, false, context);
			context.startService(new Intent(context, AddedSongsResponseHandler.class));
		}
		
		
		
		
		
		
	}


}

