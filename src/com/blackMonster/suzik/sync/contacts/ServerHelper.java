package com.blackMonster.suzik.sync.contacts;

import java.util.HashMap;
import java.util.HashSet;
import java.util.concurrent.ExecutionException;

import org.json.JSONException;
import org.json.JSONObject;

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
		if (true)  return new HashMap<Contact, Integer>();
		JSONObject updates = JsonHelper.UpdateContacts.toJson(contactChanges);

		RequestFuture<JSONObject> future = RequestFuture.newFuture();
		JsonObjectRequest request = new JsonObjectRequest(AppConfig.MAIN_URL,
				updates, future, future);
		AppController.getInstance().addToRequestQueue(request);

		JSONObject response = future.get();
		return JsonHelper.UpdateContacts.parseResponse(response);

	}

}
