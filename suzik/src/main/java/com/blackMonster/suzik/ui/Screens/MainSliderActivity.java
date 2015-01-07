package com.blackMonster.suzik.ui.Screens;



import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.blackMonster.suzik.R;

import static com.blackMonster.suzik.util.LogUtils.LOGD;


//TODO set Actionbar title on different fragments.

public class MainSliderActivity  extends ActionBarActivity implements View.OnClickListener{
    public static final String TAG = "MainSliderActivity";
	private static final int NUM_PAGES = 2;
	private ViewPager mPager;
	private PagerAdapter mPagerAdapter;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setContentView(R.layout.main_slider_activity);

		mPager = (ViewPager) findViewById(R.id.pager);
		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
		mPager.setAdapter(mPagerAdapter);
	}


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
		public ScreenSlidePagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				return new TimelineFragement();
			case 1:
				return new MySongListFragement();

			default:
				return null;
			}
		}

		@Override
		public int getCount() {
			return NUM_PAGES;
		}
	}

    Menu menu;
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.search));
        searchView.setOnSearchClickListener(this);

        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));





        return true;
    }
    @Override
    public void onClick(View v) {
        LOGD(TAG,"open");
       startActivity(new Intent(this,SearchResultActivity.class));
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideSearchBar();
    }

    private void hideSearchBar() {
        if (menu!=null) {
            MenuItem v = (menu.findItem(R.id.search));
            if (v!=null && v.isActionViewExpanded())
            (menu.findItem(R.id.search)).collapseActionView();
        }
    }
}
