package com.blackMonster.suzik.ui;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.sync.music.SongsSyncer;

public class MainActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//new DbHelper(this.getApplicationContext());
		new Thread() {
			public void run() {
				try {
					SongsSyncer.startSync(getBaseContext().getApplicationContext());
				} catch (Exception e) {
					e.printStackTrace();
				}
			}

		}.start();
		
		
		
		
		//postJson(ContactJsonHandler.getJSON());
		
		
		/*
		if (MainPrefs.getMyNo(this).equals("123"))
			startActivity(new Intent(this, ActivitySignup.class));
		else
			startActivity(new Intent(this, ActivityFriends.class));
		finish();*/
	}
	
void postJson(JSONObject postJson) {
		
		final ProgressDialog pDialog = new ProgressDialog(this);
		pDialog.setMessage("posting...");
		pDialog.show();  

		// Post params to be sent to the server
	JsonObjectRequest req = new JsonObjectRequest(AppConfig.MAIN_URL,postJson, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject response) {
				try {
					VolleyLog.v("Response:%n %s", response.toString(4));
					Toast.makeText(getBaseContext(), "Done!! :  " + response.toString(4),
							Toast.LENGTH_LONG).show();
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				
				pDialog.dismiss();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.e("Error: ", error.getMessage());
				Toast.makeText(getBaseContext(), "Error!! :  " + error.getMessage(),
						Toast.LENGTH_LONG).show();
				pDialog.dismiss();
			}
		});

		// add the request object to the queue to be executed
		AppController.getInstance().addToRequestQueue(req);
	}

	
	
}
