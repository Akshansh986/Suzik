package com.blackMonster.suzik;

public class AppConfig {
	public static final boolean DEBUG = true;
	public static final String MAIN_URL = "http://niksqwer.5gbfree.com/music/reciver.php";
	
	public static final long MINUTE_IN_MILLISEC = 60000;
	public static final long TIME_PROCESSING_NEW_SONG_SERVER_MS = 1000 ;  //1 MINUTE IN MILLISEC
	public static final long SYNCER_TIME_RETRY_MS = 1 * MINUTE_IN_MILLISEC ; //millisec

}
