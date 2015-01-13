package com.blackMonster.suzik.musicPlayer;

import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.R;



public class MainActivity extends Activity implements OnSeekBarChangeListener {
    private static final String TAG = "Suzikplayer_ui";

    public static final String brodcast_uiseek = "uiseek";
    Intent intent_uiseekintent;
    private ImageView btnPlay;
    private ImageView btnNext;
    private ImageView btnPrevious;
    private ImageView btnRepeat;
    private ImageView btnShuffle;
    private TextView songTitleLabel;
    private TextView songAlbumName;
    private TextView songArtistName;
    private ImageView albumart;
    private SeekBar songProgressBar;

    UIcontroller uicontroller;

    private int seekmax;
    private static int songended = 0;

    private boolean isbplayercurrentstatus;
    private boolean isbrodcastseekregistered;
    private boolean isbrodcastuidataupdateregistered;
    private boolean isbrodcastuibtnupdateregistered;
    private boolean isbuffering;
    private boolean isbufferregistered;
    private boolean isrestui;

    Animation fadeIn;
    Animation fadeOut;
    AnimationSet animation;

    //broadcast recievers
    private BroadcastReceiver broadcastreciever_playercurrentstatus = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Log.d(TAG, "broadcastreciever_playercurrentstatus:onReceive");

