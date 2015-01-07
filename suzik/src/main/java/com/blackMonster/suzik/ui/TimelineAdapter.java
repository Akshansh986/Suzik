package com.blackMonster.suzik.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.R;
import com.blackMonster.suzik.musicstore.Timeline.TimelineItem;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

public class TimelineAdapter extends BaseAdapter {	
	private static final String TAG = "TimelineAdapter";
	private Activity activity;
	private LayoutInflater inflater;
	private List<TimelineItem> timelineItems;
	ImageLoader imageLoader = AppController.getInstance().getImageLoader();

	public TimelineAdapter(Activity activity, List<TimelineItem> timelineItems) {
		this.activity = activity;
		this.timelineItems = timelineItems;
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

	@SuppressLint("NewApi")
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

		TimelineItem item = timelineItems.get(position);

		title.setText(item.getSong().getTitle());
		
		if (item.getSong().getArtist() == null) {
			artist.setVisibility(View.GONE);
		}
		else {
			artist.setText(item.getSong().getArtist());
		}

//TODO getLocal albumart if inAppMirror is available in timeline item.

		if (item.getMediumAlbumArt()!= null) {
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

        ImageView likeButton = (ImageView) convertView.findViewById(R.id.like_icon);

        if (item.isInAppMirrorAvailable()) likeButton.setImageResource(R.drawable.redheart);
        else likeButton.setImageResource(R.drawable.whiteheart);


        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LOGD(TAG, "buttondownload");

            }
        });

		return convertView;
	}




}
