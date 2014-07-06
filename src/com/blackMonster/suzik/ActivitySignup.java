package com.blackMonster.suzik;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.blackMonster.contacts.ContactspublicFunctions;
import com.blackMonster.contacts.ServiceSendMyContacts;
import com.blackMonster.musicstore.MusicStoreManager;
import com.blackMonster.musicstore.SendMyMusic;

public class ActivitySignup extends Activity {
	private static final String TAG = "ActivitySignup";

	AlertDialog dialog = null;
	String myNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		Log.d(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_signup);
		// MusicStoreManager.updateDatabase(this);

	}

	public void buttonSubmit(View v) {
		myNumber = ((EditText) findViewById(R.id.signup_number))
				.getEditableText().toString().trim();
		myNumber = ContactspublicFunctions.getNumberWithCountryCode(myNumber);

		Log.d(TAG, "buttonSubmit" + myNumber);
		startService(ServiceSendMyContacts.getIntent(myNumber, this));

		dialog = MainStaticElements
				.createProgressDialog(R.string.loading, this);
		dialog.show();

	}

	private BroadcastReceiver broadcastSendMyContactsResult = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Log.d(TAG, "received : broadcastTempAttendenceResult");

			if (dialog != null)
				dialog.dismiss();
			dialog = null;
			boolean result;
			result = intent.getExtras().getBoolean(
					ServiceSendMyContacts.BROADCAST_SEND_MY_CONTACTS_RESULT);

			if (result == true) {
				MainPrefs.setMyNo(myNumber, getApplicationContext());
				MusicStoreManager.updateDatabase(getBaseContext());
				dialog = MainStaticElements
						.createProgressDialog(R.string.loading, getActivity());
				dialog.show();

			}
			Log.d(TAG, " " + result);

		}
	};
	
	private BroadcastReceiver broadcastUpdateDatabaseResult = new BroadcastReceiver() {
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

	@Override
	protected void onPause() {

		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				broadcastSendMyContactsResult);
		LocalBroadcastManager.getInstance(this).unregisterReceiver(
				broadcastUpdateDatabaseResult);

		if (dialog != null) {
			dialog.dismiss();
			dialog = null;

		}
		super.onPause();

	}

	@Override
	protected void onResume() {
		super.onResume();

		LocalBroadcastManager
				.getInstance(this)
				.registerReceiver(
						broadcastSendMyContactsResult,
						new IntentFilter(
								ServiceSendMyContacts.BROADCAST_SEND_MY_CONTACTS_RESULT));
		LocalBroadcastManager
		.getInstance(this)
		.registerReceiver(
				broadcastUpdateDatabaseResult,
				new IntentFilter(
						MusicStoreManager.BROADCAST_UPDATE_DATABASE_RESULT));

	}
	
	private Activity getActivity() {
		return this;
	}

}
