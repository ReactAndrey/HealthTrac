package com.sph.healthtrac.more.myaccount;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.common.XMLFunctions;
import com.sph.healthtrac.planner.createplan.HTCreateEatingPlanActivity;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HTMyAccountPersonalInfoActivity extends Activity {

    private static RelativeLayout mainContentLayout;
    private static InputMethodManager imm;
    private LinearLayout linearLayoutPersonalInfo;

    HttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;
    Toast toast;
    ProgressDialog progressDialog;
    private Thread myThread = null;

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

    private TextView birthdayLabel;
    private TextView sexLabel;
    private TextView heightLabel;
    private TextView weightGoalLabel;
    private TextView currentWeightLabel;
    private TextView activityLevelLabel;

    private TextView birthdayEdit;
    private TextView sexEdit;
    private TextView heightEdit;
    private EditText weightGoalEdit;
    private EditText currentWeightEdit;
    private TextView activityLevelEdit;

    private String[] activityLevels;
    private String[] activityLevelLabels;
    private String[] activityLevelValues;
    private String[] sexTypes = {"Male", "Female"};
    private int selectedSexIndex;
    private int selectedActivityLevelIndex;

    private Dialog chooseHeightDlg;
    private Dialog chooseSexDlg;
    private Dialog chooseActivityLevelDlg;

    private Button updateBtn;

    private NumberPicker ftPicker;
    private NumberPicker inchPicker;
    private String[] ftArray;
    private String[] inchArray;

    private Typeface avenirNextReqularFont;
    private Typeface avenirNextMediumFont;

    private String selectedDOB;
    private String selectedSex;
    private String selectedHeight;
    private String selectedHeightFeet;
    private String selectedHeightInches;
    private String selectedWeightGoal;
    private String selectedWeight;
    private String selectedActivityLevel;

    private Date birthDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myaccount_personalinfo);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        linearLayoutPersonalInfo = (LinearLayout) findViewById(R.id.linearLayoutPersonalInfo);

        ftArray = getResources().getStringArray(R.array.ft_array);
        inchArray = getResources().getStringArray(R.array.inch_array);

        activityLevels = getResources().getStringArray(R.array.activity_levels);
        activityLevelLabels = getResources().getStringArray(R.array.activity_level_labels);
        activityLevelValues = getResources().getStringArray(R.array.activity_level_values);

        avenirNextReqularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");

        progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);

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
        View mActionBar = HTActionBar.getActionBar(this, "Personal Information", "leftArrow", ""); // actually, message compose button

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

            Intent intent = new Intent(HTMyAccountPersonalInfoActivity.this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();

        } else {
            birthdayLabel = (TextView) findViewById(R.id.birthdayLabel);
            sexLabel = (TextView) findViewById(R.id.sexLabel);
            heightLabel = (TextView) findViewById(R.id.heightLabel);
            weightGoalLabel = (TextView) findViewById(R.id.weightGoalLabel);
            currentWeightLabel = (TextView) findViewById(R.id.currentWeightLabel);
            activityLevelLabel = (TextView) findViewById(R.id.activityLevelLabel);

            birthdayLabel.setTypeface(avenirNextReqularFont);
            sexLabel.setTypeface(avenirNextReqularFont);
            heightLabel.setTypeface(avenirNextReqularFont);
            weightGoalLabel.setTypeface(avenirNextReqularFont);
            currentWeightLabel.setTypeface(avenirNextReqularFont);
            activityLevelLabel.setTypeface(avenirNextReqularFont);

            birthdayEdit = (TextView) findViewById(R.id.birthdayEdit);
            sexEdit = (TextView) findViewById(R.id.sexEdit);
            heightEdit = (TextView) findViewById(R.id.heightEdit);
            weightGoalEdit = (EditText) findViewById(R.id.weightGoalEdit);
            currentWeightEdit = (EditText) findViewById(R.id.currentWeightEdit);
            activityLevelEdit = (TextView) findViewById(R.id.activityLevelEdit);

            birthdayEdit.setTypeface(avenirNextMediumFont);
            sexEdit.setTypeface(avenirNextMediumFont);
            heightEdit.setTypeface(avenirNextMediumFont);
            weightGoalEdit.setTypeface(avenirNextMediumFont);
            currentWeightEdit.setTypeface(avenirNextMediumFont);
            activityLevelEdit.setTypeface(avenirNextMediumFont);

            LayoutInflater mInflater = LayoutInflater.from(this);
            View dlgContentView = mInflater.inflate(R.layout.custom_picker, null);
            ftPicker = (NumberPicker) dlgContentView.findViewById(R.id.picker1);
            inchPicker = (NumberPicker) dlgContentView.findViewById(R.id.picker2);
            ftPicker.setDisplayedValues(ftArray);
            ftPicker.setMaxValue(2);
            ftPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            ftPicker.setWrapSelectorWheel(false);
            inchPicker.setDisplayedValues(inchArray);
            inchPicker.setMaxValue(11);
            inchPicker.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
            inchPicker.setWrapSelectorWheel(false);
            chooseHeightDlg = new AlertDialog.Builder(HTMyAccountPersonalInfoActivity.this)
                    .setTitle("Height")
                    .setView(dlgContentView)
                    .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            selectedHeightFeet = ftArray[ftPicker.getValue()];
                            selectedHeightInches = inchArray[inchPicker.getValue()];
                            heightEdit.setText(selectedHeightFeet + " " + selectedHeightInches);
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    })
                    .create();

            updateBtn =  (Button) findViewById(R.id.updateBtn);
            getPersonalInfoValues();
        }
    }

    private void getPersonalInfoValues() {
        selectedDOB = "";
        selectedSex = "";
        selectedHeight = "";
        selectedHeightFeet = "";
        selectedHeightInches = "";
        selectedWeightGoal = "";
        selectedWeight = "";
        selectedActivityLevel = "";

        progressDialog.show();
        myThread = new Thread() {

            @Override
            public void run() {

                try {
                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_personal_info_values"));
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
                    NodeList nodes = doc.getElementsByTagName("personal_info");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTMyAccountPersonalInfoActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();
                                String tempString;

                                if (XMLFunctions.tagExists(e2, "client_height")) {
                                    tempString = XMLFunctions.getValue(e2, "client_height");
                                    selectedHeight = htGlobals.cleanStringAfterReceiving(tempString);
                                    if (!"".equals(selectedHeight)) {
                                        selectedHeightFeet = selectedHeight.substring(0, selectedHeight.indexOf("ft") + 2);
                                        selectedHeightInches = selectedHeight.substring(selectedHeight.indexOf("ft") + 3, selectedHeight.length()).replace("ches", "");
                                    }
                                }

                                if (XMLFunctions.tagExists(e2, "client_weight")) {
                                    tempString = XMLFunctions.getValue(e2, "client_weight");
                                    selectedWeight = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "client_weight_goal")) {
                                    tempString = XMLFunctions.getValue(e2, "client_weight_goal");
                                    selectedWeightGoal = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "client_dob")) {
                                    tempString = XMLFunctions.getValue(e2, "client_dob");
                                    selectedDOB = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "client_sex")) {
                                    tempString = XMLFunctions.getValue(e2, "client_sex");
                                    selectedSex = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "client_activity_multiplier")) {
                                    tempString = XMLFunctions.getValue(e2, "client_activity_multiplier");
                                    selectedActivityLevel = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                showPersonalInfo();
                            }
                        });
                    }

                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            HTConnectErrDialog.showDilaog(HTMyAccountPersonalInfoActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showPersonalInfo() {

        birthdayEdit.setText(selectedDOB);
        if (!"".equals(selectedDOB)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
            calendar = Calendar.getInstance();

            try {
                Date tempDate = dateFormat.parse(selectedDOB);
                calendar.setTime(tempDate);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                birthDate = calendar.getTime();
            } catch (ParseException e) {
            }
        }

        birthdayEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDlg(birthdayEdit);
            }
        });

        if (!"".equals(selectedSex)) {

            if ("MALE".equals(selectedSex)) {
                selectedSexIndex = 0;
                sexEdit.setText("Male");

            } else if ("FEMALE".equals(selectedSex)) {
                selectedSexIndex = 1;
                sexEdit.setText("Female");
            }
        }

        chooseSexDlg = new AlertDialog.Builder(HTMyAccountPersonalInfoActivity.this)
                .setTitle("Sex")
                .setSingleChoiceItems(sexTypes, selectedSexIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sexEdit.setText(sexTypes[which]);
                        selectedSexIndex = which;
                        dialog.dismiss();

                    }
                }).create();

        sexEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                chooseSexDlg.show();
            }
        });

        if (!"".equals(selectedHeight)) {
            heightEdit.setText(selectedHeight.replace("ches", ""));
            heightEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int ftValue = 0;
                    int inchValue = 0;
                    for (int i = 0; i < ftArray.length; i++) {
                        if (selectedHeightFeet.equals(ftArray[i])) {
                            ftValue = i;
                            break;
                        }
                    }
                    for (int i = 0; i < inchArray.length; i++) {
                        if (selectedHeightInches.equals(inchArray[i])) {
                            inchValue = i;
                            break;
                        }
                    }
                    ftPicker.setValue(ftValue);
                    inchPicker.setValue(inchValue);
                    chooseHeightDlg.show();
                }
            });
        }

        weightGoalEdit.setText(selectedWeightGoal);
        currentWeightEdit.setText(selectedWeight);

        if (!"".equals(selectedActivityLevel)) {

            selectedActivityLevelIndex = 0;

            if ("1.0".equals(selectedActivityLevel)) {
                selectedActivityLevelIndex = 0;
                activityLevelEdit.setText("Base Metabolic Rate");

            }else if ("1.2".equals(selectedActivityLevel)) {
                selectedActivityLevelIndex = 1;
                activityLevelEdit.setText("Sedentary");

            } else if ("1.375".equals(selectedActivityLevel)) {
                selectedActivityLevelIndex = 2;
                activityLevelEdit.setText("Lightly Active");

            } else if ("1.55".equals(selectedActivityLevel)) {
                selectedActivityLevelIndex = 3;
                activityLevelEdit.setText("Moderately Active");

            } else if ("1.725".equals(selectedActivityLevel)) {
                selectedActivityLevelIndex = 4;
                activityLevelEdit.setText("Very Active");

            } else if ("1.9".equals(selectedActivityLevel)) {
                selectedActivityLevelIndex = 5;
                activityLevelEdit.setText("Extra Active");
            }
        }

        chooseActivityLevelDlg = new AlertDialog.Builder(HTMyAccountPersonalInfoActivity.this)
                .setTitle("Activity Level")
                .setSingleChoiceItems(activityLevels, selectedActivityLevelIndex, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activityLevelEdit.setText(activityLevelLabels[which]);
                        selectedActivityLevelIndex = which;
                        dialog.dismiss();

                    }
                }).create();

        activityLevelEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseActivityLevelDlg.show();
            }
        });

        linearLayoutPersonalInfo.setVisibility(View.VISIBLE);
        updateBtn.setVisibility(View.VISIBLE);
        updateBtn.setTypeface(avenirNextMediumFont);
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updatePersonalInfoValues();
            }
        });
    }

    private void updatePersonalInfoValues() {

        imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);

        try {
            selectedHeight = URLEncoder.encode(heightEdit.getText().toString() + "ches", "utf-8");

        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
        }

        if (selectedSexIndex == 0)
            selectedSex = "MALE";
        else
            selectedSex = "FEMALE";

        selectedWeightGoal = weightGoalEdit.getText().toString();
        selectedWeight = currentWeightEdit.getText().toString();
        selectedActivityLevel = activityLevelValues[selectedActivityLevelIndex];
        final SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yyyy", Locale.US);

        progressDialog.show();
        myThread = new Thread() {

            @Override
            public void run() {

                try {
                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "update_personal_info_values"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("client_height", selectedHeight));
                    nameValuePairs.add(new BasicNameValuePair("client_sex", selectedSex));
                    nameValuePairs.add(new BasicNameValuePair("client_weight", selectedWeight));
                    nameValuePairs.add(new BasicNameValuePair("client_weight_goal", selectedWeightGoal));
                    nameValuePairs.add(new BasicNameValuePair("client_dob", birthDate == null ? "" : dateFormat.format(birthDate)));
                    nameValuePairs.add(new BasicNameValuePair("client_activity_multiplier", selectedActivityLevel));

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

                                toast = HTToast.showToast(HTMyAccountPersonalInfoActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        finish();
                    }

                } catch (Exception e) {

                    final String error = e.getMessage();

                    runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            HTConnectErrDialog.showDilaog(HTMyAccountPersonalInfoActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showDatePickerDlg(final TextView timeView) {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        calendar.add(Calendar.MONTH, -192);
        final Date maxDate = calendar.getTime();

        if (birthDate != null)
            calendar.setTime(birthDate);
        else
            birthDate = maxDate;

        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
                calendar.set(year, monthOfYear, dayOfMonth);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if (calendar.getTime().after(maxDate)) {
                    calendar.setTime(birthDate);
                    timeView.setText(dateFormat.format(calendar.getTime()));
                } else {
                    birthDate = calendar.getTime();
                    timeView.setText(dateFormat.format(calendar.getTime()));

                }
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
        datePickerDialog.show();
    }

}
