package com.sph.healthtrac.tracker.colormyday;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sph.healthtrac.tracker.HTTrackerReminderActivity;
import com.sph.healthtrac.common.HTTypefaceSpan;
import com.sph.healthtrac.R;

import java.util.List;

public class HTColorMyDayListView extends ArrayAdapter<String> {

    private final Activity context;
    private final List<String> cellLabel;
    private final List<String> cellValue;
    private final List<String> reminder;

    public HTColorMyDayListView(Activity context, List<String> cellLabel, List<String> cellValue, List<String> reminder) {

        super(context, R.layout.color_my_day_list_view_cell, cellLabel);
        this.context = context;
        this.cellLabel = cellLabel;
        this.cellValue = cellValue;
        this.reminder = reminder;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Typeface typeSubject = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirNext-Regular.ttf");
        Typeface typeColor = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirNext-DemiBold.ttf");

        LayoutInflater inflater = context.getLayoutInflater();

        RelativeLayout.LayoutParams params;

        View rowView = inflater.inflate(R.layout.color_my_day_list_view_cell, null, true);

        ImageView imageViewColorReminderIcon = (ImageView) rowView.findViewById(R.id.imageViewColorReminderIcon);
        ImageView imageViewGreenButton = (ImageView) rowView.findViewById(R.id.imageViewGreenButton);
        ImageView imageViewYellowButton = (ImageView) rowView.findViewById(R.id.imageViewYellowButton);
        ImageView imageViewRedButton = (ImageView) rowView.findViewById(R.id.imageViewRedButton);

        TextView textViewColorLabel = (TextView) rowView.findViewById(R.id.textViewColorLabel);

        textViewColorLabel.setTypeface(typeSubject);
        textViewColorLabel.setTextColor(context.getResources().getColor(R.color.ht_color_gray_text));
        textViewColorLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        float displayDensity = displayMetrics.density;
        float screenWidth = displayMetrics.widthPixels;
        float screenHeight = displayMetrics.heightPixels;

        int rowHeight = (int)((screenHeight - (114 * displayDensity)) / 5);

        params = (RelativeLayout.LayoutParams)textViewColorLabel.getLayoutParams();

        params.setMargins(0, (int)((rowHeight - (48 * displayDensity)) * .12), 0, 0);  // left, top, right, bottom

        RelativeLayout relativeLayout = (RelativeLayout) rowView.findViewById(R.id.colorMyDayRow);

        params = (RelativeLayout.LayoutParams)relativeLayout.getLayoutParams();
        params.height = rowHeight;

        relativeLayout.setLayoutParams(params);

        if (rowHeight < 80) {

            int buttonPadding = 5;

            imageViewGreenButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageViewYellowButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            imageViewRedButton.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            imageViewGreenButton.setPadding(buttonPadding, buttonPadding + 5, buttonPadding, buttonPadding);
            imageViewYellowButton.setPadding(buttonPadding, buttonPadding + 5, buttonPadding, buttonPadding);
            imageViewRedButton.setPadding(buttonPadding, buttonPadding + 5, buttonPadding, buttonPadding);
        }

        // init color buttons
        imageViewGreenButton.setImageResource(R.drawable.ht_color_button_green_off);
        imageViewYellowButton.setImageResource(R.drawable.ht_color_button_yellow_off);
        imageViewRedButton.setImageResource(R.drawable.ht_color_button_red_off);

        final String buttonType;

        buttonType = cellLabel.get(position); // OVERALL, EAT, etc.

        imageViewGreenButton.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {

                ColorMyDayActivity.colorButtonClicked(buttonType, "GREEN");
            }
        });

        imageViewYellowButton.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {

                ColorMyDayActivity.colorButtonClicked(buttonType, "YELLOW");
            }
        });

        imageViewRedButton.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {

                ColorMyDayActivity.colorButtonClicked(buttonType, "RED");
            }
        });

        int buttonSideMargin = (int)(((screenWidth / 3) - (48 * displayDensity)) / 2);
        int buttonTopMargin = (int)((rowHeight - (48 * displayDensity)) * .72);

        params = (RelativeLayout.LayoutParams)imageViewGreenButton.getLayoutParams();
        params.setMargins(buttonSideMargin, buttonTopMargin, buttonSideMargin, 0);  // left, top, right, bottom

        imageViewGreenButton.setLayoutParams(params);

        params = (RelativeLayout.LayoutParams)imageViewYellowButton.getLayoutParams();
        params.setMargins(buttonSideMargin, buttonTopMargin, buttonSideMargin, 0);  // left, top, right, bottom

        imageViewYellowButton.setLayoutParams(params);

        params = (RelativeLayout.LayoutParams)imageViewRedButton.getLayoutParams();
        params.setMargins(buttonSideMargin, buttonTopMargin, buttonSideMargin, 0);  // left, top, right, bottom

        imageViewRedButton.setLayoutParams(params);

        // reminder icon?
        params = (RelativeLayout.LayoutParams)imageViewColorReminderIcon.getLayoutParams();

        if (cellLabel.get(position).equals("OVERALL")) {

            if (reminder.get(0).equals("Y")) {

                imageViewColorReminderIcon.setImageResource(R.drawable.ht_reminder_on);

            } else {

                imageViewColorReminderIcon.setImageResource(R.drawable.ht_reminder);
            }

            imageViewColorReminderIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent intent = new Intent(context, HTTrackerReminderActivity.class);
                    intent.putExtra("metric", "color");
                    context.startActivityForResult(intent, 1);
                }
            });

        } else {

            params.width = (int)(16 * displayDensity);
            params.height = (int)(16 * displayDensity);

            imageViewColorReminderIcon.setImageResource(android.R.color.transparent);
        }

        imageViewColorReminderIcon.setLayoutParams(params);

        //textViewColorLabel
        SpannableString spannableString;
        String colorMyDaySubject;
        String colorMyDayColor;

        if (cellLabel.get(position).equals("OVERALL")) {

            colorMyDaySubject = "I followed my program";

            if (cellValue.get(position).equals("GREEN")) {

                colorMyDayColor = "Well!";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeColor), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_green_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                imageViewGreenButton.setImageResource(R.drawable.ht_color_button_green_on);

            } else if (cellValue.get(position).equals("YELLOW")) {

                colorMyDayColor = "Fairly well";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeColor), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_yellow_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                imageViewYellowButton.setImageResource(R.drawable.ht_color_button_yellow_on);

            } else if (cellValue.get(position).equals("RED")) {

                colorMyDayColor = "Not so well";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeColor), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_red_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                imageViewRedButton.setImageResource(R.drawable.ht_color_button_red_on);

            } else {

                colorMyDayColor = "(Select to fill)";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeSubject), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_light_gray_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }

        } else if (cellLabel.get(position).equals("EAT")) {

            colorMyDaySubject = "My eating was";

            if (cellValue.get(position).equals("ONTRACK")) {

                colorMyDayColor = "Good!";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeColor), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_green_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                imageViewGreenButton.setImageResource(R.drawable.ht_color_button_green_on);

            } else if (cellValue.get(position).equals("OK")) {

                colorMyDayColor = "Fair";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeColor), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_yellow_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                imageViewYellowButton.setImageResource(R.drawable.ht_color_button_yellow_on);

            } else if (cellValue.get(position).equals("OFF")) {

                colorMyDayColor = "Poor";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeColor), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_red_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                imageViewRedButton.setImageResource(R.drawable.ht_color_button_red_on);

            } else {

                colorMyDayColor = "(Select to fill)";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeSubject), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_light_gray_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }

        } else if (cellLabel.get(position).equals("MOVE")) {

            colorMyDaySubject = "My activity levels were";

            if (cellValue.get(position).equals("ONTRACK")) {

                colorMyDayColor = "Good!";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeColor), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_green_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                imageViewGreenButton.setImageResource(R.drawable.ht_color_button_green_on);

            } else if (cellValue.get(position).equals("OK")) {

                colorMyDayColor = "Fair";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeColor), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_yellow_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                imageViewYellowButton.setImageResource(R.drawable.ht_color_button_yellow_on);

            } else if (cellValue.get(position).equals("OFF")) {

                colorMyDayColor = "Poor";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeColor), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_red_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                imageViewRedButton.setImageResource(R.drawable.ht_color_button_red_on);

            } else {

                colorMyDayColor = "(Select to fill)";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeSubject), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_light_gray_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }

        } else if (cellLabel.get(position).equals("SLEEP")) {

            colorMyDaySubject = "My sleep was";

            if (cellValue.get(position).equals("GOOD")) {

                colorMyDayColor = "Good!";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeColor), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_green_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                imageViewGreenButton.setImageResource(R.drawable.ht_color_button_green_on);

            } else if (cellValue.get(position).equals("FAIR")) {

                colorMyDayColor = "Fair";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeColor), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_yellow_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                imageViewYellowButton.setImageResource(R.drawable.ht_color_button_yellow_on);

            } else if (cellValue.get(position).equals("POOR")) {

                colorMyDayColor = "Poor";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeColor), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_red_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                imageViewRedButton.setImageResource(R.drawable.ht_color_button_red_on);

            } else {

                colorMyDayColor = "(Select to fill)";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeSubject), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_light_gray_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }

        } else { // stress

            colorMyDaySubject = "My stress levels were";

            if (cellValue.get(position).equals("LOW")) {

                colorMyDayColor = "Low!";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeColor), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_green_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                imageViewGreenButton.setImageResource(R.drawable.ht_color_button_green_on);

            } else if (cellValue.get(position).equals("MEDIUM")) {

                colorMyDayColor = "Medium";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeColor), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_yellow_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                imageViewYellowButton.setImageResource(R.drawable.ht_color_button_yellow_on);

            } else if (cellValue.get(position).equals("HIGH")) {

                colorMyDayColor = "High";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeColor), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_red_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

                imageViewRedButton.setImageResource(R.drawable.ht_color_button_red_on);

            } else {

                colorMyDayColor = "(Select to fill)";

                spannableString = new SpannableString(colorMyDayColor);
                spannableString.setSpan(new HTTypefaceSpan("", typeSubject), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                spannableString.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.ht_color_light_gray_text)), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            }
        }

        textViewColorLabel.setText(TextUtils.concat(colorMyDaySubject, " ", spannableString));

        return rowView;
    }
}
