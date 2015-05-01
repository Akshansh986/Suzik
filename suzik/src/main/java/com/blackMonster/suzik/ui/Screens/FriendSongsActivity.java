package com.blackMonster.suzik.ui.Screens;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;

import com.blackMonster.suzik.R;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

/**
 * Created by akshanshsingh on 05/01/15.
 */



public class FriendSongsActivity extends ActionBarActivity{
    public static final String TAG = "SearchReslultActivity";
    MySongListFragement fragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        LOGD(TAG, "oncreate");
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.main_activity);


//        handleIntent(getIntent());

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment = new MySongListFragement();
        fragmentTransaction.add(R.id.mainLL, fragment);
        fragmentTransaction.commit();
    }

}
