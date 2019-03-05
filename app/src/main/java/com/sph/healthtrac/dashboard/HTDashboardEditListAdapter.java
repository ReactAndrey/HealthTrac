/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sph.healthtrac.dashboard;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.sph.healthtrac.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HTDashboardEditListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> checkedDashboardEditItems;

    final int INVALID_ID = -1;

    HashMap<String, Integer> mIdMap = new HashMap<String, Integer>();

    Typeface textFont;

    int newPos = -1;

    public HTDashboardEditListAdapter(Activity context, int textViewResourceId, List<String> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        for (int i = 0; i < objects.size(); ++i) {
            mIdMap.put(objects.get(i), i);
        }

        textFont = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirNext-Medium.ttf");
    }

    @Override
    public long getItemId(int position) {
        if (position < 0 || position >= mIdMap.size()) {
            return INVALID_ID;
        }
        String item = getItem(position);
        return mIdMap.get(item);
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        view = inflater.inflate(R.layout.dashboard_editlist_cell, null, true);
        ImageView checkStatusView = (ImageView) view.findViewById(R.id.imageViewIcon);
        TextView labelView = (TextView) view.findViewById(R.id.textViewLabel);
        if(newPos == position){
            checkStatusView.setVisibility(View.INVISIBLE);
            ImageView arrowView = (ImageView) view.findViewById(R.id.imageViewArrow);
            arrowView.setVisibility(View.INVISIBLE);
            View seperatorView = view.findViewById(R.id.viewGraySeparator);
            seperatorView.setVisibility(View.INVISIBLE);
            return view;
        }
        labelView.setTypeface(textFont);
        labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        labelView.setTextColor(context.getResources().getColor(R.color.ht_gray_title_text));

        labelView.setText(getItem(position));
        if (checkedDashboardEditItems.contains(getItem(position)))
            checkStatusView.setImageResource(R.drawable.ht_dash_edit_check);
        else
            checkStatusView.setImageResource(R.drawable.ht_dash_edit_check_off);
        return view;
    }

    public static class ViewHolder {
        public ImageView checkStatusView;
        public TextView labelView;
    }

    public void setCheckedItems(ArrayList<String> checkedItems) {
        this.checkedDashboardEditItems = checkedItems;
    }

    public void setNewPos(int pos){
        this.newPos = pos;
    }

    public ArrayList<String> getCheckedItems() {
        return this.checkedDashboardEditItems;
    }
}
