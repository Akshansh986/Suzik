package com.blackMonster.suzik.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.ui.Screens.MainSliderActivity;
import com.blackMonster.suzik.util.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.blackMonster.suzik.util.LogUtils.LOGD;
import static com.blackMonster.suzik.util.LogUtils.LOGE;

/**
 * Created by akshanshsingh on 13/01/15.
 */
public class AppUpdateNotificaiton {
    public static final String TAG = "AppUpdateNotification";
    public static final String URL = "https://dl.dropboxusercontent.com/u/95984737/suzikVersion.txt";

    public static final String P_VERSION = "version";
    public static final String P_LINK = "link";

   long downloadRef;

    public void showAppUpdateDialogIfNecessary(final MainSliderActivity activity)  {
        if (!NetworkUtils.isInternetAvailable(activity)) return;

        JsonObjectRequest jsonReq = new JsonObjectRequest(Request.Method.POST,
                URL, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                LOGD(TAG, "Response: " + response.toString());

                if (response != null) {

                    try {
                        if (checkNewVersion(response.getInt(P_VERSION), activity))
                            if (MainSliderActivity.isVisible)
                                showDialog(response.getString(P_LINK),response.getInt(P_VERSION), activity);

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
//                VolleyLog.d(TAG, "Error: " + error.getMessage());
                LOGD(TAG, "Error: " + error.getMessage());

            }
        });
        jsonReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonReq);

    }


    private BroadcastReceiver downloadReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

            //check if the broadcast message is for our Enqueued download
            long referenceId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (referenceId != downloadRef ) return;
            context.unregisterReceiver(this);
            DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            Uri newApkUri = manager.getUriForDownloadedFile(referenceId);

            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            i.setDataAndType(newApkUri, "application/vnd.android.package-archive" );
            Log.d("Lofting", "About to install new .apk");
            context.startActivity(i);

            }};

    private  void showDialog(final String url, final int version, final Activity activity) {
        if (!NetworkUtils.isValidUrl(url)) {
            LOGE(TAG,"invalid url");
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setMessage("A new version of suzik will be downloaded.").
                setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        downloadRef = downloadApk("Suzik",url,"suzik"+version ,activity);

                        //set filter to only when download is complete and register broadcast receiver
                        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
                        activity.registerReceiver(downloadReceiver, filter);       }
                });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();


    }

    private  boolean checkNewVersion(int newVersion, Context context) throws PackageManager.NameNotFoundException {

        PackageInfo pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        int currentVersion = pInfo.versionCode;

        return newVersion > currentVersion;

    }


    public  long downloadApk(String title, String url, String fileName, Context context){

        LOGD(TAG, "download url : " + url);

        if (!NetworkUtils.isValidUrl(url)) return 0;

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setTitle(title);

        request.setDestinationInExternalFilesDir(context, null, fileName);

        DownloadManager manager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        return manager.enqueue(request);

    }


}
