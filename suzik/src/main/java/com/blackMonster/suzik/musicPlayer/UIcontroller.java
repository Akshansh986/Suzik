package com.blackMonster.suzik.musicPlayer;

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
import android.support.v4.content.LocalBroadcastManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.blackMonster.suzik.musicstore.Timeline.Playable;
import com.blackMonster.suzik.sync.music.InAapSongTable;
import com.blackMonster.suzik.ui.FileDownloader;
import com.blackMonster.suzik.ui.Playlist;
import com.blackMonster.suzik.ui.UiBroadcasts;

import java.util.Random;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGE;

public class UIcontroller {
    private static final String TAG = "Suzikplayer_uicontroller";

    Handler mHandler = new Handler();

    Runnable mHandlerTask = new Runnable() {
        @Override
        public void run() {
            LOGD(TAG, "Runnable setsong");

            Playable playable = getPlayableAndRecollectSongIfNecessary();


            musicSrv.setSong(playable);

        }

        private Playable getPlayableAndRecollectSongIfNecessary() {

            Playable playable = songs.getPlayable(songpos);
            Playable resultPlayable;

            if (playable.isCached()) {

                if (FileDownloader.doesFileExist(playable.getSongPath()))
                    resultPlayable = playable;
                else {

                    Playable alternatePlayable = playable.getAlternatePlayable();
                    if (alternatePlayable != null) {
                        resultPlayable = alternatePlayable;
                        ResourceRecollector.saveSong(playable.getId(),resultPlayable.getSong(),resultPlayable.getSongPath(), context);
                    } else resultPlayable = playable;
                }
            } else
                resultPlayable = playable;


            return resultPlayable;


        }
    };


    Playlist songs = null;
    boolean playbtn;
    int songpos = 0;
    int repeat = 0;
    boolean shuffle = false;
    Status savedStatus;
    boolean isphonestatechange = false;
    boolean isheadphonestatechange = false;
    private boolean isfocuslost;


    private PhoneStateListener phonestatelistener;
    private TelephonyManager telephonyManager;
    AudioManager am;
    OnAudioFocusChangeListener afChangeListener;
    public static String brodcast_uidataupdate = "uidataupdate";
    Intent Intent_uidataupdate;
    public static String brodcast_uibtnupdate = "uibtnupdate";
    Intent Intent_uibtnupdate;

    public static String brodcast_resetui = "resetui";
    Intent Intent_resetui;

    public static String brodcast_playercurrentstatus = "playercurrentstatus";
    Intent Intent_playercurrentstatus;
    public static String brodcast_playersavedstatus = "playersavedstatus";
    Intent Intent_playersavedstatus;

    public static final String brodcast_uiseek = "uiseek";
    Intent intent_uiseekintent;


    private MusicPlayerService musicSrv;
    private boolean musicBound = false;
    private Intent playIntent = null;


    Context context;
    private static UIcontroller instance = null;


    public static UIcontroller getInstance(Context context)

    {
        LOGD(TAG, "getInstance");

        if (instance == null) {
            instance = new UIcontroller(context.getApplicationContext());
        }

        return instance;

    }

    private UIcontroller(Context context) {
        // TODO Auto-generated constructor stub

        LOGD(TAG, "UIcontroller constuctor");
        this.context = context;

        Intent_uidataupdate = new Intent(brodcast_uidataupdate);
        Intent_uibtnupdate = new Intent(brodcast_uibtnupdate);

        intent_uiseekintent = new Intent(brodcast_uiseek);
        Intent_playercurrentstatus = new Intent(brodcast_playercurrentstatus);
        Intent_playersavedstatus = new Intent(brodcast_playersavedstatus);
        Intent_resetui = new Intent(brodcast_resetui);

        LocalBroadcastManager.getInstance(context).registerReceiver(broadcastreciever_songcomplete, new IntentFilter(MusicPlayerService.brodcast_playcomplete));
        context.registerReceiver(Broadcastreciever_headsetreciever, new IntentFilter(Intent.ACTION_HEADSET_PLUG));

        setaudiodocus();

    }


    public void bindtoservice() {
        LOGD(TAG, "bindtoservice");

        if (playIntent == null) {
            LOGD(TAG, "play intent == null");

            playIntent = new Intent(context, MusicPlayerService.class);
            boolean bool = context.bindService(playIntent, musicConnection, context.BIND_AUTO_CREATE);
            ComponentName bool1 = context.startService(playIntent);
            // LOGD(TAG,"bindtoservice  "+bool+"  "+bool1+"#########");
        } else {
            LOGD(TAG, "play intent != null");
            context.bindService(playIntent, musicConnection, context.BIND_AUTO_CREATE);


        }


    }


