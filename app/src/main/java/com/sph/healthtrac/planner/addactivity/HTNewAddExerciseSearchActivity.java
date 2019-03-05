package com.sph.healthtrac.planner.addactivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;
import com.sph.healthtrac.LoginActivity;
import com.sph.healthtrac.R;
import com.sph.healthtrac.common.HTActionBar;
import com.sph.healthtrac.common.HTConnectErrDialog;
import com.sph.healthtrac.common.HTGlobals;
import com.sph.healthtrac.common.HTProgressDialog;
import com.sph.healthtrac.common.HTToast;
import com.sph.healthtrac.common.XMLFunctions;
import com.sph.healthtrac.planner.OnSwipeTouchListener;

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

public class HTNewAddExerciseSearchActivity extends Activity {

    private RelativeLayout mainContentLayout;
    private View mActionBar;

    private EditText searchEdit;

    private LinearLayout databaseSelectionLayout;
    private RelativeLayout recentBtn;
    private RelativeLayout allexercisesBtn;
    private RelativeLayout favoritesBtn;
    private TextView recentLabel;
    private TextView allexercisesLabel;
    private TextView favoritesLabel;
    private View recentIndicator;
    private View allexercisesIndicator;
    private View favoritesIndicator;

    private RelativeLayout searchResultLayout;
    private TextView searchResultLabel;

    private ScrollView searchResultScrollView;
    private LinearLayout searchResultContentLayout;

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

    private List<String> addActivityID = new ArrayList<>();
    private List<String> addActivityName = new ArrayList<>();
    private List<String> addActivityType = new ArrayList<>();

    String addActivityCategory;
    String addActivitySearchFieldString;
    String addActivitySearchString;

    private String exerciseDatabaseSelection = "";

    private int numberOfResults;

    int selectedActivityID;
    int relaunchItemID;

    Typeface openSansLightFont;
    Typeface avenirNextRegularFont;
    Typeface avenirNextMediumFont;
    Typeface avenirNextDemiBoldFont;

    private int grayColor = Color.rgb(80, 80, 87);
    private int blueColor = Color.rgb(116, 204, 240);

