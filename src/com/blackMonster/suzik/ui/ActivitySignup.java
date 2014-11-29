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

import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.MainStaticElements;
import com.blackMonster.suzik.R;
import com.blackMonster.suzik.sync.Syncer;
import com.blackMonster.suzik.sync.contacts.ContactsSyncer;
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
		// MusicStoreManager.updateDatabase(this);

	}

	public void buttonSubmit(View v) {
		hideKeyboard();
		myNumber = ((EditText) findViewById(R.id.signup_number))
				.getEditableText().toString().trim();
		
		MainPrefs.setMyNo(myNumber, getApplicationContext());
		
		LocalBroadcastManager
		.getInstance(this)
		.registerReceiver(
				broadcastContactsSyncResult,
				new IntentFilter(
						ContactsSyncer.BROADCAST_CONTACTS_SYNC_RESULT));
		
		dialog = MainStaticElements.createProgressDialog("Logging in",this);
		dialog.show();
		
		startService(new Intent(this, ContactsSyncer.class));
		
	}

	private BroadcastReceiver broadcastContactsSyncResult = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			LOGD(TAG, "received : broadcastContactsSyncResult");
			
			LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
					broadcastContactsSyncResult);

			if (dialog != null)
				dialog.dismiss();
			dialog = null;
			boolean result;
			result = intent.getExtras().getBoolean(
					ContactsSyncer.BROADCAST_CONTACTS_SYNC_RESULT);

			if (result == true) {
				MainPrefs.setLoginDone(getBaseContext());
				startActivity(new Intent(getBaseContext(),ActivityTimeline.class));
				
				Syncer.callFuture(SongsSyncer.class, 10000, getBaseContext());
				finish();

			}
			else {
				MainPrefs.setMyNo("123", getBaseContext());
				Toast.makeText(getBaseContext(), "Error Logging in!", Toast.LENGTH_LONG).show();
			}
			
			LOGD(TAG, " " + result);

		}
	};
	
	/*private BroadcastReceiver broadcastUpdateDatabaseResult = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Log.d(TAG, "received : broadcastTempAttendenceResult");

			if (dialog != null)
				dialog.dismiss();
			dialog = null;
			boolean result;
			result = intent.getExtras().getBoolean(
					MusicStoreManager.BROADCAST_UPDATE_DATABASE_RESULT);

			if (result == true) {
				startActivity(new Intent(getBaseContext(), ActivityFriends.class));
				SendMyMusic.send(getBaseContext());
				finish();

			}
			Log.d(TAG, " " + result);

		}
	};
*/
	@Override
	protected void onPause() {
/*
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				broadcastContactsSyncResult);
	
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;

		}
		*/
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();

		

	}
	
	private void hideKeyboard() {
		InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		View focus = getCurrentFocus();
		if (focus != null)
			inputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(),
					InputMethodManager.HIDE_NOT_ALWAYS);
	}
	


}
