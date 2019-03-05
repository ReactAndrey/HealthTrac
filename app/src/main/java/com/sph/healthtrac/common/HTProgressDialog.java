package com.sph.healthtrac.common;

import android.app.ProgressDialog;
import android.content.Context;

import com.sph.healthtrac.R;

public class HTProgressDialog {

    public static ProgressDialog getProgressDialog(Context context) {

        ProgressDialog progressDialog = new ProgressDialog(context, R.style.MyProgressTheme);
        progressDialog.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
        progressDialog.setMessage(null);
        progressDialog.setCancelable(true);
        progressDialog.setIndeterminate(true);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.ht_progress));

        return progressDialog;
    }
}
