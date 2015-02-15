package com.blackMonster.suzik.musicPlayer;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.blackMonster.suzik.R;
import com.blackMonster.suzik.musicstore.Timeline.Playable;
import com.blackMonster.suzik.ui.FileDownloader;
import com.blackMonster.suzik.ui.LazyImageLoader;
import com.blackMonster.suzik.util.Utils;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

/**
 * Created by akshanshsingh on 14/02/15.
 */
public class PlayerNotification {
    //Notification elements.
    public static final int mNotificationId = 5620; //NOTE: Using 0 as a notification ID causes Android to ignore the notification call.

    //Custom actions for media player controls via the notification bar.
    public static final String LAUNCH_NOW_PLAYING_ACTION = "com.suzik.musicPlayer.LAUNCH_NOW_PLAYING_ACTION";
    public static final String PREVIOUS_ACTION = "com.suzik.musicPlayer.PREVIOUS_ACTION";
    public static final String PLAY_PAUSE_ACTION = "com.suzik.musicPlayer.PLAY_PAUSE_ACTION";
    public static final String NEXT_ACTION = "com.suzik.musicPlayer.NEXT_ACTION";
    public static final String STOP_SERVICE = "com.suzik.musicPlayer.STOP_SERVICE";
    private static final String TAG = "PlayerNotification";
    Context context;
//    UIcontroller uIcontroller;

    //    public  PlayerNotification(Context mContext) {
//        this.mContext = mContext;
//        uIcontroller = UIcontroller.getInstance(this.mContext);
//    }
    private static PlayerNotification instance = null;

    public static synchronized PlayerNotification getInstance(Context context)

    {
        LOGD(TAG, "getInstance");
        if (instance == null) {
            instance = new PlayerNotification(context);
        }
        return instance;

    }

    private PlayerNotification(Context mContext) {
        this.context = mContext;
    }



