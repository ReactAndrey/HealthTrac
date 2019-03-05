package com.sph.healthtrac.planner.addactivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTTimePickerDialog;
import com.sph.healthtrac.common.HTToast;
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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HTAddActivitySelectItemActivity extends Activity {
    private static RelativeLayout mainContentLayout;
    private ScrollView scrollView;
    private LinearLayout scrollViewContentLayout;

    private RelativeLayout descriptionLayout;
    private EditText descriptionEdit;
//    private RelativeLayout addPlannerLayout;
//    private TextView addPlannerLabel;
//    private TextView plannerTimeEdit;
    private LinearLayout textReminderLayout;
    private TextView textReminderLabel;
    private TextView reminderTimeEdit;
    private LinearLayout durationLayout;
    private TextView durationLabel;
    private EditText durationEdit;
    private LinearLayout caloriesBurnedLayout;
    private TextView caloriesBurnedLabel;
    private EditText caloriesBurnedEdit;
    private LinearLayout addFavoritesLayout;
    private TextView addFavoritesLabel;
    private ImageView favoriteCheck;

    String addActivityCategory;

    int selectedActivityID;
    int relaunchItemID;

    boolean relaunchPlannerItem;
    boolean caloriesBurnedRecalc = true;

    String selectedActivityType;
    String selectedActivityName;
    String selectedActivityCaloriesBurned;
    String selectedActivityDuration;

    float globalCaloriesBurned;

    String selectedActivityTime;
    String selectedActivityTimeFraction;
    String selectedActivityTimeAmPm;
    String selectedActivityReminder;
    String selectedActivityReminderFraction;
    String selectedActivityReminderAmPm;
    String selectedActivityReminderYN;
    String selectedActivityAddToFavorites;
    String selectedActivityRelaunchItem;
    String selectedActivityRelaunchItemID;

    boolean addActivityToFavorites;
    boolean doneAddingActivity;

    private Date activityTime;
    private Date activityReminder;

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

    private Typeface avenirNextRegularFont;
    private Typeface avenirNextMediumFont;
    private Typeface openSansLightFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addactivity_selectitem);

        addActivityCategory = getIntent().getStringExtra("addActivityCategory");
        selectedActivityType = getIntent().getStringExtra("selectedActivityType");
        selectedActivityID = getIntent().getIntExtra("selectedActivityID", 0);
        relaunchPlannerItem = getIntent().getBooleanExtra("relaunchPlannerItem", false);
        relaunchItemID = getIntent().getIntExtra("relaunchItemID", 0);

        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollViewContentLayout = (LinearLayout) findViewById(R.id.scrollViewContentLayout);

        descriptionLayout = (RelativeLayout) findViewById(R.id.descriptionLayout);
        descriptionEdit = (EditText) findViewById(R.id.descriptionEdit);
//        addPlannerLayout = (RelativeLayout) findViewById(R.id.addPlannerLayout);
//        addPlannerLabel = (TextView) findViewById(R.id.addPlannerLabel);
//        plannerTimeEdit = (TextView) findViewById(R.id.plannerTimeEdit);
        textReminderLayout = (LinearLayout) findViewById(R.id.textReminderLayout);
        textReminderLabel = (TextView) findViewById(R.id.textReminderLabel);
        reminderTimeEdit = (TextView) findViewById(R.id.reminderTimeEdit);
        durationLayout = (LinearLayout) findViewById(R.id.durationLayout);
        durationLabel = (TextView) findViewById(R.id.durationLabel);
        durationEdit = (EditText) findViewById(R.id.durationEdit);
        durationEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (caloriesBurnedRecalc) {
                    String value = s.toString();
                    float calcValue;
                    try {
                        calcValue = Float.parseFloat(value) * globalCaloriesBurned;
                    } catch (NumberFormatException nfe) {
                        calcValue = 0;
                    }
                    caloriesBurnedEdit.setText(String.format("%.0f", calcValue));
                }
            }
        });
        caloriesBurnedLayout = (LinearLayout) findViewById(R.id.caloriesBurnedLayout);
        caloriesBurnedLabel = (TextView) findViewById(R.id.caloriesBurnedLabel);
        caloriesBurnedEdit = (EditText) findViewById(R.id.caloriesBurnedEdit);
        caloriesBurnedEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                caloriesBurnedRecalc = false;
                return false;
            }
        });
        addFavoritesLayout = (LinearLayout) findViewById(R.id.addFavoritesLayout);
        addFavoritesLabel = (TextView) findViewById(R.id.addFavoritesLabel);
        favoriteCheck = (ImageView) findViewById(R.id.favoriteCheck);

        avenirNextRegularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");
        openSansLightFont = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");

        descriptionEdit.setTypeface(avenirNextMediumFont);
