package com.blackMonster.suzik.musicPlayer;

import java.util.List;
import java.util.Random;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.os.Handler;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.blackMonster.suzik.ui.Playlist;

public class UIcontroller {
	private static final String TAG = "Suzikplayer_uicontroller";

	Handler mHandler=new Handler();

	Runnable mHandlerTask = new Runnable()
	{
	     @Override
	     public void run() {
	    	 Log.d(TAG,"Runnable setsong");
	    	 musicSrv.setSong(songs.getPlayable(songpos));

	     }
	};


	Playlist songs=null;
	boolean playbtn;
	int songpos=0;
	int repeat=0;
	boolean shuffle=false;
	Status savedStatus;
	boolean isphonestatechange=false;
	boolean isheadphonestatechange=false;
    private boolean isfocuslost;


	private PhoneStateListener phonestatelistener;
	private	TelephonyManager telephonyManager;
	AudioManager am;
	OnAudioFocusChangeListener afChangeListener;
	public static String brodcast_uidataupdate="uidataupdate";
	Intent Intent_uidataupdate;
	public static String brodcast_uibtnupdate="uibtnupdate";
	Intent Intent_uibtnupdate;

	public static String brodcast_resetui="resetui";
	Intent Intent_resetui;

	public static String brodcast_playercurrentstatus="playercurrentstatus";
	Intent Intent_playercurrentstatus;
	public static String brodcast_playersavedstatus="playersavedstatus";
	Intent Intent_playersavedstatus;

	public static final String brodcast_uiseek ="uiseek";
	Intent intent_uiseekintent;



	private MusicPlayerService musicSrv;
	private boolean musicBound=false;
	private Intent playIntent=null;


	Context context;
	private static UIcontroller instance=null;


	public static UIcontroller getInstance(Context context)

	{
		Log.d(TAG,"getInstance");

        if(instance==null)
		{
			instance=new UIcontroller(context.getApplicationContext());
		}

	return instance;

	}

	private UIcontroller(Context context) {
	// TODO Auto-generated constructor stub

		Log.d(TAG,"UIcontroller constuctor");
		this.context=context;

	Intent_uidataupdate=new Intent(brodcast_uidataupdate);
	Intent_uibtnupdate=new Intent(brodcast_uibtnupdate);

	intent_uiseekintent=new Intent(brodcast_uiseek);
	Intent_playercurrentstatus=new Intent(brodcast_playercurrentstatus);
	Intent_playersavedstatus=new Intent(brodcast_playersavedstatus);
	Intent_resetui=new Intent(brodcast_resetui);

    context.registerReceiver(broadcastreciever_songcomplete, new IntentFilter(MusicPlayerService.brodcast_playcomplete));
    context.registerReceiver(Broadcastreciever_headsetreciever,new IntentFilter(Intent.ACTION_HEADSET_PLUG));
	
	setaudiodocus();
   
 	}

	
	
	public void bindtoservice()
	{			Log.d(TAG,"bindtoservice"); 

		if(playIntent==null){
				Log.d(TAG,"play intent == null"); 

			playIntent = new Intent(context,MusicPlayerService.class);
		   boolean bool = context.bindService(playIntent,musicConnection,context.BIND_AUTO_CREATE);
		   ComponentName bool1 = context.startService(playIntent);
		   // Log.d(TAG,"bindtoservice  "+bool+"  "+bool1+"#########"); 
		  }
		else
		{		
			Log.d(TAG,"play intent != null");
			context.bindService(playIntent,musicConnection,context.BIND_AUTO_CREATE);

			
		}
		
		
	}
	
	
	private ServiceConnection musicConnection = new ServiceConnection(){
		 
		  @Override
		  public void onServiceConnected(ComponentName name, IBinder service) {
				Log.d(TAG,"service connected ");

			  MusicPlayerService.MusicBinder binder = (MusicPlayerService.MusicBinder)service;
		    //get service
		    musicSrv = binder.getService();
		  	//pass list
		
		 
		//Log.d("Suzikplayer","returned waiting to play");

		    musicBound = true;
		    
		    
		  }
		 
		  @Override
		  public void onServiceDisconnected(ComponentName name) {
				Log.d(TAG,"service disconnected ");

		    musicBound = false;
		  }
		};
		
