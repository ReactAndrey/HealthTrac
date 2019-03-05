package com.sph.healthtrac.common;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;

public class NotifyMessage extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences mSharedPreferences;

        mSharedPreferences = getSharedPreferences("SPHPrefs", MODE_PRIVATE);

        String practice = mSharedPreferences.getString("practice", "");
        String messageTitle;
        String messageText;
        String appIsVisible;

        if (practice.equals("")) {
            practice = getString(R.string.app_name);
        }

        messageTitle = getIntent().getStringExtra("messageTitle");
        messageText = getIntent().getStringExtra("messageText");
        appIsVisible = getIntent().getStringExtra("appIsVisible");

        if (messageTitle == null || messageTitle.equals("")) {

            messageTitle = "Message from " + practice;
        }

        if (messageText != null && !messageText.equals("")) { // we have a message

            // we're already in the app!  show an alert which will dismiss back to the app
            if (!isTaskRoot() && appIsVisible.equals("true")) {

                AlertDialog.Builder builder = new AlertDialog.Builder(NotifyMessage.this, R.style.MyCustomAlertDialogStyle);

                AlertDialog alert;

                builder.setTitle(messageTitle)
                        .setMessage(messageText)
                        .setCancelable(false)
                        .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                                NotifyMessage.this.finish();
                            }
                        });

                alert = builder.create();
                alert.show();

            } else { // app is not open or not visible, show a standard notification which will start the app at the login screen when clicked

                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(messageTitle)
                        .setContentText(messageText)
                        .setStyle(new NotificationCompat.BigTextStyle().setBigContentTitle(messageTitle))
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(messageText))
                        .setTicker(messageTitle)
                        .setAutoCancel(true)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setPriority(Notification.PRIORITY_MAX);

                if (!isTaskRoot()) { // the app is running, resume it by calling and closing the NotificationActivity class

                    Intent intent = new Intent(this, NotificationActivity.class);

                    intent.setFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);

                    PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, 0);

                    mBuilder.setContentIntent(pendingIntent);

                } else { // the app is not running, launch it

                    Intent intent = new Intent(this, LoginActivity.class);

                    TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);

                    stackBuilder.addParentStack(LoginActivity.class);

                    stackBuilder.addNextIntent(intent);

                    PendingIntent pendingIntent =
                            stackBuilder.getPendingIntent(
                                    0,
                                    PendingIntent.FLAG_UPDATE_CURRENT
                            );

                    mBuilder.setContentIntent(pendingIntent);
                }

                int notificationID = (int)System.currentTimeMillis();

                notificationManager.notify(notificationID, mBuilder.build());

                Intent homeIntent = new Intent();

                homeIntent.setAction(Intent.ACTION_MAIN);
                homeIntent.addCategory(Intent.CATEGORY_HOME);

                this.startActivity(homeIntent);

                NotifyMessage.this.finish();
            }
        }
    }
}