package com.sph.healthtrac;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p/>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegIntentService";
    private static final String[] TOPICS = {"global"};
    private static final String PROPERTY_REG_ID = "registration_id";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = getSharedPreferences("SPHPrefs", MODE_PRIVATE);

        try {
            //start register_for_gcm
            //start get_token
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(getString(R.string.sender_id), GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            //end get_token

            //Log.i(TAG, "GCM Registration Token: " + token);

            //save token
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(PROPERTY_REG_ID, token).apply();
        } catch (Exception e) {
            //Log.i(TAG, "Failed to complete token refresh", e);
        }
        //Notify UI that registration has completed
        Intent registrationComplete = new Intent("registrationComplete");
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

}
