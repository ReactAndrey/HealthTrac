package com.sph.healthtrac.common;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sph.healthtrac.R;

public class HTAppletButton {

    public static View getAppletButton(Context context, String ButtonImage, String ButtonText) {

        Typeface typeTitle = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirNext-Medium.ttf");

        LayoutInflater mInflater = LayoutInflater.from(context);

        View mCustomView = mInflater.inflate(R.layout.applet_button, null);

        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.appletText);

        mTitleTextView.setTypeface(typeTitle);
        mTitleTextView.setTextColor(context.getResources().getColor(R.color.ht_gray_title_text));
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        mTitleTextView.setText(ButtonText);

        ImageView appletIcon = (ImageView) mCustomView.findViewById(R.id.appletIcon);

        appletIcon.setImageResource(context.getResources().getIdentifier(ButtonImage, "drawable", context.getPackageName()));

        return mCustomView;
    }
}