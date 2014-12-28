package com.blackMonster.suzik.ui.imagePreload;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.blackMonster.suzik.musicstore.Timeline.TimelineItem;
import com.blackMonster.suzik.ui.FeedImageView;
import com.blackMonster.suzik.ui.imagePreload.PageManager.OnWindowChange;

public class ImagePreloader implements OnWindowChange {
	private static final String TAG = "ImagePreloader";
	ImageDownloader imageDownloader = new ImageDownloader();
	PageManager pageManager = new PageManager();
	private ImageLoader mImageLoader;
	private List<TimelineItem> timelineItems;

	public ImagePreloader(ImageLoader imageLoader,
			List<TimelineItem> timelineItems) {
		this.mImageLoader = imageLoader;
		this.timelineItems = timelineItems;
		pageManager.registerOnWindowChangeListner(this);
	}

	public void insert(ImageView view, int id, String link) {
		pageManager.add(view, id);
		cancelProcessingOutsideBounds(id);

		if (imageDownloader.isDownloading(id)) {
			return;
		} else {
			imageDownloader.add(id, link);
		}

	}

	private void cancelProcessingOutsideBounds(int id) {
		imageDownloader.cancelOutsideRange(pageManager.getOverallPageBound());
	}

	@Override
	public void onWindowChange(int uselessId) {
		imageDownloader.cancel(uselessId);

	}

	class ImageDownloader {
		List<DownloadImage> list = new ArrayList<ImagePreloader.DownloadImage>();

		public void add(int id, String link) {
			list.add(new DownloadImage(id, link));
		}

		public void remove(DownloadImage x) {
			Log.d(TAG, "remove image : " + x.id);
			list.remove(x);
		}

		public boolean cancel(int id) {
			Log.d(TAG, "cancel image : " + id);
			int n = list.size();
			for (int i = 0; i < n; ++i) {
				if (list.get(i).id == id) {
					list.get(i).imageContainer.cancelRequest();
					remove(list.get(i));
					return true;
				}
			}
			return false;
		}

		public void cancelOutsideRange(Pair<Integer, Integer> range) {
			Log.d(TAG, "cancel outside range : " + range.first + "   " + range.second);
			int n = list.size();
			for (int i = 0; i < n; ++i) {
				DownloadImage di = list.get(i);
				if (di.id < range.first || di.id > range.second) {
					di.imageContainer.cancelRequest();
					list.remove(di);
				}
			}

		}

		public boolean isDownloading(int id) {
			int n = list.size();
			for (int i = 0; i < n; ++i) {
				if (list.get(i).id == id) {
					return true;
				}
			}
			return false;
		}

	}

	class DownloadImage {
		ImageContainer imageContainer;
		final int id;
		String link;

		public DownloadImage(final int id, final String link) {
			this.id = id;
			this.link = link;

			ImageContainer newContainer = mImageLoader.get(link,
					new ImageListener() {
						@Override
						public void onErrorResponse(VolleyError error) {

							Log.d(TAG, "error loading image " + link );
							// if (mErrorImageId != 0) {
							// setImageResource(mErrorImageId);
							// }
							//
							// if (mObserver != null) {
							// mObserver.onError();
							// }
						}

						@Override
						public void onResponse(final ImageContainer response,
								boolean isImmediate) {

							imageDownloader.remove(DownloadImage.this);
							if (pageManager.shouldLoadNextPage(id)) {
								Log.d(TAG, "Should load next page true");
								startNextBatch();
							}

							ImageView imageView = pageManager.getViewForId(id);
							if (imageView != null) {

								if (isImmediate) {
									imageView.post(new Runnable() {
										@Override
										public void run() {
											onResponse(response, false);
										}
									});
									return;
								}

								int bWidth = 0, bHeight = 0;
								if (response.getBitmap() != null) {

									imageView.setImageBitmap(response
											.getBitmap());
									bWidth = response.getBitmap().getWidth();
									bHeight = response.getBitmap().getHeight();
									adjustImageAspect(imageView, bWidth,
											bHeight);

								}
							}

						}

						private void startNextBatch() {
							Log.d(TAG, "Starting next batch"	);
							int n = PageManager.PAGE_SIZE + id; 
							for (int i=id ; i < n ; ++i) {
								if (! imageDownloader.isDownloading(i)) {
									imageDownloader.add(id, timelineItems.get(i).getMediumAlbumArt());
								}
							}
						}

					});

		}

		private void adjustImageAspect(ImageView imageView, int bWidth,
				int bHeight) {
			RelativeLayout.LayoutParams params = (android.widget.RelativeLayout.LayoutParams) imageView
					.getLayoutParams();

			if (bWidth == 0 || bHeight == 0)
				return;

			int swidth = imageView.getWidth();
			int new_height = 0;
			new_height = (int) ((swidth * bHeight / bWidth));
			params.width = swidth;
			params.height = new_height;
			imageView.setLayoutParams(params);
		}

	}

}
