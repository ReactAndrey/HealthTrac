package com.sph.healthtrac.tracker.setagoal;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SetAGoalActivity extends Activity {

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

    private static String goal;
    private static String goalLocation;
    private static String goalTime;
    private static String goalPrep;
    private static String goalMotivation;
    private static String goalFeedback;
    private static String goalReminder;

    Toast toast;

    private static RelativeLayout relativeLayoutSetAGoal;

    private static EditText editTextGoal;
    private static EditText editTextGoalLocation;
    private static EditText editTextGoalTime;
    private static EditText editTextGoalPrep;
    private static EditText editTextGoalMotivation;
    private static EditText editTextGoalFeedback;

    ImageView imageViewSetAGoalReminderIcon;

    View datePicker;

    Calendar calendar;

    HttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;

    private Thread myThread = null;

    private boolean leftDateButtonClicked;
    private boolean rightDateButtonClicked;
    private boolean inEditMode;

    ProgressDialog progressDialog;

    private static InputMethodManager imm;

    private View mActionBar;

    DisplayMetrics displayMetrics;

    float displayDensity;
    float screenWidth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_set_a_goal);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        relativeLayoutSetAGoal = (RelativeLayout) findViewById(R.id.setAGoalContainer);
        //editTextMyJournal = (EditText) findViewById(R.id.editTextMyJournal);

        /*
        editTextMyJournal.setTypeface(typeMyJournal);
        editTextMyJournal.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
        editTextMyJournal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        */

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

        displayMetrics = this.getResources().getDisplayMetrics();

        displayDensity = displayMetrics.density;
        screenWidth = displayMetrics.widthPixels;
        //screenHeight = displayMetrics.heightPixels;

        int dpValue = 44;
        int topBarHeight = (int)(dpValue * displayDensity);

        configureActionBar(SetAGoalActivity.this, "Set a Goal", "leftArrow", "Edit");
        //editTextMyJournal.setEnabled(false);
        inEditMode = false;

        // date picker
        datePicker = HTDatePicker.getDatePicker(SetAGoalActivity.this);

        relativeLayoutSetAGoal.addView(datePicker);

        params = (RelativeLayout.LayoutParams) datePicker.getLayoutParams();
        params.height = topBarHeight;
        params.setMargins(0, topBarHeight, 0, 0);  // left, top, right, bottom

        datePicker.setLayoutParams(params);

        if(login.equals("") || pw.equals("")) {

            Intent intent = new Intent(SetAGoalActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            imm.hideSoftInputFromWindow(relativeLayoutSetAGoal.getWindowToken(), 0);
            finish();

        } else {

            getMyGoal();
        }
    }

    private void leftDateButtonClicked() {

        ImageView leftArrow = (ImageView) datePicker.findViewById(R.id.leftArrow);

        imm.hideSoftInputFromWindow(leftArrow.getWindowToken(), 0);

        leftDateButtonClicked = true;

        if (inEditMode) {

            updateMyGoal();

        } else {

            // subtract one day
            passDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, -1, true);
            passDay = HTGlobals.getInstance().passDay;
            passMonth = HTGlobals.getInstance().passMonth;
            passYear = HTGlobals.getInstance().passYear;
            HTGlobals.getInstance().plannerShouldRefresh = true;
            getMyGoal();
        }
    }

    private void rightDateButtonClicked() {

        ImageView rightArrow = (ImageView) datePicker.findViewById(R.id.rightArrow);

        imm.hideSoftInputFromWindow(rightArrow.getWindowToken(), 0);

        rightDateButtonClicked = true;

        if (inEditMode) {

            updateMyGoal();

        } else {

            // add one day
            passDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, 1, true);
            passDay = HTGlobals.getInstance().passDay;
            passMonth = HTGlobals.getInstance().passMonth;
            passYear = HTGlobals.getInstance().passYear;
            HTGlobals.getInstance().plannerShouldRefresh = true;
            getMyGoal();
        }
    }

    private void configureActionBar(Context context, String titleText, String leftButtonText, String rightButtonText) {

        // action bar
        RelativeLayout.LayoutParams params;

        relativeLayoutSetAGoal.removeView(mActionBar);

        mActionBar = HTActionBar.getActionBar(context, titleText, leftButtonText, rightButtonText);

        relativeLayoutSetAGoal.addView(mActionBar);

        int dpValue = 44;
        int topBarHeight = (int)(dpValue * displayDensity);

        params = (RelativeLayout.LayoutParams) mActionBar.getLayoutParams();
        params.height = topBarHeight;

        mActionBar.setLayoutParams(params);

        if (leftButtonText.equals("leftArrow")) { // Cancel arrow

            mActionBar.findViewById(R.id.leftArrowAction).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    imm.hideSoftInputFromWindow(relativeLayoutSetAGoal.getWindowToken(), 0);

                    inEditMode = false;

                    setResult(1);
                    finish();
                }
            });

        } else { // Cancel button

            mActionBar.findViewById(R.id.leftButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    inEditMode = false;

                    getMyGoal();
                }
            });
        }

        if (rightButtonText.equals("Edit")) { // Edit button

            mActionBar.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    editMyGoal();
                }
            });

        } else { // Done button

            mActionBar.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    inEditMode = false;

                    updateMyGoal();
                }
            });
        }
    }

    private void getMyGoal() {

        imm.hideSoftInputFromWindow(relativeLayoutSetAGoal.getWindowToken(), 0);

        progressDialog.show();

        goal = "";
        goalLocation = "";
        goalTime = "";
        goalPrep = "";
        goalMotivation = "";
        goalFeedback = "";
        goalReminder = "";

        leftDateButtonClicked = false;
        rightDateButtonClicked = false;

        final ImageView leftArrow = (ImageView) datePicker.findViewById(R.id.leftArrow);
        final ImageView rightArrow = (ImageView) datePicker.findViewById(R.id.rightArrow);

        configureActionBar(SetAGoalActivity.this, "Set a Goal", "leftArrow", "Edit");

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
                    nameValuePairs.add(new BasicNameValuePair("action", "get_goal"));
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
                    NodeList nodes = doc.getElementsByTagName("goal_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(SetAGoalActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    }
                    else {

                        final Element e2 = (Element)nodes.item(0);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                goal = XMLFunctions.getValue(e2, "goal");
                                goalLocation = XMLFunctions.getValue(e2, "goal_place");
                                goalTime = XMLFunctions.getValue(e2, "goal_time");
                                goalPrep = XMLFunctions.getValue(e2, "goal_support");
                                goalMotivation = XMLFunctions.getValue(e2, "goal_motivation");
                                goalFeedback = XMLFunctions.getValue(e2, "goal_comment");
                                goalReminder = XMLFunctions.getValue(e2, "goal_reminder");

                                goal = HTGlobals.getInstance().cleanStringAfterReceiving(goal);
                                goalLocation = HTGlobals.getInstance().cleanStringAfterReceiving(goalLocation);
                                goalTime = HTGlobals.getInstance().cleanStringAfterReceiving(goalTime);
                                goalPrep = HTGlobals.getInstance().cleanStringAfterReceiving(goalPrep);
                                goalMotivation = HTGlobals.getInstance().cleanStringAfterReceiving(goalMotivation);
                                goalFeedback = HTGlobals.getInstance().cleanStringAfterReceiving(goalFeedback);

                                showMyGoal();

                                if (inEditMode) {

                                    showEditGoal();
                                }
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

//                            toast = HTToast.showToast(SetAGoalActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(SetAGoalActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void editMyGoal() {

        // first, move to today and getMyGoal
        passDate = currentDate;
        passDay = currentDayOfMonth;
        passMonth = currentMonth;
        passYear = currentYear;

        HTGlobals.getInstance().passDate = currentDate;
        HTGlobals.getInstance().passDay = currentDayOfMonth;
        HTGlobals.getInstance().passMonth = currentMonth;
        HTGlobals.getInstance().passYear = currentYear;

        inEditMode = true;

        getMyGoal();
    }

    private void showEditGoal() {

        configureActionBar(SetAGoalActivity.this, "Set a Goal", "Cancel", "Done");

        RelativeLayout.LayoutParams params;

        params = (RelativeLayout.LayoutParams)imageViewSetAGoalReminderIcon.getLayoutParams();
        params.width = 0;
        params.height = 0;
        imageViewSetAGoalReminderIcon.setLayoutParams(params);

        params = (RelativeLayout.LayoutParams)editTextGoal.getLayoutParams();
        params.width = (int)(screenWidth - (32 * displayDensity));
        editTextGoal.setLayoutParams(params);

        editTextGoal.setEnabled(true);
        editTextGoal.setFocusableInTouchMode(true);
        editTextGoal.setBackgroundResource(R.drawable.ht_text_field);
        editTextGoal.requestFocus();

        editTextGoalLocation.setEnabled(true);
        editTextGoalLocation.setFocusableInTouchMode(true);
        editTextGoalLocation.setBackgroundResource(R.drawable.ht_text_field);

        editTextGoalTime.setEnabled(true);
        editTextGoalTime.setFocusableInTouchMode(true);
        editTextGoalTime.setBackgroundResource(R.drawable.ht_text_field);

        editTextGoalPrep.setEnabled(true);
        editTextGoalPrep.setFocusableInTouchMode(true);
        editTextGoalPrep.setBackgroundResource(R.drawable.ht_text_field);

        editTextGoalMotivation.setEnabled(true);
        editTextGoalMotivation.setFocusableInTouchMode(true);
        editTextGoalMotivation.setBackgroundResource(R.drawable.ht_text_field);

        editTextGoalFeedback.setEnabled(true);
        editTextGoalFeedback.setFocusableInTouchMode(true);
        editTextGoalFeedback.setBackgroundResource(R.drawable.ht_text_field);

        imm.showSoftInput(relativeLayoutSetAGoal, 0);
    }

    private void updateMyGoal() {

        imm.hideSoftInputFromWindow(relativeLayoutSetAGoal.getWindowToken(), 0);

        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "update_goal"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));

                    goal = editTextGoal.getText().toString();
                    goal = HTGlobals.getInstance().cleanStringBeforeSending(goal);

                    goalLocation = editTextGoalLocation.getText().toString();
                    goalLocation = HTGlobals.getInstance().cleanStringBeforeSending(goalLocation);

                    goalTime = editTextGoalTime.getText().toString();
                    goalTime = HTGlobals.getInstance().cleanStringBeforeSending(goalTime);

                    goalPrep = editTextGoalPrep.getText().toString();
                    goalPrep = HTGlobals.getInstance().cleanStringBeforeSending(goalPrep);

                    goalMotivation = editTextGoalMotivation.getText().toString();
                    goalMotivation = HTGlobals.getInstance().cleanStringBeforeSending(goalMotivation);

                    goalFeedback = editTextGoalFeedback.getText().toString();
                    goalFeedback = HTGlobals.getInstance().cleanStringBeforeSending(goalFeedback);

                    nameValuePairs.add(new BasicNameValuePair("goal", goal));
                    nameValuePairs.add(new BasicNameValuePair("goal_place", goalLocation));
                    nameValuePairs.add(new BasicNameValuePair("goal_time", goalTime));
                    nameValuePairs.add(new BasicNameValuePair("goal_support", goalPrep));
                    nameValuePairs.add(new BasicNameValuePair("goal_motivation", goalMotivation));
                    nameValuePairs.add(new BasicNameValuePair("goal_comment", goalFeedback));

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

                                toast = HTToast.showToast(SetAGoalActivity.this, errorMessage, Toast.LENGTH_LONG);
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

                                } else if (rightDateButtonClicked) {

                                    // add one day
                                    passDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, 1, true);
                                    passDay = HTGlobals.getInstance().passDay;
                                    passMonth = HTGlobals.getInstance().passMonth;
                                    passYear = HTGlobals.getInstance().passYear;
                                }

                                inEditMode = false;

                                getMyGoal();
                            }
                        });
                    }

                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {

                            progressDialog.dismiss();

//                            toast = HTToast.showToast(SetAGoalActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                            HTConnectErrDialog.showDilaog(SetAGoalActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                        }
                    });

                }
            }
        };

        myThread.start();
    }

    private void showMyGoal() {

        LinearLayout linearLayoutSetAGoal = (LinearLayout) relativeLayoutSetAGoal.findViewById(R.id.linearLayoutSetAGoal);

        LayoutInflater inflater = this.getLayoutInflater();

        RelativeLayout.LayoutParams params;

        View rowView;

        Typeface typeSetAGoalLabels = Typeface.createFromAsset(this.getAssets(), "fonts/AvenirNext-Regular.ttf");
        Typeface typeSetAGoalValues = Typeface.createFromAsset(this.getAssets(), "fonts/AvenirNext-DemiBold.ttf");

        ImageView tempSetAGoalReminderIcon;

        TextView textViewSetAGoalLabel;

        linearLayoutSetAGoal.removeAllViewsInLayout();

        ////////////////////////////////////////////////////////////////
        // GOAL
        ////////////////////////////////////////////////////////////////

        rowView = inflater.inflate(R.layout.set_a_goal_row, null, true);

        imageViewSetAGoalReminderIcon = (ImageView) rowView.findViewById(R.id.imageViewSetAGoalReminderIcon);

        textViewSetAGoalLabel = (TextView) rowView.findViewById(R.id.textViewSetAGoalLabel);

        editTextGoal = (EditText) rowView.findViewById(R.id.editTextSetAGoal);

        // reminder icon?
        if (goalReminder.equals("Y")) {

            imageViewSetAGoalReminderIcon.setImageResource(R.drawable.ht_reminder_on);

        } else {

            imageViewSetAGoalReminderIcon.setImageResource(R.drawable.ht_reminder);
        }

        imageViewSetAGoalReminderIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SetAGoalActivity.this, HTTrackerReminderActivity.class);
                intent.putExtra("metric", "goal");
                SetAGoalActivity.this.startActivityForResult(intent, 1);
            }
        });

        // textViewSetAGoalLabel
        params = (RelativeLayout.LayoutParams)textViewSetAGoalLabel.getLayoutParams();
        params.width = 0;
        params.height = 0;
        textViewSetAGoalLabel.setLayoutParams(params);

        editTextGoal.setTypeface(typeSetAGoalValues);
        editTextGoal.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
        editTextGoal.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        editTextGoal.setEnabled(false);
        editTextGoal.setBackgroundColor(Color.TRANSPARENT);
        editTextGoal.setText(goal);

        params = (RelativeLayout.LayoutParams)editTextGoal.getLayoutParams();
        params.width = (int)(screenWidth - (64 * displayDensity));
        editTextGoal.setLayoutParams(params);

        linearLayoutSetAGoal.addView(rowView);

        ////////////////////////////////////////////////////////////////
        // GOAL LOCATION
        ////////////////////////////////////////////////////////////////

        rowView = inflater.inflate(R.layout.set_a_goal_row, null, true);

        tempSetAGoalReminderIcon = (ImageView) rowView.findViewById(R.id.imageViewSetAGoalReminderIcon);

        textViewSetAGoalLabel = (TextView) rowView.findViewById(R.id.textViewSetAGoalLabel);

        editTextGoalLocation = (EditText) rowView.findViewById(R.id.editTextSetAGoal);

        // reminder icon
        params = (RelativeLayout.LayoutParams)tempSetAGoalReminderIcon.getLayoutParams();
        params.width = 0;
        params.height = 0;
        tempSetAGoalReminderIcon.setLayoutParams(params);

        // textViewSetAGoalLabel
        textViewSetAGoalLabel.setTypeface(typeSetAGoalLabels);
        textViewSetAGoalLabel.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
        textViewSetAGoalLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textViewSetAGoalLabel.setText("Location");

        params = (RelativeLayout.LayoutParams)textViewSetAGoalLabel.getLayoutParams();
        params.width = (int)((screenWidth - (32 * displayDensity)) / 2);
        params.setMargins((int)(16 * displayDensity), 0, 0, 0);
        textViewSetAGoalLabel.setLayoutParams(params);

        params = (RelativeLayout.LayoutParams)textViewSetAGoalLabel.getLayoutParams();
        params.setMargins((int)(16 * displayDensity), 0, 0, 0);
        textViewSetAGoalLabel.setLayoutParams(params);

        editTextGoalLocation.setTypeface(typeSetAGoalValues);
        editTextGoalLocation.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
        editTextGoalLocation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        editTextGoalLocation.setEnabled(false);
        editTextGoalLocation.setBackgroundResource(0);
        editTextGoalLocation.setText(goalLocation);

        params = (RelativeLayout.LayoutParams)editTextGoalLocation.getLayoutParams();
        params.width = (int)((screenWidth - (32 * displayDensity)) / 2);
        editTextGoalLocation.setLayoutParams(params);

        linearLayoutSetAGoal.addView(rowView);

        ////////////////////////////////////////////////////////////////
        // GOAL TIME
        ////////////////////////////////////////////////////////////////

        rowView = inflater.inflate(R.layout.set_a_goal_row, null, true);

        tempSetAGoalReminderIcon = (ImageView) rowView.findViewById(R.id.imageViewSetAGoalReminderIcon);

        textViewSetAGoalLabel = (TextView) rowView.findViewById(R.id.textViewSetAGoalLabel);

        editTextGoalTime = (EditText) rowView.findViewById(R.id.editTextSetAGoal);

        // reminder icon
        params = (RelativeLayout.LayoutParams)tempSetAGoalReminderIcon.getLayoutParams();
        params.width = 0;
        params.height = 0;
        tempSetAGoalReminderIcon.setLayoutParams(params);

        // textViewSetAGoalLabel
        textViewSetAGoalLabel.setTypeface(typeSetAGoalLabels);
        textViewSetAGoalLabel.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
        textViewSetAGoalLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textViewSetAGoalLabel.setText("Time / Frequency");

        params = (RelativeLayout.LayoutParams)textViewSetAGoalLabel.getLayoutParams();
        params.width = (int)((screenWidth - (32 * displayDensity)) / 2);
        params.setMargins((int)(16 * displayDensity), 0, 0, 0);
        textViewSetAGoalLabel.setLayoutParams(params);

        editTextGoalTime.setTypeface(typeSetAGoalValues);
        editTextGoalTime.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
        editTextGoalTime.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        editTextGoalTime.setEnabled(false);
        editTextGoalTime.setBackgroundColor(Color.TRANSPARENT);
        editTextGoalTime.setText(goalTime);

        params = (RelativeLayout.LayoutParams)editTextGoalTime.getLayoutParams();
        params.width = (int)((screenWidth - (32 * displayDensity)) / 2);
        editTextGoalTime.setLayoutParams(params);

        linearLayoutSetAGoal.addView(rowView);

        ////////////////////////////////////////////////////////////////
        // GOAL PREP
        ////////////////////////////////////////////////////////////////

        rowView = inflater.inflate(R.layout.set_a_goal_row, null, true);

        tempSetAGoalReminderIcon = (ImageView) rowView.findViewById(R.id.imageViewSetAGoalReminderIcon);

        textViewSetAGoalLabel = (TextView) rowView.findViewById(R.id.textViewSetAGoalLabel);

        editTextGoalPrep = (EditText) rowView.findViewById(R.id.editTextSetAGoal);

        // reminder icon
        params = (RelativeLayout.LayoutParams)tempSetAGoalReminderIcon.getLayoutParams();
        params.width = 0;
        params.height = 0;
        tempSetAGoalReminderIcon.setLayoutParams(params);

        // textViewSetAGoalLabel
        textViewSetAGoalLabel.setTypeface(typeSetAGoalLabels);
        textViewSetAGoalLabel.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
        textViewSetAGoalLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textViewSetAGoalLabel.setText("Preparation");

        params = (RelativeLayout.LayoutParams)textViewSetAGoalLabel.getLayoutParams();
        params.width = (int)((screenWidth - (32 * displayDensity)) / 2);
        params.setMargins((int)(16 * displayDensity), 0, 0, 0);
        textViewSetAGoalLabel.setLayoutParams(params);

        params = (RelativeLayout.LayoutParams)textViewSetAGoalLabel.getLayoutParams();
        params.setMargins((int)(16 * displayDensity), 0, 0, 0);
        textViewSetAGoalLabel.setLayoutParams(params);

        editTextGoalPrep.setTypeface(typeSetAGoalValues);
        editTextGoalPrep.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
        editTextGoalPrep.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        editTextGoalPrep.setEnabled(false);
        editTextGoalPrep.setBackgroundColor(Color.TRANSPARENT);
        editTextGoalPrep.setText(goalPrep);

        params = (RelativeLayout.LayoutParams)editTextGoalPrep.getLayoutParams();
        params.width = (int)((screenWidth - (32 * displayDensity)) / 2);
        editTextGoalPrep.setLayoutParams(params);

        linearLayoutSetAGoal.addView(rowView);

        ////////////////////////////////////////////////////////////////
        // GOAL MOTIVATION
        ////////////////////////////////////////////////////////////////

        rowView = inflater.inflate(R.layout.set_a_goal_row, null, true);

        tempSetAGoalReminderIcon = (ImageView) rowView.findViewById(R.id.imageViewSetAGoalReminderIcon);

        textViewSetAGoalLabel = (TextView) rowView.findViewById(R.id.textViewSetAGoalLabel);

        editTextGoalMotivation = (EditText) rowView.findViewById(R.id.editTextSetAGoal);

        // reminder icon
        params = (RelativeLayout.LayoutParams)tempSetAGoalReminderIcon.getLayoutParams();
        params.width = 0;
        params.height = 0;
        tempSetAGoalReminderIcon.setLayoutParams(params);

        // textViewSetAGoalLabel
        textViewSetAGoalLabel.setTypeface(typeSetAGoalLabels);
        textViewSetAGoalLabel.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
        textViewSetAGoalLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textViewSetAGoalLabel.setText("My Motivation");

        params = (RelativeLayout.LayoutParams)textViewSetAGoalLabel.getLayoutParams();
        params.width = (int)((screenWidth - (32 * displayDensity)) / 2);
        params.setMargins((int)(16 * displayDensity), 0, 0, 0);
        textViewSetAGoalLabel.setLayoutParams(params);

        params = (RelativeLayout.LayoutParams)textViewSetAGoalLabel.getLayoutParams();
        params.setMargins((int)(16 * displayDensity), 0, 0, 0);
        textViewSetAGoalLabel.setLayoutParams(params);

        editTextGoalMotivation.setTypeface(typeSetAGoalValues);
        editTextGoalMotivation.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
        editTextGoalMotivation.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        editTextGoalMotivation.setEnabled(false);
        editTextGoalMotivation.setBackgroundColor(Color.TRANSPARENT);
        editTextGoalMotivation.setText(goalMotivation);

        params = (RelativeLayout.LayoutParams)editTextGoalMotivation.getLayoutParams();
        params.width = (int)((screenWidth - (32 * displayDensity)) / 2);
        editTextGoalMotivation.setLayoutParams(params);

        linearLayoutSetAGoal.addView(rowView);

        ////////////////////////////////////////////////////////////////
        // GOAL FEEDBACK
        ////////////////////////////////////////////////////////////////

        rowView = inflater.inflate(R.layout.set_a_goal_row, null, true);

        tempSetAGoalReminderIcon = (ImageView) rowView.findViewById(R.id.imageViewSetAGoalReminderIcon);

        textViewSetAGoalLabel = (TextView) rowView.findViewById(R.id.textViewSetAGoalLabel);

        editTextGoalFeedback = (EditText) rowView.findViewById(R.id.editTextSetAGoal);

        // reminder icon
        params = (RelativeLayout.LayoutParams)tempSetAGoalReminderIcon.getLayoutParams();
        params.width = 0;
        params.height = 0;
        tempSetAGoalReminderIcon.setLayoutParams(params);

        // textViewSetAGoalLabel
        textViewSetAGoalLabel.setTypeface(typeSetAGoalLabels);
        textViewSetAGoalLabel.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
        textViewSetAGoalLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textViewSetAGoalLabel.setText("Goal Feedback");

        params = (RelativeLayout.LayoutParams)textViewSetAGoalLabel.getLayoutParams();
        params.width = (int)((screenWidth - (32 * displayDensity)) / 2);
        params.setMargins((int)(16 * displayDensity), 0, 0, 0);
        textViewSetAGoalLabel.setLayoutParams(params);

        params = (RelativeLayout.LayoutParams)textViewSetAGoalLabel.getLayoutParams();
        params.setMargins((int)(16 * displayDensity), 0, 0, 0);
        textViewSetAGoalLabel.setLayoutParams(params);

        editTextGoalFeedback.setTypeface(typeSetAGoalValues);
        editTextGoalFeedback.setTextColor(this.getResources().getColor(R.color.ht_gray_title_text));
        editTextGoalFeedback.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        editTextGoalFeedback.setEnabled(false);
        editTextGoalFeedback.setBackgroundColor(Color.TRANSPARENT);
        editTextGoalFeedback.setText(goalFeedback);

        params = (RelativeLayout.LayoutParams)editTextGoalFeedback.getLayoutParams();
        params.width = (int)((screenWidth - (32 * displayDensity)) / 2);
        editTextGoalFeedback.setLayoutParams(params);

        linearLayoutSetAGoal.addView(rowView);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // coming back from a child activity, refresh

        passDate = HTGlobals.getInstance().passDate;
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;

        getMyGoal();
    }
}

