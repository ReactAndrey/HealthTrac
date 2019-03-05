package com.sph.healthtrac.planner.addfood;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.sph.healthtrac.common.RangeSeekBar;
import com.sph.healthtrac.common.XMLFunctions;
import com.sph.healthtrac.planner.OnSwipeTouchListener;
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

public class HTNewAddFoodSearchActivity extends Activity {

    private RelativeLayout mainContentLayout;
    private View mActionBar;

    private EditText searchEdit;
    private ImageView barcodeBtn;

    private LinearLayout databaseSelectionLayout;
    private RelativeLayout recentBtn;
    private RelativeLayout allfoodsBtn;
    private RelativeLayout favoritesBtn;
    private RelativeLayout recommendedBtn;
    private TextView recentLabel;
    private TextView allfoodsLabel;
    private TextView favoritesLabel;
    private TextView recommendedLabel;
    private View recentIndicator;
    private View allfoodsIndicator;
    private View favoritesIndicator;
    private View recommendedIndicator;

    private RelativeLayout exchangeMsgLayout;
    private TextView exchangeMessageLabel;

    private RelativeLayout searchResultLayout;
    private TextView searchResultLabel;

    private ScrollView searchResultScrollView;
    private LinearLayout searchResultContentLayout;

    private RelativeLayout quickAddBtn;

    private LinearLayout advancedSearchLayout;
    private RelativeLayout advancedSearchExpandLayout;
    private LinearLayout caloriesSliderLayout;
    private LinearLayout mealTypeStarterLayout;
    private TextView advancedSearchLabel;
    private ImageView additionalFieldIcon;
    private TextView caloriesLabel;
    private RangeSeekBar caloriesSeekBar;
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
    private String addFoodSearchString = "";
    private String inTemplateString;
    private String generalFoodSearchString = "";
    private String exchangeItemsString = "";
    private String caloriesOrOtherString = "calories";
    private String foodSearchMeal;
    private String foodDatabaseSelection = "";

    private int numberOfResults;
    //    private int selectedFoodID;
    private int selectedFoodIndex;
    private int relaunchItemID;
    private int exchangeNumber;
    private int generalFoodSearchPhase;

    private boolean isExchangeItem;
    private boolean showExchangeItemTextSearch;

    private float exchangeItemsAllowed;
    private float exchangeItemsSelected;
    private List<String> exchangeItemArray = new ArrayList<>();
    private List<String> exchangeItemQuantitiesArray = new ArrayList<>();
    private List<String> foodQuantitiesArray = new ArrayList<>();

    private List<String> addFoodID = new ArrayList<>();
    private List<String> addFoodName = new ArrayList<>();
    private List<String> addFoodBrand = new ArrayList<>();
    private List<String> addFoodCalories = new ArrayList<>();
    private List<String> addFoodServings = new ArrayList<>();
    private List<String> addFoodRecentCategory = new ArrayList<>();

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

    private int grayColor = Color.rgb(80, 80, 87);
    private int blueColor = Color.rgb(116, 204, 240);

    private float displayDensity;

