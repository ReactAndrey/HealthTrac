package com.sph.healthtrac.common;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sph.healthtrac.R;

public class HTDateActionBar {

    public static View getActionBar(Context context) {

        Typeface typeTitle = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirNext-Regular.ttf");

        LayoutInflater mInflater = LayoutInflater.from(context);

        View mCustomView = mInflater.inflate(R.layout.date_action_bar, null);

        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);

        mTitleTextView.setTypeface(typeTitle);
        mTitleTextView.setTextColor(context.getResources().getColor(R.color.ht_gray_title_text));
        mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

        ImageView leftArrowAction = (ImageView) mCustomView.findViewById(R.id.leftArrowAction);
        ImageView rightArrowAction = (ImageView) mCustomView.findViewById(R.id.rightArrowAction);

        leftArrowAction.setImageResource(R.drawable.ht_arrow_left_gray);
        rightArrowAction.setImageResource(R.drawable.ht_arrow_right_gray);

        return mCustomView;
    }
}
