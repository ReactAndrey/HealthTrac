package com.sph.healthtrac.dashboard;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.planner.HTPlannerCaloriesConsumedActivity;
import com.sph.healthtrac.tracker.HTMetricChartActivity;
import com.sph.healthtrac.tracker.activitytracker.ActivityTrackerActivity;
import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTDatePicker;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTTabBar;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
import com.sph.healthtrac.common.XMLFunctions;
import com.sph.healthtrac.more.inbox.InboxActivity;

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
import java.util.Locale;
import java.util.Set;

public class DashboardActivity extends Fragment {

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

    ListView listViewDashboard;

    Toast toast;

    private String calories = "";
    private String caloriesGoal = "";
    private String caloriesBurned = "";
    private String weight = "";
    private String weightStarting = "";
    private String weightGoal = "";
    private String weightOfficial = "";
    private String walkingSteps = "";
    private String walkingStepsGoal = "";
    private String walkingStepsOfficial = "";
    private String exerciseMinutes = "";
    private String exerciseMinutesGoal = "";
    private String exerciseMinutesOfficial = "";
    private String sleepHours = "";
    private String sleepHoursGoal = "";
    private String sleepHoursOfficial = "";
    private String numberOfMessages = "";
    private String newLearningModules = "";
    private String newEatingPlans = "";

    private String selectedMetric;
    private String selectedCustomMetricString;

    private boolean showMessages = false;
    private boolean ignoreCaloriesBurned;
    private boolean shouldShowAdminDashboard;
    private boolean showAdminDashboard;
    private boolean adminDashboardHasLoaded;
    private String dashboardStatus;

    private String externalLoginURL;
    private String adminDashboardURL;

    private List<String> customMetrics = new ArrayList<>();
    private List<String> customMetricsTypes = new ArrayList<>();
    private List<String> customMetricsLabels = new ArrayList<>();
    private List<String> customMetricsGoals = new ArrayList<>();
    private List<String> customMetricsOfficial = new ArrayList<>();

    private List<String> dashboardItems = new ArrayList<>();
    private List<String> dashboardItemValues = new ArrayList<>();
    private List<Integer> dashboardItemImages = new ArrayList<>();
    private List<Double> dashboardItemProgress = new ArrayList<>();

    private List<String> final_dashboardItems = new ArrayList<>();
    private List<String> final_dashboardItemValues = new ArrayList<>();
    private List<Integer> final_dashboardItemImages = new ArrayList<>();
    private List<Double> final_dashboardItemProgress = new ArrayList<>();

    private ArrayList<String> dashboardEditItems = new ArrayList<>();
    private List<String> dashboardUserSort = new ArrayList<>();


    FragmentActivity fragmentActivity;
    RelativeLayout relativeLayout;
    View mActionBar;

    private RelativeLayout userDashboardLayout;
    private RelativeLayout adminDashboardLayout;
    private WebView adminDashboardWebView;

    private AnimatorSet mInAnimation;
    private AnimatorSet mOutAnimation;

    View datePicker;

    Calendar calendar;

    DefaultHttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;

    private Thread myThread = null;

    HTListView adapter;

    SharedPreferences mSharedPreferences;
    SharedPreferences.Editor mSharedPreferencesEditor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentActivity = super.getActivity();

        mSharedPreferences = fragmentActivity.getSharedPreferences("SPHPrefs", Activity.MODE_PRIVATE);
        mSharedPreferencesEditor = mSharedPreferences.edit();
        dashboardStatus = mSharedPreferences.getString("dashboardStatus", "admin");

        HTGlobals.getInstance().plannerShouldRefresh = true;

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

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(fragmentActivity, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            fragmentActivity.finish();

        } else {

            if(relativeLayout == null) {
                relativeLayout = (RelativeLayout) inflater.inflate(R.layout.activity_dashboard, container, false);

                userDashboardLayout = (RelativeLayout) relativeLayout.findViewById(R.id.userDashboardView);
                adminDashboardLayout = (RelativeLayout) relativeLayout.findViewById(R.id.adminDashboardView);
                adminDashboardWebView = (WebView) relativeLayout.findViewById(R.id.adminDashboardWebView);
                adminDashboardWebView.setWebChromeClient(new WebChromeClient());
                adminDashboardWebView.setWebViewClient(new MyWebViewClient());

                RelativeLayout.LayoutParams params;

                // action bar
                mActionBar = HTActionBar.getActionBar(fragmentActivity, getString(R.string.app_name), "leftArrow", "Edit");

                relativeLayout.addView(mActionBar);

                DisplayMetrics displayMetrics = fragmentActivity.getResources().getDisplayMetrics();

                float displayDensity = displayMetrics.density;

                int dpValue = 44;
                int topBarHeight = (int) (dpValue * displayDensity);

                params = (RelativeLayout.LayoutParams) mActionBar.getLayoutParams();
                params.height = topBarHeight;

                mActionBar.setLayoutParams(params);

                mActionBar.findViewById(R.id.leftArrowAction).setVisibility(View.GONE);
                // Edit button
                mActionBar.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        editDashboard();
                    }
                });

                // date picker
                datePicker = HTDatePicker.getDatePicker(fragmentActivity);

                userDashboardLayout.addView(datePicker);

                params = (RelativeLayout.LayoutParams) datePicker.getLayoutParams();
                params.height = topBarHeight;
