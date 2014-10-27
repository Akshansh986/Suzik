package com.blackMonster.suzik.musicstore.module;

public class UserActivity {
	public static final int ACTION_IN_APP_DOWNLOAD = 4;
	public static final int ACTION_OUT_APP_DOWNLOAD = 3;
	public static final int ACTION_IN_APP_PLAYED = 2;
	public static final int ACTION_OUT_APP_PLAYED = 1;
	
	private long localId;
	private long serverId;
	private int action;
	public UserActivity(long localId, long serverId, int action) {
		super();
		this.localId = localId;
		this.serverId = serverId;
		this.action = action;
	}
	
	
	public UserActivity(long serverId, int action) {
		super();
		this.serverId = serverId;
		this.action = action;
	}


	public long getLocalId() {
		return localId;
	}
	public long getServerId() {
		return serverId;
	}
	public int getAction() {
		return action;
	}
	
	
	
	
	
	
}
