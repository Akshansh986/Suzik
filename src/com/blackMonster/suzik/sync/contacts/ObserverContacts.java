package com.blackMonster.suzik.sync.contacts;

import android.content.Context;
import android.os.Handler;

import com.blackMonster.suzik.sync.MyContentObserver;

public class ObserverContacts extends MyContentObserver{

	public ObserverContacts(Handler handler, Context context) {
		super(handler, context);
	}

	@Override
	public Class onContentChange() {
		return ContactsSyncer.class;
	}

}
