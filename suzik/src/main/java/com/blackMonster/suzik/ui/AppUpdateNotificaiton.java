package com.blackMonster.suzik.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.R;
import com.blackMonster.suzik.ui.Screens.MainSliderActivity;
import com.blackMonster.suzik.util.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.blackMonster.suzik.util.LogUtils.LOGE;

/**
 * Created by akshanshsingh on 13/01/15.
 */
public class AppUpdateNotificaiton {
    public static final String TAG = "AppUpdateNotification";
    public static final String URL = "https://dl.dropboxusercontent.com/u/95984737/suzikVersion.txt";

    public static final String P_VERSION = "version";
    public static final String P_LINK = "link";


    public static void showAppUpdateDialogIfNecessary(final MainSliderActivity activity)  {
        if (!NetworkUtils.isInternetAvailable(activity)) return; 

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Response: " + response.toString());

                if (response != null) {

                    try {
                        if (checkNewVersion(response.getInt(P_VERSION), activity))
                            if (MainSliderActivity.isVisible)
                                showDialog(response.getString(P_LINK), activity);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }


                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "Error: " + error.getMessage());

            }
        });
        jsonReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonReq);

    }

    private static void showDialog(final String url,final Activity activity) {
        if (!NetworkUtils.isValidUrl(url)) {
            LOGE(TAG,"invalid url");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("App update available").
                setPositiveButton("Update now", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                        activity.startActivity(browserIntent);
                    }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private static boolean checkNewVersion(int newVersion, Context context) throws PackageManager.NameNotFoundException {

        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        int currentVersion = pInfo.versionCode;

        return newVersion > currentVersion;

    }


}
