package com.blackMonster.suzik;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (MainPrefs.getMyNo(this).equals("123"))
			startActivity(new Intent(this, ActivitySignup.class));
		else
			startActivity(new Intent(this, ActivityFriends.class));
		finish();
	}
}
