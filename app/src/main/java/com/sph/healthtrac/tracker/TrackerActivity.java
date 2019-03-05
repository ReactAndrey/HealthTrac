package com.sph.healthtrac.tracker;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTTabBar;
import com.sph.healthtrac.tracker.activitytracker.ActivityTrackerActivity;
import com.sph.healthtrac.tracker.colormyday.ColorMyDayActivity;
import com.sph.healthtrac.common.HTAppletButton;
import com.sph.healthtrac.common.HTDateActionBar;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.tracker.myjournal.HTJournalActivity;
import com.sph.healthtrac.R;
import com.sph.healthtrac.tracker.setagoal.SetAGoalActivity;
import com.sph.healthtrac.common.XMLFunctions;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class TrackerActivity extends Fragment {

    FragmentActivity fragmentActivity;

    RelativeLayout relativeLayout;
    RelativeLayout trackerMainContainer;
    RelativeLayout trackerContainer;
    RelativeLayout calendarApplets;

    private Date passDate;
    private int passYear;
    private int passMonth;
    private int passDay;

    private int currentDayOfMonth;
    private int currentMonth;
    private int currentYear;
    private Date currentDate;

    private List<String> previousCalendarColors = new ArrayList<>();
    private List<String> currentCalendarColors = new ArrayList<>();
    private List<String> nextCalendarColors = new ArrayList<>();

    private List<String> previousCalendarLogins = new ArrayList<>();
    private List<String> currentCalendarLogins = new ArrayList<>();
    private List<String> nextCalendarLogins = new ArrayList<>();

    private List<String> previousCalendarActivity = new ArrayList<>();
    private List<String> currentCalendarActivity = new ArrayList<>();
    private List<String> nextCalendarActivity = new ArrayList<>();

    private String[] gridViewType = new String[50]; // at most
    private String[] gridViewText = new String[50];
    private String[] gridViewImage = new String[50];

    private String numberOfMessages = "";
    private String newLearningModules = "";
    private String newEatingPlans = "";

    private String login;
    private String pw;

    private int blockCounter;

    Calendar calendar;

    HttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;

    private Thread myThread = null;

    Toast toast;

    Typeface weekdayFont;
    Typeface calendarDayFont;

    DisplayMetrics displayMetrics;

    float displayDensity;
    int screenWidth;
    int screenHeight;

    View dateActionBar;

    RelativeLayout.LayoutParams params;
    SharedPreferences mSharedPreferences;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentActivity = super.getActivity();
        mSharedPreferences = fragmentActivity.getSharedPreferences("SPHPrefs", Activity.MODE_PRIVATE);
        relativeLayout = (RelativeLayout) inflater.inflate(R.layout.activity_tracker, container, false);

        HTGlobals.getInstance().plannerShouldRefresh = true;

        trackerMainContainer = (RelativeLayout) relativeLayout.findViewById(R.id.trackerMainContainer);
        trackerContainer = (RelativeLayout) relativeLayout.findViewById(R.id.trackerContainer);
        calendarApplets = (RelativeLayout) relativeLayout.findViewById(R.id.calendarApplets);

        weekdayFont = Typeface.createFromAsset(fragmentActivity.getAssets(), "fonts/AvenirNext-DemiBold.ttf");
        calendarDayFont = Typeface.createFromAsset(fragmentActivity.getAssets(), "fonts/AvenirNext-Regular.ttf");

        // get the current date, if none was passed in
        calendar = Calendar.getInstance();
        currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        currentMonth =  calendar.get(Calendar.MONTH);
        currentYear =  calendar.get(Calendar.YEAR);

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

        // action bar
        dateActionBar = HTDateActionBar.getActionBar(fragmentActivity);

        trackerMainContainer.addView(dateActionBar);

        displayMetrics = fragmentActivity.getResources().getDisplayMetrics();
        displayDensity = displayMetrics.density;

        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        int dpValue = 44;
        int topBarHeight = (int)(dpValue * displayDensity);

        params = (RelativeLayout.LayoutParams) dateActionBar.getLayoutParams();
        params.height = topBarHeight;

        dateActionBar.setLayoutParams(params);

        // set the tab bar label font colors
        TextView tabBarLabel;

        tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.dashboardText);
        tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));

        tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.trackText);
        tabBarLabel.setTextColor(Color.WHITE);

        if(!HTGlobals.getInstance().hidePlanner) {
            tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.planText);
            tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));
        }

        tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.learnText);
        tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));

        tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.moreText);
        tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));

        // applet icons

        // Color My Day
        View appletColorMyDay = HTAppletButton.getAppletButton(fragmentActivity, "ht_tracker_color_my_day", "Color My Day");

        calendarApplets.addView(appletColorMyDay);

        params = (RelativeLayout.LayoutParams) appletColorMyDay.getLayoutParams();
        params.height = (int)(64 * displayDensity);
        params.width = ((screenWidth - (int)(32 * displayDensity)) / 2);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.setMargins((int)(11 * displayDensity), (int)(10 * displayDensity), 0, 0);

        appletColorMyDay.setLayoutParams(params);

        appletColorMyDay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(fragmentActivity, ColorMyDayActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // Activity Tracker
        View appletActivityTracker = HTAppletButton.getAppletButton(fragmentActivity, "ht_tracker_activity_tracker", "Activity Tracker");

        calendarApplets.addView(appletActivityTracker);

        params = (RelativeLayout.LayoutParams) appletActivityTracker.getLayoutParams();
        params.height = (int)(64 * displayDensity);
        params.width = ((screenWidth - (int)(32 * displayDensity)) / 2);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.setMargins(0, (int)(10 * displayDensity), (int)(11 * displayDensity), 0);

        appletActivityTracker.setLayoutParams(params);

        appletActivityTracker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(fragmentActivity, ActivityTrackerActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // My Journal
        View appletMyJournal = HTAppletButton.getAppletButton(fragmentActivity, "ht_tracker_my_journal", "My Journal");

        calendarApplets.addView(appletMyJournal);

        params = (RelativeLayout.LayoutParams) appletMyJournal.getLayoutParams();
        params.height = (int)(64 * displayDensity);
        params.width = ((screenWidth - (int)(32 * displayDensity)) / 2);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.setMargins((int)(11 * displayDensity), (int)(10 * displayDensity), 0, (int)(10 * displayDensity));

        appletMyJournal.setLayoutParams(params);

        appletMyJournal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(fragmentActivity, HTJournalActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // Set A Goal
        View appletSetAGoal = HTAppletButton.getAppletButton(fragmentActivity, "ht_tracker_set_a_goal", "Set A Goal");

        calendarApplets.addView(appletSetAGoal);

        params = (RelativeLayout.LayoutParams) appletSetAGoal.getLayoutParams();
        params.height = (int)(64 * displayDensity);
        params.width = ((screenWidth - (int)(32 * displayDensity)) / 2);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.setMargins(0, (int)(10 * displayDensity), (int)(11 * displayDensity), (int)(10 * displayDensity));

        appletSetAGoal.setLayoutParams(params);

        appletSetAGoal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(fragmentActivity, SetAGoalActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        if(login.equals("") || pw.equals("")) {

            Intent intent = new Intent(fragmentActivity, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            fragmentActivity.finish();

        } else {

            getCalendar();
        }

        return relativeLayout;
    }

    private void getCalendar() {

        previousCalendarColors.clear();
        currentCalendarColors.clear();
        nextCalendarColors.clear();

        previousCalendarLogins.clear();
        currentCalendarLogins.clear();
        nextCalendarLogins.clear();

        previousCalendarActivity.clear();
        currentCalendarActivity.clear();
        nextCalendarActivity.clear();

        previousCalendarColors.add("");
        currentCalendarColors.add("");
        nextCalendarColors.add("");

        previousCalendarLogins.add("");
        currentCalendarLogins.add("");
        nextCalendarLogins.add("");

        previousCalendarActivity.add("");
        currentCalendarActivity.add("");
        nextCalendarActivity.add("");

        numberOfMessages = "";
        newLearningModules = "";
        newEatingPlans = "";

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(fragmentActivity);
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (passDate.getTime() > currentDate.getTime()) {

            // get the current date
            calendar = Calendar.getInstance();
            currentDayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
            currentMonth =  calendar.get(Calendar.MONTH);
            currentYear =  calendar.get(Calendar.YEAR);

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

        final ImageView leftArrowAction = (ImageView) dateActionBar.findViewById(R.id.leftArrowAction);
        final ImageView rightArrowAction = (ImageView) dateActionBar.findViewById(R.id.rightArrowAction);

        leftArrowAction.setImageResource(R.drawable.ht_arrow_left_blue);

        if (passMonth == currentMonth && passYear == currentYear) { // no right arrow click

            rightArrowAction.setImageResource(R.drawable.ht_arrow_right_gray);

            rightArrowAction.setOnClickListener(null);

        } else {

            rightArrowAction.setImageResource(R.drawable.ht_arrow_right_blue);

            rightArrowAction.setOnClickListener(new ImageView.OnClickListener() {

                @Override
                public void onClick(View v) {

                    rightDateButtonClicked();
                }
            });
        }

        leftArrowAction.setOnClickListener(new ImageView.OnClickListener() {

            @Override
            public void onClick(View v) {

                leftDateButtonClicked();
            }
        });

        // month, year
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.US);

        TextView mTitleTextView = (TextView) dateActionBar.findViewById(R.id.title_text);

        calendar = Calendar.getInstance();
        calendar.setTime(passDate);

        mTitleTextView.setText(dateFormat.format(calendar.getTime()));

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_calendar"));
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
                    NodeList nodes = doc.getElementsByTagName("calendar_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        fragmentActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(fragmentActivity, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    }
                    else {

                        final Element e2 = (Element)nodes.item(0);

                        fragmentActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                // GET AFTER IT!!!
                                for (int i=1; i<=31; i++) {

                                    previousCalendarColors.add(XMLFunctions.getValue(e2, "prev_color_" + i));
                                    previousCalendarLogins.add(XMLFunctions.getValue(e2, "prev_login_" + i));
                                    previousCalendarActivity.add(XMLFunctions.getValue(e2, "prev_activity_" + i));

                                    currentCalendarColors.add(XMLFunctions.getValue(e2, "current_color_" + i));
                                    currentCalendarLogins.add(XMLFunctions.getValue(e2, "current_login_" + i));
                                    currentCalendarActivity.add(XMLFunctions.getValue(e2, "current_activity_" + i));

                                    nextCalendarColors.add(XMLFunctions.getValue(e2, "next_color_" + i));
                                    nextCalendarLogins.add(XMLFunctions.getValue(e2, "next_login_" + i));
                                    nextCalendarActivity.add(XMLFunctions.getValue(e2, "next_activity_" + i));
                                }

                                //Show messages
                                if (XMLFunctions.tagExists(e2, "show_messages")) {
                                    String value = XMLFunctions.getValue(e2, "show_messages");
                                    if ("1".equals(value)) {
                                        if (XMLFunctions.tagExists(e2, "new_messages")) {
                                            numberOfMessages = XMLFunctions.getValue(e2, "new_messages");
                                        }

                                        if ("0".equals(numberOfMessages)) {
                                            ((HTTabBar) fragmentActivity).showDashboardBadgeView(false);
                                            ((HTTabBar) fragmentActivity).showMoreBadgeView(false);
                                        } else {
                                            Set<String> tmpCheckedItems = mSharedPreferences.getStringSet("CheckedDashboardItems", null);
                                            if (tmpCheckedItems == null || tmpCheckedItems.contains("Messages"))
                                                ((HTTabBar) fragmentActivity).showDashboardBadgeView(true);
                                            else
                                                ((HTTabBar) fragmentActivity).showDashboardBadgeView(false);
                                            ((HTTabBar) fragmentActivity).showMoreBadgeView(true);
                                            ((HTTabBar) fragmentActivity).setDashboardBadgeCount(numberOfMessages);
                                            ((HTTabBar) fragmentActivity).setMoreBadgeCount(numberOfMessages);
                                        }
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
                                showCalendar();
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

    private void showCalendar() {

        String firstDayOfMonthString = passYear + "-" + (passMonth + 1) + "-1";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        Date firstDayOfMonth;

        Calendar calendar = Calendar.getInstance();

        try {

            firstDayOfMonth = dateFormat.parse(firstDayOfMonthString);

            calendar.setTime(firstDayOfMonth);

        } catch (ParseException e) {

            //
        }

        // calendar headers

        gridViewType[0] = "header";
        gridViewText[0] = "SUN";
        gridViewImage[0] = "";

        gridViewType[1] = "header";
        gridViewText[1] = "MON";
        gridViewImage[1] = "";

        gridViewType[2] = "header";
        gridViewText[2] = "TUE";
        gridViewImage[2] = "";

        gridViewType[3] = "header";
        gridViewText[3] = "WED";
        gridViewImage[3] = "";

        gridViewType[4] = "header";
        gridViewText[4] = "THU";
        gridViewImage[4] = "";

        gridViewType[5] = "header";
        gridViewText[5] = "FRI";
        gridViewImage[5] = "";

        gridViewType[6] = "header";
        gridViewText[6] = "SAT";
        gridViewImage[6] = "";

        blockCounter = 7;

        int weekday = calendar.get(Calendar.DAY_OF_WEEK);
        int currentDay;
        Date currentCalendarDate;

        // populate the previous month's days

        if(weekday > 1) {

            currentCalendarDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(calendar.getTime(), -(weekday - 1), false);

            calendar.setTime(currentCalendarDate);
            currentDay = calendar.get(Calendar.DAY_OF_MONTH);

            do {

                // is this a valid date?
                if(HTGlobals.getInstance().isValidDate(calendar.get(Calendar.YEAR) + "-" + (calendar.get(Calendar.MONTH) + 1) + "-" + currentDay)) {

                    gridViewType[blockCounter] = "prev";
                    gridViewText[blockCounter] = currentDay + "";

                    if (previousCalendarColors.get(currentDay).equals("GREEN")) {

                        gridViewImage[blockCounter] = "ht_calendar_green";

                    } else if (previousCalendarColors.get(currentDay).equals("YELLOW")) {

                        gridViewImage[blockCounter] = "ht_calendar_yellow";

                    } else if (previousCalendarColors.get(currentDay).equals("RED")) {

                        gridViewImage[blockCounter] = "ht_calendar_red";

                    } else {

                        gridViewImage[blockCounter] = "ht_calendar_gray";
                    }

                    blockCounter += 1;
                }

                currentDay += 1;

            } while (currentDay <= 31);
        }

        // populate the current month's days

        currentDay = 1;

        do {

            // is this a valid date?
            String checkDateString;
            checkDateString = passYear + "-" + (passMonth + 1) + "-" + currentDay;

            if(HTGlobals.getInstance().isValidDate(checkDateString)) {

                try {

                    currentCalendarDate = dateFormat.parse(checkDateString);
                    calendar.setTime(currentCalendarDate);
                    weekday = calendar.get(Calendar.DAY_OF_WEEK);

                } catch (ParseException e) {

                    //
                }

                gridViewType[blockCounter] = "current";
                gridViewText[blockCounter] = currentDay + "";

                if (currentCalendarColors.get(currentDay).equals("GREEN")) {

                    if (currentDay == passDay) {

                        gridViewImage[blockCounter] = "ht_calendar_green_selected";

                    } else {

                        gridViewImage[blockCounter] = "ht_calendar_green";
                    }

                } else if (currentCalendarColors.get(currentDay).equals("YELLOW")) {

                    if (currentDay == passDay) {

                        gridViewImage[blockCounter] = "ht_calendar_yellow_selected";

                    } else {

                        gridViewImage[blockCounter] = "ht_calendar_yellow";
                    }

                } else if (currentCalendarColors.get(currentDay).equals("RED")) {

                    if (currentDay == passDay) {

                        gridViewImage[blockCounter] = "ht_calendar_red_selected";

                    } else {

                        gridViewImage[blockCounter] = "ht_calendar_red";
                    }

                } else {

                    if (currentDay == passDay) {

                        gridViewImage[blockCounter] = "ht_calendar_gray_selected";

                    } else {

                        gridViewImage[blockCounter] = "ht_calendar_gray";
                    }
                }

                blockCounter += 1;
            }

            currentDay += 1;

        } while (currentDay <= 31);

        // populate the next month's days

        currentDay = 1;

        if (weekday < 7) {

            do {

                gridViewType[blockCounter] = "next";
                gridViewText[blockCounter] = currentDay + "";

                if (nextCalendarColors.get(currentDay).equals("GREEN")) {

                    gridViewImage[blockCounter] = "ht_calendar_green";

                } else if (nextCalendarColors.get(currentDay).equals("YELLOW")) {

                    gridViewImage[blockCounter] = "ht_calendar_yellow";

                } else if (nextCalendarColors.get(currentDay).equals("RED")) {

                    gridViewImage[blockCounter] = "ht_calendar_red";

                } else {

                    gridViewImage[blockCounter] = "ht_calendar_gray";
                }

                blockCounter += 1;
                currentDay += 1;
                weekday += 1;

            } while (weekday < 7);
        }

        // GO!!!

        GridView calendarGridView = (GridView) fragmentActivity.findViewById(R.id.calendarGridView);

        if (calendarGridView != null) {

            calendarGridView.invalidateViews();
            calendarGridView.setAdapter(new gridViewAdapter(fragmentActivity));
        }
    }

    private class gridViewAdapter extends BaseAdapter {

        private Context mContext;

        // Gets the context so it can be used later
        public gridViewAdapter(Context c) {
            mContext = c;
        }


        // Total number of items contained within the adapter
        @Override
        public int getCount() {

            return blockCounter;
        }

        // Require for structure, not really used in my code.
        @Override
        public Object getItem(int position) {
            return null;
        }

        // Require for structure, not really used in my code.
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            int gridViewBlockWidth = ((screenWidth - (int)(32 * displayDensity)) / 7);
            int gridViewBlockHeight = ((screenHeight - (int)(318 * displayDensity)) / 6);

            if(position < 7) {

                LayoutInflater mInflater = LayoutInflater.from(mContext);

                View gridViewBlock = mInflater.inflate(R.layout.calendar_header, null);

                TextView gridViewBlockTextView = (TextView) gridViewBlock.findViewById(R.id.calendarHeaderText);

                gridViewBlockTextView.setTypeface(weekdayFont);
                gridViewBlockTextView.setTextColor(mContext.getResources().getColor(R.color.ht_gray_calendar_header));
                gridViewBlockTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 10);
                gridViewBlockTextView.setText(gridViewText[position]);

                GridView.LayoutParams gridViewParams = new GridView.LayoutParams(gridViewBlockWidth, (int)(gridViewBlockHeight * .8));

                gridViewBlock.setLayoutParams(gridViewParams);

                return gridViewBlock;

            } else {

                LayoutInflater mInflater = LayoutInflater.from(mContext);

                View gridViewBlock = mInflater.inflate(R.layout.calendar_block, null);
                gridViewBlock.setId(position);

                GridView.LayoutParams gridViewParams = new GridView.LayoutParams(gridViewBlockWidth, gridViewBlockHeight);

                gridViewBlock.setLayoutParams(gridViewParams);

                TextView gridViewBlockTextView = (TextView) gridViewBlock.findViewById(R.id.calendarBlockText);

                gridViewBlockTextView.setTypeface(calendarDayFont);

                ImageView calendarBlockImage = (ImageView) gridViewBlock.findViewById(R.id.calendarBlockImage);

                if (gridViewBlockHeight <= 36) {

                    calendarBlockImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                } else {

                    calendarBlockImage.setScaleType(ImageView.ScaleType.CENTER);
                }

                // may not be clickable
                if ((gridViewType[position].equals("current") || gridViewType[position].equals("next"))) {

                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
                    String checkDateString;
                    Date checkDate = new Date();

                    if (gridViewType[position].equals("current")) {

                        checkDateString = passYear + "-" + (passMonth + 1) + "-" + gridViewText[position];

                        try {

                            checkDate = dateFormat.parse(checkDateString);

                        } catch (ParseException e) {

                            //
                        }

                    } else { // next

                        if (passMonth == 11) { //  current month is december

                            checkDateString = (passYear + 1) + "-" + 1 + "-" + gridViewText[position];

                        } else {

                            checkDateString = passYear + "-" + (passMonth + 2) + "-" + gridViewText[position];
                        }

                        try {

                            checkDate = dateFormat.parse(checkDateString);

                        } catch (ParseException e) {

                            //
                        }
                    }

                    // today

                    if (currentDate.equals(checkDate)) {

                        if(gridViewImage[position].equals("ht_calendar_gray") || gridViewImage[position].equals("ht_calendar_gray_selected")) { // blank

                            gridViewBlockTextView.setTextColor(mContext.getResources().getColor(R.color.ht_gray_calendar_text));

                        } else {

                            gridViewBlockTextView.setTextColor(Color.WHITE);

                            if (gridViewBlockHeight <= 36 && gridViewImage[position].contains("_selected")) {

                                calendarBlockImage.setPadding((int)(.5 * displayDensity), (int)(.5 * displayDensity), (int)(.5 * displayDensity), (int)(.5 * displayDensity));

                            } else if (gridViewBlockHeight <= 36) {

                                calendarBlockImage.setPadding((int)(2 * displayDensity), (int)(2 * displayDensity), (int)(2 * displayDensity), (int)(2 * displayDensity));
                            }

                            calendarBlockImage.setImageResource(mContext.getResources().getIdentifier(gridViewImage[position], "drawable", mContext.getPackageName()));
                        }

                        if (gridViewType[position].equals("current")) {

                            gridViewBlock.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    clickedCurrentMonthDay(Integer.parseInt(gridViewText[position]));
                                }
                            });

                        } else { // next

                            gridViewBlock.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    clickedNextMonthDay(Integer.parseInt(gridViewText[position]));
                                }
                            });
                        }

                    } else if (checkDate.getTime() > currentDate.getTime()) { // future date

                        gridViewBlockTextView.setTextColor(mContext.getResources().getColor(R.color.ht_gray_calendar_text));

                    } else { // past date/clickable

                        gridViewBlockTextView.setTextColor(Color.WHITE);

                        if (gridViewBlockHeight <= 36 && gridViewImage[position].contains("_selected")) {

                            calendarBlockImage.setPadding((int)(.5 * displayDensity), (int)(.5 * displayDensity), (int)(.5 * displayDensity), (int)(.5 * displayDensity));

                        } else if (gridViewBlockHeight <= 36) {

                            calendarBlockImage.setPadding((int)(2 * displayDensity), (int)(2 * displayDensity), (int)(2 * displayDensity), (int)(2 * displayDensity));
                        }

                        calendarBlockImage.setImageResource(mContext.getResources().getIdentifier(gridViewImage[position], "drawable", mContext.getPackageName()));

                        if (gridViewType[position].equals("current")) {

                            gridViewBlock.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    clickedCurrentMonthDay(Integer.parseInt(gridViewText[position]));
                                }
                            });

                        } else { // next

                            gridViewBlock.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    clickedNextMonthDay(Integer.parseInt(gridViewText[position]));
                                }
                            });
                        }
                    }

                } else { // prev, always clickable

                    gridViewBlockTextView.setTextColor(Color.WHITE);

                    if (gridViewBlockHeight <= 36 && gridViewImage[position].contains("_selected")) {

                        calendarBlockImage.setPadding((int)(.5 * displayDensity), (int)(.5 * displayDensity), (int)(.5 * displayDensity), (int)(.5 * displayDensity));

                    } else if (gridViewBlockHeight <= 36) {

                        calendarBlockImage.setPadding((int)(2 * displayDensity), (int)(2 * displayDensity), (int)(2 * displayDensity), (int)(2 * displayDensity));
                    }

                    calendarBlockImage.setImageResource(mContext.getResources().getIdentifier(gridViewImage[position], "drawable", mContext.getPackageName()));

                    gridViewBlock.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickedPreviousMonthDay(Integer.parseInt(gridViewText[position]));
                        }
                    });
                }

                gridViewBlockTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

                gridViewBlockTextView.setText(gridViewText[position]);

                // Additional "today" indicator circle for calendar

                if (gridViewText[position].equals(currentDayOfMonth + "") && passMonth == currentMonth && gridViewType[position].equals("current")) { // gridViewType[position].equals("current")

                    ImageView additionalCalendarBlockImage = (ImageView) gridViewBlock.findViewById(R.id.additionalCalendarBlockImage);

                    if (gridViewBlockHeight <= 36) {

                        additionalCalendarBlockImage.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

                    } else {

                        additionalCalendarBlockImage.setScaleType(ImageView.ScaleType.CENTER);
                    }

                    if (gridViewText[position].equals(passDay + "")) {

                        if (gridViewBlockHeight <= 36) {

                            additionalCalendarBlockImage.setPadding((int)(.5 * displayDensity), (int)(.5 * displayDensity), (int)(.5 * displayDensity), (int)(.5 * displayDensity));
                        }

                        additionalCalendarBlockImage.setImageResource(mContext.getResources().getIdentifier("ht_calendar_today_selected", "drawable", mContext.getPackageName()));

                    } else {

                        if (gridViewBlockHeight <= 36) {

                            additionalCalendarBlockImage.setPadding((int)(2 * displayDensity), (int)(2 * displayDensity), (int)(2 * displayDensity), (int)(2 * displayDensity));
                        }

                        additionalCalendarBlockImage.setImageResource(mContext.getResources().getIdentifier("ht_calendar_today", "drawable", mContext.getPackageName()));
                    }
                }

                return gridViewBlock;
            }
        }
    }

    private void stopThread() {

        if(myThread != null){

            new Thread() {

                @Override
                public void run() {

                    if(post != null) {

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

        // subtract one month
        passDate = HTGlobals.getInstance().addNumberOfMonthsToPassDate(passDate, -1, true);
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;
        HTGlobals.getInstance().plannerShouldRefresh = true;
        // getCalendar
        getCalendar();
    }

    private void rightDateButtonClicked() {

        stopThread();

        // add one month
        passDate = HTGlobals.getInstance().addNumberOfMonthsToPassDate(passDate, 1, true);
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;
        HTGlobals.getInstance().plannerShouldRefresh = true;
        // getCalendar
        getCalendar();
    }

    private void clickedPreviousMonthDay(int WhichDay) {

        if (passMonth == 0) { // already january

            passMonth = 11; // december
            passYear -= 1; // previous year

        } else {

            passMonth -= 1;
        }

        passDay = WhichDay;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        String newDateString = passYear + "-" + (passMonth + 1) + "-" + passDay;

        Date newDate;

        try {

            newDate = dateFormat.parse(newDateString);

            passDate = newDate;

            HTGlobals.getInstance().passDate = passDate;
            HTGlobals.getInstance().passDay = passDay;
            HTGlobals.getInstance().passMonth = passMonth;
            HTGlobals.getInstance().passYear = passYear;

        } catch (ParseException e) {

            //
        }

        HTGlobals.getInstance().plannerShouldRefresh = true;

        getCalendar();
    }

    private void clickedCurrentMonthDay(int WhichDay) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        String newDateString = passYear + "-" + (passMonth + 1) + "-" + WhichDay;

        Date newDate;

        try {

            newDate = dateFormat.parse(newDateString);

            passDate = newDate;
            passDay = WhichDay;

            HTGlobals.getInstance().passDate = passDate;
            HTGlobals.getInstance().passDay = passDay;

        } catch (ParseException e) {

            //
        }

        HTGlobals.getInstance().plannerShouldRefresh = true;

        showCalendar(); // no need to refresh the web call (getCalendar())
    }

    private void clickedNextMonthDay(int WhichDay) {

        if (passMonth == 11) { // already december

            passMonth = 0; // january
            passYear += 1; // next year

        } else {

            passMonth += 1;
        }

        passDay = WhichDay;

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.US);

        String newDateString = passYear + "-" + (passMonth + 1) + "-" + passDay;

        Date newDate;

        try {

            newDate = dateFormat.parse(newDateString);

            passDate = newDate;

            HTGlobals.getInstance().passDate = passDate;
            HTGlobals.getInstance().passDay = passDay;
            HTGlobals.getInstance().passMonth = passMonth;
            HTGlobals.getInstance().passYear = passYear;

        } catch (ParseException e) {

            //
        }

        HTGlobals.getInstance().plannerShouldRefresh = true;
        getCalendar();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // coming back from a child activity, refresh the calendar

        passDate = HTGlobals.getInstance().passDate;
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;

        getCalendar();
    }
}
