package com.blackMonster.suzik.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.R;
import com.blackMonster.suzik.musicPlayer.UIcontroller;
import com.blackMonster.suzik.musicPlayer.WorkerThread;
import com.blackMonster.suzik.musicstore.Flag.Flag;
import com.blackMonster.suzik.musicstore.Flag.FlagTable;
import com.blackMonster.suzik.musicstore.Timeline.JsonHelperTimeline;
import com.blackMonster.suzik.musicstore.Timeline.Playable;
import com.blackMonster.suzik.musicstore.Timeline.TimelineItem;
import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.musicstore.userActivity.UserActivityManager;
import com.blackMonster.suzik.sync.music.InAapSongTable;
import com.blackMonster.suzik.util.NetworkUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.blackMonster.suzik.sync.music.InAapSongTable.InAppSongData;
import static com.blackMonster.suzik.util.LogUtils.LOGD;

public class TimelineAdapter extends BaseAdapter implements Playlist {
    private static final String TAG = "TimelineAdapter";
    private Activity activity;
    private LayoutInflater inflater;
    private List<TimelineItem> timelineItems;
    ImageLoader imageLoader = ImageLoader.getInstance();
    Context context;
    static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());


    DisplayImageOptions options;

    WorkerThread worker;


    private View playingView = null;
    private AnimationSet animation = null;

    public TimelineAdapter(Activity activity, List<TimelineItem> timelineItems, Context context) {
        this.activity = activity;
        this.timelineItems = timelineItems;
        this.context = context;
        worker = new WorkerThread();
        worker.start();
        setAnimation();
        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.white)
                .showImageForEmptyUri(R.drawable.album_art)
                .showImageOnFail(R.drawable.album_art)
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .build();
    }


    public void setData(List<TimelineItem> timelineItems) {
        this.timelineItems = timelineItems;

    }

    @Override
    public int getCount() {
        return timelineItems.size();
    }

    @Override
    public Object getItem(int location) {
        return timelineItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d("TimelineAdapter", "getview : " + position);
        final ViewHolder viewHolder;

        if (convertView == null) {
            if (inflater == null)
                inflater = (LayoutInflater) activity
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.timeline_row, null);

            viewHolder = new ViewHolder();
            viewHolder.title = ((TextView) convertView.findViewById(R.id.song_title));
            viewHolder.artist = ((TextView) convertView.findViewById(R.id.song_artist));
            viewHolder.albumArtView = (ImageView) convertView.findViewById(R.id.album_art);
            viewHolder.likeButton = ((ImageView) convertView.findViewById(R.id.like_icon));
            viewHolder.progressBar = (ProgressBar) convertView.findViewById(R.id.progressBar);
            viewHolder.flag = (ImageView) convertView.findViewById(R.id.flag);
            convertView.setTag(viewHolder);

        } else {

            viewHolder = (ViewHolder) convertView.getTag();
        }

        final TimelineItem item = timelineItems.get(position);

//        handleSongPlaying(position,convertView);
        handleFlag(item, viewHolder.flag);


        if (uiconroller.isSongPlaying(this, position)) {
            viewHolder.title.setTextColor(context.getResources().getColor(R.color.timeline_text));
            viewHolder.artist.setTextColor(context.getResources().getColor(R.color.timeline_text));
            playingView = convertView;
            Log.d(TAG,"#########################################################");
            if (uiconroller.isBuffering()) {
                isBuffring=true;
                animateView();
            } else {
                isBuffring=false;
                stopAnimation();
            }            Log.d(TAG,"#########################################################");

        } else {
            viewHolder.title.setTextColor(context.getResources().getColor(R.color.white));
            viewHolder.artist.setTextColor(context.getResources().getColor(R.color.white));
            stopAnimation();
            if (playingView == convertView) playingView = null;
        }


        //View load


//        final ImageView likeButton =

        String title, artist;
        int likeIconResource;

        if (item.isCached()) {
            LOGD(TAG, "already downloaded");
            likeIconResource = R.drawable.redheart;
        } else {
            LOGD(TAG, "online song");
            likeIconResource = R.drawable.whiteheart;
        }


        title = item.getSong().getTitle();
        artist = item.getSong().getArtist();

        //setting values to views
        viewHolder.title.setText(title);

        if (artist == null) {
            viewHolder.artist.setVisibility(View.GONE);
        } else {
            viewHolder.artist.setText(artist);
        }

        viewHolder.likeButton.setImageResource(likeIconResource);

        viewHolder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LOGD(TAG, "buttondownload");
                if (NetworkUtils.isInternetAvailable(context)) {
                    if (item.isCached()) {
                        viewHolder.likeButton.setImageResource(R.drawable.whiteheart);
                        onDelete(item);

                    } else {
                        viewHolder.likeButton.setImageResource(R.drawable.redheart);
                        OnDownload(item);
                    }
                } else
                    Toast.makeText(context, R.string.device_offline, Toast.LENGTH_SHORT).show();


            }
        });


