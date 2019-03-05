package com.sph.healthtrac.planner;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HTPlannerShareActivity extends Activity {

    private static RelativeLayout mainContentLayout;
    private ScrollView scrollViewMyPlans;
    private LinearLayout layoutMyPlans;
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
    float displayDensity;

    private String fromDateStr = "";
    private String toDateStr = "";
    private Date fromDate, toDate;
    private Date firstDate, lastDate;
    private int numberOfDays;

    private String sharePlanSubject;
    private String showIngredientListTitle;

    private boolean sharePlanChecked;
    private boolean shareShoppingListChecked;

    private String sharePlanContent;
    private String sharePlanContentHTML;

    Typeface avenirNextRegularFont;
    Typeface avenirNextMediumFont;
    Typeface openSansLightFont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner_share);

        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        scrollViewMyPlans = (ScrollView) findViewById(R.id.scrollViewMyPlans);
        layoutMyPlans = (LinearLayout) findViewById(R.id.layoutMyPlans);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        avenirNextRegularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");
        openSansLightFont = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");

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
        View mActionBar = HTActionBar.getActionBar(this, "Share Plan", "leftArrow", "");

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
            getMyPlanShareDates();
        }
    }

    private void getMyPlanShareDates() {

        sharePlanSubject = "";
        showIngredientListTitle = "";

        fromDateStr = "";
        toDateStr = "";
        numberOfDays = -1;

        sharePlanContent = "";
        sharePlanContentHTML = "";

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_planner_share_dates"));
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

                                toast = HTToast.showToast(HTPlannerShareActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {

                        final Element e2 = (Element) nodes.item(0);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                String tempString;

                                if (XMLFunctions.tagExists(e2, "first_plan_date")) {
                                    fromDateStr = XMLFunctions.getValue(e2, "first_plan_date");
                                }

                                if (XMLFunctions.tagExists(e2, "last_plan_date")) {
                                    toDateStr = XMLFunctions.getValue(e2, "last_plan_date");
                                }

                                if (XMLFunctions.tagExists(e2, "number_of_days")) {
                                    numberOfDays = Integer.parseInt(XMLFunctions.getValue(e2, "number_of_days"));
                                }

                                if (XMLFunctions.tagExists(e2, "ingredient_list_title")) {
                                    showIngredientListTitle = HTGlobals.getInstance().cleanStringAfterReceiving(XMLFunctions.getValue(e2, "ingredient_list_title"));
                                }

                                showMyPlanShare();

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

//                                    toast = HTToast.showToast(HTPlannerMyPlansActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTPlannerShareActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void sharePlan() {
        HTGlobals.getInstance().plannerShouldRefresh = false;
        sharePlanContent = "";
        sharePlanContentHTML = "";

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                int shouldSharePlan = 0;
                int shouldShareShopingList = 0;

                if (sharePlanChecked) {

                    shouldSharePlan = 1;
                }

                if (shareShoppingListChecked) {

                    shouldShareShopingList = 1;
                }

                try {

                    SimpleDateFormat dateFormat = new SimpleDateFormat("M/d/yy", Locale.US);

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_planner_share_plan"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("start_date", dateFormat.format(fromDate)));
                    nameValuePairs.add(new BasicNameValuePair("end_date", dateFormat.format(toDate)));
                    nameValuePairs.add(new BasicNameValuePair("share_plan", shouldSharePlan + ""));
                    nameValuePairs.add(new BasicNameValuePair("share_shopping_list", shouldShareShopingList + ""));

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

                                toast = HTToast.showToast(HTPlannerShareActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                String tempString;

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

                                if (sharePlanContent.length() > 0) {
                                    shareContent();
                                } else {
                                    HTToast.showToast(HTPlannerShareActivity.this, "There is no content to share for the options you’ve selected", Toast.LENGTH_LONG);
//                                   finish();
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

//                                    toast = HTToast.showToast(HTPlannerMyPlansActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTPlannerShareActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void shareContent() {
        Intent shareIntent = new Intent(
                android.content.Intent.ACTION_SEND);
        shareIntent.setType("text/*");
        shareIntent.putExtra(
                android.content.Intent.EXTRA_SUBJECT, sharePlanSubject);
        shareIntent.putExtra(Intent.EXTRA_TEXT, sharePlanContent);
        shareIntent.putExtra(Intent.EXTRA_HTML_TEXT, sharePlanContentHTML);
//        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        startActivityForResult(Intent.createChooser(shareIntent, "Share Plan"), 100);
    }

    private void showMyPlanShare() {
        scrollViewMyPlans.setVisibility(View.VISIBLE);

        if (layoutMyPlans.getChildCount() > 0)
            layoutMyPlans.removeAllViews();

        TextView createEatingPlanLabel = new TextView(this);
        createEatingPlanLabel.setText("SHARE PLAN FOR THESE DATES");
        createEatingPlanLabel.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
        createEatingPlanLabel.setTypeface(avenirNextMediumFont);
        createEatingPlanLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) (16 * displayDensity), (int) (12 * displayDensity), (int) (16 * displayDensity), (int) (4 * displayDensity));
        createEatingPlanLabel.setLayoutParams(params);
        layoutMyPlans.addView(createEatingPlanLabel);

        //from date
        RelativeLayout fromdateLayout = new RelativeLayout(this);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (45 * displayDensity));
        params.setMargins((int) (6 * displayDensity), (int) (8 * displayDensity), (int) (6 * displayDensity), (int) (0 * displayDensity));
        fromdateLayout.setLayoutParams(params);
        fromdateLayout.setBackgroundColor(Color.WHITE);
        fromdateLayout.setPadding((int) (14 * displayDensity), 0, (int) (6 * displayDensity), 0);

        TextView fromLabel = new TextView(this);
        fromLabel.setText("From");
        fromLabel.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
        fromLabel.setTypeface(avenirNextRegularFont);
        fromLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        fromLabel.setLayoutParams(relativeParams);
        fromdateLayout.addView(fromLabel);

        final TextView fromDateEdit = new TextView(this);
        fromDateEdit.setBackgroundResource(R.drawable.ht_text_field);
        fromDateEdit.setTypeface(openSansLightFont);
        fromDateEdit.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
        fromDateEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        fromDateEdit.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        relativeParams = new RelativeLayout.LayoutParams((screenWidth - (int) (32 * displayDensity)) / 2, (int) (31 * displayDensity));
        relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        fromDateEdit.setLayoutParams(relativeParams);
        fromdateLayout.addView(fromDateEdit);

        layoutMyPlans.addView(fromdateLayout);

        if ("".equals(fromDateStr)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            fromDateStr = dateFormat.format(calendar.getTime());
            firstDate = calendar.getTime();
            fromDate = calendar.getTime();
        }else{
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            calendar = Calendar.getInstance();

            try {
                Date tempDate = dateFormat.parse(fromDateStr);
                calendar.setTime(tempDate);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            } catch (ParseException e) {
                calendar.setTime(currentDate);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            }
            fromDateStr = dateFormat.format(calendar.getTime());
            firstDate = calendar.getTime();
            fromDate = calendar.getTime();
        }
        fromDateEdit.setText(fromDateStr);
        fromDateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDlg(fromDateEdit, firstDate, HTGlobals.getInstance().addNumberOfDaysToPassDate(firstDate, numberOfDays, false), true);
            }
        });

        //to date
        RelativeLayout todateLayout = new RelativeLayout(this);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (45 * displayDensity));
        params.setMargins((int) (6 * displayDensity), (int) (5 * displayDensity), (int) (6 * displayDensity), (int) (0 * displayDensity));
        todateLayout.setLayoutParams(params);
        todateLayout.setBackgroundColor(Color.WHITE);
        todateLayout.setPadding((int) (14 * displayDensity), 0, (int) (6 * displayDensity), 0);

        TextView toLabel = new TextView(this);
        toLabel.setText("To");
        toLabel.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
        toLabel.setTypeface(avenirNextRegularFont);
        toLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        toLabel.setLayoutParams(relativeParams);
        todateLayout.addView(toLabel);

        final TextView toDateEdit = new TextView(this);
        toDateEdit.setBackgroundResource(R.drawable.ht_text_field);
        toDateEdit.setTypeface(openSansLightFont);
        toDateEdit.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
        toDateEdit.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        toDateEdit.setGravity(Gravity.CENTER_VERTICAL | Gravity.RIGHT);

        relativeParams = new RelativeLayout.LayoutParams((screenWidth - (int) (32 * displayDensity)) / 2, (int) (31 * displayDensity));
        relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        toDateEdit.setLayoutParams(relativeParams);
        todateLayout.addView(toDateEdit);

        layoutMyPlans.addView(todateLayout);

        if ("".equals(toDateStr)) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            calendar = Calendar.getInstance();
            calendar.setTime(currentDate);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            toDateStr = dateFormat.format(calendar.getTime());
            toDate = calendar.getTime();
        }else{
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            calendar = Calendar.getInstance();

            try {
                Date tempDate = dateFormat.parse(toDateStr);
                calendar.setTime(tempDate);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            } catch (ParseException e) {
                calendar.setTime(currentDate);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
            }
            toDateStr = dateFormat.format(calendar.getTime());
            toDate = calendar.getTime();
        }
        toDateEdit.setText(toDateStr);
        toDateEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDlg(toDateEdit, firstDate, HTGlobals.getInstance().addNumberOfDaysToPassDate(firstDate, numberOfDays, false), false);
            }
        });


        TextView whatToShareLabel = new TextView(this);
        whatToShareLabel.setText("WHAT TO SHARE");
        whatToShareLabel.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
        whatToShareLabel.setTypeface(avenirNextMediumFont);
        whatToShareLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins((int) (16 * displayDensity), (int) (12 * displayDensity), (int) (16 * displayDensity), (int) (12 * displayDensity));
        whatToShareLabel.setLayoutParams(params);
        layoutMyPlans.addView(whatToShareLabel);

        RelativeLayout sharePlanLayout = new RelativeLayout(this);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (45 * displayDensity));
        params.setMargins((int) (6 * displayDensity), (int) (0 * displayDensity), (int) (6 * displayDensity), (int) (0 * displayDensity));
        sharePlanLayout.setLayoutParams(params);
        sharePlanLayout.setBackgroundColor(Color.WHITE);
        sharePlanLayout.setPadding((int) (14 * displayDensity), 0, (int) (6 * displayDensity), 0);

        TextView sharePlanLabel = new TextView(this);
        sharePlanLabel.setText("Plan Items");
        sharePlanLabel.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
        sharePlanLabel.setTypeface(avenirNextRegularFont);
        sharePlanLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

        relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        sharePlanLabel.setLayoutParams(relativeParams);
        sharePlanLayout.addView(sharePlanLabel);

        ImageView sharePlanCheckView = new ImageView(this);
        sharePlanCheckView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sharePlanChecked = !sharePlanChecked;
                if (sharePlanChecked)
                    ((ImageView)v).setImageResource(R.drawable.ht_check_on_green);
                else
                    ((ImageView)v).setImageResource(R.drawable.ht_check_off_green);
            }
        });

        if (sharePlanChecked)
            sharePlanCheckView.setImageResource(R.drawable.ht_check_on_green);
        else
            sharePlanCheckView.setImageResource(R.drawable.ht_check_off_green);

        relativeParams = new RelativeLayout.LayoutParams((int) (30 * displayDensity), (int) (30 * displayDensity));
        relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        sharePlanCheckView.setLayoutParams(relativeParams);
        sharePlanLayout.addView(sharePlanCheckView);

        layoutMyPlans.addView(sharePlanLayout);

        // showIngredientList?
        if (showIngredientListTitle.length() > 0) {
            RelativeLayout shareShoppingListLayout = new RelativeLayout(this);
            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (45 * displayDensity));
            params.setMargins((int) (6 * displayDensity), (int) (5 * displayDensity), (int) (6 * displayDensity), (int) (0 * displayDensity));
            shareShoppingListLayout.setLayoutParams(params);
            shareShoppingListLayout.setBackgroundColor(Color.WHITE);
            shareShoppingListLayout.setPadding((int) (14 * displayDensity), 0, (int) (6 * displayDensity), 0);

            TextView shareShoppingListLabel = new TextView(this);
            shareShoppingListLabel.setText(showIngredientListTitle);
            shareShoppingListLabel.setTextColor(getResources().getColor(R.color.ht_gray_title_text));
            shareShoppingListLabel.setTypeface(avenirNextRegularFont);
            shareShoppingListLabel.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);

            relativeParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
            shareShoppingListLabel.setLayoutParams(relativeParams);
            shareShoppingListLayout.addView(shareShoppingListLabel);

            ImageView shareShoppingListCheckView = new ImageView(this);
            shareShoppingListCheckView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    shareShoppingListChecked = !shareShoppingListChecked;
                    if (shareShoppingListChecked)
                        ((ImageView)v).setImageResource(R.drawable.ht_check_on_green);
                    else
                        ((ImageView)v).setImageResource(R.drawable.ht_check_off_green);
                }
            });

            if (shareShoppingListChecked)
                shareShoppingListCheckView.setImageResource(R.drawable.ht_check_on_green);
            else
                shareShoppingListCheckView.setImageResource(R.drawable.ht_check_off_green);

            relativeParams = new RelativeLayout.LayoutParams((int) (30 * displayDensity), (int) (30 * displayDensity));
            relativeParams.addRule(RelativeLayout.CENTER_VERTICAL);
            relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
            shareShoppingListCheckView.setLayoutParams(relativeParams);
            shareShoppingListLayout.addView(shareShoppingListCheckView);

            layoutMyPlans.addView(shareShoppingListLayout);
        }

        LinearLayout buttonLayout = new LinearLayout(this);
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.topMargin = (int) (20 * displayDensity);
        buttonLayout.setLayoutParams(params);
        buttonLayout.setGravity(Gravity.BOTTOM);
        TextView loadPlanButton = new TextView(this);
        loadPlanButton.setGravity(Gravity.CENTER);
        loadPlanButton.setText("Share Plan");
        loadPlanButton.setTypeface(avenirNextMediumFont);
        loadPlanButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
        loadPlanButton.setTextColor(Color.WHITE);
        loadPlanButton.setBackgroundColor(Color.rgb(113, 202, 94));
        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) (60 * displayDensity));
        loadPlanButton.setLayoutParams(params);
        loadPlanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!sharePlanChecked && !shareShoppingListChecked) {
                    HTToast.showToast(HTPlannerShareActivity.this, "Please check the boxes for what you would like to share", Toast.LENGTH_LONG);
                } else if (fromDate.after(toDate)) {
                    HTToast.showToast(HTPlannerShareActivity.this, "From date cannot be after To date", Toast.LENGTH_LONG);
                } else {
                    sharePlan();
                }
            }
        });

        buttonLayout.addView(loadPlanButton);
        layoutMyPlans.addView(buttonLayout);
    }

    private void showDatePickerDlg(final TextView timeView, final Date minDate, final Date maxDate, final boolean isFromDate) {
        Date selectedDate = null;
        if (isFromDate)
            selectedDate = fromDate;
        else
            selectedDate = toDate;
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        if (selectedDate != null)
            calendar.setTime(selectedDate);
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
                calendar.set(year, monthOfYear, dayOfMonth);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                if(calendar.getTime().before(minDate) || calendar.getTime().after(maxDate)){
                    if (isFromDate) {
                        calendar.setTime(fromDate);
                        timeView.setText(dateFormat.format(calendar.getTime()));
                    }else{
                        calendar.setTime(toDate);
                        timeView.setText(dateFormat.format(calendar.getTime()));
                    }
                }else {
                    timeView.setText(dateFormat.format(calendar.getTime()));
                    if (isFromDate)
                        fromDate = calendar.getTime();
                    else
                        toDate = calendar.getTime();
                }
            }
        }, mYear, mMonth, mDay);

        datePickerDialog.getDatePicker().setMinDate(minDate.getTime());
        datePickerDialog.getDatePicker().setMaxDate(maxDate.getTime());
        datePickerDialog.show();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 100 && resultCode == RESULT_OK) {
            setResult(2);
            finish();
        }
    }
}
