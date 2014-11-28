package com.blackMonster.suzik.musicstore.module;

public class UserActivity {
	public static final int ACTION_IN_APP_DOWNLOAD = 4;
	public static final int ACTION_OUT_APP_DOWNLOAD = 3;
	public static final int ACTION_IN_APP_PLAYED = 2;
	public static final int ACTION_OUT_APP_PLAYED = 1;

	private static final String ACTION_IN_APP_DOWNLOAD_STRING = "inAppDownload";
	private static final String ACTION_OUT_APP_DOWNLOAD_STRING = "outAppDownload";
	private static final String ACTION_IN_APP_PLAYED_STRING = "inAppPlay";
	private static final String ACTION_OUT_APP_PLAYED_STRING = "outAppPlay";

	public static final int STREAMING_TRUE = 1;
	public static final int STREAMING_FALSE = 0;

	private Long id;
	private long songId, completedTS;
	private int streaming;
	private int action;

	public UserActivity(Long id, long songId, int action, int streaming,
			long completedTS) {
		super();
		this.id = id;
		this.songId = songId;
		this.action = action;
		this.streaming = streaming;
		this.completedTS = completedTS;
	}

	public Long id() {
		return id;
	}

	public long songId() {
		return songId;
	}

	public int action() {
		return action;
	}

	public int getStreaming() {
		return streaming;
	}
	
	public boolean isOnlineSong(){
		return streaming == STREAMING_TRUE;
	}

	public long completedTS() {
		return completedTS;
	}

	public String getActionString() {
		switch (action) {
		case ACTION_OUT_APP_PLAYED:
			return ACTION_OUT_APP_PLAYED_STRING;
		case ACTION_IN_APP_PLAYED:
			return ACTION_IN_APP_PLAYED_STRING;
		case ACTION_IN_APP_DOWNLOAD:
			return ACTION_IN_APP_DOWNLOAD_STRING;
		case ACTION_OUT_APP_DOWNLOAD:
			return ACTION_OUT_APP_DOWNLOAD_STRING;
		}
		return null;
	}

}
