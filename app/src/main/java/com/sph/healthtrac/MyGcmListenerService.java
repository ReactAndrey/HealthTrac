package com.sph.healthtrac;

import android.content.Intent;
import android.os.Bundle;
import com.google.android.gms.gcm.GcmListenerService;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.MyLifecycleHandler;
import com.sph.healthtrac.common.NotifyMessage;

public class MyGcmListenerService extends GcmListenerService {
    private static final String TAG = "MyGcmListenerService";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */

    @Override
    public void onMessageReceived(String from, Bundle data) {

        super.onMessageReceived(from, data);

        String badgeCountStr = data.getString("badgeicon");
        String pushMessageStr = data.getString("message");
        String pushMessageTitle = data.getString("messageTitle");

        if (badgeCountStr != null && !badgeCountStr.equals("")) {

            int appIconBadgeCount;

            try {
                appIconBadgeCount = Integer.parseInt(badgeCountStr);
            } catch (NumberFormatException e) {
                appIconBadgeCount = 0;
            }

            HTGlobals.getInstance().setAppIconBadge(this, appIconBadgeCount);
        }

        if (pushMessageStr != null && !pushMessageStr.equals("")) {

            Intent intent = new Intent(this, NotifyMessage.class);

            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("messageTitle", pushMessageTitle);
            intent.putExtra("messageText", pushMessageStr);

            if (MyLifecycleHandler.isApplicationVisible() && MyLifecycleHandler.isApplicationInForeground()) {
                intent.putExtra("appIsVisible", "true");
            } else {
                intent.putExtra("appIsVisible", "false");
            }

            startActivity(intent);
        }
    }
}
