package com.blackMonster.suzik.musicstore.infoFromOtherPlayers;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

public class DuplicateBroadcastFilter {

	List<BroadcastInfo> broadcastList = new ArrayList<BroadcastInfo>();

	class BroadcastInfo {
		BroadcastSong song;
		String action;
		long time;
		boolean playing;

		public BroadcastInfo(BroadcastSong song, String action, long time, boolean playing) {
			this.song = song;
			this.action = action;
			this.time = time;
			this.playing = playing;
		}
	}

	boolean isDuplicate(BroadcastSong currSong, String currAction, boolean currPlaying, long currTime) {
		Log.d("DuplicateBroadcastfilter", "isDuplicate");
		boolean result = false, found = false;

		for (BroadcastInfo broadcast : broadcastList) {
			Log.d("DuplicateBroadcastfilter", broadcast.action + "  " + broadcast.song.track + "  " + broadcast.playing);
			if (broadcast.action.equals(currAction)) {

				if (broadcast.song.equals(currSong)
						&& (currTime - broadcast.time) <= 1000 && broadcast.playing == currPlaying)
					result = true;
				else
					result = false;
				updateBroadcast(broadcast, new BroadcastInfo(currSong, currAction, currTime, currPlaying));
				found = true;
				break;
			}

		}
		if (found == false) {
			broadcastList
					.add(new BroadcastInfo(currSong, currAction, currTime, currPlaying));
			result = false;
		}
		return result;

	}

	private void updateBroadcast(BroadcastInfo oldBroadcast,
			BroadcastInfo newBroadcast) {
		broadcastList.remove(oldBroadcast);
		broadcastList.add(newBroadcast);
		
	}

}
