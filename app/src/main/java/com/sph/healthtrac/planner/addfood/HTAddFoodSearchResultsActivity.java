package com.sph.healthtrac.planner.addfood;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
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
import com.sph.healthtrac.planner.createfavorites.HTCreateFavoritesActivity;

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
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class HTAddFoodSearchResultsActivity extends Activity {
    private static RelativeLayout mainContentLayout;
    private View mActionBar;
    private LinearLayout headLayout;
    private LinearLayout prepLayout;
    private RelativeLayout searchLayout;
    private RelativeLayout exchangeMsgLayout;
    private RelativeLayout searchResultLayout;
    private RelativeLayout generalSearchLayout;
    //    private ListView searchResultListView;
//    private SearchResultListViewAdapter listViewAdapter;
    private ScrollView searchResultScrollView;
    private LinearLayout searchResultContentLayout;

    private TextView preparationLabel;
    private TextView readyToEatLabel;
    private TextView lowLabel;
    private TextView mediumLabel;
    private TextView highLabel;
    private ImageView readyCheck;
    private ImageView lowCheck;
    private ImageView mediumCheck;
    private ImageView highCheck;
    private EditText searchEdit;
    private TextView exchangeMessageLabel;
    private TextView searchResultLabel;
    private TextView searchResultSubLabel;
    private TextView generalSearchLabel;
    private TextView generalSearchSubLabel;

    private LinearLayout advancedSearchLayout;
    private RelativeLayout advancedSearchExpandLayout;
    private LinearLayout mealTypeStarterLayout;
    private TextView advancedSearchLabel;
    private ImageView additionalFieldIcon;
    private TextView mealTypeStarterLabel;
    private TextView smoothieLabel;
    private TextView soupLabel;
    private TextView pastaLabel;
    private TextView eggLabel;
    private TextView snackLabel;
    private TextView groceryLabel;
    private ImageView smoothieCheck;
    private ImageView soupCheck;
    private ImageView pastaCheck;
    private ImageView eggCheck;
    private ImageView snackCheck;
    private ImageView groceryCheck;

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

    private String addFoodCategory;
    private String addFoodSearchFieldString = "";
    private String addFoodSearchString;
    private String inTemplateString;
    private String generalFoodSearchString = "";
    private String exchangeItemsString = "";
    private String caloriesOrOtherString = "calories";

    private int numberOfResults;
    //    private int selectedFoodID;
    private int selectedFoodIndex;
    private int relaunchItemID;
    private int exchangeNumber;
    private int generalFoodSearchPhase;

    private boolean isExchangeItem;

    private float exchangeItemsAllowed;
    private float exchangeItemsSelected;
    private List<String> exchangeItemArray = new ArrayList<>();
    private List<String> exchangeItemQuantitiesArray = new ArrayList<>();
    private List<String> foodQuantitiesArray = new ArrayList<>();

    private List<String> addFoodID = new ArrayList<>();
    private List<String> addFoodName = new ArrayList<>();
    private List<String> addFoodCalories = new ArrayList<>();
    private List<String> addFoodServings = new ArrayList<>();

    private boolean prepCheckboxRTEChecked;
    private boolean prepCheckboxLowChecked;
    private boolean prepCheckboxMediumChecked;
    private boolean prepCheckboxHighChecked;

    private boolean showAdditionalSearchFields;
    private boolean newRecommendedFoods;

    private boolean mealTypeCheckboxSmoothieChecked;
    private boolean mealTypeCheckboxSoupChecked;
    private boolean mealTypeCheckboxPastaChecked;
    private boolean mealTypeCheckboxEggChecked;
    private boolean mealTypeCheckboxSnackChecked;
    private boolean mealTypeCheckboxGroceryChecked;


//    private String[] quantities;

    private boolean allowSelections;

    private String[] quantityPickerValues;
    private String[] quantityPickerValueFractions;
    private Dialog chooseQuantityDlg;
    private NumberPicker picker1;
    private NumberPicker picker2;
    private TextView curQuantityEdit;
    private int curQuantityEditIndex;

    Typeface openSansLightFont;
    Typeface avenirNextRegularFont;
    Typeface avenirNextMediumFont;
    Typeface avenirNextDemiBoldFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfood_search_result);

        openSansLightFont = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
        avenirNextRegularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");
        avenirNextDemiBoldFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-DemiBold.ttf");

        quantityPickerValues = getResources().getStringArray(R.array.quantity_values);
        quantityPickerValueFractions = getResources().getStringArray(R.array.quantity_fraction_values);

        String title = "";
        addFoodCategory = getIntent().getStringExtra("addFoodCategory");
        addFoodSearchString = getIntent().getStringExtra("addFoodSearchString");
        addFoodSearchFieldString = getIntent().getStringExtra("addFoodSearchFieldString");
        relaunchItemID = getIntent().getIntExtra("relaunchItemID", 0);

        if (addFoodSearchString == null)
            addFoodSearchString = "";
        if (addFoodSearchFieldString == null)
            addFoodSearchFieldString = "";

        allowSelections = false;
        inTemplateString = "false";
        if ("favorites".equals(addFoodCategory)) {
            title = "My Favorites";
        } else if ("recommended".equals(addFoodCategory)) {
            title = "Recommended";
        } else if ("general".equals(addFoodCategory)) {
            title = "General Food Item";
        } else if ("template".equals(addFoodCategory) || "exchange".equals(addFoodCategory)) {
            title = "Recommended";
            inTemplateString = "true";
        }

        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        headLayout = (LinearLayout) findViewById(R.id.headLayout);
        prepLayout = (LinearLayout) findViewById(R.id.prepLayout);
        searchLayout = (RelativeLayout) findViewById(R.id.searchLayout);
        exchangeMsgLayout = (RelativeLayout) findViewById(R.id.exchangeMsgLayout);
        searchResultLayout = (RelativeLayout) findViewById(R.id.searchResultLayout);
        generalSearchLayout = (RelativeLayout) findViewById(R.id.generalSearchLayout);
        searchResultScrollView = (ScrollView) findViewById(R.id.searchResultScrollView);
        searchResultContentLayout = (LinearLayout) findViewById(R.id.searchResultContentLayout);
