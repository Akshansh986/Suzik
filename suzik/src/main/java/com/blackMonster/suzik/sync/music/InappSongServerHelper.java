package com.blackMonster.suzik.sync.music;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.RequestFuture;
import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.AppController;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

/**
 * Created by akshanshsingh on 29/01/15.
 */
public class InappSongServerHelper {
    public static final String TAG = "InappSongServerHelper";

    public static final int VOLLEY_TIMEOUT = 30000; //MS
    private static final int RETRY_LIMIT = 4;


    public static boolean addToServer(long id, String fp) throws JSONException, ExecutionException, InterruptedException {
        LOGD(TAG,"postUserActivity");
        //if (true) return dummyResponse(postParams);
        JSONObject postJson = JsonHelper.InappSong.toJson(id,fp);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(AppConfig.MAIN_URL,
                postJson, future, future);

        request.setRetryPolicy(new DefaultRetryPolicy(VOLLEY_TIMEOUT,RETRY_LIMIT,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);

        JSONObject response = future.get();
        LOGD(TAG,"response " + response.toString());
        return JsonHelper.InappSong.parseResponse(response);


    }

    public static void deleteFromServer(long id) throws InterruptedException, ExecutionException, JSONException {
        List<Long>  list = new ArrayList<Long>();
        list.add(id);


        JSONObject deletedSongs = JsonHelper.DeletedSong.toJson(list);

        RequestFuture<JSONObject> future = RequestFuture.newFuture();
        JsonObjectRequest request = new JsonObjectRequest(AppConfig.MAIN_URL,
                deletedSongs, future, future);
        request.setRetryPolicy(new DefaultRetryPolicy(VOLLEY_TIMEOUT,RETRY_LIMIT,DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        AppController.getInstance().addToRequestQueue(request);

        JSONObject response = future.get();
        LOGD(TAG,"response " + response.toString());
        JsonHelper.DeletedSong.parseResponse(response);

    }


}
