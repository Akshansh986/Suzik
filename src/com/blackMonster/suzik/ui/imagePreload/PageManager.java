package com.blackMonster.suzik.ui.imagePreload;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGE;

import java.util.ArrayList;
import java.util.List;

import android.util.Pair;
import android.view.View;
import android.widget.ImageView;

public class PageManager {
	private static int UP = 2;
	private static int DOWN = 1;
	private static final String TAG = "PageManager";
	public static final int PAGE_SIZE = 3;

	List<Container> containers;
	int direction = DOWN;
	OnWindowChange windowChange;

	public PageManager() {
		containers = new ArrayList<Container>();
	}

	public void registerOnWindowChangeListner(OnWindowChange windowChange) {
		this.windowChange = windowChange;
	}

	public ImageView getViewForId(int id) {
		for (Container container : containers) {
			if (container.id == id) {
				return container.view;
			}
		}
		return null;
	}

	public Pair<Integer, Integer> getOverallPageBound() {
		if (direction == DOWN)
			return new Pair<Integer, Integer>(getSmallestId(), getLargestId()
					+ PAGE_SIZE);
		else {
			int lower = getSmallestId() - PAGE_SIZE;
			if (lower < 0)
				lower = 0;
			return new Pair<Integer, Integer>(lower, getLargestId());
		}
	}

	public Pair<Integer, Integer> getSecondPageBound() {
		int low, high;
		if (direction == DOWN) {
			low = getLargestId() + 1;
			high = low + PAGE_SIZE - 1;

		} else {
			high = getSmallestId() - 1;
			if (high < 0)
				high = 0;
			low = high - PAGE_SIZE + 1;
			if (low < 0) low = 0;

		}
		return new Pair<Integer, Integer>(low, high);

	}

	public boolean shouldLoadNextPage(int id) {

		if (direction == DOWN) {
			return id == getLargestId();
		} else {
			return id == getSmallestId();
		}
	}

	public void add(ImageView view, int id) {
		setDirection(id);
		updateOrAddView(view, id);
	}

	private void setDirection(int id) {
		if (!containers.isEmpty()) {
			if (id > getLargestId()) {
				direction = DOWN;
				LOGD(TAG, "Direction down");

			} else if (id < getSmallestId()) {
				direction = UP;
				LOGD(TAG, "Direction up");
			} else if (id == getLargestId())
				LOGD(TAG, "ID equals largest id");
			else if ( id == getSmallestId())
				LOGD(TAG,"ID equals smallest id");
			else
				LOGE(TAG,"ID error");
				
		}
	}

	private void updateOrAddView(ImageView view, int id) {
		
		for (Container container : containers) {
			if (container.view == view) {
				LOGD(TAG, "View found for id " + id);
				windowChange.onWindowChange(container.id, container.view);
				container.id = id;
				return;
			}
		}
		containers.add(new Container(view, id));
		LOGD(TAG,"Added new view for id : " + id);

	}

	private int getSmallestId() {
		if (containers.isEmpty())
			return 0;
		int smallest = 10000;
		for (Container container : containers) {
			if (container.id < smallest)
				smallest = container.id;
		}
		LOGD(TAG,"smallest id : " + smallest);
		return smallest;
	}

	private int getLargestId() {
		if (containers.isEmpty())
			return 0;
		int largest = -1;
		for (Container container : containers) {
			if (container.id > largest)
				largest = container.id;
		}
		LOGD(TAG,"largest id : " + largest);
		return largest;
	}

	public interface OnWindowChange {

		public void onWindowChange(int uselessId, ImageView imageView);

	}

	class Container {
		ImageView view;
		int id;

		public Container(ImageView view, int id) {
			super();
			this.view = view;
			this.id = id;
		}

	}

}