    float displayDensity;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addexercise_search_new);

        addActivityCategory = getIntent().getStringExtra("addActivityCategory");

        openSansLightFont = Typeface.createFromAsset(getAssets(), "fonts/OpenSans-Light.ttf");
        avenirNextRegularFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        avenirNextMediumFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Medium.ttf");
        avenirNextDemiBoldFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-DemiBold.ttf");

        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        searchEdit = (EditText) findViewById(R.id.searchEdit);
        searchEdit.addTextChangedListener(textWatcher);
        searchEdit.setTypeface(avenirNextMediumFont);
        searchEdit.clearFocus();

        databaseSelectionLayout = (LinearLayout) findViewById(R.id.databaseSelectionLayout);
        databaseSelectionLayout.setVisibility(View.GONE);

        recentBtn = (RelativeLayout) findViewById(R.id.recentButton);
        allexercisesBtn = (RelativeLayout) findViewById(R.id.allexercisesButton);
        favoritesBtn = (RelativeLayout) findViewById(R.id.favoritesButton);

        recentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"recent".equals(exerciseDatabaseSelection)) {
                    stopThread();
                    searchEdit.clearFocus();
                    imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                    recentLabel.setTextColor(blueColor);
                    allexercisesLabel.setTextColor(grayColor);
                    favoritesLabel.setTextColor(grayColor);
                    recentIndicator.setVisibility(View.VISIBLE);
                    allexercisesIndicator.setVisibility(View.GONE);
                    favoritesIndicator.setVisibility(View.GONE);

                    exerciseDatabaseSelection = "recent";
                    updateSearchEditHint();
                    getSearchResults();
                }
            }
        });

        allexercisesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!"all".equals(exerciseDatabaseSelection)) {
                    stopThread();
                    searchEdit.clearFocus();
                    imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                    recentLabel.setTextColor(grayColor);
                    allexercisesLabel.setTextColor(blueColor);
                    favoritesLabel.setTextColor(grayColor);
                    recentIndicator.setVisibility(View.GONE);
                    allexercisesIndicator.setVisibility(View.VISIBLE);
                    favoritesIndicator.setVisibility(View.GONE);

                    exerciseDatabaseSelection = "all";
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
                if (!"favorites".equals(exerciseDatabaseSelection)) {
                    stopThread();
                    searchEdit.clearFocus();
                    imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                    recentLabel.setTextColor(grayColor);
                    allexercisesLabel.setTextColor(grayColor);
                    favoritesLabel.setTextColor(blueColor);
                    recentIndicator.setVisibility(View.GONE);
                    allexercisesIndicator.setVisibility(View.GONE);
                    favoritesIndicator.setVisibility(View.VISIBLE);

                    exerciseDatabaseSelection = "favorites";
                    updateSearchEditHint();
                    getSearchResults();
                }
            }
        });

        recentLabel = (TextView) findViewById(R.id.recentLabelView);
        allexercisesLabel = (TextView) findViewById(R.id.allexercisesLabelView);
        favoritesLabel = (TextView) findViewById(R.id.favoritesLabelView);
        recentLabel.setTypeface(avenirNextMediumFont);
        allexercisesLabel.setTypeface(avenirNextMediumFont);
        favoritesLabel.setTypeface(avenirNextMediumFont);

        recentIndicator = findViewById(R.id.recentIndicator);
        allexercisesIndicator = findViewById(R.id.allexercisesIndicator);
        favoritesIndicator = findViewById(R.id.favoritesIndicator);

        //initial selection(recent)
        recentLabel.setTextColor(blueColor);
        allexercisesLabel.setTextColor(grayColor);
        favoritesLabel.setTextColor(grayColor);
        allexercisesIndicator.setVisibility(View.GONE);
        favoritesIndicator.setVisibility(View.GONE);

        searchResultLayout = (RelativeLayout) findViewById(R.id.searchResultLayout);
        searchResultLayout.setVisibility(View.GONE);
        searchResultLabel = (TextView) findViewById(R.id.searchResultLabel);
        searchResultLabel.setTypeface(avenirNextRegularFont);

        searchResultScrollView = (ScrollView) findViewById(R.id.searchResultScrollView);
        searchResultScrollView.setVisibility(View.GONE);
        searchResultContentLayout = (LinearLayout) findViewById(R.id.searchResultContentLayout);

        addActivityCategory = "exercise";
        exerciseDatabaseSelection = "recent";

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
        mActionBar = HTActionBar.getActionBar(this, "Add Exercise", "leftArrow", "New");

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

        // right new button
        mActionBar.findViewById(R.id.rightButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                Intent intent = new Intent(HTNewAddExerciseSearchActivity.this, HTAddActivitySelectItemActivity.class);
                intent.putExtra("addActivityCategory", "exercise");
                intent.putExtra("selectedActivityType", "exercise");
                if (relaunchItemID > 0) {
                    intent.putExtra("relaunchPlannerItem", true);
                    intent.putExtra("relaunchItemID", relaunchItemID);
                } else {
                    intent.putExtra("relaunchPlannerItem", false);
                }

                startActivityForResult(intent, 1);
            }
        });

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            getSearchResults();
        }
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    private void getSearchResults() {
        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        addActivityID.clear();
        addActivityName.clear();
        addActivityType.clear();

        selectedActivityID = 0;
        numberOfResults = 0;
        addActivitySearchFieldString = searchEdit.getText().toString();

        String favoritesTypeString = "";
        /*if ("favorites".equals(addActivityCategory)) {
            if (favoritesTypeExerciseChecked)
                favoritesTypeString += "M,";

            if (favoritesTypeBalanceChecked)
                favoritesTypeString += "BAL,";

            if (favoritesTypeNoteChecked)
                favoritesTypeString += "NOTE,";

            if (favoritesTypeString.length() > 0)
                favoritesTypeString = favoritesTypeString.substring(0, favoritesTypeString.length() - 1);
        }*/

        String searchKey = "";
        try {
            searchKey = URLEncoder.encode(searchEdit.getText().toString(), "utf-8");
        } catch (UnsupportedEncodingException e) {
        }
        addActivitySearchString = "WhichCategory=" + addActivityCategory + "&search=" + searchKey
                + "&relaunch=" + relaunchItemID + "&type=" + favoritesTypeString + "&exercise_type="
                + exerciseDatabaseSelection;

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_activity_search_results"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));

                    //additional parameter from addActivitySearchString
                    String[] additionalParams = addActivitySearchString.split("&");
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
                    NodeList nodes = doc.getElementsByTagName("add_activity_details");

                    Element e1 = (Element) error.item(0);
                    final String errorMessage = XMLFunctions.getValue(e1, "error_message");

                    if (!errorMessage.equals("")) {

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {

                                toast = HTToast.showToast(HTNewAddExerciseSearchActivity.this, errorMessage, Toast.LENGTH_LONG);
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
                                while (XMLFunctions.tagExists(e2, "activity_id_" + i)) {
                                    tempString = XMLFunctions.getValue(e2, "activity_id_" + i);
                                    addActivityID.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "activity_notes_" + i);
                                    addActivityName.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    tempString = XMLFunctions.getValue(e2, "activity_type_" + i);
                                    addActivityType.add(htGlobals.cleanStringAfterReceiving(tempString));
                                    i++;
                                }

                                numberOfResults = addActivityID.size();
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

//                                    toast = HTToast.showToast(HTNewAddExerciseSearchActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTNewAddExerciseSearchActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_activity_delete_favorite"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("WhichID", selectedActivityID + ""));

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

                                toast = HTToast.showToast(HTNewAddExerciseSearchActivity.this, errorMessage, Toast.LENGTH_LONG);
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

//                                    toast = HTToast.showToast(HTNewAddExerciseSearchActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTNewAddExerciseSearchActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_activity_delete_recent"));
                    nameValuePairs.add(new BasicNameValuePair("userid", login));
                    nameValuePairs.add(new BasicNameValuePair("pw", pw));
                    nameValuePairs.add(new BasicNameValuePair("day", passDay + ""));
                    nameValuePairs.add(new BasicNameValuePair("month", (passMonth + 1) + ""));
                    nameValuePairs.add(new BasicNameValuePair("year", passYear + ""));
                    nameValuePairs.add(new BasicNameValuePair("WhichID", selectedActivityID + ""));

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

                                toast = HTToast.showToast(HTNewAddExerciseSearchActivity.this, errorMessage, Toast.LENGTH_LONG);
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

//                                    toast = HTToast.showToast(HTNewAddExerciseSearchActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTNewAddExerciseSearchActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showSearchResults() {
        if (searchResultContentLayout.getChildCount() > 0)
            searchResultContentLayout.removeAllViews();

        databaseSelectionLayout.setVisibility(View.VISIBLE);
        searchResultScrollView.setVisibility(View.VISIBLE);

        if ("all".equals(exerciseDatabaseSelection) && searchEdit.getText().toString().trim().length() == 0) {
            imm.showSoftInputFromInputMethod(mainContentLayout.getWindowToken(), 0);
        }

        // number of search results
        if (!("all".equals(exerciseDatabaseSelection) && searchEdit.getText().toString().trim().length() == 0) && numberOfResults == 0) {

            String databaseSelectionString = "";

            if ("recent".equals(exerciseDatabaseSelection)) {

                databaseSelectionString = "in Recent";

            } else if ("all".equals(exerciseDatabaseSelection)) {

                databaseSelectionString = "in All Exercises";

            } else if ("favorites".equals(exerciseDatabaseSelection)) {

                databaseSelectionString = "in Favorites";

            }

            searchResultLabel.setText("No results found " + databaseSelectionString);

            searchResultLayout.setVisibility(View.VISIBLE);
        } else {
            searchResultLayout.setVisibility(View.GONE);
        }


        LayoutInflater inflater = LayoutInflater.from(getApplicationContext());
        for (int i = 0; i < addActivityID.size(); i++) {
            final int index = i;
            View exerciseItemCell = inflater.inflate(R.layout.addexercise_searchresult_cell, null);
            TextView exerciseTitle = (TextView) exerciseItemCell.findViewById(R.id.typeLabelView);
            exerciseTitle.setTypeface(avenirNextRegularFont);

            exerciseTitle.setText(addActivityName.get(index));

            exerciseItemCell.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                    Intent intent = new Intent(HTNewAddExerciseSearchActivity.this, HTAddActivitySelectItemActivity.class);
                    intent.putExtra("addActivityCategory", exerciseDatabaseSelection);
                    intent.putExtra("selectedActivityType", "exercise");
                    intent.putExtra("selectedActivityID", Integer.parseInt(addActivityID.get(index)));
                    if (relaunchItemID > 0) {
                        intent.putExtra("relaunchPlannerItem", true);
                        intent.putExtra("relaunchItemID", relaunchItemID);
                    } else {
                        intent.putExtra("relaunchPlannerItem", false);
                    }

                    startActivityForResult(intent, 1);
                }
            });

            if ("favorites".equals(addActivityCategory) || "favorites".equals(exerciseDatabaseSelection)) {
//                exerciseItemCell.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        selectedActivityID = Integer.parseInt(addActivityID.get(index));
//                        showConfirmDeleteFavoriteItem();
//                        return false;
//                    }
//                });

                exerciseItemCell.setOnTouchListener(new OnSwipeTouchListener(this) {
                    @Override
                    public void onLongPressEvent(){
                        selectedActivityID = Integer.parseInt(addActivityID.get(index));
                        showConfirmDeleteFavoriteItem();
                    }

                    @Override
                    public void onSwipeLeft() {
                        selectedActivityID = Integer.parseInt(addActivityID.get(index));
                        showConfirmDeleteFavoriteItem();
                    }

                    @Override
                    public void onSwipeRight() {
                        selectedActivityID = Integer.parseInt(addActivityID.get(index));
                        showConfirmDeleteFavoriteItem();
                    }
                });
            } else if ("recent".equals(exerciseDatabaseSelection)) {
//                exerciseItemCell.setOnLongClickListener(new View.OnLongClickListener() {
//                    @Override
//                    public boolean onLongClick(View v) {
//                        selectedActivityID = Integer.parseInt(addActivityID.get(index));
//                        showConfirmDeleteRecentItem();
//                        return false;
//                    }
//                });

                exerciseItemCell.setOnTouchListener(new OnSwipeTouchListener(this) {
                    @Override
                    public void onLongPressEvent() {
                        selectedActivityID = Integer.parseInt(addActivityID.get(index));
                        showConfirmDeleteRecentItem();
                    }

                    @Override
                    public void onSwipeLeft() {
                        selectedActivityID = Integer.parseInt(addActivityID.get(index));
                        showConfirmDeleteRecentItem();
                    }

                    @Override
                    public void onSwipeRight() {
                        selectedActivityID = Integer.parseInt(addActivityID.get(index));
                        showConfirmDeleteRecentItem();
                    }
                });
            }

            searchResultContentLayout.addView(exerciseItemCell);
        }

        // search all exercises button
        if (!"all".equals(exerciseDatabaseSelection) && searchEdit.getText().toString().trim().length() > 0) {

            LinearLayout.LayoutParams layoutParams;

            // WHITE separator to cover up the last gray separator but keep the same offset
            View view = new View(this);
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (2 * displayDensity));
            layoutParams.setMargins(0, (int) (-2 * displayDensity), 0, 0);
            view.setLayoutParams(layoutParams);
            view.setBackgroundColor(Color.WHITE);
            searchResultContentLayout.addView(view);

            LinearLayout searchAllFoodsContainer = new LinearLayout(this);
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, (int) (54 * displayDensity));
            searchAllFoodsContainer.setBackgroundColor(Color.rgb(247, 249, 250));
            searchAllFoodsContainer.setLayoutParams(layoutParams);
            searchAllFoodsContainer.setPadding((int) (12 * displayDensity), (int) (10 * displayDensity), (int) (12 * displayDensity), (int) (10 * displayDensity));

            searchResultContentLayout.addView(searchAllFoodsContainer);

            LinearLayout searchAllFoodsBtn = new LinearLayout(this);
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            searchAllFoodsBtn.setBackgroundResource(R.drawable.ht_nutrition_button);
            searchAllFoodsBtn.setGravity(Gravity.CENTER_VERTICAL);
            searchAllFoodsBtn.setLayoutParams(layoutParams);
            searchAllFoodsContainer.addView(searchAllFoodsBtn);

            ImageView searchIconView = new ImageView(this);
            searchIconView.setImageResource(R.drawable.ht_icon_search_blue);
            layoutParams = new LinearLayout.LayoutParams((int) (24 * displayDensity), (int) (24 * displayDensity));
            layoutParams.setMargins((int) (6 * displayDensity), 0, 0, 0);
            searchIconView.setLayoutParams(layoutParams);
            searchAllFoodsBtn.addView(searchIconView);

            TextView labelView = new TextView(this);
            layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins((int) (6 * displayDensity), 0, (int) (10 * displayDensity), 0);
            labelView.setLayoutParams(layoutParams);
            labelView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 14);
            labelView.setTextColor(getResources().getColor(R.color.ht_blue));
            labelView.setTypeface(avenirNextRegularFont);
            labelView.setGravity(Gravity.CENTER);
            labelView.setSingleLine(true);
            labelView.setEllipsize(TextUtils.TruncateAt.END);
            searchAllFoodsBtn.addView(labelView);

            labelView.setText("Search all exercises for " + searchEdit.getText().toString());

            searchAllFoodsBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!"all".equals(exerciseDatabaseSelection)) {
                        stopThread();
                        searchEdit.clearFocus();
                        imm.hideSoftInputFromWindow(mainContentLayout.getWindowToken(), 0);
                        recentLabel.setTextColor(grayColor);
                        allexercisesLabel.setTextColor(blueColor);
                        favoritesLabel.setTextColor(grayColor);
                        recentIndicator.setVisibility(View.GONE);
                        allexercisesIndicator.setVisibility(View.VISIBLE);
                        favoritesIndicator.setVisibility(View.GONE);

                        exerciseDatabaseSelection = "all";
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

        }

        searchResultScrollView.scrollTo(0, 0);

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


    private void showConfirmDeleteFavoriteItem(){
        Dialog dialog = new AlertDialog.Builder(HTNewAddExerciseSearchActivity.this).setTitle("Delete Favorite?")
                .setMessage("Would you like to delete this Favorites item?")
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteFavorite();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        selectedActivityID = 0;
                    }
                }).create();
        dialog.show();
    }

    private void showConfirmDeleteRecentItem(){
        Dialog dialog = new AlertDialog.Builder(HTNewAddExerciseSearchActivity.this).setTitle("Delete Recent Item?")
                .setMessage("Would you like to delete this Recent item?")
                .setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        deleteRecent();
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        selectedActivityID = 0;
                    }
                }).create();
        dialog.show();
    }

    private void updateSearchEditHint() {
        if ("".equals(searchEdit.getText().toString())) {
            if ("recent".equals(exerciseDatabaseSelection)) {

                searchEdit.setHint("Search recent exercises");

            } else if ("all".equals(exerciseDatabaseSelection)) {

                searchEdit.setHint("Search all exercises");

            } else if ("favorites".equals(exerciseDatabaseSelection)) {

                searchEdit.setHint("Search favorite exercises");

            } else { // should never happen

                searchEdit.setHint("Search for an exercise");
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

                getSearchResults();
            }
        };

        public void afterTextChanged(final Editable s) {
            addActivitySearchFieldString = s.toString();
            handler.removeCallbacks(runnable);
            handler.postDelayed(runnable, 1000); // 2-second delay
        }
    };

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

//        getSearchResults();
    }

}
