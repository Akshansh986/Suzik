package com.blackMonster.musicstore.infoFromOtherPlayers;

import android.util.Log;

public class ExceptionUnknownBroadcast extends Exception {
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void printStackTrace() {
		 Log.e("UnknownBroadcast", "UnknownBroadcast");
		 super.printStackTrace();
	}
}
