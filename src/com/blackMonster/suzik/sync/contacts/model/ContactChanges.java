package com.blackMonster.suzik.sync.contacts.model;

public class ContactChanges {
	public static final int ACTION_DELETED = 0;
	public static final int ACTION_ADDED = 1;

	int action;
	Contact contact;
	
	public ContactChanges(Contact contact, int action) {
		this.contact = contact;
		this.action = action;
	}
	
	public Contact getContact() {
		return contact;
	}
	
	public int getAction() {
		return action;
	}
}