    public synchronized Notification buildNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            return buildJBNotification(context);
        else
            return buildICSNotification(context);
    }

    public synchronized void updateNotification() {
        Notification notification = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN)
            notification = buildJBNotification(context);
        else
            notification = buildICSNotification(context);

        //Update the current notification.
        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(mNotificationId, notification);
    }


    NotificationCompat.Builder mNotificationBuilder;
    RemoteViews expNotificationView;
    RemoteViews notificationView;
    Playable currentNotificationSong;

    @SuppressLint("NewApi")
    private android.app.Notification buildJBNotification(Context mContext) {
        UIcontroller uIcontroller = UIcontroller.getInstance(mContext);
        Playable currentSong = uIcontroller.getCurrentSong();
        mNotificationBuilder = new NotificationCompat.Builder(mContext);
        mNotificationBuilder.setOngoing(true);
        mNotificationBuilder.setAutoCancel(false);
        mNotificationBuilder.setSmallIcon(R.drawable.icon_status_bar);

        //Open up the player screen when the user taps on the notification.
        Intent launchNowPlayingIntent = new Intent();
        launchNowPlayingIntent.setAction(LAUNCH_NOW_PLAYING_ACTION);
        PendingIntent launchNowPlayingPendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), 0, launchNowPlayingIntent, 0);
        mNotificationBuilder.setContentIntent(launchNowPlayingPendingIntent);

        //Grab the notification layouts.
        notificationView = new RemoteViews(mContext.getPackageName(), R.layout.notification_custom_layout);
        expNotificationView = new RemoteViews(mContext.getPackageName(), R.layout.notification_custom_expanded_layout);

        //Initialize the notification layout buttons.
        Intent previousTrackIntent = new Intent();
        previousTrackIntent.setAction(PREVIOUS_ACTION);
        PendingIntent previousTrackPendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), 0, previousTrackIntent, 0);

        Intent playPauseTrackIntent = new Intent();
        playPauseTrackIntent.setAction(PLAY_PAUSE_ACTION);
        PendingIntent playPauseTrackPendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), 0, playPauseTrackIntent, 0);

        Intent nextTrackIntent = new Intent();
        nextTrackIntent.setAction(NEXT_ACTION);
        PendingIntent nextTrackPendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), 0, nextTrackIntent, 0);

        Intent stopServiceIntent = new Intent();
        stopServiceIntent.setAction(STOP_SERVICE);
        PendingIntent stopServicePendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), 0, stopServiceIntent, 0);

        //Check if audio is playing and set the appropriate play/pause button.
        if (uIcontroller.isplaying() || uIcontroller.isBuffering()) {
            notificationView.setImageViewResource(R.id.notification_base_play, R.drawable.btn_playback_pause_light);
            expNotificationView.setImageViewResource(R.id.notification_expanded_base_play, R.drawable.btn_playback_pause_light);
        } else {
            notificationView.setImageViewResource(R.id.notification_base_play, R.drawable.btn_playback_play_light);
            expNotificationView.setImageViewResource(R.id.notification_expanded_base_play, R.drawable.btn_playback_play_light);
        }
        //Set the notification content.
        expNotificationView.setTextViewText(R.id.notification_expanded_base_line_one, currentSong.getSong().getTitle());
        expNotificationView.setTextViewText(R.id.notification_expanded_base_line_two, currentSong.getSong().getArtist());
        expNotificationView.setTextViewText(R.id.notification_expanded_base_line_three, currentSong.getSong().getAlbum());

        notificationView.setTextViewText(R.id.notification_base_line_one, currentSong.getSong().getTitle());
        notificationView.setTextViewText(R.id.notification_base_line_two, currentSong.getSong().getArtist());

        //Set the states of the next/previous buttons and their pending intents.
        if (uIcontroller.isOnlySongInList()) {
            //This is the only song in the queue, so disable the previous/next buttons.
            expNotificationView.setViewVisibility(R.id.notification_expanded_base_next, View.INVISIBLE);
            expNotificationView.setViewVisibility(R.id.notification_expanded_base_previous, View.INVISIBLE);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_play, playPauseTrackPendingIntent);

            notificationView.setViewVisibility(R.id.notification_base_next, View.INVISIBLE);
            notificationView.setViewVisibility(R.id.notification_base_previous, View.INVISIBLE);
            notificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);

        } else {
            //We're smack dab in the middle of the queue, so keep the previous and next buttons enabled.
            expNotificationView.setViewVisibility(R.id.notification_expanded_base_previous, View.VISIBLE);
            expNotificationView.setViewVisibility(R.id.notification_expanded_base_next, View.VISIBLE);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_play, playPauseTrackPendingIntent);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_next, nextTrackPendingIntent);
            expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_previous, previousTrackPendingIntent);

            notificationView.setViewVisibility(R.id.notification_base_previous, View.VISIBLE);
            notificationView.setViewVisibility(R.id.notification_base_next, View.VISIBLE);
            notificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.notification_base_next, nextTrackPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.notification_base_previous, previousTrackPendingIntent);

        }

        //Set the "Stop Service" pending intents.
        expNotificationView.setOnClickPendingIntent(R.id.notification_expanded_base_collapse, stopServicePendingIntent);
        notificationView.setOnClickPendingIntent(R.id.notification_base_collapse, stopServicePendingIntent);

//        //Set the album art.
//        expNotificationView.setImageViewResource(R.id.notification_expanded_base_image,R.drawable.album_art );
//        notificationView.setImageViewResource(R.id.notification_base_image, R.drawable.album_art);

//        if (currentSong.isOffline()) {
//
//            if (currentSong.isCached()) {
//                if (!FileDownloader.doesFileExist(currentSong.getAlbumArtPath())) {
//                    expNotificationView.setImageViewResource(R.id.notification_expanded_base_image, R.drawable.album_art);
//                    notificationView.setImageViewResource(R.id.notification_base_image, R.drawable.album_art);
//                }
//            }
//            Uri uri =  Uri.parse(Utils.formatStringForUIL(currentSong.getAlbumArtPath()));
//            expNotificationView.setImageViewUri(R.id.notification_expanded_base_image, uri);
//            notificationView.setImageViewUri(R.id.notification_base_image, uri);
//        } else
//
            downloadAlbumart(currentSong);


