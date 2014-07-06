package com.blackMonster.contacts;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.blackMonster.suzik.MainStaticElements;

public class ServiceSendMyContacts extends IntentService {
	private static final String TAG = "ServiceSendMyContactsTAG";
	public static final String BROADCAST_SEND_MY_CONTACTS_RESULT = "BROADCAST_SEND_MY_CONTACTS_RESULT";


	public ServiceSendMyContacts() {
		super(TAG);
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Log.d(TAG, "onHandleIntent");
		final String myNo = intent.getExtras().getString("myNumber");

		final String myContacts = getMyContacts(getContentResolver());
		
		Log.d(TAG, myContacts);
		new Thread() {
			public void run() {
				try {
					boolean result = sendContacts(myContacts,myNo);
					broadcastResult(result);

				} catch (Exception e) {
					broadcastResult(false);
				}
			}

		}.start();

	}

	private void broadcastResult(boolean result) {
		Intent intent = new Intent(BROADCAST_SEND_MY_CONTACTS_RESULT).putExtra(BROADCAST_SEND_MY_CONTACTS_RESULT, result);
		LocalBroadcastManager.getInstance(this).sendBroadcast(intent);		
	}

	private String getMyContacts(ContentResolver cr) {
		Log.d(TAG, "getMyconta");
		String myContacts = "";
		String phoneNumber;

		Cursor phones = cr.query(
				ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null,
				null, null);
		while (phones.moveToNext()) {
			// String name = phones
			// .getString(phones
			// .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
			phoneNumber = phones
					.getString(phones
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

			if (phoneNumber == null || phoneNumber.equals(""))
				continue;
			phoneNumber = ContactspublicFunctions.getNumberWithCountryCode(phoneNumber);
			Log.d(TAG, phoneNumber);
			myContacts += "$" + phoneNumber;

			// System.out.println(".................." + phoneNumber);
			// String in = name ;
			// sync_contacts_info task = new sync_contacts_info();
			// task.execute(new String[] { name ,phoneNumber });
			// aa.add(in);

		}
		phones.close();// close cursor
		// ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
		// android.R.layout.simple_list_item_1, aa);
		// lv.setAdapter(adapter);
		// display contact numbers in the list
		Log.d(TAG, myContacts);

		return myContacts;
	}

	private boolean sendContacts(String contacts, String myNo) {
		boolean result = false;
		HttpClient httpclient = new DefaultHttpClient();
		HttpPost httppost = new HttpPost(
				MainStaticElements.MAIN_URL + "make_contacts.php");

		try {
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(2);
			nameValuePairs.add(new BasicNameValuePair("no", myNo));
			nameValuePairs.add(new BasicNameValuePair("contacts", contacts));

			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost);
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					response.getEntity().getContent()));
			result = getResult(reader);
			reader.close();

		} catch (Exception e) {
			e.printStackTrace();
		}

		httpclient.getConnectionManager().shutdown();
		return result;

	}

	private boolean getResult(BufferedReader reader)
			throws BadHtmlSourceException, IOException {
		if (reachToData(reader, "RESULT").toUpperCase().contains("DONE"))
			return true;
		else
			return false;
	}

	public static Intent getIntent(String myNumber, Context context) {
		Intent intent = new Intent(context, ServiceSendMyContacts.class);
		intent.putExtra("myNumber", myNumber);
		return intent;
	}

	public String reachToData(BufferedReader reader, String tag)
			throws BadHtmlSourceException, IOException {
		String tmp;
		if (tag != null)
			tag = tag.toUpperCase();
		while (true) {
			tmp = reader.readLine();
			Log.d(TAG, tmp);
			if (tmp == null)
				throw new BadHtmlSourceException();

			if (tmp.toUpperCase().contains(tag))
				return tmp;

		}
	}

}
