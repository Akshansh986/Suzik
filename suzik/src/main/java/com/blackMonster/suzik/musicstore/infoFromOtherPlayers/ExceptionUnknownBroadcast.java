package com.blackMonster.suzik.musicstore.infoFromOtherPlayers;

import static com.blackMonster.suzik.util.LogUtils.LOGE;

public class ExceptionUnknownBroadcast extends Exception {
	
	 /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void printStackTrace() {
		 LOGE("UnknownBroadcast", "UnknownBroadcast");
		 super.printStackTrace();
	}
}
