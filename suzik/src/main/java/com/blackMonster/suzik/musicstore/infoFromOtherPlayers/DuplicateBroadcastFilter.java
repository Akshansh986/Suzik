package com.blackMonster.suzik.musicstore.infoFromOtherPlayers;

import java.util.ArrayList;
import java.util.List;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

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
		LOGD("DuplicateBroadcastfilter", "isDuplicate");
		boolean result = false, found = false;

		BroadcastInfo tmpBroadcast=null;
		for (BroadcastInfo broadcast : broadcastList) {
			LOGD("DuplicateBroadcastfilter", broadcast.action + "  " + broadcast.song.getTitle() + "  " + broadcast.playing);
			if (broadcast.action.equals(currAction)) {

				if (broadcast.song.equals(currSong)
						&& (currTime - broadcast.time) <= 1000 && broadcast.playing == currPlaying)
					result = true;
				else
					result = false;
				found = true;
				tmpBroadcast = broadcast;
				break;
			}

		}

		if (tmpBroadcast !=null)
			updateBroadcast(tmpBroadcast, new BroadcastInfo(currSong, currAction, currTime, currPlaying));


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
