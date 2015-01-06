
package com.blackMonster.suzik.ui;


import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;


import android.os.Bundle;

import com.blackMonster.suzik.R;

import java.sql.Time;

/**
 * Created by akshanshsingh on 03/01/15.
 */
public class MainActivity extends ActionBarActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);






//
//        // find the retained fragment on activity restarts
//        FragmentManager fm = getSupportFragmentManager();
//        TimelineFragement timelineFragment = (TimelineFragement) fm.findFragmentByTag(TimelineFragement.FRAGMENT_TAG);
//
//        // create the fragment and data the first time
//        if (timelineFragment == null) {
//            // add the fragment
//            timelineFragment = new TimelineFragement();
//            fm.beginTransaction().add(R.id.mainLL,timelineFragment, TimelineFragement.FRAGMENT_TAG).commit();
//            // load the data from the web
////            timelineFragment.setData(loadMyData());
//        }





        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();


        TimelineFragement fragment = new TimelineFragement();
        fragmentTransaction.add(R.id.mainLL, fragment,TimelineFragement.FRAGMENT_TAG);
        fragmentTransaction.commit();












    }
}

