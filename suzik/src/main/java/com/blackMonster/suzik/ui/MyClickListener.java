package com.blackMonster.suzik.ui;

import android.util.Log;
import android.view.View;

import com.blackMonster.suzik.musicstore.Timeline.JsonHelperTimeline;
import com.blackMonster.suzik.ui.Screens.ContactsData;

import org.json.JSONException;

/**
 * Created by home on 2/15/2015.
 */
public class MyClickListener implements View.OnClickListener {
    MyContactsFilterAdapter adapter;
    ContactsData data;
    public MyClickListener(MyContactsFilterAdapter myContactsFilterAdapter, ContactsData contactsData) {
        Log.d("sa","s");
        this.adapter=myContactsFilterAdapter;
        this.data=contactsData;
    }

    @Override
    public void onClick(View v) {
        try {
            JsonHelperTimeline.FilterContacts.JsonCreator.filterFriendsOnApp(data);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
