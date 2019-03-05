package com.sph.healthtrac.tracker.activitytracker;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTDatePicker;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.common.HTTypefaceSpan;
import com.sph.healthtrac.common.PixelUtil;
import com.sph.healthtrac.planner.OnSwipeTouchListener;
import com.sph.healthtrac.planner.addactivity.HTAddActivitySelectItemActivity;
import com.sph.healthtrac.planner.addactivity.HTNewAddExerciseSearchActivity;
import com.sph.healthtrac.tracker.HTMetricChartEditActivity;
import com.sph.healthtrac.tracker.HTTrackerReminderActivity;
import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
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

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ActivityTrackerActivity extends Activity {

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

    private static LinearLayout linearLayoutActivityTracker;

    Toast toast;

    private static List<String> activityTrackerLabels = new ArrayList<>();
    private static List<String> activityTrackerTypes = new ArrayList<>();
    private static List<String> activityTrackerValues = new ArrayList<>();
    private static List<String> activityTrackerGoals = new ArrayList<>();
    private static List<String> activityTrackerReminders = new ArrayList<>();
    private static List<String> activityTrackerMetricNumber = new ArrayList<>();

    private static RelativeLayout relativeLayoutActivityTracker;

    View datePicker;

    Calendar calendar;

    HttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;

    private Thread myThread = null;

    private boolean leftDateButtonClicked;
    private boolean rightDateButtonClicked;

    private int metricReminderClicked;

    ProgressDialog progressDialog;

    private static InputMethodManager imm;

    private View addExerciseView;
    private boolean showExerciseItems;
    private String exerciseCalories;
    private String selectedPlannerItemID = "";
    private List<String> plannerItemID = new ArrayList<>();
    private List<String> plannerItemNotes = new ArrayList<>();
    private List<String> plannerItemCaloriesBurned = new ArrayList<>();

    private boolean isShowingDeleteConfirmDlg;

    private Typeface avenirNextMediumFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activity_tracker);

        progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        relativeLayoutActivityTracker = (RelativeLayout) findViewById(R.id.activityTrackerContainer);

        Typeface typeCurrentGoalLabels = Typeface.createFromAsset(this.getAssets(), "fonts/AvenirNext-Regular.ttf");
        avenirNextMediumFont = Typeface.createFromAsset(this.getAssets(), "fonts/AvenirNext-Medium.ttf");

        TextView textViewCurrent = (TextView) findViewById(R.id.textViewCurrent);
        TextView textViewGoal = (TextView) findViewById(R.id.textViewGoal);

        textViewCurrent.setTypeface(typeCurrentGoalLabels);
        textViewCurrent.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
        textViewCurrent.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        textViewGoal.setTypeface(typeCurrentGoalLabels);
        textViewGoal.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
        textViewGoal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

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

        RelativeLayout.LayoutParams params;

        // action bar
        View mActionBar = HTActionBar.getActionBar(this, "Activity Tracker", "Cancel", "Done");

        relativeLayoutActivityTracker.addView(mActionBar);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        float displayDensity = displayMetrics.density;

        int dpValue = 44;
        int topBarHeight = (int)(dpValue * displayDensity);

        params = (RelativeLayout.LayoutParams) mActionBar.getLayoutParams();
        params.height = topBarHeight;

        mActionBar.setLayoutParams(params);

        // Cancel button
        mActionBar.findViewById(R.id.leftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imm.hideSoftInputFromWindow(relativeLayoutActivityTracker.getWindowToken(), 0);
                setResult(1);
                finish();
            }
        });

        // Done button
        mActionBar.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                updateActivityTracker();
            }
        });

        // date picker
        datePicker = HTDatePicker.getDatePicker(ActivityTrackerActivity.this);

        relativeLayoutActivityTracker.addView(datePicker);

        params = (RelativeLayout.LayoutParams) datePicker.getLayoutParams();
        params.height = topBarHeight;
        params.setMargins(0, topBarHeight, 0, 0);  // left, top, right, bottom

        datePicker.setLayoutParams(params);

        metricReminderClicked = 0;

        if(login.equals("") || pw.equals("")) {

            Intent intent = new Intent(ActivityTrackerActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();

        } else {

            getActivityTracker();
        }
    }

    private void leftDateButtonClicked() {

        ImageView leftArrow = (ImageView) datePicker.findViewById(R.id.leftArrow);

        imm.hideSoftInputFromWindow(leftArrow.getWindowToken(), 0);

        leftDateButtonClicked = true;

        updateActivityTracker();
    }

    private void rightDateButtonClicked() {

        ImageView rightArrow = (ImageView) datePicker.findViewById(R.id.rightArrow);

        imm.hideSoftInputFromWindow(rightArrow.getWindowToken(), 0);
        rightDateButtonClicked = true;

        updateActivityTracker();
    }

    public void metricChecked(Integer whichTag) {

        linearLayoutActivityTracker = (LinearLayout)relativeLayoutActivityTracker.findViewById(R.id.linearLayoutActivityTracker);
        ImageView metricCheckbox = (ImageView) linearLayoutActivityTracker.findViewWithTag(whichTag);

        if (activityTrackerValues.get(whichTag).equals("1")) {

            metricCheckbox.setImageResource(R.drawable.ht_check_off_green);

            activityTrackerValues.set(whichTag, "0");

        } else {

            metricCheckbox.setImageResource(R.drawable.ht_check_on_green);

            activityTrackerValues.set(whichTag, "1");
        }
    }

    private void getActivityTracker() {

        progressDialog.show();

        activityTrackerLabels.clear();
        activityTrackerTypes.clear();
        activityTrackerValues.clear();
        activityTrackerGoals.clear();
        activityTrackerReminders.clear();
        activityTrackerMetricNumber.clear();

        leftDateButtonClicked = false;
        rightDateButtonClicked = false;

        metricReminderClicked = 0;

        plannerItemID.clear();
        plannerItemNotes.clear();
        plannerItemCaloriesBurned.clear();
        exerciseCalories = "";

        final ImageView leftArrow = (ImageView) datePicker.findViewById(R.id.leftArrow);
        final ImageView rightArrow = (ImageView) datePicker.findViewById(R.id.rightArrow);

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
                    nameValuePairs.add(new BasicNameValuePair("action", "get_activity"));
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

                    NodeList error = doc.getElementsByTagName("error");
                    NodeList nodes = doc.getElementsByTagName("dashboard_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(ActivityTrackerActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    }
                    else {

                        final Element e2 = (Element)nodes.item(0);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                Double tempDouble;
                                String tempString;

                                // weight
                                activityTrackerLabels.add("Weight");
                                activityTrackerTypes.add("text");

                                tempString = XMLFunctions.getValue(e2, "weight").replace(",", "");
                                if (tempString.equals("")) {
                                    tempString = "0";
                                }
                                tempDouble = Double.parseDouble(tempString);
                                tempString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(tempDouble);
                                activityTrackerValues.add(tempString);
                                activityTrackerMetricNumber.add("11");

                                tempString = XMLFunctions.getValue(e2, "weight_goal").replace(",", "");
                                if (tempString.equals("")) {
                                    tempString = "0";
                                }
                                tempDouble = Double.parseDouble(tempString);
                                tempString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(tempDouble);
                                activityTrackerGoals.add(tempString);

                                activityTrackerReminders.add(XMLFunctions.getValue(e2, "metric11_reminder"));

                                // walking_steps
                                if(XMLFunctions.tagExists(e2, "walking_steps")) {

                                    activityTrackerLabels.add("Walking Steps");
                                    activityTrackerTypes.add("text");

                                    tempString = XMLFunctions.getValue(e2, "walking_steps").replace(",", "");
                                    if (tempString.equals("")) {
                                        tempString = "0";
                                    }
                                    tempDouble = Double.parseDouble(tempString);
                                    tempString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(tempDouble);
                                    activityTrackerValues.add(tempString);
                                    activityTrackerMetricNumber.add("12");

                                    tempString = XMLFunctions.getValue(e2, "walking_steps_goal").replace(",", "");
                                    if (tempString.equals("")) {
                                        tempString = "0";
                                    }
                                    tempDouble = Double.parseDouble(tempString);
                                    tempString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(tempDouble);
                                    activityTrackerGoals.add(tempString);

                                    activityTrackerReminders.add(XMLFunctions.getValue(e2, "metric12_reminder"));
                                }

                                // exercise_minutes
                                if(XMLFunctions.tagExists(e2, "exercise_minutes")) {

                                    activityTrackerLabels.add("Exercise Minutes");
                                    activityTrackerTypes.add("text");

                                    tempString = XMLFunctions.getValue(e2, "exercise_minutes").replace(",", "");
                                    if (tempString.equals("")) {
                                        tempString = "0";
                                    }
                                    tempDouble = Double.parseDouble(tempString);
                                    tempString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(tempDouble);
                                    activityTrackerValues.add(tempString);
                                    activityTrackerMetricNumber.add("13");

                                    tempString = XMLFunctions.getValue(e2, "exercise_minutes_goal").replace(",", "");
                                    if (tempString.equals("")) {
                                        tempString = "0";
                                    }
                                    tempDouble = Double.parseDouble(tempString);
                                    tempString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(tempDouble);
                                    activityTrackerGoals.add(tempString);

                                    activityTrackerReminders.add(XMLFunctions.getValue(e2, "metric13_reminder"));
                                }

                                // sleep_hours
                                if(XMLFunctions.tagExists(e2, "sleep_hours")) {

                                    activityTrackerLabels.add("Sleep Hours");
                                    activityTrackerTypes.add("text");

                                    tempString = XMLFunctions.getValue(e2, "sleep_hours").replace(",", "");
                                    if (tempString.equals("")) {
                                        tempString = "0";
                                    }
                                    tempDouble = Double.parseDouble(tempString);
                                    tempString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(tempDouble);
                                    activityTrackerValues.add(tempString);
                                    activityTrackerMetricNumber.add("14");

                                    tempString = XMLFunctions.getValue(e2, "sleep_hours_goal").replace(",", "");
                                    if (tempString.equals("")) {
                                        tempString = "0";
                                    }
                                    tempDouble = Double.parseDouble(tempString);
                                    tempString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(tempDouble);
                                    activityTrackerGoals.add(tempString);

                                    activityTrackerReminders.add(XMLFunctions.getValue(e2, "metric14_reminder"));
                                }

                                int j = 1;
                                while (XMLFunctions.tagExists(e2, "planner_item_" + j)) {
                                    tempString = XMLFunctions.getValue(e2, "id_" + j);
                                    plannerItemID.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "burned_calories_" + j);
                                    plannerItemCaloriesBurned.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "notes_" + j);
                                    plannerItemNotes.add(tempString);
                                    j++;
                                }

                                //exercise calories
                                if (XMLFunctions.tagExists(e2, "exercise_calories")) {
                                    exerciseCalories = XMLFunctions.getValue(e2, "exercise_calories");
                                }

                                // CUSTOM METRICS!!!
                                for (int i=1; i<=10; i++) {

                                    tempString = XMLFunctions.getValue(e2, "metric" + i + "_label");

                                    if (!tempString.equals("")) {

                                        activityTrackerLabels.add(tempString);

                                        if (XMLFunctions.getValue(e2, "metric" + i + "_type").equals("2")) { // check box metric

                                            activityTrackerTypes.add("checkbox");

                                        } else {

                                            activityTrackerTypes.add("text");
                                        }

                                        tempString = XMLFunctions.getValue(e2, "metric" + i).replace(",", "");
                                        if (tempString.equals("")) {
                                            tempString = "0";
                                        }
                                        tempDouble = Double.parseDouble(tempString);
                                        tempString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(tempDouble);
                                        activityTrackerValues.add(tempString);
                                        activityTrackerMetricNumber.add(i + "");

                                        tempString = XMLFunctions.getValue(e2, "metric" + i + "_goal").replace(",", "");
                                        if (tempString.equals("")) {
                                            tempString = "0";
                                        }
                                        tempDouble = Double.parseDouble(tempString);
                                        tempString = NumberFormat.getNumberInstance(getResources().getConfiguration().locale).format(tempDouble);
                                        activityTrackerGoals.add(tempString);

                                        activityTrackerReminders.add(XMLFunctions.getValue(e2, "metric" + i + "_reminder"));
                                    }
                                }

                                showActivityTracker();
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
//                            toast = HTToast.showToast(ActivityTrackerActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(ActivityTrackerActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                        }
                    });

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

    private void updateActivityTracker() {

        imm.hideSoftInputFromWindow(relativeLayoutActivityTracker.getWindowToken(), 0);

        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "update_activity"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));

                    linearLayoutActivityTracker = (LinearLayout)relativeLayoutActivityTracker.findViewById(R.id.linearLayoutActivityTracker);

                    EditText metricEditText;
                    String tempValue;

                    for (int i=0; i<activityTrackerMetricNumber.size(); i++) {

                        if (activityTrackerMetricNumber.get(i).equals("11")) { // weight

                            metricEditText = (EditText) linearLayoutActivityTracker.findViewWithTag(i);
                            tempValue = metricEditText.getText().toString();
                            tempValue = tempValue.replace(",", "");
                            nameValuePairs.add(new BasicNameValuePair("weight", tempValue));

                            metricEditText = (EditText) linearLayoutActivityTracker.findViewWithTag(i + 20);
                            tempValue = metricEditText.getText().toString();
                            tempValue = tempValue.replace(",", "");
                            nameValuePairs.add(new BasicNameValuePair("weight_goal", tempValue));

                        } else if (activityTrackerMetricNumber.get(i).equals("12")) { // walking steps

                            metricEditText = (EditText) linearLayoutActivityTracker.findViewWithTag(i);
                            tempValue = metricEditText.getText().toString();
                            tempValue = tempValue.replace(",", "");
                            nameValuePairs.add(new BasicNameValuePair("walking_steps", tempValue));


                            metricEditText = (EditText) linearLayoutActivityTracker.findViewWithTag(i + 20);
                            tempValue = metricEditText.getText().toString();
                            tempValue = tempValue.replace(",", "");
                            nameValuePairs.add(new BasicNameValuePair("walking_steps_goal", tempValue));

                        } else if (activityTrackerMetricNumber.get(i).equals("13")) { // exercise minutes

                            metricEditText = (EditText) linearLayoutActivityTracker.findViewWithTag(i);
                            tempValue = metricEditText.getText().toString();
                            tempValue = tempValue.replace(",", "");
                            nameValuePairs.add(new BasicNameValuePair("exercise_minutes", tempValue));

                            metricEditText = (EditText) linearLayoutActivityTracker.findViewWithTag(i + 20);
                            tempValue = metricEditText.getText().toString();
                            tempValue = tempValue.replace(",", "");
                            nameValuePairs.add(new BasicNameValuePair("exercise_minutes_goal", tempValue));

                        } else if (activityTrackerMetricNumber.get(i).equals("14")) { // sleep hours

                            metricEditText = (EditText) linearLayoutActivityTracker.findViewWithTag(i);
                            tempValue = metricEditText.getText().toString();
                            tempValue = tempValue.replace(",", "");
                            nameValuePairs.add(new BasicNameValuePair("sleep_hours", tempValue));

                            metricEditText = (EditText) linearLayoutActivityTracker.findViewWithTag(i + 20);
                            tempValue = metricEditText.getText().toString();
                            tempValue = tempValue.replace(",", "");
                            nameValuePairs.add(new BasicNameValuePair("sleep_hours_goal", tempValue));

                        } else { // custom metrics

                            if (activityTrackerTypes.get(i).equals("checkbox")) {

                                tempValue = activityTrackerValues.get(i);
                                nameValuePairs.add(new BasicNameValuePair("metric" + activityTrackerMetricNumber.get(i), tempValue));

                            } else {

                                metricEditText = (EditText) linearLayoutActivityTracker.findViewWithTag(i);
                                tempValue = metricEditText.getText().toString();
                                tempValue = tempValue.replace(",", "");
                                nameValuePairs.add(new BasicNameValuePair("metric" + activityTrackerMetricNumber.get(i), tempValue));

                                metricEditText = (EditText) linearLayoutActivityTracker.findViewWithTag(i + 20);
                                tempValue = metricEditText.getText().toString();
                                tempValue = tempValue.replace(",", "");
                                nameValuePairs.add(new BasicNameValuePair("metric" + activityTrackerMetricNumber.get(i) + "_goal", tempValue));
                            }
                        }
                    }

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

                                progressDialog.dismiss();

                                toast = HTToast.showToast(ActivityTrackerActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    }
                    else {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                progressDialog.dismiss();

                                if (leftDateButtonClicked) {

                                    // subtract one day
                                    passDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, -1, true);
                                    passDay = HTGlobals.getInstance().passDay;
                                    passMonth = HTGlobals.getInstance().passMonth;
                                    passYear = HTGlobals.getInstance().passYear;
                                    HTGlobals.getInstance().plannerShouldRefresh = true;
                                    getActivityTracker();

                                } else if (rightDateButtonClicked) {

                                    // add one day
                                    passDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, 1, true);
                                    passDay = HTGlobals.getInstance().passDay;
                                    passMonth = HTGlobals.getInstance().passMonth;
                                    passYear = HTGlobals.getInstance().passYear;
                                    HTGlobals.getInstance().plannerShouldRefresh = true;
                                    getActivityTracker();

                                } else if (metricReminderClicked != 0) {

                                    Intent intent = new Intent(ActivityTrackerActivity.this, HTTrackerReminderActivity.class);
                                    intent.putExtra("metric", metricReminderClicked + "");
                                    ActivityTrackerActivity.this.startActivityForResult(intent, 1);

                                    //getActivityTracker();

                                } else { // DONE

                                    setResult(1);
                                    finish();
                                }
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            progressDialog.dismiss();
//                            toast = HTToast.showToast(ActivityTrackerActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(ActivityTrackerActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                        }
                    });
                }
            }
        };

        myThread.start();
    }

    private void showActivityTracker() {

        LinearLayout linearLayoutActivityTracker = (LinearLayout) relativeLayoutActivityTracker.findViewById(R.id.linearLayoutActivityTracker);

        LayoutInflater inflater = this.getLayoutInflater();

        RelativeLayout.LayoutParams params;

        View rowView;

        Typeface typeLabel = Typeface.createFromAsset(this.getAssets(), "fonts/AvenirNext-Regular.ttf");
        Typeface typeValue = Typeface.createFromAsset(this.getAssets(), "fonts/OpenSans-Light.ttf");

        ImageView imageViewActivityTrackerReminderIcon;

        TextView textViewActivityLabel;

        EditText editTextActivityValue;
        EditText editTextActivityGoal;

        ImageView imageViewActivityValueCheck;

        linearLayoutActivityTracker.removeAllViews();

        for (int i=0; i<activityTrackerLabels.size(); i++) {

            final Integer thisTag = i;

            rowView = inflater.inflate(R.layout.activity_tracker_list_view_cell, null, true);

            imageViewActivityTrackerReminderIcon = (ImageView) rowView.findViewById(R.id.imageViewActivityTrackerReminderIcon);

            textViewActivityLabel = (TextView) rowView.findViewById(R.id.textViewActivityLabel);

            editTextActivityValue = (EditText) rowView.findViewById(R.id.editTextActivityValue);
            editTextActivityGoal = (EditText) rowView.findViewById(R.id.editTextActivityGoal);

            imageViewActivityValueCheck = (ImageView) rowView.findViewById(R.id.imageViewActivityValueCheck);

            // reminder icon?
            if (activityTrackerReminders.get(i).equals("Y")) {

                imageViewActivityTrackerReminderIcon.setImageResource(R.drawable.ht_reminder_on);

            } else {

                imageViewActivityTrackerReminderIcon.setImageResource(R.drawable.ht_reminder);
            }

            imageViewActivityTrackerReminderIcon.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    metricReminderClicked = Integer.parseInt(activityTrackerMetricNumber.get(thisTag));

                    updateActivityTracker();

                    /*
                    Intent intent = new Intent(ActivityTrackerActivity.this, HTTrackerReminderActivity.class);
                    intent.putExtra("metric", activityTrackerMetricNumber.get(thisTag));
                    ActivityTrackerActivity.this.startActivityForResult(intent, 1);
                    */
                }
            });

            // textViewActivityLabel
            textViewActivityLabel.setTypeface(typeLabel);
            textViewActivityLabel.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
            textViewActivityLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            textViewActivityLabel.setText(activityTrackerLabels.get(i));

            // check box?
            if (activityTrackerTypes.get(i).equals("checkbox")) { // check box metric

                params = (RelativeLayout.LayoutParams) editTextActivityValue.getLayoutParams();
                params.width = 0;
                params.height = 0;
                editTextActivityValue.setLayoutParams(params);

                params = (RelativeLayout.LayoutParams) editTextActivityGoal.getLayoutParams();
                params.width = 0;
                params.height = 0;
                editTextActivityGoal.setLayoutParams(params);

                imageViewActivityValueCheck.setTag(thisTag);
                imageViewActivityValueCheck.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        ActivityTrackerActivity.this.metricChecked(thisTag);
                    }
                });

                imageViewActivityValueCheck.setImageResource(R.drawable.ht_check_off_green);

                if (activityTrackerValues.get(i).equals("1")) { // on!

                    imageViewActivityValueCheck.setImageResource(R.drawable.ht_check_on_green);
                }

            } else { // regular, numeric metric

                params = (RelativeLayout.LayoutParams) imageViewActivityValueCheck.getLayoutParams();
                params.width = 0;
                params.height = 0;
                imageViewActivityValueCheck.setLayoutParams(params);

                editTextActivityValue.setTypeface(typeValue);
                editTextActivityValue.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
                editTextActivityValue.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                editTextActivityValue.setHintTextColor(this.getResources().getColor(R.color.ht_color_light_gray_text));
                editTextActivityValue.setHint("0");
                editTextActivityValue.setTag(thisTag);

                editTextActivityGoal.setTypeface(typeValue);
                editTextActivityGoal.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
                editTextActivityGoal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                editTextActivityGoal.setHintTextColor(this.getResources().getColor(R.color.ht_color_light_gray_text));
                editTextActivityGoal.setHint("0");
                editTextActivityGoal.setTag(thisTag + 20);

                if (!(currentDate.equals(passDate))) {

                    editTextActivityGoal.setEnabled(false);
                    editTextActivityGoal.setBackgroundResource(0);
                }

                //editTextActivityValue
                if (!activityTrackerValues.get(i).equals("0")) {

                    editTextActivityValue.setText(activityTrackerValues.get(i));
                }
                //editTextActivityGoal
                if (!activityTrackerGoals.get(i).equals("0")) {

                    editTextActivityGoal.setText(activityTrackerGoals.get(i));
                }
            }

            DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

            float displayDensity = displayMetrics.density;
            float screenWidth = displayMetrics.widthPixels;

            int rowHeight = (int) (60 * displayDensity);

            params = (RelativeLayout.LayoutParams) textViewActivityLabel.getLayoutParams();

            params.width = (int) ((70 * displayDensity) + ((screenWidth * displayDensity) - 320)); // offset
            textViewActivityLabel.setLayoutParams(params);

            RelativeLayout relativeLayout = (RelativeLayout) rowView.findViewById(R.id.activityTrackerRow);

            params = (RelativeLayout.LayoutParams) relativeLayout.getLayoutParams();
            params.height = rowHeight;

            relativeLayout.setLayoutParams(params);

            linearLayoutActivityTracker.addView(rowView);

            //exercise item
            if("Exercise Minutes".equals(activityTrackerLabels.get(i))){
                editTextActivityValue.setKeyListener(null);
                editTextActivityValue.setFocusableInTouchMode(false);
                editTextActivityValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imm.hideSoftInputFromWindow(addExerciseView.getWindowToken(), 0);
                        if(showExerciseItems) {
                            addExerciseView.setVisibility(View.GONE);
                            showExerciseItems = false;
                        } else {
                            addExerciseView.setVisibility(View.VISIBLE);
                            showExerciseItems = true;
                        }
                    }
                });
                addExerciseView = inflater.inflate(R.layout.activity_tracker_add_exercise_view, null);

                TextView exerciseLabelView = (TextView) addExerciseView.findViewById(R.id.exerciseLabelView);
                TextView exerciseCaloriesView = (TextView) addExerciseView.findViewById(R.id.exerciseCaloriesView);
                exerciseLabelView.setTypeface(avenirNextMediumFont);
                exerciseCaloriesView.setTypeface(avenirNextMediumFont);
                LinearLayout exercisePlanDetailsLayout = (LinearLayout) addExerciseView.findViewById(R.id.exercisePlanDetailsLayout);
                RelativeLayout addExercisePlanLayout = (RelativeLayout) addExerciseView.findViewById(R.id.addExercisePlanLayout);
                addExercisePlanLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ActivityTrackerActivity.this, HTNewAddExerciseSearchActivity.class);
                        intent.putExtra("addActivityCategory", "exercise");
                        startActivityForResult(intent, 1);
                    }
                });

                boolean hasExerciseItems = false;
                LinearLayout.LayoutParams linearParams;

                //exercise calories
                // exercise calories
                if("".equals(exerciseCalories) || "0".equals(exerciseCalories)){
                    exerciseCaloriesView.setText("");
                }else{
                    SpannableString spannableString = new SpannableString(exerciseCalories + "cal");
                    spannableString.setSpan(new HTTypefaceSpan("", avenirNextMediumFont, PixelUtil.dpToPx(this, 11)), spannableString.length()-3, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
                    exerciseCaloriesView.setText(spannableString);
                }

                // exercise items!
                for (int j = 0; j < plannerItemID.size(); j++) {
                    final int index = j;
                    hasExerciseItems = true;

                    RelativeLayout plannerItemView = (RelativeLayout) getLayoutInflater().inflate(R.layout.planner_item_view_new, null);

                    plannerItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedPlannerItemID = plannerItemID.get(index);
                            Intent intent = new Intent(ActivityTrackerActivity.this, HTAddActivitySelectItemActivity.class);

                            intent.putExtra("selectedActivityID", Integer.parseInt(selectedPlannerItemID));
                            intent.putExtra("relaunchPlannerItem", true);

                            intent.putExtra("addActivityCategory", "exercise");

                            startActivityForResult(intent, 1);
                        }
                    });

                    plannerItemView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            selectedPlannerItemID = plannerItemID.get(index);
                            showConfirmDeleteItem();
                            return true;
                        }
                    });

                    plannerItemView.setOnTouchListener(new OnSwipeTouchListener(this) {
                        @Override
                        public void onSwipeLeft() {
                            selectedPlannerItemID = plannerItemID.get(index);
                            showConfirmDeleteItem();
                        }

                        @Override
                        public void onSwipeRight() {
                            selectedPlannerItemID = plannerItemID.get(index);
                            showConfirmDeleteItem();
                        }
                    });

                    linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(this, 44));
                    plannerItemView.setLayoutParams(linearParams);

                    TextView notesLabel = (TextView) plannerItemView.findViewById(R.id.notesLabel);
                    TextView subnotesLabel = (TextView) plannerItemView.findViewById(R.id.subnotesLabel);
                    subnotesLabel.setVisibility(View.GONE);
                    TextView caloriesView = (TextView) plannerItemView.findViewById(R.id.caloriesView);
                    notesLabel.setTypeface(avenirNextMediumFont);
                    caloriesView.setTypeface(avenirNextMediumFont);
                    notesLabel.setText(plannerItemNotes.get(j));
                    if (!"".equals(plannerItemCaloriesBurned.get(j)) && !"0".equals(plannerItemCaloriesBurned.get(j))) {
                        caloriesView.setText(plannerItemCaloriesBurned.get(j));
                    }

                    exercisePlanDetailsLayout.addView(plannerItemView);
                }

                // separator?
                if(hasExerciseItems){
                    View barView = new View(this);
                    barView.setBackgroundColor(Color.rgb(215, 226, 230));
                    linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(this, 1));
                    barView.setLayoutParams(linearParams);
                    exercisePlanDetailsLayout.addView(barView);
                }

                linearLayoutActivityTracker.addView(addExerciseView);
                linearParams = (LinearLayout.LayoutParams) addExerciseView.getLayoutParams();
                linearParams.topMargin = PixelUtil.dpToPx(this, -1);
                addExerciseView.setLayoutParams(linearParams);

                if(!showExerciseItems){
                    addExerciseView.setVisibility(View.GONE);
                }
            }
        }
    }

    private void deleteFoodItem() {
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_food_delete_item"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("WhichID", selectedPlannerItemID));

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

                                toast = HTToast.showToast(ActivityTrackerActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                getActivityTracker();
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
//                                    toast = HTToast.showToast(ActivityTrackerActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(ActivityTrackerActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showConfirmDeleteItem(){
        if(isShowingDeleteConfirmDlg)
            return;

        isShowingDeleteConfirmDlg = true;
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle("Delete Item?")
                .setMessage("Are you sure you want to delete this Exercise item?")
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteFoodItem();
                        isShowingDeleteConfirmDlg = false;
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        selectedPlannerItemID = "";
                        isShowingDeleteConfirmDlg = false;
                    }
                }).create();
        dialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // coming back from a child activity, refresh

        passDate = HTGlobals.getInstance().passDate;
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;

        getActivityTracker();
    }
}