            setcurrentstatus(intent);


        }

    };

    private void setcurrentstatus(Intent intent) {
        // TODO Auto-generated method stub
        Log.d(TAG, "broadcastreciever_playercurrentstatus:setcurrentstatus");

        String SongTitleLabel = intent.getStringExtra("songTitleLabel");
        String SongAlbumName = intent.getStringExtra("songAlbumName");
        String SongArtistName = intent.getStringExtra("songArtistName");
        String Albumart = intent.getStringExtra("albumart");
        Boolean isplaying = intent.getBooleanExtra("isplaying", false);
        int currentpos = intent.getIntExtra("currentpos", 100);
        int duration = intent.getIntExtra("duration", 100);
        Boolean shuffle = intent.getBooleanExtra("shuffle", false);
        int repeat = intent.getIntExtra("repeat", 100);
        Log.d(TAG, SongTitleLabel + SongArtistName + SongArtistName + Albumart);

        songTitleLabel.setText(SongTitleLabel);
        songAlbumName.setText(SongAlbumName);
        songArtistName.setText(SongArtistName);
        albumart.setImageResource(R.drawable.album_art);
        songProgressBar.setMax(duration);
        songProgressBar.setProgress(currentpos);

        if (isplaying) {
            btnPlay.setImageResource(R.drawable.pause);

            btnPlay.setTag("pause");

        } else {
            btnPlay.setImageResource(R.drawable.play);
            btnPlay.setTag("play");

        }
        if (shuffle) {
            btnShuffle.setImageResource(R.drawable.shuffleon);


        } else {
            btnShuffle.setImageResource(R.drawable.shuffle);


        }
        if (repeat == 0) {
            btnRepeat.setImageResource(R.drawable.repeat);

        } else {
            if (repeat == 1) {
                btnRepeat.setImageResource(R.drawable.repeat1);

            } else if (repeat == 2) {
                btnRepeat.setImageResource(R.drawable.repeatall);

            }
        }


    }

    private BroadcastReceiver broadcastreciever_seekrecieve = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            //	Log.d(TAG,"broadcastreciever_seekrecieve:onReceive");

            autoupdateuiseekbar(intent);


        }
    };

    private void autoupdateuiseekbar(Intent intent) {
        // TODO Auto-generated method stub
        //	Log.d(TAG,"broadcastreciever_seekrecieve:autoupdateuiseekbar");

        String counter = intent.getStringExtra("counter");
        String mediamax = intent.getStringExtra("mediamax");
        String strsongended = intent.getStringExtra("songended");
        int seekprogress = Integer.parseInt(counter);
        Log.d(TAG, "Current Postiton:" + counter + "Max Duration" + mediamax);

        seekmax = Integer.parseInt(mediamax);
        songended = Integer.parseInt(strsongended);
        songProgressBar.setMax(seekmax);
        songProgressBar.setProgress(seekprogress);


    }

    private BroadcastReceiver broadcastreciever_uibtnupdate = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            Log.d(TAG, "broadcastreciever_uibtnupdate:onReceive");

            Boolean isplaying = intent.getBooleanExtra("isplaying", false);
            Boolean shuffle = intent.getBooleanExtra("shuffle", false);
            int repeat = intent.getIntExtra("repeat", 100);
            if (isplaying) {
                btnPlay.setImageResource(R.drawable.pause);
                btnPlay.setTag("pause");

            } else {
                btnPlay.setImageResource(R.drawable.play);
                btnPlay.setTag("play");


            }
            if (shuffle) {
                btnShuffle.setImageResource(R.drawable.shuffleon);


            } else {
                btnShuffle.setImageResource(R.drawable.shuffle);


            }
            if (repeat == 0) {
                btnRepeat.setImageResource(R.drawable.repeat);

            } else {
                if (repeat == 1) {
                    btnRepeat.setImageResource(R.drawable.repeat1);

                } else if (repeat == 2) {
                    btnRepeat.setImageResource(R.drawable.repeatall);

                }
            }


        }
    };
    private BroadcastReceiver broadcastreciever_uidataupdate = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "broadcastreciever_uidataupdate:onReceive");
            uidataupdate(intent);


        }
    };

    private void uidataupdate(Intent intent) {
        // TODO Auto-generated method stub
        Log.d(TAG, "broadcastreciever_uidataupdate:uidataupdate");
        String SongTitleLabel = intent.getStringExtra("songTitleLabel");
        String SongAlbumName = intent.getStringExtra("songAlbumName");
        String SongArtistName = intent.getStringExtra("songArtistName");
        String Albumart = intent.getStringExtra("albumart");
        int duration = (int) intent.getLongExtra("songduration", 0);

        Log.d(TAG, SongTitleLabel + SongArtistName + SongArtistName + Albumart + duration);

        songTitleLabel.setText(SongTitleLabel);
        songAlbumName.setText(SongAlbumName);
        songArtistName.setText(SongArtistName);
        albumart.setImageResource(R.drawable.album_art);
        songProgressBar.setMax(duration);


    }

    private BroadcastReceiver broadcastreciever_bufferingplayerrecieve = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "broadcastreciever_bufferingplayerrecieve:onReceive");
            showpd(intent);
        }
    };


    private void showpd(Intent bufferintent) {
        Log.d(TAG, "broadcastreciever_bufferingplayerrecieve:showpd");

        String buffval = bufferintent.getStringExtra("buffering");
        int bval = Integer.parseInt(buffval);
        switch (bval) {
            case 1:
                isbuffering = true;
                Log.d(TAG, "Animationstarted");
                songProgressBar.startAnimation(animation);

                break;
            case 0:
                isbuffering = false;
                Log.d(TAG, "Animationstopeed");
                songProgressBar.startAnimation(animation);
                break;


        }
    }

    private BroadcastReceiver broadcastreciever_resetui = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "broadcastreciever_resetui:onReceive");

            songTitleLabel.setText("");
            songAlbumName.setText("");
            songArtistName.setText("");
            albumart.setImageResource(R.drawable.album_art);
            songProgressBar.setProgress(0);
            btnPlay.setTag("pause");
            btnPlay.setImageResource(R.drawable.pause);

        }
    };


