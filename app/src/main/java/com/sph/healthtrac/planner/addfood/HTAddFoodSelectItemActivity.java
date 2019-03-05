package com.sph.healthtrac.planner.addfood;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTTimePickerDialog;
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

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HTAddFoodSelectItemActivity extends Activity {

    private static int TAKE_PHOTO_FROM_CAMERA = 100;
    private static RelativeLayout mainContentLayout;
    private ScrollView scrollView;

    private LinearLayout layoutAddFoodSelect;
    private RelativeLayout changeFoodSelectionLayout;
    private TextView changeFoodSelectionLabel;
    private RelativeLayout foodItemLayout;
    private TextView foodNameLabel;
    private ImageView   foodItemsDetailBtn;
    private LinearLayout quantityLayout;
    private TextView quantityLabel;
    private TextView quantityEdit;
    private LinearLayout brandLayout;
    private TextView brandLabel;
    private TextView brandView;
    private LinearLayout servingSizeLayout;
    private TextView servingSizeLabel;
    private TextView servingSizeView;
    private LinearLayout mealSelectionLayout;
    private TextView whichMealLabel;
    private TextView mealTypeEdit;
    private LinearLayout textReminderLayout;
    private TextView textReminderLabel;
    private TextView reminderTimeEdit;
    private LinearLayout addFavoritesLayout;
    private TextView addFavoritesLabel;
    private ImageView favoriteCheck;
    private LinearLayout photoLayout;
    private TextView photoLabel;
    private ImageView photoView;

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

    //quick add
    private LinearLayout layoutQuickAddFood;
    private EditText foodnameEdit;
    private TextView whichMealLabel2;
    private TextView mealTypeEdit2;
    private TextView caloriesLabel2;
    private EditText caloriesEdit;
    private TextView addFavoritesLabel2;
    private ImageView favoriteCheck2;

    private TextView proteinLabel2;
    private TextView carbsLabel2;
    private TextView fatLabel;
    private TextView satfatLabel2;
    private TextView sugarsLabel2;
    private TextView fiberLabel2;
    private TextView sodiumLabel2;

    private EditText proteinEdit;
    private EditText carbsEdit;
    private EditText fatEdit;
    private EditText satfatEdit;
    private EditText sugarsEdit;
    private EditText fiberEdit;
    private EditText sodiumEdit;

    private LinearLayout quickPhotoLayout;
    private TextView quickPhotoLabel;
    private ImageView quickPhotoView;

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
    private boolean relaunchPlannerItem;
    private boolean showChangeFoodSelection;

    private String selectedFoodName;
    private String selectedFoodDetailsID;
    private String selectedFoodCalories;
    private String selectedFoodProtein;
    private String selectedFoodCarbs;
    private String selectedFoodFiber;
    private String selectedFoodSugar;
    private String selectedFoodSodium;
    private String selectedFoodFat;
    private String selectedFoodSatFat;
    private String selectedFoodServings;
    private String selectedFoodBrand;

    private String selectedFoodQuantity;
    private String selectedFoodQuantityFraction;
    private int selectedMealType;
    private String selectedFoodTime;
    private String selectedFoodTimeFraction;
    private String selectedFoodTimeAmPm;
    private String selectedFoodReminder;
    private String selectedFoodReminderFraction;
    private String selectedFoodReminderAmPm;
    private String selectedFoodReminderYN;
    private String selectedFoodAddToFavorites;
    private String selectedFoodRelaunchItem;
    private String selectedFoodRelaunchItemID;
    private String selectedFoodExchangeNumber;
    private String selectedFoodTemplate;

    boolean addFoodToFavorites;
    boolean doneAddingFood;

    private String selectedFoodID;
    private int relaunchItemID;

    private String inTemplateString;
    private String exchangeItemsString;

    private Date foodTime;
    private Date foodReminder;

    private String[] mealTypeValues;
    private String[] quantityPickerValues;
    private String[] quantityPickerValueFractions;
    private Dialog chooseQuantityDlg;
    private NumberPicker picker1;
    private NumberPicker picker2;

    private String thisPlannerItemID;
    private Uri mPhotoUri;
    private Bitmap finalBitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfood_selectitem);

        mealTypeValues = getResources().getStringArray(R.array.meal_types);
        quantityPickerValues = getResources().getStringArray(R.array.quantity_values);
        quantityPickerValueFractions = getResources().getStringArray(R.array.quantity_fraction_values);

        showChangeFoodSelection = getIntent().getBooleanExtra("showChangeFoodSelection", false);
        addFoodCategory = getIntent().getStringExtra("addFoodCategory");
        selectedFoodID = getIntent().getStringExtra("selectedFoodID");
        relaunchItemID = getIntent().getIntExtra("relaunchItemID", 0);
        relaunchPlannerItem = getIntent().getBooleanExtra("relaunchPlannerItem", false);
        inTemplateString = getIntent().getStringExtra("inTemplateString");
        exchangeItemsString = getIntent().getStringExtra("exchangeItemsString");
        selectedFoodTime = getIntent().getStringExtra("selectedFoodTime");

        if (inTemplateString == null)
            inTemplateString = "";

        if (selectedFoodTime == null)
            selectedFoodTime = "";

        selectedFoodReminder = "";
        selectedFoodReminderFraction = "";
        selectedFoodReminderYN = "";

        mainContentLayout = (RelativeLayout) findViewById(R.id.relativeLayoutAddFoodSelect);
        scrollView = (ScrollView) findViewById(R.id.scrollViewAddFoodSelect);

        layoutAddFoodSelect = (LinearLayout) findViewById(R.id.layoutAddFoodSelect);
        changeFoodSelectionLayout = (RelativeLayout) findViewById(R.id.changeFoodSelectionLayout);
        changeFoodSelectionLabel = (TextView) findViewById(R.id.changeFoodSelectionLabel);
        foodItemLayout = (RelativeLayout) findViewById(R.id.foodItemLayout);
        foodNameLabel = (TextView) findViewById(R.id.foodNameLabel);
        foodItemsDetailBtn = (ImageView) findViewById(R.id.foodItemsDetailBtn);
        quantityLayout = (LinearLayout) findViewById(R.id.quantityLayout);
        quantityLabel = (TextView) findViewById(R.id.quantityLabel);
        quantityEdit = (TextView) findViewById(R.id.quantityEdit);
        brandLayout = (LinearLayout) findViewById(R.id.brandLayout);
        brandLabel = (TextView) findViewById(R.id.brandLabel);
        brandView = (TextView) findViewById(R.id.brandView);
        servingSizeLayout = (LinearLayout) findViewById(R.id.servingSizeLayout);
        servingSizeLabel = (TextView) findViewById(R.id.servingSizeLabel);
        servingSizeView = (TextView) findViewById(R.id.servingSizeView);
        mealSelectionLayout = (LinearLayout) findViewById(R.id.mealSelectionLayout);
        whichMealLabel = (TextView) findViewById(R.id.whichMealLabel);
        mealTypeEdit = (TextView) findViewById(R.id.mealTypeEdit);
        textReminderLayout = (LinearLayout) findViewById(R.id.textReminderLayout);
        textReminderLabel = (TextView) findViewById(R.id.textReminderLabel);
        reminderTimeEdit = (TextView) findViewById(R.id.reminderTimeEdit);
        addFavoritesLayout = (LinearLayout) findViewById(R.id.addFavoritesLayout);
        addFavoritesLabel = (TextView) findViewById(R.id.addFavoritesLabel);
        favoriteCheck = (ImageView) findViewById(R.id.favoriteCheck);
        photoLayout = (LinearLayout) findViewById(R.id.photoLayout);
        photoLabel = (TextView) findViewById(R.id.photoLabel);
        photoView = (ImageView) findViewById(R.id.photoView);
        photoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoFromCamera();
            }
        });


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

        Typeface avenirNextRegularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        Typeface avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");

        changeFoodSelectionLabel.setTypeface(avenirNextMediumFont);
        foodNameLabel.setTypeface(avenirNextMediumFont);
        quantityLabel.setTypeface(avenirNextRegularFont);
        quantityEdit.setTypeface(avenirNextRegularFont);
        whichMealLabel.setTypeface(avenirNextRegularFont);
        mealTypeEdit.setTypeface(avenirNextRegularFont);
        textReminderLabel.setTypeface(avenirNextRegularFont);
        reminderTimeEdit.setTypeface(avenirNextRegularFont);
        addFavoritesLabel.setTypeface(avenirNextRegularFont);
        photoLabel.setTypeface(avenirNextRegularFont);
        brandLabel.setTypeface(avenirNextRegularFont);
        brandView.setTypeface(avenirNextRegularFont);
        servingSizeLabel.setTypeface(avenirNextRegularFont);
        servingSizeView.setTypeface(avenirNextRegularFont);

        summaryLabel.setTypeface(avenirNextMediumFont);
        caloriesLabel.setTypeface(avenirNextMediumFont);
        caloriesValue.setTypeface(avenirNextMediumFont);
        carbsLabel.setTypeface(avenirNextMediumFont);
        fiberLabel.setTypeface(avenirNextRegularFont);
        sugarsLabel.setTypeface(avenirNextRegularFont);
        proteinLabel.setTypeface(avenirNextMediumFont);
        totalFatLabel.setTypeface(avenirNextMediumFont);
        satfatLabel.setTypeface(avenirNextRegularFont);
        sodiumLabel.setTypeface(avenirNextMediumFont);
        carbsValue.setTypeface(avenirNextMediumFont);
        fiberValue.setTypeface(avenirNextRegularFont);
        sugarsValue.setTypeface(avenirNextRegularFont);
        proteinValue.setTypeface(avenirNextMediumFont);
        totalFatValue.setTypeface(avenirNextMediumFont);
        satfatValue.setTypeface(avenirNextRegularFont);
        sodiumValue.setTypeface(avenirNextMediumFont);

        //quick add
        layoutQuickAddFood = (LinearLayout) findViewById(R.id.layoutQuickAddFood);
        foodnameEdit = (EditText) findViewById(R.id.foodnameEdit);
        whichMealLabel2 = (TextView) findViewById(R.id.whichMealLabel2);
        mealTypeEdit2 = (TextView) findViewById(R.id.mealTypeEdit2);
        caloriesLabel2 = (TextView) findViewById(R.id.caloriesLabel2);
        caloriesEdit = (EditText) findViewById(R.id.caloriesEdit);
        addFavoritesLabel2 = (TextView) findViewById(R.id.addFavoritesLabel2);
        favoriteCheck2 = (ImageView) findViewById(R.id.favoriteCheck2);
        quickPhotoLayout = (LinearLayout) findViewById(R.id.quickPhotoLayout);
        quickPhotoLabel = (TextView) findViewById(R.id.quickPhotoLabel);
        quickPhotoView = (ImageView) findViewById(R.id.quickPhotoView);
        quickPhotoView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePhotoFromCamera();
            }
        });

        proteinLabel2 = (TextView) findViewById(R.id.proteinLabel2);
        carbsLabel2 = (TextView) findViewById(R.id.carbsLabel2);
        fatLabel = (TextView) findViewById(R.id.fatLabel);
        satfatLabel2 = (TextView) findViewById(R.id.satfatLabel2);
        sugarsLabel2 = (TextView) findViewById(R.id.sugarsLabel2);
        fiberLabel2 = (TextView) findViewById(R.id.fiberLabel2);
        sodiumLabel2 = (TextView) findViewById(R.id.sodiumLabel2);

        proteinEdit = (EditText) findViewById(R.id.proteinEdit);
        carbsEdit = (EditText) findViewById(R.id.carbsEdit);
        fatEdit = (EditText) findViewById(R.id.fatEdit);
        satfatEdit = (EditText) findViewById(R.id.satfatEdit);
        sugarsEdit = (EditText) findViewById(R.id.sugarsEdit);
        fiberEdit = (EditText) findViewById(R.id.fiberEdit);
        sodiumEdit = (EditText) findViewById(R.id.sodiumEdit);

        foodnameEdit.setTypeface(avenirNextMediumFont);
        whichMealLabel2.setTypeface(avenirNextRegularFont);
        mealTypeEdit2.setTypeface(avenirNextRegularFont);
        caloriesLabel2.setTypeface(avenirNextRegularFont);
        caloriesEdit.setTypeface(avenirNextMediumFont);
        addFavoritesLabel2.setTypeface(avenirNextRegularFont);
        quickPhotoLabel.setTypeface(avenirNextRegularFont);

        proteinLabel2.setTypeface(avenirNextRegularFont);
        carbsLabel2.setTypeface(avenirNextRegularFont);
        fatLabel.setTypeface(avenirNextRegularFont);
        satfatLabel2.setTypeface(avenirNextRegularFont);
        sugarsLabel2.setTypeface(avenirNextRegularFont);
        fiberLabel2.setTypeface(avenirNextRegularFont);
        sodiumLabel2.setTypeface(avenirNextRegularFont);

        proteinEdit.setTypeface(avenirNextMediumFont);
        carbsEdit.setTypeface(avenirNextMediumFont);
        fatEdit.setTypeface(avenirNextMediumFont);
        satfatEdit.setTypeface(avenirNextMediumFont);
        sugarsEdit.setTypeface(avenirNextMediumFont);
        fiberEdit.setTypeface(avenirNextMediumFont);
        sodiumEdit.setTypeface(avenirNextMediumFont);

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
        View mActionBar = null;
