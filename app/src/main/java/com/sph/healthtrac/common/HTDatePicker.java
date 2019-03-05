package com.sph.healthtrac.common;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sph.healthtrac.R;

public class HTDatePicker {

    public static View getDatePicker(Context context) {

        Typeface typeTitle = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirNext-DemiBold.ttf");

        LayoutInflater mInflater = LayoutInflater.from(context);

        View mCustomView = mInflater.inflate(R.layout.date_picker, null);

        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.date_text);

        mTitleTextView.setTypeface(typeTitle);
        mTitleTextView.setTextColor(context.getResources().getColor(R.color.ht_gray_title_text));
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);

        mTitleTextView.setText("Today");

        return mCustomView;
    }
}
