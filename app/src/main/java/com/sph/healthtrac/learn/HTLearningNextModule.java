package com.sph.healthtrac.learn;

import android.content.Context;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sph.healthtrac.R;

public class HTLearningNextModule {

    public static View getLearningNextModuleView(Context context, String title, boolean hasModules) {

        Typeface learnHeaderFont = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirNext-Medium.ttf");
        Typeface learnTitleFont = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirNext-Regular.ttf");

        LayoutInflater mInflater = LayoutInflater.from(context);

        View mCustomView = mInflater.inflate(R.layout.nextmodule_cell, null);

        ImageView iconView = (ImageView) mCustomView.findViewById(R.id.imageView);
        TextView learnLabelView = (TextView) mCustomView.findViewById(R.id.learnLabelView);
        TextView learnTitleView = (TextView) mCustomView.findViewById(R.id.learnTitleView);
        learnLabelView.setTypeface(learnHeaderFont);
        learnTitleView.setTypeface(learnTitleFont);
        learnLabelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        learnTitleView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        if(!hasModules) {
            learnTitleView.setText("There are no modules in this section");
            iconView.setVisibility(View.GONE);
        }else{
            learnTitleView.setText(title);
        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float displayDensity = displayMetrics.density;
        int marginValue = (int) (10 * displayDensity);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(marginValue, marginValue, marginValue, 0);
        mCustomView.setLayoutParams(params);

        return mCustomView;
    }
}