//        expNotificationView.setImageViewBitmap(R.id.notification_expanded_base_image, songHelper.getAlbumArt());
//        notificationView.setImageViewBitmap(R.id.notification_base_image, songHelper.getAlbumArt());

        //Attach the shrunken layout to the notification.
        mNotificationBuilder.setContent(notificationView);

        //Build the notification object.
        android.app.Notification notification = mNotificationBuilder.build();

        //Attach the expanded layout to the notification and set its flags.
        notification.bigContentView = expNotificationView;
        notification.flags = android.app.Notification.FLAG_FOREGROUND_SERVICE |
                android.app.Notification.FLAG_NO_CLEAR |
                android.app.Notification.FLAG_ONGOING_EVENT;


        currentNotificationSong = currentSong;
        return notification;
    }


    private Notification buildICSNotification(Context mContext) {
        UIcontroller uIcontroller = UIcontroller.getInstance(mContext);
        Playable currentSong = uIcontroller.getCurrentSong();


        mNotificationBuilder = new NotificationCompat.Builder(mContext);
        mNotificationBuilder.setOngoing(true);
        mNotificationBuilder.setAutoCancel(false);
        mNotificationBuilder.setSmallIcon(R.drawable.icon_status_bar);

        //Open up the player screen when the user taps on the notification.
        Intent launchNowPlayingIntent = new Intent();
        launchNowPlayingIntent.setAction(LAUNCH_NOW_PLAYING_ACTION);
        PendingIntent launchNowPlayingPendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), 0, launchNowPlayingIntent, 0);
        mNotificationBuilder.setContentIntent(launchNowPlayingPendingIntent);

        //Grab the notification layout.
        notificationView = new RemoteViews(mContext.getPackageName(), R.layout.notification_custom_layout);

        //Initialize the notification layout buttons.
        Intent previousTrackIntent = new Intent();
        previousTrackIntent.setAction(PREVIOUS_ACTION);
        PendingIntent previousTrackPendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), 0, previousTrackIntent, 0);

        Intent playPauseTrackIntent = new Intent();
        playPauseTrackIntent.setAction(PLAY_PAUSE_ACTION);
        PendingIntent playPauseTrackPendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), 0, playPauseTrackIntent, 0);

        Intent nextTrackIntent = new Intent();
        nextTrackIntent.setAction(NEXT_ACTION);
        PendingIntent nextTrackPendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), 0, nextTrackIntent, 0);

        Intent stopServiceIntent = new Intent();
        stopServiceIntent.setAction(STOP_SERVICE);
        PendingIntent stopServicePendingIntent = PendingIntent.getBroadcast(mContext.getApplicationContext(), 0, stopServiceIntent, 0);

        //Check if audio is playing and set the appropriate play/pause button.
        if (uIcontroller.isplaying() || uIcontroller.isBuffering()) {
            notificationView.setImageViewResource(R.id.notification_base_play, R.drawable.btn_playback_pause_light);
        } else {
            notificationView.setImageViewResource(R.id.notification_base_play, R.drawable.btn_playback_play_light);
        }

        //Set the notification content.
        notificationView.setTextViewText(R.id.notification_base_line_one, currentSong.getSong().getTitle());
        notificationView.setTextViewText(R.id.notification_base_line_two, currentSong.getSong().getArtist());

        //Set the states of the next/previous buttons and their pending intents.
        if (uIcontroller.isOnlySongInList()) {
            //This is the only song in the queue, so disable the previous/next buttons.
            notificationView.setViewVisibility(R.id.notification_base_next, View.INVISIBLE);
            notificationView.setViewVisibility(R.id.notification_base_previous, View.INVISIBLE);
            notificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);

        } else {
            //We're smack dab in the middle of the queue, so keep the previous and next buttons enabled.
            notificationView.setViewVisibility(R.id.notification_base_previous, View.VISIBLE);
            notificationView.setViewVisibility(R.id.notification_base_next, View.VISIBLE);
            notificationView.setOnClickPendingIntent(R.id.notification_base_play, playPauseTrackPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.notification_base_next, nextTrackPendingIntent);
            notificationView.setOnClickPendingIntent(R.id.notification_base_previous, previousTrackPendingIntent);

        }

        //Set the "Stop Service" pending intent.
        notificationView.setOnClickPendingIntent(R.id.notification_base_collapse, stopServicePendingIntent);

