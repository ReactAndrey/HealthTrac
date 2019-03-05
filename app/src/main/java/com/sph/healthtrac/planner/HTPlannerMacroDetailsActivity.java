package com.sph.healthtrac.planner;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
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
import com.sph.healthtrac.common.PixelUtil;
import com.sph.healthtrac.common.XMLFunctions;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HTPlannerMacroDetailsActivity extends Activity {

    private static RelativeLayout mainContentLayout;
    private LinearLayout macroDetailsLayout;

    private TextView carbsLabelView;
    private TextView carbsValueView;
    private LinearLayout macroCarbsDetailsLayout;
    private TextView carbsTotalLabelView;
    private TextView carbsTotalValueView;

    private TextView fiberLabelView;
    private LinearLayout macroFiberDetailsLayout;
    private TextView fiberTotalLabelView;
    private TextView fiberTotalValueView;

    private TextView sugarLabelView;
    private LinearLayout macroSugarDetailsLayout;
    private TextView sugarTotalLabelView;
    private TextView sugarTotalValueView;

    private TextView proteinLabelView;
    private TextView proteinValueView;
    private LinearLayout macroProteinDetailsLayout;
    private TextView proteinTotalLabelView;
    private TextView proteinTotalValueView;

    private TextView fatLabelView;
    private TextView fatValueView;
    private LinearLayout macroFatDetailsLayout;
    private TextView fatTotalLabelView;
    private TextView fatTotalValueView;

    private TextView satfatLabelView;
    private LinearLayout macroSatFatDetailsLayout;
    private TextView satfatTotalLabelView;
    private TextView satfatTotalValueView;

    private TextView sodiumLabelView;
    private LinearLayout macroSodiumDetailsLayout;
    private TextView sodiumTotalLabelView;
    private TextView sodiumTotalValueView;

    private static InputMethodManager imm;
    private Calendar calendar;
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

    private Thread myThread = null;
    DefaultHttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;

    Toast toast;
    private View mActionBar;
    private float displayDensity;

    private String plannerProtein;
    private String plannerCarbs;
    private String plannerFiber;
    private String plannerSugar;
    private String plannerSodium;
    private String plannerFat;
    private String plannerSatFat;
    private String plannerCarbsPercentage;
    private String plannerProteinPercentage;
    private String plannerFatPercentage;

    private List<String> macroCarbsLabels = new ArrayList<>();
    private List<String> macroFiberLabels = new ArrayList<>();
    private List<String> macroSugarLabels = new ArrayList<>();
    private List<String> macroProteinLabels = new ArrayList<>();
    private List<String> macroFatLabels = new ArrayList<>();
    private List<String> macroSatFatLabels = new ArrayList<>();
    private List<String> macroSodiumLabels = new ArrayList<>();

    private List<String> macroCarbsValues = new ArrayList<>();
    private List<String> macroFiberValues = new ArrayList<>();
    private List<String> macroSugarValues = new ArrayList<>();
    private List<String> macroProteinValues = new ArrayList<>();
    private List<String> macroFatValues = new ArrayList<>();
    private List<String> macroSatFatValues = new ArrayList<>();
    private List<String> macroSodiumValues = new ArrayList<>();

    private Typeface avenirNextReqularFont;
    private Typeface avenirNextMediumFont;

    private int macroLabelsFontColor = Color.rgb(44,45,46);
    private int grayBarColor = Color.rgb(215, 221, 224);

    private int blueFontColor = Color.rgb(116, 204, 240);
    private int greenFontColor = Color.rgb(187, 227, 69);
    private int orangeFontColor = Color.rgb(247, 168, 97);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner_macro_details);

        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        avenirNextReqularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");

        macroDetailsLayout = (LinearLayout) findViewById(R.id.macroDetailsLayout);
        macroDetailsLayout.setVisibility(View.GONE);

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
        View mActionBar = HTActionBar.getActionBar(this, "Macronutrient Details", "leftArrow", "");

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

            carbsLabelView = (TextView) findViewById(R.id.carbsLabelView);
            carbsValueView = (TextView) findViewById(R.id.carbsValueView);
            macroCarbsDetailsLayout = (LinearLayout) findViewById(R.id.macroCarbsDetailsLayout);
            carbsTotalLabelView = (TextView) findViewById(R.id.carbsTotalLabelView);
            carbsTotalValueView = (TextView) findViewById(R.id.carbsTotalValueView);

            fiberLabelView = (TextView) findViewById(R.id.fiberLabelView);
            macroFiberDetailsLayout = (LinearLayout) findViewById(R.id.macroFiberDetailsLayout);
            fiberTotalLabelView = (TextView) findViewById(R.id.fiberTotalLabelView);
            fiberTotalValueView = (TextView) findViewById(R.id.fiberTotalValueView);

            sugarLabelView = (TextView) findViewById(R.id.sugarLabelView);
            macroSugarDetailsLayout = (LinearLayout) findViewById(R.id.macroSugarDetailsLayout);
            sugarTotalLabelView = (TextView) findViewById(R.id.sugarTotalLabelView);
            sugarTotalValueView = (TextView) findViewById(R.id.sugarTotalValueView);

            proteinLabelView = (TextView) findViewById(R.id.proteinLabelView);
            proteinValueView = (TextView) findViewById(R.id.proteinValueView);
            macroProteinDetailsLayout = (LinearLayout) findViewById(R.id.macroProteinDetailsLayout);
            proteinTotalLabelView = (TextView) findViewById(R.id.proteinTotalLabelView);
            proteinTotalValueView = (TextView) findViewById(R.id.proteinTotalValueView);

            fatLabelView = (TextView) findViewById(R.id.fatLabelView);
            fatValueView = (TextView) findViewById(R.id.fatValueView);
            macroFatDetailsLayout = (LinearLayout) findViewById(R.id.macroFatDetailsLayout);
            fatTotalLabelView = (TextView) findViewById(R.id.fatTotalLabelView);
            fatTotalValueView = (TextView) findViewById(R.id.fatTotalValueView);

            satfatLabelView = (TextView) findViewById(R.id.satfatLabelView);
            macroSatFatDetailsLayout = (LinearLayout) findViewById(R.id.macroSatFatDetailsLayout);
            satfatTotalLabelView = (TextView) findViewById(R.id.satfatTotalLabelView);
            satfatTotalValueView = (TextView) findViewById(R.id.satfatTotalValueView);

            sodiumLabelView = (TextView) findViewById(R.id.sodiumLabelView);
            macroSodiumDetailsLayout = (LinearLayout) findViewById(R.id.macroSodiumDetailsLayout);
            sodiumTotalLabelView = (TextView) findViewById(R.id.sodiumTotalLabelView);
            sodiumTotalValueView = (TextView) findViewById(R.id.sodiumTotalValueView);

            carbsLabelView.setTypeface(avenirNextMediumFont);
            carbsValueView.setTypeface(avenirNextReqularFont);
            carbsTotalLabelView.setTypeface(avenirNextMediumFont);
            carbsTotalValueView.setTypeface(avenirNextMediumFont);
            carbsLabelView.setTextColor(macroLabelsFontColor);
            carbsValueView.setTextColor(macroLabelsFontColor);
            carbsTotalLabelView.setTextColor(macroLabelsFontColor);
            carbsTotalValueView.setTextColor(macroLabelsFontColor);

            fiberLabelView.setTypeface(avenirNextMediumFont);
            fiberTotalLabelView.setTypeface(avenirNextMediumFont);
            fiberTotalValueView.setTypeface(avenirNextMediumFont);
            fiberLabelView.setTextColor(macroLabelsFontColor);
            fiberTotalLabelView.setTextColor(macroLabelsFontColor);
            fiberTotalValueView.setTextColor(macroLabelsFontColor);

            sugarLabelView.setTypeface(avenirNextMediumFont);
            sugarTotalLabelView.setTypeface(avenirNextMediumFont);
            sugarTotalValueView.setTypeface(avenirNextMediumFont);
            sugarLabelView.setTextColor(macroLabelsFontColor);
            sugarTotalLabelView.setTextColor(macroLabelsFontColor);
            sugarTotalValueView.setTextColor(macroLabelsFontColor);

            proteinLabelView.setTypeface(avenirNextMediumFont);
            proteinValueView.setTypeface(avenirNextReqularFont);
            proteinTotalLabelView.setTypeface(avenirNextMediumFont);
            proteinTotalValueView.setTypeface(avenirNextMediumFont);
            proteinLabelView.setTextColor(macroLabelsFontColor);
            proteinValueView.setTextColor(macroLabelsFontColor);
            proteinTotalLabelView.setTextColor(macroLabelsFontColor);
            proteinTotalValueView.setTextColor(macroLabelsFontColor);

            fatLabelView.setTypeface(avenirNextMediumFont);
            fatValueView.setTypeface(avenirNextReqularFont);
            fatTotalLabelView.setTypeface(avenirNextMediumFont);
            fatTotalValueView.setTypeface(avenirNextMediumFont);
            fatLabelView.setTextColor(macroLabelsFontColor);
            fatValueView.setTextColor(macroLabelsFontColor);
            fatTotalLabelView.setTextColor(macroLabelsFontColor);
            fatTotalValueView.setTextColor(macroLabelsFontColor);

            satfatLabelView.setTypeface(avenirNextMediumFont);
            satfatTotalLabelView.setTypeface(avenirNextMediumFont);
            satfatTotalValueView.setTypeface(avenirNextMediumFont);
            satfatLabelView.setTextColor(macroLabelsFontColor);
            satfatTotalLabelView.setTextColor(macroLabelsFontColor);
            satfatTotalValueView.setTextColor(macroLabelsFontColor);

            sodiumLabelView.setTypeface(avenirNextMediumFont);
            sodiumTotalLabelView.setTypeface(avenirNextMediumFont);
            sodiumTotalValueView.setTypeface(avenirNextMediumFont);
            sodiumLabelView.setTextColor(macroLabelsFontColor);
            sodiumTotalLabelView.setTextColor(macroLabelsFontColor);
            sodiumTotalValueView.setTextColor(macroLabelsFontColor);

            getMacroDetails();
        }
    }

    private void getMacroDetails() {
        plannerProtein = "";
        plannerCarbs = "";
        plannerFiber = "";
        plannerSugar = "";
        plannerSodium = "";
        plannerFat = "";
        plannerSatFat = "";
        plannerCarbsPercentage = "";
        plannerProteinPercentage = "";
        plannerFatPercentage = "";

        macroCarbsLabels.clear();
        macroFiberLabels.clear();
        macroSugarLabels.clear();
        macroProteinLabels.clear();
        macroFatLabels.clear();
        macroSatFatLabels.clear();
        macroSodiumLabels.clear();

        macroCarbsValues.clear();
        macroFiberValues.clear();
        macroSugarValues.clear();
        macroProteinValues.clear();
        macroFatValues.clear();
        macroSatFatValues.clear();
        macroSodiumValues.clear();

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();


        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_planner_nutrition_breakdown"));
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

                                toast = HTToast.showToast(HTPlannerMacroDetailsActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {

                        final Element e2 = (Element) nodes.item(0);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                HTGlobals htGlobals = HTGlobals.getInstance();

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

                                if (XMLFunctions.tagExists(e2, "planner_carbs_percentage")) {
                                    plannerCarbsPercentage = XMLFunctions.getValue(e2, "planner_carbs_percentage");
                                }

                                if (XMLFunctions.tagExists(e2, "planner_protein_percentage")) {
                                    plannerProteinPercentage = XMLFunctions.getValue(e2, "planner_protein_percentage");
                                }

                                if (XMLFunctions.tagExists(e2, "planner_fat_percentage")) {
                                    plannerFatPercentage = XMLFunctions.getValue(e2, "planner_fat_percentage");
                                }

                                String tempString;

                                int i = 1;
                                while (XMLFunctions.tagExists(e2, "carbs_" + i + "_label")) {
                                    tempString = XMLFunctions.getValue(e2, "carbs_" + i + "_label");
                                    macroCarbsLabels.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "carbs_" + i + "_value");
                                    macroCarbsValues.add(tempString);
                                    i++;
                                }

                                i = 1;
                                while (XMLFunctions.tagExists(e2, "fiber_" + i + "_label")) {
                                    tempString = XMLFunctions.getValue(e2, "fiber_" + i + "_label");
                                    macroFiberLabels.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "fiber_" + i + "_value");
                                    macroFiberValues.add(tempString);
                                    i++;
                                }

                                i = 1;
                                while (XMLFunctions.tagExists(e2, "sugar_" + i + "_label")) {
                                    tempString = XMLFunctions.getValue(e2, "sugar_" + i + "_label");
                                    macroSugarLabels.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "sugar_" + i + "_value");
                                    macroSugarValues.add(tempString);
                                    i++;
                                }

                                i = 1;
                                while (XMLFunctions.tagExists(e2, "protein_" + i + "_label")) {
                                    tempString = XMLFunctions.getValue(e2, "protein_" + i + "_label");
                                    macroProteinLabels.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "protein_" + i + "_value");
                                    macroProteinValues.add(tempString);
                                    i++;
                                }

                                i = 1;
                                while (XMLFunctions.tagExists(e2, "fat_" + i + "_label")) {
                                    tempString = XMLFunctions.getValue(e2, "fat_" + i + "_label");
                                    macroFatLabels.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "fat_" + i + "_value");
                                    macroFatValues.add(tempString);
                                    i++;
                                }

                                i = 1;
                                while (XMLFunctions.tagExists(e2, "sat_fat_" + i + "_label")) {
                                    tempString = XMLFunctions.getValue(e2, "sat_fat_" + i + "_label");
                                    macroSatFatLabels.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "sat_fat_" + i + "_value");
                                    macroSatFatValues.add(tempString);
                                    i++;
                                }

                                i = 1;
                                while (XMLFunctions.tagExists(e2, "sodium_" + i + "_label")) {
                                    tempString = XMLFunctions.getValue(e2, "sodium_" + i + "_label");
                                    macroSodiumLabels.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "sodium_" + i + "_value");
                                    macroSodiumValues.add(tempString);
                                    i++;
                                }

                                showMacroDetails();
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

