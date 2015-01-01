package com.blackMonster.suzik.ui.imagePreload;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.util.Log;
import android.util.Pair;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.blackMonster.suzik.R;
import com.blackMonster.suzik.musicstore.Timeline.TimelineItem;
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
	
	
	public void changeDataset(List<TimelineItem> timelineItems) {
		this.timelineItems = timelineItems;
	}

	public void insert(ImageView view, int id, String link) {
		
		pageManager.add(view, id);
		cancelProcessingOutsideBounds(id);

		if (imageDownloader.isDownloading(id)) {
			Log.d(TAG,"downloading : " + id);
			return;
		} else {
			imageDownloader.add(id, link);
		}

	}

	private void cancelProcessingOutsideBounds(int id) {
		imageDownloader.cancelOutsideRange(pageManager.getOverallPageBound());
	}

	@Override
	public void onWindowChange(int uselessId, ImageView view) {
		Log.d(TAG, "on window changed called");
		imageDownloader.cancel(uselessId);
		view.setImageResource(R.drawable.album_art);

	}

	class ImageDownloader {
		private List<DownloadImage> list = new ArrayList<ImagePreloader.DownloadImage>();

		public void add(int id, String link) {
			Log.d(TAG, "add image : " + id);
			
			DownloadImage di = new DownloadImage(id, link);
			list.add(di);
			di.startDownload();
			
		}

		public void remove(DownloadImage x) {
			Log.d(TAG,"remove image : " + x.id + " " + list.remove(x));
		}

		public boolean cancel(int id) {
			Log.d(TAG, "cancel image : " + id);
			
			for (Iterator<DownloadImage> iterator = list.iterator(); iterator.hasNext(); ) {
			    DownloadImage obj = iterator.next();
			    
			    Log.d(TAG,obj.id + " ");
				if (obj.id == id) {
					obj.imageContainer.cancelRequest();
					iterator.remove();
					Log.e(TAG, "cancelled image and removed from list: " + id);
					return true;
				}
			    
			   			   
			}
			
			return false;
			
		}

		public void cancelOutsideRange(Pair<Integer, Integer> range) {
			Log.e(TAG, "cancel outside range : " + range.first + "   "
					+ range.second);
			
			
			for (Iterator<DownloadImage> iterator = list.iterator(); iterator.hasNext(); ) {
			    DownloadImage di = iterator.next();
			    
			    if (di.id < range.first || di.id > range.second) {
			    		Log.e(TAG, "cancelled : " + di.id);
					di.imageContainer.cancelRequest();
					iterator.remove();
				} 
			   
			   			   
			}
			Log.e(TAG, "cancel outside range : " + range.first + "   "
					+ range.second);
			
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
		}
		
		
		void startDownload() {
			
			Log.i(TAG, "getting id : " + id);
			if (link.equals("")) {
				otherWork();
				return;
			}
			imageContainer = mImageLoader.get(link, new ImageListener() {
				@Override
				public void onErrorResponse(VolleyError error) {

					Log.d(TAG, "error loading image " + link);
					otherWork();
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
					boolean isBitmapNull = response.getBitmap() == null;

					Log.d(TAG, "Response received for " + id + " : " + isImmediate + " "
							+ isBitmapNull);

					if (isImmediate && isBitmapNull) {
						Log.d(TAG, "both true");
						return;
					}

					if (!isImmediate && !isBitmapNull) {
						Log.d(TAG, "bitmap is not null");
						setImage(response);
					}
					
					if (isImmediate && !isBitmapNull) {
						ImageView imageView = pageManager.getViewForId(id);
						if (imageView != null)  {
							imageView.post(new Runnable() {
								@Override
								public void run() {
									onResponse(response, false);
								}
							});
							return;
						}
					}

					if (!isImmediate && isBitmapNull) {
						Log.e(TAG, "Image load error");

					}

					otherWork();
				

					// if (isImmediate) {
					// Log.d(TAG, "Isimeediated true for : " + id
					// + "response " + (response.getBitmap() == null));
					// return;
					// }
					// Log.d(TAG, "Isimeediated false for : " + id + "response "
					// + (response.getBitmap() == null));

					// ImageView imageView = pageManager.getViewForId(id);
					// if (isImmediate) {
					// imageView.post(new Runnable() {
					// @Override
					// public void run() {
					// onResponse(response, false);
					// }
					// });
					// return;
					// }

				}

			
			});
		}
		
		private void otherWork() {
			imageDownloader.remove(DownloadImage.this);
//			if (id ==1) startNextBatch();
								if (pageManager.shouldLoadNextPage(id)) {
				Log.d(TAG, "Should load next page true");
				 startNextBatch();
			} else {
				Log.d(TAG, "Should load next page false");
			}					
		}

		private void setImage(ImageContainer response) {
			ImageView imageView = pageManager.getViewForId(id);
			if (imageView != null) {
				Log.d(TAG, "image veiw not null");

				int bWidth = 0, bHeight = 0;
				if (response.getBitmap() != null) {
					Log.d(TAG, "setting image " + id);

					imageView.setImageBitmap(response.getBitmap());
					bWidth = response.getBitmap().getWidth();
					bHeight = response.getBitmap().getHeight();
					adjustImageAspect(imageView, bWidth, bHeight);

				} else {
					Log.e(TAG, "Image load error");
				}
			} else {
				Log.d(TAG, "image veiw null");
			}

		}

		private void startNextBatch() {
			
			Pair<Integer, Integer> bound = pageManager.getSecondPageBound();
			int second;
			if (bound.second >= timelineItems.size()) {
				second = timelineItems.size() - 1;
			}
			else {
				second = bound.second;
			}
			
			Log.i(TAG, "Starting next batch " + timelineItems.size());
//			int n = PageManager.PAGE_SIZE + id;
//			if (n >= timelineItems.size()) n = timelineItems.size() -1;
			for (int i = bound.first; i <=second; ++i) {
				if (!imageDownloader.isDownloading(i)) {
					imageDownloader.add(i, timelineItems.get(i)
							.getMediumAlbumArt());
				}
			}
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
