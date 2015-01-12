package com.blackMonster.suzik.util;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Patterns;

public class NetworkUtils {
	
	public static boolean isInternetAvailable(Context context) {
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		return (networkInfo != null && networkInfo.isConnected());
	}

    public static boolean isValidUrl(String url) {

        return url != null && Patterns.WEB_URL.matcher(url).matches();

    }
}