//        if (relaunchPlannerItem == true &&
//                ("template".equals(addFoodCategory) ||
//                        "exchange".equals(addFoodCategory))) {
//            mActionBar = HTActionBar.getActionBar(this, "Add Food", "Change Item", "checkMark");
//        } else {
//            mActionBar = HTActionBar.getActionBar(this, "Add Food", "leftArrow", "checkMark");
//        }
        mActionBar = HTActionBar.getActionBar(this, "Add Food", "leftArrow", "checkMark");

        if("quickadd".equals(addFoodCategory)) {
            mActionBar = HTActionBar.getActionBar(this, "Quick Add", "leftArrow", "checkMark");
        }
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

        // left change item button
//        mActionBar.findViewById(R.id.leftButton)
        changeFoodSelectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HTAddFoodSelectItemActivity.this, HTAddFoodSearchResultsActivity.class);
                intent.putExtra("addFoodCategory", addFoodCategory);
                intent.putExtra("relaunchItemID", relaunchItemID);
                String addFoodSearchString = "WhichCategory=" + addFoodCategory + "&search=&template=true&relaunch=" + relaunchItemID;
                intent.putExtra("addFoodSearchString", addFoodSearchString);
                startActivityForResult(intent, 1);
            }
        });

        // right check button
        mActionBar.findViewById(R.id.rightCheckAction).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if ("quickadd".equals(addFoodCategory)) {
                    String checkStringName = foodnameEdit.getText().toString().trim();
                    String checkStringCalories = caloriesEdit.getText().toString().trim();

                    if ("".equals(checkStringName)) {
                        HTToast.showToast(HTAddFoodSelectItemActivity.this, "Please enter a name for this food item", Toast.LENGTH_SHORT);
                        foodnameEdit.requestFocus();
                        return;
                    } else if ("".equals(checkStringCalories)) {
                        HTToast.showToast(HTAddFoodSelectItemActivity.this, "Please enter the calories for this food item", Toast.LENGTH_SHORT);
                        caloriesEdit.requestFocus();
                        return;
                    } else{
                        imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                        addFoodItem();
                    }
                }else {
                    addFoodItem();
                }
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
        picker1.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if (newVal == 0 && picker2.getValue() == 0) {
                    picker2.setValue(1);
                }
            }
        });
        picker2.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
                if(newVal == 0 && picker1.getValue() == 0){
                    picker.setValue(1);
                }
            }
        });
        chooseQuantityDlg = new AlertDialog.Builder(this)
                .setTitle("Quantity")
                .setView(dlgContentView)
                .setPositiveButton("Set", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String quantity = quantityPickerValues[picker1.getValue()] + quantityPickerValueFractions[picker2.getValue()];
                        quantityEdit.setText(quantity);

                        selectedFoodQuantity = quantityPickerValues[picker1.getValue()];
                        selectedFoodQuantityFraction = quantityPickerValueFractions[picker2.getValue()];
                        float newValue;
                        // calories
                        try {
                            newValue = Float.parseFloat(selectedFoodCalories) * (Integer.parseInt(selectedFoodQuantity) + Float.parseFloat(selectedFoodQuantityFraction));
                        } catch (NumberFormatException nfe) {
                            newValue = 0;
                        }
                        caloriesValue.setText(String.format("%.0f", newValue));

                        // carbs
                        try {
                            newValue = Float.parseFloat(selectedFoodCarbs) * (Integer.parseInt(selectedFoodQuantity) + Float.parseFloat(selectedFoodQuantityFraction));
                        } catch (NumberFormatException nfe) {
                            newValue = 0;
                        }
                        carbsValue.setText(String.format("%.0fg", newValue));

                        //fiber
                        try {
                            newValue = Float.parseFloat(selectedFoodFiber) * (Integer.parseInt(selectedFoodQuantity) + Float.parseFloat(selectedFoodQuantityFraction));
                        } catch (NumberFormatException nfe) {
                            newValue = 0;
                        }
                        fiberValue.setText(String.format("%.0fg", newValue));

                        //sugar
                        try {
                            newValue = Float.parseFloat(selectedFoodSugar) * (Integer.parseInt(selectedFoodQuantity) + Float.parseFloat(selectedFoodQuantityFraction));
                        } catch (NumberFormatException nfe) {
                            newValue = 0;
                        }
                        sugarsValue.setText(String.format("%.0fg", newValue));

                        //protein
                        try {
                            newValue = Float.parseFloat(selectedFoodProtein) * (Integer.parseInt(selectedFoodQuantity) + Float.parseFloat(selectedFoodQuantityFraction));
                        } catch (NumberFormatException nfe) {
                            newValue = 0;
                        }
                        proteinValue.setText(String.format("%.0fg", newValue));

                        //total fat
                        try {
                            newValue = Float.parseFloat(selectedFoodFat) * (Integer.parseInt(selectedFoodQuantity) + Float.parseFloat(selectedFoodQuantityFraction));
                        } catch (NumberFormatException nfe) {
                            newValue = 0;
                        }
                        totalFatValue.setText(String.format("%.0fg", newValue));

                        //sat fat
                        try {
                            newValue = Float.parseFloat(selectedFoodSatFat) * (Integer.parseInt(selectedFoodQuantity) + Float.parseFloat(selectedFoodQuantityFraction));
                        } catch (NumberFormatException nfe) {
                            newValue = 0;
                        }
                        satfatValue.setText(String.format("%.0fg", newValue));

                        //sodium
                        try {
                            newValue = Float.parseFloat(selectedFoodSodium) * (Integer.parseInt(selectedFoodQuantity) + Float.parseFloat(selectedFoodQuantityFraction));
                        } catch (NumberFormatException nfe) {
                            newValue = 0;
                        }
                        sodiumValue.setText(String.format("%.0fg", newValue));

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
            if("quickadd".equals(addFoodCategory)){
                scrollView.setVisibility(View.VISIBLE);
                layoutQuickAddFood.setVisibility(View.VISIBLE);
                int selectedFoodTimeInt = 0;
                try {
                    selectedFoodTimeInt = Integer.parseInt(selectedFoodTime);
                }catch (NumberFormatException ne){

                }

                switch (selectedFoodTimeInt) {
                    case 1:
                    case 2:
                    case 3:
                    case 4:
                    case 5:
                    case 6:
                    case 7:
                    case 8:
                    case 9:
                        // breakfast
                        selectedMealType = 0;
                        selectedFoodTime = "9";
                        break;

                    case 12:
                    case 13:
                        // lunch
                        selectedMealType = 1;
                        selectedFoodTime = "13";
                        break;

                    case 17:
                    case 18:
                    case 19:
                        // dinner
                        selectedMealType = 2;
                        selectedFoodTime = "18";
                        break;

                    default:
                        // snack
                        selectedMealType = 3;
                        selectedFoodTime = "16";
                        break;
                }

                final Dialog chooseMealTypeDlg = new AlertDialog.Builder(HTAddFoodSelectItemActivity.this)
                        .setTitle("Meal")
                        .setSingleChoiceItems(mealTypeValues, selectedMealType, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mealTypeEdit2.setText(mealTypeValues[which]);
                                selectedMealType = which;
                                switch (which) {
                                    case 0:
                                        selectedFoodTime = "9";
                                        break;

                                    case 1:
                                        selectedFoodTime = "13";
                                        break;

                                    case 2:
                                        selectedFoodTime = "18";
                                        break;

                                    default:
                                        selectedFoodTime = "16"; // snack
                                        break;
                                }
                                dialog.dismiss();

                            }
                        }).create();

                mealTypeEdit2.setText(mealTypeValues[selectedMealType]);
                mealTypeEdit2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        chooseMealTypeDlg.show();
                    }
                });

                favoriteCheck2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        addFoodToFavorites = !addFoodToFavorites;
                        if (addFoodToFavorites) {
                            favoriteCheck2.setImageResource(R.drawable.ht_check_on_green);
                        } else {
                            favoriteCheck2.setImageResource(R.drawable.ht_check_off_green);
                        }
                    }
                });

            }else {
                layoutAddFoodSelect.setVisibility(View.VISIBLE);
                getFoodItem();
            }
        }

    }

    private void getFoodItem() {
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        selectedFoodName = "";
        selectedFoodDetailsID = "";
        selectedFoodCalories = "";
        selectedFoodProtein = "";
        selectedFoodCarbs = "";
        selectedFoodFiber = "";
        selectedFoodSugar = "";
        selectedFoodSodium = "";
        selectedFoodFat = "";
        selectedFoodSatFat = "";
        selectedFoodServings = "";
        selectedFoodBrand = "";

        addFoodToFavorites = false;
        doneAddingFood = false;

        if (relaunchPlannerItem) {

            if (relaunchItemID == 0) {

                try {
                    relaunchItemID = Integer.parseInt(selectedFoodID);
                }catch(NumberFormatException e){

                }
            }

            if (!"exchange".equals(addFoodCategory)) {
                addFoodCategory = ""; // this gets populated on the web svc side for a relaunch
            }
        }

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_food_select_item"));
                    nameValuePairs.add(new BasicNameValuePair("nutritionix", "true"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("WhichCategory", addFoodCategory));
                    if (!"".equals(exchangeItemsString) && exchangeItemsString != null) { // exchanges!
                        nameValuePairs.add(new BasicNameValuePair("WhichID", URLEncoder.encode(exchangeItemsString, "utf-8")));
                    } else {
                        nameValuePairs.add(new BasicNameValuePair("WhichID", selectedFoodID + ""));
                    }
                    nameValuePairs.add(new BasicNameValuePair("relaunch_id", relaunchItemID + ""));

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

                                toast = HTToast.showToast(HTAddFoodSelectItemActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();
                                String tempString = "";

                                if (XMLFunctions.tagExists(e2, "food_item_name")) {
                                    tempString = XMLFunctions.getValue(e2, "food_item_name");
                                    selectedFoodName = htGlobals.cleanStringAfterReceiving(tempString);
                                    selectedFoodName = htGlobals.capitalize(selectedFoodName.toLowerCase());
                                }

                                if (XMLFunctions.tagExists(e2, "food_item_id")) {
                                    tempString = XMLFunctions.getValue(e2, "food_item_id");
                                    selectedFoodDetailsID = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "food_item_calories")) {
                                    tempString = XMLFunctions.getValue(e2, "food_item_calories");
                                    selectedFoodCalories = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "food_item_protein")) {
                                    tempString = XMLFunctions.getValue(e2, "food_item_protein");
                                    selectedFoodProtein = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "food_item_carbs")) {
                                    tempString = XMLFunctions.getValue(e2, "food_item_carbs");
                                    selectedFoodCarbs = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "food_item_fiber")) {
                                    tempString = XMLFunctions.getValue(e2, "food_item_fiber");
                                    selectedFoodFiber = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "food_item_sugar")) {
                                    tempString = XMLFunctions.getValue(e2, "food_item_sugar");
                                    selectedFoodSugar = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "food_item_sodium")) {
                                    tempString = XMLFunctions.getValue(e2, "food_item_sodium");
                                    selectedFoodSodium = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "food_item_fat")) {
                                    tempString = XMLFunctions.getValue(e2, "food_item_fat");
                                    selectedFoodFat = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "food_item_sat_fat")) {
                                    tempString = XMLFunctions.getValue(e2, "food_item_sat_fat");
                                    selectedFoodSatFat = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "food_item_brand")) {
                                    tempString = XMLFunctions.getValue(e2, "food_item_brand");
                                    selectedFoodBrand = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "food_item_servings")) {
                                    tempString = XMLFunctions.getValue(e2, "food_item_servings");
                                    selectedFoodServings = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (XMLFunctions.tagExists(e2, "food_item_quantity")) {
                                    tempString = XMLFunctions.getValue(e2, "food_item_quantity");
                                    tempString = htGlobals.cleanStringAfterReceiving(tempString);
                                    if(!"".equals(tempString))
                                        selectedFoodQuantity = tempString;
                                }

                                if (XMLFunctions.tagExists(e2, "food_item_category")) {
                                    tempString = XMLFunctions.getValue(e2, "food_item_category");
                                    tempString = htGlobals.cleanStringAfterReceiving(tempString);
                                    if(!"".equals(tempString))
                                        addFoodCategory = tempString;
                                }

                                if (XMLFunctions.tagExists(e2, "food_item_time")) {
                                    tempString = XMLFunctions.getValue(e2, "food_item_time");
                                    tempString = htGlobals.cleanStringAfterReceiving(tempString);
                                    if(!"".equals(tempString))
                                        selectedFoodTime = tempString;
                                    /*if (!"".equals(selectedFoodTime)) {
                                        try {
                                            int timeValue = Integer.parseInt(selectedFoodTime);

                                            if (timeValue > 12
                                                    && timeValue != 24) {

                                                selectedFoodTime = "" + (timeValue - 12);

                                                selectedFoodTimeAmPm = "pm";

                                            } else if (timeValue == 24 ||
                                                    timeValue == 0) {

                                                selectedFoodTime = "12";
                                                selectedFoodTimeAmPm = "am";

                                            } else if (timeValue == 12) {

                                                selectedFoodTime = "12";
                                                selectedFoodTimeAmPm = "pm";

                                            } else {

                                                selectedFoodTimeAmPm = "am";
                                            }
                                        } catch (NumberFormatException nfe) {
                                            selectedFoodTime = "";
                                        }
                                    }*/
                                }

                                if (XMLFunctions.tagExists(e2, "food_item_time_fraction")) {
                                    /*tempString = XMLFunctions.getValue(e2, "food_item_time_fraction");
                                    tempString = htGlobals.cleanStringAfterReceiving(tempString);
                                    if ("".equals(tempString))
                                        selectedFoodTimeFraction = ":00";
                                    else
                                        selectedFoodTimeFraction = tempString;*/

                                }

                                if (XMLFunctions.tagExists(e2, "food_item_reminder_time")) {
                                    tempString = XMLFunctions.getValue(e2, "food_item_reminder_time");
                                    tempString = htGlobals.cleanStringAfterReceiving(tempString);
                                    if (!"".equals(tempString)) {
                                        selectedFoodReminder = tempString;
                                        try {
                                            int timeValue = Integer.parseInt(selectedFoodReminder);
                                            if (timeValue > 12
                                                    && timeValue != 24) {

                                                selectedFoodReminder = "" + (timeValue - 12);

                                                selectedFoodReminderAmPm = "pm";

                                            } else if (timeValue == 24 ||
                                                    timeValue == 0) {

                                                selectedFoodReminder = "12";
                                                selectedFoodReminderAmPm = "am";

                                            } else if (timeValue == 12) {

                                                selectedFoodReminder = "12";
                                                selectedFoodReminderAmPm = "pm";

                                            } else {

                                                selectedFoodReminderAmPm = "am";
                                            }
                                        } catch (NumberFormatException nfe) {
                                            selectedFoodReminder = "";
                                        }
                                    }
                                }

                                if (XMLFunctions.tagExists(e2, "food_item_reminder_time_fraction")) {
                                    tempString = XMLFunctions.getValue(e2, "food_item_reminder_time_fraction");
                                    tempString = htGlobals.cleanStringAfterReceiving(tempString);
                                    if ("".equals(tempString))
                                        selectedFoodReminderFraction = ":00";
                                    else
                                        selectedFoodReminderFraction = tempString;

                                }

                                showFoodItem();

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

//                                    toast = HTToast.showToast(HTAddFoodSelectItemActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTAddFoodSelectItemActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showFoodItem() {
        scrollView.setVisibility(View.VISIBLE);

        if (showChangeFoodSelection && relaunchPlannerItem == true &&
                ("template".equals(addFoodCategory) ||
                        "exchange".equals(addFoodCategory))) {
            changeFoodSelectionLayout.setVisibility(View.VISIBLE);
        } else {
            changeFoodSelectionLayout.setVisibility(View.GONE);
        }

        // details panel icon
        if (!"general".equals(addFoodCategory) &&
                !"exchange".equals(addFoodCategory)) {
            foodItemsDetailBtn.setVisibility(View.VISIBLE);
            foodItemsDetailBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int mealItemID = 0;
                    try {
                        mealItemID = Integer.parseInt(selectedFoodDetailsID);
                    } catch (NumberFormatException nfe) {

                    }
                    Intent intent = new Intent(HTAddFoodSelectItemActivity.this, HTAddFoodSelectItemDetailsActivity.class);
                    intent.putExtra("mealItemID", mealItemID);
                    startActivityForResult(intent, 1);
                }
            });
        }
        foodNameLabel.setText(selectedFoodName);

        final SimpleDateFormat sdf = new SimpleDateFormat("h:mma", Locale.US);

        // meal slot
        /*if ("".equals(selectedFoodTime) || selectedFoodTime == null) {

            selectedFoodTime = "12";
            selectedFoodTimeFraction = ":00";
            selectedFoodTimeAmPm = "pm";
        }

        if ("0".equals(selectedFoodTime)) {

            selectedFoodTime = "12";
            selectedFoodTimeFraction = ":00";
            selectedFoodTimeAmPm = "am";
        }

        foodTime = sdf.parse(selectedFoodTime + selectedFoodTimeFraction + selectedFoodTimeAmPm, new ParsePosition(0));
        final HTTimePickerDialog foodTimePickerDialog = new HTTimePickerDialog(this, TimePickerDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                view.setCurrentMinute(minute);
                foodTime.setHours(hourOfDay);
                foodTime.setMinutes(minute * 30);
                mealTypeEdit.setText(sdf.format(foodTime).toLowerCase());
            }
        }, foodTime.getHours(), foodTime.getMinutes(), false, 30);
        mealTypeEdit.setText(selectedFoodTime + selectedFoodTimeFraction + selectedFoodTimeAmPm);
        mealTypeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodTimePickerDialog.show();
            }
        });*/

        //brand?
        if(!"".equals(selectedFoodBrand)){
            brandLayout.setVisibility(View.VISIBLE);
            brandView.setText(selectedFoodBrand);
        }else{
            brandLayout.setVisibility(View.GONE);
        }

        //serving size
        if(!"".equals(selectedFoodServings)){
            servingSizeLayout.setVisibility(View.VISIBLE);
            servingSizeView.setText(selectedFoodServings);
        }else{
            servingSizeLayout.setVisibility(View.GONE);
        }

        int selectedFoodTimeInt = 0;
        try {
            selectedFoodTimeInt = Integer.parseInt(selectedFoodTime);
        }catch (NumberFormatException ne){

        }

        switch (selectedFoodTimeInt) {
            case 1:
            case 2:
            case 3:
            case 4:
            case 5:
            case 6:
            case 7:
            case 8:
            case 9:
                // breakfast
                selectedMealType = 0;
                selectedFoodTime = "9";
                break;

            case 12:
            case 13:
                // lunch
                selectedMealType = 1;
                selectedFoodTime = "13";
                break;

            case 17:
            case 18:
            case 19:
                // dinner
                selectedMealType = 2;
                selectedFoodTime = "18";
                break;

            default:
                // snack
                selectedMealType = 3;
                selectedFoodTime = "16";
                break;
        }

        final Dialog chooseMealTypeDlg = new AlertDialog.Builder(HTAddFoodSelectItemActivity.this)
                .setTitle("Meal")
                .setSingleChoiceItems(mealTypeValues, selectedMealType, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mealTypeEdit.setText(mealTypeValues[which]);
                        selectedMealType = which;
                        switch (which) {
                            case 0:
                                selectedFoodTime = "9";
                                break;

                            case 1:
                                selectedFoodTime = "13";
                                break;

                            case 2:
                                selectedFoodTime = "18";
                                break;

                            default:
                                selectedFoodTime = "16"; // snack
                                break;
                        }
                        dialog.dismiss();

                    }
                }).create();

        mealTypeEdit.setText(mealTypeValues[selectedMealType]);
        mealTypeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseMealTypeDlg.show();
            }
        });

        // number of servings
        if (!"exchange".equals(addFoodCategory) &&
                !"template".equals(addFoodCategory)) {
//            if ("general".equals(addFoodCategory) && !"".equals(selectedFoodServings)) {
//                quantityLabel.setText(String.format("Quantity (%s)", selectedFoodServings.replace("(", "").replace(")", "")));
//            } else {
            quantityLabel.setText(R.string.number_of_servings);
//            }

            if (selectedFoodQuantity == null || "".equals(selectedFoodQuantity)) {
                selectedFoodQuantity = "1.00";
                quantityEdit.setText("1.00");
            } else {
                if(!selectedFoodQuantity.contains("."))
                    selectedFoodQuantity += ".00";
                quantityEdit.setText(selectedFoodQuantity);
            }

            quantityEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String quantity = (String) quantityEdit.getText();
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
            quantityLayout.setVisibility(View.GONE);
        }

        //Text Reminder
        if (!"".equals(selectedFoodReminder) && selectedFoodReminder != null) {
            reminderTimeEdit.setText(selectedFoodReminder + selectedFoodReminderFraction + selectedFoodReminderAmPm);
            foodReminder = sdf.parse(selectedFoodReminder + selectedFoodReminderFraction + selectedFoodReminderAmPm, new ParsePosition(0));
        }else{
            reminderTimeEdit.setText("none");
        }
        foodReminder = sdf.parse("12:00pm", new ParsePosition(0));
        final HTTimePickerDialog foodReminderPickerDialog = new HTTimePickerDialog(this, TimePickerDialog.THEME_HOLO_LIGHT, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                view.setCurrentMinute(minute);
                foodReminder.setHours(hourOfDay);
                foodReminder.setMinutes(minute * 15);
                reminderTimeEdit.setText(sdf.format(foodReminder).toLowerCase());
            }
        }, foodReminder.getHours(), foodReminder.getMinutes(), false, 15);
        foodReminderPickerDialog.setButton(DialogInterface.BUTTON_NEUTRAL, "None", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                reminderTimeEdit.setText("none");
                foodReminder = sdf.parse("12:00pm", new ParsePosition(0));
                foodReminderPickerDialog.updateTime(foodReminder.getHours(), foodReminder.getMinutes());
            }
        });

        reminderTimeEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                foodReminderPickerDialog.show();
            }
        });

        //add to favorites
        if (!"favorites".equals(addFoodCategory) &&
                !"exchange".equals(addFoodCategory)) {
            if (addFoodToFavorites) {
                favoriteCheck.setImageResource(R.drawable.ht_check_on_green);
            } else {
                favoriteCheck.setImageResource(R.drawable.ht_check_off_green);
            }

            favoriteCheck.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addFoodToFavorites = !addFoodToFavorites;
                    if (addFoodToFavorites) {
                        favoriteCheck.setImageResource(R.drawable.ht_check_on_green);
                    } else {
                        favoriteCheck.setImageResource(R.drawable.ht_check_off_green);
                    }
                }
            });
        } else {
            addFavoritesLayout.setVisibility(View.GONE);
        }

        // nutritional info - calories
        float nutritionalCalc;
        try {
            if ("exchange".equals(addFoodCategory)) {
                nutritionalCalc = Float.parseFloat(selectedFoodCalories);
            } else {
                nutritionalCalc = Float.parseFloat(selectedFoodCalories) * Float.parseFloat(selectedFoodQuantity);
            }
        } catch (NumberFormatException nfe) {
            nutritionalCalc = 0;
        }
        caloriesValue.setText(String.format("%.0f", nutritionalCalc));

        try {
            if ("exchange".equals(addFoodCategory)) {
                nutritionalCalc = Float.parseFloat(selectedFoodCarbs);
            } else {
                nutritionalCalc = Float.parseFloat(selectedFoodCarbs) * Float.parseFloat(selectedFoodQuantity);
            }
        } catch (NumberFormatException nfe) {
            nutritionalCalc = 0;
        }
        carbsValue.setText(String.format("%.0f", nutritionalCalc) + "g");
        try {
            if ("exchange".equals(addFoodCategory)) {
                nutritionalCalc = Float.parseFloat(selectedFoodFiber);
            } else {
                nutritionalCalc = Float.parseFloat(selectedFoodFiber) * Float.parseFloat(selectedFoodQuantity);
            }
        } catch (NumberFormatException nfe) {
            nutritionalCalc = 0;
        }
        fiberValue.setText(String.format("%.0f", nutritionalCalc) + "g");

        try {
            if ("exchange".equals(addFoodCategory)) {
                nutritionalCalc = Float.parseFloat(selectedFoodSugar);
            } else {
                nutritionalCalc = Float.parseFloat(selectedFoodSugar) * Float.parseFloat(selectedFoodQuantity);
            }
        } catch (NumberFormatException nfe) {
            nutritionalCalc = 0;
        }
        sugarsValue.setText(String.format("%.0f", nutritionalCalc) + "g");

        try {
            if ("exchange".equals(addFoodCategory)) {
                nutritionalCalc = Float.parseFloat(selectedFoodProtein);
            } else {
                nutritionalCalc = Float.parseFloat(selectedFoodProtein) * Float.parseFloat(selectedFoodQuantity);
            }
        } catch (NumberFormatException nfe) {
            nutritionalCalc = 0;
        }
        proteinValue.setText(String.format("%.0f", nutritionalCalc) + "g");

        try {
            if ("exchange".equals(addFoodCategory)) {
                nutritionalCalc = Float.parseFloat(selectedFoodFat);
            } else {
                nutritionalCalc = Float.parseFloat(selectedFoodFat) * Float.parseFloat(selectedFoodQuantity);
            }
        } catch (NumberFormatException nfe) {
            nutritionalCalc = 0;
        }
        totalFatValue.setText(String.format("%.0f", nutritionalCalc) + "g");

        try {
            if ("exchange".equals(addFoodCategory)) {
                nutritionalCalc = Float.parseFloat(selectedFoodSatFat);
            } else {
                nutritionalCalc = Float.parseFloat(selectedFoodSatFat) * Float.parseFloat(selectedFoodQuantity);
            }
        } catch (NumberFormatException nfe) {
            nutritionalCalc = 0;
        }
        satfatValue.setText(String.format("%.0f", nutritionalCalc) + "g");

        try {
            if ("exchange".equals(addFoodCategory)) {
                nutritionalCalc = Float.parseFloat(selectedFoodSodium);
            } else {
                nutritionalCalc = Float.parseFloat(selectedFoodSodium) * Float.parseFloat(selectedFoodQuantity);
            }
        } catch (NumberFormatException nfe) {
            nutritionalCalc = 0;
        }
        sodiumValue.setText(String.format("%.0f", nutritionalCalc) + "mg");
    }

    private void addFoodItem() {
        thisPlannerItemID = "";
        HTGlobals.getInstance().plannerShouldRefresh = true;
//        selectedFoodTime = foodTime.getHours() + "";
//        selectedFoodTimeFraction = foodTime.getMinutes() + "";
        if ("quickadd".equals(addFoodCategory)) {
            selectedFoodReminder = "";
            selectedFoodReminderFraction = "";
        }else {
            if ("none".equals(reminderTimeEdit.getText())) {
                selectedFoodReminder = "";
                selectedFoodReminderFraction = "00";
            } else {
                selectedFoodReminder = foodReminder.getHours() + "";
                selectedFoodReminderFraction = foodReminder.getMinutes() + "";
            }
        }
        if (!"".equals(selectedFoodReminder)) {

            selectedFoodReminderYN = "Y";
        }

        if (addFoodToFavorites) {
            selectedFoodAddToFavorites = "Y";
        }

        if ("quickadd".equals(addFoodCategory)) {

            selectedFoodName = foodnameEdit.getText().toString();
        }

        try {
            selectedFoodName = URLEncoder.encode(selectedFoodName, "utf-8");
        } catch (UnsupportedEncodingException e) {
        }


        if ("exchange".equals(addFoodCategory)) {
            selectedFoodQuantity = "1";
        }else if ("quickadd".equals(addFoodCategory)) {
            selectedFoodQuantity = "1";

            selectedFoodCalories = caloriesEdit.getText().toString();
            selectedFoodProtein = proteinEdit.getText().toString();
            selectedFoodCarbs = carbsEdit.getText().toString();
            selectedFoodFat = fatEdit.getText().toString();
            selectedFoodSatFat = satfatEdit.getText().toString();
            selectedFoodSugar = sugarsEdit.getText().toString();
            selectedFoodFiber = fiberEdit.getText().toString();
            selectedFoodSodium = sodiumEdit.getText().toString();
        }else{
            selectedFoodQuantity = quantityEdit.getText().toString();
            selectedFoodCalories = caloriesValue.getText().toString();
        }

        if (relaunchPlannerItem) {

            selectedFoodRelaunchItem = "true";
            selectedFoodRelaunchItemID = relaunchItemID + "";

        } else {
            selectedFoodRelaunchItem = "false";
            selectedFoodRelaunchItemID = "";
        }

        if (!"".equals(selectedFoodDetailsID)) {
//                try {
//                    selectedFoodID = Integer.parseInt(selectedFoodDetailsID);
//                } catch (NumberFormatException nfe) {
//
//                }
            selectedFoodID = selectedFoodDetailsID;
        }

        selectedFoodExchangeNumber = "";
        selectedFoodTemplate = inTemplateString;

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_food_add_item"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("WhichCategory", addFoodCategory));
                    if (!"".equals(exchangeItemsString) && exchangeItemsString != null) { // exchanges!
                        nameValuePairs.add(new BasicNameValuePair("WhichID", exchangeItemsString));
                    } else {
                        nameValuePairs.add(new BasicNameValuePair("WhichID", selectedFoodID + ""));
                    }
                    nameValuePairs.add(new BasicNameValuePair("hour", selectedFoodTime));
                    nameValuePairs.add(new BasicNameValuePair("hour_half", "00"));
                    nameValuePairs.add(new BasicNameValuePair("name", selectedFoodName));
                    nameValuePairs.add(new BasicNameValuePair("reminder", selectedFoodReminder));
                    nameValuePairs.add(new BasicNameValuePair("reminder_half", selectedFoodReminderFraction));
                    nameValuePairs.add(new BasicNameValuePair("reminder_yn", selectedFoodReminderYN));
                    nameValuePairs.add(new BasicNameValuePair("add_to_favs", selectedFoodAddToFavorites));
                    nameValuePairs.add(new BasicNameValuePair("quantity", selectedFoodQuantity));
                    nameValuePairs.add(new BasicNameValuePair("relaunch", selectedFoodRelaunchItem));
                    nameValuePairs.add(new BasicNameValuePair("relaunch_id", selectedFoodRelaunchItemID));
                    nameValuePairs.add(new BasicNameValuePair("plan_calories", ""));
                    nameValuePairs.add(new BasicNameValuePair("exchange_number", selectedFoodExchangeNumber));
                    nameValuePairs.add(new BasicNameValuePair("template", selectedFoodTemplate));
                    nameValuePairs.add(new BasicNameValuePair("calories", selectedFoodCalories));

                    if("quickadd".equals(addFoodCategory)){
                        nameValuePairs.add(new BasicNameValuePair("protein", selectedFoodProtein));
                        nameValuePairs.add(new BasicNameValuePair("carbs", selectedFoodCarbs));
                        nameValuePairs.add(new BasicNameValuePair("fat", selectedFoodFat));
                        nameValuePairs.add(new BasicNameValuePair("sat_fat", selectedFoodSatFat));
                        nameValuePairs.add(new BasicNameValuePair("sugars", selectedFoodSugar));
                        nameValuePairs.add(new BasicNameValuePair("fiber", selectedFoodFiber));
                        nameValuePairs.add(new BasicNameValuePair("sodium", selectedFoodSodium));
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

                                toast = HTToast.showToast(HTAddFoodSelectItemActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                HTGlobals htGlobals = HTGlobals.getInstance();
                                String tempString = "";
                                if (XMLFunctions.tagExists(e2, "planner_item_id")) {
                                    tempString = XMLFunctions.getValue(e2, "planner_item_id");
                                    thisPlannerItemID = htGlobals.cleanStringAfterReceiving(tempString);
                                }

                                if (finalBitmap != null) {
                                    saveAndUploadImage();
                                } else {
                                    setResult(2);
                                    finish();
                                }
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

//                                    toast = HTToast.showToast(HTAddFoodSelectItemActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTAddFoodSelectItemActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void saveAndUploadImage() {
        OutputStream outStream = null;

        File file = new File(getExternalFilesDir("plannerPhoto"), thisPlannerItemID + ".png");
        if (file.exists()) {
            file.delete();
        }

        try {
            outStream = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        uploadPlannerPhoto();
    }

    private void uploadPlannerPhoto() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
        byte[] byteArray = byteArrayOutputStream .toByteArray();
        String base64Data = Base64.encodeToString(byteArray, Base64.DEFAULT);
        final String imageString = base64Data.replace("+", "*PLUS*");

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));
                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_food_add_photo"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("WhichID", thisPlannerItemID));
                    nameValuePairs.add(new BasicNameValuePair("image", imageString));

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

                                toast = HTToast.showToast(HTAddFoodSelectItemActivity.this, errorMessage, Toast.LENGTH_LONG);
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

//                                    toast = HTToast.showToast(HTAddFoodSelectItemActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTAddFoodSelectItemActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void takePhotoFromCamera() {
        Calendar cal = Calendar.getInstance();
        File file = new File(getExternalFilesDir("plannerPhoto"), (cal.getTimeInMillis() + ".jpg"));
        if(file.exists()) {
            file.delete();
        }

        try {
            file.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mPhotoUri = Uri.fromFile(file);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoUri);
        startActivityForResult(intent, TAKE_PHOTO_FROM_CAMERA);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // coming back from a child activity, refresh the calendar

        if (requestCode == TAKE_PHOTO_FROM_CAMERA) {
            if (resultCode == RESULT_OK) {
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(),mPhotoUri);
                    float ratio = (float)bitmap.getHeight() / (float)bitmap.getWidth();
                    int newWidth = 300;
                    int newHeight = (int)(300 * ratio);
                    ExifInterface exif = new ExifInterface(mPhotoUri.getPath());
                    int rotate = 0;
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                    switch (orientation) {
                        case ExifInterface.ORIENTATION_ROTATE_270:
                            rotate = 270;
                            ratio = (float)bitmap.getWidth() / (float)bitmap.getHeight();
                            newWidth = (int)(300 * ratio);
                            newHeight = 300;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_180:
                            rotate = 180;
                            break;
                        case ExifInterface.ORIENTATION_ROTATE_90:
                            rotate = 90;
                            ratio = (float)bitmap.getWidth() / (float)bitmap.getHeight();
                            newWidth = (int)(300 * ratio);
                            newHeight = 300;
                            break;
                    }

                    finalBitmap = Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, false);
                    bitmap.recycle();

                    if(rotate > 0) {
                        Matrix matrix = new Matrix();
                        matrix.postRotate(rotate);
                        finalBitmap = Bitmap.createBitmap(finalBitmap, 0, 0, finalBitmap.getWidth(), finalBitmap.getHeight(), matrix, true);
                    }

                    new File(mPhotoUri.getPath()).delete();
                    if ("quickadd".equals(addFoodCategory)) {
                        quickPhotoView.setImageBitmap(finalBitmap);
                    } else {
                        photoView.setImageBitmap(finalBitmap);
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {

            if (resultCode == 2) {
                setResult(2);
                finish();
                return;
            }

            passDate = HTGlobals.getInstance().passDate;
            passDay = HTGlobals.getInstance().passDay;
            passMonth = HTGlobals.getInstance().passMonth;
            passYear = HTGlobals.getInstance().passYear;

            getFoodItem();
        }
    }

}
