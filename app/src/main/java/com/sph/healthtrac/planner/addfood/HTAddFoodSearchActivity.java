package com.sph.healthtrac.planner.addfood;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.util.DisplayMetrics;
import android.view.View;
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
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.common.HTTypefaceSpan;
import com.sph.healthtrac.common.PixelUtil;
import com.sph.healthtrac.common.RangeSeekBar;
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

public class HTAddFoodSearchActivity extends Activity implements View.OnClickListener {

    private static RelativeLayout mainContentLayout;
    private ScrollView scrollViewAddFoodSearch;
    private TextView caloriesLabel;
    private RangeSeekBar caloriesSeekBar;
    private RangeSeekBar proteinSeekBar;
    private RangeSeekBar carbsSeekBar;
    private RangeSeekBar netcarbsSeekBar;
    private RangeSeekBar fatSeekBar;
    private RangeSeekBar satfatSeekBar;
    private RelativeLayout additionalFieldsLayout;
    private LinearLayout productsOptionLayout;
    private TextView additionalLabel;
    private ImageView additionalIcon;
    private LinearLayout proteinLayout;
    private LinearLayout carbsLayout;
    private LinearLayout netcarbsLayout;
    private LinearLayout fatLayout;
    private LinearLayout satfatLayout;
    private TextView proteinLabel;
    private TextView carbsLabel;
    private TextView netcarbsLabel;
    private TextView fatLabel;
    private TextView satfatLabel;

    private TextView typeLabel;
    private TextView preparationLabel;
    private TextView productsLabel;
    private TextView keywordsLabel;

    private ImageView snackCheck;
    private ImageView amMealCheck;
    private ImageView pmMealCheck;
    private ImageView otherCheck;
    private ImageView readyCheck;
    private ImageView lowCheck;
    private ImageView mediumCheck;
    private ImageView highCheck;
    private ImageView includeOption;
    private ImageView onlyProductsOption;
    private ImageView noProductsOption;

    private TextView snackLabel;
    private TextView amMealLabel;
    private TextView pmMealLabel;
    private TextView otherLabel;
    private TextView readyToEatLabel;
    private TextView lowLabel;
    private TextView mediumLabel;
    private TextView highLabel;
    private TextView includeLabel;
    private TextView onlyProductsLabel;
    private TextView noProductsLabel;

    private EditText keywordsEdit;

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

    private boolean showProteinSeekbar;
    private boolean showCarbsSeekbar;
    private boolean showNetCarbsSeekbar;
    private boolean showFatSeekbar;
    private boolean showSatFatSeekbar;
    private boolean showSearchProducts;
    private boolean showAdditionalFields;

    private boolean typeCheckboxSnackChecked;
    private boolean typeCheckboxAMChecked;
    private boolean typeCheckboxPMChecked;
    private boolean typeCheckboxOtherChecked;
    private boolean prepCheckboxRTEChecked;
    private boolean prepCheckboxLowChecked;
    private boolean prepCheckboxMediumChecked;
    private boolean prepCheckboxHighChecked;

