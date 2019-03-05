package com.sph.healthtrac.planner.addfood;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LabeledIntent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.common.HTTypefaceSpan;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HTAddFoodSelectItemDetailsActivity extends Activity {

    private static RelativeLayout mainContentLayout;
    private ScrollView scrollView;
    private LinearLayout scrollContentLayout;

    private TextView foodNameLabel;
    private TextView descriptionView;
    private TextView typeView;
    private TextView prepeffortView;
    private TextView servingsView;
    private TextView ingredientsView;
    private TextView directionsView;
    private TextView recommendedView;
    private TextView commentsView;
    private TextView caloriesView;
    private TextView proteinView;
    private TextView carbsView;
    private TextView fatView;
    private TextView satfatView;
    private TextView sugarsView;
    private TextView fiberView;
    private TextView sodiumView;

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

    private int mealItemID;

    String selectedMealName = "";
    String selectedMealType = "";
    String selectedMealPrep = "";

    String selectedMealCalories = "";
    String selectedMealProtein = "";
    String selectedMealCarbs = "";
    String selectedMealFat = "";
    String selectedMealSatFat = "";
    String selectedMealSugars = "";
    String selectedMealFiber = "";
    String selectedMealSodium = "";

    String selectedMealDescription = "";
    String selectedMealServings = "";
    String selectedMealIngredients = "";
    String selectedMealDirections = "";
    String selectedMealRecommended = "";
    String selectedMealComments = "";

    private String sharePlanSubject = "";
    private String sharePlanContent = "";
    private String sharePlanContentHTML = "";

    Typeface avenirNextRegularFont;
    Typeface avenirNextDemiBoldFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfood_selectitem_details);

        mealItemID = getIntent().getIntExtra("mealItemID", 0);

        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        scrollView = (ScrollView) findViewById(R.id.scrollView);
        scrollContentLayout = (LinearLayout) findViewById(R.id.contentLayout);

        foodNameLabel = (TextView) findViewById(R.id.foodNameLabel);
        descriptionView = (TextView) findViewById(R.id.descriptionView);
        typeView = (TextView) findViewById(R.id.typeView);
        prepeffortView = (TextView) findViewById(R.id.prepeffortView);
        servingsView = (TextView) findViewById(R.id.servingsView);
        ingredientsView = (TextView) findViewById(R.id.ingredientsView);
        directionsView = (TextView) findViewById(R.id.directionsView);
        recommendedView = (TextView) findViewById(R.id.recommendedView);
        commentsView = (TextView) findViewById(R.id.commentsView);
        caloriesView = (TextView) findViewById(R.id.caloriesView);
        proteinView = (TextView) findViewById(R.id.proteinView);
        carbsView = (TextView) findViewById(R.id.carbsView);
        fatView = (TextView) findViewById(R.id.fatView);
        satfatView = (TextView) findViewById(R.id.satfatView);
        sugarsView = (TextView) findViewById(R.id.sugarsView);
        fiberView = (TextView) findViewById(R.id.fiberView);
        sodiumView = (TextView) findViewById(R.id.sodiumView);

        avenirNextRegularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        avenirNextDemiBoldFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-DemiBold.ttf");

        foodNameLabel.setTypeface(avenirNextDemiBoldFont);
        descriptionView.setTypeface(avenirNextRegularFont);

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
        View mActionBar = HTActionBar.getActionBar(this, "Food Item Details", "leftArrow", "deleteMark");

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

        // right share button
        ((ImageView) mActionBar.findViewById(R.id.rightCheckAction)).setImageResource(R.drawable.ht_nav_bar_button_share);
        mActionBar.findViewById(R.id.rightCheckAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shareContent();
            }
        });

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            if (mealItemID != 0) {
                getMealItem();
            } else {
                setResult(1);
                finish();
            }
        }
    }

    private void getMealItem() {
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_food_item_details"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("WhichID", mealItemID + ""));

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();
                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);

                    NodeList error = doc.getElementsByTagName("error");
                    NodeList nodes = doc.getElementsByTagName("food_item_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTAddFoodSelectItemDetailsActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();
                                String tempString = "";

                                if (XMLFunctions.tagExists(e2, "meal_item_name")) {
                                    tempString = XMLFunctions.getValue(e2, "meal_item_name");
                                    selectedMealName = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "meal_item_type")) {
                                    tempString = XMLFunctions.getValue(e2, "meal_item_type");
                                    tempString = htGlobals.cleanStringAfterReceiving(tempString);
                                    if ("SN".equals(tempString))
                                        selectedMealType = "Snack";
                                    else if ("AM".equals(tempString))
                                        selectedMealType = "AM Meal";
                                    else if ("PM".equals(tempString))
                                        selectedMealType = "PM Meal";
                                    else if ("Other".equals(tempString))
                                        selectedMealType = "Other";
                                }

                                if (XMLFunctions.tagExists(e2, "meal_item_prep")) {
                                    tempString = XMLFunctions.getValue(e2, "meal_item_prep");
                                    tempString = htGlobals.cleanStringAfterReceiving(tempString);
                                    if ("R".equals(tempString))
                                        selectedMealPrep = "Ready to Eat";
                                    else if ("L".equals(tempString))
                                        selectedMealPrep = "Low";
                                    else if ("M".equals(tempString))
                                        selectedMealPrep = "Medium";
                                    else if ("H".equals(tempString))
                                        selectedMealPrep = "High";
                                }

                                if (XMLFunctions.tagExists(e2, "meal_item_calories")) {
                                    tempString = XMLFunctions.getValue(e2, "meal_item_calories");
                                    selectedMealCalories = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "meal_item_protein")) {
                                    tempString = XMLFunctions.getValue(e2, "meal_item_protein");
                                    selectedMealProtein = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "meal_item_carbs")) {
                                    tempString = XMLFunctions.getValue(e2, "meal_item_carbs");
                                    selectedMealCarbs = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "meal_item_fat")) {
                                    tempString = XMLFunctions.getValue(e2, "meal_item_fat");
                                    selectedMealFat = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "meal_item_sat_fat")) {
                                    tempString = XMLFunctions.getValue(e2, "meal_item_sat_fat");
                                    selectedMealSatFat = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "meal_item_sugar")) {
                                    tempString = XMLFunctions.getValue(e2, "meal_item_sugar");
                                    selectedMealSugars = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "meal_item_fiber")) {
                                    tempString = XMLFunctions.getValue(e2, "meal_item_fiber");
                                    selectedMealFiber = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "meal_item_sodium")) {
                                    tempString = XMLFunctions.getValue(e2, "meal_item_sodium");
                                    selectedMealSodium = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "meal_item_description")) {
                                    tempString = XMLFunctions.getValue(e2, "meal_item_description");
                                    selectedMealDescription = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "meal_item_servings")) {
                                    tempString = XMLFunctions.getValue(e2, "meal_item_servings");
                                    selectedMealServings = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "meal_item_menu")) {
                                    tempString = XMLFunctions.getValue(e2, "meal_item_menu");
                                    selectedMealIngredients = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "meal_item_directions")) {
                                    tempString = XMLFunctions.getValue(e2, "meal_item_directions");
                                    selectedMealDirections = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "meal_item_servewith")) {
                                    tempString = XMLFunctions.getValue(e2, "meal_item_servewith");
                                    selectedMealRecommended = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "meal_item_nutrition")) {
                                    tempString = XMLFunctions.getValue(e2, "meal_item_nutrition");
                                    selectedMealComments = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "share_plan_subject")) {
                                    tempString = XMLFunctions.getValue(e2, "share_plan_subject");
                                    sharePlanSubject = HTGlobals.getInstance().cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "share_plan_content")) {
                                    tempString = XMLFunctions.getValue(e2, "share_plan_content");
                                    sharePlanContent = HTGlobals.getInstance().cleanStringAfterReceiving(tempString);
                                    sharePlanContent = sharePlanContent.replace("<br>", "\n");
                                }

                                if (XMLFunctions.tagExists(e2, "share_plan_content_html")) {
                                    tempString = XMLFunctions.getValue(e2, "share_plan_content_html");
                                    sharePlanContentHTML = HTGlobals.getInstance().cleanStringAfterReceiving(tempString);
                                }

                                showMealItem();

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

