package com.blackMonster.suzik.ui.Screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.blackMonster.suzik.DbHelper;
import com.blackMonster.suzik.MainPrefs;
import com.blackMonster.suzik.R;
import com.blackMonster.suzik.sync.Syncer;
import com.blackMonster.suzik.sync.contacts.ContactsSyncer;
import com.blackMonster.suzik.sync.music.InitMusicDb;
import com.blackMonster.suzik.sync.music.SongsSyncer;
import com.blackMonster.suzik.util.NetworkUtils;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

public class ActivitySignup extends Activity {
    private static final String TAG = "ActivitySignup";
    private static final int RUNNING = 1;
    private static final int STOPPED = 0;
    private static final int COMPLETED = 2;
    Bitmap bm;

    AlertDialog dialog;

    int[] status = new int[2];
    boolean finalResult;

    private void resetGlobals() {
        status[0] = STOPPED;
        status[1] = STOPPED;
        finalResult = true;
        dialog = null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LOGD(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        resetGlobals();
        registerReceivers();


        Drawable drawable = ((RelativeLayout)findViewById(R.id.RelativeLayout1)).getBackground();

        bm = BitmapFactory.decodeResource(getResources(), R.drawable.photo);



        final RenderScript rs = RenderScript.create(this);
        final Allocation input = Allocation.createFromBitmap( rs, bm, Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT );
        final Allocation output = Allocation.createTyped( rs, input.getType() );
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create( rs, Element.U8_4(rs) );
        script.setRadius( 20.f /* e.g. 3.f */ );
        script.setInput( input );
        script.forEach( output );
        output.copyTo( bm );



//        DigitsAuthButton digitsButton = (DigitsAuthButton) findViewById(R.id.auth_button);
//        digitsButton.setCallback(new AuthCallback() {
//            @Override
//            public void success(DigitsSession session, String phoneNumber) {
//
//                LOGD(TAG, "Sucess " + phoneNumber);
//                onSuccessfulRegistration(phoneNumber);
//
//            }
//
//            @Override
//            public void failure(DigitsException exception) {
//                LOGD(TAG, "failure");
//                Toast.makeText(getBaseContext(), "Registration failed!",
//                        Toast.LENGTH_LONG).show();
//            }
//        });

    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public void buttonSubmit(View v) {
        String myNumber;
        LOGD(TAG, "buttonSubmit");

        myNumber = ((EditText) findViewById(R.id.signup_number))
                .getEditableText().toString().trim();
        if (myNumber.length() != 10) return;

        hideKeyboard();
        myNumber = "+91" + myNumber;

        onSuccessfulRegistration(myNumber);


    }


    private void onSuccessfulRegistration(String number) {

        if (!NetworkUtils.isInternetAvailable(this)) {
            Toast.makeText(this,R.string.device_offline,Toast.LENGTH_SHORT).show();
            return;
        }

        clearDatabaseAndPrefs();
        MainPrefs.setMyNo(number, getApplicationContext());
        setNewUi();

       // dialog = UiUtils.createProgressDialog(getString(R.string.logging_in), this);
       // dialog.show();

        startService(new Intent(this, InitMusicDb.class));
        status[0] = RUNNING;

        startService(new Intent(getBaseContext(), ContactsSyncer.class).
                putExtra(Syncer.SHOULD_RETRY, false));
        status[1] = RUNNING;
    }


    private void setNewUi(){
        findViewById(R.id.act_songs_list_song_name).setVisibility(View.INVISIBLE);
        findViewById(R.id.signup_button_submit).setVisibility(View.INVISIBLE);
        findViewById(R.id.textView1).setVisibility(View.INVISIBLE);
        findViewById(R.id. signup_number).setVisibility(View.INVISIBLE);
        ((ImageView ) findViewById(R.id.imageView)).setImageBitmap(bm);
        findViewById(R.id.gloss).setVisibility(View.VISIBLE);
        findViewById(R.id.progressBar2).setVisibility(View.VISIBLE);
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
            findViewById(R.id.progressBar2).setVisibility(View.GONE);

            //if (dialog != null)
            //    dialog.dismiss();
           // dialog = null;

            if (finalResult) {
                MainPrefs.setLoginDone(getBaseContext());
                startActivity(new Intent(getBaseContext(),
                        MainSliderActivity.class));
                Syncer.callFuture(SongsSyncer.class, 10000, getBaseContext());
                finish();

            } else {
                Toast.makeText(getBaseContext(),R.string.loggingIn_error,
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
