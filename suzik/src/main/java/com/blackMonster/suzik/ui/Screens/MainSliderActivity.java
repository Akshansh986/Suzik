package com.blackMonster.suzik.ui.Screens;


import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
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
import com.blackMonster.suzik.musicPlayer.MusicPlayerFragment;
import com.blackMonster.suzik.musicPlayer.MusicPlayerService;
import com.blackMonster.suzik.musicPlayer.PlayerErrorCodes;
import com.blackMonster.suzik.musicPlayer.UIcontroller;
import com.blackMonster.suzik.ui.AppUpdateNotificaiton;
import com.blackMonster.suzik.ui.UiBroadcasts;

import static com.blackMonster.suzik.util.LogUtils.LOGD;


public class MainSliderActivity extends ActionBarActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {
    public static final String TAG = "MainSliderActivity";
    private static final int NUM_PAGES = 3;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    UIcontroller uIcontroller;

    //TODO isVisible complete jugad, remove it after App update notification is not required
    public static boolean isVisible = false;

    @Override
    protected void onCreate(Bundle arg0) {

        super.onCreate(arg0);

        LOGD(TAG, "oncreate");
        uIcontroller = UIcontroller.getInstance(this);
        uIcontroller.bindtoservice();

        setContentView(R.layout.main_slider_activity);

        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOnPageChangeListener(this);
        onPageSelected(0);
        AppUpdateNotificaiton.showAppUpdateDialogIfNecessary(this);


        //TODO jugad
        mPager.setCurrentItem(1);
        onPageSelected(1);
        registerReceivers();

        hadleError(uIcontroller.getErrorState());
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        setTitle(mPagerAdapter.getPageTitle(position));
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MusicPlayerFragment();
                case 1:
                    return new TimelineFragement();
                case 2:
                    return new MySongListFragement();
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return getResources().getString(R.string.title_player);
                case 1:
                    return getResources().getString(R.string.title_timeline);
                case 2:
                    return getResources().getString(R.string.title_allSongs);

                default:
                    return null;
            }
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
        LOGD(TAG, "open");
        startActivity(new Intent(this, SearchResultActivity.class));
    }


    @Override
    protected void onPause() {
        super.onPause();
        isVisible = false;
    }

    @Override
    protected void onResume() {
        super.onResume();

        hideSearchBar();
        isVisible = true;
    }

    private void hideSearchBar() {
        if (menu != null) {
            MenuItem v = (menu.findItem(R.id.search));
            if (v != null && v.isActionViewExpanded())
                (menu.findItem(R.id.search)).collapseActionView();
        }
    }


    private void hadleError(int error) {
        switch (error) {
            case PlayerErrorCodes.DATA_SRC:
                showAlertDialog(R.string.player_data_not_set);
                break;
            case PlayerErrorCodes.UNKNOWN:
                showAlertDialog(R.string.player_unknown_error);
                break;
        }
    }


    private void showAlertDialog(int message) {


        new AlertDialog.Builder(this)
                .setMessage(getResources().getString(message))
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // continue with delete
                    }
                })

                .show();

    }

    @Override
    protected void onDestroy() {
        LOGD(TAG, "onDestroy");

        super.onDestroy();
        if (uIcontroller != null) {
            uIcontroller.unbind();
            LOGD(TAG, "UNBIND");

        }
        unregisterReceivers();
    }


    private BroadcastReceiver broadcastPlayerError = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            int error = intent.getIntExtra("Error", 100);
            if (error == 100) return;
            hadleError(error);


        }


    };

    private BroadcastReceiver broadcastFirstTimeFlag = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            showAlertDialog(R.string.first_time_flag_msg);
        }

    };

    private void registerReceivers() {
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastPlayerError, new IntentFilter(MusicPlayerService.broadcastError));
        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastFirstTimeFlag, new IntentFilter(UiBroadcasts.FIRST_TIME_FLAG));

    }

    private void unregisterReceivers() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastPlayerError);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(broadcastFirstTimeFlag);


    }
}
