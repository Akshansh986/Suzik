package com.blackMonster.suzik.util;

import android.util.Log;

import com.blackMonster.suzik.AppConfig;
import com.crashlytics.android.Crashlytics;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

public class LogUtils {

	public static void LOGD(final String tag, String message) {
		if (AppConfig.DEBUG) {
			Log.d(tag, message);
			log(tag, message);
		} else 	Crashlytics.log("D/" + tag + " " + message);
	}


	public static void LOGI(final String tag, String message)
    {
        if (AppConfig.DEBUG) {
            Log.i(tag, message);
            log(tag, message);
        } else Crashlytics.log("I/" + tag + " " + message);	}

	public static void LOGW(final String tag, String message) {
        if (AppConfig.DEBUG) {
            Log.w(tag, message);
            log(tag, message);
        } else Crashlytics.log("W/" + tag + " " + message);
	}

	public static void LOGE(final String tag, String message) {
        if (AppConfig.DEBUG) {
            Log.e(tag, message);
            log(tag, message);
        } else  Crashlytics.log("E/" + tag + " " + message);

	}

	public static void log(String TAG, String text) {

		File logFile = new File("sdcard/suziklog.txt");
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			// BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
					true));
			String mydate = java.text.DateFormat.getDateTimeInstance().format(
					Calendar.getInstance().getTime());
			buf.append(mydate + "     " + TAG + " --->  " + text);
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
