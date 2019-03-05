package com.sph.healthtrac.planner.createfavorites;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
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

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HTCreateFavoritesActivity extends Activity {
    private static RelativeLayout mainContentLayout;
    private LinearLayout headLayout;
    private EditText foodDescEdit;

    private ScrollView scrollView;
    private LinearLayout scrollContentLayout;

    private TextView typeLabel;
    private TextView preparationLabel;
    private RelativeLayout addtionalLayout;
    private TextView additionalLabel;
    private ImageView additionalIcon;

    private ImageView snackOption;
    private ImageView amMealOption;
    private ImageView pmMealOption;
    private ImageView otherOption;
    private ImageView readyOption;
    private ImageView lowOption;
    private ImageView mediumOption;
    private ImageView highOption;
    private TextView snackLabel;
    private TextView amMealLabel;
    private TextView pmMealLabel;
    private TextView otherLabel;
    private TextView readyToEatLabel;
    private TextView lowLabel;
    private TextView mediumLabel;
    private TextView highLabel;

    private LinearLayout additionalFieldListLayout;
    private LinearLayout servingsLayout;
    private LinearLayout descriptionLayout;
    private LinearLayout ingredientsLayout;
    private LinearLayout directionsLayout;
    private LinearLayout recommendedLayout;
    private LinearLayout commentsLayout;
    private TextView servingsLabel;
    private TextView descriptionLabel;
    private TextView ingredientsLabel;
    private TextView directionsLabel;
    private TextView recommendedLabel;
    private TextView commentsLabel;
    private EditText servingsEdit;
    private EditText descriptionEdit;
    private EditText ingredientsEdit;
    private EditText directionsEdit;
    private EditText recommendedEdit;
    private EditText commentsEdit;

    private TextView caloriesLabel;
    private TextView proteinLabel;
    private TextView carbsLabel;
    private TextView fatLabel;
    private TextView satfatLabel;
    private TextView sugarsLabel;
    private TextView fiberLabel;
    private TextView sodiumLabel;
    private EditText caloriesEdit;
    private EditText proteinEdit;
    private EditText carbsEdit;
    private EditText fatEdit;
    private EditText satfatEdit;
    private EditText sugarsEdit;
    private EditText fiberEdit;
    private EditText sodiumEdit;

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

    String selectedFavoriteRelaunchItem;
    int relaunchItemID;

    String selectedFavoriteName;
    String selectedFavoriteType;
    String selectedFavoritePrep;

    String selectedFavoriteCalories;
    String selectedFavoriteProtein;
    String selectedFavoriteCarbs;
    String selectedFavoriteFat;
    String selectedFavoriteSatFat;
    String selectedFavoriteSugars;
    String selectedFavoriteFiber;
    String selectedFavoriteSodium;

    String selectedFavoriteDescription;
    String selectedFavoriteServings;
    String selectedFavoriteIngredients;
    String selectedFavoriteDirections;
    String selectedFavoriteRecommended;
    String selectedFavoriteComments;

    boolean typeSnackChecked;
    boolean typeAMChecked;
    boolean typePMChecked;
    boolean typeOtherChecked;
    boolean prepRTEChecked;
    boolean prepLowChecked;
    boolean prepMediumChecked;
    boolean prepHighChecked;

    boolean showAdditionalFields;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createfavorites);

        selectedFavoriteRelaunchItem = getIntent().getStringExtra("selectedFavoriteRelaunchItem");
        relaunchItemID = getIntent().getIntExtra("relaunchItemID", 0);

        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        headLayout = (LinearLayout) findViewById(R.id.headLayout);
        foodDescEdit = (EditText) findViewById(R.id.foodDescEdit);

        scrollView = (ScrollView) findViewById(R.id.scrollViewCreateFavorites);
        scrollContentLayout = (LinearLayout) findViewById(R.id.contentLayout);

        headLayout.setVisibility(View.GONE);
        scrollView.setVisibility(View.GONE);

        typeLabel = (TextView) findViewById(R.id.typeLabel);
        preparationLabel = (TextView) findViewById(R.id.preparationLabel);
        addtionalLayout = (RelativeLayout) findViewById(R.id.additionalFieldsLayout);
        additionalLabel = (TextView) findViewById(R.id.additionalFieldLabel);
        additionalIcon = (ImageView) findViewById(R.id.additionalFieldIcon);

        snackOption = (ImageView) findViewById(R.id.snackOption);
        amMealOption = (ImageView) findViewById(R.id.am_mealOption);
        pmMealOption = (ImageView) findViewById(R.id.pm_mealOption);
        otherOption = (ImageView) findViewById(R.id.otherOption);
        readyOption = (ImageView) findViewById(R.id.readyOption);
        lowOption = (ImageView) findViewById(R.id.lowOption);
        mediumOption = (ImageView) findViewById(R.id.mediumOption);
        highOption = (ImageView) findViewById(R.id.highOption);
        snackLabel = (TextView) findViewById(R.id.snackLabel);
        amMealLabel = (TextView) findViewById(R.id.am_mealLabel);
        pmMealLabel = (TextView) findViewById(R.id.pm_mealLabel);
        otherLabel = (TextView) findViewById(R.id.otherLabel);
        readyToEatLabel = (TextView) findViewById(R.id.readyLabel);
        lowLabel = (TextView) findViewById(R.id.lowLabel);
        mediumLabel = (TextView) findViewById(R.id.mediumLabel);
        highLabel = (TextView) findViewById(R.id.highLabel);

        additionalFieldListLayout = (LinearLayout) findViewById(R.id.additionalFieldListLayout);
        servingsLayout = (LinearLayout) findViewById(R.id.servingsLayout);
        descriptionLayout = (LinearLayout) findViewById(R.id.descriptionLayout);
        ingredientsLayout = (LinearLayout) findViewById(R.id.ingredientsLayout);
        directionsLayout = (LinearLayout) findViewById(R.id.directionsLayout);
        recommendedLayout = (LinearLayout) findViewById(R.id.recommendedLayout);
        commentsLayout = (LinearLayout) findViewById(R.id.commentsLayout);
        servingsLabel = (TextView) findViewById(R.id.servingsLabel);
        descriptionLabel = (TextView) findViewById(R.id.descriptionLabel);
        ingredientsLabel = (TextView) findViewById(R.id.ingredientsLabel);
        directionsLabel = (TextView) findViewById(R.id.directionsLabel);
        recommendedLabel = (TextView) findViewById(R.id.recommendedLabel);
        commentsLabel = (TextView) findViewById(R.id.commentsLabel);
        servingsEdit = (EditText) findViewById(R.id.servingsEdit);
        descriptionEdit = (EditText) findViewById(R.id.descriptionEdit);
        ingredientsEdit = (EditText) findViewById(R.id.ingredientsEdit);
        directionsEdit = (EditText) findViewById(R.id.directionsEdit);
        recommendedEdit = (EditText) findViewById(R.id.recommendedEdit);
        commentsEdit = (EditText) findViewById(R.id.commentsEdit);

        caloriesLabel = (TextView) findViewById(R.id.caloriesLabel);
        proteinLabel = (TextView) findViewById(R.id.proteinLabel);
        carbsLabel = (TextView) findViewById(R.id.carbsLabel);
        fatLabel = (TextView) findViewById(R.id.fatLabel);
        satfatLabel = (TextView) findViewById(R.id.satfatLabel);
        sugarsLabel = (TextView) findViewById(R.id.sugarsLabel);
        fiberLabel = (TextView) findViewById(R.id.fiberLabel);
        sodiumLabel = (TextView) findViewById(R.id.sodiumLabel);
        caloriesEdit = (EditText) findViewById(R.id.caloriesEdit);
        proteinEdit = (EditText) findViewById(R.id.proteinEdit);
        carbsEdit = (EditText) findViewById(R.id.carbsEdit);
        fatEdit = (EditText) findViewById(R.id.fatEdit);
        satfatEdit = (EditText) findViewById(R.id.satfatEdit);
        sugarsEdit = (EditText) findViewById(R.id.sugarsEdit);
        fiberEdit = (EditText) findViewById(R.id.fiberEdit);
        sodiumEdit = (EditText) findViewById(R.id.sodiumEdit);

        Typeface avenirNextRegularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        Typeface avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");
        Typeface opensansLightFont = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");

        foodDescEdit.setTypeface(avenirNextMediumFont);
        typeLabel.setTypeface(avenirNextRegularFont);
        preparationLabel.setTypeface(avenirNextRegularFont);
        additionalLabel.setTypeface(avenirNextRegularFont);
        servingsLabel.setTypeface(avenirNextRegularFont);
        descriptionLabel.setTypeface(avenirNextRegularFont);
        ingredientsLabel.setTypeface(avenirNextRegularFont);
        directionsLabel.setTypeface(avenirNextRegularFont);
        recommendedLabel.setTypeface(avenirNextRegularFont);
        commentsLabel.setTypeface(avenirNextRegularFont);
        caloriesLabel.setTypeface(avenirNextRegularFont);
        proteinLabel.setTypeface(avenirNextRegularFont);
        carbsLabel.setTypeface(avenirNextRegularFont);
        fatLabel.setTypeface(avenirNextRegularFont);
        satfatLabel.setTypeface(avenirNextRegularFont);
        sugarsLabel.setTypeface(avenirNextRegularFont);
        fiberLabel.setTypeface(avenirNextRegularFont);
        sodiumLabel.setTypeface(avenirNextRegularFont);

        servingsEdit.setTypeface(avenirNextMediumFont);
        descriptionEdit.setTypeface(avenirNextMediumFont);
        ingredientsEdit.setTypeface(avenirNextMediumFont);
        directionsEdit.setTypeface(avenirNextMediumFont);
        recommendedEdit.setTypeface(avenirNextMediumFont);
        commentsEdit.setTypeface(avenirNextMediumFont);
        caloriesEdit.setTypeface(avenirNextMediumFont);
        proteinEdit.setTypeface(avenirNextMediumFont);
        carbsEdit.setTypeface(avenirNextMediumFont);
        fatEdit.setTypeface(avenirNextMediumFont);
        satfatEdit.setTypeface(avenirNextMediumFont);
        sugarsEdit.setTypeface(avenirNextMediumFont);
        fiberEdit.setTypeface(avenirNextMediumFont);
        sodiumEdit.setTypeface(avenirNextMediumFont);

        snackLabel.setTypeface(avenirNextMediumFont);
        amMealLabel.setTypeface(avenirNextMediumFont);
        pmMealLabel.setTypeface(avenirNextMediumFont);
        otherLabel.setTypeface(avenirNextMediumFont);
        readyToEatLabel.setTypeface(avenirNextMediumFont);
        lowLabel.setTypeface(avenirNextMediumFont);
        mediumLabel.setTypeface(avenirNextMediumFont);
        highLabel.setTypeface(avenirNextMediumFont);

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
        View mActionBar = HTActionBar.getActionBar(this, "Create Favorite", "leftArrow", "checkMark");

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

        selectedFavoriteName = "";
        selectedFavoriteType = "";
        selectedFavoritePrep = "";

        selectedFavoriteCalories = "";
        selectedFavoriteProtein = "";
        selectedFavoriteCarbs = "";
        selectedFavoriteFat = "";
        selectedFavoriteSatFat = "";
        selectedFavoriteSugars = "";
        selectedFavoriteFiber = "";
        selectedFavoriteSodium = "";

        selectedFavoriteDescription = "";
        selectedFavoriteServings = "";
        selectedFavoriteIngredients = "";
        selectedFavoriteDirections = "";
        selectedFavoriteRecommended = "";
        selectedFavoriteComments = "";

        showAdditionalFields = false;

        typeSnackChecked = false;
        typeAMChecked = false;
        typePMChecked = false;
        typeOtherChecked = false;

        prepRTEChecked = false;
        prepLowChecked = false;
        prepMediumChecked = false;
        prepHighChecked = false;

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            if (!"".equals(selectedFavoriteRelaunchItem)
                    && selectedFavoriteRelaunchItem != null
                    && relaunchItemID != 0) {

                ((TextView) mActionBar.findViewById(R.id.title_text)).setText("Edit Favorite");
                getFavoriteItem();

            } else { // create new favorite

                ((TextView) mActionBar.findViewById(R.id.title_text)).setText("Create Favorite");
                showFavoriteItem();
            }
        }

        snackOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackOption.setImageResource(R.drawable.ht_color_button_green_on);
                amMealOption.setImageResource(R.drawable.ht_color_button_green_off);
                pmMealOption.setImageResource(R.drawable.ht_color_button_green_off);
                otherOption.setImageResource(R.drawable.ht_color_button_green_off);
                selectedFavoriteType = "SN";
            }
        });
        amMealOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackOption.setImageResource(R.drawable.ht_color_button_green_off);
                amMealOption.setImageResource(R.drawable.ht_color_button_green_on);
                pmMealOption.setImageResource(R.drawable.ht_color_button_green_off);
                otherOption.setImageResource(R.drawable.ht_color_button_green_off);
                selectedFavoriteType = "AM";
            }
        });
        pmMealOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackOption.setImageResource(R.drawable.ht_color_button_green_off);
                amMealOption.setImageResource(R.drawable.ht_color_button_green_off);
                pmMealOption.setImageResource(R.drawable.ht_color_button_green_on);
                otherOption.setImageResource(R.drawable.ht_color_button_green_off);
                selectedFavoriteType = "PM";
            }
        });
        otherOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackOption.setImageResource(R.drawable.ht_color_button_green_off);
                amMealOption.setImageResource(R.drawable.ht_color_button_green_off);
                pmMealOption.setImageResource(R.drawable.ht_color_button_green_off);
                otherOption.setImageResource(R.drawable.ht_color_button_green_on);
                selectedFavoriteType = "Other";
            }
        });
        readyOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readyOption.setImageResource(R.drawable.ht_color_button_green_on);
                lowOption.setImageResource(R.drawable.ht_color_button_green_off);
                mediumOption.setImageResource(R.drawable.ht_color_button_green_off);
                highOption.setImageResource(R.drawable.ht_color_button_green_off);
                selectedFavoritePrep = "R";
            }
        });
        lowOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readyOption.setImageResource(R.drawable.ht_color_button_green_off);
                lowOption.setImageResource(R.drawable.ht_color_button_green_on);
                mediumOption.setImageResource(R.drawable.ht_color_button_green_off);
                highOption.setImageResource(R.drawable.ht_color_button_green_off);
                selectedFavoritePrep = "L";
            }
        });
        mediumOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readyOption.setImageResource(R.drawable.ht_color_button_green_off);
                lowOption.setImageResource(R.drawable.ht_color_button_green_off);
                mediumOption.setImageResource(R.drawable.ht_color_button_green_on);
                highOption.setImageResource(R.drawable.ht_color_button_green_off);
                selectedFavoritePrep = "M";
            }
        });
        highOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                readyOption.setImageResource(R.drawable.ht_color_button_green_off);
                lowOption.setImageResource(R.drawable.ht_color_button_green_off);
                mediumOption.setImageResource(R.drawable.ht_color_button_green_off);
                highOption.setImageResource(R.drawable.ht_color_button_green_on);
                selectedFavoritePrep = "H";
            }
        });

        addtionalLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                showAdditionalFields = !showAdditionalFields;
                if (showAdditionalFields) {
                    additionalIcon.setImageResource(R.drawable.ht_expand_content_minus);
                    additionalFieldListLayout.setVisibility(View.VISIBLE);
//                    servingsLayout.setVisibility(View.VISIBLE);
//                    descriptionLayout.setVisibility(View.VISIBLE);
//                    ingredientsLayout.setVisibility(View.VISIBLE);
//                    directionsLayout.setVisibility(View.VISIBLE);
//                    recommendedLayout.setVisibility(View.VISIBLE);
//                    commentsLayout.setVisibility(View.VISIBLE);
                } else {
                    additionalIcon.setImageResource(R.drawable.ht_expand_content_plus);
                    additionalFieldListLayout.setVisibility(View.GONE);
//                    servingsLayout.setVisibility(View.GONE);
//                    descriptionLayout.setVisibility(View.GONE);
//                    ingredientsLayout.setVisibility(View.GONE);
//                    directionsLayout.setVisibility(View.GONE);
//                    recommendedLayout.setVisibility(View.GONE);
//                    commentsLayout.setVisibility(View.GONE);
                }
            }
        });

    }

    private void getFavoriteItem() {
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_food_get_favorite"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("WhichID", relaunchItemID + ""));

                    post.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                    client = new DefaultHttpClient();
                    response = client.execute(post);
                    entity = response.getEntity();

                    String responseText = EntityUtils.toString(entity);

                    Document doc = XMLFunctions.XMLfromString(responseText);

                    NodeList error = doc.getElementsByTagName("error");
                    NodeList nodes = doc.getElementsByTagName("add_favorite_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTCreateFavoritesActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();
                                String tempString = "";

                                if (XMLFunctions.tagExists(e2, "favorite_meal_name")) {
                                    tempString = XMLFunctions.getValue(e2, "favorite_meal_name");
                                    selectedFavoriteName = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "favorite_meal_type")) {
                                    tempString = XMLFunctions.getValue(e2, "favorite_meal_type");
                                    if ("SN".equals(tempString))
                                        typeSnackChecked = true;
                                    else if ("AM".equals(tempString))
                                        typeAMChecked = true;
                                    else if ("PM".equals(tempString))
                                        typePMChecked = true;
                                    else if ("Other".equals(tempString))
                                        typeOtherChecked = true;

                                    selectedFavoriteType = tempString;
                                }

                                if (XMLFunctions.tagExists(e2, "favorite_meal_prep")) {
                                    tempString = XMLFunctions.getValue(e2, "favorite_meal_prep");
                                    if ("R".equals(tempString))
                                        prepRTEChecked = true;
                                    else if ("L".equals(tempString))
                                        prepLowChecked = true;
                                    else if ("M".equals(tempString))
                                        prepMediumChecked = true;
                                    else if ("H".equals(tempString))
                                        prepHighChecked = true;

                                    selectedFavoritePrep = tempString;
                                }

                                if (XMLFunctions.tagExists(e2, "favorite_meal_calories")) {
                                    tempString = XMLFunctions.getValue(e2, "favorite_meal_calories");
                                    selectedFavoriteCalories = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "favorite_meal_protein")) {
                                    tempString = XMLFunctions.getValue(e2, "favorite_meal_protein");
                                    selectedFavoriteProtein = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "favorite_meal_carbs")) {
                                    tempString = XMLFunctions.getValue(e2, "favorite_meal_carbs");
                                    selectedFavoriteCarbs = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "favorite_meal_fat")) {
                                    tempString = XMLFunctions.getValue(e2, "favorite_meal_fat");
                                    selectedFavoriteFat = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "favorite_meal_sat_fat")) {
                                    tempString = XMLFunctions.getValue(e2, "favorite_meal_sat_fat");
                                    selectedFavoriteSatFat = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "favorite_meal_sugar")) {
                                    tempString = XMLFunctions.getValue(e2, "favorite_meal_sugar");
                                    selectedFavoriteSugars = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "favorite_meal_fiber")) {
                                    tempString = XMLFunctions.getValue(e2, "favorite_meal_fiber");
                                    selectedFavoriteFiber = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "favorite_meal_sodium")) {
                                    tempString = XMLFunctions.getValue(e2, "favorite_meal_sodium");
                                    selectedFavoriteSodium = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "favorite_meal_description")) {
                                    tempString = XMLFunctions.getValue(e2, "favorite_meal_description");
                                    selectedFavoriteDescription = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "favorite_meal_servings")) {
                                    tempString = XMLFunctions.getValue(e2, "favorite_meal_servings");
                                    selectedFavoriteServings = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "favorite_meal_menu")) {
                                    tempString = XMLFunctions.getValue(e2, "favorite_meal_menu");
                                    selectedFavoriteIngredients = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "favorite_meal_directions")) {
                                    tempString = XMLFunctions.getValue(e2, "favorite_meal_directions");
                                    selectedFavoriteDirections = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "favorite_meal_servewith")) {
                                    tempString = XMLFunctions.getValue(e2, "favorite_meal_servewith");
                                    selectedFavoriteRecommended = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "favorite_meal_nutrition")) {
                                    tempString = XMLFunctions.getValue(e2, "favorite_meal_nutrition");
                                    selectedFavoriteComments = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                showFavoriteItem();

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

//                                    toast = HTToast.showToast(HTCreateFavoritesActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTCreateFavoritesActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void addFavoriteItem() {
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();
        selectedFavoriteName = foodDescEdit.getText().toString();
        selectedFavoriteCalories = caloriesEdit.getText().toString();
        selectedFavoriteProtein = proteinEdit.getText().toString();
        selectedFavoriteCarbs = carbsEdit.getText().toString();
        selectedFavoriteFat = fatEdit.getText().toString();
        selectedFavoriteSatFat = satfatEdit.getText().toString();
        selectedFavoriteSugars = sugarsEdit.getText().toString();
        selectedFavoriteFiber = fiberEdit.getText().toString();
        selectedFavoriteSodium = sodiumEdit.getText().toString();
        selectedFavoriteServings = servingsEdit.getText().toString();
        selectedFavoriteDescription = descriptionEdit.getText().toString();
        selectedFavoriteIngredients = ingredientsEdit.getText().toString();
        selectedFavoriteDirections = directionsEdit.getText().toString();
        selectedFavoriteRecommended = recommendedEdit.getText().toString();
        selectedFavoriteComments = commentsEdit.getText().toString();

        if (!"".equals(selectedFavoriteRelaunchItem)
                && selectedFavoriteRelaunchItem != null
                && relaunchItemID != 0) {

            selectedFavoriteRelaunchItem = relaunchItemID + "";
        } else {

            selectedFavoriteRelaunchItem = "";
        }

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_food_add_favorite"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("WhichID", selectedFavoriteRelaunchItem));
                    nameValuePairs.add(new BasicNameValuePair("name", URLEncoder.encode(selectedFavoriteName, "utf-8")));
                    nameValuePairs.add(new BasicNameValuePair("type", selectedFavoriteType));
                    nameValuePairs.add(new BasicNameValuePair("prep", selectedFavoritePrep));
                    nameValuePairs.add(new BasicNameValuePair("description", URLEncoder.encode(selectedFavoriteDescription, "utf-8")));
                    nameValuePairs.add(new BasicNameValuePair("servings", URLEncoder.encode(selectedFavoriteServings, "utf-8")));
                    nameValuePairs.add(new BasicNameValuePair("ingredients", URLEncoder.encode(selectedFavoriteIngredients, "utf-8")));
                    nameValuePairs.add(new BasicNameValuePair("directions", URLEncoder.encode(selectedFavoriteDirections, "utf-8")));
                    nameValuePairs.add(new BasicNameValuePair("recommended_with", URLEncoder.encode(selectedFavoriteRecommended, "utf-8")));
                    nameValuePairs.add(new BasicNameValuePair("comments", URLEncoder.encode(selectedFavoriteComments, "utf-8")));
                    nameValuePairs.add(new BasicNameValuePair("calories", URLEncoder.encode(selectedFavoriteCalories, "utf-8")));
                    nameValuePairs.add(new BasicNameValuePair("protein", URLEncoder.encode(selectedFavoriteProtein, "utf-8")));
                    nameValuePairs.add(new BasicNameValuePair("carbs", URLEncoder.encode(selectedFavoriteCarbs, "utf-8")));
                    nameValuePairs.add(new BasicNameValuePair("fat", URLEncoder.encode(selectedFavoriteFat, "utf-8")));
                    nameValuePairs.add(new BasicNameValuePair("sat_fat", URLEncoder.encode(selectedFavoriteSatFat, "utf-8")));
                    nameValuePairs.add(new BasicNameValuePair("sugars", URLEncoder.encode(selectedFavoriteSugars, "utf-8")));
                    nameValuePairs.add(new BasicNameValuePair("fiber", URLEncoder.encode(selectedFavoriteFiber, "utf-8")));
                    nameValuePairs.add(new BasicNameValuePair("sodium", URLEncoder.encode(selectedFavoriteSodium, "utf-8")));

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

                                toast = HTToast.showToast(HTCreateFavoritesActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                if (relaunchItemID != 0) { // update

//                                    [[self navigationController] popViewControllerAnimated:YES];

                                } else { // new item

//                                    [[self navigationController] popToRootViewControllerAnimated:YES];
                                }

                                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                                setResult(3);
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

//                                    toast = HTToast.showToast(HTCreateFavoritesActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTCreateFavoritesActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showFavoriteItem() {
        headLayout.setVisibility(View.VISIBLE);
        scrollView.setVisibility(View.VISIBLE);

        headLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
            }
        });
        scrollContentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
            }
        });

        // favorite name
        if (selectedFavoriteName != null)
            foodDescEdit.setText(selectedFavoriteName);

        //type
        if (typeSnackChecked) {
            snackOption.setImageResource(R.drawable.ht_color_button_green_on);
        } else {
            snackOption.setImageResource(R.drawable.ht_color_button_green_off);
        }
        if (typeAMChecked) {
            amMealOption.setImageResource(R.drawable.ht_color_button_green_on);
        } else {
            amMealOption.setImageResource(R.drawable.ht_color_button_green_off);
        }
        if (typePMChecked) {
            pmMealOption.setImageResource(R.drawable.ht_color_button_green_on);
        } else {
            pmMealOption.setImageResource(R.drawable.ht_color_button_green_off);
        }
        if (typeOtherChecked) {
            otherOption.setImageResource(R.drawable.ht_color_button_green_on);
        } else {
            otherOption.setImageResource(R.drawable.ht_color_button_green_off);
        }

        //preparation effort
        if (prepRTEChecked) {
            readyOption.setImageResource(R.drawable.ht_color_button_green_on);
        } else {
            readyOption.setImageResource(R.drawable.ht_color_button_green_off);
        }
        if (prepLowChecked) {
            lowOption.setImageResource(R.drawable.ht_color_button_green_on);
        } else {
            lowOption.setImageResource(R.drawable.ht_color_button_green_off);
        }
        if (prepMediumChecked) {
            mediumOption.setImageResource(R.drawable.ht_color_button_green_on);
        } else {
            mediumOption.setImageResource(R.drawable.ht_color_button_green_off);
        }
        if (prepHighChecked) {
            highOption.setImageResource(R.drawable.ht_color_button_green_on);
        } else {
            highOption.setImageResource(R.drawable.ht_color_button_green_off);
        }

        // additional fields
        if (showAdditionalFields) {
            additionalIcon.setImageResource(R.drawable.ht_expand_content_minus);
            additionalFieldListLayout.setVisibility(View.VISIBLE);
//            servingsLayout.setVisibility(View.VISIBLE);
//            descriptionLayout.setVisibility(View.VISIBLE);
//            ingredientsLayout.setVisibility(View.VISIBLE);
//            directionsLayout.setVisibility(View.VISIBLE);
//            recommendedLayout.setVisibility(View.VISIBLE);
//            commentsLayout.setVisibility(View.VISIBLE);
        } else {
            additionalIcon.setImageResource(R.drawable.ht_expand_content_plus);
            additionalFieldListLayout.setVisibility(View.GONE);
//            servingsLayout.setVisibility(View.GONE);
//            descriptionLayout.setVisibility(View.GONE);
//            ingredientsLayout.setVisibility(View.GONE);
//            directionsLayout.setVisibility(View.GONE);
//            recommendedLayout.setVisibility(View.GONE);
//            commentsLayout.setVisibility(View.GONE);
        }

        if (selectedFavoriteServings != null)
            servingsEdit.setText(selectedFavoriteServings);
        if (selectedFavoriteDescription != null)
            descriptionEdit.setText(selectedFavoriteDescription);
        if (selectedFavoriteIngredients != null)
            ingredientsEdit.setText(selectedFavoriteIngredients);
        if (selectedFavoriteDirections != null)
            directionsEdit.setText(selectedFavoriteDirections);
        if (selectedFavoriteRecommended != null)
            recommendedEdit.setText(selectedFavoriteRecommended);
        if (selectedFavoriteComments != null)
            commentsEdit.setText(selectedFavoriteComments);

        descriptionEdit.setOnTouchListener(editTouchListener);
        ingredientsEdit.setOnTouchListener(editTouchListener);
        directionsEdit.setOnTouchListener(editTouchListener);
        recommendedEdit.setOnTouchListener(editTouchListener);
        commentsEdit.setOnTouchListener(editTouchListener);

        // calories
        if (selectedFavoriteCalories != null)
            caloriesEdit.setText(selectedFavoriteCalories);

        // protein
        if (selectedFavoriteProtein != null)
            proteinEdit.setText(selectedFavoriteProtein);

        // carbs
        if (selectedFavoriteCarbs != null)
            carbsEdit.setText(selectedFavoriteCarbs);

        // fat
        if (selectedFavoriteFat != null)
            fatEdit.setText(selectedFavoriteFat);

        // sat fat
        if (selectedFavoriteSatFat != null)
            satfatEdit.setText(selectedFavoriteSatFat);

        // sugars
        if (selectedFavoriteSugars != null)
            sugarsEdit.setText(selectedFavoriteSugars);

        // fiber
        if (selectedFavoriteFiber != null)
            fiberEdit.setText(selectedFavoriteFiber);

        // sodium
        if (selectedFavoriteSodium != null)
            sodiumEdit.setText(selectedFavoriteSodium);
    }

    View.OnTouchListener editTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_UP:
                    v.getParent().requestDisallowInterceptTouchEvent(false);
                    break;
            }

            return false;
        }
    };

    private void checkButtonPressed() {
        String checkStringName = foodDescEdit.getText().toString().trim();
        String checkStringCalories = caloriesEdit.getText().toString().trim();

        if ("".equals(checkStringName)) {

            HTToast.showToast(HTCreateFavoritesActivity.this, "Please enter a name for this Favorite item", Toast.LENGTH_LONG);
            foodDescEdit.requestFocus();

        } else if ("".equals(selectedFavoriteType)) {

            HTToast.showToast(HTCreateFavoritesActivity.this, "Please choose a type for this Favorite item", Toast.LENGTH_LONG);

        } else if ("".equals(selectedFavoritePrep)) {

            HTToast.showToast(HTCreateFavoritesActivity.this, "Please choose the preparation effort for this Favorite item", Toast.LENGTH_LONG);

        } else if ("".equals(checkStringCalories)) {

            HTToast.showToast(HTCreateFavoritesActivity.this, "Please enter the calories for this Favorite item", Toast.LENGTH_LONG);

            caloriesEdit.requestFocus();

        } else {
            imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
            addFavoriteItem();
        }

    }
}