//        addPlannerLabel.setTypeface(avenirNextRegularFont);
        textReminderLabel.setTypeface(avenirNextRegularFont);
        durationLabel.setTypeface(avenirNextRegularFont);
        caloriesBurnedLabel.setTypeface(avenirNextRegularFont);
        addFavoritesLabel.setTypeface(avenirNextRegularFont);
//        plannerTimeEdit.setTypeface(openSansLightFont);
        reminderTimeEdit.setTypeface(openSansLightFont);
        durationEdit.setTypeface(openSansLightFont);
        caloriesBurnedEdit.setTypeface(openSansLightFont);

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

        String title = "";
        if("exercise".equals(addActivityCategory) || "exercise".equals(selectedActivityType))
            title = "Add Exercise";
        else
            title = "Add Note";
        RelativeLayout.LayoutParams params;
        // action bar
        View mActionBar = HTActionBar.getActionBar(this, title, "leftArrow", "checkMark");

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

        // right check button
        mActionBar.findViewById(R.id.rightCheckAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkButtonPressed();
            }
        });

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            getActivityItem();
        }
    }

    private void getActivityItem() {
        selectedActivityName = "";
        if (relaunchPlannerItem) {

            if (relaunchItemID == 0) {
                relaunchItemID = selectedActivityID;
            }
        }

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_activity_select_item"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("WhichCategory", addActivityCategory));
                    nameValuePairs.add(new BasicNameValuePair("WhichID", selectedActivityID + ""));
                    nameValuePairs.add(new BasicNameValuePair("relaunch_id", relaunchItemID + ""));

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();
                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);

                    NodeList error = doc.getElementsByTagName("error");
                    NodeList nodes = doc.getElementsByTagName("add_activity_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTAddActivitySelectItemActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();
                                String tempString = "";

                                if (XMLFunctions.tagExists(e2, "activity_id")) {
                                    tempString = XMLFunctions.getValue(e2, "activity_id");
                                    tempString = htGlobals.cleanStringAfterReceiving(tempString);
                                    selectedActivityID = Integer.parseInt(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "activity_type")) {
                                    tempString = XMLFunctions.getValue(e2, "activity_type");
                                    selectedActivityType = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "activity_notes")) {
                                    tempString = XMLFunctions.getValue(e2, "activity_notes");
                                    selectedActivityName = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "activity_calories_burned")) {
                                    tempString = XMLFunctions.getValue(e2, "activity_calories_burned");
                                    selectedActivityCaloriesBurned = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "activity_duration_minutes")) {
                                    tempString = XMLFunctions.getValue(e2, "activity_duration_minutes");
                                    selectedActivityDuration = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "activity_item_time")) {
                                    tempString = XMLFunctions.getValue(e2, "activity_item_time");
                                    selectedActivityTime = htGlobals.cleanStringAfterReceiving(tempString);
                                    if (!"".equals(selectedActivityTime)) {
                                        try {
                                            int timeValue = Integer.parseInt(selectedActivityTime);

                                            if (timeValue > 12
                                                    && timeValue != 24) {

                                                selectedActivityTime = "" + (timeValue - 12);

                                                selectedActivityTimeAmPm = "pm";

                                            } else if (timeValue == 24 ||
                                                    timeValue == 0) {

                                                selectedActivityTime = "12";
                                                selectedActivityTimeAmPm = "am";

                                            } else if (timeValue == 12) {

                                                selectedActivityTime = "12";
                                                selectedActivityTimeAmPm = "pm";

                                            } else {

                                                selectedActivityTimeAmPm = "am";
                                            }
                                        } catch (NumberFormatException nfe) {
                                            selectedActivityTime = "";
                                        }
                                    }
                                }

                                if (XMLFunctions.tagExists(e2, "activity_item_time_fraction")) {
                                    tempString = XMLFunctions.getValue(e2, "activity_item_time_fraction");
                                    tempString = htGlobals.cleanStringAfterReceiving(tempString);
                                    if ("".equals(tempString))
                                        selectedActivityTimeFraction = ":00";
                                    else
                                        selectedActivityTimeFraction = tempString;

                                }

                                if (XMLFunctions.tagExists(e2, "activity_item_reminder_time")) {
                                    tempString = XMLFunctions.getValue(e2, "activity_item_reminder_time");
                                    selectedActivityReminder = htGlobals.cleanStringAfterReceiving(tempString);
                                    if (!"".equals(selectedActivityReminder)) {
                                        try {
                                            int timeValue = Integer.parseInt(selectedActivityReminder);
                                            if (timeValue > 12
                                                    && timeValue != 24) {

                                                selectedActivityReminder = "" + (timeValue - 12);

                                                selectedActivityReminderAmPm = "pm";

                                            } else if (timeValue == 24 ||
                                                    timeValue == 0) {

                                                selectedActivityReminder = "12";
                                                selectedActivityReminderAmPm = "am";

                                            } else if (timeValue == 12) {

                                                selectedActivityReminder = "12";
                                                selectedActivityReminderAmPm = "pm";

                                            } else {

                                                selectedActivityReminderAmPm = "am";
                                            }
                                        } catch (NumberFormatException nfe) {
                                            selectedActivityReminder = "";
                                        }
                                    }
                                }

                                if (XMLFunctions.tagExists(e2, "activity_item_reminder_time_fraction")) {
                                    tempString = XMLFunctions.getValue(e2, "activity_item_reminder_time_fraction");
                                    tempString = htGlobals.cleanStringAfterReceiving(tempString);
                                    if ("".equals(tempString))
                                        selectedActivityReminderFraction = ":00";
                                    else
                                        selectedActivityReminderFraction = tempString;

                                }

                                if ("".equals(selectedActivityDuration)
                                        || "0".equals(selectedActivityDuration)
                                        || selectedActivityDuration == null) {

                                    selectedActivityDuration = "1";

                                }

                                if ("".equals(selectedActivityCaloriesBurned)
                                        || selectedActivityCaloriesBurned == null) {

                                    selectedActivityCaloriesBurned = "0";
                                }

                                try {
                                    globalCaloriesBurned = Float.parseFloat(selectedActivityCaloriesBurned) /
                                            Float.parseFloat(selectedActivityDuration);
                                } catch (NumberFormatException nfe) {
                                    globalCaloriesBurned = 0;
                                }

                                showActivityItem();
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

