package com.blackMonster.suzik.sync.contacts.model;

public class Contact {
	private String number;
	
	public Contact(String number) {
		this.number = number;
	}
	
	public String getNumber() {
		return number;
	}
	
	@Override
	public boolean equals(Object o) {
		Contact cn = (Contact) o;
		return cn.getNumber().equals(getNumber());
	}
	
	@Override
	public String toString() {
		return number;
	}

}
