package com.blackMonster.suzik;

import java.util.List;

import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract.PhoneLookup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blackMonster.musicstore.Database;

public class ActivityFriends extends ListActivity {
	MyAdapter adapter;
	List<String> noList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// setContentView(R.layout.activity_friends);

		noList = Database.TableNOSongID.getAllPhoneNo(this);
		if (noList.size() == 0) return;
		adapter = new MyAdapter(this, noList);
		setListAdapter(adapter);

		getListView().setOnItemClickListener(
				new android.widget.AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view,
							int position, long id) {
						Log.d("Actigityfriends", "onItemClick");
						startActivity(new Intent(getBaseContext(),
								ActivitySongsList.class).putExtra(
								ActivitySongsList.U_ID, noList.get(position)));
					}
				});

	}

	private class MyAdapter extends ArrayAdapter<String> {
		Context context;
		List<String> values;

		public MyAdapter(Context context, List<String> objects) {
			super(context, R.layout.activity_friends, objects);
			this.context = context;
			values = objects;

		}

		public void updateDataset(List<String> list) {
			values = list;
			this.notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return values.size();
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			View rowView = inflater.inflate(R.layout.activity_friends, parent,
					false);
			String tmp = values.get(position);
			tmp = getContactName(getBaseContext(), tmp);
			if (tmp == null)
				tmp = "";
			((TextView) rowView.findViewById(R.id.act_friends_name))
					.setText(tmp);
			return rowView;
		}
	}

	public static String getContactName(Context context, String phoneNumber) {
		ContentResolver cr = context.getContentResolver();
		Uri uri = Uri.withAppendedPath(PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phoneNumber));
		Cursor cursor = cr.query(uri,
				new String[] { PhoneLookup.DISPLAY_NAME }, null, null, null);
		if (cursor == null) {
			return null;
		}
		String contactName = null;
		if (cursor.moveToFirst()) {
			contactName = cursor.getString(cursor
					.getColumnIndex(PhoneLookup.DISPLAY_NAME));
		}

		if (cursor != null && !cursor.isClosed()) {
			cursor.close();
		}

		return contactName;
	}
	/*
	 * @Override public void onItemClick(AdapterView<?> arg0, View arg1, int
	 * position, long arg3) { Log.d("Actigityfriends", "onItemClick");
	 * startActivity(new Intent(getBaseContext(), ActivitySongsList.class)
	 * .putExtra(ActivitySongsList.U_ID, noList.get(position)));
	 * 
	 * }
	 */

}