//        searchResultListView = (ListView) findViewById(R.id.searchResultListView);
//        listViewAdapter = new SearchResultListViewAdapter(this);
//        searchResultListView.setAdapter(listViewAdapter);

        preparationLabel = (TextView) findViewById(R.id.preparationLabel);
        readyToEatLabel = (TextView) findViewById(R.id.readyLabel);
        lowLabel = (TextView) findViewById(R.id.lowLabel);
        mediumLabel = (TextView) findViewById(R.id.mediumLabel);
        highLabel = (TextView) findViewById(R.id.highLabel);
        readyCheck = (ImageView) findViewById(R.id.readyCheck);
        lowCheck = (ImageView) findViewById(R.id.lowCheck);
        mediumCheck = (ImageView) findViewById(R.id.mediumCheck);
        highCheck = (ImageView) findViewById(R.id.highCheck);
        searchEdit = (EditText) findViewById(R.id.searchEdit);
        exchangeMessageLabel = (TextView) findViewById(R.id.exchangeMessageLabel);
        searchResultLabel = (TextView) findViewById(R.id.searchResultLabel);
        searchResultSubLabel = (TextView) findViewById(R.id.searchResultSubLabel);
        generalSearchLabel = (TextView) findViewById(R.id.generalSearchLabel);
        generalSearchSubLabel = (TextView) findViewById(R.id.generalSearchSubLabel);

        preparationLabel.setTypeface(avenirNextRegularFont);
        readyToEatLabel.setTypeface(avenirNextMediumFont);
        lowLabel.setTypeface(avenirNextMediumFont);
        mediumLabel.setTypeface(avenirNextMediumFont);
        highLabel.setTypeface(avenirNextMediumFont);
        searchEdit.setTypeface(avenirNextMediumFont);
        exchangeMessageLabel.setTypeface(avenirNextDemiBoldFont);
        searchResultLabel.setTypeface(avenirNextDemiBoldFont);
        searchResultSubLabel.setTypeface(avenirNextDemiBoldFont);
        generalSearchLabel.setTypeface(avenirNextDemiBoldFont);
        generalSearchSubLabel.setTypeface(avenirNextDemiBoldFont);

        readyCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepCheckboxRTEChecked = !prepCheckboxRTEChecked;
                if (prepCheckboxRTEChecked)
                    readyCheck.setImageResource(R.drawable.ht_check_on_green);
                else
                    readyCheck.setImageResource(R.drawable.ht_check_off_green);

                getSearchResults();
            }
        });

        lowCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepCheckboxLowChecked = !prepCheckboxLowChecked;
                if (prepCheckboxLowChecked)
                    lowCheck.setImageResource(R.drawable.ht_check_on_green);
                else
                    lowCheck.setImageResource(R.drawable.ht_check_off_green);
                getSearchResults();
            }
        });

        mediumCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepCheckboxMediumChecked = !prepCheckboxMediumChecked;
                if (prepCheckboxMediumChecked)
                    mediumCheck.setImageResource(R.drawable.ht_check_on_green);
                else
                    mediumCheck.setImageResource(R.drawable.ht_check_off_green);
                getSearchResults();
            }
        });

        highCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prepCheckboxHighChecked = !prepCheckboxHighChecked;
                if (prepCheckboxHighChecked)
                    highCheck.setImageResource(R.drawable.ht_check_on_green);
                else
                    highCheck.setImageResource(R.drawable.ht_check_off_green);
                getSearchResults();
            }
        });

        advancedSearchLayout = (LinearLayout) findViewById(R.id.advancedSearchLayout);
        advancedSearchExpandLayout = (RelativeLayout) findViewById(R.id.advancedSearchExpandLayout);
        mealTypeStarterLayout = (LinearLayout) findViewById(R.id.mealTypeStarterLayout);
        advancedSearchLabel = (TextView) findViewById(R.id.advancedSearchLabel);
        additionalFieldIcon = (ImageView) findViewById(R.id.additionalFieldIcon);
        mealTypeStarterLabel = (TextView) findViewById(R.id.mealTypeStarterLabel);
        smoothieLabel = (TextView) findViewById(R.id.smoothieLabel);
        soupLabel = (TextView) findViewById(R.id.soupLabel);
        pastaLabel = (TextView) findViewById(R.id.pastaLabel);
        eggLabel = (TextView) findViewById(R.id.eggLabel);
        snackLabel = (TextView) findViewById(R.id.snackLabel);
        groceryLabel = (TextView) findViewById(R.id.groceryLabel);
        smoothieCheck = (ImageView) findViewById(R.id.smoothieCheck);
        soupCheck = (ImageView) findViewById(R.id.soupCheck);
        pastaCheck = (ImageView) findViewById(R.id.pastaCheck);
        eggCheck = (ImageView) findViewById(R.id.eggCheck);
        snackCheck = (ImageView) findViewById(R.id.snackCheck);
        groceryCheck = (ImageView) findViewById(R.id.groceryCheck);

        advancedSearchLabel.setTypeface(avenirNextMediumFont);
        mealTypeStarterLabel.setTypeface(avenirNextMediumFont);
        smoothieLabel.setTypeface(avenirNextMediumFont);
        soupLabel.setTypeface(avenirNextMediumFont);
        pastaLabel.setTypeface(avenirNextMediumFont);
        eggLabel.setTypeface(avenirNextMediumFont);
        snackLabel.setTypeface(avenirNextMediumFont);
        groceryLabel.setTypeface(avenirNextMediumFont);

        advancedSearchExpandLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdditionalSearchFields = !showAdditionalSearchFields;
                if (showAdditionalSearchFields) {
                    additionalFieldIcon.setImageResource(R.drawable.ht_expand_content_minus);
                    mealTypeStarterLayout.setVisibility(View.VISIBLE);
                } else {
                    additionalFieldIcon.setImageResource(R.drawable.ht_expand_content_plus);
                    mealTypeStarterLayout.setVisibility(View.GONE);
                }
            }
        });

        smoothieCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealTypeCheckboxSmoothieChecked = !mealTypeCheckboxSmoothieChecked;
                if (mealTypeCheckboxSmoothieChecked)
                    smoothieCheck.setImageResource(R.drawable.ht_check_on_green);
                else
                    smoothieCheck.setImageResource(R.drawable.ht_check_off_green);

                getSearchResults();
            }
        });

        soupCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealTypeCheckboxSoupChecked = !mealTypeCheckboxSoupChecked;
                if (mealTypeCheckboxSoupChecked)
                    soupCheck.setImageResource(R.drawable.ht_check_on_green);
                else
                    soupCheck.setImageResource(R.drawable.ht_check_off_green);

                getSearchResults();
            }
        });

        pastaCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealTypeCheckboxPastaChecked = !mealTypeCheckboxPastaChecked;
                if (mealTypeCheckboxPastaChecked)
                    pastaCheck.setImageResource(R.drawable.ht_check_on_green);
                else
                    pastaCheck.setImageResource(R.drawable.ht_check_off_green);

                getSearchResults();
            }
        });

        eggCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealTypeCheckboxEggChecked = !mealTypeCheckboxEggChecked;
                if (mealTypeCheckboxEggChecked)
                    eggCheck.setImageResource(R.drawable.ht_check_on_green);
                else
                    eggCheck.setImageResource(R.drawable.ht_check_off_green);

                getSearchResults();
            }
        });

        snackCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealTypeCheckboxSnackChecked = !mealTypeCheckboxSnackChecked;
                if (mealTypeCheckboxSnackChecked)
                    snackCheck.setImageResource(R.drawable.ht_check_on_green);
                else
                    snackCheck.setImageResource(R.drawable.ht_check_off_green);

                getSearchResults();
            }
        });

        groceryCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mealTypeCheckboxGroceryChecked = !mealTypeCheckboxGroceryChecked;
                if (mealTypeCheckboxGroceryChecked)
                    groceryCheck.setImageResource(R.drawable.ht_check_on_green);
                else
                    groceryCheck.setImageResource(R.drawable.ht_check_off_green);

                getSearchResults();
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
        mActionBar = HTActionBar.getActionBar(this, title, "leftArrow", "checkMark");

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

        mActionBar.findViewById(R.id.rightCheckAction).setVisibility(View.INVISIBLE);
        // right check button
        mActionBar.findViewById(R.id.rightCheckAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkButtonPressed();
            }
        });

        LayoutInflater mInflater = LayoutInflater.from(this);
        View dlgContentView = mInflater.inflate(R.layout.custom_picker, null);
        picker1 = (NumberPicker) dlgContentView.findViewById(R.id.picker1);
        picker2 = (NumberPicker) dlgContentView.findViewById(R.id.picker2);
        picker1.setDisplayedValues(quantityPickerValues);
        picker1.setMaxValue(16);
        picker1.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picker1.setWrapSelectorWheel(false);
        picker2.setDisplayedValues(quantityPickerValueFractions);
        picker2.setMaxValue(3);
        picker2.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
        picker2.setWrapSelectorWheel(false);
        chooseQuantityDlg = new AlertDialog.Builder(this)
                .setTitle("Quantity")
                .setView(dlgContentView)
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String quantity = quantityPickerValues[picker1.getValue()] + quantityPickerValueFractions[picker2.getValue()];
                        curQuantityEdit.setText(quantity);
                        foodQuantitiesArray.set(curQuantityEditIndex, quantity);
                        updateExchangeMessageLabel();
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

        HTGlobals.getInstance().exchangeItemTextSearch = false;

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            if ("general".equals(addFoodCategory) && "".equals(addFoodSearchFieldString)) {
                showSearchResults();
            } else {
                getSearchResults();
            }
        }

    }

    private void checkButtonPressed() {
        if ("general".equals(addFoodCategory) || ("template".equals(addFoodCategory) && isExchangeItem == false) ||
                ("recommended".equals(addFoodCategory) && HTGlobals.getInstance().hideRecommendedFoodSearchCriteria)) {
            addFoodSearchFieldString = searchEdit.getText().toString();
            try {
                addFoodSearchString = "WhichCategory=" + addFoodCategory + "&search=" + URLEncoder.encode(addFoodSearchFieldString, "utf-8") + "&template=" + inTemplateString
                        + "&relaunch=" + relaunchItemID + "&prep=";
            } catch (UnsupportedEncodingException e) {
            }

            if ("template".equals(addFoodCategory) || ("recommended".equals(addFoodCategory) && HTGlobals.getInstance().hideRecommendedFoodSearchCriteria)) {
                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
            }

            getSearchResults();

        } else {
            chooseExchangeItems();
        }
    }

    private void chooseExchangeItems() {
        if (exchangeItemsSelected > exchangeItemsAllowed && exchangeItemsAllowed != 0) {
            String alertString = "";

            if (exchangeItemsAllowed == 1) {
                alertString = "Please choose 1 item or less";
            } else {
                alertString = String.format("Please choose %d items or less", (long) exchangeItemsAllowed);
            }

            toast = HTToast.showToast(this, alertString, Toast.LENGTH_LONG);
        } else {
            for (int i = 0; i < numberOfResults; i++) {
                if (!"0.00".equals(foodQuantitiesArray.get(i)))
                    exchangeItemsString = exchangeItemsString + addFoodID.get(i) + "||" + foodQuantitiesArray.get(i) + "}";
            }

            Intent intent = new Intent(HTAddFoodSearchResultsActivity.this, HTAddFoodSelectItemActivity.class);
            intent.putExtra("showChangeFoodSelection", false);
            //intent.putExtra("selectedFoodID", Integer.parseInt(addFoodID.get(selectedFoodIndex)));
            if(addFoodID.size() > 0)
                intent.putExtra("selectedFoodID", addFoodID.get(selectedFoodIndex));
            else
                intent.putExtra("selectedFoodID", "");

            if (relaunchItemID > 0) {
                intent.putExtra("relaunchPlannerItem", true);
                intent.putExtra("relaunchItemID", relaunchItemID);
            }
//            else {
//                intent.putExtra("relaunchPlannerItem", false);
//            }

            intent.putExtra("inTemplateString", "true");
            intent.putExtra("addFoodCategory", "exchange");
            intent.putExtra("exchangeItemsString", exchangeItemsString);
            startActivityForResult(intent, 1);
        }
    }

    private void generalFoodSearchPhaseII() {
        generalFoodSearchPhase = 2;
        getSearchResults();
    }

    private void generalFoodSearchSubmitRequest() {
        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(this);
        AlertDialog alert;
        builder.setTitle("Search Request Submitted")
                .setMessage(String.format("We're sorry you couldn't find what you were searching for.\n\nYour request for '%s' has been submitted to our research department.\n\nYou should receive an email from us within one business day.", searchEdit.getText().toString()))
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alert = builder.create();
        alert.show();

        generalFoodSearchPhase = 1;
        submitSearchRequest();
    }

    private void getSearchResults() {

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        addFoodID.clear();
        addFoodName.clear();
        addFoodCalories.clear();
        addFoodServings.clear();

        exchangeItemArray.clear();
        foodQuantitiesArray.clear();
        exchangeItemQuantitiesArray.clear();

        numberOfResults = 0;
        selectedFoodIndex = 0;
        isExchangeItem = false;

        exchangeItemsAllowed = 0;
        exchangeItemsSelected = 0;

        caloriesOrOtherString = "calories";

        // prep effort

        String prepString = "";
        if ("template".equals(addFoodCategory)) {
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
        }

        //meal types

        String mealTypeString = "";
        if("template".equals(addFoodCategory) && !isExchangeItem) {

            if (mealTypeCheckboxSmoothieChecked) {
                mealTypeString += "SMOOTHIE,";
            }

            if (mealTypeCheckboxSoupChecked) {
                mealTypeString += "SOUP,";
            }

            if (mealTypeCheckboxPastaChecked) {
                mealTypeString += "PASTA,";
            }

            if (mealTypeCheckboxEggChecked) {
                mealTypeString += "EGG,";
            }

            if (mealTypeCheckboxSnackChecked) {
                mealTypeString += "SNACK,";
            }

            if (mealTypeCheckboxGroceryChecked) {
                mealTypeString += "GROCERY,";
            }

            if (mealTypeString.length() > 0) {
                mealTypeString = mealTypeString.substring(0, mealTypeString.length() - 1);
            }
        }
        exchangeItemsString = "";

        if (("template".equals(addFoodCategory) && relaunchItemID > 0) ||
                ("exchange".equals(addFoodCategory) && relaunchItemID > 0 && HTGlobals.getInstance().exchangeItemTextSearch)) {
            String searchKey = "";
            try {
                searchKey = URLEncoder.encode(searchEdit.getText().toString(), "utf-8");
            } catch (UnsupportedEncodingException e) {
            }

            addFoodSearchString = "WhichCategory=" + addFoodCategory + "&search=" + searchKey + "&template=" + inTemplateString
                    + "&relaunch=" + relaunchItemID + "&prep=" + prepString + "&meal_type=" + mealTypeString;
        }

        // new generalFoodSearch stuff
        if ("general".equals(addFoodCategory)) {
            // re-searching the same terms
            if ("".equals(addFoodSearchFieldString) || !addFoodSearchFieldString.equals(generalFoodSearchString)) {
                generalFoodSearchPhase = 1;
            }
            generalFoodSearchString = addFoodSearchFieldString;
        }

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_food_search_results"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("generalFoodSearchPhase", generalFoodSearchPhase + ""));

                    //additional parameter from addFoodSearchString
                    String[] additionalParams = addFoodSearchString.split("&");
                    for (int i = 0; i < additionalParams.length; i++) {
                        String[] addtionalParam = additionalParams[i].split("=");
                        if (addtionalParam.length > 1) {
                            nameValuePairs.add(new BasicNameValuePair(addtionalParam[0], addtionalParam[1]));
                        } else {
                            nameValuePairs.add(new BasicNameValuePair(addtionalParam[0], ""));
                        }
                    }

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

                                toast = HTToast.showToast(HTAddFoodSearchResultsActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();
                                String tempString = "";

                                int i = 1;
                                while (XMLFunctions.tagExists(e2, "search_result_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "food_id_" + i);
                                    addFoodID.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "food_name_" + i);
                                    addFoodName.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "food_calories_" + i);
                                    addFoodCalories.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "food_servings_" + i);
                                    addFoodServings.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                numberOfResults = addFoodID.size();

                                //exchange_number
                                if (XMLFunctions.tagExists(e2, "exchange_number")) {
                                    isExchangeItem = true;
                                    exchangeItemsAllowed = Float.valueOf(XMLFunctions.getValue(e2, "exchange_number"));
                                }

                                if (XMLFunctions.tagExists(e2, "exchange_number_selected")) {
                                    exchangeItemsSelected = Float.valueOf(XMLFunctions.getValue(e2, "exchange_number_selected"));
                                }

                                if (XMLFunctions.tagExists(e2, "exchange_array")) {
                                    String[] exchange_array_data = XMLFunctions.getValue(e2, "exchange_array").split(",");
                                    Collections.addAll(exchangeItemArray, exchange_array_data);
                                }

                                if (XMLFunctions.tagExists(e2, "exchange_array_quantities")) {
                                    String[] exchange_array_quantities_data = XMLFunctions.getValue(e2, "exchange_array_quantities").split(",");
                                    Collections.addAll(exchangeItemQuantitiesArray, exchange_array_quantities_data);
                                }

                                // caloriesOrOtherString
                                if (XMLFunctions.tagExists(e2, "calories_or_other_string")) {
                                    caloriesOrOtherString = XMLFunctions.getValue(e2, "calories_or_other_string");
                                }

                                // exchangeItemTextSearch
                                if (XMLFunctions.tagExists(e2, "exchange_text_search")) {
                                    tempString = XMLFunctions.getValue(e2, "exchange_text_search");
                                    if ("1".equals(tempString)) {
                                        HTGlobals.getInstance().exchangeItemTextSearch = true;
                                    } else {
                                        HTGlobals.getInstance().exchangeItemTextSearch = false;
                                    }
                                }

                                if (XMLFunctions.tagExists(e2, "new_recommended_foods")) {
                                    tempString = XMLFunctions.getValue(e2, "new_recommended_foods");
                                    if ("1".equals(tempString)) {
                                        newRecommendedFoods = true;
                                    } else {
                                        newRecommendedFoods = false;
                                    }
                                }

                                showSearchResults();
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

//                                    toast = HTToast.showToast(HTAddFoodSearchResultsActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTAddFoodSearchResultsActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void submitSearchRequest() {
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "submit_search_request"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("search", URLEncoder.encode(searchEdit.getText().toString(), "utf-8")));

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

                                toast = HTToast.showToast(HTAddFoodSearchResultsActivity.this, errorMessage, Toast.LENGTH_LONG);
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

