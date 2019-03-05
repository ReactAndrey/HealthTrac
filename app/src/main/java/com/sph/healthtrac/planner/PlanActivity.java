package com.sph.healthtrac.planner;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
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

import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTAppletButton;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTDatePicker;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTTabBar;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
import com.sph.healthtrac.common.XMLFunctions;
import com.sph.healthtrac.planner.addactivity.HTAddActivityActivity;
import com.sph.healthtrac.planner.addactivity.HTAddActivitySelectItemActivity;
import com.sph.healthtrac.planner.addfood.HTAddFoodActivity;
import com.sph.healthtrac.planner.addfood.HTAddFoodSearchResultsActivity;
import com.sph.healthtrac.planner.addfood.HTAddFoodSelectItemActivity;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class PlanActivity extends Fragment {
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

    FragmentActivity fragmentActivity;
    RelativeLayout planMainContainer;
    View mActionBar;
    View datePicker;
    TextView mDateTitleTextView;
    RelativeLayout buttonsLayout;
    ScrollView scrollViewPlan;
    LinearLayout linearLayoutMyPlans;

    Calendar calendar;
    DefaultHttpClient client;
    HttpPost post;
    HttpResponse response;
    HttpEntity entity;

    Toast toast;

    DisplayMetrics displayMetrics;
    float displayDensity;
    int screenWidth;
    int screenHeight;

    private Thread myThread = null;

    private String numberOfMessages = "";
    private String newLearningModules = "";
    private String newEatingPlans = "";
    private String planName = "";
    private String templateName = "";
    private String planCalories = "";
    private String selectedPlannerItemID = "";
    private String selectedFoodCategory = "";
    private String exchangeItemsString = "";
    private String caloriesOrOtherString = "calories";

    private int plannerItemCount = 0;
    private boolean hasSavedMyPlans = false;

    private List<String> plannerItemID = new ArrayList<>();
    private List<String> plannerItemHour = new ArrayList<>();
    private List<String> plannerItemEat = new ArrayList<>();
    private List<String> plannerItemMove = new ArrayList<>();
    private List<String> plannerItemBalance = new ArrayList<>();
    private List<String> plannerItemReminder = new ArrayList<>();
    private List<String> plannerItemCalories = new ArrayList<>();
    private List<String> plannerItemMealID = new ArrayList<>();
    private List<String> plannerItemExchangeItems = new ArrayList<>();
    private List<String> plannerItemPlaceholder = new ArrayList<>();
    private List<String> plannerItemNotes = new ArrayList<>();
    private List<String> plannerItemSubNotes = new ArrayList<>();
    private List<String> plannerItemImage = new ArrayList<>();

    LayoutInflater mInflater;
    private static InputMethodManager imm;

    SharedPreferences mSharedPreferences;

	private View appletCreatePlan;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        fragmentActivity = super.getActivity();
        mSharedPreferences = fragmentActivity.getSharedPreferences("SPHPrefs", Activity.MODE_PRIVATE);
        imm = (InputMethodManager) fragmentActivity.getSystemService(Context.INPUT_METHOD_SERVICE);

        mInflater = inflater;

        planMainContainer = (RelativeLayout) inflater.inflate(R.layout.activity_plan, container, false);
        buttonsLayout = (RelativeLayout) planMainContainer.findViewById(R.id.buttonsLayout);
        scrollViewPlan = (ScrollView) planMainContainer.findViewById(R.id.scrollViewMyPlans);
        linearLayoutMyPlans = (LinearLayout) planMainContainer.findViewById(R.id.linearLayoutPlan);

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", Locale.US);
        calendar = Calendar.getInstance();
        calendar.setTime(passDate);

        mActionBar = HTActionBar.getActionBar(fragmentActivity, dateFormat.format(calendar.getTime()), "Save As", "My Plans");
        planMainContainer.addView(mActionBar);

        displayMetrics = fragmentActivity.getResources().getDisplayMetrics();
        displayDensity = displayMetrics.density;

        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;

        int dpValue = 44;
        int topBarHeight = (int) (dpValue * displayDensity);

        params = (RelativeLayout.LayoutParams) mActionBar.getLayoutParams();
        params.height = topBarHeight;

        mActionBar.setLayoutParams(params);

        mActionBar.findViewById(R.id.leftButton).setVisibility(View.GONE);
        mActionBar.findViewById(R.id.rightButton).setVisibility(View.GONE);

        // Save As button
        mActionBar.findViewById(R.id.leftButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LayoutInflater li = LayoutInflater.from(fragmentActivity);
                View promptsView = li.inflate(R.layout.update_calories_view, null);
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

        planMainContainer.addView(datePicker);

        params = (RelativeLayout.LayoutParams) datePicker.getLayoutParams();
        params.height = topBarHeight;
        params.setMargins(0, topBarHeight, 0, 0);  // left, top, right, bottom

        datePicker.setLayoutParams(params);

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

        if(!HTGlobals.getInstance().hidePlanner) {
            tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.planText);
            tabBarLabel.setTextColor(Color.WHITE);
        }

        tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.learnText);
        tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));

        tabBarLabel = (TextView) fragmentActivity.findViewById(R.id.moreText);
        tabBarLabel.setTextColor(Color.parseColor("#99FFFFFF"));

        //BUTTONS

        // Add Fodd
        View appletAddFood = HTAppletButton.getAppletButton(fragmentActivity, "ht_planner_add_food", "Add Food");

        buttonsLayout.addView(appletAddFood);

        params = (RelativeLayout.LayoutParams) appletAddFood.getLayoutParams();
        params.height = (int) (64 * displayDensity);
        params.width = ((screenWidth - (int) (32 * displayDensity)) / 2);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.setMargins((int) (11 * displayDensity), (int) (10 * displayDensity), 0, 0);

        appletAddFood.setLayoutParams(params);

        appletAddFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(fragmentActivity, HTAddFoodActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // Add Activity
        View appletAddActivity = HTAppletButton.getAppletButton(fragmentActivity, "ht_planner_add_activity", "Add Activity");

        buttonsLayout.addView(appletAddActivity);

        params = (RelativeLayout.LayoutParams) appletAddActivity.getLayoutParams();
        params.height = (int) (64 * displayDensity);
        params.width = ((screenWidth - (int) (32 * displayDensity)) / 2);
        params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.setMargins(0, (int) (10 * displayDensity), (int) (11 * displayDensity), 0);

        appletAddActivity.setLayoutParams(params);

        appletAddActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(fragmentActivity, HTAddActivityActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // Create Favorites
        View appletCreateFavorites = HTAppletButton.getAppletButton(fragmentActivity, "ht_planner_create_favorites", "Create Favorites");

        buttonsLayout.addView(appletCreateFavorites);

        params = (RelativeLayout.LayoutParams) appletCreateFavorites.getLayoutParams();
        params.height = (int) (64 * displayDensity);
        params.width = ((screenWidth - (int) (32 * displayDensity)) / 2);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        params.setMargins((int) (11 * displayDensity), (int) (10 * displayDensity), 0, (int) (10 * displayDensity));

        appletCreateFavorites.setLayoutParams(params);

        appletCreateFavorites.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(fragmentActivity, HTCreateFavoritesActivity.class);
                startActivityForResult(intent, 1);
            }
        });

        // Create Plan
        appletCreatePlan = HTAppletButton.getAppletButton(fragmentActivity, "ht_planner_create_plan", "Create Plan");

        buttonsLayout.addView(appletCreatePlan);

        params = (RelativeLayout.LayoutParams) appletCreatePlan.getLayoutParams();
        params.height = (int) (64 * displayDensity);
        params.width = ((screenWidth - (int) (32 * displayDensity)) / 2);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        params.setMargins(0, (int) (10 * displayDensity), (int) (11 * displayDensity), (int) (10 * displayDensity));

        appletCreatePlan.setLayoutParams(params);

		/*
        appletCreatePlan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(fragmentActivity, HTCreateEatingPlanActivity.class);
                startActivityForResult(intent, 1);
            }
        });
		*/

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(fragmentActivity, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            fragmentActivity.finish();

        } else {
            getPlanner();
        }

        return planMainContainer;
    }

    private void getPlanner() {

        if (linearLayoutMyPlans.getChildCount() > 0)
            linearLayoutMyPlans.removeAllViews();

        numberOfMessages = "";
        newLearningModules = "";
        newEatingPlans = "";

        planName = "";
        templateName = "";
        planCalories = "";

        plannerItemID.clear();
        plannerItemHour.clear();
        plannerItemEat.clear();
        plannerItemMove.clear();
        plannerItemBalance.clear();
        plannerItemReminder.clear();
        plannerItemCalories.clear();
        plannerItemMealID.clear();
        plannerItemExchangeItems.clear();
        plannerItemPlaceholder.clear();
        plannerItemNotes.clear();
        plannerItemSubNotes.clear();
        plannerItemImage.clear();

        caloriesOrOtherString = "calories";
		HTGlobals.getInstance().hideCalorieCalculator = false;

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(fragmentActivity);
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
                    nameValuePairs.add(new BasicNameValuePair("action", "get_planner"));
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
                                    planCalories = XMLFunctions.getValue(e2, "plan_calories");
                                }

                                int i = 1;
                                while (XMLFunctions.tagExists(e2, "planner_item_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "id_" + i);
                                    plannerItemID.add(tempString);
                                    tempString = XMLFunctions.getValue(e2, "plan_hour_" + i);
                                    plannerItemHour.add(tempString);
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

								// hideCalorieCalculator
                                if (XMLFunctions.tagExists(e2, "hide_calorie_calculator")) {
                                    tempString = XMLFunctions.getValue(e2, "hide_calorie_calculator");
                                    if ("1".equals(tempString)) {
                                        HTGlobals.getInstance().hideCalorieCalculator = true;
                                    } else {
                                        HTGlobals.getInstance().hideCalorieCalculator = false;
                                    }
                                }
								
                                showPlanner();
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
//        if (linearLayoutMyPlans.getChildCount() > 0)
//            linearLayoutMyPlans.removeAllViews();

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

        appletCreatePlan.setOnClickListener(null);

        appletCreatePlan.setOnClickListener(new View.OnClickListener() {
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

        String currentPlannerHour = "";
        String currentPlannerItemType = "";
        String currentPlannerSubNotes = "";

        Typeface planNameFont = Typeface.createFromAsset(fragmentActivity.getAssets(), "fonts/AvenirNext-Regular.ttf");
        Typeface plannerHourFont = Typeface.createFromAsset(fragmentActivity.getAssets(), "fonts/Avenir-Medium.ttf");
        Typeface plannerNotesFont = Typeface.createFromAsset(fragmentActivity.getAssets(), "fonts/AvenirNext-Regular.ttf");
        Typeface plannerSubNotesFont = Typeface.createFromAsset(fragmentActivity.getAssets(), "fonts/AvenirNext-DemiBold.ttf");

        RelativeLayout relativeLayout = new RelativeLayout(fragmentActivity);
        LinearLayout.LayoutParams linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeLayout.setLayoutParams(linearParams);
        relativeLayout.setPadding((int) (10 * displayDensity), (int) (8 * displayDensity), (int) (10 * displayDensity), (int) (4 * displayDensity));
        //Plan Name Label
        TextView planNameLabel = new TextView(fragmentActivity);
        planNameLabel.setTypeface(planNameFont);
        planNameLabel.setTextColor(fragmentActivity.getResources().getColor(R.color.ht_gray_title_text));
        planNameLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        planNameLabel.setLayoutParams(relativeParams);
        planNameLabel.setGravity(Gravity.CENTER);

        if ("".equals(planName)) {
            planNameLabel.setText(templateName);
        } else {
            planNameLabel.setText(planName);
        }
        relativeLayout.addView(planNameLabel);

        // planner nutrition totals
        ImageView plannerImageButton = new ImageView(fragmentActivity);
        relativeParams = new RelativeLayout.LayoutParams((int) (28 * displayDensity), (int) (28 * displayDensity));
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        plannerImageButton.setLayoutParams(relativeParams);
        plannerImageButton.setImageResource(R.drawable.ht_planner_nutrition);
        plannerImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPlannerNutrition();
            }
        });
        relativeLayout.addView(plannerImageButton);

        linearLayoutMyPlans.addView(relativeLayout);

        boolean showReminderIcon;
        // planner items!
        for (int i = 0; i < plannerItemID.size(); i++) {
            showReminderIcon = false;
            // new time slot?
            if (!currentPlannerHour.equals(plannerItemHour.get(i))) {
                currentPlannerHour = plannerItemHour.get(i);

                View timeslotView = mInflater.inflate(R.layout.timeslot_view, null);
                TextView timeView = (TextView) timeslotView.findViewById(R.id.timeView);
                TextView amView = (TextView) timeslotView.findViewById(R.id.amView);
                timeView.setTypeface(plannerHourFont);
                amView.setTypeface(plannerHourFont);
                timeView.setText(currentPlannerHour.substring(0, currentPlannerHour.length() - 2));
                timeView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                amView.setText(currentPlannerHour.substring(currentPlannerHour.length() - 2));
                amView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 9);

                linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                linearParams.setMargins(0, (int) (-10 * displayDensity), 0, (int) (-4 * displayDensity));
                timeslotView.setLayoutParams(linearParams);

                linearLayoutMyPlans.addView(timeslotView);
            }

            RelativeLayout plannerItemView = (RelativeLayout) mInflater.inflate(R.layout.planner_item_view, null);
            plannerItemView.setTag(i);
            linearParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (59 * displayDensity));
            linearParams.setMargins(0, (int) (4 * displayDensity), 0, (int) (2 * displayDensity));
            plannerItemView.setLayoutParams(linearParams);

            ImageView reminderButton = (ImageView) plannerItemView.findViewById(R.id.reminderButton);
            RelativeLayout plannerBlock = (RelativeLayout) plannerItemView.findViewById(R.id.plannerBlock);
            ImageView plannerItemIconView = (ImageView) plannerItemView.findViewById(R.id.plannerItemIconView);
            TextView notesLabel = (TextView) plannerItemView.findViewById(R.id.notesLabel);
            TextView subnotesLabel = (TextView) plannerItemView.findViewById(R.id.subnotesLabel);
            notesLabel.setTypeface(plannerNotesFont);
            subnotesLabel.setTypeface(plannerSubNotesFont);
            View plannerBlockBottomBorder = (View) plannerItemView.findViewById(R.id.viewBorder);

            // reminder?

            if (!"".equals(plannerItemReminder.get(i))) {

                showReminderIcon = true; // showing this later, to attach to the same selector as the planner item itself
            }

            // currentPlannerItemType
            if (!"".equals(plannerItemEat.get(i))) { // eat item

                if ("TEMP_LABEL".equals(plannerItemEat.get(i))) {

                    currentPlannerItemType = "NOTE";

                } else if ("1".equals(plannerItemPlaceholder.get(i)) &&
                        ("".equals(plannerItemCalories.get(i)) || "0".equals(plannerItemCalories.get(i))) &&
                        ("".equals(plannerItemMealID.get(i)) || "0".equals(plannerItemMealID.get(i)))) {
                    // template w/ no selection

                    currentPlannerItemType = "TEMPLATE";

                } else {

                    currentPlannerItemType = "EAT";
                }
            } else if (!"".equals(plannerItemMove.get(i))) { // move item

                currentPlannerItemType = "MOVE";

            } else if (!"".equals(plannerItemBalance.get(i))) { // balance item

                currentPlannerItemType = "BALANCE";

            } else { // note

                currentPlannerItemType = "NOTE";
            }

            if (showReminderIcon) {
                reminderButton.setImageResource(R.drawable.ht_reminder_on);
                reminderButton.setVisibility(View.VISIBLE);
            }

            if ("TEMPLATE".equals(currentPlannerItemType)) { // empty template item
                plannerItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = (int) v.getTag();
                        selectedPlannerItemID = plannerItemID.get(index);
                        Intent intent = new Intent(fragmentActivity, HTAddFoodSearchResultsActivity.class);
                        intent.putExtra("relaunchItemID", Integer.parseInt(selectedPlannerItemID));
                        intent.putExtra("addFoodCategory", "template");
                        startActivityForResult(intent, 1);
                    }
                });

            } else if (!"".equals(plannerItemEat.get(i)) && !"TEMP_LABEL".equals(plannerItemEat.get(i))) { // eat item
                plannerItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = (int) v.getTag();
                        // template item
                        if (plannerItemPlaceholder.get(index).equals("1")) {
                            selectedFoodCategory = "template";
                        }

                        if (!"".equals(plannerItemExchangeItems.get(index))) {
                            selectedFoodCategory = "exchange";
                            exchangeItemsString = plannerItemExchangeItems.get(index);
                        }
                        Intent intent = new Intent(fragmentActivity, HTAddFoodSelectItemActivity.class);
                        if ("template".equals(selectedFoodCategory)) {
                            intent.putExtra("addFoodCategory", "template");
                            intent.putExtra("selectedFoodID", Integer.parseInt(plannerItemMealID.get(index)));
                            intent.putExtra("relaunchItemID", Integer.parseInt(plannerItemID.get(index)));
                        } else if ("exchange".equals(selectedFoodCategory)) {
                            intent.putExtra("addFoodCategory", "exchange");
                            intent.putExtra("exchangeItemsString", exchangeItemsString.replace("***", "||"));
                            intent.putExtra("selectedFoodID", Integer.parseInt(plannerItemID.get(index)));
                        } else {
                            intent.putExtra("selectedFoodID", Integer.parseInt(plannerItemID.get(index)));
                        }
                        intent.putExtra("relaunchPlannerItem", true);
                        if ("template".equals(selectedFoodCategory)
                                || "exchange".equals(selectedFoodCategory))
                            intent.putExtra("inTemplateString", "true");
                        startActivityForResult(intent, 1);
                    }
                });

            } else { // activity item - exercise, balance, note
                plannerItemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int index = (int) v.getTag();
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
            }

            if ("EAT".equals(currentPlannerItemType)) {
                plannerItemIconView.setImageResource(R.drawable.ht_planner_meal);
                plannerBlockBottomBorder.setBackgroundColor(Color.rgb(187, 227, 69));
            } else if ("TEMPLATE".equals(currentPlannerItemType)) {
                plannerItemIconView.setImageResource(R.drawable.ht_planner_meal_empty);
                plannerBlockBottomBorder.setBackgroundColor(Color.rgb(136, 136, 136));
            } else if ("MOVE".equals(currentPlannerItemType)) {
                plannerItemIconView.setImageResource(R.drawable.ht_planner_activity);
                plannerBlockBottomBorder.setBackgroundColor(Color.rgb(104, 193, 193));
            } else if ("BALANCE".equals(currentPlannerItemType)) {
                plannerItemIconView.setImageResource(R.drawable.ht_planner_balance);
                plannerBlockBottomBorder.setBackgroundColor(Color.rgb(163, 119, 201));
            } else {//NOTE
                plannerItemIconView.setImageResource(R.drawable.ht_planner_note);
                plannerBlockBottomBorder.setBackgroundColor(Color.rgb(238, 107, 138));
            }

            //sub notes
            currentPlannerSubNotes = plannerItemSubNotes.get(i);
            if ((!"".equals(currentPlannerSubNotes) ||
                    ("EAT".equals(currentPlannerItemType) && !"".equals(plannerItemCalories.get(i))))
                    && !currentPlannerSubNotes.equals(plannerItemNotes.get(i))) {
                subnotesLabel.setVisibility(View.VISIBLE);
                if (!"".equals(currentPlannerSubNotes)) { // there are sub notes, add calories to the notes
                    notesLabel.setText(plannerItemNotes.get(i) + " - " + plannerItemCalories.get(i) + " " + caloriesOrOtherString);
                    subnotesLabel.setText(currentPlannerSubNotes);
                } else {
                    notesLabel.setText(plannerItemNotes.get(i));
                    subnotesLabel.setText(plannerItemCalories.get(i) + " " + caloriesOrOtherString);
                }
            } else { // no sub_notes
                notesLabel.setText(plannerItemNotes.get(i));
            }

            plannerItemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    int index = (int) v.getTag();
                    selectedPlannerItemID = plannerItemID.get(index);
                    AlertDialog.Builder builder = null;
                    builder = new AlertDialog.Builder(fragmentActivity);
                    AlertDialog alert;
                    builder.setTitle("Delete Item?")
                            .setMessage("Are you sure you want to delete this item from your Plan?")
                            .setCancelable(false)
                            .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    deleteFoodItem();
                                }
                            })
                            .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    selectedPlannerItemID = "";
                                }
                            });
                    alert = builder.create();
                    alert.show();
                    return true;
                }
            });

            linearLayoutMyPlans.addView(plannerItemView);
        }


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

        getPlanner();
    }

}
