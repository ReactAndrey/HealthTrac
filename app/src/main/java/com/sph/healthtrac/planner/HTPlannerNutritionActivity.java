package com.sph.healthtrac.planner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTToast;
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

public class HTPlannerNutritionActivity extends Activity {

    private static RelativeLayout relativeLayoutPlannerNutrition;
    private ScrollView scrollViewNutritionDetails;
    private LinearLayout layoutNutritionDetails;
    private TextView planNameView;

    private LinearLayout caloriesPlanView;
    private TextView goalLabel;
    private TextView goalValue;
    private TextView consumedLabel;
    private TextView consumedValue;
    private TextView burnedLabel;
    private TextView burnedValue;
    private TextView remainingLabel;
    private TextView remainingValue;

    private TextView summaryLabel;
    private TextView caloriesLabel;
    private TextView caloriesValue;
    private TextView carbsLabel;
    private TextView carbsValue;
    private TextView fiberLabel;
    private TextView fiberValue;
    private TextView sugarsLabel;
    private TextView sugarsValue;
    private TextView proteinLabel;
    private TextView proteinValue;
    private TextView totalFatLabel;
    private TextView totalFatValue;
    private TextView satfatLabel;
    private TextView satfatValue;
    private TextView sodiumLabel;
    private TextView sodiumValue;

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

    private String plannerName;
    private String plannerCalories;
    private String plannerProtein;
    private String plannerCarbs;
    private String plannerFiber;
    private String plannerSugar;
    private String plannerSodium;
    private String plannerFat;
    private String plannerSatFat;
    private String plannerCaloriesBurned;
    private String plannerTargetCalories;

