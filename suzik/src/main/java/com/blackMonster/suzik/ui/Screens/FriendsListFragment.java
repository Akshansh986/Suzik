package com.blackMonster.suzik.ui.Screens;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.blackMonster.suzik.R;


public class FriendsListFragment extends Fragment implements OnItemClickListener{
    private static final String TAG = "MySongListFragement";

    ListView listView;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.list_view,
                container, false);


        listView = (ListView) rootView.findViewById(R.id.list_view);




        String[] values = new String[] { "Sathya",
                "Nikhil",
                "Utkarsh",
                "Himanshu",
                "Aayush",
        };


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_list_item_1, android.R.id.text1, values);


        // Assign adapter to ListView
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(this);
        return rootView;


    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        startActivity(new Intent(getActivity(), FriendSongsActivity.class));

    }
}
