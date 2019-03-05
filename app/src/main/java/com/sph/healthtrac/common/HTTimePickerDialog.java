package com.sph.healthtrac.common;

import android.app.TimePickerDialog;
import android.content.Context;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HTTimePickerDialog extends TimePickerDialog {

    private TimePicker timePicker;
    private int mMinuteInterval;
    private int mMinute;
    private SimpleDateFormat sdf = new SimpleDateFormat("h:mm a", Locale.US);

    public HTTimePickerDialog(Context context, int theme, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView) {

        super(context, theme, callBack, hourOfDay, minute, is24HourView);
        this.mMinute = minute;
    }

    public HTTimePickerDialog(Context context, int theme, OnTimeSetListener callBack, int hourOfDay, int minute, boolean is24HourView, int minuteInterval) {

        super(context, theme, callBack, hourOfDay, minute, is24HourView);
        mMinuteInterval = minuteInterval;
        this.mMinute = minute;
    }

    @Override
    public void onTimeChanged(TimePicker timePicker, int hourOfDay, int minute) {
        super.onTimeChanged(timePicker, hourOfDay, minute);
        this.mMinute = minute;
        Date date = new Date();
        date.setHours(hourOfDay);
        date.setMinutes(minute * mMinuteInterval);
        setTitle(sdf.format(date));
    }

    @Override
    public void onAttachedToWindow() {

        super.onAttachedToWindow();

        try {
            Class<?> classForid = Class.forName("com.android.internal.R$id");
            Field timePickerField = classForid.getField("timePicker");
            this.timePicker = (TimePicker) findViewById(timePickerField.getInt(null));

            timePicker.setDescendantFocusability(TimePicker.FOCUS_BLOCK_DESCENDANTS);

            Field field = classForid.getField("minute");

            NumberPicker mMinuteSpinner = (NumberPicker) timePicker.findViewById(field.getInt(null));
            mMinuteSpinner.setMinValue(0);
            mMinuteSpinner.setMaxValue(0);
            List<String> displayedValues = new ArrayList<>();
            if (mMinuteInterval == 0 || mMinuteInterval == 60) {
                displayedValues.add(String.format("%02d", 0));
            } else {
                for (int i = 0; i < 60; i += mMinuteInterval) {
                    displayedValues.add(String.format("%02d", i));
                }
                mMinuteSpinner.setMaxValue((60 / mMinuteInterval) - 1);
                if (mMinute > 0)
                    mMinuteSpinner.setValue(mMinute);
            }
            mMinuteSpinner.setDisplayedValues(displayedValues.toArray(new String[0]));

            Date date = new Date();
            date.setHours(timePicker.getCurrentHour());
            date.setMinutes(mMinute * mMinuteInterval);
            setTitle(sdf.format(date));

        } catch (Exception e) {

            //e.printStackTrace();
        }
    }
}