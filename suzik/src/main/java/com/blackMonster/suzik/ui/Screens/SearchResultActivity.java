package com.blackMonster.suzik.ui.Screens;
import static com.blackMonster.suzik.util.LogUtils.LOGD;
import android.app.SearchManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by akshanshsingh on 05/01/15.
 */
public class SearchResultActivity extends ActionBarActivity {
public static final String TAG = "SearchReslultActivity";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        LOGD(TAG,"oncreate");
        super.onCreate(savedInstanceState);

        handleIntent(getIntent());
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        LOGD(TAG,"handleintent");
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            LOGD(TAG,query);






            //use the query to search your data somehow
        }
    }
}
