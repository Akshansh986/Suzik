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


public class MainSliderActivity extends ActionBarActivity  {
    public static final String TAG = "MainSliderActivity";
    private static final int NUM_PAGES = 3;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;

    //TODO isVisible complete jugad, remove it after App update notification is not required
    public static boolean isVisible = false;

    @Override
    protected void onCreate(Bundle arg0) {

        super.onCreate(arg0);

        LOGD(TAG, "oncreate");

        setContentView(R.layout.empty_timeline);


    }


}
