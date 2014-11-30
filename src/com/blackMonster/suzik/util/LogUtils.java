package com.blackMonster.suzik.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;

import android.util.Log;

import com.blackMonster.suzik.AppConfig;

public class LogUtils {

	public static void LOGD(final String tag, String message) {
       if (AppConfig.DEBUG)
		Log.d(tag, message);
       log(tag, message);
       //Crashlytics.log(Log.DEBUG, tag, message);
    }
	
	public static void LOGV(final String tag, String message) {
        Log.v(tag, message);
    }
	
	public static void LOGI(final String tag, String message) {
        Log.i(tag, message);
    }
	
	public static void LOGW(final String tag, String message) {
        Log.w(tag, message);
    }
	
	public static void LOGE(final String tag, String message) {
        Log.e(tag, message);
        log(tag, message);
       // Crashlytics.log(Log.ERROR, tag, message);


    }
	
	
	public static void log(String TAG, String text) {

		File logFile = new File("sdcard/suziklog.txt");
		if (!logFile.exists()) {
			try {
				logFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		try {
			// BufferedWriter for performance, true to set append to file flag
			BufferedWriter buf = new BufferedWriter(new FileWriter(logFile,
					true));
			String mydate = java.text.DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
			buf.append(mydate + "     " + TAG + " --->  " + text);
			//Log.d(TAG, text);
			buf.newLine();
			buf.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
