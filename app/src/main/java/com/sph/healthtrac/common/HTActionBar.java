package com.sph.healthtrac.common;

import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sph.healthtrac.R;

public class HTActionBar {

    public static View getActionBar(Context context, String titleText, String leftButtonText, String rightButtonText) {

        Typeface typeTitle = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirNext-Regular.ttf");
        Typeface typeTitleHT = Typeface.createFromAsset(context.getAssets(), "fonts/Omnes-Light.otf");
        Typeface typeTitleHTBold = Typeface.createFromAsset(context.getAssets(), "fonts/Omnes-Medium.otf");
        Typeface typeButton = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirNext-DemiBold.ttf");

        RelativeLayout.LayoutParams params;

        LayoutInflater mInflater = LayoutInflater.from(context);

        View mCustomView = mInflater.inflate(R.layout.action_bar, null);

        TextView mTitleTextView = (TextView) mCustomView.findViewById(R.id.title_text);

        if (titleText.equals("HealthTrac")) {

            mTitleTextView.setTypeface(typeTitleHT);
            mTitleTextView.setTextColor(context.getResources().getColor(R.color.ht_blue));
            mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);

            SpannableString spannableString =  new SpannableString(titleText);
            spannableString.setSpan(new HTTypefaceSpan("", typeTitleHTBold), 6, 10, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

            mTitleTextView.setText(spannableString);

        } else if (titleText.equals("Track My Day")) {

            mTitleTextView.setTypeface(typeTitleHT);
            mTitleTextView.setTextColor(context.getResources().getColor(R.color.ht_blue));
            mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 23);

            mTitleTextView.setText(titleText);

        } else {

            mTitleTextView.setTypeface(typeTitle);
            mTitleTextView.setTextColor(context.getResources().getColor(R.color.ht_gray_title_text));
            mTitleTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);

            mTitleTextView.setText(titleText);
        }

        Button mLeftButton = (Button) mCustomView.findViewById(R.id.leftButton);

        mLeftButton.setTypeface(typeButton);
        mLeftButton.setTextColor(context.getResources().getColor(R.color.ht_blue));
        mLeftButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        params = (RelativeLayout.LayoutParams) mLeftButton.getLayoutParams();

        if (leftButtonText.equals("") || leftButtonText.equals("leftArrow")) {

            params.height = 0;
            params.width = 0;

        } else {

            mLeftButton.setText(leftButtonText);
        }

        mLeftButton.setLayoutParams(params);

        Button mRightButton = (Button) mCustomView.findViewById(R.id.rightButton);

        mRightButton.setTypeface(typeButton);
        mRightButton.setTextColor(context.getResources().getColor(R.color.ht_blue));
        mRightButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        params = (RelativeLayout.LayoutParams) mRightButton.getLayoutParams();

        if (rightButtonText.equals("") || rightButtonText.equals("checkMark") || rightButtonText.equals("deleteMark")) {

            params.height = 0;
            params.width = 0;

        } else {

            mRightButton.setText(rightButtonText);
        }

        mRightButton.setLayoutParams(params);

        ImageView leftArrowAction = (ImageView) mCustomView.findViewById(R.id.leftArrowAction);

        if (!leftButtonText.equals("leftArrow")) {

            params = (RelativeLayout.LayoutParams) leftArrowAction.getLayoutParams();

            params.height = 0;
            params.width = 0;

            leftArrowAction.setLayoutParams(params);
        }

        ImageView rightCheckAction = (ImageView) mCustomView.findViewById(R.id.rightCheckAction);

        if (!rightButtonText.equals("checkMark") && !rightButtonText.equals("deleteMark")) {

            params = (RelativeLayout.LayoutParams) rightCheckAction.getLayoutParams();

            params.height = 0;
            params.width = 0;

            rightCheckAction.setLayoutParams(params);
        }

        return mCustomView;
    }
}
