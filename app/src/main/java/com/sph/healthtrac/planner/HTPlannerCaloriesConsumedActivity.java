package com.sph.healthtrac.planner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentProvider;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTDatePicker;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.common.HTTypefaceSpan;
import com.sph.healthtrac.common.PixelUtil;
import com.sph.healthtrac.common.XMLFunctions;
import com.sph.healthtrac.planner.addfood.HTAddFoodSearchResultsActivity;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HTPlannerCaloriesConsumedActivity extends Activity {
    private static RelativeLayout mainContentLayout;

    private LinearLayout dayButton;
    private TextView dayLabelView;
    private View dayIndicator;
    private LinearLayout weekButton;
    private TextView weekLabelView;
    private View weekIndicator;

    private boolean showingDay = true;
    private boolean ignoreCaloriesBurned;

    private static InputMethodManager imm;
    private Calendar calendar;
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

    private Thread myThread = null;
    DefaultHttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;

    Toast toast;

    private RelativeLayout dailyConsumedView;
    private LinearLayout dailyContentView;
    private RelativeLayout weeklyConsumedView;
    private LinearLayout weeklyContentView;

    private View mActionBar;

    //day view
    private View datePicker;
    private TextView mDateTitleTextView;

    private TextView goalLabel;
    private TextView goalValue;
    private TextView consumedLabel;
    private TextView consumedValue;
    private TextView exerciseLabel;
    private TextView exerciseValue;
    private TextView remainingLabel;
    private TextView remainingValue;
    private TextView minusSignView;
    private TextView plusSignView;
    private TextView equalSignView;

    private TextView summaryLabel;
    private TextView carbsLabel;
    private TextView carbsValue;
    private TextView fiberLabel;
    private TextView fiberValue;
    private TextView sugarsLabel;
    private TextView sugarsValue;
    private TextView proteinLabel;
    private TextView proteinValue;
    private TextView totalFatLabel;
    private TextView totalFatValue;
    private TextView satfatLabel;
    private TextView satfatValue;
    private TextView sodiumLabel;
    private TextView sodiumValue;

    private LinearLayout caloriesPercentLayout;
    private TextView carbsPercentValueView;
    private TextView carbsLabelView;
    private TextView proteinPercentValueView;
    private TextView proteinLabelView;
    private TextView fatPercentValueView;
    private TextView fatLabelView;
    private View carbsPercentBar;
    private View proteinPercentBar;
    private View fatPercentBar;

    //week view
    private FrameLayout chartLayout;

    private LinearLayout targetCaloriesIndicatorLayout;
    private RelativeLayout targetCaloriesBtn;
    private TextView targetCaloriesLabel;

    private TextView yAxisLabel1;
    private TextView yAxisLabel2;
    private TextView yAxisLabel3;
    private TextView yAxisLabel4;
    private TextView yAxisLabel5;

    private LinearLayout chartContentLayout1;
    private LinearLayout chartContentLayout2;
    private LinearLayout chartContentLayout3;
    private LinearLayout chartContentLayout4;

    //chart bars for one week
    private HTPlannerCaloriesChartBarView[] chartBarArray1 = new HTPlannerCaloriesChartBarView[7];
    //chart bars for two weeks
    private HTPlannerCaloriesChartBarView[] chartBarArray2 = new HTPlannerCaloriesChartBarView[14];
    //chart bars for one month
    private HTPlannerCaloriesChartBarView[] chartBarArray3 = new HTPlannerCaloriesChartBarView[30];
    //chart bars for three months
    private HTPlannerCaloriesChartBarView[] chartBarArray4 = new HTPlannerCaloriesChartBarView[12];

    private LinearLayout xAxisLabelLayout1;
    private LinearLayout xAxisLabelLayout2;
    private LinearLayout xAxisLabelLayout3;
    private LinearLayout xAxisLabelLayout4;

    //x axis labels for one week
    private TextView[] xAxisLabels1 = new TextView[7];
    //x axis labels for two weeks
    private TextView[] xAxisLabels2 = new TextView[14];
    //x axis labels for one month
    private TextView[] xAxisLabels3 = new TextView[4];
    //x axis labels for three months
    private TextView[] xAxisLabels4 = new TextView[4];

    private LinearLayout oneWeekBtn;
    private LinearLayout twoWeeksBtn;
    private LinearLayout oneMonthBtn;
    private LinearLayout threeMonthsBtn;
    private TextView oneWeekLabel;
    private TextView twoWeeksLabel;
    private TextView oneMonthLabel;
    private TextView threeMonthsLabel;
    private View oneWeekIndicator;
    private View twoWeeksIndicator;
    private View oneMonthIndicator;
    private View threeMonthsIndicator;

    private TextView goalLabelView;
    private TextView avgLabelView;
    private TextView carbsMacroLabel;
    private TextView carbsAvgValueView;
    private TextView carbsGoalValueView;
    private TextView proteinMacroLabel;
    private TextView proteinAvgValueView;
    private TextView proteinGoalValueView;
    private TextView fatMacroLabel;
    private TextView fatAvgValueView;
    private TextView fatGoalValueView;

    private String plannerName;
    private String plannerCalories;
    private String plannerProtein;
    private String plannerCarbs;
    private String plannerFiber;
    private String plannerSugar;
    private String plannerSodium;
    private String plannerFat;
    private String plannerSatFat;
    private String plannerCaloriesBurned;
    private String plannerTargetCalories;
    private String plannerMaxCalories;
    private String plannerCarbsPercentage;
    private String plannerProteinPercentage;
    private String plannerFatPercentage;

    private String carbsOneWeek;
    private String carbsTwoWeeks;
    private String carbsOneMonth;
    private String carbsThreeMonths;

    private String proteinOneWeek;
    private String proteinTwoWeeks;
    private String proteinOneMonth;
    private String proteinThreeMonths;

    private String fatOneWeek;
    private String fatTwoWeeks;
    private String fatOneMonth;
    private String fatThreeMonths;

    private String carbsGoal;
    private String proteinGoal;
    private String fatGoal;

    private int selectedChart;

    private List<String> dailyTotalCalories = new ArrayList<>();
    private List<String> dailyMacroCalories = new ArrayList<>();
    private List<String> dailyCarbsPercentages = new ArrayList<>();
    private List<String> dailyProteinPercentages = new ArrayList<>();
    private List<String> dailyFatPercentages = new ArrayList<>();
    private List<String> dailyChartLabel = new ArrayList<>();

    private float displayDensity;
    private Typeface avenirNextRegularFont;
    private Typeface avenirNextDemiBoldFont;
    private Typeface avenirNextMediumFont;
    private Typeface OpenSansLightFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner_calories_consumed);

        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        avenirNextRegularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        avenirNextDemiBoldFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-DemiBold.ttf");
        avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");
        OpenSansLightFont = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");

        dayButton = (LinearLayout) findViewById(R.id.dayButton);
        weekButton = (LinearLayout) findViewById(R.id.weekButton);
        dayLabelView = (TextView) findViewById(R.id.dayLabelView);
        weekLabelView = (TextView) findViewById(R.id.weekLabelView);
        dayIndicator = (View) findViewById(R.id.dayIndicator);
        weekIndicator = (View) findViewById(R.id.weekIndicator);
        dayLabelView.setTypeface(avenirNextDemiBoldFont);
        weekLabelView.setTypeface(avenirNextDemiBoldFont);

        dayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopThread();
                dayIndicator.setVisibility(View.VISIBLE);
                weekIndicator.setVisibility(View.GONE);
                showingDay = true;
                getNutrition();
            }
        });

        weekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopThread();
                dayIndicator.setVisibility(View.GONE);
                weekIndicator.setVisibility(View.VISIBLE);
                showingDay = false;
                getNutrition();
            }
        });

        dailyConsumedView = (RelativeLayout) findViewById(R.id.dailyConsumedView);
        weeklyConsumedView = (RelativeLayout) findViewById(R.id.weeklyConsumedView);
        dailyContentView = (LinearLayout) findViewById(R.id.dailyContentView);
        weeklyContentView = (LinearLayout) findViewById(R.id.weeklyContentView);

        dailyContentView.setVisibility(View.GONE);
        weeklyConsumedView.setVisibility(View.GONE);

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
        View mActionBar = HTActionBar.getActionBar(this, "Calories Consumed", "leftArrow", "");

        mainContentLayout.addView(mActionBar);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        displayDensity = displayMetrics.density;

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
            //daily content view
            // date picker
            datePicker = HTDatePicker.getDatePicker(this);
            datePicker.findViewById(R.id.viewGraySeparator).setVisibility(View.GONE);
            dailyConsumedView.addView(datePicker);

            params = (RelativeLayout.LayoutParams) datePicker.getLayoutParams();
            params.height = topBarHeight;
            datePicker.setLayoutParams(params);

            final ImageView leftArrow = (ImageView) datePicker.findViewById(R.id.leftArrow);
            final ImageView rightArrow = (ImageView) datePicker.findViewById(R.id.rightArrow);
            mDateTitleTextView = (TextView) datePicker.findViewById(R.id.date_text);

            leftArrow.setImageResource(R.drawable.ht_arrow_left_blue);
            rightArrow.setImageResource(R.drawable.ht_arrow_right_blue);

            leftArrow.setOnClickListener(new ImageView.OnClickListener() {

                @Override
                public void onClick(View v) {

                    leftDateButtonClicked();
                }
            });

            rightArrow.setOnClickListener(new ImageView.OnClickListener() {

                @Override
                public void onClick(View v) {

                    rightDateButtonClicked();
                }
            });


            goalLabel = (TextView) findViewById(R.id.goalLabel);
            goalValue = (TextView) findViewById(R.id.goalValue);
            goalValue.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showUpdatePlannerTargetCaloriesDialog();
                }
            });

            consumedLabel = (TextView) findViewById(R.id.consumedLabel);
            consumedValue = (TextView) findViewById(R.id.consumedValue);
            exerciseLabel = (TextView) findViewById(R.id.exerciseLabel);
            exerciseValue = (TextView) findViewById(R.id.exerciseValue);
            remainingLabel = (TextView) findViewById(R.id.remainingLabel);
            remainingValue = (TextView) findViewById(R.id.remainingValue);
            minusSignView = (TextView) findViewById(R.id.minusSignView);
            minusSignView.setText(String.format("%C", 0x2014));
            plusSignView = (TextView) findViewById(R.id.plusSignView);
            equalSignView = (TextView) findViewById(R.id.equalSignView);

            summaryLabel = (TextView) findViewById(R.id.summaryLabel);
            carbsLabel = (TextView) findViewById(R.id.carbsLabel);
            carbsValue = (TextView) findViewById(R.id.carbsValue);
            fiberLabel = (TextView) findViewById(R.id.fiberLabel);
            fiberValue = (TextView) findViewById(R.id.fiberValue);
            sugarsLabel = (TextView) findViewById(R.id.sugarsLabel);
            sugarsValue = (TextView) findViewById(R.id.sugarsValue);
            proteinLabel = (TextView) findViewById(R.id.proteinLabel);
            proteinValue = (TextView) findViewById(R.id.proteinValue);
            totalFatLabel = (TextView) findViewById(R.id.totalFatLabel);
            totalFatValue = (TextView) findViewById(R.id.totalFatValue);
            satfatLabel = (TextView) findViewById(R.id.satfatLabel);
            satfatValue = (TextView) findViewById(R.id.satfatValue);
            sodiumLabel = (TextView) findViewById(R.id.sodiumLabel);
            sodiumValue = (TextView) findViewById(R.id.sodiumValue);

            caloriesPercentLayout = (LinearLayout) findViewById(R.id.caloriesPercentLayout);
            caloriesPercentLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HTPlannerCaloriesConsumedActivity.this, HTPlannerMacroDetailsActivity.class);
                    startActivityForResult(intent, 1);
                }
            });
            carbsPercentValueView = (TextView) findViewById(R.id.carbsPercentValueView);
            proteinPercentValueView = (TextView) findViewById(R.id.proteinPercentValueView);
            fatPercentValueView = (TextView) findViewById(R.id.fatPercentValueView);
            carbsLabelView = (TextView) findViewById(R.id.carbsLabelView);
            proteinLabelView = (TextView) findViewById(R.id.proteinLabelView);
            fatLabelView = (TextView) findViewById(R.id.fatLabelView);
            carbsPercentBar = findViewById(R.id.carbsPercentBar);
            proteinPercentBar = findViewById(R.id.proteinPercentBar);
            fatPercentBar = findViewById(R.id.fatPercentBar);

            goalLabel.setTypeface(avenirNextRegularFont);
            consumedLabel.setTypeface(avenirNextRegularFont);
            exerciseLabel.setTypeface(avenirNextRegularFont);
            remainingLabel.setTypeface(avenirNextRegularFont);
            goalValue.setTypeface(avenirNextRegularFont);
            consumedValue.setTypeface(avenirNextRegularFont);
            exerciseValue.setTypeface(avenirNextRegularFont);
            remainingValue.setTypeface(avenirNextRegularFont);
            minusSignView.setTypeface(avenirNextMediumFont);
            plusSignView.setTypeface(avenirNextMediumFont);
            equalSignView.setTypeface(avenirNextMediumFont);
            summaryLabel.setTypeface(avenirNextMediumFont);
            carbsLabel.setTypeface(avenirNextMediumFont);
            fiberLabel.setTypeface(avenirNextRegularFont);
            sugarsLabel.setTypeface(avenirNextRegularFont);
            proteinLabel.setTypeface(avenirNextMediumFont);
            totalFatLabel.setTypeface(avenirNextMediumFont);
            satfatLabel.setTypeface(avenirNextRegularFont);
            sodiumLabel.setTypeface(avenirNextMediumFont);
            carbsValue.setTypeface(avenirNextMediumFont);
            fiberValue.setTypeface(avenirNextRegularFont);
            sugarsValue.setTypeface(avenirNextRegularFont);
            proteinValue.setTypeface(avenirNextMediumFont);
            totalFatValue.setTypeface(avenirNextMediumFont);
            satfatValue.setTypeface(avenirNextRegularFont);
            sodiumValue.setTypeface(avenirNextMediumFont);
            carbsPercentValueView.setTypeface(OpenSansLightFont);
            proteinPercentValueView.setTypeface(OpenSansLightFont);
            fatPercentValueView.setTypeface(OpenSansLightFont);
            carbsLabelView.setTypeface(avenirNextMediumFont);
            proteinLabelView.setTypeface(avenirNextMediumFont);
            fatLabelView.setTypeface(avenirNextMediumFont);

            //weekly content view
            chartLayout = (FrameLayout) findViewById(R.id.chartLayout);
            targetCaloriesIndicatorLayout = (LinearLayout) findViewById(R.id.targetCaloriesIndicatorLayout);
            targetCaloriesBtn = (RelativeLayout) findViewById(R.id.targetCaloriesBtn);
            targetCaloriesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showUpdatePlannerTargetCaloriesDialog();
                }
            });
            targetCaloriesLabel = (TextView) findViewById(R.id.targetCaloriesLabel);
            targetCaloriesLabel.setTypeface(avenirNextMediumFont);

            yAxisLabel1 = (TextView) findViewById(R.id.yAxisLabel1);
            yAxisLabel2 = (TextView) findViewById(R.id.yAxisLabel2);
            yAxisLabel3 = (TextView) findViewById(R.id.yAxisLabel3);
            yAxisLabel4 = (TextView) findViewById(R.id.yAxisLabel4);
            yAxisLabel5 = (TextView) findViewById(R.id.yAxisLabel5);
            yAxisLabel1.setTypeface(avenirNextMediumFont);
            yAxisLabel2.setTypeface(avenirNextMediumFont);
            yAxisLabel3.setTypeface(avenirNextMediumFont);
            yAxisLabel4.setTypeface(avenirNextMediumFont);
            yAxisLabel5.setTypeface(avenirNextMediumFont);

            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.1f);
            LinearLayout.LayoutParams layoutParams1 = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT, 0.2f);

            chartContentLayout1 = (LinearLayout) findViewById(R.id.chartContentLayout1);
            chartContentLayout2 = (LinearLayout) findViewById(R.id.chartContentLayout2);
            chartContentLayout3 = (LinearLayout) findViewById(R.id.chartContentLayout3);
            chartContentLayout4 = (LinearLayout) findViewById(R.id.chartContentLayout4);

            for (int i = 0; i < 7; i++) {
                HTPlannerCaloriesChartBarView barView = new HTPlannerCaloriesChartBarView(this);
                barView.setLayoutParams(layoutParams);
                chartBarArray1[i] = barView;
                chartContentLayout1.addView(barView);
            }

            for (int i = 0; i < 14; i++) {
                HTPlannerCaloriesChartBarView barView = new HTPlannerCaloriesChartBarView(this);
                barView.setLayoutParams(layoutParams);
                chartBarArray2[i] = barView;
                chartContentLayout2.addView(barView);
            }

            for (int i = 0; i < 30; i++) {
                HTPlannerCaloriesChartBarView barView = new HTPlannerCaloriesChartBarView(this);
                barView.setLayoutParams(layoutParams);
                chartBarArray3[i] = barView;
                chartContentLayout3.addView(barView);
            }

            for (int i = 0; i < 12; i++) {
                HTPlannerCaloriesChartBarView barView = new HTPlannerCaloriesChartBarView(this);
                barView.setLayoutParams(layoutParams);
                chartBarArray4[i] = barView;
                chartContentLayout4.addView(barView);
            }

            xAxisLabelLayout1 = (LinearLayout) findViewById(R.id.xAxisLabelLayout1);
            xAxisLabelLayout2 = (LinearLayout) findViewById(R.id.xAxisLabelLayout2);
            xAxisLabelLayout3 = (LinearLayout) findViewById(R.id.xAxisLabelLayout3);
            xAxisLabelLayout4 = (LinearLayout) findViewById(R.id.xAxisLabelLayout4);

            for (int i = 0; i < 7; i++) {
                TextView textView = new TextView(this);
                textView.setTextColor(Color.parseColor("#363738"));
                textView.setTextSize(12);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(layoutParams);
                textView.setTypeface(avenirNextMediumFont);
                xAxisLabels1[i] = textView;
                xAxisLabelLayout1.addView(textView);
            }

            for (int i = 0; i < 14; i++) {
                TextView textView = new TextView(this);
                textView.setTextColor(Color.parseColor("#363738"));
                textView.setTextSize(9);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(layoutParams);
                textView.setTypeface(avenirNextMediumFont);
                xAxisLabels2[i] = textView;
                xAxisLabelLayout2.addView(textView);
            }

            for (int i = 0; i < 4; i++) {
                TextView textView = new TextView(this);
                textView.setTextColor(Color.parseColor("#363738"));
                textView.setTextSize(9);
                if (i == 0) {
                    textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                    textView.setLayoutParams(layoutParams);
                } else if (i == 3) {
                    textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                    textView.setLayoutParams(layoutParams);
                } else {
                    textView.setGravity(Gravity.CENTER);
                    textView.setLayoutParams(layoutParams1);
                }
                textView.setTypeface(avenirNextMediumFont);
                xAxisLabels3[i] = textView;
                xAxisLabelLayout3.addView(textView);
            }


            for (int i = 0; i < 4; i++) {
                TextView textView = new TextView(this);
                textView.setTextColor(Color.parseColor("#363738"));
                textView.setTextSize(9);
                if (i == 0) {
                    textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                    textView.setLayoutParams(layoutParams);
                } else if (i == 3) {
                    textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                    textView.setLayoutParams(layoutParams);
                } else {
                    textView.setGravity(Gravity.CENTER);
                    textView.setLayoutParams(layoutParams1);
                }
                textView.setTypeface(avenirNextMediumFont);
                xAxisLabels4[i] = textView;
                xAxisLabelLayout4.addView(textView);
            }

            oneWeekBtn = (LinearLayout) findViewById(R.id.oneWeekBtn);
            twoWeeksBtn = (LinearLayout) findViewById(R.id.twoWeeksBtn);
            oneMonthBtn = (LinearLayout) findViewById(R.id.oneMonthBtn);
            threeMonthsBtn = (LinearLayout) findViewById(R.id.threeMonthsBtn);
            oneWeekBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateChartRangeSelectorView(0);
                    setMacroPercentageValue(0);
                }
            });
            twoWeeksBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateChartRangeSelectorView(1);
                    setMacroPercentageValue(1);
                }
            });
            oneMonthBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateChartRangeSelectorView(2);
                    setMacroPercentageValue(2);
                }
            });
            threeMonthsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateChartRangeSelectorView(3);
                    setMacroPercentageValue(3);
                }
            });
            oneWeekLabel = (TextView) findViewById(R.id.oneWeekLabel);
            twoWeeksLabel = (TextView) findViewById(R.id.twoWeeksLabel);
            oneMonthLabel = (TextView) findViewById(R.id.oneMonthLabel);
            threeMonthsLabel = (TextView) findViewById(R.id.threeMonthsLabel);
            oneWeekLabel.setTypeface(avenirNextMediumFont);
            twoWeeksLabel.setTypeface(avenirNextMediumFont);
            oneMonthLabel.setTypeface(avenirNextMediumFont);
            threeMonthsLabel.setTypeface(avenirNextMediumFont);

            oneWeekIndicator = findViewById(R.id.oneWeekIndicator);
            twoWeeksIndicator = findViewById(R.id.twoWeeksIndicator);
            oneMonthIndicator = findViewById(R.id.oneMonthIndicator);
            threeMonthsIndicator = findViewById(R.id.threeMonthsIndicator);

            goalLabelView = (TextView) findViewById(R.id.goalLabelView);
            avgLabelView = (TextView) findViewById(R.id.avgLabelView);
            carbsMacroLabel = (TextView) findViewById(R.id.carbsMacroLabel);
            carbsAvgValueView = (TextView) findViewById(R.id.carbsAvgValueView);
            carbsGoalValueView = (TextView) findViewById(R.id.carbsGoalValueView);
            proteinMacroLabel = (TextView) findViewById(R.id.proteinMacroLabel);
            proteinAvgValueView = (TextView) findViewById(R.id.proteinAvgValueView);
            proteinGoalValueView = (TextView) findViewById(R.id.proteinGoalValueView);
            fatMacroLabel = (TextView) findViewById(R.id.fatMacroLabel);
            fatAvgValueView = (TextView) findViewById(R.id.fatAvgValueView);
            fatGoalValueView = (TextView) findViewById(R.id.fatGoalValueView);
            goalLabelView.setTypeface(avenirNextMediumFont);
            avgLabelView.setTypeface(avenirNextMediumFont);
            carbsMacroLabel.setTypeface(avenirNextMediumFont);
            carbsAvgValueView.setTypeface(avenirNextMediumFont);
            carbsGoalValueView.setTypeface(avenirNextMediumFont);
            proteinMacroLabel.setTypeface(avenirNextMediumFont);
            proteinAvgValueView.setTypeface(avenirNextMediumFont);
            proteinGoalValueView.setTypeface(avenirNextMediumFont);
            fatMacroLabel.setTypeface(avenirNextMediumFont);
            fatAvgValueView.setTypeface(avenirNextMediumFont);
            fatGoalValueView.setTypeface(avenirNextMediumFont);

            updateChartRangeSelectorView(0);

            //get nutrition
            getNutrition();

        }

    }

    private void getNutrition() {

        plannerName = "";
        plannerCalories = "";
        plannerProtein = "";
        plannerCarbs = "";
        plannerFiber = "";
        plannerSugar = "";
        plannerSodium = "";
        plannerFat = "";
        plannerSatFat = "";
        plannerCaloriesBurned = "";
        plannerTargetCalories = "";
        plannerMaxCalories = "";
        plannerCarbsPercentage = "";
        plannerProteinPercentage = "";
        plannerFatPercentage = "";

        carbsOneWeek = "";
        carbsTwoWeeks = "";
        carbsOneMonth = "";
        carbsThreeMonths = "";

        proteinOneWeek = "";
        proteinTwoWeeks = "";
        proteinOneMonth = "";
        proteinThreeMonths = "";

        fatOneWeek = "";
        fatTwoWeeks = "";
        fatOneMonth = "";
        fatThreeMonths = "";

        carbsGoal = "";
        proteinGoal = "";
        fatGoal = "";

        dailyTotalCalories.clear();
        dailyMacroCalories.clear();
        dailyCarbsPercentages.clear();
        dailyProteinPercentages.clear();
        dailyFatPercentages.clear();
        dailyChartLabel.clear();

        ignoreCaloriesBurned = false;

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        if (currentDate.equals(passDate)) {
            // Today
            mDateTitleTextView.setText("Today");
        } else if (currentDate.equals(HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, 1, false))) {
            // Yesterday
            mDateTitleTextView.setText("Yesterday");

        } else {
            // other past dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            calendar = Calendar.getInstance();
            calendar.setTime(passDate);

            mDateTitleTextView.setText(dateFormat.format(calendar.getTime()));
        }

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    if (showingDay)
                        nameValuePairs.add(new BasicNameValuePair("action", "get_planner_nutrition"));
                    else
                        nameValuePairs.add(new BasicNameValuePair("action", "get_planner_nutrition_charts"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();
                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);

                    NodeList error = doc.getElementsByTagName("error");
                    NodeList nodes = doc.getElementsByTagName("planner_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTPlannerCaloriesConsumedActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {

                        final Element e2 = (Element) nodes.item(0);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();

                                //planner name
                                if (XMLFunctions.tagExists(e2, "planner_name")) {
                                    plannerName = htGlobals.cleanStringAfterReceiving(XMLFunctions.getValue(e2, "planner_name"));
                                }

                                //planner calories
                                if (XMLFunctions.tagExists(e2, "planner_calories")) {
                                    plannerCalories = XMLFunctions.getValue(e2, "planner_calories");
                                }

                                //planner protein
                                if (XMLFunctions.tagExists(e2, "planner_protein")) {
                                    plannerProtein = XMLFunctions.getValue(e2, "planner_protein");
                                }

                                //planner carbs
                                if (XMLFunctions.tagExists(e2, "planner_carbs")) {
                                    plannerCarbs = XMLFunctions.getValue(e2, "planner_carbs");
                                }

                                //planner fiber
                                if (XMLFunctions.tagExists(e2, "planner_fiber")) {
                                    plannerFiber = XMLFunctions.getValue(e2, "planner_fiber");
                                }

                                //planner sugar
                                if (XMLFunctions.tagExists(e2, "planner_sugar")) {
                                    plannerSugar = XMLFunctions.getValue(e2, "planner_sugar");
                                }

                                //planner sodium
                                if (XMLFunctions.tagExists(e2, "planner_sodium")) {
                                    plannerSodium = XMLFunctions.getValue(e2, "planner_sodium");
                                }

                                //planner fat
                                if (XMLFunctions.tagExists(e2, "planner_fat")) {
                                    plannerFat = XMLFunctions.getValue(e2, "planner_fat");
                                }

                                //planner sat fat
                                if (XMLFunctions.tagExists(e2, "planner_sat_fat")) {
                                    plannerSatFat = XMLFunctions.getValue(e2, "planner_sat_fat");
                                }

                                //planner target calories
                                if (XMLFunctions.tagExists(e2, "planner_target_calories")) {
                                    plannerTargetCalories = XMLFunctions.getValue(e2, "planner_target_calories");
                                }

                                //planner max calories
                                if (XMLFunctions.tagExists(e2, "planner_max_calories")) {
                                    plannerMaxCalories = XMLFunctions.getValue(e2, "planner_max_calories");
                                }

                                //planner calories burned
                                if (XMLFunctions.tagExists(e2, "planner_calories_burned")) {
                                    plannerCaloriesBurned = XMLFunctions.getValue(e2, "planner_calories_burned");
                                }

                                if (XMLFunctions.tagExists(e2, "planner_carbs_percentage")) {
                                    plannerCarbsPercentage = XMLFunctions.getValue(e2, "planner_carbs_percentage");
                                }

                                if (XMLFunctions.tagExists(e2, "planner_protein_percentage")) {
                                    plannerProteinPercentage = XMLFunctions.getValue(e2, "planner_protein_percentage");
                                }

                                if (XMLFunctions.tagExists(e2, "planner_fat_percentage")) {
                                    plannerFatPercentage = XMLFunctions.getValue(e2, "planner_fat_percentage");
                                }

                                String tempString;
                                int i = 1;
                                while (XMLFunctions.tagExists(e2, "planner_" + i + "_total_calories")) {
                                    tempString = XMLFunctions.getValue(e2, "planner_" + i + "_total_calories");
                                    dailyTotalCalories.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "planner_" + i + "_macro_calories");
                                    dailyMacroCalories.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "planner_" + i + "_carbs_percentage");
                                    dailyCarbsPercentages.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "planner_" + i + "_protein_percentage");
                                    dailyProteinPercentages.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "planner_" + i + "_fat_percentage");
                                    dailyFatPercentages.add(tempString);
                                    i++;
                                }

                                i = 1;
                                while (XMLFunctions.tagExists(e2, "planner_" + i + "_label")) {
                                    tempString = XMLFunctions.getValue(e2, "planner_" + i + "_label");
                                    dailyChartLabel.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                if (XMLFunctions.tagExists(e2, "carbs_percentage_one_week")) {
                                    carbsOneWeek = XMLFunctions.getValue(e2, "carbs_percentage_one_week");
                                }

                                if (XMLFunctions.tagExists(e2, "carbs_percentage_two_weeks")) {
                                    carbsTwoWeeks = XMLFunctions.getValue(e2, "carbs_percentage_two_weeks");
                                }

                                if (XMLFunctions.tagExists(e2, "carbs_percentage_one_month")) {
                                    carbsOneMonth = XMLFunctions.getValue(e2, "carbs_percentage_one_month");
                                }

                                if (XMLFunctions.tagExists(e2, "carbs_percentage_three_months")) {
                                    carbsThreeMonths = XMLFunctions.getValue(e2, "carbs_percentage_three_months");
                                }

                                if (XMLFunctions.tagExists(e2, "protein_percentage_one_week")) {
                                    proteinOneWeek = XMLFunctions.getValue(e2, "protein_percentage_one_week");
                                }

                                if (XMLFunctions.tagExists(e2, "protein_percentage_two_weeks")) {
                                    proteinTwoWeeks = XMLFunctions.getValue(e2, "protein_percentage_two_weeks");
                                }

                                if (XMLFunctions.tagExists(e2, "protein_percentage_one_month")) {
                                    proteinOneMonth = XMLFunctions.getValue(e2, "protein_percentage_one_month");
                                }

                                if (XMLFunctions.tagExists(e2, "protein_percentage_three_months")) {
                                    proteinThreeMonths = XMLFunctions.getValue(e2, "protein_percentage_three_months");
                                }

                                if (XMLFunctions.tagExists(e2, "fat_percentage_one_week")) {
                                    fatOneWeek = XMLFunctions.getValue(e2, "fat_percentage_one_week");
                                }

                                if (XMLFunctions.tagExists(e2, "fat_percentage_two_weeks")) {
                                    fatTwoWeeks = XMLFunctions.getValue(e2, "fat_percentage_two_weeks");
                                }

                                if (XMLFunctions.tagExists(e2, "fat_percentage_one_month")) {
                                    fatOneMonth = XMLFunctions.getValue(e2, "fat_percentage_one_month");
                                }

                                if (XMLFunctions.tagExists(e2, "fat_percentage_three_months")) {
                                    fatThreeMonths = XMLFunctions.getValue(e2, "fat_percentage_three_months");
                                }

                                if (XMLFunctions.tagExists(e2, "carbs_goal")) {
                                    carbsGoal = XMLFunctions.getValue(e2, "carbs_goal");
                                }

                                if (XMLFunctions.tagExists(e2, "protein_goal")) {
                                    proteinGoal = XMLFunctions.getValue(e2, "protein_goal");
                                }

                                if (XMLFunctions.tagExists(e2, "fat_goal")) {
                                    fatGoal = XMLFunctions.getValue(e2, "fat_goal");
                                }

                                if (XMLFunctions.tagExists(e2, "ignore_calories_burned")) {
                                    tempString = XMLFunctions.getValue(e2, "ignore_calories_burned");
                                    if ("1".equals(tempString)) {
                                        ignoreCaloriesBurned = true;
                                    } else {
                                        ignoreCaloriesBurned = false;
                                    }
                                }

                                if (showingDay) {
                                    showNutrition();
                                } else {
                                    showChart();
                                }
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    // these all happen with the post.abort() but can be ignored, as the call will be executed again
                    if (error != null) {
                        if (!error.equals("Connection already shutdown") && !error.equals("Request already aborted") && !error.equals("No peer certificate") && !error.equals("Request must not be null.") && !error.equals("Connection has been shut down.") && !error.equals("Connection to https://www.setpointhealth.com refused") && !error.equals("Connection to https://www.trackmyday.org refused")) {

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

//                                    toast = HTToast.showToast(HTPlannerCaloriesConsumedActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTPlannerCaloriesConsumedActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                                }
                            });

                        }
                    }

                } finally {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            progressDialog.dismiss();
                        }
                    });
                }
            }
        };

        myThread.start();
    }

    private void showUpdatePlannerTargetCaloriesDialog() {
        // Set up the input
        LayoutInflater li = LayoutInflater.from(HTPlannerCaloriesConsumedActivity.this);
        View promptsView = li.inflate(R.layout.update_calories_view, null);
        final EditText input = (EditText) promptsView.findViewById(R.id.input);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        input.setText(plannerTargetCalories);
        input.setSelection(plannerTargetCalories.length());
        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(HTPlannerCaloriesConsumedActivity.this);
        final AlertDialog alert =
                builder.setTitle(R.string.update_calories_goal)
                        .setMessage(getResources()
                                .getString(R.string.enter_new_calories_goal))
                        .setView(promptsView)
                        .setCancelable(false)
                        .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                                plannerTargetCalories = input.getText().toString();
                                updatePlannerTargetCalories();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                            }
                        }).create();

        input.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().length() == 0) {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
                } else {
                    alert.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);
                }
            }
        });
        alert.getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        alert.show();
    }

    private void updatePlannerTargetCalories() {
        HTGlobals.getInstance().plannerShouldRefresh = true;
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);

        myThread = new Thread() {

            @Override
            public void run() {
                try {
                    sleep(150);
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                    return;
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.show();
                    }
                });

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "update_planner_target_calories"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("plan_calories", plannerTargetCalories));

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();
                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);

                    NodeList error = doc.getElementsByTagName("error");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTPlannerCaloriesConsumedActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                getNutrition();
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    // these all happen with the post.abort() but can be ignored, as the call will be executed again
                    if (error != null) {
                        if (!error.equals("Connection already shutdown") && !error.equals("Request already aborted") && !error.equals("No peer certificate") && !error.equals("Request must not be null.") && !error.equals("Connection has been shut down.") && !error.equals("Connection to https://www.setpointhealth.com refused") && !error.equals("Connection to https://www.trackmyday.org refused")) {

                            runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

//                                    toast = HTToast.showToast(HTPlannerCaloriesConsumedActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTPlannerCaloriesConsumedActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                                }
                            });

                        }
                    }

                } finally {

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            progressDialog.dismiss();
                        }
                    });
                }
            }
        };

        myThread.start();

    }

    private void stopThread() {

        if (myThread != null) {

            new Thread() {

                @Override
                public void run() {

                    if (post != null) {

                        post.abort();
                        post = null;
                        response = null;
                        entity = null;
                    }
                }
            }.start();

            myThread.interrupt();
            myThread = null;
        }
    }

    private void leftDateButtonClicked() {

        stopThread();

        // subtract one day
        passDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, -1, true);
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;
        HTGlobals.getInstance().plannerShouldRefresh = true;
        // getNutrition
        getNutrition();
    }

    private void rightDateButtonClicked() {

        stopThread();

        // add one day
        passDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, 1, true);
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;
        HTGlobals.getInstance().plannerShouldRefresh = true;
        // getNutrition
        getNutrition();
    }

    private void showNutrition() {
        dailyConsumedView.setVisibility(View.VISIBLE);
        weeklyConsumedView.setVisibility(View.GONE);

        if (ignoreCaloriesBurned) {
            minusSignView.setVisibility(View.INVISIBLE);
            plusSignView.setVisibility(View.INVISIBLE);
            equalSignView.setVisibility(View.INVISIBLE);
        } else {
            minusSignView.setVisibility(View.VISIBLE);
            plusSignView.setVisibility(View.VISIBLE);
            equalSignView.setVisibility(View.VISIBLE);
        }

        goalValue.setText(plannerTargetCalories);
        consumedValue.setText(plannerCalories);
        exerciseValue.setText(plannerCaloriesBurned);
        int caloriesRemaining = 0;
        try {
            if(ignoreCaloriesBurned)
                caloriesRemaining = Integer.parseInt(plannerTargetCalories) - Integer.parseInt(plannerCalories);
            else
                caloriesRemaining = Integer.parseInt(plannerTargetCalories) - Integer.parseInt(plannerCalories) + Integer.parseInt(plannerCaloriesBurned);
            remainingValue.setText(String.valueOf(caloriesRemaining));
        } catch (NumberFormatException e) {
            remainingValue.setText("");
        }

        if (caloriesRemaining < 0) {
            remainingValue.setTextColor(Color.parseColor("#ee6b73"));
        } else {
            remainingValue.setTextColor(Color.parseColor("#c1e991"));
        }

        carbsValue.setText(plannerCarbs + "g");
        fiberValue.setText(plannerFiber + "g");
        sugarsValue.setText(plannerSugar + "g");
        proteinValue.setText(plannerProtein + "g");
        totalFatValue.setText(plannerFat + "g");
        satfatValue.setText(plannerSatFat + "g");
        sodiumValue.setText(plannerSodium + "mg");

        //percent label
//        plannerCarbsPercentage = "25.5";
//        plannerProteinPercentage = "47.5";
//        plannerFatPercentage = "27";
        SpannableString spannableString = new SpannableString(plannerCarbsPercentage + "%");
        spannableString.setSpan(new HTTypefaceSpan("", OpenSansLightFont, PixelUtil.dpToPx(this, 16)), plannerCarbsPercentage.length(), plannerCarbsPercentage.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        carbsPercentValueView.setText(spannableString);
        spannableString = new SpannableString(plannerProteinPercentage + "%");
        spannableString.setSpan(new HTTypefaceSpan("", OpenSansLightFont, PixelUtil.dpToPx(this, 16)), plannerProteinPercentage.length(), plannerProteinPercentage.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        proteinPercentValueView.setText(spannableString);
        spannableString = new SpannableString(plannerFatPercentage + "%");
        spannableString.setSpan(new HTTypefaceSpan("", OpenSansLightFont, PixelUtil.dpToPx(this, 16)), plannerFatPercentage.length(), plannerFatPercentage.length() + 1, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        fatPercentValueView.setText(spannableString);

        //percent bars(carbs, protein, fat)
        LinearLayout.LayoutParams layoutParams;
        float carbsPercent = 0.0f;
        try {
            carbsPercent = Float.parseFloat(plannerCarbsPercentage);
        } catch (NumberFormatException e) {

        }

        layoutParams = (LinearLayout.LayoutParams) carbsPercentBar.getLayoutParams();
        if (carbsPercent > 0)
            layoutParams.weight = carbsPercent / 100;
        else
            layoutParams.weight = 0.0f;
        carbsPercentBar.setLayoutParams(layoutParams);

        float proteinPercent = 0.0f;
        try {
            proteinPercent = Float.parseFloat(plannerProteinPercentage);
        } catch (NumberFormatException e) {

        }

        layoutParams = (LinearLayout.LayoutParams) proteinPercentBar.getLayoutParams();
        if (proteinPercent > 0)
            layoutParams.weight = proteinPercent / 100;
        else
            layoutParams.weight = 0.0f;
        proteinPercentBar.setLayoutParams(layoutParams);

        float fatPercent = 0.0f;
        try {
            fatPercent = Float.parseFloat(plannerFatPercentage);
        } catch (NumberFormatException e) {

        }

        layoutParams = (LinearLayout.LayoutParams) fatPercentBar.getLayoutParams();
        if (fatPercent > 0)
            layoutParams.weight = fatPercent / 100;
        else
            layoutParams.weight = 0.0f;
        fatPercentBar.setLayoutParams(layoutParams);

        if (dailyContentView.getVisibility() == View.GONE)
            dailyContentView.setVisibility(View.VISIBLE);
    }

    private void updateChartRangeSelectorView(int whichChart) {
        selectedChart = whichChart;
        switch (whichChart) {
            case 0:
                oneWeekLabel.setTextColor(Color.parseColor("#363738"));
                twoWeeksLabel.setTextColor(Color.parseColor("#61363738"));
                oneMonthLabel.setTextColor(Color.parseColor("#61363738"));
                threeMonthsLabel.setTextColor(Color.parseColor("#61363738"));
                oneWeekIndicator.setVisibility(View.VISIBLE);
                twoWeeksIndicator.setVisibility(View.INVISIBLE);
                oneMonthIndicator.setVisibility(View.INVISIBLE);
                threeMonthsIndicator.setVisibility(View.INVISIBLE);
                xAxisLabelLayout1.setVisibility(View.VISIBLE);
                xAxisLabelLayout2.setVisibility(View.INVISIBLE);
                xAxisLabelLayout3.setVisibility(View.INVISIBLE);
                xAxisLabelLayout4.setVisibility(View.INVISIBLE);
                chartContentLayout1.setVisibility(View.VISIBLE);
                chartContentLayout2.setVisibility(View.INVISIBLE);
                chartContentLayout3.setVisibility(View.INVISIBLE);
                chartContentLayout4.setVisibility(View.INVISIBLE);
                break;
            case 1:
                oneWeekLabel.setTextColor(Color.parseColor("#61363738"));
                twoWeeksLabel.setTextColor(Color.parseColor("#363738"));
                oneMonthLabel.setTextColor(Color.parseColor("#61363738"));
                threeMonthsLabel.setTextColor(Color.parseColor("#61363738"));
                oneWeekIndicator.setVisibility(View.INVISIBLE);
                twoWeeksIndicator.setVisibility(View.VISIBLE);
                oneMonthIndicator.setVisibility(View.INVISIBLE);
                threeMonthsIndicator.setVisibility(View.INVISIBLE);
                xAxisLabelLayout1.setVisibility(View.INVISIBLE);
                xAxisLabelLayout2.setVisibility(View.VISIBLE);
                xAxisLabelLayout3.setVisibility(View.INVISIBLE);
                xAxisLabelLayout4.setVisibility(View.INVISIBLE);
                chartContentLayout1.setVisibility(View.INVISIBLE);
                chartContentLayout2.setVisibility(View.VISIBLE);
                chartContentLayout3.setVisibility(View.INVISIBLE);
                chartContentLayout4.setVisibility(View.INVISIBLE);
                break;
            case 2:
                oneWeekLabel.setTextColor(Color.parseColor("#61363738"));
                twoWeeksLabel.setTextColor(Color.parseColor("#61363738"));
                oneMonthLabel.setTextColor(Color.parseColor("#363738"));
                threeMonthsLabel.setTextColor(Color.parseColor("#61363738"));
                oneWeekIndicator.setVisibility(View.INVISIBLE);
                twoWeeksIndicator.setVisibility(View.INVISIBLE);
                oneMonthIndicator.setVisibility(View.VISIBLE);
                threeMonthsIndicator.setVisibility(View.INVISIBLE);
                xAxisLabelLayout1.setVisibility(View.INVISIBLE);
                xAxisLabelLayout2.setVisibility(View.INVISIBLE);
                xAxisLabelLayout3.setVisibility(View.VISIBLE);
                xAxisLabelLayout4.setVisibility(View.INVISIBLE);
                chartContentLayout1.setVisibility(View.INVISIBLE);
                chartContentLayout2.setVisibility(View.INVISIBLE);
                chartContentLayout3.setVisibility(View.VISIBLE);
                chartContentLayout4.setVisibility(View.INVISIBLE);
                break;
            case 3:
                oneWeekLabel.setTextColor(Color.parseColor("#61363738"));
                twoWeeksLabel.setTextColor(Color.parseColor("#61363738"));
                oneMonthLabel.setTextColor(Color.parseColor("#61363738"));
                threeMonthsLabel.setTextColor(Color.parseColor("#363738"));
                oneWeekIndicator.setVisibility(View.INVISIBLE);
                twoWeeksIndicator.setVisibility(View.INVISIBLE);
                oneMonthIndicator.setVisibility(View.INVISIBLE);
                threeMonthsIndicator.setVisibility(View.VISIBLE);
                xAxisLabelLayout1.setVisibility(View.INVISIBLE);
                xAxisLabelLayout2.setVisibility(View.INVISIBLE);
                xAxisLabelLayout3.setVisibility(View.INVISIBLE);
                xAxisLabelLayout4.setVisibility(View.VISIBLE);
                chartContentLayout1.setVisibility(View.INVISIBLE);
                chartContentLayout2.setVisibility(View.INVISIBLE);
                chartContentLayout3.setVisibility(View.INVISIBLE);
                chartContentLayout4.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void setMacroPercentageValue(int whichChart) {
        switch (whichChart) {
            case 0:
                carbsAvgValueView.setText(carbsOneWeek + "%");
                proteinAvgValueView.setText(proteinOneWeek + "%");
                fatAvgValueView.setText(fatOneWeek + "%");
                break;
            case 1:
                carbsAvgValueView.setText(carbsTwoWeeks + "%");
                proteinAvgValueView.setText(proteinTwoWeeks + "%");
                fatAvgValueView.setText(fatTwoWeeks + "%");
                break;
            case 2:
                carbsAvgValueView.setText(carbsOneMonth + "%");
                proteinAvgValueView.setText(proteinOneMonth + "%");
                fatAvgValueView.setText(fatOneMonth + "%");
                break;
            case 3:
                carbsAvgValueView.setText(carbsThreeMonths + "%");
                proteinAvgValueView.setText(proteinThreeMonths + "%");
                fatAvgValueView.setText(fatThreeMonths + "%");
                break;
            default:
                break;
        }

        if (!"".equals(carbsGoal)) {
            carbsGoalValueView.setText(carbsGoal + "%");
            proteinGoalValueView.setText(proteinGoal + "%");
            fatGoalValueView.setText(fatGoal + "%");
        }
    }

    private void showChart() {
        weeklyConsumedView.setVisibility(View.VISIBLE);
        dailyConsumedView.setVisibility(View.GONE);

        //y axis label
        int plannerMaxCaloriesInt = Integer.parseInt(plannerMaxCalories);

        yAxisLabel1.setText(plannerMaxCalories);
        yAxisLabel2.setText((plannerMaxCaloriesInt / 4) * 3 + "");
        yAxisLabel3.setText((plannerMaxCaloriesInt / 4) * 2 + "");
        yAxisLabel4.setText(plannerMaxCaloriesInt / 4 + "");
        yAxisLabel5.setText("0");

        //x axis label
//        for(int i=0; i < dailyChartLabel.size(); i++){
//            Log.i("Daily =",String.valueOf(i) + ":" + dailyChartLabel.get(i));
//        }
        for (int i = 0; i < 7; i++) {
            xAxisLabels1[i].setText(dailyChartLabel.get(i + 23));
        }
        for (int i = 0; i < 14; i++) {
            xAxisLabels2[i].setText(dailyChartLabel.get(i + 16));
        }
        for (int i = 0; i < 4; i++) {
            xAxisLabels3[i].setText(dailyChartLabel.get(i));
        }
        for (int i = 0; i < 4; i++) {
            xAxisLabels4[i].setText(dailyChartLabel.get(i + 4));
        }

        //chart bars
        for (int i = 0; i < 7; i++) {
            float dailyTotalCalorie = Float.parseFloat(dailyTotalCalories.get(i + 23));
            float dailyMacroCalorie = Float.parseFloat(dailyMacroCalories.get(i + 23));
            float dailyCarbsPercentage = Float.parseFloat(dailyCarbsPercentages.get(i + 23));
            float dailyProteinPercentage = Float.parseFloat(dailyProteinPercentages.get(i + 23));
            float dailyFatPercentage = Float.parseFloat(dailyFatPercentages.get(i + 23));
            chartBarArray1[i].setPlannerMaxCalorie(plannerMaxCaloriesInt);
            chartBarArray1[i].setDailyTotalCalorie(dailyTotalCalorie);
            chartBarArray1[i].setDailyMacroCalorie(dailyMacroCalorie);
            chartBarArray1[i].setDailyCarbsPercentage(dailyCarbsPercentage);
            chartBarArray1[i].setDailyProteinPercentage(dailyProteinPercentage);
            chartBarArray1[i].setDailyFatPercentage(dailyFatPercentage);
            chartBarArray1[i].invalidate();
        }

        for (int i = 0; i < 14; i++) {
            float dailyTotalCalorie = Float.parseFloat(dailyTotalCalories.get(i + 16));
            float dailyMacroCalorie = Float.parseFloat(dailyMacroCalories.get(i + 16));
            float dailyCarbsPercentage = Float.parseFloat(dailyCarbsPercentages.get(i + 16));
            float dailyProteinPercentage = Float.parseFloat(dailyProteinPercentages.get(i + 16));
            float dailyFatPercentage = Float.parseFloat(dailyFatPercentages.get(i + 16));
            chartBarArray2[i].setPlannerMaxCalorie(plannerMaxCaloriesInt);
            chartBarArray2[i].setDailyTotalCalorie(dailyTotalCalorie);
            chartBarArray2[i].setDailyMacroCalorie(dailyMacroCalorie);
            chartBarArray2[i].setDailyCarbsPercentage(dailyCarbsPercentage);
            chartBarArray2[i].setDailyProteinPercentage(dailyProteinPercentage);
            chartBarArray2[i].setDailyFatPercentage(dailyFatPercentage);
            chartBarArray2[i].setSelectedChart(1);
            chartBarArray2[i].invalidate();
        }

        for (int i = 0; i < 30; i++) {
            float dailyTotalCalorie = Float.parseFloat(dailyTotalCalories.get(i));
            float dailyMacroCalorie = Float.parseFloat(dailyMacroCalories.get(i));
            float dailyCarbsPercentage = Float.parseFloat(dailyCarbsPercentages.get(i));
            float dailyProteinPercentage = Float.parseFloat(dailyProteinPercentages.get(i));
            float dailyFatPercentage = Float.parseFloat(dailyFatPercentages.get(i));
            chartBarArray3[i].setPlannerMaxCalorie(plannerMaxCaloriesInt);
            chartBarArray3[i].setDailyTotalCalorie(dailyTotalCalorie);
            chartBarArray3[i].setDailyMacroCalorie(dailyMacroCalorie);
            chartBarArray3[i].setDailyCarbsPercentage(dailyCarbsPercentage);
            chartBarArray3[i].setDailyProteinPercentage(dailyProteinPercentage);
            chartBarArray3[i].setDailyFatPercentage(dailyFatPercentage);
            chartBarArray3[i].setSelectedChart(2);
            chartBarArray3[i].invalidate();
        }

        for (int i = 0; i < 12; i++) {
            float dailyTotalCalorie = Float.parseFloat(dailyTotalCalories.get(i + 30));
            float dailyMacroCalorie = Float.parseFloat(dailyMacroCalories.get(i + 30));
            float dailyCarbsPercentage = Float.parseFloat(dailyCarbsPercentages.get(i + 30));
            float dailyProteinPercentage = Float.parseFloat(dailyProteinPercentages.get(i + 30));
            float dailyFatPercentage = Float.parseFloat(dailyFatPercentages.get(i + 30));
            chartBarArray4[i].setPlannerMaxCalorie(plannerMaxCaloriesInt);
            chartBarArray4[i].setDailyTotalCalorie(dailyTotalCalorie);
            chartBarArray4[i].setDailyMacroCalorie(dailyMacroCalorie);
            chartBarArray4[i].setDailyCarbsPercentage(dailyCarbsPercentage);
            chartBarArray4[i].setDailyProteinPercentage(dailyProteinPercentage);
            chartBarArray4[i].setDailyFatPercentage(dailyFatPercentage);
            chartBarArray4[i].setSelectedChart(3);
            chartBarArray4[i].invalidate();
        }

        // agv and goal labels
        if ("".equals(carbsGoal)) { // there are no goals
            goalLabelView.setVisibility(View.GONE);
            carbsGoalValueView.setVisibility(View.GONE);
            proteinGoalValueView.setVisibility(View.GONE);
            fatGoalValueView.setVisibility(View.GONE);
        }

        setMacroPercentageValue(selectedChart);

        // add the target calories indicator arrow where applicable
        targetCaloriesLabel.setText(plannerTargetCalories);

        float plannerMaxCaloriesFloat = Float.parseFloat(plannerMaxCalories);
        float plannerTargetCaloriesFloat = Float.parseFloat(plannerTargetCalories);
        float targetCaloriesBtnYOffset = ((plannerMaxCaloriesFloat - plannerTargetCaloriesFloat) / plannerMaxCaloriesFloat) * 192;

        FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) targetCaloriesIndicatorLayout.getLayoutParams();
        layoutParams.topMargin = (int) (targetCaloriesBtnYOffset * displayDensity);
        targetCaloriesIndicatorLayout.setLayoutParams(layoutParams);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // coming back from a child activity, refresh the calendar

        passDate = HTGlobals.getInstance().passDate;
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;

        getNutrition();
    }

}