//                                    toast = HTToast.showToast(HTAddFoodSelectItemDetailsActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTAddFoodSelectItemDetailsActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showMealItem() {
        scrollView.setVisibility(View.VISIBLE);
        foodNameLabel.setText(selectedMealName);

        //description
        if ("".equals(selectedMealDescription)) {
            descriptionView.setVisibility(View.GONE);
        } else {
            descriptionView.setVisibility(View.VISIBLE);
            descriptionView.setText(selectedMealDescription);
        }

        String tempString = "";
        //type
        if ("".equals(selectedMealType)) {
            typeView.setVisibility(View.GONE);
        } else {
            typeView.setVisibility(View.VISIBLE);
            tempString = "Type - " + selectedMealType;
            SpannableString spannableString = new SpannableString(tempString);
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextDemiBoldFont), 0, 4, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextRegularFont), 4, tempString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            typeView.setText(spannableString);
        }

        // prep effort
        if ("".equals(selectedMealPrep)) {
            prepeffortView.setVisibility(View.GONE);
        } else {
            prepeffortView.setVisibility(View.VISIBLE);
            tempString = "Preparation Effort - " + selectedMealPrep;
            SpannableString spannableString = new SpannableString(tempString);
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextDemiBoldFont), 0, 18, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextRegularFont), 18, tempString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            prepeffortView.setText(spannableString);
        }

        //servings
        if ("".equals(selectedMealServings)) {
            servingsView.setVisibility(View.GONE);
        } else {
            servingsView.setVisibility(View.VISIBLE);
            tempString = "Servings - " + selectedMealServings;
            SpannableString spannableString = new SpannableString(tempString);
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextDemiBoldFont), 0, 8, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextRegularFont), 8, tempString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            servingsView.setText(spannableString);
        }

        //ingredients
        if ("".equals(selectedMealIngredients)) {
            ingredientsView.setVisibility(View.GONE);
        } else {
            ingredientsView.setVisibility(View.VISIBLE);
            tempString = "Ingredients\n" + selectedMealIngredients;
            SpannableString spannableString = new SpannableString(tempString);
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextDemiBoldFont), 0, 11, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextRegularFont), 11, tempString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            ingredientsView.setText(spannableString);
        }

        //directions
        if ("".equals(selectedMealDirections)) {
            directionsView.setVisibility(View.GONE);
        } else {
            directionsView.setVisibility(View.VISIBLE);
            tempString = "Directions\n" + selectedMealDirections;
            SpannableString spannableString = new SpannableString(tempString);
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextDemiBoldFont), 0, 10, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextRegularFont), 10, tempString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            directionsView.setText(spannableString);
        }

        //recommended
        if ("".equals(selectedMealRecommended)) {
            recommendedView.setVisibility(View.GONE);
        } else {
            recommendedView.setVisibility(View.VISIBLE);
            tempString = "Recommended With\n" + selectedMealRecommended;
            SpannableString spannableString = new SpannableString(tempString);
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextDemiBoldFont), 0, 16, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextRegularFont), 16, tempString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            recommendedView.setText(spannableString);
        }

        //comments
        if ("".equals(selectedMealComments)) {
            commentsView.setVisibility(View.GONE);
        } else {
            commentsView.setVisibility(View.VISIBLE);
            tempString = "Comments\n" + selectedMealComments;
            SpannableString spannableString = new SpannableString(tempString);
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextDemiBoldFont), 0, 8, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextRegularFont), 8, tempString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            commentsView.setText(spannableString);
        }

        //calories
        if ("".equals(selectedMealCalories)) {
            caloriesView.setVisibility(View.GONE);
        } else {
            caloriesView.setVisibility(View.VISIBLE);
            tempString = "Calories - " + selectedMealCalories;
            SpannableString spannableString = new SpannableString(tempString);
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextDemiBoldFont), 0, 8, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextRegularFont), 8, tempString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            caloriesView.setText(spannableString);
        }

        //protein
        if ("".equals(selectedMealProtein)) {
            selectedMealProtein = "0";
        }

        tempString = "Protein - " + selectedMealProtein + "g";
        SpannableString spannableString = new SpannableString(tempString);
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextDemiBoldFont), 0, 7, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextRegularFont), 7, tempString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        proteinView.setText(spannableString);

        //carbs
        if ("".equals(selectedMealCarbs)) {
            selectedMealCarbs = "0";
        }

        tempString = "Carbohydrates - " + selectedMealCarbs + "g";
        spannableString = new SpannableString(tempString);
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextDemiBoldFont), 0, 13, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextRegularFont), 13, tempString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        carbsView.setText(spannableString);

        //fat
        if ("".equals(selectedMealFat)) {
            selectedMealFat = "0";
        }

        tempString = "Fat - " + selectedMealFat + "g";
        spannableString = new SpannableString(tempString);
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextDemiBoldFont), 0, 3, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextRegularFont), 3, tempString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        fatView.setText(spannableString);

        //sat fat
        if ("".equals(selectedMealSatFat)) {
            selectedMealSatFat = "0";
        }
        tempString = "Sat Fat - " + selectedMealSatFat + "g";
        spannableString = new SpannableString(tempString);
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextDemiBoldFont), 0, 7, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextRegularFont), 7, tempString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        satfatView.setText(spannableString);

        //sugars
        if ("".equals(selectedMealSugars)) {
            selectedMealSugars = "0";
        }
        tempString = "Sugars - " + selectedMealSugars + "g";
        spannableString = new SpannableString(tempString);
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextDemiBoldFont), 0, 6, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextRegularFont), 6, tempString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        sugarsView.setText(spannableString);

        //fiber
        if ("".equals(selectedMealFiber)) {
            selectedMealFiber = "0";
        }
        tempString = "Fiber - " + selectedMealFiber + "g";
        spannableString = new SpannableString(tempString);
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextDemiBoldFont), 0, 5, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextRegularFont), 5, tempString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        fiberView.setText(spannableString);

        //sodium
        if ("".equals(selectedMealSodium)) {
            selectedMealSodium = "0";
        }
        tempString = "Sodium - " + selectedMealSodium + "mg";
        spannableString = new SpannableString(tempString);
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextDemiBoldFont), 0, 6, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextRegularFont), 6, tempString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        sodiumView.setText(spannableString);

    }

    private void shareContent() {
        Intent shareIntent = new Intent(
                android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/*");
        shareIntent.putExtra(
                android.content.Intent.EXTRA_SUBJECT, sharePlanSubject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharePlanContent);
        shareIntent.putExtra(Intent.EXTRA_HTML_TEXT, sharePlanContentHTML);
        startActivityForResult(Intent.createChooser(shareIntent, "Share Food Item"), 100);

    }
}
