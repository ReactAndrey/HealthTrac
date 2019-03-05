package com.sph.healthtrac.common;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

import com.sph.healthtrac.R;

public class HTToast {

    public static Toast showToast(Context context, String toastMessage, int toastLength) {

        Toast toast = Toast.makeText(context, toastMessage, toastLength);
        //toast.getView().setBackgroundColor(context.getResources().getColor(R.color.ht_blue));
        toast.getView().setBackgroundResource(R.drawable.ht_toast);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();

        return toast;
    }
}
