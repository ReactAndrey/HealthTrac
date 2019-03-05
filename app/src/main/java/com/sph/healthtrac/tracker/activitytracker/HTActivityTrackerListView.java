package com.sph.healthtrac.tracker.activitytracker;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sph.healthtrac.tracker.HTTrackerReminderActivity;
import com.sph.healthtrac.R;

import java.util.List;

// THIS IS NO LONGER USED!!!

public class HTActivityTrackerListView extends ArrayAdapter<String> {


    private final Activity context;
    private final List<String> cellLabel;
    private final List<String> cellType;
    private final List<String> cellValue;
    private final List<String> cellGoal;
    private final List<String> cellReminder;
    private final List<String> cellMetricNumber;

    private int whichFocus;

    //private static InputMethodManager imm;

    //activityTrackerLabels, activityTrackerTypes, activityTrackerValues, activityTrackerGoals, activityTrackerReminders

    public HTActivityTrackerListView(Activity context, List<String> cellLabel, List<String> cellType, List<String> cellValue, List<String> cellGoal, List<String> cellReminder, List<String> cellMetricNumber) {

        super(context, R.layout.color_my_day_list_view_cell, cellLabel);
        this.context = context;
        this.cellLabel = cellLabel;
        this.cellType = cellType;
        this.cellValue = cellValue;
        this.cellGoal = cellGoal;
        this.cellReminder = cellReminder;
        this.cellMetricNumber = cellMetricNumber;

        this.whichFocus = -1;

        //imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        final Integer thisTag;
        thisTag = position;

        Typeface typeLabel = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirNext-Regular.ttf");
        Typeface typeValue = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Light.ttf");

        LayoutInflater inflater = context.getLayoutInflater();

        RelativeLayout.LayoutParams params;

        View rowView = inflater.inflate(R.layout.activity_tracker_list_view_cell, null, true);

        ImageView imageViewActivityTrackerReminderIcon = (ImageView) rowView.findViewById(R.id.imageViewActivityTrackerReminderIcon);

        TextView textViewActivityLabel = (TextView) rowView.findViewById(R.id.textViewActivityLabel);

        final EditText editTextActivityValue = (EditText) rowView.findViewById(R.id.editTextActivityValue);
        final EditText editTextActivityGoal = (EditText) rowView.findViewById(R.id.editTextActivityGoal);

        ImageView imageViewActivityValueCheck = (ImageView) rowView.findViewById(R.id.imageViewActivityValueCheck);

        // reminder icon?
        if (cellReminder.get(position).equals("Y")) {

            imageViewActivityTrackerReminderIcon.setImageResource(R.drawable.ht_reminder_on);

        } else {

            imageViewActivityTrackerReminderIcon.setImageResource(R.drawable.ht_reminder);
        }

        imageViewActivityTrackerReminderIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(context, HTTrackerReminderActivity.class);
                intent.putExtra("metric", cellMetricNumber.get(position));
                context.startActivityForResult(intent, 1);
            }
        });

        // textViewActivityLabel
        textViewActivityLabel.setTypeface(typeLabel);
        textViewActivityLabel.setTextColor(context.getResources().getColor(R.color.ht_gray_title_text));
        textViewActivityLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        textViewActivityLabel.setText(cellLabel.get(position));

        // check box?
        if (cellType.get(position).equals("checkbox")) { // check box metric

            params = (RelativeLayout.LayoutParams)editTextActivityValue.getLayoutParams();
            params.width = 0;
            params.height = 0;
            editTextActivityValue.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams)editTextActivityGoal.getLayoutParams();
            params.width = 0;
            params.height = 0;
            editTextActivityGoal.setLayoutParams(params);

            imageViewActivityValueCheck.setTag(thisTag);
            imageViewActivityValueCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    whichFocus = -1;
                    //ActivityTrackerActivity.metricChecked(thisTag);
                }
            });

            imageViewActivityValueCheck.setImageResource(R.drawable.ht_check_off_green);

            if (cellValue.get(position).equals("1")) { // on!

                imageViewActivityValueCheck.setImageResource(R.drawable.ht_check_on_green);
            }

        } else { // regular, numeric metric

            params = (RelativeLayout.LayoutParams)imageViewActivityValueCheck.getLayoutParams();
            params.width = 0;
            params.height = 0;
            imageViewActivityValueCheck.setLayoutParams(params);

            editTextActivityValue.setTypeface(typeValue);
            editTextActivityValue.setTextColor(context.getResources().getColor(R.color.ht_gray_title_text));
            editTextActivityValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            //editTextActivityValue.setBackgroundColor(context.getResources().getColor(R.color.ht_text_field_bg));
            editTextActivityValue.setHintTextColor(context.getResources().getColor(R.color.ht_color_light_gray_text));
            editTextActivityValue.setHint("0");
            editTextActivityValue.setTag(thisTag);

            editTextActivityGoal.setTypeface(typeValue);
            editTextActivityGoal.setTextColor(context.getResources().getColor(R.color.ht_gray_title_text));
            editTextActivityGoal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            //editTextActivityGoal.setBackgroundColor(context.getResources().getColor(R.color.ht_text_field_bg));
            editTextActivityGoal.setHintTextColor(context.getResources().getColor(R.color.ht_color_light_gray_text));
            editTextActivityGoal.setHint("0");
            editTextActivityGoal.setTag(thisTag + 20);

            //editTextActivityValue
            if (!cellValue.get(position).equals("0")) {

                editTextActivityValue.setText(cellValue.get(position));
                //editTextActivityValue.setText(String.format("%,d", Long.parseLong(cellValue.get(position))));
            }
            //editTextActivityGoal
            if (!cellGoal.get(position).equals("0")) {

                editTextActivityGoal.setText(cellGoal.get(position));
            }

            // very necessary!  scrolling the ListView was refreshing the values from the initial web svc call
            editTextActivityValue.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {

                    editTextActivityValue.removeTextChangedListener(this);

                    String tempString = editTextActivityValue.getText().toString().replace(",", "");

                    cellValue.set(position, tempString);

                    if (tempString.length() > 0) {
                        tempString = String.format("%,d", Long.parseLong(tempString));
                    }

                    editTextActivityValue.setText(tempString);

                    editTextActivityValue.addTextChangedListener(this);
                }

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    /*
                    editTextActivityValue.removeTextChangedListener(this);

                    String tempString = editTextActivityValue.getText().toString().replace(",", "");

                    //cellValue.set(position, tempString);

                    tempString = String.format("%,d", Long.parseLong(tempString));

                    editTextActivityValue.setText(tempString);

                    editTextActivityValue.addTextChangedListener(this);
                    */
                }

                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    String tempString = editTextActivityValue.getText().toString().replace(",", "");

                    cellValue.set(position, tempString);
                }
            });

            editTextActivityGoal.addTextChangedListener(new TextWatcher() {

                public void afterTextChanged(Editable s) {}

                public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    cellGoal.set(position, editTextActivityGoal.getText().toString());
                }
            });

            editTextActivityValue.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        whichFocus = thisTag;
                        //Log.e("whichFocus", String.valueOf(whichFocus));
                    //} else {
                    //    whichFocus = -1;
                    }
                }
            });

            editTextActivityGoal.setOnFocusChangeListener(new View.OnFocusChangeListener() {
                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (hasFocus) {
                        whichFocus = (thisTag + 20);
                    //    Log.e("whichFocus", String.valueOf(whichFocus));
                    //} else {
                    //    whichFocus = -1;
                    }
                }
            });

            if (whichFocus >= 20 && whichFocus == (thisTag + 20)) { // goals

                editTextActivityGoal.requestFocus();

            } else if (whichFocus >= 0 && whichFocus == thisTag) { // values

                editTextActivityValue.requestFocus();

            } else {

                editTextActivityValue.clearFocus();
                editTextActivityGoal.clearFocus();
            }
        }

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        float displayDensity = displayMetrics.density;
        float screenWidth = displayMetrics.widthPixels;
        //float screenHeight = displayMetrics.heightPixels;

        int rowHeight = (int)(60 * displayDensity);

        params = (RelativeLayout.LayoutParams)textViewActivityLabel.getLayoutParams();

        //params.setMargins(0, 0, 0, (int)(2 * displayDensity));  // left, top, right, bottom
        params.width = (int)((70 * displayDensity) + ((screenWidth * displayDensity) - 320)); // offset
        textViewActivityLabel.setLayoutParams(params);

        RelativeLayout relativeLayout = (RelativeLayout) rowView.findViewById(R.id.activityTrackerRow);

        params = (RelativeLayout.LayoutParams)relativeLayout.getLayoutParams();
        params.height = rowHeight;

        relativeLayout.setLayoutParams(params);

        return rowView;
    }
}
