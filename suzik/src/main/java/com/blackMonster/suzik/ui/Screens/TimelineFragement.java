package com.blackMonster.suzik.ui.Screens;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.volley.Request.Method;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.blackMonster.suzik.AppConfig;
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.R;
import com.blackMonster.suzik.musicPlayer.MusicPlayerService;
import com.blackMonster.suzik.musicPlayer.UIcontroller;
import com.blackMonster.suzik.musicstore.Timeline.JsonHelperTimeline;
import com.blackMonster.suzik.musicstore.Timeline.TimelineItem;
import com.blackMonster.suzik.sync.ContentObserverService;
import com.blackMonster.suzik.ui.TimelineAdapter;
import com.blackMonster.suzik.ui.UiBroadcasts;
import com.blackMonster.suzik.util.NetworkUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

public class TimelineFragement extends Fragment implements OnItemClickListener, SwipeRefreshLayout.OnRefreshListener {
    private static final String TAG = "ActivityTimeline";
    public static final String FRAGMENT_TAG = "timelineFragementTag";

    ListView listView;
    List<TimelineItem> timelineItems;
    TimelineAdapter adapter;
    SwipeRefreshLayout swipeLayout;

    UIcontroller uiController;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        registerReceivers();
        uiController = UIcontroller.getInstance(getActivity());


        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.timeline_view,
                container, false);


        initSwipeRefreshLayout(rootView);

        getActivity().startService(new Intent(getActivity(), ContentObserverService.class));

        listView = (ListView) rootView.findViewById(R.id.timeline_list_view);
        timelineItems = new ArrayList<TimelineItem>();
        listView.setOnItemClickListener(this);


        try {
            adapter = new TimelineAdapter(getActivity(), timelineItems, getActivity());
            listView.setAdapter(adapter);
            loadInitData();
            setSwipeLayoutRefreshing();
            loadData();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return rootView;


    }

    private void loadInitData() {

        try {
            String data = MainPrefs.getTimelineCache(getActivity());
            if (data.equals("")) return;
            setData(new JSONObject(data));
            LOGD(TAG, (new JSONObject(data)).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void loadData() throws JSONException {
    if (!NetworkUtils.isInternetAvailable(getActivity())) {
        Toast.makeText(getActivity(),R.string.device_offline,Toast.LENGTH_SHORT).show();
        swipeLayout.setRefreshing(false);
        return;
    }
        JSONObject postJson = JsonHelperTimeline.getCredentials();
//		JSONObject postJson = JsonHelperTimeline.ServerAllSongs.getCredentials();

        JsonObjectRequest jsonReq = new JsonObjectRequest(Method.POST,
                AppConfig.MAIN_URL, postJson, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                LOGD(TAG, "Response: " + response.toString());
                if (getActivity() == null) {
                    LOGD(TAG,"on response : activity no longer visible...returning");
                    return;
                }
                swipeLayout.setRefreshing(false);

                if (response != null) {

                    try {
                        setData(response);
                        MainPrefs.setTimelineCache(response.toString(), getActivity());
                        if (timelineItems.isEmpty()) {
                            new AlertDialog.Builder(getActivity())
                                    .setMessage(R.string.emptpy_timeline)
                                    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int which) {
                                            // continue with delete
                                        }
                                    }).show();
                        }


                    } catch (JSONException e) {
                        Toast.makeText(getActivity().getBaseContext(), "Unexpected response from server", Toast.LENGTH_LONG).show();
                        e.printStackTrace();
                    }

                }


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (getActivity() == null) {
                    LOGD(TAG,"on error : activity no longer visible...returning");
                    return;
                }
                swipeLayout.setRefreshing(false);
                Toast.makeText(getActivity().getBaseContext(), "Unable to load timeline ", Toast.LENGTH_LONG).show();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                LOGD(TAG, "Error: " + error.getMessage());

            }
        });
        jsonReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonReq);

    }


    private void setData(JSONObject response) throws JSONException {
//        timelineItems = JsonHelperTimeline.ServerAllSongs.parseTimelineItems(response,getActivity());

        timelineItems = JsonHelperTimeline.parseTimelineItems(response, getActivity());
        adapter.setData(timelineItems);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, final int position, long arg3) {

        LOGD(TAG, "fsdf " + position + adapter.getPlayable(position).getSongPath());
        if (!adapter.getPlayable(position).isOffline() && !NetworkUtils.isInternetAvailable(getActivity())) {
            Toast.makeText(getActivity(),R.string.device_offline,Toast.LENGTH_SHORT).show();
            return;
        }
        uiController.setList(adapter);
        uiController.setSongpos(position);


//        ((CardView) view.findViewById(R.id.card_view)).setCardElevation(200);
//        ((CardView) view.findViewById(R.id.card_view)).setPadding(200, 200, 200, 200);
//
//        ViewGroup.MarginLayoutParams marginParams = new ViewGroup.MarginLayoutParams(((CardView) view.findViewById(R.id.card_view)).getLayoutParams());
//        marginParams.setMargins(60, 60, 60, 60);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(marginParams);
//        ((CardView) view.findViewById(R.id.card_view)).setLayoutParams(layoutParams);


    }


    @Override
    public void onRefresh() {
        try {
            loadData();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void initSwipeRefreshLayout(View view) {
        swipeLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(this);

        swipeLayout.setColorSchemeResources(R.color.primary,
                R.color.primary,
                R.color.accent,
                R.color.accent);
    }

    private void setSwipeLayoutRefreshing() {
        TypedValue typed_value = new TypedValue();
        getActivity().getTheme().resolveAttribute(android.support.v7.appcompat.R.attr.actionBarSize, typed_value, true);
        swipeLayout.setProgressViewOffset(false, 0, getResources().getDimensionPixelSize(typed_value.resourceId));
        swipeLayout.setRefreshing(true);
    }


    private BroadcastReceiver broadcastMusicDataChanged = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LOGD(TAG, "received : broadcastMusicDataChanged");

            //Resetting all local data available in timeline items.
            for (TimelineItem timelineItem : timelineItems) {
                timelineItem.setInappMirrorIfAvailable(getActivity());
            }
        }
    };


    private void unregisterReceivers() {
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
                broadcastMusicDataChanged);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
                broadcastUiDataUpdate);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(
                broadcastBufferingPlayerRecieve);

    }

    private void registerReceivers() {

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(
                broadcastMusicDataChanged,
                new IntentFilter(UiBroadcasts.MUSIC_DATA_CHANGED));

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastUiDataUpdate, new IntentFilter(UIcontroller.brodcast_uidataupdate));
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(broadcastBufferingPlayerRecieve, new IntentFilter(MusicPlayerService.brodcast_bufferingplayer));
    }


    private BroadcastReceiver broadcastUiDataUpdate = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LOGD(TAG,"uiUpdate called");
            adapter.notifyDataSetChanged();

        }
    };
    private BroadcastReceiver broadcastBufferingPlayerRecieve = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LOGD(TAG,"broadcastBufferingPlayerRecieve");
            if (isBuffering(intent)) {
                adapter.isBuffring =true;
                adapter.animateView();
            } else {
                adapter.isBuffring=false;
                adapter.stopAnimation();
            }

        }

        private boolean isBuffering(Intent intent) {
            String buffval = intent.getStringExtra("buffering");
            int bval = Integer.parseInt(buffval);
            return bval == 1;
        }
    };


    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
    }


}
