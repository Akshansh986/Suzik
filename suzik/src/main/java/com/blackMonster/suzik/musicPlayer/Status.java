package com.blackMonster.suzik.musicPlayer;

import com.blackMonster.suzik.musicstore.Timeline.Playable;

public class Status {
    boolean errorState;
    Playable playable;
	int currentPosition;
	int duration;
	boolean playing;
    boolean buffering;
	public Status(int currentPosition, int duration, boolean playing) {
		// TODO Auto-generated constructor stub
		this.currentPosition=currentPosition;
		this.duration=duration;
		this.playing=playing;
	}

    public Status(int currentPosition, int duration, boolean playing, boolean isbuffering) {
        this.currentPosition=currentPosition;
        this.duration=duration;
        this.playing=playing;
        this.buffering=isbuffering;

    }

    public Status(Playable currentSong, int currentPosition, int duration, boolean isplaying, boolean isbufferingstate) {
        this.playable=currentSong;
        this.currentPosition=currentPosition;
        this.duration=duration;
        this.playing=isplaying;
        this.buffering=isbufferingstate;

    }

    public Status(Playable currentSong, int currentPosition, int duration, boolean isplaying, boolean isbufferingstate, boolean onErrorState) {
        this.playable=currentSong;
        this.currentPosition=currentPosition;
        this.duration=duration;
        this.playing=isplaying;
        this.buffering=isbufferingstate;
        this.errorState=onErrorState;
    }

    public boolean isOnErrorState() { return errorState; }
    public void setErrorState(boolean errorState) {  this.errorState = errorState;}
    public int getCurrentPosition() {
		return currentPosition;
	}
	public void setCurrentPosition(int currentPosition) {
		this.currentPosition = currentPosition;
	}
	public int getDuration() {
		return duration;
	}
	public void setDuration(int duration) {
		this.duration = duration;
	}
	public boolean isPlaying() {
		return playing;
	}
	public void setPlaying(boolean playing) {
		this.playing = playing;
	}
    public boolean isBuffering() { return buffering;  }
    public void setBuffering(boolean buffering) { this.buffering = buffering; }
}
