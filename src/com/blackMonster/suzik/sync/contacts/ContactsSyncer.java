package com.blackMonster.suzik.sync.contacts;

import static com.blackMonster.suzik.util.LogUtils.LOGI;
import static com.blackMonster.suzik.util.LogUtils.LOGD;

import java.util.HashMap;
import java.util.HashSet;

import com.blackMonster.suzik.sync.Syncer;
import com.blackMonster.suzik.sync.contacts.JsonHelper.UpdateContacts;
import com.blackMonster.suzik.sync.contacts.model.Contact;
import com.blackMonster.suzik.sync.contacts.model.ContactChanges;

public class ContactsSyncer extends Syncer {
	private static final String TAG = "ContactsSyncer";

	@Override
	public  boolean onPerformSync() throws Exception {
		LOGI(TAG,"performing Sync");

		boolean result;
		HashSet<Contact> serverContactList;

		serverContactList = getFromServerOrCacheTable();
		LOGD(TAG,"getFromServerOrCacheTable done");

		HashSet<Contact> androidContactList = getFromAndroid();
		LOGD(TAG,"get form andorid done");

		HashSet<ContactChanges> contactChanges = getChanges(serverContactList,
				androidContactList);
		LOGD(TAG,"changes done");

		HashMap<Contact, Integer> updateServerResult = ServerHelper
				.updateServer(contactChanges);
		LOGD(TAG,"update server done");

		result = updateChangesToCacheTable(contactChanges, updateServerResult);
		LOGD(TAG,"update Cachetable done");

		LOGD(TAG,"all done");

		return result;

	}

	private boolean updateChangesToCacheTable(
			HashSet<ContactChanges> contactChanges,
			HashMap<Contact, Integer> updateServerResult) {
		boolean result = true;
		if (contactChanges == null || updateServerResult == null)
			return false;

		for (ContactChanges changes : contactChanges) {
			if (updateServerResult.containsKey(changes.getContact())) {

				
				if (updateServerResult.get(changes.getContact()) == UpdateContacts.STATUS_DONE) {
					LOGD(TAG,"inserting" + changes.getContact().getNumber());
					CacheTable.insert(changes.getContact(), this);
				} else {
					LOGD(TAG,"status false " + changes.getContact().getNumber());
					result = false;
				}

			
			} else {
				LOGD(TAG,"not found " + changes.getContact().getNumber());
				result = false;
			}
		}

		return result;
	}

	private HashSet<ContactChanges> getChanges(
			HashSet<Contact> serverContactList,
			HashSet<Contact> androidContactList) {

		if (serverContactList == null || androidContactList == null)
			return null;

		HashSet<ContactChanges> changes = new HashSet<ContactChanges>();

		for (Contact cn : serverContactList) {
			if (!androidContactList.contains(cn)) {
				changes.add(new ContactChanges(cn,
						ContactChanges.ACTION_DELETED));
			}
		}

		for (Contact cn : androidContactList) {
			if (!serverContactList.contains(cn)) {
				changes.add(new ContactChanges(cn, ContactChanges.ACTION_ADDED));
			}
		}

		return changes;
	}

	private HashSet<Contact> getFromAndroid() throws Exception {
		return AndroidHelper.getMyContacts(this);
	}

	private HashSet<Contact> getFromServerOrCacheTable() throws Exception {

		if (CacheTable.isEmpty(this)) {
			HashSet<Contact> serverContacts = ServerHelper.getMyContacts();
			CacheTable.insert(serverContacts, this);
		}

		return CacheTable.getMyContacts(this);
	}

}