package com.blackMonster.suzik.ui;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.R;
import com.blackMonster.suzik.musicstore.Timeline.TimelineItem;

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

		
		
		
		TextView title = (TextView) convertView.findViewById(R.id.song_title);
		TextView artist = (TextView) convertView.findViewById(R.id.song_artist);
		
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

		if (item.getMediumAlbumArt()!= null) {
			feedImageView.setImageUrl(item.getMediumAlbumArt(), imageLoader);
			feedImageView.setVisibility(View.VISIBLE);
			feedImageView
					.setResponseObserver(new FeedImageView.ResponseObserver() {
						@Override
						public void onError() {
						}

						@Override
						public void onSuccess() {
						}
					});
		} else {
			feedImageView.setVisibility(View.GONE);
		}
	
		/*// Feed image
		if (item.getMediumAlbumArt() != null) {
			feedImageView.setImageUrl(item.getMediumAlbumArt(), imageLoader);
			//LayoutParams lp = new LayoutParams(source)
			//lp. = (int) (lp.width* .75);
		//	networkView.setLayoutParams(lp);
			feedImageView.setVisibility(View.VISIBLE);
			feedImageView.setDefaultImageResId(R.drawable.album_art);
			LinearLayout.LayoutParams  lp =  (LayoutParams) feedImageView.getLayoutParams();
			lp.height = (int) (feedImageView.getWidth() * .75);
			LOGD(TAG,lp.height + " " + lp.width);
			feedImageView.setLayoutParams(lp);
			networkView
					.setResponseObserver(new FeedImageView.ResponseObserver() {
						@Override
						public void onError() {
						}

						@Override
						public void onSuccess() {
						}
					});
		} else {
			feedImageView.setVisibility(View.GONE);
		}*/

		return convertView;
	}

}