    float displayDensity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner_nutrition);

        relativeLayoutPlannerNutrition = (RelativeLayout) findViewById(R.id.relativeLayoutPlannerNutrition);
        scrollViewNutritionDetails = (ScrollView) findViewById(R.id.scrollViewNutritionDetail);
        layoutNutritionDetails = (LinearLayout) findViewById(R.id.layoutNutritionDetails);

        planNameView = (TextView) findViewById(R.id.titleTextView);
        caloriesPlanView = (LinearLayout) findViewById(R.id.caloriesPlanView);
        goalLabel = (TextView) findViewById(R.id.goalLabel);
        goalValue = (TextView) findViewById(R.id.goalValue);
        consumedLabel = (TextView) findViewById(R.id.consumedLabel);
        consumedValue = (TextView) findViewById(R.id.consumedValue);
        burnedLabel = (TextView) findViewById(R.id.burnedLabel);
        burnedValue = (TextView) findViewById(R.id.burnedValue);
        remainingLabel = (TextView) findViewById(R.id.remainingLabel);
        remainingValue = (TextView) findViewById(R.id.remainingValue);

        summaryLabel = (TextView) findViewById(R.id.summaryLabel);
        caloriesLabel = (TextView) findViewById(R.id.caloriesLabel);
        caloriesValue = (TextView) findViewById(R.id.caloriesValue);
        carbsLabel = (TextView) findViewById(R.id.carbsLabel);
        carbsValue = (TextView) findViewById(R.id.carbsValue);
        fiberLabel = (TextView) findViewById(R.id.fiberLabel);
        fiberValue = (TextView) findViewById(R.id.fiberValue);
        sugarsLabel = (TextView) findViewById(R.id.sugarsLabel);
        sugarsValue = (TextView) findViewById(R.id.sugarsValue);
        proteinLabel = (TextView) findViewById(R.id.proteinLabel);
        proteinValue = (TextView) findViewById(R.id.proteinValue);
        totalFatLabel = (TextView) findViewById(R.id.totalFatLabel);
        totalFatValue = (TextView) findViewById(R.id.totalFatValue);
        satfatLabel = (TextView) findViewById(R.id.satfatLabel);
        satfatValue = (TextView) findViewById(R.id.satfatValue);
        sodiumLabel = (TextView) findViewById(R.id.sodiumLabel);
        sodiumValue = (TextView) findViewById(R.id.sodiumValue);

        Typeface planNameFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        Typeface nutritionLabelFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-DemiBold.ttf");
        Typeface nutritionValueFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");
        Typeface nutritionValueFont21 = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");

        planNameView.setTypeface(planNameFont);
        goalLabel.setTypeface(nutritionValueFont);
        consumedLabel.setTypeface(nutritionValueFont);
        burnedLabel.setTypeface(nutritionValueFont);
        remainingLabel.setTypeface(nutritionValueFont);
        goalValue.setTypeface(nutritionValueFont21);
        consumedValue.setTypeface(nutritionValueFont21);
        burnedValue.setTypeface(nutritionValueFont21);
        remainingValue.setTypeface(nutritionValueFont21);
        summaryLabel.setTypeface(nutritionValueFont);
        caloriesLabel.setTypeface(planNameFont);
        caloriesValue.setTypeface(nutritionValueFont21);
        carbsLabel.setTypeface(nutritionLabelFont);
        fiberLabel.setTypeface(planNameFont);
        sugarsLabel.setTypeface(planNameFont);
        proteinLabel.setTypeface(nutritionLabelFont);
        totalFatLabel.setTypeface(nutritionLabelFont);
        satfatLabel.setTypeface(planNameFont);
        sodiumLabel.setTypeface(nutritionLabelFont);
        carbsValue.setTypeface(nutritionValueFont);
        fiberValue.setTypeface(nutritionValueFont);
        sugarsValue.setTypeface(nutritionValueFont);
        proteinValue.setTypeface(nutritionValueFont);
        totalFatValue.setTypeface(nutritionValueFont);
        satfatValue.setTypeface(nutritionValueFont);
        sodiumValue.setTypeface(nutritionValueFont);

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

        String title = "";
        if (currentDate.equals(passDate)) {
            // Today
            title = "Today";
        } else if (currentDate.equals(HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, 1, false))) {
            // Yesterday
            title = "Yesterday";

        } else {
            // other past dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            calendar = Calendar.getInstance();
            calendar.setTime(passDate);

            title = dateFormat.format(calendar.getTime());
        }

        // action bar
        View mActionBar = HTActionBar.getActionBar(this, title, "leftArrow", "");

        relativeLayoutPlannerNutrition.addView(mActionBar);

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

                imm.hideSoftInputFromWindow(relativeLayoutPlannerNutrition.getWindowToken(), 0);
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
            getNutrition();
        }
    }


    private void getNutrition() {

        plannerName = "";
        plannerCalories = "";
        plannerProtein = "";
        plannerCarbs = "";
        plannerFiber = "";
        plannerSugar = "";
        plannerSodium = "";
        plannerFat = "";
        plannerSatFat = "";
        plannerCaloriesBurned = "";
        plannerTargetCalories = "";

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_planner_nutrition"));
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
                    NodeList nodes = doc.getElementsByTagName("planner_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTPlannerNutritionActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {

                        final Element e2 = (Element) nodes.item(0);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                //planner name
                                if (XMLFunctions.tagExists(e2, "planner_name")) {
                                    plannerName = XMLFunctions.getValue(e2, "planner_name");
                                }

                                //planner calories
                                if (XMLFunctions.tagExists(e2, "planner_calories")) {
                                    plannerCalories = XMLFunctions.getValue(e2, "planner_calories");
                                }

                                //planner protein
                                if (XMLFunctions.tagExists(e2, "planner_protein")) {
                                    plannerProtein = XMLFunctions.getValue(e2, "planner_protein");
                                }

                                //planner carbs
                                if (XMLFunctions.tagExists(e2, "planner_carbs")) {
                                    plannerCarbs = XMLFunctions.getValue(e2, "planner_carbs");
                                }

                                //planner fiber
                                if (XMLFunctions.tagExists(e2, "planner_fiber")) {
                                    plannerFiber = XMLFunctions.getValue(e2, "planner_fiber");
                                }

                                //planner sugar
                                if (XMLFunctions.tagExists(e2, "planner_sugar")) {
                                    plannerSugar = XMLFunctions.getValue(e2, "planner_sugar");
                                }

                                //planner sodium
                                if (XMLFunctions.tagExists(e2, "planner_sodium")) {
                                    plannerSodium = XMLFunctions.getValue(e2, "planner_sodium");
                                }

                                //planner fat
                                if (XMLFunctions.tagExists(e2, "planner_fat")) {
                                    plannerFat = XMLFunctions.getValue(e2, "planner_fat");
                                }

                                //planner sat fat
                                if (XMLFunctions.tagExists(e2, "planner_sat_fat")) {
                                    plannerSatFat = XMLFunctions.getValue(e2, "planner_sat_fat");
                                }

                                //planner target calories
                                if (XMLFunctions.tagExists(e2, "planner_target_calories")) {
                                    plannerTargetCalories = XMLFunctions.getValue(e2, "planner_target_calories");
                                }

                                //planner calories burned
                                if (XMLFunctions.tagExists(e2, "planner_calories_burned")) {
                                    plannerCaloriesBurned = XMLFunctions.getValue(e2, "planner_calories_burned");
                                }

                                showNutrition();
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

//                                    toast = HTToast.showToast(HTPlannerNutritionActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTPlannerNutritionActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showNutrition() {

        scrollViewNutritionDetails.setVisibility(View.VISIBLE);
        if (!"".equals(plannerName)) {
            planNameView.setText(plannerName);
        } else {
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) caloriesPlanView.getLayoutParams();
            params.topMargin = (int) (20 * displayDensity);
            caloriesPlanView.setLayoutParams(params);
            planNameView.setVisibility(View.GONE);
        }

        goalValue.setText(plannerTargetCalories);
        consumedValue.setText(plannerCalories);
        burnedValue.setText(plannerCaloriesBurned);
        try {
            remainingValue.setText(String.valueOf(Integer.parseInt(plannerTargetCalories) - Integer.parseInt(plannerCalories) + Integer.parseInt(plannerCaloriesBurned)));
        } catch (NumberFormatException e) {
            remainingValue.setText("");
        }

        caloriesValue.setText(plannerCalories);
        carbsValue.setText(plannerCarbs + "g");
        fiberValue.setText(plannerFiber + "g");
        sugarsValue.setText(plannerSugar + "g");
        proteinValue.setText(plannerProtein + "g");
        totalFatValue.setText(plannerFat + "g");
        satfatValue.setText(plannerSatFat + "g");
        sodiumValue.setText(plannerSodium + "mg");

        goalValue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Set up the input

                LayoutInflater li = LayoutInflater.from(HTPlannerNutritionActivity.this);
                View promptsView = li.inflate(R.layout.update_calories_view, null);
                final EditText input = (EditText) promptsView.findViewById(R.id.input);
                input.setText(plannerTargetCalories);
                input.setSelection(plannerTargetCalories.length());
                AlertDialog.Builder builder = null;
                builder = new AlertDialog.Builder(HTPlannerNutritionActivity.this);
                final AlertDialog alert =
                        builder.setTitle(R.string.update_calories_goal)
                                .setMessage(getResources()
                                        .getString(R.string.enter_new_calories_goal))
                                .setView(promptsView)
                                .setCancelable(false)
                                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                                        plannerTargetCalories = input.getText().toString();
                                        updatePlannerTargetCalories();
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
        });
    }

    private void updatePlannerTargetCalories() {
        HTGlobals.getInstance().plannerShouldRefresh = true;
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);

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
                    nameValuePairs.add(new BasicNameValuePair("action", "update_planner_target_calories"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("plan_calories", plannerTargetCalories));

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

                                toast = HTToast.showToast(HTPlannerNutritionActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                getNutrition();
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

//                                    toast = HTToast.showToast(HTPlannerNutritionActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTPlannerNutritionActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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