//        final ProgressBar pb =
        viewHolder.progressBar.setVisibility(View.GONE);

        if (NetworkUtils.isValidUrl(item.getOnlineAlbumArtUrl())) {

            imageLoader.displayImage(item.getOnlineAlbumArtUrl(), viewHolder.albumArtView, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    viewHolder.progressBar.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    viewHolder.progressBar.setVisibility(View.GONE);

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    viewHolder.progressBar.setVisibility(View.GONE);

                    if (loadedImage != null) {
                        ImageView imageView = (ImageView) view;

                        boolean firstDisplay = !displayedImages.contains(imageUri);
                        if (firstDisplay) {
                            viewHolder.progressBar.setVisibility(View.GONE);
                            FadeInBitmapDisplayer.animate(imageView, 500);
                            displayedImages.add(imageUri);
                        }
                    }


                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    viewHolder.progressBar.setVisibility(View.GONE);

                }
            });

        } else {
            viewHolder.albumArtView.setImageResource(R.drawable.album_art);
        }


        return convertView;
    }

    private void handleFlag(final TimelineItem item, final ImageView flagView) {
        final Flag flag = item.getFlag();

        if (flag.shouldDisplay()) {
            flagView.setVisibility(View.VISIBLE);
            if (flag.isServerBadSong()) flagView.setImageResource(R.drawable.flagred);
            else {
                if (flag.isLocalBadSong()) flagView.setImageResource(R.drawable.flagred);
                else flagView.setImageResource(R.drawable.flagwhite);
            }

        } else flagView.setVisibility(View.INVISIBLE);


        flagView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LOGD(TAG, "buttonflag");

                if (flag.isLocalBadSong() || flag.isServerBadSong())
                    return;

                if (NetworkUtils.isInternetAvailable(context)) {
                    flag.setLocalBadSong(true);
                    flagView.setImageResource(R.drawable.flagred);

                    sendFlagtoServer(item.getServerId());


                } else
                    Toast.makeText(context, R.string.device_offline, Toast.LENGTH_SHORT).show();


            }

            private void sendFlagtoServer(final long serverId) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        LOGD(TAG, "postFlag");
                        JSONObject postJson;
                        try {
                            postJson = JsonHelperTimeline.FlagJosnHelper.toJson(serverId);
                            RequestFuture<JSONObject> future = RequestFuture.newFuture();
                            JsonObjectRequest request = new JsonObjectRequest("http://socialmusic.in/music/songLink/flagSong.php",
                                    postJson, future, future);
                            AppController.getInstance().addToRequestQueue(request);

                            JSONObject response = future.get();
                            LOGD(TAG, "response " + response.toString());
                            if (JsonHelperTimeline.FlagJosnHelper.isSuccessfull(response))
                                FlagTable.insert(serverId, context);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }

                    }
                }).start();
            }
        });


    }

    static class ViewHolder {

        ImageView albumArtView, likeButton, flag;
        TextView title, artist;
        ProgressBar progressBar;


    }


    UIcontroller uiconroller = UIcontroller.getInstance(context);

    private void handleSongPlaying(int position, View convertView) {

        if (uiconroller.isSongPlaying(this, position)) {
            ((TextView) convertView.findViewById(R.id.song_title)).setTextColor(context.getResources().getColor(R.color.primary));
            ((TextView) convertView.findViewById(R.id.song_artist)).setTextColor(context.getResources().getColor(R.color.primary));
            playingView = convertView;
            UIcontroller.getInstance(context).loadcurrentplayerstatus();
        } else {
            ((TextView) convertView.findViewById(R.id.song_title)).setTextColor(context.getResources().getColor(R.color.white));
            ((TextView) convertView.findViewById(R.id.song_artist)).setTextColor(context.getResources().getColor(R.color.white));
            stopAnimation();
            if (playingView == convertView) playingView = null;
        }
    }

    private void OnDownload(final TimelineItem item) {

        new Thread() {
            public void run() {

                String songFileName = FileDownloader.getNewSongFileName();
                String songLocation = FileDownloader.getLocationFromFilename(songFileName, context);
                String albumartLocation = FileDownloader.getLocationFromFilename(FileDownloader.getNewAlbumArtName(), context);

                FileDownloader.saveImageToDisk(item.getAlbumArtPath(), albumartLocation);
                FileDownloader.saveSongToDisk(item.getSong().getTitle(), item.getSong().getArtist(),
                        item.getSongPath(), songFileName, context);
                long localId = insertInAppSongTable(item, songLocation, albumartLocation);
                updateUi(item);

                UserActivityManager.add(new UserActivity(item.getSong(), null, localId, UserActivity.ACTION_IN_APP_DOWNLOAD, 0, System.currentTimeMillis()), context);
            }
        }.start();


    }

    private void onDelete(final TimelineItem item) {

        new Thread() {
            public void run() {
                FileDownloader.deleteFile(item.getInAppSongMirror().getAlbumartLocation());
                FileDownloader.deleteFile(item.getInAppSongMirror().getSongLocation());
                InAapSongTable.remove(item.getInAppSongMirror().getId(), context);
                updateUi(item);
            }
        }.start();
    }


    private void updateUi(TimelineItem item) {
//        item.setInappMirrorIfAvailable(context);
        UiBroadcasts.broadcastMusicDataChanged(context);
    }

    private long insertInAppSongTable(TimelineItem item, String songLocatoin, String albumartLocation) {
        InAppSongData inAppSongData = new InAppSongData(null, item.getId(),
                item.getSong(), "", item.getAlbumArtPath(), item.getSongPath(),
                songLocatoin, albumartLocation);
        return InAapSongTable.insert(inAppSongData, context);
    }


    @Override
    public Playable getPlayable(int position) {
        return timelineItems.get(position);
    }

    @Override
    public int getSongCount() {
        return getCount();
    }

    public boolean isBuffring;

    public void animateView() {
//        if (true) return;
        Log.d(TAG, "setanimation");
        if (playingView == null) return;

        startTitleAnimation();
        startArtistAnimation();

    }

    private void startTitleAnimation() {
        final View view = playingView.findViewById(R.id.song_title);

        Animation fadeIn = new AlphaAnimation((float) 0.2, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);

        Animation fadeOut = new AlphaAnimation(1, (float) 0.2);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setStartOffset(500);
        fadeOut.setDuration(500);
//        fadeOut.setRepeatMode(Animation.REVERSE);
//        fadeOut.setRepeatCount(Animation.INFINITE);
//        view.startAnimation(fadeOut);


        fadeOut.setFillAfter(true);

        animation = new AnimationSet(false); //change to false
        animation.addAnimation(fadeOut);
        animation.addAnimation(fadeIn);
//        animation.setRepeatCount(Animation.INFINITE);

        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
            //    Log.d(TAG, "onAnimationStart");


            }

            @Override
            public void onAnimationRepeat(Animation animation) {
//                // TODO Auto-generated method stub
//                Log.d(TAG, "onAnimationRepeat");
//                if (!isbuffering) {
//
//                    animationHandler.removeCallbacks(animationRunnable);
//                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
          //      Log.d(TAG, "onAnimationEnd");

                if (isBuffring) view.startAnimation(animation);
            }
        });


        view.startAnimation(animation);

    }
    AnimationSet animationNew;
    private void startArtistAnimation() {
        final View view = playingView.findViewById(R.id.song_artist);

        Animation fadeIn = new AlphaAnimation((float) 0.2, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setStartOffset(500);
        fadeIn.setDuration(500);
        fadeIn.setFillAfter(true);

        Animation fadeOut = new AlphaAnimation(1, (float) 0.2);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        fadeOut.setDuration(500);
//        fadeOut.setRepeatMode(Animation.REVERSE);
//        fadeOut.setRepeatCount(Animation.INFINITE);
//        view.startAnimation(fadeOut);


        fadeOut.setFillAfter(true);

        animationNew = new AnimationSet(false); //change to false
        animationNew.addAnimation(fadeIn);
        animationNew.addAnimation(fadeOut);

//        animation.setRepeatCount(Animation.INFINITE);

        animationNew.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onAnimationStart");


            }

            @Override
            public void onAnimationRepeat(Animation animation) {
//                // TODO Auto-generated method stub
//                Log.d(TAG, "onAnimationRepeat");
//                if (!isbuffering) {
//
//                    animationHandler.removeCallbacks(animationRunnable);
//                }
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // TODO Auto-generated method stub
                Log.d(TAG, "onAnimationEnd");

                if (isBuffring) view.startAnimation(animation);
            }
        });


        view.startAnimation(animationNew);

    }


    public void stopAnimation() {
//        if (true) return;
        if (playingView == null) return;
        isBuffring = false;
//        playingView.findViewById(R.id.song_title).clearAnimation();
    }

    private void setAnimation() {


    }


}
