package com.blackMonster.suzik.musicstore.infoFromOtherPlayers.testing;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.R;
import com.blackMonster.suzik.musicstore.infoFromOtherPlayers.BroadcastSong;

public class ActivityAllPlayed extends Activity implements
		OnItemClickListener {
	Cursor cursor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		DbHelper.getInstance(this);
		
		setContentView(R.layout.test_activity);

		ListView listView = (ListView) findViewById(R.id.songList);
		cursor = TableAllPlayed.getAllRows(this);
		if (cursor == null)
			return;
		CustomCursorAdapter cursorAdapter = new CustomCursorAdapter(this,
				cursor);
		listView.setAdapter(cursorAdapter);
		listView.setOnItemClickListener(this);

	}
	public void ButtonAll(View v) {
		startActivity(new Intent(this, ActivityAllPlayed.class));
		finish();
	}
	public void ButtonUser(View v) {
		startActivity(new Intent(this, ActivityUserSelectedCompleted.class));
		finish();
	}
	public void ButtonProgram(View v) {
		startActivity(new Intent(this, ActivityProgramSelectedCompleted.class));
		finish();
	}
	

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position,
			long id) {
		// cursor.moveToFirst();
		// cursor.move(position);
		cursor.moveToPosition(position);
		String track = cursor.getString(cursor
				.getColumnIndex(TableAllPlayed.C_TRACK));
		TableUserSelectedCompleted.insert(
				new BroadcastSong(cursor.getLong(cursor
						.getColumnIndex(TableAllPlayed.C_ID)), track, cursor
						.getString(cursor
								.getColumnIndex(TableAllPlayed.C_ARTIST)),
						cursor.getLong(cursor
								.getColumnIndex(TableAllPlayed.C_DURATION)),
						cursor.getLong(cursor
								.getColumnIndex(TableAllPlayed.C_STREAMING))),
				cursor.getLong(cursor
						.getColumnIndex(TableAllPlayed.C_COMPLETED_TS)), this);
		Toast.makeText(this, track + "  copied to user selected",
				Toast.LENGTH_SHORT).show();
	}

	public class CustomCursorAdapter extends CursorAdapter {

		public CustomCursorAdapter(Context context, Cursor c) {
			super(context, c);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			// when the view will be created for first time,
			// we need to tell the adapters, how each item will look
			LayoutInflater inflater = LayoutInflater.from(parent.getContext());
			View retView = inflater.inflate(R.layout.all_played_row, parent,
					false);

			return retView;
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			// here we are setting our data
			// that means, take the data from the cursor and put it in views

			// setLT(view, cursor);

			((TextView) view.findViewById(R.id.allPlayed_songname))
					.setText(cursor.getString(cursor
							.getColumnIndex(TableAllPlayed.C_TRACK)));
			
			 SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");

		        Date resultdate = new Date(cursor.getLong(cursor
						.getColumnIndex(TableAllPlayed.C_COMPLETED_TS)));
		        String time = sdf.format(resultdate);
			
			
			((TextView) view.findViewById(R.id.allPlayed_time))
			.setText(time);


		}

		/*
		 * private void setTuteMarker(View view, Cursor cursor, int color) { if
		 * (!isLab) if (cursor.getString(
		 * cursor.getColumnIndex(DetailedAttendenceTable.C_LTP))
		 * .equals("Tutorial")){ ((TextView) view
		 * .findViewById(R.id.detailed_atnd_tute_marker
		 * )).setVisibility(View.VISIBLE); ((TextView) view
		 * .findViewById(R.id.detailed_atnd_tute_marker))
		 * .setBackgroundColor(color); } else ((TextView) view
		 * .findViewById(R.id
		 * .detailed_atnd_tute_marker)).setVisibility(View.INVISIBLE);
		 * 
		 * }
		 */

	}

}
