package com.sph.healthtrac.dashboard;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sph.healthtrac.common.HTTypefaceSpan;
import com.sph.healthtrac.R;
import com.sph.healthtrac.common.PixelUtil;

import java.util.List;

public class HTListView extends ArrayAdapter<String> {

    private final Activity context;
    private final List<String> cellValue;
    private final List<String> cellLabel;
    private final List<Integer> image;
    private final List<Double> progress;
    private String numberOfMessages = "";
    private String caloriesBarColor = "green";

    public HTListView(Activity context, List<String> cellValue, List<String> cellLabel, List<Integer> image, List<Double> progress) {

        super(context, R.layout.list_view_cell, cellLabel);
        this.context = context;
        this.cellValue = cellValue;
        this.cellLabel = cellLabel;
        this.image = image;
        this.progress = progress;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {

        Typeface typeValues = Typeface.createFromAsset(context.getAssets(), "fonts/OpenSans-Light.ttf");
        Typeface typeLabels = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirNext-Regular.ttf");
        Typeface typeBadge = Typeface.createFromAsset(context.getAssets(), "fonts/AvenirNext-DemiBold.ttf");

        LayoutInflater inflater = context.getLayoutInflater();

        View rowView = inflater.inflate(R.layout.list_view_cell, null, true);

        ImageView imageViewIcon = (ImageView) rowView.findViewById(R.id.imageViewIcon);
        ImageView imageViewArrow = (ImageView) rowView.findViewById(R.id.imageViewArrow);

        TextView textViewLabel = (TextView) rowView.findViewById(R.id.textViewLabel);

        ImageView viewProgressBar = (ImageView) rowView.findViewById(R.id.viewProgressBar);
        RelativeLayout badgeLayout = (RelativeLayout) rowView.findViewById(R.id.badgeLayout);
        badgeLayout.setVisibility(View.INVISIBLE);
        TextView badgeCountTextView = (TextView) rowView.findViewById(R.id.badgeTextView);

        textViewLabel.setTypeface(typeLabels);
        textViewLabel.setTextColor(context.getResources().getColor(R.color.ht_gray_text));
        textViewLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);

        badgeCountTextView.setTypeface(typeBadge);
        badgeCountTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        badgeCountTextView.setTextColor(Color.WHITE);

        RelativeLayout.LayoutParams params;

        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();

        float displayDensity = displayMetrics.density;
        float screenWidth = (displayMetrics.widthPixels / displayDensity);
        float viewProgressBarWidth = ((screenWidth - 120) * displayDensity);

        // if there is a progress bar
        if (!cellLabel.get(position).equals("calories remaining") &&
                !cellLabel.get(position).equals("lbs to go") &&
                !cellLabel.get(position).equals("Messages") &&
                !cellLabel.get(position).contains("new message") &&
                !(cellValue.get(position).equals("") && progress.get(position) == 0.0)) {

            int dpValue = 12; // 12 when there's a bar
            int topMargin = (int)(dpValue * displayDensity); // margin in pixels * displayDensity (dp)

            params = (RelativeLayout.LayoutParams)textViewLabel.getLayoutParams();
            params.setMargins(0, topMargin, 0, 0); // left, top, right, bottom
            params.width = (int)viewProgressBarWidth;
            textViewLabel.setLayoutParams(params);

            params = (RelativeLayout.LayoutParams)viewProgressBar.getLayoutParams();
            params.width = (int)viewProgressBarWidth;

            Bitmap bitmap;

            if (cellLabel.get(position).equals("calories consumed")) {

                if (caloriesBarColor.equals("red")) {

                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ht_dot_red);

                } else {

                    bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ht_dot_green);
                }

            } else if (cellLabel.get(position).equals("lbs gained") || cellLabel.get(position).equals("lbs lost")) {

                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ht_dot_blue);

            } else if (cellLabel.get(position).equals("Walking Steps")) {

                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ht_dot_pink);

            } else if (cellLabel.get(position).equals("Exercise Minutes")) {

                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ht_dot_orange);

            } else if (cellLabel.get(position).equals("Sleep Hours")) {

                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ht_dot_purple);

            } else { // custom metrics

                bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.ht_dot_yellow);
            }

            viewProgressBar.setLayoutParams(params);
            viewProgressBar.setScaleType(ImageView.ScaleType.FIT_START);

            Integer tempInteger = (int)(viewProgressBarWidth * progress.get(position));

            if(tempInteger <= 0.0) {

                tempInteger = 1;

            } else if (tempInteger > viewProgressBarWidth) {

                tempInteger = (int)viewProgressBarWidth;
            }

            viewProgressBar.setImageBitmap(Bitmap.createScaledBitmap(bitmap, tempInteger, (int)(4 * displayDensity), false));

        } else { // no progress bar

            if(cellValue.get(position).equals("") && progress.get(position) == 0.0) { // unchecked metric

                int dpValue = 22;
                int topMargin = (int)(dpValue * displayDensity); // margin in pixels * displayDensity (dp)

                params = (RelativeLayout.LayoutParams)textViewLabel.getLayoutParams();
                params.setMargins(0, topMargin, 0, 0); // left, top, right, bottom
                params.width = (int)viewProgressBarWidth;
                textViewLabel.setLayoutParams(params);
            }

            params = (RelativeLayout.LayoutParams)viewProgressBar.getLayoutParams();
            params.width = 0;
            params.height = 0;

            viewProgressBar.setLayoutParams(params);

            if(cellLabel.get(position).contains("new message")) {
                badgeLayout.setVisibility(View.VISIBLE);
                badgeCountTextView.setText(numberOfMessages);
            }
        }

        if(!cellValue.get(position).equals("")) { // not a checkbox metric

//            SpannableString spannableString =  new SpannableString(cellValue.get(position));
//            spannableString.setSpan(new HTTypefaceSpan("", typeValues), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//            spannableString.setSpan(new AbsoluteSizeSpan(26, true), 0, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
//
//            textViewLabel.setText(TextUtils.concat(spannableString, " ", cellLabel.get(position).replace("-", " ")));

            SpannableString spannableString =  new SpannableString(TextUtils.concat(cellValue.get(position), " ", cellLabel.get(position).replace("-", " ")));
            spannableString.setSpan(new HTTypefaceSpan("", typeValues, PixelUtil.dpToPx(context, 26)), 0, cellValue.get(position).length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            textViewLabel.setText(spannableString);

        } else {

            textViewLabel.setText(cellLabel.get(position));
        }

        imageViewIcon.setImageResource(image.get(position));
        imageViewArrow.setImageResource(R.drawable.ht_list_view_arrow);

        return rowView;
    }

    public void setNewMessageCount(String count){
        numberOfMessages = count;
    }

    public void setCaloriesBarColor(String color){
        caloriesBarColor = color;
    }
}
