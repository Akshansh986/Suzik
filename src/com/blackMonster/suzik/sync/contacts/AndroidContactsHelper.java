package com.blackMonster.suzik.sync.contacts;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGE;

import java.util.HashSet;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.blackMonster.suzik.sync.contacts.model.Contact;

public class AndroidContactsHelper {
	private static final String TAG = "AndroidContactsHelper";
	public static Uri URI = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;

	   private static String[] projectionPhones = {
		        ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
		        ContactsContract.CommonDataKinds.Phone.NUMBER,
		        ContactsContract.CommonDataKinds.Phone.TYPE,
		        ContactsContract.CommonDataKinds.Phone.LABEL
		    };
	   
	   

	public static HashSet<Contact> getMyContacts(Context context) throws Exception {
	ContentResolver cr = context.getApplicationContext()
				.getContentResolver();
	HashSet<Contact> contactList = new HashSet<Contact>();

		LOGD(TAG, "In getMycontacts");
		String phoneNumber;

		Cursor cursor = cr.query(
				URI, projectionPhones, null,
				null, null);

		if (cursor != null) {
			while (cursor.moveToNext()) {
				// String name = phones
				// .getString(phones
				// .getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
				phoneNumber = cursor
						.getString(cursor
								.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

				if (phoneNumber == null || phoneNumber.equals("") )
					continue;
				//LOGD(TAG,phoneNumber);
				

				phoneNumber = FormatContact.standerdizeNumber(phoneNumber);
				contactList.add(new Contact(phoneNumber));

			}
			cursor.close();
		} else {
			LOGE(TAG, "Cursor is null");
		}

		return contactList;

	}

}
