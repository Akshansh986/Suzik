
package com.blackMonster.suzik.ui;


import android.content.Intent;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;


import android.os.Bundle;
import android.util.Log;

import com.blackMonster.suzik.R;
import com.blackMonster.suzik.sync.music.AndroidMusicHelper;
import com.blackMonster.suzik.sync.music.SongsSyncer;

import java.sql.Time;

/**
 * Created by akshanshsingh on 03/01/15.
 */
public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        TimelineFragement fragment = new TimelineFragement();
        fragmentTransaction.add(R.id.mainLL, fragment,TimelineFragement.FRAGMENT_TAG);
        fragmentTransaction.commit();


//	startService(new Intent(this, SongsSyncer.class));

//        Log.d("dfdf", Environment.get)







    }
}

