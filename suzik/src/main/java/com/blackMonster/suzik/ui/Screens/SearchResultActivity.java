package com.blackMonster.suzik.ui.Screens;
import static com.blackMonster.suzik.util.LogUtils.LOGD;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.blackMonster.suzik.R;
import com.blackMonster.suzik.sync.music.InAapSongTable;

/**
 * Created by akshanshsingh on 05/01/15.
 */

//TODO OnBackPressed is complete jugad, fix it.
//TODO Image is reloading on keyboard hide and show, which causes flicker. (fix it)




public class SearchResultActivity extends ActionBarActivity implements SearchView.OnQueryTextListener{
public static final String TAG = "SearchReslultActivity";
    SearchResultFragment fragment;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        LOGD(TAG,"oncreate");
        super.onCreate(savedInstanceState);
        getSupportActionBar().setTitle("");
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.main_activity);



//        handleIntent(getIntent());

        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
         fragment = new SearchResultFragment();
        fragmentTransaction.add(R.id.mainLL, fragment);
        fragmentTransaction.commit();
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        LOGD(TAG,"oncreateOptionsmenu");

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.search);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(menuItem);
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        searchView.setOnQueryTextListener(this);
        searchView.setIconifiedByDefault(false);
        searchView.setIconified(false);
        MenuItemCompat.setOnActionExpandListener(menuItem, new MenuItemCompat.OnActionExpandListener(){
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                finish();
                return true;
            }

            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }
        });

        menuItem.expandActionView();
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String s) {
        LOGD(TAG,s);
        return true;
    }

    @Override
    public boolean onQueryTextChange(String s) {
        LOGD(TAG,"change " + s);
        if (s.length() <=1 ) {
            fragment.setData(null,null);
            return true;
        }


        Cursor inapp = InAapSongTable.getAllDataCursorLike(s,this);
        Cursor android = getFromAndroid(s);
        fragment.setData(android,inapp);

        return false;
    }

    private Cursor getFromAndroid(String s) {

        Uri URI = android.provider.MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;

        String selection = MediaStore.Audio.Media.IS_MUSIC + " != 0 and " +  MediaStore.Audio.Media.TITLE + " LIKE '%" + s + "%'";
        final String[] projection = new String[] { MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST,
                MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.DURATION,
                MediaStore.Audio.Media.DATA,MediaStore.Audio.Media.DISPLAY_NAME ,  MediaStore.Audio.Media._ID,
                MediaStore.Audio.Media.ALBUM_ID };


        final String sortOrder = MediaStore.Audio.AudioColumns.TITLE
                + " COLLATE LOCALIZED ASC";

        return getContentResolver().query(URI, projection,
                selection, null, sortOrder);
    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        finish();
//    }


}