		private BroadcastReceiver Broadcastreciever_headsetreciever=new BroadcastReceiver() 
		{
			@Override
			public void onReceive(Context arg0, Intent intent) {
				// TODO Auto-generated method stub
				Log.d(TAG,"Broadcastreciever_headsetreciever :onReceive "); 
				int state=intent.getIntExtra("state",100);
				if(state!=100){
					switch (state) {
					case 1:Log.d(TAG,"headphone connected "); 
                   					
								break;
						
					case 0:Log.d(TAG,"headphone disconnected "); 
                    if(isplaying())
					{		
						pauseSong();
						senduibtnsetbroadcast();

					}
						break;
					}
				}
			
			}
		};
		
		
		private BroadcastReceiver broadcastreciever_songcomplete=new BroadcastReceiver() {
			
			@Override
			public void onReceive(Context context, Intent intent) {
				// TODO Auto-generated method stub
				Log.d(TAG,"broadcastreciever_songcomplete :onReceive "); 

			

				if(repeat==1)
				{
						//seek(0);
						setSong();
				}
				else
				{	if(shuffle)
					{
						shuffleSong();
					}
					nextSong();

				}
				
				
			}

			
		};
		

		
	
	
	
	
	private void senduidatasetbroadcast() {
		// TODO Auto-generated method stub
		
			
			Intent_uidataupdate.putExtra("songTitleLabel", songs.getPlayable(songpos).getSong().getTitle());
			Intent_uidataupdate.putExtra("songAlbumName",songs.getPlayable(songpos).getSong().getAlbum());
			Intent_uidataupdate.putExtra("songArtistName", songs.getPlayable(songpos).getSong().getArtist());
			Intent_uidataupdate.putExtra("albumart",songs.getPlayable(songpos).getAlbumArtPath());
			Intent_uidataupdate.putExtra("songduration", songs.getPlayable(songpos).getSong().getDuration());

			 
			
			context.sendBroadcast(Intent_uidataupdate);
			Log.d(TAG,"uidataupdatebroadcast\n"+Intent_uidataupdate.toString()); 
		}
	

	private void senduibtnsetbroadcast() {
		// TODO Auto-generated method stub
		
			
			Intent_uibtnupdate.putExtra("isplaying",isplaying());
			Intent_uibtnupdate.putExtra("repeat",repeat);
			Intent_uibtnupdate.putExtra("shuffle",shuffle);
			
			 
			
			context.sendBroadcast(Intent_uibtnupdate);
			Log.d(TAG,"uibtnupdatebroadcast\n"+Intent_uibtnupdate.toString()); 
		}


	private void resetui() {
		// TODO Auto-generated method stub
		context.sendBroadcast(Intent_resetui);
		Log.d(TAG,"resetuibroadcast");


	}

	private void setaudiodocus() {
		// TODO Auto-generated method stub
		Log.d(TAG,"setaudiodocus"); 

		 am = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);
			