//                                    toast = HTToast.showToast(HTAddActivitySelectItemActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTAddActivitySelectItemActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showActivityItem() {
        scrollView.setVisibility(View.VISIBLE);

        if ("exercise".equals(addActivityCategory)
                || "exercise".equals(selectedActivityType)) {
            descriptionEdit.setHint("Exercise description");
        }else {
            descriptionEdit.setHint("Enter note");
        }
        descriptionEdit.setText(selectedActivityName);

        final SimpleDateFormat sdf = new SimpleDateFormat("h:mma", Locale.US);

        //Add to Planner
//        addPlannerLayout.setVisibility(View.GONE);

        // THIS IS DISABLED FOR NOW ( && 1 == 2 )

        /*if ("exercise".equals(addActivityCategory) && 1 == 2){
            if ("".equals(selectedActivityTime) || selectedActivityTime == null) {

                selectedActivityTime = "12";
                selectedActivityTimeFraction = ":00";
                selectedActivityTimeAmPm = "pm";
            }

            if ("0".equals(selectedActivityTime)) {

                selectedActivityTime = "12";
                selectedActivityTimeFraction = ":00";
                selectedActivityTimeAmPm = "am";
            }

            activityTime = sdf.parse(selectedActivityTime + selectedActivityTimeFraction + selectedActivityTimeAmPm, new ParsePosition(0));
            final HTTimePickerDialog foodTimePickerDialog = new HTTimePickerDialog(this, TimePickerDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
                @Override
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                    view.setCurrentMinute(minute);
                    activityTime.setHours(hourOfDay);
                    activityTime.setMinutes(minute * 30);
                    plannerTimeEdit.setText(sdf.format(activityTime));
                }
            }, activityTime.getHours(), activityTime.getMinutes(), false, 30);
            plannerTimeEdit.setText(selectedActivityTime + selectedActivityTimeFraction + selectedActivityTimeAmPm);
            plannerTimeEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    foodTimePickerDialog.show();
                }
            });
        }*/

        //Text Reminder
        if (!"".equals(selectedActivityReminder) && selectedActivityReminder != null) {
            reminderTimeEdit.setText(selectedActivityReminder + selectedActivityReminderFraction + selectedActivityReminderAmPm);
            activityReminder = sdf.parse(selectedActivityReminder + selectedActivityReminderFraction + selectedActivityReminderAmPm, new ParsePosition(0));
        }else{
            reminderTimeEdit.setText("none");
        }
        activityReminder = sdf.parse("12:00pm", new ParsePosition(0));
        final HTTimePickerDialog foodReminderPickerDialog = new HTTimePickerDialog(this, TimePickerDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                view.setCurrentMinute(minute);
                activityReminder.setHours(hourOfDay);
                activityReminder.setMinutes(minute * 15);
                reminderTimeEdit.setText(sdf.format(activityReminder));
            }
        }, activityReminder.getHours(), activityReminder.getMinutes(), false, 15);
        foodReminderPickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "None", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reminderTimeEdit.setText("none");
                activityReminder = sdf.parse("12:00pm", new ParsePosition(0));
                foodReminderPickerDialog.updateTime(activityReminder.getHours(), activityReminder.getMinutes());
            }
        });

        reminderTimeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodReminderPickerDialog.show();
            }
        });

        // duration
        if ("exercise".equals(addActivityCategory)
                || "exercise".equals(selectedActivityType)) {
            durationLayout.setVisibility(View.VISIBLE);
            durationEdit.setText(selectedActivityDuration);
        } else {
            durationLayout.setVisibility(View.GONE);
        }

        // calories burned
        if ("exercise".equals(addActivityCategory)
                || "exercise".equals(selectedActivityType)) {
            caloriesBurnedLayout.setVisibility(View.VISIBLE);
            caloriesBurnedEdit.setText(selectedActivityCaloriesBurned);
        } else {
            caloriesBurnedLayout.setVisibility(View.GONE);
        }

        //add to favorites
