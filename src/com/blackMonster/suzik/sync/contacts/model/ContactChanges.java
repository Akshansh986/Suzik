package com.blackMonster.suzik.sync.contacts.model;

public class ContactChanges {
	public static final int ACTION_DELETED = 0;
	public static final int ACTION_ADDED = 1;
	
	public static final String ACTION_ADDED_STRING = "add";
	public static final String ACTION_DELETED_STRING = "remove";

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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + action;
		result = prime * result + ((contact == null) ? 0 : contact.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContactChanges other = (ContactChanges) obj;
		if (action != other.action)
			return false;
		if (contact == null) {
			if (other.contact != null)
				return false;
		} else if (!contact.equals(other.contact))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "ContactChanges [action=" + action + ", contact=" + contact
				+ "]";
	}

	public String getActionString() {
		if (action == ACTION_ADDED)
			return ACTION_ADDED_STRING;
		else if (action == ACTION_DELETED)
			return ACTION_DELETED_STRING;

		return null;
	}

}
