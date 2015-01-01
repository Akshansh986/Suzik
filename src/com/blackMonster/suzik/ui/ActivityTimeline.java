package com.blackMonster.suzik.ui;

import java.util.ArrayList;
import static com.blackMonster.suzik.util.LogUtils.LOGD;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.R;
import com.blackMonster.suzik.musicstore.Timeline.JsonHelperTimeline;
import com.blackMonster.suzik.musicstore.Timeline.TimelineItem;
import com.blackMonster.suzik.sync.ContentObserverService;

public class ActivityTimeline extends Activity {
	private static final String TAG = "ActivityTimeline";

	ListView listView;
	List<TimelineItem> timelineItems;
	TimelineAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_timeline);
		startService(new Intent(this, ContentObserverService.class));
		//startService(new Intent(this, InitMusicDb.class));
		//startService(new Intent(this, ContactsSyncer.class));
		
		listView = (ListView) findViewById(R.id.timeline_list);
		timelineItems = new ArrayList<TimelineItem>();
		

		/*listView.setOnScrollListener(new EndlessScrollListener() {

			@Override
			public void onLoadMore(int page, int totalItemsCount) {
				Log.d(TAG, "onLoadMore called     page : " + page
						+ " totalItemsCount " + totalItemsCount);
				 fetchMoreData();

			}

		});*/
		
		
		try {
			adapter = new TimelineAdapter(this, timelineItems);
			listView.setAdapter(adapter);
			loadData();
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	private void loadData() throws JSONException {

		
		final ProgressDialog pDialog = new ProgressDialog(this);
		pDialog.setMessage("requestin timeline...");
		pDialog.show();  
		
//				JSONObject postJson = JsonHelperTimeline.getCredentials();
		JSONObject postJson = JsonHelperTimeline.ServerAllSongs.getCredentials();
		
		JsonObjectRequest jsonReq = new JsonObjectRequest(Method.POST,
				"https://dl.dropboxusercontent.com/u/95984737/json.txt", postJson, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						LOGD(TAG, "Response: " + response.toString());
						if (response != null) {
						
							try {
								timelineItems = JsonHelperTimeline.ServerAllSongs.parseTimelineItems(response);
//								timelineItems = JsonHelperTimeline.parseTimelineItems(response);
								adapter.setData(timelineItems);
								adapter.notifyDataSetChanged();
							
								if (timelineItems.isEmpty())  {
									new AlertDialog.Builder(ActivityTimeline.this)
								      .setMessage(R.string.emptpy_timeline)
								    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								        public void onClick(DialogInterface dialog, int which) { 
								            // continue with delete
								        }
								     }).show();
								}
								
							} catch (JSONException e) {
								Toast.makeText(getBaseContext(), "Unexpected response from server", Toast.LENGTH_LONG).show();
								e.printStackTrace();
							}

						}
						

						
						pDialog.dismiss();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getBaseContext(), "Unable to load timeline", Toast.LENGTH_LONG).show();
						VolleyLog.d(TAG, "Error: " + error.getMessage());
						Log.d(TAG, "Error: " + error.getMessage());

					}
				});

		AppController.getInstance().addToRequestQueue(jsonReq);

	}

		
}