		  afChangeListener = new OnAudioFocusChangeListener() {

				public void onAudioFocusChange(int focusChange) {
			        if (focusChange ==AudioManager.AUDIOFOCUS_LOSS_TRANSIENT){
						Log.d(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
						isfocuslost=true;
						if(isplaying())
						{		
						{	isphonestatechange=true;
						   pauseSong();
							senduibtnsetbroadcast();

						}
							}

						
			        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
						Log.d(TAG, "AUDIOFOCUS_GAIN");
						isfocuslost=false;
						if(isphonestatechange)
						{   isphonestatechange=false;
						playSong();
						senduibtnsetbroadcast();

						}

			        } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
						Log.d(TAG, "AUDIOFOCUS_LOSS");
						isfocuslost=true;
						if(isplaying())
						{		
						{	isphonestatechange=true;
						   pauseSong();
							senduibtnsetbroadcast();

						}
							}

						
			            am.abandonAudioFocus(afChangeListener);

			        }
			    }
			};
		// Request audio focus for playback
		requestfocus();
		
		
		
	}

	private void requestfocus() {
		// TODO Auto-generated method stub
		Log.d(TAG,"requestfocus"); 

		int result = am.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			Log.d(TAG, "Audio focus gained");
			isfocuslost=false;
		}
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public void setList(Playlist playlist) {
		// TODO Auto-generated method stub
			Log.d(TAG,"setlist"); 
			songs=playlist;
			
		}
		public Playlist getList() {
		// TODO Auto-generated method stub
			Log.d(TAG,"getlist"); 
		return songs;
		}
			
		public void setSongpos(int songIndex)
		{  		
			Log.d(TAG,"setsongpos"); 
		     songpos=songIndex;
		     setSong();
		}
		private void setSong() {
			// TODO Auto-generated method stub
			Log.d(TAG,"setsong"); 
			resetui();

			senduidatasetbroadcast();
			if(isplaying())
			{
			pauseSong();
			}
            if(isfocuslost)
            {	requestfocus();

            }
			//delay100ms
			mHandler.removeCallbacks(mHandlerTask);
		    mHandler.postDelayed(mHandlerTask,500);

			
			
		}	
	public void playSong() {
		// TODO Auto-generated method stub
		Log.d(TAG,"play"); 
		if(isfocuslost)
		{	requestfocus();
			
		}
		musicSrv.playplayer();
	}
	public void pauseSong() {
		// TODO Auto-generated method stub
		Log.d(TAG,"pause"); 

		musicSrv.pausePlayer();
	}
	public void nextSong() {
		// TODO Auto-generated method stub
		Log.d(TAG,"next song"); 
		if(songs!=null)
		{	
			
			//resetui();

			if(repeat==1)
			{      
				seek(0);
			}
			else
			{	if(shuffle)
				{
					shuffleSong();
				}
				else{
					if(songpos==songs.getSongCount()-1 )
					{songpos=0;
					}
					else
					{
						songpos++;
					}
					setSong();


				}
	
			}

		}
	}
	public void prevSong() {
		// TODO Auto-generated method stub
		Log.d(TAG,"prevsong"); 
		if(songs!=null)
		{		
			//resetui();
			if(repeat==1)
			{
			
					seek(0);
			}
			else
			{	if(shuffle)
				{
					shuffleSong();
				}
				else{
				if(songpos==0)
					   songpos=songs.getSongCount()-1;
				   else
				   {   songpos--;
				   }
				
				setSong();


				}
	
			}

			
		}
	}
	public void stopSong() {
		// TODO Auto-generated method stub
		Log.d(TAG,"stopSong");

		musicSrv.stopPlayer();
	
	}
	
	public void shuffleSong() {
		// TODO Auto-generated method stub
		Log.d(TAG,"shuffleSong");

		Random r=new Random();
		songpos=r.nextInt(songs.getSongCount());
		setSong();
	}
	public void setrepeatStatus() {
		// TODO Auto-generated method stub
		Log.d(TAG,"setrepeatStatus");

		repeat=(repeat+1)%3;
	}
	public void setshuffleStatus() {
		// TODO Auto-generated method stub
		Log.d(TAG,"setshuffleStatus");

		shuffle=!shuffle;
	}
	public void seek(int progress) {
		// TODO Auto-generated method stub
		Log.d(TAG,"seek");

        if(musicSrv!=null){
            musicSrv.seek(progress);
        }
        /*intent_uiseekintent.putExtra("seekui",progress);
		context.sendBroadcast(intent_uiseekintent);
		*/

	}

	public void unbind() {
		// TODO Auto-generated method stub
		Log.d(TAG,"unbind");
		if(musicConnection!=null){
			context.unbindService(musicConnection);

		}
	}
	public boolean isplaying() {
		// TODO Auto-generated method stub
		Log.d(TAG,"isplaying");

		if(musicSrv!=null)
		{
			return musicSrv.isplaying();
		}
		return false;
	}
	public void loadcurrentplayerstatus() {
		// TODO Auto-generated method stub
		Log.d(TAG,"loadcurrentplayerstatus");

		if(musicSrv!=null)
		{
			if(songs!=null)
			
			{
		Status s=musicSrv.getplayerstatus();
		if(s!=null){
			Intent_playercurrentstatus.putExtra("songTitleLabel",songs.getPlayable(songpos).getSong().getTitle());
			Intent_playercurrentstatus.putExtra("songAlbumName",songs.getPlayable(songpos).getSong().getAlbum());
			Intent_playercurrentstatus.putExtra("songArtistName",songs.getPlayable(songpos).getSong().getArtist());
			Intent_playercurrentstatus.putExtra("albumart",songs.getPlayable(songpos).getAlbumArtPath());
			Intent_playercurrentstatus.putExtra("isplaying",s.isPlaying());
			Intent_playercurrentstatus.putExtra("currentpos",s.getCurrentPosition());
			Intent_playercurrentstatus.putExtra("duration",s.getDuration());
            Intent_playercurrentstatus.putExtra("isbuffering",s.isBuffering());
            Intent_playercurrentstatus.putExtra("shuffle",shuffle);
			Intent_playercurrentstatus.putExtra("repeat",repeat);



			context.sendBroadcast(Intent_playercurrentstatus);
			Log.d(TAG,"playercurrentstatus\n"+Intent_playercurrentstatus.toString()); 
				
		}
			}
		}
		
		
		
	}
	public void loadsavedplayerstatus() {
		// TODO Auto-generated method stub
		Log.d(TAG,"loadsavedplayerstatus");

		if(songs==null)
		{
			Intent_playersavedstatus.putExtra("songTitleLabel",songs.getPlayable(songpos).getSong().getTitle());
			Intent_playersavedstatus.putExtra("songAlbumName",songs.getPlayable(songpos).getSong().getAlbum());
			Intent_playersavedstatus.putExtra("songArtistName",songs.getPlayable(songpos).getSong().getArtist());
			Intent_playersavedstatus.putExtra("albumart",songs.getPlayable(songpos).getAlbumArtPath());
			Intent_playersavedstatus.putExtra("isplaying",savedStatus.isPlaying());
			Intent_playersavedstatus.putExtra("currentpos",savedStatus.getCurrentPosition());
			Intent_playersavedstatus.putExtra("duration",savedStatus.getDuration());
			Intent_playersavedstatus.putExtra("shuffle",shuffle);
			Intent_playersavedstatus.putExtra("repeat",repeat);

		}
		else
		
		
		Intent_playersavedstatus.putExtra("songTitleLabel",songs.getPlayable(songpos).getSong().getTitle());
		Intent_playersavedstatus.putExtra("songAlbumName",songs.getPlayable(songpos).getSong().getAlbum());
		Intent_playersavedstatus.putExtra("songArtistName",songs.getPlayable(songpos).getSong().getArtist());
		Intent_playersavedstatus.putExtra("albumart",songs.getPlayable(songpos).getAlbumArtPath());
		Intent_playersavedstatus.putExtra("isplaying",savedStatus.isPlaying());
		Intent_playersavedstatus.putExtra("currentpos",savedStatus.getCurrentPosition());
		Intent_playersavedstatus.putExtra("duration",savedStatus.getDuration());
		Intent_playersavedstatus.putExtra("shuffle",shuffle);
		Intent_playersavedstatus.putExtra("repeat",repeat);
		
		 
		
		context.sendBroadcast(Intent_playersavedstatus);
		Log.d(TAG,"playersavedstatus\n"+Intent_playersavedstatus.toString()); 

		
	}

	public void stophandler() {
		// TODO Auto-generated method stub
		Log.d(TAG,"stophandler");

		if(musicSrv!=null)
		{
			musicSrv.stophandler();
		}
		
	}
	public void starthandler() {
		// TODO Auto-generated method stub
		Log.d(TAG,"starthandler");

		if(musicSrv!=null)
		{
			musicSrv.starthandler();
		}
		
	}
	
	
	
	
	
}
