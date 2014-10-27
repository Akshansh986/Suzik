package com.blackMonster.suzik.sync.contacts;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.blackMonster.suzik.musicstore.module.Song;
import com.blackMonster.suzik.sync.contacts.model.Contact;
import com.blackMonster.suzik.sync.contacts.model.ContactChanges;
import com.blackMonster.suzik.util.ServerUtils;

class JsonHelper {
	private static final String TAG = "contacts.JsonHelper";

	static class ServerAllContacts {
		private static final String P_MODULE = "contact";
		private static final String P_CMD = "contactsList";

		private static final String P_R_CONTACT_LIST = "contacts";
		private static final String P_R_NUMBER = "num";

		static JSONObject getCredentials() throws JSONException {
			JSONObject root = new JSONObject();
			ServerUtils.addEssentialParamToJson(root, P_MODULE, P_CMD);
			LOGD(TAG, root.toString());
			return root;

		}

		static HashSet<Contact> parseResponse(JSONObject response)
				throws JSONException {

			HashSet<Contact> result = new HashSet<Contact>();

			JSONArray responseArray = response.getJSONArray(P_R_CONTACT_LIST);

			Song song;
			int n = responseArray.length();
			for (int i = 0; i < n; i++) {
				result.add(new Contact((String) responseArray.get(i)));
			}

			return result;

		}

	}

	static class UpdateContacts {

		private static final String P_MODULE = "contact";
		//private static final String P_CMD = "update";

		private static final String P_CONTACT_DATA = "contactsList";
		private static final String P_NUMBER = "number";
		private static final String P_ACTION = "action";

		private static final String P_R_UPDATE_RESPONSE = "contactList";
		private static final String P_R_NUMBER = "number";
		private static final String P_R_STATUS = "status";

		static final int STATUS_DONE = 1;
		static final int STATUS_FAILED = 0;


		static JSONObject toJson(HashSet<ContactChanges> contactList)
				throws JSONException {
			//JSONObject a = new JSONObject("{\"myNumber\":\"2870558803\",\"module\":\"contact\",\"contactsList\":[{\"action\":\"add\",\"number\":\"+919873386899\"},{\"action\":\"add\",\"number\":\"+919540333290\"}]}");
			//LOGD(TAG,a.toString());
			//if (true) return a;
			JSONObject root = new JSONObject();
			JSONArray contactArray = new JSONArray();

			for (ContactChanges contact : contactList) {
				contactArray.put(getSingleObject(contact));
			}

			root.put(P_CONTACT_DATA, contactArray);
			ServerUtils.addEssentialParamToJson(root, P_MODULE);
			LOGD(TAG, root.toString());
			return root;
		}

		private static JSONObject getSingleObject(ContactChanges cChagnes)
				throws JSONException {

			JSONObject obj = new JSONObject();
			obj.put(P_NUMBER, cChagnes.getContact().getNumber().replace("+", "%2B"));
			obj.put(P_ACTION, cChagnes.getActionString());
			return obj;

		}

		static HashMap<Contact, Integer> parseResponse(JSONObject response)
				throws JSONException {

			HashMap<Contact, Integer> contactStatus = new HashMap<Contact, Integer>();
			JSONArray responseArray = response
					.getJSONArray(P_R_UPDATE_RESPONSE);

			for (int i = 0; i < responseArray.length(); i++) {
				JSONObject responseObj = (JSONObject) responseArray.get(i);

				contactStatus.put(
						new Contact(responseObj.getString(P_R_NUMBER)),
						responseObj.getInt(P_R_STATUS));
			}

			return contactStatus;

		}

	}

}
