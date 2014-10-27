package com.blackMonster.suzik.sync.contacts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.sync.contacts.model.Contact;
import com.blackMonster.suzik.sync.contacts.model.ContactChanges;

class ServerHelper {

	static HashSet<Contact> getMyContacts() throws JSONException,
			InterruptedException, ExecutionException {
		if (true)  return new HashSet<Contact>();
		JSONObject credintials = JsonHelper.ServerAllContacts.getCredentials();

		RequestFuture<JSONObject> future = RequestFuture.newFuture();
		JsonObjectRequest request = new JsonObjectRequest(AppConfig.MAIN_URL,
				credintials, future, future);
		AppController.getInstance().addToRequestQueue(request);

		JSONObject response = future.get();
		HashSet<Contact> parsedResponse = JsonHelper.ServerAllContacts
				.parseResponse(response);
		return parsedResponse;
	}

	static HashMap<Contact, Integer> updateServer(
			HashSet<ContactChanges> contactChanges) throws JSONException,
			InterruptedException, ExecutionException {
		//if (true)  return getDummyResult(contactChanges);
		JSONObject updates = JsonHelper.UpdateContacts.toJson(contactChanges);

		RequestFuture<JSONObject> future = RequestFuture.newFuture();
		JsonObjectRequest request = new JsonObjectRequest(AppConfig.MAIN_URL,
				updates, future, future);
		AppController.getInstance().addToRequestQueue(request);

		JSONObject response = future.get();
		Log.d("ServerHelper", "update response : " + response.toString());
		return JsonHelper.UpdateContacts.parseResponse(response);

	}

	private static HashMap<Contact, Integer> getDummyResult(HashSet<ContactChanges> contactChanges) {
		HashMap<Contact, Integer> result = new HashMap<Contact, Integer>();
		int i=0;
		for (ContactChanges contact : contactChanges) {
			if (++i == 5)
				result.put(contact.getContact(), JsonHelper.UpdateContacts.STATUS_FAILED);
			else
				result.put(contact.getContact(), JsonHelper.UpdateContacts.STATUS_DONE);
		}		
		return result;
	}

}