//                                    toast = HTToast.showToast(HTPlannerMacroDetailsActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTPlannerMacroDetailsActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showMacroDetails(){

        LinearLayout linearLayout;
        LinearLayout.LayoutParams linearLayoutParams;
        TextView textView;
        View barView;

        //carbs
        carbsValueView.setText(plannerCarbsPercentage + "%");

        for(int i = 0; i < macroCarbsLabels.size(); i++){
            linearLayout = new LinearLayout(this);
            linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(this, 33));
            linearLayout.setLayoutParams(linearLayoutParams);
            macroCarbsDetailsLayout.addView(linearLayout);

            barView = new View(this);
            barView.setBackgroundColor(blueFontColor);
            linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(this, 1));
            barView.setLayoutParams(linearLayoutParams);
            macroCarbsDetailsLayout.addView(barView);

            textView = new TextView(this);
            textView.setTypeface(avenirNextReqularFont);
            textView.setText(macroCarbsLabels.get(i));
            textView.setTextSize(12);
            textView.setTextColor(macroLabelsFontColor);
            textView.setSingleLine(true);
            linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayoutParams.weight = 0.85f;
            linearLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(linearLayoutParams);
            linearLayout.addView(textView);

            textView = new TextView(this);
            textView.setTypeface(avenirNextReqularFont);
            textView.setText(macroCarbsValues.get(i)+ "g");
            textView.setTextSize(12);
            textView.setTextColor(macroLabelsFontColor);
            textView.setGravity(Gravity.RIGHT);
            linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayoutParams.weight = 0.15f;
            linearLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(linearLayoutParams);
            linearLayout.addView(textView);
        }

        carbsTotalValueView.setText(plannerCarbs + "g");

        //fiber
        for(int i = 0; i < macroFiberLabels.size(); i++){
            linearLayout = new LinearLayout(this);
            linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(this, 33));
            linearLayout.setLayoutParams(linearLayoutParams);
            macroFiberDetailsLayout.addView(linearLayout);

            barView = new View(this);
            barView.setBackgroundColor(grayBarColor);
            linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(this, 1));
            barView.setLayoutParams(linearLayoutParams);
            macroFiberDetailsLayout.addView(barView);

            textView = new TextView(this);
            textView.setTypeface(avenirNextReqularFont);
            textView.setText(macroFiberLabels.get(i));
            textView.setTextSize(12);
            textView.setTextColor(macroLabelsFontColor);
            textView.setSingleLine(true);
            textView.setPadding(PixelUtil.dpToPx(this, 12), 0, 0, 0);
            linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayoutParams.weight = 0.85f;
            linearLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(linearLayoutParams);
            linearLayout.addView(textView);

            textView = new TextView(this);
            textView.setTypeface(avenirNextReqularFont);
            textView.setText(macroFiberValues.get(i)+ "g");
            textView.setTextSize(12);
            textView.setTextColor(macroLabelsFontColor);
            textView.setGravity(Gravity.RIGHT);
            linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayoutParams.weight = 0.15f;
            linearLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(linearLayoutParams);
            linearLayout.addView(textView);
        }
        fiberTotalValueView.setText(plannerFiber + "g");

        //sugar
        for(int i = 0; i < macroSugarLabels.size(); i++){
            linearLayout = new LinearLayout(this);
            linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(this, 33));
            linearLayout.setLayoutParams(linearLayoutParams);
            macroSugarDetailsLayout.addView(linearLayout);

            barView = new View(this);
            barView.setBackgroundColor(grayBarColor);
            linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(this, 1));
            barView.setLayoutParams(linearLayoutParams);
            macroSugarDetailsLayout.addView(barView);

            textView = new TextView(this);
            textView.setTypeface(avenirNextReqularFont);
            textView.setText(macroSugarLabels.get(i));
            textView.setTextSize(12);
            textView.setTextColor(macroLabelsFontColor);
            textView.setSingleLine(true);
            textView.setPadding(PixelUtil.dpToPx(this, 12), 0, 0, 0);
            linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayoutParams.weight = 0.85f;
            linearLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(linearLayoutParams);
            linearLayout.addView(textView);

            textView = new TextView(this);
            textView.setTypeface(avenirNextReqularFont);
            textView.setText(macroSugarValues.get(i)+ "g");
            textView.setTextSize(12);
            textView.setTextColor(macroLabelsFontColor);
            textView.setGravity(Gravity.RIGHT);
            linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayoutParams.weight = 0.15f;
            linearLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(linearLayoutParams);
            linearLayout.addView(textView);
        }
        sugarTotalValueView.setText(plannerSugar + "g");

        //protein
        proteinValueView.setText(plannerProteinPercentage + "%");
        for(int i = 0; i < macroProteinLabels.size(); i++){
            linearLayout = new LinearLayout(this);
            linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(this, 33));
            linearLayout.setLayoutParams(linearLayoutParams);
            macroProteinDetailsLayout.addView(linearLayout);

            barView = new View(this);
            barView.setBackgroundColor(greenFontColor);
            linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(this, 1));
            barView.setLayoutParams(linearLayoutParams);
            macroProteinDetailsLayout.addView(barView);

            textView = new TextView(this);
            textView.setTypeface(avenirNextReqularFont);
            textView.setText(macroProteinLabels.get(i));
            textView.setTextSize(12);
            textView.setTextColor(macroLabelsFontColor);
            textView.setSingleLine(true);
            linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayoutParams.weight = 0.85f;
            linearLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(linearLayoutParams);
            linearLayout.addView(textView);

            textView = new TextView(this);
            textView.setTypeface(avenirNextReqularFont);
            textView.setText(macroProteinValues.get(i)+ "g");
            textView.setTextSize(12);
            textView.setTextColor(macroLabelsFontColor);
            textView.setGravity(Gravity.RIGHT);
            linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayoutParams.weight = 0.15f;
            linearLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(linearLayoutParams);
            linearLayout.addView(textView);
        }
        proteinTotalValueView.setText(plannerProtein + "g");

        //fat
        fatValueView.setText(plannerFatPercentage + "%");
        for(int i = 0; i < macroFatLabels.size(); i++){
            linearLayout = new LinearLayout(this);
            linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(this, 33));
            linearLayout.setLayoutParams(linearLayoutParams);
            macroFatDetailsLayout.addView(linearLayout);

            barView = new View(this);
            barView.setBackgroundColor(orangeFontColor);
            linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(this, 1));
            barView.setLayoutParams(linearLayoutParams);
            macroFatDetailsLayout.addView(barView);

            textView = new TextView(this);
            textView.setTypeface(avenirNextReqularFont);
            textView.setText(macroFatLabels.get(i));
            textView.setTextSize(12);
            textView.setTextColor(macroLabelsFontColor);
            textView.setSingleLine(true);
            linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayoutParams.weight = 0.85f;
            linearLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(linearLayoutParams);
            linearLayout.addView(textView);

            textView = new TextView(this);
            textView.setTypeface(avenirNextReqularFont);
            textView.setText(macroFatValues.get(i)+ "g");
            textView.setTextSize(12);
            textView.setTextColor(macroLabelsFontColor);
            textView.setGravity(Gravity.RIGHT);
            linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayoutParams.weight = 0.15f;
            linearLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(linearLayoutParams);
            linearLayout.addView(textView);
        }
        fatTotalValueView.setText(plannerFat + "g");

        //sat fat
        for(int i = 0; i < macroSatFatLabels.size(); i++){
            linearLayout = new LinearLayout(this);
            linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(this, 33));
            linearLayout.setLayoutParams(linearLayoutParams);
            macroSatFatDetailsLayout.addView(linearLayout);

            barView = new View(this);
            barView.setBackgroundColor(grayBarColor);
            linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(this, 1));
            barView.setLayoutParams(linearLayoutParams);
            macroSatFatDetailsLayout.addView(barView);

            textView = new TextView(this);
            textView.setTypeface(avenirNextReqularFont);
            textView.setText(macroSatFatLabels.get(i));
            textView.setTextSize(12);
            textView.setTextColor(macroLabelsFontColor);
            textView.setSingleLine(true);
            textView.setPadding(PixelUtil.dpToPx(this, 12), 0, 0, 0);
            linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayoutParams.weight = 0.85f;
            linearLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(linearLayoutParams);
            linearLayout.addView(textView);

            textView = new TextView(this);
            textView.setTypeface(avenirNextReqularFont);
            textView.setText(macroSatFatValues.get(i)+ "g");
            textView.setTextSize(12);
            textView.setTextColor(macroLabelsFontColor);
            textView.setGravity(Gravity.RIGHT);
            linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayoutParams.weight = 0.15f;
            linearLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(linearLayoutParams);
            linearLayout.addView(textView);
        }
        satfatTotalValueView.setText(plannerSatFat + "g");

        //sodium
        for(int i = 0; i < macroSodiumLabels.size(); i++){
            linearLayout = new LinearLayout(this);
            linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(this, 33));
            linearLayout.setLayoutParams(linearLayoutParams);
            macroSodiumDetailsLayout.addView(linearLayout);

            barView = new View(this);
            barView.setBackgroundColor(grayBarColor);
            linearLayoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(this, 1));
            barView.setLayoutParams(linearLayoutParams);
            macroSodiumDetailsLayout.addView(barView);

            textView = new TextView(this);
            textView.setTypeface(avenirNextReqularFont);
            textView.setText(macroSodiumLabels.get(i));
            textView.setTextSize(12);
            textView.setTextColor(macroLabelsFontColor);
            textView.setSingleLine(true);
            linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayoutParams.weight = 0.85f;
            linearLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(linearLayoutParams);
            linearLayout.addView(textView);

            textView = new TextView(this);
            textView.setTypeface(avenirNextReqularFont);
            textView.setText(macroSodiumValues.get(i)+ "mg");
            textView.setTextSize(12);
            textView.setTextColor(macroLabelsFontColor);
            textView.setGravity(Gravity.RIGHT);
            linearLayoutParams = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
            linearLayoutParams.weight = 0.15f;
            linearLayoutParams.gravity = Gravity.CENTER_VERTICAL;
            textView.setLayoutParams(linearLayoutParams);
            linearLayout.addView(textView);
        }
        sodiumTotalValueView.setText(plannerSodium + "mg");

        macroDetailsLayout.setVisibility(View.VISIBLE);
    }

}
