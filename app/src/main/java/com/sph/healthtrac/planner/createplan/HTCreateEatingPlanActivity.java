package com.sph.healthtrac.planner.createplan;

import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.NumberPicker;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HTCreateEatingPlanActivity extends Activity {
    private static RelativeLayout mainContentLayout;
    private ScrollView scrollView;

    private TextView heightLabel;
    private TextView weightLabel;
    private TextView ageLabel;
    private TextView sexLabel;
    private TextView activityLevelLabel;
    private TextView heightEdit;
    private EditText weightEdit;
    private EditText ageEdit;
    private TextView sexEdit;
    private TextView activityLevelEdit;

    private Button calculateBtn;

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

    String selectedHeight;
    String selectedHeightFeet;
    String selectedHeightInches;
    String selectedSex;
    String selectedWeight;
    String selectedAge;
    String selectedActivityLevel;

    String caloriesToMaintain;
    String caloriesToLoseOneLb;
    String caloriesToLoseTwoLbs;

    String[] activityLevels;
    String[] activityLevelLabels;
    String[] activityLevelValues;
    String[] sexTypes = {"Male", "Female"};
    int selectedSexIndex;
    int selectedActivityLevelIndex;

    Dialog chooseHeightDlg;
    Dialog chooseSexDlg;
    Dialog chooseActivityLevelDlg;

    NumberPicker ftPicker;
    NumberPicker inchPicker;
    String[] ftArray;
    String[] inchArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan);

        ftArray = getResources().getStringArray(R.array.ft_array);
        inchArray = getResources().getStringArray(R.array.inch_array);

        activityLevels = getResources().getStringArray(R.array.activity_levels);
        activityLevelLabels = getResources().getStringArray(R.array.activity_level_labels);
        activityLevelValues = getResources().getStringArray(R.array.activity_level_values);

        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        heightLabel = (TextView) findViewById(R.id.heightLabel);
        weightLabel = (TextView) findViewById(R.id.weightLabel);
        ageLabel = (TextView) findViewById(R.id.ageLabel);
        sexLabel = (TextView) findViewById(R.id.sexLabel);
        activityLevelLabel = (TextView) findViewById(R.id.activityLevelLabel);
        heightEdit = (TextView) findViewById(R.id.heightEdit);
        weightEdit = (EditText) findViewById(R.id.weightEdit);
        ageEdit = (EditText) findViewById(R.id.ageEdit);
        sexEdit = (TextView) findViewById(R.id.sexEdit);
        activityLevelEdit = (TextView) findViewById(R.id.activityLevelEdit);
        calculateBtn = (Button) findViewById(R.id.calculateBtn);

        Typeface avenirNextRegularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        Typeface avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");
        Typeface openSansLightFont = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");

        heightLabel.setTypeface(avenirNextRegularFont);
        weightLabel.setTypeface(avenirNextRegularFont);
        ageLabel.setTypeface(avenirNextRegularFont);
        sexLabel.setTypeface(avenirNextRegularFont);
        activityLevelLabel.setTypeface(avenirNextRegularFont);
        heightEdit.setTypeface(avenirNextMediumFont);
        weightEdit.setTypeface(avenirNextMediumFont);
        ageEdit.setTypeface(avenirNextMediumFont);
        sexEdit.setTypeface(avenirNextMediumFont);
        activityLevelEdit.setTypeface(avenirNextMediumFont);
        calculateBtn.setTypeface(avenirNextMediumFont);

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
        View mActionBar = HTActionBar.getActionBar(this, "Create Eating Plan", "leftArrow", "");

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
        chooseHeightDlg = new AlertDialog.Builder(HTCreateEatingPlanActivity.this)
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

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            getCreateEatingPlanValues();
        }
    }

    private void getCreateEatingPlanValues() {

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        selectedHeight = "";
        selectedHeightFeet = "";
        selectedHeightInches = "";
        selectedSex = "";
        selectedWeight = "";
        selectedAge = "";
        selectedActivityLevel = "";

        caloriesToMaintain = "";
        caloriesToLoseOneLb = "";
        caloriesToLoseTwoLbs = "";

        selectedActivityLevelIndex = 0;

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_create_eating_plan_values"));
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
                    NodeList nodes = doc.getElementsByTagName("create_eating_plan");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTCreateEatingPlanActivity.this, errorMessage, Toast.LENGTH_LONG);
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

                                if (XMLFunctions.tagExists(e2, "client_age")) {
                                    tempString = XMLFunctions.getValue(e2, "client_age");
                                    selectedAge = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "client_sex")) {
                                    tempString = XMLFunctions.getValue(e2, "client_sex");
                                    selectedSex = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "client_activity_multiplier")) {
                                    tempString = XMLFunctions.getValue(e2, "client_activity_multiplier");
                                    selectedActivityLevel = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                showCreateEatingPlan();
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

//                                    toast = HTToast.showToast(HTCreateEatingPlanActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTCreateEatingPlanActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showCreateEatingPlan() {
        scrollView.setVisibility(View.VISIBLE);

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

        weightEdit.setText(selectedWeight);
        ageEdit.setText(selectedAge);

        if (!"".equals(selectedSex)) {

            if ("MALE".equals(selectedSex)) {
                selectedSexIndex = 0;
                sexEdit.setText("Male");

            } else if ("FEMALE".equals(selectedSex)) {
                selectedSexIndex = 1;
                sexEdit.setText("Female");
            }
        }

        chooseSexDlg = new AlertDialog.Builder(HTCreateEatingPlanActivity.this)
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

        chooseActivityLevelDlg = new AlertDialog.Builder(HTCreateEatingPlanActivity.this)
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

        calculateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calculateButtonPressed();
            }
        });

    }

    private void updateCreateEatingPlanValues() {
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        caloriesToMaintain = "";
        caloriesToLoseOneLb = "";
        caloriesToLoseTwoLbs = "";

        try {
            selectedHeight = URLEncoder.encode(heightEdit.getText().toString() + "ches", "utf-8");

        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
        }

        if (selectedSexIndex == 0)
            selectedSex = "MALE";
        else
            selectedSex = "FEMALE";

        selectedWeight = weightEdit.getText().toString();
        selectedAge = ageEdit.getText().toString();
        selectedActivityLevel = activityLevelValues[selectedActivityLevelIndex];

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "update_create_eating_plan_values"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("client_height", selectedHeight));
                    nameValuePairs.add(new BasicNameValuePair("client_sex", selectedSex));
                    nameValuePairs.add(new BasicNameValuePair("client_weight", selectedWeight));
                    nameValuePairs.add(new BasicNameValuePair("client_age", selectedAge));
                    nameValuePairs.add(new BasicNameValuePair("client_activity_multiplier", selectedActivityLevel));

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();
                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);

                    NodeList error = doc.getElementsByTagName("error");
                    NodeList nodes = doc.getElementsByTagName("create_eating_plan");


                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTCreateEatingPlanActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();
                                String tempString;

                                if (XMLFunctions.tagExists(e2, "calories_to_maintain")) {
                                    tempString = XMLFunctions.getValue(e2, "calories_to_maintain");
                                    caloriesToMaintain = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "calories_to_lose_1_lb")) {
                                    tempString = XMLFunctions.getValue(e2, "calories_to_lose_1_lb");
                                    caloriesToLoseOneLb = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "calories_to_lose_2_lbs")) {
                                    tempString = XMLFunctions.getValue(e2, "calories_to_lose_2_lbs");
                                    caloriesToLoseTwoLbs = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                Intent intent = new Intent(HTCreateEatingPlanActivity.this, HTCreateEatingPlanSelectActivity.class);
                                intent.putExtra("caloriesToMaintain", caloriesToMaintain);
                                intent.putExtra("caloriesToLoseOneLb", caloriesToLoseOneLb);
                                intent.putExtra("caloriesToLoseTwoLbs", caloriesToLoseTwoLbs);
                                startActivityForResult(intent, 1);
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

//                                    toast = HTToast.showToast(HTCreateEatingPlanActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTCreateEatingPlanActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void calculateButtonPressed() {
        imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
        if ("".equals(heightEdit.getText().toString().trim())) {
            HTToast.showToast(HTCreateEatingPlanActivity.this, "Please select your height", Toast.LENGTH_LONG);
            chooseHeightDlg.show();
        } else if ("".equals(weightEdit.getText().toString().trim())) {
            HTToast.showToast(HTCreateEatingPlanActivity.this, "Please select your weight", Toast.LENGTH_LONG);
            weightEdit.requestFocus();
        } else if ("".equals(ageEdit.getText().toString().trim())) {
            HTToast.showToast(HTCreateEatingPlanActivity.this, "Please select your age", Toast.LENGTH_LONG);
            ageEdit.requestFocus();
        } else if ("".equals(sexEdit.getText().toString().trim())) {
            HTToast.showToast(HTCreateEatingPlanActivity.this, "Please select your sex", Toast.LENGTH_LONG);
            chooseSexDlg.show();
        } else if ("".equals(activityLevelEdit.getText().toString().trim())) {
            HTToast.showToast(HTCreateEatingPlanActivity.this, "Please select your activity level", Toast.LENGTH_LONG);
            chooseActivityLevelDlg.show();

        } else {
            updateCreateEatingPlanValues();
        }
    }


    public void onActivityResult(int requestCode, int resultCode, Intent data) { // coming back from a child activity, refresh the calendar
        if (resultCode == 2) {
            setResult(2);
            finish();
            return;
        }
        passDate = HTGlobals.getInstance().passDate;
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;

        getCreateEatingPlanValues();
    }
}
