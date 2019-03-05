package com.sph.healthtrac.planner.addfood;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RelativeLayout;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class HTAddFoodActivity extends Activity {

    private static RelativeLayout mainContentLayout;
    private RelativeLayout myFavoritesLayout;
    private RelativeLayout recommendedLayout;
    private RelativeLayout generalFoodItemLayout;

    private TextView myFavoritesLabel;
    private TextView recommendedLabel;
    private TextView generalFoodItemLabel;

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

    private boolean showAddFoodFavorites;
    private boolean showAddFoodRecommended;
    private boolean showAddFoodGeneral;

    private String addFoodCategory = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addfood);

        mainContentLayout = (RelativeLayout) findViewById(R.id.mainContentLayout);
        myFavoritesLayout = (RelativeLayout) findViewById(R.id.myFavoritesLayout);
        recommendedLayout = (RelativeLayout) findViewById(R.id.recommendedLayout);
        generalFoodItemLayout = (RelativeLayout) findViewById(R.id.generalFoodItemLayout);

        myFavoritesLabel = (TextView) findViewById(R.id.myFavoritesLabel);
        recommendedLabel = (TextView) findViewById(R.id.recommendedLabel);
        generalFoodItemLabel = (TextView) findViewById(R.id.generalItemLabel);

        Typeface labelFont = Typeface.createFromAsset(getAssets(), "fonts/AvenirNext-Regular.ttf");
        myFavoritesLabel.setTypeface(labelFont);
        recommendedLabel.setTypeface(labelFont);
        generalFoodItemLabel.setTypeface(labelFont);

        myFavoritesLayout.setVisibility(View.GONE);
        recommendedLayout.setVisibility(View.GONE);
        generalFoodItemLayout.setVisibility(View.GONE);

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
        View mActionBar = HTActionBar.getActionBar(this, "Add Food", "leftArrow", "");

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

        if (login == null || pw == null || login.equals("") || pw.equals("")) {

            Intent intent = new Intent(this, LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        } else {
            getAddFoodCategories();
            myFavoritesLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HTAddFoodActivity.this, HTAddFoodSearchActivity.class);
                    addFoodCategory = "favorites";
                    intent.putExtra("addFoodCategory", "favorites");
                    startActivityForResult(intent, 1);
                }
            });

            /*
            recommendedLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HTAddFoodActivity.this, HTAddFoodSearchActivity.class);
                    addFoodCategory = "recommended";
                    intent.putExtra("addFoodCategory", "recommended");
                    startActivityForResult(intent, 1);
                }
            });
            */

            generalFoodItemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HTAddFoodActivity.this, HTAddFoodSearchResultsActivity.class);
                    addFoodCategory = "general";
                    intent.putExtra("addFoodCategory", "general");
                    startActivityForResult(intent, 1);
                }
            });
        }
    }

    private void getAddFoodCategories() {

        showAddFoodFavorites = false;
        showAddFoodRecommended = false;
        showAddFoodGeneral = false;

        HTGlobals.getInstance().hideRecommendedFoodSearchCriteria = false;

        final ProgressDialog progressDialog = HTProgressDialog.getProgressDialog(this);
        progressDialog.show();

        myThread = new Thread() {

            @Override
            public void run() {

                try {

                    post = new HttpPost(getString(R.string.web_service_url));

                    List<NameValuePair> nameValuePairs = new ArrayList<>(6);
                    nameValuePairs.add(new BasicNameValuePair("action", "get_add_food"));
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

                                toast = HTToast.showToast(HTAddFoodActivity.this, errorMessage, Toast.LENGTH_LONG);
                            }
                        });
                    } else {
                        final Element e2 = (Element) nodes.item(0);
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                String tempString = "";

                                // My Favorites
                                if (XMLFunctions.tagExists(e2, "add_food_favorites")) {
                                    tempString = XMLFunctions.getValue(e2, "add_food_favorites");
                                    if ("1".equals(tempString))
                                        showAddFoodFavorites = true;
                                }

                                // Recommended
                                if (XMLFunctions.tagExists(e2, "add_food_recommended")) {
                                    tempString = XMLFunctions.getValue(e2, "add_food_recommended");
                                    if ("1".equals(tempString))
                                        showAddFoodRecommended = true;
                                }

                                // General
                                if (XMLFunctions.tagExists(e2, "add_food_general")) {
                                    tempString = XMLFunctions.getValue(e2, "add_food_general");
                                    if ("1".equals(tempString))
                                        showAddFoodGeneral = true;
                                }

                                // hideRecommendedFoodSearchCriteria
                                if (XMLFunctions.tagExists(e2, "hide_recommended_food_search_criteria")) {
                                    tempString = XMLFunctions.getValue(e2, "hide_recommended_food_search_criteria");
                                    if ("1".equals(tempString)) {
                                        HTGlobals.getInstance().hideRecommendedFoodSearchCriteria = true;
                                    }
                                }

                                showAddFoodCategories();
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

//                                    toast = HTToast.showToast(HTAddFoodActivity.this, "There was a problem connecting.\nPlease try again later.", Toast.LENGTH_LONG);
                                    HTConnectErrDialog.showDilaog(HTAddFoodActivity.this, HTGlobals.getInstance().customConnErrorTitle, HTGlobals.getInstance().customConnError + "\n\n" + error);
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

    private void showAddFoodCategories() {
        if (showAddFoodFavorites)
            myFavoritesLayout.setVisibility(View.VISIBLE);
        if (showAddFoodRecommended)
            recommendedLayout.setVisibility(View.VISIBLE);
        if (showAddFoodGeneral)
            generalFoodItemLayout.setVisibility(View.VISIBLE);

        recommendedLayout.setOnClickListener(null);

        if (HTGlobals.getInstance().hideRecommendedFoodSearchCriteria) {
            recommendedLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HTAddFoodActivity.this, HTAddFoodSearchResultsActivity.class);
                    addFoodCategory = "recommended";
                    intent.putExtra("addFoodCategory", "recommended");
                    intent.putExtra("addFoodSearchString", "WhichCategory=recommended");
                    startActivityForResult(intent, 1);
                }
            });
        } else {
            recommendedLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(HTAddFoodActivity.this, HTAddFoodSearchActivity.class);
                    addFoodCategory = "recommended";
                    intent.putExtra("addFoodCategory", "recommended");
                    startActivityForResult(intent, 1);
                }
            });
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { // coming back from a child activity, refresh the calendar

        if (resultCode == 2) {
            setResult(1);
            finish();
            return;
        }
        passDate = HTGlobals.getInstance().passDate;
        passDay = HTGlobals.getInstance().passDay;
        passMonth = HTGlobals.getInstance().passMonth;
        passYear = HTGlobals.getInstance().passYear;

        getAddFoodCategories();
    }
}
