package com.sph.healthtrac.learn;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sph.healthtrac.R;

public class HTLearningModule {

    public static View getLearningModuleItemView(Context context, String title) {

        Typeface learnTitleFont = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirNext-Regular.ttf");

        LayoutInflater mInflater = LayoutInflater.from(context);

        View mCustomView = mInflater.inflate(R.layout.learnmodule_cell, null);

        TextView learnTitleView = (TextView) mCustomView.findViewById(R.id.learnTitleView);
        learnTitleView.setTypeface(learnTitleFont);
        learnTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        learnTitleView.setText(title);

//        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
//        float displayDensity = displayMetrics.density;
//        int marginValue = (int) (10 * displayDensity);
//        int top_marginValue = (int) (4 * displayDensity);
//
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
//        params.setMargins(marginValue, top_marginValue, marginValue, 0);
//        mCustomView.setLayoutParams(params);
//        int left_paddingValue = (int) (12 * displayDensity);
//        int right_paddingValue = (int) (15 * displayDensity);
//        int top_paddingValue = (int) (10 * displayDensity);
//        mCustomView.setPadding(left_paddingValue, top_paddingValue, right_paddingValue, top_paddingValue);

        return mCustomView;
    }

    public static View getLearningDetailItemView(Context context, String title) {

        Typeface learnTitleFont = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirNext-Medium.ttf");

        LayoutInflater mInflater = LayoutInflater.from(context);

        View mCustomView = mInflater.inflate(R.layout.learnmodule_cell, null);

        TextView learnTitleView = (TextView) mCustomView.findViewById(R.id.learnTitleView);
        learnTitleView.setTypeface(learnTitleFont);
        learnTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        learnTitleView.setTextColor(context.getResources().getColor(R.color.ht_gray_title_text));

        learnTitleView.setText(title);

        View contentView = mCustomView.findViewById(R.id.contentLayout);
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) contentView.getLayoutParams();
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float displayDensity = displayMetrics.density;
        params.setMargins(0, (int) (4 * displayDensity), 0, 0);
        contentView.setLayoutParams(params);
        contentView.setBackgroundColor(Color.WHITE);

        return mCustomView;
    }
}
