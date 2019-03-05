package com.sph.healthtrac.tracker;

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
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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
import com.sph.healthtrac.planner.OnSwipeTouchListener;
import com.sph.healthtrac.planner.addactivity.HTAddActivitySelectItemActivity;
import com.sph.healthtrac.planner.addactivity.HTNewAddExerciseSearchActivity;
import com.sph.healthtrac.tracker.activitytracker.ActivityTrackerActivity;

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
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

public class HTMetricChartEditActivity extends Activity {

    private static RelativeLayout mainContentLayout;
    private ScrollView scrollView;
    private View datePicker;
    private TextView mDateTitleTextView;

    private LinearLayout goalLayout;
    private TextView goalLabel;
    private EditText goalEdit;
    private TextView metricLabel;
    private EditText metricValueEdit;
    private ImageView metricCheckBox;
    private TextView textReminderLabel;
    private TextView reminderTimeEdit;

    private LinearLayout addExerciseLayout;
    private TextView exerciseLabelView;
    private TextView exerciseCaloriesView;
    private LinearLayout exercisePlanDetailsLayout;
    private RelativeLayout addExercisePlanLayout;

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

    private String selectedMetric;
    private String selectedCustomMetricString;
    private String chartMetricType;

    private String selectedMetricGoal;
    private String selectedMetricValue;
    private String selectedMetricReminder;
    private String selectedMetricReminderFraction = ":00";
    private String selectedMetricReminderAmPm;
    private String exerciseCalories;

    private Typeface avenirNextRegularFont;
    private Typeface avenirNextMediumFont;
    private Typeface openSansLightFont;

    private boolean isMetricChecked;

    private String selectedPlannerItemID = "";
    private List<String> plannerItemID = new ArrayList<>();
    private List<String> plannerItemNotes = new ArrayList<>();
    private List<String> plannerItemCaloriesBurned = new ArrayList<>();

    private boolean isShowingDeleteConfirmDlg;

    private boolean leftDateButtonClicked;
    private boolean rightDateButtonClicked;

