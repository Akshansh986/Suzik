package com.blackMonster.suzik.ui;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.musicstore.Timeline.JsonHelperTimeline;
import com.blackMonster.suzik.ui.Screens.ContactsData;
import com.blackMonster.suzik.ui.Screens.TimelineFilterActivity;
import com.blackMonster.suzik.util.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

/**
 * Created by home on 2/15/2015.
 */
public class MyClickListener implements View.OnClickListener {
    private static final String TAG = "MyClickListener";
    private static final String TAG_MAKEWANTED = "makeWanted";
    private static final String TAG_MAKEUNWANTED = "makeUnwanted";

    MyContactsFilterAdapter adapter;
    ContactsData data;
    Context context;

    public MyClickListener(Context context, MyContactsFilterAdapter myContactsFilterAdapter, ContactsData contactsData) {

        this.adapter=myContactsFilterAdapter;
        this.data=contactsData;
        this.context=context;

    }

    @Override
    public void onClick(View v) {

        if(data.getFilterStatus()){
            ContactsFilterErrorTable.insert(data,context);

            data.setFilterStatus(false);
            MyContactsFilterAdapter.count--;
            Log.d(TAG,MyContactsFilterAdapter.count+"");
            JSONObject root= null;
            try {
                root = JsonHelperTimeline.FilterContacts.JsonCreator.filterFriendsOnApp(data,TAG_MAKEUNWANTED);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                postserver(root);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(MyContactsFilterAdapter.count<=TimelineFilterActivity.NOOFCONTACTS){
                adapter.lockData();
                adapter.notifyDataSetChanged();
            }
            /*adapter.addToUncheckedList(data);
            adapter.notifyDataSetChanged();
 */       }
        else{
            ContactsFilterErrorTable.insert(data,context);
            data.setFilterStatus(true);
            MyContactsFilterAdapter.count++;
            Log.d(TAG,MyContactsFilterAdapter.count+"");
            JSONObject root= null;
            try {
                root = JsonHelperTimeline.FilterContacts.JsonCreator.filterFriendsOnApp(data,TAG_MAKEWANTED);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            try {
                postserver(root);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if(MyContactsFilterAdapter.count> TimelineFilterActivity.NOOFCONTACTS){
                adapter.unlockData();
                adapter.notifyDataSetChanged();
            }

           /* adapter.addToCheckedList(data);
            adapter.notifyDataSetChanged();
*/
        }

        adapter.notifyDataSetChanged();


    }

    private void postserver(JSONObject postJson) throws JSONException {
        if (!NetworkUtils.isInternetAvailable(context.getApplicationContext())) {
           // Toast.makeText(context.getApplicationContext(), R.string.device_offline, Toast.LENGTH_SHORT).show();
            return;
        }
                   Log.d(TAG, postJson.toString());

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                AppConfig.MAIN_URL, postJson, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                LOGD(TAG, "Response: " + response.toString());
                ArrayList<ContactsData> contactsFilterList;
                if (response != null) {
                    try {
                        JsonHelperTimeline.FilterContacts.JsonParsor.parseFilterStatus(response,context);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.d(TAG,response.toString());
                    }
                else
                {
                   Log.d(TAG,response.toString());
                }

                }




        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context.getApplicationContext(), "Unable to load timeline ", Toast.LENGTH_LONG).show();
                //VolleyLog.d(TAG, "Error: " + error.getMessage());
                LOGD(TAG, "Error: " + error.getMessage());

            }
        });

        AppController.getInstance().addToRequestQueue(jsonReq);

    }
}