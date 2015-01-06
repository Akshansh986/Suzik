package com.blackMonster.suzik.ui.Screens;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.blackMonster.suzik.R;


public class MainSliderActivity {}// extends FragmentActivity implements OnManualPageChangeListner {
//
//	private static final int NUM_PAGES = 2;
//	private ViewPager mPager;
//	private PagerAdapter mPagerAdapter;
//
//	@Override
//	protected void onCreate(Bundle arg0) {
//		super.onCreate(arg0);
//		setContentView(R.layout.main_slider_activity);
//
//		mPager = (ViewPager) findViewById(R.id.pager);
//		mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
//		mPager.setAdapter(mPagerAdapter);
//	}
//
//	private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
//		public ScreenSlidePagerAdapter(FragmentManager fm) {
//			super(fm);
//		}
//
//		@Override
//		public Fragment getItem(int position) {
//			switch (position) {
//			case 0:
//				return new TimelineFragement();
//			case 1:
//				return new InAppSongFragment();
//
//			default:
//				return null;
//			}
//		}
//
//		@Override
//		public int getCount() {
//			return NUM_PAGES;
//		}
//	}
//
//	@Override
//	public void setPage(int num) {
//		mPager.setCurrentItem(num);
//	}
//}
