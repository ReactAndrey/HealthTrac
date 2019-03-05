package com.sph.healthtrac.planner.createplan;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
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

public class HTCreateEatingPlanSelectActivity extends Activity {
    private static RelativeLayout mainContentLayout;
    private ScrollView scrollView;

    private RelativeLayout topLabels;
    private RelativeLayout maintainLayout;
    private RelativeLayout loseOneLayout;
    private RelativeLayout loseTwoLayout;

    private TextView inordertoLabel;
    private TextView dailyCaloriesLabel;
    private TextView maintainLabel;
    private TextView maintainValueView;
    private TextView loseOneLabel;
    private TextView loseOneValueView;
    private TextView loseTwoLabel;
    private TextView loseTwoValueView;

    private RelativeLayout chooseEatingPlanLayout;
    private TextView chooseEatingPlanLabel;

    private LinearLayout eatingPlansLayout;
    private Button loadPlanBtn;

    private TextView toDateEdit;
    private TextView fromDateEdit;

    float displayDensity;

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

    String caloriesToMaintain;
    String caloriesToLoseOneLb;
    String caloriesToLoseTwoLbs;

    String selectedCalories = "";
    String selectedEatingPlanID = "";

    private List<String> practicePlanID = new ArrayList<>();
    private List<String> practicePlanName = new ArrayList<>();
    private List<String> practicePlanCalories = new ArrayList<>();

    Typeface avenirNextRegularFont;
    Typeface avenirNextMediumFont;
    Typeface openSansLightFont;

    private String fromDateStr = "";
    private String toDateStr = "";
    private Date fromDate, toDate;

