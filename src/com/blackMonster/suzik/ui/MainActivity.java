package com.blackMonster.suzik.ui;

import com.blackMonster.suzik.sync.contacts.AndroidHelper;

import android.app.Activity;
import android.os.Bundle;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		AndroidHelper.getMyContacts(this);
		
		
		
		
		/*
		if (MainPrefs.getMyNo(this).equals("123"))
			startActivity(new Intent(this, ActivitySignup.class));
		else
			startActivity(new Intent(this, ActivityFriends.class));
		finish();*/
	}
}