//        if ("exercise".equals(addActivityCategory)) {
        if (!"favorites".equals(addActivityCategory) && !"note".equals(addActivityCategory)) {
            if (addActivityToFavorites) {
                favoriteCheck.setImageResource(R.drawable.ht_check_on_green);
            } else {
                favoriteCheck.setImageResource(R.drawable.ht_check_off_green);
            }

            favoriteCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addActivityToFavorites = !addActivityToFavorites;
                    if (addActivityToFavorites) {
                        favoriteCheck.setImageResource(R.drawable.ht_check_on_green);
                    } else {
                        favoriteCheck.setImageResource(R.drawable.ht_check_off_green);
                    }
                }
            });
        }else{
            addFavoritesLayout.setVisibility(View.GONE);
        }

        if ("".equals(descriptionEdit.getText().toString())) {
            imm.showSoftInputFromInputMethod(mainContentLayout.getWindowToken(), 0);
        }
    }

    private void checkButtonPressed() {
        String checkString = descriptionEdit.getText().toString().trim();
        if ("".equals(checkString)) {
            HTToast.showToast(HTAddActivitySelectItemActivity.this, "Please enter your\nActivity Description", Toast.LENGTH_LONG);
            descriptionEdit.requestFocus();
        } else {
            addActivityItem();
        }
    }

    private void addActivityItem() {

        HTGlobals.getInstance().plannerShouldRefresh = true;
          /*
    if ([self.selectedActivityTimeAmPm isEqualToString:@"pm"]lue] + 12];

        && [self.selectedActivityTime integerValue] < 12) {

        self.selectedActivityTime = [NSString stringWithFormat:@"%ld",
                                 (long)[self.selectedActivityTime integerVa
    } else if ([self.selectedActivityTimeAmPm isEqualToString:@"am"]
               && [self.selectedActivityTime integerValue] == 12) {

        self.selectedActivityTime = @"0";
    }

    self.selectedActivityTimeFraction = [self.selectedActivityTimeFraction
                                     stringByReplacingOccurrencesOfString:@":" withString:@""];
    */

//        selectedActivityTime = activityTime.getHours() + "";
//        selectedActivityTimeFraction = activityTime.getMinutes() + "";
        selectedActivityTime = "23";
        selectedActivityTimeFraction = "00";
        if ("none".equals(reminderTimeEdit.getText())) {
            selectedActivityReminder = "";
            selectedActivityReminderFraction = "00";
        } else {
            selectedActivityReminder = activityReminder.getHours() + "";
            selectedActivityReminderFraction = activityReminder.getMinutes() + "";
        }

        selectedActivityReminderYN = "";
        if (!"".equals(selectedActivityReminder)) {

            selectedActivityReminderYN = "Y";
        }

        selectedActivityAddToFavorites = "";
        if (addActivityToFavorites) {
            selectedActivityAddToFavorites = "Y";
        }

        selectedActivityName = descriptionEdit.getText().toString();
        try {
            selectedActivityName = URLEncoder.encode(selectedActivityName, "utf-8");
        } catch (UnsupportedEncodingException e) {
        }

        if (relaunchPlannerItem) {

            selectedActivityRelaunchItem = "true";
            selectedActivityRelaunchItemID = relaunchItemID + "";

        } else {
            selectedActivityRelaunchItem = "false";
            selectedActivityRelaunchItemID = "";
        }

        selectedActivityDuration = durationEdit.getText().toString();
        if ("".equals(selectedActivityDuration)) {
            selectedActivityDuration = "0";
        }

        selectedActivityCaloriesBurned = caloriesBurnedEdit.getText().toString();
        if ("".equals(selectedActivityCaloriesBurned)) {
            selectedActivityCaloriesBurned = "0";
        }

        if ("favorites".equals(addActivityCategory)){
//                && !"".equals(selectedActivityType) && selectedActivityType != null) {

            addActivityCategory = "exercise";
        }


        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_activity_add_item"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("WhichCategory", addActivityCategory));
                    nameValuePairs.add(new BasicNameValuePair("WhichID", selectedActivityID + ""));
                    nameValuePairs.add(new BasicNameValuePair("hour", selectedActivityTime));
                    nameValuePairs.add(new BasicNameValuePair("hour_half", selectedActivityTimeFraction));
                    nameValuePairs.add(new BasicNameValuePair("name", selectedActivityName));
                    nameValuePairs.add(new BasicNameValuePair("reminder", selectedActivityReminder));
                    nameValuePairs.add(new BasicNameValuePair("reminder_half", selectedActivityReminderFraction));
                    nameValuePairs.add(new BasicNameValuePair("reminder_yn", selectedActivityReminderYN));
                    nameValuePairs.add(new BasicNameValuePair("add_to_favs", selectedActivityAddToFavorites));
                    nameValuePairs.add(new BasicNameValuePair("relaunch", selectedActivityRelaunchItem));
                    nameValuePairs.add(new BasicNameValuePair("relaunch_id", selectedActivityRelaunchItemID));
                    nameValuePairs.add(new BasicNameValuePair("plan_calories", ""));
                    nameValuePairs.add(new BasicNameValuePair("duration", selectedActivityDuration));
                    nameValuePairs.add(new BasicNameValuePair("calories_burned", selectedActivityCaloriesBurned));

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

                                toast = HTToast.showToast(HTAddActivitySelectItemActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                                setResult(2);
                                finish();
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

//                                    toast = HTToast.showToast(HTAddActivitySelectItemActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTAddActivitySelectItemActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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
}
