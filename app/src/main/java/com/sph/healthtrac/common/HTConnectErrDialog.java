package com.sph.healthtrac.common;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Gravity;
import android.widget.Toast;

import com.sph.healthtrac.R;

public class HTConnectErrDialog {

    public static void showDilaog(Context context, String title, String message) {

        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setCancelable(true)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).create().show();
    }
}
