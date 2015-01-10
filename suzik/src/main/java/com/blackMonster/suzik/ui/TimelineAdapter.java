package com.blackMonster.suzik.ui;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.R;
import com.blackMonster.suzik.musicstore.Timeline.TimelineItem;
import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.musicstore.userActivity.UserActivityManager;
import com.blackMonster.suzik.sync.music.InAapSongTable;

import static com.blackMonster.suzik.sync.music.InAapSongTable.InAppSongData;
import static com.blackMonster.suzik.sync.music.InAapSongTable.insert;
import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGE;

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
//        Log.d("TimelienAdapter", "getCount " + timelineItems.size());
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
//        Log.d("TimelineAdapter", "getview : " + position);
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.timeline_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();


        //View load
        FeedImageView feedImageView = (FeedImageView) convertView
                .findViewById(R.id.album_art);
        final TimelineItem item = timelineItems.get(position);
        final ImageView likeButton = ((ImageView) convertView.findViewById(R.id.like_icon));
        //

        String title, artist;
        int likeIconResource;

        if (item.isAlreadyInappDownload()) {
            LOGD(TAG,"already downloaded");
            title = item.getInAppSongMirror().getSong().getTitle();
            artist = item.getInAppSongMirror().getSong().getArtist();
            likeIconResource = R.drawable.redheart;
//            if (!loadLocalAlbumArtIfAvailable(item, feedImageView))
//                fetchAlbumArtFromNet(item, feedImageView, true);
        } else {
            LOGD(TAG,"online song");
            title = item.getSong().getTitle();
            artist = item.getSong().getArtist();
            likeIconResource = R.drawable.whiteheart;
        }
           fetchAlbumArtFromNet(item, feedImageView, false);


        //setting values to views
        ((TextView) convertView.findViewById(R.id.song_title)).setText(title);

        if (artist == null) {
            ((TextView) convertView.findViewById(R.id.song_artist)).setVisibility(View.GONE);
        } else {
            ((TextView) convertView.findViewById(R.id.song_artist)).setText(artist);
        }

        likeButton.setImageResource(likeIconResource);

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LOGD(TAG, "buttondownload");

                if (item.isAlreadyInappDownload()) {
                    FileDownloader.deleteFile(item.getInAppSongMirror().getAlbumartLocation());
                    FileDownloader.deleteFile(item.getInAppSongMirror().getSongLocation());
                    InAapSongTable.remove(item.getInAppSongMirror().getId(),context);

                    likeButton.setImageResource(R.drawable.whiteheart);
                } else {
                    String songFileName = FileDownloader.getNewSongFileName();
                    String songLocation = FileDownloader.getLocationFromFilename(songFileName,context);
                    String albumartLocation = FileDownloader.getLocationFromFilename(FileDownloader.getNewAlbumArtName(),context);

                    FileDownloader.saveImageToDisk(item.getMediumAlbumArt(),albumartLocation);
                    FileDownloader.saveSongToDisk(item.getSong().getTitle(),item.getSong().getArtist(),
                            item.getSongUrl(),songFileName,context);
                    insertInAppSongTable(item,songLocation,albumartLocation);

                    likeButton.setImageResource(R.drawable.redheart);
                    UserActivityManager.add(new UserActivity(item.getSong(), null, item.getId(), UserActivity.ACTION_IN_APP_DOWNLOAD, 0, System.currentTimeMillis()), context);

                }

                updateUi(item);


            }
        });

        return convertView;
    }

    private void fetchAlbumArtFromNet(final TimelineItem item, FeedImageView feedImageView, final boolean shouldSave) {

        if (item.getMediumAlbumArt() != null) {
            feedImageView.setImageUrl(item.getMediumAlbumArt(), imageLoader);
            feedImageView.setVisibility(View.VISIBLE);
            feedImageView
                    .setResponseObserver(new FeedImageView.ResponseObserver() {
                        @Override
                        public void onError() {
                            LOGE(TAG, "unable to load albumart");
                        }

                        @Override
                        public void onSuccess(Bitmap bmp) {
                            if (shouldSave) {
                                String location = item.getInAppSongMirror().getAlbumartLocation();
                                if (location == null || location.equals(("")))
                                    location = FileDownloader.getLocationFromFilename(FileDownloader.getNewAlbumArtName(),context);
                                FileDownloader.writeToDisk(bmp, location);
                                InAapSongTable.updateAlbumArtLocation(item.getInAppSongMirror().getId(),location,context);
                            }
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

    }

    private boolean loadLocalAlbumArtIfAvailable(final TimelineItem item, FeedImageView feedImageView) {

        String location = item.getInAppSongMirror().getAlbumartLocation();
        if (location==null || location.equals("")) {
            LOGD(TAG,"albumart null or empty location");
            return false;
        }

        File file = new File(location);
        if (file.exists()) {
            LOGD(TAG, "albumart found..setting");
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
            Bitmap bitmap = BitmapFactory.decodeFile(item.getInAppSongMirror().getAlbumartLocation(), options);

            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.album_art);

            feedImageView.setImageBitmap(bm);
            return true;
        }
        LOGD(TAG, "albumart not found");

        return false;

    }

    private void updateUi(TimelineItem item) {
        item.setInappMirrorIfAvailable(context);
        UiBroadcasts.broadcastMusicDataChanged(context);
    }


//    private boolean removeInAppSong(TimelineItem item) {
//        boolean res = deleteFile(item.getInAppSongMirror().getSongLocation());
//                res =  res && deleteFile(item.getInAppSongMirror().getAlbumartLocation());
//        LOGD(TAG, "deleted : " + res);
//
//        InAapSongTable.remove(item.getInAppSongMirror().getId(), context);
//
//
//        return res;
//
//    }
//
//
//
//    private void downloadFile(TimelineItem item) {
//        long time = System.currentTimeMillis();
//        String newSongFileName = getNewSongFileName(time);
//        String newAlbumArtFileName = getNewAlbumArtName(time);
//
//        updateInAppSongTable(item, newSongFileName, newAlbumArtFileName);
//
//        startSongDownload(item.getSong().getTitle(), item.getSong().getArtist(), item.getSongUrl(), newSongFileName);
//        saveBitmap(item, getLocation() + newAlbumArtFileName);
//
//
//
//    }

    private void insertInAppSongTable(TimelineItem item, String songLocatoin, String albumartLocation) {
        InAppSongData inAppSongData = new InAppSongData(null, item.getId(),
                item.getSong(), "", item.getMediumAlbumArt(), item.getSongUrl(),
                songLocatoin, albumartLocation);
        InAapSongTable.insert(inAppSongData, context);
    }



}
