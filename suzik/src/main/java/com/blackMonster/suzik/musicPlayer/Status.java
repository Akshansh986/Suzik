package com.blackMonster.suzik.musicPlayer;

public class Status {

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
