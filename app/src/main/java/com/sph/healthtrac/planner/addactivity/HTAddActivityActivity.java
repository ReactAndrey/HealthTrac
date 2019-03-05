package com.sph.healthtrac.planner.addactivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTGlobals;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import java.util.Calendar;
import java.util.Date;

public class HTAddActivityActivity extends Activity {

    private static RelativeLayout mainContentLayout;

    private RelativeLayout myFavoritesLayout;
    private RelativeLayout exerciseLayout;
    private RelativeLayout stressManagementLayout;
    private RelativeLayout noteLayout;

    private TextView myFavoritesLabel;
    private TextView exerciseLabel;
    private TextView stressManagementLabel;
    private TextView noteLabel;

    private static InputMethodManager imm;
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

    HttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;
    Toast toast;
    private Thread myThread = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addactivity);

        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        myFavoritesLayout = (RelativeLayout) findViewById(R.id.myFavoritesLayout);
        exerciseLayout = (RelativeLayout) findViewById(R.id.exerciseLayout);
        stressManagementLayout = (RelativeLayout) findViewById(R.id.stressManagementLayout);
        noteLayout = (RelativeLayout) findViewById(R.id.noteLayout);

        myFavoritesLabel = (TextView) findViewById(R.id.myFavoritesLabel);
        exerciseLabel = (TextView) findViewById(R.id.exerciseLabel);
        stressManagementLabel = (TextView) findViewById(R.id.stressManagementLabel);
        noteLabel = (TextView) findViewById(R.id.noteLabel);

        Typeface labelFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        myFavoritesLabel.setTypeface(labelFont);
        exerciseLabel.setTypeface(labelFont);
        stressManagementLabel.setTypeface(labelFont);
        noteLabel.setTypeface(labelFont);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

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
        View mActionBar = HTActionBar.getActionBar(this, "Add Activity", "leftArrow", "");

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

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            myFavoritesLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HTAddActivityActivity.this, HTAddActivitySearchActivity.class);
                    intent.putExtra("addActivityCategory", "favorites");
                    startActivityForResult(intent, 1);
                }
            });

            exerciseLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HTAddActivityActivity.this, HTAddActivitySearchActivity.class);
                    intent.putExtra("addActivityCategory", "exercise");
                    startActivityForResult(intent, 1);
                }
            });

            stressManagementLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HTAddActivityActivity.this, HTAddActivitySelectItemActivity.class);
                    intent.putExtra("addActivityCategory", "stress");
                    startActivityForResult(intent, 1);
                }
            });

            noteLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HTAddActivityActivity.this, HTAddActivitySelectItemActivity.class);
                    intent.putExtra("addActivityCategory", "note");
                    startActivityForResult(intent, 1);
                }
            });
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // coming back from a child activity, refresh the calendar

        if (resultCode == 2) {
            setResult(2);
            finish();
        }
    }

}
