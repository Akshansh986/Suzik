package com.blackMonster.suzik;

public class AppConfig {
	public static final boolean DEBUG = true;
	public static final String MAIN_URL = "http://Default-Environment-7zznd9kj6u.elasticbeanstalk.com/music/reciver.php";
	public static final String FLAG_URL = "http://Default-Environment-7zznd9kj6u.elasticbeanstalk.com/music/songLink/flagSong.php";

	
	public static final long MINUTE_IN_MILLISEC = 60000;
	public static final long TIME_PROCESSING_NEW_SONG_SERVER_MS = 3000 ;  //1 MINUTE IN MILLISEC
//	public static final long SYNCER_TIME_RETRY_MS = 1 * MINUTE_IN_MILLISEC ; //millisec

}