/////////////////////////////////////////////

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suzikplayer);
        setupui();
        setanimation();

        uicontroller = UIcontroller.getInstance(getApplicationContext());
        uicontroller.bindtoservice();


        Log.d(TAG, "oncreate:Register broadcast");
        registerReceiver(broadcastreciever_seekrecieve, new IntentFilter(MusicPlayerService.broadcast_playerseek));
        isbrodcastseekregistered = true;
        registerReceiver(broadcastreciever_uidataupdate, new IntentFilter(UIcontroller.brodcast_uidataupdate));
        isbrodcastuidataupdateregistered = true;
        registerReceiver(broadcastreciever_uibtnupdate, new IntentFilter(UIcontroller.brodcast_uibtnupdate));
        isbrodcastuibtnupdateregistered = true;
        registerReceiver(broadcastreciever_bufferingplayerrecieve, new IntentFilter(MusicPlayerService.brodcast_bufferingplayer));
        isbufferregistered = true;
        registerReceiver(broadcastreciever_playercurrentstatus, new IntentFilter(UIcontroller.brodcast_playercurrentstatus));
        isbplayercurrentstatus = true;
        registerReceiver(broadcastreciever_resetui, new IntentFilter(UIcontroller.brodcast_resetui));
        isrestui = true;

    }


    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onPause");
        if(uicontroller.getList()!=null) {
            if (isrestui) {
                unregisterReceiver(broadcastreciever_resetui);
                isrestui = false;

            }

            if (isbplayercurrentstatus) {
                unregisterReceiver(broadcastreciever_playercurrentstatus);
                isbplayercurrentstatus = false;

            }
            if (isbrodcastuidataupdateregistered) {
                unregisterReceiver(broadcastreciever_uidataupdate);
                isbrodcastuidataupdateregistered = false;

            }
            if (isbrodcastuibtnupdateregistered) {
                unregisterReceiver(broadcastreciever_uibtnupdate);
                isbrodcastuibtnupdateregistered = false;

            }
            if (isbufferregistered) {
                unregisterReceiver(broadcastreciever_bufferingplayerrecieve);
                isbufferregistered = false;

            }

            if (isbrodcastseekregistered) {
                unregisterReceiver(broadcastreciever_seekrecieve);
                isbrodcastseekregistered = false;

            }
            Log.d(TAG, "onPause:unregister broadcast");

            if (uicontroller != null) {

                Log.d(TAG, "onPause:stophandler");

                uicontroller.stophandler();
            }
        }
        super.onPause();
    }


    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onResume");
    if(uicontroller.getList()!=null) {
    if (!isrestui) {
        registerReceiver(broadcastreciever_resetui, new IntentFilter(UIcontroller.brodcast_resetui));
        isrestui = true;

    }
    if (!isbplayercurrentstatus) {
        registerReceiver(broadcastreciever_playercurrentstatus, new IntentFilter(UIcontroller.brodcast_playercurrentstatus));
        isbplayercurrentstatus = true;

    }
    if (!isbrodcastuidataupdateregistered) {
        registerReceiver(broadcastreciever_uidataupdate, new IntentFilter(UIcontroller.brodcast_uidataupdate));
        isbrodcastuidataupdateregistered = true;

    }
    if (!isbrodcastuibtnupdateregistered) {
        registerReceiver(broadcastreciever_uibtnupdate, new IntentFilter(UIcontroller.brodcast_uibtnupdate));
        isbrodcastuibtnupdateregistered = true;

    }
    if (!isbufferregistered) {
        registerReceiver(broadcastreciever_bufferingplayerrecieve, new IntentFilter(MusicPlayerService.brodcast_bufferingplayer));
        isbufferregistered = true;

    }
    if (!isbrodcastseekregistered) {
        registerReceiver(broadcastreciever_seekrecieve, new IntentFilter(MusicPlayerService.broadcast_playerseek));
        isbrodcastseekregistered = true;

    }
    Log.d(TAG, "onResume:registerbroadcast");

    if (uicontroller != null) {
        if (uicontroller.isplaying()) {
            Log.d(TAG, "loadcurrentplayerstatus");

            uicontroller.loadcurrentplayerstatus();
        } else {
            Log.d(TAG, "loadcurrentplayerstatus");
            uicontroller.loadcurrentplayerstatus();

            //uicontroller.loadsavedplayerstatus();
        }

        Log.d(TAG, "onResume:starthandler");

        uicontroller.starthandler();

    }

}
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onDestroy");
        if (uicontroller != null) {
            uicontroller.unbind();
        }
        super.onDestroy();
    }


    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        seekBar.setProgress(progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        uicontroller.seek(seekBar.getProgress());
    }


    private void setupui() {
        Log.d(TAG, "setupui");

        btnPlay = (ImageView) findViewById(R.id.playpause);
        btnNext = (ImageView) findViewById(R.id.next);
        btnPrevious = (ImageView) findViewById(R.id.previous);
        btnRepeat = (ImageView) findViewById(R.id.repeat);
        btnShuffle = (ImageView) findViewById(R.id.shuffle);
        songProgressBar = (SeekBar) findViewById(R.id.seekbar);
        songTitleLabel = (TextView) findViewById(R.id.songname);
        songAlbumName = (TextView) findViewById(R.id.albumname);
        songArtistName = (TextView) findViewById(R.id.artistname);
        albumart = (ImageView) findViewById(R.id.albumart);

        //settags
        btnPlay.setTag("play");
        btnShuffle.setTag("shuffleoff");
        btnRepeat.setTag("0");
        setuplistener();
    }

    private void setuplistener() {

        Log.d(TAG, "setuplisteners");

        btnPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick:btnPlay/Pause");
                ImageView t = (ImageView) v;
                if (uicontroller.getList() != null) {

                    if (t.getTag() == "play") {            //		Log.d(TAG,"playtopause");

                        t.setTag("pause");
                        t.setImageResource(R.drawable.pause);
                        uicontroller.playSong();

                    } else if (t.getTag() == "pause") {                //	Log.d(TAG,"pausetoplay");
                        t.setTag("play");
                        t.setImageResource(R.drawable.play);

                        uicontroller.pauseSong();

                    }

                } else {

                    Toast.makeText(getBaseContext(), "List not set", Toast.LENGTH_SHORT).show();


                }
            }
        });

        btnNext.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick:btnNext");

                ImageView t = (ImageView) v;
                if (uicontroller.getList() != null) {
                    uicontroller.nextSong();
                } else {
                    Toast.makeText(getBaseContext(), "List not set", Toast.LENGTH_SHORT).show();

                }
            }
        });
        btnPrevious.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick:btnPrevious");

                ImageView t = (ImageView) v;
                if (uicontroller.getList() != null) {

                    uicontroller.prevSong();

                } else {
                    Toast.makeText(getBaseContext(), "List not set", Toast.LENGTH_SHORT).show();

                }
            }
        });
        btnShuffle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick:btnShuffle");

                // TODO Auto-generated method stub
                ImageView t = (ImageView) v;
                if (uicontroller.getList() != null) {
                    if (t.getTag() == "shuffleoff") {                    //Log.d(TAG,"playtopause");

                        t.setTag("shuffleon");
                        t.setImageResource(R.drawable.shuffleon);
                        uicontroller.setshuffleStatus();
                    } else if (t.getTag() == "shuffleon") {                //	Log.d(TAG,"pausetoplay");
                        t.setTag("shuffleoff");
                        t.setImageResource(R.drawable.shuffle);
                        uicontroller.setshuffleStatus();

                    }
                } else {
                    Toast.makeText(getBaseContext(), "List not set", Toast.LENGTH_SHORT).show();


                }

                //	uicontroller.setshuffleStatus();

            }
        });
        btnRepeat.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick:btnShuffle");

                // TODO Auto-generated method stub
                ImageView t = (ImageView) v;
                if (uicontroller.getList() != null) {
                    if (t.getTag() == "0") {                    //Log.d(TAG,"playtopause");

                        t.setTag("1");
                        t.setImageResource(R.drawable.repeat1);
                        uicontroller.setrepeatStatus();
                    } else {
                        if (t.getTag() == "1") {                //	Log.d(TAG,"pausetoplay");
                            t.setTag("2");
                            t.setImageResource(R.drawable.repeatall);
                            uicontroller.setrepeatStatus();

                        } else {
                            if (t.getTag() == "2") {                //	Log.d(TAG,"pausetoplay");
                                t.setTag("0");
                                t.setImageResource(R.drawable.repeat);
                                uicontroller.setrepeatStatus();

                            }

                        }
                    }

                } else {
                    Toast.makeText(getBaseContext(), "List not set", Toast.LENGTH_SHORT).show();


                }
                //uicontroller.setrepeatStatus();

            }
        });
        songProgressBar.setOnSeekBarChangeListener(this);

        // TODO Auto-generated method stub

    }

    private void setanimation() {
        Log.d(TAG, "setanimation");

        fadeIn = new AlphaAnimation((float) 0.2, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);

        fadeOut = new AlphaAnimation(1, (float) 0.2);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(500);
        fadeOut.setDuration(500);
        fadeOut.setFillAfter(true);

        animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeOut);
        animation.addAnimation(fadeIn);

        animation.setAnimationListener(new AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onAnimationStart");
                if (!isbuffering) {
                    songProgressBar.clearAnimation();


                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onAnimationStart");

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onAnimationEnd");
                if (isbuffering) {
                    songProgressBar.startAnimation(animation);
                }

            }
        });

    }
}
