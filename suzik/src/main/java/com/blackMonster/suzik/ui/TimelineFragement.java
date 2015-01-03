package com.blackMonster.suzik.ui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
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

public class TimelineFragement extends Fragment implements OnItemClickListener{
	private static final String TAG = "ActivityTimeline";

	ListView listView;
	List<TimelineItem> timelineItems;
	TimelineAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.list_view,
                container, false);

        getActivity().startService(new Intent(getActivity(), ContentObserverService.class));

        listView = (ListView) rootView.findViewById(R.id.timeline_list);
        timelineItems = new ArrayList<TimelineItem>();
        listView.setOnItemClickListener(this);


        try {
            adapter = new TimelineAdapter(getActivity(), timelineItems);
            listView.setAdapter(adapter);
            loadData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rootView;


    }

//    @Override
//	protected void onCreate(Bundle savedInstanceState)  {
//		super.onCreate(savedInstanceState);
//		setContentView(R.layout.list_view);
//		startService(new Intent(this, ContentObserverService.class));
//		//startService(new Intent(this, InitMusicDb.class));
//		//startService(new Intent(this, ContactsSyncer.class));
//
//		listView = (ListView) findViewById(R.id.timeline_list);
//		timelineItems = new ArrayList<TimelineItem>();
//		listView.setOnItemClickListener(this);
//
//
//
//		/*listView.setOnScrollListener(new EndlessScrollListener() {
//
//			@Override
//			public void onLoadMore(int page, int totalItemsCount) {
//				Log.d(TAG, "onLoadMore called     page : " + page
//						+ " totalItemsCount " + totalItemsCount);
//				 fetchMoreData();
//
//			}
//
//		});*/
//
//
//		try {
//			adapter = new TimelineAdapter(this, timelineItems);
//			listView.setAdapter(adapter);
//			loadData();
//		} catch (JSONException e) {
//			e.printStackTrace();
//		}
//	}

	private void loadData() throws JSONException {

		
		final ProgressDialog pDialog = new ProgressDialog(getActivity());
		pDialog.setMessage("requestin timeline...");
		pDialog.show();  
		
				JSONObject postJson = JsonHelperTimeline.getCredentials();
//		JSONObject postJson = JsonHelperTimeline.ServerAllSongs.getCredentials();
		
		JsonObjectRequest jsonReq = new JsonObjectRequest(Method.POST,
				AppConfig.MAIN_URL, postJson, new Response.Listener<JSONObject>() {

					@Override
					public void onResponse(JSONObject response) {
						Log.d(TAG, "Response: " + response.toString());
						if (response != null) {
						
							try {
//								timelineItems = JsonHelperTimeline.ServerAllSongs.parseTimelineItems(response);
								timelineItems = JsonHelperTimeline.parseTimelineItems(response);
								adapter.setData(timelineItems);
								adapter.notifyDataSetChanged();
							
								if (timelineItems.isEmpty())  {
									new AlertDialog.Builder(getActivity())
								      .setMessage(R.string.emptpy_timeline)
								    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
								        public void onClick(DialogInterface dialog, int which) { 
								            // continue with delete
								        }
								     }).show();
								}
								
							} catch (JSONException e) {
								Toast.makeText(getActivity().getBaseContext(), "Unexpected response from server", Toast.LENGTH_LONG).show();
								e.printStackTrace();
							}

						}
						

						
						pDialog.dismiss();
					}
				}, new Response.ErrorListener() {

					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(getActivity().getBaseContext(), "Unable to load timeline", Toast.LENGTH_LONG).show();
						VolleyLog.d(TAG, "Error: " + error.getMessage());
						Log.d(TAG, "Error: " + error.getMessage());

					}
				});

		AppController.getInstance().addToRequestQueue(jsonReq);

	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1,final int position, long arg3) {
		
		Log.d(TAG,"fsdf " + position + timelineItems.get(position).getSongUrl());
		

		new Thread() {
			public void run() {
				try {
					play( timelineItems.get(position).getSongUrl());
				} catch (Exception e) {
				}
			}

		}.start();

		
		
		
		
		
	}
	
	
	void play(String url) {
		MediaPlayer mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		try {
			mediaPlayer.setDataSource(url);
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			mediaPlayer.prepare();
		} catch (IllegalStateException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} // might take long! (for buffering, etc)
		mediaPlayer.start();
		
	}

		
}