//                                    toast = HTToast.showToast(HTAddFoodSearchResultsActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTAddFoodSearchResultsActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void deleteFavorite() {
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_food_delete_favorite"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("WhichID", addFoodID.get(selectedFoodIndex) + ""));

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

                                toast = HTToast.showToast(HTAddFoodSearchResultsActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                getSearchResults();
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

//                                    toast = HTToast.showToast(HTAddFoodSearchResultsActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTAddFoodSearchResultsActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        // perform the search only after typing has stopped for 2 seconds
        private final Handler handler = new Handler();

        private Runnable runnable = new Runnable() {
            @Override
            public void run() {

                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                addFoodSearchString = "WhichCategory=" + addFoodCategory + "&search=" + addFoodSearchFieldString + "&template=" + inTemplateString
                        + "&relaunch=" + relaunchItemID;

                getSearchResults();
            }
        };

        public void afterTextChanged(final Editable s) {

            addFoodSearchFieldString = s.toString();

            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 2000); // 2-second delay
        }
    };

    private void showSearchResults() {
        foodQuantitiesArray.clear();
        if (searchResultContentLayout.getChildCount() > 0)
            searchResultContentLayout.removeAllViews();

        headLayout.setVisibility(View.VISIBLE);

        if (isExchangeItem) {

            mActionBar.findViewById(R.id.rightCheckAction).setVisibility(View.VISIBLE);
            addFoodCategory = "exchange";

//        } else if ("general".equals(addFoodCategory) || "template".equals(addFoodCategory) || ("recommended".equals(addFoodCategory) && HTGlobals.getInstance().hideRecommendedFoodSearchCriteria)) {
//
//            mActionBar.findViewById(R.id.rightCheckAction).setVisibility(View.VISIBLE);

        } else {

            mActionBar.findViewById(R.id.rightCheckAction).setVisibility(View.INVISIBLE);
        }

        //prep effort
        if ("template".equals(addFoodCategory) && isExchangeItem == false && !newRecommendedFoods) {
            prepLayout.setVisibility(View.VISIBLE);
        } else {
            prepLayout.setVisibility(View.GONE);
        }

        //search
        searchEdit.removeTextChangedListener(textWatcher);

        if ((("general".equals(addFoodCategory) || "template".equals(addFoodCategory)) && isExchangeItem == false) ||
                (isExchangeItem && HTGlobals.getInstance().exchangeItemTextSearch) ||
                ("recommended".equals(addFoodCategory) && HTGlobals.getInstance().hideRecommendedFoodSearchCriteria)) {

            searchLayout.setVisibility(View.VISIBLE);

            if ("general".equals(addFoodCategory)) {
                if (addFoodID.size() > 0)
                    imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                else
                    imm.showSoftInputFromInputMethod(mainContentLayout.getWindowToken(), 0);
            } else if (isExchangeItem && HTGlobals.getInstance().exchangeItemTextSearch) {

                searchEdit.addTextChangedListener(textWatcher);
            }
        } else {
            searchLayout.setVisibility(View.GONE);
        }

        //advanced search
        if ("template".equals(addFoodCategory) && !isExchangeItem && newRecommendedFoods) {
           advancedSearchLayout.setVisibility(View.VISIBLE);
            if (showAdditionalSearchFields)
                mealTypeStarterLayout.setVisibility(View.VISIBLE);
            else
                mealTypeStarterLayout.setVisibility(View.GONE);
        } else {
            advancedSearchLayout.setVisibility(View.GONE);
        }

        // exchange item!
        if (isExchangeItem) {
            exchangeMsgLayout.setVisibility(View.VISIBLE);
            updateExchangeMessageLabel();
        } else {
            exchangeMsgLayout.setVisibility(View.GONE);
        }
        // number of search results
        if (!("general".equals(addFoodCategory) && "".equals(addFoodSearchFieldString)) && !"template".equals(addFoodCategory) && !"exchange".equals(addFoodCategory)) {
            searchResultLayout.setVisibility(View.VISIBLE);
            if (numberOfResults == 200 && !"general".equals(addFoodCategory)) {
                searchResultLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setResult(1);
                        finish();
                    }
                });
            }

            if ((numberOfResults == 200 && !"general".equals(addFoodCategory)) ||
                    (numberOfResults == 50 && "general".equals(addFoodCategory) &&
                            generalFoodSearchPhase == 1)) {

                searchResultLabel.setText("Top " + numberOfResults + " results");

            } else {
                searchResultLabel.setText(numberOfResults + " results");
            }

            if ((numberOfResults == 200 && !"general".equals(addFoodCategory)) ||
                    (numberOfResults == 50 && "general".equals(addFoodCategory) &&
                            generalFoodSearchPhase == 1)) {
                searchResultSubLabel.setVisibility(View.VISIBLE);
                searchResultSubLabel.setText("Refine search to narrow results");
            } else {
                searchResultSubLabel.setVisibility(View.GONE);
            }

        } else {
            searchResultLayout.setVisibility(View.GONE);
        }

        // general food search phase II
        if ("general".equals(addFoodCategory) && !"".equals(addFoodSearchFieldString) && generalFoodSearchPhase == 1) {
            imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
            generalSearchLayout.setVisibility(View.VISIBLE);
            generalSearchSubLabel.setVisibility(View.GONE);
            generalSearchLabel.setText("Or tap here to expand this search");
            generalSearchLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    generalFoodSearchPhaseII();
                }
            });
        } else if ("general".equals(addFoodCategory) && !"".equals(addFoodSearchFieldString) && (generalFoodSearchPhase == 2 || generalFoodSearchPhase == 3)) {
            imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
            generalSearchLayout.setVisibility(View.VISIBLE);
            generalSearchSubLabel.setVisibility(View.VISIBLE);
            generalSearchLabel.setText("Not finding what you're looking for?");
            generalSearchSubLabel.setText("Tap to submit search to our research department");
            generalSearchLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    generalFoodSearchSubmitRequest();
                }
            });
        } else {
            generalSearchLayout.setVisibility(View.GONE);
        }

