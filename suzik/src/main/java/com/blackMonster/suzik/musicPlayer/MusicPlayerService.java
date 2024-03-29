package com.blackMonster.suzik.musicPlayer;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Pair;
import android.widget.RemoteViews;

import com.blackMonster.suzik.R;
import com.blackMonster.suzik.musicstore.Timeline.Playable;
import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.musicstore.userActivity.UserActivityManager;
import com.blackMonster.suzik.sync.music.MusicSyncManager;
import com.blackMonster.suzik.ui.Screens.MainSliderActivity;
import com.blackMonster.suzik.util.NetworkUtils;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGE;

public class MusicPlayerService extends Service
        implements OnPreparedListener, OnErrorListener, OnCompletionListener, OnSeekCompleteListener, OnInfoListener {

    private static final String TAG = "Suzikplayer_service";

    private static final int NOTIFY_ID = 1;

    // resources for media player
    private MediaPlayer player = null;
    private int setseekintent = 0;
    private int setstopintent = 0;
    private int setplaypauseintent = 0;


    final int idle = 0;
    final int initialized = 1;
    final int preparing = 2;
    final int prepared = 3;
    final int started = 4;
    final int stopped = 5;
    final int paused = 6;
    final int playbackcomplete = 7;
    final int end = 8;
    final int error = 9;
    int mediaplayerstate;
    private Playable CurrentSong;
    private boolean isplay;
    private boolean isasyncplay;
    private boolean isInternetAvailable;
    public boolean isReleased=false;

    private boolean sendInAppPlay = false;
    WorkerThread worker;

    private boolean isBufferingState = false;
    private int onErrorState = PlayerErrorCodes.NO_ERROR;

    //for binding with the uicontrollerclass
    private final IBinder musicBind = new MusicBinder();


    public class MusicBinder extends Binder {
        public MusicPlayerService getService() {
            return MusicPlayerService.this;
        }
    }


    //broadcast
    //for buffering
    public static String brodcast_bufferingplayer = "bufferingmedia";
    Intent Intent_bufferplayerintent;
    //for completion
    public static String brodcast_playcomplete = "playingcomplete";
    Intent Intent_completionplayerintent;
    //for error
    public static String broadcastError = "errorBroadcast";
    Intent Intent_error;
    //for seekbar
    int seekpos;
    int mediapos;
    int mediamax;
    private static int songended;
    private final Handler handler = new Handler();
    //for autoseek update
    public static final String broadcast_playerseek = "seek";
    Intent Intent_Musicplayer_seekIntent;
    private Runnable sendUpdatestoui = new Runnable() {
        @Override
        public void run() {
            // TODO Auto-generated method stub

            logmediaposition();
            handler.postDelayed(this, 500);
        }
    };

    private void setuphandler() {
        LOGD(TAG, "setuphandler");

        handler.removeCallbacks(sendUpdatestoui);
        handler.postDelayed(sendUpdatestoui, 500);
        // TODO Auto-generated method stub

    }


    protected void logmediaposition() {
        if (isplaying()) {
            sendautoseekupdatebroadcast();
        }
        // TODO Auto-generated method stub

    }

    //for manual seek change listening
    private BroadcastReceiver notificationbroadcastreciever = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            // TODO Auto-generated method stub
            LOGD(TAG, "onReceive");

        }
    };
    private boolean isseekbarupdateuiregistered;
    private BroadcastReceiver broadcastReceiver_seekbaruiupdate = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            LOGD(TAG, "broadcastReceiver_seekbaruiupdate:onReceive");
            updateseekpos(intent);
        }
    };


    protected void updateseekpos(Intent intent) {
        int seekpos = intent.getIntExtra("seekui", 0);
        //if(player.isPlaying())
        //{
        //		handler.removeCallbacks(sendUpdatestoui);
        seek(seekpos);
        setuphandler();

        //}
        // TODO Auto-generated method stub

    }


    public void initMusicPlayer() {
        LOGD(TAG, "service: intialize music player ");
        //set player properties
        player.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
        player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        player.setOnPreparedListener(this);
        player.setOnCompletionListener(this);
        player.setOnErrorListener(this);
        player.setOnInfoListener(this);

    }


    public void setSong(final Playable playable) {


        Runnable playRunnable = new Runnable() {
            @Override
            public void run() {

                if (isplaying()) player.stop();
                player.release();
                player = null;

                player = new MediaPlayer();
                isReleased=false;

                mediaplayerstate = idle;
                fullfillintent();


                initMusicPlayer();


                sendInAppPlayBroadcast();


                if (player != null) {
                    player.reset();
                    mediaplayerstate = idle;
                    fullfillintent();
                }
                CurrentSong = playable;
                if (CurrentSong != null)
                    LOGD(TAG, "ab update hoga%%%%%%%%%%%" + CurrentSong.getSong().getAlbum() + CurrentSong.getSong().getTitle() + "%%%%%%%%%%%%");
                LOGD(TAG, "^^^^^^^^^^^^^^^^^^" + CurrentSong);
                if (playable.isOffline() == false) {
                    isInternetAvailable = NetworkUtils.isInternetAvailable(getApplicationContext());
                }
                if (mediaplayerstate == idle) {
                    try {
                        //player.setDataSource("https://www.dropbox.com/s/jef2cfbpdmj3sr4/Contemporary%20Dance%20Solo.mp4?dl=0");

                        player.setDataSource(playable.getSongPath());
                        mediaplayerstate = initialized;
                        fullfillintent();
                        LOGD(TAG, "Musicplayerservice: song set == " + playable.getSongPath());
                        LOGD(TAG, "Musicplayerservice: player resource set");

                    } catch (Exception e) {
                        LOGE("MUSIC SERVICE", "Error setting data source");
                        onErrorState = PlayerErrorCodes.DATA_SRC;
                        sendOnErrorStateBroadcast(onErrorState);
                    }
                    LOGD(TAG, "Musicplayerservice: songs set complete ");
                } else {
                    LOGD(TAG, "Error setting data source ");



                }


                String songTitle = playable.getSong().getTitle();
                String songArtist = playable.getSong().getArtist();

                Intent notIntent = new Intent(getBaseContext(), MainSliderActivity.class);
                notIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                PendingIntent pendInt = PendingIntent.getActivity(getBaseContext(), 0, notIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                RemoteViews views = new RemoteViews(getPackageName(), R.layout.smallwidget);
                views.setImageViewResource(R.id.notification_small_albumart, R.drawable.album_art);
                views.setTextViewText(R.id.notification_small_songtitle, songTitle);
                views.setTextViewText(R.id.notifiacation_small_artist, songArtist);

                Notification notification =  PlayerNotification.getInstance(getBaseContext()).buildNotification();
                if (notification != null ) startForeground(PlayerNotification.mNotificationId,notification);

                if (mediaplayerstate == initialized || mediaplayerstate == stopped) {
                    sendbufferingbroadcast();
                    player.prepareAsync();
                    mediaplayerstate = preparing;
                    fullfillintent();
                } else {
                    LOGD(TAG, "Error prepareAsync ");
                }


            }
        };


        worker.doRunnable(playRunnable);


    }


    @Override
    public void onPrepared(MediaPlayer mp) {

        // TODO Auto-generated method stub
        mediaplayerstate = prepared;
        fullfillintent();
        sendbufferingcompletebroadcast();
        playplayer();

    }

    @Override
    public void onCompletion(MediaPlayer arg0) {
        // TODO Auto-generated method stub
        mediaplayerstate = playbackcomplete;
        fullfillintent();
        sendcompletionbroadcast();
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {
        // TODO Auto-generated method stub
        if (what == MediaPlayer.MEDIA_INFO_BUFFERING_START) {
            LOGD(TAG, "service buffering");
            sendbufferingbroadcast();

        } else if (what == MediaPlayer.MEDIA_INFO_BUFFERING_END) {
            LOGD(TAG, "service buffering");
            sendbufferingcompletebroadcast();
            return true;
        }

        return false;
    }

    @Override
    public void onSeekComplete(MediaPlayer mp) {
        // TODO Auto-generated method stub

        if (!mp.isPlaying()) {
            playplayer();
        }

    }


    @Override
    public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        LOGD(TAG, "service: On error" + "\n" + arg1 + "\n" + arg2);
        onErrorState = PlayerErrorCodes.UNKNOWN;
        sendOnErrorStateBroadcast(onErrorState);
        return true;
    }


    public void seek(int posn) {
        if (player != null) {
            if (mediaplayerstate == prepared || mediaplayerstate == started || mediaplayerstate == paused || mediaplayerstate == playbackcomplete)

            {
                player.seekTo(posn);
                LOGD(TAG, "seek complete");

                mediaplayerstate = prepared;
                setseekintent = 0;
                fullfillintent();
            } else {
                setseekintent = posn;
                LOGD(TAG, "setseekintent" + setseekintent);
            }
        }
    }

    public void stopPlayer() {
        if (player != null) {
            if (mediaplayerstate == stopped || mediaplayerstate == prepared || mediaplayerstate == started || mediaplayerstate == paused || mediaplayerstate == playbackcomplete) {
                player.stop();
                mediaplayerstate = stopped;
                fullfillintent();
            } else {
                setstopintent = 1;
            }
        }
    }

    public void pausePlayer() {

        if (player != null) {
            if (mediaplayerstate == prepared || mediaplayerstate == started || mediaplayerstate == paused || mediaplayerstate == playbackcomplete)

            {
                player.pause();
                mediaplayerstate = paused;
                fullfillintent();
                handler.removeCallbacks(sendUpdatestoui);
                PlayerNotification.getInstance(this).updateNotification();

            } else {
                LOGD(TAG, "intent pause");
                LOGD(TAG, "mediaplayerstate==  " + mediaplayerstate);
                setplaypauseintent = 2;
            }

        }

    }

    public void killPlayer() {
//       stopPlayer();
        handler.removeCallbacks(sendUpdatestoui);
        stopForeground(true);
        sendbufferingcompletebroadcast();
       if (player!= null) player.release();

        isReleased=true;
        mediaplayerstate = end;

       stopSelf();
    }

    public void playplayer() {
        if (player != null) {

            if (mediaplayerstate == prepared || mediaplayerstate == started || mediaplayerstate == paused || mediaplayerstate == playbackcomplete) {
                player.start();
                mediaplayerstate = started;
                fullfillintent();
                setuphandler();
                PlayerNotification.getInstance(this).updateNotification();

            } else {
                LOGD(TAG, "intent play");
                LOGD(TAG, "mediaplayerstate==  " + mediaplayerstate);

                setplaypauseintent = 1;
            }


        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO Auto-generated method stub
        LOGD(TAG, "onbind");

        return musicBind;
    }

    @Override
    public void onCreate() {
        // TODO Auto-generated method stub

        worker = new WorkerThread();
        worker.start();
        worker.doRunnable(new Runnable() {
            @Override
            public void run() {
                LOGD(TAG, "oncreate");
                if (player == null) {
                    player = new MediaPlayer();
                    isReleased=false;

                    mediaplayerstate = idle;
                    fullfillintent();
                }

                initMusicPlayer();


                Intent_Musicplayer_seekIntent = new Intent(broadcast_playerseek);
                Intent_bufferplayerintent = new Intent(brodcast_bufferingplayer);
                Intent_completionplayerintent = new Intent(brodcast_playcomplete);
                Intent_error = new Intent(broadcastError);

                LocalBroadcastManager.getInstance(getBaseContext()).registerReceiver(broadcastReceiver_seekbaruiupdate, new IntentFilter(UIcontroller.brodcast_uiseek));
                isseekbarupdateuiregistered = true;


            }
        });


        super.onCreate();
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        LOGD(TAG, "ondestroy");


        if (isseekbarupdateuiregistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver_seekbaruiupdate);

            isseekbarupdateuiregistered = false;

        }

        super.onDestroy();
    }

    @Override
    public void onRebind(Intent intent) {
        // TODO Auto-generated method stub
        super.onRebind(intent);
        LOGD(TAG, "onrebind");

        setuphandler();
        if (!isseekbarupdateuiregistered) {
            LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver_seekbaruiupdate, new IntentFilter(UIcontroller.brodcast_uiseek));

            isseekbarupdateuiregistered = true;

        }

    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        LOGD(TAG, "onStartCommand");


        super.onStartCommand(intent, flags, startId);
        return START_NOT_STICKY;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        LOGD(TAG, "onunbind");
        handler.removeCallbacks(sendUpdatestoui);
        if (isseekbarupdateuiregistered) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastReceiver_seekbaruiupdate);

            isseekbarupdateuiregistered = false;

        }



        return true;
    }


    //buffering complete functions
    private void sendbufferingbroadcast() {
        // TODO Auto-generated method stub
        LOGD(TAG, "buffering broadcast start");
        isBufferingState = true;

        Intent_bufferplayerintent.putExtra("buffering", "1");
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent_bufferplayerintent);

    }

    private void sendOnErrorStateBroadcast(int error) {

        Intent_error.putExtra("Error", error);
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(Intent_error);
    }


    private void sendautoseekupdatebroadcast() {
        // TODO Auto-generated method stub
        mediapos = player.getCurrentPosition();
        mediamax = player.getDuration();
        if ((mediapos * 1.0) >= (mediamax * 0.6)) {
//            LOGD(TAG, "sendInAppPlayBroadcast");
            sendInAppPlay = true;
        } else {
//            LOGD(TAG, "dontsendInAppPlayBroadcast");

        }
        Intent_Musicplayer_seekIntent.putExtra("counter", String.valueOf(mediapos));
        Intent_Musicplayer_seekIntent.putExtra("mediamax", String.valueOf(mediamax));
        Intent_Musicplayer_seekIntent.putExtra("songended", String.valueOf(songended));
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent_Musicplayer_seekIntent);

    }

    private void sendInAppPlayBroadcast() {
        if (sendInAppPlay) {
            sendInAppPlay = false;

//            Toast.makeText(getApplicationContext(), "Inappbroadcastsent", Toast.LENGTH_LONG).show();
            Song song;
            long id;

            if (CurrentSong.isOffline() && !CurrentSong.isCached()) {
                Pair<Long, Song> appSongData = MusicSyncManager.getSong(CurrentSong.getSong(), this);
                if (appSongData == null){
                    LOGE(TAG,"user activity : song not found in database....skipped");
                    return;
                }
                song = appSongData.second;
                id = appSongData.first;
            } else {
                song = CurrentSong.getSong();
                id = CurrentSong.getId();
            }


            UserActivityManager.add(new UserActivity(song, null, id,
                    UserActivity.getInappPlayAction(CurrentSong.isOffline(),CurrentSong.isCached()),
                    System.currentTimeMillis()), this);


        }
    }



    private void sendbufferingcompletebroadcast() {
        // TODO Auto-generated method stub
        LOGD(TAG, "buffering broadcast complete");
        isBufferingState = false;

        Intent_bufferplayerintent.putExtra("buffering", "0");
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent_bufferplayerintent);

    }

    private void sendcompletionbroadcast() {
        // TODO Auto-generated method stub
        Intent_completionplayerintent.putExtra("complete", "1");
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent_completionplayerintent);
        LOGD(TAG, "song  complete broadcast");

    }

    public boolean isplaying() {
        // TODO Auto-generated method stub
        if (player != null) {
            try {
                return player.isPlaying();
            } catch (IllegalStateException e) {
                e.printStackTrace();
                return false;
            }
        }
        return false;

    }

    public boolean isBuffering() {
        LOGD(TAG,"isbuffering  : "+ isBufferingState);
        return isBufferingState;
    }

    public int getErrorState() {

        return onErrorState;
    }


    public Status getplayerstatus() {
        // TODO Auto-generated method stub
        Status playerstatus = null;
        if (player != null)

        {
            if (isplaying()) {
                LOGD(TAG, "isPlaying ################");
                playerstatus = new Status(CurrentSong, player.getCurrentPosition(), player.getDuration(), isplaying(), isBufferingState);
                LOGD(TAG, "@@@" + CurrentSong.getSong().getAlbum() + CurrentSong.getSong().getTitle() + "@@@");
            } else {
                LOGD(TAG, "isnotPlaying ################");
                if (CurrentSong != null) {
                    playerstatus = new Status(CurrentSong, 0, (int) CurrentSong.getSong().getDuration(), isplaying(), isBufferingState);
                    LOGD(TAG, "@@@" + CurrentSong.getSong().getAlbum() + CurrentSong.getSong().getTitle() + "@@@");
                }
            }
        }

        return playerstatus;
    }

    public void syncCurrentSong(Playable playable) {
        if (CurrentSong != null) {
            if (CurrentSong != playable)
                CurrentSong = playable;
        }
    }


    public void stophandler() {
        // TODO Auto-generated method stub
        if (handler != null)
            handler.removeCallbacks(sendUpdatestoui);

    }


    public void starthandler() {
        // TODO Auto-generated method stub
        if (handler != null)
            setuphandler();

    }

    private void fullfillintent() {

        switch (mediaplayerstate) {
//		  case idle:
//			
//			break;
//		  case initialized:
//			
//			break;
//		  case preparing:
//	
//			  break;
            case prepared:
                if (setseekintent != 0) {
                    seek(setseekintent);
                }
                if (setstopintent == 1) {
                    setstopintent = 0;
                    stopPlayer();
                }
                if (setplaypauseintent == 1) {
                    setplaypauseintent = 0;
                    playplayer();
                }

                break;
            case started:
                if (setseekintent != 0) {
                    seek(setseekintent);

                }
                if (setstopintent == 1) {
                    setstopintent = 0;
                    stopPlayer();
                }
                if (setplaypauseintent == 1) {
                    setplaypauseintent = 0;
                    playplayer();
                }
                if (setplaypauseintent == 2) {
                    setplaypauseintent = 0;
                    pausePlayer();
                }

                break;
            case stopped:

                break;
            case paused:
                if (setseekintent != 0) {
                    seek(setseekintent);
                }
                if (setstopintent == 1) {
                    setstopintent = 0;
                    stopPlayer();
                }
                if (setplaypauseintent == 1) {
                    setplaypauseintent = 0;
                    playplayer();
                }
                if (setplaypauseintent == 2) {
                    setplaypauseintent = 0;
                    pausePlayer();
                }

                break;
            case playbackcomplete:

                break;
            case end:

                break;
            case error:

                break;
        }
    }


}