    private List<RelativeLayout> eatingPlanLayoutList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_plan_select);

        caloriesToMaintain = getIntent().getStringExtra("caloriesToMaintain");
        caloriesToLoseOneLb = getIntent().getStringExtra("caloriesToLoseOneLb");
        caloriesToLoseTwoLbs = getIntent().getStringExtra("caloriesToLoseTwoLbs");

        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        topLabels = (RelativeLayout) findViewById(R.id.topLabels);
        maintainLayout = (RelativeLayout) findViewById(R.id.maintainLayout);
        loseOneLayout = (RelativeLayout) findViewById(R.id.loseOneLayout);
        loseTwoLayout = (RelativeLayout) findViewById(R.id.loseTwoLayout);
        inordertoLabel = (TextView) findViewById(R.id.inordertoLabel);
        dailyCaloriesLabel = (TextView) findViewById(R.id.dailyCaloriesLabel);
        maintainLabel = (TextView) findViewById(R.id.maintainLabel);
        maintainValueView = (TextView) findViewById(R.id.maintainValueView);
        loseOneLabel = (TextView) findViewById(R.id.loseOneLabel);
        loseOneValueView = (TextView) findViewById(R.id.loseOneValueView);
        loseTwoLabel = (TextView) findViewById(R.id.loseTwoLabel);
        loseTwoValueView = (TextView) findViewById(R.id.loseTwoValueView);
        chooseEatingPlanLayout = (RelativeLayout) findViewById(R.id.chooseEatingPlanLayout);
        chooseEatingPlanLabel = (TextView) findViewById(R.id.chooseEatingPlanLabel);
        eatingPlansLayout = (LinearLayout) findViewById(R.id.eatingPlansLayout);
        loadPlanBtn = (Button) findViewById(R.id.loadPlanBtn);

        avenirNextRegularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");
        openSansLightFont = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");

        inordertoLabel.setTypeface(avenirNextMediumFont);
        dailyCaloriesLabel.setTypeface(avenirNextMediumFont);
        maintainLabel.setTypeface(avenirNextRegularFont);
        maintainValueView.setTypeface(openSansLightFont);
        loseOneLabel.setTypeface(avenirNextRegularFont);
        loseOneValueView.setTypeface(openSansLightFont);
        loseTwoLabel.setTypeface(avenirNextRegularFont);
        loseTwoValueView.setTypeface(openSansLightFont);
        chooseEatingPlanLabel.setTypeface(avenirNextMediumFont);
        loadPlanBtn.setTypeface(avenirNextMediumFont);

        loadPlanBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadPlanButtonPressed();
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
        View mActionBar = HTActionBar.getActionBar(this, "Select Eating Plan", "leftArrow", "");

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
            getPracticePlans();
        }

    }

    private void getPracticePlans() {
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_create_eating_plan_practice_plans"));
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

                                toast = HTToast.showToast(HTCreateEatingPlanSelectActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();
                                String tempString;

                                int i = 1;
                                while (XMLFunctions.tagExists(e2, "plan_id_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "plan_id_" + i);
                                    practicePlanID.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "plan_name_" + i);
                                    practicePlanName.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "plan_calories_" + i);
                                    practicePlanCalories.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                showCreateEatingPlanOptions();
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

//                                    toast = HTToast.showToast(HTCreateEatingPlanSelectActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTCreateEatingPlanSelectActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void selectEatingPlan() {
        HTGlobals.getInstance().plannerShouldRefresh = true;
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yy", Locale.US);
                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "create_eating_plan_choose_plan"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("selected_template_id", selectedEatingPlanID));
                    nameValuePairs.add(new BasicNameValuePair("start_date", dateFormat.format(fromDate)));
                    nameValuePairs.add(new BasicNameValuePair("end_date", dateFormat.format(toDate)));

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

                                toast = HTToast.showToast(HTCreateEatingPlanSelectActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
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

//                                    toast = HTToast.showToast(HTCreateEatingPlanSelectActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTCreateEatingPlanSelectActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void updatePlannerTargetCalories() {
        HTGlobals.getInstance().plannerShouldRefresh = true;
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "update_planner_target_calories"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("plan_calories", selectedCalories));

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

                                toast = HTToast.showToast(HTCreateEatingPlanSelectActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
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

//                                    toast = HTToast.showToast(HTCreateEatingPlanSelectActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTCreateEatingPlanSelectActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showCreateEatingPlanOptions() {
        scrollView.setVisibility(View.VISIBLE);
        eatingPlanLayoutList.clear();

        if (HTGlobals.getInstance().hideCalorieCalculator) {

            topLabels.setVisibility(View.GONE);
            maintainLayout.setVisibility(View.GONE);
            loseOneLayout.setVisibility(View.GONE);
            loseTwoLayout.setVisibility(View.GONE);
            chooseEatingPlanLabel.setText("CHOOSE AN EATING PLAN TEMPLATE");

        } else {

            // calories to maintain
            if (!"N/A".equals(caloriesToMaintain)) {
                maintainLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedCalories = caloriesToMaintain;
                        updatePlannerTargetCalories();
                    }
                });
            }

            maintainValueView.setText(caloriesToMaintain);

            // calories to lose 1 lb
            if (!"N/A".equals(caloriesToLoseOneLb)) {
                loseOneLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedCalories = caloriesToLoseOneLb;
                        updatePlannerTargetCalories();
                    }
                });
            }

            loseOneValueView.setText(caloriesToLoseOneLb);

            // calories to lose 2 lbs
            if (!"N/A".equals(caloriesToLoseTwoLbs)) {
                loseTwoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedCalories = caloriesToLoseTwoLbs;
                        updatePlannerTargetCalories();
                    }
                });
            }

            loseTwoValueView.setText(caloriesToLoseTwoLbs);
        }

        // practice plans
        if (practicePlanID.size() > 0) {
            chooseEatingPlanLayout.setVisibility(View.VISIBLE);
            eatingPlansLayout.setVisibility(View.VISIBLE);
            loadPlanBtn.setVisibility(View.VISIBLE);

            LayoutInflater mInflater = LayoutInflater.from(this);
            LinearLayout.LayoutParams params;

            for (int i = 0; i < practicePlanID.size(); i++) {
                // practice plan
                final RelativeLayout eatingPlanLayout = (RelativeLayout) mInflater.inflate(R.layout.practice_plan_cell, null);
                eatingPlanLayout.setTag(practicePlanID.get(i));
                eatingPlanLayoutList.add(eatingPlanLayout);

                params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (45 * displayDensity));
                params.setMargins((int) (6 * displayDensity), (int) (0 * displayDensity), (int) (6 * displayDensity), (int) (4 * displayDensity));
                eatingPlanLayout.setLayoutParams(params);

                TextView planNameView = (TextView) eatingPlanLayout.findViewById(R.id.planNameView);
                TextView planCalorieView = (TextView) eatingPlanLayout.findViewById(R.id.planValueView);
                planNameView.setTypeface(avenirNextRegularFont);
                planCalorieView.setTypeface(openSansLightFont);

                planNameView.setText(practicePlanName.get(i));
                planCalorieView.setText(practicePlanCalories.get(i));

                eatingPlanLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (selectedEatingPlanID.equals((String) v.getTag())) {
                            selectedEatingPlanID = "";
                        } else {
                            selectedEatingPlanID = (String) v.getTag();
                        }

                        for (int i = 0; i < eatingPlanLayoutList.size(); i++) {
                            RelativeLayout layout = eatingPlanLayoutList.get(i);
                            if (selectedEatingPlanID.equals((String) layout.getTag()))
                                layout.setBackgroundResource(R.drawable.ht_selected_myplan_bg);
                            else
                                layout.setBackgroundColor(Color.WHITE);
                        }
                    }
                });

                eatingPlansLayout.addView(eatingPlanLayout);
            }

            //from date
            RelativeLayout fromdateLayout = new RelativeLayout(this);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (45 * displayDensity));
            params.setMargins((int) (6 * displayDensity), (int) (20 * displayDensity), (int) (6 * displayDensity), (int) (4 * displayDensity));
            fromdateLayout.setLayoutParams(params);
            fromdateLayout.setBackgroundColor(Color.WHITE);
            fromdateLayout.setPadding((int) (14 * displayDensity), 0, (int) (6 * displayDensity), 0);

            TextView fromLabel = new TextView(this);
            fromLabel.setText("From");
            fromLabel.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
            fromLabel.setTypeface(avenirNextRegularFont);
            fromLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
            fromLabel.setLayoutParams(relativeParams);
            fromdateLayout.addView(fromLabel);

            fromDateEdit = new TextView(this);
            fromDateEdit.setBackgroundResource(R.drawable.ht_text_field);
            fromDateEdit.setTypeface(openSansLightFont);
            fromDateEdit.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
            fromDateEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            fromDateEdit.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            relativeParams = new RelativeLayout.LayoutParams((screenWidth - (int) (32 * displayDensity)) / 2, (int) (31 * displayDensity));
            relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            fromDateEdit.setLayoutParams(relativeParams);
            fromdateLayout.addView(fromDateEdit);

            eatingPlansLayout.addView(fromdateLayout);

            if ("".equals(fromDateStr)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
                calendar = Calendar.getInstance();
                if(passDate.after(currentDate))
                    calendar.setTime(passDate);
                else
                    calendar.setTime(currentDate);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                fromDateStr = dateFormat.format(calendar.getTime());
                fromDate = calendar.getTime();
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
                calendar = Calendar.getInstance();

                try {
                    Date tempDate = dateFormat.parse(fromDateStr);
                    calendar.setTime(tempDate);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                } catch (ParseException e) {
                    calendar.setTime(currentDate);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                }
                fromDateStr = dateFormat.format(calendar.getTime());
                fromDate = calendar.getTime();
            }
            fromDateEdit.setText(fromDateStr);
            fromDateEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDlg(fromDateEdit, true);
                }
            });

            //to date
            RelativeLayout todateLayout = new RelativeLayout(this);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (45 * displayDensity));
            params.setMargins((int) (6 * displayDensity), (int) (0 * displayDensity), (int) (6 * displayDensity), (int) (4 * displayDensity));
            todateLayout.setLayoutParams(params);
            todateLayout.setBackgroundColor(Color.WHITE);
            todateLayout.setPadding((int) (14 * displayDensity), 0, (int) (6 * displayDensity), 0);

            TextView toLabel = new TextView(this);
            toLabel.setText("To");
            toLabel.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
            toLabel.setTypeface(avenirNextRegularFont);
            toLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
            toLabel.setLayoutParams(relativeParams);
            todateLayout.addView(toLabel);

            toDateEdit = new TextView(this);
            toDateEdit.setBackgroundResource(R.drawable.ht_text_field);
            toDateEdit.setTypeface(openSansLightFont);
            toDateEdit.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
            toDateEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
            toDateEdit.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

            relativeParams = new RelativeLayout.LayoutParams((screenWidth - (int) (32 * displayDensity)) / 2, (int) (31 * displayDensity));
            relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            toDateEdit.setLayoutParams(relativeParams);
            todateLayout.addView(toDateEdit);

            eatingPlansLayout.addView(todateLayout);

            if ("".equals(toDateStr)) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
                calendar = Calendar.getInstance();
                if(passDate.after(currentDate))
                    calendar.setTime(passDate);
                else
                    calendar.setTime(currentDate);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                toDateStr = dateFormat.format(calendar.getTime());
                toDate = calendar.getTime();
            } else {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM d, yyyy", Locale.US);
                calendar = Calendar.getInstance();

                try {
                    Date tempDate = dateFormat.parse(toDateStr);
                    calendar.setTime(tempDate);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                } catch (ParseException e) {
                    calendar.setTime(currentDate);
                    calendar.set(Calendar.HOUR_OF_DAY, 0);
                    calendar.set(Calendar.MINUTE, 0);
                    calendar.set(Calendar.SECOND, 0);
                    calendar.set(Calendar.MILLISECOND, 0);
                }
                toDateStr = dateFormat.format(calendar.getTime());
                toDate = calendar.getTime();
            }
            toDateEdit.setText(toDateStr);
            toDateEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    showDatePickerDlg(toDateEdit, false);
                }
            });
        }

    }

    private void showDatePickerDlg(final TextView timeView, final boolean isFromDate) {
        Date selectedDate = null;
        if (isFromDate)
            selectedDate = fromDate;
        else
            selectedDate = toDate;
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        final Date minDate = calendar.getTime();
        calendar.add(Calendar.MONTH, 2);
        final Date maxDate = calendar.getTime();

        if (selectedDate != null)
            calendar.setTime(selectedDate);
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

                if (calendar.getTime().before(minDate) || calendar.getTime().after(maxDate)) {
                    if (isFromDate) {
                        calendar.setTime(fromDate);
                        timeView.setText(dateFormat.format(calendar.getTime()));
                    } else {
                        calendar.setTime(toDate);
                        timeView.setText(dateFormat.format(calendar.getTime()));
                    }
                } else {
                    timeView.setText(dateFormat.format(calendar.getTime()));
                    if (isFromDate) {
                        fromDate = calendar.getTime();
                        if(toDate.before(fromDate)) {
                            toDate = new Date(fromDate.getTime());
                            toDateEdit.setText(dateFormat.format(toDate));
                        }
                    }else {
                        toDate = calendar.getTime();
                        if(toDate.before(fromDate)) {
                            fromDate = new Date(toDate.getTime());
                            fromDateEdit.setText(dateFormat.format(fromDate));
                        }
                    }
                }
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMinDate(minDate.getTime());
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
        datePickerDialog.show();
    }

    private void loadPlanButtonPressed() {
        if ("".equals(selectedEatingPlanID)) {

            HTToast.showToast(this, "Please choose an eating plan template", Toast.LENGTH_LONG);

        } else if (fromDate.after(toDate)) {

            HTToast.showToast(this, "From date cannot be after To date", Toast.LENGTH_LONG);
        } else {

            selectEatingPlan();
        }
    }
}