    private ServiceConnection musicConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            LOGD(TAG, "service connected ");

            MusicPlayerService.MusicBinder binder = (MusicPlayerService.MusicBinder) service;
            //get service
            musicSrv = binder.getService();
            //pass list


            //LOGD("Suzikplayer","returned waiting to play");

            musicBound = true;


        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            LOGD(TAG, "service disconnected ");

            musicBound = false;
        }
    };

    private BroadcastReceiver Broadcastreciever_headsetreciever = new BroadcastReceiver() {
        @Override
        public void onReceive(Context arg0, Intent intent) {
            // TODO Auto-generated method stub
            LOGD(TAG, "Broadcastreciever_headsetreciever :onReceive ");
            int state = intent.getIntExtra("state", 100);
            if (state != 100) {
                switch (state) {
                    case 1:
                        LOGD(TAG, "headphone connected ");

                        break;

                    case 0:
                        LOGD(TAG, "headphone disconnected ");
                        if (isplaying()) {
                            pauseSong();
                            senduibtnsetbroadcast();

                        }
                        break;
                }
            }

        }
    };


    private BroadcastReceiver broadcastreciever_songcomplete = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            LOGD(TAG, "broadcastreciever_songcomplete :onReceive ");


            if (repeat == 1) {
                //seek(0);
                setSong();
            } else {
                if (shuffle) {
                    shuffleSong();
                }
                nextSong();

            }


        }


    };


    private void senduidatasetbroadcast() {
        // TODO Auto-generated method stub

        Playable playable = songs.getPlayable(songpos);


        String albumartPath;
        if (playable.isCached()) {

            if (FileDownloader.doesFileExist(playable.getAlbumArtPath()))
                albumartPath = playable.getAlbumArtPath();
            else {

                Playable alternatePlayable = playable.getAlternatePlayable();
                if (alternatePlayable != null) {
                    albumartPath = alternatePlayable.getAlbumArtPath();
                    ResourceRecollector.saveAlbumart(playable.getId(), albumartPath, context);
                } else albumartPath = playable.getAlbumArtPath();
            }
        } else
            albumartPath = playable.getAlbumArtPath();

        LOGD(TAG, "albumart path " + albumartPath);

        Intent_uidataupdate.putExtra("songTitleLabel", playable.getSong().getTitle());
        Intent_uidataupdate.putExtra("songAlbumName", playable.getSong().getAlbum());
        Intent_uidataupdate.putExtra("songArtistName", playable.getSong().getArtist());
        Intent_uidataupdate.putExtra("albumart", albumartPath);
        Intent_uidataupdate.putExtra("songduration", playable.getSong().getDuration());


        LocalBroadcastManager.getInstance(context).sendBroadcast(Intent_uidataupdate);
        LOGD(TAG, "uidataupdatebroadcast\n" + Intent_uidataupdate.toString());
    }


    private void senduibtnsetbroadcast() {
        // TODO Auto-generated method stub


        Intent_uibtnupdate.putExtra("isplaying", isplaying());
        Intent_uibtnupdate.putExtra("repeat", repeat);
        Intent_uibtnupdate.putExtra("shuffle", shuffle);


        LocalBroadcastManager.getInstance(context).sendBroadcast(Intent_uibtnupdate);
        LOGD(TAG, "uibtnupdatebroadcast\n" + Intent_uibtnupdate.toString());
    }


    private void resetui() {
        // TODO Auto-generated method stub
        LocalBroadcastManager.getInstance(context).sendBroadcast(Intent_resetui);
        LOGD(TAG, "resetuibroadcast");


    }

    private void setaudiodocus() {
        // TODO Auto-generated method stub
        LOGD(TAG, "setaudiodocus");

        am = (AudioManager) context.getSystemService(context.AUDIO_SERVICE);

        afChangeListener = new OnAudioFocusChangeListener() {

            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                    LOGD(TAG, "AUDIOFOCUS_LOSS_TRANSIENT");
                    isfocuslost = true;
                    if (isplaying()) {
                        {
                            isphonestatechange = true;
                            pauseSong();
                            senduibtnsetbroadcast();

                        }
                    }
                    else{
                        if(isBuffering()){
                            isphonestatechange = true;
                            pauseSong();
                            senduibtnsetbroadcast();
                        }
                    }


                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    LOGD(TAG, "AUDIOFOCUS_GAIN");
                    isfocuslost = false;
                    if (isphonestatechange) {
                        isphonestatechange = false;
                        playSong();
                        senduibtnsetbroadcast();

                    }

                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    LOGD(TAG, "AUDIOFOCUS_LOSS");
                    isfocuslost = true;
                    if (isplaying()) {
                        {
                            isphonestatechange = true;
                            pauseSong();
                            senduibtnsetbroadcast();

                        }
                    }
                    else{
                        if(isBuffering()){
                            isphonestatechange = true;
                            pauseSong();
                            senduibtnsetbroadcast();
                        }
                    }


                    am.abandonAudioFocus(afChangeListener);

                }
            }
        };
        // Request audio focus for playback
        isfocuslost =true;
       // requestfocus();


    }

    private void requestfocus() {
        // TODO Auto-generated method stub
        LOGD(TAG, "requestfocus");

        int result = am.requestAudioFocus(afChangeListener,
                // Use the music stream.
                AudioManager.STREAM_MUSIC,
                // Request permanent focus.
                AudioManager.AUDIOFOCUS_GAIN);
        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            LOGD(TAG, "Audio focus gained");
            isfocuslost = false;
        }

    }


    public void setList(Playlist playlist) {
        // TODO Auto-generated method stub
        LOGD(TAG, "setlist");
        songs = playlist;

    }

    public Playlist getList() {
        // TODO Auto-generated method stub
        LOGD(TAG, "getlist");
        return songs;
    }

    public void setSongpos(int songIndex) {
        LOGD(TAG, "setsongpos");
        songpos = songIndex;
        setSong();
    }

    private void setSong() {
        // TODO Auto-generated method stub
        LOGD(TAG, "setsong");
        resetui();

     //   musicSrv.syncCurrentSong(songs.getPlayable(songpos));
        senduidatasetbroadcast();
        if (isplaying()) {
            pauseSong();
        }
        if (isfocuslost) {
            requestfocus();

        }
        //delay100ms
        if (musicSrv != null) {
            mHandler.removeCallbacks(mHandlerTask);
            mHandler.postDelayed(mHandlerTask, 500);
        }

    }

    public void playSong() {
        // TODO Auto-generated method stub
        LOGD(TAG, "play");
        if (isfocuslost) {
            requestfocus();

        }
        musicSrv.playplayer();
    }

    public void pauseSong() {
        // TODO Auto-generated method stub
        LOGD(TAG, "pause");

        musicSrv.pausePlayer();
    }

    public void nextSong() {
        // TODO Auto-generated method stub
        LOGD(TAG, "next song");
        if (songs != null) {


            if (shuffle) {
                shuffleSong();
            } else {
                if (songpos == songs.getSongCount() - 1) {
                    songpos = 0;
                } else {
                    songpos++;
                }
                setSong();


            }


        }
    }

    public void prevSong() {
        // TODO Auto-generated method stub
        LOGD(TAG, "prevsong");
        if (songs != null) {


            if (shuffle) {
                shuffleSong();
            } else {
                if (songpos == 0) {
                    songpos = songs.getSongCount() - 1;
                } else {
                    songpos--;
                }

                setSong();


            }


        }
    }

    public void stopSong() {
        // TODO Auto-generated method stub
        LOGD(TAG, "stopSong");

        musicSrv.stopPlayer();

    }

    public void shuffleSong() {
        // TODO Auto-generated method stub
        LOGD(TAG, "shuffleSong");

        Random r = new Random();
        songpos = r.nextInt(songs.getSongCount());
        setSong();
    }

    public void setrepeatStatus(int value) {
        // TODO Auto-generated method stub
        LOGD(TAG, "setrepeatStatus");

        repeat = value;
    }

    public void setshuffleStatus(boolean value) {
        // TODO Auto-generated method stub
        LOGD(TAG, "setshuffleStatus");

        shuffle = value;
    }

    public void seek(int progress) {
        // TODO Auto-generated method stub
        LOGD(TAG, "seek");

        if (musicSrv != null) {
            musicSrv.seek(progress);
        }
        /*intent_uiseekintent.putExtra("seekui",progress);
        context.sendBroadcast(intent_uiseekintent);
		*/

    }

    public void unbind() {
        // TODO Auto-generated method stub
        LOGD(TAG, "unbind");

        if (musicConnection != null) {
            {if(musicBound) {
                context.unbindService(musicConnection);
                musicBound=false;
            }
            }
        }
    }

    public boolean isplaying() {
        // TODO Auto-generated method stub
        LOGD(TAG, "isplaying");

        if (musicSrv != null) {
            return musicSrv.isplaying();
        }
        return false;
    }
    public boolean isBuffering() {
        if (musicSrv != null) {
            return musicSrv.isBuffering();
        }
        return false;
    }
    public int getErrorState(){
        if(musicSrv!=null){
            return musicSrv.getErrorState();
        }
        return PlayerErrorCodes.DEFAULT;
    }

    public void loadcurrentplayerstatus() {
        // TODO Auto-generated method stub
        LOGD(TAG, "loadcurrentplayerstatus");

        if (musicSrv != null) {
            if (songs != null)

            {
                Status s = musicSrv.getplayerstatus();
                if (s != null) {
                    Intent_playercurrentstatus.putExtra("songTitleLabel",s.playable.getSong().getTitle());
                    Intent_playercurrentstatus.putExtra("songAlbumName",s.playable.getSong().getAlbum());
                    Intent_playercurrentstatus.putExtra("songArtistName",s.playable.getSong().getArtist());
                    Intent_playercurrentstatus.putExtra("albumart",s.playable.getAlbumArtPath());
                    Intent_playercurrentstatus.putExtra("isplaying",s.isPlaying());
                    Intent_playercurrentstatus.putExtra("currentpos",s.getCurrentPosition());
                    Intent_playercurrentstatus.putExtra("duration",s.getDuration());
                    Intent_playercurrentstatus.putExtra("isbuffering",s.isBuffering());
                    Intent_playercurrentstatus.putExtra("shuffle",shuffle);
                    Intent_playercurrentstatus.putExtra("repeat",repeat);



                    LocalBroadcastManager.getInstance(context).sendBroadcast(Intent_playercurrentstatus);
                    LOGD(TAG, "playercurrentstatus\n" + Intent_playercurrentstatus.toString());

                }
            }
        }


    }

    public void loadsavedplayerstatus() {
        // TODO Auto-generated method stub
        LOGD(TAG, "loadsavedplayerstatus");

        if (songs == null) {
            Intent_playersavedstatus.putExtra("songTitleLabel", songs.getPlayable(songpos).getSong().getTitle());
            Intent_playersavedstatus.putExtra("songAlbumName", songs.getPlayable(songpos).getSong().getAlbum());
            Intent_playersavedstatus.putExtra("songArtistName", songs.getPlayable(songpos).getSong().getArtist());
            Intent_playersavedstatus.putExtra("albumart", songs.getPlayable(songpos).getAlbumArtPath());
            Intent_playersavedstatus.putExtra("isplaying", savedStatus.isPlaying());
            Intent_playersavedstatus.putExtra("currentpos", savedStatus.getCurrentPosition());
            Intent_playersavedstatus.putExtra("duration", savedStatus.getDuration());
            Intent_playersavedstatus.putExtra("shuffle", shuffle);
            Intent_playersavedstatus.putExtra("repeat", repeat);

        } else


            Intent_playersavedstatus.putExtra("songTitleLabel", songs.getPlayable(songpos).getSong().getTitle());
        Intent_playersavedstatus.putExtra("songAlbumName", songs.getPlayable(songpos).getSong().getAlbum());
        Intent_playersavedstatus.putExtra("songArtistName", songs.getPlayable(songpos).getSong().getArtist());
        Intent_playersavedstatus.putExtra("albumart", songs.getPlayable(songpos).getAlbumArtPath());
        Intent_playersavedstatus.putExtra("isplaying", savedStatus.isPlaying());
        Intent_playersavedstatus.putExtra("currentpos", savedStatus.getCurrentPosition());
        Intent_playersavedstatus.putExtra("duration", savedStatus.getDuration());
        Intent_playersavedstatus.putExtra("shuffle", shuffle);
        Intent_playersavedstatus.putExtra("repeat", repeat);


        LocalBroadcastManager.getInstance(context).sendBroadcast(Intent_playersavedstatus);
        LOGD(TAG, "playersavedstatus\n" + Intent_playersavedstatus.toString());


    }

    public void stophandler() {
        // TODO Auto-generated method stub
        LOGD(TAG, "stophandler");

        if (musicSrv != null) {
            musicSrv.stophandler();
        }

    }

    public void starthandler() {
        // TODO Auto-generated method stub
        LOGD(TAG, "starthandler");

        if (musicSrv != null) {
            musicSrv.starthandler();
        }

    }


    public void onError(Playable playable) {


        startDownload(playable.getId(), playable.getAlternatePlayable());

    }

    private void startDownload(long id, Playable playable) {
        String songFileName = FileDownloader.getNewSongFileName();
        String songLocation = FileDownloader.getLocationFromFilename(songFileName, context);

        FileDownloader.saveSongToDisk(playable.getSong().getTitle(), playable.getSong().getArtist(),
                playable.getSongPath(), songFileName, context);

        if (InAapSongTable.updateSongLocation(id, songLocation, context))
            LOGD(TAG, "Successfuly updated table " + playable.toString());
        else
            LOGE(TAG, "Failed table update " + playable.toString());

        UiBroadcasts.broadcastMusicDataChanged(context);


    }

    public boolean isSongPlaying(Playlist playlist,int pos){
        return songpos==pos&&songs==playlist;
    }


}
