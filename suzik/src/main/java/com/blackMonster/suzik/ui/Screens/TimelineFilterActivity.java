package com.blackMonster.suzik.ui.Screens;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.R;
import com.blackMonster.suzik.musicstore.Timeline.JsonHelperTimeline;
import com.blackMonster.suzik.ui.MyContactsFilterAdapter;
import com.blackMonster.suzik.util.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.blackMonster.suzik.util.LogUtils.LOGD;


public class TimelineFilterActivity extends Activity {

    private static final String TAG = "TimelineFilterActivity";
    ListView listView;
    MyContactsFilterAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(NetworkUtils.isInternetAvailable(getApplicationContext())){
           intializeContactsFilter();
        }

        else {

            setContentView(R.layout.activity_filter_contacts_netnotavailable);
            ((RelativeLayout) findViewById(R.id.relative_layout)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(NetworkUtils.isInternetAvailable(getApplicationContext())) {
                        intializeContactsFilter();
                    }
                    else {
                        Context context = getApplicationContext();
                        CharSequence text = "Connect to internet and Retry ";
                        int duration = Toast.LENGTH_SHORT;

                        Toast toast = Toast.makeText(context, text, duration);
                        toast.show();
                    }
                }
            });
        }
    }

    private void intializeContactsFilter() {
        setContentView(R.layout.activity_filter_contacts);
        listView=(ListView)findViewById(R.id.filtercontacts_list_view);
        try {
            loadData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void loadData() throws JSONException {
        if (!NetworkUtils.isInternetAvailable(getApplicationContext())) {
            Toast.makeText(getApplicationContext(),R.string.device_offline,Toast.LENGTH_SHORT).show();
             return;
        }
        JSONObject postJson = JsonHelperTimeline.FilterContacts.JsonCreator.sendAllFriendsOnApp();
        Log.d(TAG,postJson.toString());

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                AppConfig.MAIN_URL, postJson, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                LOGD(TAG, "Response: " + response.toString());
                ArrayList<ContactsData> contactsFilterList;
                if (response != null) {
                    try {
                        contactsFilterList=JsonHelperTimeline.FilterContacts.JsonParsor.parseSendAllFriendsOnApp(response,getApplicationContext());
                        setData(contactsFilterList);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }


            }


        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                 Toast.makeText(getApplicationContext(), "Unable to load timeline ", Toast.LENGTH_LONG).show();
                //VolleyLog.d(TAG, "Error: " + error.getMessage());
                LOGD(TAG, "Error: " + error.getMessage());

            }
        });

        AppController.getInstance().addToRequestQueue(jsonReq);

    }
    private void setData(ArrayList<ContactsData> contactsFilterList){
        MyContactsFilterAdapter myContactsFilterAdapter=new MyContactsFilterAdapter(contactsFilterList,getApplicationContext());
        listView.setAdapter(myContactsFilterAdapter);
        for(int i=0;i<contactsFilterList.size();i++)
        Log.d(TAG,contactsFilterList.get(i).getContactName());

    }



}
