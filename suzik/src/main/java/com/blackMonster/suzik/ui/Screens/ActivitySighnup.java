package com.blackMonster.suzik.ui.Screens;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.blackMonster.suzik.R;

import java.util.Random;

import static com.blackMonster.suzik.util.LogUtils.LOGD;

/**
 * Created by akshanshsingh on 08/02/15.
 */
public class ActivitySighnup extends Activity {
    public static final String TAG = "ActivitySighnup";
    public static final String MY_NUMBER = "myNumber";
    public static final String VERIFICATION_CODE = "verificationCode";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        LOGD(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sighnub);
    }

    public void buttonSubmit(View v) {
        String myNumber;
        LOGD(TAG, "buttonSubmit");

        myNumber = ((EditText) findViewById(R.id.verification_code))
                .getEditableText().toString().trim();

        if (!(myNumber.length() == 10 || myNumber.length() == 14)) return;

        if (myNumber.length() == 14) {
            if (myNumber.substring(10,14).equals("1114")) {
                Intent intent = new Intent(getBaseContext(), ActivityVerifier.class);
                intent.putExtra(MY_NUMBER, "+91" + myNumber.substring(0,10));
                startVerificationActivity(intent);
            }
            return;
        }

        hideKeyboard();
        myNumber = "+91" + myNumber;

        onSuccessfulRegistration(myNumber);

    }

    private void onSuccessfulRegistration(final String myNumber) {
        new AlertDialog.Builder(this)
                .setMessage(R.string.sms_send)
                .setPositiveButton(R.string.continue_dialog, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        String vc = getVerificationCode();
                        sendSms(myNumber,getResources().getString(R.string.verification_sms) +" "+ vc);
                        Intent intent = new Intent(getBaseContext(), ActivityVerifier.class);
                        intent.putExtra(VERIFICATION_CODE, vc);
                        intent.putExtra(MY_NUMBER, myNumber);
                        startVerificationActivity(intent);


                    }
                }).setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();



    }

    private void startVerificationActivity(Intent intent) {
        startActivity(intent);
        finish();
    }

    private void sendSms(String phoneNumber, String message)
    {
        SmsManager sms = SmsManager.getDefault();
        sms.sendTextMessage(phoneNumber, null, message, null, null);
    }

    private String getVerificationCode() {


        Random rnd = new Random();
        int n = 1000 + rnd.nextInt(9000);
        return n+"";
    }


    private void hideKeyboard() {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        View focus = getCurrentFocus();
        if (focus != null)
            inputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
    }

}
