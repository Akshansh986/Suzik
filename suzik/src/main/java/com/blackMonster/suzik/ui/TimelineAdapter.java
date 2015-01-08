package com.blackMonster.suzik.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.os.PowerManager;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.R;
import com.blackMonster.suzik.musicstore.Timeline.TimelineItem;
import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.musicstore.userActivity.UserActivityManager;
import com.blackMonster.suzik.sync.music.InAapSongTable;
import com.twitter.sdk.android.core.models.User;

import static com.blackMonster.suzik.sync.music.InAapSongTable.InAppSongData;
import static com.blackMonster.suzik.util.LogUtils.LOGD;

public class TimelineAdapter extends BaseAdapter {
    private static final String TAG = "TimelineAdapter";
    private Activity activity;
    private LayoutInflater inflater;
    private List<TimelineItem> timelineItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();
    Context context;

    public TimelineAdapter(Activity activity, List<TimelineItem> timelineItems, Context context) {
        this.activity = activity;
        this.timelineItems = timelineItems;
        this.context = context;
    }

    public void setData(List<TimelineItem> timelineItems) {
        this.timelineItems = timelineItems;

    }

    @Override
    public int getCount() {
        Log.d("TimelienAdapter", "getCount " + timelineItems.size());
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
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.timeline_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();


        final TextView title = (TextView) convertView.findViewById(R.id.song_title);
        final TextView artist = (TextView) convertView.findViewById(R.id.song_artist);

        FeedImageView feedImageView = (FeedImageView) convertView
                .findViewById(R.id.album_art);

        final TimelineItem item = timelineItems.get(position);

        title.setText(item.getSong().getTitle());

        if (item.getSong().getArtist() == null) {
            artist.setVisibility(View.GONE);
        } else {
            artist.setText(item.getSong().getArtist());
        }

//TODO getLocal albumart if inAppMirror is available in timeline item.

        if (item.getMediumAlbumArt() != null) {
            feedImageView.setImageUrl(item.getMediumAlbumArt(), imageLoader);
            feedImageView.setVisibility(View.VISIBLE);
            final View finalConvertView = convertView;
            feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                        }

                        @Override
                        public void onSuccess(Bitmap bitmap) {
//                            if (bitmap == null) return;
//                            Palette.generateAsync(bitmap,
//                                    new Palette.PaletteAsyncListener() {
//                                        @Override
//                                        public void onGenerated(Palette palette) {
//                                            Palette.Swatch vibrant =
//                                                    palette.getVibrantSwatch();
//                                            if (vibrant != null) {
//                                                // If we have a vibrant color
//                                                // update the title TextView
//
//                                                (finalConvertView.findViewById(R.id.ll_title_artist))
//                                                        .setBackgroundColor(
//                                                                vibrant.getRgb());
//
//                                                title.setTextColor(vibrant.getTitleTextColor());
//                                                artist.setTextColor(vibrant.getTitleTextColor());
//                                            }
//                                        }
//                                    });
                        }
                    });
        } else {
            feedImageView.setVisibility(View.GONE);
        }

        final ImageView likeButton = (ImageView) convertView.findViewById(R.id.like_icon);

        if (item.isInAppMirrorAvailable()) likeButton.setImageResource(R.drawable.redheart);
        else likeButton.setImageResource(R.drawable.whiteheart);


        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LOGD(TAG, "buttondownload");
                if (item.isInAppMirrorAvailable()) {
                    if (removeInAppSong(item))  likeButton.setImageResource(R.drawable.whiteheart);
                } else {
                    likeButton.setImageResource(R.drawable.redheart);
                    downloadFile(item);
                }


            }
        });

        return convertView;
    }


    private boolean removeInAppSong(TimelineItem item) {
        File file = new File(item.getInAppSongMirror().getLocation());
        boolean res = file.delete();
        LOGD(TAG, "deleted : " + res);
        if (res) {
            InAapSongTable.remove(item.getInAppSongMirror().getId(), context);
            item.setInAppSongMirrorIfAvailable(context);
            UiBroadcasts.broadcastMusicDataChanged(context);
        }
        return  res;

    }

    private void downloadFile(TimelineItem item) {
        String newFileName = getNewFileName();
        startDownload(item.getSong().getTitle(), item.getSong().getArtist(), item.getSongUrl(), newFileName);

        InAppSongData inAppSongData = new InAppSongData(null, item.getId(),
                item.getSong(), "", item.getMediumAlbumArt(), item.getSongUrl(), getLocation() + newFileName);
        InAapSongTable.insert(inAppSongData, context);
        item.setInAppSongMirrorIfAvailable(context);   //updating current timelineList for newly download song.

        UiBroadcasts.broadcastMusicDataChanged(context);
        UserActivityManager.add(new UserActivity(item.getSong(),null,item.getId(), UserActivity.ACTION_IN_APP_DOWNLOAD, 0,System.currentTimeMillis()), context);
    }

    void startDownload(String title, String artist, String url, String fileName) {

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setDescription(artist);
        request.setTitle(title);

        // in order for this if to run, you must use the android 3.2 to compile your app
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            //request.allowScanningByMediaScanner();
           request.setVisibleInDownloadsUi(false);
//           request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN);
        }
//        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        request.setDestinationInExternalFilesDir(context,null,fileName);

        // get download service and enqueue file
        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    private String getNewFileName() {
        return "a" + System.currentTimeMillis() + ".szk";
    }

    private String getLocation() {

LOGD(TAG,context.getExternalFilesDir(null).toString());
//        return "/sdcard/Downloads/";
        return context.getExternalFilesDir(null).toString() + "/";
    }


}
