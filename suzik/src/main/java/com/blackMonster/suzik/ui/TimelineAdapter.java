package com.blackMonster.suzik.ui;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.R;
import com.blackMonster.suzik.musicstore.Timeline.Playable;
import com.blackMonster.suzik.musicstore.Timeline.TimelineItem;
import com.blackMonster.suzik.musicstore.module.UserActivity;
import com.blackMonster.suzik.musicstore.userActivity.UserActivityManager;
import com.blackMonster.suzik.sync.music.InAapSongTable;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.nostra13.universalimageloader.core.display.RoundedBitmapDisplayer;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import static com.blackMonster.suzik.sync.music.InAapSongTable.InAppSongData;
import static com.blackMonster.suzik.sync.music.InAapSongTable.insert;
import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGE;

public class TimelineAdapter extends BaseAdapter implements  Playlist {
    private static final String TAG = "TimelineAdapter";
    private Activity activity;
    private LayoutInflater inflater;
    private List<TimelineItem> timelineItems;
    ImageLoader imageLoader =    ImageLoader.getInstance();
    Context context;

    private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();

    DisplayImageOptions options;

    public TimelineAdapter(Activity activity, List<TimelineItem> timelineItems, Context context) {
        this.activity = activity;
        this.timelineItems = timelineItems;
        this.context = context;



        options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.album_art)
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
        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.timeline_row, null);

        //View load
        ImageView albumArtView = (ImageView) convertView
                .findViewById(R.id.album_art);

        final TimelineItem item = timelineItems.get(position);
        final ImageView likeButton = ((ImageView) convertView.findViewById(R.id.like_icon));

        String title, artist;
        int likeIconResource;

        if (item.isCached()) {
            LOGD(TAG,"already downloaded");
            likeIconResource = R.drawable.redheart;
        } else {
            LOGD(TAG,"online song");
            likeIconResource = R.drawable.whiteheart;
        }

        imageLoader.displayImage(item.getOnlineAlbumArtUrl(), albumArtView, options, animateFirstListener);

        title = item.getSong().getTitle();
        artist = item.getSong().getArtist();

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

                if (item.isCached()) {
                    likeButton.setImageResource(R.drawable.whiteheart);
                    FileDownloader.deleteFile(item.getInAppSongMirror().getAlbumartLocation());
                    FileDownloader.deleteFile(item.getInAppSongMirror().getSongLocation());
                    InAapSongTable.remove(item.getInAppSongMirror().getId(),context);

                } else {
                    likeButton.setImageResource(R.drawable.redheart);
                    String songFileName = FileDownloader.getNewSongFileName();
                    String songLocation = FileDownloader.getLocationFromFilename(songFileName,context);
                    String albumartLocation = FileDownloader.getLocationFromFilename(FileDownloader.getNewAlbumArtName(),context);

                    FileDownloader.saveImageToDisk(item.getAlbumArtPath(),albumartLocation);
                    FileDownloader.saveSongToDisk(item.getSong().getTitle(),item.getSong().getArtist(),
                            item.getSongPath(),songFileName,context);
                    insertInAppSongTable(item,songLocation,albumartLocation);

                    UserActivityManager.add(new UserActivity(item.getSong(), null, item.getId(), UserActivity.ACTION_IN_APP_DOWNLOAD, 0, System.currentTimeMillis()), context);

                }

                updateUi(item);


            }
        });

        return convertView;
    }

//    private void fetchAlbumArtFromNet(final TimelineItem item, final NetworkImageView feedImageView, final boolean shouldSave) {
//
//        if (item.getAlbumArtPath() != null) {
//
//            feedImageView.setImageUrl(item.getAlbumArtPath(),imageLoader);
//            feedImageView.setDefaultImageResId(R.drawable.album_art);
////            feedImageView.set
//        }

