package com.blackMonster.suzik.sync;

import static com.blackMonster.suzik.util.LogUtils.*;
import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.v4.content.LocalBroadcastManager;

import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.musicstore.userActivity.UserActivityQueueSyncer;
import com.blackMonster.suzik.sync.contacts.ContactsSyncer;
import com.blackMonster.suzik.sync.music.AddedSongsResponseHandler;
import com.blackMonster.suzik.sync.music.SongsSyncer;
import com.blackMonster.suzik.util.NetworkUtils;
import com.crashlytics.android.Crashlytics;

/**
 * Steps to use syncer
 * 
 * 1) extend syncer. 2) add unimplemented method. 3) register as service in
 * manifest. 4) register in startOnNetAvailable function in at the bottom of
 * this class. 5) return null in getBroadcastString() if you don't want
 * broadcast.
 * 
 * Retry mechanism work with backoff algoritm.
 * 
 * @author akshanshsingh
 * 
 */
public abstract class Syncer extends IntentService {
	private static final String TAG = "SyncManager";

	private static final long BATCHING_REQUEST_WAIT_TIME_MS = 3000;

	public static final int STATUS_OK = 1;
	public static final int STATUS_ERROR = 2;
	public static final int STATUS_DEVICE_OFFLINE = 3;

	 int[] retryTimes={1,5,20,120,360,720,1440,1440}; //In minutes
//	int[] retryTimes = { 1, 1, 1, 1 }; // In minutes

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
		LOGD(TAG, "onHandleIntent " + getClass().getSimpleName());
		syncNow();
	}

	private void syncNow() {

		if (!NetworkUtils.isInternetAvailable(this)) {
			LOGD(TAG, "Net not available");
			MainPrefs.setCallOnNetAvailable(
					getOnNetAvailablePrefsName(getClass()), true, this);
			broadcastResult(STATUS_DEVICE_OFFLINE);
			return;
		}

		try {
			LOGD(TAG, "calling : " + getClass().getSimpleName());

			if (onPerformSync() == false) {
				onFailure();
			} else {
				onSucess();
			}

		} catch (Exception e1) {
			e1.printStackTrace();
			Crashlytics.logException(e1);
			onFailure();
		}

	}

	private void onSucess() {
		broadcastResult(STATUS_OK);
		MainPrefs.setSyncFailureCount(getSyncFailureCountPrefsName(getClass()),
				0, this);
	}

	private void onFailure() {
		LOGD(TAG, "failedSync " + getClass().getSimpleName());
		broadcastResult(STATUS_ERROR);

		incrementFailureCount();
		Long time = getNextRetryTimeInMs();
		if (time != null) {
			callFuture(time);
		} else {
			if (MainPrefs.getSyncFailureCount(
					getSyncFailureCountPrefsName(getClass()), this) > retryTimes.length) {
				LOGD(TAG, "Retry maximum limit reached");
			}
			MainPrefs.setSyncFailureCount(
					getSyncFailureCountPrefsName(getClass()), 0, this);
			LOGE(TAG, "Error getting retry time " + getClass().getSimpleName());

		}

	}

	private void incrementFailureCount() {
		int failure = MainPrefs.getSyncFailureCount(
				getSyncFailureCountPrefsName(getClass()), this) + 1;
		MainPrefs.setSyncFailureCount(getSyncFailureCountPrefsName(getClass()),
				failure, this);
	}

	private Long getNextRetryTimeInMs() {
		int fail = MainPrefs.getSyncFailureCount(
				getSyncFailureCountPrefsName(getClass()), this) - 1;
		if (fail < 0 || fail > retryTimes.length)
			return null;
		return retryTimes[fail] * AppConfig.MINUTE_IN_MILLISEC;

	}

	private void callFuture(long time) {
		callFuture(getClass(), time, this);

	}

	public static void callFuture(Class cls, long timeMS, Context context) {
		LOGD(TAG, "calling after  " + timeMS + "  ms " + cls.getSimpleName());
		Intent intent = new Intent(context, cls);

		PendingIntent operation = PendingIntent.getService(context, -1, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);

		AlarmManager am = ((AlarmManager) context
				.getSystemService(Context.ALARM_SERVICE));
		am.cancel(operation);
		am.set(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime()
				+ timeMS, operation);
	}

	/**
	 * Start syncing after a fixed time, and resets timer if new request comes
	 * in that time period.
	 * 
	 * @param cls
	 * @param context
	 */
	public static void batchRequest(Class cls, Context context) {
		callFuture(cls, BATCHING_REQUEST_WAIT_TIME_MS, context);
	}

	public abstract boolean onPerformSync() throws Exception;

	public abstract String getBroadcastString();

	//
	// public String getCurrentClassName() {
	// return getClass().getSimpleName();
	// }

	public static void startOnNetAvaiable(Context context) {
		LOGD(TAG, "startOnNetAvaiable");
		registerOnNetAvailable(SongsSyncer.class, context);
		registerOnNetAvailable(AddedSongsResponseHandler.class, context);
		registerOnNetAvailable(ContactsSyncer.class, context);
		registerOnNetAvailable(UserActivityQueueSyncer.class, context);
	}

	private static void registerOnNetAvailable(Class javaClass, Context context) {

		String caller = getOnNetAvailablePrefsName(javaClass);
		if (MainPrefs.shouldCallOnNetAvailable(caller, context)) {
			MainPrefs.setCallOnNetAvailable(caller, false, context);
			context.startService(new Intent(context, javaClass));
		}
	}

	private static String getOnNetAvailablePrefsName(Class javaClass) {
		return "OnNA" + javaClass.getSimpleName();
	}

	private static String getSyncFailureCountPrefsName(Class javaClass) {
		return "FailCount" + javaClass.getSimpleName();
	}

	private void broadcastResult(int result) {
		if (getBroadcastString() == null)
			return;
	
		Intent intent = new Intent(getBroadcastString()).putExtra(
				getBroadcastString(), result);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		

	}

}
