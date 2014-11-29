package com.blackMonster.suzik;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

public class MainStaticElements {

	public static AlertDialog createProgressDialog(String msg,Activity activity) {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		View myView = activity.getLayoutInflater().inflate(R.layout.loading_progressbar,
				null);
		((TextView) myView.findViewById(R.id.loading_dialog_msg)).setText(msg);

		builder.setView(myView);
		builder.setCancelable(false);

		return builder.create();
	}
	
	
	
}
