package com.sph.healthtrac.tracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.common.PixelUtil;
import com.sph.healthtrac.common.XMLFunctions;
import com.sph.healthtrac.planner.HTPlannerCaloriesConsumedActivity;

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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HTMetricChartActivity extends Activity {

    private static RelativeLayout mainContentLayout;
    private ScrollView scrollView;

    LayoutInflater mInflater;
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

    //chart views
    private LinearLayout targetMetricIndicatorLayout;
    private RelativeLayout targetMetricBtn;
    private TextView targetMetricLabel;

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
    private HTMetricChartBarView[] chartBarArray1 = new HTMetricChartBarView[7];
    //chart bars for two weeks
    private HTMetricChartBarView[] chartBarArray2 = new HTMetricChartBarView[14];
    //chart bars for one month
    private HTMetricChartBarView[] chartBarArray3 = new HTMetricChartBarView[30];
    //chart bars for three months
    private HTMetricChartBarView[] chartBarArray4 = new HTMetricChartBarView[12];

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

    //metric total views
    private LinearLayout averageView;
    private TextView averageLabelView;
    private TextView averageValueView;
    private LinearLayout highestView;
    private TextView highestLabelView;
    private TextView highestValueView;
    private LinearLayout totalView;
    private TextView totalValueView;
    private TextView totalLabelView;

    //metric history layout
    private LinearLayout metricHistoryLayout;

    private float displayDensity;
    private Typeface avenirNextRegularFont;
    private Typeface avenirNextMediumFont;
    private Typeface OpenSansLightFont;

    private int selectedChart;
    private List<String> dailyTotalMetrics = new ArrayList<>();
    private List<String> metricHistoryHeaderlabels = new ArrayList<>();
    private List<String> metricHistoryHeaderValues = new ArrayList<>();
    private List<String> metricHistoryLabels = new ArrayList<>();
    private List<String> metricHistoryValues = new ArrayList<>();

    private List<String> dailyChartLabel = new ArrayList<>();

    private String selectedMetric;
    private String selectedCustomMetricString;

    private String chartMinMetric;
    private String chartMaxMetric;
    private String chartTargetMetric;
    private String chartMetricType;

    private String metricAverageOneWeek;
    private String metricAverageTwoWeeks;
    private String metricAverageOneMonth;
    private String metricAverageThreeMonths;

    private String metricHighestOneWeek;
    private String metricHighestTwoWeeks;
    private String metricHighestOneMonth;
    private String metricHighestThreeMonths;

    private String metricTotalOneWeek;
    private String metricTotalTwoWeeks;
    private String metricTotalOneMonth;
    private String metricTotalThreeMonths;

    private NumberFormat mNumberFormat;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_htmetric_chart);

        selectedMetric = getIntent().getStringExtra("selectedMetric");
        selectedCustomMetricString = getIntent().getStringExtra("selectedCustomMetricString");

        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setVisibility(View.GONE);

        mInflater = LayoutInflater.from(this);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mNumberFormat = NumberFormat.getNumberInstance(getResources().getConfiguration().locale);

        avenirNextRegularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");
        OpenSansLightFont = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");


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
        View mActionBar = HTActionBar.getActionBar(this, selectedMetric, "leftArrow", "Edit");

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

        // edit button
        mActionBar.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HTMetricChartActivity.this, HTMetricChartEditActivity.class);
                intent.putExtra("selectedMetric", selectedMetric);
                intent.putExtra("selectedCustomMetricString", selectedCustomMetricString);
                startActivityForResult(intent, 1);
            }
        });

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } else {

            targetMetricIndicatorLayout = (LinearLayout) findViewById(R.id.targetMetricIndicatorLayout);
            targetMetricBtn = (RelativeLayout) findViewById(R.id.targetMetricBtn);
            targetMetricBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!"2".equals(chartMetricType)) { // no enable for checkbox metrics
                        showUpdateChartTargetMetricDialog();
                    }
                }
            });
            targetMetricLabel = (TextView) findViewById(R.id.targetMetricLabel);
            targetMetricLabel.setTypeface(avenirNextMediumFont);

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

            for(int i = 0; i<7; i++){
                HTMetricChartBarView barView = new HTMetricChartBarView(this);
                barView.setLayoutParams(layoutParams);
                chartBarArray1[i] = barView;
                chartContentLayout1.addView(barView);
            }

            for(int i = 0; i<14; i++){
                HTMetricChartBarView barView = new HTMetricChartBarView(this);
                barView.setLayoutParams(layoutParams);
                chartBarArray2[i] = barView;
                chartContentLayout2.addView(barView);
            }

            for(int i = 0; i<30; i++){
                HTMetricChartBarView barView = new HTMetricChartBarView(this);
                barView.setLayoutParams(layoutParams);
                chartBarArray3[i] = barView;
                chartContentLayout3.addView(barView);
            }

            for(int i = 0; i<12; i++){
                HTMetricChartBarView barView = new HTMetricChartBarView(this);
                barView.setLayoutParams(layoutParams);
                chartBarArray4[i] = barView;
                chartContentLayout4.addView(barView);
            }

            xAxisLabelLayout1 = (LinearLayout) findViewById(R.id.xAxisLabelLayout1);
            xAxisLabelLayout2 = (LinearLayout) findViewById(R.id.xAxisLabelLayout2);
            xAxisLabelLayout3 = (LinearLayout) findViewById(R.id.xAxisLabelLayout3);
            xAxisLabelLayout4 = (LinearLayout) findViewById(R.id.xAxisLabelLayout4);

            for(int i = 0; i<7; i++){
                TextView textView = new TextView(this);
                textView.setTextColor(Color.parseColor("#363738"));
                textView.setTextSize(12);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(layoutParams);
                textView.setTypeface(avenirNextMediumFont);
                xAxisLabels1[i] = textView;
                xAxisLabelLayout1.addView(textView);
            }

            for(int i = 0; i<14; i++){
                TextView textView = new TextView(this);
                textView.setTextColor(Color.parseColor("#363738"));
                textView.setTextSize(9);
                textView.setGravity(Gravity.CENTER);
                textView.setLayoutParams(layoutParams);
                textView.setTypeface(avenirNextMediumFont);
                xAxisLabels2[i] = textView;
                xAxisLabelLayout2.addView(textView);
            }

            for(int i = 0; i<4; i++){
                TextView textView = new TextView(this);
                textView.setTextColor(Color.parseColor("#363738"));
                textView.setTextSize(9);
                if(i == 0) {
                    textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                    textView.setLayoutParams(layoutParams);
                }else if(i == 3) {
                    textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                    textView.setLayoutParams(layoutParams);
                }else {
                    textView.setGravity(Gravity.CENTER);
                    textView.setLayoutParams(layoutParams1);
                }
                textView.setTypeface(avenirNextMediumFont);
                xAxisLabels3[i] = textView;
                xAxisLabelLayout3.addView(textView);
            }


            for(int i = 0; i<4; i++){
                TextView textView = new TextView(this);
                textView.setTextColor(Color.parseColor("#363738"));
                textView.setTextSize(9);
                if(i == 0) {
                    textView.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);
                    textView.setLayoutParams(layoutParams);
                }else if(i == 3) {
                    textView.setGravity(Gravity.RIGHT | Gravity.CENTER_VERTICAL);
                    textView.setLayoutParams(layoutParams);
                }else {
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
                    setChartMetricTotals(0);
                }
            });
            twoWeeksBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateChartRangeSelectorView(1);
                    setChartMetricTotals(1);
                }
            });
            oneMonthBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateChartRangeSelectorView(2);
                    setChartMetricTotals(2);
                }
            });
            threeMonthsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    updateChartRangeSelectorView(3);
                    setChartMetricTotals(3);
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

            averageView = (LinearLayout) findViewById(R.id.averageView);
            averageLabelView = (TextView) findViewById(R.id.averageLabelView);
            averageValueView = (TextView) findViewById(R.id.averageValueView);
            highestView = (LinearLayout) findViewById(R.id.highestView);
            highestLabelView = (TextView) findViewById(R.id.highestLabelView);
            highestValueView = (TextView) findViewById(R.id.highestValueView);
            totalView = (LinearLayout) findViewById(R.id.totalView);
            totalValueView = (TextView) findViewById(R.id.totalValueView);
            totalLabelView = (TextView) findViewById(R.id.totalLabelView);

            averageLabelView.setTypeface(avenirNextRegularFont);
            highestLabelView.setTypeface(avenirNextRegularFont);
            totalLabelView.setTypeface(avenirNextRegularFont);
            averageValueView.setTypeface(OpenSansLightFont);
            highestValueView.setTypeface(OpenSansLightFont);
            totalValueView.setTypeface(OpenSansLightFont);

            metricHistoryLayout = (LinearLayout) findViewById(R.id.metricHistoryLayout);

            updateChartRangeSelectorView(0);

            getMetricCharts();
        }
    }

    private void getMetricCharts() {
        chartMinMetric = "";
        chartMaxMetric = "";
        chartTargetMetric = "";
        chartMetricType = "";

        metricAverageOneWeek = "";
        metricAverageTwoWeeks = "";
        metricAverageOneMonth = "";
        metricAverageThreeMonths = "";

        metricHighestOneWeek = "";
        metricHighestTwoWeeks = "";
        metricHighestOneMonth = "";
        metricHighestThreeMonths = "";

        metricTotalOneWeek = "";
        metricTotalTwoWeeks = "";
        metricTotalOneMonth = "";
        metricTotalThreeMonths = "";

        dailyTotalMetrics.clear();
        metricHistoryHeaderlabels.clear();
        metricHistoryHeaderValues.clear();
        metricHistoryLabels.clear();
        metricHistoryValues.clear();

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final String whichMetric;
        if ("Weight".equals(selectedMetric)) {
           whichMetric = "weight";
        } else if ("Walking Steps".equals(selectedMetric)) {
            whichMetric = "walking_steps";
        } else if ("Exercise Minutes".equals(selectedMetric)) {
            whichMetric = "exercise_minutes";
        } else if ("Sleep Hours".equals(selectedMetric)) {
            whichMetric = "sleep_hours";
        } else { //custom metrics
            whichMetric = selectedCustomMetricString;
        }

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_metric_charts"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("which_metric", whichMetric));

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();
                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);
                    //Log.i("responseText=",responseText);
                    NodeList error = doc.getElementsByTagName("error");
                    NodeList nodes = doc.getElementsByTagName("chart_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTMetricChartActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {

                        final Element e2 = (Element) nodes.item(0);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();

                                //Metrci type
                                if (XMLFunctions.tagExists(e2, "chart_metric_type")) {
                                    chartMetricType = XMLFunctions.getValue(e2, "chart_metric_type");
                                }

                                //min metric
                                if (XMLFunctions.tagExists(e2, "chart_min_metric")) {
                                    chartMinMetric = XMLFunctions.getValue(e2, "chart_min_metric");
                                }

                                //max metric
                                if (XMLFunctions.tagExists(e2, "chart_max_metric")) {
                                    chartMaxMetric = XMLFunctions.getValue(e2, "chart_max_metric");
                                }

                                //chart target metric
                                if (XMLFunctions.tagExists(e2, "chart_target_metric")) {
                                    chartTargetMetric = XMLFunctions.getValue(e2, "chart_target_metric");
                                }

                                String tempString;
                                int i = 1;
                                while (XMLFunctions.tagExists(e2, "chart_" + i + "_total_metric")) {
                                    tempString = XMLFunctions.getValue(e2, "chart_" + i + "_total_metric");
                                    dailyTotalMetrics.add(tempString);
                                    i++;
                                }

                                i = 1;
                                while (XMLFunctions.tagExists(e2, "chart_" + i + "_metric_history_title")) {
                                    tempString = XMLFunctions.getValue(e2, "chart_" + i + "_metric_history_title");
                                    metricHistoryLabels.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "chart_" + i + "_metric_history_value");
                                    metricHistoryValues.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "chart_" + i + "_metric_history_header_title");
                                    metricHistoryHeaderlabels.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "chart_" + i + "_metric_history_header_value");
                                    metricHistoryHeaderValues.add(tempString);
                                    i++;
                                }

                                i = 1;
                                while (XMLFunctions.tagExists(e2, "chart_" + i + "_label")) {
                                    tempString = XMLFunctions.getValue(e2, "chart_" + i + "_label");
                                    //Log.i("tempstring=",tempString);
                                    dailyChartLabel.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                if (XMLFunctions.tagExists(e2, "metric_average_one_week")) {
                                    metricAverageOneWeek = XMLFunctions.getValue(e2, "metric_average_one_week");
                                }

                                if (XMLFunctions.tagExists(e2, "metric_average_two_weeks")) {
                                    metricAverageTwoWeeks = XMLFunctions.getValue(e2, "metric_average_two_weeks");
                                }

                                if (XMLFunctions.tagExists(e2, "metric_average_one_month")) {
                                    metricAverageOneMonth = XMLFunctions.getValue(e2, "metric_average_one_month");
                                }

                                if (XMLFunctions.tagExists(e2, "metric_average_three_months")) {
                                    metricAverageThreeMonths = XMLFunctions.getValue(e2, "metric_average_three_months");
                                }

                                if (XMLFunctions.tagExists(e2, "metric_highest_one_week")) {
                                    metricHighestOneWeek = XMLFunctions.getValue(e2, "metric_highest_one_week");
                                }

                                if (XMLFunctions.tagExists(e2, "metric_highest_two_weeks")) {
                                    metricHighestTwoWeeks = XMLFunctions.getValue(e2, "metric_highest_two_weeks");
                                }

                                if (XMLFunctions.tagExists(e2, "metric_highest_one_month")) {
                                    metricHighestOneMonth = XMLFunctions.getValue(e2, "metric_highest_one_month");
                                }

                                if (XMLFunctions.tagExists(e2, "metric_highest_three_months")) {
                                    metricHighestThreeMonths = XMLFunctions.getValue(e2, "metric_highest_three_months");
                                }

                                if (XMLFunctions.tagExists(e2, "metric_total_one_week")) {
                                    metricTotalOneWeek = XMLFunctions.getValue(e2, "metric_total_one_week");
                                }

                                if (XMLFunctions.tagExists(e2, "metric_total_two_weeks")) {
                                    metricTotalTwoWeeks = XMLFunctions.getValue(e2, "metric_total_two_weeks");
                                }

                                if (XMLFunctions.tagExists(e2, "metric_total_one_month")) {
                                    metricTotalOneMonth = XMLFunctions.getValue(e2, "metric_total_one_month");
                                }

                                if (XMLFunctions.tagExists(e2, "metric_total_three_months")) {
                                    metricTotalThreeMonths = XMLFunctions.getValue(e2, "metric_total_three_months");
                                }

                                showChart();
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

//                                    toast = HTToast.showToast(HTMetricChartActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTMetricChartActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showUpdateChartTargetMetricDialog(){
        // Set up the input
        LayoutInflater li = LayoutInflater.from(this);
        View promptsView = li.inflate(R.layout.update_calories_view, null);
        final EditText input = (EditText) promptsView.findViewById(R.id.input);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
        input.setText(chartTargetMetric);
        input.setSelection(chartTargetMetric.length());
        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(this);
        final AlertDialog alert =
                builder.setTitle("Update goal?")
                        .setMessage("Enter a new " + selectedMetric + " goal")
                        .setView(promptsView)
                        .setCancelable(false)
                        .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                                chartTargetMetric = input.getText().toString();
                                updateChartTargetMetric();
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

    private void updateChartTargetMetric() {
        HTGlobals.getInstance().plannerShouldRefresh = true;
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();

        final String whichMetric;
        if ("Weight".equals(selectedMetric)) {
            whichMetric = "weight";
        } else if ("Walking Steps".equals(selectedMetric)) {
            whichMetric = "walking_steps";
        } else if ("Exercise Minutes".equals(selectedMetric)) {
            whichMetric = "exercise_minutes";
        } else if ("Sleep Hours".equals(selectedMetric)) {
            whichMetric = "sleep_hours";
        } else { //custom metrics
            whichMetric = selectedCustomMetricString;
        }

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
                    nameValuePairs.add(new BasicNameValuePair("action", "get_metric_charts_update"));
                    nameValuePairs.add(new BasicNameValuePair("metric_goal_only", "1"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("which_metric", whichMetric));
                    nameValuePairs.add(new BasicNameValuePair("metric_value", ""));
                    nameValuePairs.add(new BasicNameValuePair("metric_goal", chartTargetMetric));
                    nameValuePairs.add(new BasicNameValuePair("metric_reminder", ""));

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

                                toast = HTToast.showToast(HTMetricChartActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                getMetricCharts();
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

//                                    toast = HTToast.showToast(HTMetricChartActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTMetricChartActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    // Andrey 2019-3-6 start
    public Date addDays(Date date, int days)
    {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.add(Calendar.DATE, days); //minus number would decrement the days
        return cal.getTime();
    }
    private void setText(int type){
        //String str="3/6";
        Date c_date = new Date(System.currentTimeMillis());
        SimpleDateFormat s_format = new SimpleDateFormat("M/d");
        if(type==1) {
            //str = String.valueOf(date.getYear()) + str;
            for (int i = 6; i >= 0; i--) {
                Date myDate = addDays(c_date, -i);
                if (i % 2 == 0)
                    //s_str = s_str + s_format.format(myDate) + ",";
                    xAxisLabels1[6-i].setText(s_format.format(myDate));
                else
                    xAxisLabels1[6-i].setText("");
                    //s_str = s_str + " ,";
            }
        }
        else{
            for(int i = 13; i >= 0; i--){
                Date myDate;
                myDate = addDays(c_date, -i);
                if(i==13 || i==9 || i==4 || i==0)
                    xAxisLabels2[13-i].setText(s_format.format(myDate));
                else
                    xAxisLabels2[13-i].setText("");

            }
        }
    }
    // Andrey 2019-3-6 end
    private void showChart(){
        scrollView.setVisibility(View.VISIBLE);

        //y axis label
        int chartMaxMetricInt = Integer.parseInt(chartMaxMetric);
        int chartMinMetricInt = Integer.parseInt(chartMinMetric);

        if(!"2".equals(chartMetricType)){
            if("Weight".equals(selectedMetric)) {
                yAxisLabel1.setText(chartMaxMetric);
                yAxisLabel2.setText((((chartMaxMetricInt - chartMinMetricInt) / 4) * 3 + chartMinMetricInt) + "");
                yAxisLabel3.setText((((chartMaxMetricInt - chartMinMetricInt) / 4) * 2 + chartMinMetricInt) + "");
                yAxisLabel4.setText(((chartMaxMetricInt - chartMinMetricInt) / 4 + chartMinMetricInt) + "");
                yAxisLabel5.setText(chartMinMetric);
            } else {
                yAxisLabel1.setText(chartMaxMetric);
                yAxisLabel2.setText((chartMaxMetricInt / 4) * 3 + "");
                yAxisLabel3.setText((chartMaxMetricInt / 4) * 2 + "");
                yAxisLabel4.setText(chartMaxMetricInt / 4 + "");
                yAxisLabel5.setText("0");
            }
        }

        //x axis label
        for(int i=0; i < dailyChartLabel.size(); i++){
            Log.i("Daily =",String.valueOf(i) + ":" + dailyChartLabel.get(i));
        }

        for(int i = 0; i < 7; i++){
            //xAxisLabels1[i].setText(dailyChartLabel.get(i+23));
            setText(1);
        }
        for(int i = 0; i < 14; i++){
            //xAxisLabels2[i].setText(dailyChartLabel.get(i+16));
            setText(2);
        }
        for(int i = 0; i < 4; i++){
            xAxisLabels3[i].setText(dailyChartLabel.get(i));
        }
        for(int i = 0; i < 4; i++){
            xAxisLabels4[i].setText(dailyChartLabel.get(i+4));
        }

        float chartMaxMetricFloat = Float.parseFloat(chartMaxMetric);
        float chartMinMetricFloat = Float.parseFloat(chartMinMetric);
        float chartTargetMetricFloat = Float.parseFloat(chartTargetMetric);

        //chart bars
        for(int i = 0; i < 7; i++){
            float dailyTotalMetric = Float.parseFloat(dailyTotalMetrics.get(i+23));
            chartBarArray1[i].setChartMaxMetric(chartMaxMetricFloat);
            chartBarArray1[i].setChartMinMetric(chartMinMetricFloat);
            chartBarArray1[i].setDailyTotalMetric(dailyTotalMetric);
            chartBarArray1[i].setChartTargetMetric(chartTargetMetricFloat);
            chartBarArray1[i].setSelectedMetric(selectedMetric);
            chartBarArray1[i].invalidate();
        }

        for(int i = 0; i < 14; i++){
            float dailyTotalMetric = Float.parseFloat(dailyTotalMetrics.get(i+16));
            chartBarArray2[i].setChartMaxMetric(chartMaxMetricFloat);
            chartBarArray2[i].setChartMinMetric(chartMinMetricFloat);
            chartBarArray2[i].setDailyTotalMetric(dailyTotalMetric);
            chartBarArray2[i].setChartTargetMetric(chartTargetMetricFloat);
            chartBarArray2[i].setSelectedMetric(selectedMetric);
            chartBarArray2[i].setSelectedChart(1);
            chartBarArray2[i].invalidate();
        }

        for(int i = 0; i < 30; i++){
            float dailyTotalMetric = Float.parseFloat(dailyTotalMetrics.get(i));
            chartBarArray3[i].setChartMaxMetric(chartMaxMetricFloat);
            chartBarArray3[i].setChartMinMetric(chartMinMetricFloat);
            chartBarArray3[i].setDailyTotalMetric(dailyTotalMetric);
            chartBarArray3[i].setChartTargetMetric(chartTargetMetricFloat);
            chartBarArray3[i].setSelectedMetric(selectedMetric);
            chartBarArray3[i].setSelectedChart(2);
            chartBarArray3[i].invalidate();
        }

        for(int i = 0; i < 12; i++){
            float dailyTotalMetric = Float.parseFloat(dailyTotalMetrics.get(i+30));
            chartBarArray4[i].setChartMaxMetric(chartMaxMetricFloat);
            chartBarArray4[i].setChartMinMetric(chartMinMetricFloat);
            chartBarArray4[i].setDailyTotalMetric(dailyTotalMetric);
            chartBarArray4[i].setChartTargetMetric(chartTargetMetricFloat);
            chartBarArray4[i].setSelectedMetric(selectedMetric);
            chartBarArray4[i].setSelectedChart(3);
            chartBarArray4[i].invalidate();
        }

        if("2".equals(chartMetricType)) {
            averageView.setVisibility(View.GONE);
            highestView.setVisibility(View.GONE);
            totalLabelView.setText("Total");

        } else {
            if("Weight".equals(selectedMetric))
                totalLabelView.setText("Lowest");
            else
                totalLabelView.setText("Total");
        }

        setChartMetricTotals(selectedChart);

        //chartMetricHistory
        if (metricHistoryLayout.getChildCount() > 0)
            metricHistoryLayout.removeAllViews();

        LinearLayout.LayoutParams linearParams;

        for(int i = metricHistoryLabels.size()-1; i >= 0; i--) {
            // the headers - metricHistoryHeaderLabel
            if(!"".equals(metricHistoryHeaderlabels.get(i))) {
                View whiteSeperator = new View(this);
                whiteSeperator.setBackgroundColor(Color.WHITE);
                linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (1 * displayDensity));
                linearParams.topMargin = (int) (-1 * displayDensity);
                whiteSeperator.setLayoutParams(linearParams);
                metricHistoryLayout.addView(whiteSeperator);

                RelativeLayout headerView = (RelativeLayout) mInflater.inflate(R.layout.metric_history_header_view, null);
                TextView headerLabelView = (TextView) headerView.findViewById(R.id.headerLabelView);
                headerLabelView.setTypeface(avenirNextMediumFont);
                headerLabelView.setText(metricHistoryHeaderlabels.get(i));
                if(!"Weight".equals(selectedMetric)) {
                    TextView headerValueView = (TextView) headerView.findViewById(R.id.headerValueView);
                    headerValueView.setTypeface(avenirNextMediumFont);
                    headerValueView.setText(mNumberFormat.format(Double.parseDouble(metricHistoryHeaderValues.get(i))));
                }

                linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (28 * displayDensity));
                headerView.setLayoutParams(linearParams);
                metricHistoryLayout.addView(headerView);
            }

            RelativeLayout contentView = (RelativeLayout) mInflater.inflate(R.layout.metric_history_content_view, null);
            TextView contentLabelView = (TextView) contentView.findViewById(R.id.contentLabelView);
            TextView contentValueView = (TextView) contentView.findViewById(R.id.contentValueView);
            contentLabelView.setTypeface(avenirNextRegularFont);
            contentValueView.setTypeface(avenirNextRegularFont);
            contentLabelView.setText(metricHistoryLabels.get(i));
            contentValueView.setText(mNumberFormat.format(Double.parseDouble(metricHistoryValues.get(i))));

            linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (44 * displayDensity));
            contentView.setLayoutParams(linearParams);
            metricHistoryLayout.addView(contentView);

            View graySeperator = new View(this);
            graySeperator.setBackgroundColor(Color.rgb(217, 227, 231));
            linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (1 * displayDensity));
            linearParams.topMargin = (int) (-1 * displayDensity);
            linearParams.leftMargin = (int) (16 * displayDensity);
            graySeperator.setLayoutParams(linearParams);
            metricHistoryLayout.addView(graySeperator);

            // add the target calories indicator arrow where applicable
            if(!"".equals(chartTargetMetric) && chartTargetMetricFloat > 0) {
                targetMetricIndicatorLayout.setVisibility(View.VISIBLE);
                if(!"2".equals(chartMetricType)) { // no labels for checkbox metrics
                    targetMetricLabel.setText(chartTargetMetric);
                }

                float targetCaloriesBtnYOffset = ((chartMaxMetricFloat - chartTargetMetricFloat) / (chartMaxMetricFloat - chartMinMetricFloat)) * 192;

                FrameLayout.LayoutParams layoutParams = (FrameLayout.LayoutParams) targetMetricIndicatorLayout.getLayoutParams();
                layoutParams.topMargin = (int) (targetCaloriesBtnYOffset * displayDensity);
                targetMetricIndicatorLayout.setLayoutParams(layoutParams);
            } else {
                targetMetricIndicatorLayout.setVisibility(View.GONE);
            }
        }
    }

    private void setChartMetricTotals(int whichChart){
        switch (whichChart){
            case 0:
                averageValueView.setText(mNumberFormat.format(Double.parseDouble(metricAverageOneWeek)));
                highestValueView.setText(mNumberFormat.format(Double.parseDouble(metricHighestOneWeek)));
                totalValueView.setText(mNumberFormat.format(Double.parseDouble(metricTotalOneWeek)));
                break;
            case 1:
                averageValueView.setText(mNumberFormat.format(Double.parseDouble(metricAverageTwoWeeks)));
                highestValueView.setText(mNumberFormat.format(Double.parseDouble(metricHighestTwoWeeks)));
                totalValueView.setText(mNumberFormat.format(Double.parseDouble(metricTotalTwoWeeks)));
                break;
            case 2:
                averageValueView.setText(mNumberFormat.format(Double.parseDouble(metricAverageOneMonth)));
                highestValueView.setText(mNumberFormat.format(Double.parseDouble(metricHighestOneMonth)));
                totalValueView.setText(mNumberFormat.format(Double.parseDouble(metricTotalOneMonth)));
                break;
            case 3:
                averageValueView.setText(mNumberFormat.format(Double.parseDouble(metricAverageThreeMonths)));
                highestValueView.setText(mNumberFormat.format(Double.parseDouble(metricHighestThreeMonths)));
                totalValueView.setText(mNumberFormat.format(Double.parseDouble(metricTotalThreeMonths)));
                break;
            default:
                break;
        }
    }

    private void updateChartRangeSelectorView(int whichChart){
        selectedChart = whichChart;
        switch (whichChart){
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

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // coming back from a child activity, refresh

        passDate = HTGlobals.getInstance().passDate;
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;

        getMetricCharts();
    }
}