//        params.setMargins(0, topBarHeight, 0, 0);  // left, top, right, bottom

                datePicker.setLayoutParams(params);

                // set the tab bar label font colors
                TextView tabBarLabel;

                tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.dashboardText);
                tabBarLabel.setTextColor(Color.WHITE);

                tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.trackText);
                tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));

                if (!HTGlobals.getInstance().hidePlanner) {
                    tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.planText);
                    tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));
                }

                tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.learnText);
                tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));

                tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.moreText);
                tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));
            } else {
                container.removeView(relativeLayout);
            }

            adapter = new HTListView(fragmentActivity, final_dashboardItemValues, final_dashboardItems, final_dashboardItemImages, final_dashboardItemProgress);
            getDashboard();
        }

        return relativeLayout;
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

            dashboardItems.clear();
            dashboardItemValues.clear();
            dashboardItemImages.clear();
            dashboardItemProgress.clear();
            final_dashboardItems.clear();
            final_dashboardItemValues.clear();
            final_dashboardItemImages.clear();
            final_dashboardItemProgress.clear();

            customMetrics.clear();
            customMetricsTypes.clear();
            customMetricsLabels.clear();
            customMetricsGoals.clear();
            customMetricsOfficial.clear();

            dashboardEditItems.clear();

            adapter.notifyDataSetChanged();
        }
    }

    private void editDashboard() {
        Intent intent = new Intent(fragmentActivity, HTDashboardEditActivity.class);
        intent.putStringArrayListExtra("dashboardEditItems", dashboardEditItems);
        startActivityForResult(intent, 1);
    }

    private void leftDateButtonClicked() {

        stopThread();

        // subtract one day
        passDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, -1, true);
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;
        HTGlobals.getInstance().plannerShouldRefresh = true;
        getDashboard();
    }

    private void rightDateButtonClicked() {

        stopThread();

        // add one day
        passDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, 1, true);
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;
        HTGlobals.getInstance().plannerShouldRefresh = true;
        getDashboard();
    }

    private void getDashboard() {

        numberOfMessages = "";
        newLearningModules = "";
        newEatingPlans = "";

        selectedMetric = "";
        selectedCustomMetricString = "";

        ignoreCaloriesBurned = false;
        shouldShowAdminDashboard = false;

        dashboardItems.clear();
        dashboardItemValues.clear();
        dashboardItemImages.clear();
        dashboardItemProgress.clear();

        final_dashboardItems.clear();
        final_dashboardItemValues.clear();
        final_dashboardItemImages.clear();
        final_dashboardItemProgress.clear();

        customMetrics.clear();
        customMetricsTypes.clear();
        customMetricsLabels.clear();
        customMetricsGoals.clear();
        customMetricsOfficial.clear();

        dashboardUserSort.clear();
        dashboardEditItems.clear();

        adapter.notifyDataSetChanged();

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(fragmentActivity);
        progressDialog.show();

        final ImageView leftArrow = (ImageView) datePicker.findViewById(R.id.leftArrow);
        final ImageView rightArrow = (ImageView) datePicker.findViewById(R.id.rightArrow);

        if (passDate.getTime() > currentDate.getTime()) {

            // get the current date
            calendar = Calendar.getInstance();
            currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            currentMonth = calendar.get(Calendar.MONTH);
            currentYear = calendar.get(Calendar.YEAR);

            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            currentDate = calendar.getTime();

            passDate = currentDate;
            passDay = currentDayOfMonth;
            passMonth = currentMonth;
            passYear = currentYear;

            HTGlobals.getInstance().passDate = currentDate;
            HTGlobals.getInstance().passDay = currentDayOfMonth;
            HTGlobals.getInstance().passMonth = currentMonth;
            HTGlobals.getInstance().passYear = currentYear;
        }

        if (currentDate.equals(passDate)) {

            // Today
            TextView mTitleTextView = (TextView) datePicker.findViewById(R.id.date_text);
            mTitleTextView.setText("Today");

            leftArrow.setImageResource(R.drawable.ht_arrow_left_blue);
            rightArrow.setImageResource(R.drawable.ht_arrow_right_gray);

            leftArrow.setOnClickListener(new ImageView.OnClickListener() {

                @Override
                public void onClick(View v) {

                    leftDateButtonClicked();
                }
            });

            rightArrow.setOnClickListener(null);

        } else if (currentDate.equals(HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, 1, false))) {

            // Yesterday
            TextView mTitleTextView = (TextView) datePicker.findViewById(R.id.date_text);
            mTitleTextView.setText("Yesterday");

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

        } else {

            // other past dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);

            TextView mTitleTextView = (TextView) datePicker.findViewById(R.id.date_text);

            calendar = Calendar.getInstance();
            calendar.setTime(passDate);

            mTitleTextView.setText(dateFormat.format(calendar.getTime()));

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
        }

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_dashboard"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("exercise_from_planner", "1"));

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();

                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);

                    final NodeList error = doc.getElementsByTagName("error");
                    final NodeList nodes = doc.getElementsByTagName("dashboard_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        fragmentActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(fragmentActivity, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {

                        fragmentActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                Element e2 = (Element) nodes.item(0);

                                Integer tempInteger;
                                Double tempDouble;
                                String tempString;

                                // ignore_calories_burned
                                if (XMLFunctions.tagExists(e2, "ignore_calories_burned")) {
                                    tempString = XMLFunctions.getValue(e2, "ignore_calories_burned");
                                    if ("1".equals(tempString)) {
                                        ignoreCaloriesBurned = true;
                                    } else {
                                        ignoreCaloriesBurned = false;
                                    }
                                }

                                if (XMLFunctions.tagExists(e2, "show_admin_dashboard")) {
                                    tempString = XMLFunctions.getValue(e2, "show_admin_dashboard");
                                    if ("1".equals(tempString)) {
                                        shouldShowAdminDashboard = true;
                                    } else {
                                        shouldShowAdminDashboard = false;
                                    }
                                }

                                //AdminDashboardURL
                                if (XMLFunctions.tagExists(e2, "admin_dashboard_url")) {
                                    adminDashboardURL = XMLFunctions.getValue(e2, "admin_dashboard_url");
                                }

                                // calories
                                if(!HTGlobals.getInstance().hidePlanner && XMLFunctions.tagExists(e2, "calories")) {

                                    dashboardEditItems.add("Calories");
                                    calories = XMLFunctions.getValue(e2, "calories").replace(",", "");
                                    if (calories.equals("")) {
                                        calories = "0";
                                    }
                                    dashboardItems.add("calories consumed");
                                    tempInteger = (Integer.parseInt(calories));
                                    tempString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(tempInteger);
                                    dashboardItemValues.add(tempString);
                                    dashboardItemImages.add(R.drawable.ht_dash_calories_consumed);

                                    // calories_goal & calories_burned
                                    caloriesGoal = XMLFunctions.getValue(e2, "calories_goal").replace(",", "");
                                    if (caloriesGoal.equals("")) {
                                        caloriesGoal = "0";
                                    }
                                    caloriesBurned = XMLFunctions.getValue(e2, "calories_burned").replace(",", "");
                                    if (caloriesBurned.equals("")) {
                                        caloriesBurned = "0";
                                    }
                                    dashboardItems.add("calories remaining");
                                    if (ignoreCaloriesBurned)
                                        tempInteger = (Integer.parseInt(caloriesGoal) - Integer.parseInt(calories));
                                    else
                                        tempInteger = ((Integer.parseInt(caloriesGoal) + Integer.parseInt(caloriesBurned)) - Integer.parseInt(calories));
                                    tempString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(tempInteger);
                                    dashboardItemValues.add(tempString);
                                    dashboardItemImages.add(R.drawable.ht_dash_calories_remaining);

                                    // calories & calories_goal progressBar
                                    tempDouble = (Double.parseDouble(calories) / (Double.parseDouble(caloriesGoal) + Double.parseDouble(caloriesBurned)));
                                    dashboardItemProgress.add(tempDouble);
                                    dashboardItemProgress.add(0.0);

                                    // they've gone over on calories
                                    if (Double.parseDouble(calories) > (Double.parseDouble(caloriesGoal) + Double.parseDouble(caloriesBurned))) {

                                        adapter.setCaloriesBarColor("red");
                                    }
                                }

                                // weight
                                dashboardEditItems.add("Weight");
                                weight = XMLFunctions.getValue(e2, "weight").replace(",", "");
                                if (weight.equals("")) {
                                    weight = "0";
                                }

                                // weight_starting
                                weightStarting = XMLFunctions.getValue(e2, "weight_starting").replace(",", "");
                                if (weightStarting.equals("")) {
                                    weightStarting = "0";
                                }

                                // weight_goal
                                weightGoal = XMLFunctions.getValue(e2, "weight_goal").replace(",", "");

                                // weight_official
                                weightOfficial = XMLFunctions.getValue(e2, "weight_official");

                                dashboardItemImages.add(R.drawable.ht_dash_weight);
                                dashboardItemImages.add(R.drawable.ht_dash_weight_remaining);

                                tempDouble = (Double.parseDouble(weightStarting) - Double.parseDouble(weight));
                                if (tempDouble < 0) {
                                    tempDouble = tempDouble * -1;
                                    dashboardItems.add("lbs gained");
                                } else {
                                    dashboardItems.add("lbs lost");
                                }
                                tempString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(tempDouble);
                                dashboardItemValues.add(tempString);

                                tempDouble = (Double.parseDouble(weight) - Double.parseDouble(weightGoal));
                                tempString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(tempDouble);
                                dashboardItemValues.add(tempString);
                                dashboardItems.add("lbs to go");

                                // weight & weight_goal progressBar
                                tempDouble = ((Double.parseDouble(weightStarting) - Double.parseDouble(weight)) / (Double.parseDouble(weightStarting) - Double.parseDouble(weightGoal)));
                                dashboardItemProgress.add(tempDouble);
                                dashboardItemProgress.add(0.0);

                                // walking_steps
                                //dashboardEditItems.add("Walking Steps");
                                if (XMLFunctions.tagExists(e2, "walking_steps")) {

                                    dashboardEditItems.add("Walking Steps");

                                    walkingSteps = XMLFunctions.getValue(e2, "walking_steps").replace(",", "");
                                    if (walkingSteps.equals("")) {
                                        walkingSteps = "0";
                                    }

                                    // walking_steps_goal
                                    walkingStepsGoal = XMLFunctions.getValue(e2, "walking_steps_goal").replace(",", "");
                                    if (walkingStepsGoal.equals("")) {
                                        walkingStepsGoal = "0";
                                    }

                                    // walking_steps_official
                                    walkingStepsOfficial = XMLFunctions.getValue(e2, "walking_steps_official");

                                    dashboardItems.add("Walking Steps");
                                    tempDouble = (Double.parseDouble(walkingSteps));
                                    tempString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(tempDouble);
                                    dashboardItemValues.add(tempString);
                                    dashboardItemImages.add(R.drawable.ht_dash_steps);

                                    // progressBar
                                    tempDouble = (Double.parseDouble(walkingSteps) / Double.parseDouble(walkingStepsGoal));
                                    dashboardItemProgress.add(tempDouble);
                                }

                                // exercise_minutes
                                //dashboardEditItems.add("Exercise Minutes");
                                if (XMLFunctions.tagExists(e2, "exercise_minutes")) {

                                    dashboardEditItems.add("Exercise Minutes");

                                    exerciseMinutes = XMLFunctions.getValue(e2, "exercise_minutes").replace(",", "");
                                    if (exerciseMinutes.equals("")) {
                                        exerciseMinutes = "0";
                                    }

                                    // exercise_minutes_goal
                                    exerciseMinutesGoal = XMLFunctions.getValue(e2, "exercise_minutes_goal").replace(",", "");
                                    if (exerciseMinutesGoal.equals("")) {
                                        exerciseMinutesGoal = "0";
                                    }

                                    // exercise_minutes_official
                                    exerciseMinutesOfficial = XMLFunctions.getValue(e2, "exercise_minutes_official");

                                    dashboardItems.add("Exercise Minutes");
                                    tempDouble = (Double.parseDouble(exerciseMinutes));
                                    tempString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(tempDouble);
                                    dashboardItemValues.add(tempString);
                                    dashboardItemImages.add(R.drawable.ht_dash_minutes);

                                    // progressBar
                                    tempDouble = (Double.parseDouble(exerciseMinutes) / Double.parseDouble(exerciseMinutesGoal));
                                    dashboardItemProgress.add(tempDouble);
                                }

                                // sleep_hours
                                //dashboardEditItems.add("Sleep Hours");
                                if (XMLFunctions.tagExists(e2, "sleep_hours")) {

                                    dashboardEditItems.add("Sleep Hours");

                                    sleepHours = XMLFunctions.getValue(e2, "sleep_hours").replace(",", "");
                                    if (sleepHours.equals("")) {
                                        sleepHours = "0";
                                    }

                                    // sleep_hours_goal
                                    sleepHoursGoal = XMLFunctions.getValue(e2, "sleep_hours_goal").replace(",", "");
                                    if (sleepHoursGoal.equals("")) {
                                        sleepHoursGoal = "0";
                                    }

                                    // sleep_hours_official
                                    sleepHoursOfficial = XMLFunctions.getValue(e2, "sleep_hours_official");

                                    dashboardItems.add("Sleep Hours");
                                    tempDouble = (Double.parseDouble(sleepHours));
                                    tempString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(tempDouble);
                                    dashboardItemValues.add(tempString);
                                    dashboardItemImages.add(R.drawable.ht_dash_sleep);

                                    // progressBar
                                    tempDouble = (Double.parseDouble(sleepHours) / Double.parseDouble(sleepHoursGoal));
                                    dashboardItemProgress.add(tempDouble);
                                }

                                //Show messages
                                if (XMLFunctions.tagExists(e2, "show_messages")) {
//                                    numberOfMessages = XMLFunctions.getValue(e2, "show_messages").replace(",", "");
                                    String value = XMLFunctions.getValue(e2, "show_messages").replace(",", "");
                                    if ("1".equals(value)) {
                                        dashboardEditItems.add("Messages");
                                        showMessages = true;

                                        if (XMLFunctions.tagExists(e2, "new_messages")) {
                                            numberOfMessages = XMLFunctions.getValue(e2, "new_messages").replace(",", "");
                                        }

                                        if ("0".equals(numberOfMessages)) {
                                            dashboardItems.add("Messages");
                                            dashboardItemValues.add("");
                                            ((HTTabBar) fragmentActivity).showDashboardBadgeView(false);
                                            ((HTTabBar) fragmentActivity).showMoreBadgeView(false);
                                        } else {
                                            if ("1".equals(numberOfMessages)) {
                                                dashboardItems.add("new message");
                                            } else {
                                                dashboardItems.add("new messages");
                                            }
                                            dashboardItemValues.add(numberOfMessages);
                                            Set<String> tmpCheckedItems = mSharedPreferences.getStringSet("CheckedDashboardItems", null);
                                            if (tmpCheckedItems == null || tmpCheckedItems.contains("Messages"))
                                                ((HTTabBar) fragmentActivity).showDashboardBadgeView(true);
                                            else
                                                ((HTTabBar) fragmentActivity).showDashboardBadgeView(false);
                                            ((HTTabBar) fragmentActivity).showMoreBadgeView(true);
                                            ((HTTabBar) fragmentActivity).setDashboardBadgeCount(numberOfMessages);
                                            ((HTTabBar) fragmentActivity).setMoreBadgeCount(numberOfMessages);
                                            adapter.setNewMessageCount(numberOfMessages);
                                        }

                                        dashboardItemImages.add(R.drawable.ht_dash_inbox);
                                        dashboardItemProgress.add(0.0);
                                    }
                                }

                                //new learning modules
                                if (XMLFunctions.tagExists(e2, "new_learning_modules")) {
                                    newLearningModules = XMLFunctions.getValue(e2, "new_learning_modules");
                                }

                                if ("0".equals(newLearningModules)) {
                                    ((HTTabBar) fragmentActivity).showLearnBadgeView(false);
                                } else {
                                    ((HTTabBar) fragmentActivity).showLearnBadgeView(true);
                                    ((HTTabBar) fragmentActivity).setLearnBadgeCount(newLearningModules);
                                }

                                //new eating plan
                                if (XMLFunctions.tagExists(e2, "new_eating_plan")) {
                                    newEatingPlans = XMLFunctions.getValue(e2, "new_eating_plan");
                                }

                                if(!HTGlobals.getInstance().hidePlanner) {
                                    if ("0".equals(newEatingPlans)) {
                                        ((HTTabBar) fragmentActivity).showPlanBadgeView(false);
                                    } else {
                                        ((HTTabBar) fragmentActivity).showPlanBadgeView(true);
                                        ((HTTabBar) fragmentActivity).setPlanBadgeCount(newEatingPlans);
                                    }
                                }

                                //display badge number on App Icon.
                                int totalBadgeCount = 0, tempCount;
                                try {
                                    tempCount = Integer.parseInt(numberOfMessages);
                                }catch(NumberFormatException e){
                                    tempCount = 0;
                                }
                                totalBadgeCount += tempCount;
                                try {
                                    tempCount = Integer.parseInt(newLearningModules);
                                }catch(NumberFormatException e){
                                    tempCount = 0;
                                }
                                totalBadgeCount += tempCount;
                                if(!HTGlobals.getInstance().hidePlanner) {
                                    try {
                                        tempCount = Integer.parseInt(newEatingPlans);
                                    } catch (NumberFormatException e) {
                                        tempCount = 0;
                                    }
                                    totalBadgeCount += tempCount;
                                }

                                HTGlobals.getInstance().setAppIconBadge(fragmentActivity, totalBadgeCount);

                                // CUSTOM METRICS!!!
                                int index;
                                for (int i = 1; i <= 10; i++) {

                                    index = i - 1;
                                    customMetrics.add(XMLFunctions.getValue(e2, "metric" + i).replace(",", ""));
                                    customMetricsTypes.add(XMLFunctions.getValue(e2, "metric" + i + "_type"));
                                    customMetricsLabels.add(XMLFunctions.getValue(e2, "metric" + i + "_label"));
                                    customMetricsGoals.add(XMLFunctions.getValue(e2, "metric" + i + "_goal").replace(",", ""));
                                    customMetricsOfficial.add(XMLFunctions.getValue(e2, "metric" + i + "_official"));

                                    if (customMetrics.get(index).equals("")) {
                                        customMetrics.set(index, "0");
                                    }

                                    if (customMetricsGoals.get(index).equals("")) {
                                        customMetricsGoals.set(index, "0");
                                    }

                                    // valid metric?
                                    if (!customMetricsLabels.get(index).equals("")) {

                                        dashboardItems.add(customMetricsLabels.get(index));
                                        dashboardEditItems.add(customMetricsLabels.get(index));

                                        if (customMetricsTypes.get(index).equals("2")) { // checkbox

                                            dashboardItemValues.add("");

                                            if (!customMetrics.get(index).equals("1")) { // not checked
                                                dashboardItemProgress.add(0.0);
                                            } else {
                                                dashboardItemProgress.add(1.0);
                                            }

                                        } else {

                                            tempDouble = (Double.parseDouble(customMetrics.get(index)));
                                            tempString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(tempDouble);
                                            dashboardItemValues.add(tempString);

                                            // metric progressBar
                                            tempDouble = (Double.parseDouble(customMetrics.get(index)) / Double.parseDouble(customMetricsGoals.get(index)));
                                            dashboardItemProgress.add(tempDouble);
                                        }

                                        dashboardItemImages.add(R.drawable.ht_dash_metrics);
                                    }
                                }

                                adapter.notifyDataSetChanged();

                                showDashboard();
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    // these all happen with the post.abort() but can be ignored, as the call will be executed again
                    if (error != null) {
                        if (!error.equals("Connection already shutdown") && !error.equals("Request already aborted") && !error.equals("No peer certificate") && !error.equals("Request must not be null.") && !error.equals("Connection has been shut down.") && !error.equals("Connection to https://www.setpointhealth.com refused") && !error.equals("Connection to https://www.trackmyday.org refused")) {

                            fragmentActivity.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

//                                    toast = HTToast.showToast(fragmentActivity, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(fragmentActivity, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                                }
                            });

                        }
                    }

                } finally {

                    fragmentActivity.runOnUiThread(new Runnable() {

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

    private void changeCameraDistance() {
        int distance = 10000;
        float scale = getResources().getDisplayMetrics().density * distance;
        adminDashboardLayout.setCameraDistance(scale);
        userDashboardLayout.setCameraDistance(scale);
    }

    private void loadAnimations() {
        mInAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(fragmentActivity, R.animator.in_animation);
        mOutAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(fragmentActivity, R.animator.out_animation);

        mInAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (showAdminDashboard) {
                    adminDashboardLayout.setVisibility(View.VISIBLE);
                } else {
                    userDashboardLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mOutAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (showAdminDashboard) {
//                    userDashboardLayout.setVisibility(View.GONE);
                } else {
                    adminDashboardLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void showDashboard() {
        getSortedItems();
        listViewDashboard = (ListView) relativeLayout.findViewById(R.id.listViewDashboard);
        listViewDashboard.setAdapter(adapter);
        listViewDashboard.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                if (final_dashboardItems.get(position).equals("Messages") || final_dashboardItems.get(position).contains("new message")) {

                    Intent intent = new Intent(fragmentActivity, InboxActivity.class);
                    startActivityForResult(intent, 1);

                } else if (final_dashboardItems.get(position).contains("calories consumed")){
                    Intent intent = new Intent(fragmentActivity, HTPlannerCaloriesConsumedActivity.class);
                    startActivityForResult(intent, 1);
                } else if(final_dashboardItems.get(position).contains("calories remaining")) {
                    HTGlobals.getInstance().plannerShouldRefresh = true;
                    FragmentTabHost mTabHost = (FragmentTabHost) fragmentActivity.findViewById(android.R.id.tabhost);
                    mTabHost.setCurrentTab(2);

                } else if (final_dashboardItems.get(position).contains("lbs lost")
                        || final_dashboardItems.get(position).contains("lbs gained")
                        || final_dashboardItems.get(position).contains("lbs to go")){
                    selectedMetric = "Weight";
                    gotoMetricChartsActivity();
                } else if (final_dashboardItems.get(position).contains("Walking Steps")){
                    selectedMetric = "Walking Steps";
                    gotoMetricChartsActivity();
                } else if (final_dashboardItems.get(position).contains("Exercise Minutes")){
                    selectedMetric = "Exercise Minutes";
                    gotoMetricChartsActivity();
                } else if (final_dashboardItems.get(position).contains("Sleep Hours")){
                    selectedMetric = "Sleep Hours";
                    gotoMetricChartsActivity();
                } else {
                    int whichMetricNumber = customMetricsLabels.indexOf(final_dashboardItems.get(position));

                    if (whichMetricNumber >=0 && whichMetricNumber <= 10) {
                        selectedMetric = final_dashboardItems.get(position);
                        selectedCustomMetricString = "metric" + (whichMetricNumber + 1);
                        gotoMetricChartsActivity();
                    } else {
                        Intent intent = new Intent(fragmentActivity, ActivityTrackerActivity.class);
                        startActivityForResult(intent, 1);
                    }
                }
            }
        });

        if (shouldShowAdminDashboard) {
            loadAnimations();
            changeCameraDistance();

            ((ImageView) mActionBar.findViewById(R.id.leftArrowAction)).setImageResource(R.drawable.ht_icon_rotate_view);
            mActionBar.findViewById(R.id.leftArrowAction).setVisibility(View.VISIBLE);
            mActionBar.findViewById(R.id.leftArrowAction).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(showAdminDashboard) {
                        dashboardStatus = "user";
                        showAdminDashboard = false;
                        mOutAnimation.setTarget(adminDashboardLayout);
                        mInAnimation.setTarget(userDashboardLayout);
                        mOutAnimation.start();
                        mInAnimation.start();
                        mActionBar.findViewById(R.id.rightButton).setVisibility(View.VISIBLE);
                    } else {
                        dashboardStatus = "admin";
                        showAdminDashboard = true;
                        mOutAnimation.setTarget(userDashboardLayout);
                        mInAnimation.setTarget(adminDashboardLayout);
                        mOutAnimation.start();
                        mInAnimation.start();
                        mActionBar.findViewById(R.id.rightButton).setVisibility(View.GONE);
                    }

                    mSharedPreferencesEditor.putString("dashboardStatus", dashboardStatus);
                    mSharedPreferencesEditor.commit();

                }
            });

            if (!adminDashboardHasLoaded) {
                if ("admin".equals(dashboardStatus)) {
                    adminDashboardLayout.setVisibility(View.VISIBLE);
                    showAdminDashboard = true;
                    HTToast.showToast(fragmentActivity, "\nLoading Admin Dashboard.\nPlease wait...\n", Toast.LENGTH_LONG);
                    mActionBar.findViewById(R.id.rightButton).setVisibility(View.GONE);
                } else {
                    adminDashboardLayout.setVisibility(View.GONE);
                    showAdminDashboard = false;
                    mActionBar.findViewById(R.id.rightButton).setVisibility(View.VISIBLE);
                }

                adminDashboardWebView.getSettings().setJavaScriptEnabled(true);
                adminDashboardWebView.getSettings().setSupportZoom(true);
                adminDashboardWebView.getSettings().setBuiltInZoomControls(true);
                adminDashboardWebView.getSettings().setDisplayZoomControls(false);
                adminDashboardWebView.getSettings().setSaveFormData(false);

                String adminDashboardURL = String.format("%s?login=%s&password=%s&app=true", this.adminDashboardURL, login, pw);
                adminDashboardWebView.loadUrl(adminDashboardURL);
            }
        } else {
            mActionBar.findViewById(R.id.leftArrowAction).setVisibility(View.GONE);
            adminDashboardLayout.setVisibility(View.GONE);
        }
    }

    private void gotoMetricChartsActivity() {
        Intent intent = new Intent(fragmentActivity, HTMetricChartActivity.class);
        intent.putExtra("selectedMetric", selectedMetric);
        intent.putExtra("selectedCustomMetricString", selectedCustomMetricString);
        startActivityForResult(intent, 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // coming back from a child activity, refresh the calendar

        passDate = HTGlobals.getInstance().passDate;
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;

        getDashboard();
    }

    private void getSortedItems() {

        String tmpSortedItems = mSharedPreferences.getString("SortedDashboardItems", null);
        ArrayList<String> sortedDashboardEditItems = new ArrayList<String>();
        if (tmpSortedItems == null) {
            sortedDashboardEditItems.addAll(dashboardEditItems);
        } else {
            String items[] = tmpSortedItems.trim().split(",");
            for (String item : items) {
                if (dashboardEditItems.contains(item))
                    sortedDashboardEditItems.add(item);
            }
            for (int i = 0; i < dashboardEditItems.size(); i++)
                if (!sortedDashboardEditItems.contains(dashboardEditItems.get(i)))
                    sortedDashboardEditItems.add(dashboardEditItems.get(i));
        }

        ArrayList<String> checkedDashboardEditItems;
        Set<String> tmpCheckedItems = mSharedPreferences.getStringSet("CheckedDashboardItems", null);
        if (tmpCheckedItems == null) {
            checkedDashboardEditItems = new ArrayList<String>();
            checkedDashboardEditItems.addAll(dashboardEditItems);
        } else {
            checkedDashboardEditItems = new ArrayList<String>(tmpCheckedItems);
        }

        for (int i = 0; i < sortedDashboardEditItems.size(); i++) {
            String item = sortedDashboardEditItems.get(i);

            if (checkedDashboardEditItems.contains(item)) {
                int index = 0;
                if ("Weight".equals(item)) {
                    if (dashboardItems.contains("lbs lost")) {
                        index = dashboardItems.indexOf("lbs lost");
                    } else if (dashboardItems.contains("lbs gained")) {
                        index = dashboardItems.indexOf("lbs gained");
                    }

                    moveItemsToFinalArray(index);

                    if (dashboardItems.contains("lbs to go"))
                        index = dashboardItems.indexOf("lbs to go");

                    moveItemsToFinalArray(index);
                } else if ("Calories".equals(item)) {
                    if (dashboardItems.contains("calories consumed")) {
                        index = dashboardItems.indexOf("calories consumed");
                    }

                    moveItemsToFinalArray(index);

                    if (dashboardItems.contains("calories remaining"))
                        index = dashboardItems.indexOf("calories remaining");

                    moveItemsToFinalArray(index);
                } else if ("Messages".equals(item)) {
                    if ("0".equals(numberOfMessages)) {
                        index = dashboardItems.indexOf("Messages");
                    } else {
                        if ("1".equals(numberOfMessages)) {
                            index = dashboardItems.indexOf("new message");
                        } else {
                            index = dashboardItems.indexOf("new messages");
                        }
                    }
                    moveItemsToFinalArray(index);
                } else {
                    index = dashboardItems.indexOf(item);

                    moveItemsToFinalArray(index);
                }
            }
        }
    }

    private void moveItemsToFinalArray(int index) {

        final_dashboardItems.add(dashboardItems.get(index));
        final_dashboardItemValues.add(dashboardItemValues.get(index));
        final_dashboardItemImages.add(dashboardItemImages.get(index));
        final_dashboardItemProgress.add(dashboardItemProgress.get(index));
    }

    private class MyWebViewClient extends WebViewClient {
        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {

            view.loadUrl(url);

            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            adminDashboardHasLoaded = true;
        }
    }
}