    private NumberFormat mNumberFormat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_htmetric_chart_edit);

        selectedMetric = getIntent().getStringExtra("selectedMetric");
        selectedCustomMetricString = getIntent().getStringExtra("selectedCustomMetricString");

        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollView.setVisibility(View.GONE);

        mNumberFormat = NumberFormat.getNumberInstance(getResources().getConfiguration().locale);
        avenirNextRegularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");
        openSansLightFont = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");

        goalLayout = (LinearLayout) findViewById(R.id.goalLayout);
        goalLabel = (TextView) findViewById(R.id.goalLabel);
        goalEdit = (EditText) findViewById(R.id.goalEdit);
        metricLabel = (TextView) findViewById(R.id.metricLabel);
        metricValueEdit = (EditText) findViewById(R.id.metricValueEdit);
        metricCheckBox = (ImageView) findViewById(R.id.metricCheckBox);
        metricCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isMetricChecked = !isMetricChecked;
                if(isMetricChecked)
                    metricCheckBox.setImageResource(R.drawable.ht_icon_favs_checkmark_selected);
                else
                    metricCheckBox.setImageResource(R.drawable.ht_icon_favs_checkmark);
            }
        });
        textReminderLabel = (TextView) findViewById(R.id.textReminderLabel);
        reminderTimeEdit = (TextView) findViewById(R.id.reminderTimeEdit);

        goalLabel.setTypeface(avenirNextRegularFont);
        goalEdit.setTypeface(openSansLightFont);
        metricLabel.setTypeface(avenirNextRegularFont);
        metricValueEdit.setTypeface(openSansLightFont);
        textReminderLabel.setTypeface(avenirNextRegularFont);
        reminderTimeEdit.setTypeface(openSansLightFont);

        addExerciseLayout = (LinearLayout) findViewById(R.id.addExerciseLayout);
        addExerciseLayout.setVisibility(View.GONE);
        exerciseLabelView = (TextView) findViewById(R.id.exerciseLabelView);
        exerciseCaloriesView = (TextView) findViewById(R.id.exerciseCaloriesView);
        exerciseLabelView.setTypeface(avenirNextMediumFont);
        exerciseCaloriesView.setTypeface(avenirNextMediumFont);
        exercisePlanDetailsLayout = (LinearLayout) findViewById(R.id.exercisePlanDetailsLayout);
        addExercisePlanLayout = (RelativeLayout) findViewById(R.id.addExercisePlanLayout);
        addExercisePlanLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HTMetricChartEditActivity.this, HTNewAddExerciseSearchActivity.class);
                intent.putExtra("addActivityCategory", "exercise");
                startActivityForResult(intent, 1);
            }
        });

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
        View mActionBar = HTActionBar.getActionBar(this, selectedMetric, "Cancel", "Done");

        mainContentLayout.addView(mActionBar);

        DisplayMetrics displayMetrics = this.getResources().getDisplayMetrics();

        float displayDensity = displayMetrics.density;

        int dpValue = 44;
        int topBarHeight = (int) (dpValue * displayDensity);

        params = (RelativeLayout.LayoutParams) mActionBar.getLayoutParams();
        params.height = topBarHeight;

        mActionBar.setLayoutParams(params);


        // Cancel button
        mActionBar.findViewById(R.id.leftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                setResult(1);
                finish();
            }
        });

        // Done button
        mActionBar.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopThread();
                updateMetric();
            }
        });

        // date picker
        datePicker = HTDatePicker.getDatePicker(this);

        mainContentLayout.addView(datePicker);

        params = (RelativeLayout.LayoutParams) datePicker.getLayoutParams();
        params.height = topBarHeight;
        params.setMargins(0, topBarHeight, 0, 0);  // left, top, right, bottom

        datePicker.setLayoutParams(params);

        View viewGraySeparator = datePicker.findViewById(R.id.viewGraySeparator);
        params = (RelativeLayout.LayoutParams) viewGraySeparator.getLayoutParams();
        params.height = (int) (2 * displayDensity);
        viewGraySeparator.setLayoutParams(params);

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

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();

        } else {
            getMetric();
        }
    }

    private void getMetric() {
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();

        leftDateButtonClicked = false;
        rightDateButtonClicked = false;
        isMetricChecked = false;

        plannerItemID.clear();
        plannerItemNotes.clear();
        plannerItemCaloriesBurned.clear();

        exerciseCalories = "";
        chartMetricType = "";

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
                    nameValuePairs.add(new BasicNameValuePair("action", "get_metric_charts_edit"));
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

                    NodeList error = doc.getElementsByTagName("error");
                    NodeList nodes = doc.getElementsByTagName("metric_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTMetricChartEditActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {

                        final Element e2 = (Element) nodes.item(0);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();

                                //Metrci value
                                if (XMLFunctions.tagExists(e2, "metric_value")) {
                                    selectedMetricValue = XMLFunctions.getValue(e2, "metric_value");
                                }

                                //Metric goal
                                if (XMLFunctions.tagExists(e2, "metric_goal")) {
                                    selectedMetricGoal = XMLFunctions.getValue(e2, "metric_goal");
                                }

                                //Metric reminder
                                if (XMLFunctions.tagExists(e2, "metric_reminder")) {
                                    selectedMetricReminder = htGlobals.cleanStringAfterReceiving(XMLFunctions.getValue(e2, "metric_reminder"));
                                    if (!"".equals(selectedMetricReminder)) {
                                        try {
                                            int timeValue = Integer.parseInt(selectedMetricReminder);
                                            if (timeValue > 12
                                                    && timeValue != 24) {

                                                selectedMetricReminder = "" + (timeValue - 12);

                                                selectedMetricReminderAmPm = "pm";

                                            } else if (timeValue == 24 ||
                                                    timeValue == 0) {

                                                selectedMetricReminder = "12";
                                                selectedMetricReminderAmPm = "am";

                                            } else if (timeValue == 12) {

                                                selectedMetricReminder = "12";
                                                selectedMetricReminderAmPm = "pm";

                                            } else {

                                                selectedMetricReminderAmPm = "am";
                                            }
                                        } catch (NumberFormatException nfe) {
                                            selectedMetricReminder = "";
                                        }
                                    }
                                }

                                String tempString;

                                int i = 1;
                                while (XMLFunctions.tagExists(e2, "planner_item_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "id_" + i);
                                    plannerItemID.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "burned_calories_" + i);
                                    plannerItemCaloriesBurned.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "notes_" + i);
                                    plannerItemNotes.add(tempString);
                                    i++;
                                }

                                //exercise calories
                                if (XMLFunctions.tagExists(e2, "exercise_calories")) {
                                    exerciseCalories = XMLFunctions.getValue(e2, "exercise_calories");
                                }

                                //chart_metric_type
                                if (XMLFunctions.tagExists(e2, "chart_metric_type")) {
                                    chartMetricType = XMLFunctions.getValue(e2, "chart_metric_type");
                                }

                                showMetric();
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

//                                    toast = HTToast.showToast(HTMetricChartEditActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTMetricChartEditActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void updateMetric() {

        imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);

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

        if ("2".equals(chartMetricType)) {
            if(isMetricChecked)
                selectedMetricValue = "1";
            else
                selectedMetricValue = "0";

            selectedMetricGoal = "1";
        } else {
            selectedMetricValue = metricValueEdit.getText().toString().replace(",", "");
            selectedMetricGoal = goalEdit.getText().toString().replace(",", "");
        }

        if("pm".equals(selectedMetricReminderAmPm) && Integer.parseInt(selectedMetricReminder) < 12) {
            selectedMetricReminder = (Integer.parseInt(selectedMetricReminder) + 12) + "";
        } else if ("am".equals(selectedMetricReminderAmPm) && Integer.parseInt(selectedMetricReminder) == 12) {
            selectedMetricReminder = "0";
        }

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_metric_charts_update"));
                    nameValuePairs.add(new BasicNameValuePair("metric_goal_only", "0"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("which_metric", whichMetric));
                    nameValuePairs.add(new BasicNameValuePair("metric_value", selectedMetricValue));
                    nameValuePairs.add(new BasicNameValuePair("metric_goal", selectedMetricGoal));
                    nameValuePairs.add(new BasicNameValuePair("metric_reminder", selectedMetricReminder));

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

                                toast = HTToast.showToast(HTMetricChartEditActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                if (leftDateButtonClicked) {

                                    // subtract one day
                                    passDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, -1, true);
                                    passDay = HTGlobals.getInstance().passDay;
                                    passMonth = HTGlobals.getInstance().passMonth;
                                    passYear = HTGlobals.getInstance().passYear;
                                    HTGlobals.getInstance().plannerShouldRefresh = true;
                                    getMetric();

                                } else if (rightDateButtonClicked) {

                                    // add one day
                                    passDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, 1, true);
                                    passDay = HTGlobals.getInstance().passDay;
                                    passMonth = HTGlobals.getInstance().passMonth;
                                    passYear = HTGlobals.getInstance().passYear;
                                    HTGlobals.getInstance().plannerShouldRefresh = true;
                                    getMetric();

                                }else {
                                    setResult(1);
                                    finish();
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

//                                    toast = HTToast.showToast(HTMetricChartEditActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTMetricChartEditActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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
        leftDateButtonClicked = true;
        updateMetric();

    }

    private void rightDateButtonClicked() {
        rightDateButtonClicked = true;
        updateMetric();

    }

    private void showMetric() {
        scrollView.setVisibility(View.VISIBLE);

        if (exercisePlanDetailsLayout.getChildCount() > 0)
            exercisePlanDetailsLayout.removeAllViews();

        //goal
        if(!"2".equals(chartMetricType)){
            goalLayout.setVisibility(View.VISIBLE);
            double metricGoalDouble = 0;
            try {
                metricGoalDouble = Double.parseDouble(selectedMetricGoal);
            }catch (NumberFormatException e){
            }

            if (metricGoalDouble == 0) {
                goalEdit.setHint("0");
                goalEdit.setText("");
            } else {
                goalEdit.setHint("");
                goalEdit.setText(mNumberFormat.format(metricGoalDouble));
            }

            if(currentYear == passYear && currentMonth == passMonth && currentDayOfMonth == passDay) {
                goalEdit.setEnabled(true);
                goalEdit.setBackgroundResource(R.drawable.ht_text_field);
            } else { //read-only on goals if not current date
                goalEdit.setEnabled(false);
                goalEdit.setBackgroundColor(Color.WHITE);
            }
        }else{
            goalLayout.setVisibility(View.GONE);
        }

        //metric value

        metricLabel.setText(selectedMetric);

        if("2".equals(chartMetricType)) {
            metricCheckBox.setVisibility(View.VISIBLE);
            metricValueEdit.setVisibility(View.GONE);

            if("1".equals(selectedMetricValue)) {
                isMetricChecked = true;
                metricCheckBox.setImageResource(R.drawable.ht_icon_favs_checkmark_selected);
            } else {
                isMetricChecked = false;
                metricCheckBox.setImageResource(R.drawable.ht_icon_favs_checkmark);
            }
        } else {
            metricCheckBox.setVisibility(View.GONE);
            metricValueEdit.setVisibility(View.VISIBLE);

            double metricValueDouble = 0;
            try {
                metricValueDouble = Double.parseDouble(selectedMetricValue);
            }catch (NumberFormatException e){
            }

            if (metricValueDouble == 0) {
                metricValueEdit.setHint("0");
                metricValueEdit.setText("");

            } else {
                metricValueEdit.setHint("");
                metricValueEdit.setText(mNumberFormat.format(metricValueDouble));
            }
        }

        if("Exercise Minutes".equals(selectedMetric)){
            metricValueEdit.setKeyListener(null);
            metricValueEdit.setFocusableInTouchMode(false);
            metricValueEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                    if(addExerciseLayout.getVisibility() == View.VISIBLE)
                        addExerciseLayout.setVisibility(View.GONE);
                    else
                        addExerciseLayout.setVisibility(View.VISIBLE);
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
            for (int i = 0; i < plannerItemID.size(); i++) {
                final int index = i;
                hasExerciseItems = true;

                RelativeLayout plannerItemView = (RelativeLayout) getLayoutInflater().inflate(R.layout.planner_item_view_new, null);

                plannerItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedPlannerItemID = plannerItemID.get(index);
                        Intent intent = new Intent(HTMetricChartEditActivity.this, HTAddActivitySelectItemActivity.class);

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
                notesLabel.setText(plannerItemNotes.get(i));
                if (!"".equals(plannerItemCaloriesBurned.get(i)) && !"0".equals(plannerItemCaloriesBurned.get(i))) {
                    caloriesView.setText(plannerItemCaloriesBurned.get(i));
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
        }

        //text reminder
        if (!"".equals(selectedMetricReminder) && selectedMetricReminder != null) {
            reminderTimeEdit.setText(selectedMetricReminder + selectedMetricReminderFraction + selectedMetricReminderAmPm);
//            activityReminder = sdf.parse(selectedActivityReminder + selectedActivityReminderFraction + selectedActivityReminderAmPm, new ParsePosition(0));
        }else{
            reminderTimeEdit.setText("none");
        }
        reminderTimeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HTMetricChartEditActivity.this, HTTrackerReminderActivity.class);
                final String reminderMetricId;
                if ("Weight".equals(selectedMetric)) {
                    reminderMetricId = "11";
                } else if ("Walking Steps".equals(selectedMetric)) {
                    reminderMetricId = "12";
                } else if ("Exercise Minutes".equals(selectedMetric)) {
                    reminderMetricId = "13";
                } else if ("Sleep Hours".equals(selectedMetric)) {
                    reminderMetricId = "14";
                } else { //custom metrics
                    reminderMetricId = selectedCustomMetricString.replace("metric", "");
                }
                intent.putExtra("metric", reminderMetricId);
                startActivityForResult(intent, 1);
            }
        });
    }

    private void deleteFoodItem() {
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

//        isSavingScrollPosition = true;

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

                                toast = HTToast.showToast(HTMetricChartEditActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                getMetric();
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

//                                    toast = HTToast.showToast(HTMetricChartEditActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTMetricChartEditActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

        getMetric();
    }
}
