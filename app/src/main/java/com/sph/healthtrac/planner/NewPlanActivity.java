package com.sph.healthtrac.planner;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
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
import com.sph.healthtrac.common.HTDatePicker;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTTabBar;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.common.HTTypefaceSpan;
import com.sph.healthtrac.common.PixelUtil;
import com.sph.healthtrac.common.XMLFunctions;
import com.sph.healthtrac.planner.addactivity.HTAddActivitySelectItemActivity;
import com.sph.healthtrac.planner.addactivity.HTNewAddExerciseSearchActivity;
import com.sph.healthtrac.planner.addfood.HTAddFoodSearchResultsActivity;
import com.sph.healthtrac.planner.addfood.HTAddFoodSelectItemActivity;
import com.sph.healthtrac.planner.addfood.HTNewAddFoodSearchActivity;
import com.sph.healthtrac.planner.createfavorites.HTCreateFavoritesActivity;
import com.sph.healthtrac.planner.createplan.HTCreateEatingPlanActivity;
import com.sph.healthtrac.planner.createplan.HTCreateEatingPlanSelectActivity;

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

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class NewPlanActivity extends Fragment {
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

    private FragmentActivity fragmentActivity;
    private RelativeLayout planMainContainer;

    private ZoomLayout zoomLayout;
    //planner Layout
    private LinearLayout plannerLayout;
    private RelativeLayout datePickerLayout;
    private LinearLayout trackerContainer;
    private View mActionBar;
    private View mPhotoActionBar;
    private View datePicker;
    private TextView mDateTitleTextView;

    private RelativeLayout plannerSwitchLayout1;
    private TextView foodPhotosLabel;

    private TextView goalLabel;
    private TextView goalValue;
    private TextView consumedLabel;
    private TextView consumedValue;
    private TextView exerciseLabel;
    private TextView exerciseValue;
    private TextView remainingLabel;
    private TextView remainingValue;
    private TextView minusSignView;
    private TextView plusSignView;
    private TextView equalSignView;

    private ScrollView scrollViewPlan;
    private LinearLayout linearLayoutMyPlans;

    private TextView breakfastLabelView;
    private TextView breakfastCaloriesView;
    private TextView lunchLabelView;
    private TextView lunchCaloriesView;
    private TextView dinnerLabelView;
    private TextView dinnerCaloriesView;
    private TextView snacksLabelView;
    private TextView snackCaloriesView;
    private TextView exerciseLabelView;
    private TextView exerciseCaloriesView;
    private TextView noteLabelView;

    private TextView plusLabelView1;
    private TextView plusLabelView2;
    private TextView plusLabelView3;
    private TextView plusLabelView4;
    private TextView plusLabelView5;
    private TextView plusLabelView6;
    private TextView addFoodLabelView1;
    private TextView addFoodLabelView2;
    private TextView addFoodLabelView3;
    private TextView addFoodLabelView4;
    private TextView addFoodLabelView5;
    private TextView addFoodLabelView6;

    private TextView chooseMyMealsBtn                                                                ;
    private TextView sharePlanBtn;
    private TextView nutritionBtn;
    private TextView createFavoritesBtn;
    private TextView createPlanBtn;

    private LinearLayout breakfastPlanDetailsLayout;
    private LinearLayout lunchPlanDetailsLayout;
    private LinearLayout dinnerPlanDetailsLayout;
    private LinearLayout snackPlanDetailsLayout;
    private LinearLayout exercisePlanDetailsLayout;
    private LinearLayout notesDetailsLayout;

    private RelativeLayout addBreakfastPlanLayout;
    private RelativeLayout addLunchPlanLayout;
    private RelativeLayout addDinnerPlanLayout;
    private RelativeLayout addSnackPlanLayout;
    private RelativeLayout addExercisePlanLayout;
    private RelativeLayout addNotePlanLayout;

    //planner photo layout
    private LinearLayout plannerPhotoLayout;
    private RelativeLayout plannerSwitchLayout2;
    private TextView planLabel;
    private LinearLayout linearLayoutPlanPhoto;

    private AnimatorSet mInAnimation;
    private AnimatorSet mOutAnimation;

    Calendar calendar;
    DefaultHttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;

    Toast toast;

    private DisplayMetrics displayMetrics;
    private float displayDensity;
    private int screenWidth;
    private int screenHeight;

    private Thread myThread = null;

    private String numberOfMessages = "";
    private String newLearningModules = "";
    private String newEatingPlans = "";
    private String planName = "";
    private String templateName = "";
    private String selectedPlannerItemID = "";
    private String selectedFoodCategory = "";
    private String exchangeItemsString = "";
    private String caloriesOrOtherString = "calories";


    private String plannerTargetCalories = "";
    private String plannerCalories = "";
    private String plannerCaloriesBurned = "";
    private String breakfastCalories;
    private String lunchCalories;
    private String dinnerCalories;
    private String snackCalories;
    private String exerciseCalories;

    private int plannerItemCount = 0;
    private boolean hasSavedMyPlans = false;

    private List<String> plannerItemID = new ArrayList<>();
    private List<String> plannerItemCategory = new ArrayList<>();
    private List<String> plannerItemEat = new ArrayList<>();
    private List<String> plannerItemMove = new ArrayList<>();
    private List<String> plannerItemBalance = new ArrayList<>();
    private List<String> plannerItemReminder = new ArrayList<>();
    private List<String> plannerItemCalories = new ArrayList<>();
    private List<String> plannerItemCaloriesBurned = new ArrayList<>();
    private List<String> plannerItemMealID = new ArrayList<>();
    private List<String> plannerItemExchangeItems = new ArrayList<>();
    private List<String> plannerItemPlaceholder = new ArrayList<>();
    private List<String> plannerItemNotes = new ArrayList<>();
    private List<String> plannerItemSubNotes = new ArrayList<>();
    private List<String> plannerItemImage = new ArrayList<>();

    private List<String> plannerPhotosImage = new ArrayList<>();
    private List<String> plannerPhotosNotes = new ArrayList<>();
    private List<String> plannerPhotosCalories = new ArrayList<>();
    private List<String> plannerPhotosCategory = new ArrayList<>();
    private List<String> plannerPhotosDate = new ArrayList<>();

    LayoutInflater mInflater;
    private static InputMethodManager imm;

    SharedPreferences mSharedPreferences;

    private Typeface avenirNextReqularFont;
    private Typeface avenirNextMediumFont;

    private int grayBarColor = Color.rgb(215, 226, 230);

    private boolean isSavingScrollPosition;
    private boolean isShowingDeleteConfirmDlg;

    private boolean showFoodPhotos;

    private boolean showChooseMyMealsButton;
    private boolean showSharePlanButton;
    private boolean ignoreCaloriesBurned;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentActivity = super.getActivity();

        mSharedPreferences = fragmentActivity.getSharedPreferences("SPHPrefs", Activity.MODE_PRIVATE);
        imm = (InputMethodManager) fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        avenirNextReqularFont = Typeface.createFromAsset(fragmentActivity.getAssets(), "fonts/AvenirNext-Regular.ttf");
        avenirNextMediumFont = Typeface.createFromAsset(fragmentActivity.getAssets(), "fonts/AvenirNext-Medium.ttf");

        mInflater = inflater;

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

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(fragmentActivity, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            fragmentActivity.finish();

        } else {
            if(planMainContainer == null) {
                planMainContainer = (RelativeLayout) inflater.inflate(R.layout.activity_plan_new, container, false);

                zoomLayout = (ZoomLayout) planMainContainer.findViewById(R.id.zoomLayout);
                zoomLayout.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        zoomLayout.init(fragmentActivity);
                        return false;
                    }
                });

                plannerLayout = (LinearLayout) planMainContainer.findViewById(R.id.plannerLayout);
                plannerPhotoLayout = (LinearLayout) planMainContainer.findViewById(R.id.plannerPhotoLayout);

                datePickerLayout = (RelativeLayout) planMainContainer.findViewById(R.id.datePickerLayout);
                trackerContainer = (LinearLayout) planMainContainer.findViewById(R.id.trackerContainer);
                trackerContainer.setVisibility(View.INVISIBLE);
                scrollViewPlan = (ScrollView) planMainContainer.findViewById(R.id.scrollViewMyPlans);
                linearLayoutMyPlans = (LinearLayout) planMainContainer.findViewById(R.id.linearLayoutPlan);

                RelativeLayout.LayoutParams params;

                // action bar
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.US);
                calendar = Calendar.getInstance();
                calendar.setTime(passDate);

                mActionBar = HTActionBar.getActionBar(fragmentActivity, dateFormat.format(calendar.getTime()), "Save As", "My Plans");
                mPhotoActionBar = HTActionBar.getActionBar(fragmentActivity, "Food Photos", "", "");
                planMainContainer.addView(mActionBar);
                planMainContainer.addView(mPhotoActionBar);
                mPhotoActionBar.setVisibility(View.GONE);

                displayMetrics = fragmentActivity.getResources().getDisplayMetrics();
                displayDensity = displayMetrics.density;

                screenWidth = displayMetrics.widthPixels;
                screenHeight = displayMetrics.heightPixels;

                int dpValue = 44;
                int topBarHeight = (int) (dpValue * displayDensity);

                params = (RelativeLayout.LayoutParams) mActionBar.getLayoutParams();
                params.height = topBarHeight;
                mActionBar.setLayoutParams(params);

                params = (RelativeLayout.LayoutParams) mPhotoActionBar.getLayoutParams();
                params.height = topBarHeight;
                mPhotoActionBar.setLayoutParams(params);

                mActionBar.findViewById(R.id.leftButton).setVisibility(View.GONE);
                mActionBar.findViewById(R.id.rightButton).setVisibility(View.GONE);

                // Save As button
                mActionBar.findViewById(R.id.leftButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        LayoutInflater li = LayoutInflater.from(fragmentActivity);
                        View promptsView = li.inflate(R.layout.saveas_plan_view, null);
                        final EditText input = (EditText) promptsView.findViewById(R.id.input);
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        if ("".equals(planName)) {
                            input.setText(templateName);
                        } else {
                            input.setText(planName);
                        }
                        input.setSelection(input.getText().length());
                        AlertDialog.Builder builder = null;
                        builder = new AlertDialog.Builder(fragmentActivity);
                        final AlertDialog alert =
                                builder.setTitle(R.string.save_myplans)
                                        .setMessage(getResources()
                                                .getString(R.string.enter_name))
                                        .setView(promptsView)
                                        .setCancelable(false)
                                        .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                                imm.hideSoftInputFromWindow(input.getWindowToken(), 0);
                                                String planName = input.getText().toString();
                                                saveToMyPlans(planName);
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

                // My Plans button
                mActionBar.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(fragmentActivity, HTPlannerMyPlansActivity.class);
                        startActivityForResult(intent, 1);
                    }
                });

                // date picker
                datePicker = HTDatePicker.getDatePicker(fragmentActivity);
                RelativeLayout.LayoutParams datePickerLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
                datePickerLayout.addView(datePicker, datePickerLayoutParams);

