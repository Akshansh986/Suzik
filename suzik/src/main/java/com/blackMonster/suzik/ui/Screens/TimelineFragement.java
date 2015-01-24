package com.blackMonster.suzik.ui.Screens;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
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
import com.blackMonster.suzik.AppController;
import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.R;
import com.blackMonster.suzik.musicPlayer.UIcontroller;
import com.blackMonster.suzik.musicstore.Timeline.JsonHelperTimeline;
import com.blackMonster.suzik.musicstore.Timeline.TimelineItem;
import com.blackMonster.suzik.sync.ContentObserverService;
import com.blackMonster.suzik.ui.TimelineAdapter;

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

        uiController=UIcontroller.getInstance(getActivity());


        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.timeline_view,
                container, false);


        initSwipeRefreshLayout(rootView);

        getActivity().startService(new Intent(getActivity(), ContentObserverService.class));

        listView = (ListView) rootView.findViewById(R.id.timeline_list_view);
        timelineItems = new ArrayList<TimelineItem>();
        listView.setOnItemClickListener(this);


        try {
            adapter = new TimelineAdapter(getActivity(), timelineItems,getActivity());
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
            LOGD(TAG,(new JSONObject(data)).toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    private void loadData() throws JSONException {

        JSONObject postJson = JsonHelperTimeline.getCredentials();
//		JSONObject postJson = JsonHelperTimeline.ServerAllSongs.getCredentials();

        JsonObjectRequest jsonReq = new JsonObjectRequest(Method.POST,
                /*AppConfig.MAIN_URL*/"https://dl.dropboxusercontent.com/u/95984737/akki.txt", postJson, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, "Response: " + response.toString());
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
                swipeLayout.setRefreshing(false);
                Toast.makeText(getActivity().getBaseContext(), "Unable to load timeline", Toast.LENGTH_LONG).show();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Log.d(TAG, "Error: " + error.getMessage());

            }
        });
        jsonReq.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(jsonReq);

    }


    private void setData(JSONObject response) throws JSONException {
//        timelineItems = JsonHelperTimeline.ServerAllSongs.parseTimelineItems(response,getActivity());

        timelineItems = JsonHelperTimeline.parseTimelineItems(response,getActivity());
        adapter.setData(timelineItems);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View view, final int position, long arg3) {

        Log.d(TAG, "fsdf " + position +  adapter.getPlayable(position).getSongPath());

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


}