//        listViewAdapter.notifyDataSetChanged();
        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        for (int i = 0; i < addFoodID.size(); i++) {
            final int index = i;
            View foodItemCell = inflater.inflate(R.layout.searchresult_cell, null);
            TextView foodTitleView = (TextView) foodItemCell.findViewById(R.id.foodTitleView);
            TextView foodSubTitleView = (TextView) foodItemCell.findViewById(R.id.foodSubTitleView);
            TextView quantityEdit = (TextView) foodItemCell.findViewById(R.id.quantityEdit);
            foodTitleView.setTypeface(avenirNextMediumFont);
            foodSubTitleView.setTypeface(avenirNextRegularFont);
            quantityEdit.setTypeface(openSansLightFont);

            if (isExchangeItem) {
                quantityEdit.setVisibility(View.VISIBLE);
                if (exchangeItemArray.contains(addFoodID.get(index))) {
                    String quantity = exchangeItemQuantitiesArray.get(exchangeItemArray.indexOf(addFoodID.get(index)));
                    foodQuantitiesArray.add(quantity);
                    quantityEdit.setText(quantity);
                } else {
                    quantityEdit.setText("0.00");
                    foodQuantitiesArray.add("0.00");
                }

                quantityEdit.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        curQuantityEdit = (TextView) v;
                        curQuantityEditIndex = index;

                        final String quantity = (String) ((TextView) v).getText();
                        String quantityValue = quantity.substring(0, quantity.indexOf("."));
                        String quantityFractionValue = quantity.substring(quantity.indexOf("."), quantity.length());
                        int quantity_index = 0;
                        int quantityFraction_index = 0;
                        for (int i = 0; i < quantityPickerValues.length; i++) {
                            if (quantityValue.equals(quantityPickerValues[i])) {
                                quantity_index = i;
                                break;
                            }
                        }
                        for (int i = 0; i < quantityPickerValueFractions.length; i++) {
                            if (quantityFractionValue.equals(quantityPickerValueFractions[i])) {
                                quantityFraction_index = i;
                                break;
                            }
                        }
                        picker1.setValue(quantity_index);
                        picker2.setValue(quantityFraction_index);
                        chooseQuantityDlg.show();
                    }
                });
            } else {
                quantityEdit.setVisibility(View.GONE);
            }

            foodTitleView.setText(HTGlobals.capitalize(addFoodName.get(index).toLowerCase()));
            if (!"".equals(addFoodCalories.get(index))) {
                foodSubTitleView.setVisibility(View.VISIBLE);
                if ("general".equals(addFoodCategory) &&
                        !"".equals(addFoodServings.get(index))) {
                    foodSubTitleView.setText(addFoodCalories.get(index) + " " + caloriesOrOtherString + " - " + addFoodServings.get(index));
                } else {
                    foodSubTitleView.setText(addFoodCalories.get(index) + " " + caloriesOrOtherString);
                }
            } else {
                foodSubTitleView.setVisibility(View.GONE);
            }

            if (isExchangeItem) {
                //nothing
            } else {
                foodItemCell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedFoodIndex = index;
                        Intent intent = new Intent(HTAddFoodSearchResultsActivity.this, HTAddFoodSelectItemActivity.class);
                        intent.putExtra("showChangeFoodSelection", false);
                        intent.putExtra("addFoodCategory", addFoodCategory);
                        //intent.putExtra("selectedFoodID", Integer.parseInt(addFoodID.get(index)));
                        intent.putExtra("selectedFoodID", addFoodID.get(index));
                        if (relaunchItemID > 0) {
                            intent.putExtra("relaunchPlannerItem", true);
                            intent.putExtra("relaunchItemID", relaunchItemID);
                        }
//                        else {
//                            intent.putExtra("relaunchPlannerItem", false);
//                        }

                        if ("true".equals(inTemplateString)) {
                            intent.putExtra("inTemplateString", "true");
                        } else {
                            intent.putExtra("inTemplateString", "");
                        }

                        startActivityForResult(intent, 1);
                    }
                });
            }

            if ("favorites".equals(addFoodCategory)) {
                foodItemCell.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        selectedFoodIndex = index;
                        Dialog dialog = new AlertDialog.Builder(HTAddFoodSearchResultsActivity.this)
                                .setTitle("Edit or Delete Favorite?")
                                .setMessage("Would you like to edit or delete this My Favorites item?")
                                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Intent intent = new Intent(HTAddFoodSearchResultsActivity.this, HTCreateFavoritesActivity.class);
                                        intent.putExtra("selectedFavoriteRelaunchItem", "true");
                                        intent.putExtra("relaunchItemID", Integer.parseInt(addFoodID.get(selectedFoodIndex)));
                                        startActivityForResult(intent, 1);
                                        dialog.dismiss();
                                    }
                                })
                                .setNeutralButton("Delete", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        deleteFavorite();
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
                        dialog.show();
                        return false;
                    }
                });
            }

            searchResultContentLayout.addView(foodItemCell);
        }

        searchResultScrollView.scrollTo(0, 0);

        allowSelections = true;
    }

    private void updateExchangeMessageLabel() {
        exchangeItemsSelected = 0;
        for (int i = 0; i < foodQuantitiesArray.size(); i++) {
            exchangeItemsSelected += Float.valueOf(foodQuantitiesArray.get(i));
        }

        if (exchangeItemsAllowed == 0) { // limitless exchange icon
            if (exchangeItemsSelected == 1) {

                exchangeMessageLabel.setText("1 item selected");

            } else {

                exchangeMessageLabel.setText(String.format("%.2f items selected",
                        exchangeItemsSelected));
            }
        } else { // traditional exchange item
            if (exchangeItemsAllowed == 1) {

                exchangeMessageLabel.setText(String.format("%.2f of 1 item selected",
                        exchangeItemsSelected));

            } else {

                exchangeMessageLabel.setText(String.format("%.2f of %d items selected",
                        exchangeItemsSelected,
                        (long) exchangeItemsAllowed));
            }

        }
    }


