package com.sph.healthtrac.common;

import android.content.Context;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sph.healthtrac.R;

public class HTSearchEditBar {

    public static View getActionBar(Context context) {

        Typeface searchEditFont = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirNext-Medium.ttf");

        LayoutInflater mInflater = LayoutInflater.from(context);

        View mCustomView = mInflater.inflate(R.layout.search_edit_bar, null);

        EditText searchEditText = (EditText) mCustomView.findViewById(R.id.editText);
        searchEditText.setTypeface(searchEditFont);
        searchEditText.setTextColor(context.getResources().getColor(R.color.ht_gray_title_text));
        searchEditText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        searchEditText.setHintTextColor(context.getResources().getColor(R.color.ht_color_light_gray_text));
        searchEditText.setHint("Search");
        return mCustomView;
    }
}