    private boolean quickAddIconWasClicked;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfood_search_new);

        openSansLightFont = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
        avenirNextRegularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");
        avenirNextDemiBoldFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-DemiBold.ttf");

        quantityPickerValues = getResources().getStringArray(R.array.quantity_values);
        quantityPickerValueFractions = getResources().getStringArray(R.array.quantity_fraction_values);

        addFoodCategory = getIntent().getStringExtra("addFoodCategory");
        foodSearchMeal = getIntent().getStringExtra("foodSearchMeal");

        allowSelections = false;
        inTemplateString = "false";
        foodDatabaseSelection = "recent";

        String title = "";

        if ("template".equals(addFoodCategory) || "exchange".equals(addFoodCategory)) {
            title = "Recommended";
            inTemplateString = "true";
        } else {
            if(!foodSearchMeal.equals("")){
                title = foodSearchMeal;
            } else {
                title = "Food Search";
            }
        }

        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        searchEdit = (EditText) findViewById(R.id.searchEdit);
        searchEdit.addTextChangedListener(textWatcher);
        searchEdit.setTypeface(avenirNextMediumFont);
        searchEdit.clearFocus();

        barcodeBtn = (ImageView) findViewById(R.id.barcodeBtn);
        barcodeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HTNewAddFoodSearchActivity.this, HTBarCodeCaptureActivity.class);
                intent.putExtra("foodSearchMeal", foodSearchMeal);
                startActivityForResult(intent, 1);
            }
        });

        databaseSelectionLayout = (LinearLayout) findViewById(R.id.databaseSelectionLayout);
        databaseSelectionLayout.setVisibility(View.GONE);
        recentBtn = (RelativeLayout) findViewById(R.id.recentButton);
        allfoodsBtn = (RelativeLayout) findViewById(R.id.allfoodsButton);
        favoritesBtn = (RelativeLayout) findViewById(R.id.favoritesButton);
        recommendedBtn = (RelativeLayout) findViewById(R.id.recommendedButton);

        recentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!"recent".equals(foodDatabaseSelection)) {
                    stopThread();
                    searchEdit.clearFocus();
                    imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                    recentLabel.setTextColor(blueColor);
                    allfoodsLabel.setTextColor(grayColor);
                    favoritesLabel.setTextColor(grayColor);
                    recommendedLabel.setTextColor(grayColor);
                    recentIndicator.setVisibility(View.VISIBLE);
                    allfoodsIndicator.setVisibility(View.GONE);
                    favoritesIndicator.setVisibility(View.GONE);
                    recommendedIndicator.setVisibility(View.GONE);

                    foodDatabaseSelection = "recent";
                    addFoodCategory = "recent";
                    updateSearchEditHint();
                    getSearchResults();
                }
            }
        });

        allfoodsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!"all".equals(foodDatabaseSelection)) {
                    stopThread();
                    searchEdit.clearFocus();
                    imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                    recentLabel.setTextColor(grayColor);
                    allfoodsLabel.setTextColor(blueColor);
                    favoritesLabel.setTextColor(grayColor);
                    recommendedLabel.setTextColor(grayColor);
                    recentIndicator.setVisibility(View.GONE);
                    allfoodsIndicator.setVisibility(View.VISIBLE);
                    favoritesIndicator.setVisibility(View.GONE);
                    recommendedIndicator.setVisibility(View.GONE);

                    foodDatabaseSelection = "all";
                    addFoodCategory = "general";
                    updateSearchEditHint();

                    if (searchEdit.length() >= 1) {

                        getSearchResults();

                    } else {

                        if (searchResultContentLayout.getChildCount() > 0)
                            searchResultContentLayout.removeAllViews();

                        searchEdit.requestFocus();
                        imm.showSoftInput(searchEdit, InputMethodManager.SHOW_IMPLICIT);
//                    imm.showSoftInputFromInputMethod(searchEdit.getWindowToken(), 0);

                    }
                }
            }
        });

        favoritesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!"favorites".equals(foodDatabaseSelection)) {
                    stopThread();
                    searchEdit.clearFocus();
                    imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                    recentLabel.setTextColor(grayColor);
                    allfoodsLabel.setTextColor(grayColor);
                    favoritesLabel.setTextColor(blueColor);
                    recommendedLabel.setTextColor(grayColor);
                    recentIndicator.setVisibility(View.GONE);
                    allfoodsIndicator.setVisibility(View.GONE);
                    favoritesIndicator.setVisibility(View.VISIBLE);
                    recommendedIndicator.setVisibility(View.GONE);

                    foodDatabaseSelection = "favorites";
                    addFoodCategory = "favorites";
                    updateSearchEditHint();
                    getSearchResults();
                }
            }
        });

        recommendedBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!"recommended".equals(foodDatabaseSelection)) {
                    stopThread();
                    searchEdit.clearFocus();
                    imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                    recentLabel.setTextColor(grayColor);
                    allfoodsLabel.setTextColor(grayColor);
                    favoritesLabel.setTextColor(grayColor);
                    recommendedLabel.setTextColor(blueColor);
                    recentIndicator.setVisibility(View.GONE);
                    allfoodsIndicator.setVisibility(View.GONE);
                    favoritesIndicator.setVisibility(View.GONE);
                    recommendedIndicator.setVisibility(View.VISIBLE);

                    foodDatabaseSelection = "recommended";
                    addFoodCategory = "recommended";
                    updateSearchEditHint();
                    getSearchResults();
                }
            }
        });

        recentLabel = (TextView) findViewById(R.id.recentLabelView);
        allfoodsLabel = (TextView) findViewById(R.id.allfoodsLabelView);
        favoritesLabel = (TextView) findViewById(R.id.favoritesLabelView);
        recommendedLabel= (TextView) findViewById(R.id.recommendedLabelView);
        recentLabel.setTypeface(avenirNextMediumFont);
        allfoodsLabel.setTypeface(avenirNextMediumFont);
        favoritesLabel.setTypeface(avenirNextMediumFont);
        recommendedLabel.setTypeface(avenirNextMediumFont);

        recentIndicator = findViewById(R.id.recentIndicator);
        allfoodsIndicator = findViewById(R.id.allfoodsIndicator);
        favoritesIndicator = findViewById(R.id.favoritesIndicator);
        recommendedIndicator = findViewById(R.id.recommendedIndicator);

        //initial selection(recent)
        recentLabel.setTextColor(blueColor);
        allfoodsLabel.setTextColor(grayColor);
        favoritesLabel.setTextColor(grayColor);
        recommendedLabel.setTextColor(grayColor);
        allfoodsIndicator.setVisibility(View.GONE);
        favoritesIndicator.setVisibility(View.GONE);
        recommendedIndicator.setVisibility(View.GONE);

        exchangeMsgLayout = (RelativeLayout) findViewById(R.id.exchangeMsgLayout);
        exchangeMsgLayout.setVisibility(View.GONE);
        exchangeMessageLabel = (TextView) findViewById(R.id.exchangeMessageLabel);
        exchangeMessageLabel.setTypeface(avenirNextDemiBoldFont);

        searchResultLayout = (RelativeLayout) findViewById(R.id.searchResultLayout);
        searchResultLayout.setVisibility(View.GONE);
        searchResultLabel = (TextView) findViewById(R.id.searchResultLabel);
        searchResultLabel.setTypeface(avenirNextRegularFont);

        searchResultScrollView = (ScrollView) findViewById(R.id.searchResultScrollView);
        searchResultScrollView.setVisibility(View.GONE);
        searchResultContentLayout = (LinearLayout) findViewById(R.id.searchResultContentLayout);

        quickAddBtn = (RelativeLayout) findViewById(R.id.quickAddBtn);
        quickAddBtn.setVisibility(View.GONE);
        quickAddBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quickAddIconWasClicked = true;
                Intent intent = new Intent(HTNewAddFoodSearchActivity.this, HTAddFoodSelectItemActivity.class);
                intent.putExtra("showChangeFoodSelection", false);
                intent.putExtra("addFoodCategory", "quickadd");
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

                // new planner time slots
                intent.putExtra("selectedFoodTimeFraction", ":00");
                if("Breakfast".equals(foodSearchMeal)){
                    intent.putExtra("selectedFoodTime", "9");
                    intent.putExtra("selectedFoodTimeAmPm", "am");
                } else if("Lunch".equals(foodSearchMeal)){
                    intent.putExtra("selectedFoodTime", "13");
                    intent.putExtra("selectedFoodTimeAmPm", "pm");
                } else if("Dinner".equals(foodSearchMeal)){
                    intent.putExtra("selectedFoodTime", "18");
                    intent.putExtra("selectedFoodTimeAmPm", "pm");
                } else { //snack
                    intent.putExtra("selectedFoodTime", "16");
                    intent.putExtra("selectedFoodTimeAmPm", "pm");
                }

                startActivityForResult(intent, 1);
            }
        });

        advancedSearchLayout = (LinearLayout) findViewById(R.id.advancedSearchLayout);
        advancedSearchExpandLayout = (RelativeLayout) findViewById(R.id.advancedSearchExpandLayout);
        caloriesSliderLayout = (LinearLayout) findViewById(R.id.caloriesSliderLayout);
        caloriesLabel = (TextView) findViewById(R.id.caloriesLabel);
        caloriesSeekBar = (RangeSeekBar) findViewById(R.id.caloriesSeekBar);
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
        caloriesLabel.setTypeface(avenirNextMediumFont);
        mealTypeStarterLabel.setTypeface(avenirNextMediumFont);
        smoothieLabel.setTypeface(avenirNextMediumFont);
        soupLabel.setTypeface(avenirNextMediumFont);
        pastaLabel.setTypeface(avenirNextMediumFont);
        eggLabel.setTypeface(avenirNextMediumFont);
        snackLabel.setTypeface(avenirNextMediumFont);
        groceryLabel.setTypeface(avenirNextMediumFont);

        caloriesSliderLayout.setVisibility(View.GONE);
        mealTypeStarterLayout.setVisibility(View.GONE);

        advancedSearchExpandLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAdditionalSearchFields = !showAdditionalSearchFields;
                if (showAdditionalSearchFields) {
                    additionalFieldIcon.setImageResource(R.drawable.ht_expand_content_minus);
                    caloriesSliderLayout.setVisibility(View.VISIBLE);
                    mealTypeStarterLayout.setVisibility(View.VISIBLE);
                } else {
                    additionalFieldIcon.setImageResource(R.drawable.ht_expand_content_plus);
                    caloriesSliderLayout.setVisibility(View.GONE);
                    mealTypeStarterLayout.setVisibility(View.GONE);
                }
            }
        });

        caloriesSeekBar.setOnRangeSeekBarChangeListener(new RangeSeekBar.OnRangeSeekBarChangeListener() {
            @Override
            public void onRangeSeekBarValuesUpdated(RangeSeekBar bar, Object minValue, Object maxValue) {
                getSearchResults();
            }

            @Override
            public void onRangeSeekBarValuesChanged(RangeSeekBar bar, Object minValue, Object maxValue) {
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
        mActionBar = HTActionBar.getActionBar(this, title, "leftArrow", "");

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

        /*LayoutInflater mInflater = LayoutInflater.from(this);
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

        HTGlobals.getInstance().exchangeItemTextSearch = false;*/

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            getSearchResults();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        quickAddIconWasClicked = false;
    }

    private void getSearchResults() {

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        addFoodID.clear();
        addFoodName.clear();
        addFoodBrand.clear();
        addFoodCalories.clear();
        addFoodServings.clear();
        addFoodRecentCategory.clear();

        exchangeItemArray.clear();
        foodQuantitiesArray.clear();
        exchangeItemQuantitiesArray.clear();

        numberOfResults = 0;
        selectedFoodIndex = 0;
        isExchangeItem = false;
        showExchangeItemTextSearch = false;

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
        if("recommended".equals(addFoodCategory) && newRecommendedFoods) {

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

        // calories
        String caloriesString = "";
        if("recommended".equals(addFoodCategory) && newRecommendedFoods) {
            caloriesString = "&calories=" + caloriesSeekBar.getSelectedMinValue().intValue() + ";" + caloriesSeekBar.getSelectedMaxValue().intValue();
        }

        exchangeItemsString = "";

        // init

        String searchKey = "";
        try {
            searchKey = URLEncoder.encode(searchEdit.getText().toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
        }

        addFoodSearchString = "WhichCategory=" + addFoodCategory + "&search=" + searchKey + "&template=" + inTemplateString
                + "&relaunch=" + relaunchItemID + "&prep=" + prepString + "&meal_type=" + mealTypeString + caloriesString;


        if ("template".equals(addFoodCategory) && relaunchItemID > 0 && "".equals(searchEdit.getText().toString())) {

            addFoodSearchString = "WhichCategory=" + addFoodCategory + "&search=" + "" + "&template=" + inTemplateString
                    + "&relaunch=" + relaunchItemID + "&prep=" + prepString;
        } else if("template".equals(addFoodCategory) && relaunchItemID > 0 && !"".equals(searchEdit.getText().toString())) {
            addFoodSearchString = "WhichCategory=" + addFoodCategory + "&search=" + searchKey + "&template=" + inTemplateString
                    + "&relaunch=" + relaunchItemID + "&prep=" + prepString;
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
                    nameValuePairs.add(new BasicNameValuePair("nutritionix", "true"));
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

                                toast = HTToast.showToast(HTNewAddFoodSearchActivity.this, errorMessage, Toast.LENGTH_LONG);
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
                                    tempString = XMLFunctions.getValue(e2, "food_brand_" + i);
                                    addFoodBrand.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "food_calories_" + i);
                                    addFoodCalories.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "food_servings_" + i);
                                    addFoodServings.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "food_recent_category_" + i);
                                    addFoodRecentCategory.add(htGlobals.cleanStringAfterReceiving(tempString));
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

//                                    toast = HTToast.showToast(HTNewAddFoodSearchActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTNewAddFoodSearchActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

                                toast = HTToast.showToast(HTNewAddFoodSearchActivity.this, errorMessage, Toast.LENGTH_LONG);
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

//                                    toast = HTToast.showToast(HTNewAddFoodSearchActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTNewAddFoodSearchActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void deleteRecent() {
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_food_delete_recent"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("WhichID", addFoodID.get(selectedFoodIndex)));

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

                                toast = HTToast.showToast(HTNewAddFoodSearchActivity.this, errorMessage, Toast.LENGTH_LONG);
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

//                                    toast = HTToast.showToast(HTNewAddFoodSearchActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTNewAddFoodSearchActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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


    private void stopThread() {

        if (myThread != null) {

            new Thread() {

                @Override
                public void run() {

                    if (post != null) {

                        post.abort();
                        post = null;
                        response = null;
                        entity = null;
                    }
                }
            }.start();

            myThread.interrupt();
            myThread = null;
        }
    }

    private void updateSearchEditHint() {
        if ("".equals(searchEdit.getText().toString())) {
            if ("recent".equals(foodDatabaseSelection)) {

                searchEdit.setHint("Search recent foods");

            } else if ("all".equals(foodDatabaseSelection)) {

                searchEdit.setHint("Search all foods");

            } else if ("favorites".equals(foodDatabaseSelection)) {

                searchEdit.setHint("Search favorite foods");

            } else if ("recommended".equals(foodDatabaseSelection)) {

                searchEdit.setHint("Search recommended foods");

            } else { // should never happen

                searchEdit.setHint("Search for a food");
            }

        }
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

//                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                addFoodSearchString = "WhichCategory=" + addFoodCategory + "&search=" + addFoodSearchFieldString + "&template=" + inTemplateString
                        + "&relaunch=" + relaunchItemID;

                getSearchResults();
            }
        };

        public void afterTextChanged(final Editable s) {

            addFoodSearchFieldString = s.toString();

            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 1000); // 2-second delay
        }
    };

    private void showSearchResults() {
        if ("recommended".equals(addFoodCategory) && newRecommendedFoods) {
            advancedSearchLayout.setVisibility(View.VISIBLE);
        } else {
            advancedSearchLayout.setVisibility(View.GONE);
        }
        foodQuantitiesArray.clear();
        if (searchResultContentLayout.getChildCount() > 0)
            searchResultContentLayout.removeAllViews();

        databaseSelectionLayout.setVisibility(View.VISIBLE);
        searchResultScrollView.setVisibility(View.VISIBLE);
        quickAddBtn.setVisibility(View.VISIBLE);

        if ("general".equals(addFoodCategory) && searchEdit.getText().toString().trim().length() == 0) {
            imm.showSoftInputFromInputMethod(mainContentLayout.getWindowToken(), 0);
        }

        // exchange item!
        if (isExchangeItem) {
            exchangeMsgLayout.setVisibility(View.VISIBLE);
            updateExchangeMessageLabel();
        } else {
            exchangeMsgLayout.setVisibility(View.GONE);
        }

        // number of search results
        if (!("general".equals(addFoodCategory) && "".equals(addFoodSearchFieldString)) && !"template".equals(addFoodCategory) && !"exchange".equals(addFoodCategory) && numberOfResults == 0) {

            String databaseSelectionString = "";

            if ("recent".equals(foodDatabaseSelection)) {

                databaseSelectionString = "in Recent";

            } else if ("all".equals(foodDatabaseSelection)) {

                databaseSelectionString = "in All Foods";

            } else if ("favorites".equals(foodDatabaseSelection)) {

                databaseSelectionString = "in Favorites";

            } else if ("recommended".equals(foodDatabaseSelection)) {

                databaseSelectionString = "in Recommended";
            }

            searchResultLabel.setText("No results found " + databaseSelectionString);

            searchResultLayout.setVisibility(View.VISIBLE);
        }else{
            searchResultLayout.setVisibility(View.GONE);
        }

        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        for (int i = 0; i < addFoodID.size(); i++) {
            final int index = i;
            View foodItemCell = inflater.inflate(R.layout.searchresult_cell_new, null);
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

            if(!"".equals(addFoodBrand.get(index)))
                foodTitleView.setText(addFoodBrand.get(index) + " - " + HTGlobals.capitalize(addFoodName.get(index).toLowerCase()));
            else
                foodTitleView.setText(HTGlobals.capitalize(addFoodName.get(index).toLowerCase()));

            if (!"".equals(addFoodCalories.get(index))) {
                foodSubTitleView.setVisibility(View.VISIBLE);
//                if ("general".equals(addFoodCategory) &&
//                        !"".equals(addFoodServings.get(index))) {
//                if (!"".equals(addFoodServings.get(index))) {
//                    foodSubTitleView.setText(addFoodServings.get(index) + " - " + addFoodCalories.get(index) + " " + caloriesOrOtherString);
//                } else {
                    foodSubTitleView.setText(addFoodCalories.get(index) + " " + caloriesOrOtherString);
//                }
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
                        Intent intent = new Intent(HTNewAddFoodSearchActivity.this, HTAddFoodSelectItemActivity.class);
                        intent.putExtra("showChangeFoodSelection", false);
                        if ("recent".equals(addFoodCategory))
                            intent.putExtra("addFoodCategory", addFoodRecentCategory.get(index));
                        else
                            intent.putExtra("addFoodCategory", addFoodCategory);
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

                        // new planner time slots
                        intent.putExtra("selectedFoodTimeFraction", ":00");
                        if("Breakfast".equals(foodSearchMeal)){
                            intent.putExtra("selectedFoodTime", "9");
                            intent.putExtra("selectedFoodTimeAmPm", "am");
                        } else if("Lunch".equals(foodSearchMeal)){
                            intent.putExtra("selectedFoodTime", "13");
                            intent.putExtra("selectedFoodTimeAmPm", "pm");
                        } else if("Dinner".equals(foodSearchMeal)){
                            intent.putExtra("selectedFoodTime", "18");
                            intent.putExtra("selectedFoodTimeAmPm", "pm");
                        } else { //snack
                            intent.putExtra("selectedFoodTime", "16");
                            intent.putExtra("selectedFoodTimeAmPm", "pm");
                        }

                        startActivityForResult(intent, 1);
                    }
                });
            }

            if ("favorites".equals(addFoodCategory)) {
//                foodItemCell.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        selectedFoodIndex = index;
//                        showConfirmEditDeleteFravoriteItem();
//                        return false;
//                    }
//                });

                foodItemCell.setOnTouchListener(new OnSwipeTouchListener(this) {
                    @Override
                    public void onLongPressEvent(){
                        selectedFoodIndex = index;
                        showConfirmEditDeleteFravoriteItem();
                    }

                    @Override
                    public void onSwipeLeft() {
                        selectedFoodIndex = index;
                        showConfirmEditDeleteFravoriteItem();
                    }

                    @Override
                    public void onSwipeRight() {
                        selectedFoodIndex = index;
                        showConfirmEditDeleteFravoriteItem();
                    }
                });

            }

            if ("recent".equals(addFoodCategory)) {
//                foodItemCell.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        selectedFoodIndex = index;
//                        showConfirmDeleteRecentItem();
//                        return false;
//                    }
//                });

                foodItemCell.setOnTouchListener(new OnSwipeTouchListener(this) {
                    @Override
                    public void onLongPressEvent(){
                        selectedFoodIndex = index;
                        showConfirmDeleteRecentItem();
                    }

                    @Override
                    public void onSwipeLeft() {
                        selectedFoodIndex = index;
                        showConfirmDeleteRecentItem();
                    }

                    @Override
                    public void onSwipeRight() {
                        selectedFoodIndex = index;
                        showConfirmDeleteRecentItem();
                    }
                });
            }

            searchResultContentLayout.addView(foodItemCell);
        }

        // search all foods button
        if(!"all".equals(foodDatabaseSelection) && searchEdit.getText().toString().trim().length() > 0){

            LinearLayout.LayoutParams layoutParams;

            // WHITE separator to cover up the last gray separator but keep the same offset
            View view = new View(this);
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (2*displayDensity));
            layoutParams.setMargins(0, (int)(-2*displayDensity), 0, 0);
            view.setLayoutParams(layoutParams);
            view.setBackgroundColor(Color.WHITE);
            searchResultContentLayout.addView(view);

            LinearLayout searchAllFoodsContainer = new LinearLayout(this);
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (54*displayDensity));
            searchAllFoodsContainer.setBackgroundColor(Color.rgb(247, 249,250));
            searchAllFoodsContainer.setLayoutParams(layoutParams);
            searchAllFoodsContainer.setPadding((int) (12*displayDensity), (int) (10*displayDensity), (int) (12*displayDensity), (int) (10*displayDensity));

            searchResultContentLayout.addView(searchAllFoodsContainer);

            LinearLayout searchAllFoodsBtn = new LinearLayout(this);
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            searchAllFoodsBtn.setBackgroundResource(R.drawable.ht_nutrition_button);
            searchAllFoodsBtn.setGravity(Gravity.CENTER_VERTICAL);
            searchAllFoodsBtn.setLayoutParams(layoutParams);
            searchAllFoodsContainer.addView(searchAllFoodsBtn);

            ImageView searchIconView = new ImageView(this);
            searchIconView.setImageResource(R.drawable.ht_icon_search_blue);
            layoutParams = new LinearLayout.LayoutParams((int) (24*displayDensity), (int) (24*displayDensity));
            layoutParams.setMargins((int) (6*displayDensity), 0, 0, 0);
            searchIconView.setLayoutParams(layoutParams);
            searchAllFoodsBtn.addView(searchIconView);

            TextView labelView = new TextView(this);
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins((int) (6*displayDensity), 0, (int) (10*displayDensity), 0);
            labelView.setLayoutParams(layoutParams);
            labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            labelView.setTextColor(getResources().getColor(R.color.ht_blue));
            labelView.setTypeface(avenirNextRegularFont);
            labelView.setGravity(Gravity.CENTER);
            labelView.setSingleLine(true);
            labelView.setEllipsize(TextUtils.TruncateAt.END);
            searchAllFoodsBtn.addView(labelView);

            labelView.setText("Search all foods for " + searchEdit.getText().toString());

            searchAllFoodsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(!"all".equals(foodDatabaseSelection)) {
                        stopThread();
                        searchEdit.clearFocus();
                        imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                        recentLabel.setTextColor(grayColor);
                        allfoodsLabel.setTextColor(blueColor);
                        favoritesLabel.setTextColor(grayColor);
                        recommendedLabel.setTextColor(grayColor);
                        recentIndicator.setVisibility(View.GONE);
                        allfoodsIndicator.setVisibility(View.VISIBLE);
                        favoritesIndicator.setVisibility(View.GONE);
                        recommendedIndicator.setVisibility(View.GONE);

                        foodDatabaseSelection = "all";
                        addFoodCategory = "general";

                        if (searchEdit.length() >= 1) {

                            getSearchResults();

                        } else {

                            if (searchResultContentLayout.getChildCount() > 0)
                                searchResultContentLayout.removeAllViews();

                            searchEdit.requestFocus();
                            imm.showSoftInput(searchEdit, InputMethodManager.SHOW_IMPLICIT);
//                    imm.showSoftInputFromInputMethod(searchEdit.getWindowToken(), 0);

                        }
                    }
                }
            });

        }

        searchResultScrollView.scrollTo(0, 0);

        allowSelections = true;

    }

    private void showConfirmEditDeleteFravoriteItem(){
        Dialog dialog = new AlertDialog.Builder(HTNewAddFoodSearchActivity.this)
                .setTitle("Edit or Delete Favorite?")
                .setCancelable(false)
                .setMessage("Would you like to edit or delete this Favorites item?")
                .setPositiveButton("Edit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(HTNewAddFoodSearchActivity.this, HTCreateFavoritesActivity.class);
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
    }

    private void showConfirmDeleteRecentItem(){
        Dialog dialog = new AlertDialog.Builder(HTNewAddFoodSearchActivity.this)
                .setTitle("Delete Recent Item?")
                .setMessage("Would you like to delete this Recent item?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteRecent();
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
            getSearchResults();
        }
    }

}
