package com.blackMonster.suzik;

import java.util.List;

import android.app.ListActivity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.blackMonster.musicstore.FetchFriendsMusic.MusicInfo;
import com.blackMonster.musicstore.MusicStoreManager;

public class ActivitySongsList extends ListActivity {
	public static final String U_ID = "uid";
	MyAdapter adapter;
	String uid;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		

		Bundle extra = getIntent().getExtras();
		uid = extra.getString(U_ID);

		List<MusicInfo> songsList = MusicStoreManager.getSongsListOfFriend(uid,
				this);
		adapter = new MyAdapter(this, songsList);
		setListAdapter(adapter);

	}

	private class MyAdapter extends ArrayAdapter<MusicInfo> {
		Context context;
		List<MusicInfo> values;

		public MyAdapter(Context context, List<MusicInfo> objects) {
			super(context, R.layout.activity_songs_list, objects);
			this.context = context;
			values = objects;

		}

		public void updateDataset(List<MusicInfo> list) {
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
			View rowView = inflater.inflate(R.layout.activity_songs_list,
					parent, false);

			MusicInfo mi = values.get(position);

			((TextView) rowView.findViewById(R.id.act_songs_list_song_name))
					.setText(mi.title);
			((TextView) rowView.findViewById(R.id.act_songs_list_artist))
					.setText(mi.artist);
			return rowView;
		}
	}

}