//        //Set the album art.
        downloadAlbumart(currentSong);


        //Attach the shrunken layout to the notification.
        mNotificationBuilder.setContent(notificationView);

        //Build the notification object and set its flags.
        Notification notification = mNotificationBuilder.build();
        notification.flags = Notification.FLAG_FOREGROUND_SERVICE |
                Notification.FLAG_NO_CLEAR |
                Notification.FLAG_ONGOING_EVENT;

        currentNotificationSong = currentSong;
        return notification;
    }

    Bitmap defaultAlbumArtBitmap = null;

    private Bitmap getDefaultAlbumArtBitmap() {
        if (defaultAlbumArtBitmap == null)
            defaultAlbumArtBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.album_art);
        return defaultAlbumArtBitmap;
    }

    private void downloadAlbumart(final Playable currentSong) {
        LOGD(TAG, "downloadAlbumart");

        boolean error= false;
        if (currentSong.isOffline()) {

            if (currentSong.isCached()) {

                if (FileDownloader.doesFileExist(currentSong.getAlbumArtPath())) {
                    Uri uri =  Uri.parse(Utils.formatStringForUIL(currentSong.getAlbumArtPath()));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) expNotificationView.setImageViewUri(R.id.notification_expanded_base_image, uri);
                    notificationView.setImageViewUri(R.id.notification_base_image, uri);
                } else error = true;


            } else {

             Bitmap bmp =  LazyImageLoader.getArtworkQuick(context, Uri.parse(Utils.formatStringForUIL(currentSong.getAlbumArtPath())), 128, 128);
                if (bmp !=null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) expNotificationView.setImageViewBitmap(R.id.notification_expanded_base_image, bmp);
                    notificationView.setImageViewBitmap(R.id.notification_base_image, bmp);
                } else error = true;
            }

            if (error) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) expNotificationView.setImageViewBitmap(R.id.notification_expanded_base_image, getDefaultAlbumArtBitmap());
                notificationView.setImageViewBitmap(R.id.notification_base_image, getDefaultAlbumArtBitmap());
            }
            return;
        }













//        ImageSize imageSize = new ImageSize(128,128);
        ImageLoader.getInstance().loadImage(Utils.formatStringForUIL(currentSong.getAlbumArtPath()), new ImageLoadingListener() {


            @Override
            public void onLoadingStarted(String imageUri, View view) {
                LOGD(TAG, "started");

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                LOGD(TAG, "failed");
                setImage(imageUri,getDefaultAlbumArtBitmap());

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                LOGD(TAG, "loading complete " + imageUri + "  " + Utils.formatStringForUIL(currentNotificationSong.getAlbumArtPath()));
                    setImage(imageUri,loadedImage);

            }

            private void setImage(String imageUri, Bitmap loadedImage) {
                if (imageUri.equals(Utils.formatStringForUIL(currentNotificationSong.getAlbumArtPath()))) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        expNotificationView.setImageViewBitmap(R.id.notification_expanded_base_image, loadedImage);
                        notificationView.setImageViewBitmap(R.id.notification_base_image, loadedImage);

                        mNotificationBuilder.setContent(notificationView);

                        //Build the notification object.
                        android.app.Notification notification = mNotificationBuilder.build();

                        //Attach the expanded layout to the notification and set its flags.
                        notification.bigContentView = expNotificationView;
                        notification.flags = android.app.Notification.FLAG_FOREGROUND_SERVICE |
                                android.app.Notification.FLAG_NO_CLEAR |
                                android.app.Notification.FLAG_ONGOING_EVENT;
                        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                        notifManager.notify(mNotificationId, notification);

                    } else {

                        //Set the album art.
                        notificationView.setImageViewBitmap(R.id.notification_base_image, loadedImage);


                        //Attach the shrunken layout to the notification.
                        mNotificationBuilder.setContent(notificationView);

                        //Build the notification object and set its flags.
                        Notification notification = mNotificationBuilder.build();
                        notification.flags = Notification.FLAG_FOREGROUND_SERVICE |
                                Notification.FLAG_NO_CLEAR |
                                Notification.FLAG_ONGOING_EVENT;
                        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

                        notifManager.notify(mNotificationId, notification);
                    }


                }

            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                LOGD(TAG, "cancelled");

            }
        });
    }


}