//                params = (RelativeLayout.LayoutParams) datePicker.getLayoutParams();
//                params.height = topBarHeight;
//                params.setMargins(0, topBarHeight, 0, 0);  // left, top, right, bottom
//                datePicker.setLayoutParams(params);

                View viewGraySeparator = datePicker.findViewById(R.id.viewGraySeparator);
                params = (RelativeLayout.LayoutParams) viewGraySeparator.getLayoutParams();
                params.height = (int) (2 * displayDensity);
                viewGraySeparator.setLayoutParams(params);

                final ImageView leftArrow = (ImageView) datePicker.findViewById(R.id.leftArrow);
                final ImageView rightArrow = (ImageView) datePicker.findViewById(R.id.rightArrow);
                mDateTitleTextView = (TextView) datePicker.findViewById(R.id.date_text);

                leftArrow.setImageResource(R.drawable.ht_arrow_left_blue);
                rightArrow.setImageResource(R.drawable.ht_arrow_right_blue);

                leftArrow.setOnClickListener(new ImageView.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        leftDateButtonClicked();
                    }
                });

                rightArrow.setOnClickListener(new ImageView.OnClickListener() {

                    @Override
                    public void onClick(View v) {

                        rightDateButtonClicked();
                    }
                });

                // set the tab bar label font colors
                TextView tabBarLabel;

                tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.dashboardText);
                tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));

                tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.trackText);
                tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));

                if (!HTGlobals.getInstance().hidePlanner) {
                    tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.planText);
                    tabBarLabel.setTextColor(Color.WHITE);
                }

                tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.learnText);
                tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));

                tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.moreText);
                tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));

                plannerSwitchLayout1 = (RelativeLayout) planMainContainer.findViewById(R.id.plannerSwitchLayout1);
                foodPhotosLabel = (TextView) planMainContainer.findViewById(R.id.foodPhotosLabel);
                foodPhotosLabel.setTypeface(avenirNextMediumFont);

                goalLabel = (TextView) planMainContainer.findViewById(R.id.goalLabel);
                goalValue = (TextView) planMainContainer.findViewById(R.id.goalValue);
                goalValue.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showUpdatePlannerTargetCaloriesDialog();
                    }
                });

                consumedLabel = (TextView) planMainContainer.findViewById(R.id.consumedLabel);
                consumedValue = (TextView) planMainContainer.findViewById(R.id.consumedValue);
                exerciseLabel = (TextView) planMainContainer.findViewById(R.id.exerciseLabel);
                exerciseValue = (TextView) planMainContainer.findViewById(R.id.exerciseValue);
                remainingLabel = (TextView) planMainContainer.findViewById(R.id.remainingLabel);
                remainingValue = (TextView) planMainContainer.findViewById(R.id.remainingValue);
                minusSignView = (TextView) planMainContainer.findViewById(R.id.minusSignView);
                minusSignView.setText(String.format("%C", 0x2014));
                plusSignView = (TextView) planMainContainer.findViewById(R.id.plusSignView);
                equalSignView = (TextView) planMainContainer.findViewById(R.id.equalSignView);

                breakfastLabelView = (TextView) planMainContainer.findViewById(R.id.breakfastLabelView);
                breakfastCaloriesView = (TextView) planMainContainer.findViewById(R.id.breakfastCaloriesView);
                lunchLabelView = (TextView) planMainContainer.findViewById(R.id.lunchLabelView);
                lunchCaloriesView = (TextView) planMainContainer.findViewById(R.id.lunchCaloriesView);
                dinnerLabelView = (TextView) planMainContainer.findViewById(R.id.dinnerLabelView);
                dinnerCaloriesView = (TextView) planMainContainer.findViewById(R.id.dinnerCaloriesView);
                snacksLabelView = (TextView) planMainContainer.findViewById(R.id.snacksLabelView);
                snackCaloriesView = (TextView) planMainContainer.findViewById(R.id.snackCaloriesView);
                exerciseLabelView = (TextView) planMainContainer.findViewById(R.id.exerciseLabelView);
                exerciseCaloriesView = (TextView) planMainContainer.findViewById(R.id.exerciseCaloriesView);
                noteLabelView = (TextView) planMainContainer.findViewById(R.id.notesLabelView);

                plusLabelView1 = (TextView) planMainContainer.findViewById(R.id.plusLabelView1);
                plusLabelView2 = (TextView) planMainContainer.findViewById(R.id.plusLabelView2);
                plusLabelView3 = (TextView) planMainContainer.findViewById(R.id.plusLabelView3);
                plusLabelView4 = (TextView) planMainContainer.findViewById(R.id.plusLabelView4);
                plusLabelView5 = (TextView) planMainContainer.findViewById(R.id.plusLabelView5);
                plusLabelView6 = (TextView) planMainContainer.findViewById(R.id.plusLabelView6);
                addFoodLabelView1 = (TextView) planMainContainer.findViewById(R.id.addFoodLabelView1);
                addFoodLabelView2 = (TextView) planMainContainer.findViewById(R.id.addFoodLabelView2);
                addFoodLabelView3 = (TextView) planMainContainer.findViewById(R.id.addFoodLabelView3);
                addFoodLabelView4 = (TextView) planMainContainer.findViewById(R.id.addFoodLabelView4);
                addFoodLabelView5 = (TextView) planMainContainer.findViewById(R.id.addFoodLabelView5);
                addFoodLabelView6 = (TextView) planMainContainer.findViewById(R.id.addFoodLabelView6);

                chooseMyMealsBtn = (TextView) planMainContainer.findViewById(R.id.chooseMyMealsBtn);
                sharePlanBtn = (TextView) planMainContainer.findViewById(R.id.sharePlanBtn);
                nutritionBtn = (TextView) planMainContainer.findViewById(R.id.nutritionBtn);
                createFavoritesBtn = (TextView) planMainContainer.findViewById(R.id.createFavoritesBtn);
                createPlanBtn = (TextView) planMainContainer.findViewById(R.id.createPlanBtn);

                chooseMyMealsBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(fragmentActivity, HTPlannerScrambleActivity.class);
                        startActivityForResult(intent, 1);
                    }
                });

                sharePlanBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(fragmentActivity, HTPlannerShareActivity.class);
                        startActivityForResult(intent, 1);
                    }
                });

                nutritionBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showPlannerNutrition();
                    }
                });

                createFavoritesBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(fragmentActivity, HTCreateFavoritesActivity.class);
                        startActivityForResult(intent, 1);
                    }
                });

                createPlanBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent;
                        if (HTGlobals.getInstance().hideCalorieCalculator) {
                            intent = new Intent(fragmentActivity, HTCreateEatingPlanSelectActivity.class);
                        } else {
                            intent = new Intent(fragmentActivity, HTCreateEatingPlanActivity.class);
                        }
                        startActivityForResult(intent, 1);
                    }
                });

                goalLabel.setTypeface(avenirNextReqularFont);
                consumedLabel.setTypeface(avenirNextReqularFont);
                exerciseLabel.setTypeface(avenirNextReqularFont);
                remainingLabel.setTypeface(avenirNextReqularFont);
                goalValue.setTypeface(avenirNextReqularFont);
                consumedValue.setTypeface(avenirNextReqularFont);
                exerciseValue.setTypeface(avenirNextReqularFont);
                remainingValue.setTypeface(avenirNextReqularFont);
                minusSignView.setTypeface(avenirNextMediumFont);
                plusSignView.setTypeface(avenirNextMediumFont);
                equalSignView.setTypeface(avenirNextMediumFont);

                breakfastLabelView.setTypeface(avenirNextMediumFont);
                breakfastCaloriesView.setTypeface(avenirNextMediumFont);
                lunchLabelView.setTypeface(avenirNextMediumFont);
                lunchCaloriesView.setTypeface(avenirNextMediumFont);
                dinnerLabelView.setTypeface(avenirNextMediumFont);
                dinnerCaloriesView.setTypeface(avenirNextMediumFont);
                snacksLabelView.setTypeface(avenirNextMediumFont);
                snackCaloriesView.setTypeface(avenirNextMediumFont);
                exerciseLabelView.setTypeface(avenirNextMediumFont);
                exerciseCaloriesView.setTypeface(avenirNextMediumFont);
                noteLabelView.setTypeface(avenirNextMediumFont);

                plusLabelView1.setTypeface(avenirNextReqularFont);
                plusLabelView2.setTypeface(avenirNextReqularFont);
                plusLabelView3.setTypeface(avenirNextReqularFont);
                plusLabelView4.setTypeface(avenirNextReqularFont);
                plusLabelView5.setTypeface(avenirNextReqularFont);
                plusLabelView6.setTypeface(avenirNextReqularFont);
                addFoodLabelView1.setTypeface(avenirNextMediumFont);
                addFoodLabelView2.setTypeface(avenirNextMediumFont);
                addFoodLabelView3.setTypeface(avenirNextMediumFont);
                addFoodLabelView4.setTypeface(avenirNextMediumFont);
                addFoodLabelView5.setTypeface(avenirNextMediumFont);
                addFoodLabelView6.setTypeface(avenirNextMediumFont);

                chooseMyMealsBtn.setTypeface(avenirNextMediumFont);
                sharePlanBtn.setTypeface(avenirNextMediumFont);
                nutritionBtn.setTypeface(avenirNextMediumFont);
                createFavoritesBtn.setTypeface(avenirNextMediumFont);
                createPlanBtn.setTypeface(avenirNextMediumFont);

                breakfastPlanDetailsLayout = (LinearLayout) planMainContainer.findViewById(R.id.breakfastPlanDetailsLayout);
                lunchPlanDetailsLayout = (LinearLayout) planMainContainer.findViewById(R.id.lunchPlanDetailsLayout);
                dinnerPlanDetailsLayout = (LinearLayout) planMainContainer.findViewById(R.id.dinnerPlanDetailsLayout);
                snackPlanDetailsLayout = (LinearLayout) planMainContainer.findViewById(R.id.snackPlanDetailsLayout);
                exercisePlanDetailsLayout = (LinearLayout) planMainContainer.findViewById(R.id.exercisePlanDetailsLayout);
                notesDetailsLayout = (LinearLayout) planMainContainer.findViewById(R.id.notesDetailsLayout);

                addBreakfastPlanLayout = (RelativeLayout) planMainContainer.findViewById(R.id.addBreakfastPlanLayout);
                addLunchPlanLayout = (RelativeLayout) planMainContainer.findViewById(R.id.addLunchPlanLayout);
                addDinnerPlanLayout = (RelativeLayout) planMainContainer.findViewById(R.id.addDinnerPlanLayout);
                addSnackPlanLayout = (RelativeLayout) planMainContainer.findViewById(R.id.addSnackPlanLayout);
                addExercisePlanLayout = (RelativeLayout) planMainContainer.findViewById(R.id.addExercisePlanLayout);
                addNotePlanLayout = (RelativeLayout) planMainContainer.findViewById(R.id.addNotePlanLayout);

                addBreakfastPlanLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddFoodSearchActivity("Breakfast");
                    }
                });
                addLunchPlanLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddFoodSearchActivity("Lunch");
                    }
                });
                addDinnerPlanLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddFoodSearchActivity("Dinner");
                    }
                });
                addSnackPlanLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showAddFoodSearchActivity("Snack");
                    }
                });
                addExercisePlanLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(fragmentActivity, HTNewAddExerciseSearchActivity.class);
                        intent.putExtra("addActivityCategory", "exercise");
                        startActivityForResult(intent, 1);
                    }
                });
                addNotePlanLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(fragmentActivity, HTAddActivitySelectItemActivity.class);
                        intent.putExtra("addActivityCategory", "note");
                        startActivityForResult(intent, 1);
                    }
                });

                //planner photo layout
                plannerSwitchLayout2 = (RelativeLayout) planMainContainer.findViewById(R.id.plannerSwitchLayout2);
                planLabel = (TextView) planMainContainer.findViewById(R.id.planLabel);
                planLabel.setTypeface(avenirNextMediumFont);
                linearLayoutPlanPhoto = (LinearLayout) planMainContainer.findViewById(R.id.linearLayoutPlanPhoto);

                loadAnimations();
                changeCameraDistance();

                plannerSwitchLayout1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFoodPhotos = true;
                        mOutAnimation.setTarget(plannerLayout);
                        mInAnimation.setTarget(plannerPhotoLayout);
                        mOutAnimation.start();
                        mInAnimation.start();
                        mActionBar.setVisibility(View.GONE);
                        mPhotoActionBar.setVisibility(View.VISIBLE);
                    }
                });

                plannerSwitchLayout2.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showFoodPhotos = false;
                        mOutAnimation.setTarget(plannerPhotoLayout);
                        mInAnimation.setTarget(plannerLayout);
                        mOutAnimation.start();
                        mInAnimation.start();
                        mPhotoActionBar.setVisibility(View.GONE);
                        mActionBar.setVisibility(View.VISIBLE);
                    }
                });

            }else{
                container.removeView(planMainContainer);
            }

            if(HTGlobals.getInstance().plannerShouldRefresh)
                getPlanner();
        }

        return planMainContainer;
    }

    private void changeCameraDistance() {
        int distance = 10000;
        float scale = getResources().getDisplayMetrics().density * distance;
        plannerLayout.setCameraDistance(scale);
        plannerPhotoLayout.setCameraDistance(scale);
    }

    private void loadAnimations() {
        mInAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(fragmentActivity, R.animator.in_animation);
        mOutAnimation = (AnimatorSet) AnimatorInflater.loadAnimator(fragmentActivity, R.animator.out_animation);

        mInAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                if (showFoodPhotos) {
                    plannerPhotoLayout.setVisibility(View.VISIBLE);
                } else {
                    plannerLayout.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

        mOutAnimation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (showFoodPhotos) {
                    plannerLayout.setVisibility(View.GONE);
                } else {
                    plannerPhotoLayout.setVisibility(View.GONE);
                }

            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    private void getPlanner() {

        numberOfMessages = "";
        newLearningModules = "";
        newEatingPlans = "";

        planName = "";
        templateName = "";

        plannerTargetCalories = "";
        plannerCalories = "";
        plannerCaloriesBurned = "";
        breakfastCalories = "";
        lunchCalories = "";
        dinnerCalories = "";
        snackCalories = "";
        exerciseCalories = "";

        plannerItemID.clear();
        plannerItemCategory.clear();
        plannerItemEat.clear();
        plannerItemMove.clear();
        plannerItemBalance.clear();
        plannerItemReminder.clear();
        plannerItemCalories.clear();
        plannerItemCaloriesBurned.clear();
        plannerItemMealID.clear();
        plannerItemExchangeItems.clear();
        plannerItemPlaceholder.clear();
        plannerItemNotes.clear();
        plannerItemSubNotes.clear();
        plannerItemImage.clear();

        plannerPhotosImage.clear();
        plannerPhotosNotes.clear();
        plannerPhotosCalories.clear();
        plannerPhotosCategory.clear();
        plannerPhotosDate.clear();

        ignoreCaloriesBurned = false;

        caloriesOrOtherString = "calories";
		HTGlobals.getInstance().hideCalorieCalculator = false;

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(fragmentActivity);
        progressDialog.setCancelable(false);
        progressDialog.show();

        if (currentDate.equals(passDate)) {
            // Today
            mDateTitleTextView.setText("Today");
        } else if (currentDate.equals(HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, 1, false))) {
            // Yesterday
            mDateTitleTextView.setText("Yesterday");

        } else {
            // other past dates
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.US);
            calendar = Calendar.getInstance();
            calendar.setTime(passDate);

            mDateTitleTextView.setText(dateFormat.format(calendar.getTime()));
        }

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_planner_new"));
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

                        fragmentActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(fragmentActivity, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {

                        final Element e2 = (Element) nodes.item(0);

                        fragmentActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                String tempString;

                                //Has saved my plans
                                if (XMLFunctions.tagExists(e2, "has_saved_my_plans")) {
                                    tempString = XMLFunctions.getValue(e2, "has_saved_my_plans");
                                    if ("1".equals(tempString)) {
                                        hasSavedMyPlans = true;
                                    } else {
                                        hasSavedMyPlans = false;
                                    }
                                }

                                //Plan Name
                                if (XMLFunctions.tagExists(e2, "plan_name")) {
                                    planName = XMLFunctions.getValue(e2, "plan_name");
                                }

                                //Template Name
                                if (XMLFunctions.tagExists(e2, "template_name")) {
                                    templateName = XMLFunctions.getValue(e2, "template_name");
                                }

                                //Plan Calories
                                if (XMLFunctions.tagExists(e2, "plan_calories")) {
                                    plannerTargetCalories = XMLFunctions.getValue(e2, "plan_calories");
                                }

                                int i = 1;
                                while (XMLFunctions.tagExists(e2, "planner_item_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "id_" + i);
                                    plannerItemID.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "plan_category_" + i);
                                    plannerItemCategory.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "eat_" + i);
                                    plannerItemEat.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "move_" + i);
                                    plannerItemMove.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "balance_" + i);
                                    plannerItemBalance.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "reminder_" + i);
                                    plannerItemReminder.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "calories_" + i);
                                    plannerItemCalories.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "burned_calories_" + i);
                                    plannerItemCaloriesBurned.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "meal_id_" + i);
                                    plannerItemMealID.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "exchange_items_" + i);
                                    plannerItemExchangeItems.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "placeholder_" + i);
                                    plannerItemPlaceholder.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "notes_" + i);
                                    plannerItemNotes.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "sub_notes_" + i);
                                    plannerItemSubNotes.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "image_" + i);
                                    plannerItemImage.add(tempString);
                                    i++;
                                }

                                plannerItemCount = i;

                                i = 1;
                                while (XMLFunctions.tagExists(e2, "planner_photos_image_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "planner_photos_image_" + i);
                                    plannerPhotosImage.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "planner_photos_notes_" + i);
                                    plannerPhotosNotes.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "planner_photos_calories_" + i);
                                    plannerPhotosCalories.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "planner_photos_category_" + i);
                                    plannerPhotosCategory.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "planner_photos_date_" + i);
                                    plannerPhotosDate.add(tempString);
                                    i++;
                                }

                                //Show messages
                                if (XMLFunctions.tagExists(e2, "show_messages")) {
                                    String value = XMLFunctions.getValue(e2, "show_messages");
                                    if ("1".equals(value)) {
                                        if (XMLFunctions.tagExists(e2, "new_messages")) {
                                            numberOfMessages = XMLFunctions.getValue(e2, "new_messages");
                                        }

                                        if ("0".equals(numberOfMessages)) {
                                            ((HTTabBar) fragmentActivity).showDashboardBadgeView(false);
                                            ((HTTabBar) fragmentActivity).showMoreBadgeView(false);
                                        } else {
                                            Set<String> tmpCheckedItems = mSharedPreferences.getStringSet("CheckedDashboardItems", null);
                                            if (tmpCheckedItems == null || tmpCheckedItems.contains("Messages"))
                                                ((HTTabBar) fragmentActivity).showDashboardBadgeView(true);
                                            else
                                                ((HTTabBar) fragmentActivity).showDashboardBadgeView(false);
                                            ((HTTabBar) fragmentActivity).showMoreBadgeView(true);
                                            ((HTTabBar) fragmentActivity).setDashboardBadgeCount(numberOfMessages);
                                            ((HTTabBar) fragmentActivity).setMoreBadgeCount(numberOfMessages);
                                        }
                                    }
                                }

                                //new learning modules
                                if (XMLFunctions.tagExists(e2, "new_learning_modules")) {
                                    newLearningModules = XMLFunctions.getValue(e2, "new_learning_modules");
                                }

                                if ("0".equals(newLearningModules)) {
                                    ((HTTabBar) fragmentActivity).showLearnBadgeView(false);
                                } else {
                                    ((HTTabBar) fragmentActivity).showLearnBadgeView(true);
                                    ((HTTabBar) fragmentActivity).setLearnBadgeCount(newLearningModules);
                                }

                                //new eating plan
                                if (XMLFunctions.tagExists(e2, "new_eating_plan")) {
                                    newEatingPlans = XMLFunctions.getValue(e2, "new_eating_plan");
                                }

                                if(!HTGlobals.getInstance().hidePlanner) {
                                    if ("0".equals(newEatingPlans)) {
                                        ((HTTabBar) fragmentActivity).showPlanBadgeView(false);
                                    } else {
                                        ((HTTabBar) fragmentActivity).showPlanBadgeView(true);
                                        ((HTTabBar) fragmentActivity).setPlanBadgeCount(newEatingPlans);
                                    }
                                }

                                //display badge number on App Icon.
                                int totalBadgeCount = 0, tempCount;
                                try {
                                    tempCount = Integer.parseInt(numberOfMessages);
                                }catch(NumberFormatException e){
                                    tempCount = 0;
                                }
                                totalBadgeCount += tempCount;
                                try {
                                    tempCount = Integer.parseInt(newLearningModules);
                                }catch(NumberFormatException e){
                                    tempCount = 0;
                                }
                                totalBadgeCount += tempCount;
                                if(!HTGlobals.getInstance().hidePlanner) {
                                    try {
                                        tempCount = Integer.parseInt(newEatingPlans);
                                    } catch (NumberFormatException e) {
                                        tempCount = 0;
                                    }
                                    totalBadgeCount += tempCount;
                                }

                                HTGlobals.getInstance().setAppIconBadge(fragmentActivity, totalBadgeCount);

                                // caloriesOrOtherString
                                if (XMLFunctions.tagExists(e2, "calories_or_other_string")) {
                                    caloriesOrOtherString = XMLFunctions.getValue(e2, "calories_or_other_string");
                                }

                                //planner calories
                                if (XMLFunctions.tagExists(e2, "planner_calories")) {
                                    plannerCalories = XMLFunctions.getValue(e2, "planner_calories");
                                }

                                //planner calories burned
                                if (XMLFunctions.tagExists(e2, "planner_calories_burned")) {
                                    plannerCaloriesBurned = XMLFunctions.getValue(e2, "planner_calories_burned");
                                }

                                //breakfast calories
                                if (XMLFunctions.tagExists(e2, "breakfast_calories")) {
                                    breakfastCalories = XMLFunctions.getValue(e2, "breakfast_calories");
                                }

                                //lunch calories
                                if (XMLFunctions.tagExists(e2, "lunch_calories")) {
                                    lunchCalories = XMLFunctions.getValue(e2, "lunch_calories");
                                }

                                //dinner calories
                                if (XMLFunctions.tagExists(e2, "dinner_calories")) {
                                    dinnerCalories = XMLFunctions.getValue(e2, "dinner_calories");
                                }

                                //snack calories
                                if (XMLFunctions.tagExists(e2, "snack_calories")) {
                                    snackCalories = XMLFunctions.getValue(e2, "snack_calories");
                                }

                                //exercise calories
                                if (XMLFunctions.tagExists(e2, "exercise_calories")) {
                                    exerciseCalories = XMLFunctions.getValue(e2, "exercise_calories");
                                }

                                if (XMLFunctions.tagExists(e2, "ignore_calories_burned")) {
                                    tempString = XMLFunctions.getValue(e2, "ignore_calories_burned");
                                    if ("1".equals(tempString)) {
                                        ignoreCaloriesBurned = true;
                                    } else {
                                        ignoreCaloriesBurned = false;
                                    }
                                }

								// hideCalorieCalculator
                                if (XMLFunctions.tagExists(e2, "hide_calorie_calculator")) {
                                    tempString = XMLFunctions.getValue(e2, "hide_calorie_calculator");
                                    if ("1".equals(tempString)) {
                                        HTGlobals.getInstance().hideCalorieCalculator = true;
                                    } else {
                                        HTGlobals.getInstance().hideCalorieCalculator = false;
                                    }
                                }

                                if (XMLFunctions.tagExists(e2, "has_plans_to_scramble")) {
                                    tempString = XMLFunctions.getValue(e2, "has_plans_to_scramble");
                                    if ("1".equals(tempString)) {
                                        showChooseMyMealsButton = true;
                                    } else {
                                        showChooseMyMealsButton = false;
                                    }
                                }

                                if (XMLFunctions.tagExists(e2, "has_plans_to_share")) {
                                    tempString = XMLFunctions.getValue(e2, "has_plans_to_share");
                                    if ("1".equals(tempString)) {
                                        showSharePlanButton = true;
                                    } else {
                                        showSharePlanButton = false;
                                    }
                                }
								
                                showPlanner();
                                showPlannerPhoto();
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    // these all happen with the post.abort() but can be ignored, as the call will be executed again
                    if (error != null) {
                        if (!error.equals("Connection already shutdown") && !error.equals("Request already aborted") && !error.equals("No peer certificate") && !error.equals("Request must not be null.") && !error.equals("Connection has been shut down.") && !error.equals("Connection to https://www.setpointhealth.com refused") && !error.equals("Connection to https://www.trackmyday.org refused")) {

                            fragmentActivity.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

//                                    toast = HTToast.showToast(fragmentActivity, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(fragmentActivity, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                                }
                            });

                        }
                    }

                } finally {

                    fragmentActivity.runOnUiThread(new Runnable() {

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

    private void deleteFoodItem() {
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(fragmentActivity);
        progressDialog.show();

        isSavingScrollPosition = true;

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_food_delete_item"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("WhichID", selectedPlannerItemID));

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

                        fragmentActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(fragmentActivity, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {

                        fragmentActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                getPlanner();
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    // these all happen with the post.abort() but can be ignored, as the call will be executed again
                    if (error != null) {
                        if (!error.equals("Connection already shutdown") && !error.equals("Request already aborted") && !error.equals("No peer certificate") && !error.equals("Request must not be null.") && !error.equals("Connection has been shut down.") && !error.equals("Connection to https://www.setpointhealth.com refused") && !error.equals("Connection to https://www.trackmyday.org refused")) {

                            fragmentActivity.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

//                                    toast = HTToast.showToast(fragmentActivity, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(fragmentActivity, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                                }
                            });

                        }
                    }

                } finally {

                    fragmentActivity.runOnUiThread(new Runnable() {

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

    private void showPlanner() {
        HTGlobals.getInstance().plannerShouldRefresh = false;

        if (breakfastPlanDetailsLayout.getChildCount() > 0)
            breakfastPlanDetailsLayout.removeAllViews();
        if (lunchPlanDetailsLayout.getChildCount() > 0)
            lunchPlanDetailsLayout.removeAllViews();
        if (dinnerPlanDetailsLayout.getChildCount() > 0)
            dinnerPlanDetailsLayout.removeAllViews();
        if (snackPlanDetailsLayout.getChildCount() > 0)
            snackPlanDetailsLayout.removeAllViews();
        if (exercisePlanDetailsLayout.getChildCount() > 0)
            exercisePlanDetailsLayout.removeAllViews();
        if (notesDetailsLayout.getChildCount() > 0)
            notesDetailsLayout.removeAllViews();

        trackerContainer.setVisibility(View.VISIBLE);

        if (hasSavedMyPlans) {
            mActionBar.findViewById(R.id.rightButton).setVisibility(View.VISIBLE);
        } else {
            mActionBar.findViewById(R.id.rightButton).setVisibility(View.GONE);
        }

        if (plannerItemID.size() > 0) {
            mActionBar.findViewById(R.id.leftButton).setVisibility(View.VISIBLE);
        } else {
            mActionBar.findViewById(R.id.leftButton).setVisibility(View.GONE);
        }

        if (ignoreCaloriesBurned) {
            minusSignView.setVisibility(View.INVISIBLE);
            plusSignView.setVisibility(View.INVISIBLE);
            equalSignView.setVisibility(View.INVISIBLE);
        } else {
            minusSignView.setVisibility(View.VISIBLE);
            plusSignView.setVisibility(View.VISIBLE);
            equalSignView.setVisibility(View.VISIBLE);
        }

        goalValue.setText(plannerTargetCalories);
        consumedValue.setText(plannerCalories);
        exerciseValue.setText(plannerCaloriesBurned);
        int caloriesRemaining = 0;
        try {
            if(ignoreCaloriesBurned)
                caloriesRemaining = Integer.parseInt(plannerTargetCalories) - Integer.parseInt(plannerCalories);
            else
                caloriesRemaining = Integer.parseInt(plannerTargetCalories) - Integer.parseInt(plannerCalories) + Integer.parseInt(plannerCaloriesBurned);
            remainingValue.setText(String.valueOf(caloriesRemaining));
        } catch (NumberFormatException e) {
            remainingValue.setText("");
        }

        if(caloriesRemaining  < 0){
            remainingValue.setTextColor(Color.parseColor("#ee6b73"));
        }else{
            remainingValue.setTextColor(Color.parseColor("#c1e991"));
        }

        // planner photos??

        // check to make sure at least one of these images exists on the device
        boolean hasPlannerPhotos = false;
        File plannerPhotoDir = fragmentActivity.getExternalFilesDir("plannerPhoto");
        File[] plannerPhotos = plannerPhotoDir.listFiles();
        if (plannerPhotos.length > 0 && plannerPhotosImage.size() > 0) {
            for(int i = 0; i < plannerPhotos.length; i++){
                if(plannerPhotosImage.contains(plannerPhotos[i].getName())){
                    hasPlannerPhotos = true;
                } else {
                    plannerPhotos[i].delete();
                }
            }
        }

        if (hasPlannerPhotos) {
            plannerSwitchLayout1.setVisibility(View.VISIBLE);
        } else {
            plannerSwitchLayout1.setVisibility(View.GONE);
        }

        boolean hasBreakfastItems = false;
        boolean hasLunchItems = false;
        boolean hasDinnerItems = false;
        boolean hasSnackItems = false;
        boolean hasExerciseItems = false;
        boolean hasNoteItems = false;

        String currentPlannerItemType = "";
        String currentPlannerSubNotes = "";

        View barView;
        LinearLayout.LayoutParams linearParams;

        // breakfast calories
        if("".equals(breakfastCalories) || "0".equals(breakfastCalories)){
            breakfastCaloriesView.setText("");
        }else{
            SpannableString spannableString = new SpannableString(breakfastCalories + "cal");
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextMediumFont, PixelUtil.dpToPx(fragmentActivity, 11)), spannableString.length()-3, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            breakfastCaloriesView.setText(spannableString);
        }

        // breakfast items!
        for (int i = 0; i < plannerItemID.size(); i++) {
            final int index = i;
            if("breakfast".equals(plannerItemCategory.get(i))){
                hasBreakfastItems = true;

                // currentPlannerItemType
                currentPlannerItemType = "EAT";

                // template w/ no selection?
                if ("1".equals(plannerItemPlaceholder.get(i)) &&
                        ("".equals(plannerItemCalories.get(i)) || "0".equals(plannerItemCalories.get(i))) &&
                        ("".equals(plannerItemMealID.get(i)) || "0".equals(plannerItemMealID.get(i)))) {
                    currentPlannerItemType = "TEMPLATE";
                }

                RelativeLayout plannerItemView = (RelativeLayout) mInflater.inflate(R.layout.planner_item_view_new, null);

                if("TEMPLATE".equals(currentPlannerItemType)) { // empty template item
                    plannerItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedPlannerItemID = plannerItemID.get(index);
                            Intent intent = new Intent(fragmentActivity, HTAddFoodSearchResultsActivity.class);
                            intent.putExtra("relaunchItemID", Integer.parseInt(selectedPlannerItemID));
                            intent.putExtra("addFoodCategory", "template");
                            startActivityForResult(intent, 1);
                        }
                    });
                }else{
                    plannerItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // template item
                            if (plannerItemPlaceholder.get(index).equals("1")) {
                                selectedFoodCategory = "template";
                            }

                            if (!"".equals(plannerItemExchangeItems.get(index))) {
                                selectedFoodCategory = "exchange";
                                exchangeItemsString = plannerItemExchangeItems.get(index);
                            }
                            Intent intent = new Intent(fragmentActivity, HTAddFoodSelectItemActivity.class);
                            intent.putExtra("showChangeFoodSelection", true);
                            if ("template".equals(selectedFoodCategory)) {
                                intent.putExtra("addFoodCategory", "template");
//                                intent.putExtra("selectedFoodID", Integer.parseInt(plannerItemMealID.get(index)));
                                intent.putExtra("selectedFoodID", plannerItemMealID.get(index));
                                intent.putExtra("relaunchItemID", Integer.parseInt(plannerItemID.get(index)));
                            } else if ("exchange".equals(selectedFoodCategory)) {
                                intent.putExtra("addFoodCategory", "exchange");
                                intent.putExtra("exchangeItemsString", exchangeItemsString.replace("***", "||"));
//                                intent.putExtra("selectedFoodID", Integer.parseInt(plannerItemID.get(index)));
                                intent.putExtra("selectedFoodID", plannerItemID.get(index));
                            } else {
//                                intent.putExtra("selectedFoodID", Integer.parseInt(plannerItemID.get(index)));
                                intent.putExtra("selectedFoodID", plannerItemID.get(index));
                            }
                            intent.putExtra("relaunchPlannerItem", true);
                            if ("template".equals(selectedFoodCategory)
                                    || "exchange".equals(selectedFoodCategory))
                                intent.putExtra("inTemplateString", "true");
                            startActivityForResult(intent, 1);
                        }
                    });
                }
                plannerItemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        selectedPlannerItemID = plannerItemID.get(index);
                        showConfirmDeleteItem();
                        return true;
                    }
                });

                plannerItemView.setOnTouchListener(new OnSwipeTouchListener(fragmentActivity) {
                    @Override
                    public void onSwipeLeft() {
                        selectedPlannerItemID = plannerItemID.get(index);
                        showConfirmDeleteItem();
                    }

                    @Override
                    public void onSwipeRight() {
                        selectedPlannerItemID = plannerItemID.get(index);
                        showConfirmDeleteItem();
                    }
                });

                linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(fragmentActivity, 44));
                plannerItemView.setLayoutParams(linearParams);

                // sub_notes?
                currentPlannerSubNotes = plannerItemSubNotes.get(i);
                if("".equals(currentPlannerSubNotes)) { // no sub_notes
                    if ("TEMPLATE".equals(currentPlannerItemType)) { // empty template item

                        currentPlannerSubNotes = "Select food...";

                    } else if (!"".equals(plannerItemCalories.get(i))) {

                        currentPlannerSubNotes = plannerItemCalories.get(i) + " " + caloriesOrOtherString;
                    }
                }

                TextView notesLabel = (TextView) plannerItemView.findViewById(R.id.notesLabel);
                TextView subnotesLabel = (TextView) plannerItemView.findViewById(R.id.subnotesLabel);
                TextView caloriesView = (TextView) plannerItemView.findViewById(R.id.caloriesView);
                notesLabel.setTypeface(avenirNextMediumFont);
                subnotesLabel.setTypeface(avenirNextReqularFont);
                caloriesView.setTypeface(avenirNextMediumFont);
                notesLabel.setText(plannerItemNotes.get(i));
                subnotesLabel.setText(currentPlannerSubNotes);
                caloriesView.setText(plannerItemCalories.get(i));

                breakfastPlanDetailsLayout.addView(plannerItemView);
            }
        }

        // separator?
        if(hasBreakfastItems){
            barView = new View(fragmentActivity);
            barView.setBackgroundColor(grayBarColor);
            linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(fragmentActivity, 1));
            barView.setLayoutParams(linearParams);
            breakfastPlanDetailsLayout.addView(barView);
        }

        // lunch calories
        if("".equals(lunchCalories) || "0".equals(lunchCalories)){
            lunchCaloriesView.setText("");
        }else{
            SpannableString spannableString = new SpannableString(lunchCalories + "cal");
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextMediumFont, PixelUtil.dpToPx(fragmentActivity, 11)), spannableString.length()-3, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            lunchCaloriesView.setText(spannableString);
        }

        // lunch items!
        for (int i = 0; i < plannerItemID.size(); i++) {
            final int index = i;
            if("lunch".equals(plannerItemCategory.get(i))){
                hasLunchItems = true;

                // currentPlannerItemType
                currentPlannerItemType = "EAT";

                // template w/ no selection?
                if ("1".equals(plannerItemPlaceholder.get(i)) &&
                ("".equals(plannerItemCalories.get(i)) || "0".equals(plannerItemCalories.get(i))) &&
                ("".equals(plannerItemMealID.get(i)) || "0".equals(plannerItemMealID.get(i)))) {
                    currentPlannerItemType = "TEMPLATE";
                }

                RelativeLayout plannerItemView = (RelativeLayout) mInflater.inflate(R.layout.planner_item_view_new, null);

                if("TEMPLATE".equals(currentPlannerItemType)) { // empty template item
                    plannerItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedPlannerItemID = plannerItemID.get(index);
                            Intent intent = new Intent(fragmentActivity, HTAddFoodSearchResultsActivity.class);
                            intent.putExtra("relaunchItemID", Integer.parseInt(selectedPlannerItemID));
                            intent.putExtra("addFoodCategory", "template");
                            startActivityForResult(intent, 1);
                        }
                    });
                }else{
                    plannerItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // template item
                            if (plannerItemPlaceholder.get(index).equals("1")) {
                                selectedFoodCategory = "template";
                            }

                            if (!"".equals(plannerItemExchangeItems.get(index))) {
                                selectedFoodCategory = "exchange";
                                exchangeItemsString = plannerItemExchangeItems.get(index);
                            }
                            Intent intent = new Intent(fragmentActivity, HTAddFoodSelectItemActivity.class);
                            intent.putExtra("showChangeFoodSelection", true);
                            if ("template".equals(selectedFoodCategory)) {
                                intent.putExtra("addFoodCategory", "template");
//                                intent.putExtra("selectedFoodID", Integer.parseInt(plannerItemMealID.get(index)));
                                intent.putExtra("selectedFoodID", plannerItemMealID.get(index));
                                intent.putExtra("relaunchItemID", Integer.parseInt(plannerItemID.get(index)));
                            } else if ("exchange".equals(selectedFoodCategory)) {
                                intent.putExtra("addFoodCategory", "exchange");
                                intent.putExtra("exchangeItemsString", exchangeItemsString.replace("***", "||"));
//                                intent.putExtra("selectedFoodID", Integer.parseInt(plannerItemID.get(index)));
                                intent.putExtra("selectedFoodID", plannerItemID.get(index));
                            } else {
//                                intent.putExtra("selectedFoodID", Integer.parseInt(plannerItemID.get(index)));
                                intent.putExtra("selectedFoodID", plannerItemID.get(index));
                            }
                            intent.putExtra("relaunchPlannerItem", true);
                            if ("template".equals(selectedFoodCategory)
                                    || "exchange".equals(selectedFoodCategory))
                                intent.putExtra("inTemplateString", "true");
                            startActivityForResult(intent, 1);
                        }
                    });
                }

                plannerItemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        selectedPlannerItemID = plannerItemID.get(index);
                        showConfirmDeleteItem();
                        return true;
                    }
                });

                plannerItemView.setOnTouchListener(new OnSwipeTouchListener(fragmentActivity) {
                    @Override
                    public void onSwipeLeft() {
                        selectedPlannerItemID = plannerItemID.get(index);
                        showConfirmDeleteItem();
                    }

                    @Override
                    public void onSwipeRight() {
                        selectedPlannerItemID = plannerItemID.get(index);
                        showConfirmDeleteItem();
                    }
                });

                linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(fragmentActivity, 44));
                plannerItemView.setLayoutParams(linearParams);

                // sub_notes?
                currentPlannerSubNotes = plannerItemSubNotes.get(i);
                if("".equals(currentPlannerSubNotes)) { // no sub_notes
                    if ("TEMPLATE".equals(currentPlannerItemType)) { // empty template item

                        currentPlannerSubNotes = "Select food...";

                    } else if (!"".equals(plannerItemCalories.get(i))) {

                        currentPlannerSubNotes = plannerItemCalories.get(i) + " " + caloriesOrOtherString;
                    }
                }

                TextView notesLabel = (TextView) plannerItemView.findViewById(R.id.notesLabel);
                TextView subnotesLabel = (TextView) plannerItemView.findViewById(R.id.subnotesLabel);
                TextView caloriesView = (TextView) plannerItemView.findViewById(R.id.caloriesView);
                notesLabel.setTypeface(avenirNextMediumFont);
                subnotesLabel.setTypeface(avenirNextReqularFont);
                caloriesView.setTypeface(avenirNextMediumFont);
                notesLabel.setText(plannerItemNotes.get(i));
                subnotesLabel.setText(currentPlannerSubNotes);
                caloriesView.setText(plannerItemCalories.get(i));

                lunchPlanDetailsLayout.addView(plannerItemView);
            }
        }

        // separator?
        if(hasLunchItems){
            barView = new View(fragmentActivity);
            barView.setBackgroundColor(grayBarColor);
            linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(fragmentActivity, 1));
            barView.setLayoutParams(linearParams);
            lunchPlanDetailsLayout.addView(barView);
        }

        // dinner calories
        if("".equals(dinnerCalories) || "0".equals(dinnerCalories)){
            dinnerCaloriesView.setText("");
        }else{
            SpannableString spannableString = new SpannableString(dinnerCalories + "cal");
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextMediumFont, PixelUtil.dpToPx(fragmentActivity, 11)), spannableString.length()-3, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            dinnerCaloriesView.setText(spannableString);
        }

        // dinner items!
        for (int i = 0; i < plannerItemID.size(); i++) {
            final int index = i;
            if("dinner".equals(plannerItemCategory.get(i))){
                hasDinnerItems = true;

                // currentPlannerItemType
                currentPlannerItemType = "EAT";

                // template w/ no selection?
                if ("1".equals(plannerItemPlaceholder.get(i)) &&
                        ("".equals(plannerItemCalories.get(i)) || "0".equals(plannerItemCalories.get(i))) &&
                        ("".equals(plannerItemMealID.get(i)) || "0".equals(plannerItemMealID.get(i)))) {
                    currentPlannerItemType = "TEMPLATE";
                }

                RelativeLayout plannerItemView = (RelativeLayout) mInflater.inflate(R.layout.planner_item_view_new, null);

                if("TEMPLATE".equals(currentPlannerItemType)) { // empty template item
                    plannerItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedPlannerItemID = plannerItemID.get(index);
                            Intent intent = new Intent(fragmentActivity, HTAddFoodSearchResultsActivity.class);
                            intent.putExtra("relaunchItemID", Integer.parseInt(selectedPlannerItemID));
                            intent.putExtra("addFoodCategory", "template");
                            startActivityForResult(intent, 1);
                        }
                    });
                }else{
                    plannerItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // template item
                            if (plannerItemPlaceholder.get(index).equals("1")) {
                                selectedFoodCategory = "template";
                            }

                            if (!"".equals(plannerItemExchangeItems.get(index))) {
                                selectedFoodCategory = "exchange";
                                exchangeItemsString = plannerItemExchangeItems.get(index);
                            }
                            Intent intent = new Intent(fragmentActivity, HTAddFoodSelectItemActivity.class);
                            intent.putExtra("showChangeFoodSelection", true);
                            if ("template".equals(selectedFoodCategory)) {
                                intent.putExtra("addFoodCategory", "template");
//                                intent.putExtra("selectedFoodID", Integer.parseInt(plannerItemMealID.get(index)));
                                intent.putExtra("selectedFoodID", plannerItemMealID.get(index));
                                intent.putExtra("relaunchItemID", Integer.parseInt(plannerItemID.get(index)));
                            } else if ("exchange".equals(selectedFoodCategory)) {
                                intent.putExtra("addFoodCategory", "exchange");
                                intent.putExtra("exchangeItemsString", exchangeItemsString.replace("***", "||"));
//                                intent.putExtra("selectedFoodID", Integer.parseInt(plannerItemID.get(index)));
                                intent.putExtra("selectedFoodID", plannerItemID.get(index));
                            } else {
//                                intent.putExtra("selectedFoodID", Integer.parseInt(plannerItemID.get(index)));
                                intent.putExtra("selectedFoodID", plannerItemID.get(index));
                            }
                            intent.putExtra("relaunchPlannerItem", true);
                            if ("template".equals(selectedFoodCategory)
                                    || "exchange".equals(selectedFoodCategory))
                                intent.putExtra("inTemplateString", "true");
                            startActivityForResult(intent, 1);
                        }
                    });
                }

                plannerItemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        selectedPlannerItemID = plannerItemID.get(index);
                        showConfirmDeleteItem();
                        return true;
                    }
                });

                plannerItemView.setOnTouchListener(new OnSwipeTouchListener(fragmentActivity) {
                    @Override
                    public void onSwipeLeft() {
                        selectedPlannerItemID = plannerItemID.get(index);
                        showConfirmDeleteItem();
                    }

                    @Override
                    public void onSwipeRight() {
                        selectedPlannerItemID = plannerItemID.get(index);
                        showConfirmDeleteItem();
                    }
                });

                linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(fragmentActivity, 44));
                plannerItemView.setLayoutParams(linearParams);

                // sub_notes?
                currentPlannerSubNotes = plannerItemSubNotes.get(i);
                if("".equals(currentPlannerSubNotes)) { // no sub_notes
                    if ("TEMPLATE".equals(currentPlannerItemType)) { // empty template item

                        currentPlannerSubNotes = "Select food...";

                    } else if (!"".equals(plannerItemCalories.get(i))) {

                        currentPlannerSubNotes = plannerItemCalories.get(i) + " " + caloriesOrOtherString;
                    }
                }

                TextView notesLabel = (TextView) plannerItemView.findViewById(R.id.notesLabel);
                TextView subnotesLabel = (TextView) plannerItemView.findViewById(R.id.subnotesLabel);
                TextView caloriesView = (TextView) plannerItemView.findViewById(R.id.caloriesView);
                notesLabel.setTypeface(avenirNextMediumFont);
                subnotesLabel.setTypeface(avenirNextReqularFont);
                caloriesView.setTypeface(avenirNextMediumFont);
                notesLabel.setText(plannerItemNotes.get(i));
                subnotesLabel.setText(currentPlannerSubNotes);
                caloriesView.setText(plannerItemCalories.get(i));

                dinnerPlanDetailsLayout.addView(plannerItemView);
            }
        }

        // separator?
        if(hasDinnerItems){
            barView = new View(fragmentActivity);
            barView.setBackgroundColor(grayBarColor);
            linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(fragmentActivity, 1));
            barView.setLayoutParams(linearParams);
            dinnerPlanDetailsLayout.addView(barView);
        }

        // snack calories
        if("".equals(snackCalories) || "0".equals(snackCalories)){
            snackCaloriesView.setText("");
        }else{
            SpannableString spannableString = new SpannableString(snackCalories + "cal");
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextMediumFont, PixelUtil.dpToPx(fragmentActivity, 11)), spannableString.length()-3, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            snackCaloriesView.setText(spannableString);
        }

        // snack items!
        for (int i = 0; i < plannerItemID.size(); i++) {
            final int index = i;
            if("snack".equals(plannerItemCategory.get(i))){
                hasSnackItems = true;

                // currentPlannerItemType
                currentPlannerItemType = "EAT";

                // template w/ no selection?
                if ("1".equals(plannerItemPlaceholder.get(i)) &&
                        ("".equals(plannerItemCalories.get(i)) || "0".equals(plannerItemCalories.get(i))) &&
                        ("".equals(plannerItemMealID.get(i)) || "0".equals(plannerItemMealID.get(i)))) {
                    currentPlannerItemType = "TEMPLATE";
                }

                RelativeLayout plannerItemView = (RelativeLayout) mInflater.inflate(R.layout.planner_item_view_new, null);

                if("TEMPLATE".equals(currentPlannerItemType)) { // empty template item
                    plannerItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            selectedPlannerItemID = plannerItemID.get(index);
                            Intent intent = new Intent(fragmentActivity, HTAddFoodSearchResultsActivity.class);
                            intent.putExtra("relaunchItemID", Integer.parseInt(selectedPlannerItemID));
                            intent.putExtra("addFoodCategory", "template");
                            startActivityForResult(intent, 1);
                        }
                    });
                }else{
                    plannerItemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // template item
                            if (plannerItemPlaceholder.get(index).equals("1")) {
                                selectedFoodCategory = "template";
                            }

                            if (!"".equals(plannerItemExchangeItems.get(index))) {
                                selectedFoodCategory = "exchange";
                                exchangeItemsString = plannerItemExchangeItems.get(index);
                            }
                            Intent intent = new Intent(fragmentActivity, HTAddFoodSelectItemActivity.class);
                            intent.putExtra("showChangeFoodSelection", true);
                            if ("template".equals(selectedFoodCategory)) {
                                intent.putExtra("addFoodCategory", "template");
//                                intent.putExtra("selectedFoodID", Integer.parseInt(plannerItemMealID.get(index)));
                                intent.putExtra("selectedFoodID", plannerItemMealID.get(index));
                                intent.putExtra("relaunchItemID", Integer.parseInt(plannerItemID.get(index)));
                            } else if ("exchange".equals(selectedFoodCategory)) {
                                intent.putExtra("addFoodCategory", "exchange");
                                intent.putExtra("exchangeItemsString", exchangeItemsString.replace("***", "||"));
//                                intent.putExtra("selectedFoodID", Integer.parseInt(plannerItemID.get(index)));
                                intent.putExtra("selectedFoodID", plannerItemID.get(index));
                            } else {
//                                intent.putExtra("selectedFoodID", Integer.parseInt(plannerItemID.get(index)));
                                intent.putExtra("selectedFoodID", plannerItemID.get(index));
                            }
                            intent.putExtra("relaunchPlannerItem", true);
                            if ("template".equals(selectedFoodCategory)
                                    || "exchange".equals(selectedFoodCategory))
                                intent.putExtra("inTemplateString", "true");
                            startActivityForResult(intent, 1);
                        }
                    });
                }

                plannerItemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        selectedPlannerItemID = plannerItemID.get(index);
                        showConfirmDeleteItem();
                        return true;
                    }
                });

                plannerItemView.setOnTouchListener(new OnSwipeTouchListener(fragmentActivity) {
                    @Override
                    public void onSwipeLeft() {
                        selectedPlannerItemID = plannerItemID.get(index);
                        showConfirmDeleteItem();
                    }

                    @Override
                    public void onSwipeRight() {
                        selectedPlannerItemID = plannerItemID.get(index);
                        showConfirmDeleteItem();
                    }
                });

                linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(fragmentActivity, 44));
                plannerItemView.setLayoutParams(linearParams);

                // sub_notes?
                currentPlannerSubNotes = plannerItemSubNotes.get(i);
                if("".equals(currentPlannerSubNotes)) { // no sub_notes
                    if ("TEMPLATE".equals(currentPlannerItemType)) { // empty template item

                        currentPlannerSubNotes = "Select food...";

                    } else if (!"".equals(plannerItemCalories.get(i))) {

                        currentPlannerSubNotes = plannerItemCalories.get(i) + " " + caloriesOrOtherString;
                    }
                }

                TextView notesLabel = (TextView) plannerItemView.findViewById(R.id.notesLabel);
                TextView subnotesLabel = (TextView) plannerItemView.findViewById(R.id.subnotesLabel);
                TextView caloriesView = (TextView) plannerItemView.findViewById(R.id.caloriesView);
                notesLabel.setTypeface(avenirNextMediumFont);
                subnotesLabel.setTypeface(avenirNextReqularFont);
                caloriesView.setTypeface(avenirNextMediumFont);
                notesLabel.setText(plannerItemNotes.get(i));
                subnotesLabel.setText(currentPlannerSubNotes);
                caloriesView.setText(plannerItemCalories.get(i));

                snackPlanDetailsLayout.addView(plannerItemView);
            }
        }

        // separator?
        if(hasSnackItems){
            barView = new View(fragmentActivity);
            barView.setBackgroundColor(grayBarColor);
            linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(fragmentActivity, 1));
            barView.setLayoutParams(linearParams);
            snackPlanDetailsLayout.addView(barView);
        }

        // exercise calories
        if("".equals(exerciseCalories) || "0".equals(exerciseCalories)){
            exerciseCaloriesView.setText("");
        }else{
            SpannableString spannableString = new SpannableString(exerciseCalories + "cal");
            spannableString.setSpan(new HTTypefaceSpan("", avenirNextMediumFont, PixelUtil.dpToPx(fragmentActivity, 11)), spannableString.length()-3, spannableString.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
            exerciseCaloriesView.setText(spannableString);
        }

        // exercise items!
        for (int i = 0; i < plannerItemID.size(); i++) {
            final int index = i;
            if("exercise".equals(plannerItemCategory.get(i))){
                hasExerciseItems = true;

                RelativeLayout plannerItemView = (RelativeLayout) mInflater.inflate(R.layout.planner_item_view_new, null);

                plannerItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedPlannerItemID = plannerItemID.get(index);
                        Intent intent = new Intent(fragmentActivity, HTAddActivitySelectItemActivity.class);

                        intent.putExtra("selectedActivityID", Integer.parseInt(selectedPlannerItemID));
                        intent.putExtra("relaunchPlannerItem", true);

                        if (!"".equals(plannerItemMove.get(index))) {
                            intent.putExtra("addActivityCategory", "exercise");

                        } else if (!"".equals(plannerItemBalance.get(index))) {
                            intent.putExtra("addActivityCategory", "stress");

                        } else {
                            intent.putExtra("addActivityCategory", "note");
                        }

                        startActivityForResult(intent, 1);
                    }
                });

                plannerItemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        selectedPlannerItemID = plannerItemID.get(index);
                        showConfirmDeleteItem();
                        return true;
                    }
                });

                plannerItemView.setOnTouchListener(new OnSwipeTouchListener(fragmentActivity) {
                    @Override
                    public void onSwipeLeft() {
                        selectedPlannerItemID = plannerItemID.get(index);
                        showConfirmDeleteItem();
                    }

                    @Override
                    public void onSwipeRight() {
                        selectedPlannerItemID = plannerItemID.get(index);
                        showConfirmDeleteItem();
                    }
                });

                linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(fragmentActivity, 44));
                plannerItemView.setLayoutParams(linearParams);

                TextView notesLabel = (TextView) plannerItemView.findViewById(R.id.notesLabel);
                TextView subnotesLabel = (TextView) plannerItemView.findViewById(R.id.subnotesLabel);
                subnotesLabel.setVisibility(View.GONE);
                TextView caloriesView = (TextView) plannerItemView.findViewById(R.id.caloriesView);
                notesLabel.setTypeface(avenirNextMediumFont);
                caloriesView.setTypeface(avenirNextMediumFont);
                notesLabel.setText(plannerItemNotes.get(i));
                if (!"".equals(plannerItemCaloriesBurned.get(i)) && !"0".equals(plannerItemCaloriesBurned.get(i))) {
                    caloriesView.setText(plannerItemCaloriesBurned.get(i));
                }

                exercisePlanDetailsLayout.addView(plannerItemView);
            }
        }

        // separator?
        if(hasExerciseItems){
            barView = new View(fragmentActivity);
            barView.setBackgroundColor(grayBarColor);
            linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(fragmentActivity, 1));
            barView.setLayoutParams(linearParams);
            exercisePlanDetailsLayout.addView(barView);
        }

        // note items!
        for (int i = 0; i < plannerItemID.size(); i++) {
            final int index = i;
            if("note".equals(plannerItemCategory.get(i))){
                hasNoteItems = true;

                RelativeLayout plannerItemView = (RelativeLayout) mInflater.inflate(R.layout.planner_item_view_new, null);

                plannerItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        selectedPlannerItemID = plannerItemID.get(index);
                        Intent intent = new Intent(fragmentActivity, HTAddActivitySelectItemActivity.class);

                        intent.putExtra("selectedActivityID", Integer.parseInt(selectedPlannerItemID));
                        intent.putExtra("relaunchPlannerItem", true);

                        if (!"".equals(plannerItemMove.get(index))) {
                            intent.putExtra("addActivityCategory", "exercise");

                        } else if (!"".equals(plannerItemBalance.get(index))) {
                            intent.putExtra("addActivityCategory", "stress");

                        } else {
                            intent.putExtra("addActivityCategory", "note");
                        }

                        startActivityForResult(intent, 1);
                    }
                });

                plannerItemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        selectedPlannerItemID = plannerItemID.get(index);
                        showConfirmDeleteItem();
                        return true;
                    }
                });

                plannerItemView.setOnTouchListener(new OnSwipeTouchListener(fragmentActivity) {
                    @Override
                    public void onSwipeLeft() {
                        selectedPlannerItemID = plannerItemID.get(index);
                        showConfirmDeleteItem();
                    }

                    @Override
                    public void onSwipeRight() {
                        selectedPlannerItemID = plannerItemID.get(index);
                        showConfirmDeleteItem();
                    }
                });

                linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(fragmentActivity, 44));
                plannerItemView.setLayoutParams(linearParams);

                TextView notesLabel = (TextView) plannerItemView.findViewById(R.id.notesLabel);
                TextView subnotesLabel = (TextView) plannerItemView.findViewById(R.id.subnotesLabel);
                subnotesLabel.setVisibility(View.GONE);
                TextView caloriesView = (TextView) plannerItemView.findViewById(R.id.caloriesView);
                caloriesView.setVisibility(View.GONE);
                notesLabel.setTypeface(avenirNextMediumFont);
                notesLabel.setText(plannerItemNotes.get(i));

                notesDetailsLayout.addView(plannerItemView);
            }
        }

        // separator?
        if(hasNoteItems){
            barView = new View(fragmentActivity);
            barView.setBackgroundColor(grayBarColor);
            linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, PixelUtil.dpToPx(fragmentActivity, 1));
            barView.setLayoutParams(linearParams);
            notesDetailsLayout.addView(barView);
        }

        if (showChooseMyMealsButton) {
           chooseMyMealsBtn.setVisibility(View.VISIBLE);
        } else {
            chooseMyMealsBtn.setVisibility(View.GONE);
        }

        if (showSharePlanButton) {
            sharePlanBtn.setVisibility(View.VISIBLE);
        } else {
            sharePlanBtn.setVisibility(View.GONE);
        }

        if(!isSavingScrollPosition){
            scrollViewPlan.scrollTo(0, 0);
        }
        isSavingScrollPosition = false;
    }

    private void showPlannerPhoto() {
        linearLayoutPlanPhoto.removeAllViews();

        for (int i = 0; i <  plannerPhotosImage.size(); i++) {
            File file = new File(fragmentActivity.getExternalFilesDir("plannerPhoto"), plannerPhotosImage.get(i));
            if (file.exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
                float ratio = (float)bitmap.getHeight() / (float)bitmap.getWidth();

                LinearLayout photoItemView = (LinearLayout) mInflater.inflate(R.layout.planner_photo_cell, null);
                ImageView photoView = (ImageView) photoItemView.findViewById(R.id.photoView);

                LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) photoView.getLayoutParams();
                layoutParams.height = (int) (screenWidth * ratio);
                photoView.setLayoutParams(layoutParams);
                photoView.setImageBitmap(bitmap);

                TextView labelView = (TextView) photoItemView.findViewById(R.id.labelView);
                TextView caloriesView = (TextView) photoItemView.findViewById(R.id.caloriesView);
                TextView dateView = (TextView) photoItemView.findViewById(R.id.dateView);

                labelView.setTypeface(avenirNextMediumFont);
                caloriesView.setTypeface(avenirNextMediumFont);
                dateView.setTypeface(avenirNextReqularFont);

                labelView.setText(plannerPhotosNotes.get(i));
                caloriesView.setText(plannerPhotosCalories.get(i)+"cal");
                dateView.setText(plannerPhotosDate.get(i) + " - " + HTGlobals.capitalize(plannerPhotosCategory.get(i)));

                linearLayoutPlanPhoto.addView(photoItemView);
            }
        }

    }

    private void showConfirmDeleteItem(){
        if(isShowingDeleteConfirmDlg)
            return;

        isShowingDeleteConfirmDlg = true;
        Dialog dialog = new AlertDialog.Builder(fragmentActivity)
                .setTitle("Delete Item?")
                .setMessage("Are you sure you want to delete this item from your Plan?")
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteFoodItem();
                        isShowingDeleteConfirmDlg = false;
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        selectedPlannerItemID = "";
                        isShowingDeleteConfirmDlg = false;
                    }
                }).create();
        dialog.show();
    }

    private void showUpdatePlannerTargetCaloriesDialog(){
        // Set up the input
        LayoutInflater li = LayoutInflater.from(fragmentActivity);
        View promptsView = li.inflate(R.layout.update_calories_view, null);
        final EditText input = (EditText) promptsView.findViewById(R.id.input);
        input.setFilters(new InputFilter[]{new InputFilter.LengthFilter(4)});
        input.setText(plannerTargetCalories);
        input.setSelection(plannerTargetCalories.length());
        AlertDialog.Builder builder = null;
        builder = new AlertDialog.Builder(fragmentActivity);
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

    private void updatePlannerTargetCalories() {
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(fragmentActivity);

        myThread = new Thread() {

            @Override
            public void run() {
                try {
                    sleep(150);
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                    return;
                }

                fragmentActivity.runOnUiThread(new Runnable() {
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

                        fragmentActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(fragmentActivity, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        fragmentActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                getPlanner();
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    // these all happen with the post.abort() but can be ignored, as the call will be executed again
                    if (error != null) {
                        if (!error.equals("Connection already shutdown") && !error.equals("Request already aborted") && !error.equals("No peer certificate") && !error.equals("Request must not be null.") && !error.equals("Connection has been shut down.") && !error.equals("Connection to https://www.setpointhealth.com refused") && !error.equals("Connection to https://www.trackmyday.org refused")) {

                            fragmentActivity.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

//                                    toast = HTToast.showToast(fragmentActivity, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(fragmentActivity, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                                }
                            });

                        }
                    }

                } finally {

                    fragmentActivity.runOnUiThread(new Runnable() {

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

    private void leftDateButtonClicked() {

        stopThread();

        // subtract one day
        passDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, -1, true);
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;

        // getPlanner
        getPlanner();
    }

    private void rightDateButtonClicked() {

        stopThread();

        // add one day
        passDate = HTGlobals.getInstance().addNumberOfDaysToPassDate(passDate, 1, true);
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;

        // getPlanner
        getPlanner();
    }

    private void showAddFoodSearchActivity(String foodSearchMeal){
        Intent intent = new Intent(fragmentActivity, HTNewAddFoodSearchActivity.class);
        intent.putExtra("addFoodCategory", "recent");
        intent.putExtra("foodSearchMeal", foodSearchMeal);
        startActivityForResult(intent, 1);
    }

    private void showPlannerNutrition() {
//        Intent intent = new Intent(fragmentActivity, HTPlannerNutritionActivity.class);
        Intent intent = new Intent(fragmentActivity, HTPlannerCaloriesConsumedActivity.class);
        startActivityForResult(intent, 1);
    }

    private void saveToMyPlans(final String planName) {
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(fragmentActivity);

        myThread = new Thread() {

            @Override
            public void run() {
                try {
                    sleep(150);
                } catch (InterruptedException e) {
//                    e.printStackTrace();
                    return;
                }

                fragmentActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        progressDialog.show();
                    }
                });

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "save_planner_to_my_plans"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("plan_name", planName));

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

                        fragmentActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(fragmentActivity, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        fragmentActivity.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                getPlanner();
                            }
                        });
                    }
                } catch (Exception e) {

                    final String error = e.getMessage();

                    // these all happen with the post.abort() but can be ignored, as the call will be executed again
                    if (error != null) {
                        if (!error.equals("Connection already shutdown") && !error.equals("Request already aborted") && !error.equals("No peer certificate") && !error.equals("Request must not be null.") && !error.equals("Connection has been shut down.") && !error.equals("Connection to https://www.setpointhealth.com refused") && !error.equals("Connection to https://www.trackmyday.org refused")) {

                            fragmentActivity.runOnUiThread(new Runnable() {

                                @Override
                                public void run() {

//                                    toast = HTToast.showToast(fragmentActivity, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(fragmentActivity, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
                                }
                            });

                        }
                    }

                } finally {

                    fragmentActivity.runOnUiThread(new Runnable() {

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

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // coming back from a child activity, refresh the calendar

        if (data != null) {
//            addFoodCategory = getIntent().getStringExtra("addFoodCategory");
//            addFoodSearchString = getIntent().getStringExtra("addFoodSearchString");
//            relaunchItemID = getIntent().getIntExtra("relaunchItemID", 0);
//            addFoodSearchFieldString = "";

//            Intent intent = new Intent(fragmentActivity, HTAddFoodSearchResultsActivity.class);
//            intent.putExtra("relaunchItemID", Integer.parseInt(selectedPlannerItemID));
//            intent.putExtra("addFoodCategory", "template");
            data.setClass(fragmentActivity, HTAddFoodSearchResultsActivity.class);
            startActivityForResult(data, 1);

            return;
        }

        passDate = HTGlobals.getInstance().passDate;
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;

        isSavingScrollPosition = true;

        if(HTGlobals.getInstance().plannerShouldRefresh) {
            getPlanner();
        }
    }

}