//        if (item.getAlbumArtPath() != null) {
//            feedImageView.setImageUrl(item.getOnlineAlbumArtUrl(), imageLoader);
//            feedImageView
//                    .setResponseObserver(new FeedImageView.ResponseObserver() {
//                        @Override
//                        public void onError() {
//                            LOGE(TAG, "unable to load albumart");
//                        }
//
//                        @Override
//                        public void onSuccess(Bitmap bmp) {
////
////                            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) mlayout.getLayoutParams();
////
//////                            f
//////                            if (bWidth == 0 || bHeight == 0)
//////                                return;
//////
//////                            int swidth = getWidth();
//////                            int new_height = 0;
//////                            new_height = (int) ((swidth * bHeight / bWidth) );
////                            params.width = feedImageView.w;
////                            params.height = feedImageView.h;
////                            mlayout.setLayoutParams(params);
//
//
////
////                            if (shouldSave) {
////                                String location = item.getInAppSongMirror().getAlbumartLocation();
////                                if (location == null || location.equals(("")))
////                                    location = FileDownloader.getLocationFromFilename(FileDownloader.getNewAlbumArtName(),context);
////                                FileDownloader.writeToDisk(bmp, location);
////                                InAapSongTable.updateAlbumArtLocation(item.getInAppSongMirror().getId(),location,context);
//                        }
////                            if (bitmap == null) return;
////                            Palette.generateAsync(bitmap,
////                                    new Palette.PaletteAsyncListener() {
////                                        @Override
////                                        public void onGenerated(Palette palette) {
////                                            Palette.Swatch vibrant =
////                                                    palette.getVibrantSwatch();
////                                            if (vibrant != null) {
////                                                // If we have a vibrant color
////                                                // update the title TextView
////
////                                                (finalConvertView.findViewById(R.id.ll_title_artist))
////                                                        .setBackgroundColor(
////                                                                vibrant.getRgb());
////
////                                                title.setTextColor(vibrant.getTitleTextColor());
////                                                artist.setTextColor(vibrant.getTitleTextColor());
////                                            }
////                                        }
////                                    });
////                        }
//                    });
//        }

 //   }

//    private boolean loadLocalAlbumArtIfAvailable(final TimelineItem item, FeedImageView feedImageView) {
//
//        String location = item.getInAppSongMirror().getAlbumartLocation();
//        if (location==null || location.equals("")) {
//            LOGD(TAG,"albumart null or empty location");
//            return false;
//        }
//
//        File file = new File(location);
//        if (file.exists()) {
//            LOGD(TAG, "albumart found..setting");
//            BitmapFactory.Options options = new BitmapFactory.Options();
//            options.inPreferredConfig = Bitmap.Config.ARGB_8888;
//            Bitmap bitmap = BitmapFactory.decodeFile(item.getInAppSongMirror().getAlbumartLocation(), options);
//
//            Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.album_art);
//
//            feedImageView.setImageBitmap(bm);
//            return true;
//        }
//        LOGD(TAG, "albumart not found");
//
//        return false;
//
//    }

    private void updateUi(TimelineItem item) {
        item.setInappMirrorIfAvailable(context);
        UiBroadcasts.broadcastMusicDataChanged(context);
    }

    private void insertInAppSongTable(TimelineItem item, String songLocatoin, String albumartLocation) {
        InAppSongData inAppSongData = new InAppSongData(null, item.getId(),
                item.getSong(), "", item.getAlbumArtPath(), item.getSongPath(),
                songLocatoin, albumartLocation);
        InAapSongTable.insert(inAppSongData, context);
    }


    @Override
    public Playable getPlayable(int position) {
        return timelineItems.get(position);
    }

    @Override
    public int getSongCount() {
        return getCount();
    }









    public static class AnimateFirstDisplayListener extends SimpleImageLoadingListener {

        static final List<String> displayedImages = Collections.synchronizedList(new LinkedList<String>());

        @Override
        public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
            if (loadedImage != null) {
                ImageView imageView = (ImageView) view;
                boolean firstDisplay = !displayedImages.contains(imageUri);
                if (firstDisplay) {
                    FadeInBitmapDisplayer.animate(imageView, 500);
                    displayedImages.add(imageUri);
                }
            }
        }
    }








}
