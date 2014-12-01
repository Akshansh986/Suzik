package com.blackMonster.suzik.ui;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.MainStaticElements;
import com.blackMonster.suzik.R;
import com.blackMonster.suzik.sync.Syncer;
import com.blackMonster.suzik.sync.contacts.ContactsSyncer;
import com.blackMonster.suzik.sync.music.InitMusicDb;
import com.blackMonster.suzik.sync.music.SongsSyncer;

public class ActivitySignup extends Activity {
	private static final String TAG = "ActivitySignup";

	AlertDialog dialog = null;
	String myNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		LOGD(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);

	}

	public void buttonSubmit(View v) {
		DbHelper.shutDown();
		if (deleteDatabase(DbHelper.DB_NAME)) LOGD(TAG,"old database deleted");
		myNumber = ((EditText) findViewById(R.id.signup_number))
				.getEditableText().toString().trim();
		if (myNumber.length() != 10) return;
		
		hideKeyboard();
		myNumber = "+91" + myNumber;

		MainPrefs.setMyNo(myNumber, getApplicationContext());

		dialog = MainStaticElements.createProgressDialog("Logging in", this);
		dialog.show();

		unregisterReceivers();
		registerReceivers();

		startService(new Intent(this, InitMusicDb.class));

	}

	private void unregisterReceivers() {
		LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
				broadcastContactsSyncResult);

		LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
				broadcastInitMusicDbResult);
	}

	private void registerReceivers() {
		LocalBroadcastManager.getInstance(this)
				.registerReceiver(
						broadcastContactsSyncResult,
						new IntentFilter(
								ContactsSyncer.BROADCAST_CONTACTS_SYNC_RESULT));

		LocalBroadcastManager.getInstance(this).registerReceiver(
				broadcastInitMusicDbResult,
				new IntentFilter(InitMusicDb.BROADCAST_INIT_MUSIC_DB_RESULT));

	}

	// private int IN_PROGRESS = 1, DONE=2,ERROR=3;
	// private int initMusicDbStatus = IN_PROGRESS, contactsSyncStatus =
	// IN_PROGRESS;
	private BroadcastReceiver broadcastInitMusicDbResult = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			LOGD(TAG, "received : broadcastInitMusicDbResult");

			LocalBroadcastManager.getInstance(getBaseContext())
					.unregisterReceiver(broadcastInitMusicDbResult);

			if (dialog != null)
				dialog.dismiss();
			dialog = null;

			boolean result = intent.getExtras().getBoolean(
					InitMusicDb.BROADCAST_INIT_MUSIC_DB_RESULT);

			if (result == true) {
				startService(new Intent(getBaseContext(), ContactsSyncer.class));
				dialog = MainStaticElements.createProgressDialog("Logging in",
						ActivitySignup.this);
				dialog.show();
				// MainPrefs.setLoginDone(getBaseContext());
				// startActivity(new Intent(getBaseContext(),
				// ActivityTimeline.class));

				// Syncer.callFuture(SongsSyncer.class, 10000,
				// getBaseContext());
				// finish();

			} else {
				MainPrefs.setMyNo("123", getBaseContext());
				Toast.makeText(getBaseContext(), "Error Logging in!",
						Toast.LENGTH_LONG).show();
			}

		}
	};

	private BroadcastReceiver broadcastContactsSyncResult = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			LOGD(TAG, "received : broadcastContactsSyncResult");

			LocalBroadcastManager.getInstance(getBaseContext())
					.unregisterReceiver(broadcastContactsSyncResult);

			if (dialog != null)
				dialog.dismiss();
			dialog = null;

			boolean result = intent.getExtras().getInt(
					ContactsSyncer.BROADCAST_CONTACTS_SYNC_RESULT) == Syncer.STATUS_OK;
			if (result == true) {
				MainPrefs.setLoginDone(getBaseContext());
				startActivity(new Intent(getBaseContext(),
						ActivityTimeline.class));

				Syncer.callFuture(SongsSyncer.class, 10000, getBaseContext());
				finish();

			} else {
				MainPrefs.setMyNo("123", getBaseContext());
				Toast.makeText(getBaseContext(), "Error Logging in!",
						Toast.LENGTH_LONG).show();
			}

		}
	};

	protected void onDestroy() {
		super.onDestroy();
		unregisterReceivers();
	};

	private void hideKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		View focus = getCurrentFocus();
		if (focus != null)
			inputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
	}

}
