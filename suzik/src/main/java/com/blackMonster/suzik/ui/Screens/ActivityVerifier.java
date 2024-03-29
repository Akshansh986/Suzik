package com.blackMonster.suzik.ui.Screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.R;
import com.blackMonster.suzik.sync.Syncer;
import com.blackMonster.suzik.sync.contacts.ContactsSyncer;
import com.blackMonster.suzik.sync.music.InitMusicDb;
import com.blackMonster.suzik.sync.music.SongsSyncer;
import com.blackMonster.suzik.util.NetworkUtils;
import com.blackMonster.suzik.util.UiUtils;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

public class ActivityVerifier extends Activity {
    private static final String TAG = "ActivitySignup";
    private static final int RUNNING = 1;
    private static final int STOPPED = 0;
    private static final int COMPLETED = 2;


    AlertDialog dialog;

    int[] status = new int[2];
    boolean finalResult;

    private void resetGlobals() {
        status[0] = STOPPED;
        status[1] = STOPPED;
        finalResult = true;
        dialog = null;
    }

    String verificationCode;
    String myNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LOGD(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        myNumber = getIntent().getStringExtra(ActivitySighnup.MY_NUMBER);
        verificationCode = getIntent().getStringExtra(ActivitySighnup.VERIFICATION_CODE);
        setContentView(R.layout.activity_verifier);
        resetGlobals();
        registerReceivers();

        if (verificationCode == null) onSuccessfulRegistration(myNumber);
    }

    public void buttonSubmit(View v) {
        String vc;
        LOGD(TAG, "buttonSubmit");

        vc = ((EditText) findViewById(R.id.verification_code))
                .getEditableText().toString().trim();
        if (vc.equals(verificationCode)) {
            onSuccessfulRegistration(myNumber);
            ((TextView) findViewById(R.id.errorText)).setVisibility(View.INVISIBLE);
        } else {
            ((TextView) findViewById(R.id.errorText)).setVisibility(View.VISIBLE);
        }
        hideKeyboard();
    }


    private void onSuccessfulRegistration(String number) {

        if (!NetworkUtils.isInternetAvailable(this)) {
            Toast.makeText(this, R.string.device_offline, Toast.LENGTH_SHORT).show();
            return;
        }

        clearDatabaseAndPrefs();
        MainPrefs.setMyNo(number, getApplicationContext());

        dialog = UiUtils.createProgressDialog(getString(R.string.logging_in), this);
        dialog.show();

        startService(new Intent(this, InitMusicDb.class));
        status[0] = RUNNING;

        startService(new Intent(getBaseContext(), ContactsSyncer.class).
                putExtra(Syncer.SHOULD_RETRY, false));
        status[1] = RUNNING;
    }


    private BroadcastReceiver broadcastInitMusicDbResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LOGD(TAG, "received : broadcastInitMusicDbResult");

            boolean result = intent.getExtras().getBoolean(
                    InitMusicDb.BROADCAST_INIT_MUSIC_DB_RESULT);

            status[0] = COMPLETED;
            finalResult = finalResult && result;
            onTaskComplete();
        }
    };

    private BroadcastReceiver broadcastContactsSyncResult = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            LOGD(TAG, "received : broadcastContactsSyncResult");

            boolean result = intent.getExtras().getInt(
                    ContactsSyncer.BROADCAST_CONTACTS_SYNC_RESULT) == Syncer.STATUS_OK;

            status[1] = COMPLETED;
            finalResult = finalResult && result;
            onTaskComplete();
        }


    };


    void onTaskComplete() {

        if (status[0] == COMPLETED && status[1] == COMPLETED) {

            if (dialog != null)
                dialog.dismiss();
            dialog = null;

            if (finalResult) {
                MainPrefs.setLoginDone(getBaseContext());
                startActivity(new Intent(getBaseContext(),
                        MainSliderActivity.class));
                Syncer.callFuture(SongsSyncer.class, 10000, getBaseContext());
                finish();

            } else {
                Toast.makeText(getBaseContext(), R.string.loggingIn_error,
                        Toast.LENGTH_LONG).show();
                resetGlobals();
                clearDatabaseAndPrefs();
            }
        }
    }

    void clearDatabaseAndPrefs() {
        DbHelper.shutDown();
        if (deleteDatabase(DbHelper.DB_NAME))
            LOGD(TAG, "old database deleted");
        MainPrefs.clearAll(this);
    }


    protected void onDestroy() {
        super.onDestroy();
        unregisterReceivers();
    }


    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focus = getCurrentFocus();
        if (focus != null)
            inputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
    }


    private void unregisterReceivers() {
        LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
                broadcastContactsSyncResult);

        LocalBroadcastManager.getInstance(getBaseContext()).unregisterReceiver(
                broadcastInitMusicDbResult);
    }

    private void registerReceivers() {
        LocalBroadcastManager.getInstance(this)
                .registerReceiver(
                        broadcastContactsSyncResult,
                        new IntentFilter(
                                ContactsSyncer.BROADCAST_CONTACTS_SYNC_RESULT));

        LocalBroadcastManager.getInstance(this).registerReceiver(
                broadcastInitMusicDbResult,
                new IntentFilter(InitMusicDb.BROADCAST_INIT_MUSIC_DB_RESULT));

    }

}
