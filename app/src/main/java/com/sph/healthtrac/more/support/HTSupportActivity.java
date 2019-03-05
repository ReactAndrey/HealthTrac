package com.sph.healthtrac.more.support;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTBrowserActivity;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.more.inbox.HTInboxComposeActivity;
import com.sph.healthtrac.more.inbox.InboxActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HTSupportActivity extends Activity {

    private static RelativeLayout mainContentLayout;
    private static InputMethodManager imm;
    private LinearLayout linearLayoutSupport;

    Calendar calendar;
    private Date passDate;
    private int passYear;
    private int passMonth;
    private int passDay;
    private int currentDayOfMonth;
    private int currentMonth;
    private int currentYear;
    private Date currentDate;
    private String login;
    private String pw;

    private List<String> supportItems = new ArrayList<>();
    private List<Integer> supportItemImages = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_support);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        linearLayoutSupport = (LinearLayout) findViewById(R.id.linearLayoutSupport);

        // get the current date, if none was passed in
        calendar = Calendar.getInstance();
        currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        currentMonth = calendar.get(Calendar.MONTH);
        currentYear = calendar.get(Calendar.YEAR);

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        currentDate = calendar.getTime();

        if (HTGlobals.getInstance().passDay != 0) {

            passDate = HTGlobals.getInstance().passDate;
            passDay = HTGlobals.getInstance().passDay;
            passMonth = HTGlobals.getInstance().passMonth;
            passYear = HTGlobals.getInstance().passYear;

        } else {

            passDate = currentDate;
            passDay = currentDayOfMonth;
            passMonth = currentMonth;
            passYear = currentYear;

            HTGlobals.getInstance().passDate = currentDate;
            HTGlobals.getInstance().passDay = currentDayOfMonth;
            HTGlobals.getInstance().passMonth = currentMonth;
            HTGlobals.getInstance().passYear = currentYear;
        }

        login = HTGlobals.getInstance().passLogin;
        pw = HTGlobals.getInstance().passPw;

        RelativeLayout.LayoutParams params;

        // action bar
        View mActionBar = HTActionBar.getActionBar(this, "Support", "leftArrow", ""); // actually, message compose button

        mainContentLayout.addView(mActionBar);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        float displayDensity = displayMetrics.density;

        int dpValue = 44;
        int topBarHeight = (int) (dpValue * displayDensity);

        params = (RelativeLayout.LayoutParams) mActionBar.getLayoutParams();
        params.height = topBarHeight;

        mActionBar.setLayoutParams(params);

        // left arrow button
        mActionBar.findViewById(R.id.leftArrowAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                setResult(1);
                finish();
            }
        });

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(HTSupportActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();

        } else {
            showSupportItems();
        }

    }

    private void showSupportItems() {
        supportItems.add("VIDEO TUTORIALS");
        supportItems.add("FAQ");
        supportItems.add("CONTACT");
        supportItemImages.add(R.drawable.ht_support_video);
        supportItemImages.add(R.drawable.ht_support_faq);
        supportItemImages.add(R.drawable.ht_support_contact);

        LayoutInflater inflater = getLayoutInflater();

        View rowView = null;

        Typeface typeSupportLabels = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");

        ImageView imageViewMoreIcon;

        TextView textViewMoreLabel;

        for (int i = 0; i < supportItems.size(); i++) {
            String itemString = supportItems.get(i);

            rowView = inflater.inflate(R.layout.more_row, null, true);

            textViewMoreLabel = (TextView) rowView.findViewById(R.id.textViewMoreLabel);
            imageViewMoreIcon = (ImageView) rowView.findViewById(R.id.imageViewMoreIcon);

            // textViewMoreLabel
            textViewMoreLabel.setTypeface(typeSupportLabels);
            textViewMoreLabel.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
            textViewMoreLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);

            textViewMoreLabel.setText(itemString);

            // imageViewMoreIcon
            imageViewMoreIcon.setImageResource(supportItemImages.get(i));

            if ("VIDEO TUTORIALS".equals(itemString))
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HTSupportActivity.this, HTVideoTutorialsActivity.class);
                        startActivityForResult(intent, 1);
                    }
                });
            else if ("FAQ".equals(itemString))
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HTSupportActivity.this, HTFAQActivity.class);
                        startActivityForResult(intent, 1);
                    }
                });
            else if ("CONTACT".equals(itemString))
                rowView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(HTSupportActivity.this, HTInboxComposeActivity.class);
                        intent.putExtra("selectedMessageID", "0");
                        intent.putExtra("isSupportMessage", true);
                        startActivity(intent);
                    }
                });

            linearLayoutSupport.addView(rowView);
        }

    }
}