    private String addFoodCategory;
    private String productsSearchSelection = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfood_search);

        String title = "";
        addFoodCategory = getIntent().getStringExtra("addFoodCategory");
        if ("favorites".equals(addFoodCategory))
            title = "My Favorites";
        else if ("recommended".equals(addFoodCategory))
            title = "Recommended";
        else if ("general".equals(addFoodCategory))
            title = "General Food Item";

        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        scrollViewAddFoodSearch = (ScrollView) findViewById(R.id.scrollViewAddFoodSearch);
        scrollViewAddFoodSearch.setVisibility(View.INVISIBLE);
        caloriesLabel = (TextView) findViewById(R.id.caloriesLabel);
        caloriesSeekBar = (RangeSeekBar) findViewById(R.id.caloriesSeekBar);
        proteinSeekBar = (RangeSeekBar) findViewById(R.id.proteinSeekBar);
        carbsSeekBar = (RangeSeekBar) findViewById(R.id.carbsSeekBar);
        netcarbsSeekBar = (RangeSeekBar) findViewById(R.id.netcarbsSeekBar);
        fatSeekBar = (RangeSeekBar) findViewById(R.id.fatSeekBar);
        satfatSeekBar = (RangeSeekBar) findViewById(R.id.satfatSeekBar);
        additionalFieldsLayout = (RelativeLayout) findViewById(R.id.additionalFieldsLayout);
        productsOptionLayout = (LinearLayout) findViewById(R.id.productsOptionLayout);
        additionalLabel = (TextView) findViewById(R.id.additionalFieldLabel);
        additionalIcon = (ImageView) findViewById(R.id.additionalFieldIcon);
        proteinLayout = (LinearLayout) findViewById(R.id.proteinLayout);
        carbsLayout = (LinearLayout) findViewById(R.id.carbsLayout);
        netcarbsLayout = (LinearLayout) findViewById(R.id.netcarbsLayout);
        fatLayout = (LinearLayout) findViewById(R.id.fatLayout);
        satfatLayout = (LinearLayout) findViewById(R.id.satfatLayout);
        proteinLabel = (TextView) findViewById(R.id.proteinLabel);
        carbsLabel = (TextView) findViewById(R.id.carbsLabel);
        netcarbsLabel = (TextView) findViewById(R.id.netcarbsLabel);
        fatLabel = (TextView) findViewById(R.id.fatLabel);
        satfatLabel = (TextView) findViewById(R.id.satfatLabel);

        typeLabel = (TextView) findViewById(R.id.typeLabel);
        preparationLabel = (TextView) findViewById(R.id.preparationLabel);
        productsLabel = (TextView) findViewById(R.id.productsLabel);
        keywordsLabel = (TextView) findViewById(R.id.keywordLabel);

        snackCheck = (ImageView) findViewById(R.id.snackCheck);
        amMealCheck = (ImageView) findViewById(R.id.am_mealCheck);
        pmMealCheck = (ImageView) findViewById(R.id.pm_mealCheck);
        otherCheck = (ImageView) findViewById(R.id.otherCheck);
        readyCheck = (ImageView) findViewById(R.id.readyCheck);
        lowCheck = (ImageView) findViewById(R.id.lowCheck);
        mediumCheck = (ImageView) findViewById(R.id.mediumCheck);
        highCheck = (ImageView) findViewById(R.id.highCheck);
        includeOption = (ImageView) findViewById(R.id.includeOption);
        onlyProductsOption = (ImageView) findViewById(R.id.onlyProductsOption);
        noProductsOption = (ImageView) findViewById(R.id.noProductsOption);

        snackLabel = (TextView) findViewById(R.id.snackLabel);
        amMealLabel = (TextView) findViewById(R.id.am_mealLabel);
        pmMealLabel = (TextView) findViewById(R.id.pm_mealLabel);
        otherLabel = (TextView) findViewById(R.id.otherLabel);
        readyToEatLabel = (TextView) findViewById(R.id.readyLabel);
        lowLabel = (TextView) findViewById(R.id.lowLabel);
        mediumLabel = (TextView) findViewById(R.id.mediumLabel);
        highLabel = (TextView) findViewById(R.id.highLabel);
        includeLabel = (TextView) findViewById(R.id.includeLabel);
        onlyProductsLabel = (TextView) findViewById(R.id.onlyProductsLabel);
        noProductsLabel = (TextView) findViewById(R.id.noProductsLabel);

        keywordsEdit = (EditText) findViewById(R.id.keywordsEdit);

        Typeface avenirLightFont = Typeface.createFromAsset(getAssets(), "fonts/Avenir-Light.ttf");
        Typeface avenirNextRegularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        Typeface avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");

        caloriesLabel.setTypeface(avenirLightFont);
        additionalLabel.setTypeface(avenirNextRegularFont);
        proteinLabel.setTypeface(avenirNextMediumFont);
        carbsLabel.setTypeface(avenirNextMediumFont);
        netcarbsLabel.setTypeface(avenirNextMediumFont);
        fatLabel.setTypeface(avenirNextMediumFont);
        satfatLabel.setTypeface(avenirNextMediumFont);

        SpannableString spannableString = new SpannableString("Protein (g)");
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextMediumFont, PixelUtil.dpToPx(this, 10)), 7, 11, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        proteinLabel.setText(spannableString);

        spannableString = new SpannableString("Carbs (g)");
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextMediumFont, PixelUtil.dpToPx(this, 10)), 5, 9, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        carbsLabel.setText(spannableString);

        spannableString = new SpannableString("Net Carbs (g)");
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextMediumFont, PixelUtil.dpToPx(this, 10)), 9, 13, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        netcarbsLabel.setText(spannableString);

        spannableString = new SpannableString("Fat (g)");
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextMediumFont, PixelUtil.dpToPx(this, 10)), 3, 7, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        fatLabel.setText(spannableString);

        spannableString = new SpannableString("Sat Fat (g)");
        spannableString.setSpan(new HTTypefaceSpan("", avenirNextMediumFont, PixelUtil.dpToPx(this, 10)), 7, 11, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        satfatLabel.setText(spannableString);

        typeLabel.setTypeface(avenirNextRegularFont);
        preparationLabel.setTypeface(avenirNextRegularFont);
        productsLabel.setTypeface(avenirNextRegularFont);
        keywordsLabel.setTypeface(avenirNextRegularFont);

        snackLabel.setTypeface(avenirNextMediumFont);
        amMealLabel.setTypeface(avenirNextMediumFont);
        pmMealLabel.setTypeface(avenirNextMediumFont);
        otherLabel.setTypeface(avenirNextMediumFont);
        readyToEatLabel.setTypeface(avenirNextMediumFont);
        lowLabel.setTypeface(avenirNextMediumFont);
        mediumLabel.setTypeface(avenirNextMediumFont);
        highLabel.setTypeface(avenirNextMediumFont);
        includeLabel.setTypeface(avenirNextMediumFont);
        onlyProductsLabel.setTypeface(avenirNextMediumFont);
        noProductsLabel.setTypeface(avenirNextMediumFont);

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
            getSearchFields();
        }

        snackCheck.setOnClickListener(this);
        amMealCheck.setOnClickListener(this);
        pmMealCheck.setOnClickListener(this);
        otherCheck.setOnClickListener(this);
        readyCheck.setOnClickListener(this);
        lowCheck.setOnClickListener(this);
        mediumCheck.setOnClickListener(this);
        highCheck.setOnClickListener(this);

        includeOption.setOnClickListener(this);
        onlyProductsOption.setOnClickListener(this);
        noProductsOption.setOnClickListener(this);

    }

    private void getSearchFields() {

        showProteinSeekbar = false;
        showCarbsSeekbar = false;
        showNetCarbsSeekbar = false;
        showFatSeekbar = false;
        showSatFatSeekbar = false;
        showSearchProducts = false;
        showAdditionalFields = false;

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_food_search_fields"));
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
                    NodeList nodes = doc.getElementsByTagName("add_food_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTAddFoodSearchActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                String tempString = "";

                                //Show Protein
                                if (XMLFunctions.tagExists(e2, "search_protein")) {
                                    tempString = XMLFunctions.getValue(e2, "search_protein");
                                    if ("1".equals(tempString))
                                        showProteinSeekbar = true;
                                }

                                //Show Carbs
                                if (XMLFunctions.tagExists(e2, "search_carbs")) {
                                    tempString = XMLFunctions.getValue(e2, "search_carbs");
                                    if ("1".equals(tempString))
                                        showCarbsSeekbar = true;
                                }

                                //Show Net Carbs
                                if (XMLFunctions.tagExists(e2, "search_net_carbs")) {
                                    tempString = XMLFunctions.getValue(e2, "search_net_carbs");
                                    if ("1".equals(tempString))
                                        showNetCarbsSeekbar = true;
                                }

                                //Show Fat
                                if (XMLFunctions.tagExists(e2, "search_fat")) {
                                    tempString = XMLFunctions.getValue(e2, "search_fat");
                                    if ("1".equals(tempString))
                                        showFatSeekbar = true;
                                }

                                //Show Sat Fat
                                if (XMLFunctions.tagExists(e2, "search_sat_fat")) {
                                    tempString = XMLFunctions.getValue(e2, "search_sat_fat");
                                    if ("1".equals(tempString))
                                        showSatFatSeekbar = true;
                                }

                                //Show Products
                                if (XMLFunctions.tagExists(e2, "search_products")) {
                                    tempString = XMLFunctions.getValue(e2, "search_products");
                                    if ("1".equals(tempString))
                                        showSearchProducts = true;
                                }

                                showSearchFields();
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

//                                    toast = HTToast.showToast(HTAddFoodSearchActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTAddFoodSearchActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showSearchFields() {
        scrollViewAddFoodSearch.setVisibility(View.VISIBLE);

        if ("favorites".equals(addFoodCategory) || "general".equals(addFoodCategory))
            showSearchProducts = false;

        if (showSearchProducts)
            productsOptionLayout.setVisibility(View.VISIBLE);
        else
            productsOptionLayout.setVisibility(View.GONE);

        if (showAdditionalFields) {
            if (showProteinSeekbar)
                proteinLayout.setVisibility(View.VISIBLE);
            else
                proteinLayout.setVisibility(View.GONE);
            if (showCarbsSeekbar)
                carbsLayout.setVisibility(View.VISIBLE);
            else
                carbsLayout.setVisibility(View.VISIBLE);
            if (showNetCarbsSeekbar)
                netcarbsLayout.setVisibility(View.VISIBLE);
            else
                netcarbsLayout.setVisibility(View.GONE);
            if (showFatSeekbar)
                fatLayout.setVisibility(View.VISIBLE);
            else
                fatLayout.setVisibility(View.GONE);
            if (showSatFatSeekbar)
                satfatLayout.setVisibility(View.VISIBLE);
            else
                satfatLayout.setVisibility(View.GONE);
        }

        additionalFieldsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdditionalFields = !showAdditionalFields;
                if (showAdditionalFields) {
                    additionalIcon.setImageResource(R.drawable.ht_expand_content_minus);
                    if (showProteinSeekbar)
                        proteinLayout.setVisibility(View.VISIBLE);
                    else
                        proteinLayout.setVisibility(View.GONE);
                    if (showCarbsSeekbar)
                        carbsLayout.setVisibility(View.VISIBLE);
                    else
                        carbsLayout.setVisibility(View.VISIBLE);
                    if (showNetCarbsSeekbar)
                        netcarbsLayout.setVisibility(View.VISIBLE);
                    else
                        netcarbsLayout.setVisibility(View.GONE);
                    if (showFatSeekbar)
                        fatLayout.setVisibility(View.VISIBLE);
                    else
                        fatLayout.setVisibility(View.GONE);
                    if (showSatFatSeekbar)
                        satfatLayout.setVisibility(View.VISIBLE);
                    else
                        satfatLayout.setVisibility(View.GONE);
                } else {
                    additionalIcon.setImageResource(R.drawable.ht_expand_content_plus);
                    proteinLayout.setVisibility(View.GONE);
                    carbsLayout.setVisibility(View.GONE);
                    netcarbsLayout.setVisibility(View.GONE);
                    fatLayout.setVisibility(View.GONE);
                    satfatLayout.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v == snackCheck) {
            typeCheckboxSnackChecked = !typeCheckboxSnackChecked;
            if (typeCheckboxSnackChecked)
                snackCheck.setImageResource(R.drawable.ht_check_on_green);
            else
                snackCheck.setImageResource(R.drawable.ht_check_off_green);
        } else if (v == amMealCheck) {
            typeCheckboxAMChecked = !typeCheckboxAMChecked;
            if (typeCheckboxAMChecked)
                amMealCheck.setImageResource(R.drawable.ht_check_on_green);
            else
                amMealCheck.setImageResource(R.drawable.ht_check_off_green);
        } else if (v == pmMealCheck) {
            typeCheckboxPMChecked = !typeCheckboxPMChecked;
            if (typeCheckboxPMChecked)
                pmMealCheck.setImageResource(R.drawable.ht_check_on_green);
            else
                pmMealCheck.setImageResource(R.drawable.ht_check_off_green);
        } else if (v == otherCheck) {
            typeCheckboxOtherChecked = !typeCheckboxOtherChecked;
            if (typeCheckboxOtherChecked)
                otherCheck.setImageResource(R.drawable.ht_check_on_green);
            else
                otherCheck.setImageResource(R.drawable.ht_check_off_green);
        } else if (v == readyCheck) {
            prepCheckboxRTEChecked = !prepCheckboxRTEChecked;
            if (prepCheckboxRTEChecked)
                readyCheck.setImageResource(R.drawable.ht_check_on_green);
            else
                readyCheck.setImageResource(R.drawable.ht_check_off_green);
        } else if (v == lowCheck) {
            prepCheckboxLowChecked = !prepCheckboxLowChecked;
            if (prepCheckboxLowChecked)
                lowCheck.setImageResource(R.drawable.ht_check_on_green);
            else
                lowCheck.setImageResource(R.drawable.ht_check_off_green);
        } else if (v == mediumCheck) {
            prepCheckboxMediumChecked = !prepCheckboxMediumChecked;
            if (prepCheckboxMediumChecked)
                mediumCheck.setImageResource(R.drawable.ht_check_on_green);
            else
                mediumCheck.setImageResource(R.drawable.ht_check_off_green);
        } else if (v == highCheck) {
            prepCheckboxHighChecked = !prepCheckboxHighChecked;
            if (prepCheckboxHighChecked)
                highCheck.setImageResource(R.drawable.ht_check_on_green);
            else
                highCheck.setImageResource(R.drawable.ht_check_off_green);
        } else if (v == includeOption) {
            includeOption.setImageResource(R.drawable.ht_color_button_green_on);
            onlyProductsOption.setImageResource(R.drawable.ht_color_button_green_off);
            noProductsOption.setImageResource(R.drawable.ht_color_button_green_off);
            productsSearchSelection = "Y";
        } else if (v == onlyProductsOption) {
            includeOption.setImageResource(R.drawable.ht_color_button_green_off);
            onlyProductsOption.setImageResource(R.drawable.ht_color_button_green_on);
            noProductsOption.setImageResource(R.drawable.ht_color_button_green_off);
            productsSearchSelection = "O";
        } else if (v == noProductsOption) {
            includeOption.setImageResource(R.drawable.ht_color_button_green_off);
            onlyProductsOption.setImageResource(R.drawable.ht_color_button_green_off);
            noProductsOption.setImageResource(R.drawable.ht_color_button_green_on);
            productsSearchSelection = "N";
        }
    }

    private void checkButtonPressed() {

        imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);

        String typeString = "";
        String prepString = "";

        //type
        if (typeCheckboxSnackChecked)
            typeString += "SN,";

        if (typeCheckboxAMChecked)
            typeString += "AM,";

        if (typeCheckboxPMChecked)
            typeString += "PM,";

        if (typeCheckboxOtherChecked)
            typeString += "Other,";

        if (typeString.length() > 0)
            typeString = typeString.substring(0, typeString.length() - 1);

        //prep
        if (prepCheckboxRTEChecked)
            prepString += "R,";

        if (prepCheckboxLowChecked)
            prepString += "L,";

        if (prepCheckboxMediumChecked)
            prepString += "M,";

        if (prepCheckboxHighChecked)
            prepString += "H,";

        if (prepString.length() > 0)
            prepString = prepString.substring(0, prepString.length() - 1);

        String searchKey = "";
        try {
            searchKey = URLEncoder.encode(keywordsEdit.getText().toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
        }

        String addFoodSearchString = "WhichCategory=" + addFoodCategory
                + "&calories=" + caloriesSeekBar.getSelectedMinValue().intValue() + ";" + caloriesSeekBar.getSelectedMaxValue().intValue()
                + "&protein=" + proteinSeekBar.getSelectedMinValue().intValue() + ";" + proteinSeekBar.getSelectedMaxValue().intValue()
                + "&carbs=" + carbsSeekBar.getSelectedMinValue().intValue() + ";" + carbsSeekBar.getSelectedMaxValue().intValue()
                + "&net_carbs=" + netcarbsSeekBar.getSelectedMinValue().intValue() + ";" + netcarbsSeekBar.getSelectedMaxValue().intValue()
                + "&fat=" + fatSeekBar.getSelectedMinValue().intValue() + ";" + fatSeekBar.getSelectedMaxValue().intValue()
                + "&sat_fat=" + satfatSeekBar.getSelectedMinValue().intValue() + ";" + satfatSeekBar.getSelectedMaxValue().intValue()
                + "&type=" + typeString + "&prep=" + prepString + "&products=" + productsSearchSelection + "&search=" + searchKey + "&template=false";

        Intent intent = new Intent(this, HTAddFoodSearchResultsActivity.class);
        intent.putExtra("addFoodCategory", addFoodCategory);
        intent.putExtra("addFoodSearchString", addFoodSearchString);
        startActivityForResult(intent, 1);
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

        getSearchFields();
    }

}
