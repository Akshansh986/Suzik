package com.blackMonster.suzik.sync.contacts;

import java.util.HashMap;
import java.util.HashSet;

import android.content.Context;

import com.blackMonster.suzik.sync.contacts.model.Contact;
import com.blackMonster.suzik.sync.contacts.model.ContactChanges;

public class SyncHelper {
	Context context;
	
	public  boolean performSync(Context context) {
		this.context = context;
		
		boolean result;
		HashSet<Contact> serverContactList;
		try {
			serverContactList = getFromServerOrCacheTable();
			HashSet<Contact> androidContactList = getFromAndroid();
			HashSet<ContactChanges> contactChanges = getChanges(serverContactList,androidContactList);

			HashMap<Contact, Integer> updateServerResult = ServerHelper.updateServer(contactChanges);
			result =  updateChangesToCacheTable(contactChanges,updateServerResult);
		} catch (Exception e) {
			result = false;
			e.printStackTrace();
		}
		
		return result;
		
		
	}

	private boolean updateChangesToCacheTable(
			HashSet<ContactChanges> contactChanges,
			HashMap<Contact, Integer> updateServerResult) {
		if (contactChanges == null || updateServerResult == null) return false;
		
		for (ContactChanges changes : contactChanges) {
			if (updateServerResult.containsKey(changes.getContact())) {
			//	if (updateServerResult.)
			}
		}
		
		
		
		return false;
	}

	private HashSet<ContactChanges> getChanges(
			HashSet<Contact> serverContactList,
			HashSet<Contact> androidContactList) {
		
		if (serverContactList == null || androidContactList == null) return null;
		
		HashSet<ContactChanges> changes = new HashSet<ContactChanges>();
		
		for (Contact cn : serverContactList) {
			if (!androidContactList.contains(cn)) {
				changes.add(new ContactChanges(cn, ContactChanges.ACTION_DELETED));
			}
		}
		
		for (Contact cn : androidContactList) {
			if (!serverContactList.contains(cn)) {
				changes.add(new ContactChanges(cn, ContactChanges.ACTION_DELETED));
			}
		}
		
		return changes;
	}

	private HashSet<Contact> getFromAndroid() throws Exception {
		return AndroidHelper.getMyContacts(context);
	}

	private HashSet<Contact> getFromServerOrCacheTable() throws Exception {
		
		if (CacheTable.isEmpty(context)) {
			HashSet<Contact> serverContacts = ServerHelper.getMyContacts();
			CacheTable.insert(serverContacts, context);
		}
		
		return CacheTable.getMyContacts(context);
	}

}