//    private class SearchResultListViewAdapter extends ArrayAdapter<String> {
//        private final Activity context;
//
//        public SearchResultListViewAdapter(Activity context) {
//            super(context, R.layout.searchresult_cell, addFoodID);
//            this.context = context;
//        }
//
//        @Override
//        public View getView(int position, View view, ViewGroup parent) {
//
//            ViewHolder holder = null;
//            LayoutInflater inflater = context.getLayoutInflater();
//
//            if (view == null) {
//                view = inflater.inflate(R.layout.searchresult_cell, null, true);
//                holder = new ViewHolder();
//                holder.foodTitleView = (TextView) view.findViewById(R.id.foodTitleView);
//                holder.foodSubTitleView = (TextView) view.findViewById(R.id.foodSubTitleView);
//                holder.quantityEdit = (TextView) view.findViewById(R.id.quantityEdit);
//                view.setTag(holder);
//            } else {
//                holder = (ViewHolder) view.getTag();
//            }
//
//            if (isExchangeItem) {
//                final int quantityIndex = position;
//                holder.quantityEdit.setVisibility(View.VISIBLE);
//                if (exchangeItemArray.contains(addFoodID.get(position))) {
//                    holder.quantityEdit.setText(exchangeItemQuantitiesArray.get(exchangeItemArray.indexOf(addFoodID.get(position))));
//                } else {
//                    holder.quantityEdit.setText("0.00");
//                }
//
//                holder.quantityEdit.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(final View v) {
//                        final String quantity = (String) ((TextView) v).getText();
//                        int index = 0;
//                        for (int i = 0; i < quantities.length; i++) {
//                            if (quantity.equals(quantities[i])) {
//                                index = i;
//                                break;
//                            }
//                        }
//                        Dialog dialog = new AlertDialog.Builder(HTAddFoodSearchResultsActivity.this)
//                                .setTitle("Quantities")
//                                .setSingleChoiceItems(quantities, index, new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog, int which) {
//                                        dialog.dismiss();
////                                        ((TextView) v).setText(quantities[which]);
//                                        updateExchangeMessageLabel();
//
//                                    }
//                                }).create();
//                        dialog.show();
//                    }
//                });
//            } else {
//                holder.quantityEdit.setVisibility(View.GONE);
//            }
//
//            holder.foodTitleView.setText(addFoodName.get(position));
//            if (!"".equals(addFoodCalories.get(position))) {
//                holder.foodSubTitleView.setVisibility(View.VISIBLE);
//                if ("general".equals(addFoodCategory) &&
//                        !"".equals(addFoodServings.get(position))) {
//                    holder.foodSubTitleView.setText(addFoodCalories.get(position) + " calories - " + addFoodServings.get(position));
//                } else {
//                    holder.foodSubTitleView.setText(addFoodCalories.get(position) + " calories");
//                }
//            } else {
//                holder.foodSubTitleView.setVisibility(View.GONE);
//            }
//
//            return view;
//        }
//
//    }
//
//    private static class ViewHolder {
//        public TextView foodTitleView;
//        public TextView foodSubTitleView;
//        public TextView quantityEdit;
//    }

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

        if (data != null) {
            addFoodCategory = data.getStringExtra("addFoodCategory");
            addFoodSearchString = data.getStringExtra("addFoodSearchString");
            relaunchItemID = data.getIntExtra("relaunchItemID", 0);
            addFoodSearchFieldString = "";
        }

        if (searchResultContentLayout.getChildCount() > 0)
            searchResultContentLayout.removeAllViews();
        headLayout.setVisibility(View.GONE);

        if ("general".equals(addFoodCategory) && "".equals(addFoodSearchFieldString)) {
            showSearchResults();
        } else {
            getSearchResults();
        }
    }
